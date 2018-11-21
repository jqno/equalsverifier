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

echo " > creating equalsverifier.checkers"
$JAVAC \
	-d classes/equalsverifier.checkers \
	$(find equalsverifier.checkers -name '*.java')
$JAR --create \
	--file mods/equalsverifier.checkers.jar \
	-C classes/equalsverifier.checkers .

echo " > creating equalsverifier.gentype"
$JAVAC \
	--module-path mods \
	-d classes/equalsverifier.gentype \
	$(find equalsverifier.gentype -name '*.java')
$JAR --create \
	--file mods/equalsverifier.gentype.jar \
	-C classes/equalsverifier.gentype .


echo " > creating equalsverifier.prefabservice"
$JAVAC \
	--module-path mods \
	-d classes/equalsverifier.prefabservice \
	$(find equalsverifier.prefabservice -name '*.java')
$JAR --create \
	--file mods/equalsverifier.prefabservice.jar \
	-C classes/equalsverifier.prefabservice .

echo " > creating equalsverifier.prefabvalues"
$JAVAC \
	--module-path mods \
	-d classes/equalsverifier.prefabvalues \
	$(find equalsverifier.prefabvalues -name '*.java')
$JAR --create \
	--file mods/equalsverifier.prefabvalues.jar \
	-C classes/equalsverifier.prefabvalues .

echo " > creating equalsverifier.reflection"
# spark is required as an automatic module, so copy it to mods
cp libs/spark-core-* mods/spark.core.jar
$JAVAC \
	--module-path mods \
	-d classes/equalsverifier.reflection \
	$(find equalsverifier.reflection -name '*.java')
$JAR --create \
	--file mods/equalsverifier.reflection.jar \
	-C classes/equalsverifier.reflection .

echo " > creating equalsverifier.utils"
# spark is required as an automatic module, so copy it to mods
cp libs/spark-core-* mods/spark.core.jar
$JAVAC \
	--module-path mods \
	-d classes/equalsverifier.utils \
	$(find equalsverifier.utils -name '*.java')
$JAR --create \
	--file mods/equalsverifier.utils.jar \
	-C classes/equalsverifier.utils .

echo " > creating equalsverifier"
$JAVAC \
	--module-path mods \
	-d classes/equalsverifier \
	$(find equalsverifier -name '*.java')
$JAR --create \
	--file mods/equalsverifier.jar \
	--main-class equalsverifier.equalsverifier \
	-C classes/equalsverifier .
