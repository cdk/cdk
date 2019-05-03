/* Copyright (C) 2004-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.CrystalGeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.io.formats.CrystClustFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.math.FortranFormat;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * @cdk.module extra
 * @cdk.githash
 * @cdk.iooptions
 */
public class CrystClustReader extends DefaultChemObjectReader {

    private BufferedReader      input;
    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(CrystClustReader.class);

    public CrystClustReader() {}

    public CrystClustReader(Reader input) {
        this();
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader) input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    public CrystClustReader(InputStream input) {
        this(new InputStreamReader(input));
    }

    @Override
    public IResourceFormat getFormat() {
        return CrystClustFormat.getInstance();
    }

    @Override
    public void setReader(Reader reader) throws CDKException {
        if (reader instanceof BufferedReader) {
            this.input = (BufferedReader) reader;
        } else {
            this.input = new BufferedReader(reader);
        }
    }

    @Override
    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

    @Override
    public boolean accepts(Class<? extends IChemObject> classObject) {
        if (IChemFile.class.equals(classObject)) return true;
        Class<?>[] interfaces = classObject.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (IChemFile.class.equals(interfaces[i])) return true;
        }
        Class superClass = classObject.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
        return false;
    }

    @Override
    public <T extends IChemObject> T read(T object) throws CDKException {
        if (object instanceof IChemFile) {
            IChemFile cf = readChemFile((IChemFile) object);
            return (T) cf;
        } else {
            throw new CDKException("Only supported is reading of ChemFile.");
        }
    }

    private IChemFile readChemFile(IChemFile file) throws CDKException {
        IChemSequence seq = file.getBuilder().newInstance(IChemSequence.class);
        IChemModel model = file.getBuilder().newInstance(IChemModel.class);
        ICrystal crystal = null;

        int lineNumber = 0;
        Vector3d a, b, c;

        try {
            String line = input.readLine();
            while (input.ready() && line != null) {
                logger.debug((lineNumber++) + ": ", line);
                if (line.startsWith("frame:")) {
                    logger.debug("found new frame");
                    model = file.getBuilder().newInstance(IChemModel.class);
                    crystal = file.getBuilder().newInstance(ICrystal.class);

                    // assume the file format is correct

                    logger.debug("reading spacegroup");
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    crystal.setSpaceGroup(line);

                    logger.debug("reading unit cell axes");
                    Vector3d axis = new Vector3d();
                    logger.debug("parsing A: ");
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    axis.x = FortranFormat.atof(line);
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    axis.y = FortranFormat.atof(line);
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    axis.z = FortranFormat.atof(line);
                    crystal.setA(axis);
                    axis = new Vector3d();
                    logger.debug("parsing B: ");
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    axis.x = FortranFormat.atof(line);
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    axis.y = FortranFormat.atof(line);
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    axis.z = FortranFormat.atof(line);
                    crystal.setB(axis);
                    axis = new Vector3d();
                    logger.debug("parsing C: ");
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    axis.x = FortranFormat.atof(line);
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    axis.y = FortranFormat.atof(line);
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    axis.z = FortranFormat.atof(line);
                    crystal.setC(axis);
                    logger.debug("Crystal: ", crystal);
                    a = crystal.getA();
                    b = crystal.getB();
                    c = crystal.getC();

                    logger.debug("Reading number of atoms");
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    int atomsToRead = Integer.parseInt(line);

                    logger.debug("Reading no molecules in assym unit cell");
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    int Z = Integer.parseInt(line);
                    crystal.setZ(Z);

                    String symbol;
                    double charge;
                    Point3d cart;
                    for (int i = 1; i <= atomsToRead; i++) {
                        cart = new Point3d();
                        line = input.readLine();
                        logger.debug((lineNumber++) + ": ", line);
                        symbol = line.substring(0, line.indexOf(':'));
                        charge = Double.parseDouble(line.substring(line.indexOf(':') + 1));
                        line = input.readLine();
                        logger.debug((lineNumber++) + ": ", line);
                        cart.x = Double.parseDouble(line); // x
                        line = input.readLine();
                        logger.debug((lineNumber++) + ": ", line);
                        cart.y = Double.parseDouble(line); // y
                        line = input.readLine();
                        logger.debug((lineNumber++) + ": ", line);
                        cart.z = Double.parseDouble(line); // z
                        IAtom atom = file.getBuilder().newInstance(IAtom.class, symbol);
                        atom.setCharge(charge);
                        // convert cartesian coords to fractional
                        Point3d frac = CrystalGeometryTools.cartesianToFractional(a, b, c, cart);
                        atom.setFractionalPoint3d(frac);
                        crystal.addAtom(atom);
                        logger.debug("Added atom: ", atom);
                    }

                    model.setCrystal(crystal);
                    seq.addChemModel(model);
                } else {
                    logger.debug("Format seems broken. Skipping these lines:");
                    while (line != null && !line.startsWith("frame:") && input.ready()) {
                        line = input.readLine();
                        logger.debug(lineNumber++ + ": ", line);
                    }
                    logger.debug("Ok, resynched: found new frame");
                }
            }
            file.addChemSequence(seq);
        } catch (IOException | IllegalArgumentException exception) {
            String message = "Error while parsing CrystClust file: " + exception.getMessage();
            logger.error(message);
            logger.debug(exception);
            throw new CDKException(message, exception);
        }
        return file;
    }

    @Override
    public void close() throws IOException {
        input.close();
    }
}
