/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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

import javax.vecmath.Point3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.HINFormat;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Writer that outputs in the HIN format.
 *
 * @cdk.module io
 *
 * @author  Rajarshi Guha <rajarshi@presidency.com>
 * @cdk.created 2004-01-27
 */
public class HINWriter extends DefaultChemObjectWriter {

	static BufferedWriter writer;
    private LoggingTool logger; 
    
    /**
     * Constructor.
     * @param out the stream to write the HIN file to.
     */
    public HINWriter(Writer out) {
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

    public HINWriter(OutputStream output) {
        this(new OutputStreamWriter(output));
    }
    
    public HINWriter() {
        this(new StringWriter());
    }
    
    public IChemFormat getFormat() {
        return new HINFormat();
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
		if (IMolecule.class.isInstance(classObject)) return true;
		if (ISetOfMolecules.class.isInstance(classObject)) return true;
		return false;
	}

    public void write(IChemObject object) throws CDKException {
        if (object instanceof IMolecule) {
            try {
                ISetOfMolecules som = object.getBuilder().newSetOfMolecules();
                som.addMolecule((IMolecule)object);
                writeMolecule(som);
            } catch(Exception ex) {
                throw new CDKException("Error while writing HIN file: " + ex.getMessage(), ex);
            }
        } else if (object instanceof ISetOfMolecules) {
            try {
                writeMolecule((ISetOfMolecules)object);
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
    private void writeMolecule(ISetOfMolecules som) throws IOException {

        int na = 0;
        String info = "";
        String sym = "";
        double chrg = 0.0;
        boolean writecharge = true;

        for (int molnum = 0; molnum < som.getMoleculeCount(); molnum++) {

        	IMolecule mol = som.getMolecule(molnum);

            try {

                int natom = mol.getAtomCount();
                int nbond = mol.getBondCount();

                String molname = "mol " + (molnum+1) + " " + (String)mol.getProperty(CDKConstants.TITLE);

                writer.write(molname, 0, molname.length());
                writer.newLine();

                // Loop through the atoms and write them out:
                IAtom[] atoms = mol.getAtoms();
                IBond[] bonds = mol.getBonds();

                for (int i = 0; i < natom; i++) {

                    String line = "atom ";
                    IAtom a = atoms[i];

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
                    	IBond b = bonds[j];
                        if (b.contains(a)) {
                            // current atom is in the bond so lets get the connected atom
                        	IAtom ca = b.getConnectedAtom(a);
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


