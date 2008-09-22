/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
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
 */
package org.openscience.cdk.io.cml;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.tools.diff.AtomDiff;

/**
 * @cdk.module test-libiocml
 */
public class CDKRoundTripTest extends NewCDKTestCase {

    private static IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
    
    @Test public void testAtom() throws Exception {
        IMolecule mol = builder.newMolecule();
        IAtom atom = builder.newAtom("C");
        mol.addAtom(atom);
        IMolecule copy = CMLRoundTripTool.roundTripMolecule(mol);
        String difference = AtomDiff.diff(atom, copy.getAtom(0));;
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

    @Test public void testAtom_AtomicNumber() throws Exception {
        IMolecule mol = builder.newMolecule();
        IAtom atom = builder.newAtom("C");
        atom.setAtomicNumber(6);
        mol.addAtom(atom);
        IMolecule copy = CMLRoundTripTool.roundTripMolecule(mol);
        String difference = AtomDiff.diff(atom, copy.getAtom(0));;
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

}
