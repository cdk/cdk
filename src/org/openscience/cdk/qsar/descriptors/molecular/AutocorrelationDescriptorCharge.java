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


import org.openscience.cdk.Molecule;
import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.matrix.TopologicalMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleArrayResultType;
import org.openscience.cdk.qsar.result.IDescriptorResult;

/**
 * This class calculates ATS autocorrelation descriptor, where the weight equal
 * to the charges.
 * 
 * @author      Federico
 * @cdk.created 2007-02-27
 * @cdk.module  qsarmolecular
 * @cdk.svnrev  $Revision: 9162 $
 * @cdk.set     qsar-descriptors
 */

public class AutocorrelationDescriptorCharge implements IMolecularDescriptor{

    private static double[] listcharges (IAtomContainer container)throws CDKException{
        int natom = container.getAtomCount();
        Molecule mol = new Molecule(container);
        IAtom atom;

        try{
        GasteigerMarsiliPartialCharges peoe = new GasteigerMarsiliPartialCharges();
        peoe.assignGasteigerMarsiliSigmaPartialCharges(mol, true);
        }catch(Exception ex1) {
            throw new CDKException("Problems with assignGasteigerMarsiliPartialCharges due to " + ex1.toString(), ex1);
        }

        double[] charges = new double[natom];

        for(int i = 0; i < natom; i++){
            atom = mol.getAtom(i);
            charges[i] = atom.getCharge();
        }
        return charges;
    }


    public DescriptorValue calculate(IAtomContainer container) throws CDKException {
        try {
            double[] w = listcharges(container);
            int natom = container.getAtomCount();
            int[][] distancematrix = TopologicalMatrix.getMatrix(container);

            double[] chargeSum = new double[5];

            for (int k = 0; k < 5; k++) {
                for (int i = 0; i < natom; i++) {
                    for (int j = 0; j < natom; j++) {
                        if (distancematrix[i][j] == k) {
                            chargeSum[k] += w[i] * w[j];
                        } else chargeSum[k] += 0.0;
                    }
                }
                if (k > 0) chargeSum[k] = chargeSum[k] / 2;

            }
            DoubleArrayResult result = new DoubleArrayResult(5);
            for (double aChargeSum : chargeSum) {
                result.add(aChargeSum);
            }
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    result, new String[]{"ATSc1", "ATSc2", "ATSc3", "ATSc4", "ATSc5"});

        } catch (Exception ex) {
            throw new CDKException("Error while calculating the ATS_charge descriptor: " + ex.getMessage(), ex);
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
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#autoCorrelationCharge",
                this.getClass().getName(),
                "$Id: AutoCorrelationDescriptorCharge.java $",
                "The Chemistry Development Kit");
    }

    public IDescriptorResult getDescriptorResultType() {
        return new DoubleArrayResultType(5);
    }

    public void setParameters(Object[] params) throws CDKException {

        }



}
