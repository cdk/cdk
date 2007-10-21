/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *  */
package org.openscience.cdk.geometry;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A set of static utility classes for dealing with Z matrices.
 *
 * @cdk.module  io
 * @cdk.svnrev  $Revision$
 * @cdk.keyword Z Matrix
 *
 * @cdk.created 2004-02-09
 * @cdk.bug     1653028
 */
public class ZMatrixTools {

    /**
     * Takes the given Z Matrix coordinates and converts them to cartesian coordinates.
     * The first Atom end up in the origin, the second on on the x axis, and the third
     * one in the XY plane. The rest is added by applying the Zmatrix distances, angles
     * and dihedrals. Angles are in degrees.
     *
     * @param distances     Array of distance variables of the Z matrix
     * @param angles        Array of angle variables of the Z matrix
     * @param dihedrals     Array of distance variables of the Z matrix
     * @param first_atoms   Array of atom ids of the first involed atom in distance, angle and dihedral
     * @param second_atoms  Array of atom ids of the second involed atom in angle and dihedral
     * @param third_atoms   Array of atom ids of the third involed atom in dihedral
     *
     * @cdk.dictref blue-obelisk:zmatrixCoordinatesIntoCartesianCoordinates
     */
    public static Point3d[] zmatrixToCartesian(double[] distances, int[] first_atoms,
                                        double[] angles,    int[] second_atoms,
                                        double[] dihedrals, int[] third_atoms) {
        Point3d[] cartesianCoords = new Point3d[distances.length];
        for (int index=0; index<distances.length; index++) {
            if (index==0) {
                cartesianCoords[index] = new Point3d(0d,0d,0d);
            } else if (index==1) {
                cartesianCoords[index] = new Point3d(distances[1],0d,0d);
            } else if (index==2) {
                cartesianCoords[index] = new Point3d(-Math.cos((angles[2]/180)*Math.PI)*distances[2]+distances[1],
                                           Math.sin((angles[2]/180)*Math.PI)*distances[2],
                                           0d);
                if (first_atoms[index] == 0)
                    cartesianCoords[index].x = (cartesianCoords[index].x - distances[1]) * -1;
            } else {
                Vector3d cd = new Vector3d();
                cd.sub(cartesianCoords[third_atoms[index]], cartesianCoords[second_atoms[index]]);
                                                                        
                Vector3d bc = new Vector3d();
                bc.sub(cartesianCoords[second_atoms[index]], cartesianCoords[first_atoms[index]]);
                
                Vector3d n1 = new Vector3d();
                n1.cross(cd, bc);
                
                Vector3d n2 = rotate(n1,bc,-dihedrals[index]);
                Vector3d ba = rotate(bc,n2,-angles[index]);
                
                ba.normalize();
                ba.scale(distances[index]);
                
                Point3d result = new Point3d();
                result.add(cartesianCoords[first_atoms[index]], ba);
                cartesianCoords[index] = result;
            }
        }
        return cartesianCoords;
    }
    
    private static Vector3d rotate(Vector3d vector, Vector3d axis, double angle) {
        Matrix3d rotate = new Matrix3d();
        rotate.set(new AxisAngle4d(axis, Math.toRadians(angle)));
        Vector3d result = new Vector3d();
        rotate.transform(vector, result);
        return result;
    }
    
}



