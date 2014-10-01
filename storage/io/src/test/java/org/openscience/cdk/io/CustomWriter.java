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

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.listener.IChemObjectIOListener;
import org.openscience.cdk.io.setting.IOSetting;

/**
 * Dummy class to test the {@link WriterFactory} registerWriter functionality.
 *
 * @cdk.module test-io
 */
public class CustomWriter extends ChemObjectIO implements IChemObjectWriter {

    @Override
    public void setWriter(Writer writer) throws CDKException {}

    @Override
    public void setWriter(OutputStream writer) throws CDKException {}

    @Override
    public void write(IChemObject object) throws CDKException {}

    @Override
    public boolean accepts(Class<? extends IChemObject> classObject) {
        return false;
    }

    @Override
    public void addChemObjectIOListener(IChemObjectIOListener listener) {}

    @Override
    public void close() throws IOException {}

    @Override
    public IResourceFormat getFormat() {
        return null;
    }

    @Override
    public IOSetting[] getIOSettings() {
        return null;
    }

    @Override
    public void removeChemObjectIOListener(IChemObjectIOListener listener) {}

}
