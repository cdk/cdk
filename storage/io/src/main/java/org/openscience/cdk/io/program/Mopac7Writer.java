/* Copyright (C) 2005-2006  Ideaconsult Ltd.
 *               2012       Egon Willighagen <egonw@users.sf.net>
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.io.program;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.DefaultChemObjectWriter;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MOPAC7InputFormat;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.io.setting.StringIOSetting;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Prepares input file for running MOPAC.
 * Optimization is switched on if there are no coordinates.
 *
 * @author Nina Jeliazkova &lt;nina@acad.bg&gt;
 * @cdk.githash
 * @cdk.module  io
 */
public class Mopac7Writer extends DefaultChemObjectWriter {

    private BufferedWriter      writer;
    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(Mopac7Writer.class);

    private final static char   BLANK  = ' ';
    private NumberFormat        numberFormat;

    /**
     * Creates a writer to serialize a molecule as Mopac7 input.
     */
    public Mopac7Writer() throws IOException {
        this(new BufferedWriter(new StringWriter()));
    }

    /**
     * Creates a writer to serialize a molecule as Mopac7 input. Output is written to the
     * given {@link OutputStream}.
     *
     * @param  out {@link OutputStream} to which the output is written
     */
    public Mopac7Writer(OutputStream out) throws IOException {
        this(new BufferedWriter(new OutputStreamWriter(out)));
    }

    /**
     * Creates a writer to serialize a molecule as Mopac7 input. Output is written to the
     * given {@link Writer}.
     *
     * @param  out {@link Writer} to which the output is written
     */
    public Mopac7Writer(Writer out) throws IOException {
        numberFormat = NumberFormat.getInstance(Locale.ROOT);
        numberFormat.setMaximumFractionDigits(4);
        writer = new BufferedWriter(out);
        initIOSettings();
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void write(IChemObject arg0) throws CDKException {
        customizeJob();
        if (arg0 instanceof IAtomContainer)
            try {
                IAtomContainer container = (IAtomContainer) arg0;
                writer.write(mopacCommands.getSetting());
                int formalCharge = AtomContainerManipulator.getTotalFormalCharge(container);
                if (formalCharge != 0) writer.write(" CHARGE=" + formalCharge);
                writer.write('\n');
                if (container.getProperty("Names") != null) writer.write(container.getProperty("Names").toString());
                writer.write('\n');
                writer.write(getTitle());
                writer.write('\n');

                for (int i = 0; i < container.getAtomCount(); i++) {
                    IAtom atom = container.getAtom(i);
                    if (atom.getPoint3d() != null) {
                        Point3d point = atom.getPoint3d();
                        writeAtom(atom, point.x, point.y, point.z, optimize.isSet() ? 1 : 0);
                    } else if (atom.getPoint2d() != null) {
                        Point2d point = atom.getPoint2d();
                        writeAtom(atom, point.x, point.y, 0, optimize.isSet() ? 1 : 0);
                    } else
                        writeAtom(atom, 0, 0, 0, 1);
                }
                writer.write("0");
                writer.write('\n');

            } catch (IOException ioException) {
                logger.error(ioException);
                throw new CDKException(ioException.getMessage(), ioException);
            }
        else
            throw new CDKException("Unsupported object!\t" + arg0.getClass().getName());
    }

    private void writeAtom(IAtom atom, double xCoord, double yCoord, double zCoord, int optimize) throws IOException {

        writer.write(atom.getSymbol());
        writer.write(BLANK);
        writer.write(numberFormat.format(xCoord));
        writer.write(BLANK);
        writer.write(Integer.toString(optimize));
        writer.write(BLANK);
        writer.write(numberFormat.format(yCoord));
        writer.write(BLANK);
        writer.write(Integer.toString(optimize));
        writer.write(BLANK);
        writer.write(numberFormat.format(zCoord));
        writer.write(BLANK);
        writer.write(Integer.toString(optimize));
        writer.write(BLANK);
        writer.write('\n');
    }

    @Override
    /** {@inheritDoc} */
    public void close() throws IOException {
        writer.close();
    }

    @Override
    /** {@inheritDoc} */
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
    /** {@inheritDoc} */
    public IResourceFormat getFormat() {
        return MOPAC7InputFormat.getInstance();
    }

    @Override
    /** {@inheritDoc} */
    public void setWriter(OutputStream writer) throws CDKException {
        setWriter(new OutputStreamWriter(writer));
    }

    @Override
    /** {@inheritDoc} */
    public void setWriter(Writer writer) throws CDKException {
        if (this.writer != null) {
            try {
                this.writer.close();
            } catch (IOException exception) {
                logger.error(exception);
            }
            this.writer = null;
        }
        this.writer = new BufferedWriter(writer);
    }

    private String getTitle() {
        return "Generated by " + getClass().getName() + " at " + new Date(System.currentTimeMillis());
    }

    private StringIOSetting  mopacCommands;
    private BooleanIOSetting optimize;

    private void initIOSettings() {
        optimize = addSetting(new BooleanIOSetting("Optimize", IOSetting.Importance.MEDIUM,
                "Should the structure be optimized?", "true"));
        mopacCommands = addSetting(new StringIOSetting("Commands", IOSetting.Importance.LOW,
                "What Mopac commands should be used (overwrites other choices)?",
                "PM3 NOINTER NOMM BONDS MULLIK PRECISE"));
    }

    private void customizeJob() {
        fireIOSettingQuestion(optimize);
        try {
            if (optimize.isSet()) {
                mopacCommands.setSetting("PM3 NOINTER NOMM BONDS MULLIK PRECISE");
            } else {
                mopacCommands.setSetting("PM3 NOINTER NOMM BONDS MULLIK XYZ 1SCF");
            }
        } catch (CDKException exception) {
            throw new IllegalArgumentException(exception);
        }
        fireIOSettingQuestion(mopacCommands);
    }

}
