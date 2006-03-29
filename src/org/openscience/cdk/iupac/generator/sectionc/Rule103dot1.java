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
import org.openscience.cdk.Molecule;
import org.openscience.cdk.iupac.generator.IUPACNamePart;
import org.openscience.cdk.iupac.generator.NamingRule;

/**
 *  This class implements IUPAC rule 103.1 in Section C.
 *
 * @cdk.module experimental
 *
 * @author Egon Willighagen
 */
public class Rule103dot1 extends NamingRule {

    public String getName() {
        return "C-103.1";
    }

    public IUPACNamePart apply(AtomContainer m) {
        IUPACNamePart inp = null;
        m.setProperty(COMPLETED_FLAG, "no");
        if (m instanceof Molecule) {
            if ((((Integer)m.getProperty(BROMO_COUNT)).intValue() > 0) ||
                (((Integer)m.getProperty(CHLORO_COUNT)).intValue() > 0) ||
                (((Integer)m.getProperty(FLUORO_COUNT)).intValue() > 0)) {
                for (int i = 0; i < m.getAtomCount(); i++) {
                	org.openscience.cdk.interfaces.IAtom a = m.getAtomAt(i);
                    if ("Br".equals(a.getSymbol())) {
                        a.setProperty(ATOM_NAMED_FLAG, "yes");
                        return new IUPACNamePart(localize("bromide"), this);
                    } else if ("Cl".equals(a.getSymbol())) {
                        a.setProperty(ATOM_NAMED_FLAG, "yes");
                        return new IUPACNamePart(localize("chloride"), this);
                    } else if ("F".equals(a.getSymbol())) {
                        a.setProperty(ATOM_NAMED_FLAG, "yes");
                        return new IUPACNamePart(localize("fluoride"), this);
                    }
                }
            }
        }
        return inp;
    };
}
