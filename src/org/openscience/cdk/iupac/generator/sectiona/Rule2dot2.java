/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.iupac.generator.sectiona;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.iupac.generator.IUPACNamePart;
import org.openscience.cdk.iupac.generator.NumberingRule;

/**
 *  This class implements IUPAC rule 2.2 in Section A.
 *
 * This rule assumes (and does <b>not</b> check) that the
 * molecule is a lineair carbon chain.
 *
 * @cdkPackage experimental
 *
 * @author Egon Willighagen
 */
public class Rule2dot2 extends NumberingRule {

    public String getName() {
        return "A-2.2";
    }

    public IUPACNamePart apply(AtomContainer m) {
        IUPACNamePart inp = new IUPACNamePart("", this);
        if (m instanceof Molecule) {
            if (((((Integer)m.getProperty(ELEMENT_COUNT)).intValue() == 2) &&
                (((Integer)m.getProperty(CARBON_COUNT)).intValue() > 0) &&
                (((Integer)m.getProperty(HYDROGEN_COUNT)).intValue() > 0)) ||
                ((((Integer)m.getProperty(ELEMENT_COUNT)).intValue() == 1) &&
                (((Integer)m.getProperty(CARBON_COUNT)).intValue() > 0))) {
                // ok, molecule looks like a carbon chain
                // thus, now find the two chain end atoms
                for (int i=0; i < m.getAtomCount(); i++) {
                    Atom ai = m.getAtomAt(i);
                    // is atom first or last?
                }
            }
        }
        return inp;
    };

    private AtomContainer deleteNonCarbonAtoms(AtomContainer ac) throws NoSuchAtomException {
        AtomContainer result = (AtomContainer)ac.clone();
        for (int i=0; i < ac.getAtomCount(); i++) {
            Atom atom = ac.getAtomAt(i);
            if (!"C".equals(atom.getSymbol())) {
                ac.removeAtomAndConnectedElectronContainers(atom);
            }
        }
        return result;
    }
}
