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
import org.openscience.cdk.coverage.QsaratomicCoverageTest;
import org.openscience.cdk.qsar.descriptors.atomic.*;

/**
 * TestSuite that runs all the sample tests.
 *
 * @cdk.module test-qsaratomic
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
public class MqsaratomicTests {

    public static Test suite() {                                       

        TestSuite suite = new TestSuite("All QSAR Tests");

        suite.addTest(new JUnit4TestAdapter(QsaratomicCoverageTest.class));

//      from cdk.test.qsar.atomic
        suite.addTest(new JUnit4TestAdapter(AtomDegreeDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(AtomHybridizationDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(AtomHybridizationVSEPRDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(AtomValenceDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(BondsToAtomDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(CovalentRadiusDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(DistanceToAtomDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(EffectiveAtomPolarizabilityDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(InductiveAtomicHardnessDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(InductiveAtomicSoftnessDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(IsProtonInAromaticSystemDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(IsProtonInConjugatedPiSystemDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(PartialPiChargeDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(PartialSigmaChargeDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(PartialTChargeMMFF94DescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(PartialTChargePEOEDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(PeriodicTablePositionDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(PiElectronegativityDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(ProtonTotalPartialChargeDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(RDFProtonDescriptor_G3RTest.class));
        suite.addTest(new JUnit4TestAdapter(RDFProtonDescriptor_GDRTest.class));
        suite.addTest(new JUnit4TestAdapter(RDFProtonDescriptor_GHRTest.class));
        suite.addTest(new JUnit4TestAdapter(RDFProtonDescriptor_GHR_topolTest.class));
        suite.addTest(new JUnit4TestAdapter(RDFProtonDescriptor_GSRTest.class));
        suite.addTest(new JUnit4TestAdapter(SigmaElectronegativityDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(StabilizationPlusChargeDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(VdWRadiusDescriptorTest.class));
        suite.addTest(new JUnit4TestAdapter(IPAtomicHOSEDescriptorTest.class));
        
        return suite;
    }

}
