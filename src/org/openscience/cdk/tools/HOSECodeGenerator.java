/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.tools;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.invariant.CanonicalLabeler;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.SSSRFinder;

/**
 * Generates HOSE codes {@cdk.cite BRE78}.
 *
 * @author     steinbeck
 * @cdk.created    2002-05-10
 * @cdk.require swing
 */
public class HOSECodeGenerator implements java.io.Serializable
{
	/**
	 *  Container for the nodes in a sphere.
	 */
	protected Vector sphereNodes = null;
  protected Vector sphereNodesWithAtoms = null;
	/**
	 *  Container for the node in the next sphere Assembled in a recursive method
	 *  and then passed to the next recursion to become "sphereNodes"
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
	 *  Here we store the spheres that we assemble, in order to parse them into a
	 *  code later.
	 */
	protected Vector[] spheres = null;
  protected Vector[] spheresWithAtoms = null;

	/**
	 *  The HOSECode string that we assemble
	 */
	protected StringBuffer HOSECode = null;

	/**
	 *  The molecular structure on which we work
	 */
	protected IAtomContainer atomContainer;

	/**
	 *  Delimiters used to separate spheres in the output string. Bremser uses the
	 *  sequence"(//)" for the first four spheres.
	 */
	protected String[] sphereDelimiters =
			{
			"(", "/", "/", ")", "/", "/", "/", "/", "/", "/", "/", "/"
			};
	/**
	 *  The bond symbols used for bond orders "single", "double", "triple" and
	 *  "aromatic"
	 */
	protected String bondSymbols[] =
			{
			"", "", "=", "%", "*"
			};
			
	protected String centerCode = null;			
			
	public TreeNode rootNode = null;
			
			
	boolean debug = false;
	
	private IAtomContainer acold=null;
	private IRingSet soar=null;

	/**
	 *  The rank order for the given element symbols
	 */

	static final String[] rankedSymbols =
			{
			"C", "O", "N", "S", "P", "Si", "B", "F", "Cl", "Br", ";", "I", "#", "&", ","
			};

	/**
	 *  The ranking values to be used for the symbols above
	 */
	static final int[] symbolRankings =
			{
			9000, 8900, 8800, 8700,
			8600, 8500, 8400, 8300, 8200, 8100, 8000, 7900, 1200, 1100, 1000
			};

	/**
	 *  The bond rankings to be used for the four bond order possibilities
	 */

	static final int[] bondRankings =
			{
			0, 0, 200000, 300000, 100000
			};


	/**
	 *  Constructor for the HOSECodeGenerator
	 */
	public HOSECodeGenerator()
	{
		sphereNodes = new Vector();
    sphereNodesWithAtoms = new Vector();
		nextSphereNodes = new Vector();
		HOSECode = new StringBuffer();
	}
  
  
  public Vector[] getSpheres(Molecule ac, IAtom root, int noOfSpheres, boolean ringsize) throws org.openscience.cdk.exception.CDKException
	{
		centerCode = "";
		this.atomContainer = ac;
		maxSphere = noOfSpheres;
		spheres = new Vector[noOfSpheres + 1];
    spheresWithAtoms = new Vector[noOfSpheres + 1];
		for (int i = 0; i < ac.getAtomCount(); i++)
		{
			ac.getAtomAt(i).setFlag(CDKConstants.VISITED, false);
		}
		root.setFlag(CDKConstants.VISITED, true);
		rootNode = new TreeNode(root.getSymbol(), null, root, (double)0, atomContainer.getBondCount(root), 0);
		/*
		 *  All we need to observe is how the ranking of substituents
		 *  in the subsequent spheres of the root nodes influences the
		 *  ranking of the first sphere, sinces the order of a node in a sphere
		 *  depends on the order the preceding node in its branch
		 */
		HOSECode = new StringBuffer();
		createCenterCode(root,ac,ringsize);
		breadthFirstSearch(root, false);
		createCode();
		fillUpSphereDelimiters();
		if (debug)
		{
			System.out.println("HOSECodeGenerator -> HOSECode: " + HOSECode.toString());
		}
		return spheresWithAtoms;
	}


	/**
	 *  Produces a HOSE code for Atom 'root' in the AtomContainer 'ac'. The HOSE
	 *  code is produced for the number of spheres given by noOfSpheres
	 *  IMPORTANT: if you want aromaticity to be included in the code, you need
	 *  to run the AtomContainer ac to the HueckelAromaticityDetector prior to 
	 *  using getHOSECode(). This method only gives proper results if the molecule is
 *  fully saturated (if not, the order of the HOSE code might depend on atoms in higher spheres).
 *  This method is known to fail for protons sometimes.
	 *
	 *@param  ac  The AtomContainer with the molecular skeleton in which the root atom resides
	 *@param  root The root atom for which to produce the HOSE code
	 *@param  noOfSpheres  The number of spheres to look at
	 *@return The HOSECode value
	 *@exception  org.openscience.cdk.exception.CDKException  Thrown if something is wrong
	 */
	public String getHOSECode(IAtomContainer ac, IAtom root, int noOfSpheres) throws org.openscience.cdk.exception.CDKException
	{
		return getHOSECode(ac,root,noOfSpheres, false);
	}
	
	
	/**
	 *  Produces a HOSE code for Atom 'root' in the AtomContainer 'ac'. The HOSE
	 *  code is produced for the number of spheres given by noOfSpheres
	 *  IMPORTANT: if you want aromaticity to be included in the code, you need
	 *  to run the AtomContainer ac to the HueckelAromaticityDetector prior to 
	 *  using getHOSECode(). This method only gives proper results if the molecule is
   *  fully saturated (if not, the order of the HOSE code might depend on atoms in higher spheres).
   *  This method is known to fail for protons sometimes.
	 *
	 *@param  ac  The AtomContainer with the molecular skeleton in which the root atom resides
	 *@param  root The root atom for which to produce the HOSE code
	 *@param  noOfSpheres  The number of spheres to look at
	 *@param  ringsize  The size of the ring(s) it is in is included in center atom code
	 *@return The HOSECode value
	 *@exception  org.openscience.cdk.exception.CDKException  Thrown if something is wrong
	 */
	public String getHOSECode(IAtomContainer ac, IAtom root, int noOfSpheres, boolean ringsize) throws org.openscience.cdk.exception.CDKException
	{
    CanonicalLabeler canLabler = new CanonicalLabeler();
    canLabler.canonLabel(ac);
		centerCode = "";
		this.atomContainer = ac;
		maxSphere = noOfSpheres;
		spheres = new Vector[noOfSpheres + 1];
		for (int i = 0; i < ac.getAtomCount(); i++)
		{
			ac.getAtomAt(i).setFlag(CDKConstants.VISITED, false);
		}
		root.setFlag(CDKConstants.VISITED, true);
		rootNode = new TreeNode(root.getSymbol(), null, root, (double)0, atomContainer.getBondCount(root), 0);
		/*
		 *  All we need to observe is how the ranking of substituents
		 *  in the subsequent spheres of the root nodes influences the
		 *  ranking of the first sphere, sinces the order of a node in a sphere
		 *  depends on the order the preceding node in its branch
		 */
		HOSECode = new StringBuffer();
		createCenterCode(root, ac, ringsize);
		breadthFirstSearch(root,true);
		createCode();
		fillUpSphereDelimiters();
		if (debug)
		{
			System.out.println("HOSECodeGenerator -> HOSECode: " + HOSECode.toString());
		}
		return HOSECode.toString();
	}

	private void createCenterCode(IAtom root, IAtomContainer ac, boolean ringsize)
	{
		int partnerCount = 0;
		partnerCount = atomContainer.getBondCount(root) + root.getHydrogenCount(); 
		centerCode = root.getSymbol() + "-" + partnerCount + createChargeCode(root)+(ringsize ? getRingcode(root, ac) : "" )+";";
	}
	
	
	private String getRingcode(IAtom root, IAtomContainer ac){
		if(ac!=acold){
			soar=new SSSRFinder(ac).findSSSR();
		}
		boolean[] bool=new boolean[1000];
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<soar.getRings(root).size();i++){
			if(((IRing)soar.getRings(root).get(i)).getAtomCount()<bool.length)
				bool[((IRing)soar.getRings(root).get(i)).getAtomCount()]=true;
		}
		for(int i=0;i<bool.length;i++){
			if(bool[i])
				sb.append(i+"");
		}
		if(sb.toString().equals(""))
			return "";
		else
			return "-"+sb.toString();
	}
  
  private String createChargeCode(IAtom atom){
    StringBuffer tempCode=new StringBuffer();
    if (atom != null && atom.getFormalCharge()!=0){
      if(Math.abs(atom.getFormalCharge())==1){
        if(atom.getFormalCharge()<0)
          tempCode.append("-");
        else
          tempCode.append("+");
      }else{
        tempCode.append("'");
        if(atom.getFormalCharge()>0)
          tempCode.append("+");
        tempCode.append(atom.getFormalCharge()+"'");
      }
    }
    return(tempCode+"");
  }

	/**
	 *  Prepares for a breadth first search within the AtomContainer. The actual
	 *  recursion is done in nextSphere()
	 *
	 *@param  root  The atom at which we start the search
	 *@exception  org.openscience.cdk.exception.CDKException  If something goes wrong.
	 */
	private void breadthFirstSearch(IAtom root,boolean addTreeNode) throws org.openscience.cdk.exception.CDKException
	{
		sphere = 0;
		TreeNode tempNode = null;
		IAtom[] conAtoms = atomContainer.getConnectedAtoms(root);
		IAtom atom;
		org.openscience.cdk.interfaces.IBond bond = null;
		sphereNodes.removeAllElements();
    sphereNodesWithAtoms.removeAllElements();
		for (int i = 0; i < conAtoms.length; i++)
		{

			try
			{
				atom = conAtoms[i];
				bond = atomContainer.getBond(root, atom);
				/*
				 *  In the first sphere the atoms are labled with
				 *  their own atom atom as source
				 */
				if (bond.getFlag(CDKConstants.ISAROMATIC))
				{
					tempNode = new TreeNode(atom.getSymbol(), new TreeNode(root.getSymbol(), null, root, (double) 0, 0, (long) 0), atom, 4, atomContainer.getBondCount(atom), 0);
				} else
				{
					tempNode = new TreeNode(atom.getSymbol(), new TreeNode(root.getSymbol(), null, root, (double) 0, 0, (long) 0), atom, bond.getOrder(), atomContainer.getBondCount(atom), 0);
				}
        sphereNodes.addElement(tempNode);
        if(!addTreeNode)
          sphereNodesWithAtoms.addElement(atom);
				rootNode.childs.addElement(tempNode);
				atom.setFlag(CDKConstants.VISITED, true);
			} catch (Exception exc)
			{
				throw new CDKException("Error in HOSECodeGenerator->breadthFirstSearch.", exc);
			}
		}
    Collections.sort(sphereNodes,new TreeNodeComparator());
		nextSphere(sphereNodes);
	}

	/**
	 *  The actual recursion method for our breadth first search Each node in
	 *  sphereNodes is inspected for its decendants which are then stored in
	 *  nextSphereNodes, which again is passed to the next recursion level of
	 *  nextSphere()
	 *
	 *@param  sphereNodes The sphereNodes to be inspected
	 *@exception  org.openscience.cdk.exception.CDKException  If something goes wrong
	 */
	private void nextSphere(Vector sphereNodes) throws org.openscience.cdk.exception.CDKException
	{
		spheres[sphere] = sphereNodes;
    if(spheresWithAtoms!=null)
      spheresWithAtoms[sphere] = sphereNodesWithAtoms;
		/*
		 *  From here we start assembling the next sphere
		 */
        IAtom node = null;
        IAtom toNode = null;
        IAtom[] conAtoms = null;
		TreeNode treeNode = null;
		nextSphereNodes = new Vector();
		org.openscience.cdk.interfaces.IBond bond = null;
		for (int i = 0; i < sphereNodes.size(); i++)
		{
			treeNode = (TreeNode) sphereNodes.elementAt(i);
			if (!("&;#:,".indexOf(treeNode.symbol) >= 0))
			{
				node = treeNode.atom;
				conAtoms = atomContainer.getConnectedAtoms(node);
				if (conAtoms.length == 1)
				{
          nextSphereNodes.addElement(new TreeNode(",", treeNode, null, 0, 0, treeNode.score));
				} else
				{
					for (int j = 0; j < conAtoms.length; j++)
					{
						toNode = conAtoms[j];
						if (toNode != treeNode.source.atom)
						{
							bond = atomContainer.getBond(node, toNode);
							if (bond.getFlag(CDKConstants.ISAROMATIC))
							{
								nextSphereNodes.addElement(new TreeNode(toNode.getSymbol(), treeNode, toNode, 4, atomContainer.getBondCount(toNode), treeNode.score));
							} else
							{
								nextSphereNodes.addElement(new TreeNode(toNode.getSymbol(), treeNode, toNode, bond.getOrder(), atomContainer.getBondCount(toNode), treeNode.score));
							}
						}
					}
				}
			}
		}
    Collections.sort(nextSphereNodes,new TreeNodeComparator());
		if (sphere < maxSphere)
		{
			sphere++;
			nextSphere(nextSphereNodes);
		}
	}

	public String makeBremserCompliant(String code)
	{
		int sepIndex = code.indexOf(";");
		if (sepIndex >= 0)
		{
			code = code.substring(sepIndex + 1, code.length());	
		}
		return code;
	}
	
	/**
	 *  After recursivly having established the spheres and assigning each node an
	 *  appropriate score, we now generate the complete HOSE code.
	 *
	 *@exception  org.openscience.cdk.exception.CDKException  Thrown if something goes wrong
	 */
	private void createCode() throws org.openscience.cdk.exception.CDKException
	{
    Vector sphereNodes = null;
		TreeNode tn = null;
		for (int f = 0; f < atomContainer.getAtomCount(); f++)
		{
			atomContainer.getAtomAt(f).setFlag(CDKConstants.VISITED, false);
		}

		for (int f = 0; f < maxSphere; f++)
		{
			sphereNodes = spheres[maxSphere - f];
			for (int g = 0; g < sphereNodes.size(); g++)
			{
				tn = (TreeNode) sphereNodes.elementAt(g);
				if (tn.source != null)
				{
					tn.source.ranking += tn.degree;
				}
				
			}
		}

		for (int f = 0; f < maxSphere; f++)
		{
			sphereNodes = spheres[f];
			calculateNodeScores(sphereNodes);
			sortNodesByScore(sphereNodes);
		}

		for (int f = 0; f < maxSphere; f++)
		{
			sphereNodes = spheres[f];
			for (int g = 0; g < sphereNodes.size() ; g++)
			{
				tn = (TreeNode) sphereNodes.elementAt(g);
				tn.score += tn.ranking;
			}
			sortNodesByScore(sphereNodes);
		}
		for (int f = 0; f < maxSphere; f++)
		{
			sphereNodes = spheres[f];
			for (int g = 0; g < sphereNodes.size() ; g++)
			{
				tn = (TreeNode) sphereNodes.elementAt(g);
        String localscore=tn.score+"";
        while(localscore.length()<6){
          localscore="0"+localscore;
        }
        tn.stringscore=tn.source.stringscore+""+localscore;
			}
			sortNodesByScore(sphereNodes);
		}
		HOSECode.append(centerCode);
		for (int f = 0; f < maxSphere; f++)
		{
			sphere = f + 1;
			sphereNodes = spheres[f];
			String s = getSphereCode(sphereNodes);
			HOSECode.append(s);
		}
	}

	/**
	 *  Generates the string code for a given sphere
	 *
	 *@param  sphereNodes A vector of TreeNodes for which a string code is to be generated
	 *@return The SphereCode value
	 *@exception  org.openscience.cdk.exception.CDKException  Thrown if something goes wrong
	 */
	private String getSphereCode(Vector sphereNodes) throws org.openscience.cdk.exception.CDKException
	{
		if (sphereNodes == null || sphereNodes.size() < 1)
		{
			return sphereDelimiters[sphere - 1];
		}
		TreeNode treeNode = null;
		StringBuffer code = new StringBuffer();
		/*
		 *  append the tree node code to the HOSECode in
		 *  their now determined order, using commas to
		 *  separate nodes from different branches
		 */
		IAtom branch = ((TreeNode) (((TreeNode) sphereNodes.elementAt(0)).source)).atom;
		IAtom nextBranch;
		StringBuffer tempCode = null;
		nextBranch = null;
		for (int i = 0; i < sphereNodes.size(); i++)
		{
			treeNode = (TreeNode) sphereNodes.elementAt(i);
			tempCode = new StringBuffer();
			if (!treeNode.source.stopper && treeNode.source.atom != branch)
			{
				branch = treeNode.source.atom;
				code.append(",");
			}
			
			if (!treeNode.source.stopper && treeNode.source.atom == branch)
			{
				if (treeNode.bondType <= 4)
				{
					tempCode.append(bondSymbols[(int) treeNode.bondType]);
				} else
				{
					throw new CDKException("Unknown bond type");
				}
				if (treeNode.atom != null && !treeNode.atom.getFlag(CDKConstants.VISITED))
				{
					tempCode.append(getElementSymbol(treeNode.symbol));
				}
				else if (treeNode.atom != null && treeNode.atom.getFlag(CDKConstants.VISITED))
				{
					tempCode.append("&");
					treeNode.stopper = true;
				}
        code.append(tempCode+createChargeCode(treeNode.atom));
				treeNode.hSymbol = tempCode.toString();
			}
			if (treeNode.atom != null) treeNode.atom.setFlag(CDKConstants.VISITED, true);
			if (treeNode.source.stopper) treeNode.stopper = true;
		}
		code.append(sphereDelimiters[sphere - 1]);
		return code.toString();
	}


	/**
	 *  Gets the element rank for a given element symbol as given in Bremser's
	 *  publication
	 *
	 *@param  symbol  The element symbol for which the rank is to be determined
	 *@return         The element rank
	 */
	private double getElementRank(String symbol)
	{
		for (int f = 0; f < rankedSymbols.length; f++)
		{
			if (rankedSymbols[f].equals(symbol))
			{
				return symbolRankings[f];
			}
		}
        try {
            IIsotope isotope = IsotopeFactory.getInstance(new ChemObject().getBuilder()).getMajorIsotope(symbol);
            return ((double) 800000 - isotope.getMassNumber());
        } catch (Exception exception) {
            System.err.println("Could not find major isotope for this element!!! : " + symbol);
            System.err.println("Because of this error: " + exception.getMessage());
        }
        return (double)800000;
	}

	/**
	 *  Returns the Bremser-compatible symbols for a given element. Silicon, for
	 *  example, is actually "Q". :-)
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
	 *  Determines the ranking score for each node, allowing for a sorting of nodes
	 *  within one sphere.
	 *
	 *@param  sphereNodes The nodes for which the score is to be calculated.
	 *@exception  org.openscience.cdk.exception.CDKException  Thrown if something goes wrong.
	 */
	private void calculateNodeScores(Vector sphereNodes) throws org.openscience.cdk.exception.CDKException
	{
		TreeNode treeNode = null;
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
	private void sortNodesByScore(Vector sphereNodes)
	{
		Object obj;
		boolean changed;
		if (sphereNodes.size() == 0) return;
		/*
		 *  Now we sort by score
		 */
		do
		{
			changed = false;
			for (int i = 0; i < sphereNodes.size() - 1; i++)
			{
				if (((TreeNode) sphereNodes.elementAt(i + 1)).stringscore.compareTo(((TreeNode) sphereNodes.elementAt(i)).stringscore)>0)
				{
					obj = sphereNodes.elementAt(i + 1);
					sphereNodes.removeElementAt(i + 1);
					sphereNodes.insertElementAt(obj, i);
					changed = true;
				}
			}
		} while (changed);
		/* Having sorted a sphere, we lable the nodes with their sort order */
		TreeNode temp = null;
		for (int i = 0; i < sphereNodes.size(); i++)
		{
			temp = ((TreeNode) sphereNodes.elementAt(i));
			temp.sortOrder = sphereNodes.size() - i;
		}
	}

	/**
	 *  If we use less than four sphere, this fills up the code with the missing
	 *  delimiters such that we are compatible with Bremser's HOSE code table.
	 */
	private void fillUpSphereDelimiters()
	{
		if (debug) System.out.println("Sphere: " + sphere);
		for (int f = sphere; f < 4; f++)
		{
			HOSECode.append(sphereDelimiters[f]);
		}
	}

	public DefaultMutableTreeNode getRootNode()
	{
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(rootNode);
		DefaultMutableTreeNode workNode = null;
		DefaultMutableTreeNode rootNode = null;
		Vector treeNodes = new Vector();
		Vector newTreeNodes = new Vector();
		Vector sphereNodes = null;
		TreeNode tn = null;
		sphereNodes = spheres[0];
		for (int g = 0; g < sphereNodes.size(); g++)
		{
			tn = (TreeNode) sphereNodes.elementAt(g);
			workNode = new DefaultMutableTreeNode(tn); 
			top.add(workNode);
			treeNodes.add(workNode);
		}
		for (int f = 1; f < maxSphere; f++)
		{
			sphereNodes = spheres[f];
			for (int h = 0; h < treeNodes.size(); h++)
			{
				rootNode = (DefaultMutableTreeNode)treeNodes.elementAt(h);
				for (int g = 0; g < sphereNodes.size(); g++)
				{
					tn = (TreeNode) sphereNodes.elementAt(g);
					if (tn.source == rootNode.getUserObject())
					{
						workNode = new DefaultMutableTreeNode(tn);
						rootNode.add(workNode);
						newTreeNodes.add(workNode);
						
					}
				}
			}
			treeNodes = newTreeNodes;
			newTreeNodes = new Vector();
		}
		return top;
	}
	
	
	class TreeNodeComparator implements Comparator {
    /**
     *The compare method, compares by canonical label of atoms
     *
     * @param  obj1  The first TreeNode
     * @param  obj2  The second TreeNode
     * @return       -1,0,1
     */
    public int compare(Object obj1, Object obj2) {
      if(obj1==null || obj2==null || ((TreeNode) obj1).getAtom()==null || ((TreeNode) obj2).getAtom()==null)
        return 0;
      if (((Long) ((TreeNode) obj1).getAtom().getProperty("CanonicalLable")).intValue() < ((Long) ((TreeNode) obj2).getAtom().getProperty("CanonicalLable")).intValue()) {
        return (-1);
      }
      if (((Long) ((TreeNode) obj1).getAtom().getProperty("CanonicalLable")).intValue() > ((Long) ((TreeNode) obj2).getAtom().getProperty("CanonicalLable")).intValue()) {
        return (1);
      }
      return (0);
    }
  }
    
  /**
	 *  Helper class for storing the properties of a node in our breadth first
	 *  search
	 *
	 * @author     steinbeck
	 * @cdk.created    2002-11-16
	 */
	class TreeNode
	{
		String symbol;
		TreeNode source;
		IAtom atom;
		double bondType;
		int degree;
		long score;
		int ranking;
		int sortOrder = 1;
		Vector childs = null;
		String hSymbol = null;
		boolean stopper = false;
    String stringscore="";

		/**
		 *  Constructor for the TreeNode object
		 *
		 *@param  symbol    The Element symbol of the node
		 *@param  source    The preceding node for this node
		 *@param  atom      The cdk atom object belonging to this node
		 *@param  bondType  The bond type by which this node was connect to its
		 *      predecessor
		 *@param  score     The score used to rank this node within its sphere.
		 *@param  degree    Description of the Parameter
		 */
		TreeNode(String symbol, TreeNode source, IAtom atom, double bondType, int degree, long score)
		{
			this.symbol = symbol;
			this.source = source;
			this.atom = atom;
			this.degree = degree;
			this.score = score;
			this.bondType = bondType;
			ranking = 0;
			sortOrder = 1;
			childs = new Vector();
		}
    
    public IAtom getAtom(){
      return atom;
    }


		/**
		 *  A TreeNode is equal to another TreeNode if it
		 *  stands for the same atom object
		 *
		 *@param  o  The object tht we compare this TreeNode to
		 *@return    True, if the this TreeNode's atom object equals the one of the other TreeNode
		 */
		public boolean equals(Object o)
		{
			try
			{
				if (this.atom == ((TreeNode) o).atom)
				{
					return true;
				}
			}
			catch(Exception exc)
			{
				/* we do nothing here because anything 
				that could seriously happen here is the we 
				got something which is not a TreeNode and then got 
				a class cast exception. Thus we can just wait until the
				end of the method returns a "false" */
			}
			return false;
		}
		
		public String toString()
		{
			String s = "";
			try
			{
				s += (atomContainer.getAtomNumber(atom) + 1);
				s += " " + hSymbol;
				s += "; s=" + score; 
				s += "; r=" + ranking;
				s += "; d = " + degree;
			}
			catch(Exception exc)
			{
				return exc.toString();	
			}
			return s;
		}
	}
  
  
  public Vector getNodesInSphere(int sphereNumber){
			sphereNodes = spheres[sphereNumber-1];
      Vector atoms=new Vector();
			for (int g = 0; g < sphereNodes.size() ; g++)
			{
				atoms.add(((TreeNode) sphereNodes.elementAt(g)).atom);
			}
      return(atoms);
  }
}


