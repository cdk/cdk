/* HOSECodeGenerator.java
 *
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2001  The JChemPaint project
 *
 * Contact: steinbeck@ice.mpg.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.tools;

import java.util.Vector;
import org.openscience.cdk.*;
 
public class HOSECodeGenerator implements java.io.Serializable, CDKConstants
{

    boolean debug = false;
	
	static String[] rankedSymbols = 
	{
		"C","O","N","S","P","Si","B","F","Cl","Br",";", "I", "#", "&", ","
	};
	
	static int[] symbolRankings = 
	{
		9000,8900,8800,8700,
		8600,8500,8400,8300,8200,8100,8000,7900, 1200, 1100, 1000 	
	};

	static int[] bondRankings = 
	{
		0, 0, 200000, 300000, 100000	
	};

	protected int[] ranking;
	protected Vector sphereNodes = null;
	protected Vector nextSphereNodes = null;
//	protected setOfRings sof = null;
//	protected Atom[] setOfNodes = null;
	protected int[] ringScores;
	protected int sphere = 0;
	protected int maxSphere = 0;  
	protected StringBuffer HOSECode = null;
	
	protected Molecule molecule;
	
	
	protected String[] sphereDelimiters = 
	{
		"(", "/", "/", ")", "/", "/", "/", "/", "/", "/", "/", "/"
	};
	protected String bondSymbols[] = 
	{ 
		"", "", "=", "%", "*" 
	};
	
	
	public HOSECodeGenerator()
	{
		sphereNodes = new Vector();
		nextSphereNodes = new Vector();
		HOSECode = new StringBuffer();
	}
	
	public String getHOSECode(Molecule molecule, Atom root, int noOfSpheres)
	{
		this.molecule = molecule;
		maxSphere = noOfSpheres;
		for (int i = 0; i < molecule.getAtomCount(); i++)
		{
			molecule.getAtomAt(i).flags[VISITED] = false;
		}
		root.flags[VISITED] = true;
			
		/* All we need to observe is how the ranking of substituents
		   in the subsequent spheres of the root nodes influences the
		   ranking of the first sphere, sinces the order of a node in a sphere
		   depends on the order the preceding node in its branch */
		//makeRingList();
		ranking = new int[molecule.getDegree(root)];
		HOSECode = new StringBuffer();
		breadthFirstSearch(root);	
		fillUpSphereDelimiters();
		// remove H's at the start of the string
		// this should fix JCP bug #126050
		while (HOSECode.charAt(0) == 'H') {
		    HOSECode = HOSECode.deleteCharAt(0);
		}
		if (debug) System.out.println("HOSECodeGenerator -> HOSECode: " + HOSECode.toString());
		return 	HOSECode.toString();	
	}
	
	private void breadthFirstSearch(Atom root)
	{
		sphere = 0;
		Atom[] conAtoms = molecule.getConnectedAtoms(root);
		Atom atom;
		Bond bond = null;
		sphereNodes.removeAllElements();
		for (int i = 0; i < conAtoms.length; i++)
		{
			atom = conAtoms[i];
			try
			{
				bond = molecule.getBond(root, atom);
			}
			catch (Exception exc)
			{
				exc.printStackTrace();
			}
			/* In the first sphere the atoms are labled with 
			   their own atom number as source */
			sphereNodes.addElement(new TreeNode(root.getElement().getSymbol(), root, atom, bond.getOrder(), 0));
			atom.flags[VISITED] = true;
		}
		nextSphere(sphereNodes);
	}
	
	private void nextSphere(Vector sphereNodes)
	{
		sphere++;
		if (debug)System.out.println("nextSphere() -> sphere: " + sphere);
		if (debug)System.out.println("nextSphere() -> maxSphere: " + maxSphere);
		if (sphere > maxSphere) return;  
		String s = getSphereCode(sphereNodes);
		if (debug)System.out.println("nextSphere() -> getSphereCode(): " + s);
		HOSECode.append(s);
		if (debug)System.out.println("nextSphere() -> printing the HOSECode StringBuffer: " + HOSECode.toString());		
		Atom node = null;
		Atom toNode = null;
		Atom[] conAtoms = null;
		TreeNode treeNode = null;		
		nextSphereNodes = new Vector();
		for (int i = 0; i < sphereNodes.size(); i++)
		{
			treeNode = (TreeNode)sphereNodes.elementAt(i);
			if (!("&;#:,".indexOf(treeNode.symbol)>=0))
			{
				node = treeNode.number;
				conAtoms = molecule.getConnectedAtoms(node);
				if (conAtoms.length == 1)
				{
					nextSphereNodes.addElement(new TreeNode(",", node, null, 0, treeNode.score  * 1000000));
				}
				else
				{
					for (int j = 0; j < conAtoms.length; j++)
					{
						try
						{
							if (debug) System.out.println("HOSECodeGenerator->nextSphere()->node.number:" + molecule.getAtomNumber(node));			
							toNode = conAtoms[j];
							if (debug) System.out.println("HOSECodeGenerator->nextSphere()->toNode.number:" + molecule.getAtomNumber(toNode));
							if (!toNode.flags[VISITED])							
							{			   			   
								nextSphereNodes.addElement(new TreeNode(toNode.getElement().getSymbol(), node, toNode, molecule.getBond(node, toNode).getOrder(), treeNode.score  * 1000000));
								toNode.flags[VISITED] = true;
							}
							else if (!toNode.equals(treeNode.source))
							{
								nextSphereNodes.addElement(new TreeNode("&", node, toNode, molecule.getBond(node, toNode).getOrder(), treeNode.score  * 1000000));
							}
						}
						catch (Exception exc)
						{
							exc.printStackTrace();
						}
					}
				}
			}
		}
		nextSphere(nextSphereNodes);
	}
	
	private String getSphereCode(Vector sphereNodes)
	{
		if (sphereNodes == null || sphereNodes.size() < 1) return "";
		boolean[] done = new boolean[sphereNodes.size()];
		Atom node = null;
		TreeNode treeNode = null;		

		StringBuffer code = new StringBuffer();
		calculateNodeScores(sphereNodes);
		sortNodes(sphereNodes);
		/* append the tree node code to the HOSECode in 
		   their now determined order, using commas to 
		   separate nodes from different branches */
		Atom branch = ((TreeNode)sphereNodes.elementAt(0)).source;
		Atom nextBranch;
		int start = 0;
		do
		{
			nextBranch = null;
			for (int i = start; i < sphereNodes.size(); i ++)
			{
				treeNode = (TreeNode)sphereNodes.elementAt(i);
				if (treeNode.source == branch && !done[i])
				{
				    if (treeNode.bondType <= 4) {
					code.append(bondSymbols[treeNode.bondType]);
				    } else {
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
				if (debug) System.out.println("HOSECodeGenerator->getSphereCode()->treeNode.bondType:" + treeNode.bondType);
				if (debug) System.out.println("HOSECodeGenerator->getSphereCode()->treeNode.score:" + treeNode.score);
			}
			if (nextBranch != null)
			{
				code.append(",");
				branch = nextBranch;
				start ++;
			}
			
		}while(nextBranch != null);
		code.append(sphereDelimiters[sphere - 1]);
		return code.toString();
	}
	
	private	void calculateNodeScores(Vector sphereNodes)
	{
		TreeNode treeNode = null;
		Atom node = null;
		for (int i = 0; i < sphereNodes.size(); i ++)
		{
			treeNode = (TreeNode)sphereNodes.elementAt(i);
			treeNode.score += getElementRank(treeNode.symbol);
			if (treeNode.bondType <= 4) {
			    treeNode.score += bondRankings[treeNode.bondType];
			} else {
			    // what should be done here? with wedges for example?
			    // see SF bug #126841
			}
		}
	}
	
	private	void sortNodes(Vector sphereNodes)
	{
		Object obj;
		boolean changed;
		do
		{
			changed = false;
			for (int i = 0; i < sphereNodes.size() - 1; i ++)
			{
				if (((TreeNode)sphereNodes.elementAt(i + 1)).score > ((TreeNode)sphereNodes.elementAt(i)).score)
				{
					obj = sphereNodes.elementAt(i + 1);
					sphereNodes.removeElementAt(i + 1);
					sphereNodes.insertElementAt(obj,i);
					changed = true;
				}
			}			
		}while(changed);
	}
	
	
	private int getElementRank(String symbol)
	{
		if (debug)System.out.println("getElementRank() -> symbol: " + symbol);
		for (int f = 0; f < rankedSymbols.length; f++)
		{
			if (rankedSymbols[f].equals(symbol))
			{
				if (debug)System.out.println("getElementRank() -> symbolRankings[f]: " + symbolRankings[f]);
				return symbolRankings[f];
			}
		}
		Isotope isotope = (new StandardIsotopes()).getMajorIsotope(symbol);
		return (800000-isotope.atomicMass);
	}
	
	private String getElementSymbol(String sym)
	{
		if (sym.equals("Si")) return "Q";
		if (sym.equals("Cl")) return "X";
		if (sym.equals("Br")) return "Y";
		if (sym.equals(",")) return "";
		return sym;
	}
	
	private void fillUpSphereDelimiters()
	{
		for (int f = sphere; f < 5; f ++)
		{
			HOSECode.append(sphereDelimiters[f - 1]);
		}
	}
	
	
	class TreeNode
	{
		String symbol;
		Atom source;
		Atom number; 
		int bondType;
		long score;
		
		TreeNode(String symbol, Atom source, Atom number, int bondType, long score)
		{
			this.symbol = symbol;
			this.source = source;
			this.number = number;
			this.score = score;
			this.bondType = bondType;
			if (debug) System.out.println("This is tree node '" + symbol + "' no " + number + " with source " + source + " and bondType " + bondType);
		}
	}
}







