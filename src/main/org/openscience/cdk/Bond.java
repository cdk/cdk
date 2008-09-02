/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2007  Christoph Steinbeck <steinbeck@users.sf.net>
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Implements the concept of a covalent bond between two or more atoms. A bond is
 * considered to be a number of electrons connecting two or more  of atoms.
 * <p/>
 * It should be noted that the majority of applications will consider 2-center bonds,
 * especially since the bond orders currently supported are really only valid for
 * 2-center bonds. However the code does support multi-center bonds, though the
 * orders may not make sense at this point.
 * <p/>
 * In general code that assumes bonds are 2-centered can use this class seamlessly, as
 * the semantics are identical to the older versions. Care shoud be exercised when
 * using multi-center bonds using this class as the orders may not make sense.
 *
 * @author steinbeck
 * @cdk.module data
 * @cdk.svnrev $Revision$
 * @cdk.created 2003-10-02
 * @cdk.keyword bond
 * @cdk.keyword atom
 * @cdk.keyword electron
 */
public class Bond extends ElectronContainer implements IBond, Serializable, Cloneable {
    /**
     * Determines if a de-serialized object is compatible with this class.
     * <p/>
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
     */
    private static final long serialVersionUID = 7057060562283387384L;

    /**
     * The bond order of this bond.
     */
    protected IBond.Order order = (Order) CDKConstants.UNSET;

    /**
     * Number of atoms contained by this object.
     */
    protected int atomCount = 0;

    /**
     * A list of atoms participating in this bond.
     */
    protected IAtom[] atoms = null;

    /**
     * A descriptor the stereochemical orientation of this bond.
     *
     * @see org.openscience.cdk.CDKConstants for predefined values to be used
     *      here.
     */
    protected int stereo;

    /**
     * Constructs an empty bond.
     */
    public Bond() {
        this(null, null, null, CDKConstants.STEREO_BOND_NONE);
    }


    /**
     * Constructs a bond with a single bond order..
     *
     * @param atom1 the first Atom in the bond
     * @param atom2 the second Atom in the bond
     */
    public Bond(IAtom atom1, IAtom atom2) {
        this(atom1, atom2, IBond.Order.SINGLE, CDKConstants.STEREO_BOND_NONE);
    }


    /**
     * Constructs a bond with a given order.
     *
     * @param atom1 the first Atom in the bond
     * @param atom2 the second Atom in the bond
     * @param order the bond order
     */
    public Bond(IAtom atom1, IAtom atom2, Order order) {
        this(atom1, atom2, order, CDKConstants.STEREO_BOND_NONE);
    }

    /**
     * Constructs a multi-center bond, with undefined order and no stereo information.
     *
     * @param atoms An array of IAtom containing the atoms constituting the bond
     */
    public Bond(IAtom[] atoms) {
        this.atoms = new IAtom[atoms.length];
        System.arraycopy(atoms, 0, this.atoms, 0, atoms.length);
        atomCount = this.atoms.length;
    }

    /**
     * Constructs a multi-center bond, with a specified order and no stereo information.
     *
     * @param atoms An array of IAtom containing the atoms constituting the bond
     * @param order The order of the bond
     */
    public Bond(IAtom[] atoms, Order order) {
        this.atoms = new IAtom[atoms.length];
        System.arraycopy(atoms, 0, this.atoms, 0, atoms.length);
        atomCount = this.atoms.length;
        this.order = order;
    }


    /**
     * Constructs a bond with a given order and stereo orientation from an array
     * of atoms.
     *
     * @param atom1  the first Atom in the bond
     * @param atom2  the second Atom in the bond
     * @param order  the bond order
     * @param stereo a descriptor the stereochemical orientation of this bond
     */
    public Bond(IAtom atom1, IAtom atom2, Order order, int stereo) {
        atoms = new Atom[2];
        atoms[0] = atom1;
        atoms[1] = atom2;
        this.order = order;
        this.stereo = stereo;
        this.atomCount = 2;
    }


    /**
     * Returns the Iterator to atoms making up this bond.
     * Iterator.remove() is not implemented.
     *
     * @return An Iterator to atoms participating in this bond
     * @see #setAtoms
     */
    public Iterable<IAtom> atoms() {
        return new Iterable<IAtom>() {
        	public Iterator<IAtom> iterator() {
        		return new AtomsIterator();
        	}
        };
    }

    /**
     * The inner Iterator class.
     */
    private class AtomsIterator implements Iterator<IAtom> {

        private int pointer = 0;

        public boolean hasNext() {
            return pointer < atomCount;
        }

        public IAtom next() {
            ++pointer;
            return atoms[pointer - 1];
        }

        public void remove() {
        }

    }

    /**
     * Sets the array of atoms making up this bond.
     *
     * @param atoms An array of atoms that forms this bond
     * @see #atoms
     */
    public void setAtoms(IAtom[] atoms) {
        this.atoms = atoms;
        atomCount = atoms.length;
        notifyChanged();
    }


    /**
     * Returns the number of Atoms in this Bond.
     *
     * @return The number of Atoms in this Bond
     */
    public int getAtomCount() {
        return atomCount;
    }


    /**
     * Returns an Atom from this bond.
     *
     * @param position The position in this bond where the atom is
     * @return The atom at the specified position, null if there are no atoms in the bond
     * @see #setAtom
     */
    public IAtom getAtom(int position) {
        if (atoms == null) return null;
        else return atoms[position];
    }


    /**
     * Returns the atom connected to the given atom.
     * <p/>
     * This method is only strictly relevant for 2-center bonds
     * since in multi-center bonds, a given atom will be connected
     * to multiple atoms.
     * <p/>
     * If called for a multi-center bond, then the next atom in the
     * atom list is returned. This is probably not what is expected and
     * hence the user should instead call
     * {@link #getConnectedAtoms(org.openscience.cdk.interfaces.IAtom)}
     *
     * @param atom The atom the bond partner is searched of
     * @return the connected atom or null  if the atom is not part of the bond
     * @see #getConnectedAtoms(org.openscience.cdk.interfaces.IAtom)
     */
    public IAtom getConnectedAtom(IAtom atom) {
        if (atoms[0] == atom) {
            return atoms[1];
        } else if (atoms[1] == atom) {
            return atoms[0];
        }
        return null;
    }

    /**
     * Returns all the atoms in the bond connected to the specified atom.
     * <p/>
     * Though this can be used for traditional 2-center bonds, it is oriented
     * towards multi-center bonds, where a single atom is connected to multiple
     * atoms.
     *
     * @param atom The atom whose partners are to be searched for
     * @return An array of the connected atoms, null if the atom is not part of the bond
     * @see #getConnectedAtom(org.openscience.cdk.interfaces.IAtom)
     */
    public IAtom[] getConnectedAtoms(IAtom atom) {
        boolean atomIsInBond = false;
        for (IAtom localAtom : atoms) {
            if (localAtom == atom) {
                atomIsInBond = true;
                break;
            }
        }
        if (!atomIsInBond) return null;

        List<IAtom> conAtoms = new ArrayList<IAtom>();
        for (IAtom localAtom : atoms) {
            if (localAtom != atom) conAtoms.add(localAtom);
        }
        return conAtoms.toArray(new IAtom[]{});        
    }


    /**
     * Returns true if the given atom participates in this bond.
     *
     * @param atom The atom to be tested if it participates in this bond
     * @return true if the atom participates in this bond
     */
    public boolean contains(IAtom atom) {
        if (atoms == null) return false;
        for (IAtom localAtom : atoms) {
            if (localAtom == atom) return true;
        }
        return false;
    }


    /**
     * Sets an Atom in this bond.
     *
     * @param atom     The atom to be set
     * @param position The position in this bond where the atom is to be inserted
     * @see #getAtom
     */
    public void setAtom(IAtom atom, int position) {
        atoms[position] = atom;
        notifyChanged();
    }


    /**
     * Returns the bond order of this bond.
     *
     * @return The bond order of this bond
     * @see org.openscience.cdk.CDKConstants org.openscience.cdk.CDKConstants
     *      for predefined values.
     * @see #setOrder
     */
    public Order getOrder() {
        return this.order;
    }


    /**
     * Sets the bond order of this bond.
     *
     * @param order The bond order to be assigned to this bond
     * @see org.openscience.cdk.CDKConstants
     *      org.openscience.cdk.CDKConstants for predefined values.
     * @see #getOrder
     */
    public void setOrder(Order order) {
        this.order = order;
        notifyChanged();
    }


    /**
     * Returns the stereo descriptor for this bond.
     *
     * @return The stereo descriptor for this bond
     * @see #setStereo
     * @see org.openscience.cdk.CDKConstants for predefined values.
     */
    public int getStereo() {
        return this.stereo;
    }


    /**
     * Sets the stereo descriptor for this bond.
     *
     * @param stereo The stereo descriptor to be assigned to this bond.
     * @see #getStereo
     * @see org.openscience.cdk.CDKConstants for predefined values.
     */
    public void setStereo(int stereo) {
        this.stereo = stereo;
        notifyChanged();
    }


    /**
     * Returns the geometric 2D center of the bond.
     *
     * @return The geometric 2D center of the bond
     */
    public Point2d get2DCenter() {
        double xOfCenter = 0;
        double yOfCenter = 0;
        for (IAtom atom : atoms) {
            xOfCenter += atom.getPoint2d().x;
            yOfCenter += atom.getPoint2d().y;
        }

        return new Point2d(xOfCenter / ((double) getAtomCount()),
                yOfCenter / ((double) getAtomCount()));
    }


    /**
     * Returns the geometric 3D center of the bond.
     *
     * @return The geometric 3D center of the bond
     */
    public Point3d get3DCenter() {
        double xOfCenter = 0;
        double yOfCenter = 0;
        double zOfCenter = 0;
        for (IAtom atom : atoms) {
            xOfCenter += atom.getPoint3d().x;
            yOfCenter += atom.getPoint3d().y;
            zOfCenter += atom.getPoint3d().z;
        }

        return new Point3d(xOfCenter / getAtomCount(),
                yOfCenter / getAtomCount(),
                zOfCenter / getAtomCount());
    }

    /**
     * Compares a bond with this bond.
     *
     * @param object Object of type Bond
     * @return true if the bond is equal to this bond
     */
    public boolean compare(Object object) {
        if (object instanceof IBond) {
            Bond bond = (Bond) object;
            for (IAtom atom : atoms) {
                if (!bond.contains(atom)) {
                    return false;
                }
            }

            // not important ??!!
            //if (order==bond.order)
            //  return false;

            return true;
        }
        return false;
    }


    /**
     * Checks whether a bond is connected to another one.
     * This can only be true if the bonds have an Atom in common.
     *
     * @param bond The bond which is checked to be connect with this one
     * @return true if the bonds share an atom, otherwise false
     */
    public boolean isConnectedTo(IBond bond) {
        for (IAtom atom : atoms) {
            if (bond.contains(atom)) return true;
        }
        return false;
    }


    /**
     * Clones this bond object, including clones of the atoms between which the
     * bond is defined.
     *
     * @return The cloned object
     */
    public Object clone() throws CloneNotSupportedException {
        Bond clone = (Bond) super.clone();
        // clone all the Atoms
        if (atoms != null) {
            clone.atoms = new IAtom[atoms.length];
            for (int f = 0; f < atoms.length; f++) {
                if (atoms[f] != null) {
                    clone.atoms[f] = (IAtom) ((IAtom) atoms[f]).clone();
                }
            }
        }
        return clone;
    }


    /**
     * Returns a one line string representation of this Container. This method is
     * conform RFC #9.
     *
     * @return The string representation of this Container
     */
    public String toString() {
        StringBuffer resultString = new StringBuffer(32);
        resultString.append("Bond(").append(this.hashCode());
        if (getOrder() != null) {
            resultString.append(", #O:").append(getOrder());
        }
        resultString.append(", #S:").append(getStereo());
        if (getAtomCount() > 0) {
            resultString.append(", #A:").append(getAtomCount());
            for (int i = 0; i < atomCount; i++) {
                resultString.append(", ").append("" + atoms[i]);
            }
        }
        resultString.append(')');
        return resultString.toString();
    }

}

