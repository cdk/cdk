package org.openscience.cdk.reaction.type;


import java.util.Vector;

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
 * IReactionProcess which make an alectron impact for pi-Bond Dissociation 
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
				"http://gold.zvon.org/E01999.html",
				this.getClass().getName(),
				"$Id: ElectronImpactPDBReaction.java,v 1.6 2006/04/01 08:26:47 mrc Exp $",
				"The Chemistry Development Kit");
	}
	
	/**
	 *  Sets the parameters attribute of the ElectronImpactPDBReaction object
	 *
	 *@param  params            The parameter is the the molecule has center acitve or not
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
	 *@param  reactants         reactants to initiate.
	 *@param  agents            agents to initiate.
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
		
		if(!hasActiveCenter){
			setActiveCenters(reactants.getMolecule(0));
		}
		
		IBond[] bonds = reactants.getMolecule(0).getBonds();
		Vector controllerA = new Vector();
		for(int i = 0 ; i < bonds.length ; i++){
			if(bonds[i].getFlag(CDKConstants.REACTIVE_CENTER)){
				
				IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
				reaction.addReactant(reactants.getMolecule(0));
				IMolecule reactant = reaction.getReactants().getMolecule(0);
				int posA1 = reactant.getAtomNumber(bonds[i].getAtoms()[0]);
				int posA2 = reactant.getAtomNumber(bonds[i].getAtoms()[1]);

				for (int j = 0; j < 2; j++)
				{
					IMolecule reactantCloned = (IMolecule) reactant.clone();
					double order = reactant.getBondAt(i).getOrder();
					reactantCloned.getBondAt(i).setOrder(order - 1);
					
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
					if(!controllerA.contains(bonds[i].getAtoms()[0])){
						IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(bonds[i].getAtoms()[0], reactantCloned.getAtomAt(posA1));
				        reaction.addMapping(mapping);
				        controllerA.add(bonds[i].getAtoms()[0]);
					}
					if(!controllerA.contains(bonds[i].getAtoms()[1])){
						IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(bonds[i].getAtoms()[1], reactantCloned.getAtomAt(posA2));
				        reaction.addMapping(mapping);
				        controllerA.add(bonds[i].getAtoms()[1]);
					}
					
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
		boolean foundAC = false;
		IBond[] bonds = reactant.getBonds();
		for(int i = 0 ; i < bonds.length ; i++){
			if(bonds[i].getOrder() == 2){
				bonds[i].setFlag(CDKConstants.REACTIVE_CENTER,true);
				foundAC = true;
			}
		}
		if(!foundAC)
			throw new CDKException("it wasn't possible to find active center for this reactant: "+reactant);
		
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
