/*
 * $RCSfile$ 
 * $Author$ 
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2002  The Chemistry Development Kit (CDK) project
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

import java.io.*;
import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;


/**
 * <p>Serializes a SetOfMolecules or a Molecule object to CML code.
 *
 * <p>Output can be redirected to other Writer objects like StringWriter
 * and FileWriter. An example:
 *
 * <pre>
 *   StringWriter output = new StringWriter();
 *   boolean makeFragment = true;
 *   CMLWriter cmlwriter = new CMLWriter(output, makeFragment);
 *   cmlwriter.write(molecule);
 *   cmlwriter.close();
 *   String cmlcode = output.toString();
 * </pre>
 *
 * <p>Output to a file called "molecule.cml" can done with:
 *
 * <pre>
 *   FileWriter output = new FileWriter("molecule.cml");
 *   CMLWriter cmlwriter = new CMLWriter(output);
 *   cmlwriter.write(molecule);
 *   cmlwriter.close();
 * </pre>
 *
 * References:
 *   <a href="http://cdk.sf.net/biblio.html#PMR99">PMR99</a>
 *
 * @see FileWriter
 * @see StringWriter
 *
 * @author Egon Willighagen
 *
 * @keyword file format, CML
 */
public class CMLWriter implements ChemObjectWriter, CDKConstants {

    private Writer output;

    private boolean done;
    private boolean fragment;

    /**
     * Constructs a new CMLWriter class. Output will be stored in the Writer
     * class given as parameter. The CML code will be valid CML code with a
     * XML header. Only one object can be stored.
     *
     * @param out Writer to redirect the output to.
     */
    public CMLWriter(Writer out) {        
		this(out, false);
    }

    /**
     * Constructs a new CMLWriter class. Output will be stored in the Writer
     * class given as parameter. The CML code will be valid CML code with a
     * XML header. More than object can be stored.
     *
     * @param out       Writer to redirect the output to.
     * @param fragment  Boolean denoting that the content is not
     */
    public CMLWriter(Writer w, boolean fragment) {        
		output = w;
		this.fragment = fragment;
		this.done = false;
    }

    /**
     * Flushes the output and closes this object
     */
    public void close() throws IOException {
        output.close();
    }

    /**
     * Serializes the ChemObject to CML and redirects it to the output Writer.
     *
     * @param object A Molecule of SetOfMolecules object
     */
    public void write(ChemObject object) throws UnsupportedChemObjectException {
		if (!done) {
		    if (!fragment) {
			write("<?xml version=\"1.0\"?>\n");
		    }
		    if (object instanceof SetOfMolecules) {
				if (((SetOfMolecules)object).getMoleculeCount() > 1) 
				    write("<list>\n");
				for (int i = 0; i < ((SetOfMolecules)object).getMoleculeCount(); i++) {
				    write(((SetOfMolecules)object).getMolecule(i));
				}
				if (((SetOfMolecules)object).getMoleculeCount() > 1) 
				    write("</list>\n");
		    } else if (object instanceof Molecule) {
				write((Molecule)object);
		    } else {
				throw new UnsupportedChemObjectException("Only supported are SetOfMolecules and Molecule.");
		    }	
		    if (!fragment) {	       
				done = true;
		    }
		} else {};
    };

    // Private procedures

    private void write(SetOfMolecules som) {
		if (som.getMoleculeCount() > 1) 
		    write("<list>\n");
		for (int i = 0; i < som.getMoleculeCount(); i++) {
		    this.write(som.getMolecule(i));
		}
		if (som.getMoleculeCount() > 1) 
		    write("</list>\n");
    }

    private void write(Molecule mol) {
		write("<molecule>\n");
		write(mol.getAtoms());
		write(mol.getBonds());
		write("</molecule>\n");
    }

    private void write(Atom atoms[]) {
		write("<atomArray>\n");
		for (int i = 0; i < atoms.length; i++) {
		    write(atoms[i]);
		}
		write("</atomArray>\n");
    }
    
    private void write(Bond bonds[]) {
		write("<bondArray>\n");
		for (int i = 0; i < bonds.length; i++) {
		    write(bonds[i]);
		}
		write("</bondArray>\n");
    }

    private void write(Atom atom) {
		write("<atom id=\"a" + atom.hashCode() + "\">\n");
		write("<string builtin=\"elementType\">");
		write(atom.getSymbol());
		write("</string>\n");
		write(atom.getPoint2D());
		write(atom.getPoint3D());
		write("</atom>\n");
    }

    private void write(Bond bond) {
		write("<bond id=\"b" + bond.hashCode() + "\">\n");
		Atom atoms[] = bond.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
		    write("<string builtin=\"atomRef\">a" +
			  atoms[i].hashCode() + 
			  "</string>\n");
		}
		write("<float builtin=\"order\">" +
		      + bond.getOrder() +
		      "</float>\n");
		if (bond.getStereo() != STEREO_BOND_UNDEFINED) {
		    write("<string builtin=\"stereo\" convention=\"MDLMol\">");
		    if (bond.getStereo() == STEREO_BOND_UP) {
			write("W");
		    } else if (bond.getStereo() == STEREO_BOND_DOWN) {
			write("H");
		    }
		    write("</string>\n");
		}
		write("</bond>\n");
    }

    private void write(Point2d p) {
		if (p != null) {
		    write("<float builtin=\"x2\">");
		    write(new Float(p.x).toString());
		    write("</float>\n");
		    write("<float builtin=\"y2\">");
		    write(new Float(p.y).toString());
		    write("</float>\n");
		}
    }

    private void write(Point3d p) {
		if (p != null) {
		    write("<float builtin=\"x3\">");
		    write(new Float(p.x).toString());
		    write("</float>\n");
		    write("<float builtin=\"y3\">");
		    write(new Float(p.y).toString());
		    write("</float>\n");
		    write("<float builtin=\"z3\">");
		    write(new Float(p.z).toString());
		    write("</float>\n");
		}
    }

    private void write(String s) {
		try {
		    output.write(s);
		} catch (IOException e) {
		    System.err.println("CMLWriter IOException while printing \"" + 
	                s + "\":\n" + e.toString());
		}
    }
}
