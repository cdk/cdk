/* MoleculeViewer2D.java
 * 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2000  The CompChem project
 * 
 * Contact: steinbeck@ice.mpg.de, geelter@maul.chem.nd.edu, egonw@sci.kun.nl
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

package org.openscience.cdk.renderer;


import org.openscience.cdk.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.ringsearch.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.geometry.*;
import org.openscience.cdk.event.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;



/** 
 * A Swing-based implementation of Renderer2D for viewing molecules
 */
public class MoleculeViewer2D extends JPanel implements CDKChangeListener
{
	static AtomContainer atomContainer;
	public Renderer2DModel r2dm;
	public Renderer2D renderer;

	/**
	 * Constructs a MoleculeViewer with a molecule to display
	 * and a Renderer2DModel containing the information on how to display it.
	 *
	 * @param   molecule  The molecule to be displayed
	 * @param   r2dm  The rendere settings determining how the molecule is displayed
	 */
	public MoleculeViewer2D(AtomContainer atomContainer,Renderer2DModel r2dm)
	{
		this.atomContainer = atomContainer;
		this.r2dm = r2dm;
		r2dm.addCDKChangeListener(this);
		renderer = new Renderer2D(r2dm);
	}
	
	/**
	 * Constructs a MoleculeViewer with a molecule to display
	 *
	 * @param   molecule  The molecule to be displayed
	 */
	public MoleculeViewer2D(AtomContainer atomContainer)
	{
		this.atomContainer = atomContainer;
		r2dm = new Renderer2DModel();
		r2dm.addCDKChangeListener(this);
		renderer = new Renderer2D(r2dm);
	}
	

	/**
	 * Constructs a MoleculeViewer with a molecule to display
	 *
	 */
	public MoleculeViewer2D()
	{
		r2dm = new Renderer2DModel();
		r2dm.addCDKChangeListener(this);
		renderer = new Renderer2D(r2dm);
	}


	/**
	 * Contructs a JFrame into which this JPanel is
	 * put and displays the frame with the molecule
	 */
	public void display()
	{
		setPreferredSize(new Dimension(600, 400));
		setBackground(r2dm.getBackColor());
		GeometryTools.translateAllPositive(atomContainer);
		GeometryTools.scaleMolecule(atomContainer, getPreferredSize(), 0.8);			
		GeometryTools.center(atomContainer, getPreferredSize());
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(this);
		frame.pack();
		frame.setVisible(true);
		
	}
	

	/**
	 * Paints the molecule onto the JPanel
	 *
	 * @param   g  The graphics used to paint with.
	 */
	public void paint(Graphics g)
	{
		super.paint(g);
		if (atomContainer != null)
		{
			renderer.paintMolecule(atomContainer, g);
		}
	}

	/**
	 * The main method.
	 *
	 * @param   args    An MDL molfile
	 */	

	 public static void main(String[] args)
	 {
		try
		{
			FileInputStream fis = new FileInputStream(args[0]);
			MDLReader mr = new MDLReader(fis);
			atomContainer = ((ChemFile)mr.read(new ChemFile())).getChemSequence(0).getChemModel(0).getSetOfMolecules().getMolecule(0);
			fis.close();
		}
		catch(Exception exc)
		{
			exc.printStackTrace();		
		}
		new MoleculeViewer2D(atomContainer, new Renderer2DModel());
	}


	/**
	 * Sets a Renderer2DModel which determins the way a molecule is displayed
	 *
	 * @param   r2dm  The Renderer2DModel
	 */
	public void setRenderer2DModel(Renderer2DModel r2dm)
	{
		this.r2dm = r2dm;
	}


	/**
	 * Method to notify this CDKChangeListener if something 
	 * has changed in another object
	 *
	 * @param   e  The EventObject containing information on the nature and source of the event
	 */
	public void stateChanged(EventObject e)
	{
		repaint();
	}

	/**
	 * Returns the AtomContainer which is being displayed
	 *
	 * @return The AtomContainer which is being displayed    
	 */
	public static AtomContainer getAtomContainer()
	{
		return MoleculeViewer2D.atomContainer;
	}

	/**
	 * Sets the AtomContainer to be displayed
	 *
	 * @param   atomContainer The AtomContainer to be displayed 
	 */
	public static void setAtomContainer(AtomContainer atomContainer)
	{
		MoleculeViewer2D.atomContainer = atomContainer;
	}
}


