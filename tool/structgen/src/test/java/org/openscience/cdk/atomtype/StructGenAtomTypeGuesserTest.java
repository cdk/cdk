/* Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;

import java.util.List;

/**
 * @cdk.module test-structgen
 */
public class StructGenAtomTypeGuesserTest extends CDKTestCase {

    @Test
    public void testPossibleAtomTypes_IAtomContainer_IAtom() throws java.lang.Exception {
        IAtomContainer mol = new AtomContainer();
        Atom atom = new Atom("C");
        atom.setImplicitHydrogenCount(3);
        Atom atom2 = new Atom("N");
        atom2.setImplicitHydrogenCount(2);
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(new Bond(atom, atom2, IBond.Order.SINGLE));

        StructGenAtomTypeGuesser atm = new StructGenAtomTypeGuesser();
        List<IAtomType> matched = atm.possibleAtomTypes(mol, atom);
        Assert.assertNotNull(matched);
        Assert.assertTrue(matched.size() > 0);
        Assert.assertTrue(matched.get(0) instanceof IAtomType);

        Assert.assertEquals("C", ((IAtomType) matched.get(0)).getSymbol());
    }

    @Test
    public void testStructGenAtomTypeGuesser() throws Exception {
        StructGenAtomTypeGuesser matcher = new StructGenAtomTypeGuesser();
        Assert.assertNotNull(matcher);
    }
}
