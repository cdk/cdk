/* Copyright (C) 2001-2007  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.StringTokenizer;

import javax.vecmath.Point3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.ZMatrixTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.ZMatrixFormat;

/**
 * It reads Z matrices like in Gaussian input files. It seems that it cannot
 * handle Z matrices where values are given via a stringID for which the value
 * is given later.
 *
 * @cdk.module extra
 * @cdk.githash
 * @cdk.iooptions
 *
 * @cdk.keyword file format, Z-matrix
 */
public class ZMatrixReader extends DefaultChemObjectReader {

    private BufferedReader input;

    /**
     * Constructs a ZMatrixReader from a Reader that contains the
     * data to be parsed.
     *
     * @param     input   Reader containing the data to read
     */
    public ZMatrixReader(Reader input) {
        this.input = new BufferedReader(input);
    }

    public ZMatrixReader(InputStream input) {
        this(new InputStreamReader(input));
    }

    public ZMatrixReader() {
        this(new StringReader(""));
    }

    @Override
    public IResourceFormat getFormat() {
        return ZMatrixFormat.getInstance();
    }

    @Override
    public void setReader(Reader input) throws CDKException {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader) input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    @Override
    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

    @Override
    public boolean accepts(Class<? extends IChemObject> classObject) {
        return IChemFile.class.isAssignableFrom(classObject);
    }

    /**
     *  Returns a IChemObject of type object bye reading from
     *  the input.
     *
     *  The function supports only reading of ChemFile's.
     *
     * @param     object  IChemObject that types the class to return.
     * @throws    CDKException when a IChemObject is requested that cannot be read.
     */
    @Override
    public <T extends IChemObject> T read(T object) throws CDKException {
        if (object instanceof IChemFile)
            return (T) readChemFile((IChemFile) object);
        else
            throw new CDKException("Only ChemFile objects can be read.");
    }

    /**
     *  Private method that actually parses the input to read a ChemFile
     *  object.
     *
     * @param file  the file to read from
     * @return A ChemFile containing the data parsed from input.
     */
    private IChemFile readChemFile(IChemFile file) {
        IChemSequence chemSequence = file.getBuilder().newInstance(IChemSequence.class);

        int number_of_atoms;
        StringTokenizer tokenizer;

        try {
            String line = input.readLine();
            while (line.startsWith("#"))
                line = input.readLine();
            /*
             * while (input.ready() && line != null) {
             */
            //        logger.debug("lauf");
            // parse frame by frame
            tokenizer = new StringTokenizer(line, "\t ,;");

            String token = tokenizer.nextToken();
            number_of_atoms = Integer.parseInt(token);
            String info = input.readLine();

            IChemModel chemModel = file.getBuilder().newInstance(IChemModel.class);
            IAtomContainerSet setOfMolecules = file.getBuilder().newInstance(IAtomContainerSet.class);

            IAtomContainer m = file.getBuilder().newInstance(IAtomContainer.class);
            m.setTitle(info);

            String[] types = new String[number_of_atoms];
            double[] d = new double[number_of_atoms];
            int[] d_atom = new int[number_of_atoms]; // Distances
            double[] a = new double[number_of_atoms];
            int[] a_atom = new int[number_of_atoms]; // Angles
            double[] da = new double[number_of_atoms];
            int[] da_atom = new int[number_of_atoms]; // Diederangles
            //Point3d[] pos = new Point3d[number_of_atoms]; // calculated positions

            int i = 0;
            while (i < number_of_atoms) {
                line = input.readLine();
                //          logger.debug("line:\""+line+"\"");
                if (line == null) break;
                if (line.startsWith("#")) {
                    // skip comment in file
                } else {
                    d[i] = 0d;
                    d_atom[i] = -1;
                    a[i] = 0d;
                    a_atom[i] = -1;
                    da[i] = 0d;
                    da_atom[i] = -1;

                    tokenizer = new StringTokenizer(line, "\t ,;");
                    int fields = tokenizer.countTokens();

                    if (fields < Math.min(i * 2 + 1, 7)) {
                        // this is an error but cannot throw exception
                    } else if (i == 0) {
                        types[i] = tokenizer.nextToken();
                        i++;
                    } else if (i == 1) {
                        types[i] = tokenizer.nextToken();
                        d_atom[i] = Integer.valueOf(tokenizer.nextToken()) - 1;
                        d[i] = Double.valueOf(tokenizer.nextToken());
                        i++;
                    } else if (i == 2) {
                        types[i] = tokenizer.nextToken();
                        d_atom[i] = Integer.valueOf(tokenizer.nextToken()) - 1;
                        d[i] = Double.valueOf(tokenizer.nextToken());
                        a_atom[i] = Integer.valueOf(tokenizer.nextToken()) - 1;
                        a[i] = Double.valueOf(tokenizer.nextToken());
                        i++;
                    } else {
                        types[i] = tokenizer.nextToken();
                        d_atom[i] = Integer.valueOf(tokenizer.nextToken()) - 1;
                        d[i] = Double.valueOf(tokenizer.nextToken());
                        a_atom[i] = Integer.valueOf(tokenizer.nextToken()) - 1;
                        a[i] = Double.valueOf(tokenizer.nextToken());
                        da_atom[i] = Integer.valueOf(tokenizer.nextToken()) - 1;
                        da[i] = Double.valueOf(tokenizer.nextToken());
                        i++;
                    }
                }
            }

            // calculate cartesian coordinates
            Point3d[] cartCoords = ZMatrixTools.zmatrixToCartesian(d, d_atom, a, a_atom, da, da_atom);

            for (i = 0; i < number_of_atoms; i++) {
                m.addAtom(file.getBuilder().newInstance(IAtom.class, types[i], cartCoords[i]));
            }

            //        logger.debug("molecule:"+m);

            setOfMolecules.addAtomContainer(m);
            chemModel.setMoleculeSet(setOfMolecules);
            chemSequence.addChemModel(chemModel);
            line = input.readLine();
            file.addChemSequence(chemSequence);
        } catch (IOException e) {
            // should make some noise now
            file = null;
        }
        return file;
    }

    @Override
    public void close() throws IOException {
        input.close();
    }
}
