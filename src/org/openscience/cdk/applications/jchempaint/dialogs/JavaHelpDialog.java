/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1998-2005  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sf.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.applications.jchempaint.dialogs;

import javax.help.*;
import java.net.URL;

/**
 * Display the help system for the application.
 * 
 * <P>Display one of table of contents, index, or search tab, according
 * to argument passed to the constructor. This implementation uses 
 * Sun's <a href=http://java.sun.com/products/javahelp/>JavaHElp</a> tool.
 * 
 * <P>This action is unusual inthat it corresponds to more than one menu item
 * (contents, favorites and Search)
 *
 * @cdk.module jchempaint
 * @author     mrojas
 * @cdk.created    05. Juny 2005
 */
public class JavaHelpDialog
{
    private CSH.DisplayHelpFromSource fDisplayHelp;


	/**
	 *  Constructor for the JavaHelpDialog object
	 *
	 *@param  fr        Description of the Parameter
	 *@param  helpfile  Description of the Parameter
	 */
	public JavaHelpDialog()
	{
		try {
        URL helpurl=HelpSet.findHelpSet(this.getClass().getClassLoader(),"org/openscience/cdk/applications/jchempaint/resources/userhelp_jcp/jcp.hs");
		    HelpSet hs = new HelpSet(null, helpurl);
		    HelpBroker hb = hs.createHelpBroker();
		    
		    fDisplayHelp = new CSH.DisplayHelpFromSource(hb);
		    
		} catch(Exception ee) {
	        System.out.println("HelpSet: "+ee.getMessage());
	        System.out.println("HelpSet: "+ "jcp.hs" + " not found");
	      }
	}
	/**
	 *  get the Panel of JavaHelp
	 *
	 *@return  CSH.DisplayHelpFromSource  Description of the Parameter
	 */
	public CSH.DisplayHelpFromSource getDisplayHelp()
	{
		return  fDisplayHelp;
	}
}

