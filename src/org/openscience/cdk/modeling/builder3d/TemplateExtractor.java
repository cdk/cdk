/*
 *  Copyright (C) 2004 Christian Hoppe
 *
 *  Contact: c.hoppe_@web.de
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 *
 *  MExtractRingSystemsFromFile
 *
 *  Created on August 18, 2004, 9:50 PM
 */
package org.openscience.cdk.modeling.builder3d;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.applications.FingerPrinter;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;


public class TemplateExtractor{
	
	public TemplateExtractor(){}
	
	public void cleanDataSet(String dataFile){
		IteratingMDLReader imdl = null;
		IMoleculeSet som = new MoleculeSet();
		try{	
			System.out.println("Start clean dataset...");
			BufferedReader fin=new BufferedReader(new FileReader(dataFile));
			imdl=new IteratingMDLReader(fin, NoNotificationChemObjectBuilder.getInstance());
			System.out.print("Read File in..");
		}catch (Exception exc){
			System.out.println("Could not read Molecules from file "+dataFile+" due to: "+exc.getMessage());
		}
		System.out.println("READY");
		int c=0;
		while (imdl.hasNext()){
			c++;
			if (c%1000==0){
				System.out.println("...");
			}
			IMolecule m = new Molecule();
			m=(IMolecule)imdl.next();
			if (m.getAtomCount()>2){
				if (m.getAtom(0).getPoint3d() != null){
				som.addMolecule(m);}
			}
		}
		try{	
			imdl.close();
		}catch (Exception exc1){
			System.out.println("Could not close Reader due to: "+exc1.getMessage());
		}
		System.out.println(som.getMoleculeCount()+" Templates are read in");
		writeChemModel(som,dataFile,"_CLEAN");
	}
	
	public void ReadNCISdfFileAsTemplate(String dataFile){
		IteratingMDLReader imdl=null;
		IMoleculeSet som=new org.openscience.cdk.MoleculeSet();
		try{	
			System.out.println("Start...");
			BufferedReader fin=new BufferedReader(new FileReader(dataFile));
			imdl=new IteratingMDLReader(fin, NoNotificationChemObjectBuilder.getInstance());
			System.out.print("Read File in..");
		}catch (Exception exc){
			System.out.println("Could not read Molecules from file "+dataFile+" due to: "+exc.getMessage());
		}
		System.out.println("READY");
		while (imdl.hasNext()){
			som.addMolecule((Molecule) imdl.next());
		}
		try{	
			imdl.close();
		}catch (Exception exc1){
			System.out.println("Could not close Reader due to: "+exc1.getMessage());
		}
		System.out.println(som.getMoleculeCount()+" Templates are read in");
	}
	
	public void PartitionRingsFromComplexRing(String dataFile){
		IteratingMDLReader imdl=null;
		IMoleculeSet som=new org.openscience.cdk.MoleculeSet();
		Molecule m=null;
		try{	
			System.out.println("Start...");
			BufferedReader fin=new BufferedReader(new FileReader(dataFile));
			imdl=new IteratingMDLReader(fin, NoNotificationChemObjectBuilder.getInstance());
			System.out.print("Read File in..");
		}catch (Exception exc){
			System.out.println("Could not read Molecules from file "+dataFile+" due to: "+exc.getMessage());
		}
		System.out.println("READY");
		while (imdl.hasNext()){
			m = (Molecule) imdl.next();
			System.out.println("Atoms:"+m.getAtomCount());
			IRingSet ringSetM = new SSSRFinder(m).findSSSR();
			//som.addMolecule(m);
			for (int i = 0; i < ringSetM.getAtomContainerCount(); i++){
				som.addMolecule(new org.openscience.cdk.Molecule((AtomContainer)ringSetM.getAtomContainer(i)));
			}
		}
		try{	
			imdl.close();
		}catch (Exception exc1){
			System.out.println("Could not close Reader due to: "+exc1.getMessage());
		}
		System.out.println(som.getMoleculeCount()+" Templates are read in");
		writeChemModel(som,dataFile,"_VERSUCH");
	}
	
	
	public void extractUniqueRingSystemsFromFile(String dataFile){
		System.out.println("****** EXTRACT UNIQUE RING SYSTEMS ******");
		System.out.println("From file:"+dataFile);
		Molecule m=null;
		//RingPartitioner ringPartitioner=new RingPartitioner();
		List ringSystems = null;
		IteratingMDLReader imdl = null;
		
		HashMap HashRingSystems=new HashMap();
		SmilesGenerator smilesGenerator=new SmilesGenerator();
		
		int counterRings=0;
		int counterMolecules=0;
		int counterUniqueRings=0;
		RingSet ringSet=null;
		String key="";
		AtomContainer ac=null;
		
		String molfile=dataFile+"_UniqueRings";
		
		//FileOutputStream fout=null;
		MDLWriter mdlw=null;
		try{
			FileOutputStream fout= new FileOutputStream(molfile);
			mdlw= new MDLWriter(fout);
		}catch(Exception ex2){
			System.out.println("IOError:cannot write file due to:"+ex2.toString());
		}
		
		try{	
			System.out.println("Start...");
			BufferedReader fin=new BufferedReader(new FileReader(dataFile));
			imdl=new IteratingMDLReader(fin, NoNotificationChemObjectBuilder.getInstance());
			System.out.println("Read File in..");
		}catch (Exception exc){
			System.out.println("Could not read Molecules from file "+dataFile+" due to: "+exc.getMessage());
		}
		while (imdl.hasNext()){
			m=(Molecule) imdl.next();
			counterMolecules=counterMolecules+1;
		    /*try{
			       HueckelAromaticityDetector.detectAromaticity(m);
			}catch(Exception ex1){
			        System.out.println("Could not find aromaticity due to:"+ex1);
			}*/
			IRingSet ringSetM = new SSSRFinder(m).findSSSR();
			
			if (counterMolecules%1000==0){
			  System.out.println("Molecules:"+counterMolecules);
			}
			
			if (ringSetM.getAtomContainerCount() > 0){
				ringSystems=RingPartitioner.partitionRings(ringSetM);
								
				for (int i=0; i <ringSystems.size(); i++){
					ringSet = (RingSet)ringSystems.get(i);
					ac=new org.openscience.cdk.AtomContainer(RingSetManipulator.getAllInOneContainer(ringSet));
					counterRings=counterRings+1;
					//Only connection is important
					for (int j=0;j<ac.getAtomCount();j++){
						(ac.getAtom(j)).setSymbol("C");
					}
					
					key=smilesGenerator.createSMILES(new org.openscience.cdk.Molecule(ac));
					//System.out.println("OrgKey:"+key+" For Molecule:"+counter);
					if (HashRingSystems.containsKey(key)){
						//System.out.println("HAS KEY:ADD");
						//Vector tmp=(Vector)HashRingSystems.get(key);
						//tmp.add((AtomContainer)ringSet.getRingSetInAtomContainer());
						//HashRingSystems.put(key,tmp);
						//int tmp=((Integer)HashRingSystems.get(key)).intValue();
						//tmp=tmp+1;
						//HashRingSystems.put(key,new Integer(tmp));
					}else{
						counterUniqueRings=counterUniqueRings+1;
						//Vector rings2=new Vector();
						//rings2.add((AtomContainer)RingSetManipulator.getAllInOneContainer(ringSet));
						HashRingSystems.put(key,new String("1"));
						try{
							//mdlw.write(new Molecule ((AtomContainer)RingSetManipulator.getAllInOneContainer(ringSet)));
							mdlw.write(new org.openscience.cdk.Molecule (ac));
						}catch(Exception emdl){
						}
			
						
					}
				}
				
			}
		}	
		try{
			imdl.close();
			mdlw.close();
		}catch (Exception exc1){
			System.out.println("Could not close iterator mdl reader due to: "+exc1.getMessage());
		}
		//System.out.println("READY Molecules:"+counterMolecules);
		System.out.println("READY Molecules:"+counterMolecules+" RingSystems:"+counterRings +" UniqueRingsSystem:"+counterUniqueRings);
		System.out.println("HashtableKeys:"+HashRingSystems.size());
		


		/*int c=0;
		Set keyset = HashRingSystems.keySet();
		Iterator it=keyset.iterator();
		IMoleculeSet som=new MoleculeSet();
		SmilesParser smileParser=new SmilesParser();
		String ringSmile="";
		while (it.hasNext()) {
			key=(String)it.next();
			ringSmile=(String)HashRingSystems.get(key);
			System.out.println("HashtableSmile:"+ringSmile+" key:"+key);
			try{
				som.addMolecule(smileParser.parseSmiles(ringSmile));
			}catch (Exception ex5){
				System.out.println("Error in som.addmolecule due to:"+ex5);
			}
		}*/
			
		//writeChemModel(som,dataFile,"_TESTTESTTESTTESTTEST");
	}
	
	
	
	public void writeChemModel(IMoleculeSet som,String file, String endFix){
		System.out.println("WRITE Molecules:"+som.getMoleculeCount());
		String molfile=file+endFix;
		try{
			FileOutputStream fout=new FileOutputStream(molfile);
			MDLWriter mdlw=new MDLWriter(fout);
			mdlw.write(som);
			mdlw.close();
		}catch(Exception ex2){
			System.out.println("IOError:cannot write file due to:"+ex2.toString());
		}
	}
	
	
	public void makeCanonicalSmileFromRingSystems(String dataFileIn, String dataFileOut){
		System.out.println("Start make SMILES...");
		Molecule m=null;
		IteratingMDLReader imdl=null;
		//QueryAtomContainer query=null;
		List data = new ArrayList();
		SmilesGenerator smiles=new SmilesGenerator();
		try{	
			System.out.println("Start...");
			BufferedReader fin=new BufferedReader(new FileReader(dataFileIn));
			imdl=new IteratingMDLReader(fin, NoNotificationChemObjectBuilder.getInstance());
			//fin.close();
			System.out.println("Read File in..");
		}catch (Exception exc){
			System.out.println("Could not read Molecules from file "+dataFileIn+" due to: "+exc.getMessage());
		}
		while (imdl.hasNext()){
			m=(Molecule) imdl.next();
			/*try{
				HueckelAromaticityDetector.detectAromaticity(m);
			}catch(Exception ex1){
				System.out.println("Could not find aromaticity due to:"+ex1);
			}*/
			//query=QueryAtomContainerCreator.createAnyAtomContainer(m,true);
			//System.out.println("String:"+smiles.createSMILES(new Molecule(m)));
			try{
				
				data.add((String)smiles.createSMILES(new org.openscience.cdk.Molecule(m)));
			}catch(Exception exc1){
			System.out.println("Could not create smile due to: "+exc1.getMessage());
			}
		}
		try{
			imdl.close();
		}catch (Exception exc2){
		}
		
		
		System.out.print("...ready\nWrite data...");
		BufferedWriter fout=null;
		try{	
			fout=new BufferedWriter(new FileWriter(dataFileOut));
		}catch(Exception exc3){
			System.out.println("Could not write smile in file "+dataFileOut+" due to: "+exc3.getMessage());
		}
		for (int i=0;i<data.size();i++){
		    //System.out.println("write:"+(String)data.get(i));
			try{
				
				fout.write(((String)data.get(i)));
				fout.newLine();
			}catch(Exception exc4){
			}
		}
		System.out.println("number of smiles:"+data.size());
		System.out.println("...ready");
		try{
			fout.close();
		}catch (Exception exc5){
		}
	}
	
    public void makeFingerprintFromRingSystems(String dataFileIn, String dataFileOut, boolean anyAtom, boolean anyAtomAnyBond) throws Exception
    {
		System.out.println("Start make fingerprint from file:"+dataFileIn+" ...");
		//AllRingsFinder allRingsFinder=new AllRingsFinder();
		//allRingsFinder.setTimeout(-1);
		Fingerprinter fingerPrinter = new Fingerprinter();
		IMolecule m = null;
		IteratingMDLReader imdl=null;
		//QueryAtomContainer query=null;
		IAtomContainer query = null;
		List data = new ArrayList();
		try{	
			System.out.print("Read data file in ...");
			BufferedReader fin=new BufferedReader(new FileReader(dataFileIn));
			imdl=new IteratingMDLReader(fin, NoNotificationChemObjectBuilder.getInstance());
			//fin.close();
			System.out.println("ready");
		}catch (Exception exc){
			System.out.println("Could not read Molecules from file "+dataFileIn+" due to: "+exc.getMessage());
		}
		int moleculeCounter=0;
		int fingerprintCounter=0;
		while (imdl.hasNext()){
			//query=new QueryAtomContainer();
			query=new org.openscience.cdk.AtomContainer();
			m=(Molecule) imdl.next();
			moleculeCounter++;
			//System.out.println(m);
			if (anyAtom){
				System.out.println("AnyAtom + false");
				
				query=QueryAtomContainerCreator.createAnyAtomContainer(m,false);
			}else if (anyAtomAnyBond){
				//System.out.println(">AnyAtomAnyBond");
				//query=QueryAtomContainerCreator.createAnyAtomAnyBondContainer(m);
			}else{
				
//				try{
//					HueckelAromaticityDetector.detectAromaticity(m);
//				}catch(Exception ex1){
//					System.out.println("Could not find aromaticity due to:"+ex1);
//				}	
				//query=createAnyAtomAtomContainer(m);
				//query=(AtomContainer)m.clone();
				query=createAnyAtomAnyBondAtomContainer(m);
				

			}

			/*RingSet sssr=null;
			System.out.println("**** MOLECULE ****");
			try{
				System.out.println("Check AllringsfinderMolecule");
				AllRingsFinder arf = new AllRingsFinder();
				sssr = arf.findAllRings(m);
			}catch(Exception excAR){
				System.out.println("MoleculeAllRingsFinderError: from molecule:"+moleculeCounter+" due to:"+excAR.toString());
			}
			
			try{
				System.out.println("Check SSSRfinderMolecule");
				SSSRFinder sssrf = new SSSRFinder(m);
				sssr = sssrf.findSSSR();
	
			}catch(Exception excSR){
				System.out.println("MoleculeSSSRFFinderError: from molecule:"+moleculeCounter+" due to:"+excSR.toString());
			}
			
			The Chemistry Development Kit - http://cdk.sf.net/ | 4 tests fail in CVS | CDK News 2/2 is out | CDK5AW event on 10-15 October, see http://almost.cubic.uni-koeln.de/cdk/cdk_top/devel/events/cdk5yearworkshop/
			try{
				System.out.println("Make FingerprintMolecule");
				Fingerprinter.getFingerprint((AtomContainer) m);
				//fingerprintCounter=fingerprintCounter+1;
			}catch(Exception exc1){
				//exc1.printStackTrace();
				System.out.println("MoleculeFingerprintError: from molecule:"+moleculeCounter+" due to:"+exc1.toString());
			}
			
			System.out.println("**** QUERY ****");
			query=(QueryAtomContainer)resetFlags(query);
			try{
				System.out.println("Check AllringsfinderQuery");
				AllRingsFinder arf = new AllRingsFinder();
				sssr = arf.findAllRings(query);
			}catch(Exception excAR){
				System.out.println(" QueryAllRingsFinderError: from molecule:"+moleculeCounter+" due to:"+excAR.toString());
			}
			
			try{
				System.out.println("Check SSSRfinderQuery");
				SSSRFinder sssrf = new SSSRFinder(query);
				sssr = sssrf.findSSSR();	
			}catch(Exception excSR){
				System.out.println("QuerySSSRFFinderError: from molecule:"+moleculeCounter+" due to:"+excSR.toString());
			}			
		*/
			if (anyAtom || anyAtomAnyBond){
				try{
					System.out.println("Make Fingerprint Query");
					data.add((BitSet)fingerPrinter.getFingerprint((IAtomContainer) query));
					fingerprintCounter=fingerprintCounter+1;
				}catch(Exception exc1){
					exc1.printStackTrace();
					System.out.println("QueryFingerprintError: from molecule:"+moleculeCounter+" due to:"+exc1.toString());
				}
			}else{
				try{
					//System.out.println("Make Fingerprint Molecule");
					data.add((BitSet)fingerPrinter.getFingerprint(query));
					fingerprintCounter=fingerprintCounter+1;
				}catch(Exception exc2){
					exc2.printStackTrace();
					System.out.println("QueryFingerprintError: from molecule:"+moleculeCounter+" due to:"+exc2.toString());
				}	
			}
			
		}//while
		try{
			imdl.close();
		}catch (Exception exc2){
		}
		
		
		System.out.print("...ready with:"+moleculeCounter+" molecules\nWrite data...of data vector:"+data.size()+" fingerprintCounter:"+fingerprintCounter);
		BufferedWriter fout=null;
		try{	
			fout=new BufferedWriter(new FileWriter(dataFileOut));
		}catch(Exception exc3){
			System.out.println("Could not write Fingerprint in file "+dataFileOut+" due to: "+exc3.getMessage());
		}
		for (int i=0;i<data.size();i++){
			try{
				fout.write(((BitSet)data.get(i)).toString());
				fout.newLine();
			}catch(Exception exc4){
			}
		}
		System.out.println("\nFingerprints:"+data.size()+" are written...ready");
		try{
			fout.close();
		}catch (Exception exc5){
		}
	}
    
    
    public Molecule removeLoopBonds(Molecule molecule, int position){
    		for (int i = 0; i < molecule.getBondCount(); i++) {
    			IBond bond = molecule.getBond(i);
    			if (bond.getAtom(0) == bond.getAtom(1)){
    				System.out.println("Loop found! Molecule:"+position);
    				molecule.removeBond(bond);
    			}
    		}
    	
    	return molecule;
    }
    
	public AtomContainer createAnyAtomAtomContainer(IAtomContainer atomContainer) throws Exception
	{
		AtomContainer query = (AtomContainer) atomContainer.clone();
		//System.out.println("createAnyAtomAtomContainer");
		for (int i=0;i<query.getAtomCount();i++){
			//System.out.print(" "+i);
			query.getAtom(i).setSymbol("C");
		}
		return query;
	}
	
	public IAtomContainer createAnyAtomAnyBondAtomContainer(IAtomContainer atomContainer) throws Exception
	{
		IAtomContainer query = (IAtomContainer) atomContainer.clone();
		IBond bond = null;
		for (int i = 0; i < query.getBondCount(); i++) {
			query.getBond(i).setOrder(1);
			query.getBond(i).getAtom(0).setSymbol("C");
			query.getBond(i).getAtom(1).setSymbol("C");		}
		return query;
	}
    
	public IAtomContainer resetFlags(IAtomContainer ac)
	{
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			ac.getAtom(f).setFlag(CDKConstants.VISITED, false);
		}
		for (int f = 0; f < ac.getElectronContainerCount(); f++)
		{
			ac.getElectronContainer(f).setFlag(CDKConstants.VISITED, false);
		}
		return ac;
	}
    
	
	public static void main(String[] args)
	{
		try {
			new TemplateExtractor().makeFingerprintFromRingSystems(args[0], args[1], false, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
