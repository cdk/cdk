/* Copyright (C) 2004-2018  The Chemistry Development Kit (CDK) project
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
 * See <a href="http://www.mdl.com/downloads/public/ctfile/ctfile.jsp">here</a>.
 *
 * @cdk.module ioformats
 * @cdk.githash
 */
public class MDLRXNV2000Format extends AbstractResourceFormat implements IChemFormatMatcher {

    private static IResourceFormat myself = null;

    public MDLRXNV2000Format() {}

    public static IResourceFormat getInstance() {
        if (myself == null) myself = new MDLRXNV2000Format();
        return myself;
    }

    /** {@inheritDoc} */
    @Override
    public String getFormatName() {
        return "MDL RXN V2000";
    }

    /** {@inheritDoc} */
    @Override
    public String getMIMEType() {
        return "chemical/x-mdl-rxnfile";
    }

    /** {@inheritDoc} */
    @Override
    public String getPreferredNameExtension() {
        return getNameExtensions()[0];
    }

    /** {@inheritDoc} */
    @Override
    public String[] getNameExtensions() {
        return new String[]{"rxn"};
    }

    /** {@inheritDoc} */
    @Override
    public String getReaderClassName() {
        return "org.openscience.cdk.io.MDLRXNV2000Reader";
    }

    /** {@inheritDoc} */
    @Override
    public String getWriterClassName() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public MatchResult matches(List<String> lines) {

        // if the first line doesn't have '$RXN' then it can't match
        if (lines.size() < 1 || !lines.get(0).contains("$RXN")) return NO_MATCH;

        // check the header (fifth line)
        String header = lines.size() > 4 ? lines.get(4) : "";

        // atom count
        if (header.length() < 3 || !Character.isDigit(header.charAt(2))) return NO_MATCH;
        // bond count
        if (header.length() < 6 || !Character.isDigit(header.charAt(5))) return NO_MATCH;

        // check the rest of the header is only spaces and digits
        if (header.length() > 6) {
            String remainder = header.substring(6).trim();
            for (int i = 0; i < remainder.length(); ++i) {
                char c = remainder.charAt(i);
                if (!(Character.isDigit(c) || Character.isWhitespace(c))) {
                    return NO_MATCH;
                }
            }
        }

        return new MatchResult(true, this, 0);
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
