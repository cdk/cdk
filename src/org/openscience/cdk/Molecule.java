/*
 * $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 *
 * @keyword    molecule
 */
public class Molecule extends AtomContainer
{
	/**
	 *  Description of the Field
	 */
	public String title;
	private Vector chemNames;
	private String autonomName = "";
	private String casRN = "";
	private String beilsteinRN = "";


	/**
	 *  Creates an empty Molecule
	 */
	public Molecule()
	{
		super();
		chemNames = new Vector();
	}


	/**
	 *  Constructor for the Molecule object
	 *
	 * @param  atomCount  Description of Parameter
	 * @param  bondCount  Description of Parameter
	 */
	public Molecule(int atomCount, int bondCount)
	{
		super(atomCount, bondCount);
		chemNames = new Vector();
	}


	/**
	 *  Constructs a Molecule with a copy of the atoms and bonds of another Molecule (A shallow copy, i.e., with the same objects as in the original AtomContainer)
	 *
	 * @param  ac  An Molecule to copy the atoms and bonds from
	 */
	public Molecule(Molecule mol)
	{
		super((AtomContainer) mol);
	}

	/**
	 * Constructs a Molecule with 
	 * a copy of the atoms and bonds of another Molecule
	 * (A shallow copy, i.e., with the same objects as in the original AtomContainer)
	 *
	 * @param   ac  An Molecule to copy the atoms and bonds from 
	 */
	public Molecule(AtomContainer ac)
	{
		super(ac);
	}

	
	/**
	 *  Sets the AutonomName for this molecule. Autonom is a program that published by Beilstein, which uniquely names a chemical structure
	 *
	 * @param  autonomName  The Autonom name to be assigned to this structure
	 */
	public void setAutonomName(String autonomName)
	{
		this.autonomName = autonomName;
	}


	/**
	 *  Assigns a Beilstein Registry Number to this Molecule
	 *
	 * @param  beilsteinRN  The Beilstein Registry Number to be assigned to this molecule
	 */
	public void setBeilsteinRN(String beilsteinRN)
	{
		this.beilsteinRN = beilsteinRN;
	}


	/**
	 *  Assigns a CAS Registry Number to this Molecule
	 *
	 * @param  casRN  The CAS Registry Number to be assinged to this Molecule
	 */
	public void setCasRN(String casRN)
	{
		this.casRN = casRN;
	}


	/**
	 *  Bulk method for assigning a bunch of chemical names to this molecule by overwriting the chemical names vector as a whole.
	 *
	 * @param  chemNames  A vector containing a bunch of chemical names to be assigned to this Molecule
	 */
	public void setChemNames(Vector chemNames)
	{
		this.chemNames = chemNames;
	}


	/**
	 *  Assigns a title to this Molecule
	 *
	 * @param  title  The title to be assigned to this Molecule
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}


	/**
	 *  Returns the AutonomName for this molecule. Autonom is a program that published by Beilstein, which uniquely names a chemical structure
	 *
	 * @return    The autoname name of this structure
	 */
	public String getAutonomName()
	{
		return this.autonomName;
	}



	/**
	 *  Returns the Beilstein Registry Number of this Molecule
	 *
	 * @return    The Beilstein Registry Number of this Molecule
	 */
	public String getBeilsteinRN()
	{
		return this.beilsteinRN;
	}



	/**
	 *  Returns the CAS Registry Number of this Molecule
	 *
	 * @return    The CAS Registry Number of this Molecule
	 */
	public String getCasRN()
	{
		return this.casRN;
	}


	/**
	 *  Returns the number of chemcial names stored for this Molecule
	 *
	 * @return    The number of chemical names stored for this Molecule
	 */
	public int getChemNamesCount()
	{
		return chemNames.size();
	}


	/**
	 *  Returns the Vector contains all the chemical names stored for this Molecule
	 *
	 * @return    The Vector contains all the chemical names stored for this Molecule
	 */
	public Vector getChemNames()
	{
		return this.chemNames;
	}


	/**
	 *  Return one of the chemical names of this molecule.
	 *
	 * @param  number  The position of the chemcial name to be returned
	 * @return         The chemical name of this molecule whose position in the chemical names vector is indicated by number
	 */
	public String getChemName(int number)
	{
		return (String) chemNames.elementAt(number);
	}


	/**
	 *  Returns the title of this Molecule
	 *
	 * @return    The title of this Molecule
	 */
	public String getTitle()
	{
		return this.title;
	}


	/**
	 *  Add a new chemcial name to the collection of chemical names in this molecule
	 *
	 * @param  chemName  The new chemical name to added to the collection of chemical names of this molecule
	 */
	public void addChemName(String chemName)
	{
		this.chemNames.addElement(chemName);
	}
	
	       /**
         * Clones this molecule object.
         *
         * @return  The cloned molecule object   
         */
        public Object clone()
        {
                Object o = null;
                try
                {
                        o = super.clone();
                }
                catch (Exception e)
                {
                        e.printStackTrace(System.err);
                }
                return o;
        }
}


