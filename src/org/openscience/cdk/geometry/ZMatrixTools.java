/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.geometry;

import java.awt.Dimension;
import java.util.Vector;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.AtomEnumeration;
import org.openscience.cdk.Bond;
import org.openscience.cdk.tools.LoggingTool;

/**
 * A set of static utility classes for dealing with Z matrices.
 *
 * @cdkPackage standard
 *
 * @created 2004-02-09
 */
public class ZMatrixTools {

    private static LoggingTool logger = null;
    
    static {
        if (logger == null) logger = new LoggingTool("org.openscience.cdk.geometry.ZMatrixTools");
    }

    /**
     * Takes the given Z Matrix coordinates and converts them to cartesian coordinates.
     * The first Atom end up in the origin, the second on on the x axis, and the third
     * one in the XY plane. The rest is added by applying the Zmatrix distances, angles
     * and dihedrals.
     *
     * @param distances     Array of distance variables of the Z matrix
     * @param angles        Array of angle variables of the Z matrix
     * @param dihedrals     Array of distance variables of the Z matrix
     * @param first_atoms   Array of atom ids of the first involed atom in distance, angle and dihedral
     * @param second_atoms  Array of atom ids of the second involed atom in angle and dihedral
     * @param third_atoms   Array of atom ids of the third involed atom in dihedral
     */
    public Point3d[] zmatrixToCartesian(double[] distances, int[] first_atoms,
                                        double[] angles,    int[] second_atoms,
                                        double[] dihedrals, int[] third_atoms) {
        Point3d[] cartesianCoords = new Point3d[distances.length];
        for (index=0; index<distances.length; index++) {
            if (index==0) {
                cartesianCoords[index] = new Point3d(0d,0d,0d);
            } else if (index==1) {
                cartesianCoords[index] = new Point3d(distances[1],0d,0d);
            } else if (index==2) {
                pos[index] = new Point3d(-Math.cos((angles[2]/180)*Math.PI)*distances[2]+distances[1],
                                         Math.sin((angles[2]/180)*Math.PI)*distances[2],
                                         0d);
            } else {
                Vector3d cd = new Vector3d();
                cd.sub(cartesianCoords[third_atoms[index]], cartesianCoords[second_atoms[index]]);
                                                                        
                Vector3d bc = new Vector3d();
                bc.sub(cartesianCoords[second_atoms[index]], cartesianCoords[first_atoms[index]]);
                
                Vector3d n1 = new Vector3d();
                n1.cross(cd, bc);
                n1.normalize();
                
                Vector3d n2 = rotate(n1,bc,dihedrals[index]);
                n2.normalize();
                Vector3d ba = rotate(bc,n2,-angles[index]);
                
                ba.normalize();
                
                Vector3d ban = new Vector3d(ba);
                ban.scale(distances[index]);
                
                Point3d result = new Point3d();
                result.add(cartesianCoords[first_atoms[index]], ba);
                cartesianCoords[index] = result;
            }
        }
        return cartesianCoords;
    }
    
}



