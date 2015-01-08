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
 *
 * @author Miguel Rojas
 *
 * @cdk.module ioformats
 * @cdk.githash
 * @cdk.set    io-formats
 */
public class Chem3D_Cartesian_2Format extends AbstractResourceFormat implements IChemFormat {

    private static IResourceFormat myself = null;

    public Chem3D_Cartesian_2Format() {}

    public static IResourceFormat getInstance() {
        if (myself == null) myself = new Chem3D_Cartesian_2Format();
        return myself;
    }

    @Override
    public String getFormatName() {
        return "Chem3D Cartesian 2";
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
        return new String[]{"c3d2"};
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
