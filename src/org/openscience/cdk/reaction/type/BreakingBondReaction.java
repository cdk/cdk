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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.LonePair;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 * <p>IReactionProcess which a bond that is being broken to generate charges. 
 * As there are two directions for breaking a bond in a polar manner, 
 * each bond is investigated twice:</p>
 * <pre>A-B => [A+] + |[B-]</pre>
 * <pre>A-B => |[A-] + [B+]</pre>
 * <pre> It will not be created structures no possible, for example; C=O => [C-][O+].
 * <pre>
 *  IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new BreakingBondReaction();
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
 * @cdk.created    2006-06-09
 * @cdk.module     reaction
 * @cdk.svnrev  $Revision: 9162 $
 * @cdk.set        reaction-types
 * 
 **/
public class BreakingBondReaction implements IReactionProcess{
	private LoggingTool logger;
	private boolean hasActiveCenter;
	private CDKAtomTypeMatcher atMatcher;
	private static final int BONDTOFLAG = 8;
	
	/**
	 * Constructor of the BreakingBondReaction object
	 *
	 */
	public BreakingBondReaction(){
		logger = new LoggingTool(this);
		atMatcher = CDKAtomTypeMatcher.getInstance(NoNotificationChemObjectBuilder.getInstance());
	}

	/**
	 *  Gets the specification attribute of the BreakingBondReaction object
	 *
	 *@return    The specification value
	 */
	public ReactionSpecification getSpecification() {
		return new ReactionSpecification(
				"http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#BreakingBondReaction",
				this.getClass().getName(),
				"$Id: BreakingBondReaction.java,v 1.6 2006/04/01 08:26:47 mrc Exp $",
				"The Chemistry Development Kit");
	}
	
	/**
	 *  Sets the parameters attribute of the BreakingBondReaction object
	 *
	 *@param  params            The parameter is if the molecule has already fixed the center active or not. It 
	 *							should be set before to inize the reaction with a setFlag:  CDKConstants.REACTIVE_CENTER
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("BreakingBondReaction only expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The parameter 1 must be of type boolean");
		}
		hasActiveCenter = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the BreakingBondReaction object
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

		logger.debug("initiate reaction: BreakingBondReaction");
		
		if (reactants.getMoleculeCount() != 1) {
			throw new CDKException("BreakingBondReaction only expects one reactant");
		}
		if (agents != null) {
			throw new CDKException("BreakingBondReaction don't expects agents");
		}
		
		IReactionSet setOfReactions = DefaultChemObjectBuilder.getInstance().newReactionSet();
		IMolecule reactant = reactants.getMolecule(0);
		
		/* if the parameter hasActiveCenter is not fixed yet, set the active centers*/
		if(!hasActiveCenter){
			setActiveCenters(reactant);
		}
		
//		IBond[] bonds = reactants.getMolecule(0).getBonds();
        Iterator bonds = reactants.getMolecule(0).bonds();
        while (bonds.hasNext()) {
            IBond bond = (IBond) bonds.next();

			if(bond.getFlag(CDKConstants.REACTIVE_CENTER))
				if(bond.getAtom(0).getFormalCharge() == 0 && bond.getAtom(1).getFormalCharge() == 0){
				
				int atom1 = reactants.getMolecule(0).getAtomNumber(bond.getAtom(0));
				int atom2 = reactants.getMolecule(0).getAtomNumber(bond.getAtom(1));
				
				cleanFlagBOND(reactants.getMolecule(0));
				bond.setFlag(BONDTOFLAG, true);
				/**/
				for (int j = 0; j < 2; j++){
					IMolecule reactantCloned;
					try {
						reactantCloned = (IMolecule) reactant.clone();
					} catch (CloneNotSupportedException e) {
						throw new CDKException("Could not clone IMolecule!", e);
					}
					
					IBond aBond = null;
					IBond.Order order = null;
					for(int l = 0 ; l<reactantCloned.getBondCount();l++){
						if(reactantCloned.getBond(l).getFlag(BONDTOFLAG)){
							order = reactantCloned.getBond(l).getOrder();
							if (order == IBond.Order.SINGLE) {
								reactantCloned.removeBond(reactantCloned.getAtom(atom1), reactantCloned.getAtom(atom2));
							} else {
								BondManipulator.decreaseBondOrder(
										reactantCloned.getBond(l)
								);
							}
							aBond = reactantCloned.getBond(l);
							break;
						}
					}

					int charge = 0;
					if (j == 0){
						charge = reactantCloned.getAtom(atom1).getFormalCharge();
						reactantCloned.getAtom(atom1).setFormalCharge(charge+1);
						// check if resulting atom type is reasonable
						IAtomType type = atMatcher.findMatchingAtomType(reactantCloned, reactantCloned.getAtom(atom1));
						if (type == null) continue;
						
						charge = reactantCloned.getAtom(atom2).getFormalCharge();
						reactantCloned.getAtom(atom2).setFormalCharge(charge-1);
						reactantCloned.addLonePair(new LonePair(reactantCloned.getAtom(atom2)));
						/* an acceptor atom cannot be charged positive*/
						type = atMatcher.findMatchingAtomType(reactantCloned, reactantCloned.getAtom(atom2));
						if (type == null) continue;
					} else{
						charge = reactantCloned.getAtom(atom2).getFormalCharge();
						reactantCloned.getAtom(atom2).setFormalCharge(charge+1);
						IAtomType type = atMatcher.findMatchingAtomType(reactantCloned, reactantCloned.getAtom(atom2));
						if (type == null) continue;
						
						charge = reactantCloned.getAtom(atom1).getFormalCharge();
						reactantCloned.getAtom(atom1).setFormalCharge(-1);
						reactantCloned.addLonePair(new LonePair(reactantCloned.getAtom(atom1)));
						/* an acceptor atom cannot be charged positive*/
						type = atMatcher.findMatchingAtomType(reactantCloned, reactantCloned.getAtom(atom1));
						if (type == null) continue;
					}

					IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
					reaction.addReactant(reactants.getMolecule(0));

					/* mapping */
					IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(reactants.getMolecule(0).getAtom(atom1), reactantCloned.getAtom(atom1));
			        reaction.addMapping(mapping);
			        mapping = DefaultChemObjectBuilder.getInstance().newMapping(reactants.getMolecule(0).getAtom(atom2), reactantCloned.getAtom(atom2));
			        reaction.addMapping(mapping);
			        if(order != IBond.Order.SINGLE) {
			        	mapping = DefaultChemObjectBuilder.getInstance().newMapping(bond, aBond);
			        	reaction.addMapping(mapping);
			        	reaction.addProduct(reactantCloned);
			        } else{
				        IMoleculeSet moleculeSet = ConnectivityChecker.partitionIntoMolecules(reactantCloned);
						for(int z = 0; z < moleculeSet.getAtomContainerCount() ; z++){
							reaction.addProduct(moleculeSet.getMolecule(z));
						}
			        }
					/*adding only that contains product*/
					if(reaction.getProductCount() != 0)
						setOfReactions.addReaction(reaction);
				}
				bond.setFlag(BONDTOFLAG, false);
				
			}
				
		}
		
		return setOfReactions;	
	}
//	/**
//	 * controll if the new product was already found before
//	 * @param acSet 
//	 * @param fragment
//	 * @return True, if it contains
//	 */
//	private boolean existAC(IAtomContainerSet acSet, IMolecule fragment) {
//		QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(fragment);
//		for(int i = 0; i < acSet.getAtomContainerCount(); i++){
//			IAtomContainer ac = acSet.getAtomContainer(i);
//			try {
//				if(UniversalIsomorphismTester.isIsomorph(ac, qAC))
//					return true;
//			} catch (CDKException e) {
//				e.printStackTrace();
//			}
//		}
//		return false;
//	}
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
            if (bond.getAtom(0).getFormalCharge() == 0 && bond.getAtom(1).getFormalCharge() == 0) {
                IAtom atom1 = bond.getAtom(0);
                IAtom atom2 = bond.getAtom(1);
                atom1.setFlag(CDKConstants.REACTIVE_CENTER, true);
                atom2.setFlag(CDKConstants.REACTIVE_CENTER, true);
                bond.setFlag(CDKConstants.REACTIVE_CENTER, true);
            }
        }
    }
	/**
	 *  Gets the parameterNames attribute of the BreakingBondReaction object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "hasActiveCenter";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the BreakingBondReaction object
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
	 * @param ac
	 */
	public void cleanFlagBOND(IAtomContainer ac){
		for(int j = 0 ; j < ac.getBondCount(); j++)
			ac.getBond(j).setFlag(BONDTOFLAG, false);
	}/**
     * clean the flags CDKConstants.REACTIVE_CENTER from the molecule
     * 
	 * @param molecule
	 */
	public void cleanFlagReactiveCenter(IMolecule molecule){
		for(int j = 0 ; j < molecule.getAtomCount(); j++)
			molecule.getAtom(j).setFlag(CDKConstants.REACTIVE_CENTER, false);
		for(int j = 0 ; j < molecule.getBondCount(); j++)
			molecule.getBond(j).setFlag(CDKConstants.REACTIVE_CENTER, false);
	}
}
