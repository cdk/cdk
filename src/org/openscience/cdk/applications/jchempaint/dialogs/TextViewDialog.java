/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2006  The JChemPaint Development Team
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * A simple text viewing dialog for general use.
 *
 * @cdk.module jchempaint
 * @cdk.created 2003-08-24
 */
public class TextViewDialog extends JDialog {

    private JTextArea textArea;
    private Dimension dimension;
    private JLabel textCaption;
    
    /**
     * @see #TextViewDialog(JFrame, String, Dimension, boolean, int, int)
     */
    public TextViewDialog(JFrame fr, String title) {
        this(fr, title, null);
    }

    /**
     * @see #TextViewDialog(JFrame, String, Dimension, boolean, int, int)
     */
    public TextViewDialog(JFrame fr, String title, Dimension dim) {
        this(fr, title, dim, true);
    }

    /**
     * @see #TextViewDialog(JFrame, String, Dimension, boolean, int, int)
     */
    public TextViewDialog(JFrame fr, String title, Dimension dim, boolean wrap) {
        this(fr, title, dim, wrap, 20, 60);
    }
        
    /**
     * Constructs a new JTextViewDialog.
     *
     * @param fr     Parent JFrame
     * @param title  String that will appear in the title bar
     * @param dim    Dimension of the dialog
     * @param wrap   If true, then the lines will be wrapped
     * @param width  Number of chars per line
     * @param height Number of lines
     */
    public TextViewDialog(JFrame fr, String title, Dimension dim, boolean wrap,
                          int width, int height) {
        super(fr, title, true);
        /*if (dim == null) {
            this.dimension = new Dimension(width*10, height*15);
        } else {
            this.dimension = dim;
        }*/
        
        textArea = new JTextArea(width,height);
        textArea.setEditable(false);
        if (wrap) {
            textArea.setLineWrap(wrap);
            textArea.setWrapStyleWord(true);
        }
        JScrollPane scroller = new JScrollPane();
	if (dim != null) scroller.setPreferredSize(dim);
	else scroller.setPreferredSize(new Dimension(400,200));
        //scroller.setAlignmentX(LEFT_ALIGMENT);
        scroller.getViewport().add(textArea);
        
        JPanel textViewer = new JPanel(new BorderLayout());
        //textViewer.setAlignmentX(LEFT_ALIGNMENT);
        textViewer.add(scroller, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("OK");
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OKPressed();
            }
        });
        buttonPanel.add(ok);
        getRootPane().setDefaultButton(ok);
        
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        container.validate();

        textCaption = new JLabel("");
        
        container.add(textCaption, BorderLayout.NORTH);
        container.add(textViewer, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);
        
        getContentPane().add(container);
        pack();
    }
    
    public void OKPressed() {
        this.setVisible(false);
    }
    
    public void setText(String text) {
        textArea.setText(text);
    }
    
    public void setMessage(String caption, String text) {
        textCaption.setText(caption);
        textArea.setText(text);
    }
    
}
