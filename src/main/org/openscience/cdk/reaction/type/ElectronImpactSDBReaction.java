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


import java.util.ArrayList;
import java.util.Iterator;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.reaction.mechanism.RemovingSEofBMechanism;
import org.openscience.cdk.tools.LoggingTool;

/**
 * <p>IReactionProcess which make an electron impact for Sigma Bond Dissociation.</p>
 * This reaction type is a representation of the processes which occurs in the mass spectrometer.</p>
 * 
 * <pre>
 *  IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new ElectronImpactSDBReaction();
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
 * @cdk.created    2006-04-01
 * @cdk.module     reaction
 * @cdk.svnrev  $Revision: 9162 $
 * @cdk.set        reaction-types
 * 
 * @see RemovingSEofBMechanism
 **/
public class ElectronImpactSDBReaction implements IReactionProcess{
    private LoggingTool logger;
    private boolean hasActiveCenter;
	private RemovingSEofBMechanism mechanism;

    /**
     * Constructor of the ElectronImpactSDBReaction object.
     *
     */
    public ElectronImpactSDBReaction(){
        logger = new LoggingTool(this);
        mechanism = new RemovingSEofBMechanism();
    }
    /**
     *  Gets the specification attribute of the ElectronImpactSDBReaction object.
     *
     *@return    The specification value
     */
    public ReactionSpecification getSpecification() {
        return new ReactionSpecification(
                "http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#ElectronImpactPDBReaction",
                this.getClass().getName(),
                "$Id: ElectronImpactPDBReaction.java,v 1.6 2006/04/01 08:26:47 mrc Exp $",
                "The Chemistry Development Kit");
    }

    /**
     *  Sets the parameters attribute of the ElectronImpactSDBReaction object.
     *
     *@param  params            The parameter is if the molecule has already fixed the center active or not. It
     *							should be set before to ionize the reaction with a setFlag:  CDKConstants.REACTIVE_CENTER
     *@exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1) {
            throw new CDKException("ElectronImpactSDBReaction only expects one parameter");
        }
        if (!(params[0] instanceof Boolean)) {
            throw new CDKException("The parameter must be of type boolean");
        }
        hasActiveCenter = ((Boolean) params[0]).booleanValue();
    }


    /**
     *  Gets the parameters attribute of the ElectronImpactSDBReaction object.
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

        logger.debug("initiate reaction: ElectronImpactSDBReaction");

        if (reactants.getMoleculeCount() != 1) {
            throw new CDKException("ElectronImpactSDBReaction only expects one reactant");
        }
        if (agents != null) {
            throw new CDKException("ElectronImpactSDBReaction don't expects agents");
        }

        IReactionSet setOfReactions = DefaultChemObjectBuilder.getInstance().newReactionSet();
        IMolecule reactant = reactants.getMolecule(0);
        
        /* if the parameter hasActiveCenter is not fixed yet, set the active centers*/
        if(!hasActiveCenter){
            setActiveCenters(reactant);
        }

        Iterator<IBond> bonds = reactant.bonds();
        while (bonds.hasNext()) {
        	IBond bondi = bonds.next();
            IAtom atom1 = bondi.getAtom(0);
            IAtom atom2 = bondi.getAtom(1);
            if(bondi.getFlag(CDKConstants.REACTIVE_CENTER) && bondi.getOrder() == IBond.Order.SINGLE
				&& atom1.getFlag(CDKConstants.REACTIVE_CENTER) && atom2.getFlag(CDKConstants.REACTIVE_CENTER)
				&& (atom1.getFormalCharge() == CDKConstants.UNSET ? 0 : atom1.getFormalCharge()) == 0
				&& (atom2.getFormalCharge() == CDKConstants.UNSET ? 0 : atom2.getFormalCharge()) == 0
 				&& reactant.getConnectedSingleElectronsCount(atom1) == 0 && reactant.getConnectedSingleElectronsCount(atom2) == 0){
            	
            	/**/
                for (int j = 0; j < 2; j++){
                    
                	ArrayList<IAtom> atomList = new ArrayList<IAtom>();
                	if (j == 0){
                		atomList.add(atom1);
                		atomList.add(atom2);
                	}else{
                		atomList.add(atom2);
                		atomList.add(atom1);
                	}
                	ArrayList<IBond> bondList = new ArrayList<IBond>();
                	bondList.add(bondi);

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
        return setOfReactions;


    }
    /**
     * Set the active center for this molecule. The active center will be single bonds.
     * As default is only those atoms without charge and between a sigma bond.
     *
     * @param reactant The molecule to set the activity
     * @throws CDKException
     */
    private void setActiveCenters(IMolecule reactant) throws CDKException {
    	Iterator<IBond> bonds = reactant.bonds();
        while (bonds.hasNext()) {
        	IBond bondi = bonds.next();
            IAtom atom1 = bondi.getAtom(0);
            IAtom atom2 = bondi.getAtom(1);
            if(bondi.getOrder() == IBond.Order.SINGLE
				&& (atom1.getFormalCharge() == CDKConstants.UNSET ? 0 : atom1.getFormalCharge()) == 0
				&& (atom2.getFormalCharge() == CDKConstants.UNSET ? 0 : atom2.getFormalCharge()) == 0
 				&& reactant.getConnectedSingleElectronsCount(atom1) == 0 && reactant.getConnectedSingleElectronsCount(atom2) == 0){
            	  bondi.setFlag(CDKConstants.REACTIVE_CENTER, true);
            	  atom1.setFlag(CDKConstants.REACTIVE_CENTER, true);
            	  atom2.setFlag(CDKConstants.REACTIVE_CENTER, true);
            }
        }
    }
    /**
     *  Gets the parameterNames attribute of the ElectronImpactSDBReaction object.
     *
     * @return    The parameterNames value
     */
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "hasActiveCenter";
        return params;
    }


    /**
     *  Gets the parameterType attribute of the ElectronImpactSDBReaction object.
     *
     * @param  name  Description of the Parameter
     * @return       The parameterType value
     */
    public Object getParameterType(String name) {
        return new Boolean(false);
    }
}
