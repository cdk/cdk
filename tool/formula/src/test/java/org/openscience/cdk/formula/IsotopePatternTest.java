package org.openscience.cdk.formula;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;

/**
 * Class testing the IsotopePattern class.
 *
 * @cdk.module test-formula
 */
public class IsotopePatternTest extends CDKTestCase {

    public IsotopePatternTest() {
        super();
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testIsotopePattern() {
        IsotopePattern isoP = new IsotopePattern();
        Assert.assertNotNull(isoP);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testSetMonoIsotope_IsotopeContainer() {
        IsotopePattern isoP = new IsotopePattern();
        isoP.setMonoIsotope(new IsotopeContainer());
        Assert.assertNotNull(isoP);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testAddIsotope_IsotopeContainer() {
        IsotopePattern isoP = new IsotopePattern();
        isoP.addIsotope(new IsotopeContainer());
        Assert.assertNotNull(isoP);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetMonoIsotope() {
        IsotopePattern isoP = new IsotopePattern();
        IsotopeContainer isoC = new IsotopeContainer();
        isoP.setMonoIsotope(isoC);
        Assert.assertEquals(isoC, isoP.getMonoIsotope());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetIsotopes() {

        IsotopePattern isoP = new IsotopePattern();
        IsotopeContainer iso1 = new IsotopeContainer();
        isoP.setMonoIsotope(iso1);
        IsotopeContainer iso2 = new IsotopeContainer();
        isoP.addIsotope(iso2);
        Assert.assertEquals(iso1, isoP.getIsotopes().get(0));
        Assert.assertEquals(iso2, isoP.getIsotopes().get(1));
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetIsotope_int() {

        IsotopePattern isoP = new IsotopePattern();
        IsotopeContainer iso1 = new IsotopeContainer();
        isoP.setMonoIsotope(iso1);
        IsotopeContainer iso2 = new IsotopeContainer();
        isoP.addIsotope(iso2);
        Assert.assertEquals(iso1, isoP.getIsotope(0));
        Assert.assertEquals(iso2, isoP.getIsotope(1));
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetNumberOfIsotopes() {
        IsotopePattern isoP = new IsotopePattern();
        IsotopeContainer iso1 = new IsotopeContainer();
        isoP.setMonoIsotope(iso1);
        IsotopeContainer iso2 = new IsotopeContainer();
        isoP.addIsotope(iso2);
        Assert.assertEquals(2, isoP.getNumberOfIsotopes());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testSetCharge_double() {
        IsotopePattern isoP = new IsotopePattern();
        isoP.setCharge(1.0);
        Assert.assertEquals(1.0, isoP.getCharge(), 0.000001);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetCharge() {
        IsotopePattern isoP = new IsotopePattern();
        Assert.assertEquals(0, isoP.getCharge(), 0.000001);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testClone() throws Exception {
        IsotopePattern spExp = new IsotopePattern();
        spExp.setMonoIsotope(new IsotopeContainer(156.07770, 1));
        spExp.addIsotope(new IsotopeContainer(157.07503, 0.0004));
        spExp.addIsotope(new IsotopeContainer(157.08059, 0.0003));
        spExp.addIsotope(new IsotopeContainer(158.08135, 0.002));
        spExp.setCharge(1);

        IsotopePattern clone = (IsotopePattern) spExp.clone();
        Assert.assertEquals(156.07770, clone.getMonoIsotope().getMass(), 0.001);
        Assert.assertEquals(156.07770, clone.getIsotopes().get(0).getMass(), 0.001);
        Assert.assertEquals(157.07503, clone.getIsotopes().get(1).getMass(), 0.001);
        Assert.assertEquals(157.08059, clone.getIsotopes().get(2).getMass(), 0.001);
        Assert.assertEquals(158.08135, clone.getIsotopes().get(3).getMass(), 0.001);

        Assert.assertEquals(1, clone.getMonoIsotope().getIntensity(), 0.001);
        Assert.assertEquals(1, clone.getIsotopes().get(0).getIntensity(), 0.001);
        Assert.assertEquals(0.0004, clone.getIsotopes().get(1).getIntensity(), 0.001);
        Assert.assertEquals(0.0003, clone.getIsotopes().get(2).getIntensity(), 0.001);
        Assert.assertEquals(0.002, clone.getIsotopes().get(3).getIntensity(), 0.001);

        Assert.assertEquals(1, clone.getCharge(), 0.001);

    }

}
