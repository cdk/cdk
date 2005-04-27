/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2005  The JChemPaint project
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.applications.jchempaint.action;

import java.awt.event.ActionEvent;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.SetOfReactions;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;


/**
 * Triggers the invocation of the structure diagram generator
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 * @created    22. April 2005
 */
public class CleanupAction extends JCPAction
{

	private StructureDiagramGenerator diagramGenerator;

	/**
	 *  Constructor for the CleanupAction object
	 */
	public CleanupAction()
	{
		super();
	}


	/**
	 *  Relayouts a molecule
	 *
	 *@param  e  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent e)
	{	
		logger.info("Going to performe a clean up...");
		if (jcpPanel.getJChemPaintModel() != null)
		{
			if (diagramGenerator == null) diagramGenerator = new StructureDiagramGenerator();
			JChemPaintModel jcpmodel = jcpPanel.getJChemPaintModel();
			Renderer2DModel renderModel = jcpmodel.getRendererModel();
			double bondLength = renderModel.getBondLength() / renderModel.getScaleFactor();
			diagramGenerator.setBondLength(bondLength * 2.0);
			// FIXME this extra factor should not be necessary
			logger.debug("getting ChemModel");
			ChemModel model = jcpmodel.getChemModel();
			logger.debug("got ChemModel");
			SetOfMolecules som = model.getSetOfMolecules();
			if (som != null)
			{
				logger.debug("no mols in som: ", som.getMoleculeCount());
				SetOfMolecules newsom = new SetOfMolecules();
				Molecule[] mols = som.getMolecules();
				for (int i = 0; i < mols.length; i++)
				{
					newsom.addMolecule(relayoutMolecule(mols[i]));
				}
				model.setSetOfMolecules(newsom);
			}
			SetOfReactions reactionSet = model.getSetOfReactions();
			if (reactionSet != null)
			{
				SetOfReactions newSet = new SetOfReactions();
				// FIXME, this does not preserve reactionset properties!
				Reaction[] reactions = reactionSet.getReactions();
				for (int j = 0; j < reactions.length; j++)
				{
					Reaction reaction = reactions[j];
					Reaction newReaction = new Reaction();
					// FIXME, this does not preserve reaction properties!
					Molecule[] reactants = reaction.getReactants().getMolecules();
					for (int i = 0; i < reactants.length; i++)
					{
						newReaction.addReactant(relayoutMolecule(reactants[i]));
					}
					Molecule[] products = reaction.getProducts().getMolecules();
					for (int i = 0; i < products.length; i++)
					{
						newReaction.addProduct(relayoutMolecule(products[i]));
					}
					newSet.addReaction(newReaction);
				}
				model.setSetOfReactions(newSet);
			}

			jcpmodel.fireChange();
			jcpPanel.repaint();
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  molecule  Description of the Parameter
	 *@return           Description of the Return Value
	 */
	private Molecule relayoutMolecule(Molecule molecule)
	{
		JChemPaintModel jcpmodel = jcpPanel.getJChemPaintModel();
		Molecule cleanedMol = molecule;
		if (molecule != null)
		{
			if (molecule.getAtomCount() > 2)
			{
				try
				{
					Point2d centre = GeometryTools.get2DCentreOfMass(molecule);
					diagramGenerator.setMolecule(molecule);
					diagramGenerator.generateExperimentalCoordinates(new Vector2d(0, 1));

					cleanedMol = diagramGenerator.getMolecule();

					/*
					 *  make the molecule end up somewhere reasonable
					 *  See constructor of JCPPanel
					 */
					GeometryTools.translateAllPositive(cleanedMol);
					double scaleFactor = GeometryTools.getScaleFactor(cleanedMol, jcpmodel.getRendererModel().getBondLength());
					GeometryTools.scaleMolecule(cleanedMol, scaleFactor);
					GeometryTools.translate2DCentreOfMassTo(cleanedMol, centre);

				} catch (Exception exc)
				{
					logger.error("Could not generate coordinates for molecule");
					logger.debug(exc);
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

