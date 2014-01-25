/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.NoSuchElementException;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.SMILESFormat;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Iterating SMILES file reader. It allows to iterate over all molecules
 * in the SMILES file, without being read into memory all. Suitable
 * for very large SMILES files. These SMILES files are expected to have one 
 * molecule on each line.
 *
 * <p>For parsing each SMILES it still uses the normal SMILESReader.
 *
 * @cdk.module smiles
 * @cdk.githash
 * @cdk.iooptions
 *
 * @see org.openscience.cdk.io.SMILESReader
 * 
 * @author     Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.created    2004-12-16
 *
 * @cdk.keyword    file format, SMILES
 */
@TestClass("org.openscience.cdk.io.iterator.IteratingSMILESReaderTest")
public class IteratingSMILESReader
extends DefaultIteratingChemObjectReader<IAtomContainer> {

    private BufferedReader input;
    private static ILoggingTool logger =
        LoggingToolFactory.createLoggingTool(IteratingSMILESReader.class);
    private SmilesParser sp = null;
    
    private boolean nextAvailableIsKnown;
    private boolean hasNext;
    private IAtomContainer nextMolecule;
    
    /**
     * Constructs a new IteratingSMILESReader that can read Molecule from a given Reader.
     *
     * @param  in  The Reader to read from
     * @param builder The builder to use
     * @see org.openscience.cdk.DefaultChemObjectBuilder
     * @see org.openscience.cdk.silent.SilentChemObjectBuilder
     */
    @TestMethod("testSMILESFileWithNames")
    public IteratingSMILESReader(Reader in, IChemObjectBuilder builder) {
        sp = new SmilesParser(builder);
        setReader(in);
    }

    /**
     * Constructs a new IteratingSMILESReader that can read Molecule from a given InputStream and IChemObjectBuilder.
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

                final String line = input.readLine();
                
                if (line == null)
                    return false;
                
                final String suffix = suffix(line);

                nextMolecule = sp.parseSmiles(line);
                if (suffix != null) {
                    nextMolecule.setProperty(CDKConstants.TITLE, suffix);
                }
                if (nextMolecule.getAtomCount() > 0) {
                    hasNext = true;
                }
                else {
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
     * Obtain the suffix after a line containing SMILES. The suffix follows
     * any ' ' or '\t' termination characters.
     * 
     * @param line input line
     * @return the suffix - or an empty line
     */
    private String suffix(final String line) {
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == ' ' || c == '\t')
                return line.substring(i + 1);
        }
        return "";
    }

    /**
     * Get the next molecule from the stream.
     *
     * @return The next molecule
     */
    @TestMethod("testSMILESFileWithNames,testSMILESFileWithSpacesAndTabs,testSMILESTitles,testSMILESFile")
    public IAtomContainer next() {
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

