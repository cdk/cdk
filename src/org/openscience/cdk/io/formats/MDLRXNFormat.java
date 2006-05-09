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

/**
 * See <a href="http://www.mdl.com/downloads/public/ctfile/ctfile.jsp"></a>
 * 
 * @cdk.module io
 * @cdk.set    io-formats
 */
public class MDLRXNFormat implements IChemFormatMatcher {

    public MDLRXNFormat() {}
    
    public String getFormatName() {
        return "MDL Reaction format";
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
}
