/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.applications;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Enumeration;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.validate.BasicValidator;
import org.openscience.cdk.validate.CDKValidator;
import org.openscience.cdk.validate.ValidationReport;
import org.openscience.cdk.validate.ValidationTest;
import org.openscience.cdk.validate.ValidatorEngine;

/**
 * Command line utility for checking the chemical information from files.
 *
 * @cdk.module applications
 *
 * @author     egonw
 * @cdk.created    2003-07-14
 *
 * @cdk.keyword    command line util
 */
public class Validator {

    private static org.openscience.cdk.tools.LoggingTool logger;
    
	/*
	 *  This is a command line application            *
	 *  Do not convert these System.out/err.println() *
	 *  to logger statements
	 */
    
    public Validator() {
        logger = new LoggingTool(this);
        LoggingTool.configureLog4j();
        logger.dumpSystemProperties();
    }
    
    public ValidationReport validate(File input) throws IOException {
        Reader fileReader = new FileReader(input);
        IChemObjectReader reader = new ReaderFactory().createReader(fileReader);
        if (reader == null) {
            System.out.println("Cannot parse file with unknown file type: " + input.toString());
            return new ValidationReport();
        }
        
        // read contents from file
        ChemFile content = null;
        try {
            content = (ChemFile)reader.read((ChemObject)new ChemFile());
        } catch (CDKException exception) {
            logger.debug(exception);
            System.out.println("Error while reading file: " + exception.toString());
            return new ValidationReport();
        }
        if (content == null) {
            System.out.println("Cannot read contents from file.");
            return new ValidationReport();
        }
        
        // validate contents
        ValidatorEngine engine = new ValidatorEngine();
        engine.addValidator(new CDKValidator());
        engine.addValidator(new BasicValidator());
        return engine.validateChemFile(content);
    }
    
    public void outputErrors(String filename, ValidationReport report) {
        Enumeration errors = report.getErrors().elements();
        while (errors.hasMoreElements()) {
            ValidationTest test = (ValidationTest)errors.nextElement();
            System.out.println(filename + ": <ERROR> " + test.getError());
            if (test.getDetails().length() > 0) {
                System.out.println("  " + test.getDetails());
            }
        }
        errors = report.getWarnings().elements();
        while (errors.hasMoreElements()) {
            ValidationTest test = (ValidationTest)errors.nextElement();
            System.out.println(filename + ": <WARNING> " + test.getError());
            if (test.getDetails().length() > 0) {
                System.out.println("  " + test.getDetails());
            }
        }
        errors = report.getCDKErrors().elements();
        while (errors.hasMoreElements()) {
            ValidationTest test = (ValidationTest)errors.nextElement();
            System.out.println(filename + ": <CDK ERROR> " + test.getError());
            if (test.getDetails().length() > 0) {
                System.out.println("  " + test.getDetails());
            }
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
                    ValidationReport report = validator.validate(input);
                    validator.outputErrors(ifilename, report);
                } else {
                    System.out.println("Skipping directory: " + ifilename);
                }
            } catch (FileNotFoundException exception) {
                logger.debug(exception);
                System.out.println("Skipping file. Cannot find it: " + ifilename);
            } catch (Exception exception) {
                logger.debug(exception);
                System.err.println(ifilename + ": error=");
                exception.printStackTrace();
            }
        }
    }
    
}

