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

import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;

/**
 * @cdk.module applications
 * @cdk.require swing
 */
public class AtomEditor extends ChemObjectEditor {
    
    JTextField symbolField;
    JSpinner   hCountField;
    JSpinner   formalChargeField;
    
	public AtomEditor() {
        super();
        constructPanel();
	}
    
    private void constructPanel() {
        symbolField = new JTextField(4);
        addField("Symbol", symbolField);
        hCountField = new JSpinner(new SpinnerNumberModel());
        addField("H Count", hCountField);
        formalChargeField = new JSpinner(new SpinnerNumberModel());
        addField("Formal Charge", formalChargeField);
    }
    
    public void setChemObject(IChemObject object) {
        if (object instanceof IAtom) {
            source = object;
            // update table contents
            IAtom atom = (IAtom)source;
            symbolField.setText(atom.getSymbol());
            hCountField.setValue(new Integer(atom.getHydrogenCount()));
            formalChargeField.setValue(new Integer(atom.getFormalCharge()));
        } else {
            throw new IllegalArgumentException("Argument must be an Atom");
        }
    }
	
    public void applyChanges() {
        IAtom atom = (IAtom)source;
        atom.setSymbol(symbolField.getText());
        atom.setHydrogenCount(((Integer)hCountField.getValue()).intValue());
        atom.setFormalCharge(((Integer)formalChargeField.getValue()).intValue());
    }
}


