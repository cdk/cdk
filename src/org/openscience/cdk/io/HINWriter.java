/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.vecmath.Point3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.ChemFormat;
import org.openscience.cdk.io.formats.HINFormat;

/**
 * Writer that outputs in the HIN format.
 *
 * @cdk.module io
 *
 * @author  Rajarshi Guha <rajarshi@presidency.com>
 * @cdk.created 2004-01-27
 */
public class HINWriter extends DefaultChemObjectWriter {

    private static BufferedWriter writer;

    /**
     * Constructor.
     * @param out the stream to write the HIN file to.
     */
    public HINWriter(Writer out) {
        writer = new BufferedWriter(out);
    }

    public HINWriter(OutputStream input) {
        this(new OutputStreamWriter(input));
    }
    
    public ChemFormat getFormat() {
        return new HINFormat();
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
                SetOfMolecules som = new SetOfMolecules();
                som.addMolecule((Molecule)object);
                writeMolecule(som);
            } catch(Exception ex) {
                throw new CDKException("Error while writing HIN file: " + ex.getMessage());
            }
        } else if (object instanceof SetOfMolecules) {
            try {
                writeMolecule((SetOfMolecules)object);
            } catch (IOException ex) {
                //
            }
        } else {
            throw new CDKException("HINWriter only supports output of Molecule or SetOfMolecule classes.");
        }
    }

    /**
     * writes all the molecules supplied in a SetOfMolecules class to
     * a single HIN file. You can also supply a single Molecule object
     * as well
     * @param mol the Molecule to write
     */
    private void writeMolecule(SetOfMolecules som) throws IOException {

        int na = 0;
        String info = "";
        String sym = "";
        double chrg = 0.0;
        boolean writecharge = true;

        for (int molnum = 0; molnum < som.getMoleculeCount(); molnum++) {

        	org.openscience.cdk.interfaces.Molecule mol = som.getMolecule(molnum);

            try {

                int natom = mol.getAtomCount();
                int nbond = mol.getBondCount();

                String molname = "mol " + (molnum+1) + " " + (String)mol.getProperty(CDKConstants.TITLE);

                writer.write(molname, 0, molname.length());
                writer.newLine();

                // Loop through the atoms and write them out:
                org.openscience.cdk.interfaces.Atom[] atoms = mol.getAtoms();
                org.openscience.cdk.interfaces.Bond[] bonds = mol.getBonds();

                for (int i = 0; i < natom; i++) {

                    String line = "atom ";
                    org.openscience.cdk.interfaces.Atom a = atoms[i];

                    sym = a.getSymbol();
                    chrg = a.getCharge();
                    Point3d p3 = a.getPoint3d();

                    line = line + new Integer(i+1).toString() + " - " + sym + " ** - " + 
                        new Double(chrg).toString() + " " +
                        new Double(p3.x).toString() + " " +
                        new Double(p3.y).toString() + " " +
                        new Double(p3.z).toString() + " " ;

                    String buf = "";
                    int ncon = 0;
                    for (int j = 0; j < nbond; j++) {
                    	org.openscience.cdk.interfaces.Bond b = bonds[j];
                        if (b.contains(a)) {
                            // current atom is in the bond so lets get the connected atom
                        	org.openscience.cdk.interfaces.Atom ca = b.getConnectedAtom(a);
                            double bo = b.getOrder();
                            int serial = -1;
                            String bt = "";

                            // get the serial no for this atom
                            serial = mol.getAtomNumber(ca);

                            if (bo == 1) bt = new String("s");
                            else if (bo == 2) bt = new String("d");
                            else if (bo == 3) bt = new String("t");
                            else if (bo == 1.5) bt = new String("a");
                            buf = buf + new Integer(serial+1).toString() + " " + bt + " ";
                            ncon++;
                        }
                    }
                    line = line + " " + new Integer(ncon).toString() + " " + buf;
                    writer.write(line, 0, line.length());
                    writer.newLine();
                }
                String buf = "endmol " + (molnum+1);
                writer.write(buf,0,buf.length());
                writer.newLine();
            } catch (IOException e) {
                throw e;
            }
        }
    }
}


