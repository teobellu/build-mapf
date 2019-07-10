# build-mapf

[![Build Status](https://travis-ci.org/teobellu/build-mapf.svg?branch=master)](https://travis-ci.org/teobellu/build-mapf)
[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/teobellu/build-mapf)

The program solves 5 random MAPF instances using CBS (high-level) + EPEA* (low-level). Outputs are shown through 2D-visualization.

## How to start :rocket:
Double-click on `build.jar` or run from terminal the following command:

```
$ java -jar build.jar
```
Alternatively double-click on `build.bat` or run from terminal the following command:

```
$ javaw.exe -jar build.jar
```

You can also right-click the `build.jar` file, select <i>open with</i> and choose <i>Java Runtime Environment (JRE)</i>.

Further reference: [Italian guide](https://www.aranzulla.it/come-aprire-file-jar-927218.html)

## Assumptions :memo:

The solver is based on two assumptions:

* swap actions are allowed, hence there are no edge collision; 

* wait actions on the goal have a unitary cost.
