/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2005-2006  The JChemPaint project
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

/**
 * @cdk.module jchempaint.application
 */
public class ModifyGeneralSettingsDialog extends JFrame {

    private GeneralSettingsEditor editor;
    
	/**
	 * Displays the Info Dialog for JChemPaint.
	 */
    public ModifyGeneralSettingsDialog() {
        super("Modify General Settings Dialog");
        editor = new GeneralSettingsEditor(this);
        createDialog();
        pack();
        setVisible(true);
    }
    
    private void createDialog(){
        getContentPane().setLayout(new BorderLayout());
        setBackground(Color.lightGray);
        setTitle("General Setting");
        editor.setSettings();
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
        getRootPane().setDefaultButton(ok);
        JButton apply = new JButton("Apply");
        apply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ApplyPressed();
            }}
        );
        buttonPanel.add( apply );
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
    
    private  void ApplyPressed() {
        // apply new settings
        editor.applyChanges();
    }
    private  void OKPressed() {
        ApplyPressed();
        closeFrame();
    }

    public void closeFrame() {
        dispose();
    }
}
