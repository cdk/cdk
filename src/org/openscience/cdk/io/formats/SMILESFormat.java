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
 * See <a href="http://www.daylight.com/smiles/f_smiles.html">here</a>.
 * 
 * @cdk.module io
 * @cdk.set    io-formats
 */
public class SMILESFormat implements IChemFormat {

	private static IResourceFormat myself = null;
	
    private SMILESFormat() {}
    
    public static IResourceFormat getInstance() {
    	if (myself == null) myself = new SMILESFormat();
    	return myself;
    }
    
    public String getFormatName() {
        return "SMILES";
    }

    public String getMIMEType() {
        return "chemical/x-daylight-smiles";
    }
    public String getPreferredNameExtension() {
        return getNameExtensions()[0];
    }
    public String[] getNameExtensions() {
        return new String[]{"smi"};
    }

    public String getReaderClassName() { 
      return "org.openscience.cdk.io.SMILESReader";
    }
    public String getWriterClassName() { 
      return "org.openscience.cdk.io.SMILESWriter";
    }

	public boolean isXMLBased() {
		return false;
	}

	public int getSupportedDataFeatures() {
		return getRequiredDataFeatures() |
		       DataFeatures.HAS_GRAPH_REPRESENTATION;
	}

	public int getRequiredDataFeatures() {
		return DataFeatures.HAS_ATOM_ELEMENT_SYMBOL;
	}
}
