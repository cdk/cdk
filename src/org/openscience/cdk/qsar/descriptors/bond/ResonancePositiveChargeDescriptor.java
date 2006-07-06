/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) $
 *  $Revision: 5855 $
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
package org.openscience.cdk.qsar.descriptors.bond;

import java.util.ArrayList;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.ISetOfAtomContainers;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.interfaces.ISetOfReactions;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.charges.GasteigerPEPEPartialCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.descriptors.atomic.BondsToAtomDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.PiElectronegativityDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.reaction.type.BreakingBondReaction;
import org.openscience.cdk.tools.HydrogenAdder;
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
 *     <td>atomPosition</td>
 *     <td>0</td>
 *     <td>The position of the target atom</td>
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
	private BreakingBondReaction bbr;


    /**
     *  Constructor for the ResonancePositiveChargeDescriptor object
     */
    public ResonancePositiveChargeDescriptor() { 
    	bbr = new BreakingBondReaction();
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
    	IAtomContainer ac;
		try {
			ac = (IMolecule) acI.clone();
		} catch (CloneNotSupportedException e) {
			throw new CDKException("Could not clone IAtomContainer!", e);
		}
    	int positionCharge = 0;
    	ArrayList result = new ArrayList();
    	ArrayList distance = new ArrayList();
    	IAtom[] atoms = ac.getBondAt(bondPosition).getAtoms();
    	
    	/*break bond*/
    	BreakingBondReaction type = new BreakingBondReaction();
    	atoms[0].setFlag(CDKConstants.REACTIVE_CENTER,true);
    	atoms[1].setFlag(CDKConstants.REACTIVE_CENTER,true);
    	ac.getBondAt(bondPosition).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
        Object[] paramsR = {Boolean.TRUE};
        type.setParameters(paramsR);
        
        
        ISetOfMolecules setOfReactants = ac.getBuilder().newSetOfMolecules();
		setOfReactants.addMolecule((IMolecule) ac);
        ISetOfReactions setOfReactions = type.initiate(setOfReactants, null);
        
        /*search resonance for each product obtained*/
        for(int i = 0 ; i < setOfReactions.getReactionCount(); i++){
        	IAtomContainer product = setOfReactions.getReaction(i).getProducts().getAtomContainer(0);
        	StructureResonanceGenerator gRI = new StructureResonanceGenerator(true,false,false,false,false);
    		ISetOfAtomContainers setOfMolecules = gRI.getStructures(product);
    		if(setOfMolecules.getAtomContainerCount() > 1){
    			int positionAC = 0;
    			for(int j = 0; j < product.getAtomCount(); j++){
					 IAtom atomsPr = product.getAtomAt(j);
					 if(atomsPr.getFormalCharge() > 0){
						 positionAC = j;
						 if(atomsPr.equals(atoms[0]))
								 positionCharge = 0;
						 else if(atomsPr.equals(atoms[1]))
							 positionCharge = 1;
    				}
				}
    			for(int j = 0 ; j < setOfMolecules.getAtomContainerCount() ; j++){
    				IAtomContainer prod = setOfMolecules.getAtomContainer(j);
    				HydrogenAdder hAdder = new HydrogenAdder();
    				hAdder.addImplicitHydrogensToSatisfyValency((IMolecule) prod);
    				 QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(prod);
    				 if(!UniversalIsomorphismTester.isIsomorph(ac,qAC)){
    					 /*search positive charge*/
    					 for(int k = 0; k < prod.getAtomCount(); k++){
    						 IAtom atomsP = prod.getAtomAt(k);
    						 if(atomsP.getFormalCharge() > 0){
    						    Integer[] params = new Integer[1];
    	    					params[0] = new Integer(6);
    	    			    	pielectronegativity = new PiElectronegativityDescriptor();
    	    					pielectronegativity.setParameters(params);
    	    					pielectronegativity.calculate(atomsP,prod).getValue();
    	    					IAtom target = prod.getAtomAt(prod.getAtomNumber(atomsP));
    	    			        result.add(new DoubleResult(atomsP.getCharge()));
    	    			        
    	    			        BondsToAtomDescriptor descriptor   = new BondsToAtomDescriptor();
    	    			        Object[] paramsD = {new Integer(prod.getAtomNumber(atomsP))};
    	    			        descriptor.setParameters(paramsD);
    	    			        
    	    			        distance.add(((IntegerResult)descriptor.calculate(prod.getAtomAt(positionAC),prod).getValue()));
    	    			        
    	    					break;
    						 }
    					 }
    					 
    				 }
    			}
    		}
        }
        /*logarithm*/
        
        double value = 0.0;
        double sum = 0.0;
        for(int i = 0 ; i < result.size() ; i++){
        	double suM = ((DoubleResult)result.get(i)).doubleValue();
        	if(suM < 0)
        		suM = -1*suM;
        	sum += suM*Math.pow(0.67,((IntegerResult)distance.get(i)).intValue());
        }
        value = 2.663/sum;
        DoubleArrayResult dar = new DoubleArrayResult(2);
        if(result.size() > 0){
        	if(positionCharge == 0 ){
            	dar.add(value);
                dar.add(0.0);
            }
            else {
            	dar.add(0.0);
            	dar.add(value);
            }
        }else{
        	dar.add(0.0);
        	dar.add(0.0);
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

