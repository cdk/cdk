/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001  The CDK project
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

package org.openscience.cdk.applications;

import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.geometry.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;

public class Viewer {

    private org.openscience.cdk.tools.LoggingTool logger;

    private boolean useJava3D;

    public Viewer(String inFile, boolean useJava3D) {
        this.useJava3D = useJava3D;

      logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
      logger.dumpSystemProperties();
      logger.dumpClasspath();

      ChemFile chemFile = new ChemFile();
      try {
        ChemObjectReader reader;
        logger.info("Loading: " + inFile);
        if (inFile.endsWith(".xyz")) {
  	      reader = new XYZReader(new FileReader(inFile));
          logger.info("Expecting XYZ format...");
        } else if (inFile.endsWith(".cml")) {
          String url = "file:" + System.getProperty("user.dir") + "/" + inFile;
          reader = new CMLReader(url);
          logger.info("Expecting CML format...");
        } else if (inFile.endsWith(".pdb")) {
  	      reader = new PDBReader(new FileReader(inFile));
          logger.info("Expecting PDB format...");
        } else {
          reader = new MDLReader(new FileInputStream(inFile));
          logger.info("Expecting MDL MolFile format...");
        }
        chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
      } catch(Exception exc) {
	      logger.error("Error while reading file");
          logger.error(exc.toString());
		  System.exit(1);
      }


        ChemSequence chemSequence;
        ChemModel chemModel;
        SetOfMolecules setOfMolecules;
        logger.info("  number of sequences: " + chemFile.getChemSequenceCount());
    	for (int sequence = 0; sequence < chemFile.getChemSequenceCount(); sequence++) {
          chemSequence = chemFile.getChemSequence(sequence);
          logger.info("  number of models in sequence " + sequence + ": " +
                             chemSequence.getChemModelCount());
          for (int model = 0; model < chemSequence.getChemModelCount(); model++) {
            chemModel = chemSequence.getChemModel(model);
            setOfMolecules = chemModel.getSetOfMolecules();
            logger.info("  number of molecules in model " + model + ": " +
                               setOfMolecules.getMoleculeCount());
	          for (int i = 0; i < setOfMolecules.getMoleculeCount(); i++) {
              Molecule m = setOfMolecules.getMolecule(i);

			  // use Accelerated viewer if 3D coords are available
			  if (GeometryTools.has3DCoordinates(m)) {
			      logger.info("Viewing with 3D viewer");

				  boolean viewed = false;
				  if (useJava3D) {
            	      logger.debug(".. trying Java3D viewer");
                	  try {
                          JFrame frame = new JFrame("AcceleratedRenderer3DTest");
                          frame.getContentPane().setLayout(new BorderLayout());

                          AtomContainer atomContainer = chemModel.getAllInOneContainer();
                          AcceleratedRenderer3D renderer = new AcceleratedRenderer3D(
                              new AcceleratedRenderer3DModel(atomContainer));

                          frame.getContentPane().add(renderer, BorderLayout.CENTER);

                          frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                          frame.setSize(500,500);
                          frame.setVisible(true);
                          logger.debug(".. done");
                          viewed = true;
                      } catch (Exception e) {
                          logger.error("Viewing did not succeed!");
                          logger.error(e.toString());
                      }
                  }

                  // try to view it without Java3D
                  if (!viewed) {
                      logger.debug(".. trying non-Java3D viewer");
                      MoleculeViewer3D mv = new MoleculeViewer3D(m);
                      mv.display();
                      logger.debug(".. done");
                  }
              } else if (GeometryTools.has2DCoordinates(m)) {
                  logger.info("Viewing with 2D viewer");
                  MoleculeViewer2D mv = new MoleculeViewer2D(m);
                  mv.display();
              } else {
                  System.out.println("Molecule has no coordinates.");
              }
            }
          }
      }
    }
    
    public static void main(String[] args) {

        boolean useJava3D = true;

        String filename = "";
        if (args.length == 1) {
            filename = args[0];
        } else if (args.length > 1) {
            // parse options
            for (int i=1; i<args.length; i++) {
                String opt = args[i-1];
                if ("--nojava3d".equalsIgnoreCase(opt)) {
                    useJava3D = false;
                } else {
                    System.err.println("Unknown option: " + opt);
                    System.exit(1);
                }
            }

            filename = args[args.length -1];
        } else {
            System.out.println("Syntax : Viewer [options] <inputfile>");
            System.out.println();
            System.out.println("options: --nojava3D    Disable Java3D support");
            System.exit(0);
        }

        if (new File(filename).canRead()) {
            new Viewer(filename, useJava3D);
        } else {
            System.out.println("File " + filename + " does not exist!");
        }
    }
}

