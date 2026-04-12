import java.util.ArrayList; // import liste dynamique
import java.util.List; // import interface liste

class Plateau { // classe plateau
	private static final int BOARD_SIZE = 10; // taille 10x10
	private static final int[][] ALL_DIAGONALS = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}}; // dirs dame
	private static final int[][] RED_FORWARD = {{-1, -1}, {-1, 1}}; // dirs rouge
	private static final int[][] BLUE_FORWARD = {{1, -1}, {1, 1}}; // dirs bleu
	private Case[][] board; // grille objet
	private char currentPlayer; // joueur courant

	public Plateau() { // constructeur
		this.board = initializeBoard(); // init grille
		this.currentPlayer = 'r'; // rouge commence
	}

	public void resetPlateau() { // reset partie
		this.board = initializeBoard(); // reset grille
		this.currentPlayer = 'r'; // reset tour
	}

	private Case[][] initializeBoard() { // creer plateau initial
		Case[][] grid = new Case[BOARD_SIZE][BOARD_SIZE]; // allouer grille
		for (int row = 0; row < BOARD_SIZE; row++) { // boucle lignes
			for (int col = 0; col < BOARD_SIZE; col++) { // boucle colonnes
				grid[row][col] = new Case(); // case vide
			}
		}

		for (int row = 0; row < 4; row++) { // placer bleus haut
			for (int col = 0; col < BOARD_SIZE; col++) { // boucle colonnes
				if ((row + col) % 2 == 1) { // seulement case sombre
					grid[row][col].setPiece(new Pion('b')); // pion bleu
				}
			}
		}

		for (int row = BOARD_SIZE - 4; row < BOARD_SIZE; row++) { // placer rouges bas
			for (int col = 0; col < BOARD_SIZE; col++) { // boucle colonnes
				if ((row + col) % 2 == 1) { // seulement case sombre
					grid[row][col].setPiece(new Pion('r')); // pion rouge
				}
			}
		}
		return grid; // renvoyer grille
	}

	public List<Move> getLegalMoves() { // coups legaux
		List<Move> captures = getCaptureMoves(); // chercher captures
		return captures.isEmpty() ? getSimpleMoves() : captures; // capture prioritaire
	}

	private List<Move> getSimpleMoves() { // coups simples
		List<Move> moves = new ArrayList<>(); // resultat
		for (int row = 0; row < BOARD_SIZE; row++) { // boucle lignes
			for (int col = 0; col < BOARD_SIZE; col++) { // boucle colonnes
				Piece piece = board[row][col].getPiece(); // lire piece
				if (!isCurrentPlayerPiece(piece)) { // ignorer autres pieces
					continue; // passer suite
				}

				if (piece.isDame()) { // dame = loin
					for (int[] dir : directionsFor(piece)) { // directions diagonales
						for (int step = 1; ; step++) { // avance libre
							int newRow = row + dir[0] * step; // ligne destination
							int newCol = col + dir[1] * step; // colonne destination
							if (!isValidPosition(newRow, newCol) || !board[newRow][newCol].isEmpty()) { // blocage
								break; // stop direction
							}
							moves.add(new Move(row, col, newRow, newCol)); // ajouter coup
						}
					}
				} else { // pion normal
					for (int[] dir : directionsFor(piece)) { // parcourir directions
						int newRow = row + dir[0]; // ligne destination
						int newCol = col + dir[1]; // colonne destination
						if (isValidPosition(newRow, newCol) && board[newRow][newCol].isEmpty()) { // deplacement valide
							moves.add(new Move(row, col, newRow, newCol)); // ajouter coup
						}
					}
				}
			}
		}
		return moves; // renvoyer liste
	}

	private List<Move> getCaptureMoves() { // coups capture
		List<Move> captures = new ArrayList<>(); // resultat
		for (int row = 0; row < BOARD_SIZE; row++) { // boucle lignes
			for (int col = 0; col < BOARD_SIZE; col++) { // boucle colonnes
				Piece piece = board[row][col].getPiece(); // lire piece
				if (!isCurrentPlayerPiece(piece)) { // ignorer autres pieces
					continue; // passer suite
				}
				List<Move> pieceCaptures = new ArrayList<>(); // captures de cette piece
				findAllCaptures(row, col, new Move(row, col, row, col), pieceCaptures); // lancer recherche
				captures.addAll(pieceCaptures); // fusionner resultat
			}
		}
		return captures; // renvoyer liste
	}

	private void findAllCaptures(int row, int col, Move currentMove, List<Move> allCaptures) { // capture recursive
		boolean foundFurther = false; // drapeau capture suivante
		Piece piece = board[row][col].getPiece(); // piece courante
		for (int[] dir : directionsFor(piece)) { // parcourir directions
			if (piece.isDame()) { // dame = capture longue
				int scanRow = row + dir[0]; // scan ligne
				int scanCol = col + dir[1]; // scan colonne
				while (isValidPosition(scanRow, scanCol) && board[scanRow][scanCol].isEmpty()) { // cases vides
					scanRow += dir[0]; // avancer ligne
					scanCol += dir[1]; // avancer colonne
				}
				if (!isValidPosition(scanRow, scanCol) || !isOpponentPiece(board[scanRow][scanCol].getPiece())) { // pas d'adversaire
					continue; // prochaine dir
				}

				int opponentRow = scanRow; // ligne adverse
				int opponentCol = scanCol; // colonne adverse
				scanRow += dir[0]; // case apres adversaire
				scanCol += dir[1]; // case apres adversaire
				while (isValidPosition(scanRow, scanCol) && board[scanRow][scanCol].isEmpty()) { // landing libre
					foundFurther = true; // au moins une capture
					Piece capturedPiece = board[opponentRow][opponentCol].getPiece(); // sauver piece captee
					board[opponentRow][opponentCol].clear(); // retirer adversaire
					board[scanRow][scanCol].setPiece(piece); // poser dame
					board[row][col].clear(); // vider origine

					currentMove.capturedPieces.add(new int[]{opponentRow, opponentCol}); // stocker capture
					currentMove.toRow = scanRow; // maj fin ligne
					currentMove.toCol = scanCol; // maj fin colonne

					findAllCaptures(scanRow, scanCol, currentMove, allCaptures); // recursion

					board[row][col].setPiece(piece); // restaurer origine
					board[opponentRow][opponentCol].setPiece(capturedPiece); // restaurer adversaire
					board[scanRow][scanCol].clear(); // nettoyer destination

					currentMove.capturedPieces.remove(currentMove.capturedPieces.size() - 1); // retirer capture
					currentMove.toRow = row; // restaurer fin ligne
					currentMove.toCol = col; // restaurer fin colonne

					scanRow += dir[0]; // prochaine landing
					scanCol += dir[1]; // prochaine landing
				}
			} else { // pion normal = capture courte
				int opponentRow = row + dir[0]; // ligne adversaire
				int opponentCol = col + dir[1]; // colonne adversaire
				int destRow = row + 2 * dir[0]; // ligne destination
				int destCol = col + 2 * dir[1]; // colonne destination

				if (isValidPosition(opponentRow, opponentCol) && // adversaire dans plateau
					isValidPosition(destRow, destCol) && // destination dans plateau
					board[destRow][destCol].isEmpty() && // destination libre
					isOpponentPiece(board[opponentRow][opponentCol].getPiece())) { // case intermediaire adverse
					foundFurther = true; // au moins une capture

					Piece capturedPiece = board[opponentRow][opponentCol].getPiece(); // sauver piece captee
					board[opponentRow][opponentCol].clear(); // retirer adversaire
					board[destRow][destCol].setPiece(piece); // deplacer piece
					board[row][col].clear(); // vider origine

					currentMove.capturedPieces.add(new int[]{opponentRow, opponentCol}); // stocker capture
					currentMove.toRow = destRow; // maj fin ligne
					currentMove.toCol = destCol; // maj fin colonne

					findAllCaptures(destRow, destCol, currentMove, allCaptures); // recursion

					board[row][col].setPiece(piece); // restaurer origine
					board[opponentRow][opponentCol].setPiece(capturedPiece); // restaurer adversaire
					board[destRow][destCol].clear(); // nettoyer destination

					currentMove.capturedPieces.remove(currentMove.capturedPieces.size() - 1); // retirer capture
					currentMove.toRow = row; // restaurer fin ligne
					currentMove.toCol = col; // restaurer fin colonne
				}
			}
		}

		if (!foundFurther && !currentMove.capturedPieces.isEmpty()) { // fin de sequence
			Move completedMove = new Move(currentMove.fromRow, currentMove.fromCol, // recopie debut
										 currentMove.toRow, currentMove.toCol); // recopie fin
			completedMove.capturedPieces.addAll(currentMove.capturedPieces); // recopie captures
			allCaptures.add(completedMove); // ajouter sequence finale
		}
	}

	private int[][] directionsFor(Piece piece) { // directions autorisees
		if (piece.isDame()) { // si dame
			return ALL_DIAGONALS; // 4 diagonales
		}
		return currentPlayer == 'r' ? RED_FORWARD : BLUE_FORWARD; // sens normal selon joueur
	}

	private boolean isCurrentPlayerPiece(Piece piece) { // piece du joueur courant
		return piece != null && piece.isOwner(currentPlayer); // test appartenance
	}

	private boolean isValidPosition(int row, int col) { // case jouable valide
		return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE // dans limites
			&& (row + col) % 2 == 1; // case sombre
	}

	private boolean isOpponentPiece(Piece piece) { // piece adverse
		return piece != null && !piece.isOwner(currentPlayer); // test adversaire
	}

	public void executeMove(Move move) { // appliquer coup
		Piece piece = board[move.fromRow][move.fromCol].getPiece(); // piece source
		board[move.toRow][move.toCol].setPiece(piece); // poser destination
		board[move.fromRow][move.fromCol].clear(); // vider source

		for (int[] capturedPos : move.capturedPieces) { // supprimer captures
			board[capturedPos[0]][capturedPos[1]].clear(); // vider case captee
		}

		if (piece != null && !piece.isDame()) { // promotion pion
			if (piece.isOwner('r') && move.toRow == 0) { // promo rouge
				board[move.toRow][move.toCol].setPiece(new Dame('r')); // devient dame rouge
			} else if (piece.isOwner('b') && move.toRow == 9) { // promo bleu
				board[move.toRow][move.toCol].setPiece(new Dame('b')); // devient dame bleue
			}
		}

		currentPlayer = (currentPlayer == 'r') ? 'b' : 'r'; // changer tour
	}

	public char[][] getBoard() { // getter grille
		char[][] snapshot = new char[BOARD_SIZE][BOARD_SIZE]; // vue char pour UI
		for (int row = 0; row < BOARD_SIZE; row++) { // boucle lignes
			for (int col = 0; col < BOARD_SIZE; col++) { // boucle colonnes
				snapshot[row][col] = board[row][col].toBoardChar(); // convertir
			}
		}
		return snapshot; // renvoyer grille
	}

	public char getCurrentPlayer() { // getter joueur
		return currentPlayer; // renvoyer joueur
	}

	public void setCurrentPlayer(char player) { // setter joueur
		this.currentPlayer = player; // maj joueur
	}

	public boolean hasPieces(char player) { // pieces restantes
		for (int row = 0; row < BOARD_SIZE; row++) { // boucle lignes
			for (int col = 0; col < BOARD_SIZE; col++) { // boucle colonnes
				Piece piece = board[row][col].getPiece(); // lire piece
				if (piece != null && piece.isOwner(player)) { // piece trouvee
					return true; // oui
				}
			}
		}
		return false; // non
	}

	public boolean hasLegalMoves(char player) { // coups restants
		char savedPlayer = currentPlayer; // sauvegarder tour
		currentPlayer = player; // tester joueur
		boolean hasMoves = !getLegalMoves().isEmpty(); // verifier coups
		currentPlayer = savedPlayer; // restaurer tour
		return hasMoves; // renvoyer resultat
	}

	public void display() { // affichage console
		System.out.println("  a b c d e f g h i j"); // entete colonnes
		char[][] snapshot = getBoard(); // vue imprimable
		for (int row = 0; row < BOARD_SIZE; row++) { // boucle lignes
			int displayRow = BOARD_SIZE - row; // numero visuel
			if (displayRow < 10) { // aligner 1 chiffre
				System.out.print(displayRow + " "); // print num
			} else { // deja 2 chiffres
				System.out.print(displayRow); // print num
			}
			for (int col = 0; col < BOARD_SIZE; col++) { // boucle colonnes
				System.out.print(snapshot[row][col] + " "); // print case
			}
			System.out.println(); // saut ligne
		}
		System.out.println("Joueur actuel: " + currentPlayer); // print tour
	}
}

class Move { // modele coup
	public int fromRow; // origine ligne
	public int fromCol; // origine colonne
	public int toRow; // destination ligne
	public int toCol; // destination colonne
	public List<int[]> capturedPieces; // positions capturees

	public Move(int fromRow, int fromCol, int toRow, int toCol) { // constructeur
		this.fromRow = fromRow; // set origine ligne
		this.fromCol = fromCol; // set origine colonne
		this.toRow = toRow; // set destination ligne
		this.toCol = toCol; // set destination colonne
		this.capturedPieces = new ArrayList<>(); // init liste captures
	}

	@Override
	public String toString() { // debug texte
		return String.format("Move({%d,%d} -> {%d,%d})", fromRow, fromCol, toRow, toCol); // format affichage
	}
}
