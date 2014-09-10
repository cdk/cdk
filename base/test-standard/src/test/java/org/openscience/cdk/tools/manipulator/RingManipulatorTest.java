/* Copyright (C) 2007  Egon Willighagen
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
package org.openscience.cdk.tools.manipulator;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Ring;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.tools.manipulator.RingManipulator;

import java.util.Iterator;

/**
 * @cdk.module test-standard
 */
public class RingManipulatorTest extends CDKTestCase {

    public RingManipulatorTest() {
        super();
    }

    @Test
    public void testMarkAromaticRings() {
        IRing ring = new Ring(3, "C");
        Assert.assertNotNull(ring);
        RingManipulator.markAromaticRings(ring);
        Assert.assertFalse(ring.getFlag(CDKConstants.ISAROMATIC));

        Iterator<IAtom> atoms = ring.atoms().iterator();
        while (atoms.hasNext())
            atoms.next().setFlag(CDKConstants.ISAROMATIC, true);
        RingManipulator.markAromaticRings(ring);
        Assert.assertFalse(ring.getFlag(CDKConstants.ISAROMATIC));

        Iterator<IBond> bonds = ring.bonds().iterator();
        while (bonds.hasNext())
            bonds.next().setFlag(CDKConstants.ISAROMATIC, true);
        RingManipulator.markAromaticRings(ring);
        Assert.assertTrue(ring.getFlag(CDKConstants.ISAROMATIC));
    }

}
