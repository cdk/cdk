/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */
package org.openscience.cdk.test.config.atomtypes;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.*;
import java.io.*;

import org.openscience.cdk.*;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.config.atomtypes.AtomTypeReader;

/**
 * Checks the funcitonality of the AtomTypeReader.
 *
 * @cdk.module test
 */
public class AtomTypeReaderTest extends CDKTestCase {
     
	public AtomTypeReaderTest(String name) {
		super(name);
	}
	
	public void setUp() {}
	
	public static Test suite() {
		return new TestSuite(AtomTypeReaderTest.class);
	}

    public void testAtomTypeReader_Reader() {
        AtomTypeReader reader = new AtomTypeReader(
            new StringReader("")
        );
        assertNotNull(reader);
    }
    
    public void testReadAtomTypes() {
        AtomTypeReader reader = new AtomTypeReader(
            new StringReader("")
        );
        assertNotNull(reader);
        Vector types = reader.readAtomTypes();
        assertNotNull(types);
        assertEquals(0, types.size());
    }
    
    public void testReadAtomTypes2() {
        String data = 
            "<atomTypeList xmlns=\"http://www.xml-cml.org/schema/cml2/core\"                              " +
            "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"                                    " +
            "  xsi:schemaLocation=\"http://www.xml-cml.org/schema/cml2/core ../../io/cml/data/cmlAll.xsd\"" +
            "  id=\"mol2\" title=\"MOL2 AtomTypes\">                                                      " +
            "                                                                                             " +
            "  <metadataList>                                                                             " +
            "    <metadata name=\"cvs:last-change-by\" content=\"$Author$\"/>                     " +
            "    <metadata name=\"cvs:date\" content=\"$Date$\"/>                   " +
            "    <metadata name=\"cvs:revision\" content=\"$Revision$\"/>                           " +
            "  </metadataList>                                                                            " +
            "                                                                                             " +
            "  <atomType id=\"C.3\" title=\"1\">                                                          " +
            "    <atom elementType=\"C\"/>                                                                " +
            "    <scalar dataType=\"xsd:string\" dictRef=\"cdk:hybridization\">sp3</scalar>               " +
            "  </atomType>                                                                                " +
            "  <atomType id=\"C.2\" title=\"2\">                                                          " +
            "    <atom elementType=\"C\"/>                                                                " +
            "    <scalar dataType=\"xsd:string\" dictRef=\"cdk:hybridization\">sp2</scalar>               " +
            "  </atomType>                                                                                " +
            "</atomTypeList>";
        
        AtomTypeReader reader = new AtomTypeReader(
            new StringReader(data)
        );
        assertNotNull(reader);
        Vector types = reader.readAtomTypes();
        assertNotNull(types);
        assertEquals(2, types.size());
    }
    
}
