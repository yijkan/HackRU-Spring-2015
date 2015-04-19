package chess;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Chess extends JFrame{
	private Chessboard board;
	private chess_piece[][] pieces = new chess_piece[8][8];
	private boolean[][] canMove = new boolean[8][8]; // if a specific piece can move here OR if any piece can move here
	private boolean[][] whiteInCheck = new boolean[8][8];
	private boolean[][] blackInCheck = new boolean[8][8];
	private boolean playing = false; // false before/after gameplay
	private int turns = -1; // use mod 2 to figure out whose turn it is. 0 for white, 1 for black
	private boolean pieceSelected; 
	private int selectedCol = -1;
	private int selectedRow = -1;
	
	public static void main(String[] args) {
		new Chess();
	}
	
	public Chess() {
		super("Testing chess"); // opens a JFrame
		setSize(800, 600);
		setResizable(false);
		
		Container c = getContentPane();
		c.setLayout(new BoxLayout(c, BoxLayout.PAGE_AXIS));
		c.setBackground(new Color(255,255,255));
		
		board = new Chessboard();
		c.add(board, BorderLayout.NORTH);
		
		board.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (!playing && e.getX() > 225 && e.getX() < 450 && e.getY() > 500 && e.getY() < 575) {
					startGame();
				}
					//250, 525
				
				int xCord = (e.getX()-200) / 50;
				int yCord = (e.getY()-75) / 50;
				
				if (xCord < 0 || yCord < 0 || xCord >= 8 || yCord >= 8) { // you clicked outside the board
					deselectSquare();
				} else { // you clicked inside the board
					if (getPieceAt(xCord,yCord) != null && getPieceAt(xCord,yCord).get_color() == turns%2) { // you clicked on your piece
						deselectSquare(); // in case one was selected before. Does nothing if it wasn't
						selectSquare(xCord,yCord);
					} else { // there is no piece or it's an enemy piece
						if(pieceSelected) {
							if (canMove[xCord][yCord]) { // you can move there
								getPieceAt(selectedCol, selectedRow).set_moved(true);
								move_piece(selectedCol, selectedRow, xCord, yCord);
								System.out.println("Piece moved");
								endMove();
							} else { // you can't move there
								deselectSquare(); // deselect the previously selected square
							}
						} else {
							// nothing: you can't select your enemy's piece
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
		
		board.init();
		setVisible(true);
		
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
		return board;
	}
	
	public chess_piece getPieceAt(int col, int row) {
		return pieces[col][row];
	}
	
	public void calculateMovesFrom(int col, int row) {
		// TODO: can't do anything that puts the king in check
		if(getPieceAt(col, row) != null) {
			String type = getPieceAt(col, row).get_piece_type();
			int color = getPieceAt(col, row).get_color();
			int i = 1; // some need counters
			
			switch(type) {
				case "pawn":
					if(color == 0) {
						if(col+1 < 8 && row+1 < 8 && getPieceAt(col+1,row+1) != null && getPieceAt(col+1,row+1).get_color() == 1) {
							canMoveTo(col+1,row+1);
						}
						if(col+1 < 8 && row-1 >= 0 && getPieceAt(col+1,row-1) != null && getPieceAt(col+1,row-1).get_color() == 1) {
							canMoveTo(col+1,row-1);
						}
						if(col+1 < 8 && getPieceAt(col+1,row) == null) {
							canMoveTo(col+1, row);
							if(!getPieceAt(col,row).get_moved() && getPieceAt(col+2, row) == null) {
								canMoveTo(col+2, row);
							}
						}
					} else {
						if(col-1 >= 0 && row+1 < 8 && getPieceAt(col-1,row+1) != null && getPieceAt(col-1,row+1).get_color() == 0) {
							canMoveTo(col-1,row+1);
						}
						if(col-1 >= 0 && row-1 >= 0 && getPieceAt(col-1,row-1) != null && getPieceAt(col-1,row-1).get_color() == 0) {
							canMoveTo(col-1,row-1);
						}
						if(col-1 >= 0 && getPieceAt(col-1,row) == null) {
							canMoveTo(col-1, row);
							if(!getPieceAt(col,row).get_moved()) {
								canMoveTo(col-2, row);
							}
						}
					}
					break;
				case "rook":
					for (int x = col+1; x < 8; x++) {
						if (getPieceAt(x, row) != null && getPieceAt(x, row).get_color() == color) {
							break;
						}
						canMoveTo(x, row);
						if (getPieceAt(x, row) != null) {
							break;
						}
					}
					for (int y = row+1; y < 8; y++) {
						if (getPieceAt(col,y) != null && getPieceAt(col,y).get_color() == color) {
							break;
						}
						canMoveTo(col,y);
						if (getPieceAt(col,y) != null) {
							break;
						}
					}
					for (int x = col-1; x >= 0; x--) {
						if (getPieceAt(x, row) != null && getPieceAt(x, row).get_color() == color) {
							break;
						}
						canMoveTo(x, row);
						if (getPieceAt(x, row) != null) {
							break;
						}
					}
					for (int y = row-1; y >= 0; y--) {
						if (getPieceAt(col,y) != null && getPieceAt(col,y).get_color() == color) {
							break;
						}
						canMoveTo(col,y);
						if (getPieceAt(col,y) != null) {
							break;
						}
					}
					break;
				case "knight":
					if ((col+1 < 8 && row+2 < 8) && (getPieceAt(col+1,row+2) == null || (getPieceAt(col+1,row+2) != null && getPieceAt(col+1,row+2).get_color() != color))) {
						canMoveTo(col+1,row+2);
					}
					if ((col+2 < 8 && row+1 < 8) && (getPieceAt(col+2,row+1) == null || (getPieceAt(col+2,row+1) != null && getPieceAt(col+2,row+1).get_color() != color))) {
						canMoveTo(col+2,row+1);
					}
					if ((col-1 >= 0 && row+2 < 8) && (getPieceAt(col-1,row+2) == null || (getPieceAt(col-1,row+2) != null && getPieceAt(col-1,row+2).get_color() != color))) {
						canMoveTo(col-1,row+2);
					}
					if ((col-2 >= 0 && row+1 < 8) && (getPieceAt(col-2,row+1) == null || (getPieceAt(col-2,row+1) != null && getPieceAt(col-2,row+1).get_color() != color))) {
						canMoveTo(col-2,row+1);
					}
					if ((col+2 < 8 && row-1 >= 0) && (getPieceAt(col+2,row-1) == null || (getPieceAt(col+2,row-1) != null && getPieceAt(col+2,row-1).get_color() != color))) {
						canMoveTo(col+2,row-1);
					}
					if ((col+1 < 8 && row-2 >= 0) && (getPieceAt(col+1,row-2) == null || (getPieceAt(col+1,row-2) != null && getPieceAt(col+1,row-2).get_color() != color))) {
						canMoveTo(col+1,row-2);
					}
					if ((col-1 >= 0 && row-2 >= 0) && (getPieceAt(col-1,row-2) == null || (getPieceAt(col-1,row-2) != null && getPieceAt(col-1,row-2).get_color() != color))) {
						canMoveTo(col-1,row-2);
					}
					if ((col-2 >= 0 && row-1 >= 0) && (getPieceAt(col-2,row-1) == null || (getPieceAt(col-2,row-1) != null && getPieceAt(col-2,row-1).get_color() != color))) {
						canMoveTo(col-2,row-1);
					}
					break;
				case "bishop":
					i = 1;
					while (true) { // will break out
						if(col+i >= 8 || row+i >= 8) {
							break;
						}
						if(getPieceAt(col+i, row+i) != null && getPieceAt(col+i, row+i).get_color() == color) {
							break;
						}
						canMoveTo(col+i, row+i);
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
						canMoveTo(col-i, row-i);
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
						canMoveTo(col-i, row+i);
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
						canMoveTo(col+i, row-i);
						if (getPieceAt(col+i, row-i) != null) {
							break;
						}
						i++;
					}
					break;
				case "king":
					calculateAllCaptures((color+1)%2);
					for (int x = -1; x <= 1; x++) {
						for (int y = -1; y <=1; y++) {
							if (col+x >= 8 || row+y >= 8 || col+x < 0 || row+y < 0) { // don't got outside board
								continue;
							}
							if (getPieceAt(col+x,row+y) != null && getPieceAt(col+x,row+y).get_color() == color) { // don't select squares with pieces of the same color
								continue;
							}
							if ((color == 0 && whiteInCheck[col+x][row+y]) || (color == 1 && blackInCheck[col+x][row+y])) {
								continue;
							}
							canMoveTo(col+x, row+y);
						}
					}
					break;
				case "queen":
					for (int x = col+1; x < 8; x++) {
						if (getPieceAt(x, row) != null && getPieceAt(x, row).get_color() == color) {
							break;
						}
						canMoveTo(x, row);
						if (getPieceAt(x, row) != null) {
							break;
						}
					}
					for (int y = row+1; y < 8; y++) {
						if (getPieceAt(col,y) != null && getPieceAt(col,y).get_color() == color) {
							break;
						}
						canMoveTo(col,y);
						if (getPieceAt(col,y) != null) {
							break;
						}
					}
					for (int x = col-1; x >= 0; x--) {
						if (getPieceAt(x, row) != null && getPieceAt(x, row).get_color() == color) {
							break;
						}
						canMoveTo(x, row);
						if (getPieceAt(x, row) != null) {
							break;
						}
					}
					for (int y = row-1; y >= 0; y--) {
						if (getPieceAt(col,y) != null && getPieceAt(col,y).get_color() == color) {
							break;
						}
						canMoveTo(col,y);
						if (getPieceAt(col,y) != null) {
							break;
						}
					}
					i = 1;
					while (true) { // will break out
						if(col+i >= 8 || row+i >= 8) {
							break;
						}
						if(getPieceAt(col+i, row+i) != null && getPieceAt(col+i, row+i).get_color() == color) {
							break;
						}
						canMoveTo(col+i, row+i);
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
						canMoveTo(col-i, row-i);
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
						canMoveTo(col-i, row+i);
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
						canMoveTo(col+i, row-i);
						if (getPieceAt(col+i, row-i) != null) {
							break;
						}
						i++;
					}
					break;
				case "test":
					for (int x = 0; x < 8; x++) {
						for (int y = 0; y < 8; y++) {
							canMoveTo(x,y);
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
		board.clearThreat();
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
			int i = 1; // counter
			
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
					} else {
						blackCanCapture(col, y);
					}
					if (getPieceAt(col,y) != null) {
						break;
					}
				}
				break;
			case "knight":
				if ((col+1 < 8 && row+2 < 8) && (getPieceAt(col+1,row+2) == null || (getPieceAt(col+1,row+2) != null && getPieceAt(col+1,row+2).get_color() != color))) {
					if(color == 0) {
						whiteCanCapture(col+1,row+2);
					} else {
						blackCanCapture(col+1,row+2);
					}
				}
				if ((col+2 < 8 && row+1 < 8) && (getPieceAt(col+2,row+1) == null || (getPieceAt(col+2,row+1) != null && getPieceAt(col+2,row+1).get_color() != color))) {
					if(color == 0) {
						whiteCanCapture(col+2,row+1);
					} else {
						blackCanCapture(col+2,row+1);
					}
				}
				if ((col-1 >= 0 && row+2 < 8) && (getPieceAt(col-1,row+2) == null || (getPieceAt(col-1,row+2) != null && getPieceAt(col-1,row+2).get_color() != color))) {
					if(color == 0) {
						whiteCanCapture(col-1,row+2);
					} else {
						blackCanCapture(col-1,row+2);
					}
				}
				if ((col-2 >= 0 && row+1 < 8) && (getPieceAt(col-2,row+1) == null || (getPieceAt(col-2,row+1) != null && getPieceAt(col-2,row+1).get_color() != color))) {
					if(color == 0) {
						whiteCanCapture(col-2,row+1);
					} else {
						blackCanCapture(col-2,row+1);
					}
				}
				if ((col+2 < 8 && row-1 >= 0) && (getPieceAt(col+2,row-1) == null || (getPieceAt(col+2,row-1) != null && getPieceAt(col+2,row-1).get_color() != color))) {
					if(color == 0) {
						whiteCanCapture(col+2,row-1);
					} else {
						blackCanCapture(col+2,row-1);
					}
				}
				if ((col+1 < 8 && row-2 >= 0) && (getPieceAt(col+1,row-2) == null || (getPieceAt(col+1,row-2) != null && getPieceAt(col+1,row-2).get_color() != color))) {
					if(color == 0) {
						whiteCanCapture(col+1,row-2);
					} else {
						blackCanCapture(col+1,row-2);
					}
				}
				if ((col-1 >= 0 && row-2 >= 0) && (getPieceAt(col-1,row-2) == null || (getPieceAt(col-1,row-2) != null && getPieceAt(col-1,row-2).get_color() != color))) {
					if(color == 0) {
						whiteCanCapture(col-1,row-2);
					} else {
						blackCanCapture(col-1,row-2);
					}
				}
				if ((col-2 >= 0 && row-1 >= 0) && (getPieceAt(col-2,row-1) == null || (getPieceAt(col-2,row-1) != null && getPieceAt(col-2,row-1).get_color() != color))) {
					if(color == 0) {
						whiteCanCapture(col-2,row-1);
					} else {
						blackCanCapture(col-2,row-1);
					}
				}
				break;
			case "bishop":
				i = 1;
				while (true) { // will break out
					if(col+i >= 8 || row+i >= 8) {
						break;
					}
					if(getPieceAt(col+i, row+i) != null && getPieceAt(col+i, row+i).get_color() == color) {
						break;
					}
					if (color == 0) {
						whiteCanCapture(col+i, row+i);
					} else {
						blackCanCapture(col+i, row+i);
					}
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
					if (color == 0) {
						whiteCanCapture(col-i, row-i);
					} else {
						blackCanCapture(col-i, row-i);
					}
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
					if (color == 0) {
						whiteCanCapture(col-i, row+i);
					} else {
						blackCanCapture(col-i, row+i);
					}
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
					if (color == 0) {
						whiteCanCapture(col+i, row-i);
					} else {
						blackCanCapture(col+i, row-i);
					}
					if (getPieceAt(col+i, row-i) != null) {
						break;
					}
					i++;
				}
				break;
			case "king":
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
				for (int x = col+1; x < 8; x++) {
					if (getPieceAt(x, row) != null && getPieceAt(x, row).get_color() == color) {
						break;
					}
					if (color == 0) {
						whiteCanCapture(x, row);
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
					} else {
						blackCanCapture(col, y);
					}
					if (getPieceAt(col,y) != null) {
						break;
					}
				}
				i = 1;
				while (true) { // will break out
					if(col+i >= 8 || row+i >= 8) {
						break;
					}
					if(getPieceAt(col+i, row+i) != null && getPieceAt(col+i, row+i).get_color() == color) {
						break;
					}
					if (color == 0) {
						whiteCanCapture(col+i, row+i);
					} else {
						blackCanCapture(col+i, row+i);
					}
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
					if (color == 0) {
						whiteCanCapture(col-i, row-i);
					} else {
						blackCanCapture(col-i, row-i);
					}
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
					if (color == 0) {
						whiteCanCapture(col-i, row+i);
					} else {
						blackCanCapture(col-i, row+i);
					}
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
					if (color == 0) {
						whiteCanCapture(col+i, row-i);
					} else {
						blackCanCapture(col+i, row-i);
					}
					if (getPieceAt(col+i, row-i) != null) {
						break;
					}
					i++;
				}
				break;
			case "test":
				for (int x = 0; x < 8; x++) {
					for (int y = 0; y < 8; y++) {
						canMoveTo(x,y);
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
					calculateCapturesFrom(x, y);
				}
			}
		}
		System.out.println("Calculated all captures of prev team");
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
		calculateMovesFrom(kingCord[0], kingCord[1]);
		for (int x = -1; x <= 1; x++) {
			for(int y = -1; y <= 1; y++) {
				if (kingCord[0]+x < 0 || kingCord[1]+y < 0 || kingCord[0]+x >= 8 || kingCord[1]+y >= 8) {
					continue;
				}
				if(canMove[kingCord[0]+x][kingCord[1]+y]) {
					return false;
				}
			}
		}
		return true; 
	}
	/**
	 * 
	 * @param color whose turn it was BEFORE the move. the opposite of the king to look for
	 * @return
	 */
	public boolean checkForCheck(int color) { // whose turn it was BEFORE
		calculateAllCaptures(color);
		int[] kingCord = findKing((turns+1)%2);
		if (kingCord[0] < 0 || kingCord[1] < 0 || kingCord[0] >= 8 || kingCord[1] >= 8) {
			System.out.println("King not found");
			return false;
		} // this shouldn't happen, because the king has to exist
		if (color == 0) { // it was white's turn - black is in check
			System.out.println("Black in check: " + blackInCheck[kingCord[0]][kingCord[1]]);
			return blackInCheck[kingCord[0]][kingCord[1]];
		} else {
			
			System.out.println("White in check: " + whiteInCheck[kingCord[0]][kingCord[1]]);
			return whiteInCheck[kingCord[0]][kingCord[1]];
		}
		
	}
	
	public void endMove() {
		deselectSquare();
		
		System.out.println("checking for Check: ");
		if (checkForCheck(turns%2)) { // the king is in check!
			System.out.println("In check. Checking for Checkmate: ");
			if(checkForCheckmate(turns%2)) {
				if (turns%2 == 0) { // it was white's turn - white wins
					whiteWins();
				} else {
					blackWins();
				}
			} else if (turns%2 == 0) { // it was white's turn - black is in check
				blackInCheck();
			} else {
				whiteInCheck();
			}
			clearAllMoves();
		} else {
			clearCheck();
		}
		turns++;
		board.turn(turns);
	}
	
	public void startGame() {
		playing = true;
		turns = 0;
		board.startGame();
	}
	
	public void add_piece(chess_piece piece, int col, int row) {
		pieces[col][row] = piece;
		board.add_piece(piece, col, row);
	}
	
	public void move_piece(int col1, int row1, int col2, int row2) {
		pieces[col2][row2] = pieces[col1][row1];
		pieces[col1][row1] = null;
		board.move_piece(col1, row1, col2, row2);
	}
	
	public void kill_piece(int col, int row) {
		pieces[col][row] = null;
		board.kill_piece(col, row);
	}
	
	public void selectSquare(int col, int row) {
		board.highlightSquare(col, row);
		pieceSelected = true;
		selectedCol = col;
		selectedRow = row;
		calculateMovesFrom(col,row);
	}
	
	public void deselectSquare() {
		board.unhighlightSquare();
		pieceSelected = false;
		selectedCol = -1;
		selectedRow = -1;
		clearAllMoves();
	}
	
	public void canMoveTo(int col, int row) {
		canMove[col][row] = true;
		board.canMoveToSquare(col, row);
	}
	
	public void whiteCanCapture(int col, int row) {
		blackInCheck[col][row] = true;
		board.whiteCanCaptureSquare(col, row);
	}
	
	public void resetWhiteCanCapture() {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				blackInCheck[x][y] = false;
			}
		}
		board.resetWhiteCanCapture();
		System.out.println("resetted whiteCanCapture");
	}
	
	public void blackCanCapture(int col, int row) {
		whiteInCheck[col][row] = true;
		board.blackCanCaptureSquare(col, row);
	}
	
	public void resetBlackCanCapture() {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				whiteInCheck[x][y] = false;
			}
		}
		board.resetBlackCanCapture();
		System.out.println("resetted blackCanCapture");
	}
	
	public void whiteInCheck() {
		board.whiteInCheck();
	}
	
	public void blackInCheck() {
		board.blackInCheck();
	}
	
	public void whiteWins() {
		playing = false;
		turns = -1;
		board.whiteWins();
	}
	
	public void blackWins() {
		playing = false;
		turns = -1;
		board.blackWins();
	}
	
	public void clearCheck() {
		board.clearCheck();
	}
}