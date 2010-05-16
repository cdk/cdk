/*  $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2002-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *                200?-2007  Egon Willighagen <egonw@users.sf.net>
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smiles;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;
import org.openscience.cdk.stereo.TetrahedralChirality;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 * Parses a SMILES {@cdk.cite SMILESTUT} string and an AtomContainer. The full
 * SSMILES subset {@cdk.cite SSMILESTUT} and the '%' tag for more than 10 rings
 * at a time are supported. An example:
 * <pre>
 * try {
 *   SmilesParser sp = new SmilesParser(NewDefaultChemObjectBuilder.getInstance());
 *   IMolecule m = sp.parseSmiles("c1ccccc1");
 * } catch (InvalidSmilesException ise) {
 * }
 * </pre>
 *
 * <p>This parser does not parse stereochemical information, but the following
 * features are supported: reaction smiles, partitioned structures, charged
 * atoms, implicit hydrogen count, '*' and isotope information.
 *
 * <p>See {@cdk.cite WEI88} for further information.
 *
 * @author         Christoph Steinbeck
 * @author         Egon Willighagen
 * @cdk.module     smiles
 * @cdk.githash
 * @cdk.created    2002-04-29
 * @cdk.keyword    SMILES, parser
 * @cdk.bug        1579230
 * @cdk.bug        1579235
 * @cdk.bug        1579244
 */
@TestClass("org.openscience.cdk.smiles.SmilesParserTest")
public class SmilesParser {

	private final static String HAS_HARDCODED_HYDROGEN_COUNT = "SmilesParser.HasHardcodedHydrogenCount";
	
	private static ILoggingTool logger =
	    LoggingToolFactory.createLoggingTool(SmilesParser.class);
	private CDKHydrogenAdder hAdder;
		
	private int status = 0;
	protected IChemObjectBuilder builder;

	private enum Chirality {
	    ANTI_CLOCKWISE, // aka @
	    CLOCKWISE // aka @@
	}

	/**
	 * Constructor for the SmilesParser object.
	 * 
	 * @param builder IChemObjectBuilder used to create the IMolecules from
	 */
    public SmilesParser(IChemObjectBuilder builder)
	{
		this.builder = builder;
		try {
			hAdder = CDKHydrogenAdder.getInstance(builder);
		} catch (Exception exception) {
			logger.error("Could not instantiate hydrogenAdder: ",
					exception.getMessage());
			logger.debug(exception);
		}
	}

	int position = -1;
	int nodeCounter = -1;
	String smiles = null;
	IBond.Order bondStatus = null;
	IBond.Order bondStatusForRingClosure = IBond.Order.SINGLE;
    boolean bondIsAromatic = false;
    // array of atoms that initiated a ring closure
	IAtom[] rings = null;
    // array of atoms that complete a ring closure
    IAtom[] ringOtherAtoms = null;
	IBond.Order[] ringbonds = null;
	int thisRing = -1;
	IMolecule molecule = null;
	String currentSymbol = null;
	Map<IAtom,TemporaryChiralityStorage> chiralityInfo = null;
	
	/**
	 * Internal storage for temporary stereochemistry info. In particular, the atoms
	 * involved are generally not known until the full SMILES is processed.
	 */
	class TemporaryChiralityStorage {
        Chirality chiralityValue;
        IAtom[] atoms;
        int counter;
	    public TemporaryChiralityStorage() {
	        chiralityValue = null;
	        atoms = new IAtom[4];
	        counter = 0;
	    }
        public TemporaryChiralityStorage(IAtom atom) {
            chiralityValue = null;
            atoms = new IAtom[4];
            atoms[0] = atom;
            counter = 1;
        }
        public void addAtom(IAtom atom) {
            atoms[counter] = atom;
            counter++;
        }
	}

    /**
     * Parse a reaction SMILES.
     *
     * @param smiles The SMILES string to parse
     * @return An instance of {@link org.openscience.cdk.interfaces.IReaction}
     * @see #parseSmiles(String)
     * @throws InvalidSmilesException if the string cannot be parsed
     */
    @TestMethod("testReaction,testReactionWithAgents")
    public IReaction parseReactionSmiles(String smiles) throws InvalidSmilesException
	{
		StringTokenizer tokenizer = new StringTokenizer(smiles, ">");
		String reactantSmiles = tokenizer.nextToken();
		String agentSmiles = "";
		String productSmiles = tokenizer.nextToken();
		if (tokenizer.hasMoreTokens())
		{
			agentSmiles = productSmiles;
			productSmiles = tokenizer.nextToken();
		}

		IReaction reaction = builder.newInstance(IReaction.class);

		// add reactants
		IMolecule reactantContainer = parseSmiles(reactantSmiles);
		IMoleculeSet reactantSet = ConnectivityChecker.partitionIntoMolecules(reactantContainer);
		for (int i = 0; i < reactantSet.getAtomContainerCount(); i++)
		{
			reaction.addReactant(reactantSet.getMolecule(i));
		}

		// add reactants
		if (agentSmiles.length() > 0)
		{
			IMolecule agentContainer = parseSmiles(agentSmiles);
			IMoleculeSet agentSet = ConnectivityChecker.partitionIntoMolecules(agentContainer);
			for (int i = 0; i < agentSet.getAtomContainerCount(); i++)
			{
				reaction.addAgent(agentSet.getMolecule(i));
			}
		}

		// add products
		IMolecule productContainer = parseSmiles(productSmiles);
		IMoleculeSet productSet = ConnectivityChecker.partitionIntoMolecules(productContainer);
		for (int i = 0; i < productSet.getAtomContainerCount(); i++)
		{
			reaction.addProduct(productSet.getMolecule(i));
		}

		return reaction;
	}


	/**
	 *  Parses a SMILES string and returns a Molecule object.
	 *
	 *@param  smiles                      A SMILES string
	 *@return                             A Molecule representing the constitution
	 *      given in the SMILES string
	 *@throws  InvalidSmilesException  thrown when the SMILES string is invalid
	 */
    @TestMethod("testAromaticSmiles,testSFBug1296113")
    public IMolecule parseSmiles(String smiles) throws InvalidSmilesException {
		IMolecule molecule = this.parseString(smiles);
		
		// analyze the chirality info
		for (IAtom atom : chiralityInfo.keySet()) {
		    TemporaryChiralityStorage chirality = chiralityInfo.get(atom);
		    logger.debug("Chiral atom found: ", atom);
		    IAtom[] atoms = chirality.atoms;
		    ITetrahedralChirality l4Chiral = new TetrahedralChirality(
		        atom,
		        new IAtom[]{
		            atoms[0], atoms[1], atoms[2], atoms[3]
		        },
		        chirality.chiralityValue == Chirality.CLOCKWISE
		          ? Stereo.CLOCKWISE : Stereo.ANTI_CLOCKWISE
		    );
		    molecule.addStereoElement(l4Chiral);
		}

		// perceive atom types
		CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(molecule.getBuilder());
		int i = 0;
        for (IAtom atom : molecule.atoms()) {
            i++;
            try {
                IAtomType type = matcher.findMatchingAtomType(molecule, atom);
                AtomTypeManipulator.configure(atom, type);
            } catch (Exception e) {
                System.out.println("Cannot percieve atom type for the " + i + "th atom: " + atom.getSymbol());
                atom.setAtomTypeName("X");
            }
        }
		this.addImplicitHydrogens(molecule);
		this.perceiveAromaticity(molecule);

		return molecule;
	}

	/**
	 * This routine parses the smiles string into a molecule but does not add hydrogens, saturate, or perceive aromaticity
	 * @param smiles
	 * @return
	 * @throws InvalidSmilesException
	 */
	private IMolecule parseString(String smiles) throws InvalidSmilesException
	{
		logger.debug("parseSmiles()...");
		IBond bond = null;
		nodeCounter = 0;
		bondStatus = null;
        bondIsAromatic = false;
		boolean bondExists = true;
		thisRing = -1;
		currentSymbol = null;
		molecule = builder.newInstance(IMolecule.class);
		position = 0;
		chiralityInfo = new HashMap<IAtom,TemporaryChiralityStorage>();
		// we don't want more than 1024 rings
		final int MAX_RING_COUNT = 1024;
		rings = new IAtom[MAX_RING_COUNT];
        ringOtherAtoms = new IAtom[MAX_RING_COUNT];
		ringbonds = new IBond.Order[MAX_RING_COUNT];
		for (int f = 0; f < MAX_RING_COUNT; f++) {
			rings[f] = null;
            ringOtherAtoms[f] = null;
			ringbonds[f] = null;
		}

		char mychar = 'X';
		char[] chars = new char[1];
		IAtom lastNode = null;
		Stack<IAtom> atomStack = new Stack<IAtom>();
		Stack<IBond.Order> bondStack = new Stack<IBond.Order>();
		IAtom atom = null;
		do
		{
			try
			{
				mychar = smiles.charAt(position);
				logger.debug("");
				logger.debug("Processing: " + mychar);
				if (lastNode != null)
				{
					logger.debug("Lastnode: ", lastNode.hashCode());
				}
				if ((mychar >= 'A' && mychar <= 'Z') || (mychar >= 'a' && mychar <= 'z') ||
						(mychar == '*'))
				{
					status = 1;
					logger.debug("Found a must-be 'organic subset' element");
					// only 'organic subset' elements allowed
					atom = null;
					if (mychar == '*')
					{
						currentSymbol = "*";
						atom = builder.newInstance(IPseudoAtom.class, "*");
					} else
					{
						currentSymbol = getSymbolForOrganicSubsetElement(smiles, position);
						if (currentSymbol != null)
						{
							if (currentSymbol.length() == 1)
							{
								if (!(currentSymbol.toUpperCase()).equals(currentSymbol))
								{
									currentSymbol = currentSymbol.toUpperCase();
									atom = builder.newInstance(IAtom.class,currentSymbol);
									atom.setHybridization(Hybridization.SP2);
								} else
								{
									atom = builder.newInstance(IAtom.class,currentSymbol);
								}
							} else
							{
								atom = builder.newInstance(IAtom.class,currentSymbol);
							}
							logger.debug("Made atom: ", atom);
						} else
						{
							throw new InvalidSmilesException(
									"Found element which is not a 'organic subset' element. You must " +
									"use [" + mychar + "].");
						}
					}
					addAtomToActiveChiralities(lastNode, atom);
					molecule.addAtom(atom);
					logger.debug("Adding atom ", atom.hashCode());
					if ((lastNode != null) && bondExists)
					{
						logger.debug("Creating bond between ", atom.getSymbol(), " and ", lastNode.getSymbol());
						bond = builder.newInstance(IBond.class,atom, lastNode, bondStatus);
						            if (bondIsAromatic) {
                            bond.setFlag(CDKConstants.ISAROMATIC, true);
                        }
						molecule.addBond(bond);
					}
					bondStatus = CDKConstants.BONDORDER_SINGLE;
					lastNode = atom;
					nodeCounter++;
					position = position + currentSymbol.length();
					bondExists = true;
                    bondIsAromatic = false;
				} else if (mychar == '=')
				{
					position++;
					if (status == 2 || !((smiles.charAt(position) >= '0' && smiles.charAt(position) <= '9') || smiles.charAt(position) == '%'))
					{
						bondStatus = CDKConstants.BONDORDER_DOUBLE;
					} else
					{
						bondStatusForRingClosure = CDKConstants.BONDORDER_DOUBLE;
					}
				} else if (mychar == '#')
				{
					position++;
					if (status == 2 || !((smiles.charAt(position) >= '0' && smiles.charAt(position) <= '9') || smiles.charAt(position) == '%'))
					{
						bondStatus = CDKConstants.BONDORDER_TRIPLE;
					} else
					{
						bondStatusForRingClosure = CDKConstants.BONDORDER_TRIPLE;
					}
				} else if (mychar == '(')
				{
					atomStack.push(lastNode);
					logger.debug("Stack:");
					Enumeration<IAtom> ses = atomStack.elements();
					while (ses.hasMoreElements())
					{
						IAtom a = ses.nextElement();
						logger.debug("", a.hashCode());
					}
					logger.debug("------");
					bondStack.push(bondStatus);
					position++;
				} else if (mychar == ')')
				{
					lastNode = (IAtom) atomStack.pop();
					logger.debug("Stack:");
					Enumeration<IAtom> ses = atomStack.elements();
					while (ses.hasMoreElements())
					{
						IAtom a = ses.nextElement();
						logger.debug("", a.hashCode());
					}
					logger.debug("------");
					bondStatus = bondStack.pop();
					position++;
				} else if (mychar >= '0' && mychar <= '9')
				{
					status = 2;
					chars[0] = mychar;
					currentSymbol = new String(chars);
					thisRing = (Integer.valueOf(currentSymbol)).intValue();
					handleRing(lastNode);
					position++;
				} else if (mychar == '%')
				{
					currentSymbol = getRingNumber(smiles, position);
					thisRing = (Integer.valueOf(currentSymbol)).intValue();
					handleRing(lastNode);
					position += currentSymbol.length() + 1;
				} else if (mychar == '[')
				{
					currentSymbol = getAtomString(smiles, position);
					atom = assembleAtom(currentSymbol, lastNode, bondExists);
					addAtomToActiveChiralities(lastNode, atom);
					molecule.addAtom(atom);
					logger.debug("Added atom: ", atom);
					if (lastNode != null && bondExists)
					{
						bond = builder.newInstance(IBond.class,atom, lastNode, bondStatus);
						            if (bondIsAromatic) {
                            bond.setFlag(CDKConstants.ISAROMATIC, true);
                        }
						molecule.addBond(bond);
						logger.debug("Added bond: ", bond);
					}
					bondStatus = CDKConstants.BONDORDER_SINGLE;
                    bondIsAromatic = false;
					lastNode = atom;
					nodeCounter++;
					position = position + currentSymbol.length() + 2;
					// plus two for [ and ]
					atom.setProperty(HAS_HARDCODED_HYDROGEN_COUNT, "yes");
					if (atom.getHydrogenCount() == null) {
						// zero implicit hydrogens is implied when the Hx syntax is not used
						atom.setHydrogenCount(0);
					}
					bondExists = true;
				} else if (mychar == '.')
				{
					bondExists = false;
					position++;
				} else if (mychar == '-')
				{
					bondExists = true;
					// a simple single bond
					position++;
                } else if (mychar == ':') {
                    bondExists = true;
                    bondIsAromatic = true;
                    position++;
				} else if (mychar == '/' || mychar == '\\')
				{
					logger.warn("Ignoring stereo information for double bond");
					position++;
				} else if (mychar == '@')
				{
				    TemporaryChiralityStorage chirality = null;
				    if (lastNode != null) {
				        chirality = new TemporaryChiralityStorage(lastNode);
				    } else {
				        chirality = new TemporaryChiralityStorage();
				    }
				    // @ or @@
					if (position < smiles.length() - 1 && smiles.charAt(position + 1) == '@')
					{
	                    chirality.chiralityValue = Chirality.CLOCKWISE;
						position++;
					} else {
	                    chirality.chiralityValue = Chirality.ANTI_CLOCKWISE;
					}
					// @H or @@H ?
					if (position < smiles.length() - 1 && smiles.charAt(position + 1) == 'H') {
                        // because the current data model requires a ligancy four chirality to
					    // have 4 IAtoms, we add an explicit hydrogen
					    IAtom hydrogen = builder.newInstance(IAtom.class, "H");
					    IBond newBond = builder.newInstance(IBond.class,
					        atom, hydrogen, Order.SINGLE
					    );
					    molecule.addAtom(hydrogen);
					    molecule.addBond(newBond);
					    chirality.addAtom(hydrogen);
                        position++;
                    }
                    chiralityInfo.put(lastNode, chirality);
					position++;
				} else
				{
					throw new InvalidSmilesException("Unexpected character found: " + mychar);
				}
			} catch (InvalidSmilesException exc)
			{
				logger.error("InvalidSmilesException while parsing char (in parseSmiles()) '" + 
					mychar + "': " + exc.getMessage());
				logger.debug(exc);
				throw exc;
			} catch (Exception exception)
			{
				logger.error("Error while parsing char '" + mychar + "': " + exception.getMessage());
				logger.debug(exception);
				throw new InvalidSmilesException("Error while parsing char: " + mychar, exception);
			}
			logger.debug("Parsing next char");
		} while (position < smiles.length());


        if (thisRing != -1 && ringbonds[thisRing] != null && rings[thisRing] != null)
            throw new InvalidSmilesException("Rings weren't properly closed. Check ring numbers");

		return molecule;
	}

	private String getAtomString(String smiles, int pos) throws InvalidSmilesException
	{
		logger.debug("getAtomString()");
		StringBuffer atomString = new StringBuffer();
		try
		{
			for (int f = pos + 1; f < smiles.length(); f++)
			{
				char character = smiles.charAt(f);
				if (character == ']')
				{
					break;
				} else
				{
					atomString.append(character);
				}
			}
		} catch (Exception exception)
		{
			String message = "Problem parsing Atom specification given in brackets.\n";
			message += "Invalid SMILES string was: " + smiles;
			logger.error(message);
			logger.debug(exception);
			throw new InvalidSmilesException(message, exception);
		}
		return atomString.toString();
	}

	private int getCharge(String chargeString, int position)
	{
		logger.debug("getCharge(): Parsing charge from: ", chargeString.substring(position));
		int charge = 0;
		if (chargeString.charAt(position) == '+')
		{
			charge = +1;
			position++;
		} else if (chargeString.charAt(position) == '-')
		{
			charge = -1;
			position++;
		} else
		{
			return charge;
		}
		StringBuffer multiplier = new StringBuffer();
		while (position < chargeString.length() && Character.isDigit(chargeString.charAt(position)))
		{
			multiplier.append(chargeString.charAt(position));
			position++;
		}
		if (multiplier.length() > 0)
		{
			logger.debug("Found multiplier: ", multiplier);
			try
			{
				charge = charge * Integer.parseInt(multiplier.toString());
			} catch (Exception exception)
			{
				logger.error("Could not parse positive atomic charge!");
				logger.debug(exception);
			}
		}
		logger.debug("Found charge: ", charge);
		return charge;
	}

	private int getImplicitHydrogenCount(String s, int position)
	{
		logger.debug("getImplicitHydrogenCount(): Parsing implicit hydrogens from: " + s);

        // from the calling code, we only come here if we hit an H
        //
        // so if the function returns an explicit 0, it means that an
        // H0 was encountered.
        //
        // Howvever, H is equivalent to H1. In the latter, the calling code has
        // to increment the positon by 1, but in the former it shouldn't. So we
        // need a way to differentiate these two cases. We do so by returning
        // -1 if we get just H, and 1 if we see H1.
        //
        // This is a horrible kludge :( We need a JavaCC parser!
		int count = 0;  // for the case of no H which is same as H0
		if (s.charAt(position) == 'H')
		{
			StringBuffer multiplier = new StringBuffer();
			while (position < (s.length() - 1) && Character.isDigit(s.charAt(position + 1)))
			{
				multiplier.append(s.charAt(position + 1));
				position++;
			}
			if (multiplier.length() > 0)
			{
				try
				{
					count = Integer.parseInt(multiplier.toString());
				} catch (Exception exception)
				{
					logger.error("Could not parse number of implicit hydrogens from the multiplier: " + multiplier);
					logger.debug(exception);
				}
			} else count = -1; // since H == H1
		}
		return count;
	}

	private String getElementSymbol(String s, int pos)
	{
		logger.debug("getElementSymbol(): Parsing element symbol (pos=" + pos + ") from: " + s);
		// try to match elements not in the organic subset.
		// first, the two char elements
		if (pos < s.length() - 1)
		{
			String possibleSymbol = s.substring(pos, pos + 2);
			logger.debug("possibleSymbol: ", possibleSymbol);
			if (("HeLiBeNeNaMgAlSiClArCaScTiCrMnFeCoNiCuZnGaGeAsSe".indexOf(possibleSymbol) >= 0) ||
					("BrKrRbSrZrNbMoTcRuRhPdAgCdInSnSbTeXeCsBaLuHfTaRe".indexOf(possibleSymbol) >= 0) ||
					("OsIrPtAuHgTlPbBiPoAtRnFrRaLrRfDbSgBhHsMtDs".indexOf(possibleSymbol) >= 0))
			{
				return possibleSymbol;
			}
		}
		// if that fails, the one char elements
		String possibleSymbol = s.substring(pos, pos + 1);
		logger.debug("possibleSymbol: ", possibleSymbol);
		if (("HKUVYW".indexOf(possibleSymbol) >= 0))
		{
			return possibleSymbol;
		}
		// if that failed too, then possibly a organic subset element
		return getSymbolForOrganicSubsetElement(s, pos);
	}

	private void addAtomToActiveChiralities(IAtom chiAtom, IAtom atom) {
	    for (IAtom chiralAtom : chiralityInfo.keySet()) {
	        if (chiralAtom == atom)
	            // but not if the new atom is the chiral atom itself
	            continue;
	        if (chiAtom != chiralAtom)
	            // ok, the atom does not belong to this chirality
	            continue;
	        TemporaryChiralityStorage chirality = chiralityInfo.get(chiralAtom);
	        if (chirality.counter < 4) chirality.addAtom(atom);
	    }
	}

	/**
	 *  Gets the ElementSymbol for an element in the 'organic subset' for which
	 *  brackets may be omited. <p>
	 *
	 *  See: <a href="http://www.daylight.com/dayhtml/smiles/smiles-atoms.html">
	 *  http://www.daylight.com/dayhtml/smiles/smiles-atoms.html</a> .
	 */
	private String getSymbolForOrganicSubsetElement(String s, int pos)
	{
		logger.debug("getSymbolForOrganicSubsetElement(): Parsing organic subset element from: ", s);
		if (pos < s.length() - 1)
		{
			String possibleSymbol = s.substring(pos, pos + 2);
			if (("ClBr".indexOf(possibleSymbol) >= 0))
			{
				return possibleSymbol;
			}
		}
		if ("BCcNnOoFPSsI".indexOf((s.charAt(pos))) >= 0)
		{
			return s.substring(pos, pos + 1);
		}
		if ("fpi".indexOf((s.charAt(pos))) >= 0)
		{
			logger.warn("Element ", s, " is normally not sp2 hybridisized!");
			return s.substring(pos, pos + 1);
		}
		logger.warn("Subset element not found!");
		return null;
	}


	/**
	 *  Gets the RingNumber attribute of the SmilesParser object
	 */
	private String getRingNumber(String s, int pos) throws InvalidSmilesException {
		logger.debug("getRingNumber()");
		pos++;

		// Two digits impossible due to end of string
		if (pos >= s.length() - 1)
			throw new InvalidSmilesException("Percent sign ring closure numbers must be two-digit.");

		String retString = s.substring(pos, pos + 2);

		if (retString.charAt(0) < '0' || retString.charAt(0) > '9' || 
			retString.charAt(1) < '0' || retString.charAt(1) > '9')
			throw new InvalidSmilesException("Percent sign ring closure numbers must be two-digit.");

		return retString;
	}

	private IAtom assembleAtom(String s, IAtom lastNode, boolean bondExists) throws InvalidSmilesException
	{
		logger.debug("assembleAtom(): Assembling atom from: ", s);
		IAtom atom = null;
		int position = 0;
		String currentSymbol = null;
		StringBuffer isotopicNumber = new StringBuffer();
		char mychar;
		logger.debug("Parse everythings before and including element symbol");
		do
		{
			try
			{
				mychar = s.charAt(position);
				logger.debug("Parsing char: " + mychar);
				if ((mychar >= 'A' && mychar <= 'Z') || (mychar >= 'a' && mychar <= 'z'))
				{
					currentSymbol = getElementSymbol(s, position);
					if (currentSymbol == null)
					{
						throw new InvalidSmilesException(
								"Expected element symbol, found null!"
								);
					} else
					{
						logger.debug("Found element symbol: ", currentSymbol);
						position = position + currentSymbol.length();
						if (currentSymbol.length() == 1)
						{
							if (!(currentSymbol.toUpperCase()).equals(currentSymbol))
							{
								currentSymbol = currentSymbol.toUpperCase();
								atom = builder.newInstance(IAtom.class,currentSymbol);
								atom.setHybridization(Hybridization.SP2);

                                Integer hcount = atom.getHydrogenCount() == CDKConstants.UNSET ? 0 : atom.getHydrogenCount();
                                if (hcount > 0)
								{
									atom.setHydrogenCount(hcount - 1);
								}
							} else
							{
								atom = builder.newInstance(IAtom.class,currentSymbol);
							}
						} else
						{
							atom = builder.newInstance(IAtom.class,currentSymbol);
						}
						logger.debug("Made atom: ", atom);
					}
					break;
				} else if (mychar >= '0' && mychar <= '9')
				{
					isotopicNumber.append(mychar);
					position++;
				} else if (mychar == '*')
				{
					currentSymbol = "*";
					atom = builder.newInstance(IPseudoAtom.class, currentSymbol);
					logger.debug("Made atom: ", atom);
					position++;
					break;
				} else
				{
					throw new InvalidSmilesException("Found unexpected char: " + mychar);
				}
			} catch (InvalidSmilesException exc)
			{
				logger.error("InvalidSmilesException while parsing atom string '" + s + "': " + exc.getMessage());
				logger.debug(exc);
				throw exc;
			} catch (Exception exception)
			{
				logger.error("Could not parse atom string: ", s);
				logger.debug(exception);
				throw new InvalidSmilesException("Could not parse atom string '" + s + "': " + exception.getMessage(), exception);
			}
		} while (position < s.length());
		if (isotopicNumber.toString().length() > 0)
		{
			try
			{
				atom.setMassNumber(Integer.parseInt(isotopicNumber.toString()));
			} catch (Exception exception)
			{
				logger.error("Could not set atom's isotope number '" + isotopicNumber + "'");
				logger.debug(exception);
			}
		}
		logger.debug("Parsing part after element symbol (like charge): ", s.substring(position));
		int charge = 0;
		int implicitHydrogens = 0;
		while (position < s.length())
		{
			try
			{
				mychar = s.charAt(position);
				logger.debug("Parsing char: " + mychar);
				if (mychar == 'H')
				{
					// count implicit hydrogens
					implicitHydrogens = getImplicitHydrogenCount(s, position);
					position++;

                    // we check if H was followed by a number. if it wasn't, then the return value
                    // of getImplicitHydrogens is -1. See comments in that method
					if (implicitHydrogens >= 0)
					{
						position++;
					}
                    if (implicitHydrogens == -1) implicitHydrogens = 1;                    
					atom.setHydrogenCount(implicitHydrogens);
				} else if (mychar == '+' || mychar == '-')
				{
					charge = getCharge(s, position);
					position++; // skip the +
					// skip all digits following the +
					while (position < s.length() && Character.isDigit(s.charAt(position))) {
						position++;
					}
					atom.setFormalCharge(charge);
				} else if (mychar == '@')
				{
                    TemporaryChiralityStorage chirality = null;
                    if (lastNode != null && bondExists) {
                        chirality = new TemporaryChiralityStorage(lastNode);
                    } else {
                        chirality = new TemporaryChiralityStorage();
                    }
                    if (position < s.length() - 1 && s.charAt(position + 1) == '@')
                    {
                        chirality.chiralityValue = Chirality.CLOCKWISE;
                        position++;
                    } else {
                        chirality.chiralityValue = Chirality.ANTI_CLOCKWISE;
                    }
                    // @H or @@H ?
                    if (position < s.length() - 1 && s.charAt(position + 1) == 'H') {
                        // because the current data model requires a ligancy four chirality to
                        // have 4 IAtoms, we add an explicit hydrogen
                        IAtom hydrogen = builder.newInstance(IAtom.class, "H");
                        IBond newBond = builder.newInstance(IBond.class,
                            atom, hydrogen, Order.SINGLE
                        );
                        molecule.addAtom(hydrogen);
                        molecule.addBond(newBond);
                        chirality.addAtom(hydrogen);
                        position++;
                    }
                    chiralityInfo.put(atom, chirality);
                    position++;
				} else
				{
					throw new InvalidSmilesException("Found unexpected char: " + mychar);
				}
			} catch (InvalidSmilesException exc)
			{
				logger.error("InvalidSmilesException while parsing atom string: ", s);
				logger.debug(exc);
				throw exc;
			} catch (Exception exception)
			{
				logger.error("Could not parse atom string: ", s);
				logger.debug(exception);
				throw new InvalidSmilesException("Could not parse atom string: " + s, exception);
			}
		}
		return atom;
	}


	/**
	 *  We call this method when a ring (depicted by a number) has been found.
	 */
	private void handleRing(IAtom atom)
	{
		logger.debug("handleRing():");
		IBond.Order bondStat = bondStatusForRingClosure;
		if (BondManipulator.isHigherOrder(ringbonds[thisRing], bondStat))
			bondStat = ringbonds[thisRing];
		IBond bond = null;
		IAtom partner = null;
		IAtom thisNode = rings[thisRing];
		IAtom templateAtom = ringOtherAtoms[thisRing];
		// lookup
		if (thisNode != null)
		{
			partner = thisNode;
			replaceTemplateAtomInStereos(templateAtom, atom);
			if (chiralityInfo.containsKey(atom))
			    addAtomToActiveChiralities(atom, partner);
			bond = builder.newInstance(IBond.class,atom, partner, bondStat);
			if (bondIsAromatic) {
                bond.setFlag(CDKConstants.ISAROMATIC, true);
            }
			molecule.addBond(bond);
            bondIsAromatic = false;
			rings[thisRing] = null;
			ringbonds[thisRing] = null;
			ringOtherAtoms[thisRing] = null;
		} else
		{
			/*
			 *  First occurence of this ring:
			 *  - add current atom to list
			 */
			rings[thisRing] = atom;
			ringOtherAtoms[thisRing] = builder.newInstance(IAtom.class);
			addAtomToActiveChiralities(atom, ringOtherAtoms[thisRing]);
			ringbonds[thisRing] = bondStatusForRingClosure;
		}
		bondStatusForRingClosure = IBond.Order.SINGLE;
	}

	/**
	 * Replaces the <code>templateAtom</code> by <code>atom</code> in all currently defined stereochemistries.
	 *
	 * @param templateAtom {@link IAtom} to replace
	 * @param atom         new {@link IAtom}
	 */
	private void replaceTemplateAtomInStereos(IAtom templateAtom, IAtom atom) {
	    for (TemporaryChiralityStorage chirality : chiralityInfo.values()) {
            for (int i=0; i<4; i++) {
                if (chirality.atoms[i] == templateAtom)
                    chirality.atoms[i] = atom;
            }
        }
    }

    private void addImplicitHydrogens(IMolecule container) {
		try {
			logger.debug("before H-adding: ", container);
			Iterator<IAtom> atoms = container.atoms().iterator();
			while (atoms.hasNext()) {
				IAtom nextAtom = atoms.next();
				if (nextAtom.getProperty(HAS_HARDCODED_HYDROGEN_COUNT) == null) {
					hAdder.addImplicitHydrogens(container, nextAtom);
				}
			}
			logger.debug("after H-adding: ", container);
		} catch (Exception exception) {
			logger.error("Error while calculation Hcount for SMILES atom: ", exception.getMessage());
		}
	}

	private void perceiveAromaticity(IMolecule m) {
		IMoleculeSet moleculeSet = ConnectivityChecker.partitionIntoMolecules(m);
		logger.debug("#mols ", moleculeSet.getAtomContainerCount());
		for (int i = 0; i < moleculeSet.getAtomContainerCount(); i++) {
			IAtomContainer molecule = moleculeSet.getAtomContainer(i);
			logger.debug("mol: ", molecule);
			try {
				logger.debug(" after saturation: ", molecule);
				if (CDKHueckelAromaticityDetector
						.detectAromaticity(molecule)) {
					logger.debug("Structure is aromatic...");
				}
			} catch (Exception exception) {
				logger.error("Could not perceive aromaticity: ", exception
						.getMessage());
				logger.debug(exception);
			}
		}
	}
	
}

