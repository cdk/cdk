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
import org.openscience.cdk.LonePair;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * <p>IReactionProcess which participate in movement resonance. 
 * This reaction could be represented as two forms</p>
 * <pre>X=A => |[X-]-[A+]. X represents an acceptor atomType. 
 * It is a case specific of the method BreakingBondReaction</pre>
 * <pre>
 *  IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new DisplacementChargeFromAcceptorReaction();
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
public class DisplacementChargeFromAcceptorReaction implements IReactionProcess{
	private LoggingTool logger;
	private boolean hasActiveCenter;

	/**
	 * Constructor of the DisplacementChargeFromAcceptorReaction object
	 *
	 */
	public DisplacementChargeFromAcceptorReaction(){
		logger = new LoggingTool(this);
	}
	/**
	 *  Gets the specification attribute of the DisplacementChargeFromAcceptorReaction object
	 *
	 *@return    The specification value
	 */
	public ReactionSpecification getSpecification() {
		return new ReactionSpecification(
				"http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#DisplacementChargeFromAcceptorReaction",
				this.getClass().getName(),
				"$Id: DisplacementChargeReaction.java,v 1.6 2006/04/01 08:26:47 mrc Exp $",
				"The Chemistry Development Kit");
	}
	
	/**
	 *  Sets the parameters attribute of the DisplacementChargeFromAcceptorReaction object
	 *
	 *@param  params            The parameter is if the molecule has already fixed the center active or not. It 
	 *							should be set before to inize the reaction with a setFlag:  CDKConstants.REACTIVE_CENTER
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("DisplacementChargeFromAcceptorReaction only expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The parameter 1 must be of type boolean");
		}
		hasActiveCenter = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the DisplacementChargeFromAcceptorReaction object
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

		logger.debug("initiate reaction: DisplacementChargeFromAcceptorReaction");
		
		if (reactants.getMoleculeCount() != 1) {
			throw new CDKException("DisplacementChargeFromAcceptorReaction only expects one reactant");
		}
		if (agents != null) {
			throw new CDKException("DisplacementChargeFromAcceptorReaction don't expects agents");
		}
		
		IReactionSet setOfReactions = DefaultChemObjectBuilder.getInstance().newReactionSet();
		IMolecule reactant = reactants.getMolecule(0);
		
		/* if the parameter hasActiveCenter is not fixed yet, set the active centers*/
		if(!hasActiveCenter){
			setActiveCenters(reactant);
		}
		
		IBond[] bonds = reactants.getMolecule(0).getBonds();
		for(int i = 0 ; i < bonds.length ; i++){
			if(bonds[i].getFlag(CDKConstants.REACTIVE_CENTER) && bonds[i].getOrder() == 2.0){
				IAtom atom1 = bonds[i].getAtom(0);
				IAtom atom2 = bonds[i].getAtom(1);
				if((((atom1.getFormalCharge() == 0 && reactant.getSingleElectronSum(atom1) == 0 && reactant.getLonePairCount(atom1) > 0 && !atom1.getSymbol().equals("C")))
						&&(atom2.getFormalCharge() == 0 && reactant.getSingleElectronSum(atom2) == 0))
						|| (((atom2.getFormalCharge() == 0 && reactant.getSingleElectronSum(atom2) == 0 && reactant.getLonePairCount(atom2) > 0 && !atom2.getSymbol().equals("C")))
						&&(atom1.getFormalCharge() == 0 && reactant.getSingleElectronSum(atom1) == 0))){
							
						/* positions atoms and bonds */
						int atom0P = reactant.getAtomNumber(bonds[i].getAtom(0));
						int bond1P = reactant.getBondNumber(bonds[i]);
						int atom1P = reactant.getAtomNumber(bonds[i].getAtom(1));
						
						/* action */
						IAtomContainer acCloned;
						try {
							acCloned = (IAtomContainer)reactant.clone();
						} catch (CloneNotSupportedException e) {
							throw new CDKException("Could not clone reactant", e);
						}
						
						double order = acCloned.getBond(bond1P).getOrder();
						acCloned.getBond(bond1P).setOrder(order - 1);
						
						IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
						reaction.addReactant(reactant);

						
						if(reactant.getLonePairCount(atom1) > 0){
							acCloned.getAtom(atom0P).setFormalCharge(-1);
							acCloned.addElectronContainer(new LonePair(acCloned.getAtom(atom0P)));
							
							acCloned.getAtom(atom1P).setFormalCharge(1);
						}else{
							acCloned.getAtom(atom0P).setFormalCharge(1);
							
							acCloned.getAtom(atom1P).setFormalCharge(-1);
							acCloned.addElectronContainer(new LonePair(acCloned.getAtom(atom1P)));
							
						}
							
							/* mapping */
							IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(bonds[i], acCloned.getBond(bond1P));
					        reaction.addMapping(mapping);
					        mapping = DefaultChemObjectBuilder.getInstance().newMapping(bonds[i].getAtom(0), acCloned.getAtom(atom0P));
					        reaction.addMapping(mapping);
					        mapping = DefaultChemObjectBuilder.getInstance().newMapping(bonds[i].getAtom(1), acCloned.getAtom(atom1P));
					        reaction.addMapping(mapping);
							
					        
							reaction.addProduct((IMolecule) acCloned);
							setOfReactions.addReaction(reaction);
						}
				}
		}
		
		return setOfReactions;	
		
		
	}
	/**
	 * set the active center for this molecule. 
	 * The active center will be those which correspond with A=B. 
	 * <pre>
	 * A: Atom with lone pair electrons // TODO- not only the atoms with 
	 * lone electrons are acceptor atoms.
	 * =: Double bond
	 * B: Atom
	 *  </pre>
	 * 
	 * @param reactant The molecule to set the activity
	 * @throws CDKException 
	 */
	private void setActiveCenters(IMolecule reactant) throws CDKException {
		IBond[] bonds = reactant.getBonds();
		if(AtomContainerManipulator.getTotalNegativeFormalCharge(reactant) != 0 || AtomContainerManipulator.getTotalPositiveFormalCharge(reactant) != 0)
			return;
		for(int i = 0 ; i < bonds.length ; i++)
			if(bonds[i].getOrder() == 2.0){
				IAtom atom1 = bonds[i].getAtom(0);
				IAtom atom2 = bonds[i].getAtom(1);/* TODO - not controll from lone pair electrons*/
				if((((atom1.getFormalCharge() == 0 && reactant.getSingleElectronSum(atom1) == 0 && reactant.getLonePairCount(atom1) > 0 && !atom1.getSymbol().equals("C")) )
					&&(atom2.getFormalCharge() == 0 && reactant.getSingleElectronSum(atom2) == 0))
					|| (((atom2.getFormalCharge() == 0 && reactant.getSingleElectronSum(atom2) == 0 && reactant.getLonePairCount(atom2) > 0 && !atom2.getSymbol().equals("C")))
					&&(atom1.getFormalCharge() == 0 && reactant.getSingleElectronSum(atom1) == 0))){
						atom1.setFlag(CDKConstants.REACTIVE_CENTER,true);
						atom2.setFlag(CDKConstants.REACTIVE_CENTER,true);
						bonds[i].setFlag(CDKConstants.REACTIVE_CENTER,true);
				}
			}
	}
	/**
	 *  Gets the parameterNames attribute of the DisplacementChargeFromAcceptorReaction object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "hasActiveCenter";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the DisplacementChargeFromAcceptorReaction object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return new Boolean(false);
	}
}
