/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.applications.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.Renderer2DModel;

/**
 * @cdk.module applications
 *
 * @author      steinbeck
 * @cdk.created 2002-10-29
 * @cdk.require swing
 */
public class MoleculeListViewer extends JFrame
{
	protected MoleculeListPanel panel;
	public static boolean standAlone = false;

	/**
	 *  Constructor for the MoleculeListViewer object
	 */
	public MoleculeListViewer()
	{
		super();
		getContentPane().setLayout(new BorderLayout());
		setTitle("Structure Display");
		panel = new MoleculeListPanel();
		getContentPane().add("Center", panel);
		setTitle("MoleculeListViewer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 *  Sets the molViewDim attribute of the MoleculeListViewer object
	 *
	 *@param  molViewDim  The new molViewDim value
	 */
	public void setMolViewDim(Dimension molViewDim) {
		panel.setMolViewDim(molViewDim);
	}

	/**
	 *  Gets the molViewDim attribute of the MoleculeListViewer object
	 *
	 *@return    The molViewDim value
	 */
	public Dimension getMolViewDim() {
		return panel.getMolViewDim();
	}


	/**
	 *  Adds a feature to the Structure attribute of the MoleculeListViewer object
	 *
	 *@param  moleculeViewer  The feature to be added to the Structure attribute
	 */
	public void addStructure(MoleculeViewer2D moleculeViewer) {
		panel.addStructure(moleculeViewer);
	}

	/**
	 *  Adds a feature to the Structure attribute of the MoleculeListViewer object
	 *
	 *@param  moleculeViewer     The feature to be added to the Structure attribute
	 *@param  title  The feature to be added to the Structure attribute
	 */
	public void addStructure(MoleculeViewer2D moleculeViewer, String title) {
		panel.addStructure(moleculeViewer, title);
		panel.revalidate();
	}

	/**
	 *  Adds a molecule in a MoleculeViewer2D to this list viewer
	 *  Convenience method.
	 *
	 *@param  molecule The Molecule object for which a display to add
	 *@param  generateCoordinates true, if 2D coordinates should be generated automatically
	 *@param  drawNumbers true, if the molecule should show atom numbers
	 *@param  title Title string to display
	 */

    public void addStructure(IMolecule molecule, boolean generateCoordinates, boolean drawNumbers, String title)
    {	
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        MoleculeViewer2D moleculeViewer = new MoleculeViewer2D();

        try
        {
            if (generateCoordinates)
            {
                sdg.setMolecule((IMolecule)molecule.clone());
                sdg.generateCoordinates();
                molecule = sdg.getMolecule();
            }
            moleculeViewer.setAtomContainer(molecule);
            moleculeViewer.setPreferredSize(new Dimension(300,300));
            Renderer2DModel r2dm = moleculeViewer.getRenderer2DModel();
            r2dm.setDrawNumbers(drawNumbers);
            panel.addStructure(moleculeViewer, title);
            
        }
        catch(Exception exc)
        {
            System.out.println("*** Exit due to an unexpected error during coordinate generation ***");
            exc.printStackTrace();
        }
    }
	
	
	
	public void paint(Graphics graphics)
	{
		super.paint(graphics);
		panel.revalidate();
	}

	/**
	 *  The main program for the MoleculeListViewer class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args)
	{
		MoleculeListViewer mlv = new MoleculeListViewer();
		MoleculeListViewer.standAlone = true;
		mlv.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

