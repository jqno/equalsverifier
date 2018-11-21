#!/bin/bash

#Java executable for standard Linux environment
export JAVA=java
#Java executable for MinGW environment
#export JAVA=/c/jdk9/bin/java.exe

echo ""
echo "--- LAUNCH ---"

echo " > run equalsverifier"
echo ""

if [ "$1" == "mvn" ]
then
# the classpath is needed for Spark's dependencies
	$JAVA \
		--module-path mods-mvn \
		--class-path "libs/*" \
		--module equalsverifier/equalsverifier.equalsverifier
else
# the classpath is needed for Spark's dependencies
	$JAVA \
		--module-path mods \
		--class-path "libs/*" \
		--module equalsverifier
fi
