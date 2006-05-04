package org.openscience.cdk.reaction.type;


import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.interfaces.ISetOfReactions;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.tools.LoggingTool;

/**
 * <p>IReactionProcess which participate in movement resonance. 
 * This reaction could be represented as A=B => [A+]-[B-] or also [A-]-[B+]. 
 * </p>
 * 
 * <pre>
 *  ISetOfMolecules setOfReactants = DefaultChemObjectBuilder.getInstance().newSetOfMolecules();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new DisplacementChargeReaction();
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
public class DisplacementChargeReaction implements IReactionProcess{
	private LoggingTool logger;
	private boolean hasActiveCenter;

	/**
	 * Constructor of the DisplacementChargeReaction object
	 *
	 */
	public DisplacementChargeReaction(){
		logger = new LoggingTool(this);
	}
	/**
	 *  Gets the specification attribute of the DisplacementChargeReaction object
	 *
	 *@return    The specification value
	 */
	public ReactionSpecification getSpecification() {
		return new ReactionSpecification(
				"http://",
				this.getClass().getName(),
				"$Id: DisplacementChargeReaction.java,v 1.6 2006/04/01 08:26:47 mrc Exp $",
				"The Chemistry Development Kit");
	}
	
	/**
	 *  Sets the parameters attribute of the DisplacementChargeReaction object
	 *
	 *@param  params            The parameter is if the molecule has already fixed the center active or not. It 
	 *							should be set before to inize the reaction with a setFlag:  CDKConstants.REACTIVE_CENTER
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("DisplacementChargeReaction only expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The parameter 1 must be of type boolean");
		}
		hasActiveCenter = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the DisplacementChargeReaction object
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

		logger.debug("initiate reaction: DisplacementChargeReaction");
		
		if (reactants.getMoleculeCount() != 1) {
			throw new CDKException("DisplacementChargeReaction only expects one reactant");
		}
		if (agents != null) {
			throw new CDKException("DisplacementChargeReaction don't expects agents");
		}
		
		ISetOfReactions setOfReactions = DefaultChemObjectBuilder.getInstance().newSetOfReactions();
		IMolecule reactant = reactants.getMolecule(0);
		
		/* if the parameter hasActiveCenter is not fixed yet, set the active centers*/
		if(!hasActiveCenter){
			setActiveCenters(reactant);
		}
		
		IBond[] bonds = reactants.getMolecule(0).getBonds();
		for(int i = 0 ; i < bonds.length ; i++){
			if(bonds[i].getFlag(CDKConstants.REACTIVE_CENTER) && bonds[i].getOrder() == 2.0){
				IAtom atom1 = bonds[i].getAtoms()[0];
				IAtom atom2 = bonds[i].getAtoms()[1];
				if(atom1.getFlag(CDKConstants.REACTIVE_CENTER) && atom1.getFormalCharge() == 0 && reactant.getSingleElectronSum(atom1) == 0)
					if(atom2.getFlag(CDKConstants.REACTIVE_CENTER) && atom2.getFormalCharge() == 0 && reactant.getSingleElectronSum(atom2) == 0){
				
						/* positions atoms and bonds */
						int atom0P = reactant.getAtomNumber(bonds[i].getAtoms()[0]);
						int bond1P = reactant.getBondNumber(bonds[i]);
						int atom1P = reactant.getAtomNumber(bonds[i].getAtoms()[1]);
						
						/* action */
						for(int j = 0; j < 2 ; j++){
							IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
							reaction.addReactant(reactant);
							
							IAtomContainer acCloned;
							try {
								acCloned = (IAtomContainer)reactant.clone();
							} catch (CloneNotSupportedException e) {
								throw new CDKException("Could not clone reactant", e);
							}
							
							double order = acCloned.getBondAt(bond1P).getOrder();
							acCloned.getBondAt(bond1P).setOrder(order - 1);
							
							if (j == 0)
							{
								acCloned.getAtomAt(atom0P).setFormalCharge(1);
								acCloned.getAtomAt(atom1P).setFormalCharge(-1);
							} else
							{
								acCloned.getAtomAt(atom0P).setFormalCharge(-1);
								acCloned.getAtomAt(atom1P).setFormalCharge(1);
							}
							
							/* mapping */
							IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(bonds[i], acCloned.getBondAt(bond1P));
					        reaction.addMapping(mapping);
					        mapping = DefaultChemObjectBuilder.getInstance().newMapping(bonds[i].getAtoms()[0], acCloned.getAtomAt(atom0P));
					        reaction.addMapping(mapping);
					        mapping = DefaultChemObjectBuilder.getInstance().newMapping(bonds[i].getAtoms()[1], acCloned.getAtomAt(atom1P));
					        reaction.addMapping(mapping);
							
					        
							reaction.addProduct((IMolecule) acCloned);
							setOfReactions.addReaction(reaction);
						}
					}
			}
		}
		
		return setOfReactions;	
		
		
	}
	/**
	 * set the active center for this molecule. 
	 * The active center will be those which correspond with A=B. 
	 * <pre>
	 * A: Atom
	 * =: Double bond
	 * B: Atom
	 *  </pre>
	 * 
	 * @param reactant The molecule to set the activity
	 * @throws CDKException 
	 */
	private void setActiveCenters(IMolecule reactant) throws CDKException {
		IBond[] bonds = reactant.getBonds();
		for(int i = 0 ; i < bonds.length ; i++)
			if(bonds[i].getOrder() == 2.0){
				IAtom atom1 = bonds[i].getAtoms()[0];
				IAtom atom2 = bonds[i].getAtoms()[1];
				if(atom1.getFormalCharge() == 0 && reactant.getSingleElectronSum(atom1) == 0)
					if(atom2.getFormalCharge() == 0 && reactant.getSingleElectronSum(atom2) == 0){
						atom1.setFlag(CDKConstants.REACTIVE_CENTER,true);
						atom2.setFlag(CDKConstants.REACTIVE_CENTER,true);
						bonds[i].setFlag(CDKConstants.REACTIVE_CENTER,true);
					}
			}
	}
	/**
	 *  Gets the parameterNames attribute of the DisplacementChargeReaction object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "hasActiveCenter";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the DisplacementChargeReaction object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return new Boolean(false);
	}
}
