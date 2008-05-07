/* $Revision$ $Author$ $Date$
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.PiBondingMovementReaction;
import org.openscience.cdk.reaction.type.RearrangementAnionReaction;
import org.openscience.cdk.reaction.type.RearrangementCationReaction;
import org.openscience.cdk.reaction.type.RearrangementLonePairReaction;
import org.openscience.cdk.reaction.type.RearrangementRadicalReaction;
import org.openscience.cdk.reaction.type.SharingLonePairReaction;

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
 * MoleculeSet setOf = srG.getResonances(new Molecule());
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
 * @cdk.svnrev   $Revision$
 * @cdk.bug      1728830
 * 
 * @see org.openscience.cdk.reaction.IReactionProcess
 */
@TestClass("org.openscience.cdk.test.tools.StructureResonanceGeneratorTest")
public class StructureResonanceGenerator {
	
	private LoggingTool logger = new LoggingTool(StructureResonanceGenerator.class);
	private List<IReactionProcess> reactionsList = new ArrayList<IReactionProcess>();
	/**Generate resonance structure without looking at the symmetry*/
	private boolean lookingSymmetry;
	/** TODO: REACT: some time takes too much time. At the moment fixed to 50 structures*/
	private int maxStructures = 50;
	/**
	 * Construct an instance of StructureResonanceGenerator. Default restrictions 
	 * are initiated.
	 * 
	 * @see #setDefaultReactionss()
	 */
	public StructureResonanceGenerator(){
		this(false);	
	}
	/**
	 * Construct an instance of StructureResonanceGenerator. Default restrictions 
	 * are initiated.
	 * 
	 * @param lookingSymmetry  Specify if the resonance generation is based looking at the symmetry     
	 * @see #setDefaultReactionss()
	 */
	public StructureResonanceGenerator(boolean lookingSymmetry){
        logger.info("Initiate StructureResonanceGenerator");
        this.lookingSymmetry = lookingSymmetry;
		setDefaultReactions();
		
	}
	/**
	 * Set the reactions that must be used in the generation of the resonance.
	 * 
	 * @param newReactionsList  The IReactionsProcess's to use
	 * 
	 * @see #getReactions()
	 * @see #setReactionsDefault()
	 * @see IReactionProcess
	 */
	@TestMethod("testSetReactions_List")
	public void setReactions(List<IReactionProcess> newReactionsList)  throws CDKException {
		reactionsList = newReactionsList;
	}
	/**
	 * Get the reactions that must be presents in the generation of the resonance.
	 * 
	 * @return The reactions to be imposed
	 * 
	 * @see #getReactions(IRreactionProcess)
	 * @see #setDefaultReactions()
	 */
	@TestMethod("testGetReactions")
	public List<IReactionProcess> getReactions(){
		return this.reactionsList;
	}
	/**
	 * Set the number maximal of resonance structures to be found. The 
	 * algorithm breaks the process when is came to this number.
	 * 
	 * @param maxStruct The maximal number
	 */
	@TestMethod("testSetMaximalStructures_int")
	public void setMaximalStructures(int maxStruct){
		maxStructures = maxStruct;
	}
	/**
	 * Get the number maximal of resonance structures to be found.
	 * 
	 * @return The maximal number
	 */
	@TestMethod("testGetMaximalStructures")
	public int getMaximalStructures(){
		return maxStructures;
	}
	/**
	 * Set the default reactions that must be presents to generate the resonance.
	 *  
	 * @return The reactions imposed
	 * 
	 * @see #getReactions(IReactionProcess)
	 * @see #setDefaultReactions()
	 */
	@TestMethod("testSetDefaultReactions")
	public void setDefaultReactions(){
		callDefaultReactions();
		
	}
	/**
	 * Create the default reactions List. They are:<p>
	 * 
	 * @throws CDKException 
	 * 
	 */
	private void callDefaultReactions() {
		HashMap<String,Object> params = new HashMap<String,Object>();
		params.put("hasActiveCenter",Boolean.FALSE);;
		
		IReactionProcess type  = new SharingLonePairReaction();
        try {
			type.setParameters(params);
		} catch (CDKException e) {
			e.printStackTrace();
		}
		reactionsList.add(type);
		
		type  = new PiBondingMovementReaction();
		HashMap<String,Object> params2 = new HashMap<String,Object>();
		params2.put("hasActiveCenter",Boolean.FALSE);
		try {
			type.setParameters(params2);
		} catch (CDKException e) {
			e.printStackTrace();
		}
		reactionsList.add(type);
		
		type  = new RearrangementAnionReaction();
		try {
			type.setParameters(params);
		} catch (CDKException e) {
			e.printStackTrace();
		}
		reactionsList.add(type);
		
		type  = new RearrangementCationReaction();
		try {
			type.setParameters(params);
		} catch (CDKException e) {
			e.printStackTrace();
		}
		reactionsList.add(type);
		
		type  = new RearrangementLonePairReaction();
		try {
			type.setParameters(params);
		} catch (CDKException e) {
			e.printStackTrace();
		}
		reactionsList.add(type);
		
		type  = new RearrangementRadicalReaction();
		try {
			type.setParameters(params);
		} catch (CDKException e) {
			e.printStackTrace();
		}
		reactionsList.add(type);
		
	}
	/**
	 * <p>Get the resonance structures from an IMolecule. </p>
	 * 
	 * @param molecule The IMolecule to analyze
	 * @return         The different resonance structures
	 */
    @TestMethod("testGetStructures_IMolecule")
	public IMoleculeSet getStructures(IMolecule molecule) {
    	int countStructure = 0;
		IMoleculeSet setOfMol = molecule.getBuilder().newMoleculeSet();
		setOfMol.addMolecule(molecule);
		
		for(int i = 0 ; i < setOfMol.getMoleculeCount() ; i++){
			IMolecule mol = setOfMol.getMolecule(i);
			Iterator<IReactionProcess> itReact = reactionsList.iterator();
			while(itReact.hasNext()){
				IReactionProcess reaction = itReact.next();
				IMoleculeSet setOfReactants = molecule.getBuilder().newMoleculeSet();
				setOfReactants.addMolecule(mol);
				try {
					IReactionSet setOfReactions = reaction.initiate(setOfReactants, null);
					 if(setOfReactions.getReactionCount() != 0)
						for(int k = 0 ; k < setOfReactions.getReactionCount() ; k++)
							for(int j = 0 ; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount() ; j++){
								IMolecule product = setOfReactions.getReaction(k).getProducts().getMolecule(j);
								if(!existAC(setOfMol,product)){
									setOfMol.addMolecule(product);
									countStructure++;
									if(countStructure>maxStructures)
										return setOfMol;
								}
						}
				} catch (CDKException e) {
					e.printStackTrace();
				}
			}
		}
		return setOfMol;
	}

	/**
	 * <p>Get the container which is found resonance from a IMolecule. 
	 * It is based on looking if the order of the bond changes.</p>
	 * 
	 * @param molecule The IMolecule to analyze
	 * @return         The different containers
	 */
    @TestMethod("testGetContainers_IMolecule")
	public IAtomContainerSet getContainers(IMolecule molecule) {
    	IAtomContainerSet setOfCont = molecule.getBuilder().newAtomContainerSet();
		IMoleculeSet setOfMol = getStructures(molecule);
		if(setOfMol.getMoleculeCount() == 0)
			return setOfCont;
		
    	/*extraction of all bonds which has been produced a changes of order*/
		List<IBond> bondList = new ArrayList<IBond>();
    	for(int i = 1 ; i < setOfMol.getMoleculeCount() ; i++){
    		IMolecule mol = setOfMol.getMolecule(i);
    		for(int j = 0; j < mol.getBondCount(); j++){
				IBond bond = molecule.getBond(j);
				if(!mol.getBond(j).getOrder().equals(bond.getOrder())){
					if(!bondList.contains(bond))
						bondList.add(bond);
				}
			}
		}
    	int[] flagBelonging = new int[bondList.size()];
    	for(int i = 0; i < flagBelonging.length; i++)
    		flagBelonging[i] = 0;
    	int maxGroup = 1;
    	/*Analysis if the bond are linked together*/
    	for(int i = 0 ; i < bondList.size(); i ++){
    		if(i==0)
    			flagBelonging[i] = maxGroup;
    		else
    			if(flagBelonging[i] == 0){
    				maxGroup++;
    				flagBelonging[i] = maxGroup;
    			}
    		
    		IBond bondA = bondList.get(i);
    		IAtom atomA1 = bondA.getAtom(0);
    		IAtom atomA2 = bondA.getAtom(1);
    		for(int j = 0 ; j < bondList.size(); j ++){
        		IBond bondB = bondList.get(j);
        		if(i != j){
        			if(bondB.contains(atomA1) || bondB.contains(atomA2)){
        				flagBelonging[j] = flagBelonging[i];
        			}
        		}
    		}
    	}
    	/*creating containers according groups*/
    	for(int i = 0 ; i < maxGroup ; i ++){
    		IAtomContainer container = molecule.getBuilder().newAtomContainer();
    		for(int j = 0 ; j < bondList.size(); j++){
    			if(flagBelonging[j] != maxGroup)
    				continue;
    			IBond bond = bondList.get(j);
    			IAtom atomA1 = bond.getAtom(0);
        		IAtom atomA2 = bond.getAtom(1);
        		if(!container.contains(atomA1))
        			container.addAtom(atomA1);
        		if(!container.contains(atomA2))
        			container.addAtom(atomA2);
        		container.addBond(bond);
    		}
    		setOfCont.addAtomContainer(container);
    	}
		return setOfCont;
	}
	/**
	 * Search if the setOfAtomContainer contains the atomContainer
	 *  
	 * 
	 * @param set            ISetOfAtomContainer object where to search
	 * @param atomContainer  IAtomContainer to search
	 * @return   			 True, if the atomContainer is contained
	 */
	private boolean existAC(IAtomContainerSet set, IAtomContainer atomContainer) {

		for(int i = 0 ; i < atomContainer.getAtomCount(); i++)
			atomContainer.getAtom(i).setID(""+atomContainer.getAtomNumber(atomContainer.getAtom(i)));
		if(lookingSymmetry){
			try {
				CDKHueckelAromaticityDetector.detectAromaticity(atomContainer);
			} catch (CDKException e) {
				e.printStackTrace();
			}
			
		}
		for(int i = 0 ; i < set.getAtomContainerCount(); i++){
			IAtomContainer ss = set.getAtomContainer(i);
			for(int j = 0 ; j < ss.getAtomCount(); j++)
				ss.getAtom(j).setID(""+ss.getAtomNumber(ss.getAtom(j)));
			try {
				
				if(!lookingSymmetry ){
					QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolChargeIDQueryContainer(atomContainer);
					if(UniversalIsomorphismTester.isIsomorph(ss,qAC)){
						QueryAtomContainer qAC2 = QueryAtomContainerCreator.createSymbolAndBondOrderQueryContainer(atomContainer);
						if(UniversalIsomorphismTester.isIsomorph(ss,qAC2))
							return true;
					}
				}else{
					QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(atomContainer);
					CDKHueckelAromaticityDetector.detectAromaticity(ss);
					if(UniversalIsomorphismTester.isIsomorph(ss,qAC))
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
}
