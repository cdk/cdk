/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.layout;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * Helper class for Structure Diagram Generation. Handles templates. This is
 * our layout solution for ring systems which are notoriously difficult to
 * layout, like cubane, adamantane, porphyrin, etc.
 *
 * @author       steinbeck
 * @cdk.created  2003-09-04
 * @cdk.keyword  layout
 * @cdk.keyword  2D-coordinates
 * @cdk.require  java1.4+
 */
public class TemplateHandler
{

	private LoggingTool logger;

	IMolecule molecule;
	IRingSet sssr;
	double bondLength = 1.5;

	Vector templates = null;


	/**
	 * Creates a new TemplateHandler.
	 */
	public TemplateHandler()
	{
		logger = new LoggingTool(this);
		templates = new Vector();
		loadTemplates();
	}


	/**
	 * Loads all existing templates into memory. To add templates to be used in
	 * SDG, place a drawing with the new template in data/templates and add the
	 * template filename to data/templates/template.list
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
                logger.debug("Attempting to read template ", line);
				CMLReader structureReader = new CMLReader(
                    this.getClass().getClassLoader().getResourceAsStream(line)
                );
                IChemFile file = (IChemFile)structureReader.read(new org.openscience.cdk.ChemFile());
				templates.addElement(file.getBuilder().newMolecule(
                    ChemFileManipulator.getAllInOneContainer(file)
                ));
				logger.debug("Successfully read template ", line);
			}
		} catch (Exception exc)
		{
			logger.debug("Could not read templates");
			logger.debug("Reason: " + exc.getMessage());

		}

	}

	/**
	 * Adds a Molecule to the list of templates use by this TemplateHandler
	 *
	 * @param  molecule  The molecule to be added to the TemplateHandler
	 */
	public void addMolecule(IMolecule molecule)
	{
		templates.addElement(molecule);
	}
	
	public IMolecule removeMolecule(IMolecule molecule)  throws CDKException
	{
		IAtomContainer ac1 = molecule.getBuilder().newAtomContainer(molecule);
		IAtomContainer ac2 = null;
		IMolecule mol2 = null;
		for (int f = 0; f < templates.size(); f++)
		{
			mol2 = (IMolecule)templates.elementAt(f);
			ac2 = molecule.getBuilder().newAtomContainer(mol2);
			if (UniversalIsomorphismTester.isIsomorph(ac1, ac2)) {
				templates.removeElementAt(f);
				return mol2;
			}
		}
		return null;
	}


	/**
	 * Checks if one of the loaded templates is a substructure in the given
	 * Molecule. If so, it assigns the coordinates from the template to the
	 * respective atoms in the Molecule, and marks the atoms as ISPLACED.
	 *
	 * @param  molecule  The molecule to be check for potential templates
	 * @return           True if there was a possible mapping
	 */
	public boolean mapTemplates(IMolecule molecule) throws CDKException {
        logger.debug("Trying to map a molecule...");
		boolean mapped = false;
		IMolecule template = null;
		RMap map = null;
		org.openscience.cdk.interfaces.IAtom atom1 = null;
		org.openscience.cdk.interfaces.IAtom atom2 = null;
		for (int f = 0; f < templates.size(); f++)
		{
			template = (IMolecule) templates.elementAt(f);
			if (UniversalIsomorphismTester.isSubgraph(molecule, template))
			{
				List list = UniversalIsomorphismTester.getSubgraphAtomsMap(
                    molecule.getBuilder().newAtomContainer(molecule), 
                    molecule.getBuilder().newAtomContainer(template)
                );
				logger.debug("Found a subgraph mapping of size " + list.size());
				for (int i = 0; i < list.size(); i++)
				{
					map = (RMap) list.get(i);
					atom1 = molecule.getAtomAt(map.getId1());
					atom2 = template.getAtomAt(map.getId2());
					atom1.setX2d(atom2.getX2d());
					atom1.setY2d(atom2.getY2d());
					atom1.setFlag(CDKConstants.ISPLACED, true);
				}
				mapped = true;
			} else {
                logger.warn("Structure does not match template: ", template.getID());
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
	public IAtomContainer getTemplateAt(int position)
	{
		return (IAtomContainer) templates.elementAt(position);
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

