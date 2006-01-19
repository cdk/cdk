/* $RCSfile$
 * $Author$
 * $Date$   
 * $Revision$
 * 
 * Copyright (C) 2002-2005  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
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

package org.openscience.cdk.test.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.applications.swing.AtomicTable;
import org.openscience.cdk.applications.swing.MoleculeViewer2D;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.geometry.Projector;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.ChemObjectReader;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.XYZReader;
import org.openscience.cdk.tools.LoggingTool;

/**
 * @cdk.module test
 */
public class FileReaderTest {

    private LoggingTool logger;

    public FileReaderTest(String inFile) {
      logger = new LoggingTool(this);

      try {
        ChemObjectReader reader;
        logger.info("Loading: ", inFile);
        if (inFile.endsWith(".xyz")) {
  	      reader = new XYZReader(new FileReader(inFile));
          logger.info("Expecting XYZ format...");
        } else if (inFile.endsWith(".cml")) {
  	      reader = new CMLReader(new FileReader(inFile));
          logger.info("Expecting CML format...");
        } else {
          reader = new MDLReader(new FileInputStream(inFile));
          logger.info("Expecting MDL MolFile format...");
        }
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());

        org.openscience.cdk.interfaces.IChemSequence chemSequence;
        org.openscience.cdk.interfaces.IChemModel chemModel;
        org.openscience.cdk.interfaces.ISetOfMolecules setOfMolecules;
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
	        	  org.openscience.cdk.interfaces.IMolecule m = setOfMolecules.getMolecule(i);
              // since there is no Renderer3D yet, project in XY plane
              if (!GeometryTools.has2DCoordinates(m)) Projector.project2D(m);
              MoleculeViewer2D mv = new MoleculeViewer2D(m);
              mv.display();
              AtomicTable at = new AtomicTable(m);
              at.display();
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

