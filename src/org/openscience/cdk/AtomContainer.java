/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk;

import java.util.Enumeration;
import java.util.Vector;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

/**
 *  Base class for all chemical objects that maintain a list of Atoms and
 *  ElectronContainers. <p>
 *
 *  Looping over all Bonds in the AtomContainer is typically done like: <pre>
 *  Bond[] bonds = atomContainer.getBonds();
 *  for (int i = 0; i < bonds.length; i++) {
 *      Bond b = bonds[i];
 *  }
 *  </pre>
 *
 * @cdk.module core
 *
 * @author     steinbeck
 * @cdk.created    2000-10-02
 */
public class AtomContainer extends ChemObject implements java.io.Serializable, Cloneable {

	/**
	 *  Number of atoms contained by this object.
	 */
	protected int atomCount;

	/**
	 *  Number of electronContainers contained by this object.
	 */
	protected int electronContainerCount;

	/**
	 *  Amount by which the bond and arom arrays grow when elements are added and
	 *  the arrays are not large enough for that.
	 */
	protected int growArraySize = 10;

	/**
	 *  Internal array of atoms.
	 */
	protected Atom[] atoms;

	/**
	 *  Internal array of bond.
	 */
	protected ElectronContainer[] electronContainers;


	/**
	 *  Constructs an empty AtomContainer.
	 */
	public AtomContainer() {
        this(10, 10);
	}


	/**
	 *  Constructs an AtomContainer with a copy of the atoms and electronContainers
	 *  of another AtomContainer (A shallow copy, i.e., with the same objects as in
	 *  the original AtomContainer).
	 *
	 *@param  ac  An AtomContainer to copy the atoms and electronContainers from
	 */
	public AtomContainer(AtomContainer ac)
	{
		this();
		this.add(ac);
	}


	/**
	 *  Constructs an empty AtomContainer that will contain a certain number of
	 *  atoms and electronContainers. It will set the starting array lengths to the
	 *  defined values, but will not create any Atom or ElectronContainer's.
	 *
	 *@param  atomCount               Number of atoms to be in this container
	 *@param  electronContainerCount  Number of electronContainers to be in this
	 *      container
	 */
	public AtomContainer(int atomCount, int electronContainerCount)
	{
		this.atomCount = 0;
		this.electronContainerCount = 0;
		atoms = new Atom[atomCount];
		electronContainers = new ElectronContainer[electronContainerCount];
	}


	/**
	 *  Sets the array of atoms of this AtomContainer.
	 *
	 *@param  atoms  The array of atoms to be assigned to this AtomContainer
	 *@see           #getAtoms
	 */
	public void setAtoms(Atom[] atoms)
	{
		this.atoms = atoms;
		setAtomCount(atoms.length);

	}


	/**
	 *  Sets the array of electronContainers of this AtomContainer.
	 *
	 *@param  electronContainers  The array of electronContainers to be assigned to
	 *      this AtomContainer
	 *@see  #getElectronContainers
	 */
	public void setElectronContainers(Bond[] electronContainers)
	{
		this.electronContainers = electronContainers;
		setElectronContainerCount(electronContainers.length);
	}


	/**
	 *  Set the atom at position <code>number</code> in [0,..].
	 *
	 *@param  number  The position of the atom to be set.
	 *@param  atom    The atom to be stored at position <code>number</code>
	 *@see            #getAtomAt
	 */
	public void setAtomAt(int number, Atom atom)
	{
		atoms[number] = atom;
	}


	/**
	 *  Get the atom at position <code>number</code> in [0,..].
	 *
	 *@param  number  The position of the atom to be retrieved.
	 *@return         The atomAt value
	 *@see            #setAtomAt
	 */
	public Atom getAtomAt(int number)
	{
		return atoms[number];
	}


	/**
	 *  Get the bond at position <code>number</code> in [0,..].
	 *
	 *@param  number  The position of the bond to be retrieved.
	 *@return         The bondAt value
	 *@see            #setElectronContainerAt
	 */
	public Bond getBondAt(int number)
	{
		return getBonds()[number];
	}



	/**
	 *  Sets the ElectronContainer at position <code>number</code> in [0,..].
	 *
	 *@param  number  The position of the ElectronContainer to be set.
	 *@param  ec      The ElectronContainer to be stored at position <code>number</code>
	 *@see            #getElectronContainerAt
	 */
	public void setElectronContainerAt(int number, ElectronContainer ec)
	{
		electronContainers[number] = ec;
	}


	/**
	 *  Sets the number of electronContainers in this container.
	 *
	 *@param  electronContainerCount  The number of electronContainers in this
	 *      container
	 *@see                            #getElectronContainerCount
	 */
	public void setElectronContainerCount(int electronContainerCount)
	{
		this.electronContainerCount = electronContainerCount;
	}


	/**
	 *  Sets the number of atoms in this container.
	 *
	 *@param  atomCount  The number of atoms in this container
	 *@see               #getAtomCount
	 */
	public void setAtomCount(int atomCount)
	{
		this.atomCount = atomCount;
	}


	/**
	 *  Returns the array of atoms of this AtomContainer.
	 *
	 *@return    The array of atoms of this AtomContainer
	 *@see       #setAtoms
	 */
	public Atom[] getAtoms()
	{
		Atom[] returnAtoms = new Atom[getAtomCount()];
		System.arraycopy(this.atoms, 0, returnAtoms, 0, returnAtoms.length);
		return returnAtoms;
	}


	/**
	 *  Returns an AtomEnumeration for looping over all atoms in this container.
	 *
	 *@return    An AtomEnumeration with the atoms in this container
	 *@see       #getAtoms
	 */
	public Enumeration atoms()
	{
		return new AtomEnumeration(this);
	}


	/**
	 *  Returns the array of electronContainers of this AtomContainer.
	 *
	 *@return    The array of electronContainers of this AtomContainer
	 *@see       #setElectronContainers
	 */
	public ElectronContainer[] getElectronContainers()
	{
		ElectronContainer[] returnElectronContainers = new ElectronContainer[getElectronContainerCount()];
		System.arraycopy(this.electronContainers, 0, returnElectronContainers, 0, returnElectronContainers.length);
		return returnElectronContainers;
	}


	/**
	 *  Returns the array of Bonds of this AtomContainer.
	 *
	 *@return    The array of Bonds of this AtomContainer
	 *@see       #getElectronContainers
	 */
	public Bond[] getBonds()
	{
		int bondCount = getBondCount();
		Bond[] result = new Bond[bondCount];
		int bondCounter = 0;
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			ElectronContainer ec = getElectronContainerAt(i);
			if (ec instanceof Bond)
			{
				result[bondCounter] = (Bond) ec;
				bondCounter++;
			}
		}
		return result;
	}


	/**
	 *  Returns the array of Bonds of this AtomContainer.
	 *
	 *@return    The array of Bonds of this AtomContainer
	 *@see       #getElectronContainers
	 *@see       #getBonds
	 */
	public LonePair[] getLonePairs()
	{
		int count = getLonePairCount();
		LonePair[] result = new LonePair[count];
		int counter = 0;
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			ElectronContainer ec = getElectronContainerAt(i);
			if (ec instanceof LonePair)
			{
				result[counter] = (LonePair) ec;
				counter++;
			}
		}
		return result;
	}


	/**
	 *  Returns the array of Bonds of this AtomContainer.
	 *
	 *@param  atom  Description of the Parameter
	 *@return       The array of Bonds of this AtomContainer
	 *@see          #getElectronContainers
	 *@see          #getBonds
	 */
	public LonePair[] getLonePairs(Atom atom)
	{
		Vector lps = new Vector();
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			ElectronContainer ec = getElectronContainerAt(i);
			if ((ec instanceof LonePair) && (((LonePair) ec).contains(atom)))
			{
				lps.add(ec);
			}
		}
		LonePair[] result = new LonePair[lps.size()];
		lps.copyInto(result);
		return result;
	}


	/**
	 *  Returns the atom at position 0 in the container.
	 *
	 *@return    The atom at position 0 .
	 */
	public Atom getFirstAtom()
	{
		return atoms[0];
	}


	/**
	 *  Returns the atom at the last position in the container.
	 *
	 *@return    The atom at the last position
	 */
	public Atom getLastAtom()
	{
		return atoms[getAtomCount() - 1];
	}


	/**
	 *  Returns the position of a given atom in the atoms array. It returns -1 if
	 *  the atom atom does not exist.
	 *
	 *@param  atom  The atom to be sought
	 *@return       The Position of the atom in the atoms array in [0,..].
	 */
	public int getAtomNumber(Atom atom)
	{
		for (int f = 0; f < getAtomCount(); f++)
		{
			if (getAtomAt(f) == atom)
			{
				return f;
			}
		}
		return -1;
	}


	/**
	 *  Returns the position of the bond between two given atoms in the
	 *  electronContainers array. It returns -1 if the bond does not exist.
	 *
	 *@param  a1  The first atom
	 *@param  a2  The second atom
	 *@return     The Position of the bond between a1 and a2 in the
	 *      electronContainers array.
	 */
	public int getBondNumber(Atom a1, Atom a2)
	{
		return (getBondNumber(getBond(a1, a2)));
	}


	/**
	 *  Returns the position of a given bond in the electronContainers array. It
	 *  returns -1 if the bond does not exist.
	 *
	 *@param  b  The bond to be sought
	 *@return    The Position of the bond in the electronContainers array in [0,..].
	 */
	public int getBondNumber(Bond b)
	{
		for (int f = 0; f < getElectronContainerCount(); f++)
		{
			if (getElectronContainerAt(f) == b)
			{
				return f;
			}
		}
		return -1;
	}


	/**
	 *  Returns the ElectronContainer at position <code>number</code> in the
	 *  container.
	 *
	 *@param  number  The position of the ElectronContainer to be returned.
	 *@return         The ElectronContainer at position <code>number</code>.
	 *@see            #setElectronContainerAt
	 */
	public ElectronContainer getElectronContainerAt(int number)
	{
		return electronContainers[number];
	}


	/**
	 *  Returns the bond that connectes the two given atoms.
	 *
	 *@param  a1  The first atom
	 *@param  a2  The second atom
	 *@return     The bond that connectes the two atoms
	 */
	public Bond getBond(Atom a1, Atom a2)
	{
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainers[i] instanceof Bond &&
					((Bond) electronContainers[i]).contains(a1))
			{
				if (electronContainers[i] instanceof Bond &&
						((Bond) electronContainers[i]).getConnectedAtom(a1) == a2)
				{
					return (Bond) electronContainers[i];
				}
			}
		}
		return null;
	}


	/**
	 *  Returns an array of all atoms connected to the given atom.
	 *
	 *@param  atom  The atom the bond partners are searched of.
	 *@return       The array of <code>Atom</code>s with the size of connected
	 *      atoms
	 */
	public Atom[] getConnectedAtoms(Atom atom)
	{
		Vector atomsVec = getConnectedAtomsVector(atom);
		Atom[] conAtoms = new Atom[atomsVec.size()];
		atomsVec.copyInto(conAtoms);
		return conAtoms;
	}


	/**
	 *  Returns a vector of all atoms connected to the given atom.
	 *
	 *@param  atom  The atom the bond partners are searched of.
	 *@return       The vector with the size of connected atoms
	 */
	public Vector getConnectedAtomsVector(Atom atom)
	{
		Vector atomsVec = new Vector();
		ElectronContainer ec;
		for (int i = 0; i < electronContainerCount; i++)
		{
			ec = electronContainers[i];
			if (ec instanceof Bond && ((Bond) ec).contains(atom))
			{
				atomsVec.addElement(((Bond) ec).getConnectedAtom(atom));
			}
		}
		return atomsVec;
	}


	/**
	 *  Returns an array of all Bonds connected to the given atom.
	 *
	 *@param  atom  The atom the connected bonds are searched of
	 *@return       The array with the size of connected atoms
	 */
	public Bond[] getConnectedBonds(Atom atom)
  {
    Vector bondsVec=getConnectedBondsVector(atom);
		Bond[] conBonds = new Bond[bondsVec.size()];
		bondsVec.copyInto(conBonds);
		return conBonds;
	}
  
	/**
	 *  Returns a Vector of all Bonds connected to the given atom.
	 *
	 *@param  atom  The atom the connected bonds are searched of
	 *@return       The vector with the size of connected atoms
	 */
  public Vector getConnectedBondsVector(Atom atom)
	{
		Vector bondsVec = new Vector();
		for (int i = 0; i < electronContainerCount; i++)
		{
			if (electronContainers[i] instanceof Bond &&
					((Bond) electronContainers[i]).contains(atom))
			{
				bondsVec.addElement(electronContainers[i]);
			}
		}
    return(bondsVec);
  }


	/**
	 *  Returns an array of all electronContainers connected to the given atom.
	 *
	 *@param  atom  The atom the connected electronContainers are searched of
	 *@return       The array with the size of connected atoms
	 */
	public ElectronContainer[] getConnectedElectronContainers(Atom atom)
	{
		Vector bondsVec = new Vector();
		for (int i = 0; i < electronContainerCount; i++)
		{
			if (electronContainers[i] instanceof Bond &&
					((Bond) electronContainers[i]).contains(atom))
			{
				bondsVec.addElement(electronContainers[i]);
			} else if (electronContainers[i] instanceof LonePair &&
					((LonePair) electronContainers[i]).contains(atom))
			{
				bondsVec.addElement(electronContainers[i]);
			}
		}
		ElectronContainer[] cons = new ElectronContainer[bondsVec.size()];
		bondsVec.copyInto(cons);
		return cons;
	}


	/**
	 *  Returns the number of connected atoms (degree) to the given atom.
	 *
	 *@param  atomnumber  The atomnumber the degree is searched for
	 *@return             The number of connected atoms (degree)
	 */
	public int getBondCount(int atomnumber)
	{
		return getBondCount(getAtomAt(atomnumber));
	}


	/**
	 *  Returns the number of Atoms in this Container.
	 *
	 *@return    The number of Atoms in this Container
	 *@see       #setAtomCount
	 */
	public int getAtomCount()
	{
		return this.atomCount;
	}


	/**
	 *  Returns the number of ElectronContainers in this Container.
	 *
	 *@return    The number of ElectronContainers in this Container
	 */
	public int getElectronContainerCount()
	{
		return this.electronContainerCount;
	}


	/**
	 *  Returns the number of LonePairs in this Container.
	 *
	 *@return    The number of LonePairs in this Container
	 */
	public int getLonePairCount()
	{
		int count = 0;
		for (int i = 0; i < electronContainerCount; i++)
		{
			if (electronContainers[i] instanceof LonePair)
			{
				count++;
			}
		}
		return count;
	}


	/**
	 *  Returns the number of Bonds in this Container.
	 *
	 *@return    The number of Bonds in this Container
	 */
	public int getBondCount()
	{
		int bondCount = 0;
		for (int i = 0; i < electronContainerCount; i++)
		{
			if (electronContainers[i] instanceof Bond)
			{
				bondCount++;
			}
		}
		return bondCount;
	}


	/**
	 *  Returns the number of Bonds for a given Atom.
	 *
	 *@param  atom  The atom
	 *@return       The number of Bonds for this atom
	 */
	public int getBondCount(Atom atom)
	{
		int count = 0;
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainers[i] instanceof Bond &&
					((Bond) electronContainers[i]).contains(atom))
			{
				count++;
			}
		}
		return count;
	}


	/**
	 *  Returns the number of LonePairs for a given Atom.
	 *
	 *@param  atom  The atom
	 *@return       The number of LonePairs for this atom
	 */
	public int getLonePairCount(Atom atom)
	{
		int count = 0;
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainers[i] instanceof LonePair &&
					((LonePair) electronContainers[i]).contains(atom))
			{
				count++;
			}
		}
		return count;
	}


	/**
	 * Returns the sum of the bond orders for a given Atom.
	 *
	 * @param  atom  The atom
	 * @return       The number of bondorders for this atom
	 */
	public double getBondOrderSum(Atom atom)
	{
		double count = 0;
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainers[i] instanceof Bond &&
					((Bond) electronContainers[i]).contains(atom))
			{
				count += ((Bond) electronContainers[i]).getOrder();
			}
		}
		return count;
	}

    /**
     * Deprecated wrapper method for getMaximumBondOrder().
     *
     * @see #getMaximumBondOrder
     * @deprecated
     */
    public double getHighestCurrentBondOrder(Atom atom) {
        return getMaximumBondOrder(atom);
    }

    /**
	 * Returns the maximum bond order that this atom currently has in the context
	 * of this AtomContainer.
	 *
	 * @param  atom  The atom
	 * @return       The maximum bond order that this atom currently has
	 */
	public double getMaximumBondOrder(Atom atom) {
		double max = 0.0;
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainers[i] instanceof Bond &&
					((Bond) electronContainers[i]).contains(atom) &&
					((Bond) electronContainers[i]).getOrder() > max)
			{
				max = ((Bond) electronContainers[i]).getOrder();
			}
		}
		return max;
	}


	/**
	 *  Returns the minimum bond order that this atom currently has in the context
	 *  of this AtomContainer.
	 *
	 *@param  atom  The atom
	 *@return       The minimim bond order that this atom currently has
	 */
	public double getMinimumBondOrder(Atom atom)
	{
		double min = 6;
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainers[i] instanceof Bond &&
					((Bond) electronContainers[i]).contains(atom) &&
					((Bond) electronContainers[i]).getOrder() < min)
			{
				min = ((Bond) electronContainers[i]).getOrder();
			}
		}
		return min;
	}



	/**
	 *  Compares this AtomContainer with another given AtomContainer and returns
	 *  the Intersection between them. <p>
	 *
	 *  <b>Important Note</b> : This is not a maximum common substructure.
	 *
	 *@param  ac  an AtomContainer object
	 *@return     An AtomContainer containing the Intersection between this
	 *      AtomContainer and another given one
	 */

	public AtomContainer getIntersection(AtomContainer ac)
	{
		AtomContainer intersection = new AtomContainer();

		for (int i = 0; i < getAtomCount(); i++)
		{
			if (ac.contains(getAtomAt(i)))
			{
				intersection.addAtom(getAtomAt(i));
			}
		}
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (ac.contains(getElectronContainerAt(i)))
			{
				intersection.addElectronContainer(getElectronContainerAt(i));
			}
		}
		return intersection;
	}



	/**
	 *  Returns the geometric center of all the atoms in this atomContainer.
	 *
	 *@return    the geometric center of the atoms in this atomContainer
	 */
	public Point2d get2DCenter()
	{
		double centerX = 0;
		double centerY = 0;
		double counter = 0;
		for (int i = 0; i < getAtomCount(); i++)
		{
			if (atoms[i].getPoint2d() != null)
			{
				centerX += atoms[i].getPoint2d().x;
				centerY += atoms[i].getPoint2d().y;
				counter++;
			}
		}
		Point2d point = new Point2d(centerX / (counter), centerY / (counter));
		return point;
	}


	/**
	 *  Returns the geometric center of all the atoms in this atomContainer.
	 *
	 *@return    the geometric center of the atoms in this atomContainer
	 */
	public Point3d get3DCenter()
	{
		double centerX = 0;
		double centerY = 0;
		double centerZ = 0;
		double counter = 0;
		for (int i = 0; i < getAtomCount(); i++)
		{
			if (atoms[i].getPoint3d() != null)
			{
				centerX += atoms[i].getPoint3d().x;
				centerY += atoms[i].getPoint3d().y;
				centerZ += atoms[i].getPoint3d().z;
				counter++;
			}
		}
		Point3d point = new Point3d(centerX / (counter), centerY / (counter), centerZ / (counter));
		return point;
	}



	/**
	 *  Returns a connection matrix representation of this AtomContainer An
	 *  adjacency matrix is a matrix of quare NxN matrix, where N is the number of
	 *  atoms in the AtomContainer. If the i-th and the j-th atom in the
	 *  atomcontainer share a bond, the element i,j in the matrix is set to the
	 *  bond order value. Otherwise it is zero. References: <a
	 *  href="http://cdk.sf.net/biblio.html#TRI1992">TRI1992</a> ,
	 *
	 *@return    A connection matrix representation of this AtomContainer
	 */

	public double[][] getConnectionMatrix()
	{
		ElectronContainer ec = null;
		int i;
		int j;
		double[][] conMat = new double[getAtomCount()][getAtomCount()];
		for (int f = 0; f < getElectronContainerCount(); f++)
		{
			ec = getElectronContainerAt(f);
			if (ec instanceof Bond)
			{
				Bond bond = (Bond) ec;
				i = getAtomNumber(bond.getAtomAt(0));
				j = getAtomNumber(bond.getAtomAt(1));
				conMat[i][j] = bond.getOrder();
				conMat[j][i] = bond.getOrder();
			}
		}
		return conMat;
	}


	/**
	 *  Returns a adjacency matrix representation of this AtomContainer. An
	 *  adjacency matrix is a matrix of quare NxN matrix, where N is the number of
	 *  atoms in the AtomContainer. The element i,j of the matrix is 1, if the i-th
	 *  and the j-th atom in the atomcontainer share a bond. Otherwise it is zero.
	 *  References: <a href="http://cdk.sf.net/biblio.html#TRI1992">TRI1992</a> ,
	 *
	 *@return     A adjacency matrix representating this AtomContainer
	 *@cdk.keyword    adjacency matrix
	 */
	public int[][] getAdjacencyMatrix()
	{
		ElectronContainer ec = null;
		int i;
		int j;
		int[][] conMat = new int[getAtomCount()][getAtomCount()];
		for (int f = 0; f < getElectronContainerCount(); f++){
      ec = getElectronContainerAt(f);
			if (ec instanceof Bond)
			{
				Bond bond = (Bond) ec;
				i = getAtomNumber(bond.getAtomAt(0));
				j = getAtomNumber(bond.getAtomAt(1));
				conMat[i][j] = 1;
				conMat[j][i] = 1;
			}
		}
		return conMat;
	}


	/**
	 *  Add bonds to a set of atoms (Experimental!).
	 *
	 *@param  maxbondlength  The feature to be added to the electronContainers
	 *      attribute
	 */
	public void addBonds(double maxbondlength)
	{
		for (int i = 0; i < atomCount; i++)
		{
			for (int j = i + 1; j < atomCount; j++)
			{
				if (atoms[i].getPoint3d().distance(atoms[j].getPoint3d()) <= maxbondlength)
				{
					addBond(new Bond(atoms[i], atoms[j], CDKConstants.BONDORDER_SINGLE));
				}
			}
		}
	}


	/**
	 *  Adds the <code>ElectronContainer</code>s found in atomContainer to this
	 *  container.
	 *
	 *@param  atomContainer  AtomContainer with the new ElectronContainers
	 */
	public void addElectronContainers(AtomContainer atomContainer)
	{
		for (int f = 0; f < atomContainer.getElectronContainerCount(); f++)
		{
			if (!contains(atomContainer.getElectronContainerAt(f)))
			{
				addElectronContainer(atomContainer.getElectronContainerAt(f));
			}
		}

	}


	/**
	 *  Adds all atoms and electronContainers of a given atomcontainer to this
	 *  container.
	 *
	 *@param  atomContainer  The atomcontainer to be added
	 */
	public void add(AtomContainer atomContainer)
	{
		for (int f = 0; f < atomContainer.getAtomCount(); f++)
		{
			if (!contains(atomContainer.getAtomAt(f)))
			{
				addAtom(atomContainer.getAtomAt(f));
			}
		}
		for (int f = 0; f < atomContainer.getElectronContainerCount(); f++)
		{
			if (!contains(atomContainer.getElectronContainerAt(f)))
			{
				addElectronContainer(atomContainer.getElectronContainerAt(f));
			}
		}
	}


	/**
	 *  Adds an atom to this container.
	 *
	 *@param  atom  The atom to be added to this container
	 */
	public void addAtom(Atom atom)
	{
		if (contains(atom))
		{
			return;
		}

		if (atomCount + 1 >= atoms.length)
		{
			growAtomArray();
		}
		atoms[atomCount] = atom;
		atomCount++;
	}


	/**
	 *  Wrapper method for adding Bonds to this AtomContainer.
	 *
	 *@param  bond  The bond to added to this container
	 */
	public void addBond(Bond bond)
	{
		addElectronContainer(bond);
	}


	/**
	 *  Adds a ElectronContainer to this AtomContainer.
	 *
	 *@param  ec  The ElectronContainer to added to this container
	 */
	public void addElectronContainer(ElectronContainer ec)
	{
		if (electronContainerCount + 1 >= electronContainers.length)
		{
			growElectronContainerArray();
		}
		// are we supposed to check if the atoms forming this bond are
		// already in here and add them if neccessary? No, core classes
		// must not check parameter input.
		electronContainers[electronContainerCount] = ec;
		electronContainerCount++;
	}


	/**
	 *  Removes all atoms and electronContainers of a given atomcontainer from this
	 *  container.
	 *
	 *@param  atomContainer  The atomcontainer to be removed
	 */
	public void remove(AtomContainer atomContainer)
	{
		for (int f = 0; f < atomContainer.getAtomCount(); f++)
		{
			removeAtom(atomContainer.getAtomAt(f));
		}
		for (int f = 0; f < atomContainer.getElectronContainerCount(); f++)
		{
			removeElectronContainer(atomContainer.getElectronContainerAt(f));
		}
	}


	/**
	 *  Removes the bond at the given position from this container.
	 *
	 *@param  position  The position of the bond in the electronContainers array
	 *@return           Bond that was removed
	 */
	public ElectronContainer removeElectronContainer(int position)
	{
		ElectronContainer ec = getElectronContainerAt(position);
		for (int i = position; i < electronContainerCount - 1; i++)
		{
			electronContainers[i] = electronContainers[i + 1];
		}
		electronContainers[electronContainerCount - 1] = null;
		electronContainerCount--;
		return ec;
	}


	/**
	 *  Removes this ElectronContainer from this container.
	 *
	 *@param  ec    Description of the Parameter
	 *@return       Bond that was removed
	 */
	public ElectronContainer removeElectronContainer(ElectronContainer ec)
	{
		for (int i = getElectronContainerCount() - 1; i >= 0; i--)
		{
			if (electronContainers[i].equals(ec))
			{
				return removeElectronContainer(i);
			}
		}
		return null;
	}


	/**
	 *  Removes the bond that connects the two given atoms.
	 *
	 *@param  a1  The first atom
	 *@param  a2  The second atom
	 *@return     The bond that connectes the two atoms
	 */
	public Bond removeBond(Atom a1, Atom a2)
	{
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainers[i] instanceof Bond &&
					((Bond) electronContainers[i]).contains(a1))
			{
				if (((Bond) electronContainers[i]).getConnectedAtom(a1) == a2)
				{
					return (Bond) removeElectronContainer(electronContainers[i]);
				}
			}
		}
		return null;
	}



	/**
	 *  Removes the atom at the given position from the AtomContainer. Note that
	 *  the electronContainers are unaffected: you also have to take care of
	 *  removing all electronContainers to this atom from the container manually.
	 *
	 *@param  position  The position of the atom to be removed.
	 */
	public void removeAtom(int position)
	{
		for (int i = position; i < atomCount - 1; i++)
		{
			atoms[i] = atoms[i + 1];
		}
		atoms[atomCount - 1] = null;
		atomCount--;
	}


	/**
	 *  Removes the given atom and all connected electronContainers from the
	 *  AtomContainer.
	 *
	 *@param  atom  The atom to be removed
	 */
	public void removeAtomAndConnectedElectronContainers(Atom atom)
	{
		int position = getAtomNumber(atom);
		if (position != -1)
		{
			ElectronContainer[] electronContainers = getConnectedElectronContainers(atom);
			for (int f = 0; f < electronContainers.length; f++)
			{
				removeElectronContainer(electronContainers[f]);
			}
			removeAtom(position);
		}
	}


	/**
	 *  Removes the given atom from the AtomContainer. Note that the
	 *  electronContainers are unaffected: you also have to take care of removeing
	 *  all electronContainers to this atom from the container.
	 *
	 *@param  atom  The atom to be removed
	 */
	public void removeAtom(Atom atom)
	{
		int position = getAtomNumber(atom);
		if (position != -1)
		{
			removeAtom(position);
		}
	}


	/**
	 *  Removes all atoms and bond from this container.
	 */
	public void removeAllElements()
	{
		atoms = new Atom[growArraySize];
		electronContainers = new ElectronContainer[growArraySize];
		atomCount = 0;
		electronContainerCount = 0;
	}


	/**
	 *  Removes electronContainers from this container.
	 */
	public void removeAllElectronContainers()
	{
		electronContainers = new ElectronContainer[growArraySize];
		electronContainerCount = 0;
	}

    /**
     *  Removes all Bonds from this container.
     */
    public void removeAllBonds() {
        Bond[] bonds = getBonds();
        for (int i=0; i<bonds.length; i++) {
            removeElectronContainer(bonds[i]);
        }
    }

	/**
	 *  Adds a bond to this container.
	 *
	 *@param  atom1   Id of the first atom of the Bond in [0,..]
	 *@param  atom2   Id of the second atom of the Bond in [0,..]
	 *@param  order   Bondorder
	 *@param  stereo  Stereochemical orientation
	 */
	public void addBond(int atom1, int atom2, double order, int stereo)
	{
		Bond bond = new Bond(getAtomAt(atom1), getAtomAt(atom2), order, stereo);

		if (contains(bond))
		{
			return;
		}

		if (electronContainerCount >= electronContainers.length)
		{
			growElectronContainerArray();
		}
		addBond(bond);
	}


	/**
	 *  Adds a bond to this container.
	 *
	 *@param  atom1  Id of the first atom of the Bond in [0,..]
	 *@param  atom2  Id of the second atom of the Bond in [0,..]
	 *@param  order  Bondorder
	 */
	public void addBond(int atom1, int atom2, double order)
	{
		Bond bond = new Bond(getAtomAt(atom1), getAtomAt(atom2), order);

		if (electronContainerCount >= electronContainers.length)
		{
			growElectronContainerArray();
		}
		addBond(bond);
	}


	/**
	 *  Adds a LonePair to this Atom.
	 *
	 *@param  atomID  The atom number to which the LonePair is added in [0,..]
	 */
	public void addLonePair(int atomID)
	{
		addElectronContainer(new LonePair(atoms[atomID]));
	}


	/**
	 *  True, if the AtomContainer contains the given ElectronContainer object.
	 *
	 *@param  ec    Description of the Parameter
	 *@return       True, if the AtomContainer contains the given bond object
	 */
	public boolean contains(ElectronContainer ec)
	{
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (ec == electronContainers[i])
			{
				return true;
			}
		}
		return false;
	}


	/**
	 *  True, if the AtomContainer contains the given atom object.
	 *
	 *@param  atom  the atom this AtomContainer is searched for
	 *@return       True, if the AtomContainer contains the given atom object
	 */
	public boolean contains(Atom atom)
	{
		for (int i = 0; i < getAtomCount(); i++)
		{
			if (atom == atoms[i])
			{
				return true;
			}
		}
		return false;
	}


	/**
	 *  Returns a one line string representation of this Container. This method is
	 *  conform RFC #9.
	 *
	 *@return    The string representation of this Container
	 */
	public String toString()
	{
		ElectronContainer ec;
		StringBuffer s = new StringBuffer();
		s.append("AtomContainer(");
		s.append(this.hashCode() + ", ");
		s.append("#A:" + getAtomCount() + ", ");
		s.append("#EC:" + getElectronContainerCount() + ", ");
		for (int i = 0; i < getAtomCount(); i++)
		{
			s.append(getAtomAt(i).toString() + ", ");
		}
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			ec = getElectronContainerAt(i);
			// this check should be removed!
			if (ec != null)
			{
				s.append(ec.toString() + ", ");
			}
		}
		s.append(")");
		return s.toString();
	}


	/**
	 *  Clones this atomContainer object.
	 *
	 *@return    The cloned object
	 *@see       #shallowCopy
	 */
	public Object clone()
	{
		AtomContainer o = null;
		ElectronContainer ec = null;
		ElectronContainer newEC = null;
		Atom[] natoms;
		Atom[] newAtoms;
		try
		{
			o = (AtomContainer) super.clone();
		} catch (Exception e)
		{
			e.printStackTrace(System.err);
		}
		o.removeAllElements();
		for (int f = 0; f < getAtomCount(); f++)
		{
			o.addAtom((Atom) getAtomAt(f).clone());
		}
		for (int f = 0; f < getElectronContainerCount(); f++)
		{
			ec = this.getElectronContainerAt(f);
			newEC = new ElectronContainer();
			if (ec instanceof Bond)
			{
				Bond bond = (Bond) ec;
				newEC = new Bond();
				natoms = bond.getAtoms();
				newAtoms = new Atom[natoms.length];
				for (int g = 0; g < natoms.length; g++)
				{
					try
					{
						newAtoms[g] = o.getAtomAt(getAtomNumber(natoms[g]));
					} catch (Exception exc)
					{
						System.out.println("natoms[g]: " + natoms[g]);
						exc.printStackTrace();
					}
				}
				((Bond) newEC).setAtoms(newAtoms);
				((Bond) newEC).setOrder(bond.getOrder());
			} else if (ec instanceof LonePair)
			{
				Atom a = ((LonePair) ec).getAtom();
				newEC = new LonePair();
				((LonePair) newEC).setAtom(a);
			} else
			{
				System.out.println("Expecting EC, got: " + ec.getClass().getName());
				newEC = (ElectronContainer) ec.clone();
			}
			o.addElectronContainer(newEC);
		}
		return o;
	}


	/**
	 *  Clones this atomContainer object but leaves references to all atoms and
	 *  electronContainers the same.
	 *
	 *@return    The shallow copied object
	 *@see       #clone
	 */
	public Object shallowCopy()
	{
		Object o = null;
		try
		{
			o = super.clone();
		} catch (Exception e)
		{
			e.printStackTrace(System.err);
		}
		return o;
	}



	/**
	 *  Grows the ElectronContainer array by a given size.
	 *
	 *@see    #growArraySize
	 */
	protected void growElectronContainerArray()
	{
		growArraySize = electronContainers.length;
		ElectronContainer[] newelectronContainers = new ElectronContainer[electronContainers.length + growArraySize];
		System.arraycopy(electronContainers, 0, newelectronContainers, 0, electronContainers.length);
		electronContainers = newelectronContainers;
	}


	/**
	 *  Grows the atom array by a given size.
	 *
	 *@see    #growArraySize
	 */
	protected void growAtomArray()
	{
		growArraySize = atoms.length;
		Atom[] newatoms = new Atom[atoms.length + growArraySize];
		System.arraycopy(atoms, 0, newatoms, 0, atoms.length);
		atoms = newatoms;
	}
}

