/*
 *  AtomTypeFactory.java
 *
 *  $RCSfile$    $Author$    $Date$    $Revision$
 *
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
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
 
package org.openscience.cdk.tools;

import java.util.*;
import java.io.*;
import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;


import org.xml.sax.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

import JSX.*;

/**
 *  Used to store and return data of a particular AtomType
 *
 * @author     steinbeck
 * @created    August 29, 2001
 */

public class AtomTypeFactory
{
	private Vector atomTypes = null;

	public static String ATOMTYPE_ID_STRUCTGEN = "structgen";
	// Just an example. We might want to be more explicit here, like "modelling.mm.charm" for the charm forcefield, or so. 
	public static String ATOMTYPE_ID_MODELLING = "modelling"; 

	public AtomTypeFactory() throws IOException, OptionalDataException, ClassNotFoundException
	{
		this("org/openscience/cdk/config/structgen_atomtypes.xml");
	}

	
	/**
	 *  Constructor for the AtomTypeFactory object
	 *
	 * @exception  IOException             A problem with reading the atomtypes.xml file
	 * @exception  OptionalDataException   Unexpected data appeared in the atomtype ObjectInputStream
	 * @exception  ClassNotFoundException  A problem instantiating the atomtypes
	 * @since
	 */
	public AtomTypeFactory(String configFile) throws IOException, OptionalDataException, ClassNotFoundException
	{
		InputStream ins = null;
		ObjIn in = null;
		try
		{
			ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
		}
		catch(Exception exc)
		{
			throw new IOException("There was a problem getting org.openscience.cdk.config.atomtypes.xml as a stream");
		}
		if (ins == null) throw new IOException("There was a problem getting org/openscience/cdk/config/atomtypes.xml as a stream");
		in = new ObjIn(ins);
		atomTypes = (Vector) in.readObject();
	}
	

	public int getSize()
	{
		return atomTypes.size();
	}

	/**
	 *  Get an AtomTyp
	 *
	 * @param  symbol  An id to search for
	 * @return         The AtomType for this id
	 */
	public AtomType getAtomType(String id) throws NoSuchAtomTypeException
	{
		AtomType atomType = null;
		for (int f = 0; f < atomTypes.size(); f++)
		{
			atomType = (AtomType) atomTypes.elementAt(f); 
			if (atomType.getID().equals(id))
			{
				return atomType;
			}
		}
		throw new NoSuchAtomTypeException("The AtomType " + id + " could not be found");
	}
	
	/**
	 *  Get an array of all atomTypes known to the AtomTypeFactory for the given element symbol and atomtype class
	 *
	 * @param  symbol  An element symbol to search for
	 * @return         An array of atomtypes that matches the given element symbol and atomtype class
	 */
	public AtomType[] getAtomTypes(String symbol, String id)
	{
		ArrayList al = new ArrayList();
		AtomType atomType = null;
		for (int f = 0; f < atomTypes.size(); f++)
		{
			if (((AtomType) atomTypes.elementAt(f)).getSymbol().equals(symbol) && ((AtomType) atomTypes.elementAt(f)).getID().indexOf(id) > -1)
			{
				al.add((AtomType) ((AtomType) atomTypes.elementAt(f)).clone());
			}
		}
		AtomType[] atomTypes = new AtomType[al.size()];
		al.toArray(atomTypes);
		return atomTypes;
	}
}
