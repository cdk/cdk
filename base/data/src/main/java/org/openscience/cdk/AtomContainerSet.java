/* 
 * Copyright (C) 2003-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *                    2014  Mark B Vine (orcid:0000-0002-7794-0426)
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
 */
package org.openscience.cdk;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

/**
 * A set of AtomContainers.
 *
 * @author        hel
 * @cdk.module    data
 * @cdk.githash
 */
public class AtomContainerSet extends ChemObject implements Serializable, IAtomContainerSet, IChemObjectListener,
        Cloneable {

    /**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide/serialization/spec/version.doc.html>details</a>.
     */
    private static final long  serialVersionUID = -521290255592768395L;

    /**  Array of AtomContainers. */
    protected IAtomContainer[] atomContainers;

    /**  Number of AtomContainers contained by this container. */
    protected int              atomContainerCount;

    /**
     * Defines the number of instances of a certain molecule
     * in the set. It is 1 by default.
     */
    protected Double[]         multipliers;

    /**
     *  Amount by which the AtomContainers array grows when elements are added and
     *  the array is not large enough for that.
     */
    protected int              growArraySize    = 5;

    /**  Constructs an empty AtomContainerSet. */
    public AtomContainerSet() {
        atomContainerCount = 0;
        atomContainers = new IAtomContainer[growArraySize];
        multipliers = new Double[growArraySize];
    }

    /**
     * Adds an atomContainer to this container.
     *
     * @param  atomContainer  The atomContainer to be added to this container
     */
    @Override
    public void addAtomContainer(IAtomContainer atomContainer) {
        atomContainer.addListener(this);
        addAtomContainer(atomContainer, 1.0);
        /*
         * notifyChanged is called below
         */
    }

    /**
     * Removes an AtomContainer from this container.
     *
     * @param  atomContainer  The atomContainer to be removed from this container
     */
    @Override
    public void removeAtomContainer(IAtomContainer atomContainer) {
        for (int i = atomContainerCount - 1; i >= 0; i--) {
            if (atomContainers[i] == atomContainer) removeAtomContainer(i);
        }
    }

    /**
     * Removes all AtomContainer from this container.
     */
    @Override
    public void removeAllAtomContainers() {
        for (int pos = atomContainerCount - 1; pos >= 0; pos--) {
            atomContainers[pos].removeListener(this);
            multipliers[pos] = 0.0;
            atomContainers[pos] = null;
        }
        atomContainerCount = 0;
        notifyChanged();
    }

    /**
     * Removes an AtomContainer from this container.
     *
     * @param  pos  The position of the AtomContainer to be removed from this container
     */
    @Override
    public void removeAtomContainer(int pos) {
        atomContainers[pos].removeListener(this);
        for (int i = pos; i < atomContainerCount - 1; i++) {
            atomContainers[i] = atomContainers[i + 1];
            multipliers[i] = multipliers[i + 1];
        }
        atomContainers[atomContainerCount - 1] = null;
        atomContainerCount--;
        notifyChanged();
    }

    /**
     * Replace the AtomContainer at a specific position (array has to be large enough).
     *
     * @param position   position in array for AtomContainer
     * @param container  the replacement AtomContainer
     */
    @Override
    public void replaceAtomContainer(int position, IAtomContainer container) {
        IAtomContainer old = atomContainers[position];
        old.removeListener(this);
        atomContainers[position] = container;
        container.addListener(this);
        notifyChanged();
    }

    /**
     * Sets the coefficient of a AtomContainer to a given value.
     *
     * @param  container   The AtomContainer for which the multiplier is set
     * @param  multiplier  The new multiplier for the AtomContatiner
     * @return             true if multiplier has been set
     * @see                #getMultiplier(IAtomContainer)
     */
    @Override
    public boolean setMultiplier(IAtomContainer container, Double multiplier) {
        for (int i = 0; i < atomContainers.length; i++) {
            if (atomContainers[i] == container) {
                multipliers[i] = multiplier;
                notifyChanged();
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the coefficient of a AtomContainer to a given value.
     *
     * @param  position    The position of the AtomContainer for which the multiplier is
     *                    set in [0,..]
     * @param  multiplier  The new multiplier for the AtomContatiner at
     *                    <code>position</code>
     * @see                #getMultiplier(int)
     */
    @Override
    public void setMultiplier(int position, Double multiplier) {
        multipliers[position] = multiplier;
        notifyChanged();
    }

    /**
     * Returns an array of double with the stoichiometric coefficients
     * of the products.
     *
     * @return    The multipliers for the AtomContainer's in this set
     * @see       #setMultipliers
     */
    @Override
    public Double[] getMultipliers() {
        Double[] returnArray = new Double[this.atomContainerCount];
        System.arraycopy(this.multipliers, 0, returnArray, 0, this.atomContainerCount);
        return returnArray;
    }

    /**
     * Sets the multipliers of the AtomContainers.
     *
     * @param  newMultipliers  The new multipliers for the AtomContainers in this set
     * @return                 true if multipliers have been set.
     * @see                    #getMultipliers
     */
    @Override
    public boolean setMultipliers(Double[] newMultipliers) {
        if (newMultipliers.length == atomContainerCount) {
            if (multipliers == null) {
                multipliers = new Double[atomContainerCount];
            }
            System.arraycopy(newMultipliers, 0, multipliers, 0, atomContainerCount);
            notifyChanged();
            return true;
        }

        return false;
    }

    /**
     * Adds an atomContainer to this container with the given
     * multiplier.
     *
     * @param  atomContainer  The atomContainer to be added to this container
     * @param  multiplier     The multiplier of this atomContainer
     */
    @Override
    public void addAtomContainer(IAtomContainer atomContainer, double multiplier) {
        if (atomContainerCount + 1 >= atomContainers.length) {
            growAtomContainerArray();
        }
        atomContainer.addListener(this);
        atomContainers[atomContainerCount] = atomContainer;
        multipliers[atomContainerCount] = multiplier;
        atomContainerCount++;
        notifyChanged();
    }

    /**
     *  Adds all atomContainers in the AtomContainerSet to this container.
     *
     * @param  atomContainerSet  The AtomContainerSet
     */
    @Override
    public void add(IAtomContainerSet atomContainerSet) {
        for (IAtomContainer iter : atomContainerSet.atomContainers()) {
            addAtomContainer(iter);
        }
        /*
         * notifyChanged() is called by addAtomContainer()
         */
    }

    /**
     *  Get an iterator for this AtomContainerSet.
     *
     * @return A new Iterator for this AtomContainerSet.
     */
    @Override
    public Iterable<IAtomContainer> atomContainers() {
        return new Iterable<IAtomContainer>() {

            @Override
            public Iterator<IAtomContainer> iterator() {
                return new AtomContainerIterator();
            }
        };
    }

    /**
     * The inner Iterator class.
     *
     */
    private class AtomContainerIterator implements Iterator<IAtomContainer> {

        private int pointer = 0;

        @Override
        public boolean hasNext() {
            return pointer < atomContainerCount;
        }

        @Override
        public IAtomContainer next() {
            return atomContainers[pointer++];
        }

        @Override
        public void remove() {
            removeAtomContainer(--pointer);
        }
    }

    /**
     * Returns the AtomContainer at position <code>number</code> in the
     * container.
     *
     * @param  number  The position of the AtomContainer to be returned.
     * @return         The AtomContainer at position <code>number</code> .
     */
    @Override
    public IAtomContainer getAtomContainer(int number) {
        return atomContainers[number];
    }

    /**
     * Returns the multiplier for the AtomContainer at position <code>number</code> in the
     * container.
     *
     * @param  number  The position of the multiplier of the AtomContainer to be returned.
     * @return         The multiplier for the AtomContainer at position <code>number</code> .
     * @see            #setMultiplier(int, Double)
     */
    @Override
    public Double getMultiplier(int number) {
        return multipliers[number];
    }

    /**
     * Returns the multiplier of the given AtomContainer.
     *
     * @param  container  The AtomContainer for which the multiplier is given
     * @return            -1, if the given molecule is not a container in this set
     * @see               #setMultiplier(IAtomContainer, Double)
     */
    @Override
    public Double getMultiplier(IAtomContainer container) {
        for (int i = 0; i < atomContainerCount; i++) {
            if (atomContainers[i].equals(container)) {
                return multipliers[i];
            }
        }
        return -1.0;
    }

    /**
     *  Grows the atomContainer array by a given size.
     *
     * @see    growArraySize
     */
    protected void growAtomContainerArray() {
        growArraySize = atomContainers.length;
        IAtomContainer[] newatomContainers = new IAtomContainer[atomContainers.length + growArraySize];
        System.arraycopy(atomContainers, 0, newatomContainers, 0, atomContainers.length);
        atomContainers = newatomContainers;
        Double[] newMultipliers = new Double[multipliers.length + growArraySize];
        System.arraycopy(multipliers, 0, newMultipliers, 0, multipliers.length);
        multipliers = newMultipliers;
    }

    /**
     * Returns the number of AtomContainers in this Container.
     *
     * @return    The number of AtomContainers in this Container
     */
    @Override
    public int getAtomContainerCount() {
        return this.atomContainerCount;
    }

    /**
     * Returns the String representation of this AtomContainerSet.
     *
     * @return    The String representation of this AtomContainerSet
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(32);
        buffer.append("AtomContainerSet(");
        buffer.append(this.hashCode());
        if (getAtomContainerCount() > 0) {
            buffer.append(", M=").append(getAtomContainerCount());
            for (int i = 0; i < atomContainerCount; i++) {
                buffer.append(", ").append(atomContainers[i].toString());
            }
        }
        buffer.append(')');
        return buffer.toString();
    }

    /**
     *  Clones this AtomContainerSet and its content.
     *
     * @return    the cloned Object
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        AtomContainerSet clone = (AtomContainerSet) super.clone();
        clone.atomContainers = new IAtomContainer[atomContainerCount];
        clone.atomContainerCount = 0;
        for (int i = 0; i < atomContainerCount; i++) {
            clone.addAtomContainer((IAtomContainer) atomContainers[i].clone());
            clone.setMultiplier(i, getMultiplier(i));
        }
        return clone;
    }

    /**
     *  Called by objects to which this object has
     *  registered as a listener.
     *
     * @param  event  A change event pointing to the source of the change
     */
    @Override
    public void stateChanged(IChemObjectChangeEvent event) {
        notifyChanged(event);
    }

    /**
     * Sort the AtomContainers and multipliers using a provided Comparator.
     * @param comparator defines the sorting method
     */
    @Override
    public void sortAtomContainers(final Comparator<IAtomContainer> comparator) {

        // need to use boxed primitives as we can't customise sorting of int primitives
        Integer[] indexes = new Integer[atomContainerCount];
        for (int i = 0; i < indexes.length; i++)
            indexes[i] = i;

        // proxy the index comparison to the atom container comparator
        Arrays.sort(indexes, new Comparator<Integer>() {

            @Override
            public int compare(Integer o1, Integer o2) {
                return comparator.compare(atomContainers[o1], atomContainers[o2]);
            }
        });

        // copy the original arrays (we could modify in place with swaps but this is cleaner)
        IAtomContainer[] containersTmp = Arrays.copyOf(atomContainers, indexes.length);
        Double[] multipliersTmp = Arrays.copyOf(multipliers, indexes.length);

        // order the arrays based on the order of the indices
        for (int i = 0; i < indexes.length; i++) {
            atomContainers[i] = containersTmp[indexes[i]];
            multipliers[i] = multipliersTmp[indexes[i]];
        }

    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return atomContainerCount == 0;
    }
}
