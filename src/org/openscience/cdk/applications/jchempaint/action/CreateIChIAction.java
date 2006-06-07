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
 * All I ask is that proper credit is given for my work, which includes
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
 *
 */
package org.openscience.cdk.applications.jchempaint.action;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.graph.invariant.IChIGenerator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;


/**
 * Invokes the creation of an IChI 
 * 
 * @cdk.module jchempaint
 * @author Y. Zhang <yz237@cam.ac.uk>
 *
 */
public class CreateIChIAction extends JCPAction {
	
	private static final long serialVersionUID = 128287423627104971L;
	
	JFrame frame;

    public void actionPerformed(ActionEvent e) {
        logger.debug("Trying to create IChI: ", type);
        IChIGenerator generator = new IChIGenerator();
        String IChI = "";
        logger.debug("IChI = ", IChI);
        try {
            ChemModel model = (ChemModel)jcpPanel.getJChemPaintModel().getChemModel();
            IAtomContainer container = ChemModelManipulator.getAllInOneContainer(model);
            Molecule molecule = new Molecule(container);
            logger.debug("Before runing IChI generater: ");
            IChI = generator.createIChI(molecule);
            String message = "Generated IChI: " + IChI;
            logger.debug(message);

            //JOptionPane.showMessageDialog(jcpPanel, message);
            frame = new TextFrame(IChI);
            frame.pack();
            frame.setVisible(true);
        } catch(Exception exc) {
            logger.error(exc.toString());
        }
    }

    private class TextFrame extends JFrame {
        
		private static final long serialVersionUID = 8998255443644195659L;

		public TextFrame(String ichi) {
            super("IChI");

            //Create a text area.
            JTextArea textArea = new JTextArea(ichi);
            textArea.setFont(new Font("Serif", Font.ITALIC, 16));
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            JScrollPane areaScrollPane = new JScrollPane(textArea);
            areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            areaScrollPane.setPreferredSize(new Dimension(500, 500));
            areaScrollPane.setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createCompoundBorder(
                                    BorderFactory.createTitledBorder("IChI string"),
                                    BorderFactory.createEmptyBorder(5,5,5,5)),
                    areaScrollPane.getBorder()));

            JPanel contentPane = new JPanel();
            BoxLayout box = new BoxLayout(contentPane, BoxLayout.X_AXIS);
            contentPane.setLayout(box);
            contentPane.add(areaScrollPane);
            setContentPane(contentPane);
        }

        public void closeFrame() {
            dispose();
        }
    }
}
