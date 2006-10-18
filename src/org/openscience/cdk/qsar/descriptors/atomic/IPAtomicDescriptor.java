/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2006-05-04 21:29:58 +0200 (Do, 04 Mai 2006) $
 *  $Revision: 6171 $
 *
 *  Copyright (C) 2004-2006  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.qsar.descriptors.atomic;

import java.util.HashMap;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.descriptors.bond.BondPartialSigmaChargeDescriptor;
import org.openscience.cdk.qsar.descriptors.bond.ResonancePositiveChargeDescriptor;
import org.openscience.cdk.qsar.model.weka.J48WModel;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;

/**
 *  This class returns the ionization potential of an atom. It is
 *  based in learning machine (in this case J48 see J48WModel) 
 *  from experimental values. Up to now is
 *  only possible predict for Cl,Br,I,N,P,O,S Atoms and they are not belong to
 *  conjugated system or not adjacent to an double bond.
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
 * @author         Miguel Rojas
 * @cdk.created    2006-05-26
 * @cdk.module     qsar
 * @cdk.set        qsar-descriptors
 * @cdk.dictref qsar-descriptors:ionizationPotential
 * @cdk.depends weka.jar
 * @cdk.builddepends weka.jar
 * @see J48WModel
 */
public class IPAtomicDescriptor implements IAtomicDescriptor {
	
	/** Hash map which contains the classAttribu = value of IP */
	private HashMap hash = null;
	
	private String[] classAttrib = {
			"05_0","05_1","05_2","05_3","05_4","05_5","05_6","05_7","05_8","05_9",
			"06_0","06_1","06_2","06_3","06_4","06_5","06_6","06_7","06_8","06_9",
			"07_0","07_1","07_2","07_3","07_4","07_5","07_6","07_7","07_8","07_9",
			"08_0","08_1","08_2","08_3","08_4","08_5","08_6","08_7","08_8","08_9",
			"09_0","09_1","09_2","09_3","09_4","09_5","09_6","09_7","09_8","09_9",
			"10_0","10_1","10_2","10_3","10_4","10_5","10_6","10_7","10_8","10_9",
			"11_0","11_1","11_2","11_3","11_4","11_5","11_6","11_7","11_8","11_9",
			"12_0","12_1","12_2","12_3","12_4","12_5","12_6","12_7","12_8","12_9",
			"13_0","13_1","13_2","13_3","13_4","13_5","13_6","13_7","13_8","13_9",
			"14_0","14_1","14_2","14_3","14_4","14_5","14_6","14_7","14_8","14_9",};

	/**
	 *  Constructor for the IPAtomicDescriptor object
	 */
	public IPAtomicDescriptor() {
		this.hash = new HashMap();
		double value = 5.05;
		for(int i = 0 ; i < classAttrib.length ; i++){
			this.hash.put(classAttrib[i],new Double(value));
			value += 0.1;
		}
	}
	/**
	 *  Gets the specification attribute of the IPAtomicDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#ionizationPotential",
				this.getClass().getName(),
				"$Id: IPAtomicDescriptor.java 6171 2006-5-22 19:29:58Z egonw $",
				"The Chemistry Development Kit");
	}
    /**
     * This descriptor does have any parameter.
     */
    public void setParameters(Object[] params) throws CDKException {
    }


    /**
     *  Gets the parameters attribute of the IPAtomicDescriptor object.
     *
     *@return    The parameters value
     * @see #setParameters
     */
    public Object[] getParameters() {
        return new Object[0];
    }
	/**
	 *  This method calculates the ionization potential of an atom.
	 *
	 *@param  chemObj           The IAtom to ionize.
	 *@param  container         Parameter is the IAtomContainer.
	 *@return                   The ionization potential. Not possible the ionization.
	 *@exception  CDKException  Description of the Exception
	 */
	public DescriptorValue calculate(IAtom atom, IAtomContainer container) throws CDKException
	{
		double resultD = -1.0;
		boolean isTarget = false;
		Double[][] resultsH = null;
		String path = "";
		if(atom.getSymbol().equals("F")||
					atom.getSymbol().equals("Cl")||
					atom.getSymbol().equals("Br")||
					atom.getSymbol().equals("I")||
					atom.getSymbol().equals("N")||
					atom.getSymbol().equals("S")||
					atom.getSymbol().equals("O")||
					atom.getSymbol().equals("P")){
				if(atom.getMaxBondOrder() > 1 && atom.getSymbol().equals("O")){
					resultsH = calculateCarbonylDescriptor(atom, container);
					path = "data/arff/Carbonyl1.arff";
					isTarget = true;
				}else{
					resultsH = calculateHeteroAtomDescriptor(atom, container);
					path = "data/arff/HeteroAtom1.arff";
					isTarget = true;
				}
		}
		if(isTarget){
			J48WModel j48 = new J48WModel(true,path);
    		String[] options = new String[4];
    		options[0] = "-C";
    		options[1] = "0.25";
    		options[2] = "-M";
    		options[3] = "2";
    		j48.setOptions(options);
    		j48.build();
    		j48.setParameters(resultsH);
            j48.predict();
    		String[] result = (String[])j48.getPredictPredicted();
    		resultD = ((Double)hash.get(result[0])).doubleValue();
		}
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(resultD));
	}
	/**
	 * Calculate the necessary descriptors for Heteratom atoms
	 * @param atomContainer The IAtomContainer
	 * @return     Array with the values of the descriptors.
	 * @throws CDKException 
	 */
	private Double[][] calculateHeteroAtomDescriptor(IAtom atom, IAtomContainer atomContainer) throws CDKException {
		Double[][] results = new Double[1][3];
		SigmaElectronegativityDescriptor descriptor1 = new SigmaElectronegativityDescriptor();
		PartialSigmaChargeDescriptor descriptor2 = new PartialSigmaChargeDescriptor();
		EffectiveAtomPolarizabilityDescriptor descriptor3 = new EffectiveAtomPolarizabilityDescriptor();

		results[0][0]= new Double(((DoubleResult)descriptor1.calculate(atom,atomContainer).getValue()).doubleValue());
		results[0][1]= new Double(((DoubleResult)descriptor2.calculate(atom,atomContainer).getValue()).doubleValue());
		results[0][2]= new Double(((DoubleResult)descriptor3.calculate(atom,atomContainer).getValue()).doubleValue());
    	
		return results;
	}
	/**
	 * Calculate the necessary descriptors for Carbonyl group
	 * @param atomContainer The IAtomContainer
	 * @return     Array with the values of the descriptors.
	 */
	private Double[][] calculateCarbonylDescriptor(IAtom atom, IAtomContainer atomContainer) {
		Double[][] results = new Double[1][6];
		Integer[] params = new Integer[1];
		IAtom positionX = atom;
		IAtom positionC = (IAtom) atomContainer.getConnectedAtomsList(atom).get(0);
		IBond bond = atomContainer.getBond(positionX, positionC);
		try {
        	/*0*/
			SigmaElectronegativityDescriptor descriptor1 = new SigmaElectronegativityDescriptor();
    		results[0][0]= new Double(((DoubleResult)descriptor1.calculate(positionC, atomContainer).getValue()).doubleValue());
        	/*1*/
    		PartialSigmaChargeDescriptor descriptor2 = new PartialSigmaChargeDescriptor();
    		results[0][1]= new Double(((DoubleResult)descriptor2.calculate(positionC,atomContainer).getValue()).doubleValue());
    		/*2*/
    		BondPartialSigmaChargeDescriptor descriptor3 = new BondPartialSigmaChargeDescriptor();
    		params[0] = new Integer(atomContainer.getBondNumber(bond));
    		descriptor3.setParameters(params);
    		results[0][2]= new Double(((DoubleResult)descriptor3.calculate(atomContainer).getValue()).doubleValue());
    		/*3*/
    		SigmaElectronegativityDescriptor descriptor4 = new SigmaElectronegativityDescriptor();
    		results[0][3]= new Double(((DoubleResult)descriptor4.calculate(positionX, atomContainer).getValue()).doubleValue());
        	/*4*/
    		PartialSigmaChargeDescriptor descriptor5 = new PartialSigmaChargeDescriptor();
    		results[0][4]= new Double(((DoubleResult)descriptor5.calculate(positionX, atomContainer).getValue()).doubleValue());
    		/*5*/
    		ResonancePositiveChargeDescriptor descriptor6 = new ResonancePositiveChargeDescriptor();
    		params[0] = new Integer(atomContainer.getBondNumber(bond));
    		descriptor6.setParameters(params);
			DoubleArrayResult dar = ((DoubleArrayResult)descriptor6.calculate(atomContainer).getValue());
			double datT = (dar.get(0)+dar.get(1))/2;
			results[0][5] = new Double(datT);
 
		} catch (CDKException e) {
			e.printStackTrace();
		}
		return results;
	}
	/**
	 *  Gets the parameterNames attribute of the IPAtomicDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "targetPosition";
		return params;
	}
	/**
	 *  Gets the parameterType attribute of the IPAtomicDescriptor object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return new Integer(0);
	}
}

