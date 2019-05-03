/* Copyright (C) 2016  Egon Willighagen <egon.willighagen@gmail.com>
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
package org.openscience.cdk.config;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.exception.NoSuchAtomTypeException;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * Checks the functionality of the {@link ImmutableAtomType}.
 *
 * @cdk.module test-core
 */
public class ImmutableAtomTypeTest extends CDKTestCase {

	@Test
	public void testToString() throws NoSuchAtomTypeException {
		AtomTypeFactory factory = AtomTypeFactory.getInstance(
			"org/openscience/cdk/dict/data/cdk-atom-types.owl",
			SilentChemObjectBuilder.getInstance()
		);
		IAtomType type = factory.getAtomType("C.sp3");
		Assert.assertTrue(type instanceof ImmutableAtomType);
		String output = type.toString();
		Assert.assertTrue(output.contains("ImmutableAtomType("));
		Assert.assertTrue(output.contains("MBO:"));
	}

}
