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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Ring;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.tools.HOSECodeGenerator;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * <p>
 * This reaction could be represented as [A*]-C1-C2-C3[H] => A([H])-C1-C2-[C3*]. Due to 
 * the single electron of atom A.</p>
 * <p>Make sure that the molecule has the corresponend lone pair electrons
 * for each atom. You can use the method: <pre> LonePairElectronChecker </pre>
 * 
 * <pre>
 *  IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new HydrogenRearrangementGammaReaction();
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
 * @cdk.svnrev  $Revision: 9162 $
 * @cdk.set        reaction-types
 * 
 **/
public class HydrogenRearrangementGammaReaction implements IReactionProcess{
	private LoggingTool logger;
	private boolean hasActiveCenter;

	/**
	 * Constructor of the HydrogenRearrangementGammaReaction object
	 *
	 */
	public HydrogenRearrangementGammaReaction(){
		logger = new LoggingTool(this);
	}
	/**
	 *  Gets the specification attribute of the HydrogenRearrangementGammaReaction object
	 *
	 *@return    The specification value
	 */
	public ReactionSpecification getSpecification() {
		return new ReactionSpecification(
				"http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#RearrangementRadical3Reaction",
				this.getClass().getName(),
				"$Id: HydrogenRearrangementGammaReaction.java,v 1.6 2006/04/01 08:26:47 mrc Exp $",
				"The Chemistry Development Kit");
	}
	
	/**
	 *  Sets the parameters attribute of the HydrogenRearrangementGammaReaction object
	 *
	 *@param  params            The parameter is if the molecule has already fixed the center active or not. It 
	 *							should be set before to inize the reaction with a setFlag:  CDKConstants.REACTIVE_CENTER
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("HydrogenRearrangementGammaReaction only expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The parameter 1 must be of type boolean");
		}
		hasActiveCenter = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the HydrogenRearrangementGammaReaction object
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

		logger.debug("initiate reaction: HydrogenRearrangementGammaReaction");
		
		if (reactants.getMoleculeCount() != 1) {
			throw new CDKException("HydrogenRearrangementGammaReaction only expects one reactant");
		}
		if (agents != null) {
			throw new CDKException("HydrogenRearrangementGammaReaction don't expects agents");
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
				
				hcg.getSpheres((Molecule) reactant, atomi, 4, true);
				List atoms = hcg.getNodesInSphere(4);
				for(int j = 0 ; j < atoms.size() ; j++){
					IAtom atom4 = (IAtom)atoms.get(j);
					if(atom4 != null)
					if(atom4.getFormalCharge() == 0 && !atom4.equals("H")  && 
							reactant.getMaximumBondOrder(atom4) == IBond.Order.SINGLE &&
							atom4.getFlag(CDKConstants.REACTIVE_CENTER)){
						if(atomi.getSymbol().equals("C") && reactant.getMaximumBondOrder(atom4) != IBond.Order.SINGLE)
							continue;
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
								acCloned.addBond(atom0P,atomHP, IBond.Order.SINGLE);
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
	 * The active center will be those which correspond with [A*]=B. 
	 * <pre>
	 * C: Atom with single electron
	 * C3: Atom with Hydrogen
	 *  </pre>
	 * 
	 * @param reactant The molecule to set the activity
	 * @throws CDKException 
	 */
	private void setActiveCenters(IMolecule reactant) throws CDKException {
		HOSECodeGenerator hcg = new HOSECodeGenerator();
		IRingSet ringSet = null;
		AllRingsFinder arf = new AllRingsFinder();
		for(int i = 0; i < reactant.getAtomCount(); i++) {
			IAtom  atomi = reactant.getAtom(i);
			if(reactant.getConnectedSingleElectronsCount(atomi) == 1) {
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactant);
				CDKHueckelAromaticityDetector.detectAromaticity(reactant);
				hcg.getSpheres((Molecule) reactant, atomi, 4, true);
				/* no rearrangement if H belongs to ring*/
				ringSet = arf.findAllRings((Molecule) reactant);
				for (int ir = 0; ir < ringSet.getAtomContainerCount(); ir++) {
					Ring ring = (Ring)ringSet.getAtomContainer(ir);
					for (int jr = 0; jr < ring.getAtomCount(); jr++) {
						IAtom aring = ring.getAtom(jr);
						aring.setFlag(CDKConstants.ISINRING, true);
					}
				}
				List atoms = hcg.getNodesInSphere(4);
				for(int j = 0 ; j < atoms.size() ; j++){
					IAtom atom4 = (IAtom)atoms.get(j);
					if(atom4 != null)
						if(!atom4.getFlag(CDKConstants.ISINRING))
							if(atom4.getFormalCharge() == 0 && !atom4.equals("H") && 
								reactant.getMaximumBondOrder(atom4) == IBond.Order.SINGLE){
							
							if(atomi.getSymbol().equals("C") && reactant.getMaximumBondOrder(atom4) != IBond.Order.SINGLE)
								continue;
							
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
	 *  Gets the parameterNames attribute of the HydrogenRearrangementGammaReaction object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "hasActiveCenter";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the HydrogenRearrangementGammaReaction object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return new Boolean(false);
	}}
