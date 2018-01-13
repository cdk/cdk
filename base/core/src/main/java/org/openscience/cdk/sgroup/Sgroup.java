/*
 * Copyright (c) 2015 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.sgroup;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

import java.util.*;

/**
 * Generic CTab Sgroup (substructure group) that stores all other types of group. This representation
 * is allows reading from CTfiles (e.g. Molfile, SDfile).
 * 
 * The class uses a key-value store for Sgroup attributes simplifying both input and output.
 */
public class Sgroup {

    private final Set<IAtom>  atoms   = new HashSet<>();
    private final Set<IBond>  bonds   = new HashSet<>();
    private final Set<Sgroup> parents = new HashSet<>();

    private final Map<SgroupKey, Object> attributes = new EnumMap<>(SgroupKey.class);

    /**
     * Create a new generic Sgroup.
     */
    public Sgroup() {
        setType(SgroupType.CtabGeneric);
    }

    /**
     * Copy constructor.
     *
     * @param org original Sgroup instance
     */
    Sgroup(Sgroup org) {
        this.atoms.addAll(org.atoms);
        this.bonds.addAll(org.bonds);
        this.parents.addAll(org.parents);
        this.attributes.putAll(org.attributes);
    }

    /**
     * Access all the attribute keys of this Sgroup.
     *
     * @return attribute keys
     */
    public final Set<SgroupKey> getAttributeKeys() {
        return attributes.keySet();
    }

    /**
     * Set the type of the Sgroup.
     */
    public final void setType(SgroupType type) {
        putValue(SgroupKey.CtabType, type);
    }

    /**
     * Access the type of the Sgroup.
     */
    public final SgroupType getType() {
        return getValue(SgroupKey.CtabType);
    }

    /**
     * Access the atoms of this substructure group.
     *
     * @return unmodifiable atom set
     */
    public final Set<IAtom> getAtoms() {
        return Collections.unmodifiableSet(atoms);
    }

    /**
     * Access the bonds that belong to this substructure group.
     * For data Sgroups, the bonds are the containment bonds,
     * for all other Sgroup types, they are crossing bonds.
     *
     * @return unmodifiable bond set
     */
    public final Set<IBond> getBonds() {
        return Collections.unmodifiableSet(bonds);
    }

    /**
     * Access the parents of this Sgroup.
     *
     * @return parents
     */
    public final Set<Sgroup> getParents() {
        return Collections.unmodifiableSet(parents);
    }

    /**
     * Add an atom to this Sgroup.
     *
     * @param atom the atom
     */
    public final void addAtom(IAtom atom) {
        this.atoms.add(atom);
    }

    /**
     * Remove an atom from this Sgroup.
     * @param atom the atom
     */
    public final void removeAtom(IAtom atom) {
        this.atoms.remove(atom);
    }

    /**
     * Add a bond to this Sgroup. The bond list
     *
     * @param bond bond to add
     */
    public final void addBond(IBond bond) {
        this.bonds.add(bond);
    }

    /**
     * Remove a bond from this Sgroup.
     * @param bond the bond
     */
    public final void removeBond(IBond bond) {
        this.bonds.remove(bond);
    }

    /**
     * Add a parent Sgroup.
     *
     * @param parent parent sgroup
     */
    public final void addParent(Sgroup parent) {
        this.parents.add(parent);
    }

    /**
     * Remove the specified parent associations from this Sgroup.
     *
     * @param parents parent associations
     */
    public final void removeParents(Collection<Sgroup> parents) {
        this.parents.removeAll(parents);
    }

    /**
     * Store an attribute for the Sgroup.
     *
     * @param key attribute key
     * @param val attribute value
     */
    public void putValue(SgroupKey key, Object val) {
        attributes.put(key, val);
    }

    /**
     * Access an attribute for the Sgroup.
     *
     * @param key attribute key
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(SgroupKey key) {
        return (T) attributes.get(key);
    }

    /**
     * Access the subscript value.
     *
     * @return subscript value (or null if not present)
     */
    public final String getSubscript() {
        return getValue(SgroupKey.CtabSubScript);
    }

    /**
     * Set the subscript value.
     */
    public final void setSubscript(String label) {
        putValue(SgroupKey.CtabSubScript, label);
    }

    /**
     * Add a bracket for this Sgroup.
     *
     * @param bracket sgroup bracket
     */
    public final void addBracket(SgroupBracket bracket) {
        List<SgroupBracket> brackets = getValue(SgroupKey.CtabBracket);
        if (brackets == null) {
            putValue(SgroupKey.CtabBracket,
                     brackets = new ArrayList<>(2));
        }
        brackets.add(bracket);
    }

    /**
     * Downcast this, maybe generic, Sgroup to a specific concrete implementation. This
     * method should be called on load by a reader once all data has been added to the sgroup.
     *
     * @param <T> return type
     * @return downcast instance
     */
    @SuppressWarnings("unchecked")
    public <T> T downcast() {
        // ToDo - Implement
        return (T) this;
    }
}
