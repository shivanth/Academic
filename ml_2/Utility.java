/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml_2;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.organic.JGraphFastOrganicLayout;
import com.mxgraph.layout.mxFastOrganicLayout;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphLayoutCache;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.AbstractGraph;

/**
 *
 * @author Shivanth
 */
public class Utility {

    JFrame frame = new JFrame();
    private JGraph jgraph = null;

    public void show_graph(AbstractGraph input) {

        final JGraphFastOrganicLayout graphLayout
                = new JGraphFastOrganicLayout();
        JGraphModelAdapter m_jgAdapter = new JGraphModelAdapter(input);
        frame.getContentPane().removeAll();
        setJgraph(new JGraph(m_jgAdapter));
        getJgraph().setPreferredSize(new Dimension(1200, 600));
        final JGraphFacade graphFacade = new JGraphFacade(getJgraph());

        graphLayout.run(graphFacade);
        final Map nestedMap = graphFacade.createNestedMap(true, true);
        getJgraph().getGraphLayoutCache().edit(nestedMap);
        //frame.removeAll();
        frame.getContentPane().add(new JScrollPane(getJgraph()));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    /**
     * @return the jgraph
     */
    public JGraph getJgraph() {
        return jgraph;
    }

    /**
     * @param jgraph the jgraph to set
     */
    public void setJgraph(JGraph jgraph) {
        this.jgraph = jgraph;
    }

}
