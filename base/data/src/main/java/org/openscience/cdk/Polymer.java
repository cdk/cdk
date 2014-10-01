/* Copyright (C) 2001-2007  Edgar Luttmann <edgar@uni-paderborn.de>
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
 *  */
package org.openscience.cdk;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IPolymer;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IStereoElement;

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Subclass of Molecule to store Polymer specific attributes that a Polymer has.
 *
 * @cdk.module data
 * @cdk.githash
 *
 * @author      Edgar Luttmann <edgar@uni-paderborn.de>
 * @author      Martin Eklund <martin.eklund@farmbio.uu.se>
 * @cdk.created 2001-08-06
 * @cdk.keyword polymer
 */
public class Polymer extends AtomContainer implements java.io.Serializable, IPolymer {

    /**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide/serialization/spec/version.doc.html>details</a>.
     */
    private static final long     serialVersionUID = -2596790658835319339L;

    private Map<String, IMonomer> monomers;                                // the list of all the contained Monomers.

    /**
     * Constructs a new Polymer to store the Monomers.
     */
    public Polymer() {
        super();
        monomers = new Hashtable<String, IMonomer>();
    }

    /**
     * Adds the atom oAtom to a specified Monomer.
     *
     * @param oAtom  The atom to add
     * @param oMonomer  The monomer the atom belongs to
     */
    @Override
    public void addAtom(IAtom oAtom, IMonomer oMonomer) {

        if (!contains(oAtom)) {
            super.addAtom(oAtom);

            if (oMonomer != null) { // Not sure what's better here...throw nullpointer exception?
                oMonomer.addAtom(oAtom);
                if (!monomers.containsKey(oMonomer.getMonomerName())) {
                    monomers.put(oMonomer.getMonomerName(), oMonomer);
                }
            }
        }
        /*
         * notifyChanged() is called by addAtom in AtomContainer
         */
    }

    /**
     * Returns the number of monomers present in the Polymer.
     *
     * @return number of monomers
     */
    @Override
    public int getMonomerCount() {
        return monomers.size();
    }

    /**
     * Retrieves a Monomer object by specifying its name.
     *
     * @param cName  The name of the monomer to look for
     * @return The Monomer object which was asked for
     */
    @Override
    public IMonomer getMonomer(String cName) {
        return monomers.get(cName);
    }

    /**
     * Returns a collection of the names of all <code>Monomer</code>s in this
     * polymer.
     *
     * @return a <code>Collection</code> of all the monomer names.
     */
    @Override
    public Collection<String> getMonomerNames() {
        return monomers.keySet();
    }

    /**
     * Removes a particular monomer, specified by its name.
     *
     * @param name The name of the monomer to remove
     */
    @Override
    public void removeMonomer(String name) {
        if (monomers.containsKey(name)) {
            Monomer monomer = (Monomer) monomers.get(name);
            this.remove(monomer);
            monomers.remove(name);
        }
    }

    @Override
    public String toString() {
        StringBuffer stringContent = new StringBuffer();
        stringContent.append("Polymer(");
        stringContent.append(this.hashCode()).append(", ");
        stringContent.append(super.toString());
        stringContent.append(')');
        return stringContent.toString();
    }

    /*
     * TODO it's not clear why we need to remove all elements after the clone
     * Looks like we should only clone the monomer related stuff
     */
    @Override
    public IPolymer clone() throws CloneNotSupportedException {
        Polymer clone = (Polymer) super.clone();
        clone.removeAllElements();
        clone.monomers = new Hashtable<String, IMonomer>();
        for (String monomerName : getMonomerNames()) {
            Monomer monomerClone = (Monomer) getMonomer(monomerName).clone();
            for (IAtom atomInMonomer : monomerClone.atoms()) {
                clone.addAtom(atomInMonomer, monomerClone);
            }
        }

        // create a mapping of the original atoms/bonds to the cloned atoms/bonds
        // we need this mapping to correctly clone bonds, single/paired electrons
        // and stereo elements
        // - the expected size stop the map be resized - method from Google Guava
        Map<IAtom, IAtom> atomMap = new HashMap<IAtom, IAtom>(atomCount >= 3 ? atomCount + atomCount / 3
                : atomCount + 1);
        Map<IBond, IBond> bondMap = new HashMap<IBond, IBond>(bondCount >= 3 ? bondCount + bondCount / 3
                : bondCount + 1);

        // now consider atoms that are not associated with any monomer
        for (IAtom atom : atoms()) {
            if (!atomIsInMonomer(atom)) {
                IAtom cloned = (IAtom) atom.clone();
                clone.addAtom(cloned);
                atomMap.put(atom, cloned);
            }
        }

        // since we already removed bonds we'll have to add them back
        IBond newBond;
        for (IBond bond : bonds()) {
            newBond = (IBond) bond.clone();
            IAtom[] newAtoms = new IAtom[bond.getAtomCount()];
            for (int j = 0; j < bond.getAtomCount(); ++j) {
                newAtoms[j] = atomMap.get(bond.getAtom(j));
            }
            newBond.setAtoms(newAtoms);
            clone.addBond(newBond);
            bondMap.put(bond, newBond);
        }

        // put back lone pairs
        ILonePair lp;
        ILonePair newLp;
        for (int i = 0; i < getLonePairCount(); ++i) {
            lp = getLonePair(i);
            newLp = (ILonePair) lp.clone();
            if (lp.getAtom() != null) {
                newLp.setAtom(atomMap.get(lp.getAtom()));
            }
            clone.addLonePair(newLp);
        }

        // put back single electrons
        ISingleElectron singleElectron;
        ISingleElectron newSingleElectron;
        for (int i = 0; i < getSingleElectronCount(); ++i) {
            singleElectron = getSingleElectron(i);
            newSingleElectron = (ISingleElectron) singleElectron.clone();
            if (singleElectron.getAtom() != null) {
                newSingleElectron.setAtom(atomMap.get(singleElectron.getAtom()));
            }
            clone.addSingleElectron(newSingleElectron);
        }

        // map each stereo element to a new instance in the clone
        for (IStereoElement element : stereoElements) {
            clone.addStereoElement(element.map(atomMap, bondMap));
        }

        return clone;
    }

    private boolean atomIsInMonomer(IAtom atom) {
        for (String monomerName : getMonomerNames()) {
            IMonomer monomer = getMonomer(monomerName);
            if (monomer.contains(atom)) return true;
        }
        return false;
    }
}
