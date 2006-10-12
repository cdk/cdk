/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) $
 *  $Revision: 5855 $
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
package org.openscience.cdk.qsar.descriptors.bond;

import java.util.ArrayList;
import java.util.Iterator;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.charges.GasteigerPEPEPartialCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.BondsToAtomDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.PiElectronegativityDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.reaction.type.BreakingBondReaction;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.MFAnalyser;
import org.openscience.cdk.tools.StructureResonanceGenerator;
import org.openscience.cdk.tools.manipulator.BondManipulator;

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
public class ResonancePositiveChargeDescriptor implements IMolecularDescriptor {

    private int bondPosition = 0;
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
     *  Sets the parameters attribute of the ResonancePositiveChargeDescriptor
     *  object
     *
     *@param  params            Bond position
     *@exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1) {
            throw new CDKException("ResonancePositiveChargeDescriptor only expects one parameter");
        }
        if (!(params[0] instanceof Integer)) {
            throw new CDKException("The parameter 1 must be of type Integer");
        }
        bondPosition = ((Integer) params[0]).intValue();
    }


    /**
     *  Gets the parameters attribute of the ResonancePositiveChargeDescriptor object
     *
     *@return    The parameters value
     */
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = new Integer(bondPosition);
        return params;
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
    public DescriptorValue calculate(IAtomContainer acI) throws CDKException {
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
    	IAtom[] atoms = BondManipulator.getAtomArray(ac.getBond(bondPosition));
    	

        /* RESTRICTION: only possible to break H or doble bonds*/
//    	if(ac.getBond(bondPosition).getOrder() < 2)
//    		if(!atoms[0].getSymbol().equals("H") && !atoms[1].getSymbol().equals("H")){
//				DoubleArrayResult dar = new DoubleArrayResult();
//				dar.add(0.0);dar.add(0.0);
//				System.out.println("return 0.0");
//    			return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),dar);
//    		}
    	
    	/*break bond*/
    	if(ac.getSingleElectronSum(atoms[0]) > 0 || ac.getSingleElectronSum(atoms[1]) > 0){
    		dar.add(0.0);
    		dar.add(0.0);
    		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),dar);
    	}
    	BreakingBondReaction type = new BreakingBondReaction();
    	atoms[0].setFlag(CDKConstants.REACTIVE_CENTER,true);
    	int atomPos0 = ac.getAtomNumber(atoms[0]);
    	atoms[1].setFlag(CDKConstants.REACTIVE_CENTER,true);
    	int atomPos1 = ac.getAtomNumber(atoms[1]);
    	ac.getBond(bondPosition).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
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
	        	if(product.getAtomCount() < 2)
	        		continue;
	        	
	        	StructureResonanceGenerator gRI = new StructureResonanceGenerator(true,true,false,false,true,false);
	    		IAtomContainerSet setOfResonance = gRI.getAllStructures(product);
	    		if(setOfResonance.getAtomContainerCount() == 1)
	    			continue;
	    		
    			int positionAC = 0;
    			
    			if(product.getAtom(atomPos0) != null && product.getAtom(atomPos0).getFormalCharge() > 0){
    				positionAC = atomPos0;
    			}else{
    	        	positionAC = atomPos1;
    			}

    			if(setOfResonance.getAtomContainerCount() > 1){
    				outRes:
	    			for(int j = 1 ; j < setOfResonance.getAtomContainerCount() ; j++){
	    				IAtomContainer prod = setOfResonance.getAtomContainer(j);
	    				 QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(prod);
	    				 if(!UniversalIsomorphismTester.isIsomorph(ac,qAC)){
	    					 /*search positive charge*/
	    					 for(int k = 0; k < prod.getAtomCount(); k++){
	    						 if(prod.getAtomCount() < 2)
	    							 continue;
	    						 IAtom atomsP = prod.getAtom(k);
	    						 if(product.getAtom(k).getFormalCharge() !=
	    							 atomsP.getFormalCharge() /*> 0*/)
	    						 if(atomsP.getFormalCharge()== 1 || product.getAtom(k).getFormalCharge() == 1){
		    						 DoubleResult electroneg = new DoubleResult(0.0); 
	    						    Integer[] params = new Integer[1];
	    	    					params[0] = new Integer(6);
	    	    			    	pielectronegativity = new PiElectronegativityDescriptor();
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
     *  Gets the parameterNames attribute of the ResonancePositiveChargeDescriptor
     *  object
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "bondPosition";
        return params;
    }


    /**
     *  Gets the parameterType attribute of the ResonancePositiveChargeDescriptor
     *  object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
    	Integer[] object = {new Integer(0), new Integer(0)};
        return object;
    }
}

