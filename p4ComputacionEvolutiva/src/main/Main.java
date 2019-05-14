package main;

import evolutiva.Evolutiva;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author silvia
 */
public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String rutaMatrizDistancias = "distancias_ce_10_2016.txt";
        String rutaAleatorios = null;
        //String rutaAleatorios = "aleatorios_ce_10_2016.txt";

        // Manejo los argumentos
        if (args.length == 1) {
            rutaMatrizDistancias = args[0];
        }
        if (args.length == 2) {
            rutaMatrizDistancias = args[0];
            rutaAleatorios = args[1];
        }

        // Inicio el algoritmo de CE
        Evolutiva lanzador = new Evolutiva();
        lanzador.runComputacionEvolutiva(rutaAleatorios, rutaMatrizDistancias);
    }
}