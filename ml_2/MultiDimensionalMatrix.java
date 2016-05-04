/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml_2;

import javax.naming.OperationNotSupportedException;

/**
 *
 * @author Shivanth
 */
public class MultiDimensionalMatrix {

    int dimensions[];
    int factors[];
    Double[] data;

    public MultiDimensionalMatrix(int[] dimensions) {
        this.dimensions=new int[dimensions.length];
         System.arraycopy(dimensions, 0,this.dimensions,0, dimensions.length);
        //this.dimensions = dimensions;
        factors = new int[dimensions.length];
        int prod = 1;
        for (int i = factors.length - 1; i >= 0; i--) {
            factors[i] = prod;
            prod = dimensions[i] * prod;
        }

        data = new Double[prod];
        for (int i = 0; i <= prod - 1; i++) {
            data[i] = 0.0;
        }

    }

    public MultiDimensionalMatrix(MultiDimensionalMatrix m) {
        this.dimensions = m.dimensions.clone();
        factors = new int[dimensions.length];
        int prod = 1;
        for (int i = factors.length - 1; i >= 0; i--) {
            factors[i] = prod;
            prod = dimensions[i] * prod;
        }
        data = new Double[prod];
        for (int i = 0; i < prod; i++) {
            data[i] = new Double(m.data[i]);
        }
    }

    public int get_no_elemensts() {
        return data.length;
    }

    protected int getOffset(int[] index) {
        if (index.length != dimensions.length) {
            throw new IllegalArgumentException(
                    "MultiDimensionalMatrix.getOffset:Supplied Indexes does not match the dimension of array");
        }

//        if (index.length == 0) {
//            return -1;
//        }
        int index_into_array = 0;
        for (int i = 0; i < dimensions.length; i++) {
            if (index[i] < 0 || index[i] > dimensions[i]) {
                throw new IndexOutOfBoundsException();
            }
            index_into_array += factors[i] * index[i];
        }
        return index_into_array;
    }

    public Double get(int[] index) {
        return data[getOffset(index)];
    }

    public void put(int[] index, Double object) {
        data[getOffset(index)] = object;
    }

    public Double get(MDArrayIndex index) {
        int ind = getOffset(index.getIndex_array());
//        if (ind >= 0) {
        return data[getOffset(index.getIndex_array())];
//        }
//        else 
//            return null;
    }

    public void put(MDArrayIndex index, Double object) {
        int ind = getOffset(index.getIndex_array());
//        if (ind >= 0) {
        data[ind] = object;
//        }
//        else 
//            throw new ArrayIndexOutOfBoundsException("Passing null value as index");
           
        }
        
    

    public int get_no_dimensions() {
        return dimensions.length;
    }

    public MultiDimensionalMatrix reduce_dimension_by1(int dim, int val) {
        MDArrayIndex a = new MDArrayIndex(this);
        a.setConst_dimension(dim);
        a.setConst_index(dim, val);
        int prod = 1;
        if (val >= dimensions[dim]) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        for (int i = 0; i < dimensions.length; i++) {
            prod *= dimensions[i];
        }
        prod /= dimensions[dim];
        int k = dimensions.length;
        int[] newdimension = new int[dimensions.length - 1];
        for (int i = 0, j = 0; i < dimensions.length; i++) {
            if (i != dim) {
                newdimension[j] = dimensions[i];
                j++;
            }

        }
        MultiDimensionalMatrix ret = new MultiDimensionalMatrix(newdimension);
        MDArrayIndex b = new MDArrayIndex(ret);
        ret.put(b, new Double(get(a)));
        for (int i = 0; i < prod - 1; i++) {
            a.increment(dimensions.length - 1);
            b.increment(newdimension.length - 1);
            ret.put(b, new Double(get(a)));
        }
        if (a.increment() != 0 || b.increment() != 0) {
            System.out.println("Error");
        }
        return ret;
    }

    public MultiDimensionalMatrix[] reduce_dimension_by_n(int dim) {

        MultiDimensionalMatrix ret[] = new MultiDimensionalMatrix[dimensions[dim]];
        for (int i = 0; i < dimensions[dim]; i++) {
            ret[i] = reduce_dimension_by1(dim, i);

        }
        return ret;

    }

    void add(MultiDimensionalMatrix a, MultiDimensionalMatrix b) {
        if (a.get_no_dimensions() != b.get_no_dimensions() || a.get_no_dimensions() != get_no_dimensions()) {
            throw new UnsupportedOperationException("Adding matrices of different no of dimenions");
        }
        MDArrayIndex a_i = new MDArrayIndex(a);
        put(a_i, ((Double) a.get(a_i)).doubleValue() + ((Double) b.get(a_i)).doubleValue());
        for (int i = 0; i < get_no_elemensts() - 1; i++) {
            a_i.increment();
            put(a_i, ((Double) a.get(a_i)).doubleValue() + ((Double) b.get(a_i)).doubleValue());
        }
    }

    void add_put(MultiDimensionalMatrix a) {
        if (a.get_no_dimensions() != get_no_dimensions()) {
            throw new UnsupportedOperationException("Adding matrices of different no of dimenions");
        }
        MDArrayIndex a_i = new MDArrayIndex(a);
        try {

            put(a_i, new Double(get(a_i)) + new Double(a.get(a_i)));
            for (int i = 0; i < get_no_elemensts() - 1; i++) {
                a_i.increment();
                put(a_i, new Double(get(a_i)) + new Double(a.get(a_i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    void prod_put(MultiDimensionalMatrix a, MultiDimensionalMatrix b) {
        int total_dim = a.get_no_dimensions() + b.get_no_dimensions();
        if (get_no_dimensions() != a.get_no_dimensions() + b.get_no_dimensions()) {
            throw new UnsupportedOperationException("Product Matrix is not of proper size");
        }
        int k = 0;
        for (int i = 0; i < a.dimensions.length; i++) {
            if (a.dimensions[i] != dimensions[k]) {
                throw new UnsupportedOperationException("Product Matrix is not of proper dimension");
            }
            k++;
        }
        for (int i = 0; i < b.dimensions.length; i++) {
            if (b.dimensions[i] != dimensions[k]) {
                throw new UnsupportedOperationException("Product Matrix is not of proper dimension");
            }
            k++;
        }
        MDArrayIndex a_i = new MDArrayIndex(a);
        MDArrayIndex b_i = new MDArrayIndex(b);

        MDArrayIndex self = new MDArrayIndex(this);
        a_i.reset();;
        do {
            b_i.reset();

            do {
                Double a_d = a.get(a_i);
                Double b_d = b.get(b_i);
                put(self, a.get(a_i).doubleValue() * b.get(b_i).doubleValue());
                self.increment();
            } while (b_i.increment() != 0);
        } while (a_i.increment() != 0);

    }

    static MultiDimensionalMatrix merge(MultiDimensionalMatrix[] m) {
        int dims = m[0].get_no_dimensions();
        try {
            for (int i = 0; i < m.length; i++) {
                if (m[i].get_no_dimensions() != dims) {
                    throw new UnsupportedOperationException();
                }
            }
            for (int j = 0; j < m[0].dimensions.length; j++) {
                int dim = m[0].dimensions[j];
                for (int i = 0; i < m.length; i++) {
                    if (m[i].dimensions[j] != dim) {
                        throw new UnsupportedOperationException();
                    }
                }
            }

        } catch (Exception e) {
            throw new UnsupportedOperationException();
        }
        int[] newdimension = new int[m[0].get_no_dimensions() + 1];
        newdimension[0] = m.length;
        System.arraycopy(m[0].dimensions, 0, newdimension, 1, newdimension.length - 1);
        MultiDimensionalMatrix res = new MultiDimensionalMatrix(newdimension);
        MDArrayIndex res_i = new MDArrayIndex(res);
        MDArrayIndex old_i = new MDArrayIndex(m[0]);
        res_i.setConst_dimension(0);
        int ret = 1;
        for (int i = 0; i < m.length; i++) {
            old_i.reset();
            res_i.setConst_index(0, i);
            do {
                res.put(res_i, new Double(m[i].get(old_i)));
                ret = res_i.increment();
            } while (old_i.increment() != 0);
        }
        if (ret != 0) {
            throw new UnsupportedOperationException("Error Multiplying");
        }
        return res;
    }

}
