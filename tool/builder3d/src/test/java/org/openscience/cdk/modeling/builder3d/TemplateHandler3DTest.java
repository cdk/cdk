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
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.fingerprint.IBitFingerprint;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.isomorphism.matchers.QueryChemObject;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.templates.TestMoleculeFactory;
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

    private static BitSet parseBitSet(String str) throws Exception {
        return TemplateHandler3D.getBitSetFromFile(new StringTokenizer(str, "\t ;{, }"));
    }

    @Test
    public void testFingerprints() throws Exception {
        BitSet[] expected = new BitSet[]{
            parseBitSet("{3, 5, 8, 18, 29, 33, 39, 65, 71, 90, 105, 125, 140, 170, 182, 192, 199, 203, 209, 213, 226, 271, 272, 287, 301, 304, 319, 368, 386, 423, 433, 540, 590, 605, 618, 620, 629, 641, 649, 672, 681, 690, 694, 696, 697, 716, 726, 745, 748, 751, 760, 765, 775, 777, 780, 792, 799, 805, 810, 825, 829, 836, 844, 850, 876, 880, 882, 888, 899, 914, 924, 929, 932, 935, 967, 971, 1004, 1013, 1015, 1023}"),
            parseBitSet("{3, 8, 18, 29, 33, 65, 90, 101, 109, 117, 125, 127, 140, 170, 190, 192, 209, 213, 218, 226, 271, 272, 286, 287, 301, 304, 319, 386, 423, 433, 566, 590, 605, 618, 629, 641, 646, 649, 672, 690, 694, 696, 716, 726, 745, 748, 765, 775, 777, 780, 783, 792, 805, 810, 825, 829, 836, 844, 850, 876, 882, 899, 914, 924, 932, 934, 956, 967, 971, 994, 1004, 1013, 1015, 1023}"),
            parseBitSet("{3, 18, 26, 32, 33, 43, 140, 155, 188, 189, 226, 238, 262, 267, 287, 315, 319, 326, 375, 450, 577, 629, 644, 690, 719, 732, 745, 746, 751, 775, 847, 850, 881, 959, 971, 995, 1015, 1019}"),
            parseBitSet("{3, 18, 33, 192, 319, 745, 780, 882}"),
            parseBitSet("{3, 13, 18, 22, 26, 29, 33, 43, 71, 85, 90, 101, 103, 109, 117, 118, 125, 127, 140, 145, 153, 155, 182, 188, 189, 190, 199, 218, 225, 226, 238, 269, 272, 286, 287, 301, 304, 315, 319, 326, 370, 375, 386, 408, 423, 433, 450, 493, 502, 556, 566, 577, 590, 598, 605, 617, 618, 629, 644, 649, 672, 679, 690, 691, 694, 696, 716, 719, 727, 732, 745, 748, 751, 760, 762, 765, 775, 777, 783, 805, 806, 810, 821, 829, 844, 847, 850, 876, 888, 899, 914, 923, 924, 926, 927, 929, 934, 956, 959, 966, 971, 990, 995, 1006, 1013, 1015, 1019}"),
            parseBitSet("{3, 18, 29, 33, 53, 65, 90, 105, 125, 192, 203, 269, 271, 272, 293, 301, 319, 345, 364, 376, 386, 433, 540, 569, 590, 605, 618, 641, 649, 672, 675, 681, 696, 745, 748, 765, 780, 790, 798, 799, 801, 805, 825, 829, 836, 837, 844, 853, 876, 882, 891, 899, 914, 924, 932, 967, 996, 1004, 1013}"),
            parseBitSet("{3, 18, 33, 192, 319, 745, 780, 882}"),
            parseBitSet("{3, 18, 33, 192, 319, 745, 780, 882}"),
            parseBitSet("{3, 18, 26, 32, 33, 43, 140, 155, 188, 189, 226, 238, 262, 267, 287, 315, 319, 326, 375, 450, 577, 629, 644, 690, 719, 732, 745, 746, 751, 775, 847, 850, 881, 959, 971, 995, 1015, 1019}"),
            parseBitSet("{3, 18, 29, 33, 90, 105, 125, 272, 280, 301, 433, 521, 590, 618, 651, 672, 696, 698, 745, 760, 829, 844, 876, 890, 899, 924, 1013}")};

        String filename = "data/mdl/fingerprints_from_modelbuilder3d.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        List<IBitFingerprint> data = new TemplateExtractor().makeFingerprintsFromSdf(true, false,
                new HashMap<String, Integer>(), new BufferedReader(new InputStreamReader(ins)), 10);
        QueryChemObject obj = new QueryChemObject(DefaultChemObjectBuilder.getInstance());
        obj.getBuilder();
        for (int i = 0; i < data.size(); i++) {
            IBitFingerprint bs = data.get(i);
            Assert.assertEquals(expected[i], bs.asBitSet());
        }
    }

    @Test
    public void testAnonFingerprints() throws Exception {
        BitSet[] expected = new BitSet[]{
            parseBitSet("{148, 206, 392, 542, 637, 742, 752, 830}"),
            parseBitSet("{148, 206, 392, 542, 637, 742, 752, 830}"),
            parseBitSet("{148, 206, 392, 542, 637, 742, 752, 830}"),
            parseBitSet("{148, 206, 392, 542, 637, 742, 752, 830}"),
            parseBitSet("{148, 206, 392, 542, 637, 742, 752, 830}"),
            parseBitSet("{148, 206, 392, 542, 637, 742, 752, 830}"),
            parseBitSet("{148, 206, 392, 542, 637, 742, 752, 830}"),
            parseBitSet("{148, 206, 392, 542, 637, 742, 752, 830}"),
            parseBitSet("{148, 206, 392, 542, 637, 742, 752, 830}"),
            parseBitSet("{148, 206, 392, 542, 637, 742, 752, 830}")};

        String filename = "data/mdl/fingerprints_from_modelbuilder3d.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        List<IBitFingerprint> data = new TemplateExtractor().makeFingerprintsFromSdf(true, true,
                                                                                     new HashMap<String, Integer>(), new BufferedReader(new InputStreamReader(ins)), 10);
        QueryChemObject obj = new QueryChemObject(DefaultChemObjectBuilder.getInstance());
        obj.getBuilder();
        for (int i = 0; i < data.size(); i++) {
            IBitFingerprint bs = data.get(i);
            Assert.assertEquals(expected[i], bs.asBitSet());
        }
    }

    @Test
    public void testMapTemplates_IAtomContainer_double() throws Exception {
        IAtomContainer ac = TestMoleculeFactory.makeBicycloRings();
        TemplateHandler3D th3d = TemplateHandler3D.getInstance();
        ForceFieldConfigurator ffc = new ForceFieldConfigurator();
        ffc.setForceFieldConfigurator("mm2", ac.getBuilder());
        IRingSet ringSetMolecule = ffc.assignAtomTyps(ac);
        List<IRingSet> ringSystems = RingPartitioner.partitionRings(ringSetMolecule);
        IRingSet largestRingSet = RingSetManipulator.getLargestRingSet(ringSystems);
        IAtomContainer largestRingSetContainer = RingSetManipulator.getAllInOneContainer(largestRingSet);
        th3d.mapTemplates(largestRingSetContainer, largestRingSetContainer.getAtomCount());
        for (int i = 0; i < ac.getAtomCount(); i++) {
            Assert.assertNotNull(ac.getAtom(i).getPoint3d());
        }
        ModelBuilder3DTest.checkAverageBondLength(ac);
    }

}
