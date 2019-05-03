/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

/**
 * Included so that CoreCoverageTest won't complain. The class does not have
 * methods, only constants, so there is nothing to test.
 *
 * @cdk.module test-core
 *
 * @see org.openscience.cdk.CDKConstants
 */
public class CDKConstantsTest extends CDKTestCase {

    @Test
    public void testCDKConstants() {
        Assert.assertFalse(CDKConstants.ISAROMATIC == -1);
    }

    @Test
    public void testSingleOrDoubleFlag() throws Exception {
        AtomContainer mol = new AtomContainer();

        IAtom atom1 = new Atom(Elements.CARBON);
        atom1.setFlag(CDKConstants.SINGLE_OR_DOUBLE, true);
        IAtom atom2 = new Atom(Elements.CARBON);
        IAtom atom3 = new Atom(Elements.CARBON);
        IAtom atom4 = new Atom(Elements.CARBON);
        IAtom atom5 = new Atom(Elements.CARBON);

        IBond bond1 = new Bond(atom1, atom2);
        bond1.setFlag(CDKConstants.SINGLE_OR_DOUBLE, true);
        IBond bond2 = new Bond(atom2, atom3);
        IBond bond3 = new Bond(atom3, atom4);
        IBond bond4 = new Bond(atom4, atom5);
        IBond bond5 = new Bond(atom5, atom1);

        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);

        mol.addBond(bond1);
        mol.addBond(bond2);
        mol.addBond(bond3);
        mol.addBond(bond4);
        mol.addBond(bond5);

        for (int i = 0; i < 5; i++) {
            if (mol.getAtom(i).getFlag(CDKConstants.SINGLE_OR_DOUBLE))
                mol.setFlag(CDKConstants.SINGLE_OR_DOUBLE, true);
        }

        // Now we have created a molecule, so let's test it...
        Assert.assertTrue(atom1.getFlag(CDKConstants.SINGLE_OR_DOUBLE));
        Assert.assertTrue(bond1.getFlag(CDKConstants.SINGLE_OR_DOUBLE));
        Assert.assertTrue(mol.getAtom(0).getFlag(CDKConstants.SINGLE_OR_DOUBLE));
        Assert.assertTrue(mol.getBond(0).getFlag(CDKConstants.SINGLE_OR_DOUBLE));
        Assert.assertTrue(mol.getFlag(CDKConstants.SINGLE_OR_DOUBLE));

    }

    // FIXME: should add a test here that used introspection and test whether there
    // are not constant conflicts

}
