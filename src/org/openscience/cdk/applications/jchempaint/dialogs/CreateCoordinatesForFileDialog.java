/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2007  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sf.net
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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.ReactionSet;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.geometry.Projector;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * Dialog for coordinate creationg
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 */
public class CreateCoordinatesForFileDialog extends JInternalFrame
{

	private static final long serialVersionUID = 6717348756533287248L;
	
	private IChemModel chemModel;
	private HashMap renderingCoordinates;
	private JRadioButton generate2DButton;
	private JRadioButton from3DButton;
	private LoggingTool logger = null;


	/**
	 *  Constructor for the CreateCoordinatesForFileDialog object
	 *
	 *@param  model  Description of the Parameter
	 */
	public CreateCoordinatesForFileDialog(IChemModel model, HashMap renderingCoordinates)
	{
		super("Coordinate Creation", true, true, true, true);

		this.chemModel = model;
		this.renderingCoordinates=renderingCoordinates;
		this.logger = new LoggingTool(this);

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		// options
		JPanel radioPanel = new JPanel();
		radioPanel.setLayout(new GridLayout(0, 1));
		ButtonGroup group = new ButtonGroup();
		generate2DButton = new JRadioButton("create with layout algorithm");
		group.add(generate2DButton);
		radioPanel.add(generate2DButton);
		if (GeometryTools.has3DCoordinates(ChemModelManipulator.getAllInOneContainer(chemModel)))
		{
			from3DButton = new JRadioButton("create from 3D coordinates in file");
			group.add(from3DButton);
			radioPanel.add(from3DButton);
			from3DButton.setSelected(true);
		} else
		{
			generate2DButton.setSelected(true);
		}

		JPanel optionPane = new JPanel();
		optionPane.setLayout(new GridLayout(0, 1));
		JLabel label = new JLabel("The file does not contain 2D Coordinates or only some. Should I create those?");
		optionPane.add(label);
		optionPane.add(radioPanel);

		//buttons
		JButton cancelButton = new JButton("Cancel");
		JButton createButton = new JButton("Create");
		cancelButton.addActionListener(new CancelAction());
		createButton.addActionListener(new CreateAction());
		getRootPane().setDefaultButton(createButton);

		//Lay out the buttons from left to right.
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(createButton);
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(cancelButton);

		contentPane.add(optionPane, BorderLayout.CENTER);
		contentPane.add(buttonPane, BorderLayout.SOUTH);
		pack();
		setVisible(true);
	}

	public void closeFrame()
	{
		dispose();
	}

	class CancelAction extends AbstractAction
	{

		private static final long serialVersionUID = -2305492502437164455L;

		CancelAction()
		{
			super("Cancel");
		}

		public void actionPerformed(ActionEvent event)
		{
			closeFrame();
		}
	}


	/**
	 *  Description of the Class
	 *
	 *@author     steinbeck
	 */
	class CreateAction extends AbstractAction
	{

		private static final long serialVersionUID = 7041050310635125218L;
		
		StructureDiagramGenerator diagramGenerator;

		CreateAction()
		{
			super("Create");
			this.diagramGenerator = new StructureDiagramGenerator();
		}

		public void actionPerformed(ActionEvent event)
		{
			if (from3DButton != null && from3DButton.isSelected())
			{
				// JOptionPane.showMessageDialog(jchempaint, "Not implemented yet");
				Projector.project2D(ChemModelManipulator.getAllInOneContainer(chemModel), renderingCoordinates);
			} else
			{
				org.openscience.cdk.interfaces.IMoleculeSet som = chemModel.getMoleculeSet();
				if (som != null)
				{
					logger.debug("no mols in som: ", som.getMoleculeCount());
					MoleculeSet newsom = new MoleculeSet();
					java.util.Iterator mols = som.molecules();
					while (mols.hasNext())
					{
						newsom.addMolecule(relayoutMolecule((IMolecule)mols.next()));
					}
					chemModel.setMoleculeSet(newsom);
				}
				org.openscience.cdk.interfaces.IReactionSet reactionSet = chemModel.getReactionSet();
				if (reactionSet != null)
				{
					ReactionSet newSet = new ReactionSet();
					// FIXME, this does not preserve reactionset properties!
					java.util.Iterator reactionIter = reactionSet.reactions();
					while (reactionIter.hasNext())
					{
						org.openscience.cdk.interfaces.IReaction reaction = (org.openscience.cdk.interfaces.IReaction)reactionIter.next();
						Reaction newReaction = new Reaction();
						// FIXME, this does not preserve reaction properties!
						java.util.Iterator reactants = reaction.getReactants().molecules();
						while (reactants.hasNext())
						{
							newReaction.addReactant(relayoutMolecule((IMolecule)reactants.next()));
						}
						java.util.Iterator products = reaction.getProducts().molecules();
						while (products.hasNext())
						{
							newReaction.addProduct(relayoutMolecule((IMolecule)products.next()));
						}
						newSet.addReaction(newReaction);
					}
					chemModel.setReactionSet(newSet);
				}
			}
			JChemPaintModel jcpm = new JChemPaintModel(chemModel);
			// XXX needs to be fixed
			//JChemPaintFrame jcpf = JChemPaint.getInstance().getNewFrame(jcpm);
			//JChemPaint.getInstance().addAndShowJChemPaintFrame(jcpf);
			closeFrame();
		}

		private IMolecule relayoutMolecule(IMolecule molecule)
		{
			IMolecule cleanedMol = molecule;
			if (molecule != null)
			{
				if (molecule.getAtomCount() > 2)
				{
					try
					{
						Point2d centre = GeometryTools.get2DCentreOfMass(molecule, renderingCoordinates);
						diagramGenerator.setMolecule(molecule);
						diagramGenerator.generateCoordinates(new Vector2d(0, 1));

						cleanedMol = diagramGenerator.getMolecule();

						/*
						 *  make the molecule end up somewhere reasonable
						 *  See constructor of JCPPanel
						 */
						GeometryTools.translateAllPositive(cleanedMol,renderingCoordinates);
						GeometryTools.translate2DCentreOfMassTo(cleanedMol, centre,renderingCoordinates);

					} catch (Exception exc)
					{
						logger.error("Could not generate coordinates for molecule");
						logger.error(exc.toString());
					}
				} else
				{
					logger.info("Molecule with less than 2 atoms are not cleaned up");
				}
			} else
			{
				logger.error("Molecule is null! Cannot do layout!");
			}
			return cleanedMol;
		}

	}

}

