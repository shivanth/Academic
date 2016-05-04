/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml_2;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Shivanth
 */
public class MDArrayIndex {
    int i;
    private int index_array[];
    int dimensions[];
    private ArrayList<Integer> const_dimension = null;
    private ArrayList<Integer> const_index = null;

    public MDArrayIndex(MultiDimensionalMatrix m) {
        this.dimensions = m.dimensions;
        index_array = new int[dimensions.length];
        for (int i = 0; i < dimensions.length; i++) {
            index_array[i] = 0;
        }
        const_dimension = new ArrayList<>();
        const_index = new ArrayList<>();
    }

    void set_index(int[] a) {
        if (a.length != index_array.length) {
            throw new UnsupportedOperationException("Setting Wrong index manually in MDArray");
        }
        System.arraycopy(a, 0, index_array, 0, a.length);
    }

    void set_index(int d, int v) {
        if (v < dimensions[d]) {
            index_array[d] = v;
        } else {
            throw new UnsupportedOperationException("Setting Wrong index manually in MDArray");
        }
    }

    int get_index(int dim) {
        return index_array[dim];
    }

    public int increment(int dim) {
        if (dim == -1) {
            return 0;
        } //throw new UnsupportedOperationException("Array index is incremented beyond bounds");
        else if (const_dimension.contains(dim)) {
            return increment(dim - 1);
        } else if (getIndex_array()[dim] + 1 >= dimensions[dim]) {
            for (int i = dimensions.length - 1; i >= dim; i--) {
                if (!const_dimension.contains(i)) {
                    getIndex_array()[i] = 0;
                }
            }
            return increment(dim - 1);
        } else {
            getIndex_array()[dim]++;
            return 1;
        }
    }

    public int increment() {
        return increment(dimensions.length - 1);
    }

    /**
     * @return the index_array
     */
    public int[] getIndex_array() {
        return index_array;
    }

    /**
     * @param index_array the index_array to set
     */
    public void setIndex_array(int[] index_array) {
        this.setIndex_array(index_array);
    }

    /**
     * @return the const_dimension
     */
    public int getConst_dimensions(int i) {
        return const_dimension.get(i);
    }
    
    

    /**
     * @param const_dimension the const_dimension to set
     */
    public void setConst_dimension(int const_dimension) {
        if (this.const_dimension.contains(const_dimension)) {
            return;
        }
        this.const_dimension.add(const_dimension);
        const_index.add(this.const_dimension.indexOf(const_dimension));
    }

    /**
     * @return the const_index
     */
    public int getConst_index(int const_dim) {
        return const_index.get(const_dimension.get(const_dim));
    }

    /**
     * @param const_ind
     * @param const_dim
     */
    public void setConst_index(int const_dim, int const_ind) {
        const_index.remove(const_dimension.indexOf(const_dim));
        const_index.add(const_dimension.indexOf(const_dim), const_ind);
        index_array[const_dim] = const_ind;

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < index_array.length; i++) {
            sb.append(index_array[i] + " ");
        }
        return sb.toString();
    }

    public void reset() {
        for (int i = 0; i < dimensions.length; i++) {
            index_array[i] = 0;
        }
    }

}
