/* $RCSfile$   
 * $Author$   
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2001-2005  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.PseudoAtom;

/**
 * @cdk.module applications
 * @cdk.require swing
 */
public class PseudoAtomEditor extends ChemObjectEditor {
    
    JTextField labelField;
    
	public PseudoAtomEditor() {
        super();
        constructPanel();
	}
    
    private void constructPanel() {
        labelField = new JTextField(20);
        addField("Label", labelField);
    }
    
    public void setChemObject(ChemObject object) {
        if (object instanceof PseudoAtom) {
            source = object;
            // update table contents
            labelField.setText(((PseudoAtom)object).getLabel());
        } else {
            throw new IllegalArgumentException("Argument must be an PseudoAtom");
        }
    }
	
    public void applyChanges() {
        PseudoAtom atom = (PseudoAtom)source;
        atom.setLabel(labelField.getText());
    }
}


