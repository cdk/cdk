/* Copyright (C) 1997-2009,2011  Egon Willighagen <egonw@users.sf.net>
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

import java.io.InputStream;
import java.util.BitSet;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLV2000Reader;

/**
 * @cdk.module test-standard
 */
public class AbstractFixedLengthFingerprinterTest extends AbstractFingerprinterTest {

    /**
     * @cdk.bug 706786
     */
    @Test public void testBug706786() throws Exception {
        String filename = "data/mdl/bug706786-1.mol";
        InputStream ins =
            this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IMolecule superstructure = (IMolecule)reader.read(new Molecule());

        filename = "data/mdl/bug706786-2.mol";
        ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        reader = new MDLV2000Reader(ins, Mode.STRICT);
        IMolecule substructure = (IMolecule)reader.read(new Molecule());

        IFingerprinter fingerprinter = getFingerprinter();
        BitSet superBS = fingerprinter.getFingerprint(superstructure);
        BitSet subBS = fingerprinter.getFingerprint(substructure);
        boolean isSubset = FingerprinterTool.isSubset(superBS, subBS);
        Assert.assertTrue(isSubset);
    }

    /**
     * @cdk.bug 853254
     */
    @Test public void testBug853254() throws Exception {
        String filename = "data/mdl/bug853254-2.mol";
        InputStream ins =
            this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IMolecule superstructure = (IMolecule)reader.read(new Molecule());

        filename = "data/mdl/bug853254-1.mol";
        ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        reader = new MDLV2000Reader(ins, Mode.STRICT);
        IMolecule substructure = (IMolecule)reader.read(new Molecule());

        IFingerprinter fingerprinter = getFingerprinter();
        BitSet superBS = fingerprinter.getFingerprint(superstructure);
        BitSet subBS = fingerprinter.getFingerprint(substructure);
        boolean isSubset = FingerprinterTool.isSubset(superBS, subBS);
        Assert.assertTrue(isSubset);
    }

    /**
     * Fingerprint not subset.
     *
     * @cdk.bug 934819
     */
    @Test public void testBug934819() throws Exception {
        String filename = "data/mdl/bug934819-1.mol";
        InputStream ins =
            this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IMolecule substructure = (IMolecule)reader.read(new Molecule());

        filename = "data/mdl/bug934819-2.mol";
        ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        reader = new MDLV2000Reader(ins, Mode.STRICT);
        IMolecule superstructure = reader.read(new Molecule());

        IFingerprinter fingerprinter = getFingerprinter();
        BitSet superBS = fingerprinter.getFingerprint(superstructure);
        BitSet subBS = fingerprinter.getFingerprint(substructure);
        boolean isSubset = FingerprinterTool.isSubset(superBS, subBS);
        Assert.assertTrue(isSubset);
    }

    /**
     * Problems with different aromaticity concepts.
     *
     * @cdk.bug 771485
     */
    @Test public void testBug771485() throws Exception {
        String filename = "data/mdl/bug771485-1.mol";
        InputStream ins =
            this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IMolecule structure1 = (IMolecule)reader.read(new Molecule());

        filename = "data/mdl/bug771485-2.mol";
        ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        reader = new MDLV2000Reader(ins, Mode.STRICT);
        IMolecule structure2 = (IMolecule)reader.read(new Molecule());

        IFingerprinter fingerprinter = getFingerprinter();
        BitSet superBS = fingerprinter.getFingerprint(structure2);
        BitSet subBS = fingerprinter.getFingerprint(structure1);
        boolean isSubset = FingerprinterTool.isSubset(superBS, subBS);
        Assert.assertTrue(isSubset);
    }

    /**
     * Fingerprinter gives different fingerprints for same molecule.
     *
     * @cdk.bug 931608
     * @cdk.bug 934819
     */
    @Test public void testBug931608() throws Exception {
        String filename = "data/mdl/bug931608-1.mol";
        InputStream ins =
            this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IMolecule structure1 = (IMolecule)reader.read(new Molecule());

        filename = "data/mdl/bug931608-2.mol";
        ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        reader = new MDLV2000Reader(ins, Mode.STRICT);
        IMolecule structure2 = (IMolecule)reader.read(new Molecule());

        IFingerprinter fingerprinter = getFingerprinter();
        BitSet bs1 = fingerprinter.getFingerprint(structure1);
        BitSet bs2 = fingerprinter.getFingerprint(structure2);
        // now we do the boolean XOR on the two bitsets, leading
        // to a bitset that has all the bits set to "true" which differ
        // between the two original bitsets
        bs1.xor(bs2);
        // cardinality gives us the number of "true" bits in the
        // result of the XOR operation.
        int cardinality = bs1.cardinality();
        Assert.assertEquals(0, cardinality);
    }

}

