/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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
 * 
 */

package org.openscience.cdk.database;


import java.sql.*;
import java.io.*;
import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.exception.*;

/**
 * @author Yong Zhang <yz237@cam.ac.uk>
 */
public class XindiceReader implements ChemObjectReader {
    
    private String collection;
    
    public XindiceReader(String collection) {
        this.collection = collection;
    }

    public ChemObject read(ChemObject object) throws CDKException {
        if (object instanceof Molecule) {
            return (ChemObject)readMolecule();
        } else {
            throw new CDKException("Only supported Molecule.");
        }
    }
    
    private Molecule readMolecule() throws CDKException{
        return new Molecule();
    }

    public void setQuery(String xpath) {
    }
    
}
