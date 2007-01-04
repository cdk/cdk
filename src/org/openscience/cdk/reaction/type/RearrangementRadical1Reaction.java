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


import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * <p>IReactionProcess which participate in movement resonance. 
 * This reaction could be represented as [A*]-B| => A=[B*]. Due to 
 * excess of charge of the atom A, the lone pair electron of the atom A jointly with
 * one electrons of the lone pair are desplaced creating a double bond.</p>
 * <p>Make sure that the molecule has the corresponend lone pair electrons
 * for each atom. You can use the method: <pre> LonePairElectronChecker </pre>
 * 
 * <pre>
 *  IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new RearrangementRadical1Reaction();
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
 * @cdk.created    2006-05-05
 * @cdk.module     reaction
 * @cdk.set        reaction-types
 * 
 **/
public class RearrangementRadical1Reaction implements IReactionProcess{
	private LoggingTool logger;
	private boolean hasActiveCenter;
	private static final int BONDTOFLAG1 = 8;

	/**
	 * Constructor of the RearrangementRadical1Reaction object
	 *
	 */
	public RearrangementRadical1Reaction(){
		logger = new LoggingTool(this);
	}
	/**
	 *  Gets the specification attribute of the RearrangementRadical1Reaction object
	 *
	 *@return    The specification value
	 */
	public ReactionSpecification getSpecification() {
		return new ReactionSpecification(
				"http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#RearrangementRadical1Reaction",
				this.getClass().getName(),
				"$Id: RearrangementRadical1Reaction.java,v 1.6 2006/04/01 08:26:47 mrc Exp $",
				"The Chemistry Development Kit");
	}
	
	/**
	 *  Sets the parameters attribute of the RearrangementRadical1Reaction object
	 *
	 *@param  params            The parameter is if the molecule has already fixed the center active or not. It 
	 *							should be set before to inize the reaction with a setFlag:  CDKConstants.REACTIVE_CENTER
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("RearrangementRadical1Reaction only expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The parameter 1 must be of type boolean");
		}
		hasActiveCenter = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the RearrangementRadical1Reaction object
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

		logger.debug("initiate reaction: RearrangementRadical1Reaction");
		
		if (reactants.getMoleculeCount() != 1) {
			throw new CDKException("RearrangementRadical1Reaction only expects one reactant");
		}
		if (agents != null) {
			throw new CDKException("RearrangementRadical1Reaction don't expects agents");
		}
		
		IReactionSet setOfReactions = DefaultChemObjectBuilder.getInstance().newReactionSet();
		IMolecule reactant = reactants.getMolecule(0);

		/* if the parameter hasActiveCenter is not fixed yet, set the active centers*/
		if(!hasActiveCenter){
			setActiveCenters(reactant);
		}
		
		IAtom atomi = null;
		IBond bondj = null;
		for(int i = 0 ; i < reactant.getAtomCount() ; i++){
			atomi = reactant.getAtom(i);
			if(atomi.getFlag(CDKConstants.REACTIVE_CENTER) && reactant.getConnectedSingleElectronsCount(atomi) == 1 ){
				
				java.util.List bonds = reactant.getConnectedBondsList(atomi);
				
				for(int j = 0 ; j < bonds.size() ; j++){
					bondj = (IBond)bonds.get(j);
					if(bondj.getFlag(CDKConstants.REACTIVE_CENTER) && bondj.getOrder() == 1.0){
						IAtom atom1 = bondj.getConnectedAtom(atomi);
						List lp = reactant.getConnectedLonePairsList(atom1);
						if(atom1.getFlag(CDKConstants.REACTIVE_CENTER) && lp.size() > 0 ){
							IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
							reaction.addReactant(reactant);

							cleanFlagBOND(reactants.getMolecule(0));
							/* positions atoms and bonds */
							int atom0P = reactant.getAtomNumber(atomi);
							bondj.setFlag(BONDTOFLAG1, true);
							int atom1P = reactant.getAtomNumber(atom1);
							
							/* action */
							IAtomContainer acCloned;
							try {
								acCloned = (IAtomContainer)reactant.clone();
							} catch (CloneNotSupportedException e) {
								throw new CDKException("Could not clone IMolecule!", e);
							}
							
							List selectron = acCloned.getConnectedSingleElectronsList(acCloned.getAtom(atom0P));
							acCloned.removeSingleElectron((ISingleElectron)selectron.get(selectron.size() -1));
							
							acCloned.addSingleElectron(new SingleElectron(acCloned.getAtom(atom1P)));	
					        
							List lpelectron = acCloned.getConnectedLonePairsList(acCloned.getAtom(atom1P));
							acCloned.removeLonePair((ILonePair)lpelectron.get(selectron.size() -1));
							
							IBond bondjClon = null;
							for(int l = 0 ; l<acCloned.getBondCount();l++){
								IBond bb = acCloned.getBond(l);
								if(bb.getFlag(BONDTOFLAG1)){
									double order = bb.getOrder();
									bb.setOrder(order+1);
									bondjClon = bb;
									break;
								}
							}
							/* mapping */
							IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(atomi, acCloned.getAtom(atom0P));
					        reaction.addMapping(mapping);
					        mapping = DefaultChemObjectBuilder.getInstance().newMapping(atom1, acCloned.getAtom(atom1P));
					        reaction.addMapping(mapping);
					        mapping = DefaultChemObjectBuilder.getInstance().newMapping(bondj, bondjClon);
					        reaction.addMapping(mapping);
					        
							reaction.addProduct((IMolecule) acCloned);
							setOfReactions.addReaction(reaction);
							bondj.setFlag(BONDTOFLAG1, false);
						}
					}
				}
			}
		}
		
		return setOfReactions;	
		
		
	}
	/**
	 * set the active center for this molecule. 
	 * The active center will be those which correspond with [A*]-B|. 
	 * <pre>
	 * A: Atom with single electron (radical)
	 * -: Single bond
	 * B: Atom with lone pair electrons
	 *  </pre>
	 * 
	 * @param reactant The molecule to set the activity
	 * @throws CDKException 
	 */
	private void setActiveCenters(IMolecule reactant) throws CDKException {
		if(AtomContainerManipulator.getTotalNegativeFormalCharge(reactant) != 0 /*|| AtomContainerManipulator.getTotalPositiveFormalCharge(reactant) != 0*/)
			return;
		IAtom atomi = null;
		IBond bondj = null;
		for(int i = 0; i < reactant.getAtomCount(); i++) {
			atomi = reactant.getAtom(i);
			if(reactant.getConnectedSingleElectronsCount(atomi) == 1 ){
				java.util.List bonds = reactant.getConnectedBondsList(atomi);
				for(int j = 0 ; j < bonds.size() ; j++){
					bondj = (IBond)bonds.get(j);
					if(bondj.getOrder() == 1.0){
						IAtom atom = bondj.getConnectedAtom(atomi);
						int lpCount = reactant.getConnectedLonePairsCount(atom);
						if(lpCount > 0 ){
							atomi.setFlag(CDKConstants.REACTIVE_CENTER,true);
							atom.setFlag(CDKConstants.REACTIVE_CENTER,true);
							bondj.setFlag(CDKConstants.REACTIVE_CENTER,true);
						}
					}
				}
			}
		}
	}
	/**
	 *  Gets the parameterNames attribute of the RearrangementRadical1Reaction object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "hasActiveCenter";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the RearrangementRadical1Reaction object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return new Boolean(false);
	}
	/**
     * clean the flags BONDTOFLAG from the molecule
     * 
	 * @param mol
	 */
	public void cleanFlagBOND(IAtomContainer ac){
		for(int j = 0 ; j < ac.getBondCount(); j++){
			ac.getBond(j).setFlag(BONDTOFLAG1, false);
		}
	}
}
