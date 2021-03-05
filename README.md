# ACA

A precompiled version of the simulator can be found within the target folder. It will have a name similar to ACA-1.0-SNAPSHOT.jar.
This has been compiled to java 8 and can be run on the lab machines. From within the top directory use the following command to run the jar:

`java -jar target/ACA-1.0-SNAPSHOT.jar <program>`

<program> should be replaced with the name of the folder containing "program" and "memory" files. An example would be:

`java -jar target/ACA-1.0-SNAPSHOT.jar vectorAddition`

Other available programs are:

compilerTest
factorial
livermore3
vectorAddition

These can all be found within the "programs" folder.

The simulator will print debug information to the console. This is in the form:

Instruction object being run
Architectural register file, compare flag, PC (after instruction has completed)
Memory

The PC and SP are stored in the ARF in registers 30 and 31 respectively.

The source code can be found within src/main/java/com/company/

