/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
 * See <a href="http://www.mdl.com/downloads/public/ctfile/ctfile.jsp">here</a>.
 *
 * @cdk.module ioformats
 * @cdk.githash
 * @cdk.set    io-formats
 */
public class SDFFormat extends SimpleChemFormatMatcher implements IChemFormatMatcher {

    private static IResourceFormat myself = null;

    public SDFFormat() {}

    public static IResourceFormat getInstance() {
        if (myself == null) myself = new SDFFormat();
        return myself;
    }

    /** {@inheritDoc} */
    @Override
    public String getFormatName() {
        return "MDL Structure-data file";
    }

    /** {@inheritDoc} */
    @Override
    public String getMIMEType() {
        return "chemical/x-mdl-sdfile";
    }

    /** {@inheritDoc} */
    @Override
    public String getPreferredNameExtension() {
        return getNameExtensions()[0];
    }

    /** {@inheritDoc} */
    @Override
    public String[] getNameExtensions() {
        return new String[]{"sdf", "sd"};
    }

    /** {@inheritDoc} */
    @Override
    public String getReaderClassName() {
        return "org.openscience.cdk.io.MDLV2000Reader";
    }

    /** {@inheritDoc} */
    @Override
    public String getWriterClassName() {
        return "org.openscience.cdk.io.SDFWriter";
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(int lineNumber, String line) {
        if (line.equals("$$$$")) {
            return true;
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isXMLBased() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int getSupportedDataFeatures() {
        return getRequiredDataFeatures() | DataFeatures.HAS_2D_COORDINATES | DataFeatures.HAS_3D_COORDINATES
                | DataFeatures.HAS_GRAPH_REPRESENTATION;
    }

    /** {@inheritDoc} */
    @Override
    public int getRequiredDataFeatures() {
        return DataFeatures.HAS_ATOM_ELEMENT_SYMBOL;
    }
}
