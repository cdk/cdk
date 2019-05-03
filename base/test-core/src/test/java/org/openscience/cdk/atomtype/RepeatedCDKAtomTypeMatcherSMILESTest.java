/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.atomtype;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * This class tests that a second atom typing results in the same atom
 * types as the first perception.
 *
 * @cdk.module test-core
 */
public class RepeatedCDKAtomTypeMatcherSMILESTest extends CDKTestCase {

    private static SmilesParser       smilesParser;
    private static CDKAtomTypeMatcher atomTypeMatcher;

    @BeforeClass
    public static void setup() {
        smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        atomTypeMatcher = CDKAtomTypeMatcher.getInstance(SilentChemObjectBuilder.getInstance());
    }

    @Test
    public void testSMILES() throws Exception {
        typeAndRetype("C=1N=CNC=1");
    }

    @Test
    public void testSMILES2() throws Exception {
        typeAndRetype("OCN1C=CN=C1");
    }

    @Test
    public void testSMILES3() throws Exception {
        typeAndRetype("OC(=O)N1C=CN=C1");
    }

    @Test
    public void testSMILES4() throws Exception {
        typeAndRetype("CN(C)CCC1=CNC2=C1C=C(C=C2)CC1NC(=O)OC1");
    }

    @Test
    public void testSMILES5() throws Exception {
        typeAndRetype("CN(C)CCC1=CNc2c1cc(cc2)CC1NC(=O)OC1");
    }

    @Test
    public void testSMILES6() throws Exception {
        typeAndRetype("c1c2cc[NH]cc2nc1");
    }

    @Test
    public void testSMILES7() throws Exception {
        typeAndRetype("c1cnc2s[cH][cH]n12");
    }

    @Test
    public void testSMILES8() throws Exception {
        typeAndRetype("Cl[Pt]1(Cl)(Cl)(Cl)NC2CCCCC2N1");
    }

    @Test
    public void testSMILES9() throws Exception {
        typeAndRetype("[Pt](Cl)(Cl)(N)N");
    }

    @Test
    public void testSMILES10() throws Exception {
        typeAndRetype("CN(C)(=O)CCC=C2c1ccccc1CCc3ccccc23");
    }

    @Test
    public void testSMILES11() throws Exception {
        typeAndRetype("CCCN1CC(CSC)CC2C1Cc3c[nH]c4cccc2c34");
    }

    private void typeAndRetype(String smiles) throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles(smiles);
        IAtomType[] types = atomTypeMatcher.findMatchingAtomTypes(mol);
        for (int i = 0; i < types.length; i++) {
            AtomTypeManipulator.configure(mol.getAtom(i), types[i]);
        }
        IAtomType[] retyped = atomTypeMatcher.findMatchingAtomTypes(mol);
        for (int i = 0; i < types.length; i++) {
            Assert.assertEquals("First perception resulted in " + types[i] + " but the second perception " + "gave "
                    + retyped[i], types[i], retyped[i]);
        }
        retyped = atomTypeMatcher.findMatchingAtomTypes(mol);
        for (int i = 0; i < types.length; i++) {
            Assert.assertEquals("First perception resulted in " + types[i] + " but the third perception " + "gave "
                    + retyped[i], types[i], retyped[i]);
        }
    }
}
