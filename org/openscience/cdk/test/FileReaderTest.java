/* 
 * $RCSfile$   
 * $Author$   
 * $Date$   
 * $Revision$
 * 
 * Copyright (C) 1997-2001  The CDK project
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

package org.openscience.cdk.test;

import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.renderer.*;
import java.util.*;
import java.io.*;

public class FileReaderTest {

    public FileReaderTest(String inFile) {
      try {        
        ChemObjectReader reader;
        System.out.println("Loading: " + inFile);
        if (inFile.endsWith(".cml")) {
  	      reader = new CMLReader(new FileReader(inFile));
          System.out.println("Expecting CML format...");
        } else {
          reader = new MDLReader(new FileInputStream(inFile));
          System.out.println("Expecting MDL MolFile format...");
        }
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());

        ChemSequence chemSequence;
        ChemModel chemModel;
        SetOfMolecules setOfMolecules;
        System.out.println("  number of sequences: " + chemFile.getChemSequenceCount());
	      for (int sequence = 0; sequence < chemFile.getChemSequenceCount(); sequence++) {
          chemSequence = chemFile.getChemSequence(sequence);
          System.out.println("  number of models in sequence " + sequence + ": " + 
                             chemSequence.getChemModelCount());
          for (int model = 0; model < chemSequence.getChemModelCount(); model++) {
            chemModel = chemSequence.getChemModel(model);
            setOfMolecules = chemModel.getSetOfMolecules();
            System.out.println("  number of molecules in model " + model + ": " + 
                               setOfMolecules.getMoleculeCount());
	          for (int i = 0; i < setOfMolecules.getMoleculeCount(); i++) {
              MoleculeViewer2D mv = new MoleculeViewer2D(setOfMolecules.getMolecule(i));
              mv.display();
            }
          }
	      }
      } catch(Exception exc) {
	      exc.printStackTrace();
      }
    }
    
    public static void main(String[] args) {
      if (args.length == 1) {
        String filename = args[0];
        if (new File(filename).canRead()) {
          new FileReaderTest(filename);
        } else {
          System.out.println("File " + filename + " does not exist!");
        }
      } else {
        System.out.println("Syntax: FileReaderTest <inputfile>");
      }
    }
}

