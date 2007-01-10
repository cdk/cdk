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

import org.openscience.cdk.tools.DataFeatures;

/**
 * See <a href="http://www.mdl.com/downloads/public/ctfile/ctfile.jsp"></a>
 * 
 * @cdk.module io
 * @cdk.set    io-formats
 */
public class MDLFormat implements IChemFormatMatcher {

	private static IResourceFormat myself = null;
	
    private MDLFormat() {}
    
    public static IResourceFormat getInstance() {
    	if (myself == null) myself = new MDLFormat();
    	return myself;
    }
    
    public String getFormatName() {
        return "MDL Molfile";
    }

    public String getMIMEType() {
        return "chemical/x-mdl-molfile";
    }
    public String getPreferredNameExtension() {
        return getNameExtensions()[0];
    }
    public String[] getNameExtensions() {
        return new String[]{"mol"};
    }

    public String getReaderClassName() { 
      return "org.openscience.cdk.io.MDLReader";
    }
    public String getWriterClassName() { 
      return "org.openscience.cdk.io.MDLWriter";
    }

    public boolean matches(int lineNumber, String line) {
        if (lineNumber == 4 && line.length()>7 && 
        		   (line.indexOf("2000") == -1) && // MDL Mol V2000 format 
        		   (line.indexOf("3000") == -1))    // MDL Mol V3000 format 
        {
            // possibly a MDL mol file
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
                // Integers not found on fourth line; therefore not a MDL file
            }
        }
        return false;
    }

	public boolean isXMLBased() {
		return false;
	}

	public int getSupportedDataFeatures() {
		return DataFeatures.HAS_2D_COORDINATES |
		       DataFeatures.HAS_3D_COORDINATES |
		       DataFeatures.HAS_GRAPH_REPRESENTATION;
	}

	public int getRequiredDataFeatures() {
		return DataFeatures.NONE;
	}
}
