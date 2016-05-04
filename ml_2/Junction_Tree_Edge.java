/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml_2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 *
 * @author Shivanth
 */
public class Junction_Tree_Edge extends DefaultWeightedEdge {

    HashSet<Vertex> intersectionset;

    Junction_Tree_Edge(HashSet<Vertex> temp) {
        this.intersectionset = temp;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Vertex v : intersectionset) {
            s.append(v.toString()).append(',');
        }
        if (s.length() > 0) {
            s.deleteCharAt(s.length() - 1);
        }
        return s.toString();
    }
}
