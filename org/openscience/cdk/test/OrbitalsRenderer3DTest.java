/*
 * OrbitalsRenderer3DTest.java
 *
 * Autor: Stephan Michels
 * EMail: stephan@vern.chem.tu-berlin.de
 * Datum: 9.6.2001
 *
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
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
 */

package org.openscience.cdk.test;

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
import java.awt.event.*;

/**
 *  Description of the Class
 *
 * @author     steinbeck
 * @created    July 22, 2001
 */
public class OrbitalsRenderer3DTest {
	private AcceleratedRenderer3DModel model;


	/**
	 *  Constructor for the OrbitalsRenderer3DTest object
	 *
	 * @param  inFile  Description of Parameter
	 */
	public OrbitalsRenderer3DTest(String inFile) {
		try {
			ChemObjectReader reader;
			System.out.println("Loading: " + inFile);
			if (inFile.endsWith(".xyz")) {
				reader = new XYZReader(new FileReader(inFile));
				System.out.println("Expecting XYZ format...");
			}
			else if (inFile.endsWith(".cml")) {
				reader = new CMLReader(new FileReader(inFile));
				System.out.println("Expecting CML format...");
			}
			else {
				reader = new MDLReader(new FileInputStream(inFile));
				System.out.println("Expecting MDL MolFile format...");
			}
			ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());

			ChemSequence[] chemSequence = chemFile.getChemSequences();
			ChemModel[] chemModels = chemSequence[0].getChemModels();
			AtomContainer atomContainer = chemModels[0].getAllInOneContainer();
			Atom[] atoms = atomContainer.getAtoms();
			System.out.println("Setting up Gaussian calculation ...");
			GaussiansBasis basis = new SimpleBasisSet(atoms);

			Orbitals orbitals = new Orbitals(basis);

			int count_electrons = 0;
			for (int i = 0; i < atoms.length; i++) {
				count_electrons += atoms[i].getElement().getAtomicNumber();
			}
			orbitals.setCountElectrons(count_electrons);
			System.out.println("Running Gaussian calculation ...");
			Job job = new Job(orbitals);
			
			orbitals = job.calculate();
			System.out.println("Gaussian calculation finished");
			JFrame frame = new JFrame("OrbitalsRenderer3DTest");
			frame.getContentPane().setLayout(new BorderLayout());

			model = new AcceleratedRenderer3DModel(atomContainer, orbitals, 0);
			AcceleratedRenderer3D renderer = new AcceleratedRenderer3D(model);

			frame.getContentPane().add(renderer, BorderLayout.CENTER);

			JComboBox orbitalChooser = new JComboBox();
			orbitalChooser.setLightWeightPopupEnabled(false);
			for (int i = 0; i < orbitals.getCountOrbitals(); i++) {
				orbitalChooser.addItem((i + 1) + ".Orbital");
			}

			orbitalChooser.addItemListener(
				new ItemListener() {
					/**
					 *  Description of the Method
					 *
					 * @param  e  Description of Parameter
					 */
					public void itemStateChanged(ItemEvent e) {
						model.setCurrentOrbital(((JComboBox) e.getSource()).getSelectedIndex());
					}
				});
			frame.getContentPane().add(orbitalChooser, BorderLayout.SOUTH);

			JCheckBox opaque = new JCheckBox("Opaque", true);
			opaque.addItemListener(
				new ItemListener() {
					/**
					 *  Description of the Method
					 *
					 * @param  e  Description of Parameter
					 */
					public void itemStateChanged(ItemEvent e) {
						model.setOrbitalOpaque(((JCheckBox) e.getSource()).isSelected());
					}
				});
			frame.getContentPane().add(opaque, BorderLayout.NORTH);

			//frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setSize(500, 500);
			frame.setVisible(true);
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}


	/**
	 *  The main program for the OrbitalsRenderer3DTest class
	 *
	 * @param  args  The command line arguments
	 */
	public static void main(String[] args) {
		if (args.length == 1) {
			String filename = args[0];
			if (new File(filename).canRead()) {
				new OrbitalsRenderer3DTest(filename);
			}
			else {
				System.out.println("File " + filename + " does not exist!");
			}
		}
		else {
			System.out.println("Syntax: OrbitalsRenderer3DTest <inputfile>");
		}
	}
}

