/* Copyright (C) 2009  Stefan Kuhn <shk3@users.sf.net>
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
package org.openscience.cdk.structgen.stochastic;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.SaturationChecker;

/**
 * @cdk.module test-structgen
 */
public class PartialFilledStructureMergerTest extends CDKTestCase {

    @Test
    public void testGenerate_IAtomContainerSet() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainerSet acs = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        acs.addAtomContainer(sp.parseSmiles("CCCCC"));
        acs.addAtomContainer(sp.parseSmiles("C1=C(C1)CC"));
        acs.getAtomContainer(0).getAtom(0).setImplicitHydrogenCount(2);
        acs.getAtomContainer(0).getAtom(1).setImplicitHydrogenCount(2);
        acs.getAtomContainer(0).getAtom(2).setImplicitHydrogenCount(2);
        acs.getAtomContainer(0).getAtom(3).setImplicitHydrogenCount(2);
        acs.getAtomContainer(0).getAtom(4).setImplicitHydrogenCount(2);
        acs.getAtomContainer(1).getAtom(0).setImplicitHydrogenCount(0);
        acs.getAtomContainer(1).getAtom(1).setImplicitHydrogenCount(0);
        acs.getAtomContainer(1).getAtom(2).setImplicitHydrogenCount(2);
        acs.getAtomContainer(1).getAtom(3).setImplicitHydrogenCount(2);
        acs.getAtomContainer(1).getAtom(4).setImplicitHydrogenCount(2);
        PartialFilledStructureMerger pfsm = new PartialFilledStructureMerger();
        IAtomContainer result = pfsm.generate(acs);
        Assert.assertTrue(ConnectivityChecker.isConnected(result));
        Assert.assertTrue(new SaturationChecker().allSaturated(result));
    }

    @Test
    public void testPartialFilledStructureMerger2() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainerSet acs = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        acs.addAtomContainer(sp.parseSmiles("C=CCCC"));
        acs.addAtomContainer(sp.parseSmiles("C(C)=C1CC1"));
        acs.getAtomContainer(0).getAtom(0).setImplicitHydrogenCount(0);
        acs.getAtomContainer(0).getAtom(1).setImplicitHydrogenCount(0);
        acs.getAtomContainer(0).getAtom(2).setImplicitHydrogenCount(2);
        acs.getAtomContainer(0).getAtom(3).setImplicitHydrogenCount(2);
        acs.getAtomContainer(0).getAtom(4).setImplicitHydrogenCount(2);
        acs.getAtomContainer(1).getAtom(0).setImplicitHydrogenCount(0);
        acs.getAtomContainer(1).getAtom(1).setImplicitHydrogenCount(2);
        acs.getAtomContainer(1).getAtom(2).setImplicitHydrogenCount(0);
        acs.getAtomContainer(1).getAtom(3).setImplicitHydrogenCount(2);
        acs.getAtomContainer(1).getAtom(4).setImplicitHydrogenCount(2);
        PartialFilledStructureMerger pfsm = new PartialFilledStructureMerger();
        IAtomContainer result = pfsm.generate(acs);
        Assert.assertTrue(ConnectivityChecker.isConnected(result));
        Assert.assertTrue(new SaturationChecker().allSaturated(result));
    }

    @Test
    public void testPartialFilledStructureMerger3() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainerSet acs = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        acs.addAtomContainer(sp.parseSmiles("CCCCC"));
        acs.addAtomContainer(sp.parseSmiles("CCC"));
        acs.addAtomContainer(sp.parseSmiles("CC"));
        acs.getAtomContainer(0).getAtom(0).setImplicitHydrogenCount(2);
        acs.getAtomContainer(0).getAtom(1).setImplicitHydrogenCount(2);
        acs.getAtomContainer(0).getAtom(2).setImplicitHydrogenCount(2);
        acs.getAtomContainer(0).getAtom(3).setImplicitHydrogenCount(2);
        acs.getAtomContainer(0).getAtom(4).setImplicitHydrogenCount(2);
        acs.getAtomContainer(1).getAtom(0).setImplicitHydrogenCount(2);
        acs.getAtomContainer(1).getAtom(1).setImplicitHydrogenCount(2);
        acs.getAtomContainer(1).getAtom(2).setImplicitHydrogenCount(2);
        acs.getAtomContainer(2).getAtom(0).setImplicitHydrogenCount(2);
        acs.getAtomContainer(2).getAtom(1).setImplicitHydrogenCount(2);
        PartialFilledStructureMerger pfsm = new PartialFilledStructureMerger();
        IAtomContainer result = pfsm.generate(acs);
        Assert.assertTrue(ConnectivityChecker.isConnected(result));
        Assert.assertTrue(new SaturationChecker().allSaturated(result));
    }

    @Test
    public void testPartialFilledStructureMerger4() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainerSet acs = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        acs.addAtomContainer(sp.parseSmiles("CCCCC"));
        acs.addAtomContainer(sp.parseSmiles("CCCC"));
        acs.addAtomContainer(sp.parseSmiles("C"));
        acs.getAtomContainer(0).getAtom(0).setImplicitHydrogenCount(0);
        acs.getAtomContainer(0).getAtom(1).setImplicitHydrogenCount(2);
        acs.getAtomContainer(0).getAtom(2).setImplicitHydrogenCount(2);
        acs.getAtomContainer(0).getAtom(3).setImplicitHydrogenCount(2);
        acs.getAtomContainer(0).getAtom(4).setImplicitHydrogenCount(2);
        acs.getAtomContainer(1).getAtom(0).setImplicitHydrogenCount(0);
        acs.getAtomContainer(1).getAtom(1).setImplicitHydrogenCount(2);
        acs.getAtomContainer(1).getAtom(2).setImplicitHydrogenCount(2);
        acs.getAtomContainer(1).getAtom(3).setImplicitHydrogenCount(2);
        acs.getAtomContainer(2).getAtom(0).setImplicitHydrogenCount(2);
        PartialFilledStructureMerger pfsm = new PartialFilledStructureMerger();
        IAtomContainer result = pfsm.generate(acs);
        Assert.assertTrue(ConnectivityChecker.isConnected(result));
        Assert.assertTrue(new SaturationChecker().allSaturated(result));
    }

    @Test
    public void testPartialFilledStructureMerger5() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainerSet acs = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        acs.addAtomContainer(sp.parseSmiles("C1CCC1"));
        acs.addAtomContainer(sp.parseSmiles("C(C)CCC"));
        acs.addAtomContainer(sp.parseSmiles("C"));
        acs.getAtomContainer(0).getAtom(0).setImplicitHydrogenCount(0);
        acs.getAtomContainer(0).getAtom(1).setImplicitHydrogenCount(2);
        acs.getAtomContainer(0).getAtom(2).setImplicitHydrogenCount(2);
        acs.getAtomContainer(0).getAtom(3).setImplicitHydrogenCount(2);
        acs.getAtomContainer(1).getAtom(0).setImplicitHydrogenCount(0);
        acs.getAtomContainer(1).getAtom(1).setImplicitHydrogenCount(2);
        acs.getAtomContainer(1).getAtom(2).setImplicitHydrogenCount(2);
        acs.getAtomContainer(1).getAtom(3).setImplicitHydrogenCount(2);
        acs.getAtomContainer(1).getAtom(4).setImplicitHydrogenCount(2);
        acs.getAtomContainer(2).getAtom(0).setImplicitHydrogenCount(2);
        PartialFilledStructureMerger pfsm = new PartialFilledStructureMerger();
        IAtomContainer result = pfsm.generate(acs);
        Assert.assertTrue(ConnectivityChecker.isConnected(result));
        Assert.assertTrue(new SaturationChecker().allSaturated(result));
    }
}
