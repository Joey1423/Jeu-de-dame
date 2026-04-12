class Joueur { // entite joueur
    private final String nom; // pseudo joueur
    private final char couleur; // r ou b

    public Joueur(String nom, char couleur) { // constructeur
        this.nom = nom; // set nom
        this.couleur = couleur; // set couleur
    }

    public String getNom() { // getter nom
        return nom; // retour nom
    }

    public char getCouleur() { // getter couleur
        return couleur; // retour couleur
    }
}
