/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 *
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.config;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;

import org.openscience.cdk.AtomType;

/**
 * AtomType list configurator that uses the AtomTypes originally
 * defined in Jmol v5. This class was added to be able to port
 * Jmol to CDK. The AtomType's themselves seems have a computational
 * background, but this is not clear. 
 *
 * @cdk.module standard
 *
 * @author     Bradley A. Smith <bradley@baysmith.com>
 *
 * @cdk.keyword    atom, type
 */
public class TXTBasedAtomTypeConfigurator implements AtomTypeConfigurator {

    private String configFile = "jmol_atomtypes.txt";
    private InputStream ins = null;
    
	public TXTBasedAtomTypeConfigurator() {
	}

    /**
     * Sets the file containing the config data
     */
    public void setInputStream(InputStream ins) {
        this.ins = ins;
    };
    
    /**
     * Read a text based configuration file
     */
    public Vector readAtomTypes() throws Exception {
        Vector atomTypes = new Vector();

        if (ins == null) {
            // trying the default
            //System.out.println("readAtomTypes getResourceAsStream:"
            //                   + configFile);
            ins = getClass().getResourceAsStream(configFile);
        }
        if (ins == null) 
            throw new IOException("There was a problem getting the default stream: " + configFile);

        // read the contents from file
        BufferedReader r = new BufferedReader(new InputStreamReader(ins), 1024);
        StringTokenizer st;
        String s;
        
        try {
            while (true) {
                s = r.readLine();
                if (s == null) {
                    break;
                }
                if (!s.startsWith("#")) {
                    String name = "";
                    String rootType = "";
                    int an = 0, rl = 0, gl = 0, bl = 0;
                    double mass = 0.0, vdw = 0.0, covalent = 0.0;
                    st = new StringTokenizer(s, "\t ,;");
                    int nt = st.countTokens();
                    
                    if (nt == 9) {
                        name = st.nextToken();
                        rootType = st.nextToken();
                        String san = st.nextToken();
                        String sam = st.nextToken();
                        String svdw = st.nextToken();
                        String scov = st.nextToken();
                        String sr = st.nextToken();
                        String sg = st.nextToken();
                        String sb = st.nextToken();
                        
                        try {
                            mass = new Double(sam).doubleValue();
                            vdw = new Double(svdw).doubleValue();
                            covalent = new Double(scov).doubleValue();
                            an = Integer.parseInt(san);
                            rl = Integer.parseInt(sr);
                            gl = Integer.parseInt(sg);
                            bl = Integer.parseInt(sb);
                        } catch (NumberFormatException nfe) {
                            throw new IOException("AtomTypeTable.ReadAtypes: " +
                            "Malformed Number");
                        }
                        
                        AtomType at = new AtomType(name, rootType);
                        at.setAtomicNumber(an);
                        at.setExactMass(mass);
                        at.setVanderwaalsRadius(vdw);
                        at.setCovalentRadius(covalent);
                        Color co = new Color(rl, gl, bl);
                        at.setProperty("org.openscience.cdk.renderer.color", co);
                        atomTypes.addElement(at);
                    } else {
                        throw new IOException("AtomTypeTable.ReadAtypes: " + 
                        "Wrong Number of fields");
                    }
                }
            }    // end while
            ins.close();
        } catch (IOException e) {
            System.err.println(e.toString());
            e.printStackTrace();
        }        
        return atomTypes;
    }
}
