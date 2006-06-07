/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2006  The JChemPaint project
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
import java.util.Locale;

import javax.swing.JOptionPane;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.iupac.generator.IUPACName;
import org.openscience.cdk.iupac.generator.IUPACNameGenerator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;


/**
 * Creates the IUPAC name for the active compound. It uses the
 * experimental IUPAC name generator from CDK.
 *
 * @cdk.module jchempaint
 * @author  Egon Willighagen
 * @cdk.created 2003-08-11
 */
public class CreateIUPACNameAction extends JCPAction {

	private static final long serialVersionUID = -6369815894943464691L;

	public void actionPerformed(ActionEvent e) {
        logger.debug("Trying to create IUPAC name: ", type);
        Locale locale = new Locale("en", "US");
        IUPACNameGenerator generator = new IUPACNameGenerator(locale);
        ChemModel model = (ChemModel)jcpPanel.getJChemPaintModel().getChemModel();
        IAtomContainer container = ChemModelManipulator.getAllInOneContainer(model);
        Molecule molecule = new Molecule(container);
        generator.generateName(molecule);
        IUPACName name = (IUPACName)generator.getName();
        String message = "IUPAC name: " + name.getName();
        logger.debug(message);
        JOptionPane.showMessageDialog(jcpPanel, message);
    }
}
