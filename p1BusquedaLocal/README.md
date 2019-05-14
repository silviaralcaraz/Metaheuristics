# Execution #

To execute the jar file (located in dist folder):
		java -jar nombreJar.jar ficheroAleatorios.txt [ficheroDistancias.txt]

Example:
		java -jar p1BusquedaLocal.jar aleatorios_ls_2016.txt distancias_10.txt > mi_traza.txt

# Checks #

To check if the results and the trace match:
		diff mi_traza.txt traza_ls_2016.txt

# Files #

In the netbeans folder are the following files:

- aleatorios_ls_2016.txt -> file with the random data necessary for practice.
- distancias_10.txt -> file with the distances matrix between cities.
- traza_ls_2016.txt -> trace with the correct results.
- mi_traza.txt -> trace obtained executing this program using as input the file with the random data and the file with the distances matrix mentioned.
- diferencias_trazas.txt -> file with the diferences between my trace and reference trace. The only difference: my trace shows the final solution to the problem.
