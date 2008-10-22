/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2007  Rajarshi Guha <rajarshi@users.sf.net>
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
 */
package org.openscience.cdk.qsar.descriptors.molecular;

import Jama.Matrix;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleArrayResultType;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.PeriodicTable;

import javax.vecmath.Point3d;

/**
 * Evaluates length over breadth descriptors.
 * <p/>
 * The current implementation reproduces the results obtained from the LOVERB descriptor
 * routine in ADAPT. As a result ti does not perform any orientation and only considers the
 * X & Y extents for a series of rotations about the Z axis (in 10 degree increments).
 * <p/>
 * The class gives two descriptors
 * <ul>
 * <li>LOBMAX - The maximum L/B ratio
 * <li>LOBMIN - The L/B ratio for the rotation that results in the minimum area
 * (defined by the product of the X & Y extents for that orientation)
 * </ul>
 * <B>Note:</B> The descriptor assumes that the atoms have been configured.
 *
 * @author      Rajarshi Guha
 * @cdk.created 2006-09-26
 * @cdk.module  qsarmolecular
 * @cdk.svnrev  $Revision$
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:lengthOverBreadth
 * @cdk.bug     1862142
 */
public class LengthOverBreadthDescriptor implements IMolecularDescriptor {
    private LoggingTool logger;

    private static final String[] names = {"LOBMAX", "LOBMIN"};
    /**
     * Constructor for the LengthOverBreadthDescriptor object.
     */
    public LengthOverBreadthDescriptor() {
        logger = new LoggingTool(this);
    }


    /**
     * Gets the specification attribute of the PetitjeanNumberDescriptor object
     *
     * @return The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#lengthOverBreadth",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }


    /**
     * Sets the parameters attribute of the PetitjeanNumberDescriptor object
     *
     * @param params The new parameters value
     * @throws org.openscience.cdk.exception.CDKException
     *          Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        // no parameters for this descriptor
    }


    /**
     * Gets the parameters attribute of the PetitjeanNumberDescriptor object
     *
     * @return The parameters value
     */
    public Object[] getParameters() {
        return (null);
        // no parameters to return
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return names;
    }


    private DescriptorValue getDummyDescriptorValue(Exception e) {
        DoubleArrayResult result = new DoubleArrayResult(2);
        result.add(Double.NaN);
        result.add(Double.NaN);
        return new DescriptorValue(getSpecification(), getParameterNames(),
                getParameters(), result, getDescriptorNames(), e);
    }
    /**
     * Evaluate the descriptor for the molecule.
     *
     * @param atomContainer AtomContainer
     * @return A {@link org.openscience.cdk.qsar.result.DoubleArrayResult} containing LOBMAX and LOBMIN in that
     *         order     
     */
    public DescriptorValue calculate(IAtomContainer atomContainer) {
        if (!GeometryTools.has3DCoordinates(atomContainer))
            return getDummyDescriptorValue(new CDKException("Molecule must have 3D coordinates"));

        double angle = 10.0;
        double maxLOB = 0;
        double minArea = 1e6;
        double mmLOB = 0;

        double lob, bol, area;
        double[] xyzRanges;

        double[][] coords = new double[atomContainer.getAtomCount()][3];
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            coords[i][0] = atomContainer.getAtom(i).getPoint3d().x;
            coords[i][1] = atomContainer.getAtom(i).getPoint3d().y;
            coords[i][2] = atomContainer.getAtom(i).getPoint3d().z;
        }

        // get the com
        Point3d com = GeometryTools.get3DCentreOfMass(atomContainer);
        if (com == null) return getDummyDescriptorValue(new CDKException("Error in center of mass calculation"));

        // translate everything to COM
        for (int i = 0; i < coords.length; i++) {
            coords[i][0] -= com.x;
            coords[i][1] -= com.y;
            coords[i][2] -= com.z;
        }


        int nangle = (int) (90 / angle);
        for (int i = 0; i < nangle; i++) {
            rotateZ(coords, Math.PI / 180.0 * angle);
            try {
                xyzRanges = extents(atomContainer, coords, true);
            } catch (CDKException e) {
                return getDummyDescriptorValue(e);
            }
            lob = xyzRanges[0] / xyzRanges[1];
            bol = 1.0 / lob;
            if (lob < bol) {
                double tmp = lob;
                lob = bol;
                bol = tmp;
            }
            area = xyzRanges[0] * xyzRanges[1];
            if (lob > maxLOB) maxLOB = lob;
            if (area < minArea) {
                minArea = area;
                mmLOB = lob;
            }
        }

        DoubleArrayResult result = new DoubleArrayResult(2);
        result.add(maxLOB);
        result.add(mmLOB);

        return new DescriptorValue(getSpecification(),
                getParameterNames(), getParameters(),
                result, getDescriptorNames());
    }

    /**
     * Returns the specific type of the DescriptorResult object.
     * <p/>
     * The return value from this method really indicates what type of result will
     * be obtained from the {@link org.openscience.cdk.qsar.DescriptorValue} object. Note that the same result
     * can be achieved by interrogating the {@link org.openscience.cdk.qsar.DescriptorValue} object; this method
     * allows you to do the same thing, without actually calculating the descriptor.
     *
     * @return an object that implements the {@link org.openscience.cdk.qsar.result.IDescriptorResult} interface indicating
     *         the actual type of values returned by the descriptor in the {@link org.openscience.cdk.qsar.DescriptorValue} object
     */
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleArrayResultType(2);
    }

    private void rotateZ(double[][] coords, double theta) {
        int natom = coords.length;
        Matrix rZ = new Matrix(4, 4);
        rZ.set(0, 0, Math.cos(theta));
        rZ.set(0, 1, Math.sin(theta));
        rZ.set(1, 0, -1 * Math.sin(theta));
        rZ.set(1, 1, Math.cos(theta));
        rZ.set(2, 2, 1);
        rZ.set(3, 3, 1);
        Matrix newCoord = new Matrix(4, natom);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < natom; j++) {
                newCoord.set(i, j, coords[j][i]);
                newCoord.set(3, j, 1.0);
            }
        }
        newCoord = rZ.times(newCoord);
        newCoord = newCoord.transpose();
        for (int i = 0; i < natom; i++) {
            for (int j = 0; j < 3; j++)
                coords[i][j] = newCoord.get(i, j);
        }
    }

    private double[] extents(IAtomContainer atomContainer, double[][] coords, boolean withRadii) throws CDKException {
        double xmax = -1e30;
        double ymax = -1e30;
        double zmax = -1e30;

        double xmin = 1e30;
        double ymin = 1e30;
        double zmin = 1e30;

        int natom = atomContainer.getAtomCount();
        for (int i = 0; i < natom; i++) {
            double[] coord = new double[coords[0].length];
            System.arraycopy(coords[i], 0, coord, 0, coords[0].length);
            if (withRadii) {
                IAtom atom = atomContainer.getAtom(i);
                double radius = PeriodicTable.getCovalentRadius(atom.getSymbol());

                xmax = Math.max(xmax, coord[0] + radius);
                ymax = Math.max(ymax, coord[1] + radius);
                zmax = Math.max(zmax, coord[2] + radius);

                xmin = Math.min(xmin, coord[0] - radius);
                ymin = Math.min(ymin, coord[1] - radius);
                zmin = Math.min(zmin, coord[2] - radius);
            } else {
                xmax = Math.max(xmax, coord[0]);
                ymax = Math.max(ymax, coord[1]);
                zmax = Math.max(zmax, coord[2]);

                xmin = Math.min(xmin, coord[0]);
                ymin = Math.min(ymin, coord[1]);
                zmin = Math.min(zmin, coord[2]);
            }
        }
        double[] ranges = new double[3];
        ranges[0] = xmax - xmin;
        ranges[1] = ymax - ymin;
        ranges[2] = zmax - zmin;
        return ranges;
    }

    /**
     * Gets the parameterNames attribute of the PetitjeanNumberDescriptor object
     *
     * @return The parameterNames value
     */
    public String[] getParameterNames() {
        // no param names to return
        return (null);
    }


    /**
     * Gets the parameterType attribute of the PetitjeanNumberDescriptor object
     *
     * @param name Description of the Parameter
     * @return The parameterType value
     */
    public Object getParameterType(String name) {
        return (null);
    }
}
