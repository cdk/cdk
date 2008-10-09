/* $Revision:$
 * 
 * Copyright (C) 2005-2008   Nina Jeliazkova <nina@acad.bg>
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

import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLFormat;
import org.openscience.cdk.io.listener.IReaderListener;

/**
 * Random access of SDF file. Doesn't load molecules in memory, uses prebuilt index and seeks to find the correct record offset.
 * 
 * @author Nina Jeliazkova nina@acad.bg
 */
public class RandomAccessSDFReader extends RandomAccessReader {

    /**
     * @param file
     * @param builder
     * @throws IOException
     */
    public RandomAccessSDFReader(File file, IChemObjectBuilder builder)
            throws IOException {
        this(file, builder,null);
    }
    public RandomAccessSDFReader(File file, IChemObjectBuilder builder, IReaderListener listener)
    throws IOException {
        super(file, builder,listener);
    }
    @Override
    public ISimpleChemObjectReader createChemObjectReader() {
    	return new MDLV2000Reader();
    }
    protected boolean isRecordEnd(String line) {
        return line.equals("$$$$");
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectIO#getFormat()
     */
    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return MDLFormat.getInstance();
    }
    protected IChemObject processContent() throws CDKException {
        	/*
            return chemObjectReader.read(builder.newMolecule());
            */
            //read(IMolecule) doesn't read properties ...
            IChemObject co = chemObjectReader.read(builder.newChemFile());
            if (co instanceof IChemFile) {
                int c = ((IChemFile) co).getChemSequenceCount();
                for (int i=0; i <c;i++) {
                    Iterator cm = ((IChemFile) co).getChemSequence(i).chemModels().iterator();
                    while (cm.hasNext()) {
                    	Iterator sm = ((IChemModel)cm.next()).getMoleculeSet().molecules().iterator();
                        while (sm.hasNext()) {
                        	
                        	co = (IMolecule) sm.next();
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
	@TestMethod("testSetReader_Reader")
    public void setReader(Reader reader) throws CDKException {
		throw new UnsupportedOperationException();
		
	}
	@TestMethod("testSetReader_InputStream")
    public void setReader(InputStream reader) throws CDKException {
		throw new UnsupportedOperationException();
		
	}
	
    @TestMethod("testAccepts")
    public boolean accepts(Class classObject) {
		return chemObjectReader.accepts(classObject);
	}

}
