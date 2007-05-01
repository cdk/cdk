/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2003-2007  The JChemPaint project
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
package org.openscience.cdk.applications.jchempaint.action;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.undo.UndoableEdit;
import javax.vecmath.Point2d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.undoredo.AddHydrogenEdit;
import org.openscience.cdk.controller.Controller2DModel;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.layout.HydrogenPlacer;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.ReactionSetManipulator;


/**
 * An action triggering the addition of hydrogens to 
 * selected structures
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 */
public class AddHydrogenAction extends JCPAction
{

	private static final long serialVersionUID = 7696756423842199080L;
	private HydrogenAdder hydrogenAdder = null;
    private IAtomContainer changedAtomsAndBonds = null;
    private HashMap hydrogenAtomMap = null;

	public void actionPerformed(ActionEvent event)
	{
        this.hydrogenAtomMap = null;
        this.changedAtomsAndBonds = null;
		logger.debug("Trying to add hydrogen in mode: ", type);
		if (hydrogenAdder == null)
		{
			hydrogenAdder = new HydrogenAdder("org.openscience.cdk.tools.ValencyChecker");
		}

		if (jcpPanel.getJChemPaintModel() != null)
		{
			// now add hydrogens
			JChemPaintModel jcpmodel = jcpPanel.getJChemPaintModel();
			org.openscience.cdk.interfaces.IChemModel model = jcpmodel.getChemModel();
            
			IChemObject object = getSource(event);
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
            UndoableEdit edit = null;
            if (type.equals("explicit")) {
                edit = new  AddHydrogenEdit(model, changedAtomsAndBonds);
            }
            else if ( type.equals("implicit")) {
                edit = new  AddHydrogenEdit(model, hydrogenAtomMap);
            }
            else if (type.equals("allimplicit")) {
               edit = new  AddHydrogenEdit(model, hydrogenAtomMap);
            }
            jcpPanel.getUndoSupport().postEdit(edit);
			jcpmodel.fireChange();
			Controller2DModel controllerModel = jcpPanel.getJChemPaintModel().getControllerModel();
			if (type.equals("implicit"))
			{
				if(!controllerModel.getAutoUpdateImplicitHydrogens()){
					controllerModel.setAutoUpdateImplicitHydrogens(true);
				}else{
	                controllerModel.setAutoUpdateImplicitHydrogens(false);
				}
			}
		}
	}


	/**
	 *  Adds a feature to the HydrogenToAllAtoms attribute of the AddHydrogenAction
	 *  object
	 *
	 *@param  model  The feature to be added to the HydrogenToAllAtoms attribute
	 */
	private void addHydrogenToAllAtoms(org.openscience.cdk.interfaces.IChemModel model)
	{
		IMoleculeSet som = model.getMoleculeSet();
		org.openscience.cdk.interfaces.IReactionSet sor = model.getReactionSet();
		if (som != null)
		{
			addHydrogenToAllMolecules(som);
		} else if (sor != null)
		{
			logger.debug("#reactions ", sor.getReactionCount());
			som = ReactionSetManipulator.getAllMolecules(sor);
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
	private void addHydrogenToAllMolecules(IMoleculeSet som)
	{
		JChemPaintModel jcpmodel = jcpPanel.getJChemPaintModel();
		Controller2DModel controllerModel = jcpmodel.getControllerModel();
        try
		{
        	java.util.Iterator mols = som.molecules();
			while (mols.hasNext())
			{
				IMolecule molecule = (IMolecule)mols.next();
				if (molecule != null)
				{
					if (type.equals("implicit"))
					{
						if(!controllerModel.getAutoUpdateImplicitHydrogens()){
							hydrogenAtomMap = hydrogenAdder.addImplicitHydrogensToSatisfyValency(molecule);
						}else{
							for (int j = 0; j < molecule.getAtomCount(); j++)
							{
								logger.debug("Checking atom: ", j);
								molecule.getAtom(j).setHydrogenCount(0);
							}
	            		}
					} else if (type.equals("explicit"))
					{
						double bondLength = GeometryTools.getBondLengthAverage(molecule, jcpmodel.getRendererModel().getRenderingCoordinates());
						if (Double.isNaN(bondLength))
						{
							logger.warn("Could not determine average bond length from structure!");
							bondLength = controllerModel.getBondPointerLength();
						}
                        changedAtomsAndBonds = hydrogenAdder.addExplicitHydrogensToSatisfyValency(molecule);
                        HydrogenPlacer hPlacer = new HydrogenPlacer();
						hPlacer.placeHydrogens2D(molecule, bondLength, jcpmodel.getRendererModel().getRenderingCoordinates());

						Iterator containers =  ChemModelManipulator.getAllAtomContainers(jcpmodel.getChemModel()).iterator();
						while (containers.hasNext()) {
							IAtomContainer atomCon = (IAtomContainer)containers.next(); 
							for (int k = 0; k < atomCon.getAtomCount(); k++)
							{
								IAtom currentAtom = atomCon.getAtom(k);
								if(jcpmodel.getRendererModel().getRenderingCoordinate(currentAtom)!=null){
									currentAtom.setPoint2d(new Point2d((Point2d)jcpmodel.getRendererModel().getRenderingCoordinate(currentAtom)));
								}
							}
						}
						
						//((PopupController2D)jcpPanel.getDrawingPanel().getMouseListeners()[0]).updateMoleculeCoordinates();
					} else if (type.equals("allimplicit"))
					{
							// remove explicit hydrogen if necessary
							//org.openscience.cdk.interfaces.IAtom[] atoms = molecule.getAtoms();
							for (int j = 0; j < molecule.getAtomCount(); j++)
							{
								org.openscience.cdk.interfaces.IAtom atom = molecule.getAtom(j);
								logger.debug("Checking atom: ", j);
								if (atom.getSymbol().equals("H"))
								{
									logger.debug("Atom is a hydrogen");
									molecule.removeAtomAndConnectedElectronContainers(atom);
									if(j-1!=molecule.getAtomCount())
										j--;
								}
							}
							// add implicit hydrogen
	                        hydrogenAtomMap = hydrogenAdder.addImplicitHydrogensToSatisfyValency(molecule);
					}
				} else
				{
					logger.error("Molecule is null! Cannot add hydrogens!");
				}
			}
		} catch (Exception exc)
		{
			exc.printStackTrace();
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
	private void addHydrogenToOneAtom(IAtomContainer container, Atom atom)
	{
		JChemPaintModel jcpmodel = jcpPanel.getJChemPaintModel();
		Controller2DModel controllerModel = jcpmodel.getControllerModel();
		try
		{
			if (type.equals("implicit"))
			{
               int[] hydrogens = hydrogenAdder.addImplicitHydrogensToSatisfyValency(container, atom);
               hydrogenAtomMap.put(atom, hydrogens);
//                changedAtomsAndBonds = hydrogenAdder.addImplicitHydrogensToSatisfyValency(container, atom);
			} else if (type.equals("explicit"))
			{
				double bondLength = GeometryTools.getBondLengthAverage(container, jcpmodel.getRendererModel().getRenderingCoordinates());
				if (Double.isNaN(bondLength))
				{
					logger.warn("Could not determine average bond length from structure!");
					bondLength = controllerModel.getBondPointerLength();
				}
//                hydrogenAdder.addExplicitHydrogensToSatisfyValency(container, atom, container);
                changedAtomsAndBonds = hydrogenAdder.addExplicitHydrogensToSatisfyValency(container, atom, container);
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

