/*
 *  Copyright (C) 2004-2006  Rajarshi Guha <rajarshi@users.sourceforge.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.molecular;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import javax.vecmath.Point3d;


/**
 * Evaluates the Petitjean shape indices,
 * <p/>
 * These original Petitjean number was described by Petitjean ({@cdk.cite PET92})
 * and considered the molecular graph. This class also implements the geometric analog
 * of the topological shape index described by Bath et al ({@cdk.cite BAT95}).
 * <p/>
 * The descriptor returns a <code>DoubleArrayResult</code> which contains the topological
 * shape index and the geometric shape index in that order.
 * 
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td></td>
 *     <td></td>
 *     <td>no parameters</td>
 *   </tr>
 * </table>
 * 
 * @author Rajarshi Guha
 * @cdk.created 2006-01-14
 * @cdk.module qsar
 * @cdk.set qsar-descriptors
 * @cdk.dictref qsar-descriptors:petitjeanShapeIndex
 */
public class PetitjeanShapeIndexDescriptor implements IMolecularDescriptor {

    public PetitjeanShapeIndexDescriptor() {
    }

    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#petitjeanShapeIndex",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }


    /**
     * Sets the parameters attribute of the PetitjeanShapeIndexDescriptor object.
     *
     * @param params The new parameters value
     * @throws org.openscience.cdk.exception.CDKException
     *          Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        // no parameters for this descriptor
    }

    /**
     * Gets the parameters attribute of the PetitjeanShapeIndexDescriptor object.
     *
     * @return The parameters value
     */
    public Object[] getParameters() {
        // no parameters to return
        return (null);
    }

    /**
     * Gets the parameterNames attribute of the PetitjeanShapeIndexDescriptor object.
     *
     * @return The parameterNames value
     */
    public String[] getParameterNames() {
        // no param names to return
        return (null);
    }


    /**
     * Gets the parameterType attribute of the PetitjeanShapeIndexDescriptor object.
     *
     * @param name Description of the Parameter
     * @return The parameterType value
     */
    public Object getParameterType(String name) {
        return (null);
    }

    /**
     * Calculates the two Petitjean shape indices.
     *
     * @param container Parameter is the atom container.
     * @return A DoubleArrayResult value representing the Petitjean shape indices
     */

    public DescriptorValue calculate(IAtomContainer container) throws CDKException {
        IAtomContainer local = AtomContainerManipulator.removeHydrogens(container);

        int tradius = PathTools.getMolecularGraphRadius(local);
        int tdiameter = PathTools.getMolecularGraphDiameter(local);

        DoubleArrayResult retval = new DoubleArrayResult();
        retval.add((double) (tdiameter - tradius) / (double) tradius);

        // get the 3D distance matrix
        if (GeometryTools.has3DCoordinates(container)) {
            int natom = container.getAtomCount();
            double[][] distanceMatrix = new double[natom][natom];
            for (int i = 0; i < natom; i++) {
                for (int j = 0; j < natom; j++) {
                    if (i == j) {
                        distanceMatrix[i][j] = 0.0;
                        continue;
                    }

                    Point3d a = container.getAtom(i).getPoint3d();
                    Point3d b = container.getAtom(j).getPoint3d();
                    distanceMatrix[i][j] = Math.sqrt((a.x - b.x) * (a.x - b.x) +
                            (a.y - b.y) * (a.y - b.y) +
                            (a.z - b.z) * (a.z - b.z));
                }
            }
            double gradius = 999999;
            double gdiameter = -999999;
            double[] geta = new double[natom];
            for (int i = 0; i < natom; i++) {
                double max = -99999;
                for (int j = 0; j < natom; j++) {
                    if (distanceMatrix[i][j] > max) max = distanceMatrix[i][j];
                }
                geta[i] = max;
            }
            for (int i = 0; i < natom; i++) {
                if (geta[i] < gradius) gradius = geta[i];
                if (geta[i] > gdiameter) gdiameter = geta[i];
            }
            retval.add((gdiameter - gradius) / gradius);
        } else {
            throw new CDKException("Structure must have 3D coordinates");
        }

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), retval);
    }
}
    

