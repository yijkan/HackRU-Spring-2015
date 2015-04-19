package chess;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Test extends JFrame{
	private chess_graphics boardImage;
	private chess_piece[][] pieces = new chess_piece[8][8];
	private boolean[][] threatened = new boolean[8][8]; // if a specific piece can move here OR if any piece can move here 
	private boolean playing = false; // false before/after gameplay
	private int turns; // use mod 2 to figure out whose turn it is
	private boolean pieceSelected; 
	private int selectedCol = -1;
	private int selectedRow = -1;
	
	public static void main(String[] args) {
		// this is all testing stuff for now
		Test test = new Test();
		chess_piece testPiece = new chess_piece("test", "black");
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
				
				if (!pieceSelected) { // TODO: if the piece you clicked on is yours
					System.out.println("Piece is not selected\nWill calculate all possible moves.");
					calculateMovesFrom(xCord, yCord);
					selectSquare(xCord, yCord);
				} else { // a piece has been selected to move
					System.out.print(threatened[xCord][yCord]);
					if (threatened[xCord][yCord]) { // the piece can move there
						move_piece(selectedCol, selectedRow, xCord, yCord);
						boardImage.unhighlightSquare();
						
						clearAllMoves();
						deselectSquare();
						
						turns++;
						if(checkForCheckmate()) {
							// TODO: the player wins!
						} else if (checkForCheck()) { // the king is in check!
							// TODO: in check message
						}
					} else { // TODO: if clicked piece is the player's own, 
						deselectSquare();
					}
				}
				
				// TODO
			}
		});
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		boardImage.init();
		setVisible(true);
	}
	
	public chess_graphics getBoard() {
		return boardImage;
	}
	
	public chess_piece getPieceAt(int col, int row) {
		return pieces[col][row];
	}
	
	public void calculateMovesFrom(int col, int row) {
		System.out.println("Calculating moves");
		if(getPieceAt(col, row) != null) {
			String type = getPieceAt(col, row).get_piece_type();
			System.out.println(type);
			String color = getPieceAt(col, row).get_color();
			
			switch(type) {
				case "pawn":
					// TODO
					break;
				case "rook":
					// TODO
					for (int x = col; x < 8; x++) {
						for (int y = row; y < 0; y++) {
							if (getPieceAt(x,y) != null && getPieceAt(x,y).get_color().equals(color)) {
								break;
							}
							threatenSquare(x,y);
							if (getPieceAt(x,y) != null) {
								break;
							}
						}
					}
					
					for (int x = col; x >=0; x--) {
						for (int y = row; y >= 0; y--) {
							if (getPieceAt(x,y) != null && getPieceAt(x,y).get_color().equals(color)) {
								continue;
							}
							threatenSquare(x,y);
							if (getPieceAt(x,y) != null) {
								continue;
							}
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
							if (getPieceAt(x,y).get_color().equals(color)) { // don't select squares with pieces of the same color
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
	
	public int[] findKing() {
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 8; y++) {
				if(getPieceAt(x,y) != null && getPieceAt(x,y).get_piece_type().equals("king")) {
					return new int[]{x, y};
				}
			}
		}
		return new int[]{-1,-1};
	}
	
	public boolean checkForCheckmate() {
		calculateAllMoves();
		int[] kingCord = findKing();
		for (int x = -1; x <= 1; x++) {
			for(int y = -1; y <= 1; y++) {
				if (kingCord[0]+x < 0 || kingCord[1]+y < 0 || kingCord[0]+x >= 8 || kingCord[1]+y >= 8) {
					continue;
				}
				if(!threatened[kingCord[0]+x][kingCord[1]+y]) {
					return false;
				}
			}
		}
		return true; 
	}
	
	public boolean checkForCheck() {
		calculateAllMoves();
		int[] kingCord = findKing();
		if (kingCord[0] < 0 || kingCord[1] < 0 || kingCord[0] >= 8 || kingCord[1] >= 8) {
			return false;
		} // this shouldn't happen, because the king has to exist
		return threatened[kingCord[0]][kingCord[1]];
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
	}
	
	public void deselectSquare() {
		boardImage.unhighlightSquare();
		pieceSelected = false;
		selectedCol = -1;
		selectedRow = -1;
	}
	
	public void threatenSquare(int col, int row) {
		threatened[col][row] = true;
	}
	
	public void unthreatenSquare(int col, int row) {
		threatened[col][row] = false;
	}
}