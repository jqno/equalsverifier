#!/bin/bash
set -e

#Java executable for standard Linux environment
export JAVAC=javac
export JAR=jar
#Java executable for MinGW environment
#export JAVAC=/c/jdk9/bin/javac.exe
#export JAR=/c/jdk9/bin/jar.exe

echo "--- COMPILATION & PACKAGING ---"

echo " > creating clean directories"
rm -rf classes
mkdir classes
rm -rf mods
mkdir mods

echo " > multi-compiling modules"
# spark is required as an automatic module, so copy it to mods
cp libs/spark-core-* mods/spark.core.jar
$JAVAC \
	--module-path mods \
	--module-source-path "./*/src/main/java" \
	-d classes \
	--module equalsverifier

echo " > packaging modules"
$JAR --create \
	--file mods/equalsverifier.checkers.jar \
	-C classes/equalsverifier.checkers .
$JAR --create \
	--file mods/equalsverifier.gentype.jar \
	-C classes/equalsverifier.gentype .
$JAR --create \
	--file mods/equalsverifier.prefabservice.jar \
	-C classes/equalsverifier.prefabservice .
$JAR --create \
	--file mods/equalsverifier.prefabvalues.jar \
	-C classes/equalsverifier.prefabvalues .
$JAR --create \
	--file mods/equalsverifier.reflection.jar \
	-C classes/equalsverifier.reflection .
$JAR --create \
	--file mods/equalsverifier.utils.jar \
	-C classes/equalsverifier.utils .
$JAR --create \
	--file mods/equalsverifier.jar \
	--main-class equalsverifier.equalsverifier \
	-C classes/equalsverifier .
