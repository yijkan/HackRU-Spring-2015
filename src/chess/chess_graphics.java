package chess;

import processing.core.*; 
/* 
 * if this causes errors, right click the project and under Build Path,
 * choose Add External Archives
 * Find the folder where the project is located and choose processing-core.jar
 */

public class chess_graphics extends PApplet {
	private PImage[][] pieces = new PImage[8][8];
	private boolean[][] threatened = new boolean[8][8];
	private int sqDim = 50; // the dimensions of each square; depends on image dimensions
	private int winWidth = 8*sqDim + 400; // the width of the window
	private int winHeight = 8*sqDim + 100; // the height of the window
	private int highlightedX = 10000; // off screen
	private int highlightedY = 10000; // off screen
	private int status = -1; 
	/*
	 * -1 means nothing
	 * 0 means white in check
	 * 1 means black in check
	 * 2 means white victory
	 * 3 means black victory
	 */
	
	public void setup() {
		size(winWidth, winHeight);
		background(255);
	}
	
	public void draw() {
		fill(0);
		textSize(20);
		text("Player 1", 25, 50);
		text("Player 2", winWidth - 100, 50);

		// create all of the black squares
		fill (10);
		for (int x = 200; x <= winWidth - 250; x += sqDim * 2) {
			for (int y = 75; y <= winHeight - 75; y += sqDim * 2) {
				rect(x,y,sqDim,sqDim);
				rect(x+sqDim,y+sqDim,sqDim,sqDim);
			}
		}

		// create all of the white squares
		fill (245);
		for (int x = 200 + sqDim; x <= winWidth - 250; x += sqDim * 2) {
			for (int y = 75; y <= winHeight - 75; y += sqDim * 2) {
				rect(x,y,sqDim,sqDim);
				rect(x-sqDim,y+sqDim,sqDim,sqDim);
			}
		}
		
		//draw threatened squares
		fill(128);
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if(threatened[x][y]) {
					rect(x * sqDim + 200, y * sqDim + 75, sqDim, sqDim);
				}
			}
		}
		
		// draw highlighted square
		fill (0,0,255);
		rect(highlightedX * sqDim + 200, highlightedY * sqDim + 75, sqDim, sqDim);
		
		// add all pieces
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (pieces[x][y] != null) {
					image(pieces[x][y], 200 + x*sqDim, 75 + y*sqDim);
				}
			}
		}
		
//		trying to find out where to put pictures
//		PImage i = loadImage("test1.png");
//		image (i, 200, 75);
		
		// status message
		String mess = "";
		if(status == 0) {
			mess = "The white king is in check!";
		} else if (status == 1) {
			mess = "The black king is in check!";
		} else if (status == 2) {
			mess = "White wins!";
		} else if (status == 3) {
			mess = "Black wins!";
		}
		fill(255,0,0);
		textSize(20);
		text(mess, 650, 400);
	}
	
	public void add_piece(chess_piece piece, int col, int row) {
		String type = piece.get_piece_type();
		int color = piece.get_color();
		pieces[col][row] = loadImage(type + color + ".png");
	}
	
	public void move_piece(int col1, int row1, int col2, int row2) {
		pieces[col2][row2] = pieces[col1][row1];
		pieces[col1][row1] = null;
	}
	
	public void kill_piece(int col, int row) {
		pieces[col][row] = null;
	}
	
	public void threatenSquare(int col, int row) {
		threatened[col][row] = true;
	}
	
	public void clearThreat() {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				threatened[x][y] = false;
			}
		}
	}
	
	public void highlightSquare(int col, int row) {
		highlightedX = col;
		highlightedY = row;
	}
	
	public void unhighlightSquare() {
		highlightedX = 10000;
		highlightedY = 10000;
	}
	
	public void whiteInCheck() {
		status = 0;
	}
	
	public void blackInCheck() {
		status = 1;
	}
	
	public void whiteWins() {
		status = 2;
	}
	
	public void blackWins() {
		status = 3;
	}
}