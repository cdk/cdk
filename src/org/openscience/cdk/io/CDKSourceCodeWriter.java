/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.formats.CDKSourceCodeFormat;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.tools.IDCreator;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Converts a Molecule into CDK source code that would build the same
 * molecule. It's typical use is:
 * <pre>
 * StringWriter stringWriter = new StringWriter();
 * ChemObjectWriter writer = new CDKSourceCodeWriter(stringWriter);
 * writer.write((Molecule)molecule);
 * writer.close();
 * System.out.print(stringWriter.toString());
 * </pre>
 *
 * @cdk.module io
 *
 * @author  Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.created 2003-10-01
 * 
 * @cdk.keyword file format, CDK source code
 */
public class CDKSourceCodeWriter extends DefaultChemObjectWriter {

	static BufferedWriter writer;
    private LoggingTool logger;

    /**
     * Contructs a new CDKSourceCodeWriter.
     *
     * @param   out  The Writer to write to
     */
    public CDKSourceCodeWriter(Writer out) {
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

    public CDKSourceCodeWriter(OutputStream out) {
        this(new OutputStreamWriter(out));
    }
    public CDKSourceCodeWriter() {
        this(new StringWriter());
    }
    public IChemFormat getFormat() {
        return new CDKSourceCodeFormat();
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

    public void write(IChemObject object) throws CDKException {
        if (object instanceof IMolecule) {
            try {
                writeMolecule((IMolecule)object);
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                logger.debug(ex);
            }
        } else {
            throw new CDKException("Only supported is writing of Molecule objects.");
        }
    }
    
    public void writeMolecule(IMolecule molecule) throws Exception {
        writer.write("{\n");
        writer.write("  Molecule mol = new Molecule();\n");
        IDCreator idCreator = new IDCreator();
        idCreator.createIDs(molecule);
        org.openscience.cdk.interfaces.IAtom[] atoms = molecule.getAtoms();
        for (int i=0; i<atoms.length; i++) {
        	org.openscience.cdk.interfaces.IAtom atom = atoms[i];
            writeAtom(atom);
            writer.write("  mol.addAtom(" + atom.getID() + ");\n");
        }
        org.openscience.cdk.interfaces.IBond[] bonds = molecule.getBonds();
        for (int i=0; i<bonds.length; i++) {
        	org.openscience.cdk.interfaces.IBond bond = bonds[i];
            writeBond(bond);
            writer.write("  mol.addBond(" + bond.getID() + ");\n");
        }
        writer.write("}\n");
    }

    public void writeAtom(org.openscience.cdk.interfaces.IAtom atom) throws Exception {
        writer.write("  Atom " + atom.getID() + " = new Atom(\"" + atom.getSymbol() +
                     "\");\n");
    }
    
    public void writeBond(org.openscience.cdk.interfaces.IBond bond) throws Exception {
        writer.write("  Bond " + bond.getID() + " = new Bond(" + 
                     bond.getAtomAt(0).getID() + ", " +
                     bond.getAtomAt(1).getID() + ", " +
                     bond.getOrder() + ");\n");
    }
    
}


