/* Copyright (C) 2006-2007  Sam Adams <sea36@users.sf.net>
 *               2010,2012  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.inchi;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import io.github.dan2097.jnainchi.InchiFlag;
import io.github.dan2097.jnainchi.InchiOptions;
import io.github.dan2097.jnainchi.InchiStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.ExtendedTetrahedral;
import org.openscience.cdk.stereo.TetrahedralChirality;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TestCase for the InChIGenerator.
 *
 * @cdk.module test-inchi
 * @author Sam Adams
 * @author Egon Willighagen
 * @author Uli Fechner
 *
 * @see org.openscience.cdk.inchi.InChIGenerator
 */
class InChIGeneratorTest extends CDKTestCase {

    private static InChIGeneratorFactory factory;

    InChIGeneratorFactory getFactory() throws Exception {
        if (factory == null) {
            factory = InChIGeneratorFactory.getInstance();
        }
        return (factory);
    }

    /**
     * Tests element name is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    void testGetInchiFromChlorineAtom() throws Exception {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        ac.addAtom(new Atom("ClH"));
        InChIGenerator gen = getFactory().getInChIGenerator(ac, "FixedH");
        assertThat(gen.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(gen.getInchi()).isEqualTo("InChI=1/ClH/h1H");
    }

    @Test
    void testGetLog() throws Exception {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        ac.addAtom(new Atom("Cl"));
        InChIGenerator gen = getFactory().getInChIGenerator(ac, "FixedH");
        assertThat(gen.getLog()).isNotNull();
    }

    @Test
    void testGetAuxInfo() throws Exception {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        a1.setImplicitHydrogenCount(3);
        a2.setImplicitHydrogenCount(3);
        ac.addAtom(a1);
        ac.addAtom(a2);
        ac.addBond(new Bond(a1, a2, Order.SINGLE));
        InChIGenerator gen = getFactory().getInChIGenerator(ac, "");
        assertThat(gen.getAuxInfo()).isNotNull();
        assertThat(gen.getAuxInfo()).startsWith("AuxInfo=");
    }

    @Test
    void testGetMessage() throws Exception {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        ac.addAtom(new Atom("Cl"));
        InChIGenerator gen = getFactory().getInChIGenerator(ac, "FixedH");
        assertThat(gen.getMessage()).isEmpty();
    }

    @Test
    void testGetWarningMessage() throws Exception {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        ac.addAtom(new Atom("Cl"));
        ac.addAtom(new Atom("H"));
        ac.addBond(0, 1, Order.TRIPLE);
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        assertThat(gen.getMessage()).isNotNull();
        assertThat(gen.getMessage()).contains("Accepted unusual valence");
    }

    /**
     * Tests charge is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    void testGetInchiFromLithiumIon() throws Exception {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a = new Atom("Li");
        a.setFormalCharge(+1);
        ac.addAtom(a);
        InChIGenerator gen = getFactory().getInChIGenerator(ac, "FixedH");
        assertThat(gen.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(gen.getInchi()).isEqualTo("InChI=1/Li/q+1");
    }

    /**
    * Tests isotopic mass is correctly passed to InChI.
    *
    * @throws Exception
    */
    @Test
    void testGetInchiFromChlorine37Atom() throws Exception {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a = new Atom("ClH");
        a.setMassNumber(37);
        ac.addAtom(a);
        InChIGenerator gen = getFactory().getInChIGenerator(ac, "FixedH");
        assertThat(gen.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(gen.getInchi()).isEqualTo("InChI=1/ClH/h1H/i1+2");
    }

    /**
     * Tests implicit hydrogen count is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    void testGetInchiFromHydrogenChlorideImplicitH() throws Exception {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a = new Atom("Cl");
        a.setImplicitHydrogenCount(1);
        ac.addAtom(a);
        InChIGenerator gen = getFactory().getInChIGenerator(ac, "FixedH");
        assertThat(gen.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(gen.getInchi()).isEqualTo("InChI=1/ClH/h1H");
    }

    /**
     * Tests radical state is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    void testGetInchiFromMethylRadical() throws Exception {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a = new Atom("C");
        a.setImplicitHydrogenCount(3);
        ac.addAtom(a);
        ac.addSingleElectron(new SingleElectron(a));
        InChIGenerator gen = getFactory().getInChIGenerator(ac, "FixedH");
        assertThat(gen.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(gen.getInchi()).isEqualTo("InChI=1/CH3/h1H3");
    }

    /**
     * Tests single bond is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    void testGetInchiFromEthane() throws Exception {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        a1.setImplicitHydrogenCount(3);
        a2.setImplicitHydrogenCount(3);
        ac.addAtom(a1);
        ac.addAtom(a2);
        ac.addBond(new Bond(a1, a2, Order.SINGLE));
        InChIGenerator gen = getFactory().getInChIGenerator(ac, "FixedH");
        assertThat(gen.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(gen.getInchi()).isEqualTo("InChI=1/C2H6/c1-2/h1-2H3");
        assertThat(gen.getInchiKey()).isEqualTo("OTMSDBZUPAUEDD-UHFFFAOYNA-N");
    }

    /**
     * Test generation of non-standard InChIs.
     *
     * @throws Exception
     * @cdk.bug 1384
     * @see <a href="https://sourceforge.net/p/cdk/bugs/1384/">BUG:1384</a>
     */
    @Test
    void nonStandardInChIWithEnumOptions() throws Exception {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        a1.setImplicitHydrogenCount(3);
        a2.setImplicitHydrogenCount(3);
        ac.addAtom(a1);
        ac.addAtom(a2);
        ac.addBond(new Bond(a1, a2, Order.SINGLE));
        InchiOptions options = new InchiOptions.InchiOptionsBuilder()
                .withFlag(InchiFlag.FixedH)
                .withFlag(InchiFlag.SAbs)
                .withFlag(InchiFlag.AuxNone)
                .build();
        InChIGenerator gen = getFactory().getInChIGenerator(ac, options);
        assertThat(gen.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(gen.getInchi()).isEqualTo("InChI=1/C2H6/c1-2/h1-2H3");
        assertThat(gen.getInchiKey()).isEqualTo("OTMSDBZUPAUEDD-UHFFFAOYNA-N");
    }

    /**
     * Tests double bond is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    void testGetInchiFromEthene() throws Exception {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        a1.setImplicitHydrogenCount(2);
        a2.setImplicitHydrogenCount(2);
        ac.addAtom(a1);
        ac.addAtom(a2);
        ac.addBond(new Bond(a1, a2, Order.DOUBLE));
        InChIGenerator gen = getFactory().getInChIGenerator(ac, "FixedH");
        assertThat(gen.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(gen.getInchi()).isEqualTo("InChI=1/C2H4/c1-2/h1-2H2");
    }

    /**
     * Tests triple bond is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    void testGetInchiFromEthyne() throws Exception {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        a1.setImplicitHydrogenCount(1);
        a2.setImplicitHydrogenCount(1);
        ac.addAtom(a1);
        ac.addAtom(a2);
        ac.addBond(new Bond(a1, a2, Order.TRIPLE));
        InChIGenerator gen = getFactory().getInChIGenerator(ac, "FixedH");
        assertThat(gen.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(gen.getInchi()).isEqualTo("InChI=1/C2H2/c1-2/h1-2H");
    }

    /**
     * Tests 2D coordinates are correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    void testGetInchiEandZ12Dichloroethene2D() throws Exception {

        // (E)-1,2-dichloroethene
        IAtomContainer acE = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a1E = new Atom("C", new Point2d(2.866, -0.250));
        IAtom a2E = new Atom("C", new Point2d(3.732, 0.250));
        IAtom a3E = new Atom("Cl", new Point2d(2.000, 2.500));
        IAtom a4E = new Atom("Cl", new Point2d(4.598, -0.250));
        a1E.setImplicitHydrogenCount(1);
        a2E.setImplicitHydrogenCount(1);
        acE.addAtom(a1E);
        acE.addAtom(a2E);
        acE.addAtom(a3E);
        acE.addAtom(a4E);

        acE.addBond(new Bond(a1E, a2E, Order.DOUBLE));
        acE.addBond(new Bond(a1E, a3E, Order.SINGLE));
        acE.addBond(new Bond(a2E, a4E, Order.SINGLE));

        InChIGenerator genE = getFactory().getInChIGenerator(acE, "FixedH");
        assertThat(genE.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(genE.getInchi()).isEqualTo("InChI=1/C2H2Cl2/c3-1-2-4/h1-2H/b2-1+");

        // (Z)-1,2-dichloroethene
        IAtomContainer acZ = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a1Z = new Atom("C", new Point2d(2.866, -0.440));
        IAtom a2Z = new Atom("C", new Point2d(3.732, 0.060));
        IAtom a3Z = new Atom("Cl", new Point2d(2.000, 0.060));
        IAtom a4Z = new Atom("Cl", new Point2d(3.732, 1.060));
        a1Z.setImplicitHydrogenCount(1);
        a2Z.setImplicitHydrogenCount(1);
        acZ.addAtom(a1Z);
        acZ.addAtom(a2Z);
        acZ.addAtom(a3Z);
        acZ.addAtom(a4Z);

        acZ.addBond(new Bond(a1Z, a2Z, Order.DOUBLE));
        acZ.addBond(new Bond(a1Z, a3Z, Order.SINGLE));
        acZ.addBond(new Bond(a2Z, a4Z, Order.SINGLE));

        InChIGenerator genZ = getFactory().getInChIGenerator(acZ, "FixedH");
        assertThat(genZ.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(genZ.getInchi()).isEqualTo("InChI=1/C2H2Cl2/c3-1-2-4/h1-2H/b2-1-");
    }

    /**
     * Tests 3D coordinates are correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    void testGetInchiFromLandDAlanine3D() throws Exception {

        // L-Alanine
        IAtomContainer acL = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a1L = new Atom("C", new Point3d(-0.358, 0.819, 20.655));
        IAtom a2L = new Atom("C", new Point3d(-1.598, -0.032, 20.905));
        IAtom a3L = new Atom("N", new Point3d(-0.275, 2.014, 21.574));
        IAtom a4L = new Atom("C", new Point3d(0.952, 0.043, 20.838));
        IAtom a5L = new Atom("O", new Point3d(-2.678, 0.479, 21.093));
        IAtom a6L = new Atom("O", new Point3d(-1.596, -1.239, 20.958));
        a1L.setImplicitHydrogenCount(1);
        a3L.setImplicitHydrogenCount(2);
        a4L.setImplicitHydrogenCount(3);
        a5L.setImplicitHydrogenCount(1);
        acL.addAtom(a1L);
        acL.addAtom(a2L);
        acL.addAtom(a3L);
        acL.addAtom(a4L);
        acL.addAtom(a5L);
        acL.addAtom(a6L);

        acL.addBond(new Bond(a1L, a2L, Order.SINGLE));
        acL.addBond(new Bond(a1L, a3L, Order.SINGLE));
        acL.addBond(new Bond(a1L, a4L, Order.SINGLE));
        acL.addBond(new Bond(a2L, a5L, Order.SINGLE));
        acL.addBond(new Bond(a2L, a6L, Order.DOUBLE));

        InChIGenerator genL = getFactory().getInChIGenerator(acL, "FixedH");
        assertThat(genL.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(genL.getInchi()).isEqualTo("InChI=1/C3H7NO2/c1-2(4)3(5)6/h2H,4H2,1H3,(H,5,6)/t2-/m0/s1/f/h5H");

        // D-Alanine
        IAtomContainer acD = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a1D = new Atom("C", new Point3d(0.358, 0.819, 20.655));
        IAtom a2D = new Atom("C", new Point3d(1.598, -0.032, 20.905));
        IAtom a3D = new Atom("N", new Point3d(0.275, 2.014, 21.574));
        IAtom a4D = new Atom("C", new Point3d(-0.952, 0.043, 20.838));
        IAtom a5D = new Atom("O", new Point3d(2.678, 0.479, 21.093));
        IAtom a6D = new Atom("O", new Point3d(1.596, -1.239, 20.958));
        a1D.setImplicitHydrogenCount(1);
        a3D.setImplicitHydrogenCount(2);
        a4D.setImplicitHydrogenCount(3);
        a5D.setImplicitHydrogenCount(1);
        acD.addAtom(a1D);
        acD.addAtom(a2D);
        acD.addAtom(a3D);
        acD.addAtom(a4D);
        acD.addAtom(a5D);
        acD.addAtom(a6D);

        acD.addBond(new Bond(a1D, a2D, Order.SINGLE));
        acD.addBond(new Bond(a1D, a3D, Order.SINGLE));
        acD.addBond(new Bond(a1D, a4D, Order.SINGLE));
        acD.addBond(new Bond(a2D, a5D, Order.SINGLE));
        acD.addBond(new Bond(a2D, a6D, Order.DOUBLE));

        InChIGenerator genD = getFactory().getInChIGenerator(acD, "FixedH");
        assertThat(genD.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(genD.getInchi()).isEqualTo("InChI=1/C3H7NO2/c1-2(4)3(5)6/h2H,4H2,1H3,(H,5,6)/t2-/m1/s1/f/h5H");
    }

    // ensure only
    @Test
    void zeroHydrogenCount() throws Exception {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        ac.addAtom(new Atom("O"));
        ac.getAtom(0).setImplicitHydrogenCount(0);
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        assertThat(gen.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(gen.getInchi()).isEqualTo("InChI=1S/O");
    }

    /**
     * Tests element name is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    void testGetStandardInchiFromChlorineAtom() throws Exception {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        ac.addAtom(new Atom("ClH"));
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        assertThat(gen.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(gen.getInchi()).isEqualTo("InChI=1S/ClH/h1H");
    }

    /**
     * Tests charge is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    void testGetStandardInchiFromLithiumIon() throws Exception {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a = new Atom("Li");
        a.setFormalCharge(+1);
        ac.addAtom(a);
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        assertThat(gen.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(gen.getInchi()).isEqualTo("InChI=1S/Li/q+1");
    }

    /**
    * Tests isotopic mass is correctly passed to InChI.
    *
    * @throws Exception
    */
    @Test
    void testGetStandardInchiFromChlorine37Atom() throws Exception {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a = new Atom("ClH");
        a.setMassNumber(37);
        ac.addAtom(a);
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        assertThat(gen.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(gen.getInchi()).isEqualTo("InChI=1S/ClH/h1H/i1+2");
    }

    /**
     * Tests implicit hydrogen count is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    void testGetStandardInchiFromHydrogenChlorideImplicitH() throws Exception {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a = new Atom("Cl");
        a.setImplicitHydrogenCount(1);
        ac.addAtom(a);
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        assertThat(gen.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(gen.getInchi()).isEqualTo("InChI=1S/ClH/h1H");
    }

    /**
     * Tests radical state is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    void testGetStandardInchiFromMethylRadical() throws Exception {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a = new Atom("C");
        a.setImplicitHydrogenCount(3);
        ac.addAtom(a);
        ac.addSingleElectron(new SingleElectron(a));
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        assertThat(gen.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(gen.getInchi()).isEqualTo("InChI=1S/CH3/h1H3");
    }

    /**
     * Tests single bond is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    void testGetStandardInchiFromEthane() throws Exception {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        a1.setImplicitHydrogenCount(3);
        a2.setImplicitHydrogenCount(3);
        ac.addAtom(a1);
        ac.addAtom(a2);
        ac.addBond(new Bond(a1, a2, Order.SINGLE));
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        assertThat(gen.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(gen.getInchi()).isEqualTo("InChI=1S/C2H6/c1-2/h1-2H3");
        assertThat(gen.getInchiKey()).isEqualTo("OTMSDBZUPAUEDD-UHFFFAOYSA-N");
    }

    /**
     * Tests double bond is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    void testGetStandardInchiFromEthene() throws Exception {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        a1.setImplicitHydrogenCount(2);
        a2.setImplicitHydrogenCount(2);
        ac.addAtom(a1);
        ac.addAtom(a2);
        ac.addBond(new Bond(a1, a2, Order.DOUBLE));
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        assertThat(gen.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(gen.getInchi()).isEqualTo("InChI=1S/C2H4/c1-2/h1-2H2");
    }

    /**
     * Tests triple bond is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    void testGetStandardInchiFromEthyne() throws Exception {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        a1.setImplicitHydrogenCount(1);
        a2.setImplicitHydrogenCount(1);
        ac.addAtom(a1);
        ac.addAtom(a2);
        ac.addBond(new Bond(a1, a2, Order.TRIPLE));
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        assertThat(gen.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(gen.getInchi()).isEqualTo("InChI=1S/C2H2/c1-2/h1-2H");
    }

    /**
     * Tests 2D coordinates are correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    void testGetStandardInchiEandZ12Dichloroethene2D() throws Exception {

        // (E)-1,2-dichloroethene
        IAtomContainer acE = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a1E = new Atom("C", new Point2d(2.866, -0.250));
        IAtom a2E = new Atom("C", new Point2d(3.732, 0.250));
        IAtom a3E = new Atom("Cl", new Point2d(2.000, 2.500));
        IAtom a4E = new Atom("Cl", new Point2d(4.598, -0.250));
        a1E.setImplicitHydrogenCount(1);
        a2E.setImplicitHydrogenCount(1);
        acE.addAtom(a1E);
        acE.addAtom(a2E);
        acE.addAtom(a3E);
        acE.addAtom(a4E);

        acE.addBond(new Bond(a1E, a2E, Order.DOUBLE));
        acE.addBond(new Bond(a1E, a3E, Order.SINGLE));
        acE.addBond(new Bond(a2E, a4E, Order.SINGLE));

        InChIGenerator genE = getFactory().getInChIGenerator(acE);
        assertThat(genE.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(genE.getInchi()).isEqualTo("InChI=1S/C2H2Cl2/c3-1-2-4/h1-2H/b2-1+");

        // (Z)-1,2-dichloroethene
        IAtomContainer acZ = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a1Z = new Atom("C", new Point2d(2.866, -0.440));
        IAtom a2Z = new Atom("C", new Point2d(3.732, 0.060));
        IAtom a3Z = new Atom("Cl", new Point2d(2.000, 0.060));
        IAtom a4Z = new Atom("Cl", new Point2d(3.732, 1.060));
        a1Z.setImplicitHydrogenCount(1);
        a2Z.setImplicitHydrogenCount(1);
        acZ.addAtom(a1Z);
        acZ.addAtom(a2Z);
        acZ.addAtom(a3Z);
        acZ.addAtom(a4Z);

        acZ.addBond(new Bond(a1Z, a2Z, Order.DOUBLE));
        acZ.addBond(new Bond(a1Z, a3Z, Order.SINGLE));
        acZ.addBond(new Bond(a2Z, a4Z, Order.SINGLE));

        InChIGenerator genZ = getFactory().getInChIGenerator(acZ);
        assertThat(genZ.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(genZ.getInchi()).isEqualTo("InChI=1S/C2H2Cl2/c3-1-2-4/h1-2H/b2-1-");
    }

    /**
     * Tests 3D coordinates are correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    void testGetStandardInchiFromLandDAlanine3D() throws Exception {

        // L-Alanine
        IAtomContainer acL = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a1L = new Atom("C", new Point3d(-0.358, 0.819, 20.655));
        IAtom a2L = new Atom("C", new Point3d(-1.598, -0.032, 20.905));
        IAtom a3L = new Atom("N", new Point3d(-0.275, 2.014, 21.574));
        IAtom a4L = new Atom("C", new Point3d(0.952, 0.043, 20.838));
        IAtom a5L = new Atom("O", new Point3d(-2.678, 0.479, 21.093));
        IAtom a6L = new Atom("O", new Point3d(-1.596, -1.239, 20.958));
        a1L.setImplicitHydrogenCount(1);
        a3L.setImplicitHydrogenCount(2);
        a4L.setImplicitHydrogenCount(3);
        a5L.setImplicitHydrogenCount(1);
        acL.addAtom(a1L);
        acL.addAtom(a2L);
        acL.addAtom(a3L);
        acL.addAtom(a4L);
        acL.addAtom(a5L);
        acL.addAtom(a6L);

        acL.addBond(new Bond(a1L, a2L, Order.SINGLE));
        acL.addBond(new Bond(a1L, a3L, Order.SINGLE));
        acL.addBond(new Bond(a1L, a4L, Order.SINGLE));
        acL.addBond(new Bond(a2L, a5L, Order.SINGLE));
        acL.addBond(new Bond(a2L, a6L, Order.DOUBLE));

        InChIGenerator genL = getFactory().getInChIGenerator(acL);
        assertThat(genL.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(genL.getInchi()).isEqualTo("InChI=1S/C3H7NO2/c1-2(4)3(5)6/h2H,4H2,1H3,(H,5,6)/t2-/m0/s1");

        // D-Alanine
        IAtomContainer acD = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a1D = new Atom("C", new Point3d(0.358, 0.819, 20.655));
        IAtom a2D = new Atom("C", new Point3d(1.598, -0.032, 20.905));
        IAtom a3D = new Atom("N", new Point3d(0.275, 2.014, 21.574));
        IAtom a4D = new Atom("C", new Point3d(-0.952, 0.043, 20.838));
        IAtom a5D = new Atom("O", new Point3d(2.678, 0.479, 21.093));
        IAtom a6D = new Atom("O", new Point3d(1.596, -1.239, 20.958));
        a1D.setImplicitHydrogenCount(1);
        a3D.setImplicitHydrogenCount(2);
        a4D.setImplicitHydrogenCount(3);
        a5D.setImplicitHydrogenCount(1);
        acD.addAtom(a1D);
        acD.addAtom(a2D);
        acD.addAtom(a3D);
        acD.addAtom(a4D);
        acD.addAtom(a5D);
        acD.addAtom(a6D);

        acD.addBond(new Bond(a1D, a2D, Order.SINGLE));
        acD.addBond(new Bond(a1D, a3D, Order.SINGLE));
        acD.addBond(new Bond(a1D, a4D, Order.SINGLE));
        acD.addBond(new Bond(a2D, a5D, Order.SINGLE));
        acD.addBond(new Bond(a2D, a6D, Order.DOUBLE));

        InChIGenerator genD = getFactory().getInChIGenerator(acD);
        assertThat(genD.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(genD.getInchi()).isEqualTo("InChI=1S/C3H7NO2/c1-2(4)3(5)6/h2H,4H2,1H3,(H,5,6)/t2-/m1/s1");
    }

    @Test
    void testTetrahedralStereo() throws Exception {
        // L-Alanine
        IAtomContainer acL = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom[] ligandAtoms = new IAtom[4];
        IAtom a1 = new Atom("C");
        IAtom a1H = new Atom("H");
        ligandAtoms[0] = a1H;
        IAtom a2 = new Atom("C");
        ligandAtoms[1] = a2;
        IAtom a3 = new Atom("N");
        ligandAtoms[2] = a3;
        IAtom a4 = new Atom("C");
        ligandAtoms[3] = a4;
        IAtom a5 = new Atom("O");
        IAtom a6 = new Atom("O");
        a1.setImplicitHydrogenCount(0);
        a3.setImplicitHydrogenCount(2);
        a4.setImplicitHydrogenCount(3);
        a5.setImplicitHydrogenCount(1);
        acL.addAtom(a1);
        acL.addAtom(a1H);
        acL.addAtom(a2);
        acL.addAtom(a3);
        acL.addAtom(a4);
        acL.addAtom(a5);
        acL.addAtom(a6);

        acL.addBond(new Bond(a1, a1H, Order.SINGLE));
        acL.addBond(new Bond(a1, a2, Order.SINGLE));
        acL.addBond(new Bond(a1, a3, Order.SINGLE));
        acL.addBond(new Bond(a1, a4, Order.SINGLE));
        acL.addBond(new Bond(a2, a5, Order.SINGLE));
        acL.addBond(new Bond(a2, a6, Order.DOUBLE));

        ITetrahedralChirality chirality = new TetrahedralChirality(a1, ligandAtoms, Stereo.ANTI_CLOCKWISE);
        acL.addStereoElement(chirality);

        InChIGenerator genL = getFactory().getInChIGenerator(acL);
        assertThat(genL.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(genL.getInchi()).isEqualTo("InChI=1S/C3H7NO2/c1-2(4)3(5)6/h2H,4H2,1H3,(H,5,6)/t2-/m0/s1");
    }

    @Test
    void testDoubleBondStereochemistry() throws Exception {
        // (E)-1,2-dichloroethene
        IAtomContainer acE = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a1E = new Atom("C");
        IAtom a2E = new Atom("C");
        IAtom a3E = new Atom("Cl");
        IAtom a4E = new Atom("Cl");
        a1E.setImplicitHydrogenCount(1);
        a2E.setImplicitHydrogenCount(1);
        acE.addAtom(a1E);
        acE.addAtom(a2E);
        acE.addAtom(a3E);
        acE.addAtom(a4E);

        acE.addBond(new Bond(a1E, a2E, Order.DOUBLE));
        acE.addBond(new Bond(a1E, a3E, Order.SINGLE));
        acE.addBond(new Bond(a2E, a4E, Order.SINGLE));

        IBond[] ligands = new IBond[2];
        ligands[0] = acE.getBond(1);
        ligands[1] = acE.getBond(2);
        IDoubleBondStereochemistry stereo = new DoubleBondStereochemistry(acE.getBond(0), ligands,
                org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation.OPPOSITE);
        acE.addStereoElement(stereo);

        InChIGenerator genE = getFactory().getInChIGenerator(acE);
        assertThat(genE.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(genE.getInchi()).isEqualTo("InChI=1S/C2H2Cl2/c3-1-2-4/h1-2H/b2-1+");
    }

    /**
     * @cdk.bug 1295
     */
    @Test
    void bug1295() throws Exception {
        try (MDLV2000Reader reader = new MDLV2000Reader(getClass().getResourceAsStream("bug1295.mol"))) {
            IAtomContainer container = reader.read(DefaultChemObjectBuilder.getInstance().newAtomContainer());
            InChIGenerator generator = getFactory().getInChIGenerator(container);
            assertThat(generator.getInchi()).isEqualTo("InChI=1S/C7H15NO/c1-4-7(3)6-8-9-5-2/h6-7H,4-5H2,1-3H3");
        }
    }

    @Test
    void r_penta_2_3_diene_impl_h() throws Exception {
        IAtomContainer m = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        m.addAtom(new Atom("CH3"));
        m.addAtom(new Atom("CH"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("CH"));
        m.addAtom(new Atom("CH3"));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);

        int[][] atoms = new int[][]{{0, 1, 3, 4}, {1, 0, 3, 4}, {1, 0, 4, 3}, {0, 1, 4, 3}, {4, 3, 1, 0}, {4, 3, 0, 1},
                {3, 4, 0, 1}, {3, 4, 1, 0},};
        Stereo[] stereos = new Stereo[]{Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE,
                Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE};

        for (int i = 0; i < atoms.length; i++) {
            IStereoElement element = new ExtendedTetrahedral(m.getAtom(2), new IAtom[]{m.getAtom(atoms[i][0]),
                    m.getAtom(atoms[i][1]), m.getAtom(atoms[i][2]), m.getAtom(atoms[i][3])}, stereos[i]);
            m.setStereoElements(Collections.singletonList(element));

            InChIGenerator generator = getFactory().getInChIGenerator(m);
            assertThat(generator.getInchi()).isEqualTo("InChI=1S/C5H8/c1-3-5-4-2/h3-4H,1-2H3/t5-/m0/s1");

        }
    }

    @Test
    void s_penta_2_3_diene_impl_h() throws Exception {
        IAtomContainer m = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        m.addAtom(new Atom("CH3"));
        m.addAtom(new Atom("CH"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("CH"));
        m.addAtom(new Atom("CH3"));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);

        int[][] atoms = new int[][]{{0, 1, 3, 4}, {1, 0, 3, 4}, {1, 0, 4, 3}, {0, 1, 4, 3}, {4, 3, 1, 0}, {4, 3, 0, 1},
                {3, 4, 0, 1}, {3, 4, 1, 0},};
        Stereo[] stereos = new Stereo[]{Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE,
                Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE};

        for (int i = 0; i < atoms.length; i++) {

            IStereoElement element = new ExtendedTetrahedral(m.getAtom(2), new IAtom[]{m.getAtom(atoms[i][0]),
                    m.getAtom(atoms[i][1]), m.getAtom(atoms[i][2]), m.getAtom(atoms[i][3])}, stereos[i]);
            m.setStereoElements(Collections.singletonList(element));

            InChIGenerator generator = getFactory().getInChIGenerator(m);
            assertThat(generator.getInchi()).isEqualTo("InChI=1S/C5H8/c1-3-5-4-2/h3-4H,1-2H3/t5-/m1/s1");

        }
    }

    @Test
    void r_penta_2_3_diene_expl_h() throws Exception {
        IAtomContainer m = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        m.addAtom(new Atom("CH3"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("CH3"));
        m.addAtom(new Atom("H"));
        m.addAtom(new Atom("H"));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(1, 5, IBond.Order.SINGLE);
        m.addBond(3, 6, IBond.Order.SINGLE);

        int[][] atoms = new int[][]{{0, 5, 6, 4}, {5, 0, 6, 4}, {5, 0, 4, 6}, {0, 5, 4, 6}, {4, 6, 5, 0}, {4, 6, 0, 5},
                {6, 4, 0, 5}, {6, 4, 5, 0},};
        Stereo[] stereos = new Stereo[]{Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE,
                Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE};

        for (int i = 0; i < atoms.length; i++) {

            IStereoElement element = new ExtendedTetrahedral(m.getAtom(2), new IAtom[]{m.getAtom(atoms[i][0]),
                    m.getAtom(atoms[i][1]), m.getAtom(atoms[i][2]), m.getAtom(atoms[i][3])}, stereos[i]);
            m.setStereoElements(Collections.singletonList(element));

            InChIGenerator generator = getFactory().getInChIGenerator(m);
            assertThat(generator.getInchi()).isEqualTo("InChI=1S/C5H8/c1-3-5-4-2/h3-4H,1-2H3/t5-/m0/s1");

        }
    }

    @Test
    void s_penta_2_3_diene_expl_h() throws Exception {
        IAtomContainer m = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        m.addAtom(new Atom("CH3"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("CH3"));
        m.addAtom(new Atom("H"));
        m.addAtom(new Atom("H"));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(1, 5, IBond.Order.SINGLE);
        m.addBond(3, 6, IBond.Order.SINGLE);

        int[][] atoms = new int[][]{{0, 5, 6, 4}, {5, 0, 6, 4}, {5, 0, 4, 6}, {0, 5, 4, 6}, {4, 6, 5, 0}, {4, 6, 0, 5},
                {6, 4, 0, 5}, {6, 4, 5, 0},};
        Stereo[] stereos = new Stereo[]{Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE,
                Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE};

        for (int i = 0; i < atoms.length; i++) {

            IStereoElement element = new ExtendedTetrahedral(m.getAtom(2), new IAtom[]{m.getAtom(atoms[i][0]),
                    m.getAtom(atoms[i][1]), m.getAtom(atoms[i][2]), m.getAtom(atoms[i][3])}, stereos[i]);
            m.setStereoElements(Collections.singletonList(element));

            InChIGenerator generator = getFactory().getInChIGenerator(m);
            assertThat(generator.getInchi()).isEqualTo("InChI=1S/C5H8/c1-3-5-4-2/h3-4H,1-2H3/t5-/m1/s1");

        }
    }

    @Test
    @Timeout(value = 1500, unit = TimeUnit.MILLISECONDS)
    void timeout() throws CDKException {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        String smiles = "C(CCCNC(=N)N)(COCC(COP([O])(=O)OCCCCCCNC(NC1=CC(=C(C=C1)C2(C3=CC=C(C=C3OC=4C2=CC=C(C4)O)O)C)C(=O)[O])=S)OP(=O)([O])OCC(COCC(CCC/[NH]=C(\\[NH])/N)(CCCNC(=N)N)CCCNC(=N)N)OP(=O)([O])OCC(COCC(CCCNC(=N)N)(CCC/[NH]=C(\\[NH])/N)CCCNC(=N)N)OP(OCC(COCC(CCCNC(=N)N)(CCCNC(=N)N)CCC/[NH]=C(\\[NH])/N)OP(=O)([O])OCC(COCC(CCCNC(=N)N)(CCCNC(N)=N)CCC/[NH]=C(/N)\\[NH])OP([O])(=O)CCC(COCC(CCCNC(=N)N)(CCC/[NH]=C(\\[NH])/N)CCCNC(=N)N)OP([O])(=O)OCC(COCC(CCCNC(N)=N)(CCCNC(N)=N)CCC/[NH]=C(\\[NH])/N)OP(OCC(COCC(CCCNC(N)=N)(CCC/[NH]=C(/N)\\[NH])CCCNC(N)=N)O=P([O])(OCC(COP(=OC(COCC(CCC/[NH]=C(\\[NH])/N)(CCCNC(N)=N)CCCNC(N)=N)COP([O])(=O)OC(COP(OC(COCC(CCCNC(=N)N)(CCC/[NH]=C(\\[NH])/N)CCCNC(=N)N)COP(OC(COCC(CCCNC(=N)N)(CCC/[NH]=C(\\[NH])/N)CCCNC(=N)N)COP([O])(=O)OC(COP(OC(COP(OC(COP(=O)([O])OC(COCC(CCC/[NH]=C(/N)\\[NH])(CCCNC(N)=N)CCCNC(=N)N)COP([O])(=O)OCCCCCCNC(NC=5C=CC(=C(C5)C(=O)[O])C6(C7=CC=C(C=C7OC=8C6=CC=C(C8)O)O)C)=S)COCC(CCCNC(N)=N)(CCC/[NH]=C(\\[NH])/N)CCCNC(=N)N)([O])=O)COCC(CCCNC(=N)N)(CCC/[NH]=C(\\[NH])/N)CCCNC(=N)N)([O])=O)COCC(CCCNC(=N)N)(CCCNC(=N)N)CCC/[NH]=C(\\[NH])/N)([O])=O)([O])=O)COCC(CCC/[NH]=C(/N)\\[NH])(CCCNC(=N)N)CCCNC(=N)N)([O])[O])(C)COP(OCCCCCCO)(=O)[O])[O])(=O)[O])([O])=O)(CCC/[NH]=C(\\[NH])/[NH])CCCNC(=N)N";
        IAtomContainer mol = smipar.parseSmiles(smiles);
        InChIGeneratorFactory inchiFact = InChIGeneratorFactory.getInstance();
        InChIGenerator generator = inchiFact.getInChIGenerator(mol, "W0.01");
        assertThat(generator.getStatus()).isEqualTo(InchiStatus.ERROR);
        assertThat(generator.getMessage()).containsAnyOf("Time limit exceeded", "Structure normalization timeout");
    }

    /**
     * Standard inchi for guanine.
     * @cdk.smiles NC1=NC2=C(N=CN2)C(=O)N1
     */
    @Test
    void guanine_std() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        String smiles = "NC1=NC2=C(N=CN2)C(=O)N1";
        IAtomContainer mol = smipar.parseSmiles(smiles);
        InChIGeneratorFactory inchiFact = InChIGeneratorFactory.getInstance();
        InChIGenerator inchigen = inchiFact.getInChIGenerator(mol);
        assertThat(inchigen.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(inchigen.getInchi()).isEqualTo("InChI=1S/C5H5N5O/c6-5-9-3-2(4(11)10-5)7-1-8-3/h1H,(H4,6,7,8,9,10,11)");
    }

    /**
     * Ensures KET (Keto-enol) option can be passed to InChI for guanine.
     * @cdk.smiles NC1=NC2=C(N=CN2)C(=O)N1
     */
    @Test
    void guanine_ket() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        String smiles = "NC1=NC2=C(N=CN2)C(=O)N1";
        IAtomContainer mol = smipar.parseSmiles(smiles);
        InChIGeneratorFactory inchiFact = InChIGeneratorFactory.getInstance();
        InChIGenerator inchigen = inchiFact.getInChIGenerator(mol, "KET");
        assertThat(inchigen.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(inchigen.getInchi()).isEqualTo("InChI=1/C5H5N5O/c6-5-9-3-2(4(11)10-5)7-1-8-3/h1H,(H4,2,6,7,8,9,10,11)");
    }

    /**
     * Standard test for aminopropenol.
     * @cdk.smiles N\C=C/C=O
     */
    @Test
    void aminopropenol_std() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        String smiles = "N\\C=C/C=O";
        IAtomContainer mol = smipar.parseSmiles(smiles);
        InChIGeneratorFactory inchiFact = InChIGeneratorFactory.getInstance();
        InChIGenerator stdinchi = inchiFact.getInChIGenerator(mol);
        assertThat(stdinchi.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(stdinchi.getInchi()).isEqualTo("InChI=1S/C3H5NO/c4-2-1-3-5/h1-3H,4H2/b2-1-");
        InChIGenerator inchigen = inchiFact.getInChIGenerator(mol, "15T");
        assertThat(inchigen.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(inchigen.getInchi()).isEqualTo("InChI=1/C3H5NO/c4-2-1-3-5/h1-3H,(H2,4,5)");
    }

    /**
     * Ensures 15T (1,5-shifts) option can be passed to InChI for aminopropenol.
     * @cdk.smiles N\C=C/C=O
     */
    @Test
    void aminopropenol_15T() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        String smiles = "N\\C=C/C=O";
        IAtomContainer mol = smipar.parseSmiles(smiles);
        InChIGeneratorFactory inchiFact = InChIGeneratorFactory.getInstance();
        InChIGenerator inchigen = inchiFact.getInChIGenerator(mol, "15T");
        assertThat(inchigen.getStatus()).isEqualTo(InchiStatus.SUCCESS);
        assertThat(inchigen.getInchi()).isEqualTo("InChI=1/C3H5NO/c4-2-1-3-5/h1-3H,(H2,4,5)");
    }
    
    /**
     * Ensures default timeout option is passed with proper switch character.
     */
    @Test
    void testFiveSecondTimeoutFlag() throws Exception {
    	IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        ac.addAtom(new Atom("C"));
    	InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
    	InChIGenerator generator = factory.getInChIGenerator(ac);
        assertThat(generator.options.getTimeoutMilliSeconds()).isEqualTo(5000);
    }

    @Test
    void testTc99() throws CDKException {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        ac.addAtom(new Atom("99Tc"));
        InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
        InChIGenerator generator = factory.getInChIGenerator(ac);
        assertThat(generator.getInchi()).isEqualTo("InChI=1S/Tc/i1+1");
    }
}
