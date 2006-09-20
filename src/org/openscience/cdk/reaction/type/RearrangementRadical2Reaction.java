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


import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * <p>IReactionProcess which participate in movement resonance. 
 * This reaction could be represented as [A*]-B=C => A=B-[c*]. Due to 
 * excess of charge of the atom B, the sinble electron of the atom A is 
 * desplaced through the double bond.</p>
 * <p>Make sure that the molecule has the corresponend lone pair electrons
 * for each atom. You can use the method: <pre> LonePairElectronChecker </pre>
 * 
 * <pre>
 *  IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new RearrangementRadical2Reaction();
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
public class RearrangementRadical2Reaction implements IReactionProcess{
	private LoggingTool logger;
	private boolean hasActiveCenter;

	/**
	 * Constructor of the RearrangementRadical2Reaction object
	 *
	 */
	public RearrangementRadical2Reaction(){
		logger = new LoggingTool(this);
	}
	/**
	 *  Gets the specification attribute of the RearrangementRadical2Reaction object
	 *
	 *@return    The specification value
	 */
	public ReactionSpecification getSpecification() {
		return new ReactionSpecification(
				"http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#RearrangementRadical2Reaction",
				this.getClass().getName(),
				"$Id: RearrangementRadical2Reaction.java,v 1.6 2006/04/01 08:26:47 mrc Exp $",
				"The Chemistry Development Kit");
	}
	
	/**
	 *  Sets the parameters attribute of the RearrangementRadical2Reaction object
	 *
	 *@param  params            The parameter is if the molecule has already fixed the center active or not. It 
	 *							should be set before to inize the reaction with a setFlag:  CDKConstants.REACTIVE_CENTER
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("RearrangementRadical2Reaction only expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The parameter 1 must be of type boolean");
		}
		hasActiveCenter = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the RearrangementRadical2Reaction object
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

		logger.debug("initiate reaction: RearrangementRadical2Reaction");
		
		if (reactants.getMoleculeCount() != 1) {
			throw new CDKException("RearrangementRadical2Reaction only expects one reactant");
		}
		if (agents != null) {
			throw new CDKException("RearrangementRadical2Reaction don't expects agents");
		}
		
		IReactionSet setOfReactions = DefaultChemObjectBuilder.getInstance().newReactionSet();
		IMolecule reactant = reactants.getMolecule(0);

		/* if the parameter hasActiveCenter is not fixed yet, set the active centers*/
		if(!hasActiveCenter){
			setActiveCenters(reactant);
		}
		
		IAtom[] atoms = reactants.getMolecule(0).getAtoms();
		for(int i = 0 ; i < atoms.length ; i++){
			if(atoms[i].getFlag(CDKConstants.REACTIVE_CENTER)&& reactant.getSingleElectron(atoms[i]).length == 1){
				IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
				reaction.addReactant(reactant);
				
				IBond[] bonds = reactant.getConnectedBonds(atoms[i]);
				
				for(int j = 0 ; j < bonds.length ; j++){
					if(bonds[j].getFlag(CDKConstants.REACTIVE_CENTER)&& bonds[j].getOrder() == 1.0){
						IAtom atom = bonds[j].getConnectedAtom(reactant.getAtom(i));
						IBond[] bondsI = reactant.getConnectedBonds(atom);
						for(int k = 0 ; k < bondsI.length ; k++){
							if(bondsI[k].getFlag(CDKConstants.REACTIVE_CENTER) && bondsI[k].getOrder() == 2.0){
								IAtom atom1 = bondsI[k].getConnectedAtom(atom);
								if(atom1.getFlag(CDKConstants.REACTIVE_CENTER)&& atom1.getFormalCharge() == 0 ){
									/* positions atoms and bonds */
									int atom0P = reactant.getAtomNumber(atoms[i]);
									int bond1P = reactant.getBondNumber(bonds[j]);
									int bond2P = reactant.getBondNumber(bondsI[k]);
									int atom1P = reactant.getAtomNumber(atom);
									int atom2P = reactant.getAtomNumber(atom1);
									
									/* action */
									IAtomContainer acCloned;
									try {
										acCloned = (IAtomContainer)reactant.clone();
									} catch (CloneNotSupportedException e) {
										throw new CDKException("Could not clone IMolecule!", e);
									}
									
									ISingleElectron[] selectron = acCloned.getSingleElectron(acCloned.getAtom(atom0P));
									acCloned.removeElectronContainer(selectron[0]);
									
									acCloned.addElectronContainer(new SingleElectron(acCloned.getAtom(atom2P)));	

									double order = acCloned.getBond(bond1P).getOrder();
									acCloned.getBond(bond1P).setOrder(order+1);
									
									order = acCloned.getBond(bond2P).getOrder();
									acCloned.getBond(bond2P).setOrder(order-1);
									
									/* mapping */
									IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(atoms[i], acCloned.getAtom(atom0P));
							        reaction.addMapping(mapping);
							        mapping = DefaultChemObjectBuilder.getInstance().newMapping(atom, acCloned.getAtom(atom1P));
							        reaction.addMapping(mapping);
							        mapping = DefaultChemObjectBuilder.getInstance().newMapping(atom1, acCloned.getAtom(atom2P));
							        reaction.addMapping(mapping);
							        mapping = DefaultChemObjectBuilder.getInstance().newMapping(bonds[j], acCloned.getBond(bond1P));
							        reaction.addMapping(mapping);
							        mapping = DefaultChemObjectBuilder.getInstance().newMapping(bondsI[k], acCloned.getBond(bond2P));
							        reaction.addMapping(mapping);
									
									reaction.addProduct((IMolecule) acCloned);
									setOfReactions.addReaction(reaction);
								}
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
	 * The active center will be those which correspond with  [A*]-B=C . 
	 * <pre>
	 * A: Atom with single electron
	 * -: Single bond
	 * B: Atom 
	 * =: Double bond
	 * C: Atom
	 *  </pre>
	 * 
	 * @param reactant The molecule to set the activity
	 * @throws CDKException 
	 */
	private void setActiveCenters(IMolecule reactant) throws CDKException {
		IAtom[] atoms = reactant.getAtoms();
		if(AtomContainerManipulator.getTotalNegativeFormalCharge(reactant) != 0 /*|| AtomContainerManipulator.getTotalPositiveFormalCharge(reactant) != 0*/)
			return;
		for(int i = 0 ; i < atoms.length ; i++)
			if(reactant.getSingleElectron(atoms[i]).length == 1 ){
				IBond[] bonds = reactant.getConnectedBonds(atoms[i]);
				
				for(int j = 0 ; j < bonds.length ; j++){
					if(bonds[j].getOrder() == 1.0){
						IAtom atom = bonds[j].getConnectedAtom(reactant.getAtom(i));
						IBond[] bondsI = reactant.getConnectedBonds(atom);
						for(int k = 0 ; k < bondsI.length ; k++){
							if(bondsI[k].getOrder() == 2.0){
								IAtom atom1 = bondsI[k].getConnectedAtom(atom);
								if(atom1.getFormalCharge() == 0 ){
									atoms[i].setFlag(CDKConstants.REACTIVE_CENTER,true);
									atom.setFlag(CDKConstants.REACTIVE_CENTER,true);
									atom1.setFlag(CDKConstants.REACTIVE_CENTER,true);
									bondsI[k].getConnectedAtom(atom).setFlag(CDKConstants.REACTIVE_CENTER,true);
									bonds[j].setFlag(CDKConstants.REACTIVE_CENTER,true);
									bondsI[k].setFlag(CDKConstants.REACTIVE_CENTER,true);
								}
							}
						}
					}
				}
			}
	}
	/**
	 *  Gets the parameterNames attribute of the RearrangementRadical2Reaction object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "hasActiveCenter";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the RearrangementRadical2Reaction object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return new Boolean(false);
	}
}
