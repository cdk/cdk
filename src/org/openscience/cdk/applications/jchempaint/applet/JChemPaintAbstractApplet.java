/*
 *  Copyright (C) 2002-2005  The Jmol Development Team
 *
 *  Contact: jmol-developers@lists.sf.net
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

package org.openscience.cdk.applications.jchempaint.applet;

import java.awt.BorderLayout;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import javax.swing.JApplet;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.applications.jchempaint.JCPPropertyHandler;
import org.openscience.cdk.applications.jchempaint.JChemPaintEditorPanel;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.jchempaint.JChemPaintPanel;
import org.openscience.cdk.io.ChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;

/**
 * The
 * 
 * @cdk.module jchempaint.applet
 * @author dirk49
 * @created 22. April 2005
 */
public abstract class JChemPaintAbstractApplet extends JApplet {
	private JChemPaintPanel theJcpp = null;
	private JChemPaintModel theModel = null;
	private String localeString = null;
	private PropertyResourceBundle appletProperties = null;

	private static String appletInfo = "JChemPaint Applet. See http://cdk.sourceforge.net "
			+ "for more information";

	private static String[][] paramInfo = {
			{ "bgcolor", "color",
					"Background color to HTML color name or #RRGGBB" },
			{ "load", "url", "URL of the chemical data" }, };

	public String getAppletInfo() {
		return appletInfo;
	}

	public String[][] getParameterInfo() {
		return paramInfo;
	}

	private String getValue(String propertyName, String defaultValue) {
		String stringValue = getParameter(propertyName);
		if (stringValue != null)
			return stringValue;
		if (appletProperties != null) {
			try {
				stringValue = appletProperties.getString(propertyName);
				return stringValue;
			} catch (MissingResourceException ex) {
			}
		}
		return defaultValue;
	}

	private int getValue(String propertyName, int defaultValue) {
		String stringValue = getValue(propertyName, null);
		if (stringValue != null)
			try {
				return Integer.parseInt(stringValue);
			} catch (NumberFormatException ex) {
				System.out.println(propertyName + ":" + stringValue
						+ " is not an integer");
			}
		return defaultValue;
	}

	private double getValue(String propertyName, double defaultValue) {
		String stringValue = getValue(propertyName, null);
		if (stringValue != null)
			try {
				return (new Double(stringValue)).doubleValue();
			} catch (NumberFormatException ex) {
				System.out.println(propertyName + ":" + stringValue
						+ " is not an integer");
			}
		return defaultValue;
	}

	public void initPanelAndModel(JChemPaintPanel jcpp, JChemPaintModel model) {
		getContentPane().removeAll();
		getContentPane().setLayout(new BorderLayout());
		model.setTitle("JCP Applet" /* getNewFrameName() */);
		model.setAuthor(JCPPropertyHandler.getInstance().getJCPProperties().getProperty("General.UserName"));
		Package self = Package.getPackage("org.openscience.cdk.applications.jchempaint");
		String version = self.getImplementationVersion();
		model.setSoftware("JChemPaint " + version);
		model.setGendate((Calendar.getInstance()).getTime().toString());
		jcpp.setJChemPaintModel(model);
		// TODO implement empty method registerModel in JChemPaintViewerOnlyPanel or JChemPaintModel
		if (jcpp instanceof JChemPaintEditorPanel)
			((JChemPaintEditorPanel) jcpp).registerModel(model);

		//embedded means that additional instances can't be created, which is
		// needed for applet as well
		jcpp.setEmbedded();
		getContentPane().add(jcpp, BorderLayout.CENTER);
	}

	public void loadModel() {
		URL fileURL = null;
		theModel = new JChemPaintModel();
		try {
			URL documentBase = getDocumentBase();
			fileURL = new URL(documentBase, getParameter("load"));
		} catch (Exception exception) {
			System.out.println("Cannot load model: " + exception.toString());
			exception.printStackTrace();
		}
		if (fileURL != null) {
			try {
				InputStreamReader isReader = new InputStreamReader(fileURL.openStream());
				ChemObjectReader reader = new ReaderFactory().createReader(isReader);
				ChemModel chemModel = (ChemModel) reader.read(new ChemModel());
				theModel = new JChemPaintModel(chemModel);
			} catch (Exception exception) {
				System.out.println("Cannot parse model: " + exception.toString());
				exception.printStackTrace();
			}
		}
		initPanelAndModel(theJcpp, theModel);
	}

	public void start() {
		//Parameter parsing goes here
		loadModel();
	}

	public void stop() {

	}
	/**
	 * @return Returns the theJcpp.
	 */
	public JChemPaintPanel getTheJcpp() {
		return theJcpp;
	}
	/**
	 * @param theJcpp The theJcpp to set.
	 */
	public void setTheJcpp(JChemPaintPanel theJcpp) {
		this.theJcpp = theJcpp;
	}
}
