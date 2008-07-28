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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
 * @cdk.svnrev  $Revision$
 */
public class NeighborList {
    HashMap<String,List> boxes;
    double boxSize;
    IAtom[] atoms;


    public NeighborList(IAtom[] atoms, double radius) {
        this.atoms = atoms;
        this.boxes = new HashMap<String,List>();
        this.boxSize = 2 * radius;
        for (int i = 0; i < atoms.length; i++) {
            String key = getKeyString(atoms[i]);

            if (this.boxes.containsKey(key)) {
                List arl = this.boxes.get(key);
                arl.add(i);
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
        k1 = (int)(Math.floor(x/ boxSize));
        k2 = (int)(Math.floor(y/ boxSize));
        k3 = (int)(Math.floor(z/ boxSize));

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
        k1 = (int)(Math.floor(x/ boxSize));
        k2 = (int)(Math.floor(y/ boxSize));
        k3 = (int)(Math.floor(z/ boxSize));

        int[] ret = { k1, k2, k3 };
        return(ret);
    }


    public int getNumberOfNeighbors(int i) {
        return getNeighbors(i).length;
    }

    public int[] getNeighbors(int ii) {
        double maxDist2 = this.boxSize *this.boxSize;

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
                            int i2 = (Integer) nbrs.get(l);
                            if (i2 != ii) {
                                IAtom aj = atoms[i2];
                                double x12 = aj.getPoint3d().x - ai.getPoint3d().x;
                                double y12 = aj.getPoint3d().y - ai.getPoint3d().y;
                                double z12 = aj.getPoint3d().z - ai.getPoint3d().z;
                                double d2 = x12*x12 + y12*y12 + z12*z12;
                                if (d2 < maxDist2) nlist.add(i2);
                            }
                        }
                    }
                }
            }
        }
        Object[] tmp = nlist.toArray();
        int[] ret = new int[ tmp.length ];
        for (int j = 0; j < tmp.length; j++) ret[j] = (Integer) tmp[j];
        return(ret);
    }
}


