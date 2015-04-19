package chess;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Chess extends JFrame{
	private Chessboard boardImage;
	private chess_piece[][] pieces = new chess_piece[8][8];
	private boolean[][] canMove = new boolean[8][8]; // if a specific piece can move here OR if any piece can move here
	private boolean[][] whiteInCheck = new boolean[8][8];
	private boolean[][] blackInCheck = new boolean[8][8];
	private boolean playing = false; // false before/after gameplay
	private int turns; // use mod 2 to figure out whose turn it is. 0 for white, 1 for black
	private boolean pieceSelected; 
	private int selectedCol = -1;
	private int selectedRow = -1;
	
	public static void main(String[] args) {
		// this is all testing stuff for now
		Chess game = new Chess();
		// TODO remove test piece once we're done with it
//		chess_piece testPiece = new chess_piece("bishop", 0);
//		test.add_piece(testPiece, 0, 0); 
	}
	
	public Chess() {
		super("Testing chess"); // opens a JFrame
		setSize(800, 550);
		setResizable(false);
		
		Container c = getContentPane();
		c.setLayout(new BoxLayout(c, BoxLayout.PAGE_AXIS));
		c.setBackground(new Color(255,255,255));
		
		boardImage = new Chessboard();
		c.add(boardImage, BorderLayout.NORTH);
		
		boardImage.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int xCord = (e.getX()-200) / 50;
				int yCord = (e.getY()-75) / 50;
				
				if (xCord < 0 || yCord < 0 || xCord >= 8 || yCord >= 8) { // you clicked outside the board
					deselectSquare();
//					System.out.println("Clicked outside board - Piece deselected");
				} else { // you clicked inside the board
					if (getPieceAt(xCord,yCord) != null && getPieceAt(xCord,yCord).get_color() == turns%2) { // you clicked on your piece
						deselectSquare(); // in case one was selected before. Does nothing if it wasn't
						selectSquare(xCord,yCord);
//						System.out.println("Piece selected");
					} else { // there is no piece or it's an enemy piece
						if(pieceSelected) {
							if (canMove[xCord][yCord]) { // you can move there
								getPieceAt(selectedCol, selectedRow).set_moved(true);
								move_piece(selectedCol, selectedRow, xCord, yCord);
//								System.out.println("Piece moved");
								endMove();
//								System.out.println("Turn ended");
							} else { // you can't move there
								deselectSquare(); // deselect the previously selected square
//								System.out.println("Can't move there - Piece deselected");
							}
						} else {
							// nothing: you can't select your enemy's piece
//							System.out.println("Select your OWN piece");
						}
					}
				}
			}
		});
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		boardImage.init();
		setVisible(true);
		
		playing = true; // create a start button eventually
		turns = 0;
		
		// add all initial pieces - comment out when testing
		add_piece(new chess_piece("rook", 0), 0, 0); // white rook
		add_piece(new chess_piece("knight", 0), 0, 1); // white knight
		add_piece(new chess_piece("bishop", 0), 0, 2); // white bishop
		add_piece(new chess_piece("queen", 0), 0, 3); // white queen
		add_piece(new chess_piece("king", 0), 0, 4); // white king
		add_piece(new chess_piece("bishop", 0), 0, 5); // white bishop
		add_piece(new chess_piece("knight", 0), 0, 6); // white knight
		add_piece(new chess_piece("rook", 0), 0, 7); // white rook
		for (int y = 0; y < 8; y++) {
			add_piece(new chess_piece("pawn", 0), 1, y); // white pawns
		}
		
		add_piece(new chess_piece("rook", 1), 7, 0); // black rook
		add_piece(new chess_piece("knight", 1), 7, 1); // black knight
		add_piece(new chess_piece("bishop", 1), 7, 2); // black bishop
		add_piece(new chess_piece("queen", 1), 7, 3); // black queen
		add_piece(new chess_piece("king", 1), 7, 4); // black king
		add_piece(new chess_piece("bishop", 1), 7, 5); // black bishop
		add_piece(new chess_piece("knight", 1), 7, 6); // black knight
		add_piece(new chess_piece("rook", 1), 7, 7); // black rook
		for (int y = 0; y < 8; y++) {
			add_piece(new chess_piece("pawn", 1), 6, y); // black pawns
		}
	}
	
	public Chessboard getBoard() {
		return boardImage;
	}
	
	public chess_piece getPieceAt(int col, int row) {
		return pieces[col][row];
	}
	
	public void calculateMovesFrom(int col, int row) {
		if(getPieceAt(col, row) != null) {
			String type = getPieceAt(col, row).get_piece_type();
//			System.out.println(type);
			int color = getPieceAt(col, row).get_color();
			
			switch(type) {
				case "pawn":
					if(color == 0) {
						if(col+1 < 8 && row+1 < 8 && getPieceAt(col+1,row+1) != null && getPieceAt(col+1,row+1).get_color() == 1) {
							canMoveToSquare(col+1,row+1);
						}
						if(col+1 < 8 && row-1 >= 0 && getPieceAt(col+1,row-1) != null && getPieceAt(col+1,row-1).get_color() == 1) {
							canMoveToSquare(col+1,row-1);
						}
						if(col+1 < 8 && getPieceAt(col+1,row) == null) {
							canMoveToSquare(col+1, row);
							if(!getPieceAt(col,row).get_moved()) {
								canMoveToSquare(col+2, row);
							}
						}
					} else {
						if(col-1 >= 0 && row+1 < 8 && getPieceAt(col-1,row+1) != null && getPieceAt(col-1,row+1).get_color() == 0) {
							canMoveToSquare(col-1,row+1);
						}
						if(col-1 >= 0 && row-1 >= 0 && getPieceAt(col-1,row-1) != null && getPieceAt(col-1,row-1).get_color() == 0) {
							canMoveToSquare(col-1,row-1);
						}
						if(col-1 >= 0 && getPieceAt(col-1,row) == null) {
							canMoveToSquare(col-1, row);
							if(!getPieceAt(col,row).get_moved()) {
								canMoveToSquare(col-2, row);
							}
						}
					}
					
					// TODO diagonal capture?
					break;
				case "rook":
					for (int x = col+1; x < 8; x++) {
						if (getPieceAt(x, row) != null && getPieceAt(x, row).get_color() == color) {
							break;
						}
						canMoveToSquare(x, row);
						if (getPieceAt(x, row) != null) {
							break;
						}
					}
					for (int y = row+1; y < 8; y++) {
						if (getPieceAt(col,y) != null && getPieceAt(col,y).get_color() == color) {
							break;
						}
						canMoveToSquare(col,y);
						if (getPieceAt(col,y) != null) {
							break;
						}
					}
					for (int x = col-1; x >= 0; x--) {
						if (getPieceAt(x, row) != null && getPieceAt(x, row).get_color() == color) {
							break;
						}
						canMoveToSquare(x, row);
						if (getPieceAt(x, row) != null) {
							break;
						}
					}
					for (int y = row-1; y >= 0; y--) {
						if (getPieceAt(col,y) != null && getPieceAt(col,y).get_color() == color) {
							break;
						}
						canMoveToSquare(col,y);
						if (getPieceAt(col,y) != null) {
							break;
						}
					}
					break;
				case "knight":
					// TODO
					break;
				case "bishop":
					int i = 1;
					while (true) { // will break out
						if(col+i >= 8 || row+i >= 8) {
							break;
						}
						if(getPieceAt(col+i, row+i) != null && getPieceAt(col+i, row+i).get_color() == color) {
							break;
						}
						canMoveToSquare(col+i, row+i);
						if (getPieceAt(col+i, row+i) != null) {
							break;
						}
						i++;
					}
					i = 1;
					while (true) {
						if(col-i < 0 || row-i < 0) {
							break;
						}
						if(getPieceAt(col-i, row-i) != null && getPieceAt(col-i, row-i).get_color() == color) {
							break;
						}
						canMoveToSquare(col-i, row-i);
						if (getPieceAt(col-i, row-i) != null) {
							break;
						}
						i++;
					}
					i = 1;
					while (true) {
						if((col-i) < 0 || (row+i) >= 8) {
							break;
						}
						if(getPieceAt(col-i, row+i) != null && getPieceAt(col-i, row+i).get_color() == color) {
							break;
						}
						canMoveToSquare(col-i, row+i);
						if (getPieceAt(col-i, row+i) != null) {
							break;
						}
						i++;
					}
					i = 1;
					while (true) {
						if(col+i >= 8 || row-i < 0) {
							break;
						}
						if(getPieceAt(col+i, row-i) != null && getPieceAt(col+i, row-i).get_color() == color) {
							break;
						}
						canMoveToSquare(col+i, row-i);
						if (getPieceAt(col+i, row-i) != null) {
							break;
						}
						i++;
					}
					break;
				case "king":
					// TODO
					calculateAllCaptures((color+1)%2);
					for (int x = -1; x <= 1; x++) {
						for (int y = -1; y <=1; y++) {
							System.out.print("checking king's move to " + (col+x) + " " + (row+y) + " ");
							if (col+x >= 8 || row+y >= 8 || col+x < 0 || row+y < 0) { // don't got outside board
								System.out.println("!Outside board");
								continue;
							}
							if (getPieceAt(col+x,row+y) != null && getPieceAt(col+x,row+y).get_color() == color) { // don't select squares with pieces of the same color
								System.out.println("!same piece");
								continue;
							}
							if ((color == 0 && whiteInCheck[col+x][row+y]) || (color == 1 && blackInCheck[col+x][row+y])) {
								System.out.println("!check");
								continue;
							}
							System.out.println("Can move to " + (col+x) + " " + (row+y));
							canMoveToSquare(col+x, row+y);
						}
					}
					break;
				case "queen":
					// TODO
					break;
				case "test":
					for (int x = 0; x < 8; x++) {
						for (int y = 0; y < 8; y++) {
							canMoveToSquare(x,y);
						}
					}
					break;
			}
		}
	}
	
	public void clearAllMoves() {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				canMove[x][y] = false;
			}
		}
		boardImage.clearThreat();
	}
	
	public void calculateAllMoves() {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				calculateMovesFrom(x, y);
			}
		}
	}
	
	public void calculateCapturesFrom(int col, int row) {
		if(getPieceAt(col, row) != null) {
			String type = getPieceAt(col, row).get_piece_type();
			int color = getPieceAt(col, row).get_color();
			
			switch(type) {
			case "pawn":
				if(color == 0) {
					if(col+1 < 8 && row+1 < 8) {
						whiteCanCapture(col+1,row+1);
					}
					if(col+1 < 8 && row-1 >= 0) {
						whiteCanCapture(col+1,row-1);
					}
				} else {
					if(col-1 >= 0 && row+1 < 8) {
						blackCanCapture(col-1,row+1);
					}
					if(col-1 >= 0 && row-1 >= 0) {
						blackCanCapture(col-1,row-1);
					}
				}
				break;
			case "rook":
				for (int x = col+1; x < 8; x++) {
					if (getPieceAt(x, row) != null && getPieceAt(x, row).get_color() == color) {
						break;
					}
					if (color == 0) {
						whiteCanCapture(x, row);
						System.out.println("white can capture " + x + " " + row);
					} else {
						blackCanCapture(x, row);
					}
					if (getPieceAt(x, row) != null) {
						break;
					}
				}
				for (int y = row+1; y < 8; y++) {
					if (getPieceAt(col,y) != null && getPieceAt(col,y).get_color() == color) {
						break;
					}
					if (color == 0) {
						whiteCanCapture(col, y);
						System.out.println("white can capture " + col + " " + y);
					} else {
						blackCanCapture(col, y);
					}
					if (getPieceAt(col,y) != null) {
						break;
					}
				}
				for (int x = col-1; x >= 0; x--) {
					if (getPieceAt(x, row) != null && getPieceAt(x, row).get_color() == color) {
						break;
					}
					if (color == 0) {
						whiteCanCapture(x, row);
						System.out.println("white can capture " + x + " " + row);
					} else {
						blackCanCapture(x, row);
					}
					if (getPieceAt(x, row) != null) {
						break;
					}
				}
				for (int y = row-1; y >= 0; y--) {
					if (getPieceAt(col,y) != null && getPieceAt(col,y).get_color() == color) {
						break;
					}
					if (color == 0) {
						whiteCanCapture(col, y);
						System.out.println("white can capture " + col + " " + y);
					} else {
						blackCanCapture(col, y);
					}
					if (getPieceAt(col,y) != null) {
						break;
					}
				}
				break;
				
			case "knight":
				// TODO
				break;
			case "bishop":
				int i = 1;
				while (true) { // will break out
					if(col+i >= 8 || row+i >= 8) {
						System.out.println("1out of range");
						System.out.println("loop 1; i = " + i);
						break;
					}
					if(getPieceAt(col+i, row+i) != null && getPieceAt(col+i, row+i).get_color() == color) {
						System.out.println("1same piece in the way");
						System.out.println("loop 1; i = " + i);
						break;
					}
					if (color == 0) {
						whiteCanCapture(col+i, row+i);
						System.out.println("1White can capture " + (col+i) + " " + (row+i));
						System.out.println("loop 1; i = " + i);
					} else {
						blackCanCapture(col+i, row+i);
					}
					if (getPieceAt(col+i, row+i) != null) {
						System.out.println("1there is a black piece there");
						System.out.println("loop 1; i = " + i);
						break;
					}
					i++;
				}
				i = 1;
				while (true) {
					if(col-i < 0 || row-i < 0) {
						System.out.println("2out of range");
						break;
					}
					if(getPieceAt(col-i, row-i) != null && getPieceAt(col-i, row-i).get_color() == color) {
						System.out.println("2same piece in the way");
						break;
					}
					if (color == 0) {
						whiteCanCapture(col-i, row-i);
						System.out.println("2White can capture " + (col-i) + " " + (row-i)); 
					} else {
						blackCanCapture(col-i, row-i);
					}
					if (getPieceAt(col-i, row-i) != null) {
						System.out.println("2there is a black piece there");
						break;
					}
					System.out.println("loop 2; i = " + i);
					i++;
				}
				i = 1;
				while (true) {
					if((col-i) < 0 || (row+i) >= 8) {
						System.out.println("3out of range");
						break;
					}
					if(getPieceAt(col-i, row+i) != null && getPieceAt(col-i, row+i).get_color() == color) {
						System.out.println("3same piece in the way");
						break;
					}
					if (color == 0) {
						whiteCanCapture(col-i, row+i);
						System.out.println("3White can capture " + (col-i) + " " + (row+i)); 
					} else {
						blackCanCapture(col-i, row+i);
					}
					if (getPieceAt(col-i, row+i) != null) {
						System.out.println("3there is a black piece there");
						break;
					}
					System.out.println("loop 3; i = " + i);
					i++;
				}
				i = 1;
				while (true) {
					if(col+i >= 8 || row-i < 0) {
						System.out.println("4out of range");
						break;
					}
					if(getPieceAt(col+i, row-i) != null && getPieceAt(col+i, row-i).get_color() == color) {
						System.out.println("4same piece in the way");
						break;
					}
					if (color == 0) {
						System.out.println("4White can capture " + (col+i) + " " + (row-i)); 
						whiteCanCapture(col+i, row-i);
					} else {
						blackCanCapture(col+i, row-i);
					}
					if (getPieceAt(col+i, row-i) != null) {
						System.out.println("4there is a black piece there");
						break;
					}
					System.out.println("loop 4; i = " + i);
					i++;
				}
				System.out.println("Done with bishop");
				break;
			case "king":
				// TODO
				for (int x = -1; x <= 1; x++) {
					for (int y = -1; y <=1; y++) {
						if (col+x >= 8 || row+y >= 8 || col+x < 0 || row+y < 0) { // don't got outside board
							continue;
						}
						if (getPieceAt(col+x,row+y) != null && getPieceAt(col+x,row+y).get_color() == color) { // don't select squares with pieces of the same color
							continue;
						}
						if (color == 0) {
							whiteCanCapture(col+x, row+y);
						} else {
							blackCanCapture(col+x, row+y);
						}
					}
				}
				break;
			case "queen":
				// TODO
				break;
			case "test":
				System.out.println("threatening all squares");
				for (int x = 0; x < 8; x++) {
					for (int y = 0; y < 8; y++) {
						canMoveToSquare(x,y);
					}
				}
				break; 
				
			} 
		}
	}
	
	public void calculateAllCaptures(int color) { // whose turn it was BEFORE
		if (color == 0) {
			resetWhiteCanCapture();
		} else {
			resetBlackCanCapture();
		}
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (getPieceAt(x,y) != null && getPieceAt(x,y).get_color() == color) {
					System.out.println("Calculate captures from " + x + ", " + y);
					calculateCapturesFrom(x, y);
				}
			}
		}
	}
	
	public int[] findKing(int color) {
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 8; y++) {
				if(getPieceAt(x,y) != null && getPieceAt(x,y).get_piece_type().equals("king") && getPieceAt(x,y).get_color() == color) {
					return new int[]{x, y};
				}
			}
		}
		return new int[]{-1,-1};
	}
	
	public boolean checkForCheckmate(int color) { // whose turn it was BEFORE
		calculateAllCaptures(color);
		int[] kingCord = findKing((turns+1)%2);
		for (int x = -1; x <= 1; x++) {
			for(int y = -1; y <= 1; y++) {
				if (kingCord[0]+x < 0 || kingCord[1]+y < 0 || kingCord[0]+x >= 8 || kingCord[1]+y >= 8) {
					continue;
				}
				if(color == 0 && !whiteInCheck[kingCord[0]+x][kingCord[1]+y]) {
					return false;
				}
				if(color == 1 && !blackInCheck[kingCord[0]+x][kingCord[1]+y]) {
					return false;
				}
			}
		}
		return true; 
	}
	
	public boolean checkForCheck(int color) { // whose turn it was BEFORE
		calculateAllCaptures(color);
		int[] kingCord = findKing((turns+1)%2);
		System.out.print("King is at " + kingCord[0] + ", " + kingCord[1]);
		if (kingCord[0] < 0 || kingCord[1] < 0 || kingCord[0] >= 8 || kingCord[1] >= 8) {
			return false;
		} // this shouldn't happen, because the king has to exist
		if (color == 0) {
			return whiteInCheck[kingCord[0]][kingCord[1]];
		} else {
			return blackInCheck[kingCord[0]][kingCord[1]];
		}
		
	}
	
	public void endMove() {
		deselectSquare();
		
		if(checkForCheckmate(turns%2)) {
			if (turns%2 == 0) { // it was white's turn - white wins
				whiteWins();
			} else {
				blackWins();
			}
		} else if (checkForCheck(turns%2)) { // the king is in check!
			if (turns%2 == 0) { // it was white's turn - black is in check
				blackInCheck();
			} else {
				whiteInCheck();
			}
		} else {
			clearCheck();
		}
		turns++;
	}
	
	public void add_piece(chess_piece piece, int col, int row) {
		pieces[col][row] = piece;
		boardImage.add_piece(piece, col, row);
	}
	
	public void move_piece(int col1, int row1, int col2, int row2) {
		pieces[col2][row2] = pieces[col1][row1];
		pieces[col1][row1] = null;
		boardImage.move_piece(col1, row1, col2, row2);
	}
	
	public void kill_piece(int col, int row) {
		pieces[col][row] = null;
		boardImage.kill_piece(col, row);
	}
	
	public void selectSquare(int col, int row) {
		boardImage.highlightSquare(col, row);
		pieceSelected = true;
		selectedCol = col;
		selectedRow = row;
		calculateMovesFrom(col,row);
	}
	
	public void deselectSquare() {
		boardImage.unhighlightSquare();
		pieceSelected = false;
		selectedCol = -1;
		selectedRow = -1;
		clearAllMoves();
	}
	
	public void canMoveToSquare(int col, int row) {
		canMove[col][row] = true;
		boardImage.canMoveToSquare(col, row);
	}
	
	public void whiteCanCapture(int col, int row) {
		blackInCheck[col][row] = true;
		boardImage.whiteCanCaptureSquare(col, row);
	}
	
	public void resetWhiteCanCapture() {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				blackInCheck[x][y] = false;
			}
		}
		boardImage.resetWhiteCanCapture();
		System.out.println("resetted whiteCanCapture");
	}
	
	public void blackCanCapture(int col, int row) {
		whiteInCheck[col][row] = true;
		boardImage.blackCanCaptureSquare(col, row);
	}
	
	public void resetBlackCanCapture() {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				whiteInCheck[x][y] = false;
			}
		}
		boardImage.resetBlackCanCapture();
	}
	
	public void whiteInCheck() {
		boardImage.whiteInCheck();
	}
	
	public void blackInCheck() {
		boardImage.blackInCheck();
	}
	
	public void whiteWins() {
		boardImage.whiteWins();
	}
	
	public void blackWins() {
		boardImage.blackWins();
	}
	
	public void clearCheck() {
		boardImage.clearCheck();
	}
}