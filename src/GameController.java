import java.util.List; // import liste
import java.util.Random; // import aleatoire

class GameController { // controle partie
    public static class ClickResult { // resultat action
        public boolean selectionChanged; // selection modifiee
        public boolean moveExecuted; // coup joue
        public boolean wasRedTurn; // tour rouge avant coup
        public String moveText; // texte coup
        public Character winner; // gagnant si fin
    }

    private final int boardSize = 10; // taille plateau
    private final Plateau plateau = new Plateau(); // moteur regles
    private final Random random = new Random(); // rng IA
    private int selectedRow = -1; // ligne selection
    private int selectedCol = -1; // colonne selection
    private boolean gameOver; // partie terminee

    public char[][] getBoard() { // expose grille
        return plateau.getBoard(); // retour vue chars
    }

    public char getCurrentPlayer() { // expose joueur courant
        return plateau.getCurrentPlayer(); // retour joueur
    }

    public int getSelectedRow() { // expose ligne selection
        return selectedRow; // retour ligne
    }

    public int getSelectedCol() { // expose colonne selection
        return selectedCol; // retour colonne
    }

    public boolean isGameOver() { // expose etat fin
        return gameOver; // retour etat
    }

    public ClickResult handleClick(int row, int col) { // clic humain
        ClickResult result = new ClickResult(); // init resultat
        if (gameOver || !isInsideBoard(row, col)) { // clic ignore
            return result; // rien a faire
        }

        char clickedPiece = plateau.getBoard()[row][col]; // piece cliquee
        char currentPlayer = plateau.getCurrentPlayer(); // joueur courant

        if (selectedRow < 0) { // rien selectionne
            if (clickedPiece != '.' && Character.toLowerCase(clickedPiece) == currentPlayer) { // piece a soi
                selectedRow = row; // set ligne
                selectedCol = col; // set colonne
                result.selectionChanged = true; // notifier UI
            }
            return result; // fin phase select
        }

        if (clickedPiece != '.' && Character.toLowerCase(clickedPiece) == currentPlayer) { // changer piece
            selectedRow = row; // set ligne
            selectedCol = col; // set colonne
            result.selectionChanged = true; // notifier UI
            return result; // rester en selection
        }

        Move selectedMove = findLegalMove(selectedRow, selectedCol, row, col); // valider coup
        if (selectedMove != null) { // coup legal
            result.wasRedTurn = currentPlayer == 'r'; // sauver camp
            result.moveText = formatMove(selectedMove); // texte coup
            plateau.executeMove(selectedMove); // appliquer coup
            result.moveExecuted = true; // notifier coup
            checkGameOver(result); // verifier fin
        }

        selectedRow = -1; // clear ligne
        selectedCol = -1; // clear colonne
        result.selectionChanged = true; // notifier clear
        return result; // renvoyer resultat
    }

    public ClickResult playRandomMoveForCurrentPlayer() { // tour IA simple
        ClickResult result = new ClickResult(); // init resultat
        if (gameOver) { // deja fini
            return result; // stop
        }

        List<Move> legalMoves = plateau.getLegalMoves(); // coups legaux
        if (legalMoves.isEmpty()) { // aucun coup
            checkGameOver(result); // maj fin
            return result; // stop
        }

        Move selectedMove = legalMoves.get(random.nextInt(legalMoves.size())); // choix aleatoire
        result.wasRedTurn = plateau.getCurrentPlayer() == 'r'; // sauver camp
        result.moveText = formatMove(selectedMove); // texte coup
        plateau.executeMove(selectedMove); // jouer coup
        result.moveExecuted = true; // notifier coup

        selectedRow = -1; // clear ligne
        selectedCol = -1; // clear colonne
        result.selectionChanged = true; // notifier clear
        checkGameOver(result); // verifier fin
        return result; // renvoyer resultat
    }

    private Move findLegalMove(int fromRow, int fromCol, int toRow, int toCol) { // cherche coup exact
        List<Move> legalMoves = plateau.getLegalMoves(); // liste legale
        for (Move move : legalMoves) { // boucle coups
            if (move.fromRow == fromRow && move.fromCol == fromCol && move.toRow == toRow && move.toCol == toCol) { // match
                return move; // renvoyer coup
            }
        }
        return null; // pas trouve
    }

    private String formatMove(Move move) { // formater coup
        return positionToNotation(move.fromRow, move.fromCol) + " -> " + positionToNotation(move.toRow, move.toCol); // ex a3 -> b4
    }

    private String positionToNotation(int row, int col) { // coords vers notation
        char file = (char) ('a' + col); // lettre colonne
        int rank = boardSize - row; // numero ligne
        return "" + file + rank; // concat
    }

    private boolean isInsideBoard(int row, int col) { // borne plateau
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize; // test bornes
    }

    private void checkGameOver(ClickResult result) { // test fin partie
        char nextPlayer = plateau.getCurrentPlayer(); // joueur qui doit jouer
        char winner = nextPlayer == 'r' ? 'b' : 'r'; // camp gagnant
        if (!plateau.hasPieces(nextPlayer) || !plateau.hasLegalMoves(nextPlayer)) { // condition fin
            gameOver = true; // set fin
            result.winner = winner; // set gagnant
        }
    }
}
