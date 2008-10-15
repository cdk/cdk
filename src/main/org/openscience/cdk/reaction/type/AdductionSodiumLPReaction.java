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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionEngine;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.reaction.mechanism.AdductionLPMechanism;
import org.openscience.cdk.reaction.type.parameters.IParameterReact;
import org.openscience.cdk.reaction.type.parameters.SetReactionCenter;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * <p>IReactionProcess which produces an adduction of the Sodium. 
 * As most commonly encountered, this reaction results in the formal migration
 * of a hydrogen atom or proton, accompanied by a switch of a single bond and adjacent double bond</p>
 * 
 * <pre>[X-] + [Na+] => X -Na</pre>
 * <pre>|X + [Na+]   => [X+]-Na</pre>
 * 
 * <p>Below you have an example how to initiate the mechanism.</p>
 * <p>It is processed by the AdductionLPMechanism class</p>
 * <pre>
 *  IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new AdductionSodiumLPReaction();
 *  Object[] params = {Boolean.FALSE};
    type.setParameters(params);
 *  IReactionSet setOfReactions = type.initiate(setOfReactants, null);
 *  </pre>
 * 
 * <p>We have the possibility to localize the reactive center. Good method if you
 * want to specify the reaction in a fixed point.</p>
 * <pre>atoms[0].setFlag(CDKConstants.REACTIVE_CENTER,true);</pre>
 * <p>Moreover you must put the parameter Boolean.TRUE</p>
 * <p>If the reactive center is not specified then the reaction process will
 * try to find automatically the possible reaction centers.</p>
 * 
 * 
 * @author         Miguel Rojas
 * 
 * @cdk.created    2008-02-11
 * @cdk.module     reaction
 * @cdk.set        reaction-types
 * 
 * @see AdductionLPMechanism
 **/
@TestClass(value="org.openscience.cdk.reaction.type.AdductionSodiumLPReactionTest")
public class AdductionSodiumLPReaction extends ReactionEngine implements IReactionProcess{
	private LoggingTool logger;
	
	/**
	 * Constructor of the AdductionSodiumLPReaction object.
	 *
	 */
	public AdductionSodiumLPReaction(){
		logger = new LoggingTool(this);
	}

	/**
	 *  Gets the specification attribute of the AdductionSodiumLPReaction object.
	 *
	 *@return    The specification value
	 */
    @TestMethod("testGetSpecification")
	public ReactionSpecification getSpecification() {
		return new ReactionSpecification(
				"http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#AdductionSodiumLP",
				this.getClass().getName(),
				"$Id$",
				"The Chemistry Development Kit");
	}
	
	/**
	 *  Initiate process.
	 *  It is needed to call the addExplicitHydrogensToSatisfyValency
	 *  from the class tools.HydrogenAdder.
	 *
	 *@param  reactants         reactants of the reaction
	 *@param  agents            agents of the reaction (Must be in this case null)
	 *
	 *@exception  CDKException  Description of the Exception
	 */
    @TestMethod("testInitiate_IMoleculeSet_IMoleculeSet")
	public IReactionSet initiate(IMoleculeSet reactants, IMoleculeSet agents) throws CDKException{

		logger.debug("initiate reaction: AdductionSodiumLPReaction");
		
		if (reactants.getMoleculeCount() != 1) {
			throw new CDKException("AdductionSodiumLPReaction only expects one reactant");
		}
		if (agents != null) {
			throw new CDKException("AdductionSodiumLPReaction don't expects agents");
		}
		
		IReactionSet setOfReactions = DefaultChemObjectBuilder.getInstance().newReactionSet();
		IMolecule reactant = reactants.getMolecule(0);
		
		/* if the parameter hasActiveCenter is not fixed yet, set the active centers*/
		IParameterReact ipr = super.getParameterClass(SetReactionCenter.class);
		if( ipr != null && !ipr.isSetParameter())
			setActiveCenters(reactant);
		
		if(AtomContainerManipulator.getTotalCharge(reactant) > 0)
			return setOfReactions;
		
		Iterator<IAtom> atoms = reactant.atoms().iterator();
        while (atoms.hasNext()) {
			IAtom atomi = atoms.next(); // Atom pos 1
			if(atomi.getFlag(CDKConstants.REACTIVE_CENTER) 
					&& (atomi.getFormalCharge() == CDKConstants.UNSET ? 0 : atomi.getFormalCharge()) <= 0
					&& reactant.getConnectedLonePairsCount(atomi) > 0 && reactant.getConnectedSingleElectronsCount(atomi) == 0){
				
				ArrayList<IAtom> atomList = new ArrayList<IAtom>();
				atomList.add(atomi);
				IAtom atomH = reactant.getBuilder().newAtom("Na");
				atomH.setFormalCharge(1);
				atomList.add(atomH);
				
				IMoleculeSet moleculeSet = reactant.getBuilder().newMoleculeSet();
				moleculeSet.addMolecule(reactant);
				IMolecule adduct = reactant.getBuilder().newMolecule();
				adduct.addAtom(atomH);
				moleculeSet.addMolecule(adduct);
				
				IReaction reaction = mechanism.initiate(moleculeSet, atomList, null);
				if(reaction == null)
					continue;
				else
					setOfReactions.addReaction(reaction);
				
			}
		}
		
		return setOfReactions;	
	}
	/**
	 * set the active center for this molecule. 
	 * The active center will be those which correspond with X=Y-Z-Na.
	 * <pre>
	 * [X-]
	 *  </pre>
	 * 
	 * @param reactant The molecule to set the activity
	 * @throws CDKException 
	 */
    private void setActiveCenters(IMolecule reactant) throws CDKException {
    	if(AtomContainerManipulator.getTotalCharge(reactant) > 0)
			return;
		
    	Iterator<IAtom> atoms = reactant.atoms().iterator();
        while (atoms.hasNext()) {
			IAtom atomi = atoms.next(); // Atom pos 1
			if((atomi.getFormalCharge() == CDKConstants.UNSET ? 0 : atomi.getFormalCharge()) <= 0	
				&& reactant.getConnectedLonePairsCount(atomi) > 0
				&& reactant.getConnectedSingleElectronsCount(atomi) == 0){
					atomi.setFlag(CDKConstants.REACTIVE_CENTER, true);
				
            }
        }
    }
}
