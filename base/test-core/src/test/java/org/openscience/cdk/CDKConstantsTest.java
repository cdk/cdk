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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * Included so that CoreCoverageTest won't complain. The class does not have
 * methods, only constants, so there is nothing to test.
 *
 *
 * @see org.openscience.cdk.CDKConstants
 */
class CDKConstantsTest {

    @Test
    void testCDKConstants() {
        Assertions.assertFalse(IChemObject.AROMATIC == -1);
    }

    @Test
    void testSingleOrDoubleFlag() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();

        IAtom atom1 = mol.newAtom(Elements.CARBON.getAtomicNumber());
        atom1.setFlag(IChemObject.SINGLE_OR_DOUBLE, true);
        IAtom atom2 = mol.newAtom(Elements.CARBON.getAtomicNumber());
        IAtom atom3 = mol.newAtom(Elements.CARBON.getAtomicNumber());
        IAtom atom4 = mol.newAtom(Elements.CARBON.getAtomicNumber());
        IAtom atom5 = mol.newAtom(Elements.CARBON.getAtomicNumber());

        IBond bond1 = mol.newBond(atom1, atom2);
        bond1.setFlag(IChemObject.SINGLE_OR_DOUBLE, true);
        IBond bond2 = mol.newBond(atom2, atom3);
        IBond bond3 = mol.newBond(atom3, atom4);
        IBond bond4 = mol.newBond(atom4, atom5);
        IBond bond5 = mol.newBond(atom5, atom1);

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
            if (mol.getAtom(i).getFlag(IChemObject.SINGLE_OR_DOUBLE))
                mol.setFlag(IChemObject.SINGLE_OR_DOUBLE, true);
        }

        // Now we have created a molecule, so let's test it...
        Assertions.assertTrue(atom1.getFlag(IChemObject.SINGLE_OR_DOUBLE));
        Assertions.assertTrue(bond1.getFlag(IChemObject.SINGLE_OR_DOUBLE));
        Assertions.assertTrue(mol.getAtom(0).getFlag(IChemObject.SINGLE_OR_DOUBLE));
        Assertions.assertTrue(mol.getBond(0).getFlag(IChemObject.SINGLE_OR_DOUBLE));
        Assertions.assertTrue(mol.getFlag(IChemObject.SINGLE_OR_DOUBLE));

    }

    // FIXME: should add a test here that used introspection and test whether there
    // are not constant conflicts

}
