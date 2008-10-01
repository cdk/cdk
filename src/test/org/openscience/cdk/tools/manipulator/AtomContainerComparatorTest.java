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
package org.openscience.cdk.tools.manipulator;

import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRing;

/**
 * @cdk.module test-standard
 */
public class AtomContainerComparatorTest extends NewCDKTestCase {
		
	public AtomContainerComparatorTest() {
		super();
	}

    @Test
    public void testCompare_Null_IAtomContainer() {
		DefaultChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
		IRing cycloPentane = builder.newRing(5, "C");

		// Instantiate the comparator
		Comparator<IAtomContainer> comparator = new AtomContainerComparator();

		// Assert.assert correct comparison
		Assert.assertEquals("null <-> cycloPentane", -1, comparator.compare(null, cycloPentane));
	}

    @Test
    public void testCompare_Null_Null() {
		// Instantiate the comparator
		Comparator<IAtomContainer> comparator = new AtomContainerComparator();

		// Assert.assert correct comparison
		Assert.assertEquals("null <-> null", 0, comparator.compare(null, null));
	}

    @Test
    public void testCompare_IAtomContainer_Null() {
		DefaultChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
		IRing cycloPentane = builder.newRing(5, "C");

		// Instantiate the comparator
		Comparator<IAtomContainer> comparator = new AtomContainerComparator();

		// Assert.assert correct comparison
		Assert.assertEquals("cycloPentane <-> null", 1, comparator.compare(cycloPentane, null));
    }

    @Test
    public void testCompare_IAtomContainer_Object() {
		// Create some IAtomContainers
		DefaultChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
		IRing cycloPentane = builder.newRing(5, "C");
		
		// Instantiate the comparator
		Comparator comparator = new AtomContainerComparator();

		Object object = new Object();
		Assert.assertEquals("cycloPentane <-> object", 1, comparator.compare(cycloPentane, object));
    }

    @Test
    public void testCompare_Object_Object() {
		// Instantiate the comparator
		Comparator comparator = new AtomContainerComparator();

		Object object = new Object();
		Assert.assertEquals("object <-> object", 0, comparator.compare(object, object));
    }

    @Test
    public void testCompare_Object_IAtomContainer() {
		// Create some IAtomContainers
		DefaultChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
		IRing cycloPentane = builder.newRing(5, "C");

		// Instantiate the comparator
		Comparator comparator = new AtomContainerComparator();

		Object object = new Object();
		Assert.assertEquals("object <-> cycloPentane", -1, comparator.compare(object, cycloPentane));
    }

	@Test
    public void testCompare_RingSize() {
		// Create some IAtomContainers
		DefaultChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
		IRing cycloPentane = builder.newRing(5, "C");
		IRing cycloHexane = builder.newRing(6, "C");

		// Instantiate the comparator
		Comparator<IAtomContainer> comparator = new AtomContainerComparator();

		Assert.assertEquals("cycloPentane <-> cycloHexane", -1, comparator.compare(cycloPentane, cycloHexane));
		Assert.assertEquals("cycloPentane <-> cycloPentane", 0, comparator.compare(cycloPentane, cycloPentane));
		Assert.assertEquals("cycloHexane <-> cycloPentane", 1, comparator.compare(cycloHexane, cycloPentane));
    }

	@Test
    public void testCompare_Ring_NonRing() {
		// Create some IAtomContainers
		DefaultChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
		IRing cycloHexane = builder.newRing(6, "C");
		IAtomContainer hexaneNitrogen = builder.newRing(6, "N");
		hexaneNitrogen.removeBond(0);

		// Instantiate the comparator
		Comparator<IAtomContainer> comparator = new AtomContainerComparator();

		Assert.assertEquals("cycloHexane <-> hexaneNitrogen", -1, comparator.compare(cycloHexane, hexaneNitrogen));
		Assert.assertEquals("cycloHexane <-> cycloHexane", 0, comparator.compare(cycloHexane, cycloHexane));
		Assert.assertEquals("hexaneNitrogen <-> cycloHexane", 1, comparator.compare(hexaneNitrogen, cycloHexane));
    }

	@Test
    public void testCompare_Ring_NonRing2() {
		// Create some IAtomContainers
		DefaultChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
		IAtomContainer hexaneNitrogen = builder.newRing(6, "N");
		hexaneNitrogen.removeBond(0);
		IRing cycloHexaneNitrogen = builder.newRing(6, "N");

		// Instantiate the comparator
		Comparator<IAtomContainer> comparator = new AtomContainerComparator();

		Assert.assertEquals("hexaneNitrogen <-> cycloHexaneNitrogen", -1, comparator.compare(hexaneNitrogen, cycloHexaneNitrogen));
		Assert.assertEquals("hexaneNitrogen <-> hexaneNitrogen", 0, comparator.compare(hexaneNitrogen, hexaneNitrogen));
		Assert.assertEquals("cycloHexaneNitrogen <-> hexaneNitrogen", 1, comparator.compare(cycloHexaneNitrogen, hexaneNitrogen));
    }

	@Test
    public void testCompare_BondOrder() {
		// Create some IAtomContainers
		DefaultChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
		IRing cycloHexaneNitrogen = builder.newRing(6, "N");
		IRing cycloHexeneNitrogen = builder.newRing(6, "N");
		cycloHexeneNitrogen.getBond(0).setOrder(CDKConstants.BONDORDER_DOUBLE);

		// Instantiate the comparator
		Comparator<IAtomContainer> comparator = new AtomContainerComparator();

		Assert.assertEquals("cycloHexaneNitrogen <-> cycloHexeneNitrogen", -1, comparator.compare(cycloHexaneNitrogen, cycloHexeneNitrogen));
		Assert.assertEquals("cycloHexaneNitrogen <-> cycloHexaneNitrogen", 0, comparator.compare(cycloHexaneNitrogen, cycloHexaneNitrogen));
		Assert.assertEquals("cycloHexeneNitrogen <-> cycloHexeneNitrogen", 0, comparator.compare(cycloHexeneNitrogen, cycloHexeneNitrogen));
		Assert.assertEquals("cycloHexeneNitrogen <-> cycloHexaneNitrogen", 1, comparator.compare(cycloHexeneNitrogen, cycloHexaneNitrogen));
    }

}

