package chess;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Test extends JFrame{
	private chess_graphics boardImage;
	private chess_piece[][] pieces = new chess_piece[8][8];
	private boolean[][] threatened = new boolean[8][8]; // if a specific piece can move here OR if any piece can move here 
	private boolean playing = false; // false before/after gameplay
	private int turns; // use mod 2 to figure out whose turn it is. 0 for white, 1 for black
	private boolean pieceSelected; 
	private int selectedCol = -1;
	private int selectedRow = -1;
	
	public static void main(String[] args) {
		// this is all testing stuff for now
		Test test = new Test();
		// TODO remove test piece once we're done with it
		chess_piece testPiece = new chess_piece("rook", 0);
		test.add_piece(testPiece, 0, 0); 
	}
	
	public Test() {
		super("Testing chess"); // opens a JFrame
		setSize(800, 550);
		setResizable(false);
		
		Container c = getContentPane();
		c.setLayout(new BoxLayout(c, BoxLayout.PAGE_AXIS));
		c.setBackground(new Color(255,255,255));
		
		boardImage = new chess_graphics();
		c.add(boardImage, BorderLayout.NORTH);
		
		boardImage.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int xCord = (e.getX()-200) / 50;
				int yCord = (e.getY()-75) / 50;
				
				if (xCord < 0 || yCord < 0 || xCord >= 8 || yCord >= 8) { // you clicked outside the board
					deselectSquare();
					System.out.println("Clicked outside board - Piece deselected");
				} else { // you clicked inside the board
					if (getPieceAt(xCord,yCord) != null && getPieceAt(xCord,yCord).get_color() == turns%2) { // you clicked on your piece
						deselectSquare(); // in case one was selected before. Does nothing if it wasn't
						selectSquare(xCord,yCord);
						System.out.println("Piece selected");
					} else { // there is no piece or it's an enemy piece
						if(pieceSelected) {
							if (threatened[xCord][yCord]) { // you can move there
								getPieceAt(selectedCol, selectedRow).set_moved(true);
								move_piece(selectedCol, selectedRow, xCord, yCord);
								System.out.println("Piece moved");
								endMove();
								System.out.println("Turn ended");
							} else { // you can't move there
								deselectSquare(); // deselect the previously selected square
								System.out.println("Can't move there - Piece deselected");
							}
						} else {
							// nothing: you can't select your enemy's piece
							System.out.println("Select your OWN piece");
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
//		add_piece(new chess_piece("rook", 0), 0, 0); // white rook
//		add_piece(new chess_piece("knight", 0), 0, 1); // white knight
//		add_piece(new chess_piece("bishop", 0), 0, 2); // white bishop
//		add_piece(new chess_piece("queen", 0), 0, 3); // white queen
//		add_piece(new chess_piece("king", 0), 0, 4); // white king
//		add_piece(new chess_piece("bishop", 0), 0, 5); // white bishop
//		add_piece(new chess_piece("knight", 0), 0, 6); // white knight
//		add_piece(new chess_piece("rook", 0), 0, 7); // white rook
//		for (int y = 0; y < 8; y++) {
//			add_piece(new chess_piece("pawn", 0), 1, y); // white pawns
//		}
//		
//		add_piece(new chess_piece("rook", 1), 8, 0); // black rook
//		add_piece(new chess_piece("knight", 1), 8, 1); // black knight
//		add_piece(new chess_piece("bishop", 1), 8, 2); // black bishop
//		add_piece(new chess_piece("queen", 1), 8, 3); // black queen
//		add_piece(new chess_piece("king", 1), 8, 4); // black king
//		add_piece(new chess_piece("bishop", 1), 8, 5); // black bishop
//		add_piece(new chess_piece("knight", 1), 8, 6); // black knight
//		add_piece(new chess_piece("rook", 1), 8, 7); // black rook
//		for (int y = 0; y < 8; y++) {
//			add_piece(new chess_piece("pawn", 1), 7, y); // black pawns
//		}
	}
	
	public chess_graphics getBoard() {
		return boardImage;
	}
	
	public chess_piece getPieceAt(int col, int row) {
		return pieces[col][row];
	}
	
	public void calculateMovesFrom(int col, int row) {
		if(getPieceAt(col, row) != null) {
			String type = getPieceAt(col, row).get_piece_type();
			System.out.println(type);
			int color = getPieceAt(col, row).get_color();
			
			switch(type) {
				case "pawn":
					if(color == 0) {
						for (int y = 0; y < 8; y++) {
							if(col < 7) {
								threatenSquare(col+1, y);
							}
						}
						if(!getPieceAt(col,row).get_moved()) {
							for (int y = 0; y < 8; y++) {
								threatenSquare(col+2, y);
							}
						}
					} else {
						for (int y = 0; y < 8; y++) {
							if(col > 0) {
								threatenSquare(col-1, y);
							}
						}
						if(!getPieceAt(col,row).get_moved()) {
							for (int y = 0; y < 8; y++) {
								threatenSquare(col-2, y);
							}
						}
					}
					
					// TODO
					break;
				case "rook":
					// TODO
					for (int x = col+1; x < 8; x++) {
						if (getPieceAt(x, row) != null && getPieceAt(x, row).get_color() == color) {
							break;
						}
						threatenSquare(x, row);
						if (getPieceAt(x, row) != null) {
							break;
						}
					}
					for (int y = row+1; y < 8; y++) {
						if (getPieceAt(col,y) != null && getPieceAt(col,y).get_color() == color) {
							break;
						}
						threatenSquare(col,y);
						if (getPieceAt(col,y) != null) {
							break;
						}
					}
					for (int x = col-1; x >= 0; x--) {
						if (getPieceAt(x, row) != null && getPieceAt(x, row).get_color() == color) {
							break;
						}
						threatenSquare(x, row);
						if (getPieceAt(x, row) != null) {
							break;
						}
					}
					for (int y = row-1; y >= 0; y--) {
						if (getPieceAt(col,y) != null && getPieceAt(col,y).get_color() == color) {
							break;
						}
						threatenSquare(col,y);
						if (getPieceAt(col,y) != null) {
							break;
						}
					}
					break;
				case "knight":
					// TODO
					break;
				case "bishop":
					// TODO
					break;
				case "king":
					// TODO
					for (int x = -1; x <= 1; x++) {
						for (int y = -1; y <=1; y++) {
							if (getPieceAt(x,y).get_color() == color) { // don't select squares with pieces of the same color
								continue;
							}
							threatenSquare(col+x, row+y);
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
							threatenSquare(x,y);
						}
					}
					break;
			}
		}
	}
	
	public void clearAllMoves() {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				threatened[x][y] = false;
			}
		}
	}
	
	public void calculateAllMoves() {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				calculateMovesFrom(x, y);
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
	
	public boolean checkForCheckmate() {
		calculateAllMoves();
		int[] kingCord = findKing(turns%2);
		for (int x = -1; x <= 1; x++) {
			for(int y = -1; y <= 1; y++) {
				if (kingCord[0]+x < 0 || kingCord[1]+y < 0 || kingCord[0]+x >= 8 || kingCord[1]+y >= 8) {
					continue;
				}
				if(!threatened[kingCord[0]+x][kingCord[1]+y]) {
					clearThreat();
					return false;
				}
			}
		}
		clearThreat();
		return true; 
	}
	
	public boolean checkForCheck() {
		calculateAllMoves();
		int[] kingCord = findKing(turns%2);
		if (kingCord[0] < 0 || kingCord[1] < 0 || kingCord[0] >= 8 || kingCord[1] >= 8) {
			return false;
		} // this shouldn't happen, because the king has to exist
		boolean result = threatened[kingCord[0]][kingCord[1]];
		clearThreat();
		return result;
	}
	
	public void endMove() {
		clearAllMoves();
		deselectSquare();
		
		if(checkForCheckmate()) {
			if (turns%2 == 0) { // it was white's turn - white wins
				whiteWins();
			} else {
				blackWins();
			}
		} else if (checkForCheck()) { // the king is in check!
			if (turns%2 == 0) { // it was white's turn - black is in check
				blackInCheck();
			} else {
				whiteInCheck();
			}
		}
		turns++;
	}
	
	public void add_piece(chess_piece piece, int col, int row) {
		pieces[col][row] = piece;
		boardImage.add_piece(piece, col, row);
		System.out.println(getPieceAt(col,row).get_color());
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
		clearThreat();
	}
	
	public void threatenSquare(int col, int row) {
		threatened[col][row] = true;
		boardImage.threatenSquare(col, row);
	}
	
	public void clearThreat() {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				threatened[x][y] = false;
			}
		}
		boardImage.clearThreat();
		System.out.println("Cleared threat");
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
}