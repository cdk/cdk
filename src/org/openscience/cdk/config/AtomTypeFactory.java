/*  $RCSfile$
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
package org.openscience.cdk.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OptionalDataException;
import java.util.Hashtable;
import java.util.Vector;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.NoSuchAtomTypeException;
import org.openscience.cdk.tools.LoggingTool;

/**
 *  General class for defining AtomTypes. This class itself does not define the
 *  items types; for this classes implementing the AtomTypeConfiguration
 *  interface are used.
 *
 *  <p>To see which AtomTypeConfigurator's CDK provides, one should check the
 *  AtomTypeConfigurator API.
 *
 *  <p>The AtomTypeFactory is a singleton class, which means that there exists
 *  only one instance of the class. Well, almost. For each atom type table,
 *  there is one AtomTypeFactory instance. An instance of this class is
 *  obtained with:
 *  <pre>
 *  AtomTypeFactory factory = AtomTypeFactory.getInstance();
 *  </pre>
 *  For each atom type list a separate AtomTypeFactory is instantiated.
 *
 *  <p>To get all the atom types of an element from a specific list, this 
 *  code can be used:
 *  <pre>
 *  AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/jmol_atomtypes.txt");
 *  AtomType[] types = factory.getAtomTypes("C");
 *  </pre>
 *
 * @cdk.module standard
 *
 * @author     steinbeck
 * @cdk.created    2001-08-29
 * @cdk.keyword    atom, type
 * @see        AtomTypeConfigurator
 */
public class AtomTypeFactory {

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
    
    private static LoggingTool logger;

    private static Hashtable tables = null;
    
    private Vector atomTypes = null;
    
	/**
	 * Private constructor for the AtomTypeFactory singleton.
	 *
	 * @exception  IOException             Thrown if something goes wrong with reading the config
	 * @exception  OptionalDataException   What ever that may be
	 * @exception  ClassNotFoundException  Thrown if a class was not found :-)
	 */
    private AtomTypeFactory(String configFile) throws IOException, OptionalDataException, ClassNotFoundException {
        if (logger == null) {
            logger = new LoggingTool(this);
        }
        atomTypes = new Vector(30);
        readConfiguration(configFile);
    }

	/**
	 * Private constructor for the AtomTypeFactory singleton.
	 *
	 * @exception  IOException             Thrown if something goes wrong with reading the config
	 * @exception  OptionalDataException   What ever that may be
	 * @exception  ClassNotFoundException  Thrown if a class was not found :-)
	 */
    private AtomTypeFactory(InputStream ins, String format) throws IOException, OptionalDataException, ClassNotFoundException {
        if (logger == null) {
            logger = new LoggingTool(this);
        }
        atomTypes = new Vector(30);
        readConfiguration(ins, format);
    }

    /**
     * Method to create a default AtomTypeFactory, using the structgen atom type list.
     * An AtomType of this kind is not cached.
     *
     * @see #getInstance(String)
     */
    public static AtomTypeFactory getInstance(InputStream ins, String format) throws IOException, OptionalDataException, ClassNotFoundException {
        return new AtomTypeFactory(ins, format);
    }

    /**
     * Method to create a default AtomTypeFactory, using the structgen atom type list.
     *
     * @see #getInstance(String)
     */
    public static AtomTypeFactory getInstance() throws IOException, OptionalDataException, ClassNotFoundException {
        return getInstance("org/openscience/cdk/config/data/structgen_atomtypes.xml");
    }

    /**
     * Method to create a specialized AtomTypeFactory. Available lists in CDK are:
     * <ul>
     *  <li>org/openscience/cdk/config/data/jmol_atomtypes.txt
     *  <li>org/openscience/cdk/config/data/mol2_atomtypes.xml
     *  <li>org/openscience/cdk/config/data/structgen_atomtypes.xml
     *  <li>org/openscience/cdk/config/data/valency_atomtypes.xml
     * </ul>
     */
    public static AtomTypeFactory getInstance(String configFile) throws IOException, OptionalDataException, ClassNotFoundException {
        if (tables == null) {
            tables = new Hashtable();
        }
        if (!(tables.containsKey(configFile))) {
            tables.put(configFile, new AtomTypeFactory(configFile));
        }
        return (AtomTypeFactory)tables.get(configFile);
    }

	/**
	 * Read the config from a text file.
	 *
	 * @param  configFile  name of the config file
	 */
	private void readConfiguration(String configFile)
	{
		logger.info("Reading config file from ", configFile);
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
				logger.error("There was a problem getting a stream for ",
						configFile);
			}
		}

        String format = "xml";
        if (configFile.endsWith("txt")) {
            format = "txt";
        } else if (configFile.endsWith("xml")) {
            format = "xml";
        }
        readConfiguration(ins, format);
    }
    
    private AtomTypeConfigurator constructConfigurator(String format) {
        try {
            if (format.equals("txt")) {
                return (AtomTypeConfigurator) this.getClass().getClassLoader().
                    loadClass("org.openscience.cdk.config.TXTBasedAtomTypeConfigurator").
                    newInstance();
            } else if (format.equals("xml")) {
                return (AtomTypeConfigurator) this.getClass().getClassLoader().
                 loadClass("org.openscience.cdk.config.CDKBasedAtomTypeConfigurator").
                 newInstance();
            }
		} catch (Exception exc) {
			logger.error("Could not get instance of AtomTypeConfigurator for format ", format);
            logger.debug(exc);
		}
        return null;
    }
    
    private void readConfiguration(InputStream ins, String format) {
        AtomTypeConfigurator atc = constructConfigurator(format);
		if (atc != null) {
			atc.setInputStream(ins);
			try
			{
				atomTypes = atc.readAtomTypes();
			} catch (Exception exc)
			{
				logger.error("Could not read AtomType's from file due to: ", exc.getMessage());
                logger.debug(exc);
			}
		} else
		{
			logger.debug("AtomTypeConfigurator was null!");
			atomTypes = new Vector();
		}
	}


	/**
	 * Returns the number of atom types in this list.
	 *
	 * @return    The number of atom types
	 */
	public int getSize()
	{
		return atomTypes.size();
	}


	/**
	 * Get an AtomType with the given ID.
	 *
	 * @param  id                           an ID for a particular atom type (like C$)
	 * @return                              The AtomType for this id
	 * @exception  NoSuchAtomTypeException  Thrown if the atom type does not exist.
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
	 * Get an array of all atomTypes known to the AtomTypeFactory for the given
	 * element symbol and atomtype class.
	 *
	 * @param  symbol  An element symbol to search for
	 * @return         An array of atomtypes that matches the given element symbol
	 *                 and atomtype class
	 */
	public AtomType[] getAtomTypes(String symbol)
	{
        logger.debug("Request for atomtype for symbol ", symbol);
        Vector al = new Vector();
        AtomType atomType = null;
        for (int f = 0; f < atomTypes.size(); f++)
        {
            AtomType at = (AtomType) atomTypes.elementAt(f);
            if (at.getSymbol().equals(symbol)) {
                logger.debug("Atom type found for symbol: ", symbol);
                al.addElement((AtomType) at.clone());
            }
        }
        AtomType[] atomTypes = new AtomType[al.size()];
        al.copyInto(atomTypes);
        if (atomTypes.length > 0)
            logger.debug("Atomtype for symbol ", symbol, " has this number of types: " + atomTypes.length);
        else
            logger.debug("No atomtype for symbol ", symbol);
        return atomTypes;
	}


	/**
	 * Gets the allAtomTypes attribute of the AtomTypeFactory object.
	 *
	 * @return    The allAtomTypes value
	 */
	public org.openscience.cdk.AtomType[] getAllAtomTypes()
	{
		logger.debug("Returning list of size: ", getSize());
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
	 * Configures an atom. Finds the correct element type by looking at the Atom's
	 * atom type name, and if that fails, picks the first atom type matching
     * the Atom's element symbol..
	 *
	 * @param  atom  The atom to be configured
	 * @return       The configured atom
	 */
    public Atom configure(Atom atom) throws CDKException {
        if (atom instanceof PseudoAtom) {
            // do not try to configure PseudoAtom's
            return atom;
        }
        try {
            AtomType at = null;
            String atomTypeName = atom.getAtomTypeName();
            if (atomTypeName == null || atomTypeName.length() == 0) {
                logger.debug("Using atom symbol because atom type name is empty...");
                AtomType[] types = getAtomTypes(atom.getSymbol());
                if (types.length > 0) {
                    logger.warn("Taking first atom type, but other may exist");
                    at = types[0];
                } else {
                    String message = "Could not configure atom with unknown ID: " +
                        atom.toString() + " + (id=" + atom.getAtomTypeName() + ")";
                    logger.warn(message);
                    throw new CDKException(message);
                }
            } else {
                at = getAtomType(atom.getAtomTypeName());
            }
            logger.debug("Configuring with atomtype: ", at);
            atom.setSymbol(at.getSymbol());
            atom.setMaxBondOrder(at.getMaxBondOrder());
            atom.setBondOrderSum(at.getBondOrderSum());
            atom.setVanderwaalsRadius(at.getVanderwaalsRadius());
            atom.setCovalentRadius(at.getCovalentRadius());
            atom.setHybridization(at.getHybridization());
            Object color = at.getProperty("org.openscience.cdk.renderer.color");
            if (color != null) {
                atom.setProperty("org.openscience.cdk.renderer.color", color);
            }
            if (at.getAtomicNumber() != 0) {
                atom.setAtomicNumber(at.getAtomicNumber());
            } else {
                logger.debug("Did not configure atomic number: AT.an=", at.getAtomicNumber());
            }
            if (at.getExactMass() > 0.0) {
                atom.setExactMass(at.getExactMass());
            } else {
                logger.debug("Did not configure mass: AT.mass=", at.getAtomicNumber());
            }
        } catch (Exception exception) {
            logger.warn("Could not configure atom with unknown ID: ", atom,
                        " + (id=", atom.getAtomTypeName(), ")");
            logger.debug(exception);
            throw new CDKException(exception.toString());
        }
        logger.debug("Configured: ", atom);
        return atom;
    }
}

