/* Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
import java.io.StringReader;
import java.util.NoSuchElementException;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.PCCompoundASNReader;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.PubChemSubstancesASNFormat;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * Iterating PubChem PCCompound ASN reader.
 *
 * @cdk.module io
 * @cdk.githash
 * @cdk.iooptions
 *
 * @see org.openscience.cdk.io.PCCompoundASNReader
 *
 * @author       Egon Willighagen <egonw@users.sf.net>
 * @cdk.created  2008-05-05
 *
 * @cdk.keyword  file format, ASN
 * @cdk.keyword  PubChem
 */
public class IteratingPCCompoundASNReader extends DefaultIteratingChemObjectReader<IAtomContainer> {

    private BufferedReader      input;
    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(IteratingPCCompoundASNReader.class);
    private IChemObjectBuilder  builder;

    private boolean             nextAvailableIsKnown;
    private boolean             hasNext;
    private IAtomContainer      nextMolecule;

    private String              currentLine;
    private int                 depth;

    /**
     * Constructs a new IteratingPCCompoundASNReader that can read Molecule from a given Reader.
     *
     * @param  in  The Reader to read from
     */
    public IteratingPCCompoundASNReader(Reader in, IChemObjectBuilder builder) {
        this.builder = builder;
        setReader(in);
    }

    /**
     * Constructs a new IteratingPCCompoundASNReader that can read Molecule from a given InputStream and IChemObjectBuilder.
     *
     * @param in      The input stream
     * @param builder The builder
     */
    public IteratingPCCompoundASNReader(InputStream in, IChemObjectBuilder builder) {
        this(new InputStreamReader(in), builder);
    }

    @Override
    public IResourceFormat getFormat() {
        return PubChemSubstancesASNFormat.getInstance();
    }

    @Override
    public boolean hasNext() {
        if (!nextAvailableIsKnown) {
            hasNext = false;

            // now try to read the next molecule
            try {
                currentLine = input.readLine();
                boolean endMoleculeFound = false;
                boolean startMoleculeFound = false;

                StringBuffer buffer = new StringBuffer();
                while (!startMoleculeFound && currentLine != null) {
                    int depthDiff = countBrackets(currentLine);
                    depth += depthDiff;
                    if (depthDiff > 0 && depth == 3) {
                        String command = getCommand(currentLine);
                        if (command.equals("compound")) {
                            startMoleculeFound = true;
                            buffer.append("PC-Compound ::= {\n");
                        }
                    }
                    currentLine = input.readLine();
                }
                while (!endMoleculeFound && currentLine != null) {
                    int depthDiff = countBrackets(currentLine);
                    depth += depthDiff;
                    if (depthDiff < 0 && depth == 2) {
                        endMoleculeFound = true;
                        buffer.append("}\n");
                        break;
                    } else {
                        buffer.append(currentLine).append('\n');
                    }
                    currentLine = input.readLine();
                }
                if (startMoleculeFound && endMoleculeFound) {
                    hasNext = true;
                    PCCompoundASNReader asnReader = new PCCompoundASNReader(new StringReader(buffer.toString()));
                    IChemFile cFile = (IChemFile) asnReader.read(builder.newInstance(IChemFile.class));
                    asnReader.close();
                    nextMolecule = ChemFileManipulator.getAllAtomContainers(cFile).get(0);
                }
            } catch (IOException | IllegalArgumentException | CDKException exception) {
                logger.error("Error while reading next molecule: ", exception.getMessage());
                logger.debug(exception);
                exception.printStackTrace();
                hasNext = false;
            }
            if (!hasNext) nextMolecule = null;
            nextAvailableIsKnown = true;
        }
        return hasNext;
    }

    private int countChars(String copy, char character) {
        int occurences = 0;
        for (int i = 0; i < copy.length(); i++) {
            if (character == copy.charAt(i)) occurences++;
        }
        return occurences;
    }

    private int countBrackets(String currentLine) {
        int bracketsOpen = countChars(currentLine, '{');
        int bracketsClose = countChars(currentLine, '}');
        return bracketsOpen - bracketsClose;
    }

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

    @Override
    public void close() throws IOException {
        input.close();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private String getCommand(String line) {
        StringBuffer buffer = new StringBuffer();
        int i = 0;
        boolean foundBracket = false;
        while (i < line.length() && !foundBracket) {
            char currentChar = line.charAt(i);
            if (currentChar == '{') {
                foundBracket = true;
            } else {
                buffer.append(currentChar);
            }
            i++;
        }
        return foundBracket ? buffer.toString().trim() : null;
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
