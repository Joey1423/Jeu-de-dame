class Case { // case du plateau
    private Piece piece; // piece possiblement null

    public boolean isEmpty() { // test vide
        return piece == null; // true si null
    }

    public Piece getPiece() { // getter piece
        return piece; // retour piece
    }

    public void setPiece(Piece piece) { // setter piece
        this.piece = piece; // affecter piece
    }

    public void clear() { // vider case
        this.piece = null; // reset null
    }

    public char toBoardChar() { // export char
        return isEmpty() ? '.' : piece.toBoardChar(); // . ou piece
    }
}
