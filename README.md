# Jeu de Dames

Application Java Swing de jeu de dames avec interface graphique, selection du mode de jeu, choix des prenoms, choix d'arene et mode contre IA.

## Apercu

Le projet propose une interface simple et navigable pour lancer une partie de dames. L'objectif est de garder une base claire, facile a modifier, tout en gardant le code principal fonctionnel.

## Fonctionnalites

- ecran d'accueil
- selection du mode de jeu
- mode 1v1
- mode contre IA
- saisie des prenoms en 1v1
- choix de map / arene
- plateau de jeu 10x10

## Pre-requis

- Java installe sur la machine
- un terminal ou PowerShell pour lancer les commandes

## Lancement

1. Compiler le projet :

```powershell
javac -cp src -d src src/game.java src/Plateau.java
```

2. Lancer l'application :

```powershell
java -cp src game
```

## Organisation

Le projet est volontairement compact. Les fichiers principaux sont :

- `src/game.java` : interface Swing principale et ecrans du jeu
- `src/Plateau.java` : logique du plateau, des coups et de la gestion des pions

## Notes

- La structure peut etre decoupee plus tard si tu veux separer l'interface, la logique et l'IA.
- Le projet est pense pour rester simple a comprendre et a faire evoluer.
