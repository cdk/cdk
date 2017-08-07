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
import org.openscience.cdk.exception.CDKException;
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
 * molecule on each line. If a line could not be parsed and empty molecule is
 * returned and the property {@link #BAD_SMILES_INPUT} is set to the attempted
 * input. The error is also logged.
 *
 * <p>For parsing each SMILES it still uses the normal SMILESReader.
 *
 * @cdk.module smiles
 * @cdk.githash
 * @cdk.iooptions
 *
 * @see org.openscience.cdk.io.SMILESReader
 *
 * @author Egon Willighagen &lt;egonw@sci.kun.nl&gt;
 * @cdk.created    2004-12-16
 *
 * @cdk.keyword    file format, SMILES
 */
public class IteratingSMILESReader extends DefaultIteratingChemObjectReader<IAtomContainer> {

    private BufferedReader           input;
    private static ILoggingTool      logger           = LoggingToolFactory
                                                              .createLoggingTool(IteratingSMILESReader.class);
    private SmilesParser             sp               = null;

    private boolean                  nextAvailableIsKnown;
    private boolean                  hasNext;
    private IAtomContainer           nextMolecule;
    private final IChemObjectBuilder builder;

    /** Store the problem input as a property. */
    public static final String       BAD_SMILES_INPUT = "bad.smiles.input";

    /**
     * Constructs a new IteratingSMILESReader that can read Molecule from a given Reader.
     *
     * @param  in  The Reader to read from
     * @param builder The builder to use
     * @see org.openscience.cdk.DefaultChemObjectBuilder
     * @see org.openscience.cdk.silent.SilentChemObjectBuilder
     */
    public IteratingSMILESReader(Reader in, IChemObjectBuilder builder) {
        sp = new SmilesParser(builder);
        setReader(in);
        this.builder = builder;
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
    @Override
    public IResourceFormat getFormat() {
        return SMILESFormat.getInstance();
    }

    /**
     * Checks whether there is another molecule to read.
     *
     * @return  true if there are molecules to read, false otherwise
     */
    @Override
    public boolean hasNext() {
        if (!nextAvailableIsKnown) {
            hasNext = false;

            // now try to parse the next Molecule
            try {

                final String line = input.readLine();

                if (line == null) {
                    nextAvailableIsKnown = true;
                    return false;
                }

                hasNext = true;
                final String suffix = suffix(line);

                nextMolecule = readSmiles(line);
                nextMolecule.setProperty(CDKConstants.TITLE, suffix);

            } catch (Exception exception) {
                logger.error("Unexpected problem: ", exception.getMessage());
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
            if (c == ' ' || c == '\t') return line.substring(i + 1);
        }
        return "";
    }

    /**
     * Read the SMILES given in the input line - or return an empty container.
     *
     * @param line input line
     * @return the read container (or an empty one)
     */
    private IAtomContainer readSmiles(final String line) {
        try {
            return sp.parseSmiles(line);
        } catch (CDKException e) {
            logger.error("Error while reading the SMILES from: " + line + ", ", e);
            final IAtomContainer empty = builder.newInstance(IAtomContainer.class, 0, 0, 0, 0);
            empty.setProperty(BAD_SMILES_INPUT, line);
            return empty;
        }
    }

    /**
     * Get the next molecule from the stream.
     *
     * @return The next molecule
     */
    @Override
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
    @Override
    public void close() throws IOException {
        if (input != null) input.close();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setReader(Reader reader) {
        if (reader instanceof BufferedReader) {
            input = (BufferedReader) reader;
        } else {
            input = new BufferedReader(reader);
        }
        nextMolecule = null;
        nextAvailableIsKnown = false;
        hasNext = false;
    }

    @Override
    public void setReader(InputStream reader) {
        setReader(new InputStreamReader(reader));
    }

}
