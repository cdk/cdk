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
 *   input: CML, XYZ, MDL Molfile, PMP, ShelX
 *  output: CML, MDL Molfile, SMILES, ShelX
 *
 *  @keyword command line util
 *  @keyword file format
 */
public class FileConvertor {

    private org.openscience.cdk.tools.LoggingTool logger;

    private String iformat;
    private String ifilename;
    private ChemObjectReader cor;

    private String oformat;
    private String ofilename;
    private ChemObjectWriter cow;

    private ChemFile chemFile;

    private Vector chemObjectNames = new Vector();

    /**
     * Constructs a convertor for the file formats <code>iformat</code>
     * and <code>oformat</code>.
     *
     * @param iformat   format of the input
     * @param oformat   format of the output
     */
    public FileConvertor(String iformat, String oformat) {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
        logger.dumpSystemProperties();

        this.iformat = iformat;
        this.oformat = oformat;

        chemObjectNames.add("org.openscience.cdk.Molecule");
        chemObjectNames.add("org.openscience.cdk.SetOfMolecules");
        chemObjectNames.add("org.openscience.cdk.Crystal");
        chemObjectNames.add("org.openscience.cdk.ChemModel");
        chemObjectNames.add("org.openscience.cdk.ChemSequence");
        chemObjectNames.add("org.openscience.cdk.ChemFile");
    }

    /**
     * Convert file <code>ifilename</code> into a file <code>ofilename</code>.
     *
     * @param ifilename name of input file
     * @param ofilename name of output file
     */
    public boolean convert(String ifilename, String ofilename) {
        boolean success = true;
        this.ifilename = ifilename;
        this.ofilename = ofilename;
        try {
            cor = getChemObjectReader(this.iformat, 
                                      new FileReader(new File(ifilename)));
            if (cor == null) {
                logger.warn("Format " + iformat + " is an unsupported input format.");
                System.err.println("Unsupported input format!");
                return false;
            }

            ChemFile content = (ChemFile)cor.read((ChemObject)new ChemFile());
            if (content == null) {
                return false;
            }
            
            // create output file
            cow = getChemObjectWriter(this.oformat, ofilename);
            if (cow == null) {
                logger.warn("Format " + oformat + " is an unsupported output format.");
                System.err.println("Unsupported output format!");
                return false;
            }
            write(content);
            cow.close();
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
      String ifilename = args[2];
      String ofilename = args[3];

      // do conversion
      FileConvertor fc = new FileConvertor(input_format, output_format);
      boolean success = fc.convert(ifilename, ofilename);
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
        } else if (format.equalsIgnoreCase("ICHI")) {
            return new IChIReader(f);
        }
        return null;
    }

    private ChemObjectWriter getChemObjectWriter(String format, String ofilename) 
        throws IOException {
        FileWriter fw = new FileWriter(new File(ofilename));
        if (format.equalsIgnoreCase("CML")) {
            return new CMLWriter(fw);
        } else if (format.equalsIgnoreCase("MOL")) {
            return new MDLWriter(fw);
        } else if (format.equalsIgnoreCase("SMILES")) {
            return new SMILESWriter(fw);
        } else if (format.equalsIgnoreCase("SHELX")) {
            return new ShelXWriter(fw);
        } else if (format.equalsIgnoreCase("SVG")) {
            return new SVGWriter(fw);
        }
        return null;
    }

    /**
    * Since we do not know what kind of ChemObject the Writer supports,
    * and we want to output as much information as possible, use
    * the generalized mechanism below.
    */
    private void write(ChemFile cf) throws IOException {
        try {
            cow.write(cf);
        } catch (UnsupportedChemObjectException e) {
            logger.info("Cannot write ChemFile, recursing into ChemSequence's.");
            int count = cf.getChemSequenceCount();
            boolean needMoreFiles =
              (compare(new ChemSequence(), cow.highestSupportedChemObject()) < 0);
            for (int i=0; i < count; i++) {
                if (needMoreFiles) {
                    cow.close(); // possibly closing empty file
                    String fname = ofilename + "." + (i+1);
                    cow = getChemObjectWriter(this.oformat, fname);
                }
                write(cf.getChemSequence(i));
            }
        }
    }

    private void write(ChemSequence cs) throws IOException {
        try {
            cow.write(cs);
        } catch (UnsupportedChemObjectException e) {
            int count = cs.getChemModelCount();
            boolean needMoreFiles =
              (compare(new ChemModel(), cow.highestSupportedChemObject()) < 0);
            logger.info("Cannot write ChemSequence, recursing into ChemModel's.");
            for (int i=0; i < count; i++) {
                if (needMoreFiles) {
                    cow.close(); // possibly closing empty file
                    String fname = ofilename + "." + (i+1);
                    cow = getChemObjectWriter(this.oformat, fname);
                }
                write(cs.getChemModel(i));
            }
        }
    }

    private void write(ChemModel cm) throws IOException {
        try {
            cow.write(cm);
        } catch (UnsupportedChemObjectException e) {
            logger.info("Cannot write ChemModel, trying Crystal.");
            Crystal crystal = cm.getCrystal();
            if (crystal != null) {
                write(crystal);
            }
            SetOfMolecules som = cm.getSetOfMolecules();
            if (som != null) {
                write(som);
            }
        }
    }

    private void write(Crystal c) throws IOException {
        try {
            cow.write(c);
        } catch (UnsupportedChemObjectException e) {
            logger.error("Cannot write Crystal!");
        }
    }

    private void write(SetOfMolecules som) throws IOException {
        try {
            cow.write(som);
        } catch (UnsupportedChemObjectException e) {
            logger.error("Cannot write SetOfMolecules!");
        }
    }

    /**
     * Returns -1 if first object is 'larger' than second, zero
     * if equal and 1 if second is 'larger'.
     */
    private int compare(ChemObject one, ChemObject two) {
        String oneName = one.getClass().getName();
        int oneIndex   = chemObjectNames.indexOf(oneName);
        String twoName = two.getClass().getName();
        int twoIndex   = chemObjectNames.indexOf(twoName);
        int diff = twoIndex - oneIndex;
        logger.debug("Comparing " + oneName + " and " + twoName + ": " + diff);
        return diff;
    }

}



