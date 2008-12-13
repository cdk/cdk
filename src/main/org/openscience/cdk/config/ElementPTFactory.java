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

import org.openscience.cdk.PeriodicTableElement;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.elements.ElementPTReader;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.tools.LoggingTool;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

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
@TestClass("org.openscience.cdk.config.ElementPTFactoryTest")
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
	 */
	private ElementPTFactory() throws IOException
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
	 */
    @TestMethod("testGetInstance")
	public static ElementPTFactory getInstance() throws IOException
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
    @TestMethod("testGetSize")
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
    @TestMethod("testGetElements")
    public List<PeriodicTableElement> getElements() {
        return elements;
    }

    /**
	 * Returns an Element with a given element symbol.
	 *
	 *@param  symbol  An element symbol to search for
	 *@return         An array of element that matches the given element symbol
	 */
    @TestMethod("testGetElement_String")
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
	 *  Configures a PeriodicTableElement. Finds the correct element type
	 *  by looking at the element symbol.
	 *
	 *@param  element     The PeriodicTableElement to be configure
	 *@return             The configured PeriodicTableElement
     * @throws org.openscience.cdk.exception.CDKException if there is an error during configuration
     * (such as invalid IUPAC group number)
	 */
    @TestMethod("testConfigure_PeriodicTableElement")
	public PeriodicTableElement configure(PeriodicTableElement element) throws CDKException {
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
	/**
	 *  Configures a IElement given a PeridicTableElement. 
	 *
	 *@param  elementPT   The element of the Periodic Table to be configure
	 *@return element     The configured element
	 */
    @TestMethod("testConfigureE_PeriodicTableElement")
	public IElement configureE(PeriodicTableElement elementPT)
	{
		IElement element = elementPT.getBuilder().newElement(elementPT.getSymbol());
		element.setSymbol(elementPT.getSymbol());
		element.setAtomicNumber(elementPT.getAtomicNumber());
		return element;
	}
	
	/**
	 *  Gets the atomic number of this element in the periodic table.
	 *
	 * @param  element                     The PeriodicTableElement object
	 * @return                             The atomic number value
	 */
    @TestMethod("testGetAtomicNumber_PeriodicTableElement")
	public double getAtomicNumber(PeriodicTableElement element){
    	PeriodicTableElement elementInt = getElement(element.getSymbol());
		return elementInt.getAtomicNumber();
	}

    /**
	 *  Gets the name of this element in the periodic table.
	 *
	 * @param  element                     The PeriodicTableElement object
	 * @return                             The name value of this element
	 */
    @TestMethod("testGetName_PeriodicTableElement")
	public String getName(PeriodicTableElement element){
    	PeriodicTableElement elementInt = getElement(element.getSymbol());
		return elementInt.getName();
	}

    /**
	 *  Gets the chemical serie of this element in the periodic table.
	 *
	 * @param  element                     The PeriodicTableElement object
	 * @return                             The chemical serie value of this element
	 */
    @TestMethod("testGetChemicalSerie_PeriodicTableElement")
	public String getChemicalSerie(PeriodicTableElement element){
    	PeriodicTableElement elementInt = getElement(element.getSymbol());
		return elementInt.getChemicalSerie();
	}
    
    /**
	 *  Gets the period of this element in the periodic table.
	 *
	 * @param  element                     The PeriodicTableElement object
	 * @return                             The period value of this element
	 */
    @TestMethod("testGetPeriod_PeriodicTableElement")
	public int getPeriod(PeriodicTableElement element){
    	PeriodicTableElement elementInt = getElement(element.getSymbol());
		return elementInt.getPeriod();
	}
    
    /**
	 *  Gets the group of this element in the periodic table.
	 *
	 * @param  element                     The PeriodicTableElement object
	 * @return                             The group value of this element
	 */
    @TestMethod("testGetGroup_PeriodicTableElement")
	public int getGroup(PeriodicTableElement element){
    	PeriodicTableElement elementInt = getElement(element.getSymbol());
		return elementInt.getGroup();
	}

    /**
	 *  Gets the phase of this element in the periodic table.
	 *
	 * @param  element                     The PeriodicTableElement object
	 * @return                             The phase value of this element
	 */
    @TestMethod("testGetPhase_PeriodicTableElement")
	public String getPhase(PeriodicTableElement element){
    	PeriodicTableElement elementInt = getElement(element.getSymbol());
		return elementInt.getPhase();
	}

    /**
	 *  Gets the CAS id of this element in the periodic table.
	 *
	 * @param  element                     The PeriodicTableElement object
	 * @return                             The CASE id value of this element
	 */
    @TestMethod("testGetCASid_PeriodicTableElement")
	public String getCASid(PeriodicTableElement element){
    	PeriodicTableElement elementInt = getElement(element.getSymbol());
		return elementInt.getCASid();
	}

    /**
	 *  Gets the Vdw radios of this element in the periodic table.
	 *
	 * @param  element                     The PeriodicTableElement object
	 * @return                             The Vdw radio value of this element
	 */
    @TestMethod("testGetVdwRadius_PeriodicTableElement")
	public double getVdwRadius(PeriodicTableElement element){
    	PeriodicTableElement elementInt = getElement(element.getSymbol());
		return elementInt.getVdwRadius();
	}

    /**
	 *  Gets the covalent radios of this element in the periodic table.
	 *
	 * @param  element                     The PeriodicTableElement object
	 * @return                             The covalent radio value of this element
	 */
    @TestMethod("testGetCovalentRadius_PeriodicTableElement")
	public double getCovalentRadius(PeriodicTableElement element){
    	PeriodicTableElement elementInt = getElement(element.getSymbol());
		return elementInt.getCovalentRadius();
	}
    /**
	 *  Gets the Pauling Electronegativity radios of this element in the periodic table.
	 *
	 * @param  element                     The PeriodicTableElement object
	 * @return                             The Pauling Electronegativity value of this element
	 */
    @TestMethod("testGetPaulingEneg_PeriodicTableElement")
	public double getPaulingEneg(PeriodicTableElement element){
    	PeriodicTableElement elementInt = getElement(element.getSymbol());
		return elementInt.getPaulingEneg();
	}
}

