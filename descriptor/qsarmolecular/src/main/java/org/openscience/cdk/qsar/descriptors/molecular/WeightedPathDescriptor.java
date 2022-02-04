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
import org.openscience.cdk.interfaces.IBond;
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
import java.util.function.Predicate;

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

    public static boolean unique(List<IAtom> path) {
        return path.get(0).getIndex() < path.get(path.size()-1).getIndex();
    }

    private void collectPaths(boolean[] visit,
                              List<List<IAtom>> paths,
                              List<IAtom> path,
                              IAtom atom,
                              IBond prev) {
        visit[atom.getIndex()] = true;
        path.add(atom);
        if (path.size() > 1)
            paths.add(new ArrayList<>(path));
        for (IBond bond : atom.bonds()) {
            if (bond == prev)
                continue;
            IAtom nbor = bond.getOther(atom);
            if (!visit[nbor.getIndex()])
                collectPaths(visit, paths, path, nbor, prev);
        }
        visit[atom.getIndex()] = false;
        path.remove(path.size()-1);
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

        DoubleArrayResult retval = new DoubleArrayResult();

        // unique paths
        List<List<IAtom>> pathList = new ArrayList<>();
        boolean[] visit = new boolean[local.getAtomCount()];
        for (IAtom a : local.atoms()) {
            collectPaths(visit, pathList, new ArrayList<>(), a, null);
        }

        int numAtoms = local.getAtomCount();
        int numHetero = 0;
        int numOxygen = 0;
        int numNitrogen = 0;
        for (IAtom a : local.atoms()) {
            switch ((byte)a.getAtomicNumber().intValue()) {
                case IAtom.C: break;
                case IAtom.N: numNitrogen++; numHetero++; break;
                case IAtom.O: numOxygen++; numHetero++; break;
                default:      numHetero++; break;
            }
        }

        double mid = calcWeight(pathList, local, numAtoms,
                WeightedPathDescriptor::unique);
        retval.add(mid);
        retval.add(mid / (double) numAtoms);
        retval.add(calcWeight(pathList, local, numHetero,
                p -> p.get(0).getAtomicNumber() != IAtom.C));
        retval.add(calcWeight(pathList, local, numOxygen,
                p -> p.get(0).getAtomicNumber() == IAtom.O));
        retval.add(calcWeight(pathList, local, numNitrogen,
                p -> p.get(0).getAtomicNumber() == IAtom.N));

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
                              int natom,
                              Predicate<List<IAtom>> pred) {
        double result = 0.0;
        for (List<IAtom> p : pathList) {
            if (!pred.test(p))
                continue;
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
