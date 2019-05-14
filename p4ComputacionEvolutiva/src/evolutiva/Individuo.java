package evolutiva;

import java.util.ArrayList;

/**
 *
 * @author silvia
 */
public class Individuo{
    private ArrayList<Integer> recorrido;
    private Integer fitness;
    private Integer iteracion;

    public Individuo() {
        this.recorrido = new ArrayList<>();
        this.fitness = Integer.MAX_VALUE;
        this.setIteracion(0);
    }

    public Individuo(ArrayList<Integer> recorrido) {
        Evolutiva e = new Evolutiva();
        this.recorrido = recorrido;
        this.fitness = e.calcularFitness(recorrido);
    }

    public Individuo(ArrayList<Integer> recorrido, Integer iteracion) {
        Evolutiva e = new Evolutiva();
        this.recorrido = recorrido;
        this.fitness = e.calcularFitness(recorrido);
        this.setIteracion(iteracion);
    }

    public Individuo(ArrayList<Integer> recorrido, Integer fitness, Integer iteracion) {
        Evolutiva e = new Evolutiva();
        this.recorrido = recorrido;
        this.fitness = fitness;
        this.iteracion = iteracion;
    }

    public ArrayList<Integer> getRecorrido() {
        return recorrido;
    }

    public void setRecorrido(ArrayList<Integer> recorrido) {
        Evolutiva e = new Evolutiva();
        this.recorrido = recorrido;
        this.fitness = e.calcularFitness(recorrido);
    }

    public Integer getFitness() {
        return fitness;
    }

    public void setFitness(Integer fitness) {
        this.fitness = fitness;
    }

    public Integer getIteracion() {
        return iteracion;
    }

    public void setIteracion(Integer iteracion) {
        this.iteracion = iteracion;
    }
}