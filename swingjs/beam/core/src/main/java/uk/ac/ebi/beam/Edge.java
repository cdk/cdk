/*
 * Copyright (c) 2013, European Bioinformatics Institute (EMBL-EBI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */

package uk.ac.ebi.beam;

/**
 * An edge defines two vertex end points and an associated {@link Bond} label.
 * Edges are created from their {@link Bond} label as follows.
 *
 * <blockquote>
 * <pre>
 * // an edge between the vertices 1 and 2 the bond label is implicit
 * Edge e = Bond.IMPLICIT.edge(1, 2);
 *
 * // an edge between the vertices 5 and 3 the bond label is double
 * Edge e = Bond.DOUBLE.edge(1, 2);
 * </pre>
 * </blockquote>
 *
 * @author John May
 * @see Bond
 */
public final class Edge {

    /** Endpoints of the edge. */
    private final int u, v, xor;

    /** Label on the edge. */
    private Bond bond;

    Edge(final int u, final int v, final Bond bond) {
        this.u    = u;
        this.v    = v;
        this.xor  = u ^ v; 
        this.bond = bond;
    }

    Edge(Edge e) {
        this(e.u, e.v, e.bond);
    }

    /**
     * Access either endpoint of the edge. For directional bonds, the endpoint
     * can be considered as relative to this vertex.
     *
     * @return either endpoint
     */
    public int either() {
        return u;
    }

    /**
     * Given one endpoint, access the other endpoint of the edge.
     *
     * @param x an endpoint of the edge
     * @return the other endpoint
     * @throws IllegalArgumentException {@code x} was not an endpoint of this
     *                                  edge
     */
    public int other(final int x) {
        return x ^ xor;
    }

    /**
     * Access the bond label without considering which endpoint the label is
     * relative to. For the directional bonds {@link Bond#UP} and {@link
     * Bond#DOWN} the {@link #bond(int)} should be used to provided the label
     * relative to rhe provided endpoint.
     *
     * @return bond label
     * @see #bond(int)
     */
    public Bond bond() {
        return bond;
    }

    /**
     * Set the bond label.
     * 
     * @param bond the bond label
     */
    void bond(Bond bond) {
        this.bond = bond;     
    }

    /**
     * Access the bond label relative to a specified endpoint.
     *
     * <blockquote><pre>
     * Edge e = Bond.UP.edge(2, 3);
     * e.bond(2); // UP
     * e.bond(3); // DOWN
     * </pre></blockquote>
     *
     * @param x endpoint to which the label is relative to
     * @return the bond label
     */
    public Bond bond(final int x) {
        if (x == u) return bond;
        if (x == v) return bond.inverse();
        throw new IllegalArgumentException(invalidEndpointMessage(x));
    }


    /**
     * Inverse of the edge label but keep the vertices the same.
     *
     * @return inverse edge
     */
    Edge inverse() {
        return bond.inverse().edge(u, v);
    }

    /** Helper method to print error message. */
    private String invalidEndpointMessage(final int x) {
        return "Vertex " + x + ", is not an endpoint of the edge " + toString();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return xor;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final Edge o = (Edge) other;
        return (u == o.u && v == o.v && bond.equals(o.bond)) ||
                (u == o.v && v == o.u && bond.equals(o.bond.inverse()));
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return new StringBuilder(20).append('{')
                                    .append(u)
                                    .append(", ")
                                    .append(v)
                                    .append('}')
                                    .append(": '")
                                    .append(bond)
                                    .append("'")
                                    .toString();
    }
}
