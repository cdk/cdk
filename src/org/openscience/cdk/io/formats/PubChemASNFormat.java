/* $Revision: 5855 $ $Author: egonw $$Date: 2006-03-29 10:27:08 +0200 (Mi, 29 Mrz 2006) $
 *
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
 * @cdk.module io
 * @cdk.set    io-formats
 */
public class PubChemASNFormat implements IChemFormatMatcher {

	private static IResourceFormat myself = null;
	
    private PubChemASNFormat() {}
    
    public static IResourceFormat getInstance() {
    	if (myself == null) myself = new PubChemASNFormat();
    	return myself;
    }
    
    public String getFormatName() {
        return "PubChem Compound ASN";
    }

    public String getMIMEType() {
        return null;
    }
    public String getPreferredNameExtension() {
        return getNameExtensions()[0];
    }
    public String[] getNameExtensions() {
        return new String[]{"asn"};
    }

    public String getReaderClassName() { 
    	return "org.openscience.cdk.io.PCCompoundASNReader";
    }
    public String getWriterClassName() { 
    	return null;
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

	public boolean matches(int lineNumber, String line) {
		if (lineNumber == 1 && line.startsWith("PC-Compound")) return true;
		return false;
	}
}
