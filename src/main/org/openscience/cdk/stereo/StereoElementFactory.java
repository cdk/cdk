/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *  
 * Contact: cdk-devel@lists.sourceforge.net
 *  
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above 
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.stereo;

import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.List;

import static org.openscience.cdk.graph.GraphUtil.EdgeToBondMap;
import static org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation;
import static org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;

/**
 * Create stereo elements for a structure with 2D and 3D coordinates. The
 * factory does not verify whether atoms can or cannot support stereochemistry -
 * for this functionality use {@link Stereocenters}. The factory will not create
 * stereo elements if there is missing information (wedge/hatch bonds, undefined
 * coordinates) or the layout indicates unspecified configuration.
 * 
 * <blockquote><pre>
 * IAtomContainer       container = ...;
 * StereoElementFactory stereo    = StereoElementFactory.using2DCoordinates();
 * 
 * // set the elements replacing any existing elements (recommended)
 * container.setStereoElements(stereo.createAll());
 * 
 * // adding elements individually is no recommended as the AtomContainer
 * // does not check for duplicate or contradicting elements
 * for (IStereoElement element : stereo.createAll())
 *     container.addStereoElement(element); // bad, there may already be elements
 * 
 * </pre></blockquote>
 *
 * @author John May
 * @cdk.module standard
 * @see Stereocenters
 * @cdk.githash
 */
public abstract class StereoElementFactory {

    /** Native CDK structure representation. */
    protected final IAtomContainer container;

    /** Adjacency list graph representation. */
    protected final int[][] graph;

    /** A bond map for fast access to bond labels between two atom indices. */
    protected final EdgeToBondMap bondMap;

    /**
     * Internal constructor.
     * 
     * @param container an atom container
     * @param graph     adjcancey list representation
     * @param bondMap   lookup bonds by atom index
     */
    protected StereoElementFactory(IAtomContainer container, int[][] graph, EdgeToBondMap bondMap) {
        this.container = container;
        this.graph     = graph;
        this.bondMap   = bondMap;
    }

    /**
     * Creates all stereo elements found by {@link Stereocenters} using the or
     * 2D/3D coordinates to specify the configuration (clockwise/anticlockwise).
     * Currently only {@link ITetrahedralChirality} and {@link
     * IDoubleBondStereochemistry} elements are created..
     *
     * @return a list of stereo elements
     */
    public List<IStereoElement> createAll() {

        Stereocenters        centers  = new Stereocenters(container, graph, bondMap);
        List<IStereoElement> elements = new ArrayList<IStereoElement>();

        for (int v = 0; v < graph.length; v++) {
            if (!centers.isStereocenter(v))
                continue;
            switch (centers.elementType(v)) {
                case Tricoordinate:
                    for (int w : graph[v]) {
                        if (w > v && bondMap.get(v, w).getOrder() == IBond.Order.DOUBLE) {
                            if (centers.isStereocenter(w)) {
                                IStereoElement element = createGeometric(v, w);
                                if (element != null) 
                                    elements.add(element);
                            }
                            break;
                        }
                    }
                    break;
                case Tetracoordinate:
                    IStereoElement element = createTetrahedral(v);
                    if (element != null)
                        elements.add(element);
                    break;
            }
        }

        return elements;
    }

    /**
     * Create a tetrahedral element for the atom at index {@code v}. If a
     * tetrahedral element could not be created then null is returned. An
     * element can not be created if, one or more atoms was missing coordinates,
     * the atom has an unspecified (wavy) bond, the atom is no non-planar bonds
     * (i.e. up/down, wedge/hatch). The method does not check if tetrahedral
     * chirality is supported - for this functionality use {@link
     * Stereocenters}.
     *
     * <blockquote><pre>
     * StereoElementFactory  factory   = ...; // 2D/3D
     * IAtomContainer        container = ...; // container
     *
     * for (int v = 0; v < container.getAtomCount(); v++) {
     *     // ... verify v is a stereo atom ...
     *     ITetrahedralChirality element = factory.createTetrahedral(v);
     *     if (element != null)
     *         container.addStereoElement(element);
     * }
     * </pre></blockquote>
     *
     * @param v atom index (vertex)
     * @return a new stereo element
     */
    abstract ITetrahedralChirality createTetrahedral(int v);

    /**
     * Create a tetrahedral element for the atom. If a tetrahedral element could
     * not be created then null is returned. An element can not be created if,
     * one or more atoms was missing coordinates, the atom has an unspecified
     * (wavy) bond, the atom is no non-planar bonds (i.e. up/down, wedge/hatch).
     * The method does not check if tetrahedral chirality is supported - for
     * this functionality use {@link Stereocenters}.
     *
     * <blockquote><pre>
     * StereoElementFactory  factory   = ...; // 2D/3D
     * IAtomContainer        container = ...; // container
     *
     * for (IAtom atom : container.atoms()) {
     *     // ... verify atom is a stereo atom ...
     *     ITetrahedralChirality element = factory.createTetrahedral(atom);
     *     if (element != null)
     *         container.addStereoElement(element);
     * }
     * </pre></blockquote>
     *
     * @param atom atom
     * @return a new stereo element
     */
    abstract ITetrahedralChirality createTetrahedral(IAtom atom);

    /**
     * Create a geometric element (double-bond stereochemistry) for the provided
     * atom indices. If the configuration could not be created a null element is
     * returned. There is no configuration is the coordinates do not indicate a
     * configuration, there were undefined coordinates or an unspecified bond
     * label. The method does not check if double bond stereo is supported - for
     * this functionality use {@link Stereocenters}.
     *
     * @param u an atom index
     * @param v an atom pi bonded 'v'
     * @return a new stereo element
     */
    abstract IDoubleBondStereochemistry createGeometric(int u, int v);

    /**
     * Create a geometric element (double-bond stereochemistry) for the provided
     * double bond. If the configuration could not be created a null element is
     * returned. There is no configuration is the coordinates do not indicate a
     * configuration, there were undefined coordinates or an unspecified bond
     * label. The method does not check if double bond stereo is supported - for
     * this functionality use {@link Stereocenters}.
     *
     * <blockquote><pre>
     * StereoElementFactory  factory   = ...; // 2D/3D
     * IAtomContainer        container = ...; // container
     *
     * for (IBond bond : container.bonds()) {
     *     if (bond.getOrder() != DOUBLE)
     *         continue;
     *     // ... verify bond is a stereo bond...
     *     IDoubleBondStereochemistry element = factory.createGeometric(bond);
     *     if (element != null)
     *         container.addStereoElement(element);
     * }
     * </pre></blockquote>
     *
     * @param bond the bond to create a configuration for
     * @return a new stereo element
     */
    abstract IDoubleBondStereochemistry createGeometric(IBond bond);

    /**
     * Create a stereo element factory for creating stereo elements using 2D
     * coordinates and depiction labels (up/down, wedge/hatch).
     *
     * @param container the structure to create the factory for
     * @return the factory instance
     */
    public static StereoElementFactory using2DCoordinates(IAtomContainer container) {
        EdgeToBondMap bondMap = EdgeToBondMap.withSpaceFor(container);
        int[][] graph = GraphUtil.toAdjList(container, bondMap);
        return new StereoElementFactory2D(container, graph, bondMap);
    }

    /**
     * Create a stereo element factory for creating stereo elements using 3D
     * coordinates and depiction labels (up/down, wedge/hatch).
     *
     * @param container the structure to create the factory for
     * @return the factory instance
     */
    public static StereoElementFactory using3DCoordinates(IAtomContainer container) {
        EdgeToBondMap bondMap = EdgeToBondMap.withSpaceFor(container);
        int[][] graph = GraphUtil.toAdjList(container, bondMap);
        return new StereoElementFactory3D(container, graph, bondMap);
    }
    
    private static boolean hasUnspecifiedParity(IAtom atom) {
        return atom.getStereoParity() != null && atom.getStereoParity() == 3;
    }

    /** Create stereo-elements from 2D coordinates. */
    static final class StereoElementFactory2D extends StereoElementFactory {

        /**
         * Threshold at which the determinant is considered too small (unspeicifed
         * by coordinates).
         */
        private static final double THRESHOLD = 0.1;

        /**
         * Create a new stereo-element factory for the specified structure.
         *
         * @param container native CDK structure representation
         * @param graph     adjacency list representation
         * @param bondMap   fast bond lookup from atom indices
         */
        StereoElementFactory2D(IAtomContainer container,
                               int[][] graph,
                               EdgeToBondMap bondMap) {
            super(container, graph, bondMap);
        }

        /** @inheritDoc */
        @Override ITetrahedralChirality createTetrahedral(IAtom atom) {
            return createTetrahedral(container.getAtomNumber(atom));
        }

        /** @inheritDoc */
        @Override IDoubleBondStereochemistry createGeometric(IBond bond) {
            return createGeometric(container.getAtomNumber(bond.getAtom(0)),
                                   container.getAtomNumber(bond.getAtom(1)));
        }

        /** @inheritDoc */
        @Override ITetrahedralChirality createTetrahedral(int v) {

            IAtom focus = container.getAtom(v);
            
            if (hasUnspecifiedParity(focus))
                return null;

            IAtom[] neighbors = new IAtom[4];
            int[] elevation = new int[4];

            neighbors[3] = focus;

            boolean nonplanar = false;
            int n = 0;

            for (int w : graph[v]) {
                IBond bond = bondMap.get(v, w);

                // wavy bond
                if (isUnspecified(bond))
                    return null;

                neighbors[n] = container.getAtom(w);
                elevation[n] = elevationOf(focus, bond);

                if (elevation[n] != 0)
                    nonplanar = true;

                n++;
            }

            // too few/many neighbors
            if (n < 3 || n > 4)
                return null;

            // TODO: verify valid wedge/hatch configurations using similar procedure
            // to NonPlanarBonds in the cdk-sdg package

            // no up/down bonds present?
            if (!nonplanar)
                return null;

            int parity = parity(neighbors, elevation);

            if (parity == 0)
                return null;

            Stereo winding = parity > 0 ? Stereo.ANTI_CLOCKWISE
                                        : Stereo.CLOCKWISE;

            return new TetrahedralChirality(focus, neighbors, winding);
        }

        /** @inheritDoc */
        @Override IDoubleBondStereochemistry createGeometric(int u, int v) {

            if (hasUnspecifiedParity(container.getAtom(u)) 
                    || hasUnspecifiedParity(container.getAtom(v)))
                return null;
            
            int[] us = graph[u];
            int[] vs = graph[v];

            if (us.length < 2 || us.length > 3 || vs.length < 2 || vs.length > 3)
                return null;

            // move pi bonded neighbors to back
            moveToBack(us, v);
            moveToBack(vs, u);

            IAtom[] vAtoms = new IAtom[]{
                    container.getAtom(us[0]),
                    container.getAtom(us.length > 2 ? us[1] : u),
                    container.getAtom(v)
            };
            IAtom[] wAtoms = new IAtom[]{
                    container.getAtom(vs[0]),
                    container.getAtom(vs.length > 2 ? vs[1] : v),
                    container.getAtom(u)
            };

            // are any substituents a wavy unspecified bond
            if (isUnspecified(bondMap.get(u, us[0]))
                    || isUnspecified(bondMap.get(u, us[1]))
                    || isUnspecified(bondMap.get(v, vs[0]))
                    || isUnspecified(bondMap.get(v, vs[1])))
                return null;

            int parity = parity(vAtoms) * parity(wAtoms);
            Conformation conformation = parity > 0 ? Conformation.OPPOSITE
                                                   : Conformation.TOGETHER;

            if (parity == 0)
                return null;

            IBond bond = bondMap.get(u, v);

            // crossed bond
            if (isUnspecified(bond))
                return null;

            // put the bond in to v is the first neighbor
            bond.setAtoms(new IAtom[]{container.getAtom(u),
                                      container.getAtom(v)});
            
            return new DoubleBondStereochemistry(bond,
                                                 new IBond[]{bondMap.get(u, us[0]),
                                                             bondMap.get(v, vs[0])
                                                 },
                                                 conformation);
        }

        /**
         * Is the provided bond have an unspecified stereo label.
         *
         * @param bond a bond
         * @return the bond has unspecified stereochemistry
         */
        private boolean isUnspecified(IBond bond) {
            switch (bond.getStereo()) {
                case UP_OR_DOWN:
                case UP_OR_DOWN_INVERTED:
                case E_OR_Z:
                    return true;
                default:
                    return false;
            }
        }

        /**
         * Parity computation for one side of a double bond in a geometric center.
         *
         * @param atoms atoms around the double bonded atom, 0: substituent, 1:
         *              other substituent (or focus), 2: double bonded atom
         * @return the parity of the atoms
         */
        private int parity(IAtom[] atoms) {

            if (atoms.length != 3)
                throw new IllegalArgumentException("incorrect number of atoms");

            Point2d a = atoms[0].getPoint2d();
            Point2d b = atoms[1].getPoint2d();
            Point2d c = atoms[2].getPoint2d();

            if (a == null || b == null || c == null)
                return 0;

            double det = det(a.x, a.y, b.x, b.y, c.x, c.y);

            // unspecified by coordinates
            if (Math.abs(det) < THRESHOLD)
                return 0;

            return (int) Math.signum(det);
        }

        /**
         * Parity computation for 2D tetrahedral stereocenters.
         *
         * @param atoms      the atoms surrounding the central focus atom
         * @param elevations the elevations of each atom
         * @return the parity (winding)
         */
        private int parity(IAtom[] atoms, int[] elevations) {

            if (atoms.length != 4)
                throw new IllegalArgumentException("incorrect number of atoms");

            Point2d[] coordinates = new Point2d[atoms.length];
            for (int i = 0; i < atoms.length; i++) {
                coordinates[i] = atoms[i].getPoint2d();
                if (coordinates[i] == null)
                    return 0;
            }

            double x1 = coordinates[0].x;
            double x2 = coordinates[1].x;
            double x3 = coordinates[2].x;
            double x4 = coordinates[3].x;

            double y1 = coordinates[0].y;
            double y2 = coordinates[1].y;
            double y3 = coordinates[2].y;
            double y4 = coordinates[3].y;

            double det = (elevations[0] * det(x2, y2, x3, y3, x4, y4)) -
                    (elevations[1] * det(x1, y1, x3, y3, x4, y4)) +
                    (elevations[2] * det(x1, y1, x2, y2, x4, y4)) -
                    (elevations[3] * det(x1, y1, x2, y2, x3, y3));

            return (int) Math.signum(det);
        }

        /** 3x3 determinant helper for a constant third column */
        private static double det(double xa, double ya, double xb, double yb, double xc, double yc) {
            return (xa - xc) * (yb - yc) - (ya - yc) * (xb - xc);
        }

        /**
         * Utility find the specified value, {@code v}, in the array of values,
         * {@code vs} and moves it to the back.
         *
         * @param vs an array of values (containing v)
         * @param v  a value
         */
        private static void moveToBack(int[] vs, int v) {
            for (int i = 0; i < vs.length; i++) {
                if (vs[i] == v) {
                    System.arraycopy(vs, i + 1, vs, i + 1 - 1, vs.length - (i + 1));
                    vs[vs.length - 1] = v;
                    return;
                }
            }
        }

        /**
         * Obtain the elevation of an atom connected to the {@code focus} by the
         * specified {@code bond}.
         *
         * @param focus a focus of stereochemistry
         * @param bond  a bond connecting the focus to a substituent
         * @return the elevation of the connected atom, +1 above, -1 below, 0
         *         planar
         */
        private int elevationOf(IAtom focus, IBond bond) {
            switch (bond.getStereo()) {
                case UP:
                    return bond.getAtom(0) == focus ? +1 : 0;
                case UP_INVERTED:
                    return bond.getAtom(1) == focus ? +1 : 0;
                case DOWN:
                    return bond.getAtom(0) == focus ? -1 : 0;
                case DOWN_INVERTED:
                    return bond.getAtom(1) == focus ? -1 : 0;
            }
            return 0;
        }
    }

    /** Create stereo-elements from 3D coordinates. */
    private static final class StereoElementFactory3D extends StereoElementFactory {

        /**
         * Create a new stereo-element factory for the specified structure.
         *
         * @param container native CDK structure representation
         * @param graph     adjacency list representation
         * @param bondMap   fast bond lookup from atom indices
         */
        StereoElementFactory3D(IAtomContainer container,
                               int[][] graph,
                               EdgeToBondMap bondMap) {
            super(container, graph, bondMap);
        }

        /** @inheritDoc */
        @Override ITetrahedralChirality createTetrahedral(IAtom atom) {
            return createTetrahedral(container.getAtomNumber(atom));
        }

        /** @inheritDoc */
        @Override IDoubleBondStereochemistry createGeometric(IBond bond) {
            return createGeometric(container.getAtomNumber(bond.getAtom(0)),
                                   container.getAtomNumber(bond.getAtom(1)));
        }

        /** @inheritDoc */
        @Override ITetrahedralChirality createTetrahedral(int v) {

            IAtom focus = container.getAtom(v);
            
            if (hasUnspecifiedParity(focus))
                return null;
            
            IAtom[] neighbors = new IAtom[4];

            neighbors[3] = focus;

            int n = 0;

            for (int w : graph[v])
                neighbors[n++] = container.getAtom(w);

            // too few/many neighbors
            if (n < 3 || n > 4)
                return null;

            // TODO: verify valid wedge/hatch configurations using similar procedure
            // to NonPlanarBonds in the cdk-sdg package

            int parity = parity(neighbors);

            Stereo winding = parity > 0 ? Stereo.ANTI_CLOCKWISE
                                        : Stereo.CLOCKWISE;

            return new TetrahedralChirality(focus, neighbors, winding);
        }

        /** @inheritDoc */
        @Override IDoubleBondStereochemistry createGeometric(int u, int v) {

            if (hasUnspecifiedParity(container.getAtom(u))
                    || hasUnspecifiedParity(container.getAtom(v)))
                return null;
            
            int[] us = graph[u];
            int[] vs = graph[v];

            int x = us[0] == v ? us[1] : us[0];
            int w = vs[0] == u ? vs[1] : vs[0];

            IAtom uAtom = container.getAtom(u);
            IAtom vAtom = container.getAtom(v);
            IAtom uSubstituentAtom = container.getAtom(x);
            IAtom vSubstituentAtom = container.getAtom(w);

            if (uAtom.getPoint3d() == null
                    || vAtom.getPoint3d() == null
                    || uSubstituentAtom.getPoint3d() == null
                    || vSubstituentAtom.getPoint3d() == null)
                return null;

            int parity = parity(uAtom.getPoint3d(),
                                vAtom.getPoint3d(),
                                uSubstituentAtom.getPoint3d(),
                                vSubstituentAtom.getPoint3d());

            Conformation conformation = parity > 0 ? Conformation.OPPOSITE
                                                   : Conformation.TOGETHER;

            IBond bond = bondMap.get(u, v);
            bond.setAtoms(new IAtom[]{uAtom, vAtom});

            return new DoubleBondStereochemistry(bond,
                                                 new IBond[]{
                                                         bondMap.get(u, x),
                                                         bondMap.get(v, w),
                                                 },
                                                 conformation);
        }

        /** 3x3 determinant helper for a constant third column */
        private static double det(double xa, double ya, double xb, double yb, double xc, double yc) {
            return (xa - xc) * (yb - yc) - (ya - yc) * (xb - xc);
        }

        /**
         * Parity computation for one side of a double bond in a geometric center.
         * The method needs the 3D coordinates of the double bond atoms (first 2
         * arguments) and the coordinates of two substituents (one at each end).
         *
         * @param u an atom double bonded to v
         * @param v an atom double bonded to u
         * @param x an atom sigma bonded to u
         * @param w an atom sigma bonded to v
         * @return the parity of the atoms
         */
        private int parity(Point3d u, Point3d v, Point3d x, Point3d w) {

            // create three vectors, v->u, v->w and u->x
            double[] vu = toVector(v, u);
            double[] vw = toVector(v, w);
            double[] ux = toVector(u, x);

            // normal vector (to compare against), the normal vector (n) looks like:
            // x     n w
            //  \    |/
            //   u = v
            double[] normal = crossProduct(vu, crossProduct(vu, vw));

            // compare the dot products of v->w and u->x, if the signs are the same
            // they are both pointing the same direction. if a value is close to 0
            // then it is at pi/2 radians (i.e. unspecified) however 3D coordinates
            // are generally discrete and do not normally represent on unspecified
            // stereo configurations so we don't check this
            int parity = (int) Math.signum(dot(normal, vw))
                    * (int) Math.signum(dot(normal, ux));

            // invert sign, this then matches with Sp2 double bond parity
            return parity * -1;
        }

        /**
         * Parity computation for 3D tetrahedral stereocenters.
         *
         * @param atoms the atoms surrounding the central focus atom
         * @return the parity (winding)
         */
        private int parity(IAtom[] atoms) {

            if (atoms.length != 4)
                throw new IllegalArgumentException("incorrect number of atoms");

            Point3d[] coordinates = new Point3d[atoms.length];
            for (int i = 0; i < atoms.length; i++) {
                coordinates[i] = atoms[i].getPoint3d();
                if (coordinates[i] == null)
                    return 0;
            }

            double x1 = coordinates[0].x;
            double x2 = coordinates[1].x;
            double x3 = coordinates[2].x;
            double x4 = coordinates[3].x;

            double y1 = coordinates[0].y;
            double y2 = coordinates[1].y;
            double y3 = coordinates[2].y;
            double y4 = coordinates[3].y;

            double z1 = coordinates[0].z;
            double z2 = coordinates[1].z;
            double z3 = coordinates[2].z;
            double z4 = coordinates[3].z;

            double det = (z1 * det(x2, y2, x3, y3, x4, y4)) -
                    (z2 * det(x1, y1, x3, y3, x4, y4)) +
                    (z3 * det(x1, y1, x2, y2, x4, y4)) -
                    (z4 * det(x1, y1, x2, y2, x3, y3));

            return (int) Math.signum(det);
        }

        /**
         * Create a vector by specifying the source and destination coordinates.
         *
         * @param src  start point of the vector
         * @param dest end point of the vector
         * @return a new vector
         */
        private static double[] toVector(Point3d src, Point3d dest) {
            return new double[]{dest.x - src.x,
                                dest.y - src.y,
                                dest.z - src.z};
        }

        /**
         * Dot product of two 3D coordinates
         *
         * @param u either 3D coordinates
         * @param v other 3D coordinates
         * @return the dot-product
         */
        private static double dot(double[] u, double[] v) {
            return (u[0] * v[0]) + (u[1] * v[1]) + (u[2] * v[2]);
        }

        /**
         * Cross product of two 3D coordinates
         *
         * @param u either 3D coordinates
         * @param v other 3D coordinates
         * @return the cross-product
         */
        private static double[] crossProduct(double[] u, double[] v) {
            return new double[]{(u[1] * v[2]) - (v[1] * u[2]),
                                (u[2] * v[0]) - (v[2] * u[0]),
                                (u[0] * v[1]) - (v[0] * u[1])};
        }
    }
}
