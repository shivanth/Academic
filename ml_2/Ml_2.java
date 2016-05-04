///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package ml_2;
//
//import com.sun.media.sound.InvalidFormatException;
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.StringTokenizer;
//import org.jgraph.graph.DefaultEdge;
//import org.jgrapht.UndirectedGraph;
//import org.jgrapht.graph.AbstractBaseGraph;
//import org.jgrapht.graph.SimpleGraph;
//import ml_2.Utility;
//import org.jgrapht.graph.AbstractGraph;
//import org.jgrapht.graph.SimpleWeightedGraph;
//
///**
// *
// * @author Shivanth
// */
//public class Ml_2 {
//
//    /**
//     * @param args the command line arguments
//     */
//    
//    static ArrayList<Vertex> Vertexlist = new ArrayList();
//    static ArrayList<Factor> Factorlist = new ArrayList();
//    static SimpleWeightedGraph<Junction_tree_node, Junction_Tree_Edge> JT;
//    
//    public static void main(String[] args) throws IOException {
//        
//    }
//    
//    public static void  infer(String Potential_file){
//        ArrayList<Junction_tree_node> list = new ArrayList<>(JT.vertexSet());
//        read_Factors(Potential_file);
//        for (Junction_tree_node jt_vertex : list) {
//            HashSet<Vertex> set = jt_vertex.getVariables();
//            HashSet<Vertex> temp = (HashSet<Vertex>) set.clone();
//            ArrayList<Factor> f_list = new ArrayList();
//            ArrayList<Factor> list_factor = (ArrayList<Factor>) Factorlist.clone();
//            Collections.sort(list_factor);
//            for (Factor f : list_factor) {
//                if (temp.containsAll(f.getVertex_to_dimension().keySet())) {
//                    f_list.add(f);
//                }
//            }
//            Iterator it = f_list.iterator();
//            Factor F = null;
//            if (it.hasNext()) {
//                F = (Factor) it.next();
//                list_factor.remove(F);
//            }
//            while (it.hasNext()) {
//                Factor ft = (Factor) it.next();
//                Factor tempf = new Factor(ft);
//                tempf.getVertex_to_dimension().keySet().retainAll(F.getVertex_to_dimension().keySet());
//                list_factor.remove(ft);
//                F = Factor.Product_factors_st(F, ft, new HashSet<>(tempf.getVertex_to_dimension().keySet()));
//            }
//            jt_vertex.setNode_factor(F);
//            
//            //F.setName(jt_vertex.toString());
//        }
//        
//        Message_Passing M=new Message_Passing(JT);
//        M.execute();
//        ArrayList<Vertex> q= new ArrayList();
//        q.add(Vertexlist.get(1));
//        Factor f =M.query(q);
//        
//        
//    }
//
//    public static UndirectedGraph<Vertex, DefaultEdge> readgraph(String Filename) throws FileNotFoundException, IOException {
//        UndirectedGraph<Vertex, DefaultEdge> input = new SimpleGraph(DefaultEdge.class);
//        BufferedReader br = null;
//        int nodes, values, features;
//        String Line;
//        
//
//        String sCurrentLine;
//        br = new BufferedReader(new FileReader(Filename));
//        br.readLine();//no_of instances
//        nodes = Integer.parseInt(br.readLine());//no of variables
//        values = Integer.parseInt(br.readLine());//no of values
//        for (int i = 0; i < nodes; i++) {
//            Vertex temp = new Vertex(i);
//            temp.setNo_of_values(values);
//            Vertexlist.add(temp);
//            input.addVertex(temp);
//        }
//        features = Integer.parseInt(br.readLine());//no of features
//        for (int i = 0; i < features; i++) {
//            Line = br.readLine();
//            StringTokenizer s = new StringTokenizer(Line, " ,");
//            int arity = Integer.parseInt(s.nextToken());
//            int count = Integer.parseInt(s.nextToken());
//            ArrayList<Vertex> Clique=new ArrayList();
//            
//            
//            while (count > 0) {
//                int temp=arity;
//                while (temp > 0) {
//                    
//                    Clique.add(Vertexlist.get(Integer.parseInt(s.nextToken())));
//                    temp--;
//                }
//                System.out.println(Clique);
//                for(Vertex v1:Clique){
//                    for(Vertex v2:Clique){
//                        if(v1!=v2&&!input.containsEdge(v1, v2))
//                        input.addEdge(v1, v2);
//                    }
//                }
//                Clique.clear();
//                temp=arity;
//                while (temp > 0) {
//                    
//                    s.nextToken();
//                    temp--;
//                }
//                count--;
//            }
//            System.out.println("-------------------");
//        }
//        Utility u=new Utility();
//        u.show_graph((AbstractGraph) input);
//        return input;
//    }
//    
//    static void read_Factors(String Filename) {
//        BufferedReader br = null;
//        int nodes, edges;
//        String Line;
//        Factorlist.clear();
//
//        try {
//            String sCurrentLine;
//            br = new BufferedReader(new FileReader(Filename));
//            while ((Line = br.readLine()) != null) {
//                String a[] = Line.split(" ");
//                ArrayList<Vertex> factor_vertex_list = new ArrayList<>();
//                int count_lines = 1;
//                if (a.equals("#")) {
//                    throw new InvalidFormatException("File format is not correct");
//                }
//                for (int i = 1; i < a.length; i++) {
//                    factor_vertex_list.add(Vertexlist.get(Integer.parseInt(a[i])));
//                    count_lines *= Vertexlist.get(Integer.parseInt(a[i])).getNo_of_values();
//                }
//                Factor f = new Factor(factor_vertex_list);
//                MDArrayIndex index = new MDArrayIndex(f.getTable());
//                for (int i = 0; i < count_lines; i++) {
//                    Line = br.readLine();
//                    a = Line.split(" ");
//                    for (int k = 0; k < factor_vertex_list.size(); k++) {
//                        index.set_index(f.get_dimension_of_vertex(factor_vertex_list.get(k)), Integer.parseInt(a[k]));
//                    }
//                    f.getTable().put(index, Double.parseDouble(a[a.length - 1]));
//                }
//                f.setName(Line);
//                Factorlist.add(f);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                for (Factor f : Factorlist) {
//                   
//                }
//                if (br != null) {
//                    br.close();
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//
//    }
//
//}
