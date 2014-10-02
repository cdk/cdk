/* Copyright (C) 2007  Federico
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

package org.openscience.cdk.qsar.descriptors.molecular;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.charges.Polarizability;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.AdjacencyMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.qsar.AbstractMolecularDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleArrayResultType;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

import java.util.Iterator;

/**
 * This class calculates ATS autocorrelation descriptor, where the weight equal
 * to the charges.
 *
 * @author Federico
 * @cdk.created 2007-03-01
 * @cdk.module qsarmolecular
 * @cdk.githash
 * @cdk.set qsar-descriptors
 */

@TestClass("org.openscience.cdk.qsar.descriptors.molecular.AutocorrelationDescriptorPolarizabilityTest")
public class AutocorrelationDescriptorPolarizability extends AbstractMolecularDescriptor implements
        IMolecularDescriptor {

    private static final String[] names = {"ATSp1", "ATSp2", "ATSp3", "ATSp4", "ATSp5"};

    private static double[] listpolarizability(IAtomContainer container, int[][] dmat) throws CDKException {
        int natom = container.getAtomCount();
        double[] polars = new double[natom];

        Polarizability polar = new Polarizability();
        for (int i = 0; i < natom; i++) {
            IAtom atom = container.getAtom(i);
            try {
                polars[i] = polar.calculateGHEffectiveAtomPolarizability(container, atom, false, dmat);
            } catch (Exception ex1) {
                throw new CDKException("Problems with assign Polarizability due to " + ex1.toString(), ex1);
            }
        }

        return polars;
    }

    /**
     * This method calculate the ATS Autocorrelation descriptor.
     */
    @TestMethod("testCalculate_IAtomContainer")
    @Override
    public DescriptorValue calculate(IAtomContainer container) {
        IAtomContainer molecule;
        try {
            molecule = (IAtomContainer) container.clone();
        } catch (CloneNotSupportedException e) {
            return getDummyDescriptorValue(new CDKException("Error occured during clone " + e));
        }

        // add H's in case they're not present
        try {
            CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(molecule.getBuilder());
            Iterator<IAtom> atoms = molecule.atoms().iterator();
            while (atoms.hasNext()) {
                IAtom atom = atoms.next();
                IAtomType type = matcher.findMatchingAtomType(molecule, atom);
                AtomTypeManipulator.configure(atom, type);
            }
            CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(molecule.getBuilder());
            hAdder.addImplicitHydrogens(molecule);
            AtomContainerManipulator.convertImplicitToExplicitHydrogens(molecule);
        } catch (Exception e) {
            return getDummyDescriptorValue(new CDKException("Could not add hydrogens: " + e.getMessage(), e));
        }

        // do aromaticity detecttion for calculating polarizability later on
        try {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        } catch (CDKException e) {
            return getDummyDescriptorValue(new CDKException("Could not percieve atom types: " + e.getMessage(), e));
        }
        try {
            Aromaticity.cdkLegacy().apply(molecule);
        } catch (CDKException e) {
            return getDummyDescriptorValue(new CDKException("Could not percieve aromaticity: " + e.getMessage(), e));
        }

        // get the distance matrix for pol calcs as well as for later on
        int[][] distancematrix = PathTools.computeFloydAPSP(AdjacencyMatrix.getMatrix(molecule));

        try {
            double[] w = listpolarizability(molecule, distancematrix);
            int natom = molecule.getAtomCount();
            double[] polarizabilitySum = new double[5];

            for (int k = 0; k < 5; k++) {
                for (int i = 0; i < natom; i++) {
                    if (molecule.getAtom(i).getSymbol().equals("H")) continue;
                    for (int j = 0; j < natom; j++) {
                        if (molecule.getAtom(j).getSymbol().equals("H")) continue;
                        if (distancematrix[i][j] == k) {
                            polarizabilitySum[k] += w[i] * w[j];
                        } else
                            polarizabilitySum[k] += 0.0;
                    }
                }
                if (k > 0) polarizabilitySum[k] = polarizabilitySum[k] / 2;

            }
            DoubleArrayResult result = new DoubleArrayResult(5);
            for (double aPolarizabilitySum : polarizabilitySum) {
                result.add(aPolarizabilitySum);

            }

            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), result,
                    getDescriptorNames());

        } catch (Exception ex) {
            return getDummyDescriptorValue(new CDKException("Error while calculating the ATSpolarizabilty descriptor: "
                    + ex.getMessage(), ex));
        }
    }

    private DescriptorValue getDummyDescriptorValue(Exception e) {
        DoubleArrayResult results = new DoubleArrayResult(5);
        for (int i = 0; i < 5; i++)
            results.add(Double.NaN);
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), results,
                getDescriptorNames(), e);
    }

    @TestMethod("testGetParameterNames")
    @Override
    public String[] getParameterNames() {
        return null;
    }

    @TestMethod("testGetParameterType_String")
    @Override
    public Object getParameterType(String name) {
        return null;
    }

    @TestMethod("testGetParameters")
    @Override
    public Object[] getParameters() {
        return null;
    }

    @TestMethod(value = "testNamesConsistency")
    @Override
    public String[] getDescriptorNames() {
        return names;
    }

    @TestMethod("testGetSpecification")
    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#autoCorrelationPolarizability",
                this.getClass().getName(), "The Chemistry Development Kit");
    }

    @TestMethod("testGetDescriptorResultType")
    @Override
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleArrayResultType(5);
    }

    @TestMethod("testSetParameters_arrayObject")
    @Override
    public void setParameters(Object[] params) throws CDKException {

    }

}
