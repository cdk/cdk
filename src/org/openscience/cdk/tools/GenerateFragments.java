/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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


import java.util.List;
import java.util.Vector;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

/**
 * Generate fragments
 * - ring fragments (largest ringsystems)
 * - Murcko fragments described by Murcko et al. {@cdk.cite MURCKO96}.
 *
 * Due to some problems with SaturationChecker the SMILES generation might be a problem.
 * When you want to use the get..SmileArray methods please do (that seems to work, refer test 13+14):
 * 
 * HydrogenAdder ha= new HydrogenAdder();
 * ha.addExplicitHydrogensToSatisfyValency(molecule);
 * GenerateFragments gf=new GenerateFragments();
 * gf.generateMurckoFragments(molecule,booelan,booelan);
 * String[] smiles=gf.getMurckoFrameworksAsSmileArray();
 *
 * @author      chhoppe from EUROSCREEN
 * @cdk.created     2006-3-23
 * @cdk.module experimental
 * 
 **/
public class GenerateFragments {
	
	private List murckoFragments =null;
	private List ringFragments=null;
	private List linkerFragments=null;
	private IRingSet ringSetsMolecule=null;
	private boolean sidechainHetatoms=true;
	private boolean exocyclicDoubleBonds=true;
	//private IChemObjectBuilder builder;
	
	public GenerateFragments() {
    }
	
	/**
	 * generates ring fragments from SSSR and RingPartitioner method
	 * @param molecule
	 * void
	 */
	public void generateRingFragments(IMolecule molecule){
		this.ringFragments=new Vector();
		this.ringSetsMolecule = new SSSRFinder(molecule).findSSSR();
		if (this.ringSetsMolecule.getAtomContainerCount() > 0) {
			this.ringFragments=RingPartitioner.partitionRings(ringSetsMolecule);
		}
	}
	
	
	/**
	 * generates Murcko fragments takes two parameters
	 * @param molecule	the molecule
	 * @param sidechainHetatoms	boolean if sidchain hetero atoms should be included (true)
	 * @param exocyclicDoubleBonds boolean if bonds with order >1 should be included
	 * @param minimumRingSize int indicates the minimum ring size as to considered as fragment (ringSize<minimimRingSize)
	 * return	void
	 */
	public void generateMurckoFragments(IMolecule molecule, boolean sidechainHetatoms,boolean exocyclicDoubleBonds, int minimumRingSize) throws org.openscience.cdk.exception.CDKException{
		//System.out.println("****** generatemurckoFragments *******");
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
			molecule.getAtomAt(i).setFlag(CDKConstants.ISINRING, false);
		}
				
		//System.out.println("Number of RingSystems:"+this.ringFragments.size());
		for (int f = 0; f < this.ringFragments.size(); f++) {
			firstRingAtomContainer = RingSetManipulator.getAllInOneContainer((IRingSet) this.ringFragments.get(f));
			
			if (firstRingAtomContainer.getAtomCount()>=minimumRingSize){
				tmpRingFragments.add(firstRingAtomContainer);
				for (int g = 0; g < firstRingAtomContainer.getAtomCount(); g++) {
					molecule.getAtomAt(molecule.getAtomNumber(firstRingAtomContainer.getAtomAt(g))).setFlag(CDKConstants.ISINRING, true);
				}
				
			}
		}

//		START
		//System.out.println("Number of RingSystems:"+tmpRingFragments.size());
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
						firstRingAtom=firstRingAtomContainer.getAtomAt(h);
						firstRingSubstituents=getPossibleLinkerSubstituents(firstRingAtom,molecule,firstRingAtomContainer);
						if (firstRingSubstituents.getAtomCount()>0){
							//go through substituents of first ring
							for (int i = 0; i < firstRingSubstituents.getAtomCount(); i++){
//								System.out.println("First Ring Sub is in RING");
//								//check for ring-ring system
								if (firstRingSubstituents.getAtomAt(i).getFlag(CDKConstants.ISINRING) && secondRingAtomContainer.contains(firstRingSubstituents.getAtomAt(i))){
									//System.out.println("\tFound a ring-ring System");
									murckoFragment=new Molecule();
									murckoFragment=addFragments(firstRingAtomContainer,murckoFragment,molecule);
									murckoFragment=addFragments(secondRingAtomContainer,murckoFragment,molecule);
									murckoFragment=addFragmentBonds(murckoFragment,molecule);
									
									this.murckoFragments.add(murckoFragment);
									//System.out.println("MFragment:"+murckoFragment.getAtomCount()+" CC:"+ConnectivityChecker.isConnected(murckoFragment));
									//System.out.println(murckoFragment.toString());
									//System.out.println("\tADD MURCKOFRAGMENT");
									break;
								}
//								compare to substituents of second ring
								for (int j = 0; j < secondRingAtomContainer.getAtomCount(); j++){
									secondRingAtom=secondRingAtomContainer.getAtomAt(j);
									secondRingSubstituents=getPossibleLinkerSubstituents(secondRingAtom,molecule,secondRingAtomContainer);
									if (secondRingSubstituents.getAtomCount()>0){
										//go through substituents of second ring
										for (int k = 0; k < secondRingSubstituents.getAtomCount(); k++){//For-k
											
											//System.out.println("First Ring Size:"+firstRingAtomContainer.getAtomCount()+" 2.Ring Size:"+secondRingAtomContainer.getAtomCount());
											//System.out.println(f+".ringSub:"+molecule.getAtomNumber(firstRingSubstituents.getAtomAt(i))+" Sym:"+firstRingSubstituents.getAtomAt(i).getSymbol()+" "+g+".ringSub:"+molecule.getAtomNumber(secondRingSubstituents.getAtomAt(k)));
											path=new AtomContainer();
											try {
												resetFlags(molecule);
												PathTools.depthFirstTargetSearch(molecule,firstRingSubstituents.getAtomAt(i),secondRingSubstituents.getAtomAt(k),path);
												/*System.out.print("\tPATHSIZE:"+path.getAtomCount());
												System.out.print("\tFIRST PATHATOM:"+molecule.getAtomNumber(path.getAtomAt(0)));
												try{
													System.out.println("\tLAST PATHATOM:"+molecule.getAtomNumber(path.getAtomAt(path.getAtomCount()-1)));
												}catch(Exception eS){
													System.out.println("\tNO LAST PATHATOM");
												}*/
												
												if (firstRingSubstituents.getAtomAt(i)==secondRingSubstituents.getAtomAt(k)){
													//System.out.println("\tSubstituents are equal");
													path.addAtom(firstRingSubstituents.getAtomAt(i));
												}
												
												//Check Path, direct connection between the substituents ->linker
												if (checkPath(firstRingAtom, secondRingAtom, path) && path.getAtomCount()>0){
													murckoFragment=new Molecule();
													
													//add root atom to path
													if (!path.contains(firstRingSubstituents.getAtomAt(i))){
														path.addAtom(firstRingSubstituents.getAtomAt(i));
													}												
													//1. add path
													//2. add rings  
													//3. connect ring atoms to path
													murckoFragment=addPathFragments(path,murckoFragment,molecule);
													murckoFragment=addFragments(firstRingAtomContainer,murckoFragment,molecule);
													murckoFragment=addFragments(secondRingAtomContainer,murckoFragment,molecule);
													
													murckoFragment=addFragmentBonds(murckoFragment,molecule);
													linkerFragment=new Molecule(murckoFragment);
																																					
													this.linkerFragments.add(linkerFragment);
													this.murckoFragments.add(murckoFragment);
													//System.out.println("\tADD MURCKOFRAGMENT");
												}else{
													//System.out.println("\tEND PATH");
												}
												
												
											} catch (NoSuchAtomException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}//catch
										}//For-k
									}//if 2.ring sub
								}//For-j
							}//For-i
						}//if 1.ring sub
					}//For-h
				}//For-g				
			}//For-f				
		}else if (tmpRingFragments.size() ==1){
			//System.out.println("Number of RingSystems is 1");
			murckoFragment=new Molecule();
			murckoFragment=addFragments(RingSetManipulator.getAllInOneContainer((IRingSet) this.ringFragments.get(0)),murckoFragment,molecule);
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
		IAtom[] atoms=null;
		
		//1. check if linker atom is member of a ring system
		//2. check if heteroatoms bonded to a non ring linker atom should be included
		//3. check if exocyclic double or triple bonded atoms to linker schould be included
		for (int i=0;i<addAtomContainer.getAtomCount();i++){
			
			if (addAtomContainer.getAtomAt(i).getFlag(CDKConstants.ISINRING)&& !targetMolecule.contains(addAtomContainer.getAtomAt(i))){
				//Find all Ring atoms and add them 
				for (int j = 0; j < this.ringFragments.size(); j++) {
					ringAtomContainer = RingSetManipulator.getAllInOneContainer((IRingSet) this.ringFragments.get(j));
					if (ringAtomContainer.contains(addAtomContainer.getAtomAt(i))){
						targetMolecule=addFragments(ringAtomContainer, targetMolecule,mainMolecule);
						break;
					}
				}
			}else if((this.sidechainHetatoms || this.exocyclicDoubleBonds) && !targetMolecule.contains(addAtomContainer.getAtomAt(i))){
				atoms=mainMolecule.getConnectedAtoms(addAtomContainer.getAtomAt(i));
				targetMolecule.addAtom(addAtomContainer.getAtomAt(i));	
				for (int j = 0; j < atoms.length; j++) {
					//System.out.println("HETATOM:"+atoms[j].getSymbol());
					if (this.sidechainHetatoms && !addAtomContainer.getAtomAt(i).getFlag(CDKConstants.ISINRING) && !(atoms[j].getSymbol()).equals("C") && !(atoms[j].getSymbol()).equals("H") && !targetMolecule.contains(atoms[j])){
						//System.out.println("HETATOM TRUE");
						targetMolecule.addAtom(atoms[j]);
					}
					if (this.exocyclicDoubleBonds && mainMolecule.getBond(atoms[j],addAtomContainer.getAtomAt(i)).getOrder()>1 && !targetMolecule.contains(atoms[j])){
						//System.out.println("EXOCYCLIC DB TRUE");
						targetMolecule.addAtom(atoms[j]);
					}	
				}
			}else{
				if (!targetMolecule.contains(addAtomContainer.getAtomAt(i))){
					targetMolecule.addAtom(addAtomContainer.getAtomAt(i));
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
				
				if (mainMolecule.getBond(targetMolecule.getAtomAt(i),targetMolecule.getAtomAt(j)) !=null){
					firstAtomNumber=targetMolecule.getAtomNumber(targetMolecule.getAtomAt(i));
					secondAtomNumber=targetMolecule.getAtomNumber(targetMolecule.getAtomAt(j));
					targetMolecule.addBond(firstAtomNumber,secondAtomNumber,mainMolecule.getBond(targetMolecule.getAtomAt(i),targetMolecule.getAtomAt(j)).getOrder());
					if (mainMolecule.getBond(targetMolecule.getAtomAt(i),targetMolecule.getAtomAt(j)).getFlag(CDKConstants.ISAROMATIC) == true){
						targetMolecule.getBond(targetMolecule.getAtomAt(firstAtomNumber),targetMolecule.getAtomAt(secondAtomNumber)).setFlag(CDKConstants.ISAROMATIC, true);
					}
				}
			}
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
		IAtom[] atoms=null;
		for (int i=0;i<addAtomContainer.getAtomCount();i++){
			targetMolecule.addAtom(addAtomContainer.getAtomAt(i));
			targetMolecule.addAtom(addAtomContainer.getAtomAt(i));	
			//Check for double bonds
			atoms=mainMolecule.getConnectedAtoms(addAtomContainer.getAtomAt(i));
			for (int j = 0; j < atoms.length; j++) {
				if (this.exocyclicDoubleBonds && mainMolecule.getBond(atoms[j],addAtomContainer.getAtomAt(i)).getOrder()>1 && !targetMolecule.contains(atoms[j])){
					targetMolecule.addAtom(atoms[j]);
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
		//System.out.println("CHECK PATH");
		if (path.contains(firstRingAtom) || path.contains(secondRingAtom)){
			return false;
		}
		return true;
	}
	
	/**
	 * get starting points (IAtom) of possible linkers  
	 * @param ringAtom		IAtom the ring atom
	 * @param molecule		IMolecule original molecule
	 * @param ringSystem	IAtomContainer the ring system
	 * @return	IAtomContainer possible starting points of linkers
	 */
	private IAtomContainer getPossibleLinkerSubstituents(IAtom ringAtom,IMolecule molecule, IAtomContainer ringSystem){
		IAtom[] atoms = molecule.getConnectedAtoms(ringAtom);
		IAtomContainer substituents=new AtomContainer();
		for (int i = 0; i<atoms.length;i++){
			if (!ringSystem.contains(atoms[i])&& !atoms[i].getSymbol().equals("H")){
				substituents.addAtom(atoms[i]);
			}
		}
		return substituents;
	}

	
	/**
	 * @return String[] smiles of the murcko fragments
	 */
	public String[] getMurckoFrameworksAsSmileArray(){
		SmilesGenerator sg =null;
		String[] murckoFragmentsmiles={};
		if (this.murckoFragments !=null){
			murckoFragmentsmiles=new String[this.murckoFragments.size()];
			//System.out.println("SIZE OF MURCKO VECTOR:"+this.murckoFragments.size());
			//System.out.println("SIZE OF SMILES[]:"+murckoFragmentsmiles.length);
			for (int i =0;i<this.murckoFragments.size();i++){
				try{
					IMolecule mol=(IMolecule)this.murckoFragments.get(i);
					if (ConnectivityChecker.isConnected(mol)){
						sg = new SmilesGenerator(mol.getBuilder());
						murckoFragmentsmiles[i]=sg.createSMILES(mol);
					}else{
						System.out.println("ERROR in getMurckoFrameworksAsSmileArray due to:Molecule is not connected");
					}
				} catch (Exception e){
					System.out.println("ERROR in getMurckoFrameworksAsSmileArray due to:"+e.toString());
					//e.printStackTrace();
				}		
			}	
		}
		return murckoFragmentsmiles;
	}
	
	/**
	 * @return String[] smiles of the ring fragments NOT WORKING
	 */
	public String[] getRingFragmentsAsSmileArray(){
		SmilesGenerator sg =null;
		String[] ringFragmentSmiles={};
		if (this.ringFragments !=null){
			ringFragmentSmiles=new String[this.ringFragments.size()];
			//System.out.println("SIZE OF MURCKO VECTOR:"+this.ringFragments.size());
			//System.out.println("SIZE OF SMILES[]:"+ringFragmentSmiles.length);
			for (int i =0;i<this.ringFragments.size();i++){
				try{
					IMolecule mol=(IMolecule)this.ringFragments.get(i);
					sg = new SmilesGenerator(mol.getBuilder());
					ringFragmentSmiles[i]=sg.createSMILES(mol);
				} catch (Exception e){
					System.out.println("ERROR in smile generation due to:"+e.toString());			
				}		
			}	
		}
		return ringFragmentSmiles;
	}
	
	/**
	 * @return String[] smiles of the linker fragments
	 */
	public String[] getLinkerFragmentsAsSmileArray(){
		SmilesGenerator sg =null;
		String[] linkerFragmentSmiles={};
		if (this.linkerFragments !=null){
			linkerFragmentSmiles=new String[this.linkerFragments.size()];
			//System.out.println("SIZE OF MURCKO VECTOR:"+this.ringFragments.size());
			//System.out.println("SIZE OF SMILES[]:"+ringFragmentSmiles.length);
			for (int i =0;i<this.linkerFragments.size();i++){
				try{
					IMolecule mol=(IMolecule)this.linkerFragments.get(i);
					sg = new SmilesGenerator(mol.getBuilder());
					linkerFragmentSmiles[i]=sg.createSMILES(mol);
				} catch (Exception e){
					System.out.println("ERROR in smile generation due to:"+e.toString());
				}		
			}	
		}
		return linkerFragmentSmiles;
	}
	
	
	private void resetFlags(IMolecule molecule){
		for (int i=0;i<molecule.getAtomCount();i++){
			molecule.getAtomAt(i).setFlag(CDKConstants.VISITED, false);
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
}
