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
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionEngine;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.reaction.mechanism.HomolyticCleavageMechanism;
import org.openscience.cdk.reaction.type.parameters.IParameterReact;
import org.openscience.cdk.reaction.type.parameters.SetReactionCenter;
import org.openscience.cdk.tools.LoggingTool;

/**
 * <p>IReactionProcess which breaks the bond homogeneously leading to radical ions.
 * It is also called homogeneous bond-breaking.Depending of the bond order, 
 * the bond will be removed or simply the order decreased.</p>
 * <pre>A-B => [A*] + [B*]</pre>
 * <p>It is processed by the HomolyticCleavageMechanism class</p>
 * <pre>
 *  IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new HomolyticCleavageReaction();
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
 * @cdk.created    2006-10-27
 * @cdk.module     reaction
 * @cdk.svnrev  $Revision$
 * @cdk.set        reaction-types
 *
 * @see HomolyticCleavageMechanism
 **/
@TestClass(value="org.openscience.cdk.reaction.type.HomolyticCleavageReactionTest")
public class HomolyticCleavageReaction extends ReactionEngine implements IReactionProcess{
    private LoggingTool logger;
    /**
     * Constructor of the HomolyticCleavageReaction object.
     *
     */
    public HomolyticCleavageReaction(){
        logger = new LoggingTool(this);
    }
    /**
     *  Gets the specification attribute of the HomolyticCleavageReaction object.
     *
     *@return    The specification value
     */
    @TestMethod("testGetSpecification")
    public ReactionSpecification getSpecification() {
        return new ReactionSpecification(
                "http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#HomolyticCleavage",
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
    @TestMethod("testInitiate_IMoleculeSet_IMoleculeSet")
    public IReactionSet initiate(IMoleculeSet reactants, IMoleculeSet agents) throws CDKException{

        logger.debug("initiate reaction: HomolyticCleavageReaction");

        if (reactants.getMoleculeCount() != 1) {
            throw new CDKException("HomolyticCleavageReaction only expects one reactant");
        }
        if (agents != null) {
            throw new CDKException("HomolyticCleavageReaction don't expects agents");
        }

        IReactionSet setOfReactions = DefaultChemObjectBuilder.getInstance().newReactionSet();
        IMolecule reactant = reactants.getMolecule(0);

        /* if the parameter hasActiveCenter is not fixed yet, set the active centers*/
        IParameterReact ipr = super.getParameterClass(SetReactionCenter.class);
		if( ipr != null && !ipr.isSetParameter())
			setActiveCenters(reactant);

        Iterator<IBond> bondis = reactant.bonds().iterator();
        while (bondis.hasNext()) {
            IBond bondi = bondis.next();
            IAtom atom1 = bondi.getAtom(0);
            IAtom atom2 = bondi.getAtom(1);
            if(bondi.getFlag(CDKConstants.REACTIVE_CENTER)
					&& atom1.getFlag(CDKConstants.REACTIVE_CENTER) && atom2.getFlag(CDKConstants.REACTIVE_CENTER)
					&& (atom1.getFormalCharge() == CDKConstants.UNSET ? 0 : atom1.getFormalCharge()) == 0
					&& (atom2.getFormalCharge() == CDKConstants.UNSET ? 0 : atom2.getFormalCharge()) == 0
	 				&& reactant.getConnectedSingleElectronsCount(atom1) == 0 && reactant.getConnectedSingleElectronsCount(atom2) == 0){
            	
                ArrayList<IAtom> atomList = new ArrayList<IAtom>();
            	atomList.add(atom1);
            	atomList.add(atom2);
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

        return setOfReactions;
    }
    /**
     * set the active center for this molecule.
     * The active center will be those which correspond with A-B. If
     * the bond is simple, it will be broken forming two fragments.
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
        Iterator<IBond> bondis = reactant.bonds().iterator();
        while (bondis.hasNext()) {
        	 IBond bond = bondis.next();
             IAtom atom1 = bond.getAtom(0);
             IAtom atom2 = bond.getAtom(1);
             if((atom1.getFormalCharge() == CDKConstants.UNSET ? 0 : atom1.getFormalCharge()) == 0
 				&& (atom2.getFormalCharge() == CDKConstants.UNSET ? 0 : atom2.getFormalCharge()) == 0
 				&& reactant.getConnectedSingleElectronsCount(atom1) == 0 && reactant.getConnectedSingleElectronsCount(atom2) == 0){
            	 bond.setFlag(CDKConstants.REACTIVE_CENTER, true);
            	 atom1.setFlag(CDKConstants.REACTIVE_CENTER, true);
            	 atom2.setFlag(CDKConstants.REACTIVE_CENTER, true);
             }
        }
    }
}
