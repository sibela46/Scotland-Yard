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
* IntelliJ / Eclipse
* Maven plugin

## Licence
MIT License

Copyright (c) 2017 Scotland-Yard

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
