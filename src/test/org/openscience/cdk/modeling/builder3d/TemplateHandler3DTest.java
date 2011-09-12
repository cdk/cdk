/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *                    2011  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@list.sourceforge.net
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
package org.openscience.cdk.modeling.builder3d;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.fingerprint.IBitFingerprint;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

/**
 * @cdk.module test-builder3d
 *
 * @author      chhoppe
 * @author      Christoph Steinbeck
 * @cdk.created 2004-11-04
 */
public class TemplateHandler3DTest extends CDKTestCase {
		
	@Test
	public void testGetInstance() throws Exception {
		TemplateHandler3D th3d = TemplateHandler3D.getInstance();
		// need to trigger a load of the templates
		th3d.mapTemplates(new AtomContainer(), 0);
		Assert.assertEquals(10751, th3d.getTemplateCount());
	}

	@Test
	public void testFingerprints() throws Exception{
		BitSet[] bsmb=new BitSet[10];
		bsmb[0]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{18, 37, 61, 74, 105, 110, 115, 149, 187, 227, 249, 302, 307, 337, 343, 346, 356, 395, 409, 444, 478, 514, 527, 552, 567, 569, 576, 580, 683, 689, 710, 717, 747, 800, 810, 830, 837, 867, 873, 916, 929, 932, 935, 971, 983}", "\t ;{, }"));
		bsmb[1]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{18, 37, 61, 74, 105, 110, 115, 149, 187, 227, 249, 302, 307, 337, 343, 346, 356, 395, 409, 444, 478, 514, 527, 552, 567, 569, 576, 580, 683, 689, 710, 717, 747, 800, 810, 830, 837, 867, 873, 916, 929, 932, 935, 971, 983}", "\t ;{, }"));
		bsmb[2]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{18, 42, 72, 92, 175, 184, 225, 227, 231, 246, 252, 271, 278, 280, 289, 298, 307, 308, 322, 349, 356, 360, 391, 395, 415, 424, 444, 467, 498, 500, 502, 520, 532, 541, 543, 552, 563, 567, 569, 571, 610, 614, 629, 654, 673, 692, 697, 700, 711, 717, 731, 747, 761, 778, 783, 800, 810, 818, 830, 837, 842, 863, 867, 873, 879, 882, 883, 913, 916, 932, 935, 971, 983, 1011, 1019, 1020}", "\t ;{, }"));
		bsmb[3]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{110, 187, 409, 444, 689, 747, 873, 916, 983}", "\t ;{, }"));
		bsmb[4]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{18, 37, 42, 61, 72, 74, 92, 105, 110, 115, 140, 149, 162, 184, 187, 195, 199, 207, 225, 227, 231, 234, 249, 252, 264, 278, 280, 289, 302, 307, 308, 322, 331, 337, 343, 346, 349, 350, 356, 360, 391, 395, 409, 415, 435, 444, 467, 478, 500, 502, 514, 520, 527, 532, 541, 543, 552, 563, 567, 569, 571, 576, 580, 610, 613, 614, 629, 640, 673, 683, 689, 692, 710, 711, 717, 731, 747, 761, 778, 783, 798, 800, 810, 818, 830, 837, 842, 855, 863, 867, 873, 879, 882, 883, 887, 892, 913, 916, 929, 932, 935, 971, 983, 1016, 1019, 1020}", "\t ;{, }"));
		bsmb[5]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{110, 187, 409, 444, 689, 747, 873, 916, 983}", "\t ;{, }"));
		bsmb[6]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{110, 187, 409, 444, 689, 747, 873, 916, 983}", "\t ;{, }"));
		bsmb[7]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{110, 409, 444, 689, 747, 873, 916, 983}", "\t ;{, }"));
		bsmb[8]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{18, 42, 72, 92, 175, 184, 225, 227, 231, 246, 252, 271, 278, 280, 289, 298, 307, 308, 322, 349, 356, 360, 391, 395, 415, 424, 444, 467, 498, 500, 502, 520, 532, 541, 543, 552, 563, 567, 569, 571, 610, 614, 629, 654, 673, 692, 697, 700, 711, 717, 731, 747, 761, 778, 783, 800, 810, 818, 830, 837, 842, 863, 867, 873, 879, 882, 883, 913, 916, 932, 935, 971, 983, 1011, 1019, 1020}", "\t ;{, }"));
		bsmb[9]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{110, 187, 409, 444, 689, 747, 873, 916, 983}", "\t ;{, }"));		
        
		String filename = "data/mdl/fingerprints_from_modelbuilder3d.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        List<IBitFingerprint> data= new TemplateExtractor().makeFingerprintsFromSdf(
            true,false, new HashMap<String,Integer>(),
            new BufferedReader ( new InputStreamReader ( ins ) ),
            10
        );
        for(int i=0; i<data.size(); i++){
        	IBitFingerprint bs = data.get(i);
        	Assert.assertEquals(bsmb[i], bs);
        }		
	}
	
	@Test
	public void testMapTemplates_IAtomContainer_double() throws Exception{
	    IAtomContainer ac = MoleculeFactory.makeBicycloRings();
		TemplateHandler3D th3d = TemplateHandler3D.getInstance();
		ForceFieldConfigurator ffc = new ForceFieldConfigurator();
		ffc.setForceFieldConfigurator("mm2");
		IRingSet ringSetMolecule = ffc.assignAtomTyps(ac);
		List<IRingSet> ringSystems = RingPartitioner.partitionRings(ringSetMolecule);
		IRingSet largestRingSet = RingSetManipulator.getLargestRingSet(ringSystems);
		IAtomContainer largestRingSetContainer = RingSetManipulator.getAllInOneContainer(largestRingSet);
		th3d.mapTemplates(largestRingSetContainer, largestRingSetContainer.getAtomCount());
		for (int i=0;i<ac.getAtomCount();i++){
			Assert.assertNotNull(ac.getAtom(i).getPoint3d());
		}
		ModelBuilder3DTest.checkAverageBondLength(ac);
	}

}
