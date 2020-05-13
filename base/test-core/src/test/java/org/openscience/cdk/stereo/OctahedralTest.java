/*
 * Copyright (c) 2017 John Mayfield <jwmay@users.sf.net>
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

package org.openscience.cdk.stereo;

import org.junit.Test;
import org.mockito.Mockito;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.Arrays;
import java.util.Iterator;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class OctahedralTest {


    @Test public void normalizeOh() throws InvalidSmilesException {
        SmilesParser             smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer           mol    = smipar.parseSmiles("C[Co@OH8](F)(Br)(Cl)(I)S");
        Iterator<IStereoElement> ses    = mol.stereoElements().iterator();
        assertTrue(ses.hasNext());
        IStereoElement se = ses.next();
        assertThat(se, instanceOf(Octahedral.class));
        assertThat(se.getConfigOrder(), is(8));
        Octahedral oh = (Octahedral) se;
        Octahedral ohNorm = oh.normalize();
        assertThat(ohNorm.getCarriers(), is(Arrays.asList(
            mol.getAtom(0),
            mol.getAtom(2),
            mol.getAtom(4),
            mol.getAtom(3),
            mol.getAtom(5),
            mol.getAtom(6)
        )));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooManyCarriers() {
        IAtom a0 = Mockito.mock(IAtom.class);
        IAtom a1 = Mockito.mock(IAtom.class);
        IAtom a2 = Mockito.mock(IAtom.class);
        IAtom a3 = Mockito.mock(IAtom.class);
        IAtom a4 = Mockito.mock(IAtom.class);
        IAtom a5 = Mockito.mock(IAtom.class);
        IAtom a6 = Mockito.mock(IAtom.class);
        IAtom a7 = Mockito.mock(IAtom.class);
        new Octahedral(a0, new IAtom[]{a1,a2,a3,a4,a5,a6,a7}, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void badConfigurationOrder() {
        IAtom a0 = Mockito.mock(IAtom.class);
        IAtom a1 = Mockito.mock(IAtom.class);
        IAtom a2 = Mockito.mock(IAtom.class);
        IAtom a3 = Mockito.mock(IAtom.class);
        IAtom a4 = Mockito.mock(IAtom.class);
        IAtom a5 = Mockito.mock(IAtom.class);
        IAtom a6 = Mockito.mock(IAtom.class);
        new Octahedral(a0, new IAtom[]{a1,a2,a3,a4,a5,a6}, 32);
    }
}
