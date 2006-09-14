package org.openscience.cdk.reaction.type;


import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
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
 *  ISetOfMolecules setOfReactants = DefaultChemObjectBuilder.getInstance().newSetOfMolecules();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new RadicalSiteInitiationReaction();
 *  Object[] params = {Boolean.FALSE};
    type.setParameters(params);
 *  ISetOfReactions setOfReactions = type.initiate(setOfReactants, null);
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
		
		IAtom[] atoms = reactants.getMolecule(0).getAtoms();
		for(int i = 0 ; i < atoms.length ; i++){
			if(atoms[i].getFlag(CDKConstants.REACTIVE_CENTER)&& reactant.getSingleElectron(atoms[i]).length == 1 ){
				
				IBond[] bonds = reactant.getConnectedBonds(atoms[i]);
				
				for(int j = 0 ; j < bonds.length ; j++){
					if(bonds[j].getFlag(CDKConstants.REACTIVE_CENTER)&& bonds[j].getOrder() < 3.0 ){
						IAtom atom = bonds[j].getConnectedAtom(reactant.getAtom(i));
						if(atom.getFormalCharge() != 0)
							continue;
						IBond[] bondsI = reactant.getConnectedBonds(atom);
						for(int k = 0 ; k < bondsI.length ; k++){
							if(bondsI[k].getFlag(CDKConstants.REACTIVE_CENTER) && bondsI[k].getOrder() ==  1.0 && !bondsI[k].equals(bonds[j])){
								IAtom atomConn = bondsI[k].getConnectedAtom(atom);
								if(atomConn.getFlag(CDKConstants.REACTIVE_CENTER) && atomConn.getFormalCharge() == 0
										&& !atomConn.equals(atoms[i]) && !atomConn.getSymbol().equals("H")){
									
									IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
									reaction.addReactant(reactant);
									
									/* positions atoms and bonds */
									int atom0P = reactant.getAtomNumber(atoms[i]);
									int bond1P = reactant.getBondNumber(bonds[j]);
									int bond2P = reactant.getBondNumber(bondsI[k]);
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
									acCloned.removeElectronContainer(selectron[0]);
									
									acCloned.addElectronContainer(new SingleElectron(acCloned.getAtom(atom2P)));	
									
									
									double order = acCloned.getBond(bond1P).getOrder();
									acCloned.getBond(bond1P).setOrder(order+1);

									acCloned.removeElectronContainer(bond2P);
									
									

									/* mapping */
									IMapping mapping = atom.getBuilder().newMapping(atoms[i], acCloned.getAtom(atom0P));
							        reaction.addMapping(mapping);
							        mapping = atom.getBuilder().newMapping(atom, acCloned.getAtom(atom1P));
							        reaction.addMapping(mapping);
							        mapping = atom.getBuilder().newMapping(atomConn, acCloned.getAtom(atom2P));
							        reaction.addMapping(mapping);
							        mapping = atom.getBuilder().newMapping(bonds[j], acCloned.getBond(bond1P));
							        reaction.addMapping(mapping);
							        /*breaked bond*/
//							        mapping = atom.getBuilder().newMapping(bondsI[k], acCloned.getBond(bond2P));
//							        reaction.addMapping(mapping);
							        
									IMoleculeSet moleculeSet = ConnectivityChecker.partitionIntoMolecules(acCloned);
									for(int z = 0; z < moleculeSet.getAtomContainerCount() ; z++)
										reaction.addProduct(moleculeSet.getMolecule(z));
									
								
									
									
									setOfReactions.addReaction(reaction);
									
									
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
		IAtom[] atoms = reactant.getAtoms();
		for(int i = 0 ; i < atoms.length ; i++)
			if(reactant.getSingleElectron(atoms[i]).length == 1 ){
				IBond[] bonds = reactant.getConnectedBonds(atoms[i]);
				for(int j = 0 ; j < bonds.length ; j++){
					if(bonds[j].getOrder() < 3.0){
						IAtom atom = bonds[j].getConnectedAtom(atoms[i]);
						if(atom.getFormalCharge() != 0)
							continue;
						IBond[] bondsI = reactant.getConnectedBonds(atom);
						for(int k = 0 ; k < bondsI.length ; k++){
							if(bondsI[k].getOrder() == 1 && !bondsI[k].equals(bonds[j])){
							IAtom atomConn = bondsI[k].getConnectedAtom(atom);
							if(atomConn.getFormalCharge() == 0 && !atomConn.getSymbol().equals("H")){
								atoms[i].setFlag(CDKConstants.REACTIVE_CENTER,true);
								atom.setFlag(CDKConstants.REACTIVE_CENTER,true);
								atomConn.setFlag(CDKConstants.REACTIVE_CENTER,true);
								bonds[j].setFlag(CDKConstants.REACTIVE_CENTER,true);
								bondsI[k].setFlag(CDKConstants.REACTIVE_CENTER,true); 
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
}
