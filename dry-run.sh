#!/bin/bash

#Java executable for standard Linux environment
export JAVA=java
#Java executable for MinGW environment
#export JAVA=/c/jdk9/bin/java.exe

echo ""
echo "--- LAUNCH ---"

echo " > dry-run equalsverifier"
echo ""

if [ "$1" == "mvn" ]
then
# the classpath is needed for Spark's dependencies
	$JAVA \
		--module-path mods-mvn \
		--class-path "libs/*" \
		--dry-run \
		--module equalsverifier
else
# the classpath is needed for Spark's dependencies
	$JAVA \
		--module-path mods \
		--class-path "libs/*" \
		--dry-run \
		--module equalsverifier
fi
