/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
 *  */
package org.openscience.cdk;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.templates.TestMoleculeFactory;

/**
 * TestCase for the AtomContainer class.
 *
 * @cdk.module test-extra
 *
 * @author  Christoph Steinbeck
 * @cdk.created 2001-08-09
 */
public class CloneAtomContainerTest extends CDKTestCase {

    @Test
    public void testClone() throws Exception {
        IAtomContainer molecule = TestMoleculeFactory.makeAlphaPinene();
        IAtomContainer clonedMol = (IAtomContainer) molecule.clone();
        Assert.assertTrue(molecule.getAtomCount() == clonedMol.getAtomCount());
        for (int f = 0; f < molecule.getAtomCount(); f++) {
            for (int g = 0; g < clonedMol.getAtomCount(); g++) {
                Assert.assertNotNull(molecule.getAtom(f));
                Assert.assertNotNull(clonedMol.getAtom(g));
                Assert.assertTrue(molecule.getAtom(f) != clonedMol.getAtom(g));
            }
        }
    }
}
