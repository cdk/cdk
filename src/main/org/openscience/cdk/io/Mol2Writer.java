/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2005-2007  Egon Willighagen <egonw@users.sf.net>
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
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.atomtype.SybylAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.Mol2Format;
import org.openscience.cdk.tools.LoggingTool;

/**
 * An output Writer that writes molecular data into the
 * <a href="http://www.tripos.com/data/support/mol2.pdf">Tripos Mol2 format</a>.
 * Writes the atoms and the bonds only at this moment.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 *
 * @author     Egon Willighagen
 */
@TestClass("org.openscience.cdk.io.Mol2WriterTest")
public class Mol2Writer extends DefaultChemObjectWriter {

    private BufferedWriter writer;
	private LoggingTool logger;
	  private SybylAtomTypeMatcher matcher;
    
    public Mol2Writer() {
    	this(new StringWriter());
    }

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

    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return Mol2Format.getInstance();
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
    @TestMethod("testClose")
    public void close() throws IOException {
        writer.close();
    }

	@TestMethod("testAccepts")
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
        matcher = SybylAtomTypeMatcher.getInstance(mol.getBuilder());
        try {

/*
#        Name: benzene 
#        Creating user name: tom 
#        Creation time: Wed Dec 28 00:18:30 1988 

#        Modifying user name: tom 
#        Modification time: Wed Dec 28 00:18:30 1988
*/

        	logger.debug("Writing header...");
            if (mol.getProperty(CDKConstants.TITLE) != null) {
                writer.write("#        Name: " + mol.getProperty(CDKConstants.TITLE));
                writer.newLine();
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

            logger.debug("Writing molecule block...");
            writer.write("@<TRIPOS>MOLECULE");
            writer.newLine();
            if (mol.getID() == null) {
                writer.write("CDKMolecule");
            } else {
                writer.write(mol.getID());
            }
            writer.newLine();
            writer.write(mol.getAtomCount() + " " + 
                        mol.getBondCount()); // that's the minimum amount of info required the format
            writer.newLine();
            writer.write("SMALL"); // no biopolymer
            writer.newLine();
            writer.write("NO CHARGES"); // other options include Gasteiger charges
            writer.newLine();

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
            logger.debug("Writing atom block...");
            writer.write("@<TRIPOS>ATOM");
            writer.newLine();
            for (int i = 0; i < mol.getAtomCount(); i++) {
            	IAtom atom = mol.getAtom(i);
                writer.write(i + " " +
                             atom.getID() + " ");
                if (atom.getPoint3d() != null) {
                    writer.write(atom.getPoint3d().x + " ");
                    writer.write(atom.getPoint3d().y + " ");
                    writer.write(atom.getPoint3d().z + " ");
                } else if (atom.getPoint2d() != null) {
                    writer.write(atom.getPoint2d().x + " ");
                    writer.write(atom.getPoint2d().y + " ");
                    writer.write(" 0.000 ");
                } else {
                    writer.write("0.000 0.000 0.000 ");
                }
                IAtomType sybylType = null;
                try {
                    sybylType = matcher.findMatchingAtomType(mol, atom);
                } catch ( CDKException e ) {
                    e.printStackTrace();
                }
                if (sybylType != null) {
                    writer.write(sybylType.getAtomTypeName());
                } else {
                    writer.write(atom.getSymbol());
                }
                writer.newLine();
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
            logger.debug("Writing bond block...");
            writer.write("@<TRIPOS>BOND");
            writer.newLine();

            int counter = 0;
            Iterator bonds = mol.bonds().iterator();
            while (bonds.hasNext()) {
                IBond bond = (IBond) bonds.next();
                writer.write(counter + " " +
                             mol.getAtomNumber(bond.getAtom(0)) + " " +
                             mol.getAtomNumber(bond.getAtom(1)) + " " +
                             ((int)bond.getOrder().ordinal()));
                writer.newLine();
                counter++;
            } 

        } catch (IOException e) {
            throw e;
        }
    }
}


