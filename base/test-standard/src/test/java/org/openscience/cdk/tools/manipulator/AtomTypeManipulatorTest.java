/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.tools.manipulator;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomType;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @cdk.module test-standard
 */
public class AtomTypeManipulatorTest extends CDKTestCase {

    public AtomTypeManipulatorTest() {
        super();
    }

    @Test
    public void testConfigure_IAtom_IAtomType() {
        IAtom atom = new Atom(Elements.CARBON);
        IAtomType atomType = new AtomType(Elements.CARBON);
        atomType.setFlag(CDKConstants.IS_HYDROGENBOND_ACCEPTOR, true);
        AtomTypeManipulator.configure(atom, atomType);
        Assert.assertEquals(atomType.getFlag(CDKConstants.IS_HYDROGENBOND_ACCEPTOR),
                atom.getFlag(CDKConstants.IS_HYDROGENBOND_ACCEPTOR));
    }

    @Test
    public void testConfigureUnsetProperties_DontOverwriterSetProperties() {
        IAtom atom = new Atom(Elements.CARBON);
        atom.setExactMass(13.0);
        IAtomType atomType = new AtomType(Elements.CARBON);
        atomType.setExactMass(12.0);
        AtomTypeManipulator.configureUnsetProperties(atom, atomType);
        Assert.assertEquals(13.0, atom.getExactMass(), 0.1);
    }

    @Test
    public void testConfigureUnsetProperties() {
        IAtom atom = new Atom(Elements.CARBON);
        IAtomType atomType = new AtomType(Elements.CARBON);
        atomType.setExactMass(12.0);
        AtomTypeManipulator.configureUnsetProperties(atom, atomType);
        Assert.assertEquals(12.0, atom.getExactMass(), 0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConfigure_IAtom_Null() {
        IAtom atom = new Atom(Elements.CARBON);
        IAtomType atomType = null;
        AtomTypeManipulator.configure(atom, atomType);
    }

    @Test
    public void unknownAtomTypeDoesNotModifyProperties() {
        IAtom atom = new Atom(Elements.CARBON);
        IAtomType atomType = new AtomType(Elements.Unknown.toIElement());
        atomType.setAtomTypeName("X");
        AtomTypeManipulator.configure(atom, atomType);
        assertThat(atom.getSymbol(), is("C"));
        assertThat(atom.getAtomicNumber(), is(6));
    }

    /**
     * @cdk.bug 1322
     */
    @Test
    public void aromaticityIsNotOverwritten() {
        IAtom atom = new Atom(Elements.CARBON);
        atom.setFlag(CDKConstants.ISAROMATIC, true);
        IAtomType atomType = new AtomType(Elements.Unknown.toIElement());
        atomType.setFlag(CDKConstants.ISAROMATIC, false);
        atomType.setAtomTypeName("C.sp3");
        AtomTypeManipulator.configure(atom, atomType);
        assertThat(atom.getFlag(CDKConstants.ISAROMATIC), is(true));
    }

    /**
     * @cdk.bug 1322
     */
    @Test
    public void aromaticitySetIfForType() {
        IAtom atom = new Atom(Elements.CARBON);
        atom.setFlag(CDKConstants.ISAROMATIC, false);
        IAtomType atomType = new AtomType(Elements.Unknown.toIElement());
        atomType.setFlag(CDKConstants.ISAROMATIC, true);
        atomType.setAtomTypeName("C.am");
        AtomTypeManipulator.configure(atom, atomType);
        assertThat(atom.getFlag(CDKConstants.ISAROMATIC), is(true));
    }
}
