/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2008 Miguel Rojas <miguelrojasch@users.sf.net>
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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Ring;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.reaction.IReactionMechanism;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionEngine;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.reaction.mechanism.RadicalSiteRearrangementMechanism;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.tools.HOSECodeGenerator;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * <p>
 * This reaction could be represented as [A*]-(C)_3-C4[R] => A([R])-(C_3)-[C4*]. Due to 
 * the single electron of atom A the R is moved.</p>
 * <p>It is processed by the RadicalSiteRearrangementMechanism class</p>
 * 
 * <pre>
 *  IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new RadicalSiteRrBetaReaction();
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
 * try to find automatically the possible reactive center.</p>
 * 
 * 
 * @author         Miguel Rojas
 * 
 * @cdk.created    2006-10-20
 * @cdk.module     reaction
 * @cdk.svnrev  $Revision$
 * @cdk.set        reaction-types
 * 
 * @see RadicalSiteRearrangementMechanism
 **/
public class RadicalSiteRrBetaReaction extends ReactionEngine implements IReactionProcess{
	private LoggingTool logger;
	private IReactionMechanism mechanism;

	/**
	 * Constructor of the RadicalSiteRrBetaReaction object
	 *
	 */
	public RadicalSiteRrBetaReaction(){
		logger = new LoggingTool(this);
		mechanism = new RadicalSiteRearrangementMechanism();
	}
	/**
	 *  Gets the specification attribute of the RadicalSiteRrBetaReaction object
	 *
	 *@return    The specification value
	 */
	public ReactionSpecification getSpecification() {
		return new ReactionSpecification(
				"http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#RadicalSiteRrBeta",
				this.getClass().getName(),
				"$Id$",
				"The Chemistry Development Kit");
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

		logger.debug("initiate reaction: RadicalSiteRrBetaReaction");
		
		if (reactants.getMoleculeCount() != 1) {
			throw new CDKException("RadicalSiteRrBetaReaction only expects one reactant");
		}
		if (agents != null) {
			throw new CDKException("RadicalSiteRrBetaReaction don't expects agents");
		}
		
		IReactionSet setOfReactions = DefaultChemObjectBuilder.getInstance().newReactionSet();
		IMolecule reactant = reactants.getMolecule(0);

		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactant);
		CDKHueckelAromaticityDetector.detectAromaticity(reactant);
		AllRingsFinder arf = new AllRingsFinder();
		IRingSet ringSet = arf.findAllRings((Molecule) reactant);
		for (int ir = 0; ir < ringSet.getAtomContainerCount(); ir++) {
			Ring ring = (Ring)ringSet.getAtomContainer(ir);
			for (int jr = 0; jr < ring.getAtomCount(); jr++) {
				IAtom aring = ring.getAtom(jr);
				aring.setFlag(CDKConstants.ISINRING, true);
			}
		}
		/* if the parameter hasActiveCenter is not fixed yet, set the active centers*/
		if(!(Boolean)paramsMap.get("hasActiveCenter")){
			setActiveCenters(reactant);
		}
		
		HOSECodeGenerator hcg = new HOSECodeGenerator();
		Iterator<IAtom> atomis = reactant.atoms().iterator();
		while(atomis.hasNext()){
			IAtom  atomi = atomis.next();
			if(atomi.getFlag(CDKConstants.REACTIVE_CENTER)
					&& reactant.getConnectedSingleElectronsCount(atomi) == 1) {

				hcg.getSpheres((Molecule) reactant, atomi, 2, true);
				List<IAtom> atom1s = hcg.getNodesInSphere(2);
				
				hcg.getSpheres((Molecule) reactant, atomi, 3, true);
				Iterator<IAtom> atomls = hcg.getNodesInSphere(3).iterator();
				while(atomls.hasNext()){
					IAtom atoml = atomls.next();
					if(atoml != null && atoml.getFlag(CDKConstants.REACTIVE_CENTER) && !atoml.getFlag(CDKConstants.ISINRING) &&
							(atoml.getFormalCharge() == CDKConstants.UNSET ? 0 : atoml.getFormalCharge()) == 0 && !atoml.equals("H")  && 
							reactant.getMaximumBondOrder(atoml) == IBond.Order.SINGLE){
						
						Iterator<IAtom> atomRs = reactant.getConnectedAtomsList(atoml).iterator();
						while(atomRs.hasNext()){
							IAtom atomR = atomRs.next();
							if(atom1s.contains(atomR))
								continue;
							if(reactant.getBond(atomR, atoml).getFlag(CDKConstants.REACTIVE_CENTER) &&
									atomR.getFlag(CDKConstants.REACTIVE_CENTER) && 
									(atomR.getFormalCharge() == CDKConstants.UNSET ? 0 : atomR.getFormalCharge()) == 0){
								
								ArrayList<IAtom> atomList = new ArrayList<IAtom>();
				            	atomList.add(atomR);
				            	atomList.add(atomi);
				            	atomList.add(atoml);
				            	ArrayList<IBond> bondList = new ArrayList<IBond>();
				            	bondList.add(reactant.getBond(atomR, atoml));

								IMoleculeSet moleculeSet = reactant.getBuilder().newMoleculeSet();
								moleculeSet.addMolecule(reactant);
								IReaction reaction = mechanism.initiate(moleculeSet, atomList, bondList);
								if(reaction == null)
									continue;
								else
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
	 * The active center will be those which correspond with [A*]-(C)_2-C3[R]
	 * <pre>
	 * C: Atom with single electron
	 * C5: Atom with the R to move
	 *  </pre>
	 * 
	 * @param reactant The molecule to set the activity
	 * @throws CDKException 
	 */
	private void setActiveCenters(IMolecule reactant) throws CDKException {
		HOSECodeGenerator hcg = new HOSECodeGenerator();
		Iterator<IAtom> atomis = reactant.atoms().iterator();
		while(atomis.hasNext()){
			IAtom  atomi = atomis.next();
			if(reactant.getConnectedSingleElectronsCount(atomi) == 1) {
				
				hcg.getSpheres((Molecule) reactant, atomi, 2, true);
				List<IAtom> atom1s = hcg.getNodesInSphere(2);
				
				hcg.getSpheres((Molecule) reactant, atomi, 3, true);
				Iterator<IAtom> atomls = hcg.getNodesInSphere(3).iterator();
				while(atomls.hasNext()){
					IAtom atoml = atomls.next();
					if(atoml != null && !atoml.getFlag(CDKConstants.ISINRING) &&
							(atoml.getFormalCharge() == CDKConstants.UNSET ? 0 : atoml.getFormalCharge()) == 0 && !atoml.equals("H")  && 
							reactant.getMaximumBondOrder(atoml) == IBond.Order.SINGLE){
						
						Iterator<IAtom> atomRs = reactant.getConnectedAtomsList(atoml).iterator();
						while(atomRs.hasNext()){
							IAtom atomR = atomRs.next();
							if(atom1s.contains(atomR))
								continue;
							if((atomR.getFormalCharge() == CDKConstants.UNSET ? 0 : atomR.getFormalCharge()) == 0){

								atomi.setFlag(CDKConstants.REACTIVE_CENTER,true);
								atoml.setFlag(CDKConstants.REACTIVE_CENTER,true);
								atomR.setFlag(CDKConstants.REACTIVE_CENTER,true);
								reactant.getBond(atomR, atoml).setFlag(CDKConstants.REACTIVE_CENTER,true);
							}
						}
					}
				}
			}
		}
	}
}
