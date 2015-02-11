/* Copyright (c) 2014 Collaborative Drug Discovery, Inc. <alex@collaborativedrug.com>
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
 * All I ask is that proper credit is given for my work, which includes
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
package org.openscience.cdk.fingerprint;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.SlowTest;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.vecmath.Point2d;

/**
 * @cdk.module test-standard
 */
public class CircularFingerprinterTest extends CDKTestCase {

    private static ILoggingTool   logger     = LoggingToolFactory.createLoggingTool(CircularFingerprinterTest.class);

    private static IAtomContainer trivialMol = null;
    static {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        try {
            trivialMol = parser.parseSmiles("CCC(=O)N");
        } catch (InvalidSmilesException ex) {
        }
    }

    @Test
    @Category(SlowTest.class)
    public void testFingerprints() throws Exception {
        logger.info("CircularFingerprinter test: loading source materials");

        String fnzip = "data/cdd/circular_validation.zip";
        logger.info("Loading source content: " + fnzip);
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(fnzip);
        validate(in);
        in.close();

        logger.info("CircularFingerprinter test: completed without any problems");
    }

    @Test
    public void testGetBitFingerprint() throws Exception {
        assert (trivialMol != null);
        CircularFingerprinter circ = new CircularFingerprinter();
        IBitFingerprint result = circ.getBitFingerprint(trivialMol);

        BitSet wantBits = new BitSet(), gotBits = result.asBitSet();
        final int[] REQUIRE_BITS = {19, 152, 293, 340, 439, 480, 507, 726, 762, 947, 993};
        for (int b : REQUIRE_BITS)
            wantBits.set(b);
        if (!wantBits.equals(gotBits)) throw new CDKException("Got " + gotBits + ", wanted " + wantBits);
    }

    @Test
    public void testGetCountFingerprint() throws Exception {
        assert (trivialMol != null);
        CircularFingerprinter circ = new CircularFingerprinter();
        ICountFingerprint result = circ.getCountFingerprint(trivialMol);

        final int[] ANSWER_KEY = {-414937772, 1, -1027418143, 1, 1627608083, 1, -868007456, 1, -1006701866, 1,
                -1059145289, 1, -801752141, 1, 790592664, 1, -289109509, 1, -1650154758, 1, 1286833445, 1};

        int wantBits = ANSWER_KEY.length >> 1;
        boolean fail = result.numOfPopulatedbins() != wantBits;
        for (int n = 0; !fail && n < result.numOfPopulatedbins(); n++) {
            int gotHash = result.getHash(n), gotCount = result.getCount(n);
            boolean found = false;
            for (int i = 0; i < wantBits; i++) {
                int wantHash = ANSWER_KEY[i * 2], wantCount = ANSWER_KEY[i * 2 + 1];
                if (gotHash == wantHash) {
                    found = true;
                    if (gotCount != wantCount)
                        throw new CDKException("For hash " + gotHash + " got count " + gotCount + " but wanted "
                                + wantCount);
                }
            }
            if (!found) {
                fail = true;
                break;
            }
        }
        if (fail) throw new CDKException("Hash values do not match.");
    }

    @Test
    public void testGetRawFingerprint() throws Exception {
        // currently no-op
    }

    private void validate(InputStream in) throws Exception {
        ZipInputStream zip = new ZipInputStream(in);

        // stream the contents form the zipfile: these are all short
        HashMap<String, byte[]> content = new HashMap<String, byte[]>();
        while (true) {
            ZipEntry ze = zip.getNextEntry();
            if (ze == null) break;
            String fn = ze.getName();
            ByteArrayOutputStream buff = new ByteArrayOutputStream();
            while (true) {
                int b = zip.read();
                if (b < 0) break;
                buff.write(b);
            }
            content.put(fn, buff.toByteArray());
        }

        zip.close();

        for (int idx = 1;; idx++) {
            String basefn = String.valueOf(idx);
            while (basefn.length() < 6)
                basefn = "0" + basefn;
            byte[] molBytes = content.get(basefn + ".mol");
            if (molBytes == null) break;

            AtomContainer mol = new AtomContainer();
            MDLV2000Reader mdl = new MDLV2000Reader(new ByteArrayInputStream(molBytes));
            mdl.read(mol);
            mdl.close();

            CircularFingerprinter.FP[] validateECFP = parseValidation(content.get(basefn + ".ecfp"));
            CircularFingerprinter.FP[] validateFCFP = parseValidation(content.get(basefn + ".fcfp"));

            logger.info("FN=" + basefn + " MOL=" + mol.getAtomCount() + "," + mol.getBondCount() + " Requires ECFP="
                    + validateECFP.length + " FCFP=" + validateFCFP.length);

            validateFingerprints("ECFP6", mol, CircularFingerprinter.CLASS_ECFP6, validateECFP);
            validateFingerprints("FCFP6", mol, CircularFingerprinter.CLASS_FCFP6, validateFCFP);
        }
    }

    private CircularFingerprinter.FP[] parseValidation(byte[] raw) throws Exception {
        InputStream in = new ByteArrayInputStream(raw);
        BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
        ArrayList<CircularFingerprinter.FP> list = new ArrayList<CircularFingerprinter.FP>();

        while (true) {
            String line = rdr.readLine();
            if (line == null || line.length() == 0) break;
            String[] bits = line.split(" ");
            int hashCode = Integer.parseInt(bits[0]);
            int iteration = Integer.parseInt(bits[1]);
            int[] atoms = new int[bits.length - 2];
            for (int n = 0; n < atoms.length; n++)
                atoms[n] = Integer.parseInt(bits[n + 2]) - 1; // note: atom#'s are 1-based in reference file
            list.add(new CircularFingerprinter.FP(hashCode, iteration, atoms));
        }

        rdr.close();
        return list.toArray(new CircularFingerprinter.FP[list.size()]);
    }

    private void validateFingerprints(String label, AtomContainer mol, int classType,
            CircularFingerprinter.FP[] validate) throws Exception {
        CircularFingerprinter circ = new CircularFingerprinter(classType);
        try {
            circ.calculate(mol);
        } catch (Exception ex) {
            System.out.println("Fingerprint calculation failed for molecule:");
            MDLV2000Writer molwr = new MDLV2000Writer(System.out);
            molwr.write(mol);
            molwr.close();
            throw ex;
        }

        CircularFingerprinter.FP[] obtained = new CircularFingerprinter.FP[circ.getFPCount()];
        for (int n = 0; n < circ.getFPCount(); n++)
            obtained[n] = circ.getFP(n);

        boolean same = obtained.length == validate.length;
        for (int i = 0; i < obtained.length && same; i++) {
            boolean hit = false;
            for (int j = 0; j < validate.length; j++)
                if (equalFingerprints(obtained[i], validate[j])) {
                    hit = true;
                    break;
                }
            if (!hit) same = false;
        }
        for (int i = 0; i < validate.length && same; i++) {
            boolean hit = false;
            for (int j = 0; j < obtained.length; j++)
                if (equalFingerprints(validate[i], obtained[j])) {
                    hit = true;
                    break;
                }
            if (!hit) same = false;
        }
        if (same) return;

        System.out.println("Fingerprint mismatch, validation failed.\nMolecular structure");
        MDLV2000Writer molwr = new MDLV2000Writer(System.out);
        molwr.write(mol);
        molwr.close();

        System.out.println("Obtained fingerprints:");
        for (int n = 0; n < obtained.length; n++)
            System.out.println((n + 1) + "/" + obtained.length + ": " + formatFP(obtained[n]));
        System.out.println("Validation fingerprints:");
        for (int n = 0; n < validate.length; n++)
            System.out.println((n + 1) + "/" + validate.length + ": " + formatFP(validate[n]));

        throw new CDKException("Fingerprint comparison failed.");
    }

    private boolean equalFingerprints(CircularFingerprinter.FP fp1, CircularFingerprinter.FP fp2) {
        if (fp1.hashCode != fp2.hashCode || fp1.iteration != fp2.iteration || fp1.atoms.length != fp2.atoms.length)
            return false;
        for (int n = 0; n < fp1.atoms.length; n++)
            if (fp1.atoms[n] != fp2.atoms[n]) return false;
        return true;
    }

    private String formatFP(CircularFingerprinter.FP fp) {
        String str = "[" + fp.hashCode + "] iter=" + fp.iteration + " atoms={";
        for (int n = 0; n < fp.atoms.length; n++)
            str += (n > 0 ? "," : "") + fp.atoms[n];
        return str + "}";
    }

    @Test
    public void protonsDontCauseNPE() throws Exception {
        IAtomContainer proton = new AtomContainer(1, 0, 0, 0);
        proton.addAtom(atom("H", +1, 0));
        CircularFingerprinter circ = new CircularFingerprinter(CircularFingerprinter.CLASS_FCFP2);
        assertThat(circ.getBitFingerprint(proton).cardinality(), is(0));
    }

    @Test
    public void iminesDetectionDoesntCauseNPE() throws Exception {
        IAtomContainer pyrazole = new AtomContainer(6, 6, 0, 0);
        pyrazole.addAtom(atom("H", 0, 0));
        pyrazole.addAtom(atom("N", 0, 0));
        pyrazole.addAtom(atom("C", 0, 1));
        pyrazole.addAtom(atom("C", 0, 1));
        pyrazole.addAtom(atom("C", 0, 1));
        pyrazole.addAtom(atom("N", 0, 0));
        pyrazole.addBond(0, 1, IBond.Order.SINGLE);
        pyrazole.addBond(1, 2, IBond.Order.SINGLE);
        pyrazole.addBond(2, 3, IBond.Order.DOUBLE);
        pyrazole.addBond(3, 4, IBond.Order.SINGLE);
        pyrazole.addBond(4, 5, IBond.Order.DOUBLE);
        pyrazole.addBond(1, 5, IBond.Order.SINGLE);
        CircularFingerprinter circ = new CircularFingerprinter(CircularFingerprinter.CLASS_FCFP2);
        assertNotNull(circ.getBitFingerprint(pyrazole));
    }

    /**
     * @cdk.bug 1357
     */
    @Test
    public void partialCoordinatesDontCauseNPE() throws Exception {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("C", 3, 0.000, 0.000));
        m.addAtom(atom("C", 0, 1.299, -0.750));
        m.addAtom(atom("H", 0, 0));
        m.addAtom(atom("O", 0, 1));
        m.addAtom(atom("C", 2, 2.598, -0.000));
        m.addAtom(atom("C", 3, 3.897, -0.750));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(1, 3, IBond.Order.SINGLE, IBond.Stereo.DOWN);
        m.addBond(1, 4, IBond.Order.SINGLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        CircularFingerprinter circ = new CircularFingerprinter(CircularFingerprinter.CLASS_ECFP6);
        assertNotNull(circ.getBitFingerprint(m));
    }
    
    static IAtom atom(String symbol, int q, int h) {
        IAtom a = new Atom(symbol);
        a.setFormalCharge(q);
        a.setImplicitHydrogenCount(h);
        return a;
    }

    static IAtom atom(String symbol, int h, double x, double y) {
        IAtom a = new Atom(symbol);
        a.setPoint2d(new Point2d(x,y));
        a.setImplicitHydrogenCount(h);
        return a;
    }

}
