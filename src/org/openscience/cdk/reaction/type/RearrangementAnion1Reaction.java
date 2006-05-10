package org.openscience.cdk.reaction.type;


import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.interfaces.ISetOfReactions;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * <p>IReactionProcess which participate in movement resonance. 
 * This reaction could be represented as [A-]-B[+] => A=B. Due to 
 * deficiency of charge of the atom A, the lone pair electron of the atom A is 
 * desplaced creating a double bond.</p>
 * <p>Make sure that the molecule has the corresponend lone pair electrons
 * for each atom. You can use the method: <pre> LonePairElectronChecker </pre>
 * 
 * <pre>
 *  ISetOfMolecules setOfReactants = DefaultChemObjectBuilder.getInstance().newSetOfMolecules();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new RearrangementAnion1Reaction();
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
 * @cdk.dictref    reaction-types:rearrangementCharge
 * 
 **/
public class RearrangementAnion1Reaction implements IReactionProcess{
	private LoggingTool logger;
	private boolean hasActiveCenter;

	/**
	 * Constructor of the RearrangementAnion1Reaction object
	 *
	 */
	public RearrangementAnion1Reaction(){
		logger = new LoggingTool(this);
	}
	/**
	 *  Gets the specification attribute of the RearrangementAnion1Reaction object
	 *
	 *@return    The specification value
	 */
	public ReactionSpecification getSpecification() {
		return new ReactionSpecification(
				"http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#RearrangementAnion1Reaction",
				this.getClass().getName(),
				"$Id: RearrangementAnion1Reaction.java,v 1.6 2006/04/01 08:26:47 mrc Exp $",
				"The Chemistry Development Kit");
	}
	
	/**
	 *  Sets the parameters attribute of the RearrangementAnion1Reaction object
	 *
	 *@param  params            The parameter is if the molecule has already fixed the center active or not. It 
	 *							should be set before to inize the reaction with a setFlag:  CDKConstants.REACTIVE_CENTER
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("RearrangementAnion1Reaction only expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The parameter 1 must be of type boolean");
		}
		hasActiveCenter = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the RearrangementAnion1Reaction object
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
	public ISetOfReactions initiate(ISetOfMolecules reactants, ISetOfMolecules agents) throws CDKException{

		logger.debug("initiate reaction: RearrangementAnion1Reaction");
		
		if (reactants.getMoleculeCount() != 1) {
			throw new CDKException("RearrangementAnion1Reaction only expects one reactant");
		}
		if (agents != null) {
			throw new CDKException("RearrangementAnion1Reaction don't expects agents");
		}
		
		ISetOfReactions setOfReactions = DefaultChemObjectBuilder.getInstance().newSetOfReactions();
		IMolecule reactant = reactants.getMolecule(0);
		
		/* if the parameter hasActiveCenter is not fixed yet, set the active centers*/
		if(!hasActiveCenter){
			setActiveCenters(reactant);
		}
		/*control of the volume of number of charge - it will be made possible if
		 * the atoms[i] is the only atom with charge*/
		int negCharge = AtomContainerManipulator.getTotalNegativeFormalCharge((IAtomContainer)reactant);
		int posCharge = AtomContainerManipulator.getTotalPositiveFormalCharge((IAtomContainer)reactant);
		if(posCharge+Math.abs(negCharge) > 2)
			return setOfReactions;
		
		IAtom[] atoms = reactants.getMolecule(0).getAtoms();
		for(int i = 0 ; i < atoms.length ; i++){
			if(atoms[i].getFlag(CDKConstants.REACTIVE_CENTER) && atoms[i].getFormalCharge() == -1
					&& reactant.getLonePairCount(atoms[i])  > 1  ){
				IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
				reaction.addReactant(reactant);
				
				IBond[] bonds = reactant.getConnectedBonds(atoms[i]);
				
				for(int j = 0 ; j < bonds.length ; j++){
					if(bonds[j].getFlag(CDKConstants.REACTIVE_CENTER) && bonds[j].getOrder() == 1.0){
						IAtom atom1 = bonds[j].getConnectedAtom(atoms[i]);
						
						if(atom1.getFlag(CDKConstants.REACTIVE_CENTER) && atom1.getFormalCharge() == 1){
							/* positions atoms and bonds */
							int atom0P = reactant.getAtomNumber(atoms[i]);
							int bond1P = reactant.getBondNumber(bonds[j]);
							int atom1P = reactant.getAtomNumber(atom1);
							
							/* action */
							IAtomContainer acCloned;
							try {
								acCloned = (IAtomContainer)reactant.clone();
							} catch (CloneNotSupportedException e) {
								throw new CDKException("Could not clone IMolecule!", e);
							}
							
							int charge = acCloned.getAtomAt(atom0P).getFormalCharge();
							acCloned.getAtomAt(atom0P).setFormalCharge(charge+1);
							
							double order = acCloned.getBondAt(bond1P).getOrder();
							acCloned.getBondAt(bond1P).setOrder(order+1);
							
							charge = acCloned.getAtomAt(atom1P).getFormalCharge();
							acCloned.getAtomAt(atom1P).setFormalCharge(charge-1);
	
							ILonePair[] selectron = acCloned.getLonePairs(acCloned.getAtomAt(atom0P));
							acCloned.removeElectronContainer(selectron[0]);
							
							/* mapping */
							IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(atoms[i], acCloned.getAtomAt(atom0P));
					        reaction.addMapping(mapping);
					        mapping = DefaultChemObjectBuilder.getInstance().newMapping(bonds[j], acCloned.getBondAt(bond1P));
					        reaction.addMapping(mapping);
					        mapping = DefaultChemObjectBuilder.getInstance().newMapping(atom1, acCloned.getAtomAt(atom1P));
					        reaction.addMapping(mapping);
					        
							reaction.addProduct((IMolecule) acCloned);
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
	 * The active center will be those which correspond with [A-]-B[+]. 
	 * <pre>
	 * A: Atom with negative charge (Moreover it contains lone pair electrons)
	 * -: Single bond
	 * B: Atom with positive charge
	 *  </pre>
	 * 
	 * @param reactant The molecule to set the activity
	 * @throws CDKException 
	 */
	private void setActiveCenters(IMolecule reactant) throws CDKException {
		IAtom[] atoms = reactant.getAtoms();
		for(int i = 0 ; i < atoms.length ; i++)
			if(atoms[i].getFormalCharge() == -1 && reactant.getLonePairCount(atoms[i]) > 1 ){
				IBond[] bonds = reactant.getConnectedBonds(atoms[i]);
				for(int j = 0 ; j < bonds.length ; j++){
					if(bonds[j].getOrder() == 1.0){
						IAtom atom = bonds[j].getConnectedAtom(atoms[i]);
						if(atom.getFormalCharge() == 1 ){
							atoms[i].setFlag(CDKConstants.REACTIVE_CENTER,true);
							atom.setFlag(CDKConstants.REACTIVE_CENTER,true);
							bonds[j].setFlag(CDKConstants.REACTIVE_CENTER,true);
						}
					}
				}
			}
	}
	/**
	 *  Gets the parameterNames attribute of the RearrangementAnion1Reaction object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "hasActiveCenter";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the RearrangementAnion1Reaction object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return new Boolean(false);
	}
}
