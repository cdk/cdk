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

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.bond.BondPartialSigmaChargeDescriptor;
import org.openscience.cdk.qsar.descriptors.bond.ResonancePositiveChargeDescriptor;
import org.openscience.cdk.qsar.model.weka.J48WModel;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;

import java.util.HashMap;
import java.util.Vector;

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
 *     <td>targetPosition</td>
 *     <td>0</td>
 *     <td>The position of the target atom</td>
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
public class IPAtomicDescriptor implements IMolecularDescriptor {
	/** Position of the target in the AtomContainer*/
	private int targetPosition = 0;
	/** Type of the target in the AtomContainer*/
	private String targetType = "";
	/** Target which edintify with type IAtom target*/
	public static String AtomicTarget = "AtomicTarget";
	/** Target which edintify with type IBond target*/
	public static String BondTarget = "BondTarget";
	
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
	 *  Sets the parameters attribute of the IPAtomicDescriptor object
	 *
	 *@param  params            The parameter is the position and type the target (IAtom or IBond)
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length != 2) {
			throw new CDKException("IPAtomicDescriptor expects two parameter. 1: the position and 2: type");
		}
		if (!(params[0] instanceof Integer)) {
			throw new CDKException("The parameter must be of type Integer");
		}
		if (!(params[1] instanceof String) /* && ( 
				params[1].equals(AtomicTarget) ||params[1].equals(BondTarget))*/) {
			throw new CDKException("The parameter must be of type String: BondTarget or AtomicTarget");
		}
		targetPosition = ((Integer) params[0]).intValue();
		targetType = (String) params[1];
	}
	/**
	 *  Gets the parameters attribute of the IPAtomicDescriptor object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		Object[] params = new Object[2];
		params[0] = new Integer(targetPosition);
		params[1] = new String(targetType);
		return params;
	}
	/**
	 *  This method calculates the ionization potential of an atom.
	 *
	 *@param  container         Parameter is the IAtomContainer.
	 *@return                   The ionization potential
	 *@exception  CDKException  Description of the Exception
	 */
	public DescriptorValue calculate(IAtomContainer container) throws CDKException
	{
		double resultD = -1.0;
		boolean isTarget = false;
		Double[][] resultsH = null;
		String path = "";
		if(targetType.equals(AtomicTarget)){
			IAtom atom = container.getAtomAt(targetPosition);
			if(atom.getSymbol().equals("F")||
					atom.getSymbol().equals("Cl")||
					atom.getSymbol().equals("Br")||
					atom.getSymbol().equals("I")||
					atom.getSymbol().equals("N")||
					atom.getSymbol().equals("S")||
					atom.getSymbol().equals("O")||
					atom.getSymbol().equals("P")){
				
				resultsH = calculateHeteroAtomDescriptor(container);
				path = "data/arff/HeteroAtom1.arff";
				isTarget = true;
			}
		}else if(targetType.equals(BondTarget)){
			IBond bond = container.getBondAt(targetPosition);
			IAtom[] atoms = bond.getAtoms();
			if((bond.getOrder() == 2)){
				if((atoms[0].getSymbol().equals("C")) && ( atoms[1].getSymbol().equals("C"))) {
					resultsH = calculatePiSystWithoutHeteroDescriptor(container);
					path = "data/arff/PySystWithoutHetero.arff";
					isTarget = true;
				}else {
					resultsH = calculateCarbonylDescriptor(container);
					path = "data/arff/Carbonyl1.arff";
					isTarget = true;
				}
			}
		}
		if(isTarget){
			J48WModel j48 = new J48WModel(path);
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
	 */
	private Double[][] calculateHeteroAtomDescriptor(IAtomContainer atomContainer) {
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
	 * Calculate the necessary descriptors for Carbonyl group
	 * @param atomContainer The IAtomContainer
	 * @return     Array with the values of the descriptors.
	 */
	private Double[][] calculateCarbonylDescriptor(IAtomContainer atomContainer) {
		Double[][] results = new Double[1][6];
		Integer[] params = new Integer[1];
		IBond bond = atomContainer.getBondAt(targetPosition);
		IAtom[] atoms = bond.getAtoms();
		int positionC = 0;
		int positionX = 0;
		if((atoms[0].getSymbol().equals("C") && !atoms[1].getSymbol().equals("C"))){
			positionC = atomContainer.getAtomNumber(atoms[0]);
			positionX = atomContainer.getAtomNumber(atoms[1]);
		}else if((atoms[1].getSymbol().equals("C") && !atoms[0].getSymbol().equals("C"))){
			positionC = atomContainer.getAtomNumber(atoms[1]);
			positionX = atomContainer.getAtomNumber(atoms[0]);
		}
		try {
        	/*0*/
        	IMolecularDescriptor descriptor = new SigmaElectronegativityDescriptor();
        	params[0] = new Integer(positionC);
            descriptor.setParameters(params);
    		results[0][0]= new Double(((DoubleResult)descriptor.calculate(atomContainer).getValue()).doubleValue());
        	/*1*/
    		descriptor = new PartialSigmaChargeDescriptor();
            descriptor.setParameters(params);
    		results[0][1]= new Double(((DoubleResult)descriptor.calculate(atomContainer).getValue()).doubleValue());
    		/*2*/
    		descriptor = new BondPartialSigmaChargeDescriptor();
    		params[0] = new Integer(targetPosition);
            descriptor.setParameters(params);
    		results[0][2]= new Double(((DoubleResult)descriptor.calculate(atomContainer).getValue()).doubleValue());
    		/*3*/
        	descriptor = new SigmaElectronegativityDescriptor();
        	params[0] = new Integer(positionX);
            descriptor.setParameters(params);
    		results[0][3]= new Double(((DoubleResult)descriptor.calculate(atomContainer).getValue()).doubleValue());
        	/*4*/
    		descriptor = new PartialSigmaChargeDescriptor();
            descriptor.setParameters(params);
    		results[0][4]= new Double(((DoubleResult)descriptor.calculate(atomContainer).getValue()).doubleValue());
    		/*5*/
			descriptor = new ResonancePositiveChargeDescriptor();
			params[0] = new Integer(targetPosition);
			descriptor.setParameters(params);
			DoubleArrayResult dar = ((DoubleArrayResult)descriptor.calculate(atomContainer).getValue());
			double datT = (dar.get(0)+dar.get(1))/2;
			results[0][5] = datT;
			
		} catch (CDKException e) {
			e.printStackTrace();
		}
		return results;
	}
	/**
	 * Calculate the necessary descriptors for pi systems without heteroatom
	 * 
	 * @param atomContainer The IAtomContainer
	 * @return     Array with the values of the descriptors.
	 */
	private Double[][] calculatePiSystWithoutHeteroDescriptor(IAtomContainer atomContainer) {
		Double[][] results = new Double[1][6];
		Integer[] params = new Integer[1];
		IBond bond = atomContainer.getBondAt(targetPosition);
		IAtom[] atoms = bond.getAtoms();
		int positionC = atomContainer.getAtomNumber(atoms[0]);
		int positionX = atomContainer.getAtomNumber(atoms[1]);
		try {
        	/*0_1*/
        	IMolecularDescriptor descriptor = new SigmaElectronegativityDescriptor();
        	params[0] = new Integer(positionC);
            descriptor.setParameters(params);
    		results[0][0]= new Double(((DoubleResult)descriptor.calculate(atomContainer).getValue()).doubleValue());
        	/*1_1*/
    		descriptor = new PartialSigmaChargeDescriptor();
            descriptor.setParameters(params);
    		results[0][1]= new Double(((DoubleResult)descriptor.calculate(atomContainer).getValue()).doubleValue());
    		
    		/*0_2*/
        	descriptor = new SigmaElectronegativityDescriptor();
        	params[0] = new Integer(positionX);
            descriptor.setParameters(params);
    		results[0][2]= new Double(((DoubleResult)descriptor.calculate(atomContainer).getValue()).doubleValue());
        	/*1_2*/
    		descriptor = new PartialSigmaChargeDescriptor();
            descriptor.setParameters(params);
    		results[0][3]= new Double(((DoubleResult)descriptor.calculate(atomContainer).getValue()).doubleValue());
    		
    		/*  */
			descriptor = new ResonancePositiveChargeDescriptor();
			params[0] = new Integer(targetPosition);
			descriptor.setParameters(params);
			DoubleArrayResult dar = ((DoubleArrayResult)descriptor.calculate(atomContainer).getValue());
			results[0][4] = dar.get(0);
			results[0][5] = dar.get(1);
    		
    		
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

