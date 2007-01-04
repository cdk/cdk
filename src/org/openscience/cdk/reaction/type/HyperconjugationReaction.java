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
import org.openscience.cdk.interfaces.IAtom;
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
 * <p>HyperconjugationReaction is the stabilising interaction that results 
 * from the interaction of the electrons in a s-bond (for our case only C-H)
 * with an adjacent empty (or partially filled) p-orbital.</p>
 * <p>Based on the valence bond model of bonding, hyperconjugation can be described as 
 * "double bond - no bond resonance"</p>
 * <p>This reaction could be represented like</p>
 * <pre>[C+]-C => C=C + [H+] </pre>
 * 
 * <pre>
 *  IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new HyperconjugationReaction();
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
 * @cdk.created    2006-07-04
 * @cdk.module     reaction
 * @cdk.set        reaction-types
 * 
 **/
public class HyperconjugationReaction implements IReactionProcess{
	private LoggingTool logger;
	private boolean hasActiveCenter;

	/**
	 * Constructor of the HyperconjugationReaction object
	 *
	 */
	public HyperconjugationReaction(){
		logger = new LoggingTool(this);
	}
	/**
	 *  Gets the specification attribute of the HyperconjugationReaction object
	 *
	 *@return    The specification value
	 */
	public ReactionSpecification getSpecification() {
		return new ReactionSpecification(
				"http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#HyperconjugationReaction",
				this.getClass().getName(),
				"$Id: HyperconjugationReaction.java,v 1.6 2006/04/01 08:26:47 mrc Exp $",
				"The Chemistry Development Kit");
	}
	
	/**
	 *  Sets the parameters attribute of the HyperconjugationReaction object
	 *
	 *@param  params            The parameter is if the molecule has already fixed the center active or not. It 
	 *							should be set before to inize the reaction with a setFlag:  CDKConstants.REACTIVE_CENTER
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("HyperconjugationReaction only expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The parameter 1 must be of type boolean");
		}
		hasActiveCenter = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the HyperconjugationReaction object
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

		logger.debug("initiate reaction: HyperconjugationReaction");
		
		if (reactants.getMoleculeCount() != 1) {
			throw new CDKException("HyperconjugationReaction only expects one reactant");
		}
		if (agents != null) {
			throw new CDKException("HyperconjugationReaction don't expects agents");
		}
		
		IReactionSet setOfReactions = reactants.getBuilder().newReactionSet();
		IMolecule reactant = reactants.getMolecule(0);
		
		/* if the parameter hasActiveCenter is not fixed yet, set the active centers*/
		if(!hasActiveCenter){
			setActiveCenters(reactant);
		}
		IAtomContainerSet acSet = reactant.getBuilder().newAtomContainerSet();
		
		//IAtom[] atoms = reactant.getAtoms();
		IAtom atomi = null;
		IAtom atomj = null;
		IAtom atomk = null;
		for(int i = 0 ; i < reactant.getAtomCount() ; i++) {
			atomi = reactant.getAtom(i);
			if(atomi.getFlag(CDKConstants.REACTIVE_CENTER)&& atomi.getFormalCharge() == 1&& !(atomi.getSymbol().equals("H"))){
				java.util.List atoms1 = reactant.getConnectedAtomsList(atomi);
				for(int j = 0; j < atoms1.size(); j++) {
					atomj = (IAtom)atoms1.get(j);
					if(atomj.getFlag(CDKConstants.REACTIVE_CENTER)&& !(atomj.getSymbol().equals("H"))){
						IBond bond = reactant.getBond(atomi, atomj);
						if(bond.getOrder() == 1) {
							if(bond.getFlag(CDKConstants.REACTIVE_CENTER)){
								java.util.List atoms2 = reactant.getConnectedAtomsList(atomj);
								for(int k = 0; k < atoms2.size() ; k++) {
									atomk = (IAtom)atoms2.get(k);
									if(atomk.getSymbol().equals("H")){

										int atom1 = reactants.getMolecule(0).getAtomNumber(atomi);
										int atom2 = reactants.getMolecule(0).getAtomNumber(atomj);
										int atomH = reactants.getMolecule(0).getAtomNumber(atomk);
										int bond1 =  reactants.getMolecule(0).getBondNumber(bond);

										IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
										reaction.addReactant(reactants.getMolecule(0));

										IMolecule reactantCloned;
										try {
											reactantCloned = (IMolecule) reactant.clone();
										} catch (CloneNotSupportedException e) {
											throw new CDKException("Could not clone IMolecule!", e);
										}

										double order = reactantCloned.getBond(bond1).getOrder();
										reactantCloned.getBond(bond1).setOrder(order + 1);

										int charge = reactantCloned.getAtom(atom1).getFormalCharge();
										reactantCloned.getAtom(atom1).setFormalCharge(charge-1);

										reactantCloned.removeAtomAndConnectedElectronContainers(reactantCloned.getAtom(atomH));


										/* mapping */
										IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(bond, reactantCloned.getBond(bond1));
										reaction.addMapping(mapping);
										mapping = DefaultChemObjectBuilder.getInstance().newMapping(atomi, reactantCloned.getAtom(atom1));
										reaction.addMapping(mapping);
										mapping = DefaultChemObjectBuilder.getInstance().newMapping(atomj, reactantCloned.getAtom(atom2));
										reaction.addMapping(mapping);

										if(existAC(acSet,reactantCloned))
											continue;
										acSet.addAtomContainer(reactantCloned);

										reaction.addProduct(reactantCloned);

										IAtom hydrogen = reactants.getBuilder().newAtom("H");
										hydrogen.setFormalCharge(1);
										IMolecule proton = reactants.getBuilder().newMolecule();
										proton.addAtom(hydrogen);
										reaction.addProduct(proton);

										setOfReactions.addReaction(reaction);
									}
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
	 * The active center will be those which correspond with [A+]-B([H]). 
	 * <pre>
	 * A: Atom with charge
	 * -: Singlebond
	 * B: Atom
	 *  </pre>
	 * 
	 * @param reactant The molecule to set the activity
	 * @throws CDKException 
	 */
	private void setActiveCenters(IMolecule reactant) throws CDKException {
		//IAtom[] atoms = reactant.getAtoms();
		IAtom atomi = null;
		IAtom atomj = null;
		IAtom atomk = null;
		for(int i = 0 ; i < reactant.getAtomCount() ; i++) {
			atomi = reactant.getAtom(i);
			if(!atomi.getSymbol().equals("H")&& atomi.getFormalCharge() == 1){
				java.util.List atoms1 = reactant.getConnectedAtomsList(atomi);
				for(int j = 0; j < atoms1.size(); j++) {
					atomj = (IAtom)atoms1.get(j);
					if(!atomj.getSymbol().equals("H") && atomj.getFormalCharge() == 0){
						IBond bond = reactant.getBond(atomi, atomj);
						if(bond.getOrder() == 1){
							java.util.List atoms2 = reactant.getConnectedAtomsList(atomj);
							for(int k = 0; k < atoms2.size() ; k++){
								atomk = (IAtom)atoms2.get(k);
								if(atomk.getSymbol().equals("H")){
									atomi.setFlag(CDKConstants.REACTIVE_CENTER,true);
									atomj.setFlag(CDKConstants.REACTIVE_CENTER,true);
									bond.setFlag(CDKConstants.REACTIVE_CENTER,true);
								}
							}
						}

					}
				}
			}
		}
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
	/**
	 *  Gets the parameterNames attribute of the HyperconjugationReaction object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "hasActiveCenter";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the HyperconjugationReaction object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return new Boolean(false);
	}
}
