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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.openscience.cdk.Atom;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.test.atomtype.AbstractAtomTypeTest;
import org.openscience.cdk.tools.AtomTypeTools;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * Checks the functionality of the AtomType-MMFF2AtomTypeMatcher.
 *
 * @cdk.module test-extra
 *
 * @see MM2AtomTypeMatcher
 */
@Tag("SlowTest")
class MM2AtomTypeMatcherTest extends AbstractAtomTypeTest {

    private static final ILoggingTool         logger          = LoggingToolFactory
                                                                .createLoggingTool(MM2AtomTypeMatcherTest.class);
    private static IAtomContainer       testMolecule    = null;

    private static final Map<String, Integer> testedAtomTypes = new HashMap<>();

    @BeforeAll
    static void setUp() throws Exception {
        if (testMolecule == null) {
            // read the test file and percieve atom types
            AtomTypeTools att = new AtomTypeTools();
            MM2AtomTypeMatcher atm = new MM2AtomTypeMatcher();
            logger.debug("**** reading MOL file ******");
            InputStream ins = MM2AtomTypeMatcher.class.getResourceAsStream(
                    "mmff94AtomTypeTest_molecule.mol");
            ISimpleChemObjectReader mdl = new MDLV2000Reader(ins);
            testMolecule = mdl.read(new AtomContainer());
            logger.debug("Molecule load:" + testMolecule.getAtomCount());
            att.assignAtomTypePropertiesToAtom(testMolecule);
            for (int i = 0; i < testMolecule.getAtomCount(); i++) {
                logger.debug("atomNr:" + i);
                IAtomType matched;
                matched = atm.findMatchingAtomType(testMolecule, testMolecule.getAtom(i));
                logger.debug("Found AtomType: ", matched);
                AtomTypeManipulator.configure(testMolecule.getAtom(i), matched);
            }
        }
    }

    @Test
    void testMMFF94AtomTypeMatcher() {
        MM2AtomTypeMatcher matcher = new MM2AtomTypeMatcher();
        Assertions.assertNotNull(matcher);
    }

    @Test
    void testFindMatchingAtomType_IAtomContainer() throws Exception {
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
            Assertions.assertEquals(type.getAtomTypeName(), types[i].getAtomTypeName());
        }
    }

    @Test
    void testFindMatchingAtomType_IAtomContainer_IAtom() throws Exception {
        for (int i = 0; i < testMolecule.getAtomCount(); i++) {
            Assertions.assertNotNull(testMolecule.getAtom(i).getAtomTypeName());
            Assertions.assertTrue(testMolecule.getAtom(i).getAtomTypeName().length() > 0);
        }
    }

    // FIXME: Below should be tests for *all* atom types in the MM2 atom type specificiation

    @Test
    void testSthi() {
        assertAtomType(testedAtomTypes, "Sthi", testMolecule.getAtom(0));
    }

    @Test
    void testCsp2() {
        assertAtomType(testedAtomTypes, "Csp2", testMolecule.getAtom(7));
    }

    @Test
    void testCsp() {
        assertAtomType(testedAtomTypes, "Csp", testMolecule.getAtom(51));
    }

    @Test
    void testNdbC() {
        assertAtomType(testedAtomTypes, "N=C", testMolecule.getAtom(148));
    }

    @Test
    void testOar() {
        assertAtomType(testedAtomTypes, "Oar", testMolecule.getAtom(198));
    }

    @Test
    void testN2OX() {
        assertAtomType(testedAtomTypes, "N2OX", testMolecule.getAtom(233));
    }

    @Test
    void testNsp2() {
        assertAtomType(testedAtomTypes, "Nsp2", testMolecule.getAtom(256));
    }

    /**
     * The test seems to be run by JUnit in the order in which they are found
     * in the source. Ugly, but @AfterClass does not work because that
     * method cannot Assert.assert anything.
     */
    @Disabled("Atom type matcher is incomplete")
    void countTestedAtomTypes() {
        AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/mm2_atomtypes.xml",
                SilentChemObjectBuilder.getInstance());

        IAtomType[] expectedTypes = factory.getAllAtomTypes();
        if (expectedTypes.length != testedAtomTypes.size()) {
            String errorMessage = "Atom types not tested:";
            for (IAtomType expectedType : expectedTypes) {
                if (!testedAtomTypes.containsKey(expectedType.getAtomTypeName()))
                    errorMessage += " " + expectedType.getAtomTypeName();
            }
            Assertions.assertEquals(factory.getAllAtomTypes().length, testedAtomTypes.size(), errorMessage);
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
