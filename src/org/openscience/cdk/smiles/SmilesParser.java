/*  $RCSfile$
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
 *  All I ask is that proper credit is given for my work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.smiles;

import java.util.*;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.*;

/**
 *  Parses a SMILES string and an AtomContainer. So far only the SSMILES subset
 *  and the '%' tag for more than 10 rings at a time are supported, but this
 *  should be sufficient for most organic molecules. An example:
 *  <pre>
 *  try {
 *    SmilesParser sp = new SmilesParser();
 *    Molecule m = sp.parseSmiles("c1ccccc1");
 *  } catch (InvalidSmilesException ise) {
 *  }
 *  </pre> 
 *
 * <p>This parser does not parse stereochemical information.
 *
 * <p>References: <a href="http://cdk.sf.net/biblio.html#WEI88">WEI88</a>
 *
 * @author     Christoph Steinbeck
 * @author     Egon Willighagen
 * @created    29. April 2002
 * @keyword    SMILES, parser
 */
public class SmilesParser
{

	private org.openscience.cdk.tools.LoggingTool logger;


	/**
	 *  Constructor for the SmilesParser object
	 */
	public SmilesParser()
	{
		logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
	}


	String message = "Can't handle SMILES string";
	int position = -1;
	int nodeCounter = -1;
	String smiles = null;
	double bondStatus = -1;
	Atom[] rings = null;
	double[] ringbonds = null;
	int thisRing = -1;
	Molecule molecule = null;
	String currentSymbol = null;

    /**
     * Parses a SMILES string and returns a Molecule object. The SMILES string
     * may not contain any '.' characters.
     *
     * @param  smiles    A SMILES string
     * @return           A Molecule representing the constitution
     *                   given in the SMILES string
     * @exception        InvalidSmilesException  Description of the Exception
     */
    public Molecule parseSmiles(String smiles) throws InvalidSmilesException {
		Bond bond = null;
		boolean aromaticAtom = false;
		nodeCounter = 0;
		bondStatus = 0;
        boolean bondExists = true;
		thisRing = -1;
		currentSymbol = null;
		molecule = new Molecule();
		position = 0;
		// we don't want more than 1024 rings
		rings = new Atom[1024];
		ringbonds = new double[1024];
		for (int f = 0; f < 1024; f++)
		{
			rings[f] = null;
			ringbonds[f] = -1;
		}

		char mychar = 'X';
		char[] chars = new char[1];
		Atom lastNode = null;
		Atom thisNode = null;
		Stack atomStack = new Stack();
		Stack bondStack = new Stack();
		Atom atom = null;
		Atom partner = null;
		do
		{
			try
			{
				mychar = smiles.charAt(position);
				logger.debug("");
				logger.debug("Processing: " + mychar);
				if (lastNode != null)
				{
					logger.debug("Lastnode: " + lastNode.hashCode());
				}
				if ((mychar >= 'A' && mychar <= 'Z') || (mychar >= 'a' && mychar <= 'z'))
				{
					currentSymbol = getElementSymbol(smiles, position);
					if (bondStatus == CDKConstants.BONDORDER_AROMATIC && !(mychar == 'c' || mychar == 'n' || mychar == 's' || mychar == 'o'))
					{
						bondStatus = CDKConstants.BONDORDER_SINGLE;
					}
					atom = new Atom(currentSymbol);
                    if (currentSymbol.length() == 1) {
                        if (!(currentSymbol.toUpperCase()).equals(currentSymbol)) {
                            atom.flags[CDKConstants.ISAROMATIC] = true;
                            atom.setSymbol(currentSymbol.toUpperCase());
                        }
                    }

					molecule.addAtom(atom);
					logger.debug("Adding atom " + atom.hashCode());
                    if ((lastNode != null) && bondExists) {
                        logger.debug("Creating bond between " + atom.getSymbol() + " and " + lastNode.getSymbol());
						bond = new Bond(atom, lastNode, bondStatus);
						bond.flags[CDKConstants.ISAROMATIC] = true;
						molecule.addBond(bond);
					}
					bondStatus = CDKConstants.BONDORDER_SINGLE;
					if (mychar == 'c' || mychar == 'n' || mychar == 's' || mychar == 'o')
					{
						bondStatus = CDKConstants.BONDORDER_AROMATIC;
					}
					lastNode = atom;
					nodeCounter++;
					position = position + currentSymbol.length();
                    bondExists = true;
				} else if (mychar == '=')
				{
					bondStatus = CDKConstants.BONDORDER_DOUBLE;
					position++;
				} else if (mychar == '#')
				{
					bondStatus = CDKConstants.BONDORDER_TRIPLE;
					position++;
				} else if (mychar == '(')
				{
					atomStack.push(lastNode);
					logger.debug("Stack:");
					Enumeration ses = atomStack.elements();
					while (ses.hasMoreElements())
					{
						Atom a = (Atom) ses.nextElement();
						logger.debug("" + a.hashCode());
					}
					logger.debug("------");
					bondStack.push(new Double(bondStatus));
					position++;
				} else if (mychar == ')')
				{
					lastNode = (Atom) atomStack.pop();
					logger.debug("Stack:");
					Enumeration ses = atomStack.elements();
					while (ses.hasMoreElements())
					{
						Atom a = (Atom) ses.nextElement();
						logger.debug("" + a.hashCode());
					}
					logger.debug("------");
					bondStatus = ((Double) bondStack.pop()).doubleValue();
					position++;
				} else if (mychar >= '0' && mychar <= '9')
				{
					chars[0] = mychar;
					currentSymbol = new String(chars);
					thisRing = (new Integer(new String(chars))).intValue();
					handleRing(lastNode);
					position++;
				} else if (mychar == '%')
				{
					currentSymbol = getRingNumber(smiles, position);
					thisRing = (new Integer(currentSymbol)).intValue();
					handleRing(lastNode);
					position += currentSymbol.length() + 1;
				} else if (mychar == '[')
				{
					currentSymbol = getAtomString(smiles, position);
					atom = assembleAtom(currentSymbol, nodeCounter);
					molecule.addAtom(atom);
                    logger.debug("Added atom: " + atom);
                    if (lastNode != null && bondExists) {
						bond = new Bond(atom, lastNode, bondStatus);
						bond.flags[CDKConstants.ISAROMATIC] = true;
						molecule.addBond(new Bond(atom, lastNode, bondStatus));
                        logger.debug("Added bond: " + bond);
					}
					bondStatus = CDKConstants.BONDORDER_SINGLE;
					if (mychar == 'c' || mychar == 'n' || mychar == 's' || mychar == 'o')
					{
						bondStatus = CDKConstants.BONDORDER_AROMATIC;
					}
					lastNode = atom;
					nodeCounter++;
					position = position + currentSymbol.length() + 2; // plus two for [ and ]
                    bondExists = true;
                } else if (mychar == '.'){
                    bondExists = false;
                    position++;
				} else if (mychar == '/' || mychar == '\\') {
                    logger.warn("Ignoring stereo information for double bond");
                    position++;
				} else if (mychar == '@') {
                    if (position < smiles.length()-1 && smiles.charAt(position+1) == '@') {
                        position++;
                    }
                    logger.warn("Ignoring stereo information for atom");
                    position++;
				} else {
					throw new InvalidSmilesException("Unexpected character found: " + mychar);
				}
			} catch (InvalidSmilesException exc) {
                throw exc;
			} catch (Exception exception) {
                logger.error("Error while parsing char: " + mychar);
                logger.debug(exception);
				throw new InvalidSmilesException(message);
			}
		} while (position < smiles.length());

		return molecule;
	}


	/**
	 *  Gets the AtomString attribute of the SmilesParser object
	 *
	 *@param  s                           Description of the Parameter
	 *@param  pos                         Description of the Parameter
	 *@return                             The AtomString value
	 *@exception  InvalidSmilesException  Description of the Exception
	 */
	private String getAtomString(String smiles, int pos) throws InvalidSmilesException
	{
		StringBuffer atomString = new StringBuffer();
		try {
			for (int f = pos + 1; f < smiles.length(); f++) {
                char character = smiles.charAt(f);
				if (character == ']') {
					break;
				} else {
					atomString.append(character);
				}
			}
		} catch (Exception exception) {
			String message = "Problem parsing Atom specification given in brackets.\n";
			message += "Invalid SMILES string was: " + smiles;
            logger.error(message);
            logger.debug(exception);
			throw new InvalidSmilesException(message);
		}
		return atomString.toString();
	}


    /**
     * Gets the Charge attribute of the SmilesParser object
     *
     *@param  s  Description of the Parameter
     *@return    The Charge value
     */
    private int getCharge(String chargeString, int position) {
        logger.debug("Parsing charge from: " + chargeString.substring(position));
        int charge = 0;
        if (chargeString.charAt(position) == '+') {
            charge = +1;
            position++;
        } else if (chargeString.charAt(position) == '-') {
            charge = -1;
            position++;
        } else {
            return charge;
        }
        StringBuffer multiplier = new StringBuffer();
        while (position < chargeString.length() && Character.isDigit(chargeString.charAt(position))) {
            multiplier.append(chargeString.charAt(position));
            position++;
        }
        if (multiplier.length() > 0) {
            logger.debug("Found multiplier: " + multiplier.toString());
            try {
                charge = charge * Integer.parseInt(multiplier.toString());
            } catch (Exception exception) {
                logger.error("Could not parse positive atomic charge!");
                logger.debug(exception);
            }
        }
        logger.debug("Found charge: " + charge);
        return charge;
    }
    
    private int getImplicitHydrogenCount(String s, int position) {
        int count = 1;
        if (s.charAt(position) == 'H') {
            StringBuffer multiplier = new StringBuffer();
            while (position < (s.length() -1) && Character.isDigit(s.charAt(position+1))) {
                multiplier.append(position+1);
                position++;
            }
            if (multiplier.length() > 0) {
                try {
                    count = count + Integer.parseInt(multiplier.toString());
                } catch (Exception exception) {
                    logger.error("Could not parse number of implicit hydrogens!");
                    logger.debug(exception);
                }
            }
        }
        return count;
    }

	/**
	 * Gets the ElementSymbol attribute of the SmilesParser object
	 *
	 * @param  s    Description of the Parameter
	 * @param  pos  Description of the Parameter
	 * @return      The ElementSymbol value
	 */
     private String getElementSymbol(String s, int pos) {
         if (pos < s.length() - 1) {
             String possibleSymbol = s.substring(pos, pos + 2);
             if (("HeLiBeNeNaMgAlSiClArCaScTiCrMnFeCoNiCuZnGaGeAsSe".indexOf(possibleSymbol) >= 0) ||
                 ("BrKrRbSrZrNbMoTcRuRhPdAgCdInSnSbTeXeCsBaLuHfTaRe".indexOf(possibleSymbol) >= 0) ||
                 ("OsIrPtAuHgTlPbBiPoAtRnFrRaLrRfDbSgBhHsMtDs".indexOf(possibleSymbol) >= 0)) {
                 return possibleSymbol;
             }            
        }
        if ("HBCcNnOoFPSsIU".indexOf((s.charAt(pos))) >= 0) {
            return s.substring(pos, pos + 1);
        }
        return null;
    }


	/**
	 *  Gets the RingNumber attribute of the SmilesParser object
	 *
	 *@param  s    Description of the Parameter
	 *@param  pos  Description of the Parameter
	 *@return      The RingNumber value
	 */
	private String getRingNumber(String s, int pos)
	{
		char mychar = ' ';
		String retString = "";
		pos++;
		do
		{
			retString += s.charAt(pos);
			pos++;
		} while (pos < s.length() &&
				(s.charAt(pos) >= '0' && s.charAt(pos) <= '9'))
				;
		return retString;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  s                           Description of the Parameter
	 *@param  nodeCounter                 Description of the Parameter
	 *@return                             Description of the Return Value
	 *@exception  InvalidSmilesException  Description of the Exception
	 */
	private Atom assembleAtom(String s, int nodeCounter) throws InvalidSmilesException {
        logger.debug("Assembling atom from: " + s);
        Atom atom = null;
        int position = 0;
        String currentSymbol = null;
        StringBuffer isotopicNumber = new StringBuffer();
        char mychar;
        logger.debug("Parse everythings before and including element symbol");
        do {
            try {
                mychar = s.charAt(position);
                logger.debug("Parsing char: " + mychar);
                if ((mychar >= 'A' && mychar <= 'Z') || (mychar >= 'a' && mychar <= 'z')) {
                    currentSymbol = getElementSymbol(s, position);
                    logger.debug("Found element symbol: " + currentSymbol);
                    position = position + currentSymbol.length();
                    atom = new Atom(currentSymbol);
                    if (currentSymbol.length() == 1) {
                        if (!(currentSymbol.toUpperCase()).equals(currentSymbol)) {
                            atom.flags[CDKConstants.ISAROMATIC] = true;
                            atom.setSymbol(currentSymbol.toUpperCase());
                        }
                    }
                    logger.debug("Made atom: " + atom);
                    break;
                } else if (mychar >= '0' && mychar <= '9') {
                    isotopicNumber.append(mychar);
                    position++;
                } else {
                    throw new InvalidSmilesException("Found unexpected char: " + mychar);
                }
            } catch (Exception exception) {
                logger.error("Could not parse atom string: " + s);
                logger.debug(exception);
                throw new InvalidSmilesException("Could not parse atom string: " + s);
            }
        } while (position < s.length());
        if (isotopicNumber.toString().length() > 0) {
            try {            
                atom.setAtomicMass(Integer.parseInt(isotopicNumber.toString()));
            } catch (Exception exception) {
                logger.error("Could not set atom's atom number.");
                logger.debug(exception);
            }
        }
        logger.debug("Parsing part after element symbol (like charge): " + s.substring(position));
        int charge = 0;
        int implicitHydrogens = 0;
        while (position < s.length()) {
            try {
                mychar = s.charAt(position);
                logger.debug("Parsing char: " + mychar);
                if (mychar == 'H') {
                    // count implicit hydrogens
                    implicitHydrogens = getImplicitHydrogenCount(s, position);
                    position++;
                    if (implicitHydrogens > 1) {
                        position++;
                    }
                    atom.setHydrogenCount(implicitHydrogens);
                } else if (mychar == '+' || mychar == '-') {
                    charge = getCharge(s, position);
                    position++;
                    if (charge < -1 || charge > 1) position++;
                    atom.setFormalCharge(charge);
                } else if (mychar == '@') {
                    if (position < s.length()-1 && s.charAt(position+1) == '@') {
                        position++;
                    }
                    logger.warn("Ignoring stereo information for atom");
                    position++;
                } else {
                    throw new InvalidSmilesException("Found unexpected char: " + mychar);
                }
            } catch (Exception exception) {
                logger.error("Could not parse atom string: " + s);
                logger.debug(exception);
                throw new InvalidSmilesException("Could not parse atom string: " + s);
            }
        };
        return atom;
    }


	/**
	 *  We call this method when a ring (depicted by a number) has been found.
	 *
	 *@param  atom  Description of the Parameter
	 */
	private void handleRing(Atom atom)
	{
		double bondStat = bondStatus;
		Bond bond = null;
		Atom partner = null;
		Atom thisNode = rings[thisRing];
		// lookup
		if (thisNode != null)
		{
			/*
			 *  Second occurence of this ring:
			 *  - close ring
			 */
			if (bondStat == CDKConstants.BONDORDER_AROMATIC)
			{
				if (ringbonds[thisRing] != CDKConstants.BONDORDER_AROMATIC)
				{
					bondStat = CDKConstants.BONDORDER_SINGLE;
				}
			}

			partner = thisNode;
			bond = new Bond(atom, partner, bondStat);
			bond.flags[CDKConstants.ISAROMATIC] = true;
			molecule.addBond(bond);
			rings[thisRing] = null;
			ringbonds[thisRing] = -1;

		} else
		{
			/*
			 *  First occurence of this ring:
			 *  - add current atom to list
			 */
			rings[thisRing] = atom;
			ringbonds[thisRing] = bondStatus;
		}
	}
}

