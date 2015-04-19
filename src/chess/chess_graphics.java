package chess;

import processing.core.*; 
/* 
 * if this causes errors, right click the project and under Build Path,
 * choose Add External Archives
 * Find the folder where the project is located and choose processing-core.jar
 */

public class chess_graphics extends PApplet {
	private PImage[][] pieces = new PImage[8][8];
	private int sqDim = 50; // the dimensions of each square; depends on image dimensions
	private int winWidth = 8*sqDim + 400; // the width of the window
	private int winHeight = 8*sqDim + 100; // the height of the window
	private int highlightedX = 10000; // off screen
	private int highlightedY = 10000; // off screen
	
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
//		PImage i = loadImage("blacktest.png");
//		image (i, 200, 75);
	}
	
	public void add_piece(chess_piece piece, int col, int row) {
		String type = piece.get_piece_type();
		String color = piece.get_color();
		pieces[col][row] = loadImage(color + type + ".png");
	}
	
	public void move_piece(int col1, int row1, int col2, int row2) {
		pieces[col2][row2] = pieces[col1][row1];
		pieces[col1][row1] = null;
	}
	
	public void kill_piece(int col, int row) {
		pieces[col][row] = null;
	}
	
	public void highlightSquare(int col, int row) {
		highlightedX = col;
		highlightedY = row;
		System.out.println("X highlighted: " + col + "\nY highlighted: " + row);
	}
	
	public void unhighlightSquare() {
		highlightedX = 10000;
		highlightedY = 10000;
	}
}