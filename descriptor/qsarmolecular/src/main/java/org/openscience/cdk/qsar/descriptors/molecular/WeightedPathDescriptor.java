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

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.qsar.AbstractMolecularDescriptor;
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
 *
 * These decsriptors were described  by Randic ({@cdk.cite RAN84}) and characterize molecular
 * branching. Five descriptors are calculated, based on the implementation in the ADAPT
 * software package. Note that the descriptor is based on identifying <b>all</b> paths between pairs of
 * atoms and so is NP-hard. This means that it can take some time for large, complex molecules.
 * The class returns a <code>DoubleArrayResult</code> containing the five
 * descriptors in the order described below.
 *
 * <div>
 * <table border=1>
 * <caption><span id="dmwp">DMWP</span></caption>
 * <tr>
 * <td>WTPT1</td><td>molecular ID</td></tr><tr>
 * <td>WTPT2</td><td> molecular ID / number of atoms</td></tr><tr>
 * <td>WTPT3</td><td> sum of path lengths starting
 * from heteroatoms</td></tr><tr>
 *
 * <td>WTPT4</td><td> sum of path lengths starting
 * from oxygens</td></tr><tr>
 * <td>WTPT5</td><td> sum of path lengths starting
 * from nitrogens</td></tr>
 * </table>
 * </div>
 *
 * <table border="1"><caption>Parameters for this descriptor:</caption>
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
 * @cdk.githash
 * @cdk.dictref qsar-descriptors:weightedPath
 */
public class WeightedPathDescriptor extends AbstractMolecularDescriptor implements IMolecularDescriptor {

    private static final String[] NAMES = {"WTPT-1", "WTPT-2", "WTPT-3", "WTPT-4", "WTPT-5"};

    public WeightedPathDescriptor() {}

    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#weightedPath", this.getClass()
                        .getName(), "The Chemistry Development Kit");
    }

    /**
     * Sets the parameters attribute of the WeightedPathDescriptor object.
     *
     * @param params The new parameters value
     * @throws org.openscience.cdk.exception.CDKException
     *          Description of the Exception
     */
    @Override
    public void setParameters(Object[] params) throws CDKException {
        // no parameters for this descriptor
    }

    /**
     * Gets the parameters attribute of the WeightedPathDescriptor object.
     *
     * @return The parameters value
     */
    @Override
    public Object[] getParameters() {
        // no parameters to return
        return (null);
    }

    @Override
    public String[] getDescriptorNames() {
        return NAMES;
    }

    /**
     * Gets the parameterNames attribute of the WeightedPathDescriptor object.
     *
     * @return The parameterNames value
     */
    @Override
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
    @Override
    public Object getParameterType(String name) {
        return (null);
    }

    /**
     * Calculates the weighted path descriptors.
     *
     * @param container Parameter is the atom container.
     * @return A DoubleArrayResult value representing the weighted path values
     */
    @Override
    public DescriptorValue calculate(IAtomContainer container) {
        IAtomContainer local = AtomContainerManipulator.removeHydrogens(container);
        int natom = local.getAtomCount();
        DoubleArrayResult retval = new DoubleArrayResult();

        // unique paths
        List<List<IAtom>> pathList = new ArrayList<>();
        for (int i = 0; i < natom - 1; i++) {
            IAtom a = local.getAtom(i);
            for (int j = i + 1; j < natom; j++) {
                IAtom b = local.getAtom(j);
                pathList.addAll(PathTools.getAllPaths(local, a, b));
            }
        }

        // heteroatoms
        double mid = calcWeight(pathList, local, natom);
        retval.add(mid);
        retval.add(mid / (double) natom);

        pathList.clear();
        int count = 0;
        for (int i = 0; i < natom; i++) {
            IAtom a = local.getAtom(i);
            if (a.getAtomicNumber() == IElement.C) continue;
            count++;
            for (int j = 0; j < natom; j++) {
                IAtom b = local.getAtom(j);
                if (a.equals(b)) continue;
                pathList.addAll(PathTools.getAllPaths(local, a, b));
            }
        }
        retval.add(calcWeight(pathList, local, count));

        // oxygens
        pathList.clear();
        count = 0;
        for (int i = 0; i < natom; i++) {
            IAtom a = local.getAtom(i);
            if (a.getAtomicNumber() != IElement.O) continue;
            count++;
            for (int j = 0; j < natom; j++) {
                IAtom b = local.getAtom(j);
                if (a.equals(b)) continue;
                pathList.addAll(PathTools.getAllPaths(local, a, b));
            }
        }
        retval.add(calcWeight(pathList, local, count));

        // nitrogens
        pathList.clear();
        count = 0;
        for (int i = 0; i < natom; i++) {
            IAtom a = local.getAtom(i);
            if (a.getAtomicNumber() != IElement.N) continue;
            count++;
            for (int j = 0; j < natom; j++) {
                IAtom b = local.getAtom(j);
                if (a.equals(b)) continue;
                pathList.addAll(PathTools.getAllPaths(local, a, b));
            }
        }
        retval.add(calcWeight(pathList, local, count));

        return new DescriptorValue(getSpecification(),
                                   getParameterNames(),
                                   getParameters(),
                                   retval,
                                   getDescriptorNames());
    }

    /**
     * Returns the specific type of the DescriptorResult object.
     *
     * The return value from this method really indicates what type of result will
     * be obtained from the {@link org.openscience.cdk.qsar.DescriptorValue} object. Note that the same result
     * can be achieved by interrogating the {@link org.openscience.cdk.qsar.DescriptorValue} object; this method
     * allows you to do the same thing, without actually calculating the descriptor.
     *
     * @return an object that implements the {@link org.openscience.cdk.qsar.result.IDescriptorResult} interface indicating
     *         the actual type of values returned by the descriptor in the {@link org.openscience.cdk.qsar.DescriptorValue} object
     */
    @Override
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleArrayResultType(5);
    }

    private double calcWeight(List<List<IAtom>> pathList,
                              IAtomContainer atomContainer,
                              int natom) {
        double result = 0.0;
        for (List<IAtom> p : pathList) {
            double val = 1.0;
            for (int j = 0; j < p.size() - 1; j++) {
                IAtom a = p.get(j);
                IAtom b = p.get(j + 1);
                int n1 = a.getBondCount();
                int n2 = b.getBondCount();
                val /= Math.sqrt(n1 * n2);
            }
            result += val;
        }
        result += natom; // since we don't calculate paths of length 0 above
        return result;
    }
}
