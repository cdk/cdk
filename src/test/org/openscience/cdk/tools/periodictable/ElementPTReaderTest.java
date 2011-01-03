/* Copyright (C) 2011  Egon Willighagen <egonw@users.sourceforge.net>
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
package org.openscience.cdk.tools.periodictable;

import java.io.StringReader;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @cdk.module test-core
 */
public class ElementPTReaderTest {

	@Test
	public void testReading() {
		String string =
		"<list xmlns=\"http://www.xml-cml.org/schema\"" +
		"	  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
		"	  xsi:schemaLocation=\"http://www.xml-cml.org/schema ../../io/cml/data/cml23.xsd\"" +
		"	  xmlns:cdk=\"http://cdk.sf.net/dict/cdk/\"" +
		"	  xmlns:cvs=\"https://www.cvshome.org/\"" +
		"	  id=\"chemicalElement\" title=\"Properties of the elements\">" +
		"	  <metadataList>" +
		"	    <metadata name=\"cvs:last-change-by\" content=\"$Author$\"/>" +
		"	    <metadata name=\"cvs:date\" content=\"$Date$\"/>" +
		"	    <metadata name=\"cvs:revision\" content=\"$Revision$\"/>" +
		"	  </metadataList>" +
		"	  <elementType id=\"R\">" +
		"	      <label dictRef=\"cas:id\"></label>" +
		"         <scalar dataType=\"xsd:String\" dictRef=\"cdk:name\">Pseudoatom</scalar>" +
		"	      <scalar dataType=\"xsd:Integer\" dictRef=\"cdk:atomicNumber\">0</scalar>" +
		"	  </elementType>" +
		"</list>";
		ElementPTReader reader = new ElementPTReader(
			new StringReader(string)
		);
		List<PeriodicTableElement> elements = reader.readElements();
		Assert.assertNotNull(elements);
		Assert.assertEquals(1, elements.size());
		PeriodicTableElement element = elements.get(0);
		Assert.assertNotNull(element);
		Assert.assertEquals("Pseudoatom", element.getName());
	}

}
