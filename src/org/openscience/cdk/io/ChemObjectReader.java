/* $RCSfile$
 * $Author$
 * $Date$  
 * $Revision$
 *
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.io;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.exception.CDKException;

/**
 * This class is the interface that all IO readers should implement.
 * Programs need only care about this interface for any kind of IO.
 * Currently, database IO and file IO is supported.
 *
 * <p>The easiest way to implement a new ChemObjectReader is to
 * subclass the DefaultChemObjectReader. If a Reader is develop that
 * is not yet implemented, but just used to format detection (allowed
 * too!), then it should subclass the DummyReader.
 *
 * <p>I don't know how this should be enforced, but a Reader should
 * also provide an empty constructor so that ClassLoader/getInstance()
 * can be used to instantiate a ChemObjectReader.
 *
 * @cdk.module io
 *
 * @see DefaultChemObjectReader
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 **/
public interface ChemObjectReader extends ChemObjectIO {

    /**
     * Reads an ChemObject of type "object" from input. The constructor
     * of the actual implementation may take a Reader as input to get
     * a very flexible reader that can read from string, files, etc.
     * 
     * @param  object    the type of object to return
     * @return returns an object of that contains the content (or 
     *         part) of the input content
     *
     * @exception CDKException it is thrown if
     *            the type of information is not available from 
     *            the input
     **/
    public ChemObject read(ChemObject object) throws CDKException, IOException;

    /**
     * Method that checks wether the given line is part of the format
     * read by this reader.
     *
     * @param lineNumber  number of the line
     * @param line        line in the file being checked
     *
     * @return true if the line is of a file format read by this reader
     */
    public boolean matches(int lineNumber, String line);

    /**
     * Sets the Reader from which this ChemObjectReader should read
     * the contents.
     */
    public void setReader(ChemObjectReader reader) throws CDKException;
}

