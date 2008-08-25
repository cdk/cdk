/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2005-2007  Christian Hoppe <chhoppe@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.tools;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.smiles.SmilesGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Generate ring and Murcko-like fragments.
 * <ul>
 *   <li>ring fragments (largest ringsystems)</li>
 *   <li>Murcko fragments described by Murcko et al. {@cdk.cite MURCKO96}</li>
 * </ul>
 *
 * Linkers include the ring atoms. In CDK notion aromatic atoms are flagged as ISAROMATIC and have small letters in smiles.
 * If you want to create molecules out of these smiles you have to change all the letters to upper ones.
 * For this please use isSmilesToUpperCase() and setSmilesToUpperCase()
 * 
 * <p>Due to some problems with SaturationChecker the SMILES generation might be a problem.
 * When you want to use the get..SmileArray methods please do (that seems to work, refer test 13+14):
 * <pre>
 * HydrogenAdder ha= new HydrogenAdder();
 * ha.addExplicitHydrogensToSatisfyValency(molecule);
 * GenerateFragments gf=new GenerateFragments();
 * gf.generateMurckoFragments(molecule,booelan,booelan);
 * String[] smiles=gf.getMurckoFrameworksAsSmileArray();
 * </pre>
 *
 * @author      chhoppe from EUROSCREEN
 * @cdk.created 2006-3-23
 * @cdk.module  extra
 * @cdk.svnrev  $Revision$
 * @cdk.keyword Murcko fragments
 * @cdk.bug     1848591
 **/
public class GenerateFragments {
	
	private LoggingTool logger;
	
	private List murckoFragments =null;
	private List ringFragments=null;
	private List linkerFragments=null;
	private IRingSet ringSetsMolecule=null;
	private boolean sidechainHetatoms=true;
	private boolean exocyclicDoubleBonds=true;
	private boolean smilesToUpperCase=false;
	//private IChemObjectBuilder builder;
	
	public GenerateFragments() {
		logger = new LoggingTool(this);
    }
	
	/**
	 * generates ring fragments from SSSR and RingPartitioner method
	 * @param molecule
	 * void
	 */
	public void generateRingFragments(IMolecule molecule){
		this.ringFragments= new ArrayList();
		this.ringSetsMolecule = new SSSRFinder(molecule).findSSSR();
		if (this.ringSetsMolecule.getAtomContainerCount() > 0) {
			this.ringFragments=RingPartitioner.partitionRings(ringSetsMolecule);
		}
	}
	
	
	/**
	 * generates Murcko fragments takes two parameters
	 * @param molecule	the molecule
	 * @param sidechainHetatoms	boolean if sidchain hetero atoms should be included (true)
	 * @param exocyclicDoubleBonds boolean if bonds with order >1 should be included on ring systems and linkers
	 * @param minimumRingSize int indicates the minimum ring size as to considered as fragment (ringSize<minimimRingSize)
	 * return	void
	 */
	public void generateMurckoFragments(IMolecule molecule, boolean sidechainHetatoms,boolean exocyclicDoubleBonds, int minimumRingSize) throws org.openscience.cdk.exception.CDKException{
		//logger.debug("****** generatemurckoFragments *******");
		//VARIABLES
		this.murckoFragments =new Vector();
		this.linkerFragments=new Vector();
		this.sidechainHetatoms=sidechainHetatoms;
		this.exocyclicDoubleBonds=exocyclicDoubleBonds;
					
		IAtom firstRingAtom = null;	
		IAtom secondRingAtom = null;	
		IAtomContainer firstRingAtomContainer=null;
		IAtomContainer secondRingAtomContainer=null;
		IAtomContainer firstRingSubstituents=null;
		IAtomContainer secondRingSubstituents=null;
		IAtomContainer path=null;
		IMolecule murckoFragment=null;
		IMolecule linkerFragment=null;
		List tmpRingFragments=new Vector();
		
		//Initialize
		generateRingFragments(molecule);
		
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			molecule.getAtom(i).setFlag(CDKConstants.ISINRING, false);
		}
				
		//logger.debug("Number of RingSystems:"+this.ringFragments.size());
		for (int f = 0; f < this.ringFragments.size(); f++) {
			firstRingAtomContainer = molecule.getBuilder().newAtomContainer();
			IRingSet ringSet = (IRingSet)this.ringFragments.get(f);
			for (int i=0;i<ringSet.getAtomContainerCount();i++) {
				firstRingAtomContainer.add(ringSet.getAtomContainer(i));
			}
			
			if (firstRingAtomContainer.getAtomCount()>=minimumRingSize){
				tmpRingFragments.add(firstRingAtomContainer);
				for (int g = 0; g < firstRingAtomContainer.getAtomCount(); g++) {
					molecule.getAtom(molecule.getAtomNumber(firstRingAtomContainer.getAtom(g))).setFlag(CDKConstants.ISINRING, true);
				}
				
			}
		}

//		START
		//logger.debug("Number of RingSystems:"+tmpRingFragments.size());
		if (tmpRingFragments.size() > 1) {
			//go through all ringsystems
			//for (int f = 0; f < this.ringFragments.size()-1; f++) {
			for (int f = 0; f < tmpRingFragments.size()-1; f++) {	
			//firstRingAtomContainer = RingSetManipulator.getAllInOneContainer((IRingSet) this.ringFragments.get(f));
				firstRingAtomContainer = (IAtomContainer)tmpRingFragments.get(f);
				//for (int g = f+1; g < this.ringFragments.size(); g++) {
					//secondRingAtomContainer = RingSetManipulator.getAllInOneContainer((IRingSet) this.ringFragments.get(g));
				for (int g = f+1; g < tmpRingFragments.size(); g++) {
					secondRingAtomContainer = (IAtomContainer)tmpRingFragments.get(g);
					for (int h = 0; h < firstRingAtomContainer.getAtomCount(); h++){
						firstRingAtom=firstRingAtomContainer.getAtom(h);
						firstRingSubstituents=getPossibleLinkerSubstituents(firstRingAtom,molecule,firstRingAtomContainer);
						if (firstRingSubstituents.getAtomCount()>0){
							//go through substituents of first ring
							for (int i = 0; i < firstRingSubstituents.getAtomCount(); i++){
//								logger.debug("First Ring Sub is in RING");
//								//check for ring-ring system
								//Is a zero-atom linker
								if (firstRingSubstituents.getAtom(i).getFlag(CDKConstants.ISINRING) && secondRingAtomContainer.contains(firstRingSubstituents.getAtom(i))){
									//logger.debug("\tFound a ring-ring System");
									murckoFragment=new Molecule();
									
									murckoFragment=addFragments(firstRingAtomContainer,murckoFragment,molecule);
									murckoFragment=addFragments(secondRingAtomContainer,murckoFragment,molecule);
									murckoFragment=addFragmentBonds(murckoFragment,molecule);
									
									linkerFragment=new Molecule();
									linkerFragment.addAtom(firstRingAtom);
									linkerFragment.addAtom(firstRingSubstituents.getAtom(i));
									linkerFragment=addFragmentBonds(linkerFragment, molecule);
									
									this.linkerFragments.add(linkerFragment);
									this.murckoFragments.add(murckoFragment);
									//logger.debug("MFragment:"+murckoFragment.getAtomCount()+" CC:"+ConnectivityChecker.isConnected(murckoFragment));
									//logger.debug(murckoFragment.toString());
									//logger.debug("\tADD MURCKOFRAGMENT");
									break;
								}
//								compare to substituents of second ring
								for (int j = 0; j < secondRingAtomContainer.getAtomCount(); j++){
									secondRingAtom=secondRingAtomContainer.getAtom(j);
									secondRingSubstituents=getPossibleLinkerSubstituents(secondRingAtom,molecule,secondRingAtomContainer);
									if (secondRingSubstituents.getAtomCount()>0){
										//go through substituents of second ring
										for (int k = 0; k < secondRingSubstituents.getAtomCount(); k++){//For-k
											
											//logger.debug("First Ring Size:"+firstRingAtomContainer.getAtomCount()+" 2.Ring Size:"+secondRingAtomContainer.getAtomCount());
											//logger.debug(f+".ringSub:"+molecule.getAtomNumber(firstRingSubstituents.getAtomAt(i))+" Sym:"+firstRingSubstituents.getAtomAt(i).getSymbol()+" "+g+".ringSub:"+molecule.getAtomNumber(secondRingSubstituents.getAtomAt(k)));
											path=new AtomContainer();
											resetFlags(molecule);
											PathTools.depthFirstTargetSearch(molecule,firstRingSubstituents.getAtom(i),secondRingSubstituents.getAtom(k),path);
											/*logger.debug("\tPATHSIZE:"+path.getAtomCount());
											logger.debug("\tFIRST PATHATOM:"+molecule.getAtomNumber(path.getAtomAt(0)));
											try{
												logger.debug("\tLAST PATHATOM:"+molecule.getAtomNumber(path.getAtomAt(path.getAtomCount()-1)));
											}catch(Exception eS){
												logger.debug("\tNO LAST PATHATOM");
											}*/
											
											if (firstRingSubstituents.getAtom(i)==secondRingSubstituents.getAtom(k)){
												//logger.debug("\tSubstituents are equal");
												path.addAtom(firstRingSubstituents.getAtom(i));
											}
											
											//Check Path, direct connection between the substituents ->linker
											if ((checkPath(firstRingAtom, secondRingAtom, path) && path.getAtomCount()>0)){
												murckoFragment=new Molecule();
												
												//add both root atoms to path
												if (!path.contains(firstRingSubstituents.getAtom(i))){
													path.addAtom(firstRingSubstituents.getAtom(i));
												}
												if (!path.contains(secondRingSubstituents.getAtom(i))){
													path.addAtom(secondRingSubstituents.getAtom(i));
												}
//													//add both ring atoms to path
												//if (!path.contains(firstRingAtom)){
												//	path.addAtom(firstRingAtom);
												//}
												//if (!path.contains(secondRingAtom)){
												//	path.addAtom(secondRingAtom);
												//}
												//1. add path
												//2. add rings  
												//3. connect ring atoms to path
												murckoFragment=addPathFragments(path,murckoFragment,molecule);
												linkerFragment=new Molecule(murckoFragment);
												if (!linkerFragment.contains(firstRingAtom)){
													linkerFragment.addAtom(firstRingAtom);
												}
												if (!linkerFragment.contains(secondRingAtom)){
													linkerFragment.addAtom(secondRingAtom);
												}													
												linkerFragment=addFragmentBonds(linkerFragment, molecule);
												murckoFragment=addFragments(firstRingAtomContainer,murckoFragment,molecule);
												murckoFragment=addFragments(secondRingAtomContainer,murckoFragment,molecule);
												
												murckoFragment=addFragmentBonds(murckoFragment,molecule);
																																																	
												this.linkerFragments.add(linkerFragment);
												this.murckoFragments.add(murckoFragment);
												//logger.debug("\tADD MURCKOFRAGMENT");
											}else{
												//logger.debug("\tEND PATH");
											}
										}//For-k
									}//if 2.ring sub
								}//For-j
							}//For-i
						}//if 1.ring sub
					}//For-h
				}//For-g				
			}//For-f				
		}else if (tmpRingFragments.size() ==1){
			//logger.debug("Number of RingSystems is 1");
			murckoFragment=new Molecule();
			murckoFragment=addFragments((IRingSet) this.ringFragments.get(0),murckoFragment,molecule);
			murckoFragment=addFragmentBonds(murckoFragment,molecule);
			this.murckoFragments.add(murckoFragment);
		}
		
	}
	
	
	/**
	 * add the atoms on the shortest path to the murcko fragment
	 * Care about ring atoms in the path and sidechainHetatoms
	 * @param addAtomContainer	path
	 * @param targetMolecule	murcko fragment storage
	 * @param mainMolecule		original molecule
	 * @return IMolecule		murcko fragment
	 */
	private IMolecule addPathFragments(IAtomContainer addAtomContainer,IMolecule targetMolecule, IMolecule mainMolecule){
		IAtomContainer ringAtomContainer=null;
		List atoms=null;
		
		//1. check if linker atom is member of a ring system
		//2. check if heteroatoms bonded to a non ring linker atom should be included
		//3. check if exocyclic double or triple bonded atoms to linker schould be included
		for (int i=0;i<addAtomContainer.getAtomCount();i++){
			
			if (addAtomContainer.getAtom(i).getFlag(CDKConstants.ISINRING)&& !targetMolecule.contains(addAtomContainer.getAtom(i))){
				//Find all Ring atoms and add them 
				for (int j = 0; j < this.ringFragments.size(); j++) {
					ringAtomContainer = addAtomContainer.getBuilder().newAtomContainer();
					IRingSet ringSet = (IRingSet)this.ringFragments.get(j);
					for (int k=0; k<ringSet.getAtomContainerCount(); k++) {
						ringAtomContainer.add(ringSet.getAtomContainer(k));
					}
					if (ringAtomContainer.contains(addAtomContainer.getAtom(i))){
						targetMolecule=addFragments(ringAtomContainer, targetMolecule,mainMolecule);
						break;
					}
				}
			}else if((this.sidechainHetatoms || this.exocyclicDoubleBonds) && !targetMolecule.contains(addAtomContainer.getAtom(i))){
				atoms=mainMolecule.getConnectedAtomsList(addAtomContainer.getAtom(i));
				targetMolecule.addAtom(addAtomContainer.getAtom(i));	
				for (int j = 0; j < atoms.size(); j++) {
					IAtom atom = (IAtom)atoms.get(j);
					//logger.debug("HETATOM:"+atoms[j].getSymbol());
					if (this.sidechainHetatoms && !addAtomContainer.getAtom(i).getFlag(CDKConstants.ISINRING) && !(atom.getSymbol()).equals("C") && !(atom.getSymbol()).equals("H") && !targetMolecule.contains(atom)){
						//logger.debug("HETATOM TRUE");
						targetMolecule.addAtom(atom);
					}
					if (this.exocyclicDoubleBonds &&
						mainMolecule.getBond(atom,addAtomContainer.getAtom(i)).getOrder() != IBond.Order.SINGLE && 
						!targetMolecule.contains(atom)){
						//logger.debug("EXOCYCLIC DB TRUE");
						targetMolecule.addAtom(atom);
					}	
				}
			}else{
				if (!targetMolecule.contains(addAtomContainer.getAtom(i))){
					targetMolecule.addAtom(addAtomContainer.getAtom(i));
				}
			}
		}

		return targetMolecule;
	}
	
	/**
	 * add bonds to the murcko fragments
	 * @param targetMolecule	murcko fragment storage
	 * @param mainMolecule		original molecule
	 * @return 	IMolecule		murcko fragment
	 */
	private IMolecule addFragmentBonds(IMolecule targetMolecule, IMolecule mainMolecule){
		int firstAtomNumber=0;
		int secondAtomNumber=0;
		for (int i=0;i<targetMolecule.getAtomCount()-1;i++){
			for (int j = i+1; j < targetMolecule.getAtomCount(); j++) {
				
				if (mainMolecule.getBond(targetMolecule.getAtom(i),targetMolecule.getAtom(j)) !=null){
					firstAtomNumber=targetMolecule.getAtomNumber(targetMolecule.getAtom(i));
					secondAtomNumber=targetMolecule.getAtomNumber(targetMolecule.getAtom(j));
					targetMolecule.addBond(firstAtomNumber,secondAtomNumber,mainMolecule.getBond(targetMolecule.getAtom(i),targetMolecule.getAtom(j)).getOrder());
					if (mainMolecule.getBond(targetMolecule.getAtom(i),targetMolecule.getAtom(j)).getFlag(CDKConstants.ISAROMATIC) == true){
						targetMolecule.getBond(targetMolecule.getAtom(firstAtomNumber),targetMolecule.getAtom(secondAtomNumber)).setFlag(CDKConstants.ISAROMATIC, true);
					}
				}
			}
		}
		return targetMolecule;
	}
	
	private IMolecule addFragments(IRingSet ringSet, IMolecule targetMolecule, IMolecule mainMolecule){
		for (int i=0;i<ringSet.getAtomContainerCount();i++) {
			addFragments(ringSet.getAtomContainer(i), targetMolecule, mainMolecule);
		}
		return targetMolecule;
	}	
	
	/**
	 * add the rings to the murcko fragment
	 * @param addAtomContainer	IAtomContainer with the ring atoms
	 * @param targetMolecule	murcko fragment
	 * @return	IMolecule		murcko fragment
	 */
	private IMolecule addFragments(IAtomContainer addAtomContainer, IMolecule targetMolecule, IMolecule mainMolecule){
		List<IAtom> atoms;
		for (int i=0;i<addAtomContainer.getAtomCount();i++){
			targetMolecule.addAtom(addAtomContainer.getAtom(i));
			targetMolecule.addAtom(addAtomContainer.getAtom(i));	
			//Check for double bonds
			atoms=mainMolecule.getConnectedAtomsList(addAtomContainer.getAtom(i));
            for (IAtom atom : atoms) {
                if (this.exocyclicDoubleBonds &&
                        mainMolecule.getBond(atom, addAtomContainer.getAtom(i)).getOrder() != IBond.Order.SINGLE &&
                        !targetMolecule.contains(atom)) {
                    targetMolecule.addAtom(atom);
                }
            }
		}
		return targetMolecule;
	}
	
	/**
	 * checks if the starting point and the end point of the shortest path are in the path
	 * if true path is rejected 
	 * @param firstRingAtom		IAtom start point
	 * @param secondRingAtom	IAtom end point
	 * @param path				IAtomContainer path
	 * @return	boolean			true if path is reasonable
	 */
	private boolean checkPath(IAtom firstRingAtom, IAtom secondRingAtom, IAtomContainer path){
		//logger.debug("CHECK PATH");
        return !(path.contains(firstRingAtom) || path.contains(secondRingAtom));
    }
	
	/**
	 * get starting points (IAtom) of possible linkers  
	 * @param ringAtom		IAtom the ring atom
	 * @param molecule		IMolecule original molecule
	 * @param ringSystem	IAtomContainer the ring system
	 * @return	IAtomContainer possible starting points of linkers
	 */
	private IAtomContainer getPossibleLinkerSubstituents(IAtom ringAtom,IMolecule molecule, IAtomContainer ringSystem){
		List<IAtom> atoms = molecule.getConnectedAtomsList(ringAtom);
		IAtomContainer substituents=new AtomContainer();        
        for (IAtom atom : atoms) {
            if (!ringSystem.contains(atom) && !atom.getSymbol().equals("H")) {
                substituents.addAtom(atom);
            }
        }
		return substituents;
	}

	/**
	 * check for zero-Atom linkers  
	 * @param firstRingAtom		IAtom of the first root atom
	 * @param secondRingAtom	IAtom of the second root atom
	 * @param molecule			IMolecule original molecule
	 * @return	boolean true for zero atom linker, eg in biphenyl systems
	 */
	private boolean zeroAtomLinker(IAtom firstRingAtom, IAtom secondRingAtom, IMolecule molecule){
		
		List atoms= molecule.getConnectedAtomsList(firstRingAtom);
        return atoms.contains(secondRingAtom);
	}
	
	
	
	
	/**
	 * @return String[] smiles of the murcko fragments
	 */
	public String[] getMurckoFrameworksAsSmileArray(){
		SmilesGenerator sg;
		String[] murckoFragmentsmiles={};
		if (this.murckoFragments !=null){
			murckoFragmentsmiles=new String[this.murckoFragments.size()];
			//logger.debug("SIZE OF MURCKO VECTOR:"+this.murckoFragments.size());
			//logger.debug("SIZE OF SMILES[]:"+murckoFragmentsmiles.length);
			for (int i =0;i<this.murckoFragments.size();i++){
				try{
					IMolecule mol=(IMolecule)this.murckoFragments.get(i);
					if (ConnectivityChecker.isConnected(mol)){
						sg = new SmilesGenerator();
						if (smilesToUpperCase){
							murckoFragmentsmiles[i]=sg.createSMILES(mol).toUpperCase();
						}else{
							murckoFragmentsmiles[i]=sg.createSMILES(mol);
						}
					}else{
						logger.debug("ERROR in getMurckoFrameworksAsSmileArray due to:Molecule is not connected");
					}
				} catch (Exception e){
					logger.error("ERROR in getMurckoFrameworksAsSmileArray due to:"+e.toString());
					logger.debug(e);
				}		
			}	
		}
		return murckoFragmentsmiles;
	}
	
	/**
	 * @return String[] smiles of the ring fragments NOT WORKING
	 */
	public String[] getRingFragmentsAsSmileArray(){
		SmilesGenerator sg;
		String[] ringFragmentSmiles={};
		if (this.ringFragments !=null){
			ringFragmentSmiles=new String[this.ringFragments.size()];
			//logger.debug("SIZE OF MURCKO VECTOR:"+this.ringFragments.size());
			//logger.debug("SIZE OF SMILES[]:"+ringFragmentSmiles.length);
			for (int i =0;i<this.ringFragments.size();i++){
				try{
					IMolecule mol=(IMolecule)this.ringFragments.get(i);
					sg = new SmilesGenerator();
					if (smilesToUpperCase){
						ringFragmentSmiles[i]=sg.createSMILES(mol).toUpperCase();
					}else{
						ringFragmentSmiles[i]=sg.createSMILES(mol);
					}					
				} catch (Exception e){
					logger.error("ERROR in smile generation due to:"+e.toString());			
				}		
			}	
		}
		return ringFragmentSmiles;
	}
	
	/**
	 * @return String[] smiles of the linker fragments
	 */
	public String[] getLinkerFragmentsAsSmileArray(){
		SmilesGenerator sg;
		String[] linkerFragmentSmiles={};
		if (this.linkerFragments !=null){
			linkerFragmentSmiles=new String[this.linkerFragments.size()];
			//logger.debug("SIZE OF MURCKO VECTOR:"+this.ringFragments.size());
			//logger.debug("SIZE OF SMILES[]:"+ringFragmentSmiles.length);
			for (int i =0;i<this.linkerFragments.size();i++){
				try{
					IMolecule mol=(IMolecule)this.linkerFragments.get(i);
					sg = new SmilesGenerator();
					if (smilesToUpperCase){
						linkerFragmentSmiles[i]=sg.createSMILES(mol).toUpperCase();
					}else{
						linkerFragmentSmiles[i]=sg.createSMILES(mol);
					}
				} catch (Exception e){
					logger.error("ERROR in smile generation due to:"+e.toString());
				}		
			}	
		}
		return linkerFragmentSmiles;
	}
	
	
	private void resetFlags(IMolecule molecule){
		for (int i=0;i<molecule.getAtomCount();i++){
			molecule.getAtom(i).setFlag(CDKConstants.VISITED, false);
		}
	}
	
	/**
	 * @return Vector murckoFragments
	 */
	public List getMurckoFrameworks() {
		return this.murckoFragments;
	}
	
	/**
	 * @return Vector ringFragments
	 */
	public List getRingFragments() {
		return this.ringFragments;
	}
	
	/**
	 * @return Vector linkerFragments
	 */
	public List getLinkerFragments() {
		return this.linkerFragments;
	}

	public boolean isSmilesToUpperCase() {
		return smilesToUpperCase;
	}

	public void setSmilesToUpperCase(boolean smilesToUpperCase) {
		this.smilesToUpperCase = smilesToUpperCase;
	}
}
