/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.mcss.RMap;

/**
 *  Helper class for Structure Diagram Generation. Handles templates. This is
 *  our layout solution for ring systems which are notoriously difficult to
 *  layout, like cubane, adamantane, porphyrin, etc.
 *
 *@author     steinbeck
 *@cdk.created    September 4, 2003
 *@cdk.keyword    layout
 *@cdk.keyword    2D-coordinates
 *@since      Java 1.4
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
	 *  Loads all existing templates into memory To add templates to be used in
	 *  SDG, place a drawing with the new template in data/templates and add the
	 *  template filename to data/templates/template.list
	 */
	public void loadTemplates()
	{
		String line = null;
		try
		{
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream("data/templates/templates.list");
			BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
			while (reader.ready())
			{
				line = reader.readLine();
				line = "data/templates/" + line;
				MDLReader structureReader = new MDLReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(line)));
				templates.addElement((Molecule) structureReader.read((ChemObject) new Molecule()));
				logger.debug("Successfully read template " + line);
			}
		} catch (Exception exc)
		{
			logger.debug("Could not read templates");
			logger.debug("Reason: " + exc.getMessage());

		}

	}

	/**
	 *  Adds a Molecule to the list of 
	 *  templates use by this TemplateHandler
	 *
	 *@param  molecule  The molecule to be added to the TemplateHandler
	 */
	public void addMolecule(Molecule molecule)
	{
		templates.addElement(molecule);
	}
	
	public Molecule removeMolecule(Molecule molecule)
	{
		AtomContainer ac1 = new AtomContainer(molecule);
		AtomContainer ac2 = null;
		Molecule mol2 = null;
		for (int f = 0; f < templates.size(); f++)
		{
			mol2 = (Molecule)templates.elementAt(f);
			ac2 = new AtomContainer(mol2);
			if (UniversalIsomorphismTester.isIsomorph(new AtomContainer(ac1), new AtomContainer(ac2)))
			{
				templates.removeElementAt(f);
				return mol2;
			}
		}
		return null;
	}


	/**
	 *  Checks if one of the loaded templates is a substructure in the given
	 *  Molecule. If so, it assigns the coordinates from the template to the
	 *  respective atoms in the Molecule.
	 *
	 *@param  molecule  The molecule to be check for potential templates
	 *@return           True if there was a possible mapping
	 */
	public boolean mapTemplates(Molecule molecule)
	{
		boolean mapped = false;
		Molecule template = null;
		RMap map = null;
		Atom atom1 = null;
		Atom atom2 = null;
		for (int f = 0; f < templates.size(); f++)
		{
			template = (Molecule) templates.elementAt(f);
			if (UniversalIsomorphismTester.isSubgraph(new AtomContainer(molecule), new AtomContainer(template)))
			{
				List list = UniversalIsomorphismTester.getSubgraphAtomsMap(new AtomContainer(molecule), new AtomContainer(template));
				logger.debug("Found a subgraph mapping of size " + list.size());
				for (int i = 0; i < list.size(); i++)
				{
					map = (RMap) list.get(i);
					atom1 = molecule.getAtomAt(map.getId1());
					atom2 = template.getAtomAt(map.getId2());
					atom1.setX2D(atom2.getX2D());
					atom1.setY2D(atom2.getY2D());
					atom1.setFlag(CDKConstants.ISPLACED, true);
				}
				mapped = true;
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

