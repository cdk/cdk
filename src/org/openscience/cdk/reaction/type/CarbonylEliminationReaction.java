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
package org.openscience.cdk.reaction.type;

import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.LonePair;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.tools.LoggingTool;

/**
 * <p>IReactionProcess which participate mass spectrum process.  
 * This reaction could be represented as RC-C#[O+] => R[C] + |C#[O+]</p>
 * <p>Make sure that the molecule has the corresponend lone pair electrons
 * for each atom. You can use the method: <pre> LonePairElectronChecker </pre>
 * 
 * <pre>
 *  IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new CarbonylEliminationReaction();
 *  Object[] params = {Boolean.FALSE};
    type.setParameters(params);
 *  IReactionSet setOfReactions = type.initiate(setOfReactants, null);
 *  </pre>
 * 
 * <p>We have the possibility to localize the reactive center. Good method if you
 * want to localize the reaction in a fixed point</p>
 * <pre>atoms[0].setFlag(CDKConstants.REACTIVE_CENTER,true);</pre>
 * <p>Moreover you must put the parameter Boolean.TRUE</p>
 * <p>If the reactive center is not localized then the reaction process will
 * try to find automatically the posible reactive center.</p>
 * 
 * 
 * @author         Miguel Rojas
 * 
 * @cdk.created    2006-10-16
 * @cdk.module     reaction
 * @cdk.set        reaction-types
 * 
 **/
public class CarbonylEliminationReaction implements IReactionProcess{
	private LoggingTool logger;
	private boolean hasActiveCenter;
	
	/**
	 * Constructor of the CarbonylEliminationReaction object
	 *
	 */
	public CarbonylEliminationReaction(){
		logger = new LoggingTool(this);
	}
	/**
	 *  Gets the specification attribute of the CarbonylEliminationReaction object
	 *
	 *@return    The specification value
	 */
	public ReactionSpecification getSpecification() {
		return new ReactionSpecification(
				"http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#CarbonylEliminationReaction",
				this.getClass().getName(),
				"$Id: RadicalSiteInitiationReaction.java,v 1.6 2006/04/01 08:26:47 mrc Exp $",
				"The Chemistry Development Kit");
	}
	
	/**
	 *  Sets the parameters attribute of the CarbonylEliminationReaction object
	 *
	 *@param  params            The parameter is if the molecule has already fixed the center active or not. It 
	 *							should be set before to inize the reaction with a setFlag:  CDKConstants.REACTIVE_CENTER
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("CarbonylEliminationReaction only expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The parameter 1 must be of type boolean");
		}
		hasActiveCenter = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the CarbonylEliminationReaction object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		Object[] params = new Object[1];
		params[0] = new Boolean (hasActiveCenter);
		return params;
	}
	
	/**
	 *  Initiate process.
	 *
	 *@param  reactants         reactants of the reaction.
	 *@param  agents            agents of the reaction (Must be in this case null).
	 *
	 *@exception  CDKException  Description of the Exception
	 */
	public IReactionSet initiate(IMoleculeSet reactants, IMoleculeSet agents) throws CDKException{
		logger.debug("initiate reaction: CarbonylEliminationReaction");
		
		if (reactants.getMoleculeCount() != 1) {
			throw new CDKException("CarbonylEliminationReaction only expects one reactant");
		}
		if (agents != null) {
			throw new CDKException("CarbonylEliminationReaction don't expects agents");
		}
		
		IReactionSet setOfReactions = reactants.getBuilder().newReactionSet();
		IMolecule reactant = reactants.getMolecule(0);

		/* if the parameter hasActiveCenter is not fixed yet, set the active centers*/
		if(!hasActiveCenter){
			setActiveCenters(reactant);
		}
		
		for(int i = 0 ; i < reactant.getBondCount() ; i++) {
			IBond bond = reactant.getBond(i);
			if(bond.getOrder() == 3 ){
				IAtom atom1 = null;
				IAtom atom2 = null;
				if(bond.getAtom(0).getSymbol().equals("C") && bond.getAtom(1).getSymbol().equals("O") && 
						bond.getAtom(0).getFormalCharge() == 0 && bond.getAtom(1).getFormalCharge() == 1) {
					atom1 = bond.getAtom(1); /*Oxygen*/
					atom2 = bond.getAtom(0);
				}else if(bond.getAtom(1).getSymbol().equals("C") && bond.getAtom(0).getSymbol().equals("O") && 
								bond.getAtom(1).getFormalCharge() == 0 && bond.getAtom(0).getFormalCharge() == 1){
					atom1 = bond.getAtom(0);/*Oxygen*/
					atom2 = bond.getAtom(1);
				}
				if(atom1 != null && atom2 != null)
				if(atom1.getFlag(CDKConstants.REACTIVE_CENTER) && atom2.getFlag(CDKConstants.REACTIVE_CENTER)){
					List atomConL = reactant.getConnectedAtomsList(atom2);
					 Iterator iterator = atomConL.iterator();
					 IAtom atom3 = null;
					 while(iterator.hasNext()){
						 IAtom a = (IAtom) iterator.next();
						 if(a != atom1)
							 atom3 = a;
					 }
					 if(atom3 != null){
						 IBond bondCon = reactant.getBond(atom2, atom3);
					
							IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
							reaction.addReactant(reactant);
							
							/* positions atoms and bonds */
							int atom1P = reactant.getAtomNumber(atom1);
							int bondP = reactant.getBondNumber(bond);
//									System.out.println("bondP: "+bondP);
							int atom2P = reactant.getAtomNumber(atom2);
							int bondCP = reactant.getBondNumber(bondCon);
							int atom3P = reactant.getAtomNumber(atom3);

							/* action */
							IMolecule acCloned;
							try {
								acCloned = (IMolecule) reactant.clone();
							} catch (CloneNotSupportedException e) {
								throw new CDKException("Could not clone IMolecule!", e);
							}
							
							acCloned.addElectronContainer(new LonePair(acCloned.getAtom(atom2P)));	
							acCloned.removeElectronContainer(bondCP);
							if(bondCP < bondP)
								bondP--;
							
							/* mapping */
							IMapping mapping = atom1.getBuilder().newMapping(atom1, acCloned.getAtom(atom1P));
					        reaction.addMapping(mapping);
					        mapping = atom1.getBuilder().newMapping(atom2, acCloned.getAtom(atom2P));
					        reaction.addMapping(mapping);
					        mapping = atom1.getBuilder().newMapping(atom3, acCloned.getAtom(atom3P));
					        reaction.addMapping(mapping);
					        mapping = atom1.getBuilder().newMapping(bond, acCloned.getBond(bondP));
					        reaction.addMapping(mapping);
					        
							IMoleculeSet moleculeSet = ConnectivityChecker.partitionIntoMolecules(acCloned);
							for(int z = 0; z < moleculeSet.getAtomContainerCount() ; z++){
								reaction.addProduct(moleculeSet.getMolecule(z));
							}
							
							setOfReactions.addReaction(reaction);
					 	}
					}
				}
			}
		return setOfReactions;
		
	}
	/**
	 * set the active center for this molecule. 
	 * The active center will be those which correspond with RC-C#[O+]. 
	 * <pre>
	 * C: Atom
	 * -: single bond
	 * C: Atom
	 * #: triple bond
	 * O: Atom with formal charge = 1
	 *  </pre>
	 * 
	 * @param reactant The molecule to set the activity
	 * @throws CDKException 
	 */
	private void setActiveCenters(IMolecule reactant) throws CDKException {
		for(int i = 0 ; i < reactant.getBondCount() ; i++) {
			IBond bond = reactant.getBond(i);
			if(bond.getOrder() == 3 ){
				IAtom atom1 = null;
				IAtom atom2 = null;
				if(bond.getAtom(0).getSymbol().equals("C") && bond.getAtom(1).getSymbol().equals("O") && 
						bond.getAtom(0).getFormalCharge() == 0 && bond.getAtom(1).getFormalCharge() == 1) {
					atom1 = bond.getAtom(1); /*Oxygen*/
					atom2 = bond.getAtom(0);
				}else if(bond.getAtom(1).getSymbol().equals("C") && bond.getAtom(0).getSymbol().equals("O") && 
								bond.getAtom(1).getFormalCharge() == 0 && bond.getAtom(0).getFormalCharge() == 1){
					atom1 = bond.getAtom(0);/*Oxygen*/
					atom2 = bond.getAtom(1);
				}
				if(atom1 != null && atom2 != null){
					 List atomConL = reactant.getConnectedAtomsList(atom2);
					 Iterator iterator = atomConL.iterator();
					 IAtom atom3 = null;
					 while(iterator.hasNext()){
						 IAtom a = (IAtom) iterator.next();
						 if(a != atom1)
							 atom3 = a;
					 }
					 if(atom3 != null){
						 IBond bondCon = reactant.getBond(atom2, atom3);
						 atom1.setFlag(CDKConstants.REACTIVE_CENTER,true);
						 bond.setFlag(CDKConstants.REACTIVE_CENTER,true); 
						 atom2.setFlag(CDKConstants.REACTIVE_CENTER,true);
						 bondCon.setFlag(CDKConstants.REACTIVE_CENTER,true); 
						 atom3.setFlag(CDKConstants.REACTIVE_CENTER,true);
					 }
				}
			}
		}
	}
	/**
	 *  Gets the parameterNames attribute of the CarbonylEliminationReaction object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "hasActiveCenter";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the CarbonylEliminationReaction object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return new Boolean(false);
	}
}
