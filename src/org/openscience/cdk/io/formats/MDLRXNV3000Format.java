/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.tools.DataFeatures;

/**
 * See <a href="http://www.mdl.com/downloads/public/ctfile/ctfile.jsp"></a>
 * 
 * @cdk.module io
 * @cdk.set    io-formats
 */
public class MDLRXNV3000Format implements IChemFormatMatcher {

	private static IResourceFormat myself = null;
	
	// OK, add some state info
	boolean isRXN = false;
	
    private MDLRXNV3000Format() {}
    
    public static IResourceFormat getInstance() {
    	if (myself == null) myself = new MDLRXNV3000Format();
    	return myself;
    }
    
    public String getFormatName() {
        return "MDL RXN V3000";
    }

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
      return "org.openscience.cdk.io.MDLRXNV3000Reader";
    }
    public String getWriterClassName() { return null; }

    public boolean matches(int lineNumber, String line) {
    	if (lineNumber == 1) {
    		isRXN = (line.indexOf("$RXN") != -1);
        } else if (lineNumber == 5 && isRXN) {
        	try {
                String atomCountString = line.substring(0, 3).trim();
                String bondCountString = line.substring(3, 6).trim();
                new Integer(atomCountString);
                new Integer(bondCountString);
                boolean mdlFile = true;
                if (line.length() > 6) {
                    String remainder = line.substring(6).trim();
                    for (int i = 0; i < remainder.length(); ++i) {
                        char c = remainder.charAt(i);
                        if (!(Character.isDigit(c) || Character.isWhitespace(c))) {
                            mdlFile = false;
                        }
                    }
                }
                // all tests succeeded, likely to be a MDL file
                if (mdlFile) {
                    return true;
                }
            } catch (NumberFormatException nfe) {
                // Integers not found on fifth line; therefore not a MDL file
            }
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
