/* $RCSfile$   
 * $Author$   
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2001-2006  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.applications.swing;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Swing class that allows easy building of edit forms.
 *
 * @cdk.module applications
 * @cdk.require swing
 */
public class FieldTablePanel extends JPanel {
        
	private static final long serialVersionUID = -697566299504877020L;
	
	protected int rows;
    
    public FieldTablePanel() {
        setLayout(new GridBagLayout());
        rows = 0;
    }
    
    /**
     * Adds a new JComponent to the 2 column table layout. Both
     * elements will be layed out in the same row. For larger
     * <code>JComponent</code>s the addArea() can be used.
     *
     * @see #addArea(String, JComponent)
     */
    public void addField(String labelText, JComponent component) {
        rows++;
        GridBagConstraints constraints = new GridBagConstraints();
        JLabel label = new JLabel("", JLabel.TRAILING);
        if (labelText != null && labelText.length() > 0) {
            label = new JLabel(labelText + ": ", JLabel.TRAILING);
        }
        label.setLabelFor(component);
        constraints.gridx = 0;
        constraints.gridy = rows;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.weightx = 1.0;
        add(label, constraints);
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(component, constraints);
    }
    
    /**
     * Adds a new JComponent to the 2 column table layout. The JLabel
     * will be placed in one row, while the <code>JComponent</code>
     * will be placed in a second row.
     *
     * @see #addField(String, JComponent)
     */
    public void addArea(String labelText, JComponent component) {
        rows++;
        GridBagConstraints constraints = new GridBagConstraints();
        JLabel label = new JLabel(labelText + ": ");
        label.setLabelFor(component);
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.gridy = rows;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.weightx = 1.0;
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



