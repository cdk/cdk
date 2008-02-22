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


import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.BondManipulator;

import java.util.Iterator;

/**
 * <p>IReactionProcess which a bond that is being broken to generate single electron
 * for each atom. This reaction is a extension of CleavageBondReaction. The difference
 * consists that this reaction makes a multifragmentation. It doesn't obtain the fragments
 * which are obtained before. The reason is that for big molecules we obtain a errorOfMemory.
 * 
 * 
 * <pre>A-B => [A*] + [B*]</pre>
 * <pre>
 *  IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new CleavageBondMultiReaction();
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
 * @cdk.created    2006-11-17
 * @cdk.module     reaction
 * @cdk.svnrev  $Revision: 9162 $
 * @cdk.set        reaction-types
 * 
 **/
public class CleavageBondMultiReaction implements IReactionProcess{
	private LoggingTool logger;
	private boolean hasActiveCenter;
	private IMoleculeSet moleculeSetTOTAL;
	private static final int BONDTOFLAG = 8;
	/**
	 * Constructor of the CleavageBondMultiReaction object
	 *
	 */
	public CleavageBondMultiReaction(){
		moleculeSetTOTAL = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
	
		logger = new LoggingTool(this);
	}
	/**
	 *  Gets the specification attribute of the CleavageBondMultiReaction object
	 *
	 *@return    The specification value
	 */
	public ReactionSpecification getSpecification() {
		return new ReactionSpecification(
				"http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#CleavageBondMultiReaction",
				this.getClass().getName(),
				"$Id: CleavageBondMultiReaction.java,v 1.6 2006/04/01 08:26:47 mrc Exp $",
				"The Chemistry Development Kit");
	}
	
	/**
	 *  Sets the parameters attribute of the CleavageBondMultiReaction object
	 *
	 *@param  params            The parameter is if the molecule has already fixed the center active or not. It 
	 *							should be set before to inize the reaction with a setFlag:  CDKConstants.REACTIVE_CENTER
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("CleavageBondMultiReaction only expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The parameter 1 must be of type boolean");
		}
		hasActiveCenter = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the CleavageBondMultiReaction object
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
		logger.debug("initiate reaction: CleavageBondMultiReaction");
		
		
		if (reactants.getMoleculeCount() != 1) {
			throw new CDKException("CleavageBondMultiReaction only expects one reactant");
		}
		if (agents != null) {
			throw new CDKException("CleavageBondMultiReaction don't expects agents");
		}
		
		IReactionSet setOfReactions = reactants.getBuilder().newReactionSet();
		
		IMolecule reactant = reactants.getMolecule(0);
		if(existAC(moleculeSetTOTAL,reactant))
			return setOfReactions;
		else
			moleculeSetTOTAL.addMolecule(reactant);
		
		/* if the parameter hasActiveCenter is not fixed yet, set the active centers*/
		if(!hasActiveCenter){
			setActiveCenters(reactant);
		}

        Iterator bonds = reactants.getMolecule(0).bonds();
        while (bonds.hasNext()) {
            IBond aBond = (IBond) bonds.next();

			if(aBond.getFlag(CDKConstants.REACTIVE_CENTER))
				if(aBond.getAtom(0).getFormalCharge() == 0 && aBond.getAtom(1).getFormalCharge() == 0){
				
				int atom1 = reactants.getMolecule(0).getAtomNumber(aBond.getAtom(0));
				int atom2 = reactants.getMolecule(0).getAtomNumber(aBond.getAtom(1));
				cleanFlagBOND(reactants.getMolecule(0));
				aBond.setFlag(BONDTOFLAG, true);
				
				IReaction reaction = reactants.getBuilder().newReaction();
				reaction.addReactant(reactants.getMolecule(0));
				
				IMolecule reactantCloned;
				try {
					reactantCloned = (IMolecule) reactant.clone();
				} catch (CloneNotSupportedException e) {
					throw new CDKException("Could not clone IMolecule!", e);
				}
				
				reactantCloned.addSingleElectron(reactant.getBuilder().newSingleElectron(reactantCloned.getAtom(atom1)));
				reactantCloned.addSingleElectron(reactant.getBuilder().newSingleElectron(reactantCloned.getAtom(atom2)));
				
				IBond.Order order = null;
				IBond bondClon = null;
				for(int l = 0 ; l<reactantCloned.getBondCount();l++){
					if(reactantCloned.getBond(l).getFlag(BONDTOFLAG)){
						IBond bondFlag = reactantCloned.getBond(l);
						order = bondFlag.getOrder();
						if(order == IBond.Order.SINGLE){
							reactantCloned.removeBond(bondFlag.getAtom(0), bondFlag.getAtom(1));
						}
						else{
							BondManipulator.decreaseBondOrder(reactantCloned.getBond(l));
							bondClon = reactantCloned.getBond(l);
						}
						break;
					}
				}

				IMoleculeSet moleculeSet;
				if(order == IBond.Order.SINGLE)/*break molecule*/{
					moleculeSet = ConnectivityChecker.partitionIntoMolecules(reactantCloned);
					int exx = 0;
					for(int z = 0 ; z < moleculeSet.getAtomContainerCount(); z++){
						IMolecule ac = moleculeSet.getMolecule(z);
						
						if(!existAC(moleculeSetTOTAL,ac)){
							exx++;
							reaction.addProduct(ac);
						}
					}
					if(exx == 0)
						return setOfReactions;
				}
				else{
					if(existAC(moleculeSetTOTAL,reactantCloned))
						return setOfReactions;
					else{
						reaction.addProduct(reactantCloned);
					}
				}
					
				
				/*adding only that contains product*/
				if(reaction.getProductCount() != 0)
					setOfReactions.addReaction(reaction);
				

				aBond.setFlag(BONDTOFLAG, false);
				
				/* fragmentation again with the obtained fragments*/
				if(reaction.getProductCount() != 0)
				for(Iterator iter = reaction.getProducts().molecules(); iter.hasNext();){
					IMolecule molF = (IMolecule) iter.next();
					if(existAC(moleculeSetTOTAL,molF))
						continue;
					IMoleculeSet moleculeSetF = molF.getBuilder().newMoleculeSet();
					moleculeSetF.addMolecule(molF);
					IReactionSet setOfReactionsF = initiate(moleculeSetF, null);
					if(setOfReactionsF != null)
					if(setOfReactionsF.getReactionCount() != 0)
						for(Iterator iterR = setOfReactionsF.reactions(); iterR.hasNext();){
							setOfReactions.addReaction((IReaction) iterR.next());
						}
				}
				
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
        Iterator bonds = reactant.bonds();
        while (bonds.hasNext()) {
            IBond bond = (IBond) bonds.next();
            if (bond.getAtom(0).getFormalCharge() == 0 && bond.getAtom(1).getFormalCharge() == 0)
                bond.setFlag(CDKConstants.REACTIVE_CENTER, true);
        }
    }
	/**
	 *  Gets the parameterNames attribute of the CleavageBondMultiReaction object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "hasActiveCenter";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the CleavageBondMultiReaction object
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

	/**
	 * controll if the new product was already found before
	 * @param acSet 
	 * @param fragment
	 * @return True, if it contains
	 */
	private boolean existAC(IAtomContainerSet acSet, IMolecule fragment) {
		QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(fragment);
		for(int i = 0; i < acSet.getAtomContainerCount(); i++){
			IAtomContainer ac = acSet.getAtomContainer(i);
			try {
				if(UniversalIsomorphismTester.isIsomorph(ac, qAC))
					return true;
			} catch (CDKException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
