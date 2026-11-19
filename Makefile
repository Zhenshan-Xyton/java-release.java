all: release.java
	javac --release 17 -d target/classes release.java
