/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openscience.cdk.Atom;
import org.openscience.cdk.SlowTest;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.AtomTypeTools;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Checks the functionality of the AtomType-MMFF2AtomTypeMatcher.
 *
 * @cdk.module test-extra
 *
 * @see MM2AtomTypeMatcher
 */
@Category(SlowTest.class)
public class MM2AtomTypeMatcherTest extends AbstractAtomTypeTest {

    private static ILoggingTool         logger          = LoggingToolFactory
                                                                .createLoggingTool(MM2AtomTypeMatcherTest.class);
    private static IAtomContainer       testMolecule    = null;

    private static Map<String, Integer> testedAtomTypes = new HashMap<String, Integer>();

    @BeforeClass
    public static void setUp() throws Exception {
        if (testMolecule == null) {
            // read the test file and percieve atom types
            AtomTypeTools att = new AtomTypeTools();
            MM2AtomTypeMatcher atm = new MM2AtomTypeMatcher();
            logger.debug("**** reading MOL file ******");
            InputStream ins = MM2AtomTypeMatcher.class.getClassLoader().getResourceAsStream(
                    "data/mdl/mmff94AtomTypeTest_molecule.mol");
            ISimpleChemObjectReader mdl = new MDLV2000Reader(ins);
            testMolecule = mdl.read(new AtomContainer());
            logger.debug("Molecule load:" + testMolecule.getAtomCount());
            att.assignAtomTypePropertiesToAtom(testMolecule);
            for (int i = 0; i < testMolecule.getAtomCount(); i++) {
                logger.debug("atomNr:" + i);
                IAtomType matched = null;
                matched = atm.findMatchingAtomType(testMolecule, testMolecule.getAtom(i));
                logger.debug("Found AtomType: ", matched);
                AtomTypeManipulator.configure(testMolecule.getAtom(i), matched);
            }
        }
    }

    @Test
    public void testMMFF94AtomTypeMatcher() {
        MM2AtomTypeMatcher matcher = new MM2AtomTypeMatcher();
        Assert.assertNotNull(matcher);
    }

    @Test
    public void testFindMatchingAtomType_IAtomContainer() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        final IAtomType.Hybridization thisHybridization = IAtomType.Hybridization.SP3;
        atom.setHybridization(thisHybridization);
        mol.addAtom(atom);

        // just check consistency; other methods do perception testing
        MM2AtomTypeMatcher matcher = new MM2AtomTypeMatcher();
        AtomTypeTools att = new AtomTypeTools();
        att.assignAtomTypePropertiesToAtom(mol);
        IAtomType[] types = matcher.findMatchingAtomTypes(mol);
        for (int i = 0; i < types.length; i++) {
            IAtomType type = matcher.findMatchingAtomType(mol, mol.getAtom(i));
            Assert.assertEquals(type.getAtomTypeName(), types[i].getAtomTypeName());
        }
    }

    @Test
    public void testFindMatchingAtomType_IAtomContainer_IAtom() throws Exception {
        for (int i = 0; i < testMolecule.getAtomCount(); i++) {
            Assert.assertNotNull(testMolecule.getAtom(i).getAtomTypeName());
            Assert.assertTrue(testMolecule.getAtom(i).getAtomTypeName().length() > 0);
        }
    }

    // FIXME: Below should be tests for *all* atom types in the MM2 atom type specificiation

    @Test
    public void testSthi() {
        assertAtomType(testedAtomTypes, "Sthi", testMolecule.getAtom(0));
    }

    @Test
    public void testCsp2() {
        assertAtomType(testedAtomTypes, "Csp2", testMolecule.getAtom(7));
    }

    @Test
    public void testCsp() {
        assertAtomType(testedAtomTypes, "Csp", testMolecule.getAtom(51));
    }

    @Test
    public void testNdbC() {
        assertAtomType(testedAtomTypes, "N=C", testMolecule.getAtom(148));
    }

    @Test
    public void testOar() {
        assertAtomType(testedAtomTypes, "Oar", testMolecule.getAtom(198));
    }

    @Test
    public void testN2OX() {
        assertAtomType(testedAtomTypes, "N2OX", testMolecule.getAtom(233));
    }

    @Test
    public void testNsp2() {
        assertAtomType(testedAtomTypes, "Nsp2", testMolecule.getAtom(256));
    }

    /**
     * The test seems to be run by JUnit in the order in which they are found
     * in the source. Ugly, but @AfterClass does not work because that
     * method cannot Assert.assert anything.
     */
    @Ignore("Atom type matcher is incomplete")
    public void countTestedAtomTypes() {
        AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/mm2_atomtypes.xml",
                SilentChemObjectBuilder.getInstance());

        IAtomType[] expectedTypes = factory.getAllAtomTypes();
        if (expectedTypes.length != testedAtomTypes.size()) {
            String errorMessage = "Atom types not tested:";
            for (int i = 0; i < expectedTypes.length; i++) {
                if (!testedAtomTypes.containsKey(expectedTypes[i].getAtomTypeName()))
                    errorMessage += " " + expectedTypes[i].getAtomTypeName();
            }
            Assert.assertEquals(errorMessage, factory.getAllAtomTypes().length, testedAtomTypes.size());
        }
    }

    @Override
    public String getAtomTypeListName() {
        return "mm2";
    }

    @Override
    public AtomTypeFactory getFactory() {
        return AtomTypeFactory.getInstance("org/openscience/cdk/config/data/mm2_atomtypes.xml",
                SilentChemObjectBuilder.getInstance());
    }

    @Override
    public IAtomTypeMatcher getAtomTypeMatcher(IChemObjectBuilder builder) {
        return new MM2AtomTypeMatcher();
    }
}
