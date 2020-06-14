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
 * @cdk.module ioformats
 * @cdk.githash
 */
public class INChIPlainTextFormat extends SimpleChemFormatMatcher implements IChemFormatMatcher {

    private static IResourceFormat myself = null;

    public INChIPlainTextFormat() {}

    public static IResourceFormat getInstance() {
        if (myself == null) myself = new INChIPlainTextFormat();
        return myself;
    }

    /** {@inheritDoc} */
    @Override
    public String getFormatName() {
        return "IUPAC-NIST Chemical Identifier (Plain Text)";
    }

    /** {@inheritDoc} */
    @Override
    public String getMIMEType() {
        return "chemical/x-inchi";
    }

    /** {@inheritDoc} */
    @Override
    public String getPreferredNameExtension() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String[] getNameExtensions() {
        return new String[0];
    }

    /** {@inheritDoc} */
    @Override
    public String getReaderClassName() {
        return "org.openscience.cdk.io.INChIPlainTextReader";
    }

    /** {@inheritDoc} */
    @Override
    public String getWriterClassName() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(int lineNumber, String line) {
        return line.startsWith("InChI=");
    }

    /** {@inheritDoc} */
    @Override
    public boolean isXMLBased() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int getSupportedDataFeatures() {
        return DataFeatures.NONE;
    }

    /** {@inheritDoc} */
    @Override
    public int getRequiredDataFeatures() {
        return DataFeatures.NONE;
    }
}
