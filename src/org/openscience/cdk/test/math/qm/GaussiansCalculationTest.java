/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2006  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *  
 */
package org.openscience.cdk.test.math.qm;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.XYZReader;
import org.openscience.cdk.math.qm.ClosedShellJob;
import org.openscience.cdk.math.qm.GaussiansBasis;
import org.openscience.cdk.math.qm.Orbitals;
import org.openscience.cdk.math.qm.SimpleBasisSet;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
 
/**
 * Demonstration of the quantum mechanical capabilities of CDK.
 * This application takes a XYZ, CML or MDL mol file, calculates
 * orbitals and outputs them to STDOUT.
 *
 * @cdk.module test
 *
 * @author  Stephan Michels <stephan@vern.chem.tu-berlin.de>
 * @cdk.created 2001-06-09
 *
 * @cdk.keyword command line util
 */
public class GaussiansCalculationTest
{
	public GaussiansCalculationTest(String inFile)
  { 
    try {
      IChemObjectReader reader;
      System.out.println("Loading: " + inFile);
      if (inFile.endsWith(".xyz"))
      { 
        reader = new XYZReader(new FileReader(inFile));
        System.out.println("Expecting XYZ format...");
      } else if (inFile.endsWith(".cml"))
      { 
        reader = new CMLReader(new FileReader(inFile));
        System.out.println("Expecting CML format...");
      } else
      { 
        reader = new MDLReader(new FileInputStream(inFile));
        System.out.println("Expecting MDL MolFile format...");
      }
      ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());

      org.openscience.cdk.interfaces.IChemSequence[] chemSequence = chemFile.getChemSequences();
      org.openscience.cdk.interfaces.IChemModel[] chemModels = chemSequence[0].getChemModels();
      IAtomContainer atomContainer = ChemModelManipulator.getAllInOneContainer(chemModels[0]);
      org.openscience.cdk.interfaces.IAtom[] atoms = atomContainer.getAtoms();

			GaussiansBasis basis = new SimpleBasisSet(atoms);
        
      Orbitals orbitals = new Orbitals(basis);
      
      int count_electrons = 0;
      for(int i=0; i<atoms.length; i++)
        count_electrons += atoms[i].getAtomicNumber();
      orbitals.setCountElectrons(count_electrons);
      
      ClosedShellJob job = new ClosedShellJob(orbitals);
      orbitals = job.calculate();
    } catch(Exception exc)
    { 
      exc.printStackTrace();
    }
  }

	public static void main(String[] args)
  { 
    if (args.length == 1)
    { 
      String filename = args[0];
      if (new File(filename).canRead())
      { 
        new GaussiansCalculationTest(filename);
      } else
      { 
        System.out.println("File " + filename + " does not exist!");
      }
    } else
    { 
      System.out.println("Syntax: GaussiansCalculationTest <inputfile>");
    }
  }
}
