package org.openscience.cdk.modulesuites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.openscience.cdk.coverage.PcoreCoverageTest;
import org.openscience.cdk.pharmacophore.*;

/**
 * TestSuite that runs all the sample tests for pharmacophore classes.
 *
 * @cdk.module test-pcore
 */
@RunWith(value=Suite.class)
@SuiteClasses(value = {
        PcoreCoverageTest.class,
        PharmacophoreMatcherTest.class,
        PharmacophoreUtilityTest.class,
        PharmacophoreQueryTest.class,
        PharmacophoreQueryBondTest.class,
        PharmacophoreQueryAtomTest.class,
        PharmacophoreQueryAngleBondTest.class,
        PharmacophoreBondTest.class,
        PharmacophoreAngleBondTest.class,
        PharmacophoreAtomTest.class
})
public class MpcoreTests {}
