/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk.io;

import org.openscience.cdk.exception.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.*;
import java.io.*;
import java.util.*;
import javax.vecmath.*;

/**
 *  Reads a molecule from an MDL RXN file.
 *
 * @author     Egon Willighagen
 * @created    2003-07-24
 *
 * @keyword    file format, MDL RXN
 *
 * <p>References:
 *   href="http://cdk.sf.net/biblio.html#DAL92">DAL92</a>
 */
public class MDLRXNReader extends DefaultChemObjectReader {

    BufferedReader input = null;
    private org.openscience.cdk.tools.LoggingTool logger = null;

    /**
     * Contructs a new MDLReader that can read Molecule from a given Reader.
     *
     * @param  in  The Reader to read from
     */
    public MDLRXNReader(Reader in) {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
        input = new BufferedReader(in);
    }


    /**
     * Takes an object which subclasses ChemObject, e.g.Molecule, and will read
     * this (from file, database, internet etc). If the specific implementation
     * does not support a specific ChemObject it will throw an Exception.
     *
     * @param  object                              The object that subclasses
     *      ChemObject
     * @return                                     The ChemObject read
     * @exception  CDKException
     */
     public ChemObject read(ChemObject object) throws CDKException {
         if (object instanceof Reaction) {
             return (ChemObject) readReaction();
         } else {
             throw new CDKException("Only supported is Reaction.");
         }
     }


    /**
     * Read a Reaction from a file in MDL RXN format
     *
     * @return  The Reaction that was read from the MDL file.
     */
    private Reaction readReaction() throws CDKException {
        Reaction reaction = new Reaction();
        return reaction;
    }
    
    public void close() throws IOException {
        input.close();
    }
}

