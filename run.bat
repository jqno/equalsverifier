@echo off
echo ""
echo "--- LAUNCH ---"

if "%1" == "mvn" goto maven
echo " > run equalsverifier"
echo ""
rem the classpath is needed for Spark's dependencies
java --module-path mods --class-path "libs/*" --module equalsverifier
goto end

:maven
echo " > run equalsverifier from Maven build"
echo ""
rem This version runs the application when built with Maven.
rem the classpath is needed for Spark's dependencies
java --module-path mods-mvn --class-path "libs/*" --module equalsverifier/equalsverifier.equalsverifier

:end
