/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 *
 * Copyright (C) 2003-2007  The JChemPaint project
 *
 * Contact: jchempaint-devel@lists.sourceforge.net
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
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;

import javax.swing.JFileChooser;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.jchempaint.io.JCPCompChemInputSaveFileFilter;
import org.openscience.cdk.applications.jchempaint.io.JCPFileView;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.listener.SwingGUIListener;
import org.openscience.cdk.io.program.GaussianInputWriter;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * Export current model to computational chemistry programs
 * 
 * @cdk.module jchempaint
 * @author Egon Willighagen
 */
public class ExportCompChemAction extends SaveAction {

	private static final long serialVersionUID = -407195104869621963L;

	/**
    * Opens a dialog frame and manages the saving of a file.
    */
    public void actionPerformed(ActionEvent e) {
        
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(jcpPanel.getCurrentWorkDirectory());
        JCPCompChemInputSaveFileFilter.addChoosableFileFilters(chooser);
        chooser.setFileView(new JCPFileView());
        int returnVal = chooser.showSaveDialog(jcpPanel);
        String type = null;
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            type = ((JCPCompChemInputSaveFileFilter)chooser.getFileFilter()).getType();
            File outFile = chooser.getSelectedFile();

            if (type.equals(JCPCompChemInputSaveFileFilter.gin)) {
                logger.info("Saving the contents as Gaussian input...");
                try {
                    cow = new GaussianInputWriter(new FileWriter(outFile));
                    if (cow != null) {
                        cow.addChemObjectIOListener(new SwingGUIListener(jcpPanel, 4));
                    }
                    Iterator containers = ChemModelManipulator.getAllAtomContainers(jcpPanel.getJChemPaintModel().getChemModel()).iterator();
            		while (containers.hasNext()) {
            			IAtomContainer ac = (IAtomContainer)containers.next();
            			if (ac != null) {
            				cow.write(new Molecule(ac));
            			} else {
            				logger.error("AtomContainer is empty!!!");
            				System.err.println("AC == null!");
            			}
            		}
                } catch(Exception exception) {
                    logger.error("Exception while trying to save Gaussian input");
                    logger.debug(exception);
                }
            } // there is no else
        }

        jcpPanel.setCurrentWorkDirectory(chooser.getCurrentDirectory());        
    }
}
    
