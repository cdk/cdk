package org.openscience.cdk.modulesuites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.openscience.cdk.coverage.PcoreCoverageTest;
import org.openscience.cdk.pharmacophore.PharmacophoreMatcherTest;
import org.openscience.cdk.pharmacophore.PharmacophoreUtilityTest;

/**
 * TestSuite that runs all the sample tests for pharmacophore classes.
 *
 * @cdk.module test-pcore
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={
    PcoreCoverageTest.class,
    PharmacophoreMatcherTest.class,
    PharmacophoreUtilityTest.class
})
public class MpcoreTests {}
