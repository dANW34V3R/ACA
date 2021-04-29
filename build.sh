#!/bin/sh
mkdir -p target
javac -d target/ src/main/java/com/company/*.java
cd target
echo "Main-Class: com.company.Main" > MANIFEST.MF
jar cmf MANIFEST.MF ../ra18837ACA.jar *
cd ..
