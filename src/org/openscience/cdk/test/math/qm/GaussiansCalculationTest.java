/*
 * $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */

package org.openscience.cdk.test.math.qm;
 
import org.openscience.cdk.*;
import org.openscience.cdk.math.qm.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.tools.*; 
import org.openscience.cdk.geometry.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
 
/**
 *
 * @author  Stephan Michels <stephan@vern.chem.tu-berlin.de>
 * @created 2001-06-09
 */
public class GaussiansCalculationTest
{
	public GaussiansCalculationTest(String inFile)
  { 
    try      
    {        
      ChemObjectReader reader;
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

      ChemSequence[] chemSequence = chemFile.getChemSequences();
      ChemModel[] chemModels = chemSequence[0].getChemModels();
      AtomContainer atomContainer = chemModels[0].getAllInOneContainer();
			Atom[] atoms = atomContainer.getAtoms();

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
