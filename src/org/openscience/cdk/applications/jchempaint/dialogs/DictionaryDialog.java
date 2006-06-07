/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2006  The JChemPaint project
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
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import org.openscience.cdk.applications.jchempaint.JChemPaintEditorPanel;
import org.openscience.cdk.dict.DictionaryDatabase;

/**
  * Simple Dialog that shows the loaded dictionaries.
  *
  * @cdk.module jchempaint
  */
public class DictionaryDialog extends JFrame {

	private static final long serialVersionUID = -8933920987749316390L;
	
	private JEditorPane infoPane;
    
	/**
	 * Displays the Info Dialog for JChemPaint. 
	 */
    public DictionaryDialog() {
        super("Loaded Dictionaries");
        createDialog();
        displayContent();
        pack();
        setVisible(true);
    }
    
    
    
    private void createDialog(){
        
        getContentPane().setLayout(new BorderLayout());
        setBackground(Color.white);

        Border lb = BorderFactory.createLineBorder(Color.white, 5);
        JTextArea jtf1 = new JTextArea("Loaded Dictionaries");
        jtf1.setBorder(lb);
        jtf1.setEditable(false);
        infoPane = new JEditorPane();
        infoPane.setEditable(false);
        infoPane.setBorder(lb);
        infoPane.revalidate();
        JScrollPane scrollPane = new JScrollPane(infoPane);
        scrollPane.setPreferredSize(new Dimension(400, 350));
        
        setTitle("Information");
        getContentPane().add("Center",scrollPane);
        getContentPane().add("North",jtf1);
    }
    
    private void displayContent() {
        StringBuffer content = new StringBuffer();
        content.append("<html>\n");
        
        // convert Dictionary to HTML
        DictionaryDatabase dictdb = JChemPaintEditorPanel.getDictionaryDatabase();
        String[] dicts = dictdb.getDictionaryNames();
        if (dicts.length > 0) {
            content.append("<table>");
            for (int i=0; i<dicts.length; i++) {
                String[] entries = dictdb.getDictionaryEntries(dicts[i]);
                for (int j=0; j<entries.length; j++) {
                    content.append("<tr>");
                    content.append("<td>" + dicts[i]);
                    content.append("<td>" + entries[j]);
                    content.append("</tr>\n");
                }
            }
            content.append("</table>");
        } else {
            content.append("<p>No dictionaries are loaded.");
        }
        
        content.append("</html>\n");
        
        infoPane.setContentType("text/html");
        infoPane.setText(content.toString());
        infoPane.revalidate();
    }

}
