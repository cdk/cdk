/* Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@slists.sourceforge.net
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
 *  */
package org.openscience.cdk.io;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.test.io.SimpleChemObjectReaderTest;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * TestCase for the reading MDL V3000 mol files.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.MDLReader
 * @see org.openscience.cdk.io.SDFReaderTest
 */
class MDLV3000ReaderTest extends SimpleChemObjectReaderTest {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(MDLV3000ReaderTest.class);
    // used in several test methods to match query bonds against them
    private IBond singleBond, doubleBond, tripleBond, aromaticBond;

    @BeforeAll
    static void setup() throws Exception {
        setSimpleChemObjectReader(new MDLV3000Reader(), "org/openscience/cdk/io/iterator/molV3000.mol");
    }

    @BeforeEach
    void setupBondsToMatch() {
        singleBond = SilentChemObjectBuilder.getInstance().newBond();
        singleBond.setOrder(IBond.Order.SINGLE);
        doubleBond = SilentChemObjectBuilder.getInstance().newBond();
        doubleBond.setOrder(IBond.Order.DOUBLE);
        tripleBond = SilentChemObjectBuilder.getInstance().newBond();
        tripleBond.setOrder(IBond.Order.TRIPLE);
        aromaticBond = SilentChemObjectBuilder.getInstance().newBond();
        aromaticBond.setOrder(IBond.Order.UNSET);
        aromaticBond.setFlag(IChemObject.AROMATIC, true);
        aromaticBond.setFlag(IChemObject.SINGLE_OR_DOUBLE, true);
    }

    @Test
    void testAccepts() {
        MDLV3000Reader reader = new MDLV3000Reader();
        Assertions.assertTrue(reader.accepts(AtomContainer.class));
    }

    /**
     * @cdk.bug 1571207
     */
    @Test
    void testBug1571207() throws Exception {
        String filename = "iterator/molV3000.mol";
        logger.info("Testing: " + filename);
        try (InputStream ins = this.getClass().getResourceAsStream(filename);
             MDLV3000Reader reader = new MDLV3000Reader(ins)) {
            IAtomContainer m = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            reader.close();
            Assertions.assertNotNull(m);
            Assertions.assertEquals(31, m.getAtomCount());
            Assertions.assertEquals(34, m.getBondCount());

            IAtom atom = m.getAtom(0);
            Assertions.assertNotNull(atom);
            Assertions.assertNotNull(atom.getPoint2d());
            Assertions.assertEquals(10.4341, atom.getPoint2d().x, 0.0001);
            Assertions.assertEquals(5.1053, atom.getPoint2d().y, 0.0001);
        }
    }

    @Test
    void testEmptyString() throws Exception {
        String emptyString = "";
        try (MDLV3000Reader reader = new MDLV3000Reader(new StringReader(emptyString))) {
            reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            reader.close();
            Assertions.fail("Should have received a CDK Exception");
        } catch (CDKException cdkEx) {
            Assertions.assertEquals("Expected a header line, but found nothing.", cdkEx.getMessage());
        }
    }

    @Test
    void testPseudoAtomLabels() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("pseudoatomsv3000.mol");
        MDLV3000Reader reader = new MDLV3000Reader(in)) {
            IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
            molecule = reader.read(molecule);
            reader.close();
            Assertions.assertInstanceOf(IPseudoAtom.class, molecule.getAtom(9));
            Assertions.assertEquals("R", molecule.getAtom(9).getSymbol());
            IPseudoAtom pa = (IPseudoAtom) molecule.getAtom(9);
            Assertions.assertEquals("Leu", pa.getLabel());
        }
    }

    @Test
    void pseudoAtomReplacement() throws Exception {
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("pseudoAtomReplacement.mol"))) {
            IAtomContainer container = reader.read(DefaultChemObjectBuilder.getInstance().newAtomContainer());
            for (IAtom atom : container.getBond(9).atoms()) {
                Assertions.assertTrue(container.contains(atom));
            }
        }
    }

    @Test
    void positionalVariation() throws Exception {
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("multicenterBond.mol"))) {
            IAtomContainer container = reader.read(DefaultChemObjectBuilder.getInstance().newAtomContainer());
            assertThat(container.getBondCount(), is(8));
            List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);
            Assertions.assertNotNull(sgroups);
            assertThat(sgroups.size(), is(1));
            assertThat(sgroups.get(0).getType(), is(SgroupType.ExtMulticenter));
        }
    }

    @Test
    void radicalsInCH3() throws Exception {
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("CH3.mol"))) {
            IAtomContainer container = reader.read(DefaultChemObjectBuilder.getInstance().newAtomContainer());
            assertThat(container.getSingleElectronCount(), is(1));
            assertThat(container.getAtom(0).getImplicitHydrogenCount(), is(3));
        }
    }

    @Test
    void issue602() throws Exception {
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("issue602.mol"))) {
            IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            assertThat(mol.getAtomCount(), CoreMatchers.is(31));
        }
    }

    @Test
    void forceReadAs3D() throws Exception {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("issue602.mol"))) {
            IAtomContainer mol = reader.read(builder.newAtomContainer());
            Assertions.assertEquals(31, mol.getAtomCount());
            for (IAtom atom : mol.atoms()) {
                Assertions.assertNotNull(atom.getPoint2d());
                Assertions.assertNull(atom.getPoint3d());
            }
        }

        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("issue602.mol"))) {
            reader.getSetting("ForceReadAs3DCoordinates").setSetting("true");
            IAtomContainer mol = reader.read(builder.newAtomContainer());
            Assertions.assertEquals(31, mol.getAtomCount());
            for (IAtom atom : mol.atoms()) {
                Assertions.assertNull(atom.getPoint2d());
                Assertions.assertNotNull(atom.getPoint3d());
            }
        }
    }

    @Test
    void massSpecification() throws Exception {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("(methyl-13C)-cyclohexane.mdl3"))) {
            IAtomContainer mol = reader.read(builder.newAtomContainer());
            IAtom atom = mol.getAtom(6);
            Assertions.assertNotNull(atom.getMassNumber());
            Assertions.assertEquals(13, (int)atom.getMassNumber());
        }
    }

    @Test
    void hydIsotopes() throws Exception {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("cyclohexane-d6.mdl3"))) {
            IAtomContainer mol = reader.read(builder.newAtomContainer());
            int nDeuterium = 0;
            for (IAtom atom : mol.atoms()) {
                if (atom.getAtomicNumber() == IElement.H) {
                    if (atom.getMassNumber() != null && atom.getMassNumber() == 2)
                        nDeuterium++;
                }
            }
            Assertions.assertEquals(6, nDeuterium);
        }
    }

    // InterpretHydrogenIsotopes=false means D comes in as an IPseudoAtom
    @Test
    void hydIsotopesAsPseudo() throws Exception {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("cyclohexane-d6.mdl3"))) {
            reader.getSetting("InterpretHydrogenIsotopes").setSetting("false");
            IAtomContainer mol = reader.read(builder.newAtomContainer());
            int nDeuterium = 0;
            for (IAtom atom : mol.atoms()) {
                if (atom.getAtomicNumber() == IElement.H) {
                    if (atom.getMassNumber() != null && atom.getMassNumber() == 2)
                        nDeuterium++;
                }
            }
            Assertions.assertEquals(0, nDeuterium);
        }
    }

    @Test
    void fluoroethane_test() throws Exception {
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("fluoroethane_v3000.mol"))) {
            final IAtomContainer atomContainer = reader.readMolecule(SilentChemObjectBuilder.getInstance());
            assertThat(atomContainer.getAtomCount(), is(3));
            assertThat(atomContainer.getBondCount(), is(2));
        }
    }

    /**
     * @cdk.bug https://github.com/cdk/cdk/issues/664
     */
    @Test
    void bondType4_aromaticBond_test() throws Exception {
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("bondType4_aromaticBond_v3000.mol"))) {
            final IAtomContainer container = reader.readMolecule(SilentChemObjectBuilder.getInstance());
            for (IBond bond: container.bonds()) {
                assertThat(bond.getOrder(), notNullValue());
                assertThat(bond.getOrder(), is(IBond.Order.UNSET));
                assertThat(bond.getFlag(IChemObject.AROMATIC), is(true));
                assertThat(bond.getFlag(IChemObject.SINGLE_OR_DOUBLE), is(true));
            }
        }
    }

    @Test
    void bondType5_singleOrDouble_test() throws Exception {
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("bondType5_singleOrDouble_v3000.mol"))) {
            final IAtomContainer atomContainer = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            // atom container assertions
            assertThat(atomContainer.getClass().getSimpleName(), is("QueryAtomContainer"));
            // atom assertions
            assertThat(atomContainer.getAtomCount(), is(3));
            for (IAtom atom: atomContainer.atoms()) {
                assertThat(atom, instanceOf(IAtom.class));
            }
            // bond assertions
            assertThat(atomContainer.getBondCount(), is(2));
            Assertions.assertInstanceOf(IBond.class, atomContainer.getBond(0));
            Assertions.assertInstanceOf(IQueryBond.class, atomContainer.getBond(1));
            Assertions.assertTrue(((IQueryBond) atomContainer.getBond(1)).matches(singleBond));
            Assertions.assertTrue(((IQueryBond) atomContainer.getBond(1)).matches(doubleBond));
            Assertions.assertFalse(((IQueryBond) atomContainer.getBond(1)).matches(tripleBond));
            Assertions.assertFalse(((IQueryBond) atomContainer.getBond(1)).matches(aromaticBond));
        }
    }

    @Test
    void bondType6_singleOrAromatic_test() throws Exception {
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("bondType6_singleOrAromatic_v3000.mol"))) {
            final IAtomContainer atomContainer = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            // atom container assertions
            assertThat(atomContainer.getClass().getSimpleName(), is("QueryAtomContainer"));
            // atom assertions
            assertThat(atomContainer.getAtomCount(), is(3));
            for (IAtom atom: atomContainer.atoms()) {
                assertThat(atom, instanceOf(IAtom.class));
            }
            // bond assertions
            assertThat(atomContainer.getBondCount(), is(2));
            Assertions.assertInstanceOf(IBond.class, atomContainer.getBond(0));
            Assertions.assertInstanceOf(IQueryBond.class, atomContainer.getBond(1));
            Assertions.assertTrue(((IQueryBond) atomContainer.getBond(1)).matches(singleBond));
            Assertions.assertFalse(((IQueryBond) atomContainer.getBond(1)).matches(doubleBond));
            Assertions.assertFalse(((IQueryBond) atomContainer.getBond(1)).matches(tripleBond));
            Assertions.assertTrue(((IQueryBond) atomContainer.getBond(1)).matches(aromaticBond));
        }
    }

    @Test
    void bondType7_doubleOrAromatic_test() throws Exception {
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("bondType7_doubleOrAromatic_v3000.mol"))) {
            final IAtomContainer atomContainer = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            // atom container assertions
            assertThat(atomContainer.getClass().getSimpleName(), is("QueryAtomContainer"));
            // atom assertions
            assertThat(atomContainer.getAtomCount(), is(3));
            for (IAtom atom: atomContainer.atoms()) {
                assertThat(atom, instanceOf(IAtom.class));
            }
            // bond assertions
            assertThat(atomContainer.getBondCount(), is(2));
            Assertions.assertInstanceOf(IBond.class, atomContainer.getBond(0));
            Assertions.assertInstanceOf(IQueryBond.class, atomContainer.getBond(1));
            Assertions.assertFalse(((IQueryBond) atomContainer.getBond(1)).matches(singleBond));
            Assertions.assertTrue(((IQueryBond) atomContainer.getBond(1)).matches(doubleBond));
            Assertions.assertFalse(((IQueryBond) atomContainer.getBond(1)).matches(tripleBond));
            Assertions.assertTrue(((IQueryBond) atomContainer.getBond(1)).matches(aromaticBond));
        }
    }


    @Test
    void bondType8_anyBond_test() throws Exception {
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("bondType8_anyBond_v3000.mol"))) {
            final IAtomContainer atomContainer = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            // atom container assertions
            assertThat(atomContainer.getClass().getSimpleName(), is("QueryAtomContainer"));
            // atom assertions
            assertThat(atomContainer.getAtomCount(), is(3));
            for (IAtom atom: atomContainer.atoms()) {
                assertThat(atom, instanceOf(IAtom.class));
            }
            // bond assertions
            assertThat(atomContainer.getBondCount(), is(2));
            Assertions.assertInstanceOf(IBond.class, atomContainer.getBond(0));
            Assertions.assertInstanceOf(IQueryBond.class, atomContainer.getBond(1));
            Assertions.assertTrue(((IQueryBond) atomContainer.getBond(1)).matches(singleBond));
            Assertions.assertTrue(((IQueryBond) atomContainer.getBond(1)).matches(doubleBond));
            Assertions.assertTrue(((IQueryBond) atomContainer.getBond(1)).matches(tripleBond));
            Assertions.assertTrue(((IQueryBond) atomContainer.getBond(1)).matches(aromaticBond));
        }
    }

    @Test
    void bondType9_coordinationBond_cdkExceptionExpected_test() throws Exception {
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("bondType9_coordinationBond_v3000.mol"))) {
            final CDKException exception =
                    assertThrows(CDKException.class, () -> reader.readMolecule(SilentChemObjectBuilder.getInstance()));
            assertThat(exception.getMessage(), is("Error while parsing bond type: Unsupported bond type: 9, line='M  V30 2 9 2 3'"));
        }
    }

    @Test
    void bondType10_hydrogenBond_cdkExceptionExpected_test() throws Exception {
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("bondType10_hydrogenBond_v3000.mol"))) {
            final CDKException exception =
                    assertThrows(CDKException.class, () -> reader.readMolecule(SilentChemObjectBuilder.getInstance()));
            assertThat(exception.getMessage(), is("Error while parsing bond type: Unsupported bond type: 10, line='M  V30 2 10 2 3'"));
        }
    }

    @Test
    void bondType11_invalidType_cdkExceptionExpected_test() throws Exception {
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("bondType11_invalidType_v3000.mol"))) {
            final CDKException exception =
                    assertThrows(CDKException.class, () -> reader.readMolecule(SilentChemObjectBuilder.getInstance()));
            assertThat(exception.getMessage(), is("Error while parsing bond type: Invalid bond type: 11, line='M  V30 2 11 2 3'"));
        }
    }

    @Test
    void testNoChiralFlag() throws Exception {
        final String input = "\n" +
                "  Mrv1810 02052112362D          \n" +
                "\n" +
                "  0  0  0     0  0            999 V3000\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 7 7 0 0 0\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C -2.1407 12.3148 0 0 CFG=2\n" +
                "M  V30 2 C -3.4743 11.5447 0 0\n" +
                "M  V30 3 C -3.4743 10.0047 0 0\n" +
                "M  V30 4 C -2.1407 9.2347 0 0\n" +
                "M  V30 5 C -0.807 10.0047 0 0\n" +
                "M  V30 6 N -0.807 11.5447 0 0\n" +
                "M  V30 7 O -2.1407 13.8548 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 1 2\n" +
                "M  V30 2 1 2 3\n" +
                "M  V30 3 1 3 4\n" +
                "M  V30 4 1 4 5\n" +
                "M  V30 5 1 5 6\n" +
                "M  V30 6 1 1 6\n" +
                "M  V30 7 1 1 7 CFG=1\n" +
                "M  V30 END BOND\n" +
                "M  V30 END CTAB\n" +
                "M  END\n";
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        try (MDLV3000Reader mdlr = new MDLV3000Reader(new StringReader(input))) {
            IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
            Iterable<IStereoElement> iter = mol.stereoElements();
            Assertions.assertTrue(iter.iterator().hasNext());
            for (IStereoElement<?,?> se : iter) {
                assertThat(se.getGroupInfo(), is(IStereoElement.GRP_RAC1));
            }
        }
    }

    @Test
    void testChiralFlag() throws Exception {
        final String input = "\n" +
                "  Mrv1810 02052112362D          \n" +
                "\n" +
                "  0  0  0     0  0            999 V3000\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 7 7 0 0 1\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C -2.1407 12.3148 0 0 CFG=2\n" +
                "M  V30 2 C -3.4743 11.5447 0 0\n" +
                "M  V30 3 C -3.4743 10.0047 0 0\n" +
                "M  V30 4 C -2.1407 9.2347 0 0\n" +
                "M  V30 5 C -0.807 10.0047 0 0\n" +
                "M  V30 6 N -0.807 11.5447 0 0\n" +
                "M  V30 7 O -2.1407 13.8548 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 1 2\n" +
                "M  V30 2 1 2 3\n" +
                "M  V30 3 1 3 4\n" +
                "M  V30 4 1 4 5\n" +
                "M  V30 5 1 5 6\n" +
                "M  V30 6 1 1 6\n" +
                "M  V30 7 1 1 7 CFG=1\n" +
                "M  V30 END BOND\n" +
                "M  V30 END CTAB\n" +
                "M  END\n";
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        try (MDLV3000Reader mdlr = new MDLV3000Reader(new StringReader(input))) {
            IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
            Iterable<IStereoElement> iter = mol.stereoElements();
            Assertions.assertTrue(iter.iterator().hasNext());
            for (IStereoElement<?,?> se : iter) {
                // Grp Abs is actually just 0
                assertThat(se.getGroupInfo(), is(IStereoElement.GRP_ABS));
            }
        }
    }

    @Test
    void testStereoRac1() throws Exception {
        final String input = "\n" +
                "  Mrv1810 02052113162D          \n" +
                "\n" +
                "  0  0  0     0  0            999 V3000\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 7 7 0 0 0\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C -2.1407 12.3148 0 0 CFG=2\n" +
                "M  V30 2 C -3.4743 11.5447 0 0\n" +
                "M  V30 3 C -3.4743 10.0047 0 0\n" +
                "M  V30 4 C -2.1407 9.2347 0 0\n" +
                "M  V30 5 C -0.807 10.0047 0 0\n" +
                "M  V30 6 N -0.807 11.5447 0 0\n" +
                "M  V30 7 O -2.1407 13.8548 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 1 2\n" +
                "M  V30 2 1 2 3\n" +
                "M  V30 3 1 3 4\n" +
                "M  V30 4 1 4 5\n" +
                "M  V30 5 1 5 6\n" +
                "M  V30 6 1 1 6\n" +
                "M  V30 7 1 1 7 CFG=1\n" +
                "M  V30 END BOND\n" +
                "M  V30 BEGIN COLLECTION\n" +
                "M  V30 MDLV30/STERAC1 ATOMS=(1 1)\n" +
                "M  V30 END COLLECTION\n" +
                "M  V30 END CTAB\n" +
                "M  END";
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        try (MDLV3000Reader mdlr = new MDLV3000Reader(new StringReader(input))) {
            IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
            Iterable<IStereoElement> iter = mol.stereoElements();
            Assertions.assertTrue(iter.iterator().hasNext());
            for (IStereoElement<?,?> se : iter) {
                assertThat(se.getGroupInfo(), is(IStereoElement.GRP_RAC1));
            }
        }
    }

    @Test
    void testStereoRel1() throws Exception {
        final String input = "\n" +
                "  Mrv1810 02052113162D          \n" +
                "\n" +
                "  0  0  0     0  0            999 V3000\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 7 7 0 0 0\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C -2.1407 12.3148 0 0 CFG=2\n" +
                "M  V30 2 C -3.4743 11.5447 0 0\n" +
                "M  V30 3 C -3.4743 10.0047 0 0\n" +
                "M  V30 4 C -2.1407 9.2347 0 0\n" +
                "M  V30 5 C -0.807 10.0047 0 0\n" +
                "M  V30 6 N -0.807 11.5447 0 0\n" +
                "M  V30 7 O -2.1407 13.8548 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 1 2\n" +
                "M  V30 2 1 2 3\n" +
                "M  V30 3 1 3 4\n" +
                "M  V30 4 1 4 5\n" +
                "M  V30 5 1 5 6\n" +
                "M  V30 6 1 1 6\n" +
                "M  V30 7 1 1 7 CFG=1\n" +
                "M  V30 END BOND\n" +
                "M  V30 BEGIN COLLECTION\n" +
                "M  V30 MDLV30/STEREL1 ATOMS=(1 1)\n" +
                "M  V30 END COLLECTION\n" +
                "M  V30 END CTAB\n" +
                "M  END";
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        try (MDLV3000Reader mdlr = new MDLV3000Reader(new StringReader(input))) {
            IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
            for (IStereoElement<?,?> se : mol.stereoElements()) {
                assertThat(se.getGroupInfo(), is(IStereoElement.GRP_REL1));
            }
        }
    }

    @Test
    void testNonSequentialAtomIdx() throws Exception {
        final String input = "\n" +
                "  Mrv2219 11302223302D          \n" +
                "\n" +
                "  0  0  0     0  0            999 V3000\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 2 1 0 0 0\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C -6.458 3.4367 0 0\n" +
                "M  V30 22 C -7.7917 2.6667 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 1 22\n" +
                "M  V30 END BOND\n" +
                "M  V30 END CTAB\n" +
                "M  END";
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        try (MDLV3000Reader mdlr = new MDLV3000Reader(new StringReader(input))) {
            IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
            Assertions.assertEquals(2, mol.getAtomCount());
            Assertions.assertEquals(1, mol.getBondCount());
        }
    }

    @Test
    void testStereo0d() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        try (InputStream in = getClass().getResourceAsStream("stereo0d.mdl3");
             MDLV3000Reader mdlr = new MDLV3000Reader(in)) {
            IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
            int numTetrahedrals = 0;
            for (IStereoElement<?,?> se : mol.stereoElements()) {
                if (se.getConfigClass() == IStereoElement.TH)
                    numTetrahedrals++;
            }
            Assertions.assertEquals(2, numTetrahedrals);
        }

        try (InputStream in = getClass().getResourceAsStream("stereo0d.mdl3");
             MDLV3000Reader mdlr = new MDLV3000Reader(in)) {
            mdlr.getSetting("AddStereo0d").setSetting("false");
            IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
            int numTetrahedrals = 0;
            for (IStereoElement<?,?> se : mol.stereoElements()) {
                if (se.getConfigClass() == IStereoElement.TH)
                    numTetrahedrals++;
            }
            Assertions.assertEquals(0, numTetrahedrals);
        }
    }

    @Test
    void testV3000AtomMapping() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        try (InputStream in = getClass().getResourceAsStream("atom-mapping-v3000.mol");
             MDLV3000Reader mdlr = new MDLV3000Reader(in)) {
            IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
            assertThat(mol.getAtomCount(), is(3));
            for (IAtom atom : mol.atoms()) {
                assertThat(atom.getMapIdx(), is(not(0)));
            }
        }
    }

    @Test
    void testSdProperties() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        try (InputStream in = getClass().getResourceAsStream("pubchem_paracetamol.mol");
             MDLV3000Reader mdlr = new MDLV3000Reader(in)) {
            IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
            Assertions.assertEquals("AAADccByMAAAAAAAAAAAAAAAAAAAAAAAAAAwAAAAAAAAAAABAAAAHgAQCAAACAyBkAAyxoLAAgCIACVSUACCAAAhIgAIiAAGbIgIJiLCkZOEcAhk1BHI2AewQAAAAEAAAAAAAAAAgAAAAAAAAAAAAAAAAA==",
                                    mol.getProperty("PUBCHEM_CACTVS_SUBSKEYS"));
            Assertions.assertEquals("N-(4-hydroxyphenyl)acetamide",
                                    mol.getProperty("PUBCHEM_IUPAC_OPENEYE_NAME"));
            Assertions.assertEquals("0.5",
                                    mol.getProperty("PUBCHEM_XLOGP3"));
        }
    }

    @Test
    void testSdPropertiesNoDelim() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        try (InputStream in = getClass().getResourceAsStream("pubchem_paracetamol_nodelim.mol");
             MDLV3000Reader mdlr = new MDLV3000Reader(in)) {
            IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
            Assertions.assertEquals("AAADccByMAAAAAAAAAAAAAAAAAAAAAAAAAAwAAAAAAAAAAABAAAAHgAQCAAACAyBkAAyxoLAAgCIACVSUACCAAAhIgAIiAAGbIgIJiLCkZOEcAhk1BHI2AewQAAAAEAAAAAAAAAAgAAAAAAAAAAAAAAAAA==",
                                    mol.getProperty("PUBCHEM_CACTVS_SUBSKEYS"));
            Assertions.assertEquals("N-(4-hydroxyphenyl)acetamide",
                                    mol.getProperty("PUBCHEM_IUPAC_OPENEYE_NAME"));
            Assertions.assertEquals("0.5",
                                    mol.getProperty("PUBCHEM_XLOGP3"));
        }
    }

    @Test
    void testSdPropertiesDelimTrailingSpace() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        try (InputStream in = getClass().getResourceAsStream("pubchem_paracetamol_delimnoise.mol");
             MDLV3000Reader mdlr = new MDLV3000Reader(in)) {
            IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
            Assertions.assertEquals("AAADccByMAAAAAAAAAAAAAAAAAAAAAAAAAAwAAAAAAAAAAABAAAAHgAQCAAACAyBkAAyxoLAAgCIACVSUACCAAAhIgAIiAAGbIgIJiLCkZOEcAhk1BHI2AewQAAAAEAAAAAAAAAAgAAAAAAAAAAAAAAAAA==",
                                    mol.getProperty("PUBCHEM_CACTVS_SUBSKEYS"));
            Assertions.assertEquals("N-(4-hydroxyphenyl)acetamide",
                                    mol.getProperty("PUBCHEM_IUPAC_OPENEYE_NAME"));
            Assertions.assertEquals("0.5",
                                    mol.getProperty("PUBCHEM_XLOGP3"));
        }
    }

    @Test
    void testInvalidStereochemistryCollectionShouldThrow() throws IOException {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        try (InputStream in = getClass().getResourceAsStream("invalid_stereochemistry_collection.mol");
             MDLV3000Reader mdlr = new MDLV3000Reader(in)) {
            mdlr.setReaderMode(IChemObjectReader.Mode.STRICT);
            Exception exception = assertThrows(CDKException.class, () -> mdlr.read(bldr.newAtomContainer()));
            Assertions.assertEquals("Error while parsing stereo group: Expected an atom collection.", exception.getMessage());
        }
    }

    @Test
    void testShouldIgnoreInvalidStereochemistryCollection() throws IOException, CDKException {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        try (InputStream in = getClass().getResourceAsStream("invalid_stereochemistry_collection.mol");
             MDLV3000Reader mdlr = new MDLV3000Reader(in)) {
            IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
            Assertions.assertNotNull(mol);
        }
    }
}
