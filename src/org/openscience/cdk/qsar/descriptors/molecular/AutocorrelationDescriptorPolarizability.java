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

import org.openscience.cdk.charges.Polarizability;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.matrix.TopologicalMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

/**
 * This class calculates ATS autocorrelation descriptor, where the weight equal
 * to the charges.
 * 
 * @author      Federico
 * @cdk.created 2007-03-01
 * @cdk.module  qsar
 * @cdk.svnrev  $Revision: 9162 $
 * @cdk.set     qsar-descriptors
 */

public class AutocorrelationDescriptorPolarizability implements IMolecularDescriptor{
	
	private static double[] listpolarizability (IAtomContainer container)throws CDKException{
		int natom = container.getAtomCount();
		double[] polars = new double[natom];
		
		for(int i = 0; i < natom; i++){
			IAtom atom = container.getAtom(i);
			try{
				Polarizability polar = new Polarizability();
				polars[i] = polar.calculateGHEffectiveAtomPolarizability(container, atom, 100);
			}catch(Exception ex1) {
				throw new CDKException("Problems with assign Polarizability due to " + ex1.toString(), ex1);
			}
		}
		
		return polars;
	}


    /**
     * This method calculate the ATS Autocorrelation descriptor.
     */
    public DescriptorValue calculate(IAtomContainer container) throws CDKException {
        try {
            double[] w = listpolarizability(container);
            int natom = container.getAtomCount();
            int[][] distancematrix = TopologicalMatrix.getMatrix(container);
            double[] PolarizabilitySum = new double[5];

            for (int k = 0; k < 5; k++) {
                for (int i = 0; i < natom; i++) {
                    for (int j = 0; j < natom; j++) {

                        if (distancematrix[i][j] == k) {
                            PolarizabilitySum[k] += w[i] * w[j];
                        } else PolarizabilitySum[k] += 0.0;
                    }
                }
                if (k > 0) PolarizabilitySum[k] = PolarizabilitySum[k] / 2;

            }
            DoubleArrayResult result = new DoubleArrayResult(5);
            for (double aPolarizabilitySum : PolarizabilitySum) {
                result.add(aPolarizabilitySum);

            }

            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    result, new String[]{"ATSp1", "ATSp2", "ATSp3", "ATSp4", "ATSp5"});

        } catch (Exception ex) {
            throw new CDKException("Error while calculating the ATS_polarizability descriptor: " + ex.getMessage(), ex);
        }
    }

	public String[] getParameterNames() {
		return null;
	}

	public Object getParameterType(String name) {
		return null;
	}

	public Object[] getParameters() {
		return null;
	}

	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#autoCorrelationPolarizability",
                this.getClass().getName(),
                "$Id: AutoCorrelationDescriptorPolarizability.java $",
                "The Chemistry Development Kit");
	}
	
	public IDescriptorResult getDescriptorResultType() {
        return new DoubleArrayResult();
    }

	public void setParameters(Object[] params) throws CDKException {
		
		}
	
}