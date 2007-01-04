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

import java.util.ArrayList;
import java.util.HashMap;

import org.openscience.cdk.interfaces.IAtom;

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
 * @cdk.module extra
 */
public class NeighborList {
    HashMap boxes;
    double box_size;
    IAtom[] atoms;


    public NeighborList(IAtom[] atoms, double radius) {
        this.atoms = atoms;
        this.boxes = new HashMap();
        this.box_size = 2 * radius;
        for (int i = 0; i < atoms.length; i++) {
            String key = getKeyString(atoms[i]);

            if (this.boxes.containsKey(key)) {
                ArrayList arl = (ArrayList)this.boxes.get(key);
                arl.add( new Integer(i) );
                this.boxes.put( key, arl );
            } else {
                this.boxes.put( key, new ArrayList() );
            }
        }
    }

    private String getKeyString(IAtom atom) {
        double x = atom.getPoint3d().x;
        double y = atom.getPoint3d().y;
        double z = atom.getPoint3d().z;

        int k1,k2,k3;
        k1 = (int)(Math.floor(x/box_size));
        k2 = (int)(Math.floor(y/box_size));
        k3 = (int)(Math.floor(z/box_size));

        String key = 
            Integer.toString(k1) + " " + 
            Integer.toString(k2) + " " +
            Integer.toString(k3) + " " ;
        return(key);
    }
    private int[] getKeyArray(IAtom atom) {
        double x = atom.getPoint3d().x;
        double y = atom.getPoint3d().y;
        double z = atom.getPoint3d().z;

        int k1,k2,k3;
        k1 = (int)(Math.floor(x/box_size));
        k2 = (int)(Math.floor(y/box_size));
        k3 = (int)(Math.floor(z/box_size));

        int[] ret = { k1, k2, k3 };
        return(ret);
    }


    public int getNumberOfNeighbors(int i) {
        return getNeighbors(i).length;
    }

    public int[] getNeighbors(int ii) {
        double max_dist_2 = this.box_size*this.box_size;

        IAtom ai = this.atoms[ii];
        int[] key = getKeyArray(ai);
        ArrayList nlist = new ArrayList();

        int[] bval = {-1,0,1};
        for (int i = 0; i < bval.length; i++) {
            int x = bval[i];
            for (int j = 0; j < bval.length; j++) {
                int y = bval[j];
                for (int k = 0; k < bval.length; k++) {
                    int z = bval[k];

                    String keyj = 
                        Integer.toString(key[0]+x) + " " + 
                        Integer.toString(key[1]+y) + " " +
                        Integer.toString(key[2]+z) + " " ;
                    if (boxes.containsKey(keyj)) {
                        ArrayList nbrs = (ArrayList)boxes.get(keyj);
                        for (int l = 0; l < nbrs.size(); l++) {
                            int i2 = ((Integer)nbrs.get(l)).intValue();
                            if (i2 != ii) {
                                IAtom aj = atoms[i2];
                                double x12 = aj.getPoint3d().x - ai.getPoint3d().x;
                                double y12 = aj.getPoint3d().y - ai.getPoint3d().y;
                                double z12 = aj.getPoint3d().z - ai.getPoint3d().z;
                                double d2 = x12*x12 + y12*y12 + z12*z12;
                                if (d2 < max_dist_2) nlist.add( new Integer(i2) );
                            }
                        }
                    }
                }
            }
        }
        Object[] tmp = nlist.toArray();
        int[] ret = new int[ tmp.length ];
        for (int j = 0; j < tmp.length; j++) ret[j] = ((Integer)tmp[j]).intValue();
        return(ret);
    }
}


