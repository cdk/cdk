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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.stereo.ExtendedTetrahedral;

import java.util.Iterator;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * TestCase for the {@link InChIToStructure} class.
 *
 * @cdk.module test-inchi
 */
public class InChIToStructureTest extends CDKTestCase {

    @Test
    public void testConstructor_String_IChemObjectBuilder() throws CDKException {
        InChIToStructure parser = new InChIToStructure("InChI=1S/CH4/h1H4", DefaultChemObjectBuilder.getInstance());
        assertNotNull(parser);
    }

    @Test
    public void testGetAtomContainer() throws CDKException {
        InChIToStructure parser = new InChIToStructure("InChI=1S/CH4/h1H4", DefaultChemObjectBuilder.getInstance());
        IAtomContainer container = parser.getAtomContainer();
        assertNotNull(container);
        Assert.assertEquals(1, container.getAtomCount());
    }

    /** @cdk.bug 1293 */
    @Test
    public void nonNullAtomicNumbers() throws CDKException {
        InChIToStructure parser = new InChIToStructure("InChI=1S/CH4/h1H4", DefaultChemObjectBuilder.getInstance());
        IAtomContainer container = parser.getAtomContainer();
        for (IAtom atom : container.atoms()) {
            assertNotNull(atom.getAtomicNumber());
        }
        assertNotNull(container);
        Assert.assertEquals(1, container.getAtomCount());
    }

    @Test
    public void testFixedHydrogens() throws CDKException {
        InChIToStructure parser = new InChIToStructure("InChI=1/CH2O2/c2-1-3/h1H,(H,2,3)/f/h2H",
                DefaultChemObjectBuilder.getInstance());
        IAtomContainer container = parser.getAtomContainer();
        assertNotNull(container);
        Assert.assertEquals(3, container.getAtomCount());
        Assert.assertEquals(2, container.getBondCount());
        assertTrue(container.getBond(0).getOrder() == Order.DOUBLE || container.getBond(1).getOrder() == Order.DOUBLE);
    }

    @Test
    public void testGetReturnStatus_EOF() throws CDKException {
        InChIToStructure parser = new InChIToStructure("InChI=1S", DefaultChemObjectBuilder.getInstance());
        parser.getAtomContainer();
        INCHI_RET returnStatus = parser.getReturnStatus();
        assertNotNull(returnStatus);
        Assert.assertEquals(INCHI_RET.EOF, returnStatus);
    }

    @Test
    public void testGetMessage() throws CDKException {
        InChIToStructure parser = new InChIToStructure("InChI=1S/CH5/h1H4", DefaultChemObjectBuilder.getInstance());
        parser.getAtomContainer();
        String message = parser.getMessage();
        assertNotNull(message);
    }

    @Test
    public void testGetMessageNull() throws CDKException {
        InChIToStructure parser = new InChIToStructure("InChI=1S", DefaultChemObjectBuilder.getInstance());
        parser.getAtomContainer();
        String message = parser.getMessage();
        Assert.assertNull(message);
    }

    @Test
    public void testGetLog() throws CDKException {
        InChIToStructure parser = new InChIToStructure("InChI=1S/CH5/h1H4", DefaultChemObjectBuilder.getInstance());
        parser.getAtomContainer();
        String message = parser.getMessage();
        assertNotNull(message);
    }

    @Test
    public void testGetWarningFlags() throws CDKException {
        InChIToStructure parser = new InChIToStructure("InChI=1S/CH5/h1H4", DefaultChemObjectBuilder.getInstance());
        parser.getAtomContainer();
        long[][] flags = parser.getWarningFlags();
        assertNotNull(flags);
        Assert.assertEquals(2, flags.length);
        Assert.assertEquals(2, flags[0].length);
        Assert.assertEquals(2, flags[1].length);
    }

    @Test
    public void testGetAtomContainer_IChemObjectBuilder() throws CDKException {
        InChIToStructure parser = new InChIToStructure("InChI=1S/CH5/h1H4", DefaultChemObjectBuilder.getInstance());
        parser.generateAtomContainerFromInchi(SilentChemObjectBuilder.getInstance());
        IAtomContainer container = parser.getAtomContainer();
        // test if the created IAtomContainer is done with the Silent module...
        // OK, this is not typical use, but maybe the above generate method should be private
        org.hamcrest.MatcherAssert.assertThat(container, is(instanceOf(SilentChemObjectBuilder.getInstance().newAtomContainer().getClass())));
    }

    @Test
    public void atomicOxygen() throws CDKException {
        InChIToStructure parser = new InChIToStructure("InChI=1S/O", DefaultChemObjectBuilder.getInstance());
        parser.generateAtomContainerFromInchi(SilentChemObjectBuilder.getInstance());
        IAtomContainer container = parser.getAtomContainer();
        org.hamcrest.MatcherAssert.assertThat(container, is(instanceOf(SilentChemObjectBuilder.getInstance().newAtomContainer().getClass())));
        org.hamcrest.MatcherAssert.assertThat(container.getAtom(0).getImplicitHydrogenCount(), is(notNullValue()));
        org.hamcrest.MatcherAssert.assertThat(container.getAtom(0).getImplicitHydrogenCount(), is(0));
    }

    @Test
    public void heavyOxygenWater() throws CDKException {
        InChIToStructure parser = new InChIToStructure("InChI=1S/H2O/h1H2/i1+2", DefaultChemObjectBuilder.getInstance());
        parser.generateAtomContainerFromInchi(SilentChemObjectBuilder.getInstance());
        IAtomContainer container = parser.getAtomContainer();
        org.hamcrest.MatcherAssert.assertThat(container.getAtom(0).getImplicitHydrogenCount(), is(notNullValue()));
        org.hamcrest.MatcherAssert.assertThat(container, is(instanceOf(SilentChemObjectBuilder.getInstance().newAtomContainer().getClass())));        org.hamcrest.MatcherAssert.assertThat(container.getAtom(0).getImplicitHydrogenCount(), is(notNullValue()));
        org.hamcrest.MatcherAssert.assertThat(container.getAtom(0).getImplicitHydrogenCount(), is(2));
        org.hamcrest.MatcherAssert.assertThat(container.getAtom(0).getMassNumber(), is(18));
    }

    @Test
    public void e_bute_2_ene() throws Exception {
        InChIToStructure parser = new InChIToStructure("InChI=1/C4H8/c1-3-4-2/h3-4H,1-2H3/b4-3+",
                DefaultChemObjectBuilder.getInstance());
        parser.generateAtomContainerFromInchi(SilentChemObjectBuilder.getInstance());
        IAtomContainer container = parser.getAtomContainer();
        Iterator<IStereoElement> ses = container.stereoElements().iterator();
        org.hamcrest.MatcherAssert.assertThat(container, is(instanceOf(SilentChemObjectBuilder.getInstance().newAtomContainer().getClass())));
        assertTrue(ses.hasNext());
        IStereoElement se = ses.next();
        assertThat(se, is(instanceOf(IDoubleBondStereochemistry.class)));
        assertThat(((IDoubleBondStereochemistry) se).getStereo(), is(IDoubleBondStereochemistry.Conformation.OPPOSITE));
    }

    @Test
    public void z_bute_2_ene() throws Exception {
        InChIToStructure parser = new InChIToStructure("InChI=1/C4H8/c1-3-4-2/h3-4H,1-2H3/b4-3-",
                DefaultChemObjectBuilder.getInstance());
        parser.generateAtomContainerFromInchi(SilentChemObjectBuilder.getInstance());
        IAtomContainer container = parser.getAtomContainer();
        Iterator<IStereoElement> ses = container.stereoElements().iterator();
        org.hamcrest.MatcherAssert.assertThat(container, is(instanceOf(SilentChemObjectBuilder.getInstance().newAtomContainer().getClass())));
        assertTrue(ses.hasNext());
        IStereoElement se = ses.next();
        assertThat(se, is(instanceOf(IDoubleBondStereochemistry.class)));
        assertThat(((IDoubleBondStereochemistry) se).getStereo(), is(IDoubleBondStereochemistry.Conformation.TOGETHER));
    }

    /**
     * (R)-penta-2,3-diene
     */
    @Test
    public void r_penta_2_3_diene() throws Exception {
        InChIToStructure parser = new InChIToStructure("InChI=1S/C5H8/c1-3-5-4-2/h3-4H,1-2H3/t5-/m0/s1",
                SilentChemObjectBuilder.getInstance());
        IAtomContainer container = parser.getAtomContainer();

        Iterator<IStereoElement> ses = container.stereoElements().iterator();
        org.hamcrest.MatcherAssert.assertThat(container, is(instanceOf(SilentChemObjectBuilder.getInstance().newAtomContainer().getClass())));
        assertTrue(ses.hasNext());
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
    public void s_penta_2_3_diene() throws Exception {
        InChIToStructure parser = new InChIToStructure("InChI=1S/C5H8/c1-3-5-4-2/h3-4H,1-2H3/t5-/m1/s1",
                SilentChemObjectBuilder.getInstance());
        IAtomContainer container = parser.getAtomContainer();

        Iterator<IStereoElement> ses = container.stereoElements().iterator();
        org.hamcrest.MatcherAssert.assertThat(container, is(instanceOf(SilentChemObjectBuilder.getInstance().newAtomContainer().getClass())));
        assertTrue(ses.hasNext());
        IStereoElement se = ses.next();
        assertThat(se, is(instanceOf(ExtendedTetrahedral.class)));
        ExtendedTetrahedral element = (ExtendedTetrahedral) se;
        assertThat(element.peripherals(),
                is(new IAtom[]{container.getAtom(5), container.getAtom(0), container.getAtom(1), container.getAtom(6)}));
        assertThat(element.focus(), is(container.getAtom(4)));
        assertThat(element.winding(), is(ITetrahedralChirality.Stereo.CLOCKWISE));
    }

    @Test
    public void diazene() throws Exception {
        InChIToStructure parse = new InChIToStructure("InChI=1S/H2N2/c1-2/h1-2H/b2-1+",
                                                      SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = parse.getAtomContainer();
        assertThat(mol.getAtomCount(), is(4));
        assertThat(mol.stereoElements().iterator().hasNext(), is(true));
    }

    @Test
    public void readImplicitDeuteriums() throws Exception {
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
}
