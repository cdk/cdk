/* $Revision: 7634 $ $Author: egonw $ $Date: 2007-01-04 18:26:00 +0100 (Thu, 04 Jan 2007) $
 *
 * Copyright (C) 2006-2007  Sam Adams
 *
 * Contact: jchempaint-devel@lists.sf.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.applications.jchempaint.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

import net.sf.jniinchi.INCHI_RET;

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.applications.jchempaint.dialogs.TextViewDialog;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * Creates an InChI from the current model
 *
 * @cdk.module jchempaint
 * @author     Sam Adams
 */
public class CreateInChIAction extends JCPAction
{

	private static final long serialVersionUID = -4886982931009753347L;
	
	TextViewDialog dialog = null;
	JFrame frame = null;

	public void actionPerformed(ActionEvent e)
	{
		logger.debug("Trying to create InChI: ", type);
		
		if (dialog == null)
		{
			dialog = new TextViewDialog(frame, "InChI", null, false, 40, 2);
		}
        
        InChIGeneratorFactory factory = null;
        try {
            factory = new InChIGeneratorFactory();
        } catch (CDKException cdke) {
            String message = "Error loading InChI library: " + cdke.getMessage();
            logger.error(message);
            logger.debug(cdke);
            dialog.setMessage("Error", message);
        }
        
        if (factory != null) {
            ChemModel model = (ChemModel) jcpPanel.getJChemPaintModel().getChemModel();
            List containersList = ChemModelManipulator.getAllAtomContainers(model);
            StringBuffer dialogText = new StringBuffer();
            String eol = System.getProperty("line.separator");
            List molecules = new ArrayList();
            Iterator iterator = containersList.iterator();
			while(iterator.hasNext())
			{
				IAtomContainer ac = (IAtomContainer)iterator.next();
				if (ac.getAtomCount() > 0) {
                    molecules.add(ac);
                }
            }
            for (int i = 0; i < molecules.size(); i ++) {
                if (molecules.size() > 1) {
                    dialogText.append("Structure #" + (i+1) + eol);
                }
                IAtomContainer container = (IAtomContainer) molecules.get(i);
                
                try {
                    HydrogenAdder hAdd = new HydrogenAdder();
                    hAdd.addImplicitHydrogensToSatisfyValency(container);
                    
                    InChIGenerator inchiGen = factory.getInChIGenerator(container);
                    INCHI_RET ret = inchiGen.getReturnStatus();
                    String inchi = inchiGen.getInchi();
                    String auxinfo = inchiGen.getAuxInfo();
                    String message = inchiGen.getMessage();
                    if (ret == INCHI_RET.OKAY) {
                        dialogText.append(inchi + eol + auxinfo + eol + eol);
                    } else if (ret == INCHI_RET.WARNING) {
                        dialogText.append(inchi + eol + auxinfo + eol + "Warning: " + message + eol + eol);
                    } else {
                        dialogText.append("InChI generation failed (" + ret.toString() + ")" + eol + message + eol + eol);
                    }
                } catch (CDKException cdke) {
                    dialogText.append("InChI generation failed: " + cdke.getMessage() + eol + eol);
                }
            }
            
            dialog.setMessage("Generated InChI:", dialogText.toString());
        }
        
        
		dialog.setVisible(true);
	}
}

