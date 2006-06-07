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

/**
 * Simple Dialog that shows information about JChemPaint's plugins.
 *
 * @cdk.module jchempaint
 */
public class PluginInfoDialog extends JFrame {

	private static final long serialVersionUID = 6636619283453734094L;
	
	private JEditorPane infoPane;
    
	/**
	 * Displays the Plugin Info Dialog for JChemPaint. 
	 */
    public PluginInfoDialog() {
        super("JChemPaint License");
        createDialog();
        displayContent();
        pack();
        setVisible(true);
    }
    
    private void createDialog(){
        
        getContentPane().setLayout(new BorderLayout());
        setBackground(Color.white);

        Border lb = BorderFactory.createLineBorder(Color.white, 5);
        JTextArea jtf1 = new JTextArea("Information about JChemPaint");
        jtf1.setBorder(lb);
        jtf1.setEditable(false);
        infoPane = new JEditorPane();
        infoPane.setEditable(false);
        infoPane.setBorder(lb);
        infoPane.revalidate();
        JScrollPane scrollPane = new JScrollPane(infoPane);
        scrollPane.setPreferredSize(new Dimension(400, 350));
        
        setTitle("Plugin Information");
        getContentPane().add("Center",scrollPane);
        getContentPane().add("North",jtf1);
    }
    
    private void displayContent() {
        StringBuffer content = new StringBuffer();
        content.append("<html>\n");
        addPluginsInfo(content);
        content.append("</html>\n");
        
        infoPane.setContentType("text/html");
        infoPane.setText(content.toString());
        infoPane.revalidate();
    }
    
    private void addPluginsInfo(StringBuffer content) {
        content.append("<h3>Plugins</h3>\n");
        content.append("<p>Plugins should be installed in: <i>" + 
                       System.getProperty("user.home") + "/.jchempaint/plugins</i><br>\n");
        content.append("They can be downloaded from http://cdk.sf.net/plugins.html");
        // XXX needs fixing
	/*Enumeration plugins = mothership.getPluginManager().getPlugins();
	if (plugins.hasMoreElements()) {
            content.append("<p>These plugins are installed:\n");
            content.append("<table>");
            while (plugins.hasMoreElements()) {
                CDKPluginInterface plugin = (CDKPluginInterface)plugins.nextElement();
                content.append("<tr>");
                content.append("<td>" + plugin.getName());
                if (Double.parseDouble(plugin.getAPIVersion()) >= 1.4) {
                    content.append("<td>" + plugin.getPluginVersion());
                } else {
                    content.append("<td>");
                }
                content.append("</tr>");
            }
            content.append("</table>");
        } else {
            content.append("<p>No plugins are loaded.");
        }*/
    }
    
}
