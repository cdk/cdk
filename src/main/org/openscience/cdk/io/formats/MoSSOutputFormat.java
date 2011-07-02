/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. All we ask is that proper credit is given for our work,
 * which includes - but is not limited to - adding the above copyright notice to
 * the beginning of your source code files, and to any copyright notice that you
 * may distribute with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io.formats;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.tools.DataFeatures;

/**
 * @cdk.module ioformats
 * @cdk.githash
 * @cdk.set    io-formats
 */
@TestClass("org.openscience.cdk.io.formats.MoSSOutputFormatTest")
public class MoSSOutputFormat implements IChemFormatMatcher {

    private static IResourceFormat myself = null;

    private MoSSOutputFormat() {}

    @TestMethod("testResourceFormatSet")
    public static IResourceFormat getInstance() {
        if (myself == null) myself = new MoSSOutputFormat();
        return myself;
    }

    /** {@inheritDoc} */
    @TestMethod("testGetFormatName")
    public String getFormatName() {
        return "MoSS Output Format";
    }

    /** {@inheritDoc} */
    @TestMethod("testGetMIMEType")
    public String getMIMEType() {
        return "text/csv";
    }

    /** {@inheritDoc} */
    @TestMethod("testGetPreferredNameExtension")
    public String getPreferredNameExtension() {
        return getNameExtensions()[0];
    }

    /** {@inheritDoc} */
    @TestMethod("testGetNameExtensions")
    public String[] getNameExtensions() {
        return new String[]{"mossoutput"};
    }

    /** {@inheritDoc} */
    @TestMethod("testGetReaderClassName")
    public String getReaderClassName() { 
        return null;
    }

    /** {@inheritDoc} */
    @TestMethod("testGetWriterClassName")
    public String getWriterClassName() { return null; }

    /** {@inheritDoc} */
    public boolean matches(int lineNumber, String line) {
        if (lineNumber == 1 && line.equals("id,description,nodes,edges,s_abs,s_rel,c_abs,c_rel")) {
            return true;
        }
        return false;
    }

    /** {@inheritDoc} */
    @TestMethod("testIsXMLBased")
    public boolean isXMLBased() {
        return false;
    }

    /** {@inheritDoc} */
    @TestMethod("testGetSupportedDataFeatures")
    public int getSupportedDataFeatures() {
        return DataFeatures.HAS_GRAPH_REPRESENTATION;
    }

    /** {@inheritDoc} */
    @TestMethod("testGetRequiredDataFeatures")
    public int getRequiredDataFeatures() {
        return DataFeatures.HAS_GRAPH_REPRESENTATION;
    }
}
