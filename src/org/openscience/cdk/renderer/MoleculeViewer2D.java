/*  $RCSfile$    
 *  $Author$    
 *  $Date$    
 *  $Revision$
 *
 *  Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 *
 *  Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.renderer;

import org.openscience.cdk.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.ringsearch.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.geometry.*;
import org.openscience.cdk.event.*;
import org.openscience.cdk.layout.*;

import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;


/**
 *  A Swing-based implementation of Renderer2D for viewing molecules
 *
 * @author     steinbeck
 * @created    May 30, 2002
 */
public class MoleculeViewer2D extends JPanel implements CDKChangeListener
{
	public AtomContainer atomContainer;
	public Renderer2DModel r2dm;
	public Renderer2D renderer;
	public String title = "Molecule Viewer";

	private JFrame frame = null;


	/**
	 *  Constructs a MoleculeViewer with a molecule to display and a Renderer2DModel containing the information on how to display it.
	 *
	 * @param  r2dm           The rendere settings determining how the molecule is displayed
	 */
	public MoleculeViewer2D(AtomContainer atomContainer, Renderer2DModel r2dm)
	{
		this.atomContainer = atomContainer;
		this.r2dm = r2dm;
		r2dm.addCDKChangeListener(this);
		renderer = new Renderer2D(r2dm);
		frame = new JFrame();
	}


	/**
	 *  Constructs a MoleculeViewer with a molecule to display
	 */
	public MoleculeViewer2D(AtomContainer atomContainer)
	{
		this(atomContainer, new Renderer2DModel());
	}


	/**
	 *  Constructs a MoleculeViewer with a molecule to display
	 */
	public MoleculeViewer2D()
	{
		this(null, new Renderer2DModel());
	}


	/**
	 *  Sets the Frame attribute of the MoleculeViewer2D object
	 *
	 * @param  frame  The new Frame value
	 */
	public void setFrame(JFrame frame)
	{
		this.frame = frame;
	}


	/**
	 *  Sets a Renderer2DModel which determins the way a molecule is displayed
	 *
	 * @param  r2dm  The Renderer2DModel
	 */
	public void setRenderer2DModel(Renderer2DModel r2dm)
	{
		this.r2dm = r2dm;
		r2dm.addCDKChangeListener(this);
		renderer = new Renderer2D(r2dm);
	}


	/**
	 *  Sets the AtomContainer to be displayed
	 *
	 * @param  atomContainer  The AtomContainer to be displayed
	 */
	public void setAtomContainer(AtomContainer atomContainer)
	{
		this.atomContainer = atomContainer;
	}


	/**
	 *  Gets the Frame attribute of the MoleculeViewer2D object
	 *
	 * @return    The Frame value
	 */
	public JFrame getFrame()
	{
		return frame;
	}


	/**
	 *  Gets the Renderer2DModel which determins the way a molecule is displayed
	 *
	 * @return    The Renderer2DModel value
	 */
	public Renderer2DModel getRenderer2DModel()
	{
		return renderer.getRenderer2DModel();
	}


	/**
	 *  Returns the AtomContainer which is being displayed
	 *
	 * @return    The AtomContainer which is being displayed
	 */
	public AtomContainer getAtomContainer()
	{
		return this.atomContainer;
	}



	/**
	 *  Contructs a JFrame into which this JPanel is put and displays the frame with 
     *  the molecule.
	 */
	public void display()
	{
		setPreferredSize(new Dimension(600, 400));
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.getContentPane().add(this);
		frame.setTitle(title);
		frame.pack();
		frame.setVisible(true);
	}
	
	public static void display(Molecule molecule, boolean generateCoordinates)
	{	
		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
		MoleculeViewer2D mv = new MoleculeViewer2D();
		mv.getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Renderer2DModel r2dm = mv.getRenderer2DModel();
		r2dm.setDrawNumbers(true);
		
		try
		{
			if (generateCoordinates)
			{
				sdg.setMolecule((Molecule)molecule.clone());
				sdg.generateCoordinates();
				molecule = sdg.getMolecule();
			}
			mv.setAtomContainer(molecule);
			mv.display();
		}
		catch(Exception exc)
		{
			System.out.println("*** Exit due to an unexpected error during coordinate generation ***");
			exc.printStackTrace();
		}
	}

	/**
	 *  Paints the molecule onto the JPanel
	 *
	 * @param  g  The graphics used to paint with.
	 */
	public void paint(Graphics g)
	{
		super.paint(g);
		if (atomContainer != null)
		{
			setBackground(r2dm.getBackColor());
			GeometryTools.translateAllPositive(atomContainer);
			GeometryTools.scaleMolecule(atomContainer, getSize(), 0.8);
			GeometryTools.center(atomContainer, getSize());
			renderer.paintMolecule(atomContainer, g);
		}
	}



	/**
	 *  Method to notify this CDKChangeListener if something has changed in another object
	 *
	 * @param  e  The EventObject containing information on the nature and source of the event
	 */
	public void stateChanged(EventObject e)
	{
		repaint();
	}


	/**
	 *  The main method.
	 *
	 * @param  args  An MDL molfile
	 */

	public static void main(String[] args)
	{
		AtomContainer ac = null;
		try
		{
			FileInputStream fis = new FileInputStream(args[0]);
			MDLReader mr = new MDLReader(fis);
			ac = ((ChemFile) mr.read(new ChemFile())).getChemSequence(0).getChemModel(0).getSetOfMolecules().getMolecule(0);
			fis.close();
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
		}

		new MoleculeViewer2D(ac, new Renderer2DModel());
	}
}


