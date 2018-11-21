@echo off
echo "--- COMPILATION & PACKAGING ---"

echo " > creating clean directories"
del /s /q classes
rmdir /s /q classes
mkdir classes
del /s /q mods
rmdir /s /q mods
mkdir mods

echo " > creating equalsverifier.checkers"
dir /S /B equalsverifier.checkers\src\*.java > sources.txt
javac -d classes/equalsverifier.checkers @sources.txt
del sources.txt
jar --create --file mods/equalsverifier.checkers.jar -C classes/equalsverifier.checkers .

echo " > creating equalsverifier.gentype"
dir /S /B equalsverifier.gentype\src\*.java > sources.txt
javac --module-path mods -d classes/equalsverifier.gentype @sources.txt
del sources.txt
jar --create --file mods/equalsverifier.gentype.jar -C classes/equalsverifier.gentype .

echo " > creating equalsverifier.prefabservice"
dir /S /B equalsverifier.prefabservice\src\*.java > sources.txt
javac --module-path mods -d classes/equalsverifier.prefabservice @sources.txt
del sources.txt
jar --create --file mods/equalsverifier.prefabservice.jar -C classes/equalsverifier.prefabservice .

echo " > creating equalsverifier.prefabvalues"
dir /S /B equalsverifier.prefabvalues\src\*.java > sources.txt
javac --module-path mods -d classes/equalsverifier.prefabvalues @sources.txt
del sources.txt
jar --create --file mods/equalsverifier.prefabvalues.jar -C classes/equalsverifier.prefabvalues .

echo " > creating equalsverifier.reflection"
dir /S /B equalsverifier.reflection\src\*.java > sources.txt
javac --module-path mods -d classes/equalsverifier.reflection @sources.txt
del sources.txt
jar --create --file mods/equalsverifier.reflection.jar -C classes/equalsverifier.reflection .

echo " > creating equalsverifier.utils"
rem spark is required as an automatic module, so copy it to mods
copy /y libs\spark-core-* mods\
ren mods\spark-core-*.jar spark.core.jar
dir /S /B equalsverifier.utils\src\*.java > sources.txt
javac --module-path mods -d classes/equalsverifier.utils @sources.txt
del sources.txt
jar --create --file mods/equalsverifier.utils.jar -C classes/equalsverifier.utils .

echo " > creating equalsverifier"
dir /S /B equalsverifier\src\*.java > sources.txt
javac --module-path mods -d classes/equalsverifier @sources.txt
del sources.txt
jar --create --file mods/equalsverifier.jar --main-class equalsverifier.equalsverifier -C classes/equalsverifier .
