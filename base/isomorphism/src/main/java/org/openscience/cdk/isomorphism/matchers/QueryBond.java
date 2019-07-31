/* Copyright (C) 2010  M.Rijnbeek <markr@ebi.ac.uk>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.isomorphism.matchers;

import java.util.Iterator;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.BondRef;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * Implements the concept of a "query bond" between two or more atoms.
 * Query bonds can be used to capture types such as "Single or Double" or "Any".
 *
 * @cdk.module isomorphism
 * @cdk.githash
 * @cdk.created 2010-12-16
 */
public class QueryBond extends QueryChemObject implements IQueryBond {

    /**
     * The bond order of this query bond.
     */
    protected IQueryBond.Order  order     = (Order) CDKConstants.UNSET;

    /**
     * Number of atoms contained by this object.
     */
    protected int               atomCount = 0;

    /**
     * A list of atoms participating in this query bond.
     */
    protected IAtom[]           atoms     = null;

    /**
     * A descriptor the stereochemical orientation of this query bond.
     */
    protected IQueryBond.Stereo stereo;

    /**
     * The bond expression.
     */
    private Expr expr = new Expr(Expr.Type.TRUE);

    /**
     * Constructs an query bond from an expression.
     *
     * <pre>{@code
     * // pi-bond in a ring
     * Expr e = new Expr(IS_IN_RING);
     * e.and(new Expr(ALIPHATIC_ORDER, 2));
     * new QueryBond(beg, end, e);
     * }</pre>
     *
     * @param expr the expression
     */
    public QueryBond(IAtom beg, IAtom end, Expr expr) {
        this(beg, end, null, IQueryBond.Stereo.NONE, beg.getBuilder());
        this.expr.set(expr);
        atomCount = 2;
    }

    /**
     * Constructs an query bond from an expression type.
     *
     * <pre>{@code
     * new QueryBond(beg, end, IS_IN_RING);
     * }</pre>
     *
     * @param type the expression type
     */
    public QueryBond(IAtom beg, IAtom end, Expr.Type type) {
        this(beg, end, null, IQueryBond.Stereo.NONE, beg.getBuilder());
        this.expr.setPrimitive(type);
        atomCount = 2;
    }

    /**
     * Constructs an query bond from an expression type and value.
     *
     * <pre>{@code
     * new QueryBond(beg, end, ALIPHATIC_ORDER, 8);
     * }</pre>
     *
     * @param type the expression type
     * @param val the expression value
     */
    public QueryBond(IAtom beg, IAtom end, Expr.Type type, int val) {
        this(beg, end, null, IQueryBond.Stereo.NONE, beg.getBuilder());
        this.expr.setPrimitive(type, val);
        atomCount = 2;
    }

    /**
     * Constructs an empty query bond.
     */
    public QueryBond(IChemObjectBuilder builder) {
        this(null, null, null, IQueryBond.Stereo.NONE, builder);
        atomCount = 0;
    }

    /**
     * Constructs a query bond with a single query bond order..
     *
     * @param atom1 the first Atom in the query bond
     * @param atom2 the second Atom in the query bond
     */
    public QueryBond(IAtom atom1, IAtom atom2, IChemObjectBuilder builder) {
        this(atom1, atom2, IQueryBond.Order.SINGLE, IQueryBond.Stereo.NONE, builder);
    }

    /**
     * Constructs a query bond with a given order.
     *
     * @param atom1 the first Atom in the query bond
     * @param atom2 the second Atom in the query bond
     * @param order the query bond order
     */
    public QueryBond(IAtom atom1, IAtom atom2, Order order, IChemObjectBuilder builder) {
        this(atom1, atom2, order, IQueryBond.Stereo.NONE, builder);
    }

    /**
     * Constructs a multi-center query bond, with undefined order and no stereo information.
     *
     * @param atoms An array of IAtom containing the atoms constituting the query bond
     */
    public QueryBond(IAtom[] atoms, IChemObjectBuilder builder) {
        super(builder);
        this.atoms = new IAtom[atoms.length];
        System.arraycopy(atoms, 0, this.atoms, 0, atoms.length);
        atomCount = this.atoms.length;
    }

    /**
     * Constructs a multi-center query bond, with a specified order and no stereo information.
     *
     * @param atoms An array of IAtom containing the atoms constituting the query bond
     * @param order The order of the query bond
     */
    public QueryBond(IAtom[] atoms, Order order, IChemObjectBuilder builder) {
        super(builder);
        this.atoms = new IAtom[atoms.length];
        System.arraycopy(atoms, 0, this.atoms, 0, atoms.length);
        atomCount = this.atoms.length;
        this.order = order;
    }

    /**
     * Constructs a query bond with a given order and stereo orientation from an array
     * of atoms.
     *
     * @param atom1  the first Atom in the query bond
     * @param atom2  the second Atom in the query bond
     * @param order  the query bond order
     * @param stereo a descriptor the stereochemical orientation of this query bond
     */
    public QueryBond(IAtom atom1, IAtom atom2, Order order, IQueryBond.Stereo stereo, IChemObjectBuilder builder) {
        super(builder);
        atoms = new IAtom[2];
        atoms[0] = atom1;
        atoms[1] = atom2;
        this.order = order;
        this.stereo = stereo;
        this.atomCount = 2;
    }

    /**
     * Returns the Iterator to atoms making up this query bond.
     * Iterator.remove() is not implemented.
     *
     * @return An Iterator to atoms participating in this query bond
     * @see #setAtoms
     */
    @Override
    public Iterable<IAtom> atoms() {
        return new Iterable<IAtom>() {

            @Override
            public Iterator<IAtom> iterator() {
                return new AtomsIterator();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIndex() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtomContainer getContainer() {
        return null;
    }

    /**
     * The inner Iterator class.
     */
    private class AtomsIterator implements Iterator<IAtom> {

        private int pointer = 0;

        @Override
        public boolean hasNext() {
            return pointer < atomCount;
        }

        @Override
        public IAtom next() {
            ++pointer;
            return atoms[pointer - 1];
        }

        @Override
        public void remove() {}

    }

    /**
     * Sets the array of atoms making up this query bond.
     *
     * @param atoms An array of atoms that forms this query bond
     * @see #atoms
     */
    @Override
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
    @Override
    public int getAtomCount() {
        return atomCount;
    }

    /**
     * Returns an Atom from this query bond.
     *
     * @param position The position in this query bond where the atom is
     * @return The atom at the specified position, null if there are no atoms in the query bond
     * @see #setAtom
     */
    @Override
    public IAtom getAtom(int position) {
        if (atoms == null)
            return null;
        else
            return atoms[position];
    }

    /**
     * {@inheritDoc}
     */
    public IAtom getBegin() {
        return atoms[0];
    }

    /**
     * {@inheritDoc}
     */
    public IAtom getEnd() {
        return atoms[1];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtom getOther(IAtom atom) {
        if (atoms[0].equals(atom))
            return atoms[1];
        else if (atoms[1].equals(atom))
            return atoms[0];
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtom getConnectedAtom(IAtom atom) {
        return getOther(atom);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtom[] getConnectedAtoms(IAtom atom) {
        if (atomCount < 1) return null;
        IAtom[] connected = new IAtom[atomCount-1];
        int j = 0;
        for (int i = 0; i < atomCount; i++) {
            if (!this.atoms[i].equals(atom)) {
                if (j >= connected.length)
                    return null;
                connected[j++] = this.atoms[i];
            }
        }
        return connected;
    }

    /**
     * Returns true if the given atom participates in this query bond.
     *
     * @param atom The atom to be tested if it participates in this query bond
     * @return true if the atom participates in this query bond
     */
    @Override
    public boolean contains(IAtom atom) {
        if (atoms == null) return false;
        for (IAtom localAtom : atoms) {
            if (localAtom.equals(atom)) return true;
        }
        return false;
    }

    /**
     * Sets an atom in this query bond.
     *
     * @param atom     The atom to be set
     * @param position The position in this query bond where the atom is to be inserted
     * @see #getAtom
     */
    @Override
    public void setAtom(IAtom atom, int position) {
        if (atoms[position] == null && atom != null) atomCount++;
        if (atoms[position] != null && atom == null) atomCount--;
        atoms[position] = atom;
        notifyChanged();
    }

    /**
     * Returns the query bond order of this query bond.
     *
     * @return The query bond order of this query bond
     * @see org.openscience.cdk.CDKConstants org.openscience.cdk.CDKConstants
     *      for predefined values.
     * @see #setOrder
     */
    @Override
    public Order getOrder() {
        return this.order;
    }

    /**
     * Sets the bond order of this query bond.
     *
     * @param order The query bond order to be assigned to this query bond
     * @see org.openscience.cdk.CDKConstants
     *      org.openscience.cdk.CDKConstants for predefined values.
     * @see #getOrder
     */
    @Override
    public void setOrder(Order order) {
        this.order = order;
        notifyChanged();
    }

    /**
     * Returns the stereo descriptor for this query bond.
     *
     * @return The stereo descriptor for this query bond
     * @see #setStereo
     * @see org.openscience.cdk.CDKConstants for predefined values.
     */
    @Override
    public IQueryBond.Stereo getStereo() {
        return this.stereo;
    }

    /**
     * Sets the stereo descriptor for this query bond.
     *
     * @param stereo The stereo descriptor to be assigned to this query bond.
     * @see #getStereo
     * @see org.openscience.cdk.CDKConstants for predefined values.
     */
    @Override
    public void setStereo(IQueryBond.Stereo stereo) {
        this.stereo = stereo;
        notifyChanged();
    }

    /**
     * Not used for query bonds. {@inheritDoc}
     */
    @Override
    public Display getDisplay() {
        return null;
    }

    /**
     * Not used for query bonds. {@inheritDoc}
     */
    @Override
    public void setDisplay(Display display) {

    }

    /**
     * Returns the geometric 2D center of the query bond.
     *
     * @return The geometric 2D center of the query bond
     */
    @Override
    public Point2d get2DCenter() {
        double xOfCenter = 0;
        double yOfCenter = 0;
        for (IAtom atom : atoms) {
            xOfCenter += atom.getPoint2d().x;
            yOfCenter += atom.getPoint2d().y;
        }

        return new Point2d(xOfCenter / ((double) getAtomCount()), yOfCenter / ((double) getAtomCount()));
    }

    /**
     * Returns the geometric 3D center of the query bond.
     *
     * @return The geometric 3D center of the query bond
     */
    @Override
    public Point3d get3DCenter() {
        double xOfCenter = 0;
        double yOfCenter = 0;
        double zOfCenter = 0;
        for (IAtom atom : atoms) {
            xOfCenter += atom.getPoint3d().x;
            yOfCenter += atom.getPoint3d().y;
            zOfCenter += atom.getPoint3d().z;
        }

        return new Point3d(xOfCenter / getAtomCount(), yOfCenter / getAtomCount(), zOfCenter / getAtomCount());
    }

    /**
     * Compares a query bond with this query bond.
     *
     * @param object Object of type Bond
     * @return true if the query bond is equal to this query bond
     */
    @Override
    public boolean compare(Object object) {
        if (object instanceof IQueryBond) {
            QueryBond queryBond = (QueryBond) object;
            for (IAtom atom : atoms) {
                if (!queryBond.contains(atom)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Checks whether a query bond is connected to another one.
     * This can only be true if the query bonds have an Atom in common.
     *
     * @param bond bond The query bond which is checked to be connect with this one
     * @return true if the query bonds share an atom, otherwise false
     */
    @Override
    public boolean isConnectedTo(IBond bond) {
        for (IAtom atom : atoms) {
            if (bond.contains(atom)) return true;
        }
        return false;
    }

    /**
     * Clones this query bond object, including clones of the atoms between which the
     * query bond is defined.
     *
     * @return The cloned object
     */
    @Override
    public IQueryBond clone() throws CloneNotSupportedException {
        QueryBond clone = (QueryBond) super.clone();
        // clone all the Atoms
        if (atoms != null) {
            clone.atoms = new IAtom[atoms.length];
            for (int f = 0; f < atoms.length; f++) {
                if (atoms[f] != null) {
                    clone.atoms[f] = (IAtom) (atoms[f]).clone();
                }
            }
        }
        return clone;
    }

    /**
     * Returns a one line string representation of this query bond.
     *
     * @return The string representation of this query bond
     */
    @Override
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
                resultString.append(", ").append(atoms[i] == null ? "null" : atoms[i].toString());
            }
        }
        resultString.append(", ").append(super.toString());
        resultString.append(')');
        return resultString.toString();
    }

    //From ElectronContainer
    /** Number of electrons in the ElectronContainer. */
    protected Integer electronCount;

    /**
     * Returns the number of electrons in this bond
     * @return The number of electrons in this electron container.
     * @see     #setElectronCount
     */
    @Override
    public Integer getElectronCount() {
        return this.electronCount;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAromatic() {
        return getFlag(CDKConstants.ISAROMATIC);
    }

    /** {@inheritDoc} */
    @Override
    public void setIsAromatic(boolean arom) {
        setFlag(CDKConstants.ISAROMATIC, arom);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isInRing() {
        return getFlag(CDKConstants.ISINRING);
    }

    /** {@inheritDoc} */
    @Override
    public void setIsInRing(boolean ring) {
        setFlag(CDKConstants.ISINRING, ring);
    }

    /**
     * Sets the number of electrons in this bond
     * @param   electronCount The number of electrons in this electron container.
     * @see     #getElectronCount
     */
    @Override
    public void setElectronCount(Integer electronCount) {
        this.electronCount = electronCount;
        notifyChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BondRef)
            return super.equals(((BondRef) obj).deref());
        return super.equals(obj);
    }

    /**
     * Access the bond expression predicate associated with this query bond.
     * @return the bond expression
     */
    public Expr getExpression() {
        return expr;
    }

    /**
     * Set the bond expression for this query bond.
     * @param expr the new bond expression
     */
    public void setExpression(Expr expr) {
        this.expr = expr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(IBond bond) {
        return expr.matches(bond);
    }
}
