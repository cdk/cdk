/*
 * ChemLoader.java
 *
 * Author: edgar luttmann
 *
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
package org.openscience.cdk.io;

import org.openscience.cdk.*;
import java.io.*;

/**
 * <p>ChemLoader is a central point for reading any kind of chemical file. 
 * It is not possible to instantiate an oject of this class, and the only 
 * supplied method is a static one.
 *
 * <p>The ReaderFactory does the same job based on the content of the file.
 * 
 * @author  Edgar Luttmann
 * 
 * @keyword file IO
 *
 * @see org.openscience.cdk.io.ReaderFactory
 */
public class ChemLoader {

    /** 
     * private constructor, not to allow instantiating 
     */
    private ChemLoader() {
    }

    /**
     * central static method for loading a chemfile.
     *
     * @param inFile the name of the file to load
     *
     * @return the loaded ChemFile
     */
    public static ChemFile read(String inFile) {
        ChemFile chemFile;
          try {
            ChemObjectReader reader;
            if (inFile.endsWith(".xyz")) {
                  reader = new XYZReader(new FileReader(inFile));
            } else if (inFile.endsWith(".cml")) {
                  reader = new CMLReader(new FileReader(inFile));
            } else if (inFile.endsWith(".pdb")) {
                  reader = new PDBReader(new FileReader(inFile));
            } else if (inFile.endsWith(".smi")) {
                  reader = new SMILESReader(new FileReader(inFile));
            } else if (inFile.endsWith(".pmp")) {
                  reader = new PMPReader(new FileReader(inFile));
            } else if (inFile.endsWith(".mol")) {
                  reader = new MDLReader(new FileReader(inFile));
            } else {
              reader = new MDLReader(new FileInputStream(inFile));
            }
            chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            return chemFile;
          } catch(Exception exc) {
              System.out.println("Error while reading file "+inFile);
              return null;
          }
    }
    
}
