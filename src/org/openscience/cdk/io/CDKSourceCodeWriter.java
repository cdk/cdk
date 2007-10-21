/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2003-2007  Egon Willighagen <egonw@users.sf.net>
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

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.formats.CDKSourceCodeFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.tools.DataFeatures;
import org.openscience.cdk.tools.IDCreator;
import org.openscience.cdk.tools.LoggingTool;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.io.*;
import java.util.Iterator;

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
 * @cdk.svnrev  $Revision$
 *
 * @author  Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.created 2003-10-01
 * 
 * @cdk.keyword file format, CDK source code
 */
public class CDKSourceCodeWriter extends DefaultChemObjectWriter {

    private BufferedWriter writer;
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
    public IResourceFormat getFormat() {
        return CDKSourceCodeFormat.getInstance();
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
    	writer.flush();
        writer.close();
    }

	public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IMolecule.class.equals(interfaces[i])) return true;
			if (IAtomContainer.class.equals(interfaces[i])) return true;
		}
		return false;
	}

    public void write(IChemObject object) throws CDKException {
        if (object instanceof IMolecule) {
            try {
                writeMolecule((IMolecule)object);
                writer.flush();
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                logger.debug(ex);
                throw new CDKException("Exception while writing to CDK source code: " + ex.getMessage(), ex);
            }
        } else if (object instanceof IAtomContainer) {
            try {
                writeAtomContainer((IAtomContainer)object);
                writer.flush();
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                logger.debug(ex);
                throw new CDKException("Exception while writing to CDK source code: " + ex.getMessage(), ex);
            } 
        } else {
            throw new CDKException("Only supported is writing of Molecule objects.");
        }
    }
    
    public void writeMolecule(IMolecule molecule) throws Exception {
        writer.write("{\n");
        writer.write("  IMolecule mol = new Molecule();\n");
        IDCreator.createIDs(molecule);
        java.util.Iterator atoms = molecule.atoms();
        while (atoms.hasNext()) {
        	IAtom atom = (IAtom)atoms.next();
            writeAtom(atom);
            writer.write("  mol.addAtom(" + atom.getID() + ");\n");
        }

        Iterator bonds = molecule.bonds();
        while (bonds.hasNext()) {
            IBond bond = (IBond) bonds.next();
            writeBond(bond);
            writer.write("  mol.addBond(" + bond.getID() + ");\n");
        }
        writer.write("}\n");
    }

    public void writeAtomContainer(IAtomContainer molecule) throws Exception {
        writer.write("{\n");
        writer.write("  IAtomContainer mol = new AtomContainer();\n");
        IDCreator.createIDs(molecule);
        java.util.Iterator atoms = molecule.atoms();
        while (atoms.hasNext()) {
        	IAtom atom = (IAtom)atoms.next();
            writeAtom(atom);
            writer.write("  mol.addAtom(" + atom.getID() + ");\n");
        }

        Iterator bonds = molecule.bonds();
        while (bonds.hasNext()) {
            IBond bond = (IBond) bonds.next();
            writeBond(bond);
            writer.write("  mol.addBond(" + bond.getID() + ");\n");
        }
        writer.write("}\n");
    }

    public void writeAtom(IAtom atom) throws Exception {
        writer.write("  IAtom " + atom.getID() + " = mol.getBuilder().newAtom(\"" + atom.getSymbol() +
                     "\");\n");
        if (atom.getPoint2d() != null) {
        	Point2d p2d = atom.getPoint2d();
        	writer.write("  " + atom.getID() + ".setPoint2d(new Point2d(" +
        		p2d.x + ", " + p2d.y + "));");
        }
        if (atom.getPoint3d() != null) {
        	Point3d p3d = atom.getPoint3d();
        	writer.write("  " + atom.getID() + ".setPoint3d(new Point3d(" +
        		p3d.x + ", " + p3d.y + ", " + p3d.z + "));");
        }
    }
    
    public void writeBond(IBond bond) throws Exception {
        writer.write("  IBond " + bond.getID() + " = mol.getBuilder().newBond(" + 
                     bond.getAtom(0).getID() + ", " +
                     bond.getAtom(1).getID() + ", " +
                     bond.getOrder() + ");\n");
    }
    
	public int getSupportedDataFeatures() {
		return DataFeatures.HAS_2D_COORDINATES |
               DataFeatures.HAS_3D_COORDINATES |
               DataFeatures.HAS_GRAPH_REPRESENTATION |
               DataFeatures.HAS_ATOM_ELEMENT_SYMBOL;
	}

	public int getRequiredDataFeatures() {
		return DataFeatures.HAS_GRAPH_REPRESENTATION |
        	   DataFeatures.HAS_ATOM_ELEMENT_SYMBOL;
	}
}


