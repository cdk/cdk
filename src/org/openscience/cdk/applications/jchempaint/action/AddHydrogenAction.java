/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2005  The JChemPaint project
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

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.SetOfReactions;
import org.openscience.cdk.controller.Controller2DModel;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.layout.HydrogenPlacer;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.manipulator.SetOfReactionsManipulator;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;


/**
 * An action triggering the addition of hydrogens to 
 * selected structures
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 * @created    22. April 2005
 */
public class AddHydrogenAction extends JCPAction
{

	private HydrogenAdder hydrogenAdder = null;


	/**
	 *  Description of the Method
	 *
	 *@param  event  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent event)
	{
		logger.debug("Trying to add hydrogen in mode: ", type);
		if (hydrogenAdder == null)
		{
			hydrogenAdder = new HydrogenAdder("org.openscience.cdk.tools.ValencyChecker");
		}

		if (jcpPanel.getJChemPaintModel() != null)
		{
			// now add hydrogens
			JChemPaintModel jcpmodel = jcpPanel.getJChemPaintModel();
			ChemModel model = jcpmodel.getChemModel();

			ChemObject object = getSource(event);
			if (object != null)
			{
				if (object instanceof Atom)
				{
					logger.debug("Adding hydrogens to this specific atom");
					Atom atom = (Atom) object;
					addHydrogenToOneAtom(ChemModelManipulator.getRelevantAtomContainer(model, atom), atom);
				} else if (object instanceof ChemModel) {
					logger.debug("Adding hydrogens to all atoms");
					addHydrogenToAllAtoms(model);
				} else {
					logger.error("Can only add hydrogens to Atom's");
				}
			} else
			{
				logger.debug("Adding hydrogens to all atoms");
				addHydrogenToAllAtoms(model);
			}
			jcpmodel.fireChange();
		}
	}


	/**
	 *  Adds a feature to the HydrogenToAllAtoms attribute of the AddHydrogenAction
	 *  object
	 *
	 *@param  model  The feature to be added to the HydrogenToAllAtoms attribute
	 */
	private void addHydrogenToAllAtoms(ChemModel model)
	{
		SetOfMolecules som = model.getSetOfMolecules();
		SetOfReactions sor = model.getSetOfReactions();
		if (som != null)
		{
			addHydrogenToAllMolecules(som);
		} else if (sor != null)
		{
			logger.debug("#reactions ", sor.getReactionCount());
			som = SetOfReactionsManipulator.getAllMolecules(sor);
			logger.debug("Found molecules: ", som.getMoleculeCount());
			addHydrogenToAllMolecules(som);
		}
	}


	/**
	 *  Adds a feature to the HydrogenToAllMolecules attribute of the
	 *  AddHydrogenAction object
	 *
	 *@param  som  The feature to be added to the HydrogenToAllMolecules attribute
	 */
	private void addHydrogenToAllMolecules(SetOfMolecules som)
	{
		JChemPaintModel jcpmodel = jcpPanel.getJChemPaintModel();
		Controller2DModel controllerModel = jcpmodel.getControllerModel();
		try
		{
			Molecule[] mols = som.getMolecules();
			for (int i = 0; i < mols.length; i++)
			{
				Molecule molecule = mols[i];
				if (molecule != null)
				{
					if (type.equals("implicit"))
					{
						hydrogenAdder.addImplicitHydrogensToSatisfyValency(molecule);
					} else if (type.equals("explicit"))
					{
						double bondLength = GeometryTools.getBondLengthAverage(molecule);
						if (Double.isNaN(bondLength))
						{
							logger.warn("Could not determine average bond length from structure!");
							bondLength = controllerModel.getBondPointerLength();
						}
						hydrogenAdder.addExplicitHydrogensToSatisfyValency(molecule);
						HydrogenPlacer hPlacer = new HydrogenPlacer();
						hPlacer.placeHydrogens2D(molecule, bondLength);
					} else if (type.equals("allimplicit"))
					{
						// remove explicit hydrogen if necessary
						Atom[] atoms = molecule.getAtoms();
						for (int j = 0; j < atoms.length; j++)
						{
							logger.debug("Checking atom: ", j);
							if (atoms[j].getSymbol().equals("H"))
							{
								logger.debug("Atom is a hydrogen");
								molecule.removeAtomAndConnectedElectronContainers(atoms[j]);
							}
						}
						// add implicit hydrogen
						hydrogenAdder.addImplicitHydrogensToSatisfyValency(molecule);
					}
				} else
				{
					logger.error("Molecule is null! Cannot add hydrogens!");
				}
			}
		} catch (Exception exc)
		{
			logger.error("Error while adding hydrogen: ", exc.getMessage());
			logger.debug(exc);
		}
	}


	/**
	 *  Adds a feature to the HydrogenToOneAtom attribute of the AddHydrogenAction
	 *  object
	 *
	 *@param  container  The feature to be added to the HydrogenToOneAtom attribute
	 *@param  atom       The feature to be added to the HydrogenToOneAtom attribute
	 */
	private void addHydrogenToOneAtom(AtomContainer container, Atom atom)
	{
		JChemPaintModel jcpmodel = jcpPanel.getJChemPaintModel();
		Controller2DModel controllerModel = jcpmodel.getControllerModel();
		try
		{
			if (type.equals("implicit"))
			{
				hydrogenAdder.addImplicitHydrogensToSatisfyValency(container, atom);
			} else if (type.equals("explicit"))
			{
				double bondLength = GeometryTools.getBondLengthAverage(container);
				if (Double.isNaN(bondLength))
				{
					logger.warn("Could not determine average bond length from structure!");
					bondLength = controllerModel.getBondPointerLength();
				}
				hydrogenAdder.addExplicitHydrogensToSatisfyValency(container, atom, container);
				HydrogenPlacer hPlacer = new HydrogenPlacer();
				hPlacer.placeHydrogens2D(container, atom, bondLength);
			}
		} catch (Exception exc)
		{
			logger.error("Error while adding hydrogen: ", exc.getMessage());
			logger.debug(exc);
		}
	}
}

