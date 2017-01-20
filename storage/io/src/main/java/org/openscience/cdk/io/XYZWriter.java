/* Copyright (C) 2002  Bradley A. Smith <bradley@baysmith.com>
 *               2002  Miguel Howard
 *               2003-2007  Egon Willighagen <egonw@users.sf.net>
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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.XYZFormat;
import org.openscience.cdk.tools.FormatStringBuffer;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * @cdk.module io
 * @cdk.githash
 * @cdk.iooptions
 *
 * @author Bradley A. Smith &lt;bradley@baysmith.com&gt;
 * @author  J. Daniel Gezelter
 * @author  Egon Willighagen
 */
public class XYZWriter extends DefaultChemObjectWriter {

    private BufferedWriter      writer;
    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(XYZWriter.class);
    private FormatStringBuffer  fsb;

    /**
    * Constructor.
    *
    * @param out the stream to write the XYZ file to.
    */
    public XYZWriter(Writer out) {
        fsb = new FormatStringBuffer("%-8.6f");
        try {
            if (out instanceof BufferedWriter) {
                writer = (BufferedWriter) out;
            } else {
                writer = new BufferedWriter(out);
            }
        } catch (Exception exc) {
        }
    }

    public XYZWriter(OutputStream output) {
        this(new OutputStreamWriter(output));
    }

    public XYZWriter() {
        this(new StringWriter());
    }

    @Override
    public IResourceFormat getFormat() {
        return XYZFormat.getInstance();
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
        if (IAtomContainer.class.equals(classObject)) return true;
        Class<?>[] interfaces = classObject.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (IAtomContainer.class.equals(interfaces[i])) return true;
        }
        Class superClass = classObject.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
        return false;
    }

    @Override
    public void write(IChemObject object) throws CDKException {
        if (object instanceof IAtomContainer) {
            try {
                writeMolecule((IAtomContainer) object);
            } catch (Exception ex) {
                throw new CDKException("Error while writing XYZ file: " + ex.getMessage(), ex);
            }
        } else {
            throw new CDKException("XYZWriter only supports output of Molecule classes.");
        }
    }

    /**
    * writes a single frame in XYZ format to the Writer.
    * @param mol the Molecule to write
    */
    public void writeMolecule(IAtomContainer mol) throws IOException {

        String st = "";
        boolean writecharge = true;

        try {

            String s1 = "" + mol.getAtomCount();
            writer.write(s1, 0, s1.length());
            writer.newLine();

            String s2 = null; // FIXME: add some interesting comment
            if (s2 != null) {
                writer.write(s2, 0, s2.length());
            }
            writer.newLine();

            // Loop through the atoms and write them out:
            Iterator<IAtom> atoms = mol.atoms().iterator();
            while (atoms.hasNext()) {
                IAtom a = atoms.next();
                st = a.getSymbol();

                Point3d p3 = a.getPoint3d();
                if (p3 != null) {
                    st = st + "\t" + (p3.x < 0 ? "" : " ") + fsb.format(p3.x) + "\t" + (p3.y < 0 ? "" : " ")
                            + fsb.format(p3.y) + "\t" + (p3.z < 0 ? "" : " ") + fsb.format(p3.z);
                } else {
                    st = st + "\t " + fsb.format(0.0) + "\t " + fsb.format(0.0) + "\t " + fsb.format(0.0);
                }

                if (writecharge) {
                    double ct = a.getCharge() == CDKConstants.UNSET ? 0.0 : a.getCharge();
                    st = st + "\t" + ct;
                }

                writer.write(st, 0, st.length());
                writer.newLine();

            }
        } catch (IOException e) {
            //            throw e;
            logger.error("Error while writing file: ", e.getMessage());
            logger.debug(e);
        }
    }
}
