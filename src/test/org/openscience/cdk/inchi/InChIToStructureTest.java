/* Copyright (C) 2011  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.inchi;

import net.sf.jniinchi.INCHI_RET;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * TestCase for the {@link InChIToStructure} class.
 *
 * @cdk.module test-inchi
 */
public class InChIToStructureTest extends CDKTestCase {

	@Test
	public void testConstructor_String_IChemObjectBuilder()
	throws CDKException {
		InChIToStructure parser = new InChIToStructure(
			"InChI=1S/CH4/h1H4",
			DefaultChemObjectBuilder.getInstance()
		);
		Assert.assertNotNull(parser);
	}

	@Test
	public void testGetAtomContainer() throws CDKException {
		InChIToStructure parser = new InChIToStructure(
			"InChI=1S/CH4/h1H4",
			DefaultChemObjectBuilder.getInstance()
		);
		IAtomContainer container = parser.getAtomContainer();
		Assert.assertNotNull(container);
		Assert.assertEquals(1, container.getAtomCount());
	}

	@Test
	public void testFixedHydrogens() throws CDKException {
		InChIToStructure parser = new InChIToStructure(
			"InChI=1/CH2O2/c2-1-3/h1H,(H,2,3)/f/h2H",
			DefaultChemObjectBuilder.getInstance()
		);
		IAtomContainer container = parser.getAtomContainer();
		Assert.assertNotNull(container);
		Assert.assertEquals(3, container.getAtomCount());
		Assert.assertEquals(2, container.getBondCount());
		Assert.assertTrue(
			container.getBond(0).getOrder() == Order.DOUBLE ||
			container.getBond(1).getOrder() == Order.DOUBLE
		);
	}

	@Test
	public void testGetReturnStatus_EOF() throws CDKException {
		InChIToStructure parser = new InChIToStructure(
			"InChI=1S",
			DefaultChemObjectBuilder.getInstance()
		);
		parser.getAtomContainer();
		INCHI_RET returnStatus = parser.getReturnStatus();
		Assert.assertNotNull(returnStatus);
		Assert.assertEquals(INCHI_RET.EOF, returnStatus);
	}

	@Test
	public void testGetMessage() throws CDKException {
		InChIToStructure parser = new InChIToStructure(
			"InChI=1S/CH5/h1H4", DefaultChemObjectBuilder.getInstance()
		);
		parser.getAtomContainer();
		String message = parser.getMessage();
		Assert.assertNotNull(message);
	}

	@Test
	public void testGetMessageNull() throws CDKException {
		InChIToStructure parser = new InChIToStructure(
			"InChI=1S", DefaultChemObjectBuilder.getInstance()
		);
		parser.getAtomContainer();
		String message = parser.getMessage();
		Assert.assertNull(message);
	}

	@Test
	public void testGetLog() throws CDKException {
		InChIToStructure parser = new InChIToStructure(
			"InChI=1S/CH5/h1H4", DefaultChemObjectBuilder.getInstance()
		);
		parser.getAtomContainer();
		String message = parser.getMessage();
		Assert.assertNotNull(message);
	}

	@Test
	public void testGetWarningFlags() throws CDKException {
		InChIToStructure parser = new InChIToStructure(
			"InChI=1S/CH5/h1H4", DefaultChemObjectBuilder.getInstance()
		);
		parser.getAtomContainer();
		long[][] flags = parser.getWarningFlags();
		Assert.assertNotNull(flags);
		Assert.assertEquals(2, flags.length);
		Assert.assertEquals(2, flags[0].length);
		Assert.assertEquals(2, flags[1].length);
	}

	@Test
	public void testGetAtomContainer_IChemObjectBuilder() throws CDKException {
		InChIToStructure parser = new InChIToStructure(
			"InChI=1S/CH5/h1H4", DefaultChemObjectBuilder.getInstance()
		);
		parser.generateAtomContainerFromInchi(SilentChemObjectBuilder.getInstance());
		IAtomContainer container = parser.getAtomContainer();
		// test if the created IAtomContainer is done with the Silent module...
		// OK, this is not typical use, but maybe the above generate method should be private
		Assert.assertTrue(container instanceof AtomContainer);
	}

}
