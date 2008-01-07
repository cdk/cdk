/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.ringsearch;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.NewCDKTestCase;

import java.util.List;
//import org.openscience.cdk.tools.LoggingTool;

/**
 * This class tests the RingPartitioner class.
 *
 * @cdk.module test-standard
 *
 * @author         kaihartmann
 * @cdk.created    2005-05-24
 */
public class RingPartitionerTest extends NewCDKTestCase
{

	static boolean standAlone = false;
	//private LoggingTool logger = null;



	public RingPartitionerTest()
	{
		super();
	}

    @Test
    public void testConvertToAtomContainer_IRingSet()
	{
		IMolecule molecule = MoleculeFactory.makeAlphaPinene();
		SSSRFinder sssrf = new SSSRFinder(molecule);

		IRingSet ringSet = sssrf.findSSSR();
		IAtomContainer ac = RingPartitioner.convertToAtomContainer(ringSet);
        Assert.assertEquals(7, ac.getAtomCount());
        Assert.assertEquals(8, ac.getBondCount());
	}

    @Test
    public void testPartitionIntoRings() {
        IMolecule azulene = MoleculeFactory.makeAzulene();
        SSSRFinder sssrf = new SSSRFinder(azulene);
        IRingSet ringSet = sssrf.findSSSR();
        List list = RingPartitioner.partitionRings(ringSet);
        Assert.assertEquals(1, list.size());

//        IMolecule biphenyl = MoleculeFactory.makeBiphenyl();
//        sssrf = new SSSRFinder(biphenyl);
//        ringSet = sssrf.findSSSR();
//        list = RingPartitioner.partitionRings(ringSet);
//        Assert.assertEquals(0, list.size());

        IMolecule spiro = MoleculeFactory.makeSpiroRings();
        sssrf = new SSSRFinder(spiro);
        ringSet = sssrf.findSSSR();
        list = RingPartitioner.partitionRings(ringSet);
        Assert.assertEquals(1, list.size());

    }

}

