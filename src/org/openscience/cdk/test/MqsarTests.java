/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-04-21 10:59:31 +0200 (Fr, 21 Apr 2006) $
 * $Revision: 6067 $
 *
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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

package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.qsar.DescriptorEngineTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.AtomCountDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.AtomDegreeDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.AtomHybridizationDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.AtomValenceDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.BondsToAtomDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.CovalentRadiusDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.DistanceToAtomDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.EffectiveAtomPolarizabilityDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.InductiveAtomicHardnessDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.InductiveAtomicSoftnessDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.IsProtonInAromaticSystemDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.IsProtonInConjugatedPiSystemDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.PartialPiChargeDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.PartialSigmaChargeDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.PartialTChargeMMFF94DescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.PartialTChargePEOEDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.PeriodicTablePositionDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.PiContactDetectionDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.ProtonTotalPartialChargeDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.RDFProtonDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.SigmaElectronegativityDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.VdWRadiusDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.WeightDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.bond.BondCountDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.bond.BondPartialPiChargeDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.bond.BondPartialSigmaChargeDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.bond.BondSigmaElectronegativityDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.APolDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.AromaticAtomsCountDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.AromaticBondsCountDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.BCUTDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.BPolDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.CPSADescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.CarbonConnectivityOrderOneDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.CarbonConnectivityOrderZeroDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.ConnectivityOrderOneDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.ConnectivityOrderZeroDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.EccentricConnectivityIndexDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.GravitationalIndexDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.HBondAcceptorCountDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.HBondDonorCountDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.KappaShapeIndicesDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.MomentOfInertiaDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.PetitjeanNumberDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.PetitjeanShapeIndexDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.RotatableBondsCountDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.RuleOfFiveDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.TPSADescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.ValenceCarbonConnectivityOrderOneDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.ValenceCarbonConnectivityOrderZeroDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.ValenceConnectivityOrderOneDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.ValenceConnectivityOrderZeroDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.WHIMDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.WeightedPathDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.WienerNumbersDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.XLogPDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.ZagrebIndexDescriptorTest;
import org.openscience.cdk.test.qsar.model.QSARRModelTests;

/**
 * TestSuite that runs all the sample tests.
 *
 * @cdk.module  test-qsar
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
public class MqsarTests {
    
    public static Test suite() {
    	
        TestSuite suite= new TestSuite("All QSAR Tests");

        // Individual Tests
        
        // from cdk.test.qsar
        suite.addTest(AtomCountDescriptorTest.suite());
        suite.addTest(BondCountDescriptorTest.suite());
        suite.addTest(RotatableBondsCountDescriptorTest.suite());
        suite.addTest(IsProtonInAromaticSystemDescriptorTest.suite());
        suite.addTest(BondSigmaElectronegativityDescriptorTest.suite());
        suite.addTest(BondPartialPiChargeDescriptorTest.suite());
        suite.addTest(BondPartialSigmaChargeDescriptorTest.suite());
        suite.addTest(SigmaElectronegativityDescriptorTest.suite());
        suite.addTest(AromaticAtomsCountDescriptorTest.suite());
        suite.addTest(AromaticBondsCountDescriptorTest.suite());
        suite.addTest(IsProtonInConjugatedPiSystemDescriptorTest.suite());
        suite.addTest(ProtonTotalPartialChargeDescriptorTest.suite());
        suite.addTest(EffectiveAtomPolarizabilityDescriptorTest.suite());
        suite.addTest(HBondAcceptorCountDescriptorTest.suite());
        suite.addTest(HBondDonorCountDescriptorTest.suite());
        suite.addTest(ValenceConnectivityOrderZeroDescriptorTest.suite());
        suite.addTest(ValenceCarbonConnectivityOrderZeroDescriptorTest.suite());
        suite.addTest(ValenceConnectivityOrderOneDescriptorTest.suite());
        suite.addTest(ValenceCarbonConnectivityOrderOneDescriptorTest.suite());
        suite.addTest(ConnectivityOrderZeroDescriptorTest.suite());
        suite.addTest(CarbonConnectivityOrderZeroDescriptorTest.suite());
        suite.addTest(ConnectivityOrderOneDescriptorTest.suite());
        suite.addTest(CarbonConnectivityOrderOneDescriptorTest.suite());
        suite.addTest(ZagrebIndexDescriptorTest.suite());
        suite.addTest(GravitationalIndexDescriptorTest.suite());
        suite.addTest(BCUTDescriptorTest.suite());
        suite.addTest(WHIMDescriptorTest.suite());
        suite.addTest(KappaShapeIndicesDescriptorTest.suite());
        suite.addTest(WienerNumbersDescriptorTest.suite());
        suite.addTest(PetitjeanNumberDescriptorTest.suite());
        suite.addTest(APolDescriptorTest.suite());
        suite.addTest(BPolDescriptorTest.suite());
        suite.addTest(TPSADescriptorTest.suite());
        suite.addTest(XLogPDescriptorTest.suite());
        suite.addTest(DescriptorEngineTest.suite());
        suite.addTest(RuleOfFiveDescriptorTest.suite());
        suite.addTest(RDFProtonDescriptorTest.suite());
        suite.addTest(MomentOfInertiaDescriptorTest.suite());
        suite.addTest(CovalentRadiusDescriptorTest.suite());
        suite.addTest(VdWRadiusDescriptorTest.suite());
        suite.addTest(BondsToAtomDescriptorTest.suite());
        suite.addTest(DistanceToAtomDescriptorTest.suite());
        suite.addTest(AtomDegreeDescriptorTest.suite());
        suite.addTest(AtomValenceDescriptorTest.suite());
        suite.addTest(PiContactDetectionDescriptorTest.suite());
        suite.addTest(PeriodicTablePositionDescriptorTest.suite());
        suite.addTest(AtomHybridizationDescriptorTest.suite());
        suite.addTest(EccentricConnectivityIndexDescriptorTest.suite());
        suite.addTest(WeightDescriptorTest.suite());
        suite.addTest(InductiveAtomicHardnessDescriptorTest.suite());
        suite.addTest(InductiveAtomicSoftnessDescriptorTest.suite());
        suite.addTest(CPSADescriptorTest.suite());
        suite.addTest(WeightedPathDescriptorTest.suite());
        suite.addTest(PetitjeanShapeIndexDescriptorTest.suite());
        suite.addTest(PartialPiChargeDescriptorTest.suite());
        suite.addTest(PartialSigmaChargeDescriptorTest.suite());
        suite.addTest(PartialTChargeMMFF94DescriptorTest.suite());
        suite.addTest(PartialTChargePEOEDescriptorTest.suite());

        // from cdk.test.qsar.model
        suite.addTest(QSARRModelTests.suite());
        
        
        return suite;
    }
    
}
