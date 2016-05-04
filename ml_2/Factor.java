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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
//import static machine_learning.Machine_Learning.trace;

/**
 *
 * @author Shivanth
 */
public class Factor implements Comparable<Factor> {

    private String name;
    private MultiDimensionalMatrix table;
    private HashMap<Vertex, Integer> vertex_to_dimension;
    private HashMap<Integer, Vertex> dimension_to_vertex;
    static BufferedWriter trace;
    

//    static {
//        try {
//            trace = new BufferedWriter(new FileWriter(new File("Multi_trace")));
//            
//        } catch (IOException ex) {
//            Logger.getLogger(Factor.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    private boolean mark=false;

    Factor(List<Vertex> list) {
        
        vertex_to_dimension = new HashMap<>();
        dimension_to_vertex = new HashMap<>();
        int dimensions[] = new int[list.size()];
        for (int i = 0; i < dimensions.length; i++) {
            dimensions[i] = list.get(i).getNo_of_values();
            vertex_to_dimension.put(list.get(i), i);
            dimension_to_vertex.put(i, list.get(i));
        }
        table = new MultiDimensionalMatrix(dimensions);

        
    }

    Factor(List<Vertex> list, MultiDimensionalMatrix mt) {

        vertex_to_dimension = new HashMap<>();
        dimension_to_vertex = new HashMap<>();
        int dimensions[] = new int[list.size()];
        for (int i = 0; i < dimensions.length; i++) {
            dimensions[i] = list.get(i).getNo_of_values();
            vertex_to_dimension.put(list.get(i), i);
            dimension_to_vertex.put(i, list.get(i));
        }
        table = new MultiDimensionalMatrix(mt);
        
    }

    public Factor(Factor f) {
        if(f==null)
            return;
        if (f.getVertex_to_dimension().keySet().size() != f.getDimension_to_vertex().keySet().size()) {
            System.out.println("Error");
        }
        
        
        
        if (f != null) {
            table = new MultiDimensionalMatrix(f.getTable());

            vertex_to_dimension = new HashMap<Vertex, Integer>();
            dimension_to_vertex = new HashMap<Integer, Vertex>();
            for (Vertex v : f.vertex_to_dimension.keySet()) {
                vertex_to_dimension.put(v, (f.vertex_to_dimension.get(v).intValue()));
                dimension_to_vertex.put(f.vertex_to_dimension.get(v).intValue(), v);
            }

        }

        
    }
    
    
    ArrayList<Vertex> get_clique(){
        ArrayList<Vertex> ret= new ArrayList();
        for(int i=0;i<dimension_to_vertex.size();i++){
            ret.add(dimension_to_vertex.get(i));
        }
     return ret;
    }

    int get_dimension_of_vertex(Vertex v) {
        return getVertex_to_dimension().get(v);
    }

    Vertex get_vertex_at_dimension(int d) {
        return getDimension_to_vertex().get(d);
    }

    
    Factor Sum_out(Vertex v) {
        int dim = get_dimension_of_vertex(v);
        ArrayList<Vertex> list = new ArrayList<>(getVertex_to_dimension().keySet());
        //Collections.sort(list);
        list.remove(v);
        Factor ret = new Factor(list);
        for (int i = 0; i < dimension_to_vertex.size(); i++) {
            if (i < dim) {
                ret.getDimension_to_vertex().put(i, getDimension_to_vertex().get(i));
                ret.getVertex_to_dimension().put(getDimension_to_vertex().get(i), i);
            }
            if (i > dim) {
                ret.getDimension_to_vertex().put(i - 1, getDimension_to_vertex().get(i));
                ret.getVertex_to_dimension().put(getDimension_to_vertex().get(i), i - 1);
            }
        }

        for (int i = 0; i < v.getNo_of_values(); i++) {
            MultiDimensionalMatrix tab = new MultiDimensionalMatrix(getTable().reduce_dimension_by1(dim, i));
            ret.getTable().add_put(tab);
        }
        return ret;
        
    }

    

    Factor Map_out(ArrayList<Vertex> v_list) {
        Factor ret = new Factor(v_list);
        MDArrayIndex new_index = new MDArrayIndex(ret.getTable());
        MDArrayIndex index = new MDArrayIndex(getTable());

        int k = 0;
        Iterator it = this.vertex_to_dimension.keySet().iterator();
        Vertex a;
        for (int i = 0; i < this.vertex_to_dimension.keySet().size(); i++) {
            if (it.hasNext()) {
                a = (Vertex) it.next();
            } else {
                throw new UnsupportedOperationException("Dimensions");
            }
            if (v_list.contains(a)) {
                ret.vertex_to_dimension.put(a, i - k);
                ret.dimension_to_vertex.put(i - k, a);
            } else {
                k++;
            }

        }
        index.reset();
        do {
            for (Vertex v : v_list) {
                index.setConst_dimension(getVertex_to_dimension().get(v));
                index.setConst_index(getVertex_to_dimension().get(v), new_index.get_index(v_list.indexOf(v)));
            }
            do {
                ret.table.put(new_index, Math.max(ret.table.get(new_index), table.get(index)));
            } while (index.increment() != 0);
        } while (new_index.increment() != 0);

        return ret;
    }

    Factor Sum_out(ArrayList<Vertex> v_list) {
        Factor F = new Factor(this);
        //Collections.sort(v_list);
        try {
            //trace.write("\nSumming out " + print_table1() + "\nResult \n ");

            for (Vertex v : v_list) {
                F = F.Sum_out(v);
            }
            //trace.write(F.print_table1());
//        Factor F = new Factor(this);
//        MDArrayIndex index=new MDArrayIndex(F.getTable());
//        for(Vertex v : v_list){
//            index.setConst_dimension(F.get_dimension_of_vertex(v));
//        }

        } catch (Exception ex) {
            Logger.getLogger(Factor.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    
        return F;
    }

    public double Normalise() {
        MDArrayIndex i = new MDArrayIndex(getTable());
        double ret = 0;
        do {
            ret += getTable().get(i);
        } while (i.increment() != 0);
        return ret;

    }

    public Factor Normalise_factor() {
        Factor ret = new Factor(this);
        MDArrayIndex i = new MDArrayIndex(getTable());
        do {
            ret.getTable().put(i, new Double(getTable().get(i) / Normalise()));
        } while (i.increment() != 0);
        return ret;
    }

//    void Product_factors(Factor factor1, Factor factor2, HashSet<Vertex> intersection) {
//        if(intersection.isEmpty())
//            System.out.println("Error");
//        
//        int dima[] = new int[intersection.size()];
//        int dimb[] = new int[intersection.size()];
//        int i = 0;
//        vertex_to_dimension.clear();
//        dimension_to_vertex.clear();
//        for (Vertex v : intersection) {
//            dima[i] = factor1.get_dimension_of_vertex(v);
//            dimb[i] = factor2.get_dimension_of_vertex(v);
//            vertex_to_dimension.put(v, i + factor1.getTable().get_no_dimensions() + factor2.getTable().get_no_dimensions());
//            dimension_to_vertex.put(i + factor1.getTable().get_no_dimensions() + factor2.getTable().get_no_dimensions(), v);
//        }
//        int k = 0;
//        for (int j = 0; j < factor1.getTable().dimensions.length; j++) {
//            vertex_to_dimension.put(factor1.get_vertex_at_dimension(j), j);
//            dimension_to_vertex.put(j, factor1.get_vertex_at_dimension(j));
//
//        }
//        for (int j = 0; j < factor2.getTable().dimensions.length; j++) {
//            vertex_to_dimension.put(factor2.get_vertex_at_dimension(j), factor1.getTable().dimensions.length + j);
//            dimension_to_vertex.put(factor2.getTable().dimensions.length + j, factor1.get_vertex_at_dimension(j));
//        }
//        
//        setTable(recurse(factor1.getTable(), dima, factor2.getTable(), dimb, i));
//
//    }
    static Factor Gen_Product_factors_st(Factor factor1, Factor factor2) {
        HashSet<Vertex> intersection;
        //Factor tempf = new Factor(factor1);
        HashMap<Integer, Vertex> temp;
        temp = new HashMap(factor1.dimension_to_vertex);
        //System.out.println(factor1+"*"+factor2);
        boolean retainAll = temp.values().retainAll(factor2.dimension_to_vertex.values());
        intersection = new HashSet<>(temp.values());
        Factor res = Product_factors_st(factor1, factor2, intersection);
        //System.out.println(res.check(Product_factors_st(factor1, factor2, intersection)));
    
        if (res.getVertex_to_dimension().keySet().size() != res.getDimension_to_vertex().keySet().size()) {
            System.out.println("Error");
        }

        return res;
    }

    static Factor Product_factors_st(Factor factor1, Factor factor2, HashSet<Vertex> intersection) {
        if (intersection.isEmpty()) {

            return new Factor(factor1);

        }
                Factor ret = new Factor(factor1);
        
        if(factor1.dimension_to_vertex.values().containsAll(factor2.dimension_to_vertex.values()))
            ret.mark();
        else if(factor2.dimension_to_vertex.values().containsAll(factor1.dimension_to_vertex.values())){
            ret.mark();
        }
        int dima[] = new int[intersection.size()];
        int dimb[] = new int[intersection.size()];
        int i = 0;
        
        ret.vertex_to_dimension.clear();
        ret.dimension_to_vertex.clear();
        ArrayList<Vertex> tmp = new ArrayList(intersection);
        //Collections.sort(tmp);
        for (Vertex v : tmp) {
            dima[i] = factor1.get_dimension_of_vertex(v);
            dimb[i] = factor2.get_dimension_of_vertex(v);
            //ret.vertex_to_dimension.put(v, i + factor1.getTable().get_no_dimensions() + factor2.getTable().get_no_dimensions()-intersection.size());
            ret.vertex_to_dimension.put(v, i);
            ret.dimension_to_vertex.put(i, v);
            i++;
        }
        int k = 0;
        for (int j = 0; j < factor1.getTable().dimensions.length; j++) {
            Vertex v = factor1.get_vertex_at_dimension(j);
            if (!intersection.contains(v)) {
                ret.vertex_to_dimension.put(v, i);
                ret.dimension_to_vertex.put(i, v);
                i++;
            }

        }
        for (int j = 0; j < factor2.getTable().dimensions.length; j++) {
            Vertex v = factor2.get_vertex_at_dimension(j);
            if (!intersection.contains(v)) {
                ret.vertex_to_dimension.put(v, i);
                ret.dimension_to_vertex.put(i, v);
                i++;
            }
        }

        ret.setTable(recurse1(factor1.getTable(), dima, factor2.getTable(), dimb, k));
//        try {
//            //trace.write("\nMultiplying " + factor1.print_table1() + "\n * \n" + factor2.print_table1() + "\nResult " + ret.print_table1() + "\n");
//
//        } catch (IOException ex) {
//            Logger.getLogger(Factor.class
//                    .getName()).log(Level.SEVERE, null, ex);
//        }
        if (factor1.getName() != null && factor2.getName() != null) {
            ret.setName(factor1.getName() + " " + factor2.getName());
        }
        return ret;

    }

    MultiDimensionalMatrix recurse(MultiDimensionalMatrix m, int[] dima, MultiDimensionalMatrix n, int[] dimb, int i) {
        MultiDimensionalMatrix ret = null;
        if (i != dima.length) {
            MultiDimensionalMatrix[] a = m.reduce_dimension_by_n(dima[i]);
            MultiDimensionalMatrix[] b = n.reduce_dimension_by_n(dimb[i]);
            MultiDimensionalMatrix[] res = new MultiDimensionalMatrix[a.length];
            for (int k = 0; k < a.length; k++) {
                res[k] = recurse(a[k], dima, b[k], dimb, i + 1);
                ret = MultiDimensionalMatrix.merge(res);
            }
        } else {
            int[] newdimensions = new int[m.dimensions.length + n.dimensions.length];
            System.arraycopy(m.dimensions, 0, newdimensions, 0, m.dimensions.length);
            System.arraycopy(n.dimensions, 0, newdimensions, m.dimensions.length, n.dimensions.length);
            ret = new MultiDimensionalMatrix(newdimensions);
            ret.prod_put(m, n);
        }

        return ret;
    }

    static MultiDimensionalMatrix recurse1(MultiDimensionalMatrix m, int[] dima, MultiDimensionalMatrix n, int[] dimb, int i) {
        MultiDimensionalMatrix ret = null;
        if (i != dima.length) {
            MultiDimensionalMatrix[] a = m.reduce_dimension_by_n(dima[i]);
            MultiDimensionalMatrix[] b = n.reduce_dimension_by_n(dimb[i]);
            MultiDimensionalMatrix[] res = new MultiDimensionalMatrix[a.length];
            int[] dima1 = new int[dima.length];
            int[] dimb1 = new int[dimb.length];
            System.arraycopy(dima, 0, dima1, 0, dima.length);
            System.arraycopy(dimb, 0, dimb1, 0, dimb.length);
            for (int j = i; j < dima.length; j++) {
                if (dima[j] > dima[i]) {
                    dima1[j]--;
                }
                if (dimb[j] > dimb[i]) {
                    dimb1[j]--;
                }
            }

            for (int k = 0; k < a.length; k++) {
                res[k] = recurse1(a[k], dima1, b[k], dimb1, i + 1);
            }
            ret = MultiDimensionalMatrix.merge(res);
        } else {
            int[] newdimensions = new int[m.dimensions.length + n.dimensions.length];
            System.arraycopy(m.dimensions, 0, newdimensions, 0, m.dimensions.length);
            System.arraycopy(n.dimensions, 0, newdimensions, m.dimensions.length, n.dimensions.length);
            ret = new MultiDimensionalMatrix(newdimensions);
            ret.prod_put(m, n);
        }

        return ret;
    }

    /**
     * @return the table
     */
    public MultiDimensionalMatrix getTable() {
        return table;
    }

    /**
     * @param table the table to set
     */
    public void setTable(MultiDimensionalMatrix table) {
        this.table = table;
    }

    /**
     * @return the vertex_to_dimension
     */
    public HashMap<Vertex, Integer> getVertex_to_dimension() {
        return vertex_to_dimension;
    }

    /**
     * @param vertex_to_dimension the vertex_to_dimension to set
     */
    public void setVertex_to_dimension(HashMap<Vertex, Integer> vertex_to_dimension) {
        this.vertex_to_dimension = vertex_to_dimension;
    }

    /**
     * @return the dimension_to_vertex
     */
    public HashMap<Integer, Vertex> getDimension_to_vertex() {
        return dimension_to_vertex;
    }

    /**
     * @param dimension_to_vertex the dimension_to_vertex to set
     */
    public void setDimension_to_vertex(HashMap<Integer, Vertex> dimension_to_vertex) {
        this.dimension_to_vertex = dimension_to_vertex;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        for (Vertex v : dimension_to_vertex.values()) {
            sb.append(v + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");

        return sb.toString();
    }

    public String print_table() {
        StringBuilder sb = new StringBuilder();
        MDArrayIndex index = new MDArrayIndex(getTable());
        if (table.dimensions.length == 2) {
            for (int i = 0; i < table.dimensions.length; i++) {
                sb.append(get_vertex_at_dimension(i)).append(" ");
            }
            sb.append("\n");
            sb.append("   ");
            for (int j = 0; j < table.dimensions[1]; j++) {
                sb.append(j).append("   ");
            }
            sb.append("\n");
            for (int i = 0; i < table.dimensions[0]; i++) {
                sb.append(i + " ");
                for (int j = 0; j < table.dimensions[1]; j++) {
                    sb.append(getTable().get(index)).append(" ");
                    index.increment();
                }
                sb.append("\n");

            }
        } else {
            for (int i = 0; i < table.dimensions.length; i++) {
                sb.append(get_vertex_at_dimension(i)).append(" ");
            }
            sb.append(" \n");
            do {
                sb.append(getTable().get(index)).append(" ");
            } while (index.increment() != 0);
        }
        return sb.toString();

    }

    @Override
    public int compareTo(Factor o) {
        return o.getDimension_to_vertex().values().size() - getDimension_to_vertex().values().size();
    }

    public boolean check(Factor f2) {

        if (f2.dimension_to_vertex.size() != dimension_to_vertex.size()) {
            return false;
        }
        if (!f2.dimension_to_vertex.values().containsAll(dimension_to_vertex.values())) {
            return false;
        }
        MDArrayIndex i, j;
        i = new MDArrayIndex(getTable());
        j = new MDArrayIndex(f2.getTable());
        int count = 1;
        double prev1=0,prev2=0;
        boolean prev =false;
        HashMap<Vertex, Integer> map = new HashMap();
        for (Vertex v : dimension_to_vertex.values()) {
            map.put(v, 0);
            count *= v.getNo_of_values();
        }

        for (int k = 0; k < count; k++) {
            for (Vertex v : dimension_to_vertex.values()) {
                try {
                    //i.set_index(vertex_to_dimension.get(v), map.get(v));
                    j.set_index(f2.vertex_to_dimension.get(v), map.get(v));
                } catch (NullPointerException n) {
                    return false;
                }
            }
            if (prev&&getTable().get(i).doubleValue() != f2.getTable().get(j).doubleValue()) {
                
                if (Math.abs((prev1/getTable().get(i).doubleValue()) - (prev2/f2.getTable().get(j).doubleValue()))>.0000000001d&&!f2.mark&&!mark)
                    return false;
            } else {
                
            }
            
            prev1=getTable().get(i).doubleValue() ;
            prev2=f2.getTable().get(j).doubleValue();
            prev=true;
            
            
            i.increment();
            for (Vertex v : dimension_to_vertex.values()) {
                Integer put = map.put(v, new Integer(i.get_index(vertex_to_dimension.get(v))));
            }
        }

        return true;
    }

    public String print_table1() {
        StringBuilder sb = new StringBuilder();
        MDArrayIndex index = new MDArrayIndex(getTable());
        if (table.dimensions.length == 2) {
            for (int i = 0; i < table.dimensions.length; i++) {
                sb.append(get_vertex_at_dimension(i)).append(" ");
            }
            sb.append(this.hashCode() + "\n");
            sb.append("   ");
            for (int j = 0; j < table.dimensions[1]; j++) {
                sb.append(j).append("   ");
            }
            sb.append("\n");
            for (int i = 0; i < table.dimensions[0]; i++) {
                sb.append(i + " ");
                for (int j = 0; j < table.dimensions[1]; j++) {
                    sb.append(getTable().get(index)).append(" ");
                    index.increment();
                }
                sb.append("\n");

            }
        } else {
            for (int i = 0; i < table.dimensions.length; i++) {
                sb.append(get_vertex_at_dimension(i)).append(" ");
            }
            sb.append(this.hashCode() + " \n");
            do {
                sb.append(index).append(" ").append(getTable().get(index)).append("\n");
            } while (index.increment() != 0);
        }
        return sb.toString();

    }

    String print_table2() {
        return new String("Pr([" + dimension_to_vertex.values() + "]):" + print_table3());

    }

    private String print_table3() {
        StringBuilder sb = new StringBuilder();
        MDArrayIndex index = new MDArrayIndex(getTable());
        do {
            sb.append(" ").append(getTable().get(index));
        } while (index.increment() != 0);
        return sb.toString();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    private void mark() {
        mark = true;
    }
    
    

}
