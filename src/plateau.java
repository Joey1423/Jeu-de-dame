import java.util.ArrayList;
import java.util.List;

public class Plateau {
	private static final int BOARD_SIZE = 10;
	private char[][] board;
	private char currentPlayer;

	public Plateau() {
		this.board = initializeBoard();
		this.currentPlayer = 'r'; // Red starts
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

	// ===== OBTENIR TOUS LES COUPS LEGAUX (REGLE: CAPTURES OBLIGATOIRES) =====
	public List<Move> getLegalMoves() {
		List<Move> captures = getCaptureMoves();
		
		// RÈGLE CRITIQUE: Si des captures existent, SEULES elles sont légales
		if (!captures.isEmpty()) {
			return captures;
		}
		
		// Sinon, retourner les mouvements simples
		return getSimpleMoves();
	}

	// ===== OBTENIR LES MOUVEMENTS SIMPLES =====
	private List<Move> getSimpleMoves() {
		List<Move> moves = new ArrayList<>();
		
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				char piece = board[row][col];
				if (piece == '.' || Character.toLowerCase(piece) != currentPlayer) {
					continue;
				}
				
				boolean isDame = Character.isUpperCase(piece);
				
				// Directions possibles
				int[][] directions = isDame 
					? new int[][] {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}} // Dame: toutes les diagonales
					: (currentPlayer == 'r' 
						? new int[][] {{-1, -1}, {-1, 1}} // Rouge: vers le haut
						: new int[][] {{1, -1}, {1, 1}}); // Bleu: vers le bas
				
				for (int[] dir : directions) {
					int newRow = row + dir[0];
					int newCol = col + dir[1];
					
					if (isValidPosition(newRow, newCol) && board[newRow][newCol] == '.') {
						Move move = new Move(row, col, newRow, newCol);
						moves.add(move);
					}
				}
			}
		}
		
		return moves;
	}

	// ===== OBTENIR LES CAPTURES OBLIGATOIRES =====
	private List<Move> getCaptureMoves() {
		List<Move> captures = new ArrayList<>();
		
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				char piece = board[row][col];
				if (piece == '.' || Character.toLowerCase(piece) != currentPlayer) {
					continue;
				}
				
				// Chercher les captures multiples pour ce pion
				List<Move> startingCaptures = new ArrayList<>();
				Move tempMove = new Move(row, col, row, col);
				findAllCaptures(row, col, tempMove, startingCaptures);
				
				captures.addAll(startingCaptures);
			}
		}
		
		return captures;
	}

	// ===== TROUVE LES CAPTURES MULTIPLES (RECURSIF) =====
	private void findAllCaptures(int row, int col, Move currentMove, List<Move> allCaptures) {
		boolean foundFurther = false;
		boolean isDame = Character.isUpperCase(board[row][col]);
		
		// Directions possibles pour capturer
		int[][] directions = isDame 
			? new int[][] {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}}
			: (currentPlayer == 'r' 
				? new int[][] {{-1, -1}, {-1, 1}}
				: new int[][] {{1, -1}, {1, 1}});
		
		for (int[] dir : directions) {
			int opponentRow = row + dir[0];
			int opponentCol = col + dir[1];
			int destRow = row + 2 * dir[0];
			int destCol = col + 2 * dir[1];
			
			// Vérifie si la capture est possible
			if (isValidPosition(opponentRow, opponentCol) && 
				isValidPosition(destRow, destCol) &&
				board[destRow][destCol] == '.' &&
				isOpponentPiece(opponentRow, opponentCol)) {
				
				foundFurther = true;
				
				// Faire la capture temporairement
				char capturedPiece = board[opponentRow][opponentCol];
				board[opponentRow][opponentCol] = '.';
				board[destRow][destCol] = board[row][col];
				board[row][col] = '.';
				
				// Ajouter à la capture courante
				int[] captured = {opponentRow, opponentCol};
				currentMove.capturedPieces.add(captured);
				currentMove.toRow = destRow;
				currentMove.toCol = destCol;
				
				// Continuer la recherche récursive
				findAllCaptures(destRow, destCol, currentMove, allCaptures);
				
				// Restaurer l'état
				board[row][col] = board[destRow][destCol];
				board[opponentRow][opponentCol] = capturedPiece;
				board[destRow][destCol] = '.';
				
				// Retirer de la capture courante
				currentMove.capturedPieces.remove(currentMove.capturedPieces.size() - 1);
				currentMove.toRow = row;
				currentMove.toCol = col;
			}
		}
		
		// Si aucune capture supplémentaire n'est trouvée, ajouter cette séquence
		if (!foundFurther && !currentMove.capturedPieces.isEmpty()) {
			Move completedMove = new Move(currentMove.fromRow, currentMove.fromCol, 
										 currentMove.toRow, currentMove.toCol);
			completedMove.capturedPieces.addAll(currentMove.capturedPieces);
			allCaptures.add(completedMove);
		}
	}

	// ===== VERIFICATIONS UTILITAIRES =====
	private boolean isValidPosition(int row, int col) {
		return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE 
			&& (row + col) % 2 == 1; // Case sombre
	}

	private boolean isOpponentPiece(int row, int col) {
		char piece = board[row][col];
		if (piece == '.') return false;
		return Character.toLowerCase(piece) != currentPlayer;
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

	// ===== GETTERS =====
	public char[][] getBoard() {
		return board;
	}

	public char getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(char player) {
		this.currentPlayer = player;
	}

	// ===== AFFICHER LE PLATEAU =====
	public void display() {
		System.out.println("   0 1 2 3 4 5 6 7 8 9");
		for (int row = 0; row < BOARD_SIZE; row++) {
			System.out.print(row + " ");
			for (int col = 0; col < BOARD_SIZE; col++) {
				System.out.print(board[row][col] + " ");
			}
			System.out.println();
		}
		System.out.println("Joueur actuel: " + currentPlayer);
	}
}
