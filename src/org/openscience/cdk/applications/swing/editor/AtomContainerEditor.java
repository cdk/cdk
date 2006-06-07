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
package org.openscience.cdk.applications.swing.editor;

import javax.swing.JTextField;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * @cdk.module applications
 * @cdk.require swing
 */
public class AtomContainerEditor extends ChemObjectEditor {
    
    private static final long serialVersionUID = -5106331683641108940L;
    
    JTextField titleField;
    
	public AtomContainerEditor() {
        super();
        constructPanel();
	}
    
    private void constructPanel() {
        titleField = new JTextField(30);
        addField("Title", titleField);
    }
    
    public void setChemObject(IChemObject object) {
        if (object instanceof IAtomContainer) {
            source = object;
            // update table contents
            IAtomContainer container = (IAtomContainer)source;
            String title = "";
            if (container.getProperty(CDKConstants.TITLE) != null) title = container.getProperty(CDKConstants.TITLE).toString();
            titleField.setText(title);
        } else {
            throw new IllegalArgumentException("Argument must be an Atom");
        }
    }
	
    public void applyChanges() {
        source.setProperty(CDKConstants.TITLE, titleField.getText());
    }
}


