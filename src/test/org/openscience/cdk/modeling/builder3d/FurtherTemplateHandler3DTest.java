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

import static org.junit.Assert.*;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

import java.util.List;

/**
 * 
 * @author danielszisz
 * @created 05/14/2012
 */
public class FurtherTemplateHandler3DTest {
	
	@Test public void testLoadTemplates() throws CDKException {
		TemplateHandler3D tmphandler3d = TemplateHandler3D.getInstance();
		int tmpCounter = tmphandler3d.getTemplateCount();
		assertEquals(0, tmpCounter);
		//cannot test TemplateHandler3D#loadTemplates as it is a private method
	}
	
	@Test public void testMapTemplates_cyclicMol1() throws Exception {
		TemplateHandler3D tmphandler3d = TemplateHandler3D.getInstance();
		String cyclicMolSmi = "O(CC(O)CN1CCN(CC1)CC(=O)Nc1c(cccc1C)C)c1c(cccc1)OC";
		IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
		SmilesParser smiparser = new SmilesParser(builder);
		IAtomContainer molecule = smiparser.parseSmiles(cyclicMolSmi);
		ForceFieldConfigurator forcefconf = new ForceFieldConfigurator();
		forcefconf.setForceFieldConfigurator("mmff94");
		IRingSet rings = forcefconf.assignAtomTyps(molecule);
		List<IRingSet> ringSystems = RingPartitioner.partitionRings(rings);
		IRingSet largestRingSet = RingSetManipulator.getLargestRingSet(ringSystems);
		IAtomContainer allAtomsInOneContainer = RingSetManipulator.
				getAllInOneContainer(largestRingSet);
		tmphandler3d.mapTemplates(allAtomsInOneContainer, allAtomsInOneContainer.getAtomCount());
		ModelBuilder3DTest.checkAverageBondLength(molecule);
	}

}
