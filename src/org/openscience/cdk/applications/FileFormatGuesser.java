/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
import java.io.FileReader;

import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Program that guesses the format of a file.
 *
 * @cdk.module applications
 *
 *  @cdk.keyword command line util
 *  @cdk.keyword file format
 */
public class FileFormatGuesser {

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
            System.err.println("syntax: FileFormatGuesser <file> <file2> ...");
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
                    IChemObjectReader reader = factory.createReader(new FileReader(input));
                    if (reader != null) {
                        System.out.println(ifilename + ": " + reader.getFormat().getFormatName());
                    } else {
                        System.out.println(ifilename + ": unknown format");
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
}



