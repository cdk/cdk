/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
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
 *
 */
package org.openscience.cdk.layout;

import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.geometry.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.isomorphism.*;
import javax.vecmath.*;
import java.io.*;
import java.util.*;

/**
 *  Helper class for Structure Diagram Generation. Handles templates. This is
 *  our layout solution for ring systems which are notoriously difficult to
 *  layout, like cubane, adamantane, porphyrin, etc.
 *
 *@author     steinbeck
 *@created    September 4, 2003
 *@keyword    layout
 *@keyword    2D-coordinates
 */
public class TemplateHandler
{

	private org.openscience.cdk.tools.LoggingTool logger;

	Molecule molecule;
	RingSet sssr;
	double bondLength = 1.5;

	Vector templates = null;


	/**
	 *  The empty constructor.
	 */
	public TemplateHandler()
	{
		logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
		templates = new Vector();
		loadTemplates();
	}


	/**
	 *  Loads all existing templates into memory
	 */
	public void loadTemplates()
	{
		File file = new File("data/templates");
		File[] templateFiles = file.listFiles();
		logger.debug("found " + templateFiles.length + " templates for Structure Diagram Generation");
		for (int f = 0; f < templateFiles.length; f++)
		{
			try
			{
				MDLReader reader = new MDLReader(new FileReader(templateFiles[f]));
				templates.addElement((Molecule) reader.read((ChemObject) new Molecule()));
			} catch (Exception exc)
			{
				logger.debug("Could not read template from file " + templateFiles[f]);
				logger.debug("Reason: " + exc.getMessage());
			}
		}

	}


	/**
	 *  Checks if one of the loaded templates is a substructure in the given
	 *  Molecule. If so, it assigns the coordinates from the template to the
	 *  respective atoms in the Molecule.
	 *
	 *@param  molecule       The molecule to be check for potential templates
	 *@return                True if there was a possible mapping
	 */
	public boolean mapTemplates(Molecule molecule)
	{
		boolean mapped = false;
		Molecule template = null;
		
		for (int f = 0; f < templates.size(); f++)
		{
			template = (Molecule) templates.elementAt(f);
			if (UniversalIsomorphismTester.isSubgraph(new AtomContainer(molecule), new AtomContainer(template)))
			{
				mapped = true;
				logger.debug("Found a subgraph mapping");
				List list = UniversalIsomorphismTester.getSubgraphAtomsMap(new AtomContainer(molecule), new AtomContainer(template));
				logger.debug("found mapping of size " + list.size());
			}
		}
		return mapped;
	}

	/**
	 *  Gets the templateCount attribute of the TemplateHandler object
	 *
	 *@return    The templateCount value
	 */
	public int getTemplateCount()
	{
		return templates.size();
	}


	/**
	 *  Gets the templateAt attribute of the TemplateHandler object
	 *
	 *@param  position  Description of the Parameter
	 *@return           The templateAt value
	 */
	public AtomContainer getTemplateAt(int position)
	{
		return (AtomContainer) templates.elementAt(position);
	}


	/**
	 *  Set the bond length used for laying out the molecule
	 *
	 *@param  bondLength  The new bondLength value
	 */
	public void setBondLength(double bondLength)
	{
		this.bondLength = bondLength;
	}
}

