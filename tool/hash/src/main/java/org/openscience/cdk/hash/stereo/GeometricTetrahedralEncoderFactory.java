/*
 * Copyright (c) 2013 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.hash.stereo;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A stereo encoder factory for tetrahedral centres. This factory generates
 * {@link StereoEncoder}s for centres with specified by 2D and 3D coordinates.
 * The required preconditions are the central atom must have 3/4 neighboring
 * atoms, Sp3 hybridization and no query bonds (e.g. wiggly). If there is at
 * least one up/down bond and all required atoms have coordinates a new 2D
 * encoder is created. If the there are no stereo bonds (up/down) and all
 * required atoms have 3D coordinates then a new 3D encoder is created.
 *
 * @author John May
 * @cdk.module hash
 * @cdk.githash
 */
public class GeometricTetrahedralEncoderFactory implements StereoEncoderFactory {

    /**
     * Create a stereo encoder for all potential 2D and 3D tetrahedral
     * elements.
     *
     * @param container an atom container
     * @param graph     adjacency list representation of the container
     * @return a new encoder for tetrahedral elements
     */
    @Override
    public StereoEncoder create(IAtomContainer container, int[][] graph) {

        // XXX: this code isn't pretty, the current IAtomContainer
        // implementations are  slow for the queries (i.e. looking at connected
        // atoms/bonds) we need to ask to decide if something is a potential
        // tetrahedral centre. We can help out a little with the adjacency list
        // (int[][]) but this doesn't help with the bonds.

        int n = container.getAtomCount();

        List<StereoEncoder> encoders = new ArrayList<StereoEncoder>();
        Map<IAtom, Integer> elevation = new HashMap<IAtom, Integer>(10);

        ATOMS: for (int i = 0; i < n; i++) {

            int degree = graph[i].length;

            // ignore those which don't have 3 or 4 neighbors
            if (degree < 3 || degree > 4) continue;

            IAtom atom = container.getAtom(i);

            // only create encoders for SP3 hybridized atom. atom typing is
            // currently wrong for some atoms, in sulfoxide for example the atom
            // type sets SP2... but there we don't to fuss about with that here
            if (!sp3(atom)) continue;

            // avoid nitrogen-inversion
            if (Integer.valueOf(7).equals(atom.getAtomicNumber()) && degree == 3) continue;

            // TODO: we could be more strict with our selection, InChI uses C,
            // Si, Ge, P, As, B, Sn, N, P, S, Se but has preconditions for
            // certain cases. An atom or ion N, P, As, S or Se is not stereogenic
            // if it has a terminal H or two terminal neighbors -XHm, -XHn (n+m>0)
            // where X is O, S, Se, Te, or N

            // XXX: likely bottle neck
            List<IBond> bonds = container.getConnectedBondsList(atom);

            // try to create geometric parity
            GeometricParity geometric = geometric(elevation, bonds, i, graph[i], container);

            if (geometric != null) {
                // add a new encoder if a geometric parity
                encoders.add(new GeometryEncoder(i, new BasicPermutationParity(graph[i]), geometric));
            }
        }

        // no encoders, replace with the empty encoder
        return encoders.isEmpty() ? StereoEncoder.EMPTY : new MultiStereoEncoder(encoders);
    }

    /**
     * Create the geometric part of an encoder
     *
     * @param elevationMap temporary map to store the bond elevations (2D)
     * @param bonds        list of bonds connected to the atom at i
     * @param i            the central atom (index)
     * @param adjacent     adjacent atoms (indices)
     * @param container    container
     * @return geometric parity encoder (or null)
     */
    private static GeometricParity geometric(Map<IAtom, Integer> elevationMap, List<IBond> bonds, int i,
            int[] adjacent, IAtomContainer container) {
        int nStereoBonds = nStereoBonds(bonds);
        if (nStereoBonds > 0)
            return geometric2D(elevationMap, bonds, i, adjacent, container);
        else if (nStereoBonds == 0) return geometric3D(i, adjacent, container);
        return null;
    }

    /**
     * Create the geometric part of an encoder of 2D configurations
     *
     * @param elevationMap temporary map to store the bond elevations (2D)
     * @param bonds        list of bonds connected to the atom at i
     * @param i            the central atom (index)
     * @param adjacent     adjacent atoms (indices)
     * @param container    container
     * @return geometric parity encoder (or null)
     */
    private static GeometricParity geometric2D(Map<IAtom, Integer> elevationMap, List<IBond> bonds, int i,
            int[] adjacent, IAtomContainer container) {

        IAtom atom = container.getAtom(i);

        // create map of the atoms and their elevation from the center,
        makeElevationMap(atom, bonds, elevationMap);

        Point2d[] coordinates = new Point2d[4];
        int[] elevations = new int[4];

        // set the forth ligand to centre as default (overwritten if
        // we have 4 neighbors)
        if (atom.getPoint2d() != null)
            coordinates[3] = atom.getPoint2d();
        else
            return null;

        for (int j = 0; j < adjacent.length; j++) {
            IAtom neighbor = container.getAtom(adjacent[j]);
            elevations[j] = elevationMap.get(neighbor);

            if (neighbor.getPoint2d() != null)
                coordinates[j] = neighbor.getPoint2d();
            else
                return null; // skip to next atom

        }

        return new Tetrahedral2DParity(coordinates, elevations);

    }

    /**
     * Create the geometric part of an encoder of 3D configurations
     *
     * @param i         the central atom (index)
     * @param adjacent  adjacent atoms (indices)
     * @param container container
     * @return geometric parity encoder (or null)
     */
    private static GeometricParity geometric3D(int i, int[] adjacent, IAtomContainer container) {

        IAtom atom = container.getAtom(i);
        Point3d[] coordinates = new Point3d[4];

        // set the forth ligand to centre as default (overwritten if
        // we have 4 neighbors)
        if (atom.getPoint3d() != null)
            coordinates[3] = atom.getPoint3d();
        else
            return null;

        // for each neighboring atom check if we have 3D coordinates
        for (int j = 0; j < adjacent.length; j++) {
            IAtom neighbor = container.getAtom(adjacent[j]);

            if (neighbor.getPoint3d() != null)
                coordinates[j] = neighbor.getPoint3d();
            else
                return null; // skip to next atom
        }

        // add new 3D stereo encoder
        return new Tetrahedral3DParity(coordinates);

    }

    /**
     * check whether the atom is Sp3 hybridization
     *
     * @param atom an atom
     * @return whether the atom is Sp3
     */
    private static boolean sp3(IAtom atom) {
        return IAtomType.Hybridization.SP3.equals(atom.getHybridization());
    }

    /**
     * access the number of stereo bonds in the provided bond list.
     *
     * @param bonds input list
     * @return number of UP/DOWN bonds in the list, -1 if a query bond was
     *         found
     */
    private static int nStereoBonds(List<IBond> bonds) {
        int count = 0;
        for (IBond bond : bonds) {
            IBond.Stereo stereo = bond.getStereo();
            switch (stereo) {
            // query bonds... no configuration possible
                case E_OR_Z:
                case UP_OR_DOWN:
                case UP_OR_DOWN_INVERTED:
                    return -1;
                case UP:
                case DOWN:
                case UP_INVERTED:
                case DOWN_INVERTED:
                    count++;
                    break;
            }
        }
        return count;
    }

    /**
     * Maps the input bonds to a map of Atom->Elevation where the elevation is
     * whether the bond is off the plane with respect to the central atom.
     *
     * @param atom  central atom
     * @param bonds bonds connected to the central atom
     * @param map   map to load with elevation values (can be reused)
     */
    private static void makeElevationMap(IAtom atom, List<IBond> bonds, Map<IAtom, Integer> map) {
        map.clear();
        for (IBond bond : bonds) {

            int elevation = 0;
            switch (bond.getStereo()) {
                case UP:
                case DOWN_INVERTED:
                    elevation = +1;
                    break;
                case DOWN:
                case UP_INVERTED:
                    elevation = -1;
                    break;
            }

            // change elevation depending on which end of the wedge/hatch
            // the atom is on
            if (bond.getBeg().equals(atom)) {
                map.put(bond.getEnd(), elevation);
            } else {
                map.put(bond.getBeg(), -1 * elevation);
            }
        }
    }

}
