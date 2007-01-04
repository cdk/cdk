/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) $
 *  $Revision: 5855 $
 *
 *  Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.qsar.descriptors.bond;

import java.util.ArrayList;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.charges.GasteigerPEPEPartialCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IBondDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.BondsToAtomDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.PiElectronegativityDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.reaction.type.BreakingBondReaction;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.tools.StructureResonanceGenerator;

/**
 *  <p>The calculation of Resonance stabilization of a positive charge of an heavy 
 *  atom is based on Gasteiger H.Saller.</p>
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>bondPosition</td>
 *     <td>0</td>
 *     <td>The position of the target bond</td>
 *   </tr>
 * </table>
 *
 *
 * @author      Miguel Rojas
 * @cdk.created 2006-04-15
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:resonancePositiveCharge
 * @see GasteigerPEPEPartialCharges
 */
public class ResonancePositiveChargeDescriptor implements IBondDescriptor {

    private PiElectronegativityDescriptor pielectronegativity = null;


    /**
     *  Constructor for the ResonancePositiveChargeDescriptor object
     */
    public ResonancePositiveChargeDescriptor() { 
    }


    /**
     *  Gets the specification attribute of the ResonancePositiveChargeDescriptor
     *  object
     *
     *@return    The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#resonancePositiveCharge",
            this.getClass().getName(),
            "$Id: ResonancePositiveChargeDescriptor.java 5855 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) egonw $",
            "The Chemistry Development Kit");
    }


    /**
     * This descriptor does have any parameter.
     */
    public void setParameters(Object[] params) throws CDKException {
    }


    /**
     *  Gets the parameters attribute of the VdWRadiusDescriptor object.
     *
     *@return    The parameters value
     * @see #setParameters
     */
    public Object[] getParameters() {
        return new Object[0];
    }


    /**
     *  The method returns Resonance stabilization value assigned to an heavy atom through Gasteiger Saller
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *  
     *
     *@param  acI                AtomContainer
     *@return                   Value of the Resonance stabilization of the two atoms
     *							which belong to the bond.
     *@exception  CDKException  Possible Exceptions
     */
    public DescriptorValue calculate(IBond bond, IAtomContainer acI) throws CDKException {
    	
    	cleanFlagReactiveCenter((IMolecule) acI);

    	DoubleArrayResult dar = new DoubleArrayResult(2);
    	IAtomContainer ac;
		try {
			ac = (IMolecule) acI.clone();
		} catch (CloneNotSupportedException e) {
			throw new CDKException("Could not clone IAtomContainer!", e);
		}

		ArrayList result1 = new ArrayList();
    	ArrayList distance1 = new ArrayList();
    	ArrayList result2 = new ArrayList();
    	ArrayList distance2 = new ArrayList();
    	
    	
    	IAtom atom0 = bond.getAtom(0);
    	int atomPos0 = acI.getAtomNumber(atom0);
    	IAtom atom1 = bond.getAtom(1);
    	int atomPos1 = acI.getAtomNumber(atom1);
    	
    	ac.getAtom(atomPos0).setFlag(CDKConstants.REACTIVE_CENTER,true);
    	ac.getAtom(atomPos1).setFlag(CDKConstants.REACTIVE_CENTER,true);
    	ac.getBond(acI.getBondNumber(bond)).setFlag(CDKConstants.REACTIVE_CENTER,true);
		

    	/*break bond*/
    	if(ac.getConnectedSingleElectronsCount(atom0) > 0 || ac.getConnectedSingleElectronsCount(atom1) > 0){
    		dar.add(0.0);
    		dar.add(0.0);
    		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),dar);
    	}
    	
    	BreakingBondReaction type = new BreakingBondReaction();
    	
        Object[] paramsR = {Boolean.TRUE};
        type.setParameters(paramsR);
        
        IMoleculeSet setOfReactants = ac.getBuilder().newMoleculeSet();
		setOfReactants.addMolecule((IMolecule) ac);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
    	
        /*search resonance for each product obtained. Only 2*/
        for(int i = 0 ; i < 2; i++){
        	if(setOfReactions.getReaction(i) == null)
        		continue;
			for(int z = 0; z < setOfReactions.getReaction(i).getProducts().getAtomContainerCount(); z++){
	        	IAtomContainer product = setOfReactions.getReaction(i).getProducts().getAtomContainer(z);

	        	int positionAC = 0;
    			IMapping mapping = (IMapping)setOfReactions.getReaction(i).getMapping(0);
    			IAtom mappedProductA1 = (IAtom)mapping.getChemObject(1);
    			if(!product.contains(mappedProductA1)){
    				mapping = (IMapping)setOfReactions.getReaction(i).getMapping(1);
    				mappedProductA1 = (IAtom)mapping.getChemObject(1);
    			}
    			if(mappedProductA1.getFormalCharge() > 0){
    				positionAC = product.getAtomNumber(mappedProductA1);
    			}else{
    				if(setOfReactions.getReaction(i).getProducts().getAtomContainerCount() == 1)
    					positionAC =  product.getAtomNumber((IAtom)((IMapping)setOfReactions.getReaction(i).getMapping(1)).getChemObject(1));
    				else
    					continue;
    			}
    			
	        	
	        	if(product.getAtomCount() < 2)
	        		continue;
	        	
	        	int maxNumbStruc = 50;
	        	boolean isAromatic = false;
	        	if(HueckelAromaticityDetector.detectAromaticity(acI)){
	        		 isAromatic = true;
	        		 IRingSet ringSet = new SSSRFinder(product).findSSSR();
	        		 if( ringSet.getAtomContainerCount() > 4)
						maxNumbStruc = 1;
	        		 else
	        			 maxNumbStruc = 5;
	        	}
	        	StructureResonanceGenerator gRI = new StructureResonanceGenerator(true,true,false,false,true,false,maxNumbStruc);
	        	
	        	IAtomContainerSet setOfResonance = gRI.getAllStructures(product);
	    		if(setOfResonance.getAtomContainerCount() == 1)
	    			continue;

		    	if(setOfResonance.getAtomContainerCount() > 1){
	    			for(int j = 1 ; j < setOfResonance.getAtomContainerCount() ; j++){
	    				IAtomContainer prod = setOfResonance.getAtomContainer(j);
	    	        	QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(prod);
	    				 if(!UniversalIsomorphismTester.isIsomorph(ac,qAC)){
	    			        	/*search positive charge*/
	    					 pielectronegativity = new PiElectronegativityDescriptor();
	    		    			for(int k = 0; k < prod.getAtomCount(); k++){
	    						 if(prod.getAtomCount() < 2)
	    							 continue;
	    						 IAtom atomsP = prod.getAtom(k);
	    						 if(product.getAtom(k).getFormalCharge() !=
	    							 atomsP.getFormalCharge() /*> 0*/)
	    						 if(atomsP.getFormalCharge()== 1 || product.getAtom(k).getFormalCharge() == 1){
		    						 DoubleResult electroneg = new DoubleResult(0.0); 
	    						    Object[] params = new Integer[2];
	    	    					params[0] = new Integer(6);
	    	    					if(isAromatic)
		    	    					params[1] = new Integer(maxNumbStruc);
	    	    						
	    	    					pielectronegativity.setParameters(params);
	    	    					try{
	    	    						electroneg = (DoubleResult)pielectronegativity.calculate(atomsP,prod).getValue();
	    	    					} catch (Exception ex1) {
	    	    						continue;
	    	    					}
	    	    			        if(i == 0)result1.add(electroneg);
	    	    			        else result2.add(electroneg);
	    	    			        BondsToAtomDescriptor descriptor   = new BondsToAtomDescriptor();
	    	    			        Object[] paramsD = {new Integer(prod.getAtomNumber(atomsP))};
	    	    			        descriptor.setParameters(paramsD);
	    	    			        IntegerResult dis = ((IntegerResult)descriptor.calculate(prod.getAtom(positionAC),prod).getValue());
	    	    			        
	    	    			        if(i == 0)distance1.add(dis);
	    	    			        else distance2.add(dis);
	    	    					
	    	    			        break;
	    						 }
	    					 }
	    				 }
	    			}
    			}
    		}
        }
        /*logarithm*/
        
        double value = 0.0;
        double sum = 0.0;
        for(int i = 0 ; i < result1.size() ; i++){
        	double suM = ((DoubleResult)result1.get(i)).doubleValue();
        	if(suM < 0)
        		suM = -1*suM;
        	sum += suM*Math.pow(0.67,((IntegerResult)distance1.get(i)).intValue());
        }
        value = 26.63/sum;
        if(result1.size() > 0){
            dar.add(value);
        }else{
        	dar.add(0.0);
        }
        value = 0.0;
        sum = 0.0;
        for(int i = 0 ; i < result2.size() ; i++){
        	double suM = ((DoubleResult)result2.get(i)).doubleValue();
        	if(suM < 0)
        		suM = -1*suM;
        	sum += suM*Math.pow(0.67,((IntegerResult)distance2.get(i)).intValue());
        }
        value = 26.63/sum;
        if(result2.size() > 0){
            dar.add(value);
        }else{
        	dar.add(0.0);
        }
        
        /*put first the atom which is smaller*/
        int p0 = atomPos0;
        int p1 = atomPos1;
        if(p0 > p1){
        	double o1 = dar.get(0);
        	double o0 = dar.get(1);
        	dar = new DoubleArrayResult(2);
        	dar.add(o0);
        	dar.add(o1);
        }
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),dar);
    }



	 /**
    * Gets the parameterNames attribute of the ResonancePositiveChargeDescriptor object.
    *
    * @return    The parameterNames value
    */
   public String[] getParameterNames() {
       return new String[0];
   }


   /**
    * Gets the parameterType attribute of the ResonancePositiveChargeDescriptor object.
    *
    * @param  name  Description of the Parameter
    * @return       An Object of class equal to that of the parameter being requested
    */
   public Object getParameterType(String name) {
       return null;
   }
    /**
     * clean the flags CDKConstants.REACTIVE_CENTER from the molecule
     * 
	 * @param mol
	 */
	public void cleanFlagReactiveCenter(IMolecule molecule){
		for(int j = 0 ; j < molecule.getAtomCount(); j++)
			molecule.getAtom(j).setFlag(CDKConstants.REACTIVE_CENTER, false);
		for(int j = 0 ; j < molecule.getBondCount(); j++)
			molecule.getBond(j).setFlag(CDKConstants.REACTIVE_CENTER, false);
	}
}

