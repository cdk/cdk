/* Copyright (C) 2009-2010 Syed Asad Rahman <asad@ebi.ac.uk>
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
package org.openscience.cdk.smsd.ring;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smsd.algorithm.vflib.Molecules;

import org.junit.jupiter.api.BeforeEach;

/**
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 *
 * @cdk.require java1.6+
 */
class HanserRingFinderTest {

    /**
     * Test of findRings method, of class HanserRingFinder.
     */
    @Test
    void testFindRings() {
        IAtomContainer molecule = null;
        HanserRingFinder instance = new HanserRingFinder();
        Collection expResult = null;
        Collection result = instance.findRings(molecule);
        Assertions.assertEquals(expResult, result);
    }

    private HanserRingFinder finder;

    @BeforeEach
    void setUp() throws Exception {
        finder = new HanserRingFinder();
    }

    void testItShoudFindOneRingInBenzene() throws CDKException {
        IAtomContainer benzene = Molecules.createBenzene();
        Collection<List<IAtom>> rings = finder.findRings(benzene);

        Assertions.assertEquals(1, rings.size());
    }

    void testItShouldFindThreeRingsInNaphthalene() throws CDKException {
        IAtomContainer naphthalene = Molecules.createNaphthalene();
        Collection rings = finder.findRings(naphthalene);

        Assertions.assertEquals(3, rings.size());
    }

    void testItShouldFind28RingsInCubane() throws CDKException {
        IAtomContainer cubane = Molecules.createCubane();
        Collection rings = finder.findRings(cubane);

        Assertions.assertEquals(28, rings.size());
    }
}
