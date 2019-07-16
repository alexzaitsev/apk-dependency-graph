# Apk Dependency Graph

[![Build Status](https://travis-ci.org/alexzaitsev/apk-dependency-graph.svg?branch=master)](https://travis-ci.org/alexzaitsev/apk-dependency-graph)
[![version](https://img.shields.io/badge/version-0.1.5-brightgreen.svg)](https://github.com/alexzaitsev/apk-dependency-graph/releases/tag/0.1.5) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-apk--dependency--graph-blue.svg?style=flat)](http://android-arsenal.com/details/1/4411)

Class dependency visualizer. Only `apk` file is needed.  
Class coupling is one of the significant code metrics that shows how easy is to change, maintain and test the code. This tool helps to view whole picture of the project.

**Table of contents**
* [Project structure](#Project-structure)
* [Compile](#Compile)
* [Run](#Run)
* [Usage](#Usage)
* [Examples](#Examples)
* [Demo](#Demo)
* [Contributors](#Contributors)
* [Credits](#Credits)

## Project structure

This project consists of the several parts:

* filters (json)
* gui (html+js)
* src (java)

###### To get more information please check our [wiki page](https://github.com/alexzaitsev/apk-dependency-graph/wiki).

## Compile

At least **Java 8** is needed.

Ways to compile `build/jar/apk-dependency-graph.jar`:

`>> gradle build` (Gradle 5.0 or newer)  
`>> gradlew build` (Gradle Wrapper)  
`>> ant` (Ant)

Classes will be generated to `build/classes` folder and jar file will appear onto `build/jar` folder.

## Run

You need at least **Java 8** to run `apk-dependency-graph.jar`.

## Usage

I've prepared helpful scripts for you. All you need to do is to download and unpack [the latest release](https://github.com/alexzaitsev/apk-dependency-graph/releases) and type the next command in your command line:  

*For Windows*:

```shell
run.bat full\path\to\the\apk\app-release.apk full\path\to\the\filterset.json
```

Where:
* `run.bat` is a path to script in your local repository
* `full\path\to\the\apk\app-release.apk` is a full path to the apk file you want to analize
* `full\path\to\the\filterset.json` is a full path to the filterset file

The tool is provided with the [default filterset](https://github.com/alexzaitsev/apk-dependency-graph/blob/master/filters/default.json). However, you're highly encouraged to customize it. Read [filter instructions](https://github.com/alexzaitsev/apk-dependency-graph/blob/master/filters/instructions.txt) for the details.

*For Unix*:

```shell
./run.sh full/path/to/the/apk/app-release.apk full/path/to/the/filterset.json
```

Wait until the command finishes:

```shell
Baksmaling classes.dex...
Analyzing dependencies...
Success! Now open index.html in your browser.
```

It will decompile your apk and create `output/apk-file-name` folder in the same folder where the script is. After this it will analyze the smali code and generate `gui/analyzed.js` file which contains all dependencies.
**Now open `gui/index.html` in your browser and enjoy!**

## Examples

Here is the sample of good architecture with low class coupling:  
![Good sample](image-good-example.jpg)

And this one looks like a spaghetti:  
![Good sample](image-bad-example.jpg)

Does your project look like the first or the second picture? :)

## Demo

Watch [demo video](https://www.youtube.com/watch?v=rw501tvT4ko).

## Contributors

I want to say thank you to all the people who made even tiny pull request. This project is intended to improve current state of Android architecture all over the world so each detail is important. In the [contributors page](https://github.com/alexzaitsev/apk-dependency-graph/graphs/contributors) you can find a list of people who have found some time to improve this tool.

## Credits

There is the same tool for iOS: <https://github.com/PaulTaykalo/objc-dependency-visualizer>
I have used `gui/index.html` of that project. Thanks Paul for the great tool.

