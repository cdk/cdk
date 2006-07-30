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
package org.openscience.cdk.iupac.generator.sectionc;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Fragment;
import org.openscience.cdk.iupac.generator.IUPACNamePart;
import org.openscience.cdk.iupac.generator.NamingRule;

/**
 *  This class implements IUPAC rule 102.1 in Section C.
 *
 * @cdk.module experimental
 *
 * @author Egon Willighagen
 */
public class Rule102dot1 extends NamingRule {

    public String getName() {
        return "C-102.1";
    }

    public IUPACNamePart apply(AtomContainer m) {
        IUPACNamePart inp = null;
        if (m instanceof Fragment) {
            if (m.getAtomCount() == 1) {
            	org.openscience.cdk.interfaces.IAtom atom = m.getAtom(0);
                String symbol = atom.getSymbol();
                m.setProperty(COMPLETED_FLAG, "yes");
                atom.setProperty(ATOM_NAMED_FLAG, "yes");
                if ("F".equals(symbol)) {
                    inp = new IUPACNamePart(localize("fluoro"), this);
                } else if ("Br".equals(symbol)) {
                    inp = new IUPACNamePart(localize("bromo"), this);
                } else if ("Cl".equals(symbol)) {
                    inp = new IUPACNamePart(localize("chloro"), this);
                } else {
                    m.setProperty(COMPLETED_FLAG, "no");
                    atom.setProperty(ATOM_NAMED_FLAG, "no");
                }
            }
        }
        return inp;
    };
}
