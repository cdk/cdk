/* Copyright (C) 2003-2007,2010  Egon Willighagen <egonw@users.sf.net>
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
import java.util.Iterator;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.formats.CDKSourceCodeFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.io.setting.StringIOSetting;
import org.openscience.cdk.tools.DataFeatures;
import org.openscience.cdk.tools.IDCreator;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

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
 * @cdk.githash
 *
 * @author Egon Willighagen &lt;egonw@sci.kun.nl&gt;
 * @cdk.created 2003-10-01
 *
 * @cdk.keyword file format, CDK source code
 * @cdk.iooptions
 */
public class CDKSourceCodeWriter extends DefaultChemObjectWriter {

    private BufferedWriter      writer;
    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(CDKSourceCodeWriter.class);

    private BooleanIOSetting    write2DCoordinates;
    private BooleanIOSetting    write3DCoordinates;
    private StringIOSetting     builder;

    /**
     * Constructs a new CDKSourceCodeWriter.
     *
     * @param   out  The Writer to write to
     */
    public CDKSourceCodeWriter(Writer out) {
        initIOSettings();
        try {
            setWriter(out);
        } catch (Exception exc) {
        }
    }

    public CDKSourceCodeWriter(OutputStream out) {
        this(new OutputStreamWriter(out));
    }

    public CDKSourceCodeWriter() {
        this(new StringWriter());
    }

    @Override
    public IResourceFormat getFormat() {
        return CDKSourceCodeFormat.getInstance();
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
        writer.flush();
        writer.close();
    }

    @Override
    public boolean accepts(Class<? extends IChemObject> classObject) {
        Class<?>[] interfaces = classObject.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (IAtomContainer.class.equals(interfaces[i])) return true;
        }
        if (IAtomContainer.class.equals(classObject)) return true;
        Class superClass = classObject.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
        return false;
    }

    @Override
    public void write(IChemObject object) throws CDKException {
        customizeJob();
        if (object instanceof IAtomContainer) {
            try {
                writeAtomContainer((IAtomContainer) object);
                writer.flush();
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                logger.debug(ex);
                throw new CDKException("Exception while writing to CDK source code: " + ex.getMessage(), ex);
            }
        } else {
            throw new CDKException("Only supported is writing of IAtomContainer objects.");
        }
    }

    private void writeAtoms(IAtomContainer molecule) throws Exception {
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom atom = (IAtom) atoms.next();
            writeAtom(atom);
            writer.write("  mol.addAtom(" + atom.getID() + ");");
            writer.newLine();
        }
    }

    private void writeBonds(IAtomContainer molecule) throws Exception {
        Iterator<IBond> bonds = molecule.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = (IBond) bonds.next();
            writeBond(bond);
            writer.write("  mol.addBond(" + bond.getID() + ");");
            writer.newLine();
        }
    }

    private void writeAtomContainer(IAtomContainer molecule) throws Exception {
        writer.write("{");
        writer.newLine();
        writer.write("  IChemObjectBuilder builder = ");
        writer.write(builder.getSetting());
        writer.write(".getInstance();");
        writer.newLine();
        writer.write("  IAtomContainer mol = builder.newInstance(IAtomContainer.class);");
        writer.newLine();
        IDCreator.createIDs(molecule);
        writeAtoms(molecule);
        writeBonds(molecule);
        writer.write("}");
        writer.newLine();
    }

    private void writeAtom(IAtom atom) throws Exception {
        if (atom instanceof IPseudoAtom) {
            writer.write("  IPseudoAtom " + atom.getID() + " = builder.newInstance(IPseudoAtom.class);");
            writer.newLine();
            writer.write("  atom.setLabel(\"" + ((IPseudoAtom) atom).getLabel() + "\");");
            writer.newLine();
        } else {
            writer.write("  IAtom " + atom.getID() + " = builder.newInstance(IAtom.class,\"" + atom.getSymbol()
                    + "\");");
            writer.newLine();
        }
        if (atom.getFormalCharge() != null) {
            writer.write("  " + atom.getID() + ".setFormalCharge(" + atom.getFormalCharge() + ");");
            writer.newLine();
        }
        if (write2DCoordinates.isSet() && atom.getPoint2d() != null) {
            Point2d p2d = atom.getPoint2d();
            writer.write("  " + atom.getID() + ".setPoint2d(new Point2d(" + p2d.x + ", " + p2d.y + "));");
            writer.newLine();
        }
        if (write3DCoordinates.isSet() && atom.getPoint3d() != null) {
            Point3d p3d = atom.getPoint3d();
            writer.write("  " + atom.getID() + ".setPoint3d(new Point3d(" + p3d.x + ", " + p3d.y + ", " + p3d.z + "));");
            writer.newLine();
        }
    }

    private void writeBond(IBond bond) throws Exception {
        writer.write("  IBond " + bond.getID() + " = builder.newInstance(IBond.class," + bond.getAtom(0).getID() + ", "
                + bond.getAtom(1).getID() + ", IBond.Order." + bond.getOrder() + ");");
        writer.newLine();
    }

    public int getSupportedDataFeatures() {
        return DataFeatures.HAS_2D_COORDINATES | DataFeatures.HAS_3D_COORDINATES
                | DataFeatures.HAS_GRAPH_REPRESENTATION | DataFeatures.HAS_ATOM_ELEMENT_SYMBOL;
    }

    public int getRequiredDataFeatures() {
        return DataFeatures.HAS_GRAPH_REPRESENTATION | DataFeatures.HAS_ATOM_ELEMENT_SYMBOL;
    }

    private void initIOSettings() {
        write2DCoordinates = addSetting(new BooleanIOSetting("write2DCoordinates", IOSetting.Importance.LOW,
                "Should 2D coordinates be added?", "true"));

        write3DCoordinates = addSetting(new BooleanIOSetting("write3DCoordinates", IOSetting.Importance.LOW,
                "Should 3D coordinates be added?", "true"));

        builder = addSetting(new StringIOSetting("builder", IOSetting.Importance.LOW,
                "Which IChemObjectBuilder should be used?", "DefaultChemObjectBuilder"));
    }

    private void customizeJob() {
        fireIOSettingQuestion(write2DCoordinates);
        fireIOSettingQuestion(write3DCoordinates);
        fireIOSettingQuestion(builder);
    }

}
