/* Copyright (C) 2012 Daniel Szisz
 *
 * Contact: orlando@caesar.elte.hu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.modeling.builder3d;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

import org.junit.Test;

/**
 * Test class for {@link TemplateHandler3D}.
 *
 * @author danielszisz
 * @created 05/14/2012
 * @cdk.module test-builder3d
 */
public class FurtherTemplateHandler3DTest {

    @Test
    public void testLoadTemplates() throws Exception {
        // test order is not guaranteed so the templates may have already been loaded,
        // to avoid this we create a new instance using reflection. This is a hack and
        // requires changing if the underlying class is modified
        Constructor<TemplateHandler3D> constructor = TemplateHandler3D.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        TemplateHandler3D tmphandler3d = constructor.newInstance();
        assertEquals(0, tmphandler3d.getTemplateCount());
        //cannot test TemplateHandler3D#loadTemplates as it is a private method

        // but we can using reflection ...
        Method loadTemplates = TemplateHandler3D.class.getDeclaredMethod("loadTemplates");
        loadTemplates.setAccessible(true); // private -> public
        loadTemplates.invoke(tmphandler3d);
        assertEquals(10751, tmphandler3d.getTemplateCount());
    }

    @Test
    public void testMapTemplates_cyclicMol1() throws Exception {
        TemplateHandler3D tmphandler3d = TemplateHandler3D.getInstance();
        String cyclicMolSmi = "O(CC(O)CN1CCN(CC1)CC(=O)Nc1c(cccc1C)C)c1c(cccc1)OC";
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        SmilesParser smiparser = new SmilesParser(builder);
        IAtomContainer molecule = smiparser.parseSmiles(cyclicMolSmi);
        ForceFieldConfigurator forcefconf = new ForceFieldConfigurator();
        forcefconf.setForceFieldConfigurator("mmff94", builder);
        IRingSet rings = forcefconf.assignAtomTyps(molecule);
        List<IRingSet> ringSystems = RingPartitioner.partitionRings(rings);
        IRingSet largestRingSet = RingSetManipulator.getLargestRingSet(ringSystems);
        IAtomContainer allAtomsInOneContainer = RingSetManipulator.getAllInOneContainer(largestRingSet);
        tmphandler3d.mapTemplates(allAtomsInOneContainer, allAtomsInOneContainer.getAtomCount());
        for (int j = 0; j < allAtomsInOneContainer.getAtomCount(); j++) {
            assertNotNull(allAtomsInOneContainer.getAtom(j).getPoint3d());
        }
    }

    @Test
    public void testMapTemplates_cyclicMol2() throws Exception {
        TemplateHandler3D tmphandler3d = TemplateHandler3D.getInstance();
        String cyclicMolSmi = "CC(C)(C)NC(=O)C1CN(CCN1CC(CC(Cc1ccccc1)C(=O)NC1c2ccccc2CC1O)O)Cc1cccnc1";
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        SmilesParser smiparser = new SmilesParser(builder);
        IAtomContainer molecule = smiparser.parseSmiles(cyclicMolSmi);
        ForceFieldConfigurator forcefconf = new ForceFieldConfigurator();
        forcefconf.setForceFieldConfigurator("mmff94", builder);
        IRingSet rings = forcefconf.assignAtomTyps(molecule);
        List<IRingSet> ringSystems = RingPartitioner.partitionRings(rings);
        IRingSet largestRingSet = RingSetManipulator.getLargestRingSet(ringSystems);
        IAtomContainer allAtomsInOneContainer = RingSetManipulator.getAllInOneContainer(largestRingSet);
        tmphandler3d.mapTemplates(allAtomsInOneContainer, allAtomsInOneContainer.getAtomCount());
        for (int j = 0; j < allAtomsInOneContainer.getAtomCount(); j++) {
            assertNotNull(allAtomsInOneContainer.getAtom(j).getPoint3d());
        }
    }

}
