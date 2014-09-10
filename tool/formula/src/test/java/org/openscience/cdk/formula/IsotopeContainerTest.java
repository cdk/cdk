package org.openscience.cdk.formula;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * Class testing the IsotopeContainer class.
 *
 * @cdk.module test-formula
 */
public class IsotopeContainerTest extends CDKTestCase {

    private static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    /**
     *  Constructor for the IsotopeContainerTest object.
     *
     */
    public IsotopeContainerTest() {
        super();
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testIsotopeContainer() {
        IsotopeContainer isoC = new IsotopeContainer();
        Assert.assertNotNull(isoC);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testIsotopeContainer_IMolecularFormula_double() {
        IMolecularFormula formula = builder.newInstance(IMolecularFormula.class);
        double intensity = 130.00;
        IsotopeContainer isoC = new IsotopeContainer(formula, intensity);

        Assert.assertNotNull(isoC);
        Assert.assertEquals(formula, isoC.getFormula());
        Assert.assertEquals(intensity, isoC.getIntensity(), 0.001);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testIsotopeContainer_double_double() {
        double mass = 130.00;
        double intensity = 500000.00;
        IsotopeContainer isoC = new IsotopeContainer(mass, intensity);

        Assert.assertNotNull(isoC);
        Assert.assertEquals(mass, isoC.getMass(), 0.001);
        Assert.assertEquals(intensity, isoC.getIntensity(), 0.001);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testSetFormula_IMolecularFormula() {
        IsotopeContainer isoC = new IsotopeContainer();
        IMolecularFormula formula = builder.newInstance(IMolecularFormula.class);
        isoC.setFormula(formula);
        Assert.assertNotNull(isoC);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testSetMass_double() {
        IsotopeContainer isoC = new IsotopeContainer();
        isoC.setMass(130.00);
        Assert.assertNotNull(isoC);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testSetIntensity_double() {
        IsotopeContainer isoC = new IsotopeContainer();
        isoC.setIntensity(5000000.0);
        Assert.assertNotNull(isoC);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetFormula() {
        IsotopeContainer isoC = new IsotopeContainer();
        IMolecularFormula formula = builder.newInstance(IMolecularFormula.class);
        isoC.setFormula(formula);
        Assert.assertEquals(formula, isoC.getFormula());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetMass() {
        IsotopeContainer isoC = new IsotopeContainer();
        double mass = 130.00;
        isoC.setMass(mass);
        Assert.assertEquals(mass, isoC.getMass(), 0.001);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetIntensity() {
        IsotopeContainer isoC = new IsotopeContainer();
        double intensity = 130.00;
        isoC.setIntensity(intensity);
        Assert.assertEquals(intensity, isoC.getIntensity(), 0.001);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testClone() throws Exception {
        IsotopeContainer isoC = new IsotopeContainer();
        IMolecularFormula formula = builder.newInstance(IMolecularFormula.class);
        isoC.setFormula(formula);
        double mass = 130.00;
        isoC.setMass(mass);
        double intensity = 130.00;
        isoC.setIntensity(intensity);

        IsotopeContainer clone = (IsotopeContainer) isoC.clone();
        Assert.assertEquals(mass, clone.getMass(), 0.001);
        Assert.assertEquals(intensity, clone.getIntensity(), 0.001);
        Assert.assertEquals(formula, clone.getFormula());

    }

}
