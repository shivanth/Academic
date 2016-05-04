/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml_2;

/**
 *
 * @author Shivanth
 */
public class Vertex implements Comparable {

    private int id;
    private boolean clique_mark;//used in max cardin search algo
    private int no_of_values;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    public Vertex(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "" + (id);

    }

    /**
     * @return the clique_mark
     */
    public boolean isClique_mark() {
        return clique_mark;
    }

    /**
     * @param clique_mark the clique_mark to set
     */
    public void setClique_mark(boolean clique_mark) {
        this.clique_mark = clique_mark;
    }

    /**
     * @return the no_of_values
     */
    public int getNo_of_values() {
        return no_of_values;
    }

    /**
     * @param no_of_values the no_of_values to set
     */
    public void setNo_of_values(int no_of_values) {
        this.no_of_values = no_of_values;
    }

    @Override
    public int compareTo(Object o) {
        return this.id-((Vertex)o).getId();
    }
}
