/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p2busquedatabu;

import java.awt.Point;
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
public class P2BusquedaTabu {

    //Datos generales del problema:
    static BufferedReader bufferAleatorios;
    static int n = 10;
    static int numMaxVecinos = ((n - 1) * (n - 2)) / 2;
    static ArrayList<ArrayList<Integer>> matrizDistancias = new ArrayList<ArrayList<Integer>>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String rutaMatrizDistancias = "distancias_ts_100_2016.txt";
        String rutaAleatorios = null;

        //Si existe un argumento sera el archivo con aleatorios
        if (args.length == 1) {
            rutaMatrizDistancias = args[0];
        }
        if (args.length == 2) {
            rutaMatrizDistancias = args[0];
            rutaAleatorios = args[1];
        }
        if (rutaAleatorios != null) {
            FileReader file = new FileReader(rutaAleatorios);
            bufferAleatorios = new BufferedReader(file);
        }

        //Almaceno el contenido del fichero correspondiente a la matriz de distancias        
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
        //Declaro la lista tabu: es un array list de coordenadas x,y siendo estas el par de indices usados
        ArrayList<Point> listaTabu = new ArrayList<Point>();

        /*      ALGORITMO       */
        //Genero la primera solucion
        ArrayList<Integer> Sini = new ArrayList<Integer>();
        Sini = generarPrimeraSolucion(rutaAleatorios);
        //Declaro variables
        ArrayList<Integer> Sact = new ArrayList<Integer>();
        ArrayList<Integer> Smejor = new ArrayList<Integer>();
        ArrayList<Integer> mejorVecino = new ArrayList<Integer>();
        Sact = (ArrayList<Integer>) Sini.clone();
        Smejor = (ArrayList<Integer>) Sini.clone();
        mejorVecino = (ArrayList<Integer>) Sini.clone();        

        //Imprimo primera solucion
        System.out.println("RECORRIDO INICIAL");
        System.out.print("\tRECORRIDO: ");
        for (int i = 0; i < Sact.size(); i++) {
            System.out.print(Sact.get(i) + " ");
        }
        //Calculo el coste de la primera solucion (mejor coste de partida):
        int mejorCoste = costeSolucion(Sact);
        System.out.println("\n\tCOSTE (km): " + mejorCoste);
        System.out.println("");
        Point indicesIntercambiados = new Point(), indicesTB = new Point();
        int iteracionesSinMejora = 0, numReinicios = 0, coste = 0, mejorIteracion = 0;

        //Inicio las 10000 iteraciones
        for (int iteracion = 1; iteracion <= 10000; iteracion++) {            
            //Si se llevan 100 iteraciones sin mejora reinicio la LT
            if (iteracionesSinMejora == 100) {
                iteracionesSinMejora = 0;
                numReinicios++;
                listaTabu.clear();
                System.out.println("***************\n"
                        + "REINICIO: " + numReinicios + "\n"
                        + "***************\n");
                Sact = (ArrayList<Integer>) Smejor.clone();
            }
            System.out.println("ITERACION: " + iteracion);
            //Exploro el vecindario:            
            mejorVecino.clear();
            mejorVecino = generarMejorVecino(Sact, listaTabu, indicesIntercambiados);
            indicesTB = (Point) indicesIntercambiados.clone();
            coste = costeSolucion(mejorVecino);
            if (coste < mejorCoste) {
                mejorCoste = coste;
                Sact = (ArrayList<Integer>) mejorVecino.clone();
                Smejor = (ArrayList<Integer>) mejorVecino.clone();                
                mejorIteracion = iteracion;
                iteracionesSinMejora = 0;
            } else {                
                iteracionesSinMejora++;                
                Sact = (ArrayList<Integer>) mejorVecino.clone();                
            }                            
            //Registro los indices intercambiados en la lista tabu (usados)
            if (listaTabu.size() == 100) {
                //Si ya tiene tam = 100, elimino el elemento mas antiguo
                listaTabu.remove(0);
            }
            listaTabu.add(indicesTB);
            //Imprimo la solucion obtenida en la iteracion:
            System.out.println("\tINTERCAMBIO: (" + (int) indicesTB.getX() + ", " + (int) indicesTB.getY() + ")");
            System.out.print("\tRECORRIDO: ");
            for (int i = 0; i < Sact.size(); i++) {
                System.out.print(Sact.get(i) + " ");
            }
            System.out.println("\n\tCOSTE (km): " + coste);
            System.out.println("\tITERACIONES SIN MEJORA: " + iteracionesSinMejora);
            System.out.println("\tLISTA TABU:");
            for (int x = 0; x < listaTabu.size(); x++) {
                System.out.println("\t" + listaTabu.get(x).x + " " + listaTabu.get(x).y);
            }
            System.out.println("");
        }        
        //Imprimo la mejor solucion obtenida
        System.out.println("MEJOR SOLUCION: ");
        System.out.print("\tRECORRIDO: ");
        for (int i = 0; i < Smejor.size(); i++) {
            System.out.print(Smejor.get(i) + " ");
        }
        System.out.println("\n\tCOSTE (km): " + mejorCoste);
        System.out.println("\tITERACION: " + mejorIteracion);
        System.out.println("");
    }

    /*Funcion que devuelve un indice double random (del fichero en caso de usarse o generado
    por el programa en caso de no usarse fichero de aleatorios).*/
    public static double getRandom(String rutaAleatorios) throws FileNotFoundException, IOException {
        double numRandom = 0;
        if (rutaAleatorios != null) {
            String linea = bufferAleatorios.readLine();
            if (linea == null) { //Cuando se llega al fin del fichero se vuelve al principio
                FileReader file = new FileReader(rutaAleatorios);
                bufferAleatorios = new BufferedReader(file);
                linea = bufferAleatorios.readLine();
                numRandom = Double.parseDouble(linea);
            } else {
                numRandom = Double.parseDouble(linea);
            }
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
        for (int i = 0; i < 99; i++) {
            //transformo el double:
            double r = Math.floor((getRandom(rutaAleatorios)) * 99);
            int numero = (int) (1 + r);
            //si ya se ha usado sumo 1 en modulo 99
            while (Sini.contains(numero)) {
                numero = (numero % 99) + 1;
            }
            //Una vez obtenido el numero correcto lo añado a la solucion
            Sini.add(numero);
        }
        return Sini;
    }

    /*Funcion que genera un vecino en funcion a la solucion dada*/
    public static ArrayList generarMejorVecino(ArrayList Sact, ArrayList<Point> listaTabu, Point coordenadas) throws IOException {
        ArrayList Vactual = new ArrayList<>(), Vmejor = new ArrayList<>(), aux = new ArrayList<>();
        Vactual = (ArrayList) Sact.clone();//igualo el valor del vecino a la Sact para luego intercambiar indices                
        int coste = 0, mejorCoste = 0;
        Point indices = new Point();
        //Obtengo dos indices                        
        int count = 0;
        for (int i = 1; i < 99; i++) {
            for (int j = 0; j < i; j++) {
                indices.setLocation(i, j);
                //Busco indices validos (condiciones: indice1!=indice2, indice1>indice2, LT no contiene los indices)                        
                if (!listaTabu.contains(indices)) {
                    //Intercambio los indices                    
                    Vactual = (ArrayList) Sact.clone();
                    Vactual.set(i, Sact.get(j));
                    Vactual.set(j, Sact.get(i));
                    coste = costeSolucion(Vactual);

                    if (count == 0) {
                        mejorCoste = coste;
                        Vmejor = (ArrayList) Vactual.clone();
                    }
                    count = 1;
                    if (coste < mejorCoste) {
                        mejorCoste = coste;
                        Vmejor = (ArrayList) Vactual.clone();
                        coordenadas.setLocation(i, j);
                    }
                }                
            }
        }
        //Devuelvo el nuevo vecino
        return Vmejor;
    }

    /*Funcion que calcula el coste de una solucion*/
    public static int costeSolucion(ArrayList Sact) {
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
