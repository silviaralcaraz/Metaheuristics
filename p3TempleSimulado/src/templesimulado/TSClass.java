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
import java.util.ArrayList;
import java.util.Random;

/**
 * @author silvia
 */
public class TSClass {

    // DATOS GENERALES //
    public static BufferedReader bufferAleatorios; // buffer lectura fichero aleatorios
    public static int n = 100;    // tam. de las soluciones
    public static ArrayList<ArrayList<Integer>> matrizDistancias = new ArrayList<ArrayList<Integer>>(); // matriz distancias    

    // DATOS GLOBALES TEMPLE SIMULADO //
    public static double phi = 0.5, mu = 0.01; // valores mu y phi    
    public int indiceCiudad = 0, indiceInsercion = 0, ciudad = 0;
    public int mejorIteracion = 0, iteracionActual = 0;
    public static int maxAceptaciones = 20, maxCandidatas = 80;
    public int numCandidatas = 0, numCandidatasAceptadas = 0, k = 0;
    public double temperaturaInicial = 0.0, temperatura = 0.0, randomAceptacion = 0.0;
    public double costeInicial = 0.0, costeActual = 0.0, mejorCoste = 0.0, costeAceptado = 0.0;
    public double delta = 0.0, exponencial = 0.0;
    public ArrayList solucionInicial = new ArrayList(), solucionCandidata = new ArrayList(), solucionActual = new ArrayList(), mejorSolucion = new ArrayList();
    public Boolean serAceptada = false;

    public void runTemple(String rutaAleatorios, String rutaMatrizDistancias) throws IOException {
        this.inicializarFicheroRandom(rutaAleatorios);
        this.inicializarMatrizDistancias(rutaMatrizDistancias);

        solucionInicial = this.generarPrimeraSolucion(rutaAleatorios);
        mejorSolucion = (ArrayList) solucionInicial.clone();
        solucionActual = (ArrayList) solucionInicial.clone();

        costeInicial = this.costeSolucion(solucionInicial);
        mejorCoste = costeInicial;
        costeAceptado = costeInicial;

        temperaturaInicial = (mu / -(Math.log(phi))) * costeInicial;
        temperatura = temperaturaInicial;

        // IMPRIMO LA SOLUCION INICIAL:
        System.out.println("SOLUCION INICIAL:");
        System.out.print("\tRECORRIDO: ");
        for (int i = 0; i < solucionInicial.size(); i++) {
            System.out.print("" + solucionInicial.get(i) + " ");
        }
        System.out.println("");
        System.out.println("\tFUNCION OBJETIVO (km): " + (int) costeInicial);
        System.out.println("\tTEMPERATURA INICIAL: " + Math.rint((temperaturaInicial) * 1000000) / 1000000);

        // INICIO EL BUCLE DE 10000 ITERACIONES
        for (iteracionActual = 1; iteracionActual <= 10000; iteracionActual++) {
            System.out.println("");
            System.out.println("ITERACION: " + iteracionActual);
            solucionCandidata.clear();
            solucionCandidata = generarSolucionCandidata(rutaAleatorios, solucionActual);
            costeActual = costeSolucion(solucionCandidata);
            numCandidatas++;
            System.out.println("\tINDICE CIUDAD: " + indiceCiudad);
            System.out.println("\tCIUDAD: " + ciudad);
            System.out.println("\tINDICE INSERCION: " + indiceInsercion);
            System.out.print("\tRECORRIDO: ");
            for (int i = 0; i < solucionCandidata.size(); i++) {
                System.out.print("" + solucionCandidata.get(i) + " ");
            }
            System.out.println("");
            System.out.println("\tFUNCION OBJETIVO (km): " + (int) costeActual);
            delta = costeActual - costeAceptado;
            exponencial = Math.pow(Math.E, (-delta) / temperatura);
            randomAceptacion = getRandom(rutaAleatorios);
            if (delta < 0 || randomAceptacion < exponencial) {
                numCandidatasAceptadas++;
                costeAceptado = costeActual;
                serAceptada = true;
                solucionActual = (ArrayList) solucionCandidata.clone();
                if (costeActual < mejorCoste) {
                    mejorSolucion = (ArrayList) solucionActual.clone();
                    costeAceptado = costeActual;
                    mejorCoste = costeActual;
                    mejorIteracion = iteracionActual;
                }
            }
            System.out.println("\tDELTA: " + (int) delta);
            System.out.println("\tTEMPERATURA: " + Math.rint((temperatura) * 1000000) / 1000000);
            System.out.println("\tVALOR DE LA EXPONENCIAL: " + Math.rint((exponencial) * 1000000) / 1000000);
            if (serAceptada) {
                System.out.println("\tSOLUCION CANDIDATA ACEPTADA");
            }
            serAceptada = false;
            System.out.println("\tCANDIDATAS PROBADAS: " + numCandidatas + ", ACEPTADAS: " + numCandidatasAceptadas);
            /*Enfrio la temperatura si se cumplen las condiciones de enfriamiento segun esquema de Cauchy*/
            if (numCandidatas == maxCandidatas || numCandidatasAceptadas == maxAceptaciones) {
                k++; // k = num veces que se enfria la temperatura
                //Reinicio las condiciones de enfriamiento
                numCandidatas = 0;
                numCandidatasAceptadas = 0;
                temperatura = temperaturaInicial / (1 + k); // La primera vez a la mitad, luego /3, etc.
                System.out.println();
                System.out.println("============================\n"
                        + "ENFRIAMIENTO: " + k + "\n"
                        + "============================");
                System.out.println("TEMPERATURA: " + Math.rint((temperatura) * 1000000) / 1000000);
            }
        }
        //IMPRIMO LA MEJOR SOLUCION
        System.out.println();
        System.out.println();
        System.out.println("MEJOR SOLUCION: ");
        System.out.print("\tRECORRIDO: ");
        for (int i = 0; i < mejorSolucion.size(); i++) {
            System.out.print("" + mejorSolucion.get(i) + " ");
        }
        System.out.println();
        System.out.println("\tFUNCION OBJETIVO (km): " + (int) mejorCoste);
        System.out.println("\tITERACION: " + mejorIteracion);
        System.out.println("\tmu = " + mu + ", phi = " + phi);
    }

    /*Funcion que genera el mejor vecino en funcion del aleatorio recibido y el valor de aceptacion*/
    public ArrayList<Integer> generarSolucionCandidata(String rutaAleatorios, ArrayList<Integer> Sact) throws IOException {
        ArrayList nuevoVecino = new ArrayList<>(), ScandidataMejor = new ArrayList<>(), ScandidataPeor = new ArrayList<>();
        int mejorCosteGlobal = costeSolucion(Sact), costeVecino = 0, mejorCosteLocal = Integer.MAX_VALUE, indicePeor = 0;
        // Determino el indice a insertar (primer double del par random que obtengo)
        double i1 = Math.floor((getRandom(rutaAleatorios)) * 99);
        indiceCiudad = (int) i1;
        ciudad = Sact.get(indiceCiudad);
        for (int i = 0; i < Sact.size(); i++) {
            if (i != indiceCiudad) {
                nuevoVecino = (ArrayList) Sact.clone();
                nuevoVecino.remove((int) indiceCiudad);
                nuevoVecino.add(i, ciudad);
                costeVecino = costeSolucion(nuevoVecino);
                if (costeVecino < mejorCosteGlobal) {
                    ScandidataMejor = (ArrayList) nuevoVecino.clone();
                    indiceInsercion = i;
                    mejorCosteGlobal = costeVecino;
                } else {
                    if (costeVecino < mejorCosteLocal) {
                        ScandidataPeor = (ArrayList) nuevoVecino.clone();
                        indicePeor = i;
                        mejorCosteLocal = costeVecino;
                    }
                    //Si un coste se repite, me quedo con el primer indice que lo proporciono
                    if(costeVecino==mejorCosteLocal){
                        ScandidataPeor = (ArrayList) ScandidataPeor.clone();
                        indicePeor = indicePeor;
                        mejorCosteLocal = mejorCosteLocal;
                    }
                }
            }
        }
        if (ScandidataMejor.size() == 0) {
            indiceInsercion = indicePeor;
            mejorCosteGlobal = mejorCosteLocal;
            return ScandidataPeor;
        } else {
            return ScandidataMejor;
        }
    }

    /*Funcion que genera la primera solucion*/
    public ArrayList<Integer> generarPrimeraSolucion(String rutaAleatorios) throws IOException {
        ArrayList<Integer> Sini = new ArrayList();
        for (int i = 0; i < 99; i++) {
            //transformo el double:
            double r = Math.floor((getRandom(rutaAleatorios)) * 99);
            int numero = (int) (1 + r);
            //si ya se ha usado sumo 1 en modulo 9
            while (Sini.contains(numero)) {
                numero = (numero % 99) + 1;
            }
            //Una vez obtenido el numero correcto lo añado a la solucion
            Sini.add(numero);
        }
        return Sini;
    }

    /*Funcion que inicializa la matriz de distancias segun el fichero de distancias introducido.*/
    public void inicializarMatrizDistancias(String rutaMatrizDistancias) throws FileNotFoundException, IOException {
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
    }

    /*Funcion que calcula el coste de una solucion*/
    public int costeSolucion(ArrayList Sact) {
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

    /*Funcion que inicializa el bufferReader de los aleatorios en caso de haber cargado los random con un fichero*/
    public void inicializarFicheroRandom(String rutaAleatorios) throws FileNotFoundException {
        if (rutaAleatorios != null) {
            FileReader file = new FileReader(rutaAleatorios);
            bufferAleatorios = new BufferedReader(file);
        }
    }

    /*Funcion que devuelve un indice double random (del fichero en caso de usarse o generado
    por el programa en caso de no usarse fichero de aleatorios).*/
    public double getRandom(String rutaAleatorios) throws FileNotFoundException, IOException {
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
}