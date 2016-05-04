/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml_2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Shivanth
 */
public class Feature_Function {

    ArrayList<HashMap<Vertex, Integer>> feature = new ArrayList();
    ArrayList<ArrayList<Vertex>> cliques=new ArrayList();
    void add_predicate(ArrayList<Vertex> a, ArrayList<Integer> b) {
        HashMap<Vertex, Integer> map = new HashMap();
        if (a.size() != b.size()) {
            throw new UnsupportedOperationException();
        }
        int i = 0;
        for (Vertex v : a) {
            map.put(v, b.get(i));
            i++;
        }
        feature.add(map);

    }

    int value(HashMap<Vertex, Integer> variables) {
        for (HashMap<Vertex, Integer> map : feature) {
            boolean satisfies=true;
            for (Vertex v : map.keySet()) {
                if (variables.keySet().contains(v)) {
                    if (map.get(v).intValue() == variables.get(v).intValue()) {
                        continue;
                    }
                    else {
                        satisfies=false;
                        break;
                    }
                } else {
                    satisfies=false;
                }
            }
            if(satisfies)
                return 1;
            
        }

        return 0;
    }
    
    int value(ArrayList<Vertex> variables,ArrayList<Integer> values) {
        boolean cont=false;
        for( ArrayList<Vertex> c:cliques){
            if(variables.containsAll(c)&&c.containsAll(variables)){
                cont=true;
                break;
            }
        }
        if(!cont)
            return 0;
        if(variables.size()!=values.size())
            throw new UnsupportedOperationException();
        for (HashMap<Vertex, Integer> map : feature) {
            boolean satisfies=true;
            int i=0;//index for both variable and value
            for (Vertex v : map.keySet()) {
                if (variables.contains(v)) {
                    if (map.get(v).intValue() == values.get(variables.indexOf(v)).intValue()) {
                        continue;
                    }
                    else {
                        satisfies=false;
                        break;
                    }
                } else {
                    satisfies=false;
                }
                i++;
            }
            if(satisfies)
                return 1;
            
        }

        return 0;
    }
    void add_cliques(ArrayList<Vertex> v_list){
        cliques.add(v_list);
    }
}
