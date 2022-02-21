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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.HybridizationFingerprinter;
import org.openscience.cdk.fingerprint.BitSetFingerprint;
import org.openscience.cdk.fingerprint.IBitFingerprint;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
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

    static final String                     usage   = "Usage: TemplateExtractor SDFinfile outfile anyAtom=true/false anyBondAnyAtom=true/false";

    private final static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    public TemplateExtractor() {}

    public void cleanDataSet(String dataFile) {
        IAtomContainerSet som = builder.newInstance(IAtomContainerSet.class);
        System.out.println("Start clean dataset...");
        try (BufferedReader fin = new BufferedReader(new FileReader(dataFile));
             IteratingSDFReader imdl = new IteratingSDFReader(fin, builder)){
            System.out.print("Read File in..");
            System.out.println("READY");
            int c = 0;
            while (imdl.hasNext()) {
                c++;
                if (c % 1000 == 0) {
                    System.out.println("...");
                }
                IAtomContainer m = builder.newInstance(IAtomContainer.class);
                m = imdl.next();
                if (m.getAtomCount() > 2) {
                    if (m.getAtom(0).getPoint3d() != null) {
                        som.addAtomContainer(m);
                    }
                }
            }
            System.out.println(som.getAtomContainerCount() + " Templates are read in");
            writeChemModel(som, dataFile, "_CLEAN");
        } catch (Exception exc) {
            System.out.println("Could not read Molecules from file " + dataFile + " due to: " + exc.getMessage());
        }
    }

    public void ReadNCISdfFileAsTemplate(String dataFile) {
        IAtomContainerSet som = builder.newInstance(IAtomContainerSet.class);
        System.out.println("Start...");
        try (BufferedReader fin = new BufferedReader(new FileReader(dataFile));
             IteratingSDFReader imdl = new IteratingSDFReader(fin, builder)) {
            System.out.print("Read File in..");
            System.out.println("READY");
            while (imdl.hasNext()) {
                som.addAtomContainer(imdl.next());
            }
        } catch (Exception exc) {
            System.out.println("Could not read Molecules from file " + dataFile + " due to: " + exc.getMessage());
        }
        System.out.println(som.getAtomContainerCount() + " Templates are read in");
    }

    public void PartitionRingsFromComplexRing(String dataFile) {
        IAtomContainerSet som = builder.newInstance(IAtomContainerSet.class);
        IAtomContainer m;
        try (BufferedReader fin = new BufferedReader(new FileReader(dataFile));
             IteratingSDFReader imdl = new IteratingSDFReader(fin, builder)) {
            while (imdl.hasNext()) {
                m = imdl.next();
                System.out.println("Atoms:" + m.getAtomCount());
                IRingSet ringSetM = Cycles.sssr(m).toRingSet();
                // som.addAtomContainer(m);
                for (int i = 0; i < ringSetM.getAtomContainerCount(); i++) {
                    som.addAtomContainer(builder.newInstance(IAtomContainer.class, ringSetM.getAtomContainer(i)));
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        System.out.println(som.getAtomContainerCount() + " Templates are read in");
        writeChemModel(som, dataFile, "_VERSUCH");
    }

    public void extractUniqueRingSystemsFromFile(String dataFile) {
        System.out.println("****** EXTRACT UNIQUE RING SYSTEMS ******");
        System.out.println("From file:" + dataFile);
        IAtomContainer m;
        // RingPartitioner ringPartitioner=new RingPartitioner();
        List<IRingSet> ringSystems;

        HashMap<String, String> hashRingSystems = new HashMap<>();
        SmilesGenerator smilesGenerator = new SmilesGenerator();

        int counterRings = 0;
        int counterMolecules = 0;
        int counterUniqueRings = 0;
        IRingSet ringSet;
        String key;
        IAtomContainer ac;

        String molfile = dataFile + "_UniqueRings";

        // FileOutputStream fout=null;
        try (FileOutputStream fout = new FileOutputStream(molfile);
             MDLV2000Writer mdlw = new MDLV2000Writer(fout);
             BufferedReader fin = new BufferedReader(new FileReader(dataFile));
             IteratingSDFReader imdl = new IteratingSDFReader(fin, builder)) {
            while (imdl.hasNext()) {
                m = imdl.next();
                counterMolecules = counterMolecules + 1;
                /*
                 * try{ HueckelAromaticityDetector.detectAromaticity(m);
                 * }catch(Exception ex1){ System.out.println("Could not find
                 * aromaticity due to:"+ex1); }
                 */
                IRingSet ringSetM = Cycles.sssr(m).toRingSet();

                if (counterMolecules % 1000 == 0) {
                    System.out.println("Molecules:" + counterMolecules);
                }

                if (ringSetM.getAtomContainerCount() > 0) {
                    ringSystems = RingPartitioner.partitionRings(ringSetM);

                    for (IRingSet ringSystem : ringSystems) {
                        ringSet = ringSystem;
                        ac = builder.newInstance(IAtomContainer.class);
                        for (IAtomContainer container : RingSetManipulator.getAllAtomContainers(ringSet)) {
                            ac.add(container);
                        }
                        counterRings = counterRings + 1;
                        // Only connection is important
                        for (int j = 0; j < ac.getAtomCount(); j++) {
                            (ac.getAtom(j)).setSymbol("C");
                        }

                        try {
                            key = smilesGenerator.create(builder.newInstance(IAtomContainer.class, ac));
                        } catch (CDKException e) {
                            LoggingToolFactory.createLoggingTool(getClass()).error(e);
                            return;
                        }

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
                            hashRingSystems.put(key, "1");
                            try {
                                // mdlw.write(new Molecule
                                // ((AtomContainer)RingSetManipulator.getAllInOneContainer(ringSet)));
                                mdlw.write(builder.newInstance(IAtomContainer.class, ac));
                            } catch (IllegalArgumentException | CDKException emdl) {
                            }

                        }
                    }

                }
            }
        } catch (Exception exc) {
            System.out.println("Could not read/write Molecules from file " + dataFile + " due to: " + exc.getMessage());
        }

        // System.out.println("READY Molecules:"+counterMolecules);
        System.out.println("READY Molecules:" + counterMolecules + " RingSystems:" + counterRings
                + " UniqueRingsSystem:" + counterUniqueRings);
        System.out.println("HashtableKeys:" + hashRingSystems.size());

        /*
         * int c=0; Set keyset = HashRingSystems.keySet(); Iterator
         * it=keyset.iterator(); IAtomContainerSet som=new AtomContainerSet();
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
        try ( FileOutputStream fout = new FileOutputStream(molfile);
              MDLV2000Writer mdlw = new MDLV2000Writer(fout)) {
            mdlw.write(som);
        } catch (CDKException | IOException ex2) {
            System.out.println("IOError:cannot write file due to:" + ex2);
        }
    }

    public void makeCanonicalSmileFromRingSystems(String dataFileIn, String dataFileOut) {
        System.out.println("Start make SMILES...");
        List<String> data = new ArrayList<>();
        SmilesGenerator smiles = new SmilesGenerator();
        System.out.println("Start...");
        try (BufferedReader fin = new BufferedReader(new FileReader(dataFileIn));
             IteratingSDFReader imdl = new IteratingSDFReader(fin, builder)) {
            System.out.println("Read File in..");
            while (imdl.hasNext()) {
                IAtomContainer m = imdl.next();
                try {
                    data.add(smiles.create(builder.newInstance(IAtomContainer.class, m)));
                } catch (IllegalArgumentException | CDKException exc1) {
                    System.out.println("Could not create smile due to: " + exc1.getMessage());
                }
            }
        } catch (Exception exc) {
            System.out.println("Could not read Molecules from file " + dataFileIn + " due to: " + exc.getMessage());
            return;
        }

        System.out.print("...ready\nWrite data...");
        try (BufferedWriter fout = new BufferedWriter(new FileWriter(dataFileOut))) {
            for (String datum : data) {
                try {
                    fout.write(datum);
                    fout.write('\n');
                } catch (Exception ignored) {
                }
            }
        } catch (Exception exc3) {
            System.out.println("Could not write smile in file " + dataFileOut + " due to: " + exc3.getMessage());
            return;
        }
        System.out.println("number of smiles:" + data.size());
        System.out.println("...ready");
    }

    public List<IBitFingerprint> makeFingerprintsFromSdf(boolean anyAtom, boolean anyAtomAnyBond,
            Map<String, Integer> timings, BufferedReader fin, int limit) throws Exception {

        HybridizationFingerprinter fingerPrinter = new HybridizationFingerprinter(HybridizationFingerprinter.DEFAULT_SIZE,
                HybridizationFingerprinter.DEFAULT_SEARCH_DEPTH);
        fingerPrinter.setHashPseudoAtoms(true);
        IAtomContainer m;
        IteratingSDFReader imdl = null;
        //QueryAtomContainer query=null;
        IAtomContainer query;
        List<IBitFingerprint> data = new ArrayList<>();
        ILoggingTool logger = LoggingToolFactory.createLoggingTool(getClass());
        try {
            logger.info("Read data file in ...");
            imdl = new IteratingSDFReader(fin, builder);
            // fin.close();
            logger.info("ready");
        } catch (Exception exc) {
            System.out.println("Could not read Molecules from file" + " due to: " + exc.getMessage());
        }
        int moleculeCounter = 0;
        int fingerprintCounter = 0;
        logger.info("Generated Fingerprints: " + fingerprintCounter + "    ");
        while (imdl.hasNext() && (moleculeCounter < limit || limit == -1)) {
            m = imdl.next();
            moleculeCounter++;
            if (anyAtom && !anyAtomAnyBond) {
                query = QueryAtomContainerCreator.createAnyAtomContainer(m, false);
            } else {
                query = AtomContainerManipulator.anonymise(m);

            }
            try {
                long time = -System.currentTimeMillis();
                // query fp for anyAtom is probably not useful
                data.add(fingerPrinter.getBitFingerprint(query));
                fingerprintCounter = fingerprintCounter + 1;
                time += System.currentTimeMillis();
                // store the time
                String bin = Integer.toString((int) Math.floor(time / 10.0));
                if (timings.containsKey(bin)) {
                    timings.put(bin, (timings.get(bin)) + 1);
                } else {
                    timings.put(bin, 1);
                }
            } catch (Exception exc1) {
                logger.info("QueryFingerprintError: from molecule:" + moleculeCounter + " due to:"
                        + exc1.getMessage());

                // OK, just adds a fingerprint with all ones, so that any
                // structure will match this template, and leave it up
                // to substructure match to figure things out
                IBitFingerprint allOnesFingerprint = new BitSetFingerprint(fingerPrinter.getSize());
                for (int i = 0; i < fingerPrinter.getSize(); i++) {
                    allOnesFingerprint.set(i, true);
                }
                data.add(allOnesFingerprint);
                fingerprintCounter = fingerprintCounter + 1;
            }

            if (fingerprintCounter % 2 == 0)
                logger.info("\b" + "/");
            else
                logger.info("\b" + "\\");

            if (fingerprintCounter % 100 == 0)
                logger.info("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b"
                        + "Generated Fingerprints: " + fingerprintCounter + "   \n");

        }// while
        try {
            imdl.close();
        } catch (Exception exc2) {
            LoggingToolFactory.createLoggingTool(TemplateExtractor.class)
                              .warn("Unexpected Exception:", exc2);
        }
        logger.info("...ready with:" + moleculeCounter + " molecules\nWrite data...of data vector:" + data.size()
                + " fingerprintCounter:" + fingerprintCounter);

        return data;
    }

    public void makeFingerprintFromRingSystems(String dataFileIn, String dataFileOut, boolean anyAtom,
            boolean anyAtomAnyBond) throws Exception {
        Map<String, Integer> timings = new HashMap<>();

        System.out.println("Start make fingerprint from file:" + dataFileIn + " ...");
        BufferedReader fin = new BufferedReader(new FileReader(dataFileIn));
        List<IBitFingerprint> data = makeFingerprintsFromSdf(anyAtom, anyAtomAnyBond, timings, fin, -1);
        BufferedWriter fout = null;
        try {
            fout = new BufferedWriter(new FileWriter(dataFileOut));
        } catch (Exception exc3) {
            System.out.println("Could not write Fingerprint in file " + dataFileOut + " due to: " + exc3.getMessage());
            return;
        }
        for (IBitFingerprint datum : data) {
            try {
                fout.write(datum.toString());
                fout.write('\n');
            } catch (Exception exc4) {
            }
        }
        System.out.println("\nFingerprints:" + data.size() + " are written...ready");
        System.out.println("\nComputing time statistics:\n" + timings);
        try {
            fout.close();
        } catch (Exception exc5) {
        }
    }

    public IAtomContainer removeLoopBonds(IAtomContainer molecule, int position) {
        for (int i = 0; i < molecule.getBondCount(); i++) {
            IBond bond = molecule.getBond(i);
            if (bond.getBegin().equals(bond.getEnd())) {
                System.out.println("Loop found! Molecule:" + position);
                molecule.removeBond(bond);
            }
        }

        return molecule;
    }

    public IAtomContainer createAnyAtomAtomContainer(IAtomContainer atomContainer) throws Exception {
        IAtomContainer query = atomContainer.clone();
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
            new TemplateExtractor().makeFingerprintFromRingSystems(args[0], args[1], Boolean.valueOf(args[2]),
                    Boolean.valueOf(args[3]));
        } catch (Exception e) {
            LoggingToolFactory.createLoggingTool(TemplateExtractor.class)
                              .warn("Unexpected Exception:", e);
        }
    }

}
