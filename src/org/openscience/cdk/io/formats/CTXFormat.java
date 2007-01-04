/* $RCSfile: $
 * $Author: egonw $
 * $Date: 2006-08-03 07:41:59 -0400 (Thu, 03 Aug 2006) $
 * $Revision: 6733 $
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
public class CTXFormat implements IChemFormatMatcher {

	private static IResourceFormat myself = null;
	
    private CTXFormat() {}
    
    public static IResourceFormat getInstance() {
    	if (myself == null) myself = new CTXFormat();
    	return myself;
    }
    
    public String getFormatName() {
        return "CTX";
    }

    public String getMIMEType() {
        return null;
    }
    public String getPreferredNameExtension() {
        return getNameExtensions()[0];
    }
    public String[] getNameExtensions() {
        return new String[]{"ctx"};
    }

    public String getReaderClassName() { 
      return "org.openscience.cdk.io.CTXReader";
    }
    public String getWriterClassName() { 
      return null;
    }

    public boolean matches(int lineNumber, String line) {
        if (lineNumber == 1 && line.startsWith(" /IDENT")) {
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
