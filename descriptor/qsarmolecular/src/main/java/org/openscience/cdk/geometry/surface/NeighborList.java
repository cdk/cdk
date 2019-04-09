/*
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.geometry.surface;

import org.openscience.cdk.interfaces.IAtom;

import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Creates a list of atoms neighboring each atom in the molecule.
 *
 * <p>The routine is a simplified version of the neighbor list described
 * in {@cdk.cite EIS95} and is based on the implementation by Peter McCluskey.
 * Due to the fact that it divides the cube into a fixed number of sub cubes,
 * some accuracy may be lost.
 *
 * @author Rajarshi Guha
 * @cdk.created 2005-05-09
 * @cdk.module  qsarmolecular
 * @cdk.githash
 */
public class NeighborList {

    Map<Key, List<Integer>> boxes;
    double                  boxSize;
    IAtom[]                 atoms;

    /**
     * Custom key class for looking up items in the map.
     */
    private final class Key {
        private final int x, y, z;

        public Key(IAtom atom) {
            double x = atom.getPoint3d().x;
            double y = atom.getPoint3d().y;
            double z = atom.getPoint3d().z;
            this.x = (int) (Math.floor(x / boxSize));
            this.y = (int) (Math.floor(y / boxSize));
            this.z = (int) (Math.floor(z / boxSize));
        }

        public Key(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return x == key.x &&
                   y == key.y &&
                   z == key.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }
    }

    public NeighborList(IAtom[] atoms, double radius) {
        this.atoms = atoms;
        this.boxes = new HashMap<>();
        this.boxSize = 2 * radius;
        for (int i = 0; i < atoms.length; i++) {
            Key key = new Key(atoms[i]);
            List<Integer> arl = this.boxes.get(key);
            if (arl == null)
                this.boxes.put(key, arl = new ArrayList<>());
            arl.add(i);
        }
    }

    public int getNumberOfNeighbors(int i) {
        return getNeighbors(i).length;
    }

    /**
     * Get the neighbors that are with the given radius of atom i.
     * @param i atom index
     * @return atom indexs within that radius
     */
    public int[] getNeighbors(int i) {
        List<Integer> result   = new ArrayList<>();
        double        maxDist2 = this.boxSize * this.boxSize;
        IAtom         atom     = this.atoms[i];
        Key           key      = new Key(atom);
        int[]         bval     = {-1, 0, 1};
        for (int x : bval) {
            for (int y : bval) {
                for (int z : bval) {
                    Key probe = new Key(key.x+x, key.y+y, key.z+z);
                    List<Integer> nbrs = boxes.get(probe);
                    if (nbrs != null) {
                        for (Integer nbr : nbrs) {
                            if (nbr != i) {
                                IAtom   anbr = atoms[nbr];
                                Point3d p1 = anbr.getPoint3d();
                                Point3d p2 = atom.getPoint3d();
                                if (p1.distanceSquared(p2) < maxDist2)
                                    result.add(nbr);
                            }
                        }
                    }
                }
            }
        }

        // convert to primitive array
        int[] ret = new int[result.size()];
        for (int j = 0; j < ret.length; j++)
            ret[j] = result.get(j);
        return (ret);
    }
}
