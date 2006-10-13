/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) $
 *  $Revision: 5855 $
 *
 *  Copyright (C) 2004-2006  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.tools.LoggingTool;

/**
 * <p>IReactionProcess which participate mass spectrum process. Homolitic dissocitation. 
 * This reaction could be represented as A-B-[c*] => [A*] + B=C.</p>
 * <p>Make sure that the molecule has the corresponend lone pair electrons
 * for each atom. You can use the method: <pre> LonePairElectronChecker </pre>
 * 
 * <pre>
 *  IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new RadicalSiteInitiationReaction();
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
 * @cdk.created    2006-05-05
 * @cdk.module     reaction
 * @cdk.set        reaction-types
 * 
 **/
public class RadicalSiteInitiationReaction implements IReactionProcess{
	private LoggingTool logger;
	private boolean hasActiveCenter;
	private static final int BONDTOFLAG = 8;
	
	/**
	 * Constructor of the RadicalSiteInitiationReaction object
	 *
	 */
	public RadicalSiteInitiationReaction(){
		logger = new LoggingTool(this);
	}
	/**
	 *  Gets the specification attribute of the RadicalSiteInitiationReaction object
	 *
	 *@return    The specification value
	 */
	public ReactionSpecification getSpecification() {
		return new ReactionSpecification(
				"http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#RearrangementRadical3Reaction",
				this.getClass().getName(),
				"$Id: RadicalSiteInitiationReaction.java,v 1.6 2006/04/01 08:26:47 mrc Exp $",
				"The Chemistry Development Kit");
	}
	
	/**
	 *  Sets the parameters attribute of the RadicalSiteInitiationReaction object
	 *
	 *@param  params            The parameter is if the molecule has already fixed the center active or not. It 
	 *							should be set before to inize the reaction with a setFlag:  CDKConstants.REACTIVE_CENTER
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("RadicalSiteInitiationReaction only expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The parameter 1 must be of type boolean");
		}
		hasActiveCenter = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the RadicalSiteInitiationReaction object
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
	 *
	 *@param  reactants         reactants of the reaction.
	 *@param  agents            agents of the reaction (Must be in this case null).
	 *
	 *@exception  CDKException  Description of the Exception
	 */
	public IReactionSet initiate(IMoleculeSet reactants, IMoleculeSet agents) throws CDKException{
		logger.debug("initiate reaction: RadicalSiteInitiationReaction");
		
		if (reactants.getMoleculeCount() != 1) {
			throw new CDKException("RadicalSiteInitiationReaction only expects one reactant");
		}
		if (agents != null) {
			throw new CDKException("RadicalSiteInitiationReaction don't expects agents");
		}
		
		IReactionSet setOfReactions = reactants.getBuilder().newReactionSet();
		IMolecule reactant = reactants.getMolecule(0);

		/* if the parameter hasActiveCenter is not fixed yet, set the active centers*/
		if(!hasActiveCenter){
			setActiveCenters(reactant);
		}
		
		IMolecule reactant0 = reactants.getMolecule(0);
		IAtom atomi = null;
		IBond bondj;
		IBond bondk;
		for(int i = 0 ; i < reactant0.getAtomCount() ; i++){
			atomi = reactant0.getAtom(i);
			if(atomi.getFlag(CDKConstants.REACTIVE_CENTER)&& reactant.getSingleElectron(atomi).length == 1 ){
				
				java.util.List bonds = reactant.getConnectedBondsList(atomi);
				
				for(int j = 0 ; j < bonds.size() ; j++){
					bondj = (IBond)bonds.get(j);
					if(bondj.getFlag(CDKConstants.REACTIVE_CENTER)&& bondj.getOrder() < 3.0 ){
						IAtom atom = bondj.getConnectedAtom(reactant.getAtom(i));
						if(atom.getFormalCharge() != 0)
							continue;
						java.util.List bondsI = reactant.getConnectedBondsList(atom);
						for(int k = 0 ; k < bondsI.size() ; k++){
							bondk = (IBond)bondsI.get(k);
							if(bondk.getFlag(CDKConstants.REACTIVE_CENTER) && bondk.getOrder() ==  1.0 && !bondk.equals(bondj)){
								IAtom atomConn = bondk.getConnectedAtom(atom);
								if(atomConn.getFlag(CDKConstants.REACTIVE_CENTER) && atomConn.getFormalCharge() == 0
										&& !atomConn.equals(atomi) && !atomConn.getSymbol().equals("H")){
									
									IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
									reaction.addReactant(reactant);
									
									/* positions atoms and bonds */
									int atom0P = reactant.getAtomNumber(atomi);
									int bond1P = 0;/*reactant.getBondNumber(bondj);*/
									cleanFlagBOND(reactants.getMolecule(0));
									bondj.setFlag(BONDTOFLAG, true);
									int bond2P = reactant.getBondNumber(bondk);
									int atom1P = reactant.getAtomNumber(atom);
									int atom2P = reactant.getAtomNumber(atomConn);
									/* action */
									IMolecule acCloned;
									try {
										acCloned = (IMolecule) reactant.clone();
									} catch (CloneNotSupportedException e) {
										throw new CDKException("Could not clone IMolecule!", e);
									}
									
									ISingleElectron[] selectron = acCloned.getSingleElectron(acCloned.getAtom(atom0P));
									acCloned.removeElectronContainer(selectron[selectron.length-1]);
									selectron = acCloned.getSingleElectron(acCloned.getAtom(atom0P));
									
									acCloned.addElectronContainer(new SingleElectron(acCloned.getAtom(atom2P)));	
									
									double order = 0;
									for(int l = 0 ; l < acCloned.getBondCount();l++){
										if(acCloned.getBond(l).getFlag(BONDTOFLAG)){
											order = acCloned.getBond(l).getOrder();
											acCloned.getBond(l).setOrder(order+1);
											bond1P = acCloned.getBondNumber(acCloned.getBond(l));
											break;
										}
									}

//									double order = acCloned.getBond(bond1P).getOrder();
//									acCloned.getBond(bond1P).setOrder(order+1);

									acCloned.removeElectronContainer(bond2P);
									
									
									/* mapping */
									IMapping mapping = atom.getBuilder().newMapping(atomi, acCloned.getAtom(atom0P));
							        reaction.addMapping(mapping);
							        mapping = atom.getBuilder().newMapping(atom, acCloned.getAtom(atom1P));
							        reaction.addMapping(mapping);
							        mapping = atom.getBuilder().newMapping(atomConn, acCloned.getAtom(atom2P));
							        reaction.addMapping(mapping);
							        mapping = atom.getBuilder().newMapping(bondj, acCloned.getBond(bond1P));
							        reaction.addMapping(mapping);
							        /*breaked bond*/
//							        mapping = atom.getBuilder().newMapping(bondk, acCloned.getBond(bond2P));
//							        reaction.addMapping(mapping);
							        
									IMoleculeSet moleculeSet = ConnectivityChecker.partitionIntoMolecules(acCloned);
									for(int z = 0; z < moleculeSet.getAtomContainerCount() ; z++){
										reaction.addProduct(moleculeSet.getMolecule(z));
									}
									
									setOfReactions.addReaction(reaction);

									bondj.setFlag(BONDTOFLAG, true);
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
	 * The active center will be those which correspond with A-B-[C*]. 
	 * <pre>
	 * A: Atom
	 * -: bond
	 * B: Atom
	 * -: bond
	 * C: Atom with single electron
	 *  </pre>
	 * 
	 * @param reactant The molecule to set the activity
	 * @throws CDKException 
	 */
	private void setActiveCenters(IMolecule reactant) throws CDKException {
		IAtom atomi = null;
		IBond bondj = null;
		IBond bondk = null;
		for(int i = 0 ; i < reactant.getAtomCount() ; i++) {
			atomi = reactant.getAtom(i);
			if(reactant.getSingleElectron(atomi).length == 1 ){
				java.util.List bonds = reactant.getConnectedBondsList(atomi);
				for(int j = 0 ; j < bonds.size() ; j++){
					bondj = (IBond)bonds.get(j);
					if(bondj.getOrder() < 3.0){
						IAtom atom = bondj.getConnectedAtom(atomi);
						if(atom.getFormalCharge() != 0)
							continue;
						java.util.List bondsI = reactant.getConnectedBondsList(atom);
						for(int k = 0 ; k < bondsI.size() ; k++){
							bondk = (IBond)bondsI.get(k);
							if(bondk.getOrder() == 1 && !bondk.equals(bondj)){
							IAtom atomConn = bondk.getConnectedAtom(atom);
							if(atomConn.getFormalCharge() == 0 && !atomConn.getSymbol().equals("H")){
								atomi.setFlag(CDKConstants.REACTIVE_CENTER,true);
								atom.setFlag(CDKConstants.REACTIVE_CENTER,true);
								atomConn.setFlag(CDKConstants.REACTIVE_CENTER,true);
								bondj.setFlag(CDKConstants.REACTIVE_CENTER,true);
								bondk.setFlag(CDKConstants.REACTIVE_CENTER,true); 
							}
						}
						}
					}
				}
			}
		}
	}
	/**
	 *  Gets the parameterNames attribute of the RadicalSiteInitiationReaction object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "hasActiveCenter";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the RadicalSiteInitiationReaction object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return new Boolean(false);
	}
	/**
     * clean the flags BONDTOFLAG from the molecule
     * 
	 * @param mol
	 */
	public void cleanFlagBOND(IAtomContainer ac){
		for(int j = 0 ; j < ac.getBondCount(); j++)
			ac.getBond(j).setFlag(BONDTOFLAG, false);
	}
}
