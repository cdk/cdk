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
 * @cdk.module ioformats
 * @cdk.svnrev  $Revision$
 * @cdk.set    io-formats
 */
@TestClass("org.openscience.cdk.io.formats.MDLRXNV3000FormatTest")
public class MDLRXNV3000Format implements IChemFormatMatcher {

	private static IResourceFormat myself = null;
	
	// OK, add some state info
	boolean isRXN = false;
	
    private MDLRXNV3000Format() {}
    
    @TestMethod("testResourceFormatSet")
    public static IResourceFormat getInstance() {
    	if (myself == null) myself = new MDLRXNV3000Format();
    	return myself;
    }
    
    @TestMethod("testGetFormatName")
    public String getFormatName() {
        return "MDL RXN V3000";
    }

    @TestMethod("testGetMIMEType")
    public String getMIMEType() {
        return "chemical/x-mdl-rxnfile";
    }
    @TestMethod("testGetPreferredNameExtension")
    public String getPreferredNameExtension() {
        return getNameExtensions()[0];
    }
    @TestMethod("testGetNameExtensions")
    public String[] getNameExtensions() {
        return new String[]{"rxn"};
    }

    @TestMethod("testGetReaderClassName")
    public String getReaderClassName() { 
      return "org.openscience.cdk.io.MDLRXNV3000Reader";
    }
    @TestMethod("testGetWriterClassName")
    public String getWriterClassName() { return null; }

    public boolean matches(int lineNumber, String line) {
    	if (lineNumber == 1) {
    		isRXN = (line.indexOf("$RXN") != -1);
        } else if (lineNumber == 5 && isRXN) {
        	try {
                String atomCountString = line.substring(0, 3).trim();
                String bondCountString = line.substring(3, 6).trim();
                Integer.valueOf(atomCountString);
                Integer.valueOf(bondCountString);
                if (line.length() > 6) {
                    String remainder = line.substring(6).trim();
                    for (int i = 0; i < remainder.length(); ++i) {
                        char c = remainder.charAt(i);
                        if (!(Character.isDigit(c) || Character.isWhitespace(c))) {
                            return false;
                        }
                    }
                }
            } catch (NumberFormatException nfe) {
                // Integers not found on fifth line; therefore not a MDL file
            	return false;
            }
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
		return DataFeatures.NONE;
	}

	@TestMethod("testGetRequiredDataFeatures")
    public int getRequiredDataFeatures() {
		return DataFeatures.NONE;
	}
}
