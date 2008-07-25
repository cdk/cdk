/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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

package org.openscience.cdk.modulesuites;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.QsarmolecularCoverageTest;
import org.openscience.cdk.qsar.ChiIndexUtilsTest;
import org.openscience.cdk.qsar.DescriptorEngineTest;
import org.openscience.cdk.qsar.DescriptorNamesTest;
import org.openscience.cdk.qsar.descriptors.molecular.ALOGPDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.APolDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.AromaticAtomsCountDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.AromaticBondsCountDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.AutocorrelationDescriptorChargeTest;
import org.openscience.cdk.qsar.descriptors.molecular.AutocorrelationDescriptorMassTest;
import org.openscience.cdk.qsar.descriptors.molecular.AutocorrelationDescriptorPolarizabilityTest;
import org.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.BPolDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.BondCountDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.CPSADescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.CarbonTypesDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.ChiChainDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.ChiClusterDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.ChiPathClusterDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.ChiPathDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.EccentricConnectivityIndexDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.GravitationalIndexDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.HBondAcceptorCountDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.HBondDonorCountDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.KappaShapeIndicesDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.KierHallSmartsDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.LargestChainDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.LargestPiSystemDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.LengthOverBreadthDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.LongestAliphaticChainDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.MDEDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.MomentOfInertiaDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.PetitjeanNumberDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.PetitjeanShapeIndexDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.RotatableBondsCountDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.RuleOfFiveDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.TPSADescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.VAdjMaDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.WHIMDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.WeightDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.WeightedPathDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.WienerNumbersDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.ZagrebIndexDescriptorTest;


/**
 * TestSuite that runs all the sample tests.
 *
 * @cdk.module  test-qsarmolecular
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 * 
 * @cdk.bug     1860497
 */
public class MqsarmolecularTests {

    public static Test suite() {

        TestSuite suite = new TestSuite("All QSAR molecular Tests");

        suite.addTest(QsarmolecularCoverageTest.suite());
        
        // Individual Tests - Please add correlatively	
        suite.addTest(ChiIndexUtilsTest.suite());
        suite.addTest(new JUnit4TestAdapter(DescriptorEngineTest.class));
        suite.addTest(new JUnit4TestAdapter(DescriptorNamesTest.class));

//      from cdk.test.qsar.molecular
        suite.addTest(ALOGPDescriptorTest.suite());
        suite.addTest(APolDescriptorTest.suite());
        suite.addTest(AromaticAtomsCountDescriptorTest.suite());
        suite.addTest(AromaticBondsCountDescriptorTest.suite());
        suite.addTest(AtomCountDescriptorTest.suite());
        suite.addTest(BCUTDescriptorTest.suite());
        suite.addTest(BondCountDescriptorTest.suite());
        suite.addTest(BPolDescriptorTest.suite());
        suite.addTest(ChiChainDescriptorTest.suite());
        suite.addTest(ChiPathDescriptorTest.suite());
        suite.addTest(ChiClusterDescriptorTest.suite());
        suite.addTest(ChiPathClusterDescriptorTest.suite());
        suite.addTest(CPSADescriptorTest.suite());
        suite.addTest(EccentricConnectivityIndexDescriptorTest.suite());
        suite.addTest(GravitationalIndexDescriptorTest.suite());
        suite.addTest(HBondAcceptorCountDescriptorTest.suite());
        suite.addTest(HBondDonorCountDescriptorTest.suite());
        suite.addTest(KappaShapeIndicesDescriptorTest.suite());
        suite.addTest(KierHallSmartsDescriptorTest.suite());
        suite.addTest(LargestChainDescriptorTest.suite());
        suite.addTest(LargestPiSystemDescriptorTest.suite());
        suite.addTest(LengthOverBreadthDescriptorTest.suite());
        suite.addTest(LongestAliphaticChainDescriptorTest.suite());
        suite.addTest(MDEDescriptorTest.suite());
        suite.addTest(MomentOfInertiaDescriptorTest.suite());
        suite.addTest(PetitjeanNumberDescriptorTest.suite());
        suite.addTest(PetitjeanShapeIndexDescriptorTest.suite());
        suite.addTest(RotatableBondsCountDescriptorTest.suite());
        suite.addTest(RuleOfFiveDescriptorTest.suite());
        suite.addTest(TPSADescriptorTest.suite());
        suite.addTest(VAdjMaDescriptorTest.suite());
        suite.addTest(WeightDescriptorTest.suite());
        suite.addTest(WeightedPathDescriptorTest.suite());
        suite.addTest(WHIMDescriptorTest.suite());
        suite.addTest(WienerNumbersDescriptorTest.suite());
        suite.addTest(XLogPDescriptorTest.suite());
        suite.addTest(ZagrebIndexDescriptorTest.suite());
        suite.addTest(AutocorrelationDescriptorChargeTest.suite());
        suite.addTest(AutocorrelationDescriptorMassTest.suite());
        suite.addTest(AutocorrelationDescriptorPolarizabilityTest.suite());
        suite.addTest(CarbonTypesDescriptorTest.suite());

        return suite;
    }

}
