/*
 * $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2007  Andreas Schueller <archvile18@users.sf.net>
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
package org.openscience.cdk.test.tools.manipulator;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.test.NewCDKTestCase;
import org.openscience.cdk.tools.manipulator.AtomContainerComparator;

import java.util.Comparator;

/**
 * @cdk.module test-standard
 */
public class AtomContainerComparatorTest extends NewCDKTestCase {
		
		public AtomContainerComparatorTest() {
				super();
		}

    @Test
    public void testCompare_Object_Object() {
		// Create some IAtomContainers
		DefaultChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
		IRing cycloPentane = builder.newRing(5, "C");
		IRing cycloHexane = builder.newRing(6, "C");
		IAtomContainer hexaneNitrogen = builder.newRing(6, "N");
		hexaneNitrogen.removeBond(0);
		IRing cycloHexaneNitrogen = builder.newRing(6, "N");
		IRing cycloHexeneNitrogen = builder.newRing(6, "N");
		cycloHexeneNitrogen.getBond(0).setOrder(CDKConstants.BONDORDER_DOUBLE);
		
		// Instanciate the comparator
		Comparator comparator = new AtomContainerComparator();
		
		// Assert.assert correct comparison
		Assert.assertEquals("null <-> cycloPentane", -1, comparator.compare(null, cycloPentane));
		Assert.assertEquals("null <-> null", 0, comparator.compare(null, null));
		Assert.assertEquals("cycloPentane <-> null", 1, comparator.compare(cycloPentane, null));
		
		Object object = new Object();
		Assert.assertEquals("object <-> cycloPentane", -1, comparator.compare(object, cycloPentane));
		Assert.assertEquals("object <-> object", 0, comparator.compare(object, object));
		Assert.assertEquals("cycloPentane <-> object", 1, comparator.compare(cycloPentane, object));

		Assert.assertEquals("cycloPentane <-> cycloHexane", -1, comparator.compare(cycloPentane, cycloHexane));
		Assert.assertEquals("cycloPentane <-> cycloPentane", 0, comparator.compare(cycloPentane, cycloPentane));
		Assert.assertEquals("cycloHexane <-> cycloPentane", 1, comparator.compare(cycloHexane, cycloPentane));

		Assert.assertEquals("cycloHexane <-> hexaneNitrogen", -1, comparator.compare(cycloHexane, hexaneNitrogen));
		Assert.assertEquals("cycloHexane <-> cycloHexane", 0, comparator.compare(cycloHexane, cycloHexane));
		Assert.assertEquals("hexaneNitrogen <-> cycloHexane", 1, comparator.compare(hexaneNitrogen, cycloHexane));

		Assert.assertEquals("hexaneNitrogen <-> cycloHexaneNitrogen", -1, comparator.compare(hexaneNitrogen, cycloHexaneNitrogen));
		Assert.assertEquals("hexaneNitrogen <-> hexaneNitrogen", 0, comparator.compare(hexaneNitrogen, hexaneNitrogen));
		Assert.assertEquals("cycloHexaneNitrogen <-> hexaneNitrogen", 1, comparator.compare(cycloHexaneNitrogen, hexaneNitrogen));

		Assert.assertEquals("cycloHexaneNitrogen <-> cycloHexeneNitrogen", -1, comparator.compare(cycloHexaneNitrogen, cycloHexeneNitrogen));
		Assert.assertEquals("cycloHexaneNitrogen <-> cycloHexaneNitrogen", 0, comparator.compare(cycloHexaneNitrogen, cycloHexaneNitrogen));
		Assert.assertEquals("cycloHexeneNitrogen <-> cycloHexaneNitrogen", 1, comparator.compare(cycloHexeneNitrogen, cycloHexaneNitrogen));

		Assert.assertEquals("cycloHexeneNitrogen <-> cycloHexeneNitrogen", 0, comparator.compare(cycloHexeneNitrogen, cycloHexeneNitrogen));
	}

}

