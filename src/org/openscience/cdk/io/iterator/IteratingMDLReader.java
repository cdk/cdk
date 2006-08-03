/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLFormat;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Iterating MDL SDF reader. It allows to iterate over all molecules
 * in the SD file, without reading them into memory first. Suitable
 * for (very) large SDF files. For parsing the molecules in the
 * SD file, it uses the <code>MDLReader</code>.
 * 
 * <p>Example use:
 * <pre>
 * File sdfFile = new File("../zinc-structures/ZINC_subset3_3D_charged_wH_maxmin1000.sdf");
 * IteratingMDLReader reader = new IteratingMDLReader(
 *   new FileInputStream(sdfFile), DefaultChemObjectBuilder.getInstance()
 * );
 * while (reader.hasNext()) {
 *   IMolecule molecule = (IMolecule)reader.next();
 * }
 * </pre>
 *
 * @cdk.module io
 *
 * @see org.openscience.cdk.io.MDLReader
 * 
 * @author     Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.created    2003-10-19
 *
 * @cdk.keyword    file format, MDL molfile
 * @cdk.keyword    file format, SDF
 */
public class IteratingMDLReader extends DefaultIteratingChemObjectReader {

    private BufferedReader input;
    private LoggingTool logger;
    private String currentLine;
    
    private boolean nextAvailableIsKnown;
    private boolean hasNext;
    private IChemObjectBuilder builder;
    private IMolecule nextMolecule;
    
    /**
     * Contructs a new IteratingMDLReader that can read Molecule from a given Reader.
     *
     * @param  in  The Reader to read from
     */
    public IteratingMDLReader(Reader in, IChemObjectBuilder builder) {
        logger = new LoggingTool(this);
        input = new BufferedReader(in);
        this.builder = builder;
        nextMolecule = null;
        nextAvailableIsKnown = false;
        hasNext = false;
    }

    /**
     * Contructs a new IteratingMDLReader that can read Molecule from a given InputStream.
     *
     * @param  in  The InputStream to read from
     */
    public IteratingMDLReader(InputStream in, IChemObjectBuilder builder) {
        this(new InputStreamReader(in), builder);
    }

    public IResourceFormat getFormat() {
        return MDLFormat.getInstance();
    }

    /**
     * Returns true if another IMolecule can be read.
     */
    public boolean hasNext() {
        if (!nextAvailableIsKnown) {
            hasNext = false;
            
            // now try to parse the next Molecule
            try {
                if (input.ready()) {
                    currentLine = input.readLine();
                    StringBuffer buffer = new StringBuffer();
                    while (currentLine != null && !currentLine.equals("M  END")) {
                        // still in a molecule
                        buffer.append(currentLine);
                        buffer.append("\n");
                        if (input.ready()) {
                            currentLine = input.readLine();
                        } else {
                            currentLine = null;
                        }
                    }
                    buffer.append(currentLine);
                    buffer.append("\n");
                    logger.debug("MDL file part read: ", buffer);
                    MDLReader reader = new MDLReader(new StringReader(buffer.toString()));
                    nextMolecule = (IMolecule)reader.read(builder.newMolecule());
                    if (nextMolecule.getAtomCount() > 0) {
                        hasNext = true;
                    } else {
                        hasNext = false;
                    }
                    // now read the data part
                    currentLine = input.readLine();
                    readDataBlockInto(nextMolecule);
                } else {
                    hasNext = false;
                }
            } catch (Exception exception) {
                logger.error("Error while reading next molecule: " +
                             exception.getMessage());
                logger.debug(exception);
                hasNext = false;
            }
            if (!hasNext) nextMolecule = null;
            nextAvailableIsKnown = true;
        }
        return hasNext;
    }

    private void readDataBlockInto(IMolecule m) throws IOException {
        String fieldName = null;
        while (currentLine != null && !(currentLine.trim().equals("$$$$"))) {
            logger.debug("looking for data header: ", currentLine);
            String str = new String(currentLine);
            if (str.startsWith("> ")) {
                // ok, should extract the field name
                int index = str.indexOf("<");
                if (index != -1) {
                    int index2 = str.substring(index).indexOf(">");
                    if (index2 != -1) {
                        fieldName = str.substring(
                        index+1,
                        index+index2
                        );
                    }
                }
                // end skip all other lines
                while (str.startsWith("> ")) {
                    logger.debug("data header line: ", currentLine);
                    currentLine = input.readLine();
                    str = new String(currentLine);
                }
                String data = "";
                while (str.trim().length() > 0) {
                    logger.debug("data line: ", currentLine);
                    data += str;
                    currentLine = input.readLine();
                    str = new String(currentLine).trim();
                }
                if (fieldName != null) {
                    logger.info("fieldName, data: ", fieldName, ", ", data);
                    m.setProperty(fieldName, data);
                }
            }
            currentLine = input.readLine();
        }
    }
    
    /**
     * Returns the next IMolecule.
     */
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

