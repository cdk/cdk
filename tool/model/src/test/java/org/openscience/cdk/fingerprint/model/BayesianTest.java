/* $Revision$ $Author$ $Date$
 *
 * Copyright (c) 2015 Collaborative Drug Discovery, Inc. <alex@collaborativedrug.com>
 *
 * Implemented by Alex M. Clark, produced by Collaborative Drug Discovery, Inc.
 * Made available to the CDK community under the terms of the GNU LGPL.
 *
 *    http://collaborativedrug.com
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
 */

package org.openscience.cdk.fingerprint.model;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SlowTest;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.CircularFingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Map;

/**
 * Validation test for the Bayesian model building & serialisation.
 * @cdk.module test-standard
 */
public class BayesianTest {

    private static ILoggingTool logger          = LoggingToolFactory.createLoggingTool(BayesianTest.class);

    private final String        REF_MOLECULE    = "\n\n\n"
                                                        + " 18 19  0  0  0  0  0  0  0  0999 V2000\n"
                                                        + "   -2.5317   -1.1272    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
                                                        + "   -1.5912    0.1672    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
                                                        + "   -2.2420    1.6289    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
                                                        + "    0.0000    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
                                                        + "    1.0706    1.1890    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
                                                        + "    2.5323    0.5383    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
                                                        + "    2.3650   -1.0530    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
                                                        + "    0.8000   -1.3856    0.0000 S   0  0  0  0  0  0  0  0  0  0  0  0\n"
                                                        + "    3.5541   -2.1236    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
                                                        + "    5.0758   -1.6292    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
                                                        + "    5.4084   -0.0641    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
                                                        + "    6.2648   -2.6998    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
                                                        + "    7.7865   -2.2053    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
                                                        + "    8.1191   -0.6403    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
                                                        + "    9.6408   -0.1459    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
                                                        + "   10.8299   -1.2165    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
                                                        + "   10.4972   -2.7815    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
                                                        + "    8.9755   -3.2760    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
                                                        + "  1  2  1  0  0  0  0\n" + "  2  3  1  0  0  0  0\n"
                                                        + "  2  4  1  0  0  0  0\n" + "  4  8  1  0  0  0  0\n"
                                                        + "  4  5  2  0  0  0  0\n" + "  5  6  1  0  0  0  0\n"
                                                        + "  6  7  2  0  0  0  0\n" + "  7  8  1  0  0  0  0\n"
                                                        + "  7  9  1  0  0  0  0\n" + "  9 10  1  0  0  0  0\n"
                                                        + " 10 11  2  0  0  0  0\n" + " 10 12  1  0  0  0  0\n"
                                                        + " 12 13  1  0  0  0  0\n" + " 13 18  2  0  0  0  0\n"
                                                        + " 13 14  1  0  0  0  0\n" + " 14 15  2  0  0  0  0\n"
                                                        + " 15 16  1  0  0  0  0\n" + " 16 17  2  0  0  0  0\n"
                                                        + " 17 18  1  0  0  0  0\n" + "M  END";

    private final int[]         REF_ECFP6_0     = {-1951192287, -1876567787, -1685505461, -1594062081, -1494889718,
            -1469934531, -1064027736, -1006701866, -976660244, -964879417, -854951091, -836160636, -801752141,
            -790042671, -777607960, -636984940, -568302198, -563910794, -513573682, -289109509, -203612477, 22318543,
            86479455, 134489603, 229166175, 369386629, 423552486, 543172923, 598483088, 684703116, 747997863,
            772035298, 790592664, 887527738, 962328941, 1053690696, 1143774000, 1194907145, 1323701668, 1413433893,
            1444795951, 1627608083, 1777673917, 1932154898, 1987069734, 1994067521, 2078126852, 2147204365};
    private final int[]         REF_ECFP6_1024  = {18, 19, 61, 95, 133, 144, 152, 206, 232, 236, 269, 277, 314, 315,
            365, 394, 396, 404, 420, 424, 463, 486, 507, 515, 521, 549, 559, 577, 587, 607, 679, 701, 707, 726, 738,
            767, 772, 778, 801, 806, 816, 840, 845, 886, 900, 947, 967, 977};
    private final int[]         REF_ECFP6_32768 = {2555, 2727, 2815, 2888, 3535, 3649, 4703, 5181, 5540, 6458, 6960,
            7111, 7875, 8336, 9448, 9731, 9917, 10555, 11041, 13060, 13188, 14760, 14923, 15283, 15629, 15756, 18214,
            18981, 19210, 19551, 21218, 21523, 22025, 22063, 22546, 22764, 22805, 24980, 25733, 25994, 26086, 26486,
            26577, 29398, 31085, 31565, 31896, 31950};

    private final int[]         REF_FCFP6_0     = {-2128353587, -1853365819, -1764181020, -1625147000, -1589802267,
            -1589654580, -1571133932, -1555670640, -1475665446, -1377516953, -1369998514, -1226686118, -1114704338,
            -983437780, -674976432, -620757428, -454679744, -79956240, 0, 2, 3, 16, 32192941, 147050355, 193192566,
            205312945, 252180819, 346770359, 627637376, 785469695, 822686044, 824716024, 901194889, 960613971,
            994111779, 1018173271, 1481939742, 1629496255, 1992157502, 2101841914};
    private final int[]         REF_FCFP6_1024  = {0, 2, 3, 16, 128, 137, 255, 291, 318, 336, 339, 346, 348, 392, 400,
            429, 453, 474, 532, 556, 558, 588, 595, 615, 630, 717, 741, 752, 760, 798, 832, 846, 855, 883, 945, 951,
            959, 972, 996, 1018                 };
    private final int[]         REF_FCFP6_32768 = {0, 2, 3, 16, 2789, 4090, 5975, 6942, 8666, 9024, 9151, 9353, 11000,
            11600, 12636, 14728, 14765, 15332, 16730, 16999, 19383, 19404, 20051, 20339, 20735, 21425, 22928, 25029,
            25206, 26132, 26317, 26942, 28204, 28963, 30254, 30448, 31059, 31566, 31872, 32332};

    // ----------------- public methods -----------------

    /*
     * temporary: standalone test public static void main(String[] argv) {new
     * BayesianTest().run();} public void run() {
     * writeln("Beginning Bayesian model test..."); try {
     * checkFP(REF_MOLECULE,CircularFingerprinter.CLASS_ECFP6,0,REF_ECFP6_0);
     * checkFP
     * (REF_MOLECULE,CircularFingerprinter.CLASS_ECFP6,1024,REF_ECFP6_1024);
     * checkTextFields(); confirmPredictions("Tiny.sdf",8,8,0,0);
     * confirmPredictions("Small.sdf",6,12,0,6);
     * compareFolding("FoldedProbes.sdf"
     * ,"ECFP6/0",CircularFingerprinter.CLASS_ECFP6,0);
     * compareFolding("FoldedProbes.sdf"
     * ,"ECFP6/1024",CircularFingerprinter.CLASS_ECFP6,1024);
     * compareFolding("FoldedProbes.sdf"
     * ,"ECFP6/32768",CircularFingerprinter.CLASS_ECFP6,32768);
     * compareFolding("FoldedProbes.sdf"
     * ,"FCFP6/0",CircularFingerprinter.CLASS_FCFP6,0);
     * runTest("Binders.sdf","active"
     * ,CircularFingerprinter.CLASS_ECFP6,1024,0,"Binders-ECFP6-1024-loo.bayesian"
     * );
     * runTest("Binders.sdf","active",CircularFingerprinter.CLASS_ECFP6,32768,
     * 5,"Binders-ECFP6-32768-xv5.bayesian");
     * runTest("Binders.sdf","active",CircularFingerprinter
     * .CLASS_FCFP6,0,0,"Binders-FCFP6-0-loo.bayesian");
     * runTest("MLProbes.sdf","Lipinski score"
     * ,CircularFingerprinter.CLASS_ECFP6,
     * 1024,0,"MLProbes-ECFP6-1024-loo.bayesian");
     * runTest("MLProbes.sdf","Lipinski score"
     * ,CircularFingerprinter.CLASS_ECFP6,
     * 32768,5,"MLProbes-ECFP6-32768-xv5.bayesian");
     * runTest("MLProbes.sdf","Lipinski score"
     * ,CircularFingerprinter.CLASS_FCFP6,0,0,"MLProbes-FCFP6-0-loo.bayesian");
     * runTest
     * ("MLProbes.sdf","Lipinski score",CircularFingerprinter.CLASS_FCFP6,
     * 256,3,"MLProbes-FCFP6-256-xv3.bayesian"); } catch (CDKException ex) {
     * writeln("** Test failed **"); ex.printStackTrace(); return; }
     * writeln("Model test complete."); }
     */

    @Test
    public void testFingerprints() throws Exception {
        logger.info("Bayesian/Fingerprints test: verifying circular fingerprints for a single molecule");

        checkFP(REF_MOLECULE, CircularFingerprinter.CLASS_ECFP6, 0, REF_ECFP6_0);
        checkFP(REF_MOLECULE, CircularFingerprinter.CLASS_ECFP6, 1024, REF_ECFP6_1024);
    }

    @Test
    public void testAuxiliary() throws Exception {
        logger.info("Bayesian/Fingerprints test: making sure auxiliary fields are preserved");

        checkTextFields();
    }

    @Test
    public void testConfusion() throws Exception {
        logger.info("Bayesian/Fingerprints test: ensuring expected truth table for canned data");

        confirmPredictions("Tiny.sdf", 8, 8, 0, 0);
        confirmPredictions("Small.sdf", 6, 12, 0, 6);
    }

    @Test
    public void testFolding() throws Exception {
        logger.info("Bayesian/Fingerprints test: comparing folded fingerprints to reference set");

        compareFolding("FoldedProbes.sdf", "ECFP6/0", CircularFingerprinter.CLASS_ECFP6, 0);
        compareFolding("FoldedProbes.sdf", "ECFP6/1024", CircularFingerprinter.CLASS_ECFP6, 1024);
        compareFolding("FoldedProbes.sdf", "ECFP6/32768", CircularFingerprinter.CLASS_ECFP6, 32768);
        compareFolding("FoldedProbes.sdf", "FCFP6/0", CircularFingerprinter.CLASS_FCFP6, 0);
    }

    @Test
    @Category(SlowTest.class)
    public void testExample1() throws Exception {
        logger.info("Bayesian/Fingerprints test: using dataset of binding data to compare to reference data");

        runTest("Binders.sdf", "active", CircularFingerprinter.CLASS_ECFP6, 1024, 0, "Binders-ECFP6-1024-loo.bayesian");
        runTest("Binders.sdf", "active", CircularFingerprinter.CLASS_ECFP6, 32768, 5,
                "Binders-ECFP6-32768-xv5.bayesian");
        runTest("Binders.sdf", "active", CircularFingerprinter.CLASS_FCFP6, 0, 0, "Binders-FCFP6-0-loo.bayesian");
    }

    @Test
    @Category(SlowTest.class)
    public void testExample2() throws Exception {
        logger.info("Bayesian/Fingerprints test: using dataset of molecular probes to compare to reference data");

        runTest("MLProbes.sdf", "Lipinski score", CircularFingerprinter.CLASS_ECFP6, 1024, 0,
                "MLProbes-ECFP6-1024-loo.bayesian");
        runTest("MLProbes.sdf", "Lipinski score", CircularFingerprinter.CLASS_ECFP6, 32768, 5,
                "MLProbes-ECFP6-32768-xv5.bayesian");
        runTest("MLProbes.sdf", "Lipinski score", CircularFingerprinter.CLASS_FCFP6, 0, 0,
                "MLProbes-FCFP6-0-loo.bayesian");
        runTest("MLProbes.sdf", "Lipinski score", CircularFingerprinter.CLASS_FCFP6, 256, 3,
                "MLProbes-FCFP6-256-xv3.bayesian");
    }

    // ----------------- private methods -----------------

    // make sure that for a single molecule, the way that the hashes are created & folded is consistent with a reference
    private void checkFP(String molstr, int classType, int folding, int[] refHash) throws CDKException {
        String strType = classType == CircularFingerprinter.CLASS_ECFP6 ? "ECFP6" : "FCFP6";
        writeln("Comparing hash codes for " + strType + "/folding=" + folding);

        IAtomContainer mol = new IteratingSDFReader(new StringReader(molstr), DefaultChemObjectBuilder.getInstance())
                .next();
        Bayesian model = new Bayesian(classType, folding);
        model.addMolecule(mol, false);

        int[] calcHash = model.training.get(0);
        boolean same = calcHash.length == refHash.length;
        if (same) for (int n = 0; n < calcHash.length; n++)
            if (calcHash[n] != refHash[n]) {
                same = false;
                break;
            }
        if (!same) {
            writeln("    ** calculated: " + arrayStr(calcHash));
            writeln("    ** reference:  " + arrayStr(refHash));
            throw new CDKException("Hashes differ.");
        }
    }

    // make sure auxiliary fields like title & comments can serialise/deserialise
    private void checkTextFields() throws CDKException {
        writeln("Checking integrity of text fields");

        final String dummyTitle = "some title", dummyOrigin = "some origin";
        final String[] dummyComments = new String[]{"comment1", "comment2"};

        Bayesian model1 = new Bayesian(CircularFingerprinter.CLASS_ECFP6);
        model1.setNoteTitle(dummyTitle);
        model1.setNoteOrigin(dummyOrigin);
        model1.setNoteComments(dummyComments);

        Bayesian model2 = null;
        try {
            model2 = Bayesian.deserialise(model1.serialise());
        } catch (IOException ex) {
            throw new CDKException("Reserialisation failed", ex);
        }

        if (!dummyTitle.equals(model1.getNoteTitle()) || !dummyTitle.equals(model2.getNoteTitle())
                || !dummyOrigin.equals(model1.getNoteOrigin()) || !dummyOrigin.equals(model2.getNoteOrigin()))
            throw new CDKException("Note integrity failure for origin");

        String[] comments1 = model1.getNoteComments(), comments2 = model2.getNoteComments();
        if (comments1.length != dummyComments.length || comments2.length != dummyComments.length
                || !comments1[0].equals(dummyComments[0]) || !comments2[0].equals(dummyComments[0])
                || !comments1[1].equals(dummyComments[1]) || !comments2[1].equals(dummyComments[1]))
            throw new CDKException("Note integrity failure for origin");
    }

    // builds a model and uses the scaled predictions to rack up a confusion matrix, for comparison
    private void confirmPredictions(String sdfile, int truePos, int trueNeg, int falsePos, int falseNeg)
            throws CDKException {
        writeln("[" + sdfile + "] comparing confusion matrix");

        ArrayList<IAtomContainer> molecules = new ArrayList<IAtomContainer>();
        ArrayList<Boolean> activities = new ArrayList<Boolean>();
        Bayesian model = new Bayesian(CircularFingerprinter.CLASS_ECFP6, 1024);

        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("data/cdd/" + sdfile);
            IteratingSDFReader rdr = new IteratingSDFReader(in, DefaultChemObjectBuilder.getInstance());

            int row = 0, numActives = 0;
            while (rdr.hasNext()) {
                IAtomContainer mol = rdr.next();
                boolean actv = "true".equals((String) mol.getProperties().get("Active"));
                molecules.add(mol);
                activities.add(actv);
                model.addMolecule(mol, actv);
            }

            in.close();
        } catch (CDKException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CDKException("Test failed", ex);
        }

        model.build();
        model.validateLeaveOneOut();

        // build the confusion matrix
        int gotTP = 0, gotTN = 0, gotFP = 0, gotFN = 0;
        for (int n = 0; n < molecules.size(); n++) {
            double pred = model.scalePredictor(model.predict(molecules.get(n)));
            boolean actv = activities.get(n);
            if (pred >= 0.5) {
                if (actv)
                    gotTP++;
                else
                    gotFP++;
            } else {
                if (actv)
                    gotFN++;
                else
                    gotTN++;
            }
        }

        writeln("    True Positives:  got=" + gotTP + " require=" + truePos);
        writeln("         Negatives:  got=" + gotTN + " require=" + trueNeg);
        writeln("    False Positives: got=" + gotFP + " require=" + falsePos);
        writeln("          Negatives: got=" + gotFN + " require=" + falseNeg);

        if (gotTP != truePos || gotTN != trueNeg || gotFP != falsePos || gotFN != falseNeg)
            throw new CDKException("Confusion matrix mismatch");
    }

    // compares a series of molecules for folding fingerprints being literally identical
    private void compareFolding(String sdfile, String fpField, int classType, int folding) throws CDKException {
        writeln("[" + sdfile + "] calculation of: " + fpField);

        boolean failed = false;
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("data/cdd/" + sdfile);
            IteratingSDFReader rdr = new IteratingSDFReader(in, DefaultChemObjectBuilder.getInstance());

            int row = 0, numActives = 0;
            while (rdr.hasNext()) {
                IAtomContainer mol = rdr.next();
                row++;

                Bayesian model = new Bayesian(classType, folding);
                model.addMolecule(mol, false);
                int[] hashes = model.training.get(0);

                String gotHashes = arrayStr(hashes);
                String reqHashes = (String) mol.getProperties().get(fpField);

                if (!gotHashes.equals(reqHashes)) {
                    writeln("    ** mismatch at row " + row);
                    writeln("    ** calc: " + gotHashes);
                    writeln("    ** want: " + reqHashes);
                    failed = true;
                }
            }

            in.close();
        } catch (CDKException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CDKException("Test failed", ex);
        }

        if (failed) throw new CDKException("Folded hashes do not match reference.");
    }

    // performs a bulk test: loads an SDfile, builds a model with the given parameters, and compares it to a reference model
    // that has been previously serialised
    private void runTest(String sdfile, String actvField, int classType, int folding, int xval, String modelFN)
            throws CDKException {
        writeln("[" + modelFN + "]");
        writeln("    Loading " + sdfile);

        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("data/cdd/" + sdfile);
            IteratingSDFReader rdr = new IteratingSDFReader(in, DefaultChemObjectBuilder.getInstance());
            Bayesian model = new Bayesian(classType, folding);

            int row = 0, numActives = 0;
            while (rdr.hasNext()) {
                IAtomContainer mol = rdr.next();
                row++;

                String stractv = (String) mol.getProperties().get(actvField);
                int active = stractv.equals("true") ? 1 : stractv.equals("false") ? 0 : Integer.valueOf(stractv);
                if (active != 0 && active != 1) throw new CDKException("Activity field not found or invalid");

                model.addMolecule(mol, active == 1);
                numActives += active;
            }

            in.close();

            writeln("    Training with " + row + " rows, " + numActives + " actives, " + (row - numActives)
                    + " inactives");

            model.build();
            if (xval == 3)
                model.validateThreeFold();
            else if (xval == 5)
                model.validateFiveFold();
            else
                model.validateLeaveOneOut();

            writeln("    Validation: ROC AUC=" + model.getROCAUC());

            writeln("    Parsing reference model");

            //FileReader frdr=new FileReader(modelFN);
            InputStreamReader mrdr = new InputStreamReader(this.getClass().getClassLoader()
                    .getResourceAsStream("data/cdd/" + modelFN));
            Bayesian ref = Bayesian.deserialise(new BufferedReader(mrdr));
            mrdr.close();

            // start comparing the details...

            boolean failed = false;
            if (model.getFolding() != ref.getFolding()) {
                writeln("    ** reference folding size=" + ref.getFolding());
                failed = true;
            }
            if (model.getTrainingSize() != ref.getTrainingSize()) {
                writeln("    ** reference training size=" + ref.getTrainingSize());
                failed = true;
            }
            if (model.getTrainingActives() != ref.getTrainingActives()) {
                writeln("    ** reference training actives=" + ref.getTrainingActives());
                failed = true;
            }
            if (!model.getROCType().equals(ref.getROCType())) {
                writeln("    ** reference ROC type=" + ref.getROCType());
                failed = true;
            }
            if (!dblEqual(model.getROCAUC(), ref.getROCAUC())) {
                writeln("    ** reference ROC AUC=" + ref.getROCAUC());
                failed = true;
            }
            if (Math.abs(model.lowThresh - ref.lowThresh) > 0.00000000000001) {
                writeln("    ** reference lowThresh=" + ref.lowThresh + " different to calculated " + model.lowThresh);
                failed = true;
            }
            if (Math.abs(model.highThresh - ref.highThresh) > 0.00000000000001) {
                writeln("    ** reference highThresh=" + ref.highThresh + " different to calculated "
                        + model.highThresh);
                failed = true;
            }

            // make sure individual hash bit contributions match
            Map<Integer, Double> mbits = model.contribs, rbits = ref.contribs;
            if (mbits.size() != rbits.size()) {
                writeln("    ** model has " + mbits.size() + " contribution bits, reference has " + rbits.size());
                failed = true;
            }
            for (Integer h : mbits.keySet())
                if (!rbits.containsKey(h)) {
                    writeln("    ** model hash bit " + h + " not found in reference");
                    failed = true;
                    break; // one is enough
                }
            for (Integer h : rbits.keySet())
                if (!mbits.containsKey(h)) {
                    writeln("    ** reference hash bit " + h + " not found in model");
                    failed = true;
                    break; // one is enough
                }
            for (Integer h : mbits.keySet())
                if (rbits.containsKey(h)) {
                    double c1 = mbits.get(h), c2 = rbits.get(h);
                    if (!dblEqual(c1, c2)) {
                        writeln("    ** contribution for bit " + h + ": model=" + c1 + ", reference=" + c2);
                        failed = true;
                        break; // one is enough
                    }
                }

            if (failed) throw new CDKException("Comparison to reference failed");
        } catch (CDKException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CDKException("Test failed", ex);
        }
    }

    // convenience functions
    private void writeln(String str) {
        //System.out.println(str);
        logger.info(str);
    }

    private boolean dblEqual(double v1, double v2) {
        return v1 == v2 || Math.abs(v1 - v2) <= 1E-14 * Math.max(v1, v2);
    }

    private String arrayStr(int[] A) {
        if (A == null) return "{null}";
        String str = "";
        for (int n = 0; n < A.length; n++)
            str += (n > 0 ? "," : "") + A[n];
        return str;
    }
}
