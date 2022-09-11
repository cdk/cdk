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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.SaturationChecker;

/**
 * @cdk.module test-structgen
 */
class PartialFilledStructureMergerTest extends CDKTestCase {

    @Test
    void testGenerate_IAtomContainerSet() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainerSet acs = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        acs.addAtomContainer(sp.parseSmiles("[CH2]CCC[CH2]"));
        acs.addAtomContainer(sp.parseSmiles("[C]1=C(C1)C[CH2]"));
        PartialFilledStructureMerger pfsm = new PartialFilledStructureMerger();
        IAtomContainer result = pfsm.generate(acs);
        Assertions.assertTrue(ConnectivityChecker.isConnected(result));
        Assertions.assertTrue(new SaturationChecker().allSaturated(result));
    }

    @Test
    void testPartialFilledStructureMerger2() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainerSet acs = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        acs.addAtomContainer(sp.parseSmiles("[C]=[C]CC[CH2]"));
        acs.addAtomContainer(sp.parseSmiles("[C]([CH2])=C1CC1"));
        PartialFilledStructureMerger pfsm = new PartialFilledStructureMerger();
        IAtomContainer result = pfsm.generate(acs);
        Assertions.assertTrue(ConnectivityChecker.isConnected(result));
        Assertions.assertTrue(new SaturationChecker().allSaturated(result));
    }

    @Test
    void testPartialFilledStructureMerger3() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainerSet acs = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        acs.addAtomContainer(sp.parseSmiles("[CH2]CCC[CH2]"));
        acs.addAtomContainer(sp.parseSmiles("[CH2]C[CH2]"));
        acs.addAtomContainer(sp.parseSmiles("[CH2][CH2]"));
        PartialFilledStructureMerger pfsm = new PartialFilledStructureMerger();
        IAtomContainer result = pfsm.generate(acs);
        Assertions.assertTrue(ConnectivityChecker.isConnected(result));
        Assertions.assertTrue(new SaturationChecker().allSaturated(result));
    }

    @Test
    void testPartialFilledStructureMerger4() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainerSet acs = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        acs.addAtomContainer(sp.parseSmiles("[C]CCC[CH2]"));
        acs.addAtomContainer(sp.parseSmiles("[C]CC[CH2]"));
        acs.addAtomContainer(sp.parseSmiles("[CH2]"));
        PartialFilledStructureMerger pfsm = new PartialFilledStructureMerger();
        IAtomContainer result = pfsm.generate(acs);
        Assertions.assertTrue(ConnectivityChecker.isConnected(result));
        Assertions.assertTrue(new SaturationChecker().allSaturated(result));
    }

    @Test
    void testPartialFilledStructureMerger5() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainerSet acs = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        acs.addAtomContainer(sp.parseSmiles("[C]1CCC1"));
        acs.addAtomContainer(sp.parseSmiles("[C]([CH2])CC[CH2]"));
        acs.addAtomContainer(sp.parseSmiles("[CH2]"));
        PartialFilledStructureMerger pfsm = new PartialFilledStructureMerger();
        IAtomContainer result = pfsm.generate(acs);
        Assertions.assertTrue(ConnectivityChecker.isConnected(result));
        Assertions.assertTrue(new SaturationChecker().allSaturated(result));
    }
}
