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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.tools.LoggingTool;

/**
 * <p>IReactionProcess which a bond that is being broken to generate single electron
 * for each atom. 
 * <pre>A-B => [A*] + [B*]</pre>
 * <pre>
 *  IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new CleavageBondReaction();
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
 * @cdk.created    2006-10-27
 * @cdk.module     reaction
 * @cdk.set        reaction-types
 * 
 **/
public class CleavageBondReaction implements IReactionProcess{
	private LoggingTool logger;
	private boolean hasActiveCenter;
	private static final int BONDTOFLAG = 8;
	/**
	 * Constructor of the cleavageBondReaction object
	 *
	 */
	public CleavageBondReaction(){
		logger = new LoggingTool(this);
	}
	/**
	 *  Gets the specification attribute of the CleavageBondReaction object
	 *
	 *@return    The specification value
	 */
	public ReactionSpecification getSpecification() {
		return new ReactionSpecification(
				"http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#CleavageBondReaction",
				this.getClass().getName(),
				"$Id: CleavageBondReaction.java,v 1.6 2006/04/01 08:26:47 mrc Exp $",
				"The Chemistry Development Kit");
	}
	
	/**
	 *  Sets the parameters attribute of the CleavageBondReaction object
	 *
	 *@param  params            The parameter is if the molecule has already fixed the center active or not. It 
	 *							should be set before to inize the reaction with a setFlag:  CDKConstants.REACTIVE_CENTER
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("cleavageBondReaction only expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The parameter 1 must be of type boolean");
		}
		hasActiveCenter = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the CleavageBondReaction object
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

		logger.debug("initiate reaction: CleavageBondReaction");
		
		if (reactants.getMoleculeCount() != 1) {
			throw new CDKException("CleavageBondReaction only expects one reactant");
		}
		if (agents != null) {
			throw new CDKException("CleavageBondReaction don't expects agents");
		}
		
		IReactionSet setOfReactions = DefaultChemObjectBuilder.getInstance().newReactionSet();
		IMolecule reactant = reactants.getMolecule(0);
		
		/* if the parameter hasActiveCenter is not fixed yet, set the active centers*/
		if(!hasActiveCenter){
			setActiveCenters(reactant);
		}
		
		IBond[] bonds = reactants.getMolecule(0).getBonds();
		for(int i = 0 ; i < bonds.length ; i++){
			
			if(bonds[i].getFlag(CDKConstants.REACTIVE_CENTER))
				if(bonds[i].getAtom(0).getFormalCharge() == 0 && bonds[i].getAtom(1).getFormalCharge() == 0){
				
				int atom1 = reactants.getMolecule(0).getAtomNumber(bonds[i].getAtom(0));
				int atom2 = reactants.getMolecule(0).getAtomNumber(bonds[i].getAtom(1));
				int bond =  0;/*reactants.getMolecule(0).getBondNumber(bonds[i])*/
				cleanFlagBOND(reactants.getMolecule(0));
				bonds[i].setFlag(BONDTOFLAG, true);
				
				IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
				reaction.addReactant(reactants.getMolecule(0));
				
				IMolecule reactantCloned;
				try {
					reactantCloned = (IMolecule) reactant.clone();
				} catch (CloneNotSupportedException e) {
					throw new CDKException("Could not clone IMolecule!", e);
				}
				
				reactantCloned.addElectronContainer(reactant.getBuilder().newSingleElectron(reactantCloned.getAtom(atom1)));
				reactantCloned.addElectronContainer(reactant.getBuilder().newSingleElectron(reactantCloned.getAtom(atom2)));
				
				double order = 0;
				for(int l = 0 ; l<reactantCloned.getBondCount();l++){
					if(reactantCloned.getBond(l).getFlag(BONDTOFLAG)){
						IBond bondFlag = reactantCloned.getBond(l);
						order = bondFlag.getOrder();
						if(order == 1){
							reactantCloned.removeBond(bondFlag.getAtom(0), bondFlag.getAtom(1));
						}
						else{
							reactantCloned.getBond(l).setOrder(order-1);
							bond = reactantCloned.getBondNumber(reactantCloned.getBond(l));
						}
						break;
					}
				}

				IMoleculeSet moleculeSet = null;
				
				
				if(order == 1)/*break molecule*/{
					moleculeSet = ConnectivityChecker.partitionIntoMolecules(reactantCloned);
					for(int z = 0 ; z < moleculeSet.getAtomContainerCount(); z++){
						IMolecule ac = moleculeSet.getMolecule(z);
						reaction.addProduct(ac);
					}
				}
				else{
					reaction.addProduct(reactantCloned);
					
					IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(bonds[i], reactantCloned.getBond(bond));
			        reaction.addMapping(mapping);
			        
				}
					
				
				/* mapping */
		        IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(bonds[i].getAtom(0), reactantCloned.getAtom(atom1));
		        reaction.addMapping(mapping);
		        mapping = DefaultChemObjectBuilder.getInstance().newMapping(bonds[i].getAtom(1), reactantCloned.getAtom(atom2));
		        reaction.addMapping(mapping);
				
				
				/*adding only that contains product*/
				if(reaction.getProductCount() != 0)
					setOfReactions.addReaction(reaction);
				

				bonds[i].setFlag(BONDTOFLAG, false);
			}
				
		}
		
		return setOfReactions;	
	}
	/**
	 * set the active center for this molecule. 
	 * The active center will be those which correspond with A-B. If
	 * the bond is simple, it will be breaked forming two fragments 
	 * <pre>
	 * A: Atom
	 * #/=/-: bond
	 * B: Atom
	 *  </pre>
	 * 
	 * @param reactant The molecule to set the activity
	 * @throws CDKException 
	 */
	private void setActiveCenters(IMolecule reactant) throws CDKException {
		IBond[] bonds = reactant.getBonds();
		for(int i = 0 ; i < bonds.length ; i++)
			if(bonds[i].getAtom(0).getFormalCharge() == 0 && bonds[i].getAtom(1).getFormalCharge() == 0)
				bonds[i].setFlag(CDKConstants.REACTIVE_CENTER,true);
	}
	/**
	 *  Gets the parameterNames attribute of the CleavageBondReaction object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "hasActiveCenter";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the CleavageBondReaction object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return new Boolean(false);
	}
	
	/**
     * clean the flags CDKConstants.REACTIVE_CENTER from the molecule
     * 
	 * @param mol
	 */
	public void cleanFlagBOND(IAtomContainer ac){
		for(int j = 0 ; j < ac.getBondCount(); j++)
			ac.getBond(j).setFlag(BONDTOFLAG, false);
	}
}
