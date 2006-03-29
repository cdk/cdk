/* $RCSfile$   
 * $Author$   
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.applications.jchempaint.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JFrame;

import org.openscience.cdk.applications.jchempaint.JCPPropertyHandler;
import org.openscience.cdk.applications.swing.FieldTablePanel;

/**
 * JFrame that allows setting of a number of general application options.
 * 
 * @cdk.module jchempaint.application
 */
public class GeneralSettingsEditor extends FieldTablePanel implements ActionListener {
    
    private JCheckBox askForIOSettings;
    
    private JFrame frame;
    
    public GeneralSettingsEditor(JFrame frame) {
        super();
        this.frame = frame;
        constructPanel();
    }
    
    private void constructPanel() {
        askForIOSettings = new JCheckBox();
        addField("Ask for IO settings", askForIOSettings);
    }
    
    public void setSettings() {
        Properties props = JCPPropertyHandler.getInstance().getJCPProperties();
        askForIOSettings.setSelected(props.getProperty("askForIOSettings", "true").equals("true"));
        validate();
    }

    public void applyChanges() {
        Properties props = JCPPropertyHandler.getInstance().getJCPProperties();
        props.setProperty("askForIOSettings",
            askForIOSettings.isSelected() ? "true" : "false"
        );
    }
    
    /**
     * Required by the ActionListener interface.
     */
    public void actionPerformed(ActionEvent e) {
        // nothing to do whatsoever
    }

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}
}


