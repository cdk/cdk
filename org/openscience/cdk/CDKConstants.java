/* CDKConstants.java
 *
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
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
 * 
 */

package org.openscience.cdk;

/**
 * An interface providing predefined values for a number of 
 * parameters used throughout the cdk. Classes using these Constants should 
 * implement this interface.
 *
 * @keyword bond order
 * @keyword stereochemistry
 */
public interface CDKConstants
{
	/** A bond of degree 1 */
	static int BONDORDER_SINGLE = 1;

	/** A bond of degree 2 */
	static int BONDORDER_DOUBLE = 2;

	/** A bond of degree 3 */
	static int BONDORDER_TRIPLE = 3;

	/** A bonds which end is above the drawing plane */
	static int STEREO_BOND_UP = 1;

        /** A bonds for which the stereochemistry is undefined */
        static int STEREO_BOND_UNDEFINED = 0;
	
	/** A bonds which end is below the drawing plane */
	static int STEREO_BOND_DOWN = -1;
	
	/** A bonds which end is above the drawing plane */
	static int STEREO_ATOM_PARITY_PLUS = 1;
	
	/** A bonds which end is above the drawing plane */
	static int STEREO_ATOM_PARITY_MINUS = -1;

	/** Carbon NMR shift contant for use as a key in the 
	  * ChemObject.physicalProperties hashtable
	  * @see org.openscience.cdk.ChemObject 
	  */
	static String NMRSHIFT_CARBON = "carbon nmr shift";
	/** Hydrogen NMR shift contant for use as a key in the 
	  * ChemObject.physicalProperties hashtable
	  * @see org.openscience.cdk.ChemObject 
	  */
	static String NMRSHIFT_HYDROGEN = "hydrogen nmr shift";
	/** Nitrogen NMR shift contant for use as a key in the 
	  * ChemObject.physicalProperties hashtable
	  * @see org.openscience.cdk.ChemObject 
	  */
	static String NMRSHIFT_NITROGEN = "nitrogen nmr shift";
	/** Phosphorus NMR shift contant for use as a key in the 
	  * ChemObject.physicalProperties hashtable
	  * @see org.openscience.cdk.ChemObject 
	  */
	static String NMRSHIFT_PHOSPORUS = "phosphorus nmr shift";
	/** Fluorine NMR shift contant for use as a key in the 
	  * ChemObject.physicalProperties hashtable
	  * @see org.openscience.cdk.ChemObject 
	  */
	static String NMRSHIFT_FLUORINE = "fluorine nmr shift";				
	/** Deuterium NMR shift contant for use as a key in the 
	  * ChemObject.physicalProperties hashtable
	  * @see org.openscience.cdk.ChemObject 
	  */
	static String NMRSHIFT_DEUTERIUM = "deuterium nmr shift";					
	

	/****************************************
	 * Some predefined flags - keep the     *
	 * numbers below 50 free for other      *
	 * purposes                             *
	 ****************************************/

	/** Has a chemobject been placed (Wheresoever)? Flags for Structure Diagram Generation and other uses */
	static int ISPLACED = 50;
	/** Is a chemobject part of a ring? Flags for Structure Diagram Generation and other uses */
	static int ISINRING = 51;
	/** Is a chemobject part of an alipahtic chain? Flags for Structure Diagram Generation and other uses */
	static int ISALIPHATIC = 52;
	/** Has a chemobject been visited during a search? Flags for Structure Diagram Generation and other uses */
	static int VISITED = 53; // Use in tree searches

}


