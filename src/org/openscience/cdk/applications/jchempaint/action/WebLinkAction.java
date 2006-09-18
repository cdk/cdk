/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2006  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sourceforge.net
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.applications.jchempaint.action;

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.applications.jchempaint.dialogs.WebDialog;
import org.openscience.cdk.io.MACiEReader;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;


/**
 *  Action to follow a weblink
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 */
public class WebLinkAction extends JCPAction
{

    private static final long serialVersionUID = -5055722920851824045L;

    /**
	 *  Description of the Method
	 *
	 *@param  event  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent event)
	{
		logger.debug("Opening a webpage for: ", type);
		ChemModel model = null;
		try
		{
			model = (ChemModel) getSource(event);
		} catch (ClassCastException exception)
		{
			logger.error("Method is normally called on ChemModel");
			logger.debug(exception);
			JOptionPane.showMessageDialog(jcpPanel, "Cannot display webinfo entry.");
			return;
		}
		WebDialog dialog = null;
		// XXX needs fixing
		// JFrame frame = (JFrame) jcpPanel.getFrame();
		JFrame frame = new JFrame();
		URL url = null;
		try
		{
			String uri = "";
			if (type.equals("pdb"))
			{
				String pdbCode = (String) model.getProperty(MACiEReader.PDBCode);
				uri = "http://www.rcsb.org/pdb/cgi/explore.cgi?pdbId="
						 + pdbCode;
			} else if (type.equals("medline"))
			{
				String medlineCode = (String) model.getProperty(MACiEReader.MedlineID);
				uri = "http://www.ncbi.nlm.nih.gov/entrez/queryd.fcgi?cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids="
						 + medlineCode;
			}
			url = new URL(uri);
		} catch (MalformedURLException exception)
		{
			logger.error("Incorrect URL");
			return;
		}
		dialog = new WebDialog(frame, url);
		dialog.setVisible(true);
	}

}

