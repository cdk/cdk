/* $Revision: 6228 $ $Author: egonw $ $Date: 2006-05-11 18:34:42 +0200 (Thu, 11 May 2006) $
 *
 * Copyright (C) 2006  Miguel Rojas <miguel.rojas@uni-koeln.de>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.bond;

import java.util.HashMap;
import java.util.Iterator;

import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.invariant.ConjugatedPiSystemsDetector;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IBondDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.PartialPiChargeDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.PartialSigmaChargeDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.SigmaElectronegativityDescriptor;
import org.openscience.cdk.qsar.model.weka.J48WModel;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.ElectronImpactPDBReaction;

/**
 *  This class returns the ionization potential of a bond (double or triple). It is
 *  based in learning machine (in this case J48 see J48WModel) 
 *  from experimental values (NIST data). Up to now is
 *  only possible predict for double- or triple bonds and they are not belong to
 *  conjugated system or not adjacent to an heteroatom.
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
 * @author           Miguel Rojas
 * @cdk.created      2006-05-26
 * @cdk.license      GPL
 * @cdk.module       qsar-weka
 * @cdk.set          qsar-descriptors
 * @cdk.dictref      qsar-descriptors:ionizationPotential
 * @cdk.depends      weka.jar
 * @cdk.builddepends weka.jar
 * @see J48WModel
 */
public class IPBondDescriptor implements IBondDescriptor {
	
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

	/** parameter for inizate IReactionSet*/
	private boolean setEnergy = false;
	private IReactionSet reactionSet;

	/**
	 *  Constructor for the IPBondDescriptor object
	 */
	public IPBondDescriptor() {
		this.hash = new HashMap();
		double value = 5.05;
		for(int i = 0 ; i < classAttrib.length ; i++){
			this.hash.put(classAttrib[i],new Double(value));
			value += 0.1;
		}
	}
	/**
	 *  Gets the specification attribute of the IPBondDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#ionizationPotential",
				this.getClass().getName(),
				"$Id: IPBondDescriptor.java 6171 2006-5-22 19:29:58Z egonw $",
				"The Chemistry Development Kit");
	}

    /**
     * This descriptor does have any parameter.
     */
    public void setParameters(Object[] params) throws CDKException {
    }


    /**
     *  Gets the parameters attribute of the IPBondDescriptor object.
     *
     *@return    The parameters value
     * @see #setParameters
     */
    public Object[] getParameters() {
        return new Object[0];
    }
	/**
	 *  This method calculates the ionization potential of a bond.
	 *
	 *@param  container         Parameter is the IAtomContainer.
	 *@return                   The ionization potential
	 *@exception  CDKException  Description of the Exception
	 */
	public DescriptorValue calculate(IBond bond, IAtomContainer container) throws CDKException{
		reactionSet = container.getBuilder().newReactionSet();
    	double resultD = -1.0;
		boolean isTarget = false;
		Double[][] resultsH = null;
		String path = "";
		
		try{
			HueckelAromaticityDetector.detectAromaticity(container,true);
		} catch (Exception exc)
		{
		}
        
        if(bond.getOrder() > 1 && bond.getAtom(0).getSymbol().equals("C") && 
    				bond.getAtom(1).getSymbol().equals("C")){
        		
        		AtomContainerSet conjugatedPi = ConjugatedPiSystemsDetector.detect(container);
                Iterator acI = conjugatedPi.atomContainers();
        		boolean isConjugatedPi = false;
        		boolean isConjugatedPi_withHeteroatom = false;
                while(acI.hasNext()){
        			IAtomContainer ac = (IAtomContainer) acI.next();
        			if(ac.contains(bond)){
        				isConjugatedPi = true;
            			isTarget = true;
            			
            			Iterator atoms = ac.atoms();
            			while(atoms.hasNext()){
            				IAtom atomsss = (IAtom) atoms.next();
            				
            				if(!atomsss.getSymbol().equals("C")){
            					isConjugatedPi_withHeteroatom = true;
            					resultsH = calculateCojugatedPiSystWithHeteroDescriptor(bond, container, ac);
                    			path = "data/arff/PySystWithHetero.arff";
                    			break;
            				}
            			}
            			
            			if(!isConjugatedPi_withHeteroatom){
	            			resultsH = calculateCojugatedPiSystWithoutHeteroDescriptor(bond, container, ac);
	            			path = "data/arff/ConjugatedPiSys.arff";
	            			break;
            			}
        			}
        		}
        		
                if(!isConjugatedPi){

					resultsH = calculatePiSystWithoutHeteroDescriptor(bond, container);
					path = "data/arff/Acetyl_EthylWithoutHetero.arff";
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
    		
    		
    		/* extract reaction*/
    		if(setEnergy){
    			IMoleculeSet setOfReactants = container.getBuilder().newMoleculeSet();
    			setOfReactants.addMolecule((IMolecule) container);
    			IReactionProcess type  = new ElectronImpactPDBReaction();
    			bond.setFlag(CDKConstants.REACTIVE_CENTER,true);
    	        Object[] params = {Boolean.TRUE};
    	        type.setParameters(params);
    	        IReactionSet pbb = type.initiate(setOfReactants, null);
    	        Iterator it = pbb.reactions();
    	        while(it.hasNext()){
    	        	IReaction reaction = (IReaction)it.next();
    	        	reaction.setProperty("IonizationEnergy", new Double(resultD));
    	        	reactionSet.addReaction(reaction);
    	        }
    		}
		}
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(resultD));
	}

	/**
	 * This method calculates the ionization potential of a bond and set the ionization
	 * energy into each reaction as property
	 * 
	 * @return The IReactionSet value
	 */
	public IReactionSet getReactionSet(IBond bond, IAtomContainer container) throws CDKException{
		setEnergy = true;
		calculate(bond,container);
		return reactionSet;
	}
	
	/**
	 * Calculate the necessary descriptors for pi systems without heteroatom
	 * 
	 * @param atomContainer The IAtomContainer
	 * @return     Array with the values of the descriptors.
	 */
	private Double[][] calculatePiSystWithoutHeteroDescriptor(IBond bond, IAtomContainer atomContainer) {
		Double[][] results = new Double[1][6];
		Integer[] params = new Integer[1];
		IAtom positionC = bond.getAtom(0);
		IAtom positionX = bond.getAtom(1);
		try {
        	/*0_1*/
			SigmaElectronegativityDescriptor descriptor1 = new SigmaElectronegativityDescriptor();
    		results[0][0]= new Double(((DoubleResult)descriptor1.calculate(positionC, atomContainer).getValue()).doubleValue());
        	/*1_1*/
    		PartialSigmaChargeDescriptor descriptor2 = new PartialSigmaChargeDescriptor();
    		results[0][1]= new Double(((DoubleResult)descriptor2.calculate(positionC, atomContainer).getValue()).doubleValue());
    		
    		/*0_2*/
    		SigmaElectronegativityDescriptor descriptor3 = new SigmaElectronegativityDescriptor();
    		results[0][2]= new Double(((DoubleResult)descriptor3.calculate(positionX, atomContainer).getValue()).doubleValue());
        	/*1_2*/
    		PartialSigmaChargeDescriptor descriptor4 = new PartialSigmaChargeDescriptor();
    		results[0][3]= new Double(((DoubleResult)descriptor4.calculate(positionX, atomContainer).getValue()).doubleValue());
    		
    		/*  */
    		ResonancePositiveChargeDescriptor descriptor5 = new ResonancePositiveChargeDescriptor();
			DoubleArrayResult dar = ((DoubleArrayResult)descriptor5.calculate(bond, atomContainer).getValue());
			results[0][4] = new Double(dar.get(0));
			results[0][5] = new Double(dar.get(1));
    		
    		
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
	private Double[][] calculateCojugatedPiSystWithoutHeteroDescriptor(IBond bond, IAtomContainer atomContainer, IAtomContainer conjugatedSys) {
		Double[][] results = new Double[1][3];
		
		results[0][0] = new Double(0.0);
		results[0][1] = new Double(0.0);
		results[0][2] = new Double(0.0);
		Iterator atomIt = conjugatedSys.atoms();
		while(atomIt.hasNext()){
			IAtom atomsss = (IAtom) atomIt.next();
			
			PartialPiChargeDescriptor descriptor1 = new PartialPiChargeDescriptor();
			double result1;
			try {
				result1 = ((DoubleResult)descriptor1.calculate(atomsss,atomContainer).getValue()).doubleValue();
				if(result1 > results[0][0])
					results[0][0] = result1;
				
				SigmaElectronegativityDescriptor descriptor2 = new SigmaElectronegativityDescriptor();
				double result2 = ((DoubleResult)descriptor2.calculate(atomsss,atomContainer).getValue()).doubleValue();
				results[0][2] += result2;
				
			} catch (CDKException e) {
				e.printStackTrace();
			}
			
			
		}
		
		Iterator bondIt = conjugatedSys.bonds();
		while(bondIt.hasNext()){
			
			
			IBond bondsss = (IBond) bondIt.next();
			
			try {
				ResonancePositiveChargeDescriptor descriptor5 = new ResonancePositiveChargeDescriptor();
				DoubleArrayResult dar = ((DoubleArrayResult)descriptor5.calculate(bondsss,atomContainer).getValue());
				double result1 = dar.get(0);
				double resutt2 = dar.get(1);
				double result12 = (result1+resutt2);
				
				double resultT = 0;
				if(result12 != 0)
					resultT = result12/2;
				
				results[0][1] += resultT;
			
			} catch (CDKException e) {
				e.printStackTrace();
			}
		}
		if(results[0][1] != 0)
			results[0][1] = results[0][1]/conjugatedSys.getAtomCount();
		
		if(results[0][2] != 0)
			results[0][2] = results[0][2]/conjugatedSys.getAtomCount();
		
		return results;
	}
	/**
	 * Calculate the necessary descriptors for pi systems without heteroatom
	 * 
	 * @param atomContainer The IAtomContainer
	 * @return     Array with the values of the descriptors.
	 */
	private Double[][] calculateCojugatedPiSystWithHeteroDescriptor(IBond bond, IAtomContainer atomContainer, IAtomContainer conjugatedSys) {
		Double[][] results = new Double[1][4];
		
		results[0][0] = new Double(0.0);
		results[0][1] = new Double(0.0);
		results[0][2] = new Double(0.0);
		results[0][3] = new Double(0.0);
		Iterator atomIt = conjugatedSys.atoms();
		while(atomIt.hasNext()){
			IAtom atomsss = (IAtom) atomIt.next();
			
			try {
			
				if(atomsss.getSymbol().equals("C")){
					PartialPiChargeDescriptor descriptor1 = new PartialPiChargeDescriptor();
					double result1 = ((DoubleResult)descriptor1.calculate(atomsss,atomContainer).getValue()).doubleValue();
					if(result1 > results[0][0])
						results[0][0] = result1;
				}else{
					
					PartialPiChargeDescriptor descriptor1 = new PartialPiChargeDescriptor();
					double result1 = ((DoubleResult)descriptor1.calculate(atomsss,atomContainer).getValue()).doubleValue();
					results[0][1] = result1;
				}
				
				SigmaElectronegativityDescriptor descriptor2 = new SigmaElectronegativityDescriptor();
				double result2 = ((DoubleResult)descriptor2.calculate(atomsss,atomContainer).getValue()).doubleValue();
				results[0][3] += result2;
			
			} catch (CDKException e) {
				e.printStackTrace();
			}
			
		}
		
		Iterator bondIt = conjugatedSys.bonds();
		while(bondIt.hasNext()){
			IBond bondsss = (IBond) bondIt.next();
			try {
				
				ResonancePositiveChargeDescriptor descriptor5 = new ResonancePositiveChargeDescriptor();
				DoubleArrayResult dar = ((DoubleArrayResult)descriptor5.calculate(bondsss,atomContainer).getValue());
				double result1 = dar.get(0);
				double resutt2 = dar.get(1);
				double result12 = (result1+resutt2);
				
				double resultT = 0;
				if(result12 != 0)
					resultT = result12/2;
				
				results[0][2] += resultT;
			
			} catch (CDKException e) {
				e.printStackTrace();
			}
		}
		if(results[0][2] != 0)
			results[0][2] = results[0][1]/conjugatedSys.getAtomCount();
		
		if(results[0][3] != 0)
			results[0][3] = results[0][2]/conjugatedSys.getAtomCount();
		
		return results;
	}
	 /**
     * Gets the parameterNames attribute of the IPBondDescriptor object.
     *
     * @return    The parameterNames value
     */
    public String[] getParameterNames() {
        return new String[0];
    }


    /**
     * Gets the parameterType attribute of the IPBondDescriptor object.
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    public Object getParameterType(String name) {
        return null;
    }
}

