package chess;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

public class Test extends JFrame {
	private chess_graphics board;
	
	public static void main(String[] args) {
		Test test = new Test();
		chess_piece testPiece = new chess_piece("test", "black");
		test.getBoard().add_piece(testPiece, 0, 0); 
		test.getBoard().kill_piece(0, 0);
	}
	
	public Test() {
		super("Testing chess");
		setSize(800, 550);
		setResizable(false);
		
		Container c = getContentPane();
		c.setLayout(new BoxLayout(c, BoxLayout.PAGE_AXIS));
		c.setBackground(new Color(255,255,255));
		
		board = new chess_graphics();
		c.add(board, BorderLayout.NORTH);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
			
		});
		
		board.init();
		setVisible(true);
	}
	
	public chess_graphics getBoard() {
		return board;
	}
}
