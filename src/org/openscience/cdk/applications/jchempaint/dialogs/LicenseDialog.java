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

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.border.Border;



/**
 * Simple Dialog that shows the JCP logo and a textfield that allows
 * the user to copy&paste the URL of JChemPaints main site.
 *
 * @cdk.module jchempaint
 */
public class LicenseDialog extends JFrame {

	/**
	 * Displays the License Dialog for JChemPaint. 
	 */
    public LicenseDialog() {
        super("JChemPaint License");
        doInit();
    }
    
    
    public void doInit(){
        Package self = Package.getPackage("org.openscience.cdk.applications.jchempaint");
	String version = self.getImplementationVersion();

        String s1 = 
            "JChemPaint "  + version + " is licensed LGPL, but " +
            "the libraries it uses have different licenses.\n" +
            "See 'http://jchempaint.sourceforge.net' for further information " +
            "about the license you accept when using this software.\n\n";
        String s2 =
            "Copyright (C) 1997-2006  The JChemPaint project\n" +
            "\n" +
            "Contact: jchempaint-devel@lists.sf.net\n" +
            "\n" +
 "This program is free software; you can redistribute it and/or\n" +
 "modify it under the terms of the GNU Lesser General Public License\n" +
 "as published by the Free Software Foundation; either version 2.1\n" +
 "of the License, or (at your option) any later version.\n" +
 "All we ask is that proper credit is given for our work, which includes\n" +
 "- but is not limited to - adding the above copyright notice to the beginning\n" +
 "of your source code files, and to any copyright notice that you may distribute\n" +
 "with programs based on this work.\n\n" +

 "This program is distributed in the hope that it will be useful,\n" +
 "but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
 "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
 "GNU Lesser General Public License for more details.\n\n" +

 "You should have received a copy of the GNU Lesser General Public License\n" +
 "along with this program; if not, write to the Free Software\n" +
 "Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.\n";

        //JLabel label1 = new JLabel();
        getContentPane().setLayout(new BorderLayout());
        setBackground(Color.white);
        try {
		// XXX needs to be fixed
            //ImageIcon icon = new ImageIcon(JChemPaint.jcplogo);
            //label1 = new JLabel(icon);
        } catch(Exception exc) {
            exc.printStackTrace();
        }
        Border lb = BorderFactory.createLineBorder(Color.white, 5);
        JTextArea jtf1 = new JTextArea(s1);
        jtf1.setBorder(lb);
        jtf1.setEditable(false);
        JTextArea jtf2 = new JTextArea(s2);
        jtf2.setEditable(false);
        jtf2.setBorder(lb);
        setTitle("About JChemPaint's License");
        getContentPane().add("Center",jtf2);
        getContentPane().add("North",jtf1);
        pack();
        setVisible(true);
    }
}
