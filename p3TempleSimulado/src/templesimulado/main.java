/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package templesimulado;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static templesimulado.TSClass.bufferAleatorios;

/**
 *
 * @author silvia
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String rutaMatrizDistancias = "distancias_sa_100_2017.txt";
        String rutaAleatorios = null;
        //String rutaAleatorios = "aleatorios_sa_2017_caso1.txt";

        // Manejo los argumentos
        if (args.length == 1) {
            rutaMatrizDistancias = args[0];
        }
        if (args.length == 2) {
            rutaMatrizDistancias = args[0];
            rutaAleatorios = args[1];
        }

        TSClass temple = new TSClass();
        temple.runTemple(rutaAleatorios, rutaMatrizDistancias);
    }    
}