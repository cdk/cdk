/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.iupac.generator.sectiona;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.isomorphism.IsomorphismTester;
import org.openscience.cdk.iupac.generator.IUPACNamePart;
import org.openscience.cdk.iupac.generator.NamingRule;
import org.openscience.cdk.iupac.generator.NumberingRule;
import org.openscience.cdk.iupac.generator.tools.CarbonChainNames;
import org.openscience.cdk.layout.AtomPlacer;
import org.openscience.cdk.templates.saturatedhydrocarbons.IsoAlkanes;

/**
 *  This class implements IUPAC rule 2.1 in Section A.
 *
 * @cdk.module experimental
 *
 * @author Egon Willighagen
 */
public class Rule2dot1 extends NamingRule {

    NumberingRule numberingRule = null;

    public String getName() {
        return "A-2.1";
    }

    public NumberingRule getNumberingRule() {
        return numberingRule;
    }

    public IUPACNamePart apply(IAtomContainer m) {
        IUPACNamePart inp = null;
        /* structure may not have valencies */
        if (m instanceof Molecule) {
            Molecule isobutane = IsoAlkanes.getIsobutane();
            Molecule isopentane = IsoAlkanes.getIsopentane();
            Molecule isohexane = IsoAlkanes.getIsohexane();
            // check if molecule is CH only
            if (((((Integer)m.getProperty(ELEMENT_COUNT)).intValue() == 2) &&
                (((Integer)m.getProperty(CARBON_COUNT)).intValue() > 0) &&
                (((Integer)m.getProperty(HYDROGEN_COUNT)).intValue() > 0)) ||
                ((((Integer)m.getProperty(ELEMENT_COUNT)).intValue() == 1) &&
                (((Integer)m.getProperty(CARBON_COUNT)).intValue() > 0))) {
                try {
                    IsomorphismTester it = new IsomorphismTester(new Molecule(m));
                    m.setProperty(COMPLETED_FLAG, "yes");
                    if (it.isIsomorphic(isobutane)) {
                        inp = new IUPACNamePart(localize("isobutane"), this);
                    } else if (it.isIsomorphic(isopentane)) {
                        inp = new IUPACNamePart(localize("isopentane"), this);
                    } else if (it.isIsomorphic(isohexane)) {
                        inp = new IUPACNamePart(localize("isohexane"), this);
                    } else {
                        m.setProperty(COMPLETED_FLAG, "no");
                    };
                } catch (Exception e) {
                    // do nothing special
                }
            }

            /* Is molecule named? If not, it may be a
            * branched alkane compound
            */
            if (m.getProperty(COMPLETED_FLAG).equals("yes")) {
                return inp;
            } else {
                // then, the main chain needs numbering
                numberingRule = new NumberingRule();
            }

            AtomPlacer ap = new AtomPlacer();
            // determine longest C-C chain
            try {
                IAtomContainer copy = new org.openscience.cdk.AtomContainer();
//                System.err.println(" m: " + m);
                copy.add(m);
//                System.err.println("cp: " + copy);
                IAtomContainer longestChain = ap.getInitialLongestChain(
                    new Molecule(deleteNonCarbonAtoms(copy))
                );
//                System.err.println("AC: " + longestChain);

                int length = longestChain.getAtomCount();
                m.setProperty(COMPLETED_FLAG, "no");
                String name = CarbonChainNames.getName(length);
                if (name != null) {
                    inp = new IUPACNamePart(name + localize("ane"), this);
                    // mark named atoms
                    for (int i = 0; i < length; i++) {
                        longestChain.getAtom(i).setProperty(ATOM_NAMED_FLAG, "yes");
                        longestChain.getAtom(i).setProperty(ATOM_MUST_BE_NUMBERED_FLAG, "yes");
                    }
                }
            } catch (Exception e) {
                System.err.println(e.toString());
                e.printStackTrace(System.err);
            }
        }
        return inp;
    };

    private IAtomContainer deleteNonCarbonAtoms(IAtomContainer ac) throws Exception {
        IAtomContainer result = (IAtomContainer)ac.clone();
//        System.out.println("Deleting non carbon atoms...");
        java.util.Iterator atoms = ac.atoms();
        while (atoms.hasNext()) {
        	org.openscience.cdk.interfaces.IAtom atom = (org.openscience.cdk.interfaces.IAtom)atoms.next();
            if (!"C".equals(atom.getSymbol())) {
//                System.out.println("  deleting: " + atom.getSymbol());
                ac.removeAtomAndConnectedElectronContainers(atom);
            } else {
//                System.out.println("   keeping: " + atom.getSymbol());
            }
        }
        return result;
    }
}
