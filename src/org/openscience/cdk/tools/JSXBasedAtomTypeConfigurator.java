/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 *
 * Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
 *
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
package org.openscience.cdk.tools;

import java.util.*;
import java.io.*;
import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;

import org.xml.sax.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

import JSX.*;

/**
 * Used to store and return data of a particular AtomType. The data is
 * stored in an XML file which is read with the JSX library. The file
 * can be found in org.openscience.cdk.config.structgen_atomtypes.xml.
 *
 * @author     steinbeck
 * @created    2001-08-29
 *
 * @keyword    atom, type
 */
public class JSXBasedAtomTypeConfigurator implements AtomTypeConfigurator {

    private String configFile = "org.openscience.cdk.config.structgen_atomtypes.xml";
    private InputStream ins = null;
    
    public JSXBasedAtomTypeConfigurator() {
    }
    
    /**
     * Sets the file containing the config data
     */
    public void setInputStream(InputStream ins) {
        this.ins = ins;
    };
    
    /**
     * Read AtomType's from a JSX based configuration file
     *
     * @return Returns a Vector of AtomType's.
     */
    public Vector readAtomTypes() throws Exception {
        Vector atomTypes = new Vector(0);
        if (ins == null) {
            try {
                ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
            } catch(Exception exc) {
                throw new IOException("There was a problem getting a stream for " + configFile +
                                      " with getClass.getClassLoader.getResourceAsStream");
            }
            if (ins == null) {
                try {
                    ins = this.getClass().getResourceAsStream(configFile);
                } catch(Exception exc) {
                    throw new IOException("There was a problem getting a stream for " + configFile +
                                          " with getClass.getResourceAsStream");
                }
            }
        }
        if (ins == null) throw new IOException("There was a problem getting an input stream");
        ObjIn in = new ObjIn(ins, new Config().aliasID(false));
        atomTypes = (Vector) in.readObject();
        for (int f = 0; f < atomTypes.size(); f++) {
            Object o = atomTypes.elementAt(f);
            if (o instanceof org.openscience.cdk.AtomType) {
                ((AtomType)o).init();
            } else {
                System.out.println("Expecting cdk.AtomType class, but got: " + o.getClass().getName());
            }
        }
        return atomTypes;
    }
   
}
