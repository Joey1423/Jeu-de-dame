import java.util.ArrayList;
import java.util.List;

public class Move {
	public int fromRow;
	public int fromCol;
	public int toRow;
	public int toCol;
	public List<int[]> capturedPieces;

	public Move(int fromRow, int fromCol, int toRow, int toCol) {
		this.fromRow = fromRow;
		this.fromCol = fromCol;
		this.toRow = toRow;
		this.toCol = toCol;
		this.capturedPieces = new ArrayList<>();
	}

	@Override
	public String toString() {
		return String.format("Move({%d,%d} -> {%d,%d})", fromRow, fromCol, toRow, toCol);
	}
}
