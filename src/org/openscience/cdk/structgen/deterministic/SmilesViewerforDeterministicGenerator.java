
 /* SmilesViewerforDeterministicGenerator.java
 *
 * $RCSfile$    $Author$    $Date$    $Revision$
 *
 * Copyright (C) 1997, 1998, 1999, 2000  The JChemPaint project
 * 
 * Contact: steinbeck@ice.mpg.de
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
 * 
 */


package org.openscience.cdk.structgen.deterministic;

import java.util.Vector;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.vecmath.Vector2d;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.*;
import org.openscience.cdk.structgen.deterministic.*;
import org.openscience.cdk.applications.swing.*;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.swing.MoleculeListViewer;
import org.openscience.cdk.applications.swing.MoleculeViewer2D;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.structgen.RandomGenerator;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.smiles.SmilesParser;

public class SmilesViewerforDeterministicGenerator extends JApplet implements ActionListener{
	String s = "";
	TextField textField = null;
	String smilesString = "";
	Button drawButton = null;
	Checkbox toggleSymbols = null;
	Checkbox toggleHydrogens = null;
	Checkbox toggleNumbers = null;
	static boolean isApplication = false;
	
	public static void main(String[] args)
	{
		isApplication = true;
		JFrame frame = new JFrame();
		SmilesViewerforDeterministicGenerator sv = new SmilesViewerforDeterministicGenerator();
		sv.init();
		frame.addWindowListener(sv.getAWindowAdapter());
		frame.add(sv);
		frame.pack();
		frame.show();

	}

	public void init(){
		Container contentPane=getContentPane();
		contentPane.setLayout(new BorderLayout());
		textField = new TextField(30);
		textField.addActionListener(this);
		JPanel northPanel = new JPanel();
		JPanel southPanel = new JPanel();
		northPanel.setLayout(new FlowLayout());
		southPanel.setLayout(new FlowLayout());
		drawButton = new Button("Draw");
		drawButton.addActionListener(this);
		northPanel.add(textField);
		northPanel.add(drawButton);
		contentPane.add("North", northPanel);		
		toggleSymbols = new Checkbox("Draw Element Symbols");
		toggleHydrogens = new Checkbox("Draw Hydrogens");
		toggleNumbers = new Checkbox("Draw Numbers");		
		
		toggleSymbols.setState(false);
		toggleHydrogens.setState(false);
		toggleNumbers.setState(false);
		
		southPanel.add(toggleSymbols);
		southPanel.add(toggleHydrogens);		
		southPanel.add(toggleNumbers);			
		
		contentPane.add("South", southPanel);
	}
	
	public WindowAdapter getAWindowAdapter()
	{
		return new AWindowAdapter();
	}
	

	public void actionPerformed (java.awt.event.ActionEvent e){
			if (e.getSource() == drawButton || e.getSource() == textField){
				smilesString = textField.getText();
				JFrame frame = new JFrame(smilesString);
				frame.getContentPane().setLayout(new BorderLayout());
				try
				{
					MoleculeListViewer mlv = null;
					mlv = new MoleculeListViewer();
					mlv.setMolViewDim(new Dimension(400, 600));
					SmilesParser sp=new SmilesParser();
					Molecule mol=sp.parseSmiles(smilesString);
					StructureDiagramGenerator sdg = new StructureDiagramGenerator();
					MoleculeViewer2D mv = new MoleculeViewer2D();
					//mv.getRenderer2DModel().setDrawNumbers(true);
					sdg.setMolecule((Molecule) mol.clone());
					sdg.generateCoordinates();
					mv.setAtomContainer(sdg.getMolecule());
					mlv.addStructure(mv);
				}
				catch(Exception exc)
				{
					System.out.println(exc);
					return;
				}
			}
	}
	
	
	class AWindowAdapter extends java.awt.event.WindowAdapter
	{
		public void windowClosing(java.awt.event.WindowEvent we)
		{
			JFrame frame = (JFrame)we.getWindow();
			frame.setVisible(false);
			frame.dispose();
			if (isApplication) System.exit(0);
		}
	}
	
}

