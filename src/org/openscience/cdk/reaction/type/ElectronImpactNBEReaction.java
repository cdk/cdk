package org.openscience.cdk.reaction.type;


import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.interfaces.ISetOfReactions;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.tools.LoggingTool;

/**
 * IReactionProcess which make an alectron impact for for Non-Bondind Electron Lost 
 * 
 * @author         Miguel Rojas
 * 
 * @cdk.created    2006-04-01
 * @cdk.module     reaction
 * @cdk.set        reaction-types
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
				"http://gold.zvon.org/E01999.html",
				this.getClass().getName(),
				"$Id: ElectronImpactNBEReaction.java,v 1.6 2006/04/01 08:26:47 mrc Exp $",
				"The Chemistry Development Kit");
	}
	
	/**
	 *  Sets the parameters attribute of the ElectronImpactNBEReaction object
	 *
	 *@param  params            The parameter is the the molecule has center acitve or not
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
	 *@param  reactants         reactants to initiate.
	 *@param  agents            agents to initiate.
	 *
	 *@exception  CDKException  Description of the Exception
	 */

	public ISetOfReactions initiate(ISetOfMolecules reactants, ISetOfMolecules agents) throws CDKException{

		logger.debug("initiate reaction: ElectronImpactNBEReaction");
		
		if (reactants.getMoleculeCount() != 1) {
			throw new CDKException("ElectronImpactNBEReaction only expects one reactant");
		}
		if (agents != null) {
			throw new CDKException("ElectronImpactNBEReaction don't expects agents");
		}
		
		ISetOfReactions setOfReactions = DefaultChemObjectBuilder.getInstance().newSetOfReactions();
		
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
				
				IMolecule reactantCloned = (IMolecule) reactants.getMolecule(0).clone();
				
				ILonePair[] lps = reactantCloned.getLonePairs(reactantCloned.getAtomAt(posA));
				reactantCloned.removeElectronContainer(lps[0]);

				reactantCloned.addElectronContainer(new SingleElectron(reactantCloned.getAtomAt(posA)));

				reaction.addProduct(reactantCloned);


				setOfReactions.addReaction(reaction);
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
		boolean foundAC = false;
		IAtom[] atoms = reactant.getAtoms();
		for(int i = 0 ; i < atoms.length ; i++){
			if(reactant.getLonePairs(atoms[i]).length > 0){
				atoms[i].setFlag(CDKConstants.REACTIVE_CENTER,true);
				foundAC = true;
			}
		}
		if(!foundAC)
			throw new CDKException("it wasn't possible to find active center for this reactant: "+reactant);
		
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
