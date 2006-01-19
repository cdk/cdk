/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2005  The JChemPaint project
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
package org.openscience.cdk.applications.jchempaint.io;

import java.io.File;
import java.util.Vector;

import javax.swing.JFileChooser;

/**
 * A file filter for JCP
 *
 * @cdk.module jchempaint
 *@author     steinbeck
 */
public class JCPFileFilter extends javax.swing.filechooser.FileFilter implements IJCPFileFilter
{

	/**
	 *  Description of the Field
	 */
	public final static String rxn = "rxn";
	/**
	 *  Description of the Field
	 */
	public final static String rdf = "rdf";
	/**
	 *  Description of the Field
	 */
	public final static String sdf = "sdf";
	/**
	 *  Description of the Field
	 */
	public final static String mol = "mol";
	/**
	 *  Description of the Field
	 */
	public final static String cml = "cml";
	/**
	 *  Description of the Field
	 */
	public final static String xml = "xml";
	/**
	 *  Description of the Field
	 */
	public final static String ichi = "txt";
	/**
	 *  Description of the Field
	 */
	public final static String smi = "smi";

	/**
	 *  Description of the Field
	 */
	protected Vector types;


	/**
	 *  Constructor for the JCPFileFilter object
	 *
	 *@param  type  Description of the Parameter
	 */
	public JCPFileFilter(String type)
	{
		super();
		types = new Vector();
		types.add(type);
	}


	/**
	 *  Adds an additional file type to the list
	 *
	 *@param  type  The feature to be added to the Type attribute
	 */
	public void addType(String type)
	{
		types.add(type);
	}


	/**
	 *  Adds the JCPFileFilter to the JFileChooser object.
	 *
	 *@param  chooser  The feature to be added to the ChoosableFileFilters
	 *      attribute
	 */
	public static void addChoosableFileFilters(JFileChooser chooser)
	{
		chooser.addChoosableFileFilter(new JCPSaveFileFilter(JCPFileFilter.mol));
		chooser.addChoosableFileFilter(new JCPFileFilter(JCPFileFilter.smi));
		chooser.addChoosableFileFilter(new JCPFileFilter(JCPFileFilter.ichi));
		chooser.addChoosableFileFilter(new JCPFileFilter(JCPFileFilter.sdf));
		chooser.addChoosableFileFilter(new JCPFileFilter(JCPFileFilter.rxn));
		chooser.addChoosableFileFilter(new JCPFileFilter(JCPFileFilter.rdf));
		JCPFileFilter cmlFilter = new JCPFileFilter(JCPFileFilter.cml);
		cmlFilter.addType(JCPFileFilter.xml);
		chooser.addChoosableFileFilter(cmlFilter);
	}


	/*
	 *  Get the extension of a file.
	 */
	/**
	 *  Gets the extension attribute of the JCPFileFilter class
	 *
	 *@param  f  Description of the Parameter
	 *@return    The extension value
	 */
	public static String getExtension(File f)
	{
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1)
		{
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}


	// Accept all directories and all gif, jpg, or tiff files.
	/**
	 *  Description of the Method
	 *
	 *@param  f  Description of the Parameter
	 *@return    Description of the Return Value
	 */
	public boolean accept(File f)
	{
		if (f.isDirectory())
		{
			return true;
		}

		String extension = getExtension(f);
		if (extension != null)
		{
			if (types.contains(extension))
			{
				return true;
			} else
			{
				return false;
			}
		}
		return false;
	}


	// The description of this filter
	/**
	 *  Gets the description attribute of the JCPFileFilter object
	 *
	 *@return    The description value
	 */
	public String getDescription()
	{
		String type = (String) types.elementAt(0);
		if (type.equals(mol))
		{
			return "MDL MOL file";
		}
		if (type.equals(sdf))
		{
			return "MDL SDF Molfile";
		}
		if (type.equals(rxn))
		{
			return "MDL RXN Molfile";
		}
		if (type.equals(rdf))
		{
			return "MDL RDF Molfile";
		}
		if (type.equals(ichi))
		{
			return "IUPAC Chemical Identifier";
		}
		if (type.equals(smi))
		{
			return "SMILES";
		}
		if (type.equals(cml) || type.equals(xml))
		{
			return "Chemical Markup Language";
		}
		return null;
	}


	/**
	 *  Gets the type attribute of the JCPFileFilter object
	 *
	 *@return    The type value
	 */
	public String getType()
	{
		return (String) types.elementAt(0);
	}


	/**
	 *  Sets the type attribute of the JCPFileFilter object
	 *
	 *@param  type  The new type value
	 */
	public void setType(String type)
	{
		types.add(type);
	}
}

