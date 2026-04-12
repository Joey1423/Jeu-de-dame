class Dame extends Piece { // piece promue
    public Dame(char owner) { // constructeur
        super(owner); // init parent
    }

    @Override
    public boolean isDame() { // type piece
        return true; // c est une dame
    }
}
