/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2002-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.fingerprint;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.*;

/**
 *  Generates a Fingerprint for a given AtomContainer. Fingerprints are
 *  one-dimensional bit arrays, where bits are set according to a the occurence
 *  of a particular structural feature (See for example the Daylight inc. theory
 *  manual for more information) Fingerprints allow for a fast screening step to
 *  excluded candidates for a substructure search in a database. They are also a
 *  means for determining the similarity of chemical structures. <p>
 *
 *  A fingerprint is generated for an AtomContainer with this code: <pre>
 *   Molecule molecule = new Molecule();
 *   BitSet fingerprint = Fingerprinter.getFingerprint(molecule);
 * </pre> <p>
 *
 *  The FingerPrinter assumes that hydrogens are explicitely given! <p>
 *
 *  <font color="#FF0000">Warning: The aromaticity detection for this
 *  FingerPrinter relies on AllRingsFinder, which is known to take very long
 *  for some molecules with many cycles or special cyclic topologies. Thus, the
 *  AllRingsFinder has a built-in timeout of 5 seconds after which it aborts and
 *  throws an Exception. If you want your SMILES generated at any expense, you
 *  need to create your own AllRingsFinder, set the timeout to a higher value,
 *  and assign it to this FingerPrinter. In the vast majority of cases,
 *  however, the defaults will be fine. </font> <p>
 *
 *  <font color="#FF0000">Another Warning : The daylight manual says:
 *  "Fingerprints are not so definite: if a fingerprint indicates a pattern is
 *  missing then it certainly is, but it can only indicate a pattern's presence
 *  with some probability." In the case of very small molecules, the probability
 *  that you get the same fingerprint for different molecules is high. </font>
 *  </p>
 *
 * @author         steinbeck
 * @cdk.created    2002-02-24
 * @cdk.keyword    fingerprint
 * @cdk.keyword    similarity
 * @cdk.module     standard
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.fingerprint.FingerprinterTest")
public class Fingerprinter implements IFingerprinter {
	
	public final static int defaultSize = 1024;
	public final static int defaultSearchDepth = 8;
	
	private int size;
	private int searchDepth;

	final static boolean debug = true;
	static int debugCounter = 0;

	private static LoggingTool logger = new LoggingTool(Fingerprinter.class);
    

    /**
	 * Creates a fingerprint generator of length <code>defaultSize</code>
	 * and with a search depth of <code>defaultSearchDepth</code>.
	 */
	public Fingerprinter() {
		this(defaultSize, defaultSearchDepth);
	}
	
	public Fingerprinter(int size) {
		this(size, defaultSearchDepth);
	}
	
	/**
	 * Constructs a fingerprint generator that creates fingerprints of
	 * the given size, using a generation algorithm with the given search
	 * depth.
	 *
	 * @param  size        The desired size of the fingerprint
	 * @param  searchDepth The desired depth of search
	 */
	public Fingerprinter(int size, int searchDepth) {
		this.size = size;
		this.searchDepth = searchDepth;

    }
	
	/**
	 * Generates a fingerprint of the default size for the given AtomContainer.
	 *
	 *@param     ac         The AtomContainer for which a Fingerprint is generated
	 *@exception Exception  Description of the Exception
	 */

    @TestMethod("testGetFingerprint_IAtomContainer")
    public BitSet getFingerprint(IAtomContainer ac, AllRingsFinder ringFinder) throws Exception {
		String path = null;
		int position = -1;
		logger.debug("Entering Fingerprinter");
		logger.debug("Starting Aromaticity Detection");
		long before = System.currentTimeMillis();
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);
		CDKHueckelAromaticityDetector.detectAromaticity(ac);
		long after = System.currentTimeMillis();
		logger.debug("time for aromaticity calculation: " + (after - before) + " milliseconds");
		logger.debug("Finished Aromaticity Detection");
		Map paths = findPathes(ac, searchDepth);
		BitSet bs = new BitSet(size);
		for (Iterator e = paths.values().iterator(); e.hasNext(); )
		{
			path = (String)e.next();
			position = new java.util.Random(path.hashCode()).nextInt(size);
			logger.debug("Setting bit " + position + " for " + path);
			bs.set(position);
		}
		return bs;
	}


	/**
	 * Generates a fingerprint of the default size for the given AtomContainer.
	 *
	 *@param     ac         The AtomContainer for which a Fingerprint is generated
	 *@exception Exception  Description of the Exception
	 */
    @TestMethod("testGetFingerprint_IAtomContainer")
    public BitSet getFingerprint(IAtomContainer ac) throws Exception {
		return getFingerprint(ac, null);
	}
	
	/**
	 *  Gets all pathes of length 1 up to the length given by the 'searchDepth"
	 *  parameter. The pathes are aquired by a number of depth first searches, one
	 *  for each atom.
	 *
	 *@param  ac           The AtomContainer which is to be searched.
	 *@param  searchDepth  Description of the Parameter
	 */
	protected Map findPathes(IAtomContainer ac, int searchDepth)
	{
		Map paths = new HashMap();
		List currentPath = new ArrayList();
		debugCounter = 0;
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			currentPath.clear();
			currentPath.add(ac.getAtom(f));
			checkAndStore(currentPath, paths);
			logger.info("Starting at atom " + (f + 1) + " with symbol " + ac.getAtom(f).getSymbol());
			depthFirstSearch(ac, ac.getAtom(f), paths, currentPath, 0, searchDepth);
		}
		return paths;
	}


	/**
	 *  Performs a recursive depth first search
	 *
	 *@param  ac            The AtomContainer to be searched
	 *@param  root          The Atom to start the search at
	 *@param  currentPath   The Path that has been generated so far
	 *@param  currentDepth  The current depth in this recursive search
	 *@param  searchDepth   Description of the Parameter
	 */
	private void depthFirstSearch(IAtomContainer ac, IAtom root, Map paths, List currentPath, int currentDepth, int searchDepth)
	{
		List bonds = ac.getConnectedBondsList(root);

		/*
		 *  try
		 *  {
		 *  logger.debug("Currently at atom no. " + (ac.getAtomNumber(root)  + 1) + " with symbol "  + root.getSymbol());
		 *  }
		 *  catch(Exception exc){}
		 */
		org.openscience.cdk.interfaces.IAtom nextAtom = null;

		/*
		 *  try
		 *  {
		 *  logger.debug("Currently at atom no. " + (ac.getAtomNumber(root)  + 1) + " with symbol "  + root.getSymbol());
		 *  }
		 *  catch(Exception exc){}
		 */
		//Atom tempAtom = null;
		List newPath = null;
		//String symbol = null;
		String bondSymbol = null;
		currentDepth++;
		//logger.info("New incremented searchDepth " + currentDepth);
		//logger.info("Current Path is: " + currentPath);
		for (int f = 0; f < bonds.size(); f++)
		{
			IBond bond = (IBond)bonds.get(f);
			nextAtom = bond.getConnectedAtom(root);

			/*
			 *  try
			 *  {
			 *  logger.debug("Found connected atom no. " + (ac.getAtomNumber(nextAtom) + 1) + " with symbol "  + nextAtom.getSymbol() + "...");
			 *  }
			 *  catch(Exception exc){}
			 */
			if (!currentPath.contains(nextAtom))
			{
				newPath = new ArrayList(currentPath);
				bondSymbol = this.getBondSymbol(bond);
				newPath.add(bondSymbol);
				//logger.debug("Bond has symbol " + bondSymbol);
				newPath.add(nextAtom);
				checkAndStore(newPath, paths);

				if (currentDepth < searchDepth)
				{
					depthFirstSearch(ac, nextAtom, paths, newPath, currentDepth, searchDepth);
					//logger.debug("DepthFirstSearch Fallback to searchDepth " + currentDepth);
				}
			} else
			{
				//logger.debug("... already visited!");
			}
		}
	}

	private void checkAndStore(List newPath, Map paths)
	{
		StringBuilder newPathString = new StringBuilder();
        for (Object aNewPath : newPath) {
            if (aNewPath instanceof IAtom) {
                newPathString.append(convertSymbol(((IAtom) aNewPath).getSymbol()));
            } else {
                newPathString.append((String) aNewPath);
            }
        }
		//logger.debug("Checking for existence of Path " +  newPathString);
		String storePath = newPathString.toString();
		String reversePath = newPathString.reverse().toString();
		/*
		 *  Pathes can be found twice (search from one or the other side)
		 *  so they will occur in reversed order. We only handle the
		 *  lexicographically smaller path (This is an arbitrary choice)
		 */
		if (reversePath.compareTo(storePath) < 0)
		{
			/*
			 *  reversePath is smaller than newPath
			 *  so we keep reversePath
			 */
			storePath = reversePath;
		}
		if (!paths.containsKey(storePath))
		{
			paths.put(storePath, storePath);
			if (debug)
			{
				debugCounter++;
				//logger.debug("Storing path no. " + debugCounter + ": " +  storePath + ", Hash: " + storePath.hashCode());
			}
		} else
		{
			//logger.debug("Path " + storePath + " already contained");
		}
	}

	private String convertSymbol(String symbol)
	{

        String returnSymbol = symbol;
		if (symbol.equals("Cl"))
		{
			symbol = "X";
		} else if (symbol.equals("Si"))
		{
			symbol = "Y";
		} else if (symbol.equals("Br"))
		{
			symbol = "Z";
		}
		return returnSymbol;

	}


	/**
	 *  Gets the bondSymbol attribute of the Fingerprinter class
	 *
	 *@param  bond  Description of the Parameter
	 *@return       The bondSymbol value
	 */
	protected String getBondSymbol(org.openscience.cdk.interfaces.IBond bond)
	{
		String bondSymbol = "";
		if (bond.getFlag(CDKConstants.ISAROMATIC))
		{
			bondSymbol = ":";
		} else if (bond.getOrder() == IBond.Order.SINGLE)
		{
			bondSymbol = "-";
		} else if (bond.getOrder() == IBond.Order.DOUBLE)
		{
			bondSymbol = "=";
		} else if (bond.getOrder() == IBond.Order.TRIPLE)
		{
			bondSymbol = "#";
		}
		return bondSymbol;
	}

    @TestMethod("testGetSearchDepth")
    public int getSearchDepth() {
		return searchDepth;
	}

    @TestMethod("testGetSize")
	public int getSize() {
		return size;
	}

}

