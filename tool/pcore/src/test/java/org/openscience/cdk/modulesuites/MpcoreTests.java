package org.openscience.cdk.modulesuites;

import org.openscience.cdk.coverage.PcoreCoverageTest;
import org.openscience.cdk.pharmacophore.PharmacophoreAngleBondTest;
import org.openscience.cdk.pharmacophore.PharmacophoreAtomTest;
import org.openscience.cdk.pharmacophore.PharmacophoreBondTest;
import org.openscience.cdk.pharmacophore.PharmacophoreMatcherTest;
import org.openscience.cdk.pharmacophore.PharmacophoreQueryAngleBondTest;
import org.openscience.cdk.pharmacophore.PharmacophoreQueryAtomTest;
import org.openscience.cdk.pharmacophore.PharmacophoreQueryBondTest;
import org.openscience.cdk.pharmacophore.PharmacophoreQueryTest;
import org.openscience.cdk.pharmacophore.PharmacophoreUtilityTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * TestSuite that runs all the sample tests for pharmacophore classes.
 *
 * @cdk.module test-pcore
 */
@RunWith(value = Suite.class)
@SuiteClasses(value = {PcoreCoverageTest.class, PharmacophoreMatcherTest.class, PharmacophoreUtilityTest.class,
        PharmacophoreQueryTest.class, PharmacophoreQueryBondTest.class, PharmacophoreQueryAtomTest.class,
        PharmacophoreQueryAngleBondTest.class, PharmacophoreBondTest.class, PharmacophoreAngleBondTest.class,
        PharmacophoreAtomTest.class})
public class MpcoreTests {
}
