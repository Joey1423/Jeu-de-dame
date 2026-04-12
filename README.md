# Jeu de Dames

Application Java Swing de jeu de dames avec interface graphique, choix des prenoms et choix d'arene.

## Apercu

Le projet propose une interface simple et navigable pour lancer une partie de dames. L'objectif est de garder une base claire, orientee objet, et facile a faire evoluer.

## Fonctionnalites

- ecran d'accueil
- selection du mode de jeu
- mode 1v1 local
- saisie des prenoms en 1v1
- choix de map / arene
- plateau de jeu 10x10
- historique des deplacements
- promotion en dame et deplacements/captures de dame
- detection de fin de partie

## Pre-requis

- Java installe sur la machine
- un terminal ou PowerShell pour lancer les commandes

## Lancement

1. Compiler le projet :

```powershell
javac -encoding UTF-8 src\*.java
```

2. Lancer l'application :

```powershell
java -cp src game
```

## Organisation

Le projet est organise en couches simples. Les fichiers principaux sont :

- `src/game.java` : interface Swing principale et ecrans du jeu
- `src/GameController.java` : controle des interactions de jeu (selection, coup, fin)
- `src/plateau.java` : logique des regles, coups legaux, captures, tour
- `src/Case.java` : case du plateau
- `src/Piece.java` : classe abstraite de piece
- `src/Pion.java` : piece standard
- `src/Dame.java` : piece promue
- `src/Joueur.java` : entite joueur (nom, couleur)

## Notes

- Le projet reste sans IA obligatoire.
- La structure separe deja l'interface et la logique de jeu pour coller a une approche type MVC.
