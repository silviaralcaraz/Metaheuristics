/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p1busquedalocal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/**
 *
 * @author silvia
 */
public class P1BusquedaLocal {

    static BufferedReader bufferAleatorios;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String rutaMatrizDistancias = "distancias_10.txt";
        String rutaAleatorios = null;
        
        //Si existe un argumento sera el archivo con aleatorios
        if (args.length == 1) {
            rutaAleatorios = args[0];
        }
        if (args.length == 2) {
            rutaAleatorios = args[0];
            rutaMatrizDistancias = args[1];
        }
        if (rutaAleatorios != null) {
            FileReader file = new FileReader(rutaAleatorios);
            bufferAleatorios = new BufferedReader(file);
        }
        //Almaceno el contenido del fichero correspondiente a la matriz de distancias
        ArrayList<ArrayList<Integer>> matrizDistancias = new ArrayList<ArrayList<Integer>>();
        FileReader f = new FileReader(rutaMatrizDistancias);
        BufferedReader br = new BufferedReader(f);
        String linea;
        while ((linea = br.readLine()) != null) {
            String aux[] = linea.split("\t");
            ArrayList fila = new ArrayList();
            for (int w = 0; w < aux.length; w++) {
                fila.add(Integer.parseInt(aux[w]));
            }
            matrizDistancias.add(fila);
        }
        //Determino los datos generales del problema
        int n = 10, numMaxVecinos = ((n - 1) * (n - 2)) / 2;
        /*Creo la matriz binaria de indices usados (con valores True si el par de indices ya se uso
        o False si el par de indices aun no se uso)*/
        ArrayList<ArrayList> matrizUsados = new ArrayList<ArrayList>();
        
        //Algoritmo:        
        ArrayList<Integer> Sact = new ArrayList<Integer>(); //genero la primera solucion
        Sact = generarPrimeraSolucion(rutaAleatorios);
        ArrayList<Integer> vecino = new ArrayList<Integer>();
        vecino = (ArrayList<Integer>) Sact.clone(); //igualo el vecino a la solucion inicial
        ArrayList<Integer> nuevoVecino = new ArrayList<Integer>();  //creo un nuevo vecino 
        int coste = costeSolucion(Sact, matrizDistancias);  //calculo el coste de la primera solucion
        int mejorCoste = coste; //fijo el mejor coste al coste de la solucion inicial
        int numSoluciones = 0;
        while (coste <= mejorCoste) {
            mejorCoste = coste;
            vecino = (ArrayList<Integer>) Sact.clone();
            int nuevoCoste = mejorCoste, numVecinos = 0;
            System.out.println("SOLUCION S_" + numSoluciones + " -> " + vecino + "; " + mejorCoste + "km");
            //Reinicializo la matriz de indices usados con la diagonal a True y el resto de campos a False        
            matrizUsados = new ArrayList<ArrayList>();
            for (int i = 0; i < 9; i++) {
                ArrayList fila = new ArrayList();
                for (int j = 0; j < 9; j++) {
                    if (i == j) {
                        fila.add(true);
                    } else {
                        fila.add(false);
                    }
                }
                matrizUsados.add(fila);
            }
            while (nuevoCoste >= mejorCoste) {
                numVecinos++;
                if (numVecinos > numMaxVecinos) { //Si ya se ha generado todo el entorno, se finaliza
                    break;
                }
                ArrayList<Integer> indicesIntercambiados = new ArrayList<>(); //guardo los indices intercambiados
                nuevoVecino = generarVecino(vecino, matrizUsados, rutaAleatorios, indicesIntercambiados);
                nuevoCoste = costeSolucion(nuevoVecino, matrizDistancias);
                System.out.println("\tVECINO V_" + (numVecinos - 1) + " -> Intercambio: (" + indicesIntercambiados.get(0) + ", " + indicesIntercambiados.get(1) + "); " + nuevoVecino + "; " + nuevoCoste + "km");
                if (nuevoCoste < mejorCoste) {
                    coste = nuevoCoste;
                    Sact = (ArrayList<Integer>) nuevoVecino.clone();
                }
            }
            if (numVecinos >= numMaxVecinos) {
                break;
            }
            numSoluciones += 1;
            System.out.println("");
        }
        System.out.println("");
        System.out.println("SOLUCION FINAL -> " + Sact + "; " + mejorCoste + "km");
    }

    /*Funcion que devuelve un indice double random (del fichero en caso de usarse o generado
    por el programa en caso de no usarse fichero de aleatorios).*/
    public static double getRandom(String rutaAleatorios) throws FileNotFoundException, IOException {
        double numRandom = 0;
        if (rutaAleatorios != null) {
            String linea = bufferAleatorios.readLine();
            numRandom = Double.parseDouble(linea);
        } else {
            double min = 0, max = 1;
            Random r = new Random();
            numRandom = min + (max - min) * r.nextDouble();
        }
        return numRandom;
    }

    /*Funcion que genera la primera solucion*/
    public static ArrayList<Integer> generarPrimeraSolucion(String rutaAleatorios) throws IOException {
        ArrayList<Integer> Sini = new ArrayList();
        for (int i = 0; i < 9; i++) {
            //transformo el double:
            double r = Math.floor((getRandom(rutaAleatorios)) * 9);
            int numero = (int) (1 + r);
            //si ya se ha usado sumo 1 en modulo 9
            while (Sini.contains(numero)) {
                numero = (numero % 9) + 1;
            }
            //Una vez obtenido el numero correcto lo añado a la solucion
            Sini.add(numero);
        }
        return Sini;
    }

    /*Funcion que genera un vecino en funcion a la solucion dada*/
    public static ArrayList generarVecino(ArrayList Sact, ArrayList<ArrayList> matrizUsados, String rutaAleatorios, ArrayList indicesIntercambio) throws IOException {
        ArrayList nuevoVecino = new ArrayList<>();
        nuevoVecino = (ArrayList) Sact.clone(); //igualo el valor del vecino a la Sact para luego intercambiar indices
        //Obtengo dos indices
        double i1 = Math.floor((getRandom(rutaAleatorios)) * 9), i2 = Math.floor((getRandom(rutaAleatorios)) * 9);
        int indice1 = (int) i1, indice2 = (int) i2;
        //Determino el indice mayor y el menor
        int mayor = Math.max(indice1, indice2);
        int menor = Math.min(indice1, indice2);
        //Cuando el par de indices ya esta usado modifico su valor de la siguiente forma:
        while (((boolean) matrizUsados.get(mayor).get(menor)) == true) {
            if (indice1 == indice2) {//en el caso de que sean iguales (valores de la diagonal de la matriz de indices usados)
                indice1 = (mayor + 1) % 9; //Sumo 1 en modulo 9
                indice2 = 0;
            } else //Si simplemente ya han sido usados
            {
                if (menor == indice1) {
                    indice1 = (indice1 + 1) % 9;
                } else {
                    indice2 = (indice2 + 1) % 9;
                }
            }
            mayor = Math.max(indice1, indice2);
            menor = Math.min(indice1, indice2);
        }
        //Intercambio los indices
        ArrayList aux = (ArrayList) nuevoVecino.clone(); //creo un auxiliar para no tomar valores erroneos
        nuevoVecino.set(indice1, aux.get(indice2));
        nuevoVecino.set(indice2, aux.get(indice1));
        //Registro los indices usados en la matriz de usados
        matrizUsados.get(mayor).set(menor, true);
        //Imprimo los indices usados siguiendo el formato del archivo de traza:        
        indicesIntercambio.add(mayor);
        indicesIntercambio.add(menor);
        Collections.sort(indicesIntercambio); //ordeno de menor a mayor
        Comparator<Integer> comparador = Collections.reverseOrder();
        Collections.sort(indicesIntercambio, comparador); //invierto el orden (ordeno de mayor a menor)        
        //Devuelvo el nuevo vecino
        return nuevoVecino;
    }

    /*Funcion que calcula el coste de una solucion*/
    public static int costeSolucion(ArrayList Sact, ArrayList<ArrayList<Integer>> matrizDistancias) {
        int coste = 0, costeInicial = 0, costeFinal = 0, costeIntermedio = 0;
        //Sumo el coste de ir de la ciudad 0 a la primera        
        costeInicial = matrizDistancias.get(((int) Sact.get(0)) - 1).get(0);
        coste += costeInicial;
        //Sumo el coste de las ciudades intermedias
        for (int i = 1; i < Sact.size(); i++) {
            int mayor = Math.max((int) Sact.get(i - 1), (int) Sact.get(i));
            int menor = Math.min((int) Sact.get(i - 1), (int) Sact.get(i));
            costeIntermedio = matrizDistancias.get(mayor - 1).get(menor);
            coste += costeIntermedio;
        }
        //Sumo el coste de ir de la ultima ciudad a la 0
        int ultimaPos = (int) Sact.get(Sact.size() - 1);
        costeFinal = matrizDistancias.get(ultimaPos - 1).get(0);
        coste += costeFinal;
        return coste;
    }
}