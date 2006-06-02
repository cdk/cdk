/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2006-05-04 21:29:58 +0200 (Do, 04 Mai 2006) $
 *  $Revision: 6171 $
 *
 *  Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
import java.util.Vector;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.model.weka.J48WModel;
import org.openscience.cdk.qsar.result.DoubleResult;

/**
 *  This class returns the ionization potential of an atom. It is
 *  based in learning machine from experimental values.
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>targetPosition</td>
 *     <td>0</td>
 *     <td>The position of the target atom</td>
 *   </tr>
 * </table>
 *
 *@author         Miguel Rojas
 *@cdk.created    2006-05-26
 *@cdk.module     qsar
 *@cdk.set        qsar-descriptors
 * @cdk.dictref qsar-descriptors:ionizationPotential
 */
public class IPDescriptor implements IMolecularDescriptor {
	/** Position of the atom in the AtomContainer*/
	private int targetPosition = 0;

	/** Hash map which contains the classAttribu = value of IP */
	private HashMap hash = null;
	
	private String[] classAttrib = {
			"07_2","07_2","07_4","07_6","07_8",
			"08_0","08_2","08_4","08_6","08_8",
			"09_0","09_2","09_4","09_6","09_8",
			"10_0","10_2","10_4","10_6","10_8",
			"11_0","11_2","11_4","11_6","11_8",
			"12_0","12_2","12_4","12_6","12_8",
			"13_0","13_2","13_4","13_6","13_8",
			"14_0","14_2","14_4","14_6","14_8",};

	/**
	 *  Constructor for the IPDescriptor object
	 */
	public IPDescriptor() {
		this.hash = new HashMap();
		double value = 7.1;
		for(int i = 0 ; i < 40 ; i++){
			this.hash.put(classAttrib[i],new Double(value));
			value += 0.2;
		}
	}
	/**
	 *  Gets the specification attribute of the IPDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#ionizationPotential",
				this.getClass().getName(),
				"$Id: IPDescriptor.java 6171 2006-5-22 19:29:58Z egonw $",
				"The Chemistry Development Kit");
	}
	/**
	 *  Sets the parameters attribute of the IPDescriptor object
	 *
	 *@param  params            The parameter is the atom position
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("IPDescriptor only expects one parameter");
		}
		if (!(params[0] instanceof Integer)) {
			throw new CDKException("The parameter must be of type Integer");
		}
		targetPosition = ((Integer) params[0]).intValue();
	}
	/**
	 *  Gets the parameters attribute of the IPDescriptor object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		Object[] params = new Object[1];
		params[0] = new Integer(targetPosition);
		return params;
	}
	/**
	 *  This method calculates the ionization potential of an atom.
	 *
	 *@param  container         Parameter is the atom container.
	 *@return                   The ionization potential
	 *@exception  CDKException  Description of the Exception
	 */
	public DescriptorValue calculate(IAtomContainer container) throws CDKException
	{
		double resultD = -1.0;
		IAtom atom = container.getAtomAt(targetPosition);
		if(atom.getSymbol().equals("F")||
				atom.getSymbol().equals("Cl")||
				atom.getSymbol().equals("Br")||
				atom.getSymbol().equals("I")||
				atom.getSymbol().equals("N")||
				atom.getSymbol().equals("S")||
				atom.getSymbol().equals("O")||
				atom.getSymbol().equals("P")){
			Double[][] resultsH = calculateHalogenDescriptor(container);
			J48WModel j48 = new J48WModel("data/arff/HeteroAtom1.arff");
    		String[] options = new String[1];
    		options[0] = "-U";
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
	 * Calculate the necessary descriptors for Halogens atoms
	 * @param atomContaine The IAtomContainer
	 * @return     Array with the values of the descriptors.
	 */
	private Double[][] calculateHalogenDescriptor(IAtomContainer atomContainer) {
		Double[][] results = new Double[1][3];
		Integer[] params = new Integer[1];
		Vector vector = new Vector();
		vector.add(new SigmaElectronegativityDescriptor());
		vector.add(new PartialSigmaChargeDescriptor());
		vector.add(new EffectiveAtomPolarizabilityDescriptor());
		params[0] = new Integer(targetPosition);
        try {
        	for(int i = 0; i < vector.size() ; i++){
        		IMolecularDescriptor descriptor = (IMolecularDescriptor)vector.get(i); 
        		descriptor.setParameters(params);
        		results[0][i]= new Double(((DoubleResult)descriptor.calculate(atomContainer).getValue()).doubleValue());
        	}
		} catch (CDKException e) {
			e.printStackTrace();
		}
		return results;
	}
	/**
	 *  Gets the parameterNames attribute of the IPDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "targetPosition";
		return params;
	}
	/**
	 *  Gets the parameterType attribute of the IPDescriptor object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return new Integer(0);
	}
}

