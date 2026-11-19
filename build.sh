#!/bin/sh
set -eu
mkdir -p target/classes
"${JAVAC:-javac}" --release 17 -d target/classes release.java
