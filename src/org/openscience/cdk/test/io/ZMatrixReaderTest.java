/* ZMatrixReaderTest.java
 * 
 * Autor: Stephan Michels
 * EMail: stephan@vern.chem.tu-berlin.de
 * Datum: 20.7.2001
 * 
 * Copyright (C) 2001-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.io;

import java.io.File;
import java.io.FileReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.graph.rebond.RebondTool;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.ZMatrixReader;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * @cdk.module test-java3d
 */
public class ZMatrixReaderTest extends CDKTestCase {
    
    private String inFile;
    private boolean standAlone;
    
    public ZMatrixReaderTest(String name) {
        super(name);
        inFile = "";
        standAlone = false;
    }
    
    public void setInFile(String file) {
        this.inFile = file;
    }
    
    public static Test suite() {
        return new TestSuite(ZMatrixReaderTest.class);
    }

    // Do we have a ZMatrix test file??
    public void xtestFile() {
        try {        
            IChemObjectReader reader;
            System.out.println("Loading: " + inFile);
            reader = new ZMatrixReader(new FileReader(inFile));
            System.out.println("Expecting ZMatrix format...");
            
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            
            org.openscience.cdk.interfaces.IChemSequence[] chemSequence = chemFile.getChemSequences();
			org.openscience.cdk.interfaces.IChemModel[] chemModels = chemSequence[0].getChemModels();
			IAtomContainer atomContainer = ChemModelManipulator.getAllInOneContainer(chemModels[0]);
			RebondTool rebonder = new RebondTool(2.0, 0.5, 0.5);
			rebonder.rebond(atomContainer);
            
			/*if (standAlone) {
                JFrame frame = new JFrame("ZMatrixReaderTest");
                frame.getContentPane().setLayout(new BorderLayout());
                
                AcceleratedRenderer3D renderer = new AcceleratedRenderer3D(
                new AcceleratedRenderer3DModel(atomContainer));
                
                frame.getContentPane().add(renderer, BorderLayout.CENTER);
                
                //frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                frame.setSize(500,500);
                frame.setVisible(true);
            }*/
		} catch(Exception exc) {
            exc.printStackTrace();
        }
	}
    
	public static void main(String[] args) {
        if (args.length == 1) {
            String filename = args[0];
            if (new File(filename).canRead()) {
                ZMatrixReaderTest test = new ZMatrixReaderTest("Test");
                test.setInFile(filename);
                test.standAlone = true;
                test.xtestFile();
            } else {
                System.out.println("File " + filename + " does not exist!");
            }
        } else {
            System.out.println("Syntax: ZMatrixReaderTest <inputfile>");
        }
    }
}

