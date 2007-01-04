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
 * See <a href="http://www.rcsb.org/pdb/docs/format/pdbguide2.2/guide2.2_frame.html"></a>
 * 
 * @cdk.module io
 * @cdk.set    io-formats
 */
public class PDBFormat implements IChemFormatMatcher {

	private static IResourceFormat myself = null;
	
    private PDBFormat() {}
    
    public static IResourceFormat getInstance() {
    	if (myself == null) myself = new PDBFormat();
    	return myself;
    }
    
    public String getFormatName() {
        return "Protein Brookhave Database (PDB)";
    }

    public String getMIMEType() {
        return "chemical/x-pdb";
    }
    public String getPreferredNameExtension() {
        return getNameExtensions()[0];
    }
    public String[] getNameExtensions() {
        return new String[]{"pdb,ent"};
    }

    public String getReaderClassName() { 
      return "org.openscience.cdk.io.PDBReader";
    }
    public String getWriterClassName() { 
      return "org.openscience.cdk.io.PDBWriter";
    }

    public boolean matches(int lineNumber, String line) {
        if (line.startsWith("HEADER") ||
            line.startsWith("HETATM ") ||
            line.startsWith("ATOM  ")) {
            return true;
        }
        return false;
    }

	public boolean isXMLBased() {
		return false;
	}

	public int getSupportedDataFeatures() {
		return DataFeatures.HAS_FRACTIONAL_CRYSTAL_COORDINATES |
        	   getRequiredDataFeatures();
	}

	public int getRequiredDataFeatures() {
		return DataFeatures.HAS_3D_COORDINATES |
	           DataFeatures.HAS_ATOM_ELEMENT_SYMBOL;
	}
}
