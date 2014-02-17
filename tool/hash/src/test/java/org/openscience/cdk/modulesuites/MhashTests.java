/* Copyright (C) 2013  John May <jwmay@users.sf.net>
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
import org.openscience.cdk.coverage.HashCoverageTest;
import org.openscience.cdk.hash.AbstractHashGeneratorTest;
import org.openscience.cdk.hash.BasicAtomHashGeneratorTest;
import org.openscience.cdk.hash.BasicMoleculeHashGeneratorTest;
import org.openscience.cdk.hash.HashCodeScenariosTest;
import org.openscience.cdk.hash.HashGeneratorMakerTest;
import org.openscience.cdk.hash.PerturbedAtomHashGeneratorTest;
import org.openscience.cdk.hash.SuppressedAtomHashGeneratorTest;
import org.openscience.cdk.hash.XorshiftTest;
import org.openscience.cdk.hash.MinimumEquivalentCyclicSetTest;
import org.openscience.cdk.hash.BasicAtomEncoderTest;
import org.openscience.cdk.hash.ConjugatedAtomEncoderTest;
import org.openscience.cdk.hash.stereo.BasicPermutationParityTest;
import org.openscience.cdk.hash.stereo.GeometryEncoderTest;
import org.openscience.cdk.hash.stereo.MultiStereoEncoderTest;
import org.openscience.cdk.hash.stereo.GeometricCumulativeDoubleBondFactoryTest;
import org.openscience.cdk.hash.stereo.GeometricDoubleBondEncoderFactoryTest;
import org.openscience.cdk.hash.stereo.GeometricTetrahedralEncoderFactoryTest;
import org.openscience.cdk.hash.stereo.CombinedPermutationParityTest;
import org.openscience.cdk.hash.stereo.DoubleBond2DParityTest;
import org.openscience.cdk.hash.stereo.DoubleBond3DParityTest;
import org.openscience.cdk.hash.stereo.Tetrahedral2DParityTest;
import org.openscience.cdk.hash.stereo.Tetrahedral3DParityTest;

/**
 * TestSuite for the CDK <code>hash</code> module.
 *
 * @cdk.module test-hash
 */
@RunWith(value = Suite.class)
@SuiteClasses(value = {
        HashCoverageTest.class,
        XorshiftTest.class,
        BasicAtomEncoderTest.class,
        ConjugatedAtomEncoderTest.class,
        AbstractHashGeneratorTest.class,
        BasicAtomHashGeneratorTest.class,
        SuppressedAtomHashGeneratorTest.class,
        BasicMoleculeHashGeneratorTest.class,
        HashGeneratorMakerTest.class,
        BasicPermutationParityTest.class,
        CombinedPermutationParityTest.class,
        Tetrahedral2DParityTest.class,
        Tetrahedral3DParityTest.class,
        DoubleBond2DParityTest.class,
        DoubleBond3DParityTest.class,
        GeometryEncoderTest.class,
        MultiStereoEncoderTest.class,
        GeometricTetrahedralEncoderFactoryTest.class,
        GeometricDoubleBondEncoderFactoryTest.class,
        GeometricCumulativeDoubleBondFactoryTest.class,
        PerturbedAtomHashGeneratorTest.class,
        MinimumEquivalentCyclicSetTest.class,
        HashCodeScenariosTest.class
})
public class MhashTests {
}
