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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.openscience.cdk.coverage.QsaratomicCoverageTest;
import org.openscience.cdk.qsar.descriptors.atomic.AtomDegreeDescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.AtomHybridizationDescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.AtomHybridizationVSEPRDescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.AtomValenceDescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.BondsToAtomDescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.CovalentRadiusDescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.DistanceToAtomDescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.EffectiveAtomPolarizabilityDescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.IPAtomicHOSEDescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.InductiveAtomicHardnessDescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.InductiveAtomicSoftnessDescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.IsProtonInAromaticSystemDescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.IsProtonInConjugatedPiSystemDescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.PartialPiChargeDescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.PartialSigmaChargeDescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.PartialTChargeMMFF94DescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.PartialTChargePEOEDescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.PeriodicTablePositionDescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.PiElectronegativityDescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.ProtonTotalPartialChargeDescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.RDFProtonDescriptor_G3RTest;
import org.openscience.cdk.qsar.descriptors.atomic.RDFProtonDescriptor_GDRTest;
import org.openscience.cdk.qsar.descriptors.atomic.RDFProtonDescriptor_GHRTest;
import org.openscience.cdk.qsar.descriptors.atomic.RDFProtonDescriptor_GHR_topolTest;
import org.openscience.cdk.qsar.descriptors.atomic.RDFProtonDescriptor_GSRTest;
import org.openscience.cdk.qsar.descriptors.atomic.SigmaElectronegativityDescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.StabilizationPlusChargeDescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.VdWRadiusDescriptorTest;

/**
 * TestSuite that runs all the sample tests.
 *
 * @cdk.module test-qsaratomic
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={
    QsaratomicCoverageTest.class,
    AtomDegreeDescriptorTest.class,
    AtomHybridizationDescriptorTest.class,
    AtomHybridizationVSEPRDescriptorTest.class,
    AtomValenceDescriptorTest.class,
    BondsToAtomDescriptorTest.class,
    CovalentRadiusDescriptorTest.class,
    DistanceToAtomDescriptorTest.class,
    EffectiveAtomPolarizabilityDescriptorTest.class,
    InductiveAtomicHardnessDescriptorTest.class,
    InductiveAtomicSoftnessDescriptorTest.class,
    IsProtonInAromaticSystemDescriptorTest.class,
    IsProtonInConjugatedPiSystemDescriptorTest.class,
    PartialPiChargeDescriptorTest.class,
    PartialSigmaChargeDescriptorTest.class,
    PartialTChargePEOEDescriptorTest.class,
    PeriodicTablePositionDescriptorTest.class,
    PiElectronegativityDescriptorTest.class,
    ProtonTotalPartialChargeDescriptorTest.class,
    RDFProtonDescriptor_G3RTest.class,
    RDFProtonDescriptor_GDRTest.class,
    RDFProtonDescriptor_GHRTest.class,
    RDFProtonDescriptor_GHR_topolTest.class,
    RDFProtonDescriptor_GSRTest.class,
    SigmaElectronegativityDescriptorTest.class,
    StabilizationPlusChargeDescriptorTest.class,
    VdWRadiusDescriptorTest.class,
    IPAtomicHOSEDescriptorTest.class,
    PartialTChargeMMFF94DescriptorTest.class
})
public class MqsaratomicTests {}
