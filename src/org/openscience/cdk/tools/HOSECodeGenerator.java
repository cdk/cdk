/*
 *  HOSECodeGenerator.java
 *
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
package org.openscience.cdk.tools;

import java.util.Vector;
import org.openscience.cdk.*;

/**
 * Generates HOSE codes.
 *
 * References:
 *   <a href="http://cdk.sf.net/biblio.html#BRE78">BRE78</a>
 *
 *@author     steinbeck
 *@created    10. Mai 2002
 */
public class HOSECodeGenerator implements java.io.Serializable, CDKConstants
{

	/**
	 *  Description of the Field
	 */
	protected int[] ranking;
	/**
	 *  Description of the Field
	 */
	protected Vector sphereNodes = null;
	/**
	 *  Description of the Field
	 */
	protected Vector nextSphereNodes = null;
//	protected setOfRings sof = null;
//	protected Atom[] setOfNodes = null;
	/**
	 *  Description of the Field
	 */
	protected int[] ringScores;
	/**
	 *  Description of the Field
	 */
	protected int sphere = 0;
	/**
	 *  Description of the Field
	 */
	protected int maxSphere = 0;
	/**
	 *  Description of the Field
	 */
	protected StringBuffer HOSECode = null;

	/**
	 *  Description of the Field
	 */
	protected AtomContainer atomContainer;

	/**
	 *  Description of the Field
	 */
	protected String[] sphereDelimiters =
			{
			"(", "/", "/", ")", "/", "/", "/", "/", "/", "/", "/", "/"
			};
	/**
	 *  Description of the Field
	 */
	protected String bondSymbols[] =
			{
			"", "", "=", "%", "*"
			};

	boolean debug = false;

	static String[] rankedSymbols =
			{
			"C", "O", "N", "S", "P", "Si", "B", "F", "Cl", "Br", ";", "I", "#", "&", ","
			};

	static int[] symbolRankings =
			{
			9000, 8900, 8800, 8700,
			8600, 8500, 8400, 8300, 8200, 8100, 8000, 7900, 1200, 1100, 1000
			};

	static int[] bondRankings =
			{
			0, 0, 200000, 300000, 100000
			};


	/**
	 *  Constructor for the HOSECodeGenerator object
	 */
	public HOSECodeGenerator()
	{
		sphereNodes = new Vector();
		nextSphereNodes = new Vector();
		HOSECode = new StringBuffer();
	}


	/**
	 *  Produces a HOSE code for Atom 'root' in the AtomContainer 'ac'.
	 * The HOSE code is produced for the number of spheres given by 
	 * noOfSpheres
	 *
	 *@param  ac           The molecular skeleton in which the root atom resides
	 *@param  root         The root atom for which to produce the HOSE code
	 *@param  noOfSpheres  The number of spheres to look at
	 *@return              The HOSECode value
	 */
	public String getHOSECode(AtomContainer ac, Atom root, int noOfSpheres)
	{
		this.atomContainer = ac;
		maxSphere = noOfSpheres;
		for (int i = 0; i < ac.getAtomCount(); i++)
		{
			ac.getAtomAt(i).flags[VISITED] = false;
		}
		root.flags[VISITED] = true;

		/*
		 *  All we need to observe is how the ranking of substituents
		 *  in the subsequent spheres of the root nodes influences the
		 *  ranking of the first sphere, sinces the order of a node in a sphere
		 *  depends on the order the preceding node in its branch
		 */
		//makeRingList();
		ranking = new int[atomContainer.getBondCount(root)];
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
	 *  Gets the SphereCode attribute of the HOSECodeGenerator object
	 *
	 *@param  sphereNodes  Description of Parameter
	 *@return              The SphereCode value
	 */
	private String getSphereCode(Vector sphereNodes)
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
					}
					else
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
	 *  Gets the ElementRank attribute of the HOSECodeGenerator object
	 *
	 *@param  symbol  Description of Parameter
	 *@return         The ElementRank value
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
		Isotope isotope = (new StandardIsotopes()).getMajorIsotope(symbol);
		return ((double) 800000 - isotope.getAtomicMass());
	}


	/**
	 *  Gets the ElementSymbol attribute of the HOSECodeGenerator object
	 *
	 *@param  sym  Description of Parameter
	 *@return      The ElementSymbol value
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
	 *  Description of the Method
	 *
	 *@param  root  Description of Parameter
	 */
	private void breadthFirstSearch(Atom root)
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
	
				sphereNodes.addElement(new TreeNode(atom.getSymbol(), root, atom, bond.getOrder(), 0));
				atom.flags[VISITED] = true;

			}
			catch (Exception exc)
			{
				//exc.printStackTrace();
			}
		}
		nextSphere(sphereNodes);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  sphereNodes  Description of Parameter
	 */
	private void nextSphere(Vector sphereNodes)
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
		Atom node = null;
		Atom toNode = null;
		Atom[] conAtoms = null;
		TreeNode treeNode = null;
		nextSphereNodes = new Vector();
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
				}
				else
				{
					for (int j = 0; j < conAtoms.length; j++)
					{
						try
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

							if (!toNode.flags[VISITED])
							{
								nextSphereNodes.addElement(new TreeNode(toNode.getSymbol(), node, toNode, atomContainer.getBond(node, toNode).getOrder(), treeNode.score * 1000000));
								toNode.flags[VISITED] = true;
							}
							else if (!toNode.equals(treeNode.source))
							{
								nextSphereNodes.addElement(new TreeNode("&", node, toNode, atomContainer.getBond(node, toNode).getOrder(), treeNode.score * 1000000));
							}
						}
						catch (Exception exc)
						{
							//exc.printStackTrace();
						}
					}
				}
			}
		}
		nextSphere(nextSphereNodes);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  sphereNodes  Description of Parameter
	 */
	private void calculateNodeScores(Vector sphereNodes)
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
			}
			else
			{
				// what should be done here? with wedges for example?
				// see SF bug #126841
			}
		}
	}


	/**
	 *  Sorts the nodes (atoms) in the sphereNode vector accordin to their score
	 * This is used for the essential ranking of nodes in HOSE code sphere
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
	 *  Description of the Method
	 */
	private void fillUpSphereDelimiters()
	{
		for (int f = sphere; f < 5; f++)
		{
			HOSECode.append(sphereDelimiters[f - 1]);
		}
	}


	/**
	 *  Description of the Class
	 *
	 *@author     steinbeck
	 *@created    10. Mai 2002
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
		 *@param  symbol    Description of Parameter
		 *@param  source    Description of Parameter
		 *@param  number    Description of Parameter
		 *@param  bondType  Description of Parameter
		 *@param  score     Description of Parameter
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
				}
				catch (Exception exc)
				{
					System.out.println("Error during debug output");
					exc.printStackTrace();
				}
			}
		}
	}
}




