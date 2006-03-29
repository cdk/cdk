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

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.isomorphism.IsomorphismTester;
import org.openscience.cdk.iupac.generator.IUPACNamePart;
import org.openscience.cdk.iupac.generator.NamingRule;
import org.openscience.cdk.iupac.generator.tools.CarbonChainNames;
import org.openscience.cdk.templates.MoleculeFactory;

/**
 *  This class implements IUPAC rule 1.1 in Section A: Alkanes.
 *
 * @cdk.module experimental
 *
 * @author Egon Willighagen
 */
public class Rule1dot1 extends NamingRule {

    public String getName() {
        return "A-1.1";
    }

    public IUPACNamePart apply(AtomContainer m) {
        IUPACNamePart inp = null;
        if (m instanceof Molecule) {
            if (((((Integer)m.getProperty(ELEMENT_COUNT)).intValue() == 2) &&
                (((Integer)m.getProperty(CARBON_COUNT)).intValue() > 0) &&
                (((Integer)m.getProperty(HYDROGEN_COUNT)).intValue() > 0)) ||
                ((((Integer)m.getProperty(ELEMENT_COUNT)).intValue() == 1) &&
                (((Integer)m.getProperty(CARBON_COUNT)).intValue() > 0))) {
                // ok, the first requirement is fullfilled:
                // only carbon and hydrogen in this molecule
                // second requirement is that molecule is an
                // n-alkane with length CARBON_COUNT
                try {
                    IsomorphismTester it = new IsomorphismTester(new Molecule(m));
                    int length = ((Integer)m.getProperty(CARBON_COUNT)).intValue();
                    Molecule nalkane = MoleculeFactory.makeAlkane(length);
                    if (it.isIsomorphic(nalkane)) {
                        // final requirements is that this rule can name
                        // this n-alkane compound
                        String name = CarbonChainNames.getName(length);
                        if (name != null) {
                            inp = new IUPACNamePart(name + localize("ane"), this);
                            m.setProperty(COMPLETED_FLAG, "yes");
                            for (int i = 0; i < m.getAtomCount(); i++) {
                                m.getAtomAt(i).setProperty(ATOM_NAMED_FLAG, "yes");
                            }
                        }
                    };
                } catch (Exception e) {
                    System.out.println(e.toString());
                    e.printStackTrace(System.err);
                }
            } else {
                // APPLIES_FLAG is already "no"
            }
        }
        return inp;
    }
}
