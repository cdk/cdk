/* $RCSfile$   
 * $Author$   
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2001-2007  The Chemistry Development Kit (CDK) project
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

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IReaction;

/**
 * @cdk.module applications
 * @cdk.svnrev  $Revision$
 * @cdk.require swing
 */
public class ReactionEditor extends ChemObjectEditor {
    
    private static final long serialVersionUID = 2386363253522364974L;
    
    private final static String SOLVENT = "org.openscience.cdk.Reaction.Solvent";
    private final static String TEMPERATURE = "org.openscience.cdk.Reaction.Temperature";
    
    private JTextField idField;
    private JComboBox directionField;
    private JTextField solventField;
    private JTextField tempField;
    
	public ReactionEditor() {
        super();
        constructPanel();
	}
    
    private void constructPanel() {
        idField = new JTextField(40);
        addField("Reaction ID", idField);
        // the options given next should match the order in the Reaction class!
        String[] options = {
            "", "Forward", "Backward", "Bidirectional"
        };
        directionField = new JComboBox(options);
        addField("Direction", directionField);
        solventField = new JTextField(40);
        addField("Solvent", solventField);
        tempField = new JTextField(10);
        addField("Temperature", tempField);
    }
    
    public void setChemObject(IChemObject object) {
        if (object instanceof IReaction) {
            source = object;     
            // update table contents
            IReaction reaction = (IReaction)source;
            idField.setText(reaction.getID());
            directionField.setSelectedIndex(reaction.getDirection());
            solventField.setText((String)reaction.getProperty(SOLVENT));
            tempField.setText((String)reaction.getProperty(TEMPERATURE));
        } else {
            throw new IllegalArgumentException("Argument must be an Reaction");
        }
    }
	
    public void applyChanges() {
        IReaction reaction = (IReaction)source;
        reaction.setID(idField.getText());
        reaction.setDirection(directionField.getSelectedIndex());
        reaction.setProperty(SOLVENT, solventField.getText());
        reaction.setProperty(TEMPERATURE, tempField.getText());
    }
}


