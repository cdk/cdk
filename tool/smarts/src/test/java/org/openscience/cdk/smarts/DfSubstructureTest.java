/*
 * Copyright (C) 2018  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.openscience.cdk.smarts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.DfPattern;
import org.openscience.cdk.isomorphism.Pattern;

class DfSubstructureTest extends SubstructureTest {

    @Override
    Pattern create(IAtomContainer container) {
        return DfPattern.findSubstructure(container);
    }

    @Test
    void matchRoot() throws Exception {
        IAtomContainer mol  = smi("OC(=O)C(=O)O");
        IAtomContainer qry  = sma("O=*");
        DfPattern      ptrn = DfPattern.findSubstructure(qry);
        Assertions.assertFalse(ptrn.matchesRoot(mol.getAtom(0)));
        Assertions.assertTrue(ptrn.matchesRoot(mol.getAtom(2)));
        Assertions.assertTrue(ptrn.matchesRoot(mol.getAtom(4)));
        Assertions.assertFalse(ptrn.matchesRoot(mol.getAtom(5)));
    }
}
