/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.Molecule;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.SMILESFormat;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Iterating SMILES file reader. It allows to iterate over all molecules
 * in the SMILES file, without being read into memory all. Suitable
 * for very large SMILES files. These SMILES files are expected to have one 
 * molecule on each line.
 *
 * <p>For parsing each SMILES it still uses the normal SMILESReader.
 *
 * @cdk.module smiles
 *
 * @see org.openscience.cdk.io.SMILESReader
 * 
 * @author     Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.created    2004-12-16
 *
 * @cdk.keyword    file format, SMILES
 */
public class IteratingSMILESReader extends DefaultIteratingChemObjectReader {

    private BufferedReader input;
    private LoggingTool logger;
    private String currentLine;
    private SmilesParser sp = null;
    
    private boolean nextAvailableIsKnown;
    private boolean hasNext;
    private Molecule nextMolecule;
    
    /**
     * Contructs a new IteratingSMILESReader that can read Molecule from a given Reader.
     *
     * @param  in  The Reader to read from
     */
    public IteratingSMILESReader(Reader in) {
        logger = new LoggingTool(this);
        input = new BufferedReader(in);
        sp = new SmilesParser();
        nextMolecule = null;
        nextAvailableIsKnown = false;
        hasNext = false;
    }

    /**
     * Contructs a new IteratingSMILESReader that can read Molecule from a given InputStream.
     *
     * @param  in  The InputStream to read from
     */
    public IteratingSMILESReader(InputStream in) {
        this(new InputStreamReader(in));
    }

    public IChemFormat getFormat() {
        return new SMILESFormat();
    }

    public boolean hasNext() {
        if (!nextAvailableIsKnown) {
            hasNext = false;
            
            // now try to parse the next Molecule
            try {
                if (input.ready()) {
                    currentLine = input.readLine().trim();
                    logger.debug("Line: ", currentLine);
                    int indexSpace = currentLine.indexOf(" ");
                    String SMILES = currentLine;
                    String name = null;
                
                    if (indexSpace != -1) {
                        logger.debug("Space found at index: ", indexSpace);
                        SMILES = currentLine.substring(0,indexSpace);
                        name = currentLine.substring(indexSpace+1);
                        logger.debug("Line contains SMILES and name: ", SMILES,
                                     " + " , name);
                    }
                
                    nextMolecule = sp.parseSmiles(SMILES);
                    if (name != null) {
                        nextMolecule.setProperty("SMIdbNAME", name);
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
    
    public Object next() {
        if (!nextAvailableIsKnown) {
            hasNext();
        }
        nextAvailableIsKnown = false;
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        return nextMolecule;
    }
    
    public void close() throws IOException {
        input.close();
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

