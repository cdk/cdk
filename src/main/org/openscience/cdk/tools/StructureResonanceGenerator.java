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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openscience.cdk.reaction.type.parameters.IParameterReact;
import org.openscience.cdk.reaction.type.parameters.SetReactionCenter;

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
 * 
 * @see org.openscience.cdk.reaction.IReactionProcess
 */
@TestClass("org.openscience.cdk.tools.StructureResonanceGeneratorTest")
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
	 * @see #setDefaultReactions()
	 */
	public StructureResonanceGenerator(){
		this(false);	
	}
	/**
	 * Construct an instance of StructureResonanceGenerator. Default restrictions 
	 * are initiated.
	 * 
	 * @param lookingSymmetry  Specify if the resonance generation is based looking at the symmetry     
	 * @see #setDefaultReactions()
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
	 * @see #setReactions(java.util.List)
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
	 *
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
	 * @see #getReactions()
	 */
	@TestMethod("testSetDefaultReactions")
	public void setDefaultReactions(){
		callDefaultReactions();
		
	}

	private void callDefaultReactions() {
		List<IParameterReact> paramList = new ArrayList<IParameterReact>();
	    IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.FALSE);
        paramList.add(param);
        
		IReactionProcess type  = new SharingLonePairReaction();
        try {
			type.setParameterList(paramList);
		} catch (CDKException e) {
			e.printStackTrace();
		}
		reactionsList.add(type);
		
		type  = new PiBondingMovementReaction();
		List<IParameterReact> paramList2 = new ArrayList<IParameterReact>();
	    IParameterReact param2 = new SetReactionCenter();
        param2.setParameter(Boolean.FALSE);
        paramList2.add(param2);
        try {
			type.setParameterList(paramList2);
		} catch (CDKException e) {
			e.printStackTrace();
		}
		reactionsList.add(type);
		
		type  = new RearrangementAnionReaction();
		try {
			type.setParameterList(paramList);
		} catch (CDKException e) {
			e.printStackTrace();
		}
		reactionsList.add(type);
		
		type  = new RearrangementCationReaction();
		try {
			type.setParameterList(paramList);
		} catch (CDKException e) {
			e.printStackTrace();
		}
		reactionsList.add(type);
		
		type  = new RearrangementLonePairReaction();
		try {
			type.setParameterList(paramList);
		} catch (CDKException e) {
			e.printStackTrace();
		}
		reactionsList.add(type);
		
		type  = new RearrangementRadicalReaction();
		try {
			type.setParameterList(paramList);
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
            for (IReactionProcess aReactionsList : reactionsList) {
                IReactionProcess reaction = aReactionsList;
                IMoleculeSet setOfReactants = molecule.getBuilder().newMoleculeSet();
                setOfReactants.addMolecule(mol);
                try {
                    IReactionSet setOfReactions = reaction.initiate(setOfReactants, null);
                    if (setOfReactions.getReactionCount() != 0)
                        for (int k = 0; k < setOfReactions.getReactionCount(); k++)
                            for (int j = 0; j < setOfReactions.getReaction(k).getProducts().getAtomContainerCount(); j++)
                            {
                                IMolecule product = setOfReactions.getReaction(k).getProducts().getMolecule(j);
                                if (!existAC(setOfMol, product)) {
                                    setOfMol.addMolecule(product);
                                    countStructure++;
                                    if (countStructure > maxStructures)
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
    	
    	if(bondList.size() == 0)
    		return null;
    	
    	int[] flagBelonging = new int[bondList.size()];
    	for(int i = 0; i < flagBelonging.length; i++)
    		flagBelonging[i] = 0;
    	int[] position = new int[bondList.size()];
    	int maxGroup = 1;
    	
    	/*Analysis if the bond are linked together*/
    	List<IBond> newBondList = new ArrayList<IBond>();
    	newBondList.add(bondList.get(0));
    	
    	int pos = 0;
    	for(int i = 0 ; i < newBondList.size(); i ++){
    	
    		if(i==0)
    			flagBelonging[i] = maxGroup;
    		else{
    			if(flagBelonging[position[i]] == 0){
        			maxGroup++;
    				flagBelonging[position[i]] = maxGroup;
    			}
    		}
    		
    		IBond bondA = newBondList.get(i);
    		for(int ato = 0; ato < 2; ato++){
    	    	IAtom atomA1 = bondA.getAtom(ato);
    			List<IBond> bondA1s = molecule.getConnectedBondsList(atomA1);
	    		for(int j = 0 ; j < bondA1s.size(); j ++){
	        		IBond bondB = bondA1s.get(j);
	        		if(!newBondList.contains(bondB))
	        		for(int k = 0 ; k < bondList.size(); k ++)
	        			if(bondList.get(k).equals(bondB))
	                		if(flagBelonging[k] == 0){
	                			flagBelonging[k] = maxGroup;
	                			pos++;
	                			newBondList.add(bondB);
	                			position[pos] = k;
	                				
	                		}	
	    		}
    		}
    		//if it is final size and not all are added
    		if(newBondList.size()-1 == i)
    			for(int k = 0 ; k < bondList.size(); k ++)
    				if(!newBondList.contains(bondList.get(k))){
    					newBondList.add(bondList.get(k));
    					position[i+1] = k;
    					break;
    				}
    	}
    	/*creating containers according groups*/
    	for(int i = 0 ; i < maxGroup; i ++){
    		IAtomContainer container = molecule.getBuilder().newAtomContainer();
    		for(int j = 0 ; j < bondList.size(); j++){
    			if(flagBelonging[j] != i+1)
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
	 * <p>Get the container which the atom is found on resonance from a IMolecule. 
	 * It is based on looking if the order of the bond changes. Return null
	 * is any is found.</p>
	 * 
	 * @param molecule The IMolecule to analyze
	 * @param atom     The IAtom
	 * @return         The container with the atom
	 */
    @TestMethod("testGetContainer_IMolecule_IAtom")
	public IAtomContainer getContainer(IMolecule molecule, IAtom atom) {
    	IAtomContainerSet setOfCont = getContainers(molecule);
    	if(setOfCont == null)
    		return null;

        for (IAtomContainer container : setOfCont.atomContainers()) {
            if (container.contains(atom))
                return container;
        }
    	
    	return null;
	}
    /**
	 * <p>Get the container which the bond is found on resonance from a IMolecule. 
	 * It is based on looking if the order of the bond changes. Return null
	 * is any is found.</p>
	 * 
	 * @param molecule The IMolecule to analyze
	 * @param bond     The IBond
	 * @return         The container with the bond
	 */
    @TestMethod("testGetContainer_IMolecule_IBond")
	public IAtomContainer getContainer(IMolecule molecule, IBond bond) {
    	IAtomContainerSet setOfCont = getContainers(molecule);
    	if(setOfCont == null)
    		return null;

        for (IAtomContainer container : setOfCont.atomContainers()) {
            if (container.contains(bond))
                return container;
        }
    	
    	return null;
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

		IAtomContainer acClone = null;
    	try {
    		acClone = (IMolecule) atomContainer.clone();
			if(!lookingSymmetry){ /*remove all aromatic flags*/
				for (IAtom atom : acClone.atoms()) atom.setFlag(CDKConstants.ISAROMATIC, false);
				for (IBond bond : acClone.bonds()) bond.setFlag(CDKConstants.ISAROMATIC, false);
			}
		} catch (CloneNotSupportedException e1) {
			e1.printStackTrace();
		}
    	
		for(int i = 0 ; i < acClone.getAtomCount(); i++)
//			if(acClone.getAtom(i).getID() == null)
				acClone.getAtom(i).setID(""+acClone.getAtomNumber(acClone.getAtom(i)));
			
		if(lookingSymmetry){
			try {
				CDKHueckelAromaticityDetector.detectAromaticity(acClone);
			} catch (CDKException e) {
				e.printStackTrace();
			}
		}else{
				if(!lookingSymmetry){ /*remove all aromatic flags*/
					for (IAtom atom : acClone.atoms()) atom.setFlag(CDKConstants.ISAROMATIC, false);
					for (IBond bond : acClone.bonds()) bond.setFlag(CDKConstants.ISAROMATIC, false);
				}
		}
		for(int i = 0 ; i < set.getAtomContainerCount(); i++){
			IAtomContainer ss = set.getAtomContainer(i);
			for(int j = 0 ; j < ss.getAtomCount(); j++)
//				if(ss.getAtom(j).getID() == null)
					ss.getAtom(j).setID(""+ss.getAtomNumber(ss.getAtom(j)));
				
			try {
				
				if(!lookingSymmetry ){
					QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolChargeIDQueryContainer(acClone);
					if(UniversalIsomorphismTester.isIsomorph(ss,qAC)){
						QueryAtomContainer qAC2 = QueryAtomContainerCreator.createSymbolAndBondOrderQueryContainer(acClone);
						if(UniversalIsomorphismTester.isIsomorph(ss,qAC2))
							return true;
					}
				}else{
					QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(acClone);
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
