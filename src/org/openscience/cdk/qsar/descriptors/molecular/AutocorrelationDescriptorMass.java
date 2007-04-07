/* $Revision: 7636 $ $Author: egonw $ $Date: 2007-01-04 18:46:10 +0100 (gio, 04 gen 2007)$
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

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.matrix.TopologicalMatrix;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.tools.MFAnalyser;

/**
 * This class calculates ATS autocorrelation descriptor, where the weight equal
 * to the scaled atomic mass {@cdk.cite Moreau1980}.
 * 
 * @author      Federico
 * @cdk.created 2007-02-08
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 */
public class AutocorrelationDescriptorMass implements IMolecularDescriptor{

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

		double realmasses = MFAnalyser.getNaturalMass(element);
		double scaled = (realmasses / CARBON_MASS);

		return scaled;
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
    public DescriptorValue calculate(IAtomContainer container) throws CDKException {
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
            for (int i = 0; i < masSum.length; i++) {
                result.add(masSum[i]);
            }

            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    result, new String[]{"ATSm1", "ATSm2", "ATSm3", "ATSm4", "ATSm5"});

        } catch (Exception ex) {
            throw new CDKException("Error while calculating the ATS_mass descriptor: " + ex.getMessage(), ex);
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

	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#autoCorrelationMass",
                this.getClass().getName(),
                "$Id: AutoCorrelationDescriptorMass.java $",
                "The Chemistry Development Kit");
	}
	
	public IDescriptorResult getDescriptorResultType() {
        return new DoubleResult(0.0);
    }

	public void setParameters(Object[] params) throws CDKException {
		
		}

}
