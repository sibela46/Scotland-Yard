# Scotland-Yard

## About
This is a game recreation of the popular table game Scotland-Yard. The whole project was part of the University of Bristol OOP curriculum. The main focus was put on writing DRY, well-structured code that follows conventions whilst implementing many OOP design patterns. An additional [player AI](https://github.com/TheWalkingFridge/Scotland-Yard-AI) was also build.

The game was solely writen in Java using Maven

## Code status
[![MIT license](https://img.shields.io/badge/License-MIT-blue.svg)](https://lbesson.mit-license.org/) [![Build status](https://travis-ci.org/google/licenseclassifier.svg?branch=master)](https://travis-ci.org/google/licenseclassifier)

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
make install
```
### Run the container
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
