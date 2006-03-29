/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 *
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;

import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * AtomType list configurator that uses the AtomTypes originally
 * defined in Jmol v5. This class was added to be able to port
 * Jmol to CDK. The AtomType's themselves seems have a computational
 * background, but this is not clear. 
 *
 * @cdk.module core
 *
 * @author     Bradley A. Smith <bradley@baysmith.com>
 *
 * @cdk.keyword    atom, type
 */
public class TXTBasedAtomTypeConfigurator implements IAtomTypeConfigurator {

    private String configFile = "org/openscience/cdk/config/data/jmol_atomtypes.txt";
    private InputStream ins = null;
    
	public TXTBasedAtomTypeConfigurator() {
	}

    /**
     * Sets the file containing the config data
     */
    public void setInputStream(InputStream ins) {
        this.ins = ins;
    }
    
    /**
     * Read a text based configuration file
     */
    public Vector readAtomTypes(IChemObjectBuilder builder) throws IOException {
        Vector atomTypes = new Vector();

        if (ins == null) {
            // trying the default
            //System.out.println("readAtomTypes getResourceAsStream:"
            //                   + configFile);
            ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
        }
        if (ins == null) 
            throw new IOException("There was a problem getting the default stream: " + configFile);

        // read the contents from file
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins), 1024);
        StringTokenizer tokenizer;
        String string;
        
        try {
            while (true) {
                string = reader.readLine();
                if (string == null) {
                    break;
                }
                if (!string.startsWith("#")) {
                    String name = "";
                    String rootType = "";
                    int atomicNumber = 0, colorR = 0, colorG = 0, colorB = 0;
                    double mass = 0.0, vdwaals = 0.0, covalent = 0.0;
                    tokenizer = new StringTokenizer(string, "\t ,;");
                    int tokenCount = tokenizer.countTokens();
                    
                    if (tokenCount == 9) {
                        name = tokenizer.nextToken();
                        rootType = tokenizer.nextToken();
                        String san = tokenizer.nextToken();
                        String sam = tokenizer.nextToken();
                        String svdwaals = tokenizer.nextToken();
                        String scovalent = tokenizer.nextToken();
                        String sColorR = tokenizer.nextToken();
                        String sColorG = tokenizer.nextToken();
                        String sColorB = tokenizer.nextToken();
                        
                        try {
                            mass = new Double(sam).doubleValue();
                            vdwaals = new Double(svdwaals).doubleValue();
                            covalent = new Double(scovalent).doubleValue();
                            atomicNumber = Integer.parseInt(san);
                            colorR = Integer.parseInt(sColorR);
                            colorG = Integer.parseInt(sColorG);
                            colorB = Integer.parseInt(sColorB);
                        } catch (NumberFormatException nfe) {
                            throw new IOException("AtomTypeTable.ReadAtypes: " +
                            "Malformed Number");
                        }
                        
                        IAtomType atomType = builder.newAtomType(name, rootType);
                        atomType.setAtomicNumber(atomicNumber);
                        atomType.setExactMass(mass);
                        atomType.setVanderwaalsRadius(vdwaals);
                        atomType.setCovalentRadius(covalent);
                        Color color = new Color(colorR, colorG, colorB);
                        atomType.setProperty("org.openscience.cdk.renderer.color", color);
                        atomTypes.addElement(atomType);
                    } else {
                        throw new IOException("AtomTypeTable.ReadAtypes: " + 
                        "Wrong Number of fields");
                    }
                }
            }    // end while
            ins.close();
        } catch (IOException exception) {
            System.err.println(exception.toString());
            exception.printStackTrace();
            throw exception;
        }        
        return atomTypes;
    }
}
