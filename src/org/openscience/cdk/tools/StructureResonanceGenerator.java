package org.openscience.cdk.tools;


import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.ISetOfAtomContainers;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.interfaces.ISetOfReactions;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.RearrangementAnion1Reaction;
import org.openscience.cdk.reaction.type.RearrangementAnion2Reaction;
import org.openscience.cdk.reaction.type.RearrangementAnion3Reaction;
import org.openscience.cdk.reaction.type.RearrangementCation1Reaction;
import org.openscience.cdk.reaction.type.RearrangementCation2Reaction;
import org.openscience.cdk.reaction.type.RearrangementCation3Reaction;
import org.openscience.cdk.reaction.type.RearrangementRadical1Reaction;
import org.openscience.cdk.reaction.type.RearrangementRadical2Reaction;
import org.openscience.cdk.reaction.type.RearrangementRadical3Reaction;

/**
 * <p>This class try to generate resonance structure for a determinate molecule.</p>
 * <p>Make sure that the molecule has the corresponend lone pair electrons
 * for each atom. You can use the method: <pre> LonePairElectronChecker </pre>
 * <p>It is based on rearrengements of electrons and charge</p>
 * 
 * <pre>
 * StructureResonanceGenerator srG = new StructureReseonanceGenerator(true,true,true,false);
 * SetOfMolecules setOf = gf.getResonances(new Molecule());
 * </pre>
 * 
 * <p>We have the possibility to localize the reactive center. Good method if you
 * want to localize the reaction in a fixed point</p>
 * <pre>atoms[0].setFlag(CDKConstants.REACTIVE_CENTER,true);</pre>
 * <p>Moreover you must put the parameter as true</p>
 * <p>If the reactive center is not localized then the reaction process will
 * try to find automatically the posible reactive center.</p>
 *  
 * @author      Miguel Rojas
 * @cdk.created     2006-5-05
 * @cdk.module experimental
 * 
 * @see RearrangementAnion1Reaction
 * @see RearrangementAnion2Reaction
 * @see RearrangementAnion3Reaction
 * @see RearrangementCation1Reaction
 * @see RearrangementCation2Reaction
 * @see RearrangementCation3Reaction
 * @see RearrangementRadical1Reaction
 * @see RearrangementRadical2Reaction
 * @see RearrangementRadical3Reaction
 * 
 **/
public class StructureResonanceGenerator {
	
	private boolean cationR;
	private boolean anionR;
	private boolean radicalR;
	private boolean hasActiveCenter;
	/**
	 * Constructor of StructureResonanceGenerator object
	 *
	 * Default: all possible search (Aromatic,Cation,Anion), not specified the active center
	 */
	public StructureResonanceGenerator(){
		this(true,true,true,false);
	}
	/**
	 * Constructor of StructureResonanceGenerator object
	 *
	 * @param cationR          True, search of Cation.
	 * @param anionR           True, search of Anion.
	 * @param radicalR         True, search of Radical.
	 * @param hasActiveCenter  False, search of active Center.
	 */
	public StructureResonanceGenerator(
			boolean cationR,
			boolean anionR,
			boolean radicalR,
			boolean hasActiveCenter){
		this.cationR = cationR;
		this.anionR = anionR;
		this.radicalR = radicalR;
		this.hasActiveCenter = hasActiveCenter;
		
	}
	/**
	 * <p>Get the resonance structures from an atomContainer. </p>
	 * <p>This generator of resonances is limited only whose have the same order sume of bonds or higher.
	 * 
	 * @param atomContainer The atomContainer to analize
	 * @return The different resonance structures
	 */
	public ISetOfAtomContainers getStructures(IAtomContainer atomContainer) {
		ISetOfAtomContainers setOfAC = DefaultChemObjectBuilder.getInstance().newSetOfAtomContainers();
		
		ISetOfAtomContainers set = getAllStructures(atomContainer); 
		/*analize sum of bonds */
		double bondSum = 0;
		for(int i = 0; i < atomContainer.getBondCount(); i++)
			bondSum = bondSum + atomContainer.getBondAt(i).getOrder();
		for(int i = 0; i < set.getAtomContainerCount(); i++){
			double bondSumI = 0;
			for(int j = 0; j < set.getAtomContainer(i).getBondCount(); j++)
				bondSumI += set.getAtomContainer(i).getBondAt(j).getOrder();
			if(bondSumI >= bondSum)
				setOfAC.addAtomContainer(set.getAtomContainer(i));
		}
		return setOfAC;
	}
	/**
	 * <p>Get all resonance structures from an atomContainer. </p>
	 * 
	 * @param atomContainer The atomContainer to analize
	 * @param ac            Part of the atomContainer for analazing
	 * @return The different resonance structures
	 */
	public ISetOfAtomContainers getAllStructures(IAtomContainer atomContainer){
		ISetOfAtomContainers setOfAtomContainer = DefaultChemObjectBuilder.getInstance().newSetOfAtomContainers();
		setOfAtomContainer.addAtomContainer(atomContainer);
		Object[] params = new Object[1];
		if(hasActiveCenter)
			params[0] = Boolean.TRUE;
		else
			params[0] = Boolean.FALSE;

		try {
			for(int i = 0 ; i < setOfAtomContainer.getAtomContainerCount() ; i++){
				ISetOfMolecules setOfReactants = DefaultChemObjectBuilder.getInstance().newSetOfMolecules();
				setOfReactants.addAtomContainer(setOfAtomContainer.getAtomContainer(i));
				if(cationR){
					/* RearrangementCation1Reaction */
					IReactionProcess type  = new RearrangementCation1Reaction();
			        type.setParameters(params);
					
			        ISetOfReactions setOfReactions = type.initiate(setOfReactants, null);
			        
			        if(setOfReactions.getReactionCount() != 0)
						for(int k = 0 ; k < setOfReactions.getReactionCount() ; k++)
						for(int j = 0 ; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount() ; j++){
							IAtomContainer set = setOfReactions.getReaction(k).getProducts().getAtomContainer(j);
							if(!existAC(setOfAtomContainer,set))
								setOfAtomContainer.addAtomContainer(setOfReactions.getReaction(k).getProducts().getAtomContainer(j));
						}

					/* RearrangementCation2Reaction*/
					type  = new RearrangementCation2Reaction();
			        type.setParameters(params);
					
			        setOfReactions = type.initiate(setOfReactants, null);
			        
					if(setOfReactions.getReactionCount() != 0)
						for(int k = 0 ; k < setOfReactions.getReactionCount() ; k++)
						for(int j = 0 ; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount() ; j++){
							IAtomContainer set = setOfReactions.getReaction(k).getProducts().getAtomContainer(j);
							if(!existAC(setOfAtomContainer,set))
								setOfAtomContainer.addAtomContainer(setOfReactions.getReaction(k).getProducts().getAtomContainer(j));
						}
		
					/* RearrangementCation3Reaction*/
					type  = new RearrangementCation3Reaction();
			        type.setParameters(params);
					
			        setOfReactions = type.initiate(setOfReactants, null);
			        
			        if(setOfReactions.getReactionCount() != 0)
						for(int k = 0 ; k < setOfReactions.getReactionCount() ; k++)
						for(int j = 0 ; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount() ; j++){
							IAtomContainer set = setOfReactions.getReaction(k).getProducts().getAtomContainer(j);
							if(!existAC(setOfAtomContainer,set))
								setOfAtomContainer.addAtomContainer(setOfReactions.getReaction(k).getProducts().getAtomContainer(j));
						}
				}
				if(anionR){
					/* RearrangementAnion1Reaction*/
					IReactionProcess type  = new RearrangementAnion1Reaction();
			        type.setParameters(params);
					
			        ISetOfReactions setOfReactions = type.initiate(setOfReactants, null);
			        
			        if(setOfReactions.getReactionCount() != 0)
						for(int k = 0 ; k < setOfReactions.getReactionCount() ; k++)
						for(int j = 0 ; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount() ; j++){
							IAtomContainer set = setOfReactions.getReaction(k).getProducts().getAtomContainer(j);
							if(!existAC(setOfAtomContainer,set))
								setOfAtomContainer.addAtomContainer(setOfReactions.getReaction(k).getProducts().getAtomContainer(j));
						}
		
					/* RearrangementAnion2Reaction*/
					type  = new RearrangementAnion2Reaction();
			        type.setParameters(params);
					
			        setOfReactions = type.initiate(setOfReactants, null);
			        
			        if(setOfReactions.getReactionCount() != 0)
						for(int k = 0 ; k < setOfReactions.getReactionCount() ; k++)
						for(int j = 0 ; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount() ; j++){
							IAtomContainer set = setOfReactions.getReaction(k).getProducts().getAtomContainer(j);
							if(!existAC(setOfAtomContainer,set))
								setOfAtomContainer.addAtomContainer(setOfReactions.getReaction(k).getProducts().getAtomContainer(j));
						}
		
					/* RearrangementAnion3Reaction*/
					type  = new RearrangementAnion3Reaction();
			        type.setParameters(params);
					
			        setOfReactions = type.initiate(setOfReactants, null);
			        
			        if(setOfReactions.getReactionCount() != 0)
						for(int k = 0 ; k < setOfReactions.getReactionCount() ; k++)
						for(int j = 0 ; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount() ; j++){
							IAtomContainer set = setOfReactions.getReaction(k).getProducts().getAtomContainer(j);
							if(!existAC(setOfAtomContainer,set))
								setOfAtomContainer.addAtomContainer(setOfReactions.getReaction(k).getProducts().getAtomContainer(j));
						}
				}
				if(radicalR){
					/* RearrangementRadical1Reaction*/
					IReactionProcess type  = new RearrangementRadical1Reaction();
			        type.setParameters(params);
					
			        ISetOfReactions setOfReactions = type.initiate(setOfReactants, null);
			        
			        if(setOfReactions.getReactionCount() != 0)
						for(int k = 0 ; k < setOfReactions.getReactionCount() ; k++)
						for(int j = 0 ; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount() ; j++){
							IAtomContainer set = setOfReactions.getReaction(k).getProducts().getAtomContainer(j);
							if(!existAC(setOfAtomContainer,set))
								setOfAtomContainer.addAtomContainer(setOfReactions.getReaction(k).getProducts().getAtomContainer(j));
						}
					
					/* RearrangementRadical2Reaction*/
					type  = new RearrangementRadical2Reaction();
			        type.setParameters(params);
					
			        setOfReactions = type.initiate(setOfReactants, null);
			        
			        if(setOfReactions.getReactionCount() != 0)
						for(int k = 0 ; k < setOfReactions.getReactionCount() ; k++)
						for(int j = 0 ; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount() ; j++){
							IAtomContainer set = setOfReactions.getReaction(k).getProducts().getAtomContainer(j);
							if(!existAC(setOfAtomContainer,set))
								setOfAtomContainer.addAtomContainer(setOfReactions.getReaction(k).getProducts().getAtomContainer(j));
						}
	
					/* RearrangementRadical3Reaction*/
					type  = new RearrangementRadical3Reaction();
			        type.setParameters(params);
					
			        setOfReactions = type.initiate(setOfReactants, null);
			        
			        if(setOfReactions.getReactionCount() != 0)
						for(int k = 0 ; k < setOfReactions.getReactionCount() ; k++)
						for(int j = 0 ; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount() ; j++){
							IAtomContainer set = setOfReactions.getReaction(k).getProducts().getAtomContainer(j);
							if(!existAC(setOfAtomContainer,set))
								setOfAtomContainer.addAtomContainer(setOfReactions.getReaction(k).getProducts().getAtomContainer(j));
						}
				}
			}
		} catch (CDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return setOfAtomContainer;
	}
	/**
	 * Search if the setOfAtomContainer contains the atomContainer 
	 * 
	 * @param set            ISetOfAtomContainer object where to search
	 * @param atomContainer  IAtomContainer to search
	 * @return   			 True, if the atomContainer is contained
	 */
	private boolean existAC(ISetOfAtomContainers set, IAtomContainer atomContainer) {
		for(int i = 0 ; i < set.getAtomContainerCount(); i++){
			IAtomContainer ac = set.getAtomContainer(i);
			QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(ac);
			try {
				if(UniversalIsomorphismTester.isIsomorph(atomContainer,qAC)){
					return true;
				}
			} catch (CDKException e1) {
				e1.printStackTrace();
			}
		}
		return false;
	}

}
