/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml_2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Shivanth
 */
public class Junction_tree_node implements Comparable<Junction_tree_node> {

    private ArrayList<Vertex> variables;
    private Factor node_factor;
    //private Factor node_factor_bak;
    private HashMap<Junction_tree_node, Factor> Messages;
    private HashMap<Junction_tree_node, Factor> Map_Messages;
    private int edge_count;
    private Factor Belief;
    private Factor Map_Belief;

    //TODO Potential table
    public Junction_tree_node(HashSet<Vertex> variables) {
        this.variables = new ArrayList(variables);
        //Collections.sort(this.variables);
        Messages = new HashMap<>();
        Map_Messages = new HashMap<>();
        edge_count = 0;
    }

    /**
     * @return the variables
     */
    public HashSet<Vertex> getVariables() {
        return new HashSet(variables);
    }

    /**
     * @param variables the variables to set
     */
    public void setVariables(HashSet variables) {
        this.variables = new ArrayList(variables);
        //Collections.sort(this.variables);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Vertex v : variables) {
            s.append(v.toString()).append(',');
        }
        s.deleteCharAt(s.length() - 1);
        return s.toString();
    }

    /**
     * @return the node_factor
     */
    public Factor getNode_factor() {
        return node_factor;
    }

    /**
     * @param node_factor the node_factor to set
     */
    public void setNode_factor(Factor node_factor) {
        this.node_factor = node_factor;
    }

    /**
     * @return the Messages
     */
    public HashMap<Junction_tree_node, Factor> getMessages() {
        return Messages;
    }

    /**
     * @param Messages the Messages to set
     */
    public void setMessages(HashMap<Junction_tree_node, Factor> Messages) {
        this.Messages = Messages;
    }

    public Factor get_Factor_from(Junction_tree_node v) {
        return getMessages().get(v);
    }

    public void Put_Message(Junction_tree_node n, Factor f) {
        Messages.put(n, f);
    }

    public void Put_Map_Message(Junction_tree_node n, Factor f) {
        Map_Messages.put(n, f);
    }
    
    void inc_edge_count() {
        setEdge_count(getEdge_count() + 1);
    }

    void dec_edge_count() {
        setEdge_count(getEdge_count() - 1);
    }

    /**
     * @return the edge_count
     */
    public int getEdge_count() {
        return edge_count;
    }

    /**
     * @param edge_count the edge_count to set
     */
    public void setEdge_count(int edge_count) {
        this.edge_count = edge_count;
    }

    void update_belief() {
        Factor f = getNode_factor();
        Factor mapf=getNode_factor();
        
        for (Factor Message : getMessages().values()) {
            if(Message==null){
                throw new UnsupportedOperationException();
            }
            if (f != null) {
                f = Factor.Gen_Product_factors_st(f, Message);
                if(f.getVertex_to_dimension().keySet().size()!=f.getDimension_to_vertex().keySet().size())
            System.out.println("Error");
            } else {
                f = new Factor(Message);
            }
            
        }
        setBelief(f);
        for (Factor Message : getMap_Messages().values()) {
            if(Message==null){
                throw new UnsupportedOperationException();
            }
            if (mapf != null) {
                mapf = Factor.Gen_Product_factors_st(mapf, Message);
            } else {
                mapf = new Factor(Message);
            }
            
        }
        setMap_Belief(mapf);
        
        
    }

    /**
     * @return the Belief
     */
    public Factor getBelief() {
        return Belief;
    }

    /**
     * @param Belief the Belief to set
     */
    public void setBelief(Factor Belief) {
        this.Belief = Belief;
    }

    @Override
    public int compareTo(Junction_tree_node o) {
        return this.variables.size() - o.variables.size();
    }

    /**
     * @return the Map_Messages
     */
    public HashMap<Junction_tree_node, Factor> getMap_Messages() {
        return Map_Messages;
    }

    /**
     * @param Map_Messages the Map_Messages to set
     */
    public void setMap_Messages(HashMap<Junction_tree_node, Factor> Map_Messages) {
        this.Map_Messages = Map_Messages;
    }

    /**
     * @return the Map_Belief
     */
    public Factor getMap_Belief() {
        return Map_Belief;
    }

    /**
     * @param Map_Belief the Map_Belief to set
     */
    public void setMap_Belief(Factor Map_Belief) {
        this.Map_Belief = Map_Belief;
    }
    
    
    
    

}
