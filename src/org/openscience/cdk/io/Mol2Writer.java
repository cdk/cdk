/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2005  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.vecmath.Point3d;

import org.openscience.cdk.interfaces.Atom;
import org.openscience.cdk.interfaces.ChemObject;
import org.openscience.cdk.interfaces.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.ChemFormat;
import org.openscience.cdk.io.formats.Mol2Format;

/**
 * An output Writer that writes molecular data into the
 * <a href="http://www.tripos.com/data/support/mol2.pdf">Tripos Mol2 format</a>.
 *
 * @cdk.module io
 *
 * @author  Egon Willighagen
 */
public class Mol2Writer extends DefaultChemObjectWriter {

    static BufferedWriter writer;

    /**
    * Constructs a new Mol2 writer.
    * @param out the stream to write the XYZ file to.
    */
    public Mol2Writer(Writer out) {
        if (out instanceof BufferedWriter) {
            writer = (BufferedWriter)out;
        } else {
            writer = new BufferedWriter(out);
        }
    }

    public Mol2Writer(OutputStream input) {
        this(new OutputStreamWriter(input));
    }

    public ChemFormat getFormat() {
        return new Mol2Format();
    }

    /**
     * Flushes the output and closes this object.
     */
    public void close() throws IOException {
        writer.close();
    }

    public void write(ChemObject object) throws CDKException {
        if (object instanceof Molecule) {
            try {
                writeMolecule((Molecule)object);
            } catch(Exception ex) {
                throw new CDKException("Error while writing Mol2 file: " + ex.getMessage(), ex);
            }
        } else {
            throw new CDKException("Mol2Writer only supports output of Molecule classes.");
        }
    }

    /**
     * Writes a single frame in XYZ format to the Writer.
     *
     * @param mol the Molecule to write
     */
    public void writeMolecule(Molecule mol) throws IOException {

        String st = "";
        boolean writecharge = true;

        try {
            // should write something
            throw new IOException("Not implemented yet!");
        } catch (IOException e) {
            throw e;
        }
    }
}


