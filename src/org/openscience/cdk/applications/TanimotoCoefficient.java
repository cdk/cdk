/* $RCSfile$
 * $Author: miguelrojasch $
 * $Date: 2006-09-27 13:32:31 +0000 (Mi, 27 Sep 2006) $
 * $Revision: 7069 $
 *
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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
import java.util.BitSet;
import java.util.List;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * Command line utility that will generate fingerprints for a set of files and calculate the tanimoto coefficient.
 *
 * @cdk.module applications
 *
 * @author  Egon Willighagen
 * @author  Stefan Kuhn
 * @cdk.created 2006-11-11
 * @cdk.keyword tanimoto
 */
public class TanimotoCoefficient {

	/*
	 *  This is a command line application            *
	 *  Do not convert these System.out/err.println() *
	 *  to logger statements
	 */
	
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("syntax: FingerPrinter <file> <file2>");
            System.exit(0);
        }
        
        // to make sure the CDK LoggingTool is configured
        LoggingTool logger = new LoggingTool();
        LoggingTool.configureLog4j();
        logger.dumpSystemProperties();

        Fingerprinter fingerprinter = new Fingerprinter();
        
        // loop over all files
        BitSet[] bs=new BitSet[args.length];
        for (int i=0; i<args.length; i++) {
            String ifilename = args[i];
            try {
                File input = new File(ifilename);
                if (!input.isDirectory()) {
                    IChemObjectReader reader = new ReaderFactory().createReader(new FileReader(input));
                    if (reader.accepts(Molecule.class)) {
                        ChemFile content = (ChemFile)reader.read((ChemObject)new ChemFile());
                        List containersList = ChemFileManipulator.getAllAtomContainers(content);
                        if (containersList.size() > 0) {
                            for (int j = 0; j < containersList.size(); j++) {
                            	bs[i]=fingerprinter.getFingerprint((IAtomContainer)containersList.get(j));
                                String print = bs[i].toString();
                                System.out.println(ifilename + " ("+ j +"): fingerprint=" + print);
                            }
                        }
                    }
                }
            } catch (Exception exception) {
                logger.debug(exception);
                System.err.println(ifilename + ": error=");
                exception.printStackTrace();
            }
        }
        int matches=0;
        int all=0;
        for(int i=0;i<1024;i++){
        	if(bs[0].get(i) && bs[1].get(i))
        		matches++;
        	if(bs[1].get(i))
        		all++;
        	if(bs[0].get(i))
        		all++;
        }
        System.err.println("Similarity (Tanimoto) = "+((double)matches/(double)all));
    }

}
