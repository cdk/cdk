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
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
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
import org.openscience.cdk.reaction.mechanism.HeterolyticCleavageMechanism;
import org.openscience.cdk.reaction.type.parameters.IParameterReact;
import org.openscience.cdk.reaction.type.parameters.SetReactionCenter;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>IReactionProcess which participate in movement resonance.
 * This reaction could be represented as [A+]-B => A| + [B+]. Due to
 * deficiency of charge of the atom A, the double bond is displaced to atom A.</p>
 * <p>Make sure that the molecule has the correspond lone pair electrons
 * for each atom. You can use the method: <pre> LonePairElectronChecker </pre>
 * <p>It is processed by the HeterolyticCleavageMechanism class</p>
 *
 * <pre>
 *  IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newAtomContainerSet();
 *  setOfReactants.addAtomContainer(new AtomContainer());
 *  IReactionProcess type = new SharingChargeSBReaction();
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
 * @cdk.created    2006-05-05
 * @cdk.module     reaction
 * @cdk.githash
 * @cdk.set        reaction-types
 *
 * @see HeterolyticCleavageMechanism
 **/
@TestClass(value = "org.openscience.cdk.reaction.type.SharingChargeSBReactionTest")
public class SharingChargeSBReaction extends ReactionEngine implements IReactionProcess {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(SharingChargeSBReaction.class);

    /**
     * Constructor of the SharingChargeSBReaction object.
     *
     */
    public SharingChargeSBReaction() {}

    /**
     *  Gets the specification attribute of the SharingChargeSBReaction object
     *
     *@return    The specification value
     */
    @TestMethod("testGetSpecification")
    @Override
    public ReactionSpecification getSpecification() {
        return new ReactionSpecification(
                "http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#SharingChargeSB", this
                        .getClass().getName(), "$Id$", "The Chemistry Development Kit");
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
    @TestMethod("testInitiate_IAtomContainerSet_IAtomContainerSet")
    @Override
    public IReactionSet initiate(IAtomContainerSet reactants, IAtomContainerSet agents) throws CDKException {

        logger.debug("initiate reaction: SharingChargeSBReaction");

        if (reactants.getAtomContainerCount() != 1) {
            throw new CDKException("SharingChargeSBReaction only expects one reactant");
        }
        if (agents != null) {
            throw new CDKException("SharingChargeSBReaction don't expects agents");
        }

        IReactionSet setOfReactions = reactants.getBuilder().newInstance(IReactionSet.class);
        IAtomContainer reactant = reactants.getAtomContainer(0);

        /*
         * if the parameter hasActiveCenter is not fixed yet, set the active
         * centers
         */
        IParameterReact ipr = super.getParameterClass(SetReactionCenter.class);
        if (ipr != null && !ipr.isSetParameter()) setActiveCenters(reactant);

        Iterator<IAtom> atomis = reactant.atoms().iterator();
        while (atomis.hasNext()) {
            IAtom atomi = atomis.next();

            if (atomi.getFlag(CDKConstants.REACTIVE_CENTER) && atomi.getFormalCharge() == 1) {

                Iterator<IBond> bondis = reactant.getConnectedBondsList(atomi).iterator();
                while (bondis.hasNext()) {
                    IBond bondi = bondis.next();
                    if (bondi.getFlag(CDKConstants.REACTIVE_CENTER) && bondi.getOrder() == IBond.Order.SINGLE) {

                        IAtom atomj = bondi.getConnectedAtom(atomi);
                        if (atomj.getFlag(CDKConstants.REACTIVE_CENTER) && atomj.getFormalCharge() == 0)
                            if (reactant.getConnectedSingleElectronsCount(atomj) == 0) {

                                ArrayList<IAtom> atomList = new ArrayList<IAtom>();
                                atomList.add(atomj);
                                atomList.add(atomi);
                                ArrayList<IBond> bondList = new ArrayList<IBond>();
                                bondList.add(bondi);

                                IAtomContainerSet moleculeSet = reactant.getBuilder().newInstance(
                                        IAtomContainerSet.class);
                                moleculeSet.addAtomContainer(reactant);
                                IReaction reaction = mechanism.initiate(moleculeSet, atomList, bondList);
                                if (reaction == null)
                                    continue;
                                else
                                    setOfReactions.addReaction(reaction);
                            }
                    }
                }
            }
        }
        return setOfReactions;

    }

    /**
     * set the active center for this molecule.
     * The active center will be those which correspond with [A+]-B.
     * <pre>
     * A: Atom with positive charge
     * -: single bond
     * B: Atom
     *  </pre>
     *
     * @param reactant The molecule to set the activity
     * @throws CDKException
     */
    private void setActiveCenters(IAtomContainer reactant) throws CDKException {
        Iterator<IAtom> atomis = reactant.atoms().iterator();
        while (atomis.hasNext()) {
            IAtom atomi = atomis.next();

            if (atomi.getFormalCharge() == 1) {

                Iterator<IBond> bondis = reactant.getConnectedBondsList(atomi).iterator();
                while (bondis.hasNext()) {
                    IBond bondi = bondis.next();
                    if (bondi.getOrder() == IBond.Order.SINGLE) {

                        IAtom atomj = bondi.getConnectedAtom(atomi);
                        if (atomj.getFormalCharge() == 0) if (reactant.getConnectedSingleElectronsCount(atomj) == 0) {
                            atomi.setFlag(CDKConstants.REACTIVE_CENTER, true);
                            bondi.setFlag(CDKConstants.REACTIVE_CENTER, true);
                            atomj.setFlag(CDKConstants.REACTIVE_CENTER, true);
                        }
                    }
                }
            }
        }
    }
}
