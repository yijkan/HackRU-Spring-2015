package chess;

public class chess_piece {
	private String piece_type;
	private boolean made_at_least_one_move;
	private String color;
	
	public chess_piece(String piece_type, String color) {
		this.piece_type=piece_type;
		this.color=color;
		made_at_least_one_move=false;
	}
	public String get_piece_type() {
		return piece_type;
	}
	public boolean get_made_at_least_one_move() {
		return made_at_least_one_move;
	}
	public String get_color() {
		return color;
	}
	public void set_piece_type(String piece_type) {
		this.piece_type=piece_type;
	}
	public void set_made_at_least_one_move(boolean made_at_least_one_move) {
		this.made_at_least_one_move=made_at_least_one_move;
	}
	public void set_color(){
		this.color=color;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
