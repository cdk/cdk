/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2004-2006  The JChemPaint project
 *
 * Contact: jchempaint-devel@lists.sf.net
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 *  Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.applications.jchempaint.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;

import org.openscience.cdk.applications.swing.PeriodicTablePanel;
import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Simple Dialog that shows the JCP logo and a textfield that allows
 * the user to copy&amp;paste the URL of JChemPaints main site.
 *
 * @cdk.module jchempaint
 */
public class PTDialog extends JFrame {

    private static LoggingTool logger = null;
    private PeriodicTablePanel ptp;
    
    public PTDialog(ICDKChangeListener listener) {
        super("Choose an element");
        logger = new LoggingTool(this);
        doInit();
        ptp.addCDKChangeListener(listener);
    }
    
    public void doInit(){
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(Color.white);
        setTitle("Choose an element...");
        
        ptp = new PeriodicTablePanel();
        getContentPane().add("Center",ptp);
        pack();
		setVisible(true);
  }
}
