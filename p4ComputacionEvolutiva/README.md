# Execution #

To execute .jar file (located in /out/artifacts/p4ComputacionEvolutiva_jar):
		java -jar nombreJar.jar ficheroDistancias.txt [ficheroAleatorios.txt] 

Example:
		java -jar p4ComputacionEvolutiva.jar distancias_ce_10_2016.txt aleatorios_ce_10_2016.txt > mi_traza.txt

----------------------------------------------------------------------------------------
If you don't find JAR file, go to src folder and execute:
		javac evolutiva/Evolutiva.java evolutiva/Individuo.java main/Main.java
		java main.Main ficheroDistancias.txt [ficheroAleatorios.txt] 
----------------------------------------------------------------------------------------

# Checks #

To check if the results and the trace match:
		diff mi_traza.txt traza_ce_10_2016.txt

# Files #

In the Intellij folder are the following files:
- aleatorios_ce_10_2016.txt -> file with the random data necessary for practice.
- distancias_ce_10_2016.txt -> file with the distances matrix between cities.
- traza_ce_10_2016.txt -> trace with the correct results.
- mi_traza.txt -> trace obtained executing this program using as input the file with the random data and the file with the distances matrix mentioned.
