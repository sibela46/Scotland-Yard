# Scotland-Yard

## About
Scotland Yard is a board game in which a team of players, as police, cooperate to track down a player controlling a criminal around a board representing the streets of London. It is named after Scotland Yard, the headquarters of London's Metropolitan Police Service. Scotland Yard is an asymmetric board game, with the detective players cooperatively solving a variant of the pursuit-evasion problem. The game is published by Ravensburger in most of Europe and Canada and by Milton Bradley in the United States. It received the Spiel des Jahres (Game of the Year) award in 1983. (Wikipedia)

## Features
* Fully playable game Scotland-Yard, with an interactive UI.
* Follows popular OOP design patterns like **Builders, Singletons, Visitors, Observers, etc**
* A [player AI](https://github.com/TheWalkingFridge/Scotland-Yard-AI) for the player to play against.
* The AI uses mini-max and alpha-beta pruning to traverse a "move" tree in order to find the best move.
* The AI uses Dijkstra to find the minimum distance to other players.
* Written over 120 unit tests for each piece of functionality and edge case.

## Pre-requisites
* Java 7+
* Maven plugin
* Or build with Docker

## Build using Docker
### Build the image
```
make buildimage
```
### Build the jar file
```
make build
```
### Run the game from the container
```
make run
```
### Delete image and container
```
make clean
```
## TODO
* Add shared volume for savefiles
* make tests for running tests only
