/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2002  The Jmol Development Team
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307  USA.
 */
package org.openscience.cdk.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.vecmath.Point3d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.*;

/**
 * @cdk.module io
 *
 * @author  Bradley A. Smith <bradley@baysmith.com>
 * @author  J. Daniel Gezelter
 * @author  Egon Willighagen
 */
public class XYZWriter extends DefaultChemObjectWriter {
  
    static BufferedWriter writer;

    /**
    * Constructor.
    * @param out the stream to write the XYZ file to.
    */
    public XYZWriter(Writer out) {
        writer = new BufferedWriter(out);
    }

    public XYZWriter(OutputStream input) {
        this(new OutputStreamWriter(input));
    }
    
    public ChemFormat getFormat() {
        return new XYZFormat();
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
                throw new CDKException("Error while writing XYZ file: " + ex.getMessage());
            }
        } else {
            throw new CDKException("XYZWriter only supports output of Molecule classes.");
        }
    }

    public ChemObject highestSupportedChemObject() {
        return new Molecule();
    }
    
    /**
    * writes a single frame in XYZ format to the Writer.
    * @param mol the Molecule to write
    */
    public void writeMolecule(Molecule mol) throws IOException {
        
        int na = 0;
        String info = "";
        String st = "";
        boolean writecharge = true;
        
        try {
            
            String s1 = new Integer(mol.getAtomCount()).toString();
            writer.write(s1, 0, s1.length());
            writer.newLine();
            
            String s2 = null; // FIXME: add some interesting comment
            if (s2 != null) {
                writer.write(s2, 0, s2.length());
            }
            writer.newLine();
            
            // Loop through the atoms and write them out:
            Atom[] atoms = mol.getAtoms();
            for (int i = 0; i < atoms.length; i++) {
                
                Atom a = atoms[i];
                st = a.getSymbol();
                
                Point3d p3 = a.getPoint3d();
                if (p3 != null) {
                    st = st + "\t" + new Double(p3.x).toString() + "\t"
                            + new Double(p3.y).toString() + "\t"
                            + new Double(p3.z).toString();
                }
                
                if (writecharge) {
                    double ct = a.getCharge();
                    st = st + "\t" + ct;
                }
                
                writer.write(st, 0, st.length());
                writer.newLine();
                
            }
        } catch (IOException e) {
            throw e;
        }
    }
}


