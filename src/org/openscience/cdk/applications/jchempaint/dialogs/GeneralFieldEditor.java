/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2006  The JChemPaint project
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
package org.openscience.cdk.applications.jchempaint.dialogs;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.jchempaint.JChemPaintPanel;

/**
 * Internal frame to allow for changing the properties.
 *
 * @cdk.module jchempaint
 */
public class GeneralFieldEditor extends JFrame {
    
	private static final long serialVersionUID = -5332727131588533929L;
	
	Properties props;
    JChemPaintModel jcpm;

    String[] defaults;
    String[] currentValues;
    JTextField[] fields;
    JChemPaintPanel jcp;
    /**
     * IMPORTANT: the fieldTitles.length and defaults.length *must* be equal.
     */
    public GeneralFieldEditor(JChemPaintPanel jcp, String title,
        String[] fieldTitles, String[] defaults) {
        super(title);
        this.jcp = jcp;
        jcpm = jcp.getJChemPaintModel();

        getContentPane().setLayout(new BorderLayout());
        JPanel southPanel = new JPanel();
        JButton cancelButton = new JButton("Cancel");
        JButton openButton = new JButton("OK");
        openButton.addActionListener(new UpdateAction());
        cancelButton.addActionListener(new CancelAction());
        southPanel.add(openButton);
        southPanel.add(cancelButton);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(5,2));
        
        this.defaults = defaults;
        this.currentValues = this.defaults;
        fields = new JTextField[fieldTitles.length];
        for (int i=0; i<fieldTitles.length; i++) {
            JLabel label = new JLabel(fieldTitles[i]);
            JTextField textField = new JTextField("", 20);
            centerPanel.add(label);
            centerPanel.add(textField);
            fields[i] = textField;
        }
        
        setSize(100*fields.length, 500);
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add("Center", centerPanel);
        getContentPane().add("South", southPanel);
    }
    
    public String[] getFieldValues() {
        return this.currentValues;
    }
    
    public void closeFrame(){
        dispose();
    }

    class UpdateAction extends AbstractAction {
    	
		private static final long serialVersionUID = 7477381340397319284L;

		UpdateAction() {
            super("Update");
        }
        
        public void actionPerformed(ActionEvent event) {
            // update the current values
            for (int i=0; i< fields.length; i++) {
                JTextField field = fields[i];
                currentValues[i] = field.getText();
            }
            closeFrame();
        }
    }

    class CancelAction extends AbstractAction {
    	
		private static final long serialVersionUID = 509508358451482801L;

		CancelAction() {
            super("Cancel");
        }
        
        public void actionPerformed(ActionEvent e) {
            currentValues = defaults;
            closeFrame();
        }
    }

    class EditAction extends AbstractAction {
    	
		private static final long serialVersionUID = 2240224589987930306L;

		EditAction(String prop) {
            super("Edit");
        }

        public void actionPerformed(ActionEvent e) {
            // do not validate content
        }
    }
 }
