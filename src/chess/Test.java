package chess;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Test extends JFrame{
	private chess_graphics boardImage;
	private chess_piece[][] pieces;
	private boolean[][] threatened; // if a specific piece can move here OR if any piece can move here 
	private boolean playing; // false before/after gameplay
	private boolean turn; // false is white, true is black
	private boolean pieceSelected; 
	private int selectedCol = -1;
	private int selectedRow = -1;
	
	public static void main(String[] args) {
		// this is all testing stuff for now
		Test test = new Test();
		chess_piece testPiece = new chess_piece("test", "black");
		test.getBoard().add_piece(testPiece, 0, 0); 
		test.getBoard().kill_piece(0, 0);
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
				int xCord = e.getX() * 50 - 200;
				int yCord = e.getY() * 50 - 200;

				if (!pieceSelected) {
					calculateMovesFrom(xCord, yCord);
					selectedCol = xCord;
					selectedRow = yCord;
					pieceSelected = true;
				} else { // a piece has been selected to move
					if (threatened[xCord][yCord]) { // the piece can move there
						move_piece(selectedCol, selectedRow, xCord, yCord);
						
						clearAllMoves();
						selectedCol = -1;
						selectedRow = -1;
						pieceSelected = false;
						turn = !turn;
						if(checkForCheckmate()) {
							// TODO: the player wins!
						} else if (checkForCheck()) { // the king is in check!
							// TODO: in check message
						}
					} else { // TODO: if clicked piece is the player's own, 
						pieceSelected = false;
						selectedCol = -1;
						selectedRow = -1;
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
		String type = getPieceAt(col, row).get_piece_type();
		
		switch(type) {
			case "pawn":
				// TODO
				break;
			case "rook":
				for (int x = 0; x < 8; x++) {
					threatened[x][row] = true; 
				}
				for (int y = 0; y < 8; y++) {
					threatened[col][y] = true;
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
						if (x==0 && y==0) { // don't select the square the king is currently on
							continue;
						}
						threatened[col+x][row+y] = true;
					}
				}
				break;
			case "queen":
				// TODO
				break;
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
				if(getPieceAt(x,y).get_piece_type().equals("king")) {
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
		return threatened[kingCord[0]][kingCord[1]];
	}
	
	public void add_piece(chess_piece piece, int col, int row) {
		String type = piece.get_piece_type();
		String color = piece.get_color();
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
}