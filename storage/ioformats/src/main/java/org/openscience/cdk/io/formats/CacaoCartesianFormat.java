/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
 * See <a href="http://www.chembio.uoguelph.ca/oakley/310/cacao/cacao.htm">here</a>.
 *
 * @author Miguel Rojas
 *
 * @cdk.module ioformats
 * @cdk.githash
 */
public class CacaoCartesianFormat extends AbstractResourceFormat implements IChemFormat {

    private static IResourceFormat myself = null;

    public CacaoCartesianFormat() {}

    public static IResourceFormat getInstance() {
        if (myself == null) myself = new CacaoCartesianFormat();
        return myself;
    }

    @Override
    public String getFormatName() {
        return "Cacao Cartesian";
    }

    @Override
    public String getMIMEType() {
        return null;
    }

    @Override
    public String getPreferredNameExtension() {
        return getNameExtensions()[0];
    }

    @Override
    public String[] getNameExtensions() {
        return new String[]{"caccrt"};
    }

    @Override
    public String getReaderClassName() {
        return null;
    }

    @Override
    public String getWriterClassName() {
        return null;
    }

    @Override
    public boolean isXMLBased() {
        return false;
    }

    @Override
    public int getSupportedDataFeatures() {
        return DataFeatures.NONE;
    }

    @Override
    public int getRequiredDataFeatures() {
        return DataFeatures.NONE;
    }
}
