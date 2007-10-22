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


import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.tools.LoggingTool;

import java.util.Iterator;

/**
 * <p>IReactionProcess which make an alectron impact for pi-Bond Dissociation.</p>
 * This reaction type is a representation of the processes which occure in the mass spectrometer.</p>
 * 
 * <pre>
 *  IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new RearrangementAnion1Reaction();
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
 * @cdk.created    2006-04-01
 * @cdk.module     reaction
 * @cdk.svnrev  $Revision: 9162 $
 * @cdk.set        reaction-types
 * 
 **/
public class ElectronImpactPDBReaction implements IReactionProcess{
    private LoggingTool logger;
    private boolean hasActiveCenter;
    private static final int BONDTOFLAG1 = 8;

    /**
     * Constructor of the ElectronImpactPDBReaction object
     *
     */
    public ElectronImpactPDBReaction(){
        logger = new LoggingTool(this);
    }
    /**
     *  Gets the specification attribute of the ElectronImpactPDBReaction object
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
     *  Sets the parameters attribute of the ElectronImpactPDBReaction object
     *
     *@param  params            The parameter is if the molecule has already fixed the center active or not. It
     *							should be set before to inize the reaction with a setFlag:  CDKConstants.REACTIVE_CENTER
     *@exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1) {
            throw new CDKException("ElectronImpactPDBReaction only expects one parameter");
        }
        if (!(params[0] instanceof Boolean)) {
            throw new CDKException("The parameter must be of type boolean");
        }
        hasActiveCenter = ((Boolean) params[0]).booleanValue();
    }


    /**
     *  Gets the parameters attribute of the ElectronImpactPDBReaction object
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

        logger.debug("initiate reaction: ElectronImpactPDBReaction");

        if (reactants.getMoleculeCount() != 1) {
            throw new CDKException("ElectronImpactPDBReaction only expects one reactant");
        }
        if (agents != null) {
            throw new CDKException("ElectronImpactPDBReaction don't expects agents");
        }

        IReactionSet setOfReactions = DefaultChemObjectBuilder.getInstance().newReactionSet();

        /* if the parameter hasActiveCenter is not fixed yet, set the active centers*/
        if(!hasActiveCenter){
            setActiveCenters(reactants.getMolecule(0));
        }

        Iterator bonds = reactants.getMolecule(0).bonds();
        while (bonds.hasNext()) {
            IBond bond = (IBond) bonds.next();

            if(bond.getFlag(CDKConstants.REACTIVE_CENTER) && bond.getOrder() > 1){
                /**/
                for (int j = 0; j < 2; j++){
                    IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
                    reaction.addReactant(reactants.getMolecule(0));
                    IMolecule reactant = reaction.getReactants().getMolecule(0);

                    int posA1 = reactant.getAtomNumber(bond.getAtom(0));
                    int posA2 = reactant.getAtomNumber(bond.getAtom(1));
                    cleanFlagBOND(reactants.getMolecule(0));
                    int posB1 = 0;
                    bond.setFlag(BONDTOFLAG1, true);
                    IMolecule reactantCloned;
                    try {
                        reactantCloned = (IMolecule) reactant.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new CDKException("Could not clone IMolecule!", e);
                    }

                    for(int l = 0 ; l<reactantCloned.getBondCount();l++){
                        if(reactantCloned.getBond(l).getFlag(BONDTOFLAG1)){
                            double order = reactantCloned.getBond(l).getOrder();
                            reactantCloned.getBond(l).setOrder(order - 1);
                            posB1 = reactantCloned.getBondNumber(reactantCloned.getBond(l));
                            break;
                        }
                    }

                    if (j == 0){
                        reactantCloned.getAtom(posA1).setFormalCharge(1);
                        reactantCloned.addSingleElectron(
                                new SingleElectron(reactantCloned.getAtom(posA2)));
                    } else{
                        reactantCloned.getAtom(posA2).setFormalCharge(1);
                        reactantCloned.addSingleElectron(
                                new SingleElectron(reactantCloned.getAtom(posA1)));
                    }

                    /* mapping */
                    IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(bond, reactantCloned.getBond(posB1));
                    reaction.addMapping(mapping);
                    mapping = DefaultChemObjectBuilder.getInstance().newMapping(bond.getAtom(0), reactantCloned.getAtom(posA1));
                    reaction.addMapping(mapping);
                    mapping = DefaultChemObjectBuilder.getInstance().newMapping(bond.getAtom(1), reactantCloned.getAtom(posA2));
                    reaction.addMapping(mapping);


                    reaction.addProduct(reactantCloned);
                    setOfReactions.addReaction(reaction);

                    bond.setFlag(BONDTOFLAG1, false);
                }
            }
        }
        return setOfReactions;


    }
    /**
     * set the active center for this molecule. The active center will be double bonds.
     *
     * @param reactant The molecule to set the activity
     * @throws CDKException
     */
    private void setActiveCenters(IMolecule reactant) throws CDKException {
        Iterator bonds = reactant.bonds();
        IAtom atom0;
        IAtom atom1;
        while (bonds.hasNext()) {
            IBond bond = (IBond) bonds.next();
            atom0 = bond.getAtom(0);
            atom1 = bond.getAtom(1);
            if (bond.getOrder() > 1 &&
                    atom0.getSymbol().equals("C") &&
                    atom1.getSymbol().equals("C")) {
                bond.setFlag(CDKConstants.REACTIVE_CENTER, true);
                atom0.setFlag(CDKConstants.REACTIVE_CENTER, true);
                atom1.setFlag(CDKConstants.REACTIVE_CENTER, true);
            }
        }
    }
    /**
     *  Gets the parameterNames attribute of the ElectronImpactPDBReaction object
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "hasActiveCenter";
        return params;
    }


    /**
     *  Gets the parameterType attribute of the ElectronImpactPDBReaction object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
        return new Boolean(false);
    }
    /**
     * clean the flags CDKConstants.REACTIVE_CENTER from the molecule
     * 
     * @param mol
     */
    public void cleanFlagBOND(IAtomContainer ac){
        for(int j = 0 ; j < ac.getBondCount(); j++){
            ac.getBond(j).setFlag(BONDTOFLAG1, false);
        }
    }
}
