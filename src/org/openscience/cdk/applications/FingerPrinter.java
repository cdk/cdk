/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.io.ChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.tools.ChemFileManipulator;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Command line utility that will generate fingerprints for a set of files.
 *
 * @cdkPackage applications
 *
 * @author  Egon Willighagen
 * @created 2003-08-14
 */
public class FingerPrinter {

	/*
	 *  This is a command line application            *
	 *  Do not convert these System.out/err.println() *
	 *  to logger statements
	 */
	
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("syntax: FingerPrinter <file> <file2> ...");
            System.exit(0);
        }
        
        // to make sure the CDK LoggingTool is configured
        LoggingTool logger = new LoggingTool(true);

        // loop over all files
        for (int i=0; i<args.length; i++) {
            String ifilename = args[i];
            try {
                File input = new File(ifilename);
                if (!input.isDirectory()) {
                    ChemObjectReader reader = new ReaderFactory().createReader(new FileReader(input));
                    if (reader.accepts(new Molecule())) {
                        ChemFile content = (ChemFile)reader.read((ChemObject)new ChemFile());
                        AtomContainer[] containers = ChemFileManipulator.getAllAtomContainers(content);
                        if (containers.length > 0) {
                            for (int j = 0; j < containers.length; j++) {
                                String print = Fingerprinter.getFingerprint(containers[j]).toString();
                                System.out.println(ifilename + " ("+ j +"): fingerprint=" + print);
                            }
                        }
                    }
                }
            } catch (Exception exception) {
                System.err.println(ifilename + ": error=");
                exception.printStackTrace();
            }
        }
    }

}
