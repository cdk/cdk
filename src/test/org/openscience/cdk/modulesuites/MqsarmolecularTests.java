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
import org.openscience.cdk.coverage.QsarmolecularCoverageTest;
import org.openscience.cdk.qsar.ChiIndexUtilsTest;
import org.openscience.cdk.qsar.DescriptorEngineTest;
import org.openscience.cdk.qsar.DescriptorNamesTest;
import org.openscience.cdk.qsar.descriptors.molecular.*;


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
        suite.addTest(new JUnit4TestAdapter(ChiIndexUtilsTest.class));
        suite.addTest(new JUnit4TestAdapter(DescriptorEngineTest.class));
        suite.addTest(new JUnit4TestAdapter(DescriptorNamesTest.class));

//      from cdk.test.qsar.molecular
        suite.addTest(new JUnit4TestAdapter(ALOGPDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(APolDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(AromaticAtomsCountDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(AromaticBondsCountDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(AtomCountDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(BCUTDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(BondCountDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(BPolDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(ChiChainDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(ChiPathDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(ChiClusterDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(ChiPathClusterDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(CPSADescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(EccentricConnectivityIndexDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(GravitationalIndexDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(HBondAcceptorCountDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(HBondDonorCountDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(KappaShapeIndicesDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(KierHallSmartsDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(LargestChainDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(LargestPiSystemDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(LengthOverBreadthDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(LongestAliphaticChainDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(MDEDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(MomentOfInertiaDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(PetitjeanNumberDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(PetitjeanShapeIndexDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(RotatableBondsCountDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(RuleOfFiveDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(TPSADescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(VAdjMaDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(WeightDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(WeightedPathDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(WHIMDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(WienerNumbersDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(XLogPDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(ZagrebIndexDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(AutocorrelationDescriptorChargeTest.class));
        suite.addTest(new JUnit4TestAdapter(AutocorrelationDescriptorMassTest.class));
        suite.addTest(new JUnit4TestAdapter(AutocorrelationDescriptorPolarizabilityTest.class));
        suite.addTest(new JUnit4TestAdapter(CarbonTypesDescriptorTest.class));

        return suite;
    }

}
