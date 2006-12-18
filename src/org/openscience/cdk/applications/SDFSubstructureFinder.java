/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2006  Christoph Steinbeck <steinbeck@users.sf.net>
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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Command line utility that matches the given SMARTS against the given set of 
 * files.
 *
 * @cdk.module  experimental
 *
 * @author      Egon Willighagen
 * @cdk.created 2003-08-14
 * @cdk.require java1.4+
 * @cdk.require ant1.6
 */
public class SDFSubstructureFinder {

	/*
	 *  This is a command line application            *
	 *  Do not convert these System.out/err.println() *
	 *  to logger statements
	 */
	
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("syntax: SubstructureFinder <queryfile> <file1.sdf> <file2.sdf> ...");
            System.exit(0);
        }

        // to make sure the CDK LoggingTool is configured
        LoggingTool logger = new LoggingTool();
        LoggingTool.configureLog4j();
        logger.dumpSystemProperties();
	String ifilename = args[0];
	Molecule substructure = null;
	   SmilesGenerator smilesGenerator = new SmilesGenerator();
       try {
	    File input = new File(ifilename);
	    if (!input.isDirectory()) {
		IChemObjectReader reader = new ReaderFactory().createReader(new FileReader(input));
		if (reader.accepts(Molecule.class)) {
		   substructure = (Molecule)reader.read(new Molecule());
		}
	    }
	} catch (Exception exception) {
	    System.err.println(ifilename + ": error=");
	    exception.printStackTrace();
	}
 
        if (substructure != null) {
		Molecule molecule;
            // loop over all files
            for (int i=1; i<args.length; i++) {
                ifilename = args[i];
                try {
                    File input = new File(ifilename);
                    if (!input.isDirectory()) {
                        IteratingMDLReader reader = new IteratingMDLReader(new FileReader(input), DefaultChemObjectBuilder.getInstance());
                        while (reader.hasNext()) {
                            molecule = (Molecule)reader.next();
                            if (molecule != null) {
				//System.out.print(".");
                                boolean matches = UniversalIsomorphismTester.isSubgraph(molecule, substructure);
                                if (matches) {
                                    System.out.println("Query matches molecule with title: " + molecule.getProperty(CDKConstants.TITLE));
				    System.out.println("Hit molecule's remark: " + molecule.getProperty(CDKConstants.REMARK));
				    System.out.println("Hit molecule's SMILES is: " + smilesGenerator.createSMILES(molecule));
				    
                                }
                            }
                        }
                    }
                } catch (Exception exception) {
                    System.err.println(ifilename + ": error=");
                    exception.printStackTrace();
                }
            }
        } else {
            System.err.println("Too few arguments."); 
            System.exit(-1);
        }
    }

}
