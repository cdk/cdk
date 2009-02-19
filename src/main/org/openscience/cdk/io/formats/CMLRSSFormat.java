/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
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
 * @cdk.module ioformats
 * @cdk.svnrev  $Revision$
 * @cdk.set    io-formats
 */
@TestClass("org.openscience.cdk.io.formats.CMLRSSFormatTest")
public class CMLRSSFormat implements IChemFormat {

	private static IResourceFormat myself = null;
	
    private CMLRSSFormat() {}
    
    @TestMethod("testResourceFormatSet")
    public static IResourceFormat getInstance() {
    	if (myself == null) myself = new CMLRSSFormat();
    	return myself;
    }
    
    @TestMethod("testGetFormatName")
    public String getFormatName() {
        return "CML enriched RSS";
    }

    @TestMethod("testGetMIMEType")
    public String getMIMEType() {
        return null;
    }
    @TestMethod("testGetPreferredNameExtension")
    public String getPreferredNameExtension() {
        return null;
    }
    @TestMethod("testGetNameExtensions")
    public String[] getNameExtensions() {
        return new String[0];
    }

    @TestMethod("testGetReaderClassName")
    public String getReaderClassName() { return null; }
    @TestMethod("testGetWriterClassName")
    public String getWriterClassName() { return "org.openscience.cdk.io.RssWriter"; }

	@TestMethod("testIsXMLBased")
    public boolean isXMLBased() {
		return true;
	}

	@TestMethod("testGetSupportedDataFeatures")
	public int getSupportedDataFeatures() {
		return DataFeatures.HAS_3D_COORDINATES |
		       DataFeatures.HAS_GRAPH_REPRESENTATION |
		       DataFeatures.HAS_ATOM_ELEMENT_SYMBOL;
	}

	@TestMethod("testGetRequiredDataFeatures")
    public int getRequiredDataFeatures() {
		return DataFeatures.NONE;
	}
}
