/* Molecule.java
 * 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2000  The CompChem project
 * 
 * Contact: steinbeck@ice.mpg.de, geelter@maul.chem.nd.edu, egonw@sci.kun.nl
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */

package org.openscience.cdk;

import java.util.Vector;

/**
 * Represents the concept of a chemical molecule, an object composed of 
 * atoms connected by bonds
 *
 * @author     steinbeck 
 * @created    October 2, 2000 
 */
public class Molecule extends AtomContainer
{
	private Vector chemNames; 
    private String autonomName = "";
    private String casRN = "";
    private String beilsteinRN = "";



	/**
	 * Creates an empty Molecule
	 *
	 */
	public Molecule()
	{
		super();
		chemNames = new Vector();
	}
	

	/**
	 * Returns the AutonomName for this molecule. Autonom is a program that 
	 * published by Beilstein, which uniquely names a chemical structure
	 *
	 * @return The autoname name of this structure
	 */
	public String getAutonomName()
	{
		return this.autonomName;
	}


	/**
	 * Sets the AutonomName for this molecule. Autonom is a program that 
	 * published by Beilstein, which uniquely names a chemical structure
     *
	 * @param   autonomName  The Autonom name to be assigned to this structure
	 */
	public void setAutonomName(String autonomName)
	{
		this.autonomName = autonomName;
	}

	

	/**
	 * Returns the Beilstein Registry Number of this Molecule
	 *
	 * @return The Beilstein Registry Number of this Molecule 
	 */
	public String getBeilsteinRN()
	{
		return this.beilsteinRN;
	}


	/**
	 * Assigns a Beilstein Registry Number to this Molecule
	 *
	 * @param   beilsteinRN  The Beilstein Registry Number to be assigned to this molecule	
	 */
	public void setBeilsteinRN(String beilsteinRN)
	{
		this.beilsteinRN = beilsteinRN;
	}

	

	/**
	 * Returns the CAS Registry Number of this Molecule
	 *
	 * @return The CAS Registry Number of this Molecule    
	 */
	public String getCasRN()
	{
		return this.casRN;
	}


	/**
	 * Assigns a CAS Registry Number to this Molecule
	 *
	 * @param   casRN  The CAS Registry Number to be assinged to this Molecule
	 */
	public void setCasRN(String casRN)
	{
		this.casRN = casRN;
	}


	/**
	 * Returns the number of chemcial names stored for this Molecule
	 *
	 * @return The number of chemical names stored for this Molecule
	 */
	public int getChemNamesCount()
	{
		return chemNames.size();
	}
	

	/**
	 * Returns the Vector contains all the chemical names stored for this Molecule
	 *
	 * @return The Vector contains all the chemical names stored for this Molecule    
	 */
	public Vector getChemNames()
	{
		return this.chemNames;
	}
	

	/**
	 * Bulk method for assigning a bunch of chemical names to this molecule
	 * by overwriting the chemical names vector as a whole.
	 *
	 * @param   chemNames A vector containing a bunch of chemical names to be assigned to this Molecule 
	 */
	public void setChemNames(Vector chemNames)
	{
		this.chemNames = chemNames;
	}
	

	/**
	 * Return one of the chemical names of this molecule.
	 *
	 * @param   number The position of the chemcial name to be returned
	 * @return  The chemical name of this molecule whose position in the chemical names vector is indicated by number   
	 */
	public String getChemName(int number)
	{
		return (String)chemNames.elementAt(number);
	}


	/**
	 * Add a new chemcial name to the collection of chemical names in this molecule
	 *
	 * @param   chemName The new chemical name to added to the collection of chemical names of this molecule 
	 */
	public void addChemName(String chemName)
	{
		this.chemNames.addElement(chemName);
	}
	
		
	
}

