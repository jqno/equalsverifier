@echo off
echo "--- COMPILATION & PACKAGING ---"

echo " > creating clean directories"
del /s /q classes
rmdir /s /q classes
mkdir classes
del /s /q mods
rmdir /s /q mods
mkdir mods

echo " > multi-compiling modules"
rem spark is required as an automatic module, so copy it to mods
copy libs\spark-core-*.jar mods\
javac --module-path mods --module-source-path "./*/src/main/java" -d classes --module equalsverifier

echo " > packaging modules"
jar --create --file mods/equalsverifier.checkers.jar -C classes/equalsverifier.checkers .
jar --create --file mods/equalsverifier.checkers.beta.jar -C classes/equalsverifier.gentype .
jar --create --file mods/equalsverifier.statistics.jar -C classes/equalsverifier.prefabservice .
jar --create --file mods/equalsverifier.prefabvalues.jar -C classes/equalsverifier.prefabvalues .
jar --create --file mods/equalsverifier.prefabvalues.jar -C classes/equalsverifier.reflection .
jar --create --file mods/equalsverifier.rest.jar -C classes/equalsverifier.utils .
jar --create --file mods/equalsverifier.jar --main-class equalsverifier.equalsverifier -C classes/equalsverifier .
