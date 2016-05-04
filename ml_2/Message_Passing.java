/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml_2;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgrapht.alg.NeighborIndex;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

/**
 *
 * @author Shivanth
 */
public class Message_Passing {

    SimpleWeightedGraph<Junction_tree_node, Junction_Tree_Edge> JT;
    Junction_tree_node root = null;
    static BufferedWriter trace;

    static{
           try {
            trace = new BufferedWriter(new FileWriter(new File("Message_trace")));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Message_Passing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Message_Passing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Message_Passing(SimpleWeightedGraph<Junction_tree_node, Junction_Tree_Edge> JT) {
        this.JT = JT;
     
    }

    public ArrayList<Junction_tree_node> get_leaf_nodes() {
        ArrayList<Junction_tree_node> ret = new ArrayList<>();
        for (Junction_tree_node n : JT.vertexSet()) {
            if (n.getEdge_count() == 1) {
                ret.add(n);
            }
        }
        return ret;
    }

    public void set_root() {
        Object[] varray = JT.vertexSet().toArray();

        for (Junction_tree_node n : JT.vertexSet()) {
            if (n.getEdge_count() == 1) {
                root = n;
                break;
            }
        }
    }

    public void execute() {
        if (root == null) {
            set_root();
        }
        for(Junction_tree_node n : JT.vertexSet()){
            n.setBelief(null);
            n.getMessages().clear();
            
        }
        try {
            //Factor.trace.write("Message Passing started***************************************************");
        } catch (Exception ex) {
            Logger.getLogger(Message_Passing.class.getName()).log(Level.SEVERE, null, ex);
        }
        transfer_message_to_root(root, null);
        
        try {
            trace.write("Message from root \n");
        } catch (IOException ex) {
            Logger.getLogger(Message_Passing.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        transfer_message_from_root(root, null);
        
        for (Junction_tree_node jn : JT.vertexSet()) {
            jn.update_belief();
            try {
                trace.write("Belief at jn" + jn.getBelief().print_table() + "\n");
            } catch (IOException ex) {
                Logger.getLogger(Message_Passing.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void transfer_message_to_root(Junction_tree_node current, Junction_tree_node parent) {
        NeighborIndex index = new NeighborIndex(JT);
        ArrayList<Junction_tree_node> nei_list = new ArrayList<>(index.neighborListOf(current));
        Collections.sort(nei_list);
        for (Junction_tree_node j : nei_list) {
            if (j != parent) {
                transfer_message_to_root(j, current);
            }
        }
        Factor f = current.getNode_factor();
        StringBuilder Messageb = new StringBuilder();
        if (f != null) {
           // Messageb.append(f.print_table1() + " ");
        }
        ArrayList<Factor>dummy = new ArrayList<>(current.getMessages().values());
        Collections.sort(dummy);
        for (Factor Message : dummy) {
            if (f != null) {
                f = Factor.Gen_Product_factors_st(f, Message);
            } else {
                f = new Factor(Message);
            }
            
            Messageb.append(Message.print_table1() + " ");
        }
        if (parent != null) {
            Junction_Tree_Edge ed = JT.getEdge(current, parent);
            ArrayList<Vertex> set = new ArrayList(f.getDimension_to_vertex().values());
            set.removeAll(ed.intersectionset);
            try {
                trace.write("");
                //trace.write("\nBefore summing out" + f.print_table1());
            } catch (IOException ex) {
                Logger.getLogger(Message_Passing.class.getName()).log(Level.SEVERE, null, ex);
            }
            f = f.Sum_out(set); 
            try {
                trace.write("Message From "+current+" to "+parent+"\n");
            } catch (IOException ex) {
                Logger.getLogger(Message_Passing.class.getName()).log(Level.SEVERE, null, ex);
            }
            parent.Put_Message(current, f);
            
        };
//      
    }

    public void transfer_message_from_root(Junction_tree_node current, Junction_tree_node parent) {
        StringBuilder Messageb = new StringBuilder();
        NeighborIndex index = new NeighborIndex(JT);
        ArrayList<Junction_tree_node> nei_list = new ArrayList<>(index.neighborsOf(current));
        Collections.sort(nei_list);
        for (Junction_tree_node j : nei_list) {
            Factor f = current.getNode_factor();
            if (j != parent) {
                ArrayList<Junction_tree_node>dummy = new ArrayList<>(current.getMessages().keySet());
                Collections.sort(dummy);
                for (Junction_tree_node node : dummy) {
                    if (node != j) {

                        Factor Message = current.getMessages().get(node);
                        if (f != null) {
                            f = Factor.Gen_Product_factors_st(f, Message);
                        } else {
                            f = new Factor(Message);
                        }
                        Messageb.append(Message + " ");
                    }
                    else{
                        Messageb.append("Not sending message to "+j);
                    }
                }
                Junction_Tree_Edge ed = JT.getEdge(j, current);
                ArrayList<Vertex> set = new ArrayList(f.getDimension_to_vertex().values());
                set.removeAll(ed.intersectionset);

                f = f.Sum_out(set);
                try {
                    trace.write("Message From "+current+" to "+j+"\n");
                } catch (IOException ex) {
                    Logger.getLogger(Message_Passing.class.getName()).log(Level.SEVERE, null, ex);
                }
                j.Put_Message(current, f);

                transfer_message_from_root(j, current);
            }
        }
        try {
            trace.flush();
        } catch (IOException ex) {
            Logger.getLogger(Message_Passing.class.getName()).log(Level.SEVERE, null, ex);
        }
}
    Factor query(ArrayList<Vertex> list) {
        Factor f = null;
        ArrayList<Junction_tree_node> list2 = new ArrayList<>(JT.vertexSet());

        for (Junction_tree_node node : list2) {
            if (node.getVariables().containsAll(list)) {
                f = new Factor(node.getBelief());
                ArrayList<Vertex> q = new ArrayList(f.getVertex_to_dimension().keySet());
                q.removeAll(list);
                f = f.Sum_out(q).Normalise_factor();
                break;
            }
        }
        return f;
    }

}
