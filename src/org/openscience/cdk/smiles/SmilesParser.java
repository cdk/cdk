/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 *
 *  Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 *    Molecule m = sp.parseSMILES("c1ccccc1");
 *  } catch (InvalidSmilesException ise) {
 *  }
 *  </pre> 
 *
 * <p>This parser does not parse stereochemical information.
 *
 * <p>References: <a href="http://cdk.sf.net/biblio.html#WEI88">WEI88</a>
 *
 * @author     steinbeck
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
	 *  Parses a SMILES string and returns a Molecule object
	 *
	 *@param  smiles                      A SMILES string
	 *@return                             A molecule representing the constitution
	 *      given in the SMILES string
	 *@exception  InvalidSmilesException  Description of the Exception
	 */
	public Molecule parseSmiles(String smiles) throws InvalidSmilesException
	{
		Bond bond = null;
		boolean aromaticAtom = false;
		nodeCounter = 0;
		bondStatus = 0;
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

		char mychar;
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
					if (currentSymbol.length() == 1)
					{
						if (!(currentSymbol.toUpperCase()).equals(currentSymbol))
						{
							atom.flags[CDKConstants.ISAROMATIC] = true;
							atom.setSymbol(currentSymbol.toUpperCase());
						}
					}

					molecule.addAtom(atom);
					logger.debug("Adding atom " + atom.hashCode());
					if (lastNode != null)
					{
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
					if (lastNode != null)
					{
						bond = new Bond(atom, lastNode, bondStatus);
						bond.flags[CDKConstants.ISAROMATIC] = true;
						molecule.addBond(new Bond(atom, lastNode, bondStatus));
					}
					bondStatus = CDKConstants.BONDORDER_SINGLE;
					if (mychar == 'c' || mychar == 'n' || mychar == 's' || mychar == 'o')
					{
						bondStatus = CDKConstants.BONDORDER_AROMATIC;
					}
					lastNode = atom;
					nodeCounter++;
					position = position + currentSymbol.length();
				} else
				{
					throw new InvalidSmilesException(message);
				}
			} catch (Exception exc)
			{
				exc.printStackTrace();
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
	private String getAtomString(String s, int pos) throws InvalidSmilesException
	{
		StringBuffer as = new StringBuffer();
		try
		{
			for (int f = pos + 1; f < s.length(); f++)
			{
				if (s.substring(f, 1).equals("]"))
				{
					break;
				} else
				{
					as.append(s.substring(f, 1));
				}

			}
		} catch (Exception exc)
		{
			String message = "Problem parsing Atom specification given in brackets.\n";
			message += "Invalid SMILES string was: " + s;
			throw new InvalidSmilesException(message);
		}
		return as.toString();
	}


	/**
	 *  Gets the Charge attribute of the SmilesParser object
	 *
	 *@param  s  Description of the Parameter
	 *@return    The Charge value
	 */
	private int getCharge(String s)
	{
		int charge = 0;
		int signPos = s.lastIndexOf("-");
		if (signPos != -1)
		{
			signPos = s.indexOf("+");

		} else
		{
			charge = -1;
		}
		return 0;
	}


	/**
	 *  Gets the ElementSymbol attribute of the SmilesParser object
	 *
	 *@param  s    Description of the Parameter
	 *@param  pos  Description of the Parameter
	 *@return      The ElementSymbol value
	 */
	private String getElementSymbol(String s, int pos)
	{
		if (pos < s.length() - 1 && "BrCl".indexOf((s.substring(pos, pos + 2))) >= 0)
		{
			return s.substring(pos, pos + 2);
		} else if ("BCcNnOoPSsFI".indexOf((s.substring(pos, pos + 1))) >= 0)
		{
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
	private Atom assembleAtom(String s, int nodeCounter) throws InvalidSmilesException
	{
		Atom atom = null;
		int position = 0;
		String currentSymbol = null;
		char mychar;
		do
		{
			try
			{
				mychar = s.charAt(position);
				if ((mychar >= 'A' && mychar <= 'Z') || (mychar >= 'a' && mychar <= 'z'))
				{
					currentSymbol = getElementSymbol(s, position);
					position = position + currentSymbol.length();
				} else if (mychar >= '0' && mychar <= '9')
				{
					position++;
				} else
				{
					throw new InvalidSmilesException(message);
				}
			} catch (Exception exc)
			{
				throw new InvalidSmilesException(message);
			}
		} while (position < s.length());


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

