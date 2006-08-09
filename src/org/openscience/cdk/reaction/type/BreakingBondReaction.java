package org.openscience.cdk.reaction.type;


import java.io.IOException;
import java.util.ArrayList;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionSpecification;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.ValencyChecker;

/**
 * <p>IReactionProcess which a bond that is being broken to generate charges. 
 * As there are two directions for breaking a bond in a polar manner, 
 * each bond is investigated twice:</p>
 * <pre>A-B => [A+] + |[B-]</pre>
 * <pre>A-B => [A-] + |[B+]</pre>
 * <pre> It will not be created structures no possible, for example; C=O => [C-][O+].
 * <pre>
 *  ISetOfMolecules setOfReactants = DefaultChemObjectBuilder.getInstance().newSetOfMolecules();
 *  setOfReactants.addMolecule(new Molecule());
 *  IReactionProcess type = new BreakingBondReaction();
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
 * @cdk.created    2006-06-09
 * @cdk.module     reaction
 * @cdk.set        reaction-types
 * 
 **/
public class BreakingBondReaction implements IReactionProcess{
	private LoggingTool logger;
	private boolean hasActiveCenter;
	private ValencyChecker valChecker;

	/**
	 * Constructor of the BreakingBondReaction object
	 *
	 */
	public BreakingBondReaction(){
		logger = new LoggingTool(this);
		try{
			valChecker = new ValencyChecker();
		}catch(IOException e){
			e.printStackTrace();
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}
	/**
	 *  Gets the specification attribute of the BreakingBondReaction object
	 *
	 *@return    The specification value
	 */
	public ReactionSpecification getSpecification() {
		return new ReactionSpecification(
				"http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionDict#BreakingBondReaction",
				this.getClass().getName(),
				"$Id: BreakingBondReaction.java,v 1.6 2006/04/01 08:26:47 mrc Exp $",
				"The Chemistry Development Kit");
	}
	
	/**
	 *  Sets the parameters attribute of the BreakingBondReaction object
	 *
	 *@param  params            The parameter is if the molecule has already fixed the center active or not. It 
	 *							should be set before to inize the reaction with a setFlag:  CDKConstants.REACTIVE_CENTER
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("BreakingBondReaction only expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The parameter 1 must be of type boolean");
		}
		hasActiveCenter = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the BreakingBondReaction object
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

		logger.debug("initiate reaction: BreakingBondReaction");
		
		if (reactants.getMoleculeCount() != 1) {
			throw new CDKException("BreakingBondReaction only expects one reactant");
		}
		if (agents != null) {
			throw new CDKException("BreakingBondReaction don't expects agents");
		}
		
		IReactionSet setOfReactions = DefaultChemObjectBuilder.getInstance().newReactionSet();
		IMolecule reactant = reactants.getMolecule(0);
		
		/* if the parameter hasActiveCenter is not fixed yet, set the active centers*/
		if(!hasActiveCenter){
			setActiveCenters(reactant);
		}
		IAtomContainerSet acSet = reactant.getBuilder().newAtomContainerSet();
		IBond[] bonds = reactants.getMolecule(0).getBonds();
		for(int i = 0 ; i < bonds.length ; i++){
			if(bonds[i].getFlag(CDKConstants.REACTIVE_CENTER)){
				int atom1 = reactants.getMolecule(0).getAtomNumber(bonds[i].getAtoms()[0]);
				int atom2 = reactants.getMolecule(0).getAtomNumber(bonds[i].getAtoms()[1]);
				int bond =  reactants.getMolecule(0).getBondNumber(bonds[i]);
				
				/**/
				for (int j = 0; j < 2; j++){
					IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
					reaction.addReactant(reactants.getMolecule(0));
					
					IMolecule reactantCloned;
					try {
						reactantCloned = (IMolecule) reactant.clone();
					} catch (CloneNotSupportedException e) {
						throw new CDKException("Could not clone IMolecule!", e);
					}
					
					double order = reactantCloned.getBond(bond).getOrder();
					
					reactantCloned.getBond(bond).setOrder(order - 1);
		
					int charge = 0;
					IMoleculeSet setOfMolecules = null;
					if (j == 0){
						charge = reactantCloned.getAtom(atom1).getFormalCharge();
						reactantCloned.getAtom(atom1).setFormalCharge(charge+1);
						charge = reactantCloned.getAtom(atom2).getFormalCharge();
						reactantCloned.getAtom(atom2).setFormalCharge(charge-1);
						reactantCloned.addElectronContainer(new SingleElectron(reactantCloned.getAtom(atom2)));
						/* an acceptor atom cannot be charged positive*/
						if(!valChecker.isSaturated(reactantCloned.getAtom(atom1),reactantCloned))
							continue;
						if(order == 1)/*break molecule*/
							setOfMolecules = fragmentMolecule(reactantCloned,bond);
						
					} else{
						charge = reactantCloned.getAtom(atom2).getFormalCharge();
						reactantCloned.getAtom(atom2).setFormalCharge(1);
						charge = reactantCloned.getAtom(atom1).getFormalCharge();
						reactantCloned.getAtom(atom1).setFormalCharge(-1);
						reactantCloned.addElectronContainer(new SingleElectron(reactantCloned.getAtom(atom1)));
						/* an acceptor atom cannot be charged positive*/
						if(!valChecker.isSaturated(reactantCloned.getAtom(atom2),reactantCloned))
							continue;
						if(order == 1)/*break molecule*/
							setOfMolecules = fragmentMolecule(reactantCloned,bond);// TODO- better method
					}
					
					/* mapping */
					IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(bonds[i], reactantCloned.getBond(bond));
			        reaction.addMapping(mapping);
			        mapping = DefaultChemObjectBuilder.getInstance().newMapping(bonds[i].getAtoms()[0], reactantCloned.getAtom(atom1));
			        reaction.addMapping(mapping);
			        mapping = DefaultChemObjectBuilder.getInstance().newMapping(bonds[i].getAtoms()[1], reactantCloned.getAtom(atom2));
			        reaction.addMapping(mapping);
					
					if(setOfMolecules != null)
						for(int z = 0 ; z < setOfMolecules.getAtomContainerCount(); z++){
							IMolecule ac = setOfMolecules.getMolecule(z);
					        /* the fragmentation of Hydrogens can be produc duplicates*/
							if(existAC(acSet,ac))
								continue;
							reaction.addProduct(ac);
							acSet.addAtomContainer(ac);
						}
					else
						reaction.addProduct(reactantCloned);
					
					/*adding only that contains product*/
					if(reaction.getProductCount() != 0)
						setOfReactions.addReaction(reaction);
				}
			}
				
		}
		
		return setOfReactions;	
	}
	/**
	 * controll if the new product was already found before
	 * @param acSet 
	 * @param fragment
	 * @return True, if it contains
	 */
	private boolean existAC(IAtomContainerSet acSet, IMolecule fragment) {
		QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(fragment);
		for(int i = 0; i < acSet.getAtomContainerCount(); i++){
			IAtomContainer ac = acSet.getAtomContainer(i);
			try {
				if(UniversalIsomorphismTester.isIsomorph(ac, qAC))
					return true;
			} catch (CDKException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	/**
	 * fragment a molecule in two. It search where don't exist a connection between two atoms
	 * @param reactantCloned IMolecule to fragment
	 * @param bond           Bond to remove
	 * @return               The ISetOfMolecules
	 */
	private IMoleculeSet fragmentMolecule(IMolecule molecule, int bond) throws CDKException{
//		if(!GeometryTools.has2DCoordinates(molecule)){
			StructureDiagramGenerator sdg = new StructureDiagramGenerator();
			sdg.setMolecule(molecule);
			molecule = sdg.getMolecule();
			try {
				sdg.generateCoordinates();
			} catch (Exception e) {
				e.printStackTrace();
			}
//		}
		IMoleculeSet setOfFragments = molecule.getBuilder().newMoleculeSet();
		IMolecule molecule1,molecule2;
		try {
			molecule1 = (IMolecule)molecule.clone();
			molecule1.removeElectronContainer(bond);
			molecule2 = (IMolecule)molecule.clone();
			molecule2.removeElectronContainer(bond);
			molecule.removeElectronContainer(bond);
		} catch (CloneNotSupportedException e) {
			throw new CDKException("Could not clone IMolecule!", e);
		}
		
		for(int i = 0 ; i < molecule.getAtomCount() ; i++)
			molecule.getAtom(i).setFlag(CDKConstants.VISITED, false);
		
		
		
		molecule.getAtom(0).setFlag(CDKConstants.VISITED, true);
		ArrayList atomsVisited = new ArrayList();
		atomsVisited.add(molecule.getAtom(0));
		
		
		for (int i = 0; i < atomsVisited.size(); i++){
			IAtom[] atomsConnected = molecule.getConnectedAtoms((IAtom)atomsVisited.get(i));
			for (int j = 0; j < atomsConnected.length; j++){
				if(atomsConnected[j].getFlag(CDKConstants.VISITED) == false){
					atomsConnected[j].setFlag(CDKConstants.VISITED, true);
					atomsVisited.add(atomsConnected[j]);
				}
			}
		}
		for (int i = 0; i < molecule.getAtomCount(); i++){
			if(molecule.getAtom(i).getFlag(CDKConstants.VISITED) == true){
				for (int j = 0; j < molecule1.getAtomCount(); j++){
					if (compareCoordenates(molecule.getAtom(i),molecule1.getAtom(j))){
						molecule1.removeAtomAndConnectedElectronContainers(molecule1.getAtom(j));}
				}
				
			} else{
				for (int j = 0; j < molecule2.getAtomCount(); j++){
					if (compareCoordenates(molecule.getAtom(i),molecule2.getAtom(j))){
						molecule2.removeAtomAndConnectedElectronContainers(molecule2.getAtom(j));}
				}
				
			}
		}
		if(molecule1.getAtomCount()< molecule.getAtomCount())
			setOfFragments.addAtomContainer(molecule1);
		if(molecule2.getAtomCount()< molecule.getAtomCount())
			setOfFragments.addAtomContainer(molecule2);
		return setOfFragments;
	}
	/**
	 * Compare two atoms if they have the same coordenates.
	 * @param atom1 IAtom
	 * @param atom2 IAtom
	 * @return True, if they the same coordenates.
	 */
	private boolean compareCoordenates(IAtom atom1, IAtom atom2) {
		if(atom1.getPoint2d().equals(atom2.getPoint2d()))
			return true;
		return false;
	}
	/**
	 * set the active center for this molecule. 
	 * The active center will be those which correspond with A-B. If
	 * the bond is simple, it will be breaked forming two fragments 
	 * <pre>
	 * A: Atom
	 * #/=/-: bond
	 * B: Atom
	 *  </pre>
	 * 
	 * @param reactant The molecule to set the activity
	 * @throws CDKException 
	 */
	private void setActiveCenters(IMolecule reactant) throws CDKException {
		IBond[] bonds = reactant.getBonds();
		for(int i = 0 ; i < bonds.length ; i++){
			IAtom atom1 = bonds[i].getAtoms()[0];
			IAtom atom2 = bonds[i].getAtoms()[1];
			atom1.setFlag(CDKConstants.REACTIVE_CENTER,true);
			atom2.setFlag(CDKConstants.REACTIVE_CENTER,true);
			bonds[i].setFlag(CDKConstants.REACTIVE_CENTER,true);
		}
	}
	/**
	 *  Gets the parameterNames attribute of the BreakingBondReaction object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "hasActiveCenter";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the BreakingBondReaction object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return new Boolean(false);
	}
}
