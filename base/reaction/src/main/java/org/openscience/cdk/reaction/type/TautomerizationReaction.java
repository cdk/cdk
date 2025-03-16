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
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionEngine;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.reaction.mechanism.TautomerizationMechanism;
import org.openscience.cdk.reaction.type.parameters.IParameterReact;
import org.openscience.cdk.reaction.type.parameters.SetReactionCenter;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.util.ArrayList;

/**
 * <p>IReactionProcess which produces a tautomerization chemical reaction.
 * As most commonly encountered, this reaction results in the formal migration
 * of a hydrogen atom or proton, accompanied by a switch of a single bond and adjacent double bond</p>
 *
 * <pre>X=Y-Z-H =&gt; X(H)-Y=Z</pre>
 *
 * <p>Below you have an example how to initiate the mechanism.</p>
 * <p>It is processed by the HeterolyticCleavageMechanism class</p>
 * <pre>
 *  IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newAtomContainerSet();
 *  setOfReactants.addAtomContainer(new AtomContainer());
 *  IReactionProcess type = new TautomerizationReaction();
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
 * @cdk.githash
 *
 * @see TautomerizationMechanism
 **/
public class TautomerizationReaction extends ReactionEngine implements IReactionProcess {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(TautomerizationReaction.class);

    /**
     * Constructor of the TautomerizationReaction object.
     *
     */
    public TautomerizationReaction() {}

    /**
     *  Gets the specification attribute of the TautomerizationReaction object.
     *
     *@return    The specification value
     */
    @Override
    public ReactionSpecification getSpecification() {
        return new ReactionSpecification(
                "http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#Tautomerization", this
                        .getClass().getName(), "$Id$", "The Chemistry Development Kit");
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
    @Override
    public IReactionSet initiate(IAtomContainerSet reactants, IAtomContainerSet agents) throws CDKException {

        logger.debug("initiate reaction: TautomerizationReaction");

        if (reactants.getAtomContainerCount() != 1) {
            throw new CDKException("TautomerizationReaction only expects one reactant");
        }
        if (agents != null) {
            throw new CDKException("TautomerizationReaction don't expects agents");
        }

        IReactionSet setOfReactions = reactants.getBuilder().newInstance(IReactionSet.class);
        IAtomContainer reactant = reactants.getAtomContainer(0);

        /*
         * if the parameter hasActiveCenter is not fixed yet, set the active
         * centers
         */
        IParameterReact ipr = super.getParameterClass(SetReactionCenter.class);
        if (ipr != null && !ipr.isSetParameter()) setActiveCenters(reactant);

        // Atom pos 1
        for (IAtom atomi : reactant.atoms()) {
            if (atomi.getFlag(IChemObject.REACTIVE_CENTER)
                    && (atomi.getFormalCharge() == CDKConstants.UNSET ? 0 : atomi.getFormalCharge()) == 0
                    && reactant.getConnectedSingleElectronsCount(atomi) == 0) {
                for (IBond bondi : reactant.getConnectedBondsList(atomi)) {
                    if (bondi.getFlag(IChemObject.REACTIVE_CENTER) && bondi.getOrder() == IBond.Order.DOUBLE) {
                        IAtom atomj = bondi.getOther(atomi); // Atom pos 2
                        if (atomj.getFlag(IChemObject.REACTIVE_CENTER)
                                && (atomj.getFormalCharge() == CDKConstants.UNSET ? 0 : atomj.getFormalCharge()) == 0
                                && reactant.getConnectedSingleElectronsCount(atomj) == 0) {
                            for (IBond bondj : reactant.getConnectedBondsList(atomj)) {
                                if (bondj.equals(bondi)) continue;
                                if (bondj.getFlag(IChemObject.REACTIVE_CENTER)
                                        && bondj.getOrder() == IBond.Order.SINGLE) {
                                    IAtom atomk = bondj.getOther(atomj); // Atom pos 3
                                    if (atomk.getFlag(IChemObject.REACTIVE_CENTER)
                                            && (atomk.getFormalCharge() == CDKConstants.UNSET ? 0 : atomk
                                            .getFormalCharge()) == 0
                                            && reactant.getConnectedSingleElectronsCount(atomk) == 0) {
                                        for (IBond bondk : reactant.getConnectedBondsList(atomk)) {
                                            if (bondk.equals(bondj)) continue;
                                            if (bondk.getFlag(IChemObject.REACTIVE_CENTER)
                                                    && bondk.getOrder() == IBond.Order.SINGLE) {
                                                IAtom atoml = bondk.getOther(atomk); // Atom pos 4
                                                if (atoml.getFlag(IChemObject.REACTIVE_CENTER)
                                                        && atoml.getAtomicNumber() == IElement.H) {

                                                    ArrayList<IAtom> atomList = new ArrayList<>();
                                                    atomList.add(atomi);
                                                    atomList.add(atomj);
                                                    atomList.add(atomk);
                                                    atomList.add(atoml);
                                                    ArrayList<IBond> bondList = new ArrayList<>();
                                                    bondList.add(bondi);
                                                    bondList.add(bondj);
                                                    bondList.add(bondk);

                                                    IAtomContainerSet moleculeSet = reactant.getBuilder().newInstance(
                                                            IAtomContainerSet.class);
                                                    moleculeSet.addAtomContainer(reactant);
                                                    IReaction reaction = mechanism.initiate(moleculeSet, atomList,
                                                            bondList);
                                                    if (reaction == null)
                                                        continue;
                                                    else
                                                        setOfReactions.addReaction(reaction);

                                                    break; // because of the others atoms are hydrogen too.
                                                }
                                            }

                                        }
                                    }
                                }
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
     * The active center will be those which correspond with X=Y-Z-H.
     * <pre>
     * X: Atom
     * =: bond
     * Y: Atom
     * -: bond
     * Z: Atom
     * -: bond
     * H: Atom
     *  </pre>
     *
     * @param reactant The molecule to set the activity
     * @throws CDKException
     */
    private void setActiveCenters(IAtomContainer reactant) throws CDKException {
        // Atom pos 1
        for (IAtom atomi : reactant.atoms()) {
            if ((atomi.getFormalCharge() == CDKConstants.UNSET ? 0 : atomi.getFormalCharge()) == 0
                    && reactant.getConnectedSingleElectronsCount(atomi) == 0) {
                for (IBond bondi : reactant.getConnectedBondsList(atomi)) {
                    if (bondi.getOrder() == IBond.Order.DOUBLE) {
                        IAtom atomj = bondi.getOther(atomi); // Atom pos 2
                        if ((atomj.getFormalCharge() == CDKConstants.UNSET ? 0 : atomj.getFormalCharge()) == 0
                                && reactant.getConnectedSingleElectronsCount(atomj) == 0) {
                            for (IBond bondj : reactant.getConnectedBondsList(atomj)) {
                                if (bondj.equals(bondi)) continue;
                                if (bondj.getOrder() == IBond.Order.SINGLE) {
                                    IAtom atomk = bondj.getOther(atomj); // Atom pos 3
                                    if ((atomk.getFormalCharge() == CDKConstants.UNSET ? 0 : atomk.getFormalCharge()) == 0
                                            && reactant.getConnectedSingleElectronsCount(atomk) == 0) {
                                        for (IBond bondk : reactant.getConnectedBondsList(atomk)) {
                                            if (bondk.equals(bondj)) continue;
                                            if (bondk.getOrder() == IBond.Order.SINGLE) {
                                                IAtom atoml = bondk.getOther(atomk); // Atom pos 4
                                                if (atoml.getAtomicNumber() == IElement.H) {
                                                    atomi.setFlag(IChemObject.REACTIVE_CENTER, true);
                                                    atomj.setFlag(IChemObject.REACTIVE_CENTER, true);
                                                    atomk.setFlag(IChemObject.REACTIVE_CENTER, true);
                                                    atoml.setFlag(IChemObject.REACTIVE_CENTER, true);
                                                    bondi.setFlag(IChemObject.REACTIVE_CENTER, true);
                                                    bondj.setFlag(IChemObject.REACTIVE_CENTER, true);
                                                    bondk.setFlag(IChemObject.REACTIVE_CENTER, true);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
