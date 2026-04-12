abstract class Piece { // piece de base
    private final char owner; // proprietaire r/b

    protected Piece(char owner) { // constructeur
        this.owner = owner; // set proprietaire
    }

    public char getOwner() { // getter owner
        return owner; // retour owner
    }

    public boolean isOwner(char player) { // test proprio
        return owner == player; // comparaison
    }

    public abstract boolean isDame(); // type dame/pion

    public char toBoardChar() { // export char plateau
        return isDame() ? Character.toUpperCase(owner) : owner; // maj si dame
    }
}
