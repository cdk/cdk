/* ZMatrixReaderTest.java
 * 
 * Autor: Stephan Michels
 * EMail: stephan@vern.chem.tu-berlin.de
 * Datum: 20.7.2001
 * 
 * Copyright (C) 2001  The CDK project
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
import org.openscience.cdk.tools.*;
import org.openscience.cdk.geometry.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;

public class ZMatrixReaderTest
{
  public ZMatrixReaderTest(String inFile) 
	{
  	try 
		{        
    	ChemObjectReader reader;
      System.out.println("Loading: " + inFile);
  	  reader = new ZMatrixReader(new FileReader(inFile));
      System.out.println("Expecting ZMatrix format...");

      ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());

			ChemSequence[] chemSequence = chemFile.getChemSequences();
			ChemModel[] chemModels = chemSequence[0].getChemModels();
			AtomContainer atomContainer = chemModels[0].getAllInOneContainer();
      atomContainer.addBonds(1.5); // new!!!

			JFrame frame = new JFrame("ZMatrixReaderTest");
			frame.getContentPane().setLayout(new BorderLayout());
			
			AcceleratedRenderer3D renderer = new AcceleratedRenderer3D(
						new AcceleratedRenderer3DModel(atomContainer));

			frame.getContentPane().add(renderer, BorderLayout.CENTER);

			//frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setSize(500,500);
			frame.setVisible(true);
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
      	new ZMatrixReaderTest(filename);
      } else 
			{
      	System.out.println("File " + filename + " does not exist!");
      }
    } else 
		{
    	System.out.println("Syntax: ZMatrixReaderTest <inputfile>");
    }
  }
}

