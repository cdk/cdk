/* $RCSfile$   
 * $Author$   
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2001-2004  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */
package org.openscience.cdk.applications.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.util.EventObject;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JComponent;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.event.CDKChangeListener;

/**
 * Swing class that allows easy building of edit forms.
 *
 * @cdk.module applications
 */
public class FieldTablePanel extends JPanel {
        
    protected int rows;
    
    public FieldTablePanel() {
        setLayout(new GridBagLayout());
        rows = 0;
    }
    
    protected void addField(String labelText, JComponent component) {
        rows++;
        GridBagConstraints constraints = new GridBagConstraints();
        JLabel label = new JLabel(labelText + ": ", JLabel.TRAILING);
        label.setLabelFor(component);
        constraints.gridx = 0;
        constraints.gridy = rows;
        constraints.anchor = GridBagConstraints.LINE_START;
        add(label, constraints);
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(component, constraints);
    }
    
    protected void addArea(String labelText, JComponent component) {
        rows++;
        GridBagConstraints constraints = new GridBagConstraints();
        JLabel label = new JLabel(labelText + ": ");
        label.setLabelFor(component);
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.gridy = rows;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(label, constraints);
        rows++;
        constraints.gridy = rows;
        JScrollPane editorScrollPane = new JScrollPane(component);
        editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        editorScrollPane.setPreferredSize(new Dimension(250, 145));
        editorScrollPane.setMinimumSize(new Dimension(10, 10));
        add(editorScrollPane, constraints);
    }
    
}



