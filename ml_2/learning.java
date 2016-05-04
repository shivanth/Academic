package ml_2;

import com.sun.media.sound.InvalidFormatException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import ml_2.Graph_Operator;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author paramita
 */
public class learning {

    static ArrayList<Vertex> Vertexlist = new ArrayList();
    static ArrayList<Factor> Factorlist = new ArrayList();
    static SimpleWeightedGraph<Junction_tree_node, Junction_Tree_Edge> JT;
    static ArrayList<Feature_Function> features_functions = new ArrayList();

    class graph {

        //variables
        int noOfFeatures, noOfSamples, noOfVariables, noOfValues;
        ArrayList<ArrayList> features = new ArrayList();
        ArrayList<ArrayList> featureCliques = new ArrayList();//features associated to cliques

        ArrayList<ArrayList> cliqueFeatures = new ArrayList();
        int sufficientStat[];
        double w[];
        ArrayList<String> cliques = new ArrayList();
        ArrayList<ArrayList> cliqueVal = new ArrayList();
        ArrayList<ArrayList> cliqueSuffStat = new ArrayList();
        ArrayList<ArrayList> marginals = new ArrayList();
        ArrayList<ArrayList> cliqueValInf = new ArrayList();
        ArrayList<ArrayList> potentials = new ArrayList();

        graph(String files) throws FileNotFoundException, IOException {
            BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"));
            BufferedReader br = new BufferedReader(new FileReader(files));

            //reading initial values
            noOfSamples = Integer.parseInt(br.readLine());
            noOfVariables = Integer.parseInt(br.readLine());
            noOfValues = Integer.parseInt(br.readLine());
            noOfFeatures = Integer.parseInt(br.readLine());

            sufficientStat = new int[noOfFeatures];
//            
//            for(int i=0; i<noOfFeatures ; i++ ){
//                
//                ArrayList<ArrayList> list= new ArrayList();
//                features.add(list);
//            }

            String line;

            //reading features
            for (int i = 0; i < noOfFeatures; i++) {

                ArrayList<ArrayList> list = new ArrayList();

                line = br.readLine();

                String words[] = line.split(" ");

                int noOfArg = Integer.parseInt(words[0]);

                int noOfComb = Integer.parseInt(words[1]);

                int current = 2;

                int currentCliqueNo = 0;

                String currentClique = "";

                //for(int m=0;m<words.length ; m++)System.out.print(words[m]+"--");
                //System.out.println();
                for (int j = 0; j < noOfComb; j++) {

                    String s = "";

                    for (int k = 0; k < noOfArg; k++) {

                        s = s.concat(words[current + k] + " ");
                    }

                    s = s.substring(0, s.indexOf(","));

                    //int no = add( s );
                    current += noOfArg;

                    String v = "";

                    for (int k = 0; k < noOfArg; k++) {

                        v = v.concat(words[current + k] + " ");

                    }

                    v = v.substring(0, v.length() - 1);
                    current += noOfArg;

                    //bw.write(s+" "+v+"\n");
                    if (current == (2 + 2 * noOfArg)) {

                        currentClique = s;

                        ArrayList<String> list1 = new ArrayList();

                        list1.add(v);

                        list.add(list1);

                        addFeature(s, i);

                    } else if (currentClique.equals(s)) {

                        list.get(currentCliqueNo).add(v);

                    } else {

                        ArrayList<String> l2 = new ArrayList();

                        l2.add(v);

                        list.add(l2);

                        currentCliqueNo += 1;

                        currentClique = s;

                        addFeature(currentClique, i);

                    }

                }

                features.add(list);

            }
            //print cliques
            bw.write(cliques.toString() + "\n");

            for (int i = 0; i < noOfFeatures; i++) {

                bw.write(featureCliques.get(i).toString() + "\n");
            }

            for (int i = 0; i < cliques.size(); i++) {

                bw.write(cliqueFeatures.get(i).toString() + "\n");
            }

            for (int i = 0; i < noOfFeatures; i++) {

                ArrayList<ArrayList> l = features.get(i);

                for (int j = 0; j < l.size(); j++) {

                    ArrayList<String> l1 = l.get(j);

                    for (int k = 0; k < l1.size(); k++) {

                        bw.write(l1.get(k) + " ");
                    }

                    bw.newLine();
                }
            }

            //read the samples and compute sufficient statistics
            String samples[][] = new String[noOfSamples][noOfVariables];

            for (int i = 0; i < noOfSamples; i++) {

                samples[i] = br.readLine().split(" ");

            }

            for (int i = 0; i < cliques.size(); i++) {

                ArrayList<String> list1 = new ArrayList();

                ArrayList<Integer> list2 = new ArrayList();

                String words[] = cliques.get(i).split(" ");

                int variables[] = new int[words.length];

                for (int j = 0; j < words.length; j++) {

                    variables[j] = Integer.parseInt(words[j]);

                }

                for (int j = 0; j < noOfSamples; j++) {

                    String s = "";

                    for (int k = 0; k < words.length; k++) {

                        s = s.concat(samples[j][variables[k]] + " ");

                    }

                    s = s.trim();

                    int in = list1.indexOf(s);

                    if (in == -1) {

                        list1.add(s);
                        list2.add(1);
                    } else {

                        list2.set(in, list2.get(in) + 1);

                    }

                }

                cliqueVal.add(list1);
                cliqueSuffStat.add(list2);

            }

            for (int i = 0; i < cliques.size(); i++) {

                for (int j = 0; j < cliqueVal.get(i).size(); j++) {

                    bw.write((String) cliqueVal.get(i).get(j) + "><");
                }
                bw.newLine();
            }

            for (int i = 0; i < cliques.size(); i++) {

                bw.write(cliqueSuffStat.get(i).toString() + "\n");

            }
            for (int i = 0; i < noOfFeatures; i++) {

                sufficientStat[i] = 0;
                for (int j = 0; j < featureCliques.get(i).size(); j++) {

                    String currClique = (String) featureCliques.get(i).get(j);
                    int cliqueNo = cliques.indexOf(currClique);

                    ArrayList<String> l = (ArrayList) features.get(i).get(j);

                    for (int k = 0; k < l.size(); k++) {

                        String cliqueLabel = l.get(k);

                        //System.out.println(cliqueLabel+".");
                        int indexForCliqueSuffStat = cliqueVal.get(cliqueNo).indexOf(cliqueLabel);

                        //if(indexForCliqueSuffStat==-1)System.out.println(cliqueLabel);
                        ArrayList<Integer> l1 = cliqueSuffStat.get(cliqueNo);
                        //System.out.println(l1.toString());
                        if (indexForCliqueSuffStat != -1) {

                            sufficientStat[i] += l1.get(indexForCliqueSuffStat);
                        }

                    }

                }
            }

            bw.write(Arrays.toString(sufficientStat) + "\n");

            br.close();
            bw.close();
        }

        void addFeature(String clique, int feature) {

            int cliqueNo = cliques.indexOf(clique);

            if (cliqueNo == -1) {

                cliqueNo = cliques.size();

                cliques.add(clique);

                ArrayList<Integer> l = new ArrayList();

                cliqueFeatures.add(l);

            }

            cliqueFeatures.get(cliqueNo).add(feature);

            if (featureCliques.size() == feature) {

                ArrayList<String> l = new ArrayList();

                l.add(clique);

                featureCliques.add(l);

            } else {

                featureCliques.get(feature).add(clique);

            }

        }

        void constructMarginals() {

            for (int i = 0; i < cliques.size(); i++) {

                ArrayList<String> l = new ArrayList();

                ArrayList<Double> d = new ArrayList();

                marginals.add(d);

                l.addAll(cliqueVal.get(i));

                cliqueValInf.add(l);

            }

            for (int i = 0; i < noOfFeatures; i++) {

                for (int j = 0; j < featureCliques.get(i).size(); j++) {

                    String currClique = (String) featureCliques.get(i).get(j);
                    int cliqueNo = cliques.indexOf(currClique);

                    ArrayList<String> l = (ArrayList) features.get(i).get(j);

                    for (int k = 0; k < l.size(); k++) {

                        String cliqueLabel = l.get(k);
                        if (!(cliqueValInf.get(cliqueNo).contains(cliqueLabel))) {

                            cliqueValInf.get(cliqueNo).add(cliqueLabel);
                        }

                    }
                }
            }
        }

        void writeCliquesAndOthers() throws IOException {
            BufferedWriter bw = new BufferedWriter(new FileWriter("cliques.txt"));

            for (int i = 0; i < cliques.size(); i++) {

                bw.write(cliques.get(i) + "," + cliqueValInf.get(i).size() + "\n");
                for (int j = 0; j < cliqueValInf.get(i).size(); j++) {
                    bw.write(cliqueValInf.get(i).get(j) + "\n");
                }

            }
            bw.close();
        }

        void estimate() throws IOException {

            BufferedWriter bw = new BufferedWriter(new FileWriter("outputW.txt"));

            w = new double[noOfFeatures];

            double tollerance = 0.000001d;

            double grad[];

            
            
            int itr = 0;
            
            

            for (int i = 0; i < w.length; i++) {
                w[i] = 1.;
            }
            //double [] res= {10.23,9.54,9.94,-21.17,3.62,1.72,2.08,-206.89};
            //w=res;
            grad = computeGrad(w,itr);


            while (norm(grad) > tollerance) {

                itr += 1;

                //System.out.println("itr " + itr);
                if (itr > 300) {
                    break;
                }

                for (int i = 0; i < noOfFeatures; i++) {

                    w[i] += grad[i];

                }

                grad = computeGrad(w,itr);

                //System.out.println("Weights "+Arrays.toString(w)+ " ");
                //for (int i = 0; i < w.length; i++)                System.out.println(w[i]+" ");
                //                System.out.println(" ");
                
            }

            bw.write("Feature Weight\n");
            System.out.println("Weights");
            for (int i = 0; i < noOfFeatures; i++) {

                bw.write(i + " " + w[i]);
                bw.newLine();
                System.out.println(w[i]);
            }

            bw.write(Arrays.toString(w));

            bw.close();

        }

        double[] computeGrad(double w[],int itr) throws IOException {

            call(w,itr);

            double g[] = new double[noOfFeatures];

            //System.out.println("grad before:");
            for (int i = 0; i < noOfFeatures; i++) {

                g[i] = 0;

                for (int j = 0; j < featureCliques.get(i).size(); j++) {

                    String currClique = (String) featureCliques.get(i).get(j);
                    int cliqueNo = cliques.indexOf(currClique);

                    ArrayList<String> l = (ArrayList) features.get(i).get(j);

                    for (int k = 0; k < l.size(); k++) {

                        String cliqueLabel = l.get(k);
                        int indexForMarginals = cliqueValInf.get(cliqueNo).indexOf(cliqueLabel);

                        ArrayList<Double> l1 = marginals.get(cliqueNo);
                        if (indexForMarginals != -1) {
                            double d = l1.get(indexForMarginals);
                            //System.out.print(d+" ");
                            g[i] += d;
                        }

                    }
                    
                    

                }
                //System.out.println();
                //System.out.print("before"+g[i]+" ");
                g[i] = ((double) sufficientStat[i] / noOfSamples) - g[i];
                //System.out.print("after"+g[i]+" <>");
            }
            //System.out.println();
            //System.out.println(Arrays.toString(g));
            return g;

        }

        void call(double w[],int itr) throws IOException {

            //write w
//            
//            BufferedWriter bw = new BufferedWriter(new FileWriter("W.txt"));
//
//            for (int i = 0; i < noOfFeatures; i++) {
//
//                bw.write(i + " " + w[i]);
//                bw.newLine();
//            }
//
//            bw.write(Arrays.toString(w));
//
//            bw.close();
//            //******************************************************************************
            initializePotentials();
            createPotentials();
            convert_to_factors();
//            if(itr>=120){
//                for(Factor f:Factorlist)
//                    System.out.println(f.print_table1());
//                
//            }
            //convert_to_factors();

//            
//            for (String clique : cliques) {
//                Create_factor(w, clique);
//            }
            infer();

            //call junction tree to input w and find marginals and give back as arraylist
            //**********************************************************************************
            BufferedReader br = new BufferedReader(new FileReader("marginals.txt"));

            for (int i = 0; i < cliques.size(); i++) {

                br.readLine();
                ArrayList<Double> l = new ArrayList();

                for (int j = 0; j < cliqueValInf.get(i).size(); j++) {
                    l.add(Double.parseDouble(br.readLine()));

                }
                marginals.get(i).clear();
                marginals.set(i, l);

            }
//            
            for (int i = 0; i < cliques.size(); i++) {
                //System.out.println(marginals.get(i).toString());
//                
            }

            br.close();

        }

        void initializePotentials() {

            potentials = new ArrayList();

            for (int i = 0; i < cliques.size(); i++) {

                ArrayList<Double> l = new ArrayList();

                for (int j = 0; j < cliqueValInf.get(i).size(); j++) {

                    l.add(0.);

                }
                potentials.add(l);
            }
        }

        void createPotentials() {

            for (int i = 0; i < cliques.size(); i++) {

                ArrayList<String> currentCliqueValue = cliqueValInf.get(i);

                ArrayList<Integer> listOfFeatures = cliqueFeatures.get(i);

                for (int j = 0; j < listOfFeatures.size(); j++) {

                    int currentFeature = listOfFeatures.get(j);

                    int currentCliqueIndex = featureCliques.get(currentFeature).indexOf(cliques.get(i));

                    ArrayList<String> currentCliqueValueInFeature = (ArrayList<String>) features.get(currentFeature).get(currentCliqueIndex);

                    for (int k = 0; k < currentCliqueValue.size(); k++) {

                        if (currentCliqueValueInFeature.contains(currentCliqueValue.get(k))) {

                            double v = (double) potentials.get(i).get(k);

                            v += w[currentFeature];

                            potentials.get(i).set(k, v);

                        }
                    }
                }
            }

            for (int i = 0; i < cliques.size(); i++) {

                for (int j = 0; j < potentials.get(i).size(); j++) {

                    double v = (double) potentials.get(i).get(j);

                    potentials.get(i).set(j, Math.exp(v));
                }
                //System.out.println(potentials.get(i).toString());
            }
        }

        double norm(double d[]) {
            double val = 0.;
            for (int i = 0; i < d.length; i++) {

                val += d[i] * d[i];
            }

            val = Math.sqrt(val);
            //System.out.println("Norm : "+val);
            return val;
            
        }

        void convert_to_factors() {
            Factorlist.clear();
            for (int i = 0; i < cliques.size(); i++) {
                String clique = cliques.get(i);
                String[] ver = clique.split(" ");
                ArrayList<Vertex> vtx_lst = new ArrayList();
                for (String s : ver) {
                    vtx_lst.add(Vertexlist.get(Integer.parseInt(s)));
                }
                Factor F = new Factor(vtx_lst);
                MDArrayIndex index = new MDArrayIndex(F.getTable());
                ArrayList<String> string_indexs = cliqueValInf.get(i);
                int k = 0;
                for (String string_index : string_indexs) {
                    String s[] = string_index.split(" ");
                    if (s.length != index.getIndex_array().length) {
                        throw new UnsupportedOperationException();
                    }
                    for (int j = 0; j < s.length; j++) {
                        index.set_index(F.get_dimension_of_vertex(vtx_lst.get(j)), Integer.parseInt(s[j]));
                    }
                    F.getTable().put(index, (double) potentials.get(i).get(k));
                    k++;
                }
                Factorlist.add(F);
                //System.out.println(F.print_table1());
            }

        }

    }

    learning(String file) throws IOException {

        graph g = new graph(file);
        g.constructMarginals();
        g.writeCliquesAndOthers();
        //g.call();
        g.initializePotentials();
        g.estimate();

    }

    public static void main(String[] args) throws IOException {
        String Filename = args[0];
        long start=System.currentTimeMillis();
        UndirectedGraph<Vertex, DefaultEdge> input = readgraph(Filename);
        Graph_Operator g = new Graph_Operator((AbstractBaseGraph) input);
        //Utility util= new Utility();

        g.triangulate();
        g.Create_juntion_Tree();

        JT = g.getJT();
        //util.show_graph(JT);
        learning l = new learning(Filename);
        System.out.println("Time in milliseconds :"+(System.currentTimeMillis()-start));
    }

    public static void infer() {
        ArrayList<Junction_tree_node> list = new ArrayList<>(JT.vertexSet());
        //read_Factors(Potential_file);/*ToDO Get potentials to JT*/
        ArrayList<Factor> list_factor = (ArrayList<Factor>) Factorlist.clone();
        for (Junction_tree_node jt_vertex : list) {
            HashSet<Vertex> set = jt_vertex.getVariables();
            HashSet<Vertex> temp = (HashSet<Vertex>) set.clone();
            ArrayList<Factor> f_list = new ArrayList();

            Collections.sort(list_factor);
            for (Factor f : list_factor) {
                if (temp.containsAll(f.getVertex_to_dimension().keySet())) {
                    f_list.add(f);
                }
            }
            Iterator it = f_list.iterator();
            Factor F = null;
            if (it.hasNext()) {
                F = (Factor) it.next();
                list_factor.remove(F);
            }
            while (it.hasNext()) {
                Factor ft = (Factor) it.next();
                Factor tempf = new Factor(ft);
                tempf.getVertex_to_dimension().keySet().retainAll(F.getVertex_to_dimension().keySet());
                list_factor.remove(ft);
                F = Factor.Product_factors_st(F, ft, new HashSet<>(tempf.getVertex_to_dimension().keySet()));
            }
            jt_vertex.setNode_factor(F);
        }

        Message_Passing M = new Message_Passing(JT);
        M.execute();
        ArrayList<Vertex> q = new ArrayList();
        try {
            //        q.add(Vertexlist.get(1));
//        Factor f = M.query(q);
            Marginals("cliques.txt", "Marginals.txt", M);
        } catch (IOException ex) {
            Logger.getLogger(learning.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Not Used now
     *
     * @param Filename
     */
    static void read_Factors(String Filename) {
        BufferedReader br = null;
        int nodes, edges;
        String Line;
        Factorlist.clear();

        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(Filename));
            while ((Line = br.readLine()) != null) {
                String a[] = Line.split(" ");
                ArrayList<Vertex> factor_vertex_list = new ArrayList<>();
                int count_lines = 1;
                if (a.equals("#")) {
                    throw new InvalidFormatException("File format is not correct");
                }
                for (int i = 1; i < a.length; i++) {
                    factor_vertex_list.add(Vertexlist.get(Integer.parseInt(a[i])));
                    count_lines *= Vertexlist.get(Integer.parseInt(a[i])).getNo_of_values();
                }
                Factor f = new Factor(factor_vertex_list);
                MDArrayIndex index = new MDArrayIndex(f.getTable());
                for (int i = 0; i < count_lines; i++) {
                    Line = br.readLine();
                    a = Line.split(" ");
                    for (int k = 0; k < factor_vertex_list.size(); k++) {
                        index.set_index(f.get_dimension_of_vertex(factor_vertex_list.get(k)), Integer.parseInt(a[k]));
                    }
                    f.getTable().put(index, Double.parseDouble(a[a.length - 1]));
                }
                f.setName(Line);
                Factorlist.add(f);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                for (Factor f : Factorlist) {

                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public static UndirectedGraph<Vertex, DefaultEdge> readgraph(String Filename) throws FileNotFoundException, IOException {
        UndirectedGraph<Vertex, DefaultEdge> input = new SimpleGraph(DefaultEdge.class);
        BufferedReader br = null;
        int nodes, values, features;
        String Line;

        String sCurrentLine;
        br = new BufferedReader(new FileReader(Filename));
        br.readLine();//no_of instances
        nodes = Integer.parseInt(br.readLine());//no of variables
        values = Integer.parseInt(br.readLine());//no of values
        for (int i = 0; i < nodes; i++) {
            Vertex temp = new Vertex(i);
            temp.setNo_of_values(values);
            Vertexlist.add(temp);
            input.addVertex(temp);
        }
        features = Integer.parseInt(br.readLine());//no of features
        for (int i = 0; i < features; i++) {
            Line = br.readLine();
            StringTokenizer s = new StringTokenizer(Line, " ,");
            int arity = Integer.parseInt(s.nextToken());
            int count = Integer.parseInt(s.nextToken());
            ArrayList<Vertex> Clique = new ArrayList();
            ArrayList<Integer> clique_Values = new ArrayList<>();
            Feature_Function f = new Feature_Function();
            while (count > 0) {
                int temp = arity;
                while (temp > 0) {
                    Clique.add(Vertexlist.get(Integer.parseInt(s.nextToken())));
                    temp--;
                }
                //System.out.println(Clique);
                for (Vertex v1 : Clique) {
                    for (Vertex v2 : Clique) {
                        if (v1 != v2 && !input.containsEdge(v1, v2)) {
                            input.addEdge(v1, v2);
                        }
                    }
                }

                temp = arity;
                while (temp > 0) {

                    clique_Values.add(Integer.parseInt(s.nextToken()));
                    temp--;
                }

                //System.out.println(Clique);
                //System.out.println(clique_Values);
                f.add_predicate(Clique, clique_Values);
                f.add_cliques(Clique);
                Clique.clear();
                clique_Values.clear();
                count--;

            }

            features_functions.add(f);
            //System.out.println("-------------------");
        }
//        Utility u = new Utility();
//        u.show_graph((AbstractGraph) input);
        return input;
    }

    static void Create_factor(double W[], String clique) {
        StringTokenizer st = new StringTokenizer(clique, " ");
        ArrayList<Vertex> v_list = new ArrayList<>();
        while (st.hasMoreTokens()) {
            v_list.add(Vertexlist.get(Integer.parseInt(st.nextToken())));
        }
        Factor F = new Factor(v_list);
        MDArrayIndex index = new MDArrayIndex(F.getTable());

        do {
            ArrayList<Integer> intList = new ArrayList<Integer>();
            for (int i = 0; i < index.getIndex_array().length; i++) {
                intList.add(index.getIndex_array()[i]);
            }
            double sum = 0;
            for (int i = 0; i < features_functions.size(); i++) {
                sum += W[i] * features_functions.get(i).value(v_list, intList);
            }
            double phi = Math.exp(sum);
            F.getTable().put(index, phi);
        } while (index.increment() != 0);
        Factorlist.add(F);
    }

    /**
     * *
     * Call only after message passing
     *
     * @param Filename
     */
    static void Marginals(String InputFile, String OutputFile, Message_Passing MP) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(InputFile));
        BufferedWriter bw = new BufferedWriter(new FileWriter(OutputFile));
        String Line;
        ArrayList<Vertex> query_clique = new ArrayList();

        while ((Line = br.readLine()) != null) {
            String[] str = Line.split(",");
            StringTokenizer st = new StringTokenizer(str[0], " ");
            bw.write(Line);
            bw.write("\n");
            query_clique.clear();
            while (st.hasMoreTokens()) {
                query_clique.add(Vertexlist.get(Integer.parseInt(st.nextToken())));
            }

            Factor F = MP.query(query_clique);
            MDArrayIndex index = new MDArrayIndex(F.getTable());
            for (int i = 0; i < Integer.parseInt(str[1]); i++) {
                Line = br.readLine();
                StringTokenizer st1 = new StringTokenizer(Line, " ");
                int j = 0;
                while (st1.hasMoreTokens()) {
                    index.set_index(F.get_dimension_of_vertex(query_clique.get(j)), Integer.parseInt(st1.nextToken()));
                    j++;
                }
                bw.write(F.getTable().get(index).toString() + "\n");
            }

        }
        br.close();
        bw.close();

    }

}
