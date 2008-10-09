/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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

import javax.vecmath.Point3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.formats.HINFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Writer that outputs in the HIN format.
 *
 * @author Rajarshi Guha <rajarshi@presidency.com>
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 * @cdk.created 2004-01-27
 */
@TestClass("org.openscience.cdk.io.HINWriterTest")
public class HINWriter extends DefaultChemObjectWriter {

    private BufferedWriter writer;

    /**
     * Constructor.
     *
     * @param out the stream to write the HIN file to.
     */
    public HINWriter(Writer out) {
        LoggingTool logger = new LoggingTool(this);
        try {
            if (out instanceof BufferedWriter) {
                writer = (BufferedWriter) out;
            } else {
                writer = new BufferedWriter(out);
            }
        } catch (Exception exc) {
            logger.debug(exc.toString());
        }
    }

    public HINWriter(OutputStream output) {
        this(new OutputStreamWriter(output));
    }

    public HINWriter() {
        this(new StringWriter());
    }

    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return HINFormat.getInstance();
    }

    public void setWriter(Writer out) throws CDKException {
        if (out instanceof BufferedWriter) {
            writer = (BufferedWriter) out;
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
        for (int i = 0; i < interfaces.length; i++) {
            if (IMolecule.class.equals(interfaces[i])) return true;
            if (IMoleculeSet.class.equals(interfaces[i])) return true;
        }
        return false;
    }

    public void write(IChemObject object) throws CDKException {
        if (object instanceof IMolecule) {
            try {
                IMoleculeSet som = object.getBuilder().newMoleculeSet();
                som.addMolecule((IMolecule) object);
                writeMolecule(som);
            } catch (Exception ex) {
                throw new CDKException("Error while writing HIN file: " + ex.getMessage(), ex);
            }
        } else if (object instanceof IMoleculeSet) {
            try {
                writeMolecule((IMoleculeSet) object);
            } catch (IOException ex) {
                //
            }
        } else {
            throw new CDKException("HINWriter only supports output of Molecule or SetOfMolecule classes.");
        }
    }

    /**
     * writes all the molecules supplied in a MoleculeSet class to
     * a single HIN file. You can also supply a single Molecule object
     * as well
     *
     * @param som the set of molecules to write
     */
    private void writeMolecule(IMoleculeSet som) throws IOException {

        //int na = 0;
        //String info = "";
        String sym;
        double chrg;
        //boolean writecharge = true;

        for (int molnum = 0; molnum < som.getMoleculeCount(); molnum++) {

            IMolecule mol = som.getMolecule(molnum);

            try {
                String molname = "mol " + (molnum + 1) + " " + mol.getProperty(CDKConstants.TITLE);

                writer.write(molname, 0, molname.length());
                writer.newLine();

                // Loop through the atoms and write them out:
                java.util.Iterator atoms = mol.atoms().iterator();
                
                int i = 0;
                while (atoms.hasNext()) {
                	IAtom atom = (IAtom)atoms.next();
                    String line = "atom ";

                    sym = atom.getSymbol();
                    chrg = atom.getCharge();
                    Point3d point = atom.getPoint3d();

                    line = line + Integer.toString(i + 1) + " - " + sym + " ** - " +
                            Double.toString(chrg) + " " +
                            Double.toString(point.x) + " " +
                            Double.toString(point.y) + " " +
                            Double.toString(point.z) + " ";

                    String buf = "";
                    int ncon = 0;
                    Iterator bonds = mol.bonds().iterator();
                    while (bonds.hasNext()) {
                        IBond bond = (IBond)bonds.next();
                        if (bond.contains(atom)) {
                            // current atom is in the bond so lets get the connected atom
                            IAtom connectedAtom = bond.getConnectedAtom(atom);
                            IBond.Order bondOrder = bond.getOrder();
                            int serial;
                            String bondType = "";

                            // get the serial no for this atom
                            serial = mol.getAtomNumber(connectedAtom);

                            if (bondOrder == IBond.Order.SINGLE) bondType = "s";
                            else if (bondOrder == IBond.Order.DOUBLE) bondType = "d";
                            else if (bondOrder == IBond.Order.TRIPLE) bondType = "t";
                            else if (bond.getFlag(CDKConstants.ISAROMATIC)) bondType = "a";
                            buf = buf + Integer.toString(serial + 1) + " " + bondType + " ";
                            ncon++;
                        }
                    }
                    line = line + " " + Integer.toString(ncon) + " " + buf;
                    writer.write(line, 0, line.length());
                    writer.newLine();
                    i++;
                }
                String buf = "endmol " + (molnum + 1);
                writer.write(buf, 0, buf.length());
                writer.newLine();
            } catch (IOException e) {
                throw e;
            }
        }
    }
}


