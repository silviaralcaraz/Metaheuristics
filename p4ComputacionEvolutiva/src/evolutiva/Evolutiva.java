package evolutiva;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

/**
 *
 * @author silvia
 */
public class Evolutiva {
    /*      VAR. GLOBALES GENERALES     */
    public static BufferedReader bufferAleatorios; // buffer lectura fichero aleatorios
    public static ArrayList<ArrayList<Integer>> matrizDistancias = new ArrayList<ArrayList<Integer>>(); // matriz distancias
    public static int maxIteraciones = 1000, numCiudades = 9;
    public int iteracionActual = 0;

    /*      VAR. GLOBALES EVOLUTIVA     */
    public static double probCruce = 0.9, probMutacion = 0.01;
    public int numTorneos = 0;
    public ArrayList<Individuo> poblacionBase = new ArrayList<>();
    public ArrayList<Individuo> nuevaPoblacion = new ArrayList();
    public Individuo mejorIndividuo = new Individuo();


    public void runComputacionEvolutiva(String rutaAleatorios, String rutaMatrizDistancias) throws IOException {
        // Inicializo los ficheros
        this.inicializarFicheroRandom(rutaAleatorios);
        this.inicializarMatrizDistancias(rutaMatrizDistancias);

        // Obtengo la primera poblacion: 50% aleatoria y 50% voraz
        System.out.println("POBLACION INICIAL");
        for (int i = 0; i < 100; i++) {
            if (i >= 0 && i < 50) {
                poblacionBase.add(new Individuo(getIndividuoAleatorio(rutaAleatorios)));
            }
            if (i >= 50 && i < 100) {
                poblacionBase.add(new Individuo(getIndividuoVoraz(rutaAleatorios)));
            }
            System.out.print("INDIVIDUO " + i + " = {FUNCION OBJETIVO (km): " + poblacionBase.get(i).getFitness() + ", ");
            System.out.print("RECORRIDO: ");
            for (int j = 0; j < poblacionBase.get(i).getRecorrido().size(); j++) {
                System.out.print("" + poblacionBase.get(i).getRecorrido().get(j) + " ");
            }
            System.out.print("}\n");
        }

        /*      INICIO EL ALGORITMO EVOLUTIVO       */

        for (iteracionActual = 1; iteracionActual <= maxIteraciones; iteracionActual++) {
            // ----- Torneo binario ----- //
            ArrayList<Integer> ganadoresTorneo = new ArrayList<>();
            System.out.println("\nITERACION: " + iteracionActual + ", SELECCION");
            for (int i = 0; i < 98; i++) {
                ganadoresTorneo.add(torneoBinario(poblacionBase, rutaAleatorios));
                numTorneos++;
            }
            numTorneos = 0; // reinicio el numero de torneos

            // ----- Cruces (crossover) ----- //
            System.out.println("\nITERACION: " + iteracionActual + ", CRUCE ");
            for (int i = 0; i < 98; i += 2) {
                int elemento1 = i, elemento2 = i + 1;
                double rand = this.getRandom(rutaAleatorios);
                System.out.println("\tCRUCE: (" + elemento1 + ", " + elemento2 + ") (ALEATORIO: " + String.format(Locale.ENGLISH, "%.6f", rand) + ")");

                Individuo padre1 = new Individuo(poblacionBase.get(ganadoresTorneo.get(elemento1)).getRecorrido());
                Individuo padre2 = new Individuo(poblacionBase.get(ganadoresTorneo.get(elemento2)).getRecorrido());

                // Imprimo los padres
                System.out.print("\t\tPADRE: = {FUNCION OBJETIVO (km): " + padre1.getFitness() + ", RECORRIDO: ");
                for (int j = 0; j < numCiudades; j++) {
                    System.out.print(padre1.getRecorrido().get(j) + " ");
                }
                System.out.println("}");

                System.out.print("\t\tPADRE: = {FUNCION OBJETIVO (km): " + padre2.getFitness() + ", RECORRIDO: ");
                for (int j = 0; j < numCiudades; j++) {
                    System.out.print(padre2.getRecorrido().get(j) + " ");
                }
                System.out.println("}");

                if (rand >= probCruce) {    // Si aleatorio >= probabilidad de cruce -> no hay cruce
                    // Introduzco a los padres en la nueva poblacion
                    nuevaPoblacion.add(padre1);
                    nuevaPoblacion.add(padre2);
                    System.out.println("\t\tNO SE CRUZA");
                    System.out.println();
                } else {    // Si aleatorio < probabilidad de cruce -> hay cruce
                    // Calculo los cortes
                    int corte1 = (int) Math.floor((this.getRandom(rutaAleatorios) * 9));
                    int corte2 = (int) Math.floor((this.getRandom(rutaAleatorios) * 9));
                    System.out.println("\t\tCORTES: (" + corte1 + ", " + corte2 + ")");
                    // Ordeno los cortes
                    int aux = corte1;
                    corte1 = Integer.min(corte1, corte2);
                    corte2 = Integer.max(aux, corte2);
                    // Genero e introduzco a los hijos en la nueva poblacion
                    ArrayList nuevoRecorrido1 = (ArrayList) generarHijo(padre1.getRecorrido(), padre2.getRecorrido(), corte1, corte2).clone();
                    ArrayList nuevoRecorrido2 = (ArrayList)  generarHijo(padre2.getRecorrido(), padre1.getRecorrido(), corte1, corte2).clone();
                    Individuo hijo1 = new Individuo((ArrayList<Integer>) nuevoRecorrido1.clone());
                    Individuo hijo2 = new Individuo(nuevoRecorrido2);
                    nuevaPoblacion.add(hijo1);
                    nuevaPoblacion.add(hijo2);

                    // Imprimo los hijos
                    System.out.print("\t\tHIJO: = {FUNCION OBJETIVO (km): " + nuevaPoblacion.get(i).getFitness() + ", RECORRIDO: ");
                    for (int j = 0; j < numCiudades; j++) {
                        System.out.print(nuevaPoblacion.get(i).getRecorrido().get(j) + " ");
                    }
                    System.out.print("}\n\t\tHIJO: = {FUNCION OBJETIVO (km): " + nuevaPoblacion.get(i + 1).getFitness() + ", RECORRIDO: ");
                    for (int j = 0; j < numCiudades; j++) {
                        System.out.print(nuevaPoblacion.get(i + 1).getRecorrido().get(j) + " ");
                    }
                    System.out.println("}\n");
                }
            }

            // ----- Mutacion ----- //
            System.out.println("ITERACION: " + iteracionActual + ", MUTACION");
            for (int i = 0; i < 98; i++) {
                System.out.println("\tINDIVIDUO " + i);
                System.out.print("\tRECORRIDO ANTES: ");
                for (int j = 0; j < numCiudades; j++) {
                    System.out.print(nuevaPoblacion.get(i).getRecorrido().get(j) + " ");
                }
                System.out.println("");
                for (int j = 0; j < numCiudades; j++) {
                    double random = this.getRandom(rutaAleatorios);
                    if (random <= probMutacion) {   // muta
                        int intercambio = (int) Math.floor((getRandom(rutaAleatorios)) * 9);
                        System.out.println("\t\tPOSICION: " + j + " (ALEATORIO " + String.format(Locale.ENGLISH, "%.6f", random) + ") INTERCAMBIO CON: " + intercambio);
                        ArrayList<Integer> recorridoAux = (ArrayList<Integer>) nuevaPoblacion.get(i).getRecorrido().clone();
                        Integer ciudad1 = nuevaPoblacion.get(i).getRecorrido().get(j);
                        Integer ciudad2 = nuevaPoblacion.get(i).getRecorrido().get(intercambio);
                        recorridoAux.set(j, ciudad2);
                        recorridoAux.set(intercambio, ciudad1);
                        nuevaPoblacion.get(i).setRecorrido((ArrayList<Integer>) recorridoAux.clone());
                        recorridoAux.clear();
                    } else {        // no muta
                        System.out.println("\t\tPOSICION: " + j + " (ALEATORIO " + String.format(Locale.ENGLISH, "%.6f", random) + ") NO MUTA");
                    }
                }
                System.out.print("\tRECORRIDO DESPUES: ");
                for (int j = 0; j < numCiudades; j++) {
                    System.out.print(nuevaPoblacion.get(i).getRecorrido().get(j) + " ");
                }
                System.out.println("\n");
            }

            // ----- Reemplazo ----- //
            System.out.println("\nITERACION: " + iteracionActual + ", REEMPLAZO");
            nuevaPoblacion.sort((o1, o2) -> (o1.getFitness() - o2.getFitness()));
            poblacionBase.sort((o1, o2) -> (o1.getFitness() - o2.getFitness()));

            // Actualizo el mejor individuo global
            int mejorFitnessLocal = nuevaPoblacion.get(0).getFitness();
            if (mejorFitnessLocal < mejorIndividuo.getFitness()) {
                mejorIndividuo = new Individuo((ArrayList<Integer>) nuevaPoblacion.get(0).getRecorrido().clone(), mejorFitnessLocal, iteracionActual);
            }
            // Introduzco los dos mejores individuos de la poblacion base en la nueva
            nuevaPoblacion.add(0, poblacionBase.get(0));
            nuevaPoblacion.add(0, poblacionBase.get(1));
            for (int i = 0; i < 100; i++) {     // Imprimo la nueva poblacion base
                System.out.print("INDIVIDUO " + i + " = {FUNCION OBJETIVO (km): " + nuevaPoblacion.get(i).getFitness() + ", RECORRIDO: ");
                for (int j = 0; j < numCiudades; j++) {
                    System.out.print(nuevaPoblacion.get(i).getRecorrido().get(j) + " ");
                }
                System.out.println("}");
            }
            // Actualizo la poblacion base y reseteo la nueva para la siguiente iteracion
            poblacionBase = (ArrayList<Individuo>) nuevaPoblacion.clone();
            nuevaPoblacion.clear();
        }
        System.out.println("\n");       // Imprimo los datos del mejor individuo global
        System.out.println("MEJOR SOLUCION: ");
        System.out.print("RECORRIDO: ");
        for (int j = 0; j < numCiudades; j++) {
            System.out.print(mejorIndividuo.getRecorrido().get(j) + " ");
        }
        System.out.println();
        System.out.println("FUNCION OBJETIVO (km): " + mejorIndividuo.getFitness());
        System.out.println("ITERACION: " + mejorIndividuo.getIteracion());
    }

    // ------------------- Metodos evolutivos ------------------- //

    /* Funcion que implementa el algoritmo de cruce Crossover */
    public ArrayList<Integer> generarHijo(ArrayList<Integer> padre1, ArrayList<Integer> padre2, Integer corte1, Integer corte2) {
        // Inicializo el hijo
        ArrayList<Integer> hijo = new ArrayList();
        for (int i = 0; i < 9; i++) {
            hijo.add(-1);
        }
        int huecosLlenos = corte2 - corte1 + 1;
        int espacios = 9 - huecosLlenos;
        // Relleno la parte central del hijo
        for (int i = corte1; i <= corte2; i++) {
            hijo.set(i, padre1.get(i));
        }
        int posicion = 0, posicionPadre = 0;
        // Sustituyo los valores que no estan en el intervalo de corte por los correspondientes del padre contrario
        for (int i = (corte2 + 1); i <= (corte2 + espacios); i++) {
            boolean insertado = false;
            posicion = i % 9;
            if (i == (corte2 + 1)) {
                posicionPadre = posicion;
            }
            // Completo las posiciones del hijo que faltan
            while (!insertado) {
                int ciudad = padre2.get(posicionPadre);
                if (!hijo.contains(ciudad)) {
                    hijo.set(posicion, ciudad);
                    insertado = true;
                } else {
                    posicionPadre = (posicionPadre + 1) % 9;
                }
            }
        }
        return hijo;
    }

    /* Funcion que devuelve el ganador de un torneo binario. Se escogen dos invidiuos de una poblacion de forma aleatoria.
    * El individuo que tiene mejor fitness es el ganador. En caso de empate nos quedamos con el primer elemento obtenido
    * aleatoriamente.*/
    public Integer torneoBinario(ArrayList<Individuo> poblacion, String rutaAleatorios) {
        int ganador = -1;
        try {
            int elegido1 = (int) (this.getRandom(rutaAleatorios) * 100);
            int elegido2 = (int) (this.getRandom(rutaAleatorios) * 100);
            int coste1 = poblacion.get(elegido1).getFitness();
            int coste2 = poblacion.get(elegido2).getFitness();
            if (coste1 <= coste2) {
                ganador = elegido1;
            } else {
                ganador = elegido2;
            }
            System.out.println("\tTORNEO " + numTorneos + ": " + elegido1 + " " + elegido2 + " GANA " + ganador);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ganador;
    }

    // ------------------- Otros metodos ------------------- //

    /*Funcion que genera un vecinos siguiendo una estrategia voraz: se queda con el mejor vecino dada una ciudad inicial.*/
    public ArrayList<Integer> getIndividuoVoraz(String rutaAleatorios) throws IOException {
        ArrayList individuo = new ArrayList<>();
        int coste = 0, mejorCoste = -1, ciudadOrigen = 0, ciudadElegida = 0;
        //La primera ciudad se obtiene de forma aleatoria:
        double r = Math.floor((getRandom(rutaAleatorios)) * 9); //transformo el double
        ciudadOrigen = (int) (1 + r);
        individuo.add(ciudadOrigen); //introduzco la primera ciudad
        //Calculo las ciudades cuya distancia es la minima con respecto a la ciudad que le precede:
        int count = 1;
        while (count < 9) {
            ciudadOrigen = (int) individuo.get((int) individuo.size() - 1);
            for (int i = 1; i < 10; i++) {
                if (!individuo.contains(i) && i != ciudadOrigen) {
                    coste = calcularCoste(ciudadOrigen, i);
                    if (mejorCoste == -1 || coste < mejorCoste) {
                        ciudadElegida = i;
                        mejorCoste = coste;
                    }
                }
            }
            individuo.add(ciudadElegida);
            mejorCoste = -1;
            count++;
        }
        return individuo;
    }

    /*Funcion que genera la primera solucion*/
    public ArrayList<Integer> getIndividuoAleatorio(String rutaAleatorios) throws IOException {
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

    /*Funcion que calcula el coste de una solucion*/
    public int calcularFitness(ArrayList Sact) {
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

    /*Funcion que calcula la distancia que hay de una ciudad a otra*/
    public int calcularCoste(int origen, int destino) {
        int coste = 0, mayor = 0, menor = 0;
        mayor = Math.max(origen, destino);
        menor = Math.min(origen, destino);
        coste = matrizDistancias.get(mayor - 1).get(menor);
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
}