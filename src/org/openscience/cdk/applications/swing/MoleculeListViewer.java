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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.applications.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @cdk.module applications
 *
 * @author     steinbeck
 * @cdk.created    2002-10-29
 * @cdk.require swing
 */
public class MoleculeListViewer extends JFrame
{
	protected StrucContainer panel;
	protected JScrollPane scrollPane;
	protected int noOfStructures = 0;

	/**
	 *  The dimension of a single MoleculeViewerPanel
	 */
	protected Dimension molViewDim = new Dimension(250, 250);

	protected Vector moleculeViewerPanels = new Vector();

	public static boolean standAlone = false;



	/**
	 *  Constructor for the MoleculeListViewer object
	 */
	public MoleculeListViewer()
	{
		super();
        this.molViewDim = molViewDim;
		getContentPane().setLayout(new BorderLayout());
		setTitle("Structure Display");
		panel = new StrucContainer();
		scrollPane = new JScrollPane(panel);
		scrollPane.setPreferredSize(new Dimension(800, 600));
		getContentPane().add("Center", scrollPane);
		setTitle("MoleculeListViewer");
		pack();
		setVisible(true);
	}

	/**
	 *  Sets the molViewDim attribute of the MoleculeListViewer object
	 *
	 *@param  molViewDim  The new molViewDim value
	 */
	public void setMolViewDim(Dimension molViewDim)
	{
		this.molViewDim = molViewDim;
	}


	/**
	 *  Gets the molViewDim attribute of the MoleculeListViewer object
	 *
	 *@return    The molViewDim value
	 */
	public Dimension getMolViewDim()
	{
		return molViewDim;
	}


	/**
	 *  Adds a feature to the Structure attribute of the MoleculeListViewer object
	 *
	 *@param  moleculeViewer  The feature to be added to the Structure attribute
	 */
	public void addStructure(MoleculeViewer2D moleculeViewer)
	{
		String title = "Structure no. " + (noOfStructures + 1);
		addStructure(moleculeViewer, title);
	}


	/**
	 *  Adds a feature to the Structure attribute of the MoleculeListViewer object
	 *
	 *@param  moleculeViewer     The feature to be added to the Structure attribute
	 *@param  title  The feature to be added to the Structure attribute
	 */
	public void addStructure(MoleculeViewer2D moleculeViewer, String title)
	{
		noOfStructures++;
		moleculeViewer.setPreferredSize(molViewDim);
        moleculeViewer.getRenderer2DModel().setBackgroundDimension(molViewDim);
		moleculeViewer.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title));
		moleculeViewerPanels.addElement(moleculeViewer);
		panel.add(moleculeViewer);
		panel.revalidate();
	}


	public void paint(Graphics graphics)
	{
		super.paint(graphics);
		panel.revalidate();
	}


	/**
	 * @author     steinbeck
	 * @cdk.created    2002-10-29
	 */
	class StrucContainer extends JPanel
	{
		/**
		 *  Constructor for the StrucContainer object
		 */
		public StrucContainer()
		{
			super();
			setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		}


		/**
		 *  Gets the preferredSize attribute of the StrucContainer object
		 *
		 *@return    The preferredSize value
		 */
		public Dimension getPreferredSize()
		{
			int width;
			int height;
			width = getContentPane().getSize().width;
			if (width < molViewDim.width)
			{
				width = molViewDim.width;
			}
			height = ((noOfStructures / ((int) width / molViewDim.width)) + 1) * molViewDim.height;
			height = (int) (height * 1.2);
			return new Dimension(width, height);
		}


		/**
		 *  Gets the bounds attribute of the StrucContainer object
		 *
		 *@return    The bounds value
		 */
		public Rectangle getBounds()
		{
			return new Rectangle(new java.awt.Point(0, 0), getPreferredSize());
		}

	}


	/**
	 *  The main program for the MoleculeListViewer class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args)
	{
		MoleculeListViewer mlv = new MoleculeListViewer();
		mlv.standAlone = true;
		mlv.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

