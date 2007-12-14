/* $Revision: 6221 $ $Author: miguelrojasch $ $Date: 2006-05-11 14:25:07 +0200 (Do, 11 Mai 2006) $
 *
 * Copyright (C) 2006-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.tools;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.DisplacementChargeFromAcceptorReaction;
import org.openscience.cdk.reaction.type.DisplacementChargeFromDonorReaction;
import org.openscience.cdk.reaction.type.HyperconjugationReaction;
import org.openscience.cdk.reaction.type.RearrangementAnion1Reaction;
import org.openscience.cdk.reaction.type.RearrangementAnion2Reaction;
import org.openscience.cdk.reaction.type.RearrangementAnion3Reaction;
import org.openscience.cdk.reaction.type.RearrangementCation1Reaction;
import org.openscience.cdk.reaction.type.RearrangementCation2Reaction;
import org.openscience.cdk.reaction.type.RearrangementCation3Reaction;
import org.openscience.cdk.reaction.type.RearrangementRadical1Reaction;
import org.openscience.cdk.reaction.type.RearrangementRadical2Reaction;
import org.openscience.cdk.reaction.type.RearrangementRadical3Reaction;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * <p>This class try to generate resonance structure for a determinate molecule.</p>
 * <p>Make sure that the molecule has the corresponding lone pair electrons
 * for each atom. You can use the method: <pre> LonePairElectronChecker </pre>
 * <p>It is needed to call the addExplicitHydrogensToSatisfyValency
 *  from the class tools.HydrogenAdder.</p>
 * <p>It is based on rearrangements of electrons and charge</p>
 * <p>The method is based on call by reactions which occur in a resonance.</p>
 * 
 * <pre>
 * StructureResonanceGenerator srG = new StructureReseonanceGenerator(true,true,true,true,false);
 * MoleculeSet setOf = gf.getResonances(new Molecule());
 * </pre>
 * 
 * <p>We have the possibility to localize the reactive center. Good method if you
 * want to localize the reaction in a fixed point</p>
 * <pre>atoms[0].setFlag(CDKConstants.REACTIVE_CENTER,true);</pre>
 * <p>Moreover you must put the parameter as true</p>
 * <p>If the reactive center is not localized then the reaction process will
 * try to find automatically the possible reactive center.</p>
 *  
 * @author       Miguel Rojas
 * @cdk.created  2006-5-05
 * @cdk.module   reaction
 * @cdk.svnrev   $Revision: 9162 $
 * 
 * @see org.openscience.cdk.reaction.IReactionProcess
 */
public class StructureResonanceGenerator {
	
	private boolean cationR = true;
	private boolean anionR = true;
	private boolean radicalR = true;
	private boolean bondR = true;
	private boolean hasActiveCenter = false;
	private boolean hyperconjugationR = false;
	/**-1 means that there is not restrictions how many structures will be obtained*/
	private int maxStructuresToObtain = -1;
	
	private LoggingTool logger = new LoggingTool(StructureResonanceGenerator.class);
	
	/**
	 * Constructor of StructureResonanceGenerator object
	 *
	 * Default search: (Radical,Cation,Anion,Bond,hyperconjugation, no limit resonance structures)
	 */
	public StructureResonanceGenerator(){
		this(true,true,true,true,false,false, -1);
	}
	/**
	 * Constructor of StructureResonanceGenerator object
	 *
	 * @param cationR           True, search of Cation.
	 * @param anionR            True, search of Anion.
	 * @param radicalR          True, search of Radical.
	 * @param bondR             True, search of Bond.
	 * @param hyperconjugationR True, search of hyperconjugation.
	 * @param hasActiveCenter   True, search of active Center.
	 */
	public StructureResonanceGenerator(
			boolean cationR,
			boolean anionR,
			boolean radicalR,
			boolean bondR,
			boolean hyperconjugationR,
			boolean hasActiveCenter,
			int maxStructuresToObtain){
		this.cationR = cationR;
		this.anionR = anionR;
		this.radicalR = radicalR;
		this.bondR = bondR;
		this.hyperconjugationR = hyperconjugationR;
		this.hasActiveCenter = hasActiveCenter;
		this.maxStructuresToObtain = maxStructuresToObtain;
		
	}
	/**
	 * <p>Get the resonance structures from an atomContainer. </p>
	 * <p>This generator of resonances is limited only structures whose have the same order sum of bonds or higher.
	 * 
	 * @param atomContainer The atomContainer to analyze
	 * @return The different resonance structures
	 */
	public IAtomContainerSet getStructures(IAtomContainer atomContainer) {
		IAtomContainerSet setOfAC = atomContainer.getBuilder().newAtomContainerSet();
		
		IAtomContainerSet set = getAllStructures(atomContainer); 
		/*analize sum of bonds */
		int bondSum = AtomContainerManipulator.getSingleBondEquivalentSum(atomContainer);
		for(int i = 0; i < set.getAtomContainerCount(); i++){
			int bondSumI = AtomContainerManipulator.getSingleBondEquivalentSum(set.getAtomContainer(i));
			if(bondSumI >= bondSum)
				setOfAC.addAtomContainer(set.getAtomContainer(i));
		}
		return setOfAC;
	}
	/**
	 * <p>Get all resonance structures from an atomContainer. </p>
	 * 
	 * @param atomContainer The atomContainer to analyze
	 * @return The different resonance structures
	 */
	public IAtomContainerSet getAllStructures(IAtomContainer atomContainer){
		
		IAtomContainerSet setOfAtomContainer = atomContainer.getBuilder().newAtomContainerSet();
		setOfAtomContainer.addAtomContainer(atomContainer);
		Object[] params = new Object[1];
		if(hasActiveCenter)
			params[0] = Boolean.TRUE;
		else
			params[0] = Boolean.FALSE;

		try {
			for(int i = 0 ; i < setOfAtomContainer.getAtomContainerCount() ; i++){

				IMoleculeSet setOfReactants = atomContainer.getBuilder().newMoleculeSet();
				setOfReactants.addAtomContainer(setOfAtomContainer.getAtomContainer(i));
				if(cationR){
					/* RearrangementCation1Reaction */
					IReactionProcess type  = new RearrangementCation1Reaction();
			        type.setParameters(params);
					
			        removeFlags(setOfAtomContainer.getAtomContainer(i));
			        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
			        
			        if(setOfReactions.getReactionCount() != 0)
						for(int k = 0 ; k < setOfReactions.getReactionCount() ; k++)
						for(int j = 0 ; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount() ; j++){
							IAtomContainer set = setOfReactions.getReaction(k).getProducts().getAtomContainer(j);
//							logger.debug("RearrangementCation1Reaction");
							if(!existAC(setOfAtomContainer,set))
								setOfAtomContainer.addAtomContainer(setOfReactions.getReaction(k).getProducts().getAtomContainer(j));
						}

					/* RearrangementCation2Reaction*/
					type  = new RearrangementCation2Reaction();
			        type.setParameters(params);
					
			        removeFlags(setOfAtomContainer.getAtomContainer(i));
			        setOfReactions = type.initiate(setOfReactants, null);
			        
					if(setOfReactions.getReactionCount() != 0)
						for(int k = 0 ; k < setOfReactions.getReactionCount() ; k++)
						for(int j = 0 ; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount() ; j++){
							IAtomContainer set = setOfReactions.getReaction(k).getProducts().getAtomContainer(j);
//							logger.debug("RearrangementCation2Reaction");
							if(!existAC(setOfAtomContainer,set))
								setOfAtomContainer.addAtomContainer(setOfReactions.getReaction(k).getProducts().getAtomContainer(j));
						}
					/* RearrangementCation3Reaction*/
					type  = new RearrangementCation3Reaction();
			        type.setParameters(params);
					
			        removeFlags(setOfAtomContainer.getAtomContainer(i));
			        setOfReactions = type.initiate(setOfReactants, null);
			        
			        if(setOfReactions.getReactionCount() != 0)
						for(int k = 0 ; k < setOfReactions.getReactionCount() ; k++)
						for(int j = 0 ; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount() ; j++){
							IAtomContainer set = setOfReactions.getReaction(k).getProducts().getAtomContainer(j);
//							logger.debug("RearrangementCation3Reaction");
							if(!existAC(setOfAtomContainer,set))
								setOfAtomContainer.addAtomContainer(setOfReactions.getReaction(k).getProducts().getAtomContainer(j));
						}
				}
				if(anionR){
					/* RearrangementAnion1Reaction*/
					IReactionProcess type  = new RearrangementAnion1Reaction();
			        type.setParameters(params);
					
			        removeFlags(setOfAtomContainer.getAtomContainer(i));
			        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
			        
			        if(setOfReactions.getReactionCount() != 0)
						for(int k = 0 ; k < setOfReactions.getReactionCount() ; k++)
						for(int j = 0 ; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount() ; j++){
							IAtomContainer set = setOfReactions.getReaction(k).getProducts().getAtomContainer(j);
//							logger.debug("RearrangementAnion1Reaction");
							if(!existAC(setOfAtomContainer,set))
								setOfAtomContainer.addAtomContainer(setOfReactions.getReaction(k).getProducts().getAtomContainer(j));
						}
					/* RearrangementAnion2Reaction*/
			        type  = new RearrangementAnion2Reaction();
			        type.setParameters(params);
					
			        removeFlags(setOfAtomContainer.getAtomContainer(i));
			        setOfReactions = type.initiate(setOfReactants, null);
			        
			        if(setOfReactions.getReactionCount() != 0)
						for(int k = 0 ; k < setOfReactions.getReactionCount() ; k++)
						for(int j = 0 ; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount() ; j++){
							IAtomContainer set = setOfReactions.getReaction(k).getProducts().getAtomContainer(j);
//							logger.debug("RearrangementAnion2Reaction");
							if(!existAC(setOfAtomContainer,set))
								setOfAtomContainer.addAtomContainer(setOfReactions.getReaction(k).getProducts().getAtomContainer(j));
						}
					/* RearrangementAnion3Reaction*/
					type  = new RearrangementAnion3Reaction();
			        type.setParameters(params);
					
			        removeFlags(setOfAtomContainer.getAtomContainer(i));
			        setOfReactions = type.initiate(setOfReactants, null);
			        
			        if(setOfReactions.getReactionCount() != 0)
						for(int k = 0 ; k < setOfReactions.getReactionCount() ; k++)
						for(int j = 0 ; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount() ; j++){
							IAtomContainer set = setOfReactions.getReaction(k).getProducts().getAtomContainer(j);
//							logger.debug("RearrangementAnion3Reaction");
							if(!existAC(setOfAtomContainer,set))
								setOfAtomContainer.addAtomContainer(setOfReactions.getReaction(k).getProducts().getAtomContainer(j));
						}
				}
				if(radicalR){
					/* RearrangementRadical1Reaction*/
					IReactionProcess type  = new RearrangementRadical1Reaction();
			        type.setParameters(params);
					
			        removeFlags(setOfAtomContainer.getAtomContainer(i));
			        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
			        
			        if(setOfReactions.getReactionCount() != 0)
						for(int k = 0 ; k < setOfReactions.getReactionCount() ; k++)
						for(int j = 0 ; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount() ; j++){
							IAtomContainer set = setOfReactions.getReaction(k).getProducts().getAtomContainer(j);
//							logger.debug("RearrangementRadical1Reaction");
							if(!existAC(setOfAtomContainer,set))
								setOfAtomContainer.addAtomContainer(setOfReactions.getReaction(k).getProducts().getAtomContainer(j));
						}
					/* RearrangementRadical2Reaction*/
					type  = new RearrangementRadical2Reaction();
			        type.setParameters(params);
					
			        removeFlags(setOfAtomContainer.getAtomContainer(i));
			        setOfReactions = type.initiate(setOfReactants, null);
			        
			        if(setOfReactions.getReactionCount() != 0)
						for(int k = 0 ; k < setOfReactions.getReactionCount() ; k++)
						for(int j = 0 ; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount() ; j++){
							IAtomContainer set = setOfReactions.getReaction(k).getProducts().getAtomContainer(j);
//							logger.debug("RearrangementRadical2Reaction");
							if(!existAC(setOfAtomContainer,set))
								setOfAtomContainer.addAtomContainer(setOfReactions.getReaction(k).getProducts().getAtomContainer(j));
						}
					/* RearrangementRadical3Reaction*/
					type  = new RearrangementRadical3Reaction();
			        type.setParameters(params);
					
			        removeFlags(setOfAtomContainer.getAtomContainer(i));
			        setOfReactions = type.initiate(setOfReactants, null);
			        
			        if(setOfReactions.getReactionCount() != 0)
						for(int k = 0 ; k < setOfReactions.getReactionCount() ; k++)
						for(int j = 0 ; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount() ; j++){
							IAtomContainer set = setOfReactions.getReaction(k).getProducts().getAtomContainer(j);
//							logger.debug("RearrangementRadical3Reaction");
							if(!existAC(setOfAtomContainer,set))
								setOfAtomContainer.addAtomContainer(setOfReactions.getReaction(k).getProducts().getAtomContainer(j));
						}
				}
				if(bondR){
					/* DisplacementChargeFromAcceptorReaction*/
					IReactionProcess type  = new DisplacementChargeFromAcceptorReaction();
			        type.setParameters(params);

			        removeFlags(setOfAtomContainer.getAtomContainer(i));
			        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
			        
			        if(setOfReactions.getReactionCount() != 0)
						for(int k = 0 ; k < setOfReactions.getReactionCount() ; k++)
						for(int j = 0 ; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount() ; j++){
							IAtomContainer set = setOfReactions.getReaction(k).getProducts().getAtomContainer(j);
//							logger.debug("DisplacementChargeFromAcceptorReaction");
							if(!existAC(setOfAtomContainer,set))
								setOfAtomContainer.addAtomContainer(setOfReactions.getReaction(k).getProducts().getAtomContainer(j));
						}
			        /* DisplacementChargeFromDonorReaction*/
					type  = new DisplacementChargeFromDonorReaction();
			        type.setParameters(params);
					
			        removeFlags(setOfAtomContainer.getAtomContainer(i));
			        setOfReactions = type.initiate(setOfReactants, null);
			        
			        if(setOfReactions.getReactionCount() != 0)
						for(int k = 0 ; k < setOfReactions.getReactionCount() ; k++)
						for(int j = 0 ; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount() ; j++){
							IAtomContainer set = setOfReactions.getReaction(k).getProducts().getAtomContainer(j);
//							logger.debug("DisplacementChargeFromDonorReaction");
							if(!existAC(setOfAtomContainer,set))
								setOfAtomContainer.addAtomContainer(setOfReactions.getReaction(k).getProducts().getAtomContainer(j));
						}
				}
				if(hyperconjugationR){
					/* HyperconjugationReaction*/
					IReactionProcess type  = new HyperconjugationReaction();
			        type.setParameters(params);

			        removeFlags(setOfAtomContainer.getAtomContainer(i));
			        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
			        
			        if(setOfReactions.getReactionCount() != 0)
						for(int k = 0 ; k < setOfReactions.getReactionCount() ; k++)
							for(int j = 0 ; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount() ; j++){
								IAtomContainer set = setOfReactions.getReaction(k).getProducts().getAtomContainer(j);
//								logger.debug("HyperconjugationReaction");
								if(!existAC(setOfAtomContainer,set))
									setOfAtomContainer.addAtomContainer(setOfReactions.getReaction(k).getProducts().getAtomContainer(j));
							}
			    }

				/* this makes a limition of the search */
				if(maxStructuresToObtain != -1)
					if(setOfAtomContainer.getAtomContainerCount() > maxStructuresToObtain)
						break;
				
				if(i == 0 && setOfAtomContainer.getAtomContainerCount() > 9)
					return setOfAtomContainer;
			}
		} catch (CDKException e) {
			logger.error("Error while getting all resonance structures: ");
			logger.error(e.getMessage());
			logger.debug(e);
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
	private boolean existAC(IAtomContainerSet set, IAtomContainer atomContainer) {
//		logger.debug("smiles: "+(new SmilesGenerator(set.getBuilder())).createSMILES((IMolecule) atomContainer));
		atomContainer = setID(atomContainer);
		for(int i = 0 ; i < set.getAtomContainerCount(); i++){
			IAtomContainer ac = setID(set.getAtomContainer(i));
			QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolChargeIDQueryContainer(ac);
//			QueryAtomContainer qAC = QueryAtomContainerCreator.createAnyAtomContainer(atomContainer,false);
			try {
				if(UniversalIsomorphismTester.isIsomorph(atomContainer,qAC)){
//					logger.debug("exist");
					return true;
				}
			} catch (CDKException e1) {
				System.err.println(e1);
				logger.error(e1.getMessage());
				logger.debug(e1);
			}
		}
		return false;
	}
	/**
	 * remove the possible flags about CDKConstants.REACTIVE_CENTER
	 * 
	 * @param atomContainer
	 * @return
	 */
	private IAtomContainer removeFlags(IAtomContainer atomContainer){
		for(int i = 0 ; i < atomContainer.getAtomCount(); i++)
			atomContainer.getAtom(i).setFlag(CDKConstants.REACTIVE_CENTER,false);

		for(int i = 0 ; i < atomContainer.getBondCount(); i++)
			atomContainer.getBond(i).setFlag(CDKConstants.REACTIVE_CENTER,false);
		return atomContainer;
	}
	/**
	 * Set the ID as position
	 * 
	 * @param atomContainer
	 * @return
	 */
	private IAtomContainer setID(IAtomContainer atomContainer){
		for(int i = 0 ; i < atomContainer.getAtomCount(); i++){
			atomContainer.getAtom(i).setID(""+atomContainer.getAtomNumber(atomContainer.getAtom(i)));
		}
		return atomContainer;
	}

}
