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
 * See <a href="http://www.mdl.com/downloads/public/ctfile/ctfile.jsp">here</a>.
 * 
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 * @cdk.set    io-formats
 */
@TestClass("org.openscience.cdk.io.formats.MDLRXNFormatTest")
public class MDLRXNFormat implements IChemFormatMatcher {

	private static IResourceFormat myself = null;
	
    private MDLRXNFormat() {}
    
    public static IResourceFormat getInstance() {
    	if (myself == null) myself = new MDLRXNFormat();
    	return myself;
    }
    
    @TestMethod("testGetFormatName")
    public String getFormatName() {
        return "MDL Reaction format";
    }

    @TestMethod("testGetMIMEType")
    public String getMIMEType() {
        return "chemical/x-mdl-rxnfile";
    }
    public String getPreferredNameExtension() {
        return getNameExtensions()[0];
    }
    public String[] getNameExtensions() {
        return new String[]{"rxn"};
    }

    public String getReaderClassName() { 
      return "org.openscience.cdk.io.MDLRXNReader";
    }
    public String getWriterClassName() { return null; }

    public boolean matches(int lineNumber, String line) {
        if (line.startsWith("$RXN")) {
            return true;
        }
        return false;
    }

	public boolean isXMLBased() {
		return false;
	}

	public int getSupportedDataFeatures() {
		return DataFeatures.NONE;
	}

	public int getRequiredDataFeatures() {
		return DataFeatures.NONE;
	}
}
