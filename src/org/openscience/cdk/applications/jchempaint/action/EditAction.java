/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
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

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.jchempaint.action.CopyPasteAction.JcpSelection;
import org.openscience.cdk.applications.undoredo.RemoveAtomsAndBondsEdit;
import org.openscience.cdk.controller.Controller2DModel;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.MoleculeSetManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

/**
 * This class implements editing options from the 'Edit' menu.
 * These actions are implemented:
 * <ul>
 *   <li>cut, deletes all atoms and connected electron containers
 *   <li>cutSelected, deletes all selected atoms and electron containers
 *   <li>selectAll, selects all atoms and electron containers
 *   <li>selectFromChemObject,selects all atoms and electron containers in
 *       the IChemObject set in the event source
 * </ul>
 *
 * @author        hel
 * @cdk.created       27. April 2005
 * @cdk.module    jchempaint
 */
public class EditAction extends JCPAction {

	private static final long serialVersionUID = -1051272879400028225L;

	public void actionPerformed(ActionEvent event) {
		// learn some stuff about event
		logger.debug("Event source: ", event.getSource().getClass().getName());
		logger.debug("  IChemObject: ", getSource(event));

		JChemPaintModel jcpModel = jcpPanel.getJChemPaintModel();
		Renderer2DModel renderModel = jcpModel.getRendererModel();
		org.openscience.cdk.interfaces.IChemModel chemModel = jcpModel.getChemModel();
		if (type.equals("cut")) {
			org.openscience.cdk.interfaces.IAtom atomInRange = null;
			IChemObject object = getSource(event);
			logger.debug("Source of call: ", object);
			if (object instanceof Atom) {
				atomInRange = (Atom) object;
			}
			else {
				atomInRange = renderModel.getHighlightedAtom();
			}
			if (atomInRange != null) {
				try{
		            Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
		            IAtomContainer tocopyclone=atomInRange.getBuilder().newAtomContainer();
		            tocopyclone.addAtom((IAtom)atomInRange.clone());
		            tocopyclone.getAtom(0).setPoint2d(renderModel.getRenderingCoordinate(atomInRange));
		            JcpSelection jcpselection=new CopyPasteAction().new JcpSelection(tocopyclone);
		            sysClip.setContents(jcpselection,null);
				}catch(Exception ex){
					//shouldn't happen
					ex.printStackTrace();
				}
				ChemModelManipulator.removeAtomAndConnectedElectronContainers(chemModel, atomInRange);
			}
			else {
				org.openscience.cdk.interfaces.IBond bond = renderModel.getHighlightedBond();
				if (bond != null) {
					ChemModelManipulator.removeElectronContainer(chemModel, bond);
				}
			}
			jcpModel.fireChange();
		}
		else if (type.equals("cutSelected")) {
			IAtomContainer undoRedoContainer = chemModel.getBuilder().newAtomContainer();
			logger.debug("Deleting all selected atoms...");
			if (renderModel.getSelectedPart() == null || renderModel.getSelectedPart().getAtomCount() == 0) {
				JOptionPane.showMessageDialog(jcpPanel, "No selection made. Please select some atoms first!", "Error warning", JOptionPane.WARNING_MESSAGE);
			}
			else {
				IAtomContainer selected = renderModel.getSelectedPart();
				try{
		            Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
		            IAtomContainer tocopyclone=(IAtomContainer)selected.clone();
		            for(int i=0;i<tocopyclone.getAtomCount();i++){
		            	tocopyclone.getAtom(i).setPoint2d(renderModel.getRenderingCoordinate(selected.getAtom(i)));
		            }
		            JcpSelection jcpselection=new CopyPasteAction().new JcpSelection(tocopyclone);
		            sysClip.setContents(jcpselection,null);
				}catch(Exception ex){
					//shouldn't happen
					ex.printStackTrace();
				}
				logger.debug("Found # atoms to delete: ", selected.getAtomCount());
				for (int i = 0; i < selected.getAtomCount(); i++) {
					undoRedoContainer.add(selected);
					for(int k=0;k<ChemModelManipulator.getRelevantAtomContainer(chemModel,selected.getAtom(i)).getConnectedBondsCount(selected.getAtom(i));k++){
						undoRedoContainer.addBond((IBond)ChemModelManipulator.getRelevantAtomContainer(chemModel,selected.getAtom(i)).getConnectedBondsList(selected.getAtom(i)).get(k));
					}
					ChemModelManipulator.removeAtomAndConnectedElectronContainers(chemModel, selected.getAtom(i));
				}
			}
			renderModel.setSelectedPart(new org.openscience.cdk.AtomContainer());
			RemoveAtomsAndBondsEdit  edit = new RemoveAtomsAndBondsEdit(chemModel,undoRedoContainer,"Cut selected");
            jcpPanel.getUndoSupport().postEdit(edit);
			jcpModel.fireChange();
		}
		else if (type.equals("selectAll")) {
			IAtomContainer wholeModel = jcpModel.getChemModel().getBuilder().newAtomContainer();
        	Iterator containers = ChemModelManipulator.getAllAtomContainers(jcpModel.getChemModel()).iterator();
        	while (containers.hasNext()) {
        		wholeModel.add((IAtomContainer)containers.next());
        	}
			renderModel.setSelectedPart(wholeModel);
			((JButton)jcpPanel.lastAction.get(0)).setBackground(Color.LIGHT_GRAY);
			jcpPanel.lastAction.set(0,jcpPanel.getMoveButton());
			jcpPanel.getMoveButton().setBackground(Color.GRAY);
			jcpModel.getControllerModel().setDrawMode(Controller2DModel.DrawMode.MOVE);
			jcpModel.fireChange();
		} else if (type.equals("selectMolecule")) {
			IChemObject object = getSource(event);
			if (object instanceof Atom) {
				renderModel.setSelectedPart(ChemModelManipulator.getRelevantAtomContainer(jcpModel.getChemModel(),(Atom)object));
			} else if (object instanceof org.openscience.cdk.interfaces.IBond) {
				renderModel.setSelectedPart(ChemModelManipulator.getRelevantAtomContainer(jcpModel.getChemModel(),(Bond)object));
			} else {
				logger.warn("selectMolecule not defined for the calling object ", object);
			}
			jcpModel.fireChange();
		} else if (type.equals("selectFromChemObject")) {
			// FIXME: implement for others than Reaction, Atom, Bond
			IChemObject object = getSource(event);
			if (object instanceof Atom) {
				IAtomContainer container = new org.openscience.cdk.AtomContainer();
				container.addAtom((Atom) object);
				renderModel.setSelectedPart(container);
				jcpModel.fireChange();
			}
			else if (object instanceof org.openscience.cdk.interfaces.IBond) {
				IAtomContainer container = new org.openscience.cdk.AtomContainer();
				container.addBond((Bond) object);
				renderModel.setSelectedPart(container);
				jcpModel.fireChange();
			}
			else if (object instanceof Reaction) {
				IAtomContainer wholeModel = jcpModel.getChemModel().getBuilder().newAtomContainer();
	        	Iterator containers = ReactionManipulator.getAllAtomContainers((Reaction)object).iterator();
	        	while (containers.hasNext()) {
	        		wholeModel.add((IAtomContainer)containers.next());
	        	}
				renderModel.setSelectedPart(wholeModel);
				jcpModel.fireChange();
			}
			else {
				logger.warn("Cannot select everything in : ", object);
			}
		}
		else if (type.equals("selectReactionReactants")) {
			IChemObject object = getSource(event);
			if (object instanceof Reaction) {
				Reaction reaction = (Reaction) object;
				IAtomContainer wholeModel = jcpModel.getChemModel().getBuilder().newAtomContainer();
	        	Iterator containers = MoleculeSetManipulator.getAllAtomContainers(reaction.getReactants()).iterator();
	        	while (containers.hasNext()) {
	        		wholeModel.add((IAtomContainer)containers.next());
	        	}
				renderModel.setSelectedPart(wholeModel);
				jcpModel.fireChange();
			}
			else {
				logger.warn("Cannot select reactants from : ", object);
			}
		}
		else if (type.equals("selectReactionProducts")) {
			IChemObject object = getSource(event);
			if (object instanceof Reaction) {
				Reaction reaction = (Reaction) object;
				IAtomContainer wholeModel = jcpModel.getChemModel().getBuilder().newAtomContainer();
	        	Iterator containers = MoleculeSetManipulator.getAllAtomContainers(reaction.getProducts()).iterator();
	        	while (containers.hasNext()) {
	        		wholeModel.add((IAtomContainer)containers.next());
	        	}
				renderModel.setSelectedPart(wholeModel);
				jcpModel.fireChange();
			}
			else {
				logger.warn("Cannot select reactants from : ", object);
			}
		}
		else {
			logger.warn("Unsupported EditAction: " + type);
		}
	}

}

