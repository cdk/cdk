/* 
 * $RCSfile$   
 * $Author$   
 * $Date$   
 * $Revision$
 * 
 * Copyright (C) 1997-2001  The CDK project
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

package org.openscience.cdk.test;

import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.renderer.*;
import java.util.*;
import java.io.*;
import java.net.URL;

public class CMLReaderTest {

    CMLReader reader;
    ChemFile chemFile;
    ChemSequence chemSequence;
    ChemModel chemModel;
    SetOfMolecules setOfMolecules;
    Molecule[] molecules;
    
    public CMLReaderTest(String inFile) {
	try {
	    reader = new CMLReader(new FileReader(inFile));
	    chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());

	    chemSequence = chemFile.getChemSequence(0);
	    chemModel = chemSequence.getChemModel(0);
	    setOfMolecules = chemModel.getSetOfMolecules();
	    molecules = new Molecule[setOfMolecules.getMoleculeCount()];
	    for (int i = 0; i < setOfMolecules.getMoleculeCount(); i++) {
		molecules[i] = setOfMolecules.getMolecule(i);
	    }
	    new MoleculeViewer2D(molecules[0]);
	} catch(Exception exc) {
	    exc.printStackTrace();
	}
    }
    
    public static void main(String[] args) {
	if (args.length == 1) {
	    new CMLReaderTest(args[0]);
	} else {
	    System.out.println("Syntax: CMLReaderTest <inputfile>");
	}
    }
}

