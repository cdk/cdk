/* Copyright (C) 1997-2007  Bradley A. Smith <bradley@baysmith.com>
 *
 * Contact: cdk-developers@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * AtomType list configurator that uses the AtomTypes originally
 * defined in Jmol v5. This class was added to be able to port
 * Jmol to CDK. The AtomType's themselves seems have a computational
 * background, but this is not clear.
 *
 * @cdk.module core
 * @cdk.githash
 *
 * @author     Bradley A. Smith <bradley@baysmith.com>
 *
 * @cdk.keyword    atom, type
 */
@TestClass("org.openscience.cdk.config.TXTBasedAtomTypeConfiguratorTest")
public class TXTBasedAtomTypeConfigurator implements IAtomTypeConfigurator {

    private String      configFile = "org/openscience/cdk/config/data/jmol_atomtypes.txt";
    private InputStream ins        = null;

    @TestMethod("testTXTBasedAtomTypeConfigurator")
    public TXTBasedAtomTypeConfigurator() {}

    /** {@inheritDoc} */
    @Override
    @TestMethod("testSetInputStream_InputStream")
    public void setInputStream(InputStream ins) {
        this.ins = ins;
    }

    /**
     * Reads a text based configuration file.
     *
     * @param builder IChemObjectBuilder used to construct the IAtomType's.
     * @throws        IOException when a problem occurred with reading from the InputStream
     * @return        A List with read IAtomType's.
     */
    @TestMethod("testReadAtomTypes_IChemObjectBuilder")
    public List<IAtomType> readAtomTypes(IChemObjectBuilder builder) throws IOException {
        List<IAtomType> atomTypes = new ArrayList<IAtomType>();

        if (ins == null) {
            // trying the default
            //logger.debug("readAtomTypes getResourceAsStream:"
            //                   + configFile);
            ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
        }
        if (ins == null) throw new IOException("There was a problem getting the default stream: " + configFile);

        // read the contents from file
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins), 1024);
        StringTokenizer tokenizer;
        String string;

        while (true) {
            string = reader.readLine();
            if (string == null) {
                break;
            }
            if (!string.startsWith("#")) {
                String name;
                String rootType;
                int atomicNumber, colorR, colorG, colorB;
                double mass, covalent;
                tokenizer = new StringTokenizer(string, "\t ,;");
                int tokenCount = tokenizer.countTokens();

                if (tokenCount == 9) {
                    name = tokenizer.nextToken();
                    rootType = tokenizer.nextToken();
                    String san = tokenizer.nextToken();
                    String sam = tokenizer.nextToken();
                    tokenizer.nextToken(); // skip the vdw radius value
                    String scovalent = tokenizer.nextToken();
                    String sColorR = tokenizer.nextToken();
                    String sColorG = tokenizer.nextToken();
                    String sColorB = tokenizer.nextToken();

                    try {
                        mass = new Double(sam);
                        covalent = new Double(scovalent);
                        atomicNumber = Integer.parseInt(san);
                        colorR = Integer.parseInt(sColorR);
                        colorG = Integer.parseInt(sColorG);
                        colorB = Integer.parseInt(sColorB);
                    } catch (NumberFormatException nfe) {
                        throw new IOException("AtomTypeTable.ReadAtypes: " + "Malformed Number");
                    }

                    IAtomType atomType = builder.newInstance(IAtomType.class, name, rootType);
                    atomType.setAtomicNumber(atomicNumber);
                    atomType.setExactMass(mass);
                    atomType.setCovalentRadius(covalent);

                    // pack the RGB color space components into a single int. Note we
                    // avoid java.awt.Color (not available on some JREs)
                    atomType.setProperty("org.openscience.cdk.renderer.color", ((colorR << 16) & 0xff0000)
                            | ((colorG << 8) & 0x00ff00) | (colorB & 0x0000ff));
                    atomTypes.add(atomType);
                } else {
                    throw new IOException("AtomTypeTable.ReadAtypes: " + "Wrong Number of fields");
                }
            }
        } // end while
        ins.close();

        return atomTypes;
    }
}
