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
package org.openscience.cdk.tools;

import java.util.Vector;
import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;

/**
 *  Generates HOSE codes. References: <a
 *  href="http://cdk.sf.net/biblio.html#BRE78">BRE78</a>
 *
 *@author     steinbeck
 *@created    10 May 2002
 */
public class HOSECodeGenerator implements java.io.Serializable
{
	/**
	 *  Container for the nodes in a sphere.
	 */
	protected Vector sphereNodes = null;
	/**
	 *  Container for the node in the next sphere
	 * Assembled in a recursive method and then passed
	 * to the next recursion to become "sphereNodes"
	 */
	protected Vector nextSphereNodes = null;
	/**
	 *  Counter for the sphere in which we currently work
	 */
	protected int sphere = 0;
	/**
	 *  How many spheres are we supposed inspect.
	 */
	protected int maxSphere = 0;
	/**
	 *  The HOSECode string that we assemble
	 */
	protected StringBuffer HOSECode = null;

	/**
	 *  The molecular structure on which we work
	 */
	protected AtomContainer atomContainer;

	/**
	 *  Delimiters used to separate spheres in the 
	 * output string. Bremser uses the sequence"(//)"
	 * for the first four spheres.
	 */
	protected String[] sphereDelimiters =
			{
			"(", "/", "/", ")", "/", "/", "/", "/", "/", "/", "/", "/"
			};
	/**
	 *  The bond symbols used for bond orders
	 * "single", "double", "triple" and "aromatic"
	 */
	protected String bondSymbols[] =
			{
			"", "", "=", "%", "*"
			};

	boolean debug = false;

	/**
	 *  The rank order for the given element symbols
	 */

	static String[] rankedSymbols =
			{
			"C", "O", "N", "S", "P", "Si", "B", "F", "Cl", "Br", ";", "I", "#", "&", ","
			};

	/**
	 *  The ranking values to be used for the symbols above
	 */
	static int[] symbolRankings =
			{
			9000, 8900, 8800, 8700,
			8600, 8500, 8400, 8300, 8200, 8100, 8000, 7900, 1200, 1100, 1000
			};

	/**
	 *  The bond rankings to be used for the four bond order possibilities
	 */

	static int[] bondRankings =
			{
			0, 0, 200000, 300000, 100000
			};


	/**
	 *  Constructor for the HOSECodeGenerator
	 */
	public HOSECodeGenerator()
	{
		sphereNodes = new Vector();
		nextSphereNodes = new Vector();
		HOSECode = new StringBuffer();
	}


	/**
	 *  Produces a HOSE code for Atom 'root' in the AtomContainer 'ac'. The HOSE
	 *  code is produced for the number of spheres given by noOfSpheres
	 *
	 *@param  ac  The AtomContainer with the molecular skeleton in which the root atom resides
	 *@param  root The root atom for which to produce the HOSE code
	 *@param  noOfSpheres The number of spheres to look at
	 *@return  The HOSECode value
	 *@exception  org.openscience.cdk.exception.CDKException  Thrown if something is wrong
	 */
	public String getHOSECode(AtomContainer ac, Atom root, int noOfSpheres) throws org.openscience.cdk.exception.CDKException
	{
		this.atomContainer = ac;
		maxSphere = noOfSpheres;
		for (int i = 0; i < ac.getAtomCount(); i++)
		{
			ac.getAtomAt(i).flags[CDKConstants.VISITED] = false;
		}
		root.flags[CDKConstants.VISITED] = true;

		/*
		 *  All we need to observe is how the ranking of substituents
		 *  in the subsequent spheres of the root nodes influences the
		 *  ranking of the first sphere, sinces the order of a node in a sphere
		 *  depends on the order the preceding node in its branch
		 */
		HOSECode = new StringBuffer();
		breadthFirstSearch(root);
		fillUpSphereDelimiters();
		if (debug)
		{
			System.out.println("HOSECodeGenerator -> HOSECode: " + HOSECode.toString());
		}
		return HOSECode.toString();
	}


	/**
	 *  Prepares for a breadth first search within the AtomContainer
	 *  The actual recursion is done in nextSphere()
	 *
	 *@param  root  The atom at which we start the search
	 *@exception  org.openscience.cdk.exception.CDKException If something goes wrong.
	 */
	private void breadthFirstSearch(Atom root) throws org.openscience.cdk.exception.CDKException
	{
		sphere = 0;
		Atom[] conAtoms = atomContainer.getConnectedAtoms(root);
		Atom atom;
		Bond bond = null;
		sphereNodes.removeAllElements();
		for (int i = 0; i < conAtoms.length; i++)
		{

			try
			{
				atom = conAtoms[i];
				bond = atomContainer.getBond(root, atom);

				/*
				 *  In the first sphere the atoms are labled with
				 *  their own atom number as source
				 */
				if (bond.flags[CDKConstants.ISAROMATIC])
				{
					sphereNodes.addElement(new TreeNode(atom.getSymbol(), root, atom, 4, 0));
				} else
				{
					sphereNodes.addElement(new TreeNode(atom.getSymbol(), root, atom, bond.getOrder(), 0));
				}
				atom.flags[CDKConstants.VISITED] = true;

			} catch (Exception exc)
			{
				throw new CDKException("Error in HOSECodeGenerator->breadthFirstSearch.");
			}
		}
		nextSphere(sphereNodes);
	}


	/**
	 *  The actual recursion method for our breadth first search
	 * Each node in sphereNodes is inspected for its decendants which are
	 * then stored in nextSphereNodes, which again is passed to the 
	 * next recursion level of nextSphere()
	 *
	 *@param  sphereNodes The sphereNodes to be inspected 
	 *@exception  org.openscience.cdk.exception.CDKException If something goes wrong
	 */
	private void nextSphere(Vector sphereNodes) throws org.openscience.cdk.exception.CDKException
	{
		sphere++;
		if (debug)
		{
			System.out.println("nextSphere() -> sphere: " + sphere);
		}
		if (debug)
		{
			System.out.println("nextSphere() -> maxSphere: " + maxSphere);
		}
		if (sphere > maxSphere)
		{
			return;
		}
		String s = getSphereCode(sphereNodes);
		if (debug)
		{
			System.out.println("nextSphere() -> getSphereCode(): " + s);
		}
		HOSECode.append(s);
		if (debug)
		{
			System.out.println("nextSphere() -> printing the HOSECode StringBuffer: " + HOSECode.toString());
		}
		/* From here we start assembling the next sphere */
		Atom node = null;
		Atom toNode = null;
		Atom[] conAtoms = null;
		TreeNode treeNode = null;
		nextSphereNodes = new Vector();
		Bond bond = null;
		for (int i = 0; i < sphereNodes.size(); i++)
		{
			treeNode = (TreeNode) sphereNodes.elementAt(i);
			if (!("&;#:,".indexOf(treeNode.symbol) >= 0))
			{
				node = treeNode.number;
				conAtoms = atomContainer.getConnectedAtoms(node);
				if (conAtoms.length == 1)
				{
					nextSphereNodes.addElement(new TreeNode(",", node, null, 0, treeNode.score * 1000000));
				} else
				{
					for (int j = 0; j < conAtoms.length; j++)
					{
						if (debug)
						{
							System.out.println("HOSECodeGenerator->nextSphere()->node.number:" + atomContainer.getAtomNumber(node));
						}
						toNode = conAtoms[j];
						if (debug)
						{
							System.out.println("HOSECodeGenerator->nextSphere()->toNode.number:" + atomContainer.getAtomNumber(toNode));
						}

						if (!toNode.flags[CDKConstants.VISITED])
						{
							bond = atomContainer.getBond(node, toNode);			
							if (bond.flags[CDKConstants.ISAROMATIC])
							{
								nextSphereNodes.addElement(new TreeNode(toNode.getSymbol(), node, toNode, 4, treeNode.score * 1000000));
							} else
							{
								nextSphereNodes.addElement(new TreeNode(toNode.getSymbol(), node, toNode, bond.getOrder(), treeNode.score * 1000000));
							}
							toNode.flags[CDKConstants.VISITED] = true;
						} else if (!toNode.equals(treeNode.source))
						{
							nextSphereNodes.addElement(new TreeNode("&", node, toNode, atomContainer.getBond(node, toNode).getOrder(), treeNode.score * 1000000));
						}
					}
				}
			}
		}
		nextSphere(nextSphereNodes);
	}


	/**
	 *  Generates the string code for a given sphere
	 *
	 *@param  sphereNodes  A vector of tree nodes for which a string code is to be generated
	 *@return The SphereCode value
	 *@exception  org.openscience.cdk.exception.CDKException  Thrown if something goes wrong
	 */
	private String getSphereCode(Vector sphereNodes) throws org.openscience.cdk.exception.CDKException
	{
		if (sphereNodes == null || sphereNodes.size() < 1)
		{
			return "";
		}
		boolean[] done = new boolean[sphereNodes.size()];
		Atom node = null;
		TreeNode treeNode = null;

		StringBuffer code = new StringBuffer();
		calculateNodeScores(sphereNodes);
		sortNodes(sphereNodes);
		/*
		 *  append the tree node code to the HOSECode in
		 *  their now determined order, using commas to
		 *  separate nodes from different branches
		 */
		Atom branch = ((TreeNode) sphereNodes.elementAt(0)).source;
		Atom nextBranch;
		int start = 0;
		do
		{
			nextBranch = null;
			for (int i = start; i < sphereNodes.size(); i++)
			{
				treeNode = (TreeNode) sphereNodes.elementAt(i);
				if (treeNode.source == branch && !done[i])
				{
					if (treeNode.bondType <= 4)
					{
						code.append(bondSymbols[(int) treeNode.bondType]);
					} else
					{
						// what should be done here? with wedges for example?
						// see SF bug #126841
					}
					code.append(getElementSymbol(treeNode.symbol));
					done[i] = true;
				}
				if (treeNode.source != branch && nextBranch == null && !done[i])
				{
					nextBranch = treeNode.source;
				}
				if (debug)
				{
					System.out.println("HOSECodeGenerator->getSphereCode()->treeNode.bondType:" + treeNode.bondType);
				}
				if (debug)
				{
					System.out.println("HOSECodeGenerator->getSphereCode()->treeNode.score:" + treeNode.score);
				}
			}
			if (nextBranch != null)
			{
				code.append(",");
				branch = nextBranch;
				start++;
			}
		} while (nextBranch != null);
		code.append(sphereDelimiters[sphere - 1]);
		return code.toString();
	}


	/**
	 *  Gets the element rank for a given element symbol
	 *  as given in Bremser's publication
	 *
	 *@param  symbol  The element symbol for which the rank is to be determined 
	 *@return         The element rank
	 */
	private double getElementRank(String symbol)
	{
		if (debug)
		{
			System.out.println("getElementRank() -> symbol: " + symbol);
		}
		for (int f = 0; f < rankedSymbols.length; f++)
		{
			if (rankedSymbols[f].equals(symbol))
			{
				if (debug)
				{
					System.out.println("getElementRank() -> symbolRankings[f]: " + symbolRankings[f]);
				}
				return symbolRankings[f];
			}
		}
		System.out.println("IsotopSymbol: " + symbol);
		Isotope isotope = (new StandardIsotopes()).getMajorIsotope(symbol);
		return ((double) 800000 - isotope.getAtomicMass());
	}


	/**
	 *  Returns the Bremser-compatible symbols for a given element
	 *  Silicon, for example, is acutually "Q". :-)
	 *
	 *@param  sym  The element symbol to be converted
	 *@return      The converted symbol
	 */
	private String getElementSymbol(String sym)
	{
		if (sym.equals("Si"))
		{
			return "Q";
		}
		if (sym.equals("Cl"))
		{
			return "X";
		}
		if (sym.equals("Br"))
		{
			return "Y";
		}
		if (sym.equals(","))
		{
			return "";
		}
		return sym;
	}


	/**
	 *  Determins the ranking score for each node, allowing for a sorting 
	 * of nodes within one sphere.
	 *
	 *@param  sphereNodes The nodes for which the score is to be calculated.
	 *@exception  org.openscience.cdk.exception.CDKException  Thrown if something goes wrong.
	 */
	private void calculateNodeScores(Vector sphereNodes) throws org.openscience.cdk.exception.CDKException
	{
		TreeNode treeNode = null;
		Atom node = null;
		for (int i = 0; i < sphereNodes.size(); i++)
		{
			treeNode = (TreeNode) sphereNodes.elementAt(i);
			treeNode.score += getElementRank(treeNode.symbol);
			if (treeNode.bondType <= 4)
			{
				treeNode.score += bondRankings[(int) treeNode.bondType];
			} else
			{
				throw new CDKException("Unknown bond type encountered in HOSECodeGenerator");
			}
		}
	}


	/**
	 *  Sorts the nodes (atoms) in the sphereNode vector according to their score
	 *  This is used for the essential ranking of nodes in HOSE code sphere
	 *
	 *@param  sphereNodes  A vector with sphere nodes to be sorted.
	 */
	private void sortNodes(Vector sphereNodes)
	{
		Object obj;
		boolean changed;
		do
		{
			changed = false;
			for (int i = 0; i < sphereNodes.size() - 1; i++)
			{
				if (((TreeNode) sphereNodes.elementAt(i + 1)).score > ((TreeNode) sphereNodes.elementAt(i)).score)
				{
					obj = sphereNodes.elementAt(i + 1);
					sphereNodes.removeElementAt(i + 1);
					sphereNodes.insertElementAt(obj, i);
					changed = true;
				}
			}
		} while (changed);
	}


	/**
	 *  If we use less than four sphere, this fills up the code
	 * with the missing delimiters such that we are compatible with 
	 * Bremser's HOSE code table.
	 */
	private void fillUpSphereDelimiters()
	{
		for (int f = sphere; f < 5; f++)
		{
			HOSECode.append(sphereDelimiters[f - 1]);
		}
	}


	/**
	 *  Helper class for storing the properties of a node in our breadth first search
	 *
	 *@author     steinbeck
	 *@created    November 16, 2002
	 */
	class TreeNode
	{
		String symbol;
		Atom source;
		Atom number;
		double bondType;
		long score;


		/**
		 *  Constructor for the TreeNode object
		 *
		 *@param  symbol    The Element symbol of the node
		 *@param  source    The preceding node for this node
		 *@param  number    The cdk atom object belonging to this node
		 *@param  bondType  The bond type by which this node was connect to its predecessor
		 *@param  score     The score used to rank this node within its sphere.
		 */
		TreeNode(String symbol, Atom source, Atom number, double bondType, long score)
		{
			this.symbol = symbol;
			this.source = source;
			this.number = number;
			this.score = score;
			this.bondType = bondType;
			if (debug)
			{
				try
				{
					System.out.println("This is tree node '" + symbol + "' no " + atomContainer.getAtomNumber(number) + " with source " + atomContainer.getAtomNumber(source) + " and bondType " + bondType);
				} catch (Exception exc)
				{
					System.out.println("Error during debug output");
					exc.printStackTrace();
				}
			}
		}
	}
}
