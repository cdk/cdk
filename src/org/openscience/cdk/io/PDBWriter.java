/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2000-2002  The Jmol Development Team
 * Copyright (C) 2003  The CDK Project
 *
 * Contact: cdk-devel@lists.sf.net
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

import org.openscience.cdk.*;
import org.openscience.cdk.exception.CDKException;
import freeware.PrintfFormat;
import javax.vecmath.Point3d;
import java.io.BufferedWriter;
import java.io.Writer;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Saves molecules in a rudimentary PDB format.
 */
public class PDBWriter extends DefaultChemObjectWriter {

    static BufferedWriter writer;

    /**
     * Creates a PDB writer.
     */
    public PDBWriter (Writer out) {
        writer = new BufferedWriter(out);
    }

    public void write(ChemObject object) throws CDKException {
        if (object instanceof Molecule){
            writeMolecule((Molecule)object);
        } else {
            throw new CDKException("Only supported is writing of Molecule objects.");
        }
    }
    
    public ChemObject highestSupportedChemObject() {
        return new Molecule();
    }
    
   /**
    * Writes a single frame in PDB format to the Writer.
    *
    * @param molecule the Molecule to write
    */
   public void writeMolecule(Molecule molecule) throws CDKException {
       
       try {
           int na = 0;
           int atomNumber = 1;
           String info = "";
           String st = "";
           String tab = "\t";
           boolean writecharge = false;
           boolean writevect = false;
           
           String hetatmRecordName = "HETATM";
           String terRecordName = "TER";
           PrintfFormat serialFormat = new PrintfFormat("%5d");
           PrintfFormat atomNameFormat = new PrintfFormat("%-4s");
           PrintfFormat positionFormat = new PrintfFormat("%8.3f");
           
           // Loop through the atoms and write them out:
           StringBuffer buffer = new StringBuffer();
           Atom[] atoms = molecule.getAtoms();
           for (int i = 0; i < atoms.length; i++) {
               buffer.setLength(0);
               buffer.append(hetatmRecordName);
               buffer.append(serialFormat.sprintf(atomNumber));
               buffer.append(' ');
               Atom atom = atoms[i];
               buffer.append(atomNameFormat.sprintf(atom.getSymbol()));
               buffer.append(" MOL          ");
               Point3d position = atom.getPoint3D();
               buffer.append(positionFormat.sprintf(position.x));
               buffer.append(positionFormat.sprintf(position.y));
               buffer.append(positionFormat.sprintf(position.z));
               
               writer.write(buffer.toString(), 0, buffer.length());
               writer.newLine();
               ++atomNumber;
           }
           writer.write(terRecordName, 0, terRecordName.length());
           writer.newLine();
       } catch (IOException exception) {
           throw new CDKException("Error while writing file: " + exception.getMessage());
       }
   }
   
    /**
     * Flushes the output and closes this object.
     */
    public void close() throws IOException {
        writer.close();
    }

}
