/* Copyright (C) 2004-2008  Rajarshi Guha <rajarshi.guha@gmail.com>
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
package org.openscience.cdk.pharmacophore;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.smiles.smarts.parser.SMARTSParser;

/**
 * Represents a query pharmacophore group.
 * 
 * This class is meant to be used to construct pharmacophore queries in conjunction
 * with {@link org.openscience.cdk.pharmacophore.PharmacophoreQueryBond} and an
 * {@link org.openscience.cdk.isomorphism.matchers.QueryAtomContainer}.
 *
 * @author Rajarshi Guha
 * @cdk.module pcore
 * @cdk.githash
 * @cdk.keyword pharmacophore
 * @cdk.keyword 3D isomorphism
 * @see org.openscience.cdk.pharmacophore.PharmacophoreQueryBond
 * @see org.openscience.cdk.isomorphism.matchers.QueryAtomContainer
 * @see org.openscience.cdk.pharmacophore.PharmacophoreMatcher
 */
public class PharmacophoreQueryAtom extends Atom implements IQueryAtom {

    private String smarts;
    private IQueryAtomContainer[] compiledSmarts;

    /**
     * Creat a new query pharmacophore group
     *
     * @param symbol The symbol for the group
     * @param smarts The SMARTS pattern to be used for matching
     */
    public PharmacophoreQueryAtom(String symbol, String smarts) {
        setSymbol(symbol);
        this.smarts = smarts;
        // Note that we allow a special form of SMARTS where the | operator
        // represents logical or of multi-atom groups (as opposed to ','
        // which is for single atom matches)
        String[] subSmarts = smarts.split("\\|");
        this.compiledSmarts = new IQueryAtomContainer[subSmarts.length];
        for (int i = 0; i < compiledSmarts.length; i++)
            compiledSmarts[i] = SMARTSParser.parse(subSmarts[i], null);
    }

    /**
     * Get the SMARTS pattern for this pharmacophore group.
     *
     * @return The SMARTS pattern
     */
    public String getSmarts() {
        return smarts;
    }

    /**
     * Accessed the compiled SMARTS for this pcore query atom.  
     * @return compiled SMARTS
     */
    IQueryAtomContainer[] getCompiledSmarts() {
        return compiledSmarts;    
    }

    /**
     * Checks whether this query atom matches a target atom.
     * 
     * Currently a query pharmacophore atom will match a target pharmacophore group if the
     * symbols of the two groups match. This is based on the assumption that
     * pharmacophore groups with the same symbol will have the same SMARTS
     * pattern.
     *
     * @param atom A target pharmacophore group
     * @return true if the current query group has the same symbol as the target group
     */
    @Override
    public boolean matches(IAtom atom) {
        PharmacophoreAtom patom = (PharmacophoreAtom) atom;
        return patom.getSymbol().equals(getSymbol());
    }

    /**
     * String representation of this pharmacophore group.
     *
     * @return String representation of this pharmacophore group
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append(getSymbol()).append(" [").append(getSmarts()).append(']');
        return s.toString();
    }
}
