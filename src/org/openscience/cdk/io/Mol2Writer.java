/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2005-2006  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.interfaces.ISetOfReactions;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.Mol2Format;
import org.openscience.cdk.tools.LoggingTool;

/**
 * An output Writer that writes molecular data into the
 * <a href="http://www.tripos.com/data/support/mol2.pdf">Tripos Mol2 format</a>.
 * Writes the atoms and the bonds only at this moment.
 *
 * @cdk.module io
 *
 * @author  Egon Willighagen
 */
public class Mol2Writer extends DefaultChemObjectWriter {

	static BufferedWriter writer;
	private LoggingTool logger;
    
    /**
    * Constructs a new Mol2 writer.
    * @param out the stream to write the Mol2 file to.
    */
    public Mol2Writer(Writer out) {
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

    public Mol2Writer(OutputStream output) {
        this(new OutputStreamWriter(output));
    }

    public IResourceFormat getFormat() {
        return new Mol2Format();
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
     * Flushes the output and closes this object.
     */
    public void close() throws IOException {
        writer.close();
    }

	public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IMolecule.class.equals(interfaces[i])) return true;
		}
		return false;
	}

    public void write(IChemObject object) throws CDKException {
        if (object instanceof IMolecule) {
            try {
                writeMolecule((IMolecule)object);
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
    public void writeMolecule(IMolecule mol) throws IOException {

        String st = "";
        boolean writecharge = true;

        try {

/*
#        Name: benzene 
#        Creating user name: tom 
#        Creation time: Wed Dec 28 00:18:30 1988 

#        Modifying user name: tom 
#        Modification time: Wed Dec 28 00:18:30 1988
*/

            if (mol.getProperty(CDKConstants.TITLE) != null) {
                writer.write("#        Name: " + mol.getProperty(CDKConstants.TITLE) + "\n");
            }
            // FIXME: add other types of meta data
            writer.newLine();

/*
@<TRIPOS>MOLECULE 
benzene 
12 12 1  0       0 
SMALL 
NO_CHARGES 
*/

            writer.write("@<TRIPOS>MOLECULE\n");
            writer.write(mol.getID() + "\n");
            writer.write(mol.getAtomCount() + " " + 
                        mol.getBondCount() +
                        "\n"); // that's the minimum amount of info required the format
            writer.write("SMALL\n"); // no biopolymer
            writer.write("NO CHARGES\n"); // other options include Gasteiger charges

/*
@<TRIPOS>ATOM 
1       C1      1.207   2.091   0.000   C.ar    1       BENZENE 0.000 
2       C2      2.414   1.394   0.000   C.ar    1       BENZENE 0.000 
3       C3      2.414   0.000   0.000   C.ar    1       BENZENE 0.000 
4       C4      1.207   -0.697  0.000   C.ar    1       BENZENE 0.000 
5       C5      0.000   0.000   0.000   C.ar    1       BENZENE 0.000 
6       C6      0.000   1.394   0.000   C.ar    1       BENZENE 0.000 
7       H1      1.207   3.175   0.000   H       1       BENZENE 0.000 
8       H2      3.353   1.936   0.000   H       1       BENZENE 0.000 
9       H3      3.353   -0.542  0.000   H       1       BENZENE 0.000 
10      H4      1.207   -1.781  0.000   H       1       BENZENE 0.000 
11      H5      -0.939  -0.542  0.000   H       1       BENZENE 0.000 
12      H6      -0.939  1.936   0.000   H       1       BENZENE 0.000 
*/

            // write atom block
            writer.write("@<TRIPOS>ATOM\n");
            IAtom[] atoms = mol.getAtoms();
            for (int i=0; i<atoms.length; i++) {
                writer.write(i + " " +
                             atoms[i].getID() + " ");
                if (atoms[i].getPoint3d() != null) {
                    writer.write(atoms[i].getX3d() + " ");
                    writer.write(atoms[i].getY3d() + " ");
                    writer.write(atoms[i].getZ3d() + " ");
                } else if (atoms[i].getPoint2d() != null) {
                    writer.write(atoms[i].getX2d() + " ");
                    writer.write(atoms[i].getY2d() + " ");
                    writer.write(" 0.000 ");
                } else {
                    writer.write("0.000 0.000 0.000 ");
                }
                writer.write(atoms[i].getSymbol()); // FIXME: should use perceived Mol2 Atom Types!
            }

/*
@<TRIPOS>BOND 
1       1       2       ar 
2       1       6       ar 
3       2       3       ar 
4       3       4       ar 
5       4       5       ar 
6       5       6       ar 
7       1       7       1 
8       2       8       1 
9       3       9       1 
10      4       10      1 
11      5       11      1 
12      6       12      1
*/

            // write bond block
            writer.write("@<TRIPOS>BOND\n");
            IBond[] bonds = mol.getBonds();
            for (int i=0; i<bonds.length; i++) {
                writer.write(i + " " +
                             mol.getAtomNumber(bonds[i].getAtomAt(0)) + " " +
                             mol.getAtomNumber(bonds[i].getAtomAt(1)) + " " +
                             ((int)bonds[i].getOrder()) + 
                             "\n");
            } 

        } catch (IOException e) {
            throw e;
        }
    }
}


