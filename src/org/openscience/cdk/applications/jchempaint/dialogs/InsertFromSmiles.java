/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2006  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sourceforge.net
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
 */
package org.openscience.cdk.applications.jchempaint.dialogs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.vecmath.Vector2d;

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.jchempaint.JChemPaintPanel;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.layout.TemplateHandler;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * Frame to allow for changing the propterties.
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 */
public class InsertFromSmiles extends JFrame
{

	private static final long serialVersionUID = -8994882010068386135L;
	
	JChemPaintPanel jcpPanel;
	JTextField valueText;


	/**
	 *  Constructor for the InsertFromSmiles object
	 *
	 *@param  jcpPanel  Description of the Parameter
	 */
	public InsertFromSmiles(JChemPaintPanel jcpPanel)
	{
		super("Insert from SMILES");
		this.jcpPanel = jcpPanel;
		getContentPane().setLayout(new BorderLayout());
		JPanel southPanel = new JPanel();
		JButton cancelButton = new JButton("Cancel");
		JButton openButton = new JButton("Insert");
		openButton.addActionListener(new OpenAction());
		cancelButton.addActionListener(new CancelAction());
		southPanel.add(openButton);
		southPanel.add(cancelButton);

		JPanel centerPanel = new JPanel();
		JLabel valueLabel = new JLabel("Enter SMILES string:");
		valueText = new JTextField(20);
		valueText.addActionListener(new OpenAction());
		centerPanel.add(valueLabel);
		centerPanel.add(valueText);
		//setSize(300,100);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add("Center", centerPanel);
		getContentPane().add("South", southPanel);
		pack();
	}


	/**
	 *  Description of the Method
	 */
	public void closeFrame()
	{
		dispose();
	}


	/**
	 *  Description of the Class
	 *
	 *@author     steinbeck
	 */
	class OpenAction extends AbstractAction
	{
		
		private static final long serialVersionUID = -2209475955846561193L;


		/**
		 *  Constructor for the OpenAction object
		 */
		OpenAction()
		{
			super("Open");
		}


		/**
		 *  Description of the Method
		 *
		 *@param  e  Description of the Parameter
		 */
		public void actionPerformed(ActionEvent e)
		{
			generateModel();
		}


		/**
		 *  Description of the Method
		 */
		private void generateModel()
		{
			try
			{
				String SMILES = valueText.getText();
				SmilesParser sp = new SmilesParser();
				IMolecule m = sp.parseSmiles(SMILES);

				// ok, get relevent bits from active model
                JChemPaintModel jcpModel = jcpPanel.getJChemPaintModel();
                Renderer2DModel renderModel = jcpModel.getRendererModel();
                org.openscience.cdk.interfaces.IChemModel chemModel = jcpModel.getChemModel();
                org.openscience.cdk.interfaces.IMoleculeSet moleculeSet = chemModel.getMoleculeSet();
                if (moleculeSet == null) {
                    moleculeSet = new MoleculeSet();
                }

				// ok, now generate 2D coordinates
				StructureDiagramGenerator sdg = new StructureDiagramGenerator();
                sdg.setTemplateHandler(new TemplateHandler(moleculeSet.getBuilder()));
				try
				{
					sdg.setMolecule(m);
					sdg.generateCoordinates(new Vector2d(0,1));
                    m = sdg.getMolecule();
                     double bondLength = renderModel.getBondLength();
                    double scaleFactor = GeometryTools.getScaleFactor(m, bondLength);
                    GeometryTools.scaleMolecule(m, scaleFactor,renderModel.getRenderingCoordinates());
                    //if there are no atoms in the actual chemModel all 2D-coordinates would be set to NaN
                    if (ChemModelManipulator.getAllInOneContainer(chemModel).getAtomCount() != 0) {
	                    GeometryTools.translate2DCenterTo(m,
	                        GeometryTools.get2DCenter(
	                            ChemModelManipulator.getAllInOneContainer(chemModel)
	                        )
	                    );
                    }
                    GeometryTools.translate2D(m, 5*bondLength, 0,renderModel.getRenderingCoordinates()); // in pixels
				} catch (Exception exc) {
					exc.printStackTrace();
				}

                // now add the structure to the active model
                moleculeSet.addMolecule(m);
                // and select it
//                renderModel.setSelectedPart(m);
                // if the not again setting the chemModel setOfMolecules the chemModel remains empty
                jcpPanel.getChemModel().setMoleculeSet(moleculeSet);
                // to ensure, that the molecule is  shown in the actual visibile part of jcp
                jcpPanel.scaleAndCenterMolecule((ChemModel)jcpPanel.getChemModel());
//             fire a change so that the view gets updated
                jcpModel.fireChange(jcpPanel.getChemModel());
                closeFrame();
				

			} catch (InvalidSmilesException ise)
			{
				JOptionPane.showMessageDialog(jcpPanel, "Invalid SMILES String.");
			}
		}
	}


	/**
	 *  Description of the Class
	 *
	 *@author     steinbeck
	 */
	class CancelAction extends AbstractAction
	{
		
		private static final long serialVersionUID = -6894716638070084023L;


		/**
		 *  Constructor for the CancelAction object
		 */
		CancelAction()
		{
			super("Cancel");
		}


		/**
		 *  Description of the Method
		 *
		 *@param  e  Description of the Parameter
		 */
		public void actionPerformed(ActionEvent e)
		{
			closeFrame();
		}
	}
}

