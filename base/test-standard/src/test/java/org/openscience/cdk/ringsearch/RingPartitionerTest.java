/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.ringsearch;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.templates.TestMoleculeFactory;

/**
 * This class tests the RingPartitioner class.
 *
 * @cdk.module test-standard
 *
 * @author         kaihartmann
 * @cdk.created    2005-05-24
 */
public class RingPartitionerTest extends CDKTestCase {

    static boolean standAlone = false;

    //private static ILoggingTool logger = null;

    public RingPartitionerTest() {
        super();
    }

    @Test
    public void testConvertToAtomContainer_IRingSet() {
        IAtomContainer molecule = TestMoleculeFactory.makeAlphaPinene();

        IRingSet ringSet = Cycles.sssr(molecule).toRingSet();
        IAtomContainer ac = RingPartitioner.convertToAtomContainer(ringSet);
        Assert.assertEquals(7, ac.getAtomCount());
        Assert.assertEquals(8, ac.getBondCount());
    }

    @Test
    public void testPartitionIntoRings() {
        IAtomContainer azulene = TestMoleculeFactory.makeAzulene();
        IRingSet ringSet = Cycles.sssr(azulene).toRingSet();
        List<IRingSet> list = RingPartitioner.partitionRings(ringSet);
        Assert.assertEquals(1, list.size());

        IAtomContainer biphenyl = TestMoleculeFactory.makeBiphenyl();
        ringSet = Cycles.sssr(biphenyl).toRingSet();
        list = RingPartitioner.partitionRings(ringSet);
        Assert.assertEquals(2, list.size());

        IAtomContainer spiro = TestMoleculeFactory.makeSpiroRings();
        ringSet = Cycles.sssr(spiro).toRingSet();
        list = RingPartitioner.partitionRings(ringSet);
        Assert.assertEquals(1, list.size());

    }

}
