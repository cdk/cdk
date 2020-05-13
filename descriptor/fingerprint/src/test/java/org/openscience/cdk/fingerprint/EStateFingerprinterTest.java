/* Copyright (C) 2008 Rajarshi Guha
 *               2009,2011 Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 * Contact: rajarshi@users.sourceforge.net
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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smarts.SmartsPattern;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @cdk.module test-fingerprint
 */
public class EStateFingerprinterTest extends AbstractFixedLengthFingerprinterTest {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(EStateFingerprinterTest.class);

    @Override
    public IFingerprinter getBitFingerprinter() {
        return new EStateFingerprinter();
    }

    @Test
    public void testGetSize() throws Exception {
        IFingerprinter printer = new EStateFingerprinter();
        Assert.assertEquals(79, printer.getSize());
    }

    @Test
    public void testFingerprint() throws Exception {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = new EStateFingerprinter();

        IBitFingerprint bs1 = printer.getBitFingerprint(parser.parseSmiles("C=C-C#N"));
        IBitFingerprint bs2 = printer.getBitFingerprint(parser.parseSmiles("C=CCC(O)CC#N"));

        Assert.assertEquals(79, printer.getSize());

        Assert.assertTrue(bs1.get(7));
        Assert.assertTrue(bs1.get(10));
        Assert.assertTrue(FingerprinterTool.isSubset(bs2.asBitSet(), bs1.asBitSet()));
    }

    /**
     * Using EState keys, these molecules are not considered substructures
     * and should only be used for similarity. This is because the EState
     * fragments match hydrogen counts.
     */
    @Test
    @Override
    public void testBug706786() throws Exception {

        IAtomContainer superStructure = bug706786_1();
        IAtomContainer subStructure = bug706786_2();

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(superStructure);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(subStructure);
        addImplicitHydrogens(superStructure);
        addImplicitHydrogens(subStructure);

        // SMARTS is now correct and D will include H atoms, CDK had this wrong
        // for years (had it has non-H count). Whilst you can set the optional
        // SMARTS flavor CDK_LEGACY this is not correct
        AtomContainerManipulator.suppressHydrogens(superStructure);
        AtomContainerManipulator.suppressHydrogens(subStructure);

        IFingerprinter fpr = new EStateFingerprinter();
        IBitFingerprint superBits = fpr.getBitFingerprint(superStructure);
        IBitFingerprint subBits = fpr.getBitFingerprint(subStructure);

        assertThat(superBits.asBitSet(), is(asBitSet(6, 11, 12, 15, 16, 18, 33, 34, 35)));
        assertThat(subBits.asBitSet(), is(asBitSet(8, 11, 16, 35)));
    }
}
