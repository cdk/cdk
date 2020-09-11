/*
 * Copyright (c) 2018 John Mayfield
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
package org.openscience.cdk.tools.manipulator;

import org.junit.Test;
import org.mockito.Mockito;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SgroupManipulatorTest {

    @Test
    public void copyNull() throws Exception {
        assertNull(SgroupManipulator.copy(null, null));
    }

    @Test
    public void copySgroups() throws Exception {
        List<Sgroup> sgroups = new ArrayList<>();
        IAtom a1 = Mockito.mock(IAtom.class);
        IAtom a2 = Mockito.mock(IAtom.class);
        IBond b1 = Mockito.mock(IBond.class);
        IBond b2 = Mockito.mock(IBond.class);
        Sgroup       sgroup       = new Sgroup();
        sgroup.setType(SgroupType.CtabStructureRepeatUnit);
        sgroup.setSubscript("n");
        sgroup.addAtom(a1);
        sgroup.addAtom(a2);
        sgroup.addBond(b1);
        sgroup.addBond(b2);
        sgroups.add(sgroup);
        List<Sgroup> copied = SgroupManipulator.copy(sgroups, null);
        Sgroup copiedSgroup = copied.get(0);
        assertThat(copiedSgroup, not(sameInstance(sgroup)));
        assertThat(copiedSgroup.getType(), is(sgroup.getType()));
        assertThat(copiedSgroup.getSubscript(), is(sgroup.getSubscript()));
        assertThat(copiedSgroup.getAtoms(), is(sgroup.getAtoms()));
        assertThat(copiedSgroup.getBonds(), is(sgroup.getBonds()));
    }


    @Test
    public void copySgroups2() throws Exception {
        List<Sgroup>                 sgroups = new ArrayList<>();
        Map<IChemObject,IChemObject> replace = new HashMap<>();

        IAtom                        a1      = Mockito.mock(IAtom.class);
        IAtom                        a2      = Mockito.mock(IAtom.class);
        IBond                        b1      = Mockito.mock(IBond.class);
        IBond                        b2      = Mockito.mock(IBond.class);

        IAtom                        a1copy      = Mockito.mock(IAtom.class);
        IAtom                        a2copy      = Mockito.mock(IAtom.class);
        IBond                        b1copy      = Mockito.mock(IBond.class);
        IBond                        b2copy      = Mockito.mock(IBond.class);

        replace.put(a1, a1copy);
        replace.put(a2, a2copy);
        replace.put(b1, b1copy);
        replace.put(b2, b2copy);

        Sgroup                       sgroup  = new Sgroup();
        sgroup.setType(SgroupType.CtabStructureRepeatUnit);
        sgroup.setSubscript("n");
        sgroup.addAtom(a1);
        sgroup.addAtom(a2);
        sgroup.addBond(b1);
        sgroup.addBond(b2);
        sgroups.add(sgroup);
        List<Sgroup> copied = SgroupManipulator.copy(sgroups, replace);
        Sgroup copiedSgroup = copied.get(0);
        assertThat(copiedSgroup, not(sameInstance(sgroup)));
        assertThat(copiedSgroup.getType(), is(sgroup.getType()));
        assertThat(copiedSgroup.getSubscript(), is(sgroup.getSubscript()));
        assertThat(copiedSgroup.getAtoms(), is(not(sgroup.getAtoms())));
        assertThat(copiedSgroup.getBonds(), is(not(sgroup.getBonds())));
        assertTrue(copiedSgroup.getAtoms().contains(a1copy));
        assertTrue(copiedSgroup.getAtoms().contains(a2copy));
        assertTrue(copiedSgroup.getBonds().contains(b1copy));
        assertTrue(copiedSgroup.getBonds().contains(b2copy));
    }
}
