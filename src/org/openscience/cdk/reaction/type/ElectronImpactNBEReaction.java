package org.openscience.cdk.reaction.type;


import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.tools.LoggingTool;

/**
 * <p>IReactionProcess which make an alectron impact for for Non-Bondind Electron Lost. 
 * This reaction type is a representation of the processes which occure in the mass spectrometer.</p>
 * 
 *<pre>
 *  ISetOfMolecules setOfReactants = DefaultChemObjectBuilder.getInstance().newSetOfMolecules();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new ElectronImpactNBEReaction();
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
 * @cdk.created    2006-04-01
 * @cdk.module     reaction
 * @cdk.set        reaction-types
 * @cdk.dictref    reaction-types:electronImpact
 * 
 **/
public class ElectronImpactNBEReaction implements IReactionProcess{
	private LoggingTool logger;
	private boolean hasActiveCenter;

	/**
	 * Constructor of the ElectronImpactNBEReaction object
	 *
	 */
	public ElectronImpactNBEReaction(){
		logger = new LoggingTool(this);
	}
	/**
	 *  Gets the specification attribute of the ElectronImpactNBEReaction object
	 *
	 *@return    The specification value
	 */
	public ReactionSpecification getSpecification() {
		return new ReactionSpecification(
				"http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#ElectronImpactNBEReaction",
				this.getClass().getName(),
				"$Id: ElectronImpactNBEReaction.java,v 1.6 2006/04/01 08:26:47 mrc Exp $",
				"The Chemistry Development Kit");
	}
	
	/**
	 *  Sets the parameters attribute of the ElectronImpactNBEReaction object
	 *
	 *@param  params            The parameter is if the molecule has already fixed the center active or not. It 
	 *							should be set before to inize the reaction with a setFlag:  CDKConstants.REACTIVE_CENTER
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("ElectronImpactNBEReaction only expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The parameter must be of type boolean");
		}
		hasActiveCenter = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the ElectronImpactNBEReaction object
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

		logger.debug("initiate reaction: ElectronImpactNBEReaction");
		
		if (reactants.getMoleculeCount() != 1) {
			throw new CDKException("ElectronImpactNBEReaction only expects one reactant");
		}
		if (agents != null) {
			throw new CDKException("ElectronImpactNBEReaction don't expects agents");
		}
		
		IReactionSet setOfReactions = DefaultChemObjectBuilder.getInstance().newSetOfReactions();
		
		/* if the parameter hasActiveCenter is not fixed yet, set the active centers*/
		if(!hasActiveCenter){
			setActiveCenters(reactants.getMolecule(0));
		}
		
		IAtom[] atoms = reactants.getMolecule(0).getAtoms();
		for(int i = 0 ; i < atoms.length ; i++){
			if(atoms[i].getFlag(CDKConstants.REACTIVE_CENTER)){
				
				IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
				reaction.addReactant(reactants.getMolecule(0));
				IMolecule reactant = reaction.getReactants().getMolecule(0);
				
				int posA = reactant.getAtomNumber(atoms[i]);
				
				IMolecule reactantCloned;
				try {
					reactantCloned = (IMolecule) reactants.getMolecule(0).clone();
				} catch (CloneNotSupportedException e) {
					throw new CDKException("Could not clone IMolecule!", e);
				}
				
				ILonePair[] lps = reactantCloned.getLonePairs(reactantCloned.getAtom(posA));
				reactantCloned.removeElectronContainer(lps[0]);

				reactantCloned.addElectronContainer(new SingleElectron(reactantCloned.getAtom(posA)));
				reactantCloned.getAtom(posA).setFormalCharge(1);

				/* mapping */
				IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(atoms[i], reactantCloned.getAtom(posA));
		        reaction.addMapping(mapping);
				
				reaction.addProduct(reactantCloned);


				setOfReactions.addReaction(reaction);
			}
		}
		return setOfReactions;	
		
		
	}
	/**
	 * set the active center for this molecule. The active center will be heteroatoms which contain lone pair electrons.
	 * 
	 * @param reactant The molecule to set the activity
	 * @throws CDKException 
	 */
	private void setActiveCenters(IMolecule reactant) throws CDKException {
		IAtom[] atoms = reactant.getAtoms();
		for(int i = 0 ; i < atoms.length ; i++){
			if(reactant.getLonePairs(atoms[i]).length > 0){
				atoms[i].setFlag(CDKConstants.REACTIVE_CENTER,true);
			}
		}
	}
	/**
	 *  Gets the parameterNames attribute of the ElectronImpactNBEReaction object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "hasActiveCenter";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the ElectronImpactNBEReaction object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return new Boolean(false);
	}
}
