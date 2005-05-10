/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307  USA.
 */
package org.openscience.cdk.config.elements;

import java.util.Vector;

import org.openscience.cdk.PeriodicTableElement;
import org.openscience.cdk.tools.LoggingTool;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Reads an isotope list in CML2 format. An example definition is:
 * <pre>
 * <isotopeList id="H">
 *   <isotope id="H1" isotopeNumber="1" elementTyp="H">
 *     <abundance dictRef="cdk:relativeAbundance">100.0</abundance>
 *     <scalar dictRef="cdk:exactMass">1.00782504</scalar>
 *     <scalar dictRef="cdk:atomicNumber">1</scalar>
 *   </isotope>
 *   <isotope id="H2" isotopeNumber="2" elementTyp="H">
 *     <abundance dictRef="cdk:relativeAbundance">0.015</abundance>
 *     <scalar dictRef="cdk:exactMass">2.01410179</scalar>
 *     <scalar dictRef="cdk:atomicNumber">1</scalar>
 *   </isotope>
 * </isotopeList>
 * </pre> 
 *
 * @author     	   Miguel Rojas
 * @cdk.created    May 8, 2005
 * @cdk.module     core
 */
public class ElementPTHandler extends DefaultHandler 
{
	private final int SCALAR_UNSET = 0;
	private final int SCALAR_NAME = 1; 
	private final int SCALAR_ATOMICNUMBER = 2;
	private final int SCALAR_CHEMICALSERIE = 3;
	private final int SCALAR_PERIOD = 4;
	private final int SCALAR_GROUP = 5;
	private final int SCALAR_PHASE = 6;
	private int scalarType;
	private LoggingTool logger;
	private String currentChars;
	private Vector elements;
	
	private PeriodicTableElement elementType;
	private String currentElement;
	private String dictRef;
	
	public ElementPTHandler() 
	{
		logger = new LoggingTool(this);
	}

	/** 
	* Returns the element read from the XML file.
	*
	* @return A Vector object with all isotopes
	*/
	public Vector getElements() 
	{
		return elements;
	}

	// SAX Parser methods

	public void startDocument() 
	{
		elements = new Vector();
		scalarType = SCALAR_UNSET;
		elementType = null;
	}

	public void endElement(String uri, String local, String raw) 
	{
		logger.debug("end element: ", raw);
		if ("elementType".equals(local)) 
		{
			elements.addElement(elementType);
		} else if ("scalar".equals(local)) {
			currentChars.trim();
			try {
				if (scalarType == SCALAR_NAME){
					
					elementType.setName(currentChars);
				} else if (scalarType == SCALAR_ATOMICNUMBER) {
					elementType.setAtomicNumber(Integer.parseInt(currentChars));
				} else if (scalarType == SCALAR_CHEMICALSERIE) {
					elementType.setChemicalSerie(currentChars);
				} else if (scalarType == SCALAR_PERIOD) {
					elementType.setPeriod(currentChars);
				} else if (scalarType == SCALAR_GROUP) {
					elementType.setGroup(currentChars);
				} else if (scalarType == SCALAR_PHASE) {
					elementType.setPhase(currentChars);
				}
			} catch (NumberFormatException exception) {
				logger.error("The abundance value is incorrect: ", currentChars);
				logger.debug(exception);
			}
			scalarType = SCALAR_UNSET;
		}
		currentChars = "";
	}

	public void startElement(String uri, String local, 
                             String raw, Attributes atts) 
	{
		currentChars = "";
		dictRef = "";
		logger.debug("startElement: ", raw);
		logger.debug("uri: ", uri);
		logger.debug("local: ", local);
		logger.debug("raw: ", raw);
		if ("elementType".equals(local)) {
			for (int i = 0; i < atts.getLength(); i++) {
				if ("id".equals(atts.getQName(i))) {
					elementType = new PeriodicTableElement(atts.getValue(i));
				}
				else if ("idCAS".equals(atts.getQName(i))) {
					elementType.setCASid(atts.getValue(i));
				}
			}
		} else if ("scalar".equals(local)) 
			for (int i = 0; i < atts.getLength(); i++) {
				if ("dictRef".equals(atts.getQName(i))) {
					if ("cdk:name".equals(atts.getValue(i))) {
						scalarType = SCALAR_NAME;
					} else if ("cdk:atomicNumber".equals(atts.getValue(i))) {
						scalarType = SCALAR_ATOMICNUMBER;
					} else if ("cdk:name".equals(atts.getValue(i))) {
						scalarType = SCALAR_NAME;
					} else if ("cdk:chemicalSerie".equals(atts.getValue(i))) {
						scalarType = SCALAR_CHEMICALSERIE;
					} else if ("cdk:period".equals(atts.getValue(i))) {
						scalarType = SCALAR_PERIOD;
					} else if ("cdk:group".equals(atts.getValue(i))) {
						scalarType = SCALAR_GROUP;
					} else if ("cdk:phase".equals(atts.getValue(i))) {
						scalarType = SCALAR_PHASE;
					}
				}
			}
				
	}
	
	public void characters(char chars[], int start, int length) 
	{
		logger.debug("character data");
		currentChars += new String(chars, start, length);
	}

}
