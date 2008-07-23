/* $Revision$ $Author$ $Date: 2007-01-04 18:46:10 +0100 (gio, 04 gen 2007)$
 *  
 * Copyright (C) 2007  Federico
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

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Element;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.matrix.TopologicalMatrix;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleArrayResultType;
import org.openscience.cdk.qsar.result.IDescriptorResult;

/**
 * This class calculates ATS autocorrelation descriptor, where the weight equal
 * to the scaled atomic mass {@cdk.cite Moreau1980}.
 * 
 * @author      Federico
 * @cdk.created 2007-02-08
 * @cdk.module  qsarmolecular
 * @cdk.svnrev  $Revision$
 * @cdk.set     qsar-descriptors
 */
public class AutocorrelationDescriptorMass implements IMolecularDescriptor{

    private final static String[] names = {"ATSm1", "ATSm2", "ATSm3", "ATSm4", "ATSm5"};
    private final static double CARBON_MASS = 12.010735896788;
	
	/**
	 * This method gets the scaled atomic masses of atoms in a molecule.
	 * @param element
	 * @return
	 * @throws java.io.IOException
     * @throws ClassNotFoundException
     */
    private static double scaledAtomicMasses(IElement element)
            throws java.io.IOException, ClassNotFoundException {

    	IsotopeFactory isofac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
        double realmasses = isofac.getNaturalMass(new Element("H"));
        return (realmasses / CARBON_MASS);

    }

	
	/**
	 * This method gets a list o scaled atomic masses.
	 * @param container
	 * @return
	 * @throws java.io.IOException
	 * @throws ClassNotFoundException
	 */
	private static double[] listConvertion(IAtomContainer container)
			throws java.io.IOException, ClassNotFoundException{
		int natom = container.getAtomCount();

		double[] scalated = new double[natom];

		for (int i = 0; i < natom; i++) {
			scalated[i] = scaledAtomicMasses(container.getAtom(i));
		}
		return scalated;
	}
	

	/**
     * This method calculate the ATS Autocorrelation descriptor.
     */
    public DescriptorValue calculate(IAtomContainer container) {
        try {
            double[] w = listConvertion(container);
            int natom = container.getAtomCount();
            int[][] distancematrix = TopologicalMatrix.getMatrix(container);
            double[] masSum = new double[5];

            for (int k = 0; k < 5; k++) {
                for (int i = 0; i < natom; i++) {
                    for (int j = 0; j < natom; j++) {

                        if (distancematrix[i][j] == k) {
                            masSum[k] += w[i] * w[j];
                        } else masSum[k] += 0.0;
                    }
                }
                if (k > 0) masSum[k] = masSum[k] / 2;

            }
            DoubleArrayResult result = new DoubleArrayResult(5);
            for (double aMasSum : masSum) {
                result.add(aMasSum);
            }

            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    result, getDescriptorNames());

        } catch (Exception ex) {
            DoubleArrayResult result = new DoubleArrayResult(5);
            for (int i = 0; i < 5; i++) result.add(Double.NaN);
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    result, getDescriptorNames(),
                    new CDKException("Error while calculating the ATS_mass descriptor: " + ex.getMessage(), ex));
        }
    }

    public String[] getParameterNames() {
		return new String[0];
	}

	public Object getParameterType(String name) {
		return null;
	}

	public Object[] getParameters() {
		return null;
	}

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return names;
    }

    public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#autoCorrelationMass",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
	}
	
	public IDescriptorResult getDescriptorResultType() {
        return new DoubleArrayResultType(5);
    }

	public void setParameters(Object[] params) throws CDKException {
		
		}

}
