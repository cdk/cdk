/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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


import java.util.Vector;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
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
  
 * @author      chhoppe from EUROSCREEN
 * @cdk.created     2006-3-23
 * @cdk.module experimental
 * 
 **/
public class GenerateFragments {
	
	private Vector murckoFragments =new Vector();
	private Vector ringFragments=new Vector();
	private Vector linkerFragments=new Vector();
	private IRingSet ringSetsMolecule=null;
	private boolean sidechainHetatoms=true;
	private boolean exocyclicDoubleBonds=true;
	private IChemObjectBuilder builder;
	
	public GenerateFragments() {
    }
	
	/**
	 * generates ring fragments from SSSR and RingPartitioner method
	 * @param molecule
	 * void
	 */
	public void generateRingFragments(IMolecule molecule){
		this.ringSetsMolecule = new SSSRFinder(molecule).findSSSR();
		if (this.ringSetsMolecule.size() > 0) {
			this.ringFragments=RingPartitioner.partitionRings(ringSetsMolecule);
		}
	}
	
	
	/**
	 * generates Murcko fragments takes two parameters
	 * @param molecule	the molecule
	 * @param sidechainHetatoms	boolean if sidchain hetero atoms should be included (true)
	 * return	void
	 */
	public void generateMurckoFragments(IMolecule molecule, boolean sidechainHetatoms,boolean exocyclicDoubleBonds){
		//System.out.println("****** generatemurckoFragments *******");
		//VARIABLES
		this.sidechainHetatoms=sidechainHetatoms;
		this.exocyclicDoubleBonds=exocyclicDoubleBonds;
		
		generateRingFragments(molecule);
		IAtom firstRingAtom = null;	
		IAtom secondRingAtom = null;	
		IAtomContainer firstRingAtomContainer=null;
		IAtomContainer secondRingAtomContainer=null;
		IAtomContainer firstRingSubstituents=null;
		IAtomContainer secondRingSubstituents=null;
		IAtomContainer path=null;
		IMolecule murckoFragment=null;
		IMolecule linkerFragment=null;
		
		
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			if (this.ringSetsMolecule.contains(molecule.getAtomAt(i))) {
				molecule.getAtomAt(i).setFlag(CDKConstants.ISINRING, true);
			}else{
				molecule.getAtomAt(i).setFlag(CDKConstants.ISINRING, false);
			}
		}
		
//		START
		if (this.ringFragments.size() > 1) {
			//go through all ringsystems
			//System.out.println("Number of RingSystems:"+this.ringFragments.size());
			for (int f = 0; f < this.ringFragments.size()-1; f++) {
				firstRingAtomContainer = RingSetManipulator.getAllInOneContainer((IRingSet) this.ringFragments.get(f));
				for (int g = f+1; g < this.ringFragments.size(); g++) {
					secondRingAtomContainer = RingSetManipulator.getAllInOneContainer((IRingSet) this.ringFragments.get(g));
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
									murckoFragment=addFragments(firstRingAtomContainer,murckoFragment);
									murckoFragment=addFragments(secondRingAtomContainer,murckoFragment);
									murckoFragment=addFragments(firstRingAtom,firstRingSubstituents.getAtomAt(i),murckoFragment,molecule);
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
														int firstAtomNumber=path.getAtomNumber(firstRingSubstituents.getAtomAt(i));
														path.addBond(firstAtomNumber,0,molecule.getBond(firstRingSubstituents.getAtomAt(i),path.getAtomAt(0)).getOrder());
													}												
													//1. add path
													//2. add rings  
													//3. connect ring atoms to path
													murckoFragment=addPathFragments(path,murckoFragment,molecule);
													linkerFragment=new Molecule(murckoFragment);
													murckoFragment=addFragments(firstRingAtomContainer,murckoFragment);
													murckoFragment=addFragments(secondRingAtomContainer,murckoFragment);
													murckoFragment=addFragments(firstRingAtom,firstRingSubstituents.getAtomAt(i),murckoFragment,molecule);
													murckoFragment=addFragments(secondRingAtom,secondRingSubstituents.getAtomAt(k),murckoFragment,molecule);
																																						
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
		}else if (this.ringFragments.size() ==1){
			murckoFragment=new Molecule();
			murckoFragment=addFragments(RingSetManipulator.getAllInOneContainer((IRingSet) this.ringFragments.get(0)),murckoFragment);
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
		int firstAtomNumber=0;
		int secondAtomNumber=0;
		
		//1. check if linker atom is member of a ring system
		//2. check if heteroatoms bonded to a non ring linker atom should be included
		//3. check if exocyclic double or triple bonded atoms to linker schould be included
		for (int i=0;i<addAtomContainer.getAtomCount();i++){
			
			if (addAtomContainer.getAtomAt(i).getFlag(CDKConstants.ISINRING)&& !targetMolecule.contains(addAtomContainer.getAtomAt(i))){
				//Find all Ring atoms and add them 
				for (int j = 0; j < this.ringFragments.size(); j++) {
					ringAtomContainer = RingSetManipulator.getAllInOneContainer((IRingSet) this.ringFragments.get(j));
					if (ringAtomContainer.contains(addAtomContainer.getAtomAt(i))){
						targetMolecule=addFragments(ringAtomContainer, targetMolecule);
						break;
					}
				}
			}else if((this.sidechainHetatoms || this.exocyclicDoubleBonds) && !addAtomContainer.getAtomAt(i).getFlag(CDKConstants.ISINRING) && !targetMolecule.contains(addAtomContainer.getAtomAt(i))){
				atoms=mainMolecule.getConnectedAtoms(addAtomContainer.getAtomAt(i));
				targetMolecule.addAtom(addAtomContainer.getAtomAt(i));	
				for (int j = 0; j < atoms.length; j++) {
					//System.out.println("HETATOM:"+atoms[j].getSymbol());
					if (this.sidechainHetatoms && !(atoms[j].getSymbol()).equals("C") && !(atoms[j].getSymbol()).equals("H") && !targetMolecule.contains(atoms[j])){
						//System.out.println("HETATOM TRUE");
						targetMolecule.addAtom(atoms[j]);
						firstAtomNumber=targetMolecule.getAtomNumber(addAtomContainer.getAtomAt(i));
						secondAtomNumber=targetMolecule.getAtomNumber(atoms[j]);
						targetMolecule.addBond(firstAtomNumber,secondAtomNumber,mainMolecule.getBond(addAtomContainer.getAtomAt(i),atoms[j]).getOrder());
					}
					if (this.sidechainHetatoms && mainMolecule.getBond(atoms[j],addAtomContainer.getAtomAt(i)).getOrder()>1 && !targetMolecule.contains(atoms[j])){
						targetMolecule.addAtom(atoms[j]);
						firstAtomNumber=targetMolecule.getAtomNumber(addAtomContainer.getAtomAt(i));
						secondAtomNumber=targetMolecule.getAtomNumber(atoms[j]);
						targetMolecule.addBond(firstAtomNumber,secondAtomNumber,mainMolecule.getBond(addAtomContainer.getAtomAt(i),atoms[j]).getOrder());
					}
					
				}
			}else{
				targetMolecule.addAtom(addAtomContainer.getAtomAt(i));	
			}
		}
			
		for (int i=0;i<addAtomContainer.getBondCount();i++){
			if (!targetMolecule.contains(addAtomContainer.getBondAt(i))){
				targetMolecule.addBond(addAtomContainer.getBondAt(i));
			}
		}
		
		return targetMolecule;
	}
	
	
	/**
	 * connect ring systems to the path
	 * @param firstAtom			should be a ring atom
	 * @param secondAtom		is the first atom in the path
	 * @param targetMolecule	murcko fragment storage
	 * @param mainMolecule		original molecule
	 * @return 	IMolecule		murcko fragment
	 */
	private IMolecule addFragments(IAtom firstAtom, IAtom secondAtom,IMolecule targetMolecule, IMolecule mainMolecule){
		//System.out.println("--->connect ring systems to the path");
		
		int firstAtomNumber=targetMolecule.getAtomNumber(firstAtom);
		int secondAtomNumber=targetMolecule.getAtomNumber(secondAtom);
		
		targetMolecule.addBond(firstAtomNumber,secondAtomNumber,mainMolecule.getBond(firstAtom,secondAtom).getOrder());
			
		return targetMolecule;
	}
	
	
	/**
	 * add the rings to the murcko fragment
	 * @param addAtomContainer	IAtomContainer with the ring atoms
	 * @param targetMolecule	murcko fragment
	 * @return	IMolecule		murcko fragment
	 */
	private IMolecule addFragments(IAtomContainer addAtomContainer, IMolecule targetMolecule){
		for (int i=0;i<addAtomContainer.getAtomCount();i++){
			targetMolecule.addAtom(addAtomContainer.getAtomAt(i));					
		}
		for (int i=0;i<addAtomContainer.getBondCount();i++){
			targetMolecule.addBond(addAtomContainer.getBondAt(i));					
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
	 * Method checks if a ring atom has substituents (can be an other ring system)
	 * @param ringAtom		IAtom the ring atom
	 * @param molecule	 	IMolecule original molecule
	 * @param ringSystem	IAtomContainer the ring system
	 * @return
	 * boolean
	 */
	private boolean hasSubstituent(IAtom ringAtom,IMolecule molecule, IAtomContainer ringSystem){
		IAtom[] atoms = molecule.getConnectedAtoms(ringAtom);
		for (int i = 0; i<atoms.length;i++){
			if (!ringSystem.contains(atoms[i])&& !atoms[i].getSymbol().equals("H")){
				return true;
			}
		}
		return false;
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
	public Vector getMurckoFrameworks() {
		return this.murckoFragments;
	}
	
	/**
	 * @return Vector ringFragments
	 */
	public Vector getRingFragments() {
		return this.ringFragments;
	}
	
	/**
	 * @return Vector linkerFragments
	 */
	public Vector getLinkerFragments() {
		return this.linkerFragments;
	}
}
