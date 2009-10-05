/* Copyright (C) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io.formats;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.tools.DataFeatures;

/**
 * Serializes a CDK model into the Web Ontology Language using the
 * N3 format.
 * 
 * @cdk.module ioformats
 * @cdk.githash
 * @cdk.set    io-formats
 */
@TestClass("org.openscience.cdk.io.formats.CDKOWLFormatTest")
public class CDKOWLFormat implements IChemFormatMatcher {

	private static IResourceFormat myself = null;
	
    private CDKOWLFormat() {}
    
    @TestMethod("testResourceFormatSet")
    public static IResourceFormat getInstance() {
    	if (myself == null) myself = new CDKOWLFormat();
    	return myself;
    }
    
    @TestMethod("testGetFormatName")
    public String getFormatName() {
        return "CDK OWL (N3)";
    }

    @TestMethod("testGetMIMEType")
    public String getMIMEType() {
        return "text/n3";
    }
    @TestMethod("testGetPreferredNameExtension")
    public String getPreferredNameExtension() {
        return getNameExtensions()[0];
    }
    @TestMethod("testGetNameExtensions")
    public String[] getNameExtensions() {
        return new String[]{"n3"};
    }

    @TestMethod("testGetReaderClassName")
    public String getReaderClassName() { 
      return "org.openscience.cdk.io.rdf.CDKOWLReader";
    }
    @TestMethod("testGetWriterClassName")
    public String getWriterClassName() { 
      return "org.openscience.cdk.io.rdf.CDKOWLWriter";
    }

    public boolean matches(int lineNumber, String line) {
        if (line.startsWith("PREFIX") &&
            line.contains("http://cdk.sourceforge.net/model.owl#")) {
            return true;
        }
        return false;
    }

	@TestMethod("testIsXMLBased")
    public boolean isXMLBased() {
		return false;
	}

	@TestMethod("testGetSupportedDataFeatures")
	public int getSupportedDataFeatures() {
		return DataFeatures.HAS_2D_COORDINATES |
               DataFeatures.HAS_3D_COORDINATES |
               DataFeatures.HAS_ATOM_PARTIAL_CHARGES |
               DataFeatures.HAS_ATOM_FORMAL_CHARGES |
               DataFeatures.HAS_ATOM_MASS_NUMBERS |
               DataFeatures.HAS_ATOM_ISOTOPE_NUMBERS |
               DataFeatures.HAS_GRAPH_REPRESENTATION |
               DataFeatures.HAS_ATOM_ELEMENT_SYMBOL;
	}

	@TestMethod("testGetRequiredDataFeatures")
    public int getRequiredDataFeatures() {
		return DataFeatures.NONE;
	}
}
