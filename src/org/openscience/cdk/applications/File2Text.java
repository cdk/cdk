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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.applications;

import java.io.File;
import java.io.FileReader;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.io.ChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * Program that guesses the format of a file.
 *
 * @cdk.module applications
 *
 *  @cdk.keyword command line util
 *  @cdk.keyword file format
 */
public class File2Text {

    /**
     * Actual program.
     */
     
	/*
	 *  This is a command line application            *
	 *  Do not convert these System.out/err.println() *
	 *  to logger statements
	 */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("syntax: File2Text <file> <file2> ...");
            System.exit(0);
        }
        
        // to make sure the CDK LoggingTool is configured
        LoggingTool logger = new LoggingTool();
        LoggingTool.configureLog4j();
        logger.dumpSystemProperties();

        // loop over all files
        for (int i=0; i<args.length; i++) {
            String ifilename = args[i];
            try {
                ReaderFactory factory = new ReaderFactory();
                File input = new File(ifilename);
                if (!input.isDirectory()) {
                    ChemObjectReader reader = factory.createReader(new FileReader(input));
                    if (reader != null) {
                    	System.out.println("=== " + ifilename + " ===");
                        System.out.print(toText(reader));
                    } else {
                        System.out.println("Not a chemical file: " + ifilename);
                    }
                } else {
                    System.out.println("Skipping directory: " + ifilename);
                }
            } catch (Exception exception) {
                logger.debug(exception);
                System.err.println(ifilename + ": error=");
                exception.printStackTrace();
            }
        }
    }
    
    public static String toText(ChemObjectReader reader) throws Exception {
    	StringBuffer buffer = new StringBuffer();
    	ChemFile file = (ChemFile)reader.read(new ChemFile());
    	AtomContainer[] containers = ChemFileManipulator.getAllAtomContainers(file);
    	for (int i=0; i<containers.length; i++) {
    		if (containers[i].getProperty(CDKConstants.REMARK) != null) {
    			buffer.append("Remark: ").append(containers[i].getProperty(CDKConstants.REMARK)).append("\n");
    		}
    		if (containers[i].getProperty(CDKConstants.TITLE) != null) {
    			buffer.append("Title: ").append(containers[i].getProperty(CDKConstants.TITLE)).append("\n");
    		}
    		if (containers[i].getProperty(CDKConstants.COMMENT) != null) {
    			buffer.append("Comment: ").append(containers[i].getProperty(CDKConstants.COMMENT)).append("\n");
    		}
    	}
    	return buffer.toString();
    }
}



