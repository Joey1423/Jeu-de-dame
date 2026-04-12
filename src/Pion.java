class Pion extends Piece { // piece normale
    public Pion(char owner) { // constructeur
        super(owner); // init parent
    }

    @Override
    public boolean isDame() { // type piece
        return false; // pas une dame
    }
}
