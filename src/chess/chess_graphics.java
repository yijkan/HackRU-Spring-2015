package chess;

import processing.core.*; 
/* 
 * if this causes errors, right click the project and under Build Path,
 * choose Add External Archives
 * Find the folder where the project is located and choose processing-core.jar
 */

public class chess_graphics extends PApplet {
	public void setup() {
		size(800, 500);
		background(255);
	}
	
	public void draw() {
		fill(0);
		textSize(20);
		text("Player 1", 25, 50);
		text("Player 2", 700, 50);

		// create all of the black squares
		fill (10);
		for (int x = 200; x <= 550; x += 100) {
			for (int y = 75; y <= 425; y += 100) {
				rect(x,y,50,50);
				rect(x+50,y+50,50,50);
			}
		}

		// create all of the white squares
		fill (245);
		for (int x = 250; x <= 550; x += 100) {
			for (int y = 75; y <= 425; y += 100) {
				rect(x,y,50,50);
				rect(x-50,y+50,50,50);
			}
		}
	}
	
	public void add_piece(chess_piece piece, int row, int col) {
		
	}
	
	public void move_piece(int row1, int col1, int row2, int col2) {
		
	}
	
	public void kill_piece(int row, int col) {
		
	}
}