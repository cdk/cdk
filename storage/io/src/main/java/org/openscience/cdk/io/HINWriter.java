/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.formats.HINFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.tools.LoggingToolFactory;

import javax.vecmath.Point3d;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

/**
 * Writer that outputs in the HIN format.
 *
 * @author Rajarshi Guha &lt;rajarshi@presidency.com&gt;
 * @cdk.module io
 * @cdk.githash
 * @cdk.created 2004-01-27
 * @cdk.iooptions
 */
public class HINWriter extends DefaultChemObjectWriter {

    private BufferedWriter writer;

    /**
     * Constructor.
     *
     * @param out the stream to write the HIN file to.
     */
    public HINWriter(Writer out) {
        try {
            if (out instanceof BufferedWriter) {
                writer = (BufferedWriter) out;
            } else {
                writer = new BufferedWriter(out);
            }
        } catch (Exception exc) {
            LoggingToolFactory.createLoggingTool(HINWriter.class).debug(exc.toString());
        }
    }

    public HINWriter(OutputStream output) {
        this(new OutputStreamWriter(output));
    }

    public HINWriter() {
        this(new StringWriter());
    }

    @Override
    public IResourceFormat getFormat() {
        return HINFormat.getInstance();
    }

    @Override
    public void setWriter(Writer out) throws CDKException {
        if (out instanceof BufferedWriter) {
            writer = (BufferedWriter) out;
        } else {
            writer = new BufferedWriter(out);
        }
    }

    @Override
    public void setWriter(OutputStream output) throws CDKException {
        setWriter(new OutputStreamWriter(output));
    }

    /**
     * Flushes the output and closes this object.
     */
    @Override
    public void close() throws IOException {
        writer.close();
    }

    @Override
    public boolean accepts(Class<? extends IChemObject> classObject) {
        Class<?>[] interfaces = classObject.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            if (IAtomContainer.class.equals(anInterface)) return true;
            if (IAtomContainerSet.class.equals(anInterface)) return true;
        }
        return false;
    }

    @Override
    public void write(IChemObject object) throws CDKException {
        if (object instanceof IAtomContainer) {
            try {
                IAtomContainerSet som = object.getBuilder().newInstance(IAtomContainerSet.class);
                som.addAtomContainer((IAtomContainer) object);
                writeAtomContainer(som);
            } catch (IllegalArgumentException | IOException ex) {
                throw new CDKException("Error while writing HIN file: " + ex.getMessage(), ex);
            }
        } else if (object instanceof IAtomContainerSet) {
            try {
                writeAtomContainer((IAtomContainerSet) object);
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
     * @throws java.io.IOException if there is a problem writing the molecule
     */
    private void writeAtomContainer(IAtomContainerSet som) throws IOException {

        //int na = 0;
        //String info = "";
        String sym;
        double chrg;
        //boolean writecharge = true;

        for (int molnum = 0; molnum < som.getAtomContainerCount(); molnum++) {

            IAtomContainer mol = som.getAtomContainer(molnum);

            try {
                String molname = "mol " + (molnum + 1) + " " + mol.getProperty(CDKConstants.TITLE);

                writer.write(molname, 0, molname.length());
                writer.newLine();

                // Loop through the atoms and write them out:
                Iterator<IAtom> atoms = mol.atoms().iterator();

                int i = 0;
                while (atoms.hasNext()) {
                    IAtom atom = atoms.next();
                    String line = "atom ";

                    sym = atom.getSymbol();
                    chrg = atom.getCharge();
                    Point3d point = atom.getPoint3d();

                    line = line + Integer.toString(i + 1) + " - " + sym + " ** - " + Double.toString(chrg) + " "
                            + Double.toString(point.x) + " " + Double.toString(point.y) + " "
                            + Double.toString(point.z) + " ";

                    String buf = "";
                    int ncon = 0;
                    Iterator<IBond> bonds = mol.bonds().iterator();
                    while (bonds.hasNext()) {
                        IBond bond = bonds.next();
                        if (bond.contains(atom)) {
                            // current atom is in the bond so lets get the connected atom
                            IAtom connectedAtom = bond.getConnectedAtom(atom);
                            IBond.Order bondOrder = bond.getOrder();
                            int serial;
                            String bondType = "";

                            // get the serial no for this atom
                            serial = mol.getAtomNumber(connectedAtom);

                            if (bondOrder == IBond.Order.SINGLE)
                                bondType = "s";
                            else if (bondOrder == IBond.Order.DOUBLE)
                                bondType = "d";
                            else if (bondOrder == IBond.Order.TRIPLE)
                                bondType = "t";
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
