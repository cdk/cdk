/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2004-2007  The JChemPaint project
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
package org.openscience.cdk.applications.jchempaint.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.swing.editor.ChemObjectEditor;
;

/**
  * Simple Dialog that shows the loaded dictionaries.
  * 
  * @cdk.module jchempaint
 * @cdk.svnrev  $Revision$
  */
public class ChemObjectPropertyDialog extends JFrame {

	private static final long serialVersionUID = 1850053536210317644L;
	
	private ChemObjectEditor editor;
    private JChemPaintModel jcpmodel;
    
	/**
	 * Displays the Info Dialog for JChemPaint. 
	 */
    public ChemObjectPropertyDialog(JChemPaintModel jcpmodel, ChemObjectEditor editor) {
        super("IChemObject Props Dialog");
        this.jcpmodel = jcpmodel;
        this.editor = editor;
        createDialog();
        pack();
        setVisible(true);
    }
    
    private void createDialog(){
        getContentPane().setLayout(new BorderLayout());
        setBackground(Color.lightGray);
        setTitle("Properties");
        getContentPane().add("Center",editor);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout ( new FlowLayout(FlowLayout.RIGHT) );
        JButton ok = new JButton("OK");
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OKPressed();
            }}
        );
        buttonPanel.add( ok );
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeFrame();
            }}
        );
        buttonPanel.add( cancel );
        getRootPane().setDefaultButton(ok);
        getContentPane().add("South",buttonPanel);
        
        validate();
    }
    
    private  void OKPressed() {
        // apply new settings
        editor.applyChanges();
        jcpmodel.fireChange();
        closeFrame();
    }

    public void closeFrame() {
        dispose();
    }
}
