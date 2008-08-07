/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2001-2007  Christoph Steinbeck <steinbeck@users.sf.net>
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.NoSuchAtomTypeException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
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
 *  AtomTypeFactory factory = AtomTypeFactory.getInstance(someChemObjectBuilder);
 *  </pre>
 *  For each atom type list a separate AtomTypeFactory is instantiated.
 *
 *  <p>To get all the atom types of an element from a specific list, this 
 *  code can be used:
 *  <pre>
 *  AtomTypeFactory factory = AtomTypeFactory.getInstance(
 *    "org/openscience/cdk/config/data/jmol_atomtypes.txt",
      someChemObjectBuilder
 *  );
 *  AtomType[] types = factory.getAtomTypes("C");
 *  </pre>
 *
 * @cdk.module core
 * @cdk.svnrev  $Revision$
 *
 * @author     steinbeck
 * @cdk.created    2001-08-29
 * @cdk.keyword    atom, type
 * @see        IAtomTypeConfigurator
 */
@TestClass("org.openscience.cdk.config.AtomTypeFactoryTest")
public class AtomTypeFactory {

    /**
     *  Used as an ID to describe the atom type.
     */
    public final static String ATOMTYPE_ID_STRUCTGEN = "structgen";
    /**
     *  Used as an ID to describe the atom type.
     */
    public final static String ATOMTYPE_ID_MODELING = "modeling";
    // these are not available
    /**
     *  Used as an ID to describe the atom type.
     */
    public final static String ATOMTYPE_ID_JMOL = "jmol";
    
    private final static String TXT_EXTENSION = "txt";
    private final static String XML_EXTENSION = "xml";
    private final static String OWL_EXTENSION = "owl";
    
    private static LoggingTool logger;
    private static Map<String, AtomTypeFactory> tables = null;
    private List<IAtomType> atomTypes = null;

	/**
	 * Private constructor for the AtomTypeFactory singleton.
	 *
	 *
	 */
    private AtomTypeFactory(String configFile, IChemObjectBuilder builder) {
        if (logger == null) {
            logger = new LoggingTool(this);
        }
        atomTypes = new ArrayList<IAtomType>(100);
        readConfiguration(configFile, builder);
    }

	/**
	 * Private constructor for the AtomTypeFactory singleton.
	 *
	 */
    private AtomTypeFactory(InputStream ins, String format, IChemObjectBuilder builder) {
        if (logger == null) {
            logger = new LoggingTool(this);
        }
        atomTypes = new ArrayList<IAtomType>(100);
        readConfiguration(ins, format, builder);
    }

    /**
     * Method to create a default AtomTypeFactory, using the given InputStream.
     * An AtomType of this kind is not cached.
     *
     * @see #getInstance(String, IChemObjectBuilder)
     * @param  ins                    InputStream containing the data
     * @param  format                 String representing the possible formats ('xml' and 'txt')
     * @param  builder                IChemObjectBuilder used to make IChemObject instances
     * @return                        The AtomTypeFactory for the given data file
     */
    @TestMethod("testGetInstance_InputStream_String_IChemObjectBuilder")
    public static AtomTypeFactory getInstance(InputStream ins, String format, IChemObjectBuilder builder) {
        return new AtomTypeFactory(ins, format, builder);
    }

    /**
     * Method to create a default AtomTypeFactory, using the structgen atom type list.
     *
     * @see #getInstance(String, IChemObjectBuilder)
     * @param  builder                IChemObjectBuilder used to make IChemObject instances
     * @return                        The AtomTypeFactory for the given data file
     */
    @TestMethod("testGetInstance_IChemObjectBuilder")
    public static AtomTypeFactory getInstance(IChemObjectBuilder builder) {
        return getInstance("org/openscience/cdk/config/data/structgen_atomtypes.xml", builder);
    }

    /**
     * Method to create a specialized AtomTypeFactory. Available lists in CDK are:
     * <ul>
     *  <li>org/openscience/cdk/config/data/jmol_atomtypes.txt
     *  <li>org/openscience/cdk/config/data/mol2_atomtypes.xml
     *  <li>org/openscience/cdk/config/data/structgen_atomtypes.xml
     *  <li>org/openscience/cdk/config/data/mm2_atomtypes.xml
     *  <li>org/openscience/cdk/config/data/mmff94_atomtypes.xml
     *  <li>org/openscience/cdk/dict/data/cdk-atom-types.owl
     *  <li>org/openscience/cdk/dict/data/sybyl-atom-types.owl
     * </ul>
     *
     * @param  configFile             String the name of the data file
     * @param  builder                IChemObjectBuilder used to make IChemObject instances
     * @return                        The AtomTypeFactory for the given data file
     */
    @TestMethod("testGetInstance_String_IChemObjectBuilder")
    public static AtomTypeFactory getInstance(String configFile, IChemObjectBuilder builder) {
        if (tables == null) {
            tables = new Hashtable<String, AtomTypeFactory>();
        }
        if (!(tables.containsKey(configFile))) {
            tables.put(configFile, new AtomTypeFactory(configFile, builder));
        }
        return tables.get(configFile);
    }

	/**
	 * Read the config from a text file.
	 *
	 * @param  fileName  name of the config file
     * @param  builder     IChemObjectBuilder used to make IChemObject instances
	 */
	private void readConfiguration(String fileName, IChemObjectBuilder builder)
	{
		logger.info("Reading config file from ", fileName);

		InputStream ins;
		{
			//try to see if this is a resource
			ins = this.getClass().getClassLoader().getResourceAsStream(fileName);
			if(ins==null){
				// try to see if this configFile is an actual file
				File file = new File(fileName);
	            if (file.exists()) {
	                logger.debug("configFile is a File");
	                // what's next?
	                try {
	                    ins = new FileInputStream(file);
	                } catch (Exception exception) {
	                    logger.error(exception.getMessage());
	                    logger.debug(exception);
	                }
	            } else {
	            	logger.error("no stream and no file");
	            }
            }
   		}

        String format = XML_EXTENSION;
        if (fileName.endsWith(TXT_EXTENSION)) {
            format = TXT_EXTENSION;
        } else if (fileName.endsWith(XML_EXTENSION)) {
            format = XML_EXTENSION;
        } else if (fileName.endsWith(OWL_EXTENSION)) {
            format = OWL_EXTENSION;
        }
        readConfiguration(ins, format, builder);
    }
    
    private IAtomTypeConfigurator constructConfigurator(String format) {
        try {
            if (format.equals(TXT_EXTENSION)) {
                return (IAtomTypeConfigurator) this.getClass().getClassLoader().
                    loadClass("org.openscience.cdk.config.TXTBasedAtomTypeConfigurator").
                    newInstance();
            } else if (format.equals(XML_EXTENSION)) {
                return (IAtomTypeConfigurator) this.getClass().getClassLoader().
                 loadClass("org.openscience.cdk.config.CDKBasedAtomTypeConfigurator").
                 newInstance();
            } else if (format.equals(OWL_EXTENSION)) {
                return (IAtomTypeConfigurator) this.getClass().getClassLoader().
                 loadClass("org.openscience.cdk.config.OWLBasedAtomTypeConfigurator").
                 newInstance();
            }
		} catch (Exception exc) {
			logger.error("Could not get instance of AtomTypeConfigurator for format ", format);
            logger.debug(exc);
		}
        return null;
    }
    
    private void readConfiguration(InputStream ins, String format, IChemObjectBuilder builder) {
    	IAtomTypeConfigurator atc = constructConfigurator(format);
		if (atc != null) {
			atc.setInputStream(ins);
			try
			{
				atomTypes = atc.readAtomTypes(builder);
			} catch (Exception exc)
			{
				logger.error("Could not read AtomType's from file due to: ", exc.getMessage());
                logger.debug(exc);
			}
		} else
		{
			logger.debug("AtomTypeConfigurator was null!");
			atomTypes = new ArrayList<IAtomType>();
		}
	}


	/**
	 * Returns the number of atom types in this list.
	 *
	 * @return    The number of atom types
	 */
    @TestMethod("testGetSize")
    public int getSize()
	{
		return atomTypes.size();
	}


	/**
	 * Get an AtomType with the given ID.
	 *
	 * @param  identifier                   an ID for a particular atom type (like C$)
	 * @return                              The AtomType for this id
	 * @exception  NoSuchAtomTypeException  Thrown if the atom type does not exist.
	 */
    @TestMethod("testGetAtomType_String,testGetAtomTypeFromJmol,testGetAtomTypeFromMM2,testGetAtomTypeFromPDB")
    public IAtomType getAtomType(String identifier) throws NoSuchAtomTypeException
	{
        for (IAtomType atomType : atomTypes) {
            if (atomType.getAtomTypeName().equals(identifier)) {
                return atomType;
            }
        }
        throw new NoSuchAtomTypeException("The AtomType " + identifier + " could not be found");
	}


	/**
	 * Get an array of all atomTypes known to the AtomTypeFactory for the given
	 * element symbol and atomtype class.
	 *
	 * @param  symbol  An element symbol to search for
	 * @return         An array of atomtypes that matches the given element symbol
	 *                 and atomtype class
	 */
    @TestMethod("testGetAtomTypes_String")
    public IAtomType[] getAtomTypes(String symbol)
	{
        logger.debug("Request for atomtype for symbol ", symbol);
        List<IAtomType> atomList = new ArrayList<IAtomType>();
        for (IAtomType atomType : atomTypes) {
            // logger.debug("  does symbol match for: ", atomType);
            if (atomType.getSymbol().equals(symbol)) {
                // logger.debug("Atom type found for symbol: ", atomType);
                IAtomType clone;
                try {
                    clone = (IAtomType) atomType.clone();
                    atomList.add(clone);
                } catch (CloneNotSupportedException e) {
                    logger.error("Could not clone IAtomType: ", e.getMessage());
                    logger.debug(e);
                }
            }
        }
        IAtomType[] atomTypes = (IAtomType[])atomList.toArray(new IAtomType[atomList.size()]);
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
    @TestMethod("testGetAllAtomTypes")
    public IAtomType[] getAllAtomTypes()
	{
		logger.debug("Returning list of size: ", getSize());
		List<IAtomType> atomtypeList = new ArrayList<IAtomType>();
        for (IAtomType atomType : atomTypes) {
            IAtomType clone;
            try {
                clone = (IAtomType) atomType.clone();
                atomtypeList.add(clone);
            } catch (CloneNotSupportedException e) {
                logger.error("Could not clone IAtomType: ", e.getMessage());
                logger.debug(e);
            }
        }
        return (IAtomType[])atomtypeList.toArray(new IAtomType[atomtypeList.size()]);
	}


	/**
	 * Configures an atom. Finds the correct element type by looking at the Atom's
	 * atom type name, and if that fails, picks the first atom type matching
     * the Atom's element symbol..
	 *
	 * @param  atom  The atom to be configured
	 * @return       The configured atom
	 * @throws       CDKException when it could not recognize and configure the 
	 *               IAtom
	 */
    @TestMethod("testConfigure_IAtom")
    public IAtom configure(IAtom atom) throws CDKException {
        if (atom instanceof IPseudoAtom) {
            // do not try to configure PseudoAtom's
            return atom;
        }
        try {
            IAtomType atomType;
            String atomTypeName = atom.getAtomTypeName();
            if (atomTypeName == null || atomTypeName.length() == 0) {
                logger.debug("Using atom symbol because atom type name is empty...");
                IAtomType[] types = getAtomTypes(atom.getSymbol());
                if (types.length > 0) {
                    logger.warn("Taking first atom type, but other may exist");
                    atomType = types[0];
                } else {
                    String message = "Could not configure atom with unknown ID: " +
                        atom.toString() + " + (id=" + atom.getAtomTypeName() + ")";
                    logger.warn(message);
                    throw new CDKException(message);
                }
            } else {
                atomType = getAtomType(atom.getAtomTypeName());
            }
            logger.debug("Configuring with atomtype: ", atomType);
            atom.setSymbol(atomType.getSymbol());
            atom.setMaxBondOrder(atomType.getMaxBondOrder());
            atom.setBondOrderSum(atomType.getBondOrderSum());
            atom.setCovalentRadius(atomType.getCovalentRadius());
            atom.setHybridization(atomType.getHybridization());
            Object color = atomType.getProperty("org.openscience.cdk.renderer.color");
            if (color != null) {
                atom.setProperty("org.openscience.cdk.renderer.color", color);
            }
            atom.setAtomicNumber(atomType.getAtomicNumber());
            atom.setExactMass(atomType.getExactMass());
        } catch (Exception exception) {
            logger.warn("Could not configure atom with unknown ID: ", atom,
                        " + (id=", atom.getAtomTypeName(), ")");
            logger.debug(exception);
            throw new CDKException(exception.toString(), exception);
        }
        logger.debug("Configured: ", atom);
        return atom;
    }
}

