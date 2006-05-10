package org.openscience.cdk.reaction.type;


import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.exception.CDKException;
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
 * <p>IReactionProcess which make an alectron impact for pi-Bond Dissociation.</p>
 * This reaction type is a representation of the processes which occure in the mass spectrometer.</p>
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
 * @cdk.created    2006-04-01
 * @cdk.module     reaction
 * @cdk.set        reaction-types
 * 
 **/
public class ElectronImpactPDBReaction implements IReactionProcess{
	private LoggingTool logger;
	private boolean hasActiveCenter;

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
	 *
	 *@param  reactants         reactants of the reaction.
	 *@param  agents            agents of the reaction (Must be in this case null).
	 *
	 *@exception  CDKException  Description of the Exception
	 */
	public ISetOfReactions initiate(ISetOfMolecules reactants, ISetOfMolecules agents) throws CDKException{
		
		logger.debug("initiate reaction: ElectronImpactPDBReaction");
		
		if (reactants.getMoleculeCount() != 1) {
			throw new CDKException("ElectronImpactPDBReaction only expects one reactant");
		}
		if (agents != null) {
			throw new CDKException("ElectronImpactPDBReaction don't expects agents");
		}
		
		ISetOfReactions setOfReactions = DefaultChemObjectBuilder.getInstance().newSetOfReactions();
		
		/* if the parameter hasActiveCenter is not fixed yet, set the active centers*/
		if(!hasActiveCenter){
			setActiveCenters(reactants.getMolecule(0));
		}
		
		IBond[] bonds = reactants.getMolecule(0).getBonds();
		for(int i = 0 ; i < bonds.length ; i++){
			if(bonds[i].getFlag(CDKConstants.REACTIVE_CENTER) && bonds[i].getOrder() == 2){
				
				IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
				reaction.addReactant(reactants.getMolecule(0));
				IMolecule reactant = reaction.getReactants().getMolecule(0);
				
				int posA1 = reactant.getAtomNumber(bonds[i].getAtoms()[0]);
				int posA2 = reactant.getAtomNumber(bonds[i].getAtoms()[1]);
				int posB1 = reactant .getBondNumber(bonds[i]);
				
				/**/
				for (int j = 0; j < 2; j++)
				{
					IMolecule reactantCloned;
					try {
						reactantCloned = (IMolecule) reactant.clone();
					} catch (CloneNotSupportedException e) {
						throw new CDKException("Could not clone IMolecule!", e);
					}
					
					double order = reactantCloned.getBondAt(posB1).getOrder();
					reactantCloned.getBondAt(posB1).setOrder(order - 1);
					
					if (j == 0)
					{
						reactantCloned.getAtomAt(posA1).setFormalCharge(1);
						reactantCloned.addElectronContainer(
								new SingleElectron(reactantCloned.getAtomAt(posA2)));
					} else
					{
						reactantCloned.getAtomAt(posA2).setFormalCharge(1);
						reactantCloned.addElectronContainer(
								new SingleElectron(reactantCloned.getAtomAt(posA1)));
					}
					
					/* mapping */
					IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(bonds[i], reactantCloned.getBondAt(posB1));
			        reaction.addMapping(mapping);
			        mapping = DefaultChemObjectBuilder.getInstance().newMapping(bonds[i].getAtoms()[0], reactantCloned.getAtomAt(posA1));
			        reaction.addMapping(mapping);
			        mapping = DefaultChemObjectBuilder.getInstance().newMapping(bonds[i].getAtoms()[1], reactantCloned.getAtomAt(posA2));
			        reaction.addMapping(mapping);
					
					
					reaction.addProduct(reactantCloned);
				}
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
		IBond[] bonds = reactant.getBonds();
		for(int i = 0 ; i < bonds.length ; i++){
			if(bonds[i].getOrder() == 2){
				bonds[i].setFlag(CDKConstants.REACTIVE_CENTER,true);
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
}
