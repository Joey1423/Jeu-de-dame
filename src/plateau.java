import java.util.ArrayList;
import java.util.List;

class Plateau {
	private static final int BOARD_SIZE = 10;
	private static final int[][] ALL_DIAGONALS = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
	private static final int[][] RED_FORWARD = {{-1, -1}, {-1, 1}};
	private static final int[][] BLUE_FORWARD = {{1, -1}, {1, 1}};
	private char[][] board;
	private char currentPlayer;

	public Plateau() {
		this.board = initializeBoard();
		this.currentPlayer = 'r'; // Red starts
	}

	public void resetPlateau() {
		this.board = initializeBoard();
		this.currentPlayer = 'r';
	}

	private char[][] initializeBoard() {
		char[][] grid = new char[BOARD_SIZE][BOARD_SIZE];
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				grid[row][col] = '.';
			}
		}

		// Place blue pieces (top)
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				if ((row + col) % 2 == 1) {
					grid[row][col] = 'b';
				}
			}
		}

		// Place red pieces (bottom)
		for (int row = BOARD_SIZE - 4; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				if ((row + col) % 2 == 1) {
					grid[row][col] = 'r';
				}
			}
		}
		return grid;
	}

	public List<Move> getLegalMoves() {
		List<Move> captures = getCaptureMoves();
		return captures.isEmpty() ? getSimpleMoves() : captures;
	}

	private List<Move> getSimpleMoves() {
		List<Move> moves = new ArrayList<>();
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				char piece = board[row][col];
				if (!isCurrentPlayerPiece(piece)) {
					continue;
				}

				for (int[] dir : directionsFor(piece)) {
					int newRow = row + dir[0];
					int newCol = col + dir[1];
					if (isValidPosition(newRow, newCol) && board[newRow][newCol] == '.') {
						moves.add(new Move(row, col, newRow, newCol));
					}
				}
			}
		}
		return moves;
	}

	private List<Move> getCaptureMoves() {
		List<Move> captures = new ArrayList<>();
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				char piece = board[row][col];
				if (!isCurrentPlayerPiece(piece)) {
					continue;
				}
				List<Move> pieceCaptures = new ArrayList<>();
				findAllCaptures(row, col, new Move(row, col, row, col), pieceCaptures);
				captures.addAll(pieceCaptures);
			}
		}
		return captures;
	}

	private void findAllCaptures(int row, int col, Move currentMove, List<Move> allCaptures) {
		boolean foundFurther = false;
		for (int[] dir : directionsFor(board[row][col])) {
			int opponentRow = row + dir[0];
			int opponentCol = col + dir[1];
			int destRow = row + 2 * dir[0];
			int destCol = col + 2 * dir[1];

			if (isValidPosition(opponentRow, opponentCol) && 
				isValidPosition(destRow, destCol) &&
				board[destRow][destCol] == '.' &&
				isOpponentPiece(opponentRow, opponentCol)) {
				foundFurther = true;

				char capturedPiece = board[opponentRow][opponentCol];
				board[opponentRow][opponentCol] = '.';
				board[destRow][destCol] = board[row][col];
				board[row][col] = '.';

				int[] captured = {opponentRow, opponentCol};
				currentMove.capturedPieces.add(captured);
				currentMove.toRow = destRow;
				currentMove.toCol = destCol;

				findAllCaptures(destRow, destCol, currentMove, allCaptures);

				board[row][col] = board[destRow][destCol];
				board[opponentRow][opponentCol] = capturedPiece;
				board[destRow][destCol] = '.';

				currentMove.capturedPieces.remove(currentMove.capturedPieces.size() - 1);
				currentMove.toRow = row;
				currentMove.toCol = col;
			}
		}

		if (!foundFurther && !currentMove.capturedPieces.isEmpty()) {
			Move completedMove = new Move(currentMove.fromRow, currentMove.fromCol, 
										 currentMove.toRow, currentMove.toCol);
			completedMove.capturedPieces.addAll(currentMove.capturedPieces);
			allCaptures.add(completedMove);
		}
	}

	private int[][] directionsFor(char piece) {
		if (Character.isUpperCase(piece)) {
			return ALL_DIAGONALS;
		}
		return currentPlayer == 'r' ? RED_FORWARD : BLUE_FORWARD;
	}

	private boolean isCurrentPlayerPiece(char piece) {
		return piece != '.' && Character.toLowerCase(piece) == currentPlayer;
	}

	private boolean isValidPosition(int row, int col) {
		return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE 
			&& (row + col) % 2 == 1; // Case sombre
	}

	private boolean isOpponentPiece(int row, int col) {
		char piece = board[row][col];
		return piece != '.' && Character.toLowerCase(piece) != currentPlayer;
	}

	// ===== EXECUTER UN MOUVEMENT =====
	public void executeMove(Move move) {
		char piece = board[move.fromRow][move.fromCol];
		board[move.toRow][move.toCol] = piece;
		board[move.fromRow][move.fromCol] = '.';
		
		// Capturer les pions
		for (int[] capturedPos : move.capturedPieces) {
			board[capturedPos[0]][capturedPos[1]] = '.';
		}
		
		// Promotion dame (RED 'r' -> 'R' at row 0, BLUE 'b' -> 'B' at row 9)
		if (piece == 'r' && move.toRow == 0) {
			board[move.toRow][move.toCol] = 'R';
		} else if (piece == 'b' && move.toRow == 9) {
			board[move.toRow][move.toCol] = 'B';
		}
		
		// Changer joueur
		currentPlayer = (currentPlayer == 'r') ? 'b' : 'r';
	}

	public char[][] getBoard() {
		return board;
	}

	public char getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(char player) {
		this.currentPlayer = player;
	}

	public void display() {
		System.out.println("  a b c d e f g h i j");
		for (int row = 0; row < BOARD_SIZE; row++) {
			int displayRow = BOARD_SIZE - row;
			if (displayRow < 10) {
				System.out.print(displayRow + " ");
			} else {
				System.out.print(displayRow);
			}
			for (int col = 0; col < BOARD_SIZE; col++) {
				System.out.print(board[row][col] + " ");
			}
			System.out.println();
		}
		System.out.println("Joueur actuel: " + currentPlayer);
	}
}

class Move {
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
