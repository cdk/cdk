/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2007  The JChemPaint project
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
package org.openscience.cdk.applications.jchempaint.action;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.JFrame;

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.jchempaint.dialogs.TextViewDialog;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.layout.HydrogenPlacer;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;


/**
 * Creates a SMILES from the current model
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 */
public class CreateSmilesAction extends JCPAction
{

	private static final long serialVersionUID = -4886982931009753342L;
	
	TextViewDialog dialog = null;
	JFrame frame = null;

	public void actionPerformed(ActionEvent e)
	{
		logger.debug("Trying to create smile: ", type);
		/*
		 *  if (jcpPanel.getFrame() != null)
		 *  {
		 *  try
		 *  {
		 *  Frame frame = (Frame)jcpPanel.getFrame();
		 *  }
		 *  catch(Exception exc)
		 *  {
		 *  logger.debug("Could not cast JCP frame to Frame");
		 *  }
		 *  }
		 */
		if (dialog == null)
		{
			dialog = new TextViewDialog(frame, "SMILES", null, false, 40, 2);
		}
		String smiles = "";
		String chiralsmiles ="";
		try
		{
			ChemModel model = (ChemModel) jcpPanel.getJChemPaintModel().getChemModel();
            SmilesGenerator generator = new SmilesGenerator();
            Iterator containers = ChemModelManipulator.getAllAtomContainers(model).iterator();
            while (containers.hasNext()) {
            	IAtomContainer container = (IAtomContainer)containers.next();
            	Molecule molecule = new Molecule(container);
            	Molecule moleculewithh=(Molecule)molecule.clone();
            	new HydrogenAdder().addExplicitHydrogensToSatisfyValency(moleculewithh);
            	double bondLength = GeometryTools.getBondLengthAverage(container,jcpPanel.getJChemPaintModel().getRendererModel().getRenderingCoordinates());
            	new HydrogenPlacer().placeHydrogens2D(moleculewithh, bondLength);
            	smiles += generator.createSMILES(molecule);
            	boolean[] bool=new boolean[moleculewithh.getBondCount()];
            	SmilesGenerator sg = new SmilesGenerator();
            	for(int i=0;i<bool.length;i++){
            		if (sg.isValidDoubleBondConfiguration(moleculewithh, moleculewithh.getBond(i)))
            			bool[i]=true;
            	}
            	chiralsmiles += generator.createChiralSMILES(moleculewithh,bool);
            	if (containers.hasNext()) {
            		chiralsmiles += ".";
            		smiles += ".";
            	}
            }
			dialog.setMessage("Generated SMILES:", "SMILES: "+smiles+System.getProperty("line.separator")+"chiral SMILES: "+chiralsmiles);
		} catch (Exception exception)
		{
			String message = "Error while creating SMILES: " + exception.getMessage();
			logger.error(message);
			logger.debug(exception);
			dialog.setMessage("Error", message);
		}
		dialog.show();
	}
}

