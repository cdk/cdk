/* Copyright (C) 2008 Miguel Rojas <miguelrojasch@users.sf.net>
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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionEngine;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.reaction.mechanism.RemovingSEofBMechanism;
import org.openscience.cdk.reaction.type.parameters.IParameterReact;
import org.openscience.cdk.reaction.type.parameters.SetReactionCenter;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>IReactionProcess which make an electron impact for pi-Bond Dissociation.</p>
 * <p>This reaction type is a representation of the processes which occurs in the mass spectrometer.</p>
 * <p>It is processed by the RemovingSEofPBMechanism class</p>
 *
 * <pre>
 *  IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newAtomContainerSet();
 *  setOfReactants.addAtomContainer(new AtomContainer());
 *  IReactionProcess type = new ElectronImpactPDBReaction();
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
 * @cdk.githash
 *
 * @see RemovingSEofBMechanism
 *
 **/
public class ElectronImpactPDBReaction extends ReactionEngine implements IReactionProcess {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(ElectronImpactPDBReaction.class);

    /**
     * Constructor of the ElectronImpactPDBReaction object.
     *
     */
    public ElectronImpactPDBReaction() {}

    /**
     *  Gets the specification attribute of the ElectronImpactPDBReaction object.
     *
     *@return    The specification value
     */
    @Override
    public ReactionSpecification getSpecification() {
        return new ReactionSpecification(
                "http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#ElectronImpactPDB", this
                        .getClass().getName(), "$Id$", "The Chemistry Development Kit");
    }

    /**
     *  Initiate process.
     *  It is needed to call the addExplicitHydrogensToSatisfyValency
     *  from the class tools.HydrogenAdder.
     *
     *
     *@exception  CDKException  Description of the Exception

     * @param  reactants         reactants of the reaction.
    * @param  agents            agents of the reaction (Must be in this case null).
     */
    @Override
    public IReactionSet initiate(IAtomContainerSet reactants, IAtomContainerSet agents) throws CDKException {

        logger.debug("initiate reaction: ElectronImpactPDBReaction");

        if (reactants.getAtomContainerCount() != 1) {
            throw new CDKException("ElectronImpactPDBReaction only expects one reactant");
        }
        if (agents != null) {
            throw new CDKException("ElectronImpactPDBReaction don't expects agents");
        }

        IReactionSet setOfReactions = reactants.getBuilder().newInstance(IReactionSet.class);
        IAtomContainer reactant = reactants.getAtomContainer(0);

        /*
         * if the parameter hasActiveCenter is not fixed yet, set the active
         * centers
         */
        IParameterReact ipr = super.getParameterClass(SetReactionCenter.class);
        if (ipr != null && !ipr.isSetParameter()) setActiveCenters(reactant);

        Iterator<IBond> bonds = reactant.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bondi = bonds.next();
            IAtom atom1 = bondi.getAtom(0);
            IAtom atom2 = bondi.getAtom(1);
            if (bondi.getFlag(CDKConstants.REACTIVE_CENTER)
                    && (bondi.getOrder() == IBond.Order.DOUBLE || bondi.getOrder() == IBond.Order.TRIPLE)
                    && atom1.getFlag(CDKConstants.REACTIVE_CENTER) && atom2.getFlag(CDKConstants.REACTIVE_CENTER)
                    && (atom1.getFormalCharge() == CDKConstants.UNSET ? 0 : atom1.getFormalCharge()) == 0
                    && (atom2.getFormalCharge() == CDKConstants.UNSET ? 0 : atom2.getFormalCharge()) == 0
                    && reactant.getConnectedSingleElectronsCount(atom1) == 0
                    && reactant.getConnectedSingleElectronsCount(atom2) == 0) {

                for (int j = 0; j < 2; j++) {

                    ArrayList<IAtom> atomList = new ArrayList<IAtom>();
                    if (j == 0) {
                        atomList.add(atom1);
                        atomList.add(atom2);
                    } else {
                        atomList.add(atom2);
                        atomList.add(atom1);
                    }
                    ArrayList<IBond> bondList = new ArrayList<IBond>();
                    bondList.add(bondi);

                    IAtomContainerSet moleculeSet = reactant.getBuilder().newInstance(IAtomContainerSet.class);
                    moleculeSet.addAtomContainer(reactant);
                    IReaction reaction = mechanism.initiate(moleculeSet, atomList, bondList);
                    if (reaction == null)
                        continue;
                    else
                        setOfReactions.addReaction(reaction);
                }

            }
        }
        return setOfReactions;

    }

    /**
     * Set the active center for this molecule. The active center will be double bonds.
     * As default is only those atoms without charge and between a double bond.
     *
     * @param reactant The molecule to set the activity
     * @throws CDKException
     */
    private void setActiveCenters(IAtomContainer reactant) throws CDKException {
        Iterator<IBond> bonds = reactant.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bondi = bonds.next();
            IAtom atom1 = bondi.getAtom(0);
            IAtom atom2 = bondi.getAtom(1);
            if ((bondi.getOrder() == IBond.Order.DOUBLE || bondi.getOrder() == IBond.Order.TRIPLE)
                    && (atom1.getFormalCharge() == CDKConstants.UNSET ? 0 : atom1.getFormalCharge()) == 0
                    && (atom2.getFormalCharge() == CDKConstants.UNSET ? 0 : atom2.getFormalCharge()) == 0
                    && reactant.getConnectedSingleElectronsCount(atom1) == 0
                    && reactant.getConnectedSingleElectronsCount(atom2) == 0) {
                bondi.setFlag(CDKConstants.REACTIVE_CENTER, true);
                atom1.setFlag(CDKConstants.REACTIVE_CENTER, true);
                atom2.setFlag(CDKConstants.REACTIVE_CENTER, true);
            }
        }
    }
}
