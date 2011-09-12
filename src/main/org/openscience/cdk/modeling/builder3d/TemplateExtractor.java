/* Copyright (C) 2004-2007  Christian Hoppe <c.hoppe_@web.de>
 *                    2011  Egon Willighagen <egonw@users.sf.net>
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.fingerprint.HybridizationFingerprinter;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.fingerprint.BitSetFingerprint;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.fingerprint.IBitFingerprint;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

/**
 * Helper class that help setup a template library of CDK's Builder3D.
 * 
 * @author      Christian Hoppe
 * @cdk.module  builder3dtools
 * @cdk.githash
 */
public class TemplateExtractor {

	static final String usage = "Usage: TemplateExtractor SDFinfile outfile anyAtom=true/false anyBondAnyAtom=true/false";
	
	private final static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

	public TemplateExtractor() {
	}

	public void cleanDataSet(String dataFile) {
		IteratingSDFReader imdl = null;
		IAtomContainerSet som = builder.newInstance(IAtomContainerSet.class);
		try {
			System.out.println("Start clean dataset...");
			BufferedReader fin = new BufferedReader(new FileReader(dataFile));
			imdl = new IteratingSDFReader(fin, builder);
			System.out.print("Read File in..");
		} catch (Exception exc) {
			System.out.println("Could not read Molecules from file " + dataFile
					+ " due to: " + exc.getMessage());
		}
		System.out.println("READY");
		int c = 0;
		while (imdl.hasNext()) {
			c++;
			if (c % 1000 == 0) {
				System.out.println("...");
			}
			IAtomContainer m = builder.newInstance(IAtomContainer.class);
			m = (IAtomContainer) imdl.next();
			if (m.getAtomCount() > 2) {
				if (m.getAtom(0).getPoint3d() != null) {
					som.addAtomContainer(m);
				}
			}
		}
		try {
			imdl.close();
		} catch (Exception exc1) {
			System.out.println("Could not close Reader due to: "
					+ exc1.getMessage());
		}
		System.out.println(som.getAtomContainerCount() + " Templates are read in");
		writeChemModel(som, dataFile, "_CLEAN");
	}

	public void ReadNCISdfFileAsTemplate(String dataFile) {
		IteratingSDFReader imdl = null;
		IAtomContainerSet som = builder.newInstance(IAtomContainerSet.class);
		try {
			System.out.println("Start...");
			BufferedReader fin = new BufferedReader(new FileReader(dataFile));
			imdl = new IteratingSDFReader(fin, builder);
			System.out.print("Read File in..");
		} catch (Exception exc) {
			System.out.println("Could not read Molecules from file " + dataFile
					+ " due to: " + exc.getMessage());
		}
		System.out.println("READY");
		while (imdl.hasNext()) {
			som.addAtomContainer((IAtomContainer) imdl.next());
		}
		try {
			imdl.close();
		} catch (Exception exc1) {
			System.out.println("Could not close Reader due to: "
					+ exc1.getMessage());
		}
		System.out.println(som.getAtomContainerCount() + " Templates are read in");
	}

	public void PartitionRingsFromComplexRing(String dataFile) {
		IteratingSDFReader imdl = null;
		IAtomContainerSet som = builder.newInstance(IAtomContainerSet.class);
		IAtomContainer m = null;
		try {
			System.out.println("Start...");
			BufferedReader fin = new BufferedReader(new FileReader(dataFile));
			imdl = new IteratingSDFReader(fin, builder);
			System.out.print("Read File in..");
		} catch (Exception exc) {
			System.out.println("Could not read Molecules from file " + dataFile
					+ " due to: " + exc.getMessage());
		}
		System.out.println("READY");
		while (imdl.hasNext()) {
			m = (IAtomContainer) imdl.next();
			System.out.println("Atoms:" + m.getAtomCount());
			IRingSet ringSetM = new SSSRFinder(m).findSSSR();
			// som.addAtomContainer(m);
			for (int i = 0; i < ringSetM.getAtomContainerCount(); i++) {
				som.addAtomContainer(builder.newInstance(IAtomContainer.class, ringSetM.getAtomContainer(i)));
			}
		}
		try {
			imdl.close();
		} catch (Exception exc1) {
			System.out.println("Could not close Reader due to: "
					+ exc1.getMessage());
		}
		System.out.println(som.getAtomContainerCount() + " Templates are read in");
		writeChemModel(som, dataFile, "_VERSUCH");
	}

	public void extractUniqueRingSystemsFromFile(String dataFile) {
		System.out.println("****** EXTRACT UNIQUE RING SYSTEMS ******");
		System.out.println("From file:" + dataFile);
		IAtomContainer m = null;
		// RingPartitioner ringPartitioner=new RingPartitioner();
		List<IRingSet> ringSystems = null;
		IteratingSDFReader imdl = null;

		HashMap<String,String> hashRingSystems = new HashMap<String,String>();
		SmilesGenerator smilesGenerator = new SmilesGenerator();

		int counterRings = 0;
		int counterMolecules = 0;
		int counterUniqueRings = 0;
		IRingSet ringSet = null;
		String key = "";
		IAtomContainer ac = null;

		String molfile = dataFile + "_UniqueRings";

		// FileOutputStream fout=null;
		MDLV2000Writer mdlw = null;
		try {
			FileOutputStream fout = new FileOutputStream(molfile);
			mdlw = new MDLV2000Writer(fout);
		} catch (Exception ex2) {
			System.out.println("IOError:cannot write file due to:"
					+ ex2.toString());
		}

		try {
			System.out.println("Start...");
			BufferedReader fin = new BufferedReader(new FileReader(dataFile));
			imdl = new IteratingSDFReader(fin, builder);
			System.out.println("Read File in..");
		} catch (Exception exc) {
			System.out.println("Could not read Molecules from file " + dataFile
					+ " due to: " + exc.getMessage());
		}
		while (imdl.hasNext()) {
			m = (IAtomContainer) imdl.next();
			counterMolecules = counterMolecules + 1;
			/*
			 * try{ HueckelAromaticityDetector.detectAromaticity(m);
			 * }catch(Exception ex1){ System.out.println("Could not find
			 * aromaticity due to:"+ex1); }
			 */
			IRingSet ringSetM = new SSSRFinder(m).findSSSR();

			if (counterMolecules % 1000 == 0) {
				System.out.println("Molecules:" + counterMolecules);
			}

			if (ringSetM.getAtomContainerCount() > 0) {
				ringSystems = RingPartitioner.partitionRings(ringSetM);

				for (int i = 0; i < ringSystems.size(); i++) {
					ringSet = (IRingSet) ringSystems.get(i);
					ac = builder.newInstance(IAtomContainer.class);
					Iterator<IAtomContainer> containers = RingSetManipulator.getAllAtomContainers(ringSet).iterator();
					while (containers.hasNext()) {
						ac.add((IAtomContainer)containers.next());
					}
					counterRings = counterRings + 1;
					// Only connection is important
					for (int j = 0; j < ac.getAtomCount(); j++) {
						(ac.getAtom(j)).setSymbol("C");
					}

					key = smilesGenerator.createSMILES(builder.newInstance(IAtomContainer.class,ac));
					// System.out.println("OrgKey:"+key+" For
					// Molecule:"+counter);
					if (hashRingSystems.containsKey(key)) {
						// System.out.println("HAS KEY:ADD");
						// Vector tmp=(Vector)HashRingSystems.get(key);
						// tmp.add((AtomContainer)ringSet.getRingSetInAtomContainer());
						// HashRingSystems.put(key,tmp);
						// int
						// tmp=((Integer)HashRingSystems.get(key)).intValue();
						// tmp=tmp+1;
						// HashRingSystems.put(key,new Integer(tmp));
					} else {
						counterUniqueRings = counterUniqueRings + 1;
						// Vector rings2=new Vector();
						// rings2.add((AtomContainer)RingSetManipulator.getAllInOneContainer(ringSet));
						hashRingSystems.put(key, new String("1"));
						try {
							// mdlw.write(new Molecule
							// ((AtomContainer)RingSetManipulator.getAllInOneContainer(ringSet)));
							mdlw.write(builder.newInstance(IAtomContainer.class,ac));
						} catch (Exception emdl) {
						}

					}
				}

			}
		}
		try {
			imdl.close();
			mdlw.close();
		} catch (Exception exc1) {
			System.out.println("Could not close iterator mdl reader due to: "
					+ exc1.getMessage());
		}
		// System.out.println("READY Molecules:"+counterMolecules);
		System.out.println("READY Molecules:" + counterMolecules
				+ " RingSystems:" + counterRings + " UniqueRingsSystem:"
				+ counterUniqueRings);
		System.out.println("HashtableKeys:" + hashRingSystems.size());

		/*
		 * int c=0; Set keyset = HashRingSystems.keySet(); Iterator
		 * it=keyset.iterator(); IMoleculeSet som=new MoleculeSet();
		 * SmilesParser smileParser=new SmilesParser(); String ringSmile="";
		 * while (it.hasNext()) { key=(String)it.next();
		 * ringSmile=(String)HashRingSystems.get(key);
		 * System.out.println("HashtableSmile:"+ringSmile+" key:"+key); try{
		 * som.addAtomContainer(smileParser.parseSmiles(ringSmile)); }catch
		 * (Exception ex5){ System.out.println("Error in som.addmolecule due
		 * to:"+ex5); } }
		 */

		// writeChemModel(som,dataFile,"_TESTTESTTESTTESTTEST");
	}

	public void writeChemModel(IAtomContainerSet som, String file, String endFix) {
		System.out.println("WRITE Molecules:" + som.getAtomContainerCount());
		String molfile = file + endFix;
		try {
			FileOutputStream fout = new FileOutputStream(molfile);
			MDLV2000Writer mdlw = new MDLV2000Writer(fout);
			mdlw.write(som);
			mdlw.close();
		} catch (Exception ex2) {
			System.out.println("IOError:cannot write file due to:"
					+ ex2.toString());
		}
	}

	public void makeCanonicalSmileFromRingSystems(String dataFileIn,
			String dataFileOut) {
		System.out.println("Start make SMILES...");
		IAtomContainer m = null;
		IteratingSDFReader imdl = null;
		// QueryAtomContainer query=null;
		List<String> data = new ArrayList<String>();
		SmilesGenerator smiles = new SmilesGenerator();
		try {
			System.out.println("Start...");
			BufferedReader fin = new BufferedReader(new FileReader(dataFileIn));
			imdl = new IteratingSDFReader(fin, builder);
			// fin.close();
			System.out.println("Read File in..");
		} catch (Exception exc) {
			System.out.println("Could not read Molecules from file "
					+ dataFileIn + " due to: " + exc.getMessage());
		}
		while (imdl.hasNext()) {
			m = (IAtomContainer) imdl.next();
			/*
			 * try{ HueckelAromaticityDetector.detectAromaticity(m);
			 * }catch(Exception ex1){ System.out.println("Could not find
			 * aromaticity due to:"+ex1); }
			 */
			// query=QueryAtomContainerCreator.createAnyAtomContainer(m,true);
			// System.out.println("String:"+smiles.createSMILES(new
			// Molecule(m)));
			try {

				data.add((String) smiles.createSMILES(builder.newInstance(IAtomContainer.class,m)));
			} catch (Exception exc1) {
				System.out.println("Could not create smile due to: "
						+ exc1.getMessage());
			}
		}
		try {
			imdl.close();
		} catch (Exception exc2) {
		}

		System.out.print("...ready\nWrite data...");
		BufferedWriter fout = null;
		try {
			fout = new BufferedWriter(new FileWriter(dataFileOut));
		} catch (Exception exc3) {
			System.out.println("Could not write smile in file " + dataFileOut
					+ " due to: " + exc3.getMessage());
		}
		for (int i = 0; i < data.size(); i++) {
			// System.out.println("write:"+(String)data.get(i));
			try {

				fout.write(((String) data.get(i)));
				fout.newLine();
			} catch (Exception exc4) {
			}
		}
		System.out.println("number of smiles:" + data.size());
		System.out.println("...ready");
		try {
			fout.close();
		} catch (Exception exc5) {
		}
	}

	public List<IBitFingerprint> makeFingerprintsFromSdf(boolean anyAtom, boolean anyAtomAnyBond,
	        Map<String,Integer> timings, BufferedReader fin, int limit) throws Exception {
		AllRingsFinder allRingsFinder = new AllRingsFinder();
		allRingsFinder.setTimeout(10000); // 10 seconds

		IFingerprinter fingerPrinter = new HybridizationFingerprinter(
		    HybridizationFingerprinter.DEFAULT_SIZE, HybridizationFingerprinter.DEFAULT_SEARCH_DEPTH
		);
		IAtomContainer m = null;
		IteratingSDFReader imdl=null;
		//QueryAtomContainer query=null;
		IAtomContainer query = null;
		List<IBitFingerprint> data = new ArrayList<IBitFingerprint>();
		try {
			System.out.print("Read data file in ...");
			imdl = new IteratingSDFReader(fin, builder);
			// fin.close();
			System.out.println("ready");
		} catch (Exception exc) {
			System.out.println("Could not read Molecules from file"+
					" due to: " + exc.getMessage());
		}
		int moleculeCounter = 0;
		int fingerprintCounter = 0;
		System.out.print("Generated Fingerprints: " + fingerprintCounter + "    ");
		while (imdl.hasNext() && (moleculeCounter<limit || limit==-1)) {
			m = (IAtomContainer) imdl.next();
			moleculeCounter++;
            if (anyAtom && !anyAtomAnyBond) {
                query = QueryAtomContainerCreator.createAnyAtomContainer(m, false);
            } else {
                query = AtomContainerManipulator.createAllCarbonAllSingleNonAromaticBondAtomContainer(m);

            }
			try {
				long time = -System.currentTimeMillis();
				if (anyAtom || anyAtomAnyBond){
					data.add(fingerPrinter.getBitFingerprint(query));
					fingerprintCounter=fingerprintCounter+1;
				} else {
					data.add(fingerPrinter.getBitFingerprint(query));
					fingerprintCounter = fingerprintCounter + 1;
				}
				time += System.currentTimeMillis();
				// store the time
				String bin = Integer.toString((int)Math.floor(time/10));
				if (timings.containsKey(bin)) {
					timings.put(bin, (timings.get(bin)) + 1);
				} else {
					timings.put(bin, new Integer(1));
				}
			}catch(Exception exc1){
				System.out.println("QueryFingerprintError: from molecule:"
						+ moleculeCounter + " due to:" + exc1.getMessage());
				
				// OK, just adds a fingerprint with all ones, so that any
				// structure will match this template, and leave it up
				// to substructure match to figure things out
				IBitFingerprint allOnesFingerprint = new BitSetFingerprint(fingerPrinter.getSize());
				for (int i=0; i<fingerPrinter.getSize(); i++) {
					allOnesFingerprint.set(i, true);
				}
				data.add(allOnesFingerprint);
				fingerprintCounter = fingerprintCounter + 1;
			}
			
			if (fingerprintCounter % 2 == 0)
				System.out.print("\b" + "/");
			else
				System.out.print("\b" + "\\");

			
			if (fingerprintCounter % 100 == 0)
				System.out.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b" + "Generated Fingerprints: " + fingerprintCounter + "   \n");

		}// while 
		try {
			imdl.close();
		} catch (Exception exc2) 
		{
			exc2.printStackTrace();
		}
		System.out.print("...ready with:" + moleculeCounter
				+ " molecules\nWrite data...of data vector:" + data.size()
				+ " fingerprintCounter:" + fingerprintCounter);

		return data;
	}
	
	public void makeFingerprintFromRingSystems(String dataFileIn,
			String dataFileOut, boolean anyAtom, boolean anyAtomAnyBond)
	throws Exception {
		Map<String,Integer> timings = new HashMap<String,Integer>();

		System.out.println("Start make fingerprint from file:" + dataFileIn
				+ " ...");
		BufferedReader fin = new BufferedReader(new FileReader(dataFileIn));
		List<IBitFingerprint> data = makeFingerprintsFromSdf(anyAtom, anyAtomAnyBond, timings, fin,-1);
		BufferedWriter fout = null;
		try {
			fout = new BufferedWriter(new FileWriter(dataFileOut));
		} catch (Exception exc3) {
			System.out.println("Could not write Fingerprint in file "
					+ dataFileOut + " due to: " + exc3.getMessage());
		}
		for (int i = 0; i < data.size(); i++) {
			try {
				fout.write(data.get(i).toString());
				fout.newLine();
			} catch (Exception exc4) {
			}
		}
		System.out.println("\nFingerprints:" + data.size()
				+ " are written...ready");
		System.out.println("\nComputing time statistics:\n" + timings.toString());
		try {
			fout.close();
		} catch (Exception exc5) {
		}
	}

	public IAtomContainer removeLoopBonds(IAtomContainer molecule, int position) {
		for (int i = 0; i < molecule.getBondCount(); i++) {
			IBond bond = molecule.getBond(i);
			if (bond.getAtom(0) == bond.getAtom(1)) {
				System.out.println("Loop found! Molecule:" + position);
				molecule.removeBond(bond);
			}
		}

		return molecule;
	}

	public IAtomContainer createAnyAtomAtomContainer(IAtomContainer atomContainer)
	throws Exception {
		IAtomContainer query = (IAtomContainer) atomContainer.clone();
		// System.out.println("createAnyAtomAtomContainer");
		for (int i = 0; i < query.getAtomCount(); i++) {
			// System.out.print(" "+i);
			query.getAtom(i).setSymbol("C");
		}
		return query;
	}	
	
	public IAtomContainer resetFlags(IAtomContainer ac) {
		for (int f = 0; f < ac.getAtomCount(); f++) {
			ac.getAtom(f).setFlag(CDKConstants.VISITED, false);
		}
		for (int f = 0; f < ac.getElectronContainerCount(); f++) {
			ac.getElectronContainer(f).setFlag(CDKConstants.VISITED, false);
		}
		return ac;
	}

	public static void main(String[] args) {
		if (args.length < 4) {
			System.out.println(usage);

		}
		try {
			new TemplateExtractor().makeFingerprintFromRingSystems(args[0],
					args[1], Boolean.valueOf(args[2]), Boolean.valueOf(args[3]));
		} catch (Exception e) {
			System.out.println(usage);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
