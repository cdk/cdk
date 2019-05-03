/* Copyright (C) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import org.openscience.cdk.io.formats.IChemFormat;

/**
 * Dummy class to test the {@link WriterFactory} registerWriter functionality.
 * This is why only the getWriterClassName() is implemented.
 *
 * @cdk.module test-io
 */
public class CustomFormat implements IChemFormat {

    @Override
    public String getReaderClassName() {
        return null;
    }

    @Override
    public int getRequiredDataFeatures() {
        return 0;
    }

    @Override
    public int getSupportedDataFeatures() {
        return 0;
    }

    @Override
    public String getWriterClassName() {
        return "org.openscience.cdk.io.CustomWriter";
    }

    @Override
    public String getFormatName() {
        return null;
    }

    @Override
    public String getMIMEType() {
        return null;
    }

    @Override
    public String[] getNameExtensions() {
        return null;
    }

    @Override
    public String getPreferredNameExtension() {
        return null;
    }

    @Override
    public boolean isXMLBased() {
        return false;
    }
}
