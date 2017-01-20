/* Copyright (C) 2005-2008   Nina Jeliazkova <nina@acad.bg>
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
package org.openscience.cdk.io.random;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLFormat;
import org.openscience.cdk.io.listener.IReaderListener;

/**
 * Random access of SDF file. Doesn't load molecules in memory, uses prebuilt
 * index and seeks to find the correct record offset.
 *
 * @author Nina Jeliazkova &lt;nina@acad.bg&gt;
 * @cdk.module io
 * @cdk.githash
 */
public class RandomAccessSDFReader extends RandomAccessReader {

    /**
     * @param file
     * @param builder
     * @throws IOException
     */
    public RandomAccessSDFReader(File file, IChemObjectBuilder builder) throws IOException {
        this(file, builder, null);
    }

    public RandomAccessSDFReader(File file, IChemObjectBuilder builder, IReaderListener listener) throws IOException {
        super(file, builder, listener);
    }

    @Override
    public ISimpleChemObjectReader createChemObjectReader() {
        return new MDLV2000Reader();
    }

    @Override
    protected boolean isRecordEnd(String line) {
        return line.equals("$$$$");
    }

    /*
     * (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectIO#getFormat()
     */
    public IResourceFormat getFormat() {
        return MDLFormat.getInstance();
    }

    @Override
    protected IChemObject processContent() throws CDKException {
        /*
         * return chemObjectReader.read(builder.newInstance(IAtomContainer.class));
         */
        //read(IAtomContainer) doesn't read properties ...
        IChemObject co = chemObjectReader.read(builder.newInstance(IChemFile.class));
        if (co instanceof IChemFile) {
            int c = ((IChemFile) co).getChemSequenceCount();
            for (int i = 0; i < c; i++) {
                Iterator<IChemModel> cm = ((IChemFile) co).getChemSequence(i).chemModels().iterator();
                while (cm.hasNext()) {
                    Iterator<IAtomContainer> sm = (cm.next()).getMoleculeSet().atomContainers().iterator();
                    while (sm.hasNext()) {

                        co = sm.next();
                        break;
                    }
                    break;
                }
                cm = null;
                break;
            }
            //cs = null;
        }
        return co;

    }

    public void setReader(Reader reader) throws CDKException {
        throw new UnsupportedOperationException();

    }

    public void setReader(InputStream reader) throws CDKException {
        throw new UnsupportedOperationException();

    }

    public boolean accepts(Class<? extends IChemObject> classObject) {
        return chemObjectReader.accepts(classObject);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove entries with " + "the RandomAccessSDFReader");
    }

}
