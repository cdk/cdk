/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) $
 *  $Revision: 5855 $
 *
 * Copyright (C) 2008  Miguel Rojas <miguelrojasch@users.sf.net>
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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.reaction.IReactionMechanism;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.reaction.mechanism.AdductionLPMechanism;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * <p>IReactionProcess which produces a protonation. 
 * As most commonly encountered, this reaction results in the formal migration
 * of a hydrogen atom or proton, accompanied by a switch of a single bond and adjacent double bond</p>
 * 
 * <pre>[X-] + [H+] => X -H</pre>
 * <pre>|X + [H+]   => [X+]-H</pre>
 * 
 * <p>Below you have an example how to initiate the mechanism.</p>
 * <p>It is processed by the AdductionLPMechanism class</p>
 * <pre>
 *  IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new AdductionProtonLPReaction();
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
public class AdductionProtonLPReaction implements IReactionProcess{
	private LoggingTool logger;
	private boolean hasActiveCenter;
	private IReactionMechanism mechanism;
	
	/**
	 * Constructor of the AdductionProtonLPReaction object.
	 *
	 */
	public AdductionProtonLPReaction(){
		logger = new LoggingTool(this);
		mechanism = new AdductionLPMechanism();
	}

	/**
	 *  Gets the specification attribute of the AdductionProtonLPReaction object.
	 *
	 *@return    The specification value
	 */
	public ReactionSpecification getSpecification() {
		return new ReactionSpecification(
				"http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#AdductionProtonLP",
				this.getClass().getName(),
				"$Id: AdductionProtonLPReaction.java,v 1.6 2006/04/01 08:26:47 mrc Exp $",
				"The Chemistry Development Kit");
	}
	
	/**
	 *  Sets the parameters attribute of the AdductionProtonLPReaction object.
	 *
	 * @param  params            The parameter is if the molecule has already fixed the center active or not. It 
	 *						     should be set before to initiate the reaction with a setFlag:  CDKConstants.REACTIVE_CENTER
	 *@exception  CDKException   Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("AdductionProtonLPReaction only expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The parameter 1 must be of type boolean");
		}
		hasActiveCenter = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the AdductionProtonLPReaction object.
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
	 *@param  reactants         reactants of the reaction
	 *@param  agents            agents of the reaction (Must be in this case null)
	 *
	 *@exception  CDKException  Description of the Exception
	 */
	public IReactionSet initiate(IMoleculeSet reactants, IMoleculeSet agents) throws CDKException{

		logger.debug("initiate reaction: AdductionProtonLPReaction");
		
		if (reactants.getMoleculeCount() != 1) {
			throw new CDKException("AdductionProtonLPReaction only expects one reactant");
		}
		if (agents != null) {
			throw new CDKException("AdductionProtonLPReaction don't expects agents");
		}
		
		IReactionSet setOfReactions = DefaultChemObjectBuilder.getInstance().newReactionSet();
		IMolecule reactant = reactants.getMolecule(0);
		
		/* if the parameter hasActiveCenter is not fixed yet, set the active centers*/
		if(!hasActiveCenter){
			setActiveCenters(reactant);
		}
		
		if(AtomContainerManipulator.getTotalCharge(reactant) > 0)
			return setOfReactions;
		
		Iterator<IAtom> atoms = reactant.atoms();
        while (atoms.hasNext()) {
			IAtom atomi = atoms.next(); // Atom pos 1
			if(atomi.getFlag(CDKConstants.REACTIVE_CENTER) &&  
					(atomi.getFormalCharge() == CDKConstants.UNSET ? 0 : atomi.getFormalCharge()) <= 0
					&& reactant.getConnectedLonePairsCount(atomi) > 0 && reactant.getConnectedSingleElectronsCount(atomi) == 0){
				
				ArrayList<IAtom> atomList = new ArrayList<IAtom>();
				atomList.add(atomi);
				IAtom atomH = reactant.getBuilder().newAtom("H");
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
	 * The active center will be those which correspond with X=Y-Z-H.
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
		
    	Iterator<IAtom> atoms = reactant.atoms();
        while (atoms.hasNext()) {
			IAtom atomi = atoms.next(); // Atom pos 1
			if((atomi.getFormalCharge() == CDKConstants.UNSET ? 0 : atomi.getFormalCharge()) <= 0
					&& reactant.getConnectedLonePairsCount(atomi) > 0
					&& reactant.getConnectedSingleElectronsCount(atomi) == 0){
				atomi.setFlag(CDKConstants.REACTIVE_CENTER, true);
				
            }
        }
    }
	/**
	 *  Gets the parameterNames attribute of the AdductionProtonLPReaction object.
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "hasActiveCenter";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the AdductionProtonLPReaction object.
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return new Boolean(false);
	}
}
