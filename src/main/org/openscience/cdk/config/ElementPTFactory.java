/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2005-2007  Miguel Rojas <miguelrojas@users.sf.net>
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
import java.io.InputStreamReader;
import java.io.OptionalDataException;
import java.util.List;

import org.openscience.cdk.PeriodicTableElement;
import org.openscience.cdk.config.elements.ElementPTReader;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Used to store and return data of a particular chemicalElement. As this class is a
 * singleton class, one gets an instance with: 
 * <pre>
 *   ElementPTFactory efac = ElementPTFactory.getInstance();
 * </pre>
 *
 * @author     	   Miguel Rojas
 * @cdk.created    May 8, 2005
 * @cdk.module     extra
 * @cdk.svnrev  $Revision$
 */
public class ElementPTFactory
{

	private static ElementPTFactory efac = null;
	private List<PeriodicTableElement> elements = null;
	private boolean debug = false;
	private LoggingTool logger;

	/**
	 * Private constructor for the ElementPTFactory object.
	 *
	 *@exception  IOException  A problem with reading the chemicalElements.xml file
	 *@exception  OptionalDataException   Unexpected data appeared in the isotope ObjectInputStream
	 *@exception  ClassNotFoundException  A problem instantiating the isotopes
	 */
	private ElementPTFactory() throws IOException, OptionalDataException,ClassNotFoundException
	{
		logger = new LoggingTool(this);
		logger.info("Creating new ElementPTFactory");

		InputStream ins = null;
		String errorMessage = "There was a problem getting org.openscience.cdk." +
                              "config.chemicalElements.xml as a stream";
		try {
			String configFile = "org/openscience/cdk/config/data/chemicalElements.xml";
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
		ElementPTReader reader = new ElementPTReader(new InputStreamReader(ins));
		elements = reader.readElements();
		if (debug) logger.debug("Found #elements in file: ", elements.size());
		
	}

	/**
	 *  Returns an ElementPTFactory instance.
	 *
	 *@return                             The instance value
	 *@exception  IOException             Description of the Exception
	 *@exception  OptionalDataException   Description of the Exception
	 *@exception  ClassNotFoundException  Description of the Exception
	 */
	public static ElementPTFactory getInstance() throws IOException, OptionalDataException, ClassNotFoundException
	{
		if (efac == null) 
		{
			efac = new ElementPTFactory();
		}
		return efac;
	}


	/**
	 *  Returns the number of elements defined by this class.
	 *
	 *@return    The size value
	 */
	public int getSize()
	{
		return elements.size();
	}

    /**
     * Get all the elements loaded by the factory.
     *
     * @return  A Vector of PeriodicTableElement objects
     * @see org.openscience.cdk.PeriodicTableElement
     */
    public List<PeriodicTableElement> getElements() {
        return elements;
    }

    /**
	 * Returns an Element with a given element symbol.
	 *
	 *@param  symbol  An element symbol to search for
	 *@return         An array of element that matches the given element symbol
	 */
	public PeriodicTableElement getElement(String symbol) {
        for (PeriodicTableElement element : elements) {
            if (element.getSymbol().equals(symbol)) {
                try {
                    return (PeriodicTableElement) element.clone();
                } catch (CloneNotSupportedException e) {
                    logger.error("Could not clone PeriodicTableElement: ", e.getMessage());
					logger.debug(e);
                }
            }
        }
        return null;
	}

	/**
	* Checks whether the given element exists.
	*
	* @param  elementName   The element name to test
	* @return               True is the element exists, false otherwise
	*/
	public boolean isElement(String elementName) 
	{
		return (getElement(elementName) != null);
	}

	/**
	 *  Configures an element. Finds the correct element type
	 *  by looking at the element symbol.
	 *
	 *@param  element     The element to be configure
	 *@return             The configured atom
	 */
	public PeriodicTableElement configure(PeriodicTableElement element)
	{
		PeriodicTableElement elementInt = getElement(element.getSymbol());
		
		element.setSymbol(elementInt.getSymbol());
		element.setAtomicNumber(elementInt.getAtomicNumber());
		element.setName(elementInt.getName());
		element.setChemicalSerie(elementInt.getChemicalSerie());
		element.setPeriod(elementInt.getPeriod());
		element.setGroup(elementInt.getGroup());
		element.setPhase(elementInt.getPhase());
		element.setCASid(elementInt.getCASid());
		return element;
	}


}

