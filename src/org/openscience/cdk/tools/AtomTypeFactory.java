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
 *  General class for defining AtomTypes. This class itself does not define the
 *  items types; for this classes implementing the AtomTypeConfiguration
 *  interface are used. <p>
 *
 *  To see which AtomTypeConfigurator's CDK provides, one should check the
 *  AtomTypeConfigurator API.
 *
 * @author     steinbeck
 * @created    2001-08-29
 * @keyword    atom, type
 * @see        AtomTypeConfigurator
 */
public class AtomTypeFactory
{

	/**
	 *  Used as an ID to describe the atom type
	 */
	public static String ATOMTYPE_ID_STRUCTGEN = "structgen";
	/**
	 *  Used as an ID to describe the atom type
	 */
	public static String ATOMTYPE_ID_MODELING = "modeling";
	// these are not available
	/**
	 *  Used as an ID to describe the atom type
	 */
	public static String ATOMTYPE_ID_JMOL = "jmol";

	private Vector atomTypes = null;
	private org.openscience.cdk.tools.LoggingTool logger;


	/**
	 *  Constructor for the AtomTypeFactory object
	 *
	 *@exception  IOException             Thrown if something goes wrong with reading the config
	 *@exception  OptionalDataException   What ever that may be
	 *@exception  ClassNotFoundException  Thrown if a class was not found :-)
	 */
	public AtomTypeFactory() throws IOException, OptionalDataException, ClassNotFoundException
	{
		this("org/openscience/cdk/config/structgen_atomtypes.xml");
	}


	/**
	 *  Constructor for the AtomTypeFactory object
	 *
	 *@param  configFile                  The file with atom type configs
	 *@exception  IOException             A problem with reading the atomtypes.xml
	 *      file
	 *@exception  OptionalDataException   Unexpected data appeared in the atomtype
	 *      ObjectInputStream
	 *@exception  ClassNotFoundException  A problem instantiating the atomtypes
	 */
	public AtomTypeFactory(String configFile)
			 throws IOException, OptionalDataException, ClassNotFoundException
	{
		logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
		readConfiguration(configFile);
	}


	/**
	 *  Read the config from a text file
	 *
	 *@param  configFile  name of the config file
	 */
	private void readConfiguration(String configFile)
	{
		logger.info("Reading config file from " + configFile);
		AtomTypeConfigurator atc = null;

		InputStream ins = null;
		{
			// try to see if this configFile is an actual file
			File f = new File(configFile);
            if (f.exists()) {
                logger.debug("configFile is a File");
                // what's next?
                try {
                    ins = new FileInputStream(f);
                } catch (Exception exc) {
                    logger.error(exc.toString());
                }
			} else {
			 
                logger.debug("configFile must be a stream");
                // assume it is a default config file in distro
                /*
                *  this has to be this.getClass.getClassLoader.getResource,
                *  getClass.getResource fails, elw
                */            
                ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
            }
            
			if (ins == null)
			{
				logger.error("There was a problem getting a stream for " +
						configFile);
			}
		}

		try
		{
			/*
			 *  This class loading mechanism is used to not depend on JSX,
			 *  which is needed for old JVM's like in older browsers.
			 */
			if (configFile.endsWith("txt"))
			{
				atc = (AtomTypeConfigurator) this.getClass().getClassLoader().
						loadClass("org.openscience.cdk.tools.TXTBasedAtomTypeConfigurator").
						newInstance();
			} else if (configFile.endsWith("xml"))
			{
				atc = (AtomTypeConfigurator) this.getClass().getClassLoader().
						loadClass("org.openscience.cdk.tools.JSXBasedAtomTypeConfigurator").
						newInstance();
			}
			logger.debug("Instantiated a AtomTypeConfigurator of class: " +
					atc.getClass().getName());
		} catch (Exception exc)
		{
			logger.error("Could not get instance of AtomTypeConfigurator for " + configFile);
		}
		if (atc != null)
		{
			atc.setInputStream(ins);
			try
			{
				atomTypes = atc.readAtomTypes();
			} catch (Exception exc)
			{
				logger.error("Could not read AtomType's from file due to: " + exc.toString());
			}
		} else
		{
			logger.debug("AtomTypeConfigurator was null!");
			atomTypes = new Vector();
		}
	}


	/**
	 *  Gets the size attribute of the AtomTypeFactory object
	 *
	 *@return    The size value
	 */
	public int getSize()
	{
		return atomTypes.size();
	}


	/**
	 *  Get an AtomTyp
	 *
	 *@param  id                           an ID for a particular atom type (like C$)
	 *@return                              The AtomType for this id
	 *@exception  NoSuchAtomTypeException  Thrown if the atom type does not exist.
	 */
	public AtomType getAtomType(String id) throws NoSuchAtomTypeException
	{
		AtomType atomType = null;
		for (int f = 0; f < atomTypes.size(); f++)
		{
			atomType = (AtomType) atomTypes.elementAt(f);
			if (atomType.getAtomTypeName().equals(id))
			{
				return atomType;
			}
		}
		throw new NoSuchAtomTypeException("The AtomType " + id + " could not be found");
	}


	/**
	 *  Get an array of all atomTypes known to the AtomTypeFactory for the given
	 *  element symbol and atomtype class
	 *
	 *@param  symbol  An element symbol to search for
	 *@param  id      The configuration file
	 *@return         An array of atomtypes that matches the given element symbol
	 *      and atomtype class
	 */
	public AtomType[] getAtomTypes(String symbol, String id)
	{
		//System.out.println("Request for atomtype " + id + " for symbol " + symbol);
		Vector al = new Vector();
		AtomType atomType = null;
		for (int f = 0; f < atomTypes.size(); f++)
		{
			AtomType at = (AtomType) atomTypes.elementAt(f);
			if (at.getSymbol().equals(symbol) && (at.getAtomTypeName().indexOf(id) > -1))
			{
				//System.out.println("Atomtype for symbol " + symbol + " found.");
				al.addElement((AtomType) at.clone());
			}
		}
		AtomType[] atomTypes = new AtomType[al.size()];
		al.copyInto(atomTypes);
		//System.out.println("Atomtype for symbol " + symbol + " looks like this: " + atomTypes[0]);
		return atomTypes;
	}


	/**
	 *  Gets the allAtomTypes attribute of the AtomTypeFactory object
	 *
	 *@return    The allAtomTypes value
	 */
	public org.openscience.cdk.AtomType[] getAllAtomTypes()
	{
		logger.debug("Returning list of size: " + getSize());
		Vector al = new Vector();
		AtomType atomType = null;
		for (int f = 0; f < atomTypes.size(); f++)
		{
			AtomType at = (AtomType) atomTypes.elementAt(f);
			al.addElement((AtomType) at.clone());
		}
		AtomType[] atomTypes = new AtomType[al.size()];
		al.copyInto(atomTypes);
		return atomTypes;
	}


	/**
	 *  Configures an atom. Finds the correct element type by looking at the atoms
	 *  atom type id (atom.getAtomTypeName()).
	 *
	 *@param  atom  The atom to be configured
	 *@return       The configured atom
	 */
	public Atom configure(Atom atom)
	{
		try
		{
			AtomType at = getAtomType(atom.getAtomTypeName());
			atom.setMaxBondOrder(at.getMaxBondOrder());
			atom.setMaxBondOrderSum(at.getMaxBondOrderSum());
			atom.setVanderwaalsRadius(at.getVanderwaalsRadius());
			atom.setCovalentRadius(at.getCovalentRadius());
			Object color = at.getProperty("org.openscience.jmol.color");
			if (color != null)
			{
				atom.setProperty("org.openscience.jmol.color", color);
			}
			if (at.getAtomicNumber() != 0)
			{
				atom.setAtomicNumber(at.getAtomicNumber());
			} else
			{
				logger.debug("Did not configure atomic number: AT.an=" + at.getAtomicNumber());
			}
			if (at.getExactMass() > 0.0)
			{
				atom.setExactMass(at.getExactMass());
			} else
			{
				logger.debug("Did not configure mass: AT.mass=" + at.getAtomicNumber());
			}
		} catch (Exception exc)
		{
			logger.warn("Could not configure atom with unknown ID: " +
					atom.toString() + " + (id=" + atom.getAtomTypeName() + ")");
		}
		logger.debug("Configured " + atom.toString());
		return atom;
	}
}

