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
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionEngine;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.reaction.type.parameters.IParameterReact;
import org.openscience.cdk.reaction.type.parameters.SetReactionCenter;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.BondManipulator;

import java.util.Iterator;

/**
 * <p>IReactionProcess which tries to reproduce the delocalization of electrons
 *  which are unsaturated bonds from conjugated rings. Only is allowed those
 *  movements which produces from neutral to neutral structures and not take account the possible
 *  movements influenced from lone pairs, or empty orbitals. This movements are
 *  typically from rings without any access or deficiency of charge and have a
 *  even number of atoms. </p>
 *  <p>The reaction don't care if the product are the same in symmetry.</p>
 *  <pre>
 *  IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newAtomContainerSet();
 *  setOfReactants.addAtomContainer(new AtomContainer());
 *  IReactionProcess type = new PiBondingMovementReaction();
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
 * @cdk.created    2007-02-02
 * @cdk.module     reaction
 * @cdk.githash
 *
 **/
public class PiBondingMovementReaction extends ReactionEngine implements IReactionProcess {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(PiBondingMovementReaction.class);

    /**
     * Constructor of the PiBondingMovementReaction object
     *
     */
    public PiBondingMovementReaction() {}

    /**
     *  Gets the specification attribute of the PiBondingMovementReaction object
     *
     *@return    The specification value
     */
    @Override
    public ReactionSpecification getSpecification() {
        return new ReactionSpecification(
                "http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#PiBondingMovement", this
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

        logger.debug("initiate reaction: PiBondingMovementReaction");

        if (reactants.getAtomContainerCount() != 1) {
            throw new CDKException("PiBondingMovementReaction only expects one reactant");
        }
        if (agents != null) {
            throw new CDKException("PiBondingMovementReaction don't expects agents");
        }

        IReactionSet setOfReactions = reactants.getBuilder().newInstance(IReactionSet.class);
        IAtomContainer reactant = reactants.getAtomContainer(0);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactant);
        /*
         * if the parameter hasActiveCenter is not fixed yet, set the active
         * centers
         */
        IParameterReact ipr = super.getParameterClass(SetReactionCenter.class);
        if (ipr != null && !ipr.isSetParameter()) setActiveCenters(reactant);

        //		if((Boolean)paramsMap.get("lookingSymmetry")){
        //			Aromaticity.cdkLegacy().apply(reactant);
        //		}

        AllRingsFinder arf = new AllRingsFinder();
        IRingSet ringSet = arf.findAllRings(reactant);
        for (int ir = 0; ir < ringSet.getAtomContainerCount(); ir++) {
            IRing ring = (IRing) ringSet.getAtomContainer(ir);

            //only rings with even number of atoms
            int nrAtoms = ring.getAtomCount();
            if (nrAtoms % 2 == 0) {
                int nrSingleBonds = 0;
                for (IBond iBond : ring.bonds()) {
                    if (iBond.getOrder() == IBond.Order.SINGLE) nrSingleBonds++;
                }
                //if exactly half (nrAtoms/2==nrSingleBonds)
                if (nrSingleBonds != 0 && nrAtoms / 2 == nrSingleBonds) {
                    Iterator<IBond> bondfs = ring.bonds().iterator();
                    boolean ringCompletActive = false;
                    while (bondfs.hasNext()) {
                        if (bondfs.next().getFlag(CDKConstants.REACTIVE_CENTER))
                            ringCompletActive = true;
                        else {
                            ringCompletActive = false;
                            break;
                        }
                    }
                    if (!ringCompletActive) continue;

                    IReaction reaction = reactants.getBuilder().newInstance(IReaction.class);
                    reaction.addReactant(reactant);

                    IAtomContainer reactantCloned;
                    try {
                        reactantCloned = reactant.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new CDKException("Could not clone IAtomContainer!", e);
                    }

                    for (IBond bondi : ring.bonds()) {
                        int bondiP = reactant.indexOf(bondi);
                        if (bondi.getOrder() == IBond.Order.SINGLE)
                            BondManipulator.increaseBondOrder(reactantCloned.getBond(bondiP));
                        else
                            BondManipulator.decreaseBondOrder(reactantCloned.getBond(bondiP));

                    }

                    reaction.addProduct(reactantCloned);
                    setOfReactions.addReaction(reaction);
                }

            }
        }

        return setOfReactions;
    }

    /**
     * Set the active center for this molecule.
     * The active center will be those which correspond to a ring
     * with pi electrons with resonance.
     *
     * FIXME REACT: It could be possible that a ring is a super ring of others small rings
     *
     * @param reactant The molecule to set the activity
     */
    private void setActiveCenters(IAtomContainer reactant) throws CDKException {
        AllRingsFinder arf = new AllRingsFinder();
        IRingSet ringSet = arf.findAllRings(reactant);
        for (int ir = 0; ir < ringSet.getAtomContainerCount(); ir++) {
            IRing ring = (IRing) ringSet.getAtomContainer(ir);
            //only rings with even number of atoms
            int nrAtoms = ring.getAtomCount();
            if (nrAtoms % 2 == 0) {
                int nrSingleBonds = 0;
                for (IBond iBond : ring.bonds()) {
                    if (iBond.getOrder() == IBond.Order.SINGLE) nrSingleBonds++;
                }
                //if exactly half (nrAtoms/2==nrSingleBonds)
                if (nrSingleBonds != 0 && nrAtoms / 2 == nrSingleBonds) {
                    for (IBond iBond : ring.bonds())
                        iBond.setFlag(CDKConstants.REACTIVE_CENTER, true);

                }
            }
        }
    }
}
