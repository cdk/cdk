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
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.StringTokenizer;

import javax.vecmath.Point3d;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MOPAC7Format;

/**
 * Reads MOPAC output, extracts several electronic parameters and assigns them as a molecule
 * properties.<p>
 *
 * Parameters: "NO. OF FILLED LEVELS",	"TOTAL ENERGY","FINAL HEAT OF FORMATION",
 * "IONIZATION POTENTIAL", "ELECTRONIC ENERGY","CORE-CORE REPULSION","MOLECULAR WEIGHT".<p>
 * Doesn't update structure coordinates ! (TODO fix)
 *
 * @author      Nina Jeliazkova <nina@acad.bg>
 * @cdk.githash
 * @cdk.module  io
 */
public class Mopac7Reader extends DefaultChemObjectReader {

    BufferedReader          input        = null;
    private static String[] parameters   = {"NO. OF FILLED LEVELS", "TOTAL ENERGY", "FINAL HEAT OF FORMATION",
            "IONIZATION POTENTIAL", "ELECTRONIC ENERGY", "CORE-CORE REPULSION", "MOLECULAR WEIGHT", "EHOMO", "ELUMO"};
    private static String[] units        = {"", "EV", "KJ", "", "EV", "EV", "", "EV", "EV"};
    private static String   eigenvalues  = "EIGENVALUES";
    private static String   filledLevels = "NO. OF FILLED LEVELS";

    /**
     * Constructs a new Mopac7reader that can read a molecule from a given {@link Reader}.
     *
     * @param  input  The {@link Reader} to read from
     */
    public Mopac7Reader(Reader input) {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader) input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    /**
     * Constructs a new Mopac7reader that can read a molecule from a given {@link InputStream}.
     *
     * @param  input  The {@link InputStream} to read from
     */
    public Mopac7Reader(InputStream input) {
        this(new InputStreamReader(input));
    }

    /**
     * Constructs a new Mopac7reader that can read a molecule. The reader to read from has
     * yet to be set.
     */
    public Mopac7Reader() {
        this(new StringReader(""));
    }

    /*
     * FINAL HEAT OF FORMATION = -32.90826 KCAL = -137.68818 KJ TOTAL ENERGY =
     * -1618.31024 EV ELECTRONIC ENERGY = -6569.42640 EV POINT GROUP: C1
     * CORE-CORE REPULSION = 4951.11615 EV IONIZATION POTENTIAL = 10.76839 NO.
     * OF FILLED LEVELS = 23 MOLECULAR WEIGHT = 122.123
     */

    @Override
    /** {@inheritDoc} */
    public <T extends IChemObject> T read(T object) throws CDKException {
        final String[] expected_columns = {"NO.", "ATOM", "X", "Y", "Z"};
        StringBuffer eigenvalues = new StringBuffer();
        if (object instanceof IAtomContainer) {
            IAtomContainer container = (IAtomContainer) object;
            try {
                String line = input.readLine();
                while (line != null) {
                    if (line.indexOf("****  MAX. NUMBER OF ATOMS ALLOWED") > -1) throw new CDKException(line);
                    if (line.indexOf("TO CONTINUE CALCULATION SPECIFY \"GEO-OK\"") > -1) throw new CDKException(line);
                    if ("CARTESIAN COORDINATES".equals(line.trim())) {

                        IAtomContainer atomcontainer = ((IAtomContainer) object);
                        input.readLine(); //reads blank line
                        line = input.readLine();

                        String[] columns = line.trim().split(" +");
                        int okCols = 0;
                        if (columns.length == expected_columns.length)
                            for (int i = 0; i < expected_columns.length; i++)
                                okCols += (columns[i].equals(expected_columns[i])) ? 1 : 0;

                        if (okCols < expected_columns.length) continue;
                        //if (!"    NO.       ATOM         X         Y         Z".equals(line)) continue;

                        input.readLine(); //reads blank line
                        int atomIndex = 0;
                        while (!line.trim().isEmpty()) {
                            line = input.readLine();
                            StringTokenizer tokenizer = new StringTokenizer(line);
                            int token = 0;

                            IAtom atom = null;
                            double[] point3d = new double[3];
                            while (tokenizer.hasMoreTokens()) {
                                String tokenStr = tokenizer.nextToken();
                                switch (token) {
                                    case 0: {
                                        atomIndex = Integer.parseInt(tokenStr) - 1;
                                        if (atomIndex < atomcontainer.getAtomCount()) {
                                            atom = atomcontainer.getAtom(atomIndex);
                                        } else
                                            atom = null;
                                        break;
                                    }
                                    case 1: {
                                        if ((atom != null) && (!tokenStr.equals(atom.getSymbol()))) atom = null;
                                        break;
                                    }
                                    case 2: {
                                        point3d[0] = Double.parseDouble(tokenStr);
                                        break;
                                    }
                                    case 3: {
                                        point3d[1] = Double.parseDouble(tokenStr);
                                        break;
                                    }
                                    case 4: {
                                        point3d[2] = Double.parseDouble(tokenStr);
                                        if (atom != null) atom.setPoint3d(new Point3d(point3d));
                                        break;
                                    }

                                }
                                token++;
                                if (atom == null) break;
                            }
                            if ((atom == null) || ((atomIndex + 1) >= atomcontainer.getAtomCount())) break;

                        }

                    } else if (line.indexOf(Mopac7Reader.eigenvalues) >= 0) {
                        line = input.readLine();
                        line = input.readLine();
                        while (!line.trim().equals("")) {
                            eigenvalues.append(line);
                            line = input.readLine();
                        }
                        container.setProperty(Mopac7Reader.eigenvalues, eigenvalues.toString());
                    } else
                        for (int i = 0; i < parameters.length; i++)
                            if (line.indexOf(parameters[i]) >= 0) {
                                String value = line.substring(line.lastIndexOf('=') + 1).trim();

                                /*
                                 * v = v.replaceAll("EV",""); v =
                                 * v.replaceAll("KCAL",""); v =
                                 * v.replaceAll("KJ","");
                                 */
                                value = value.replaceAll(Mopac7Reader.units[i], "").trim();
                                int pos = value.indexOf(' ');
                                if (pos >= 0) value = value.substring(0, pos - 1);
                                container.setProperty(parameters[i], value.trim());
                                break;
                            }
                    line = input.readLine();
                }
                calcHomoLumo(container);
                return (T) container;
            } catch (IOException exception) {
                throw new CDKException(exception.getMessage());
            }
        } else
            return null;
    }

    private void calcHomoLumo(IAtomContainer mol) {
        Object eigenProp = mol.getProperty(eigenvalues);
        if (eigenProp == null) return;
        //mol.getProperties().remove(eigenvalues);
        Object filledLevelsProp = mol.getProperty(filledLevels);
        //mol.getProperties().remove(filledLevels);
        if (filledLevelsProp == null) return;
        int nFilledLevels = 0;
        try {
            nFilledLevels = Integer.parseInt(filledLevelsProp.toString());
        } catch (NumberFormatException exception) {
            return;
        }
        String[] eigenVals = eigenProp.toString().split("\\s");
        int levelCounter = 0;
        for (int i = 0; i < eigenVals.length; i++) {
            if (eigenVals[i].trim().isEmpty())
                continue;
            else
                try {
                    // check if the value is an proper double:
                    Double.parseDouble(eigenVals[i]);
                    levelCounter++;
                    if (levelCounter == nFilledLevels) {
                        mol.setProperty("EHOMO", eigenVals[i]);
                    } else if (levelCounter == (nFilledLevels + 1)) {
                        mol.setProperty("ELUMO", eigenVals[i]);
                    }
                } catch (NumberFormatException exception) {
                    return;
                }
        }
    }

    @Override
    /** {@inheritDoc} */
    public void setReader(Reader input) throws CDKException {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader) input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    @Override
    /** {@inheritDoc} */
    public void setReader(InputStream input) throws CDKException {
        this.input = new BufferedReader(new InputStreamReader(input));
    }

    @Override
    /** {@inheritDoc} */
    public void close() throws IOException {
        input.close();

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
        return MOPAC7Format.getInstance();
    }
}
