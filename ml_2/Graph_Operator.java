/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml_2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import ml_2.Utility;
import org.jgraph.graph.Edge;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.KruskalMinimumSpanningTree;
import org.jgrapht.alg.NeighborIndex;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.Subgraph;

/**
 *
 * @author Shivanth
 */
public class Graph_Operator {

    AbstractBaseGraph base;
    AbstractBaseGraph triangulated = null;
    ArrayList<HashSet<Vertex>> Cliques;
    //Utility util = new Utility();
    private SimpleWeightedGraph<Junction_tree_node, Junction_Tree_Edge> JT;

    Graph_Operator(AbstractBaseGraph base) {
        this.Cliques = new ArrayList();
        this.base = base;
        triangulated = (AbstractBaseGraph) base.clone();
        triangulated.removeAllVertices(base.vertexSet());
    }

    public AbstractBaseGraph getTriangulated() {
        return triangulated;
    }

    public void triangulate() {
        AbstractBaseGraph temp1 = (AbstractBaseGraph) base.clone();
        DefaultListenableGraph temp = new DefaultListenableGraph((Graph) temp1.clone());
        NeighborIndex index = new NeighborIndex(temp);
        temp.addGraphListener(index);
        ArrayList<Vertex> vertexlist = new ArrayList(temp.vertexSet());
        ArrayList<HashSet> Cliques_temp = new ArrayList();
        int count=0;
        //util.show_graph(temp1);
        //util.show_graph(temp);
        for (int i = 0; i < base.vertexSet().size(); i++) {
            Vertex v = find_best_vertex(temp, index);
            //System.out.println("Processing: " + v.toString());
            ArrayList<Vertex> neighbors = new ArrayList<>(index.neighborListOf(v));
            //System.out.println("Neighbors: " + neighbors);
            for (Vertex nb1 : neighbors) {
                for (Vertex nb2 : neighbors) {
                    if (nb1 != nb2 && !temp.containsEdge(nb1, nb2)) {
                        temp.addEdge(nb1, nb2);
                        temp1.addEdge(nb1, nb2);
                        count++;
                    }
                }

            }
            HashSet clique_set = new HashSet(neighbors);
            clique_set.add(v);
            Cliques_temp.add(clique_set);

            temp.removeVertex(v);
            //System.out.println("Removing: " + v.toString());

        }
        //System.out.println("Edges Added: "+count);
        for (HashSet clique : Cliques_temp) {
            boolean flag = false;
            for (HashSet clique1 : Cliques_temp) {
                if (clique != clique1) {
                    if (clique1.containsAll(clique)) {
                        flag = true;
                    }
                }
            }
            if (!flag) {
                Cliques.add(clique);
            }
        }
        //System.out.println(Cliques);
        triangulated = temp1;
    }

    Vertex find_best_vertex(AbstractGraph temp, NeighborIndex neib_list) {
        int count = 0, min = Integer.MAX_VALUE;
        Vertex selected = null;

        for (Iterator it = temp.vertexSet().iterator(); it.hasNext();) {
            Vertex v = (Vertex) it.next();
            ArrayList<Vertex> neighbors = new ArrayList<>(neib_list.neighborListOf(v));
            count = 0;
            for (Vertex nb1 : neighbors) {
                for (Vertex nb2 : neighbors) {
                    if (nb1 != nb2 && !temp.containsEdge(nb1, nb2)) {
                        count++;
                    }
                }

            }
            if (count < min) {
                min = count;
                selected = v;
            }
        }

        return selected;

    }

    public void Create_juntion_Tree() {

        SimpleWeightedGraph<Junction_tree_node, Junction_Tree_Edge> Temp_tree = new SimpleWeightedGraph(Edge.class);
        for (HashSet Vertex_set : Cliques) {
            Junction_tree_node temp = new Junction_tree_node(Vertex_set);
            if (Temp_tree.addVertex(temp) == false) {
                System.out.println("Error inserting to Junction tree");
            }

        }

        for (Junction_tree_node node1 : Temp_tree.vertexSet()) {
            for (Junction_tree_node node2 : Temp_tree.vertexSet()) {
                if (node1 != node2) {
                    HashSet temp = (HashSet) node1.getVariables().clone();
                    temp.retainAll(node2.getVariables());
                    if (!temp.isEmpty()) {
                        Junction_Tree_Edge e = new Junction_Tree_Edge(temp);
                        Temp_tree.addEdge(node1, node2, e);
                        node1.inc_edge_count();
                        Temp_tree.setEdgeWeight(e, 0 - temp.size());
                    }
                }
            }
        }

        KruskalMinimumSpanningTree mst = new KruskalMinimumSpanningTree(Temp_tree);
        setJT((SimpleWeightedGraph<Junction_tree_node, Junction_Tree_Edge>) Temp_tree.clone());

        Set<Junction_Tree_Edge> mst_edges = mst.getMinimumSpanningTreeEdgeSet();
        for (Junction_Tree_Edge e : Temp_tree.edgeSet()) {
            if (!mst_edges.contains(e)) {
                getJT().getEdgeSource(e).dec_edge_count();
                getJT().getEdgeTarget(e).dec_edge_count();
                getJT().removeEdge(e);
            }
        }

        //util.show_graph(getJT());

    }

    /**
     * @return the JT
     */
    public SimpleWeightedGraph<Junction_tree_node, Junction_Tree_Edge> getJT() {
        return JT;
    }

    /**
     * @param JT the JT to set
     */
    public void setJT(SimpleWeightedGraph<Junction_tree_node, Junction_Tree_Edge> JT) {
        this.JT = JT;
    }

}
