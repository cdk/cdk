/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.isotopes.IsotopeReader;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Used to store and return data of a particular isotope. As this class is a
 * singleton class, one gets an instance with: 
 * <pre>
 * IsotopeFactory ifac = IsotopFactory.getInstance(new IChemObject().getNewBuilder());
 * </pre>
 *
 * <p>Data about the isotopes are read from the file
 * org.openscience.cdk.config.isotopes.xml in the cdk-standard
 * module. Part of the data in this file was collected from
 * the website <a href="http://www.webelements.org">webelements.org</a>.
 *
 * <p>The use of this class is exemplified as follows. To get information 
 * about the major isotope of hydrogen, one can use this code:
 * <pre>
 *   IsotopeFactory factory = IsotopeFactory.getInstance(DefaultChemObjectBuilder.getInstance());
 *   Isotope major = factory.getMajorIsotope("H");
 * </pre> 
 *
 * @cdk.module core
 * @cdk.githash
 *
 * @author     steinbeck
 * @cdk.created    2001-08-29
 * @cdk.keyword    isotope
 * @cdk.keyword    element
 */
@TestClass("org.openscience.cdk.config.IsotopeFactoryTest")
public class IsotopeFactory
{

	private static IsotopeFactory ifac = null;
	private List<IIsotope> isotopes = null;
    private HashMap<String, IIsotope> majorIsotopes = null;
    private boolean debug = false;
    private static ILoggingTool logger =
        LoggingToolFactory.createLoggingTool(IsotopeFactory.class);

    /**
     * Private constructor for the IsotopeFactory object.
     *
     *@exception IOException             A problem with reading the isotopes.xml
     *      file
     * @param builder The builder from which we the factory will be generated
     */
	private IsotopeFactory(IChemObjectBuilder builder) throws IOException {
        logger.info("Creating new IsotopeFactory");

        InputStream ins;
        // ObjIn in = null;
        String errorMessage = "There was a problem getting org.openscience.cdk." +
                              "config.isotopes.xml as a stream";
        try {
            String configFile = "org/openscience/cdk/config/data/isotopes.xml";
            if (debug) logger.debug("Getting stream for ", configFile);
            ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
        } catch (Exception exception) {
            logger.error(errorMessage);
            logger.debug(exception);
            throw new IOException(errorMessage);
        }
        if (ins == null) {
            logger.error(errorMessage);
            throw new IOException(errorMessage);
        }
        IsotopeReader reader = new IsotopeReader(ins, builder);
        //in = new ObjIn(ins, new Config().aliasID(false));
        isotopes = reader.readIsotopes();
        if (debug) logger.debug("Found #isotopes in file: ", isotopes.size());
        /* for (int f = 0; f < isotopes.size(); f++) {
              Isotope isotope = (Isotope)isotopes.elementAt(f);
          } What's this loop for?? */

        majorIsotopes = new HashMap<String, IIsotope>();
    }


	/**
	 * Returns an IsotopeFactory instance.
	 *
         * @param      builder                 ChemObjectBuilder used to construct the Isotope's
	 * @return                             The instance value
	 * @exception  IOException  if isotopic data files could not be read.
	 */
    @TestMethod("testGetInstance_IChemObjectBuilder")
    public static IsotopeFactory getInstance(IChemObjectBuilder builder)
			 throws IOException {
        if (ifac == null) {
            ifac = new IsotopeFactory(builder);
        }
        return ifac;
    }


	/**
	 *  Returns the number of isotopes defined by this class.
	 *
	 *@return    The size value
	 */
    @TestMethod("testGetSize")
    public int getSize()
	{
		return isotopes.size();
	}

	/**
	 * Gets an array of all isotopes known to the IsotopeFactory for the given
	 * element symbol.
	 *
	 *@param  symbol  An element symbol to search for
	 *@return         An array of isotopes that matches the given element symbol
	 */
    @TestMethod("testGetIsotopes_String")
    public IIsotope[] getIsotopes(String symbol) {
        ArrayList<IIsotope> list = new ArrayList<IIsotope>();
        for (IIsotope isotope : isotopes) {
            if (isotope.getSymbol().equals(symbol)) {
                try {
                    IIsotope clone = (IIsotope) isotope.clone();
                    list.add(clone);
                } catch (CloneNotSupportedException e) {
                    logger.error("Could not clone IIsotope: ", e.getMessage());
                    logger.debug(e);
                }
            }
        }
        return list.toArray(new IIsotope[list.size()]);
    }

    /**
	 * Gets a array of all isotopes known to the IsotopeFactory.
	 *
	 * @return         An array of all isotopes
	 */
    @TestMethod("testGetIsotopes")
    public IIsotope[] getIsotopes() {
    	ArrayList<IIsotope> list = new ArrayList<IIsotope>();
    	for (IIsotope isotope : isotopes) {
            try {
                IIsotope clone = (IIsotope) isotope.clone();
                list.add(clone);
            } catch (CloneNotSupportedException e) {
                logger.error("Could not clone IIsotope: ", e.getMessage());
                logger.debug(e);
            }
    	}
    	return list.toArray(new IIsotope[list.size()]);
    }
    
    /**
	 * Gets an array of all isotopes matching the searched exact mass within
	 * a certain difference.
	 *
	 * @param  exactMass  search mass
	 * @param  difference mass the isotope is allowed to differ from the search mass
	 * @return            An array of all isotopes
	 */
    @TestMethod("testGetIsotopes_double_double")
    public IIsotope[] getIsotopes(double exactMass, double difference) {
    	ArrayList<IIsotope> list = new ArrayList<IIsotope>();
    	for (IIsotope isotope : isotopes) {
    		if (Math.abs(isotope.getExactMass() - exactMass) <= difference) {
    			try {
    				IIsotope clone = (IIsotope) isotope.clone();
    				list.add(clone);
    			} catch (CloneNotSupportedException e) {
    				logger.error("Could not clone IIsotope: ", e.getMessage());
    				logger.debug(e);
    			}
    		}
    	}
    	return list.toArray(new IIsotope[list.size()]);
    }
    
    /**
     * Get isotope based on element symbol and mass number.
     *
     * @param symbol the element symbol
     * @param massNumber the mass number
     * @return the corresponding isotope
     */
    @TestMethod("testGetIsotope")
    public IIsotope getIsotope(String symbol, int massNumber) {
        IIsotope ret = null;
        for (IIsotope isotope : isotopes) {
            if (isotope.getSymbol().equals(symbol) && isotope.getMassNumber() == massNumber) {
                try {
                    ret = (IIsotope) isotope.clone();
                } catch (CloneNotSupportedException e) {
                    logger.error("Could not clone IIsotope: ", e.getMessage());
                    logger.debug(e);
                }
                return ret;
            }
        }
        return null;
    }

    /**
	 * Returns the most abundant (major) isotope with a given atomic number.
     *
     * <p>The isotope's abundancy is for atoms with atomic number 60 and smaller
     * defined as a number that is proportional to the 100 of the most abundant
     * isotope. For atoms with higher atomic numbers, the abundancy is defined
     * as a percentage.
	 *
	 * @param  atomicNumber  The atomicNumber for which an isotope is to be returned
	 * @return               The isotope corresponding to the given atomic number
     *
     * @see #getMajorIsotope(String symbol)
     */
    @TestMethod("testGetMajorIsotope_int")
    public IIsotope getMajorIsotope(int atomicNumber) {
        IIsotope major = null;
        for (IIsotope isotope : isotopes) {
            if (isotope.getAtomicNumber() == atomicNumber) {
                try {
                    if (major == null) {
                        major = (IIsotope) isotope.clone();
                    } else {
                        if (isotope.getNaturalAbundance() > major.getNaturalAbundance()) {
                            major = (IIsotope) isotope.clone();
                        }
                    }
                } catch (CloneNotSupportedException e) {
                    logger.error("Could not clone IIsotope: ", e.getMessage());
                    logger.debug(e);
                }
            }
        }
        if (major == null) logger.error("Could not find major isotope for: ", atomicNumber);
        return major;
    }

    /**
     * Checks whether the given element exists.
     *
     * @param  elementName   The element name to test
     * @return               True is the element exists, false otherwise
     */
    @TestMethod("testIsElement_String")
    public boolean isElement(String elementName) {
        return (getElement(elementName) != null);
    }
    
    /**
     *  Returns the most abundant (major) isotope whose symbol equals element.
     *
     *@param  symbol  the symbol of the element in question
     *@return         The Major Isotope value
     */
    @TestMethod("testGetMajorIsotope_String")
    public IIsotope getMajorIsotope(String symbol) {
        IIsotope major = null;
        if (majorIsotopes.containsKey(symbol)) {
            major = majorIsotopes.get(symbol);
        } else {
            for (IIsotope isotope : isotopes) {
                if (isotope.getSymbol().equals(symbol)) {
                    try {
                        if (major == null) {
                            major = (IIsotope) isotope.clone();
                        } else {
                            if (isotope.getNaturalAbundance() > major.getNaturalAbundance()) {
                                major = (IIsotope) isotope.clone();
                            }
                        }
                    } catch (CloneNotSupportedException e) {
                        logger.error("Could not clone IIsotope: ", e.getMessage());
                        logger.debug(e);
                    }
                }
            }
            if (major == null) {
                logger.error("Could not find major isotope for: ", symbol);
            } else {
                majorIsotopes.put(symbol, major);
            }
        }
        return major;
    }

	/**
	 *  Returns an Element with a given element symbol.
	 *
	 *@param  symbol  The element symbol for the requested element
	 *@return         The configured element
	 */
    @TestMethod("testGetElement_String")
    public IElement getElement(String symbol)
	{
        return getMajorIsotope(symbol);
	}


	/**
	 *  Returns an element according to a given atomic number.
	 *
	 *@param  atomicNumber  The elements atomic number
	 *@return               The Element
	 */
    @TestMethod("testGetElement_int")
    public IElement getElement(int atomicNumber)
	{
        return getMajorIsotope(atomicNumber);
	}

    /**
     * Returns the symbol matching the element with the given atomic number.
     *
     * @param  atomicNumber  The elements atomic number
     * @return               The symbol of the Element
     */
    @TestMethod("testGetElementSymbol_int")
    public String getElementSymbol(int atomicNumber) {
        IIsotope isotope = getMajorIsotope(atomicNumber);
        return isotope.getSymbol();
    }

	/**
	 * Configures an atom. Finds the correct element type
	 * by looking at the atoms element symbol. If the element symbol is not recognized, it will
	 * throw an {@link IllegalArgumentException}.
	 *
	 * @param  atom  The atom to be configured
	 * @return       The configured atom
	 */
    @TestMethod("testConfigure_IAtom")
    public IAtom configure(IAtom atom)
	{
		IIsotope isotope;

        if (atom.getMassNumber() == null) isotope = getMajorIsotope(atom.getSymbol());
        else isotope = getIsotope(atom.getSymbol(), atom.getMassNumber());

        if (isotope == null)
        	throw new IllegalArgumentException("Cannot configure an unrecognized element: " + atom);
		return configure(atom, isotope);
	}


	/**
	 *  Configures an atom to have all the data of the
	 *  given isotope.
	 *
	 *@param  atom     The atom to be configure
	 *@param  isotope  The isotope to read the data from
	 *@return          The configured atom
	 */
    @TestMethod("testConfigure_IAtom_IIsotope")
    public IAtom configure(IAtom atom, IIsotope isotope)
	{
		atom.setMassNumber(isotope.getMassNumber());
		atom.setSymbol(isotope.getSymbol());
		atom.setExactMass(isotope.getExactMass());
		atom.setAtomicNumber(isotope.getAtomicNumber());
		atom.setNaturalAbundance(isotope.getNaturalAbundance());
		return atom;
	}


	/**
	 *  Configures atoms in an AtomContainer to 
	 *  carry all the correct data according to their element type.
	 *
	 *@param  container  The AtomContainer to be configured
	 */
    @TestMethod("testConfigureAtoms_IAtomContainer")
    public void configureAtoms(IAtomContainer container)
	{
		for (int f = 0; f < container.getAtomCount(); f++)
		{
			configure(container.getAtom(f));
		}
	}
    /**
	 *  Gets the natural mass of this element, defined as average of masses of isotopes, 
	 *  weighted by abundance.
	 *
	 * @param  element                     the element in question
	 * @return                             The natural mass value
	 */
    @TestMethod("testGetNaturalMass_IElement")
	public double getNaturalMass(IElement element){
		IIsotope[] isotopes = getIsotopes(element.getSymbol());
		double summedAbundances = 0;
		double summedWeightedAbundances = 0;
		double getNaturalMass = 0;
		for (int i = 0; i < isotopes.length; i++) {
			summedAbundances += isotopes[i].getNaturalAbundance();
			summedWeightedAbundances += isotopes[i].getNaturalAbundance() * isotopes[i].getExactMass();
			getNaturalMass = summedWeightedAbundances / summedAbundances;
		}
		return getNaturalMass;
	}

}

