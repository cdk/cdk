/* Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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

import java.util.List;

/**
 * @cdk.module ioformats
 * @cdk.githash
 * @cdk.set     io-formats
 */
public class PubChemCompoundXMLFormat extends AbstractResourceFormat implements IChemFormatMatcher {

    private static IResourceFormat myself = null;

    public PubChemCompoundXMLFormat() {}

    public static IResourceFormat getInstance() {
        if (myself == null) myself = new PubChemCompoundXMLFormat();
        return myself;
    }

    /** {@inheritDoc} */
    @Override
    public String getFormatName() {
        return "PubChem Compound XML";
    }

    /** {@inheritDoc} */
    @Override
    public String getMIMEType() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getPreferredNameExtension() {
        return getNameExtensions()[0];
    }

    /** {@inheritDoc} */
    @Override
    public String[] getNameExtensions() {
        return new String[]{"xml"};
    }

    /** {@inheritDoc} */
    @Override
    public String getReaderClassName() {
        return "org.openscience.cdk.io.PCCompoundXMLReader";
    }

    /** {@inheritDoc} */
    @Override
    public String getWriterClassName() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isXMLBased() {
        return true;
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

    /** {@inheritDoc} */
    @Override
    public MatchResult matches(List<String> lines) {
        MatchResult result = NO_MATCH;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains("<PC-Compound") && result == NO_MATCH) result = new MatchResult(true, this, i);
            if (line.contains("<PC-Compounds")) return NO_MATCH;
        }
        return result;
    }
}
