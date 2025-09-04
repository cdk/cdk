/* Copyright (C) 2011  Egon Willighagen <egonw@users.sf.net>
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

import net.sf.jniinchi.INCHI_RET;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.stereo.TetrahedralChirality;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.stereo.ExtendedTetrahedral;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * TestCase for the {@link InChIToStructure} class.
 *
 */
class InChIToStructureTest extends CDKTestCase {

    @Test
    void testConstructor_String_IChemObjectBuilder() throws CDKException {
        InChIToStructure parser = InChIToStructure.fromInChI("InChI=1S/CH4/h1H4", DefaultChemObjectBuilder.getInstance());
        Assertions.assertNotNull(parser);
    }

    @Test
    void testGetAtomContainer() throws CDKException {
        InChIToStructure parser = InChIToStructure.fromInChI("InChI=1S/CH4/h1H4", DefaultChemObjectBuilder.getInstance());
        IAtomContainer container = parser.getAtomContainer();
        Assertions.assertNotNull(container);
        Assertions.assertEquals(1, container.getAtomCount());
    }

    /** @cdk.bug 1293 */
    @Test
    void nonNullAtomicNumbers() throws CDKException {
        InChIToStructure parser = InChIToStructure.fromInChI("InChI=1S/CH4/h1H4", DefaultChemObjectBuilder.getInstance());
        IAtomContainer container = parser.getAtomContainer();
        for (IAtom atom : container.atoms()) {
            Assertions.assertNotNull(atom.getAtomicNumber());
        }
        Assertions.assertNotNull(container);
        Assertions.assertEquals(1, container.getAtomCount());
    }

    @Test
    void testFixedHydrogens() throws CDKException {
        InChIToStructure parser = InChIToStructure.fromInChI("InChI=1/CH2O2/c2-1-3/h1H,(H,2,3)/f/h2H",
                DefaultChemObjectBuilder.getInstance());
        IAtomContainer container = parser.getAtomContainer();
        Assertions.assertNotNull(container);
        Assertions.assertEquals(3, container.getAtomCount());
        Assertions.assertEquals(2, container.getBondCount());
        Assertions.assertTrue(container.getBond(0).getOrder() == Order.DOUBLE || container.getBond(1).getOrder() == Order.DOUBLE);
    }

    @Test
    void testGetReturnStatus_EOF() throws CDKException {
        InChIToStructure parser = InChIToStructure.fromInChI("InChI=1S", DefaultChemObjectBuilder.getInstance());
        parser.getAtomContainer();
        INCHI_RET returnStatus = parser.getReturnStatus();
        Assertions.assertNotNull(returnStatus);
        Assertions.assertEquals(INCHI_RET.WARNING, returnStatus);
        // JNA-INCHI to fix there should be a message about EOF!
    }

    @Test
    void testGetMessage() throws CDKException {
        InChIToStructure parser = InChIToStructure.fromInChI("InChI=1S/CH5/h1H4", DefaultChemObjectBuilder.getInstance());
        parser.getAtomContainer();
        String message = parser.getMessage();
        Assertions.assertNotNull(message);
    }

    @Test
    void testGetMessageNull() throws CDKException {
        InChIToStructure parser = InChIToStructure.fromInChI("InChI=1S", DefaultChemObjectBuilder.getInstance());
        parser.getAtomContainer();
        String message = parser.getMessage();
        Assertions.assertNull(message);
    }

    @Test
    void testGetLog() throws CDKException {
        InChIToStructure parser = InChIToStructure.fromInChI("InChI=1S/CH5/h1H4", DefaultChemObjectBuilder.getInstance());
        parser.getAtomContainer();
        String log = parser.getLog();
        Assertions.assertNotNull(log);
    }

    @Test
    void testGetWarningFlags() throws CDKException {
        InChIToStructure parser = InChIToStructure.fromInChI("InChI=1S/CH5/h1H4", DefaultChemObjectBuilder.getInstance());
        parser.getAtomContainer();
        long[][] flags = parser.getWarningFlags();
        Assertions.assertNotNull(flags);
        Assertions.assertEquals(2, flags.length);
        Assertions.assertEquals(2, flags[0].length);
        Assertions.assertEquals(2, flags[1].length);
    }

    @Test
    void testGetAtomContainer_IChemObjectBuilder() throws CDKException {
        InChIToStructure parser = InChIToStructure.fromInChI("InChI=1S/CH5/h1H4", SilentChemObjectBuilder.getInstance());
        IAtomContainer container = parser.getAtomContainer();
        // test if the created IAtomContainer is done with the Silent module...
        // OK, this is not typical use, but maybe the above generate method should be private
        org.hamcrest.MatcherAssert.assertThat(container, is(instanceOf(SilentChemObjectBuilder.getInstance().newAtomContainer().getClass())));
    }

    @Test
    void atomicOxygen() throws CDKException {
        InChIToStructure parser = InChIToStructure.fromInChI("InChI=1S/O", SilentChemObjectBuilder.getInstance());
        IAtomContainer container = parser.getAtomContainer();
        org.hamcrest.MatcherAssert.assertThat(container, is(instanceOf(SilentChemObjectBuilder.getInstance().newAtomContainer().getClass())));
        org.hamcrest.MatcherAssert.assertThat(container.getAtom(0).getImplicitHydrogenCount(), is(notNullValue()));
        org.hamcrest.MatcherAssert.assertThat(container.getAtom(0).getImplicitHydrogenCount(), is(0));
    }

    @Test
    void heavyOxygenWater() throws CDKException {
        InChIToStructure parser = InChIToStructure.fromInChI("InChI=1S/H2O/h1H2/i1+2", SilentChemObjectBuilder.getInstance());
        IAtomContainer container = parser.getAtomContainer();
        org.hamcrest.MatcherAssert.assertThat(container.getAtom(0).getImplicitHydrogenCount(), is(notNullValue()));
        org.hamcrest.MatcherAssert.assertThat(container, is(instanceOf(SilentChemObjectBuilder.getInstance().newAtomContainer().getClass())));        org.hamcrest.MatcherAssert.assertThat(container.getAtom(0).getImplicitHydrogenCount(), is(notNullValue()));
        org.hamcrest.MatcherAssert.assertThat(container.getAtom(0).getImplicitHydrogenCount(), is(2));
        org.hamcrest.MatcherAssert.assertThat(container.getAtom(0).getMassNumber(), is(18));
    }

    @Test
    void e_bute_2_ene() throws Exception {
        InChIToStructure parser = InChIToStructure.fromInChI("InChI=1/C4H8/c1-3-4-2/h3-4H,1-2H3/b4-3+",
                SilentChemObjectBuilder.getInstance());
        IAtomContainer container = parser.getAtomContainer();
        Iterator<IStereoElement> ses = container.stereoElements().iterator();
        org.hamcrest.MatcherAssert.assertThat(container, is(instanceOf(SilentChemObjectBuilder.getInstance().newAtomContainer().getClass())));
        Assertions.assertTrue(ses.hasNext());
        IStereoElement se = ses.next();
        assertThat(se, is(instanceOf(IDoubleBondStereochemistry.class)));
        assertThat(((IDoubleBondStereochemistry) se).getStereo(), is(IDoubleBondStereochemistry.Conformation.OPPOSITE));
    }

    @Test
    void inchiRadical() throws Exception {
        InChIToStructure parser = InChIToStructure.fromInChI("InChI=1S/C2H5/c1-2/h1H2,2H3",
                                                       SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.getAtomContainer();
        StringWriter sw = new StringWriter();
        try (MDLV2000Writer wtr = new MDLV2000Writer(sw)) {
            wtr.write(mol);
        }
        assertThat(sw.toString(), containsString("M  RAD  1   1   2"));
    }

    @Test
    void z_bute_2_ene() throws Exception {
        InChIToStructure parser = InChIToStructure.fromInChI("InChI=1/C4H8/c1-3-4-2/h3-4H,1-2H3/b4-3-",
                SilentChemObjectBuilder.getInstance());
        IAtomContainer container = parser.getAtomContainer();
        Iterator<IStereoElement> ses = container.stereoElements().iterator();
        org.hamcrest.MatcherAssert.assertThat(container, is(instanceOf(SilentChemObjectBuilder.getInstance().newAtomContainer().getClass())));
        Assertions.assertTrue(ses.hasNext());
        IStereoElement se = ses.next();
        assertThat(se, is(instanceOf(IDoubleBondStereochemistry.class)));
        assertThat(((IDoubleBondStereochemistry) se).getStereo(), is(IDoubleBondStereochemistry.Conformation.TOGETHER));
    }

    /**
     * (R)-penta-2,3-diene
     */
    @Test
    void r_penta_2_3_diene() throws Exception {
        InChIToStructure parser = InChIToStructure.fromInChI("InChI=1S/C5H8/c1-3-5-4-2/h3-4H,1-2H3/t5-/m0/s1",
                SilentChemObjectBuilder.getInstance());
        IAtomContainer container = parser.getAtomContainer();

        Iterator<IStereoElement> ses = container.stereoElements().iterator();
        org.hamcrest.MatcherAssert.assertThat(container, is(instanceOf(SilentChemObjectBuilder.getInstance().newAtomContainer().getClass())));
        Assertions.assertTrue(ses.hasNext());
        IStereoElement se = ses.next();
        assertThat(se, is(instanceOf(ExtendedTetrahedral.class)));
        ExtendedTetrahedral element = (ExtendedTetrahedral) se;
        assertThat(element.peripherals(),
                is(new IAtom[]{container.getAtom(5), container.getAtom(0), container.getAtom(1), container.getAtom(6)}));
        assertThat(element.focus(), is(container.getAtom(4)));
        assertThat(element.winding(), is(ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
    }

    /**
     * (S)-penta-2,3-diene
     */
    @Test
    void s_penta_2_3_diene() throws Exception {
        InChIToStructure parser = InChIToStructure.fromInChI("InChI=1S/C5H8/c1-3-5-4-2/h3-4H,1-2H3/t5-/m1/s1",
                SilentChemObjectBuilder.getInstance());
        IAtomContainer container = parser.getAtomContainer();

        Iterator<IStereoElement> ses = container.stereoElements().iterator();
        org.hamcrest.MatcherAssert.assertThat(container, is(instanceOf(SilentChemObjectBuilder.getInstance().newAtomContainer().getClass())));
        Assertions.assertTrue(ses.hasNext());
        IStereoElement se = ses.next();
        assertThat(se, is(instanceOf(ExtendedTetrahedral.class)));
        ExtendedTetrahedral element = (ExtendedTetrahedral) se;
        assertThat(element.peripherals(),
                is(new IAtom[]{container.getAtom(5), container.getAtom(0), container.getAtom(1), container.getAtom(6)}));
        assertThat(element.focus(), is(container.getAtom(4)));
        assertThat(element.winding(), is(ITetrahedralChirality.Stereo.CLOCKWISE));
    }

    @Test
    void diazene() throws Exception {
        InChIToStructure parse = InChIToStructure.fromInChI("InChI=1S/H2N2/c1-2/h1-2H/b2-1+",
                                                      SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = parse.getAtomContainer();
        assertThat(mol.getAtomCount(), is(4));
        assertThat(mol.stereoElements().iterator().hasNext(), is(true));
    }

    @Test
    void readImplicitDeuteriums() throws Exception {
        String inchi = "InChI=1S/C22H32O2/c1-2-3-4-5-6-7-8-9-10-11-12-13-14-15-16-17-18-19-20-21-22(23)24/h3-4,6-7,9-10,12-13,15-16,18-19H,2,5,8,11,14,17,20-21H2,1H3,(H,23,24)/b4-3-,7-6-,10-9-,13-12-,16-15-,19-18-/i1D3,2D2";
        InChIToStructure intostruct = InChIGeneratorFactory.getInstance().getInChIToStructure(inchi, DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = intostruct.getAtomContainer();
        int dCount = 0;
        for (IAtom atom : mol.atoms()) {
            Integer mass = atom.getMassNumber();
            if (mass != null && mass.equals(2))
                dCount++;
        }
        assertThat(dCount, is(5));
    }

    @Test
    void isotope() throws Exception{
        InChIToStructure parse = InChIToStructure.fromInChI("InChI=1S/HI/h1H/p-1/i1-6",
                SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = parse.getAtomContainer();
        assertThat(mol.getAtomCount(), is(1));
        assertThat(mol.getAtom(0).getMassNumber(), is(121));
    }

    @Test
    void testExtendedCisTrans() throws Exception {
        IAtomContainer mol = InChIGeneratorFactory.getInstance()
                .getInChIToStructure("InChI=1/C4BrClFI/c5-3(8)1-2-4(6)7/b4-3-",
                        SilentChemObjectBuilder.getInstance()).getAtomContainer();
        Assertions.assertNotNull(mol);
        int nExtendedCisTrans = 0;
        for (IStereoElement<?,?> se : mol.stereoElements()) {
            if (se.getConfigClass() == IStereoElement.CU)
                nExtendedCisTrans++;
            else
                Assertions.fail("Expected onl extended cis/trans");
        }
        Assertions.assertEquals(1, nExtendedCisTrans);
    }

    /**
     * Make sure the IBond{beg,end} storage order is correct for the
     * IStereoElement
     * @throws Exception
     */
    @Test
    @SuppressWarnings("unchecked")
    void ensureBondStorageOrder() throws Exception {
        IAtomContainer mol = InChIGeneratorFactory.getInstance()
                                                  .getInChIToStructure("InChI=1S/C16H25NO/c1-14(2)8-6-9-15(3)10-7-11-16(18)17-12-4-5-13-17/h7-8,10-11H,4-6,9,12-13H2,1-3H3/b11-7+,15-10+",
                                                          SilentChemObjectBuilder.getInstance()).getAtomContainer();
        Assertions.assertNotNull(mol);
        int nCisTrans = 0;
        for (IStereoElement<?,?> se : mol.stereoElements()) {
            if (se.getConfigClass() == IStereoElement.CisTrans) {
                nCisTrans++;
                IStereoElement<IBond,IBond> ctse = (IStereoElement<IBond,IBond>)se;
                IBond bond = ctse.getFocus();
                List<IBond> carriers = ctse.getCarriers();
                Assertions.assertEquals(2, carriers.size());
                Assertions.assertTrue(carriers.get(0).contains(bond.getBegin()));
                Assertions.assertTrue(carriers.get(1).contains(bond.getEnd()));
            }
        }
        Assertions.assertEquals(2, nCisTrans);
    }

    @Test
    void testTc99() throws CDKException {
        InChIToStructure parser = InChIToStructure.fromInChI("InChI=1S/Tc/i1+1", DefaultChemObjectBuilder.getInstance());
        IAtomContainer container = parser.getAtomContainer();
        Assertions.assertNotNull(container);
        Assertions.assertEquals(1, container.getAtomCount());
        Assertions.assertEquals(43, container.getAtom(0).getAtomicNumber());
        Assertions.assertEquals(99, container.getAtom(0).getMassNumber());
    }

    @Test
    void testAuxInfoConstructor_String_IChemObjectBuilder() throws CDKException {
        String auxInfo = "AuxInfo=1/0/N:1/rA:1nC/rB:/rC:8.775,-3.125,0;";
        InChIToStructure parser = InChIToStructure.fromAuxInfo(auxInfo, SilentChemObjectBuilder.getInstance());
        Assertions.assertNotNull(parser);
    }

    @Test
    void testAuxInfoGetAtomContainer() throws CDKException {
        String auxInfo = "AuxInfo=1/0/N:1/rA:1nC/rB:/rC:8.775,-3.125,0;";
        InChIToStructure parser = InChIToStructure.fromAuxInfo(auxInfo, SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.getAtomContainer();
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(1, mol.getAtomCount());
    }

    @Test
    void testAuxInfoNonNullAtomicNumbers() throws CDKException {
        String auxInfo = "AuxInfo=1/0/N:1/rA:1nC/rB:/rC:8.775,-3.125,0;";
        InChIToStructure parser = InChIToStructure.fromAuxInfo(auxInfo, SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.getAtomContainer();
        for (IAtom atom : mol.atoms()) {
            Assertions.assertNotNull(atom.getAtomicNumber());
        }
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(1, mol.getAtomCount());
    }

    @Test
    void testNullAuxInfo_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, ()-> {
            InChIToStructure.fromAuxInfo(null, SilentChemObjectBuilder.getInstance());
        });
    }

    @Test
    void testInvalidAuxInfo_EmptyAtomContainer() throws CDKException {
        String auxInfo = "INVALID";
        InChIToStructure parser = InChIToStructure.fromAuxInfo(auxInfo, SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.getAtomContainer();
        Assertions.assertEquals(0, mol.getAtomCount());
        Assertions.assertEquals(0, mol.getBondCount());
    }

    @Test
    void testAuxInfoAtomStorageOrder() throws CDKException {
        String auxInfo = "AuxInfo=1/0/N:3,2,5,1,4/it:im/rA:5ClC.oCIBr/rB:p1;s2;s2;N2;/rC:0,-1.54,0;;0,1.54,0;1.54,0,0;-1.54,0,0;";
        InChIToStructure parser = InChIToStructure.fromAuxInfo(auxInfo, SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.getAtomContainer();
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(5, mol.getAtomCount());
        Assertions.assertEquals("Cl", mol.getAtom(0).getSymbol());
        Assertions.assertEquals("C", mol.getAtom(1).getSymbol());
        Assertions.assertEquals("C", mol.getAtom(2).getSymbol());
        Assertions.assertEquals("I", mol.getAtom(3).getSymbol());
        Assertions.assertEquals("Br", mol.getAtom(4).getSymbol());
    }

    @Test
    void testAuxInfoStereoTetrahedral() throws CDKException {
        String auxInfo = "AuxInfo=1/0/N:3,2,5,1,4/it:im/rA:5ClC.oCIBr/rB:p1;s2;s2;N2;/rC:0,-1.54,0;;0,1.54,0;1.54,0,0;-1.54,0,0;";
        InChIToStructure parser = InChIToStructure.fromAuxInfo(auxInfo, SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.getAtomContainer();
        Assertions.assertNotNull(mol);

        Assertions.assertEquals(1, ((Collection<?>)mol.stereoElements()).size());

        if (mol.stereoElements().iterator().hasNext()) {
            IStereoElement<?,?> first = mol.stereoElements().iterator().next();
            Assertions.assertInstanceOf(TetrahedralChirality.class, first);
            Assertions.assertEquals("C", ((IAtom)first.getFocus()).getSymbol());
            Assertions.assertEquals("Cl", ((IAtom)first.getCarriers().get(0)).getSymbol());
            Assertions.assertEquals("C", ((IAtom)first.getCarriers().get(1)).getSymbol());
            Assertions.assertEquals("I", ((IAtom)first.getCarriers().get(2)).getSymbol());
            Assertions.assertEquals("Br", ((IAtom)first.getCarriers().get(3)).getSymbol());
        }
    }

    @Test
    @Disabled("InChI API does not return the cis trans stereochemistry.")
    void testAuxInfoExtendedCisTrans() throws CDKException {
        String auxInfo = "AuxInfo=1/0/N:1,2,3,4,5,6,7,8/rA:8nCCCCBrClFI/rB:d1;d1;d2;s3;s4;s4;s3;/rC:8.25,-4.55,0;7.25,-4.55,0;9.25,-4.55,0;6.25,-4.55,0;9.75,-5.416,0;5.75,-3.684,0;5.75,-5.416,0;9.75,-3.684,0;";
        InChIToStructure parser = InChIToStructure.fromAuxInfo(auxInfo, SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.getAtomContainer();

        Assertions.assertNotNull(mol);
        Assertions.assertEquals(1, ((Collection<?>)mol.stereoElements()).size());
    }
}
