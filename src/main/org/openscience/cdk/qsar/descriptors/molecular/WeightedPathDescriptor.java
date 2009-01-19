/*
 *  Copyright (C) 2004-2007  Rajarshi Guha <rajarshi@users.sourceforge.net>
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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleArrayResultType;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.ArrayList;
import java.util.List;


/**
 * Evaluates the weighted path descriptors.
 * <p/>
 * These decsriptors were described  by Randic ({@cdk.cite RAN84}) and characterize molecular
 * branching. Five descriptors are calculated, based on the implementation in the ADAPT
 * software package. Note that the descriptor is based on identifying <b>all</b> pahs between pairs of
 * atoms and so is NP-hard. This means that it can take some time for large, complex molecules.
 * The class returns a <code>DoubleArrayResult</code> containing the five
 * descriptors in the order described below.
 * <p/>
 * <center>
 * <table border=1>
 * <caption><a name="dmwp">DMWP</a></caption>
 * <tr>
 * <td>WTPT1</td><td>molecular ID</td></tr><tr>
 * <td>WTPT2</td><td> molecular ID / number of atoms</td></tr><tr>
 * <td>WTPT3</td><td> sum of path lengths starting
 * from heteroatoms</td></tr><tr>
 * <p/>
 * <td>WTPT4</td><td> sum of path lengths starting
 * from oxygens</td></tr><tr>
 * <td>WTPT5</td><td> sum of path lengths starting
 * from nitrogens</td></tr>
 * </table>
 * </center>
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
 * @cdk.created 2006-01-15
 * @cdk.module qsarmolecular
 * @cdk.svnrev  $Revision$
 * @cdk.set qsar-descriptors
 * @cdk.dictref qsar-descriptors:weightedPath
 */
@TestClass("org.openscience.cdk.qsar.descriptors.molecular.WeightedPathDescriptorTest")
public class WeightedPathDescriptor implements IMolecularDescriptor {

    private static final String[] names = {
            "WTPT-1", "WTPT-2", "WTPT-3", "WTPT-4", "WTPT-5"
    };

    public WeightedPathDescriptor() {
    }

    @TestMethod("testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#weightedPath",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }


    /**
     * Sets the parameters attribute of the WeightedPathDescriptor object.
     *
     * @param params The new parameters value
     * @throws org.openscience.cdk.exception.CDKException
     *          Description of the Exception
     */
    @TestMethod("testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
        // no parameters for this descriptor
    }

    /**
     * Gets the parameters attribute of the WeightedPathDescriptor object.
     *
     * @return The parameters value
     */
    @TestMethod("testGetParameters")
    public Object[] getParameters() {
        // no parameters to return
        return (null);
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return names; 
    }

    /**
     * Gets the parameterNames attribute of the WeightedPathDescriptor object.
     *
     * @return The parameterNames value
     */
    @TestMethod("testGetParameterNames")
    public String[] getParameterNames() {
        // no param names to return
        return (null);
    }


    /**
     * Gets the parameterType attribute of the WeightedPathDescriptor object.
     *
     * @param name Description of the Parameter
     * @return The parameterType value
     */
    @TestMethod("testGetParameterType_String")
    public Object getParameterType(String name) {
        return (null);
    }

    /**
     * Calculates the weighted path descriptors.
     *
     * @param container Parameter is the atom container.
     * @return A DoubleArrayResult value representing the weighted path values
     */

    @TestMethod("testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtomContainer container) {
        IAtomContainer local = AtomContainerManipulator.removeHydrogens(container);
        int natom = local.getAtomCount();
        DoubleArrayResult retval = new DoubleArrayResult();

        ArrayList pathList = new ArrayList();

        // unique paths
        for (int i = 0; i < natom - 1; i++) {
            IAtom a = local.getAtom(i);
            for (int j = i + 1; j < natom; j++) {
                IAtom b = local.getAtom(j);
                pathList.addAll(PathTools.getAllPaths(local, a, b));
            }
        }

        // heteroatoms
        double[] pathWts = getPathWeights(pathList, local);
        double mid = 0.0;
        for (double pathWt3 : pathWts) mid += pathWt3;
        mid += natom; // since we don't calculate paths of length 0 above

        retval.add(mid);
        retval.add(mid / (double) natom);

        pathList.clear();
        int count = 0;
        for (int i = 0; i < natom; i++) {
            IAtom a = local.getAtom(i);
            if (a.getSymbol().equalsIgnoreCase("C")) continue;
            count++;
            for (int j = 0; j < natom; j++) {
                IAtom b = local.getAtom(j);
                if (a.equals(b)) continue;
                pathList.addAll(PathTools.getAllPaths(local, a, b));
            }
        }
        pathWts = getPathWeights(pathList, local);
        mid = 0.0;
        for (double pathWt2 : pathWts) mid += pathWt2;
        mid += count;
        retval.add(mid);

        // oxygens
        pathList.clear();
        count = 0;
        for (int i = 0; i < natom; i++) {
            IAtom a = local.getAtom(i);
            if (!a.getSymbol().equalsIgnoreCase("O")) continue;
            count++;
            for (int j = 0; j < natom; j++) {
                IAtom b = local.getAtom(j);
                if (a.equals(b)) continue;
                pathList.addAll(PathTools.getAllPaths(local, a, b));
            }
        }
        pathWts = getPathWeights(pathList, local);
        mid = 0.0;
        for (double pathWt1 : pathWts) mid += pathWt1;
        mid += count;
        retval.add(mid);

        // nitrogens
        pathList.clear();
        count = 0;
        for (int i = 0; i < natom; i++) {
            IAtom a = local.getAtom(i);
            if (!a.getSymbol().equalsIgnoreCase("N")) continue;
            count++;
            for (int j = 0; j < natom; j++) {
                IAtom b = local.getAtom(j);
                if (a.equals(b)) continue;
                pathList.addAll(PathTools.getAllPaths(local, a, b));
            }
        }
        pathWts = getPathWeights(pathList, local);
        mid = 0.0;
        for (double pathWt : pathWts) mid += pathWt;
        mid += count;
        retval.add(mid);


        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                retval, getDescriptorNames());
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
    @TestMethod("testGetDescriptorResultType")
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleArrayResultType(5);
    }

    private double[] getPathWeights(List pathList, IAtomContainer atomContainer) {
        double[] pathWts = new double[pathList.size()];
        for (int i = 0; i < pathList.size(); i++) {
            List p = (List) pathList.get(i);
            pathWts[i] = 1.0;
            for (int j = 0; j < p.size() - 1; j++) {
                IAtom a = (IAtom) p.get(j);
                IAtom b = (IAtom) p.get(j + 1);
                int n1 = atomContainer.getConnectedAtomsList(a).size();
                int n2 = atomContainer.getConnectedAtomsList(b).size();
                pathWts[i] /= Math.sqrt(n1 * n2);
            }
        }
        return pathWts;
    }

}
    

