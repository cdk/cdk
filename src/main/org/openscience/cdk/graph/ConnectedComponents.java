package org.openscience.cdk.graph;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

import java.util.Arrays;

/**
 * Compute the connected components of an adjacency list.
 *
 * <blockquote><pre>
 *     int[][]             g          = GraphUtil.toAdjList(container(l
 *     ConnectedComponents cc         = new ConnectedComponents(g);
 *     int[]               components = cc.components();
 *     for (int v = 0; v < g.length; v++)
 *         components[v];
 * </pre></blockquote>
 *
 * @author John May
 * @cdk.module core
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.graph.ConnectedComponentsTest")
public final class ConnectedComponents {

    /** Adjacency-list representation of a graph. */
    private final int[][] g;

    /** Stores the component of each vertex. */
    private final int[] component;

    /** The number of components. */
    private int components;

    /** The number remaining vertices. */
    private int remaining;

    /**
     * Compute the connected components of an adjacency list, {@code g}.
     *
     * @param g graph (adjacency list representation)
     */
    public ConnectedComponents(int[][] g) {
        this.g = g;
        this.component = new int[g.length];
        this.remaining = g.length;
        for (int i = 0; remaining > 0 && i < g.length; i++)
            if (component[i] == 0)
                visit(i, ++components);
    }

    /**
     * Visit a vertex and mark it a member of component {@code c}.
     *
     * @param v vertex
     * @param c component
     */
    private void visit(int v, int c) {
        remaining--;
        component[v] = c;
        for (int w : g[v])
            if (component[w] == 0)
                visit(w, c);
    }

    /**
     * Access the components each vertex belongs to.
     *
     * @return component labels
     */
    @TestMethod("connected,disconnected")
    public int[] components() {
        return Arrays.copyOf(component, component.length);
    }
    
    @TestMethod("connected,disconnected")
    public int nComponents() {
        return components;
    }
}
