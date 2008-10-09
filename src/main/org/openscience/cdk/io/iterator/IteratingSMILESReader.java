/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io.iterator;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.SMILESFormat;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LoggingTool;

import java.io.*;
import java.util.NoSuchElementException;

/**
 * Iterating SMILES file reader. It allows to iterate over all molecules
 * in the SMILES file, without being read into memory all. Suitable
 * for very large SMILES files. These SMILES files are expected to have one 
 * molecule on each line.
 *
 * <p>For parsing each SMILES it still uses the normal SMILESReader.
 *
 * @cdk.module smiles
 * @cdk.svnrev  $Revision$
 *
 * @see org.openscience.cdk.io.SMILESReader
 * 
 * @author     Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.created    2004-12-16
 *
 * @cdk.keyword    file format, SMILES
 */
@TestClass("org.openscience.cdk.io.iterator.IteratingSMILESReaderTest")
public class IteratingSMILESReader extends DefaultIteratingChemObjectReader {

    private BufferedReader input;
    private LoggingTool logger;
    private String currentLine;
    private SmilesParser sp = null;
    
    private boolean nextAvailableIsKnown;
    private boolean hasNext;
    private IMolecule nextMolecule;
    
    /**
     * Contructs a new IteratingSMILESReader that can read Molecule from a given Reader.
     *
     * @param  in  The Reader to read from
     * @param builder The builder to use
     * @see org.openscience.cdk.DefaultChemObjectBuilder
     * @see org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder
     */
    @TestMethod("testSMILESFileWithNames")
    public IteratingSMILESReader(Reader in, IChemObjectBuilder builder) {
        logger = new LoggingTool(this);
        sp = new SmilesParser(builder);
        setReader(in);
    }

    /**
     * Contructs a new IteratingSMILESReader that can read Molecule from a given InputStream.
     *
     * This method will use @link{DefaultChemObjectBuilder} to build the actual molecules
     *
     * @param  in  The InputStream to read from
     */
    @TestMethod("testSMILESFileWithNames")
    public IteratingSMILESReader(InputStream in) {
        this(new InputStreamReader(in), DefaultChemObjectBuilder.getInstance());
    }

    /**
     * Contructs a new IteratingSMILESReader that can read Molecule from a given InputStream and IChemObjectBuilder.
     *
     * @param in      The input stream
     * @param builder The builder
     */
    public IteratingSMILESReader(InputStream in, IChemObjectBuilder builder) {
        this(new InputStreamReader(in), builder);
    }

    /**
     * Get the format for this reader.
     *
     * @return An instance of {@link org.openscience.cdk.io.formats.SMILESFormat}
     */
    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return SMILESFormat.getInstance();
    }

    /**
     * Checks whether there is another molecule to read.
     *
     * @return  true if there are molecules to read, false otherwise
     */
    @TestMethod("testSMILESFileWithNames,testSMILESFileWithSpacesAndTabs,testSMILESTitles,testSMILESFile")
    public boolean hasNext() {
        if (!nextAvailableIsKnown) {
            hasNext = false;
            
            // now try to parse the next Molecule
            try {
                if (input.ready()) {
                    currentLine = input.readLine().trim();
                    logger.debug("Line: ", currentLine);

                    int indexSpace = currentLine.indexOf(" ");
                    if (indexSpace == -1) indexSpace = currentLine.indexOf("\t");

                    String SMILES = currentLine;
                    String name = null;

                    if (indexSpace != -1) {
                        logger.debug("Space found at index: ", indexSpace);
                        SMILES = currentLine.substring(0,indexSpace);
                        name = currentLine.substring(indexSpace+1);
                        name = name.trim();
                        logger.debug("Line contains SMILES and name: ", SMILES,
                                     " + " , name);
                    }
                
                    nextMolecule = sp.parseSmiles(SMILES);
                    if (name != null) {
                        nextMolecule.setProperty(CDKConstants.TITLE, name);
                    }
                    if (nextMolecule.getAtomCount() > 0) {
                        hasNext = true;
                    } else {
                        hasNext = false;
                    }
                } else {
                    hasNext = false;
                }
            } catch (Exception exception) {
                logger.error("Error while reading next molecule: ", exception.getMessage());
                logger.debug(exception);
                hasNext = false;
            }
            if (!hasNext) nextMolecule = null;
            nextAvailableIsKnown = true;
        }
        return hasNext;
    }

    /**
     * Get the next molecule from the stream.
     *
     * @return The next molecule
     */
    @TestMethod("testSMILESFileWithNames,testSMILESFileWithSpacesAndTabs,testSMILESTitles,testSMILESFile")
    public IChemObject next() {
        if (!nextAvailableIsKnown) {
            hasNext();
        }
        nextAvailableIsKnown = false;
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        return nextMolecule;
    }

    /**
     * Close the reader.
     *
     * @throws IOException if there is an error during closing
     */
    @TestMethod("testSMILESFileWithNames,testSMILESFileWithSpacesAndTabs,testClose")
    public void close() throws IOException {
        input.close();
    }

    @TestMethod("testRemove")
    public void remove() {
        throw new UnsupportedOperationException();
    }

	@TestMethod("testSetReader_Reader")
    public void setReader(Reader reader) {
		if (reader instanceof BufferedReader) {
			input = (BufferedReader)reader;
		} else {
			input = new BufferedReader(reader);
		}
        nextMolecule = null;
        nextAvailableIsKnown = false;
        hasNext = false;
    }

    @TestMethod("testSetReader1,testSetReader_InputStream")
    public void setReader(InputStream reader) {
	    setReader(new InputStreamReader(reader));
    }

}

