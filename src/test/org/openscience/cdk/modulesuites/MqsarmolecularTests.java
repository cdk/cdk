/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *                    2010  Egon Willighagen <egonw@users.sf.net>
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
import org.openscience.cdk.coverage.QsarmolecularCoverageTest;
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
@RunWith(value=Suite.class)
@SuiteClasses(value={
    QsarmolecularCoverageTest.class,
        
        // Individual Tests - Please add correlatively  
    ChiIndexUtilsTest.class,
    DescriptorEngineTest.class,
    DescriptorNamesTest.class,

//      from cdk.test.qsar.molecular
    AcidicGroupCountDescriptorTest.class,
    ALOGPDescriptorTest.class,
    APolDescriptorTest.class,
    AromaticAtomsCountDescriptorTest.class,
    AromaticBondsCountDescriptorTest.class,
    AtomCountDescriptorTest.class,
    BasicGroupCountDescriptorTest.class,
    BCUTDescriptorTest.class,
    BondCountDescriptorTest.class,
    BPolDescriptorTest.class,
    ChiChainDescriptorTest.class,
    ChiPathDescriptorTest.class,
    ChiClusterDescriptorTest.class,
    ChiPathClusterDescriptorTest.class,
    CPSADescriptorTest.class,
    EccentricConnectivityIndexDescriptorTest.class,
    GravitationalIndexDescriptorTest.class,
    HBondAcceptorCountDescriptorTest.class,
    HBondDonorCountDescriptorTest.class,
    KappaShapeIndicesDescriptorTest.class,
    KierHallSmartsDescriptorTest.class,
    LargestChainDescriptorTest.class,
    LargestPiSystemDescriptorTest.class,
    LengthOverBreadthDescriptorTest.class,
    LongestAliphaticChainDescriptorTest.class,
    MDEDescriptorTest.class,
    MomentOfInertiaDescriptorTest.class,
    PetitjeanNumberDescriptorTest.class,
    PetitjeanShapeIndexDescriptorTest.class,
    RotatableBondsCountDescriptorTest.class,
    RuleOfFiveDescriptorTest.class,
    TPSADescriptorTest.class,
    VAdjMaDescriptorTest.class,
    WeightDescriptorTest.class,
    WeightedPathDescriptorTest.class,
    WHIMDescriptorTest.class,
    WienerNumbersDescriptorTest.class,
    XLogPDescriptorTest.class,
    ZagrebIndexDescriptorTest.class,
    AutocorrelationDescriptorChargeTest.class,
    AutocorrelationDescriptorMassTest.class,
    AutocorrelationDescriptorPolarizabilityTest.class,
    CarbonTypesDescriptorTest.class,
    HybridizationRatioDescriptorTest.class,
    FMFDescriptorTest.class
})
public class MqsarmolecularTests {}
