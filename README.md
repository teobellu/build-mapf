# build-mapf

[![Build Status](https://travis-ci.org/teobellu/build-mapf.svg?branch=master)](https://travis-ci.org/teobellu/build-mapf)
[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/teobellu/build-mapf)

The program solves 5 random MAPF instances using CBS (high-level) + EPEA* (low-level). Outputs are shown through 2D-visualization.

## How to start :rocket:
Double-click on `build.jar` or run from terminal one of the following commands:

```
$ java -jar build.jar
$ javaw.exe -jar build.jar
```
Alternatively, double-click on `build.bat`. You can also right-click the `build.jar` file, select <i>open with</i> and choose <i>Java Runtime Environment (JRE)</i>.

### Problems? :memo:

There are several solutions, here is one of the fastest (follow these steps):

1. Install the latest version of JDK: [check for releases](https://www.oracle.com/technetwork/java/javase/downloads/index.html)

2. Search for the new JDK folder. Probably the folder lies in `C:\Program Files\Java\`

3. Open the new JDK folder. Therefore, open the `bin` folder

4. Place here the `build.jar` file and the `build.bat` file

5. Create a shortcut link for `build.bat` file and place it in the folder you prefer

6. Double-click on the shortcut link just created

Still problems? [Check for JDK documentation](https://docs.oracle.com/javase/10/install/installation-jdk-and-jre-microsoft-windows-platforms.htm#JSJIG-GUID-A7E27B90-A28D-4237-9383-A58B416071CA).

## Assumptions :memo:

The solver is based on two assumptions:

* swap actions are allowed, hence there are no edge collisions; 

* wait actions on the goal have a unitary cost.
