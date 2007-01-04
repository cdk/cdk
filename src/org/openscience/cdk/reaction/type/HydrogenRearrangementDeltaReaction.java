/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) $
 *  $Revision: 5855 $
 *
 *  Copyright (C) 2006-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
import java.util.Vector;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.tools.HOSECodeGenerator;
import org.openscience.cdk.tools.LoggingTool;

/**
 * <p>
 * This reaction could be represented as [A*]-(C)_4-C5[H] => A([H])-(C_4)-[C5*]. Due to 
 * the single electron of atom A.</p>
 * <p>Make sure that the molecule has the corresponend lone pair electrons
 * for each atom. You can use the method: <pre> LonePairElectronChecker </pre>
 * 
 * <pre>
 *  IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new HydrogenRearrangementDeltaReaction();
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
 * @cdk.created    2006-10-20
 * @cdk.module     reaction
 * @cdk.set        reaction-types
 * 
 **/
public class HydrogenRearrangementDeltaReaction implements IReactionProcess{
	private LoggingTool logger;
	private boolean hasActiveCenter;

	/**
	 * Constructor of the HydrogenRearrangementDeltaReaction object
	 *
	 */
	public HydrogenRearrangementDeltaReaction(){
		logger = new LoggingTool(this);
	}
	/**
	 *  Gets the specification attribute of the HydrogenRearrangementDeltaReaction object
	 *
	 *@return    The specification value
	 */
	public ReactionSpecification getSpecification() {
		return new ReactionSpecification(
				"http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#HydrogenRearrangementDeltaReaction",
				this.getClass().getName(),
				"$Id: HydrogenRearrangementDeltaReaction.java,v 1.6 2006/04/01 08:26:47 mrc Exp $",
				"The Chemistry Development Kit");
	}
	
	/**
	 *  Sets the parameters attribute of the HydrogenRearrangementDeltaReaction object
	 *
	 *@param  params            The parameter is if the molecule has already fixed the center active or not. It 
	 *							should be set before to inize the reaction with a setFlag:  CDKConstants.REACTIVE_CENTER
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("HydrogenRearrangementDeltaReaction only expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The parameter 1 must be of type boolean");
		}
		hasActiveCenter = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the HydrogenRearrangementDeltaReaction object
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
	 *  It is needed to call the addExplicitHydrogensToSatisfyValency
	 *  from the class tools.HydrogenAdder.
	 *
	 *@param  reactants         reactants of the reaction.
	 *@param  agents            agents of the reaction (Must be in this case null).
	 *
	 *@exception  CDKException  Description of the Exception
	 */
	public IReactionSet initiate(IMoleculeSet reactants, IMoleculeSet agents) throws CDKException{

		logger.debug("initiate reaction: HydrogenRearrangementDeltaReaction");
		
		if (reactants.getMoleculeCount() != 1) {
			throw new CDKException("HydrogenRearrangementDeltaReaction only expects one reactant");
		}
		if (agents != null) {
			throw new CDKException("HydrogenRearrangementDeltaReaction don't expects agents");
		}
		
		IReactionSet setOfReactions = DefaultChemObjectBuilder.getInstance().newReactionSet();
		IMolecule reactant = reactants.getMolecule(0);

		/* if the parameter hasActiveCenter is not fixed yet, set the active centers*/
		if(!hasActiveCenter){
			setActiveCenters(reactant);
		}
		
		HOSECodeGenerator hcg = new HOSECodeGenerator();
		for(int i = 0; i < reactant.getAtomCount(); i++) {
			IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
			reaction.addReactant(reactant);

			IAtom  atomi = reactant.getAtom(i);
			if(reactant.getConnectedSingleElectronsCount(atomi) == 1 && atomi.getFlag(CDKConstants.REACTIVE_CENTER)) {
				hcg.getSpheres((Molecule) reactant, atomi, 5, true);
				Vector atoms = hcg.getNodesInSphere(5);
				for(int j = 0 ; j < atoms.size() ; j++){
					IAtom atom4 = (IAtom)atoms.get(j);
					if(atom4 != null)
					if(atom4.getFormalCharge() == 0 && !atom4.equals("H")  && 
							reactant.getMaximumBondOrder(atom4) == 1 && atom4.getFlag(CDKConstants.REACTIVE_CENTER)){
						Iterator iterat = reactant.getConnectedAtomsList(atom4).iterator();
						while(iterat.hasNext()){
							IAtom hydrogen = (IAtom) iterat.next();
							if(hydrogen.getSymbol().equals("H") && hydrogen.getFlag(CDKConstants.REACTIVE_CENTER)){
								/* positions atoms and bonds */
								int atom0P = reactant.getAtomNumber(atomi);
								int atom4P = reactant.getAtomNumber(atom4);
								int atomHP = reactant.getAtomNumber(hydrogen);
								
								/* action */
								IAtomContainer acCloned;
								try {
									acCloned = (IAtomContainer)reactant.clone();
								} catch (CloneNotSupportedException e) {
									throw new CDKException("Could not clone IMolecule!", e);
								}
										
								
								List selectron = acCloned.getConnectedSingleElectronsList(acCloned.getAtom(atom0P));
								acCloned.removeSingleElectron((ISingleElectron)selectron.get(selectron.size() -1));
								acCloned.addBond(atom0P,atomHP, 1);
								acCloned.removeBond(acCloned.getAtom(atom4P), acCloned.getAtom(atomHP));
	
								acCloned.addSingleElectron(new SingleElectron(acCloned.getAtom(atom4P)));	
								
	
								/* mapping */
								IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(atomi, acCloned.getAtom(atom0P));
						        reaction.addMapping(mapping);
						        mapping = DefaultChemObjectBuilder.getInstance().newMapping(atom4, acCloned.getAtom(atom4P));
						        reaction.addMapping(mapping);
						        mapping = DefaultChemObjectBuilder.getInstance().newMapping(hydrogen, acCloned.getAtom(atomHP));
						        reaction.addMapping(mapping);
	
								reaction.addProduct((IMolecule) acCloned);
								setOfReactions.addReaction(reaction);
							}

						}
						
					}
				}
			}
		}
		return setOfReactions;	
	}
	/**
	 * set the active center for this molecule. 
	 * The active center will be those which correspond with [A*]-(C)_4-C5[H]
	 * <pre>
	 * C: Atom with single electron
	 * C5: Atom with Hydrogen
	 *  </pre>
	 * 
	 * @param reactant The molecule to set the activity
	 * @throws CDKException 
	 */
	private void setActiveCenters(IMolecule reactant) throws CDKException {
		HOSECodeGenerator hcg = new HOSECodeGenerator();
		for(int i = 0; i < reactant.getAtomCount(); i++) {
			IAtom  atomi = reactant.getAtom(i);
			if(reactant.getConnectedSingleElectronsCount(atomi) == 1) {
				HueckelAromaticityDetector.detectAromaticity(reactant);
				hcg.getSpheres((Molecule) reactant, atomi, 5, true);
				Vector atoms = hcg.getNodesInSphere(5);
				for(int j = 0 ; j < atoms.size() ; j++){
					IAtom atom4 = (IAtom)atoms.get(j);
					if(atom4 != null)
					if(atom4.getFormalCharge() == 0 && !atom4.equals("H") && 
							reactant.getMaximumBondOrder(atom4) == 1){
						Iterator iterat = reactant.getConnectedAtomsList(atom4).iterator();
						while(iterat.hasNext()){
							IAtom hydrogen = (IAtom) iterat.next();
							if(hydrogen.getSymbol().equals("H")){
								atomi.setFlag(CDKConstants.REACTIVE_CENTER,true);
								atom4.setFlag(CDKConstants.REACTIVE_CENTER,true);
								hydrogen.setFlag(CDKConstants.REACTIVE_CENTER,true);
								break; /*is only necessary one hydrogen */
							}
						}
					}
				}
			}
		}
	}
	/**
	 *  Gets the parameterNames attribute of the HydrogenRearrangementDeltaReaction object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "hasActiveCenter";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the HydrogenRearrangementDeltaReaction object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return new Boolean(false);
	}}
