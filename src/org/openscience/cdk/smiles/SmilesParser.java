/*
 *  $RCSfile$    $Author$    $Date$    $Revision$
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
 *  should be sufficient for most organic molecules.
 *
 *@author     steinbeck
 *@created    29. April 2002
 */

public class SmilesParser implements CDKConstants
{
	String message = "Can't handle SMILES string";
	public static boolean debug = false;
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
	 *@param  smiles   A SMILES string
	 *@return        A molecule representing the constitution given in the SMILES string
	 *@exception  InvalidSMILESException  thrown if we could not parse the SMILES string
	 */
	public Molecule parseSmiles(String smiles) throws InvalidSmilesException
	{
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
				if ((mychar >= 'A' && mychar <= 'Z') || (mychar >= 'a' && mychar <= 'z'))
				{
					currentSymbol = getElementSymbol(smiles, position);
					if (bondStatus == BONDORDER_AROMATIC && !(mychar == 'c' || mychar == 'n' || mychar == 's' || mychar == 'o'))
					{
						bondStatus = BONDORDER_SINGLE;
					}
					atom = new Atom(currentSymbol);
					molecule.addAtom(atom);
					if (debug) System.out.println("Adding atom " + currentSymbol);
					if (lastNode != null)
					{
						molecule.addBond(new Bond(atom, lastNode, bondStatus));
					}
					bondStatus = BONDORDER_SINGLE;
					if (mychar == 'c' || mychar == 'n' || mychar == 's' || mychar == 'o')
					{
						bondStatus = BONDORDER_AROMATIC;
					}
					lastNode = atom;
					nodeCounter++;
					position = position + currentSymbol.length();
				}
				else if (mychar == '=')
				{
					bondStatus = BONDORDER_DOUBLE;
					position++;
				}
				else if (mychar == '#')
				{
					bondStatus = BONDORDER_TRIPLE;
					position++;
				}
				else if (mychar == '(')
				{
					atomStack.push(lastNode);
					bondStack.push(new Double(bondStatus));
					position++;
				}
				else if (mychar == ')')
				{
					lastNode = (Atom) atomStack.pop();
					bondStatus = ((Double) bondStack.pop()).doubleValue();
					position++;
				}
				else if (mychar >= '0' && mychar <= '9')
				{
					chars[0] = mychar;
					currentSymbol = new String(chars);
					thisRing = (new Integer(new String(chars))).intValue();
					handleRing();
					position++;
				}
				else if (mychar == '%')
				{
					currentSymbol = getRingNumber(smiles, position);
					thisRing = (new Integer(currentSymbol)).intValue();
					handleRing();
					position += currentSymbol.length() + 1;
				}
				else if (mychar == '[')
				{
					currentSymbol = getAtomString(smiles, position);
					atom = assembleAtom(currentSymbol, nodeCounter);
					molecule.addAtom(atom);
					if (lastNode != null)
					{
						molecule.addBond(new Bond(atom, lastNode, bondStatus));
					}
					bondStatus = BONDORDER_SINGLE;
					if (mychar == 'c' || mychar == 'n' || mychar == 's' || mychar == 'o')
					{
						bondStatus = BONDORDER_AROMATIC;
					}
					lastNode = atom;
					nodeCounter++;
					position = position + currentSymbol.length();
				}
				else
				{
					throw new InvalidSmilesException(message);
				}
			}
			catch (Exception exc)
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
	 *@param  s    Description of Parameter
	 *@param  pos  Description of Parameter
	 *@return      The AtomString value
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
				}
				else as.append(s.substring(f, 1));
				
			}
		}
		catch(Exception exc)
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
	 *@param  s  Description of Parameter
	 *@return    The Charge value
	 */
	private int getCharge(String s)
	{
		int charge = 0;
		int signPos = s.lastIndexOf("-");
		if (signPos != -1)
		{
			signPos = s.indexOf("+");

		}
		else
		{
			charge = -1;
		}
		return 0;
	}


	/**
	 *  Gets the ElementSymbol attribute of the SmilesParser object
	 *
	 *@param  s    Description of Parameter
	 *@param  pos  Description of Parameter
	 *@return      The ElementSymbol value
	 */
	private String getElementSymbol(String s, int pos)
	{
		if (pos < s.length() - 1 && "BrCl".indexOf((s.substring(pos, pos + 2))) >= 0)
		{
			return s.substring(pos, pos + 2);
		}
		else if ("BCcNnOoPSsFI".indexOf((s.substring(pos, pos + 1))) >= 0)
		{
			return s.substring(pos, pos + 1).toUpperCase();
		}
		return null;
	}


	/**
	 *  Gets the RingNumber attribute of the SmilesParser object
	 *
	 *@param  s    Description of Parameter
	 *@param  pos  Description of Parameter
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
	 *@param  s                                               Description of
	 *      Parameter
	 *@param  nodeCounter                                     Description of
	 *      Parameter
	 *@return                                                 Description of the
	 *      Returned Value
	 *@exception  compchem.exceptions.InvalidSMILESException  Description of
	 *      Exception
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
				}
				else if (mychar >= '0' && mychar <= '9')
				{
					position++;
				}
				else
				{
					throw new InvalidSmilesException(message);
				}
			}
			catch (Exception exc)
			{
				throw new InvalidSmilesException(message);
			}
		} while (position < s.length());



		return atom;
	}


	/**
	 *  We call this method when a ring (depicted by a number)
	 * has been found.
	 */
	private void handleRing()
	{
		double bondStat = bondStatus;
		Atom atom = null;
		Atom partner = null;
		Atom thisNode = rings[thisRing];
		if (thisNode != null)
		{
			if (bondStat == BONDORDER_AROMATIC)
			{
				if (ringbonds[thisRing] != BONDORDER_AROMATIC)
				{
					bondStat = BONDORDER_SINGLE;
				}
			}
			atom = molecule.getAtomAt(nodeCounter - 1);
			
			partner = thisNode;
			molecule.addBond(new Bond(atom, partner, bondStat));
			rings[thisRing] = null;
			ringbonds[thisRing] = -1;

		}
		else
		{
			rings[thisRing] = molecule.getAtomAt(nodeCounter - 1);
			ringbonds[thisRing] = bondStatus;
		}
	}
}

