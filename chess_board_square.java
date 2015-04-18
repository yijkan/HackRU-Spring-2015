package chess;

public class chess_board_square {
	private boolean color;
	private chess_piece overlying_piece;
	
	public chess_board_square(boolean color) {
		this.color=color;
		this.overlying_piece=null;
	}
	public boolean get_colr() {
		return color;
	}
	public chess_piece get_overlying_piece(){
		return overlying_piece;
	}
	
	public void set_color(boolean color) {
		this.color=color;
	}
	public void set_overlying_piece(chess_piece overlying_piece){
		this.overlying_piece=overlying_piece;
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
