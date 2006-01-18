/* $RCSfile$ 
 * $Author$ 
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.vecmath.Vector3d;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.UnsupportedChemObjectException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.ChemSequence;
import org.openscience.cdk.interfaces.Crystal;
import org.openscience.cdk.io.formats.ChemFormat;
import org.openscience.cdk.io.formats.CrystClustFormat;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Rather stupid file format used for storing crystal information.
 *
 * @author Egon Willighagen
 * @cdk.created 2004-01-01
 *
 * @cdk.module extra
 */
public class CrystClustWriter extends DefaultChemObjectWriter {

	static BufferedWriter writer;
    private LoggingTool logger;

    /**
     * Constructs a new CrystClustWriter class. Output will be stored in the Writer
     * class given as parameter.
     *
     * @param out Writer to redirect the output to.
     */
    public CrystClustWriter(Writer out) {
    	logger = new LoggingTool(this);
    	try {
    		if (out instanceof BufferedWriter) {
                writer = (BufferedWriter)out;
            } else {
                writer = new BufferedWriter(out);
            }
        } catch (Exception exc) {
        }
    }

    public CrystClustWriter(OutputStream output) {
        this(new OutputStreamWriter(output));
    }
    
    public CrystClustWriter() {
        this(new StringWriter());
    }
    
    public ChemFormat getFormat() {
        return new CrystClustFormat();
    }
    
    public void setWriter(Writer out) throws CDKException {
    	if (out instanceof BufferedWriter) {
            writer = (BufferedWriter)out;
        } else {
            writer = new BufferedWriter(out);
        }
    }

    public void setWriter(OutputStream output) throws CDKException {
    	setWriter(new OutputStreamWriter(output));
    }
    
    
    /**
     * Serializes the IChemObject to CrystClust format and redirects it to the output Writer.
     *
     * @param object A Molecule of SetOfMolecules object
     */
    public void write(IChemObject object) throws UnsupportedChemObjectException {
        if (object instanceof Crystal) {
            write((Crystal)object);
        }   else if (object instanceof ChemSequence) {
            write((ChemSequence)object);
        } else {
            throw new UnsupportedChemObjectException("This object type is not supported.");
        }
    };

    /**
     * Flushes the output and closes this object
     */
    public void close() throws IOException {
    	writer.close();
    }

    // Private procedures

    private void write(ChemSequence cs) throws UnsupportedChemObjectException {
        int count = cs.getChemModelCount();
        for (int i=0; i < count; i++) {
            write("frame: " + (i+1) + "\n");
            write(cs.getChemModel(i).getCrystal());
        }
    }

    /**
     * Writes a single frame to the Writer.
     *
     * <p>Format:
     * <pre>
     *            line      data
     *           -------    --------------------------
     *              1       spacegroup
     *            2,3,4     cell parameter: a
     *            5,6,7                     b
     *            8,9,10                    c
     *             11       number of atoms
     *             12       number of asym. units
     *            13-16     atomtype: charge, atomcoord x, y, z
     *            17-20     idem second atom
     *            21-24     idem third atom etc
     * </pre>
     *
     * @param crystal the Crystal to serialize
     */
    private void write(Crystal crystal) {

        String sg = crystal.getSpaceGroup();
        if ("P 2_1 2_1 2_1".equals(sg)) {
            write("P 21 21 21 (1)\n");
        } else {
            write("P 1 (1)\n");
        }

        // output unit cell axes
        writeVector3d(crystal.getA());
        writeVector3d(crystal.getB());
        writeVector3d(crystal.getC());

        // output number of atoms
        int noatoms = crystal.getAtomCount();
        write(new Integer(noatoms).toString());
        write("\n");

        // output number of asym. units (Z)
        if (sg.equals("P1")) {
            write("1\n");
        } else {
            // duno
            write("1\n");
        }

        // output atoms
        for (int i = 0; i < noatoms; i++) {
            // output atom sumbol
        	org.openscience.cdk.interfaces.IAtom atom = crystal.getAtomAt(i);
            write(atom.getSymbol());
            write(":");
            // output atom charge
            write(new Double(atom.getCharge()).toString() + "\n");
            // output coordinates
            write(new Double(atom.getX3d()).toString() + "\n");
            write(new Double(atom.getY3d()).toString() + "\n");
            write(new Double(atom.getZ3d()).toString() + "\n");
        }
    
    }

    private void write(String s) {
        try {
        	writer.write(s);
        } catch (IOException e) {
            System.err.println("CMLWriter IOException while printing \"" +
                                s + "\":\n" + e.toString());
        }
    }
    
    private void writeVector3d(Vector3d vector) {
        write(new Double(vector.x).toString());
        write("\n");
        write(new Double(vector.y).toString());
        write("\n");
        write(new Double(vector.z).toString());
        write("\n");
    }

}
