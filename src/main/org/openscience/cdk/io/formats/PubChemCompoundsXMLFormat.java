/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
 * @cdk.module  io
 * @cdk.svnrev  $Revision$
 * @cdk.set     io-formats
 */
public class PubChemCompoundsXMLFormat implements IChemFormatMatcher {

	private static IResourceFormat myself = null;
	
    private PubChemCompoundsXMLFormat() {}
    
    public static IResourceFormat getInstance() {
    	if (myself == null) myself = new PubChemCompoundsXMLFormat();
    	return myself;
    }
    
    public String getFormatName() {
        return "PubChem Compounds XML";
    }

    public String getMIMEType() {
        return null;
    }
    public String getPreferredNameExtension() {
        return getNameExtensions()[0];
    }
    public String[] getNameExtensions() {
        return new String[]{"xml"};
    }

    public String getReaderClassName() { 
    	return null;
    }
    public String getWriterClassName() { 
    	return null;
    }

	public boolean isXMLBased() {
		return true;
	}

	public int getSupportedDataFeatures() {
		return DataFeatures.NONE;
	}

	public int getRequiredDataFeatures() {
		return DataFeatures.NONE;
	}

	public boolean matches(int lineNumber, String line) {
		if (lineNumber == 2 && line.startsWith("<PC-Compounds")) return true;
		return false;
	}
}
