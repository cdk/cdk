/*
 * $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2002  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.exception.*;
import java.io.*;
import java.util.*;

/**
 * Program that converts a file from one format to a file with another format.
 * Supported formats are:
 *   input: CML, XYZ, MDLMolfile
 *  output: CML, MDL Molfile
 *
 *  @keyword command line util
 *  @keyword file format
 */
public class FileConvertor {

    private org.openscience.cdk.tools.LoggingTool logger;

    private ChemObjectReader input;
    private ChemObjectWriter output;
    private ChemFile chemFile;

    private String iformat;
    private String oformat;

    public FileConvertor(String iformat, String oformat) {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
        logger.dumpSystemProperties();

        this.iformat = iformat;
        this.oformat = oformat;
    }

  public boolean convert(File input, File output) {
    boolean success = true;
    try {
      FileReader fr = new FileReader(input);
      FileWriter fw = new FileWriter(output);

      ChemObjectReader cor = getChemObjectReader(this.iformat, fr);
      if (cor == null) {
        logger.warn("Format " + iformat + " is an unsupported input format.");
        System.err.println("Unsupported input format!");
        return false;
      }
      ChemObjectWriter cow = getChemObjectWriter(this.oformat, fw);
      if (cow == null) {
        logger.warn("Format " + oformat + " is an unsupported output format.");
        System.err.println("Unsupported output format!");
        return false;
      }

      ChemFile content = (ChemFile)cor.read((ChemObject)new ChemFile());
      fr.close();
      if (content == null) {
        return false;
      }
      try {
          cow.write(content.getChemSequence(0).getChemModel(0).getCrystal());
      } catch (UnsupportedChemObjectException e) {
          logger.error("Cannot add Crystal: " + e.toString());
          return false;
      }
      try {
          cow.write(content.getChemSequence(0).getChemModel(0).getSetOfMolecules());
      } catch (UnsupportedChemObjectException e) {
          logger.error("Cannot add SetOfMolecules: " + e.toString());
          return false;
      }
      fw.flush();
      fw.close();
    } catch (Exception e) {
      success = false;
      logger.error(e.toString());
      e.printStackTrace();
    }
    return success;
  }

  /**
   * actual program
   */
  public static void main(String[] args) {
    String input_format = "";
    String output_format = "";
    File input;
    File output;
    if (args.length == 4) {
      if (args[0].startsWith("-i")) {
        input_format = args[0].substring(2);
      }
      if (args[1].startsWith("-o")) {
        output_format = args[1].substring(2);
      }
      input = new File(args[2]);
      output = new File(args[3]);

      // do conversion
      FileConvertor fc = new FileConvertor(input_format, output_format);
      boolean success = fc.convert(input, output);
      if (success) {
          System.out.println("Conversion succeeded!");
      } else {
          System.out.println("Conversion failed!");
          System.exit(1);
      }
    } else {
      System.err.println("syntax: FileConverter -i<format> -o<format> <input> <output>");
      System.exit(1);
    }
  }

    private ChemObjectReader getChemObjectReader(String format, FileReader f) {
        if (format.equalsIgnoreCase("CML")) {
            return new CMLReader(f);
        } else if (format.equalsIgnoreCase("XYZ")) {
            return new XYZReader(f);
        } else if (format.equalsIgnoreCase("MOL")) {
            return new MDLReader(f);
        } else if (format.equalsIgnoreCase("PDB")) {
            return new PDBReader(f);
        } else if (format.equalsIgnoreCase("PMP")) {
            return new PMPReader(f);
        } else if (format.equalsIgnoreCase("SHELX")) {
            return new ShelXReader(f);
        }
        return null;
    }

    private ChemObjectWriter getChemObjectWriter(String format, FileWriter f) {
        if (format.equalsIgnoreCase("CML")) {
            return new CMLWriter(f);
        } else if (format.equalsIgnoreCase("MOL")) {
            return new MDLWriter(f);
        } else if (format.equalsIgnoreCase("SMILES")) {
            return new SMILESWriter(f);
        } else if (format.equalsIgnoreCase("SHELX")) {
            return new ShelXWriter(f);
        }
        return null;
    }
}



