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
import org.openscience.cdk.validate.*;
import java.io.*;
import java.util.Vector;
import java.util.Enumeration;

/**
 * Command line utility for checking the chemical information from files.
 *
 * @author     egonw
 * @created    2003-07-14
 *
 * @keyword    command line util
 */
public class Validator {

    private org.openscience.cdk.tools.LoggingTool logger;
    
    public Validator() {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
    }

    public Vector validate(File input) throws IOException {
        ReaderFactory factory = new ReaderFactory();
        Reader fileReader = new FileReader(input);
        String format = factory.guessFormat(fileReader);
        // reopen file, to force to start at the beginning
        fileReader.close();
        fileReader = new FileReader(input);
        
        ChemObjectReader reader = null;
        // construct right reader
        if (format.equals("org.openscience.cdk.io.CMLReader")) {
            reader = new CMLReader(fileReader);
        } else if (format.equals("org.openscience.cdk.io.IChIReader")) {
            reader = new IChIReader(fileReader);
        } else if (format.equals("org.openscience.cdk.io.MDLReader")) {
            reader = new MDLReader(fileReader);
        } else if (format.equals("org.openscience.cdk.io.PDBReader")) {
            reader = new PDBReader(fileReader);
        } else if (format.equals("org.openscience.cdk.io.PMPReader")) {
            reader = new PMPReader(fileReader);
        } else if (format.equals("org.openscience.cdk.io.ShelXReader")) {
            reader = new ShelXReader(fileReader);
        } else if (format.equals("org.openscience.cdk.io.SMILESReader")) {
            reader = new SMILESReader(fileReader);
        } else if (format.equals("org.openscience.cdk.io.XYZReader")) {
            reader = new XYZReader(fileReader);
        }
        if (reader == null) {
            System.out.println("Cannot parse file of type: " + format);
            return new Vector();
        }
        
        // read contents from file
        ChemFile content = null;
        try {
            content = (ChemFile)reader.read((ChemObject)new ChemFile());
        } catch (CDKException exception) {
            System.out.println("Error while reading file: " + exception.toString());
            return new Vector();
        }
        if (content == null) {
            System.out.println("Cannot read contents from file.");
            return new Vector();
        }
        
        // validate contents
        return ChemFileValidator.validate(content);
    }
    
    public void outputErrors(Vector chemObjectErrors) {
        Enumeration errors = chemObjectErrors.elements();
        
        // output the errors
        while (errors.hasMoreElements()) {
            ValidationError error = (ValidationError)errors.nextElement();
            System.out.println("ERROR: " + error.getError());
        }
    }
    
    /**
     * Runs the program from the command line.
     *
     * @param  args  command line arguments.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("syntax: Validator <file> <file2> ...");
            System.exit(0);
        }
        
        Validator validator = new Validator();
        for (int i=0; i<args.length; i++) {
            String ifilename = args[i];
            try {
                File input = new File(ifilename);
                if (!input.isDirectory()) {
                    Vector errors = validator.validate(input);
                    validator.outputErrors(errors);
                } else {
                    System.out.println("Skipping directory: " + ifilename);
                }
            } catch (FileNotFoundException exception) {
                System.out.println("Skipping file. Cannot find it: " + ifilename);
            } catch (Exception exception) {
                System.err.println(ifilename + ": error=");
                exception.printStackTrace();
            }
        }
    }
    
}

