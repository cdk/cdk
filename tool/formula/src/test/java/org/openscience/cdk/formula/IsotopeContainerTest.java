package org.openscience.cdk.formula;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * Class testing the IsotopeContainer class.
 *
 * @cdk.module test-formula
 */
public class IsotopeContainerTest extends CDKTestCase {

    private static final IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

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
     *
     */
    @Test
    public void testIsotopeContainer() {
        IsotopeContainer isoC = new IsotopeContainer();
        Assertions.assertNotNull(isoC);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testIsotopeContainer_IMolecularFormula_double() {
        IMolecularFormula formula = builder.newInstance(IMolecularFormula.class);
        double intensity = 130.00;
        IsotopeContainer isoC = new IsotopeContainer(formula, intensity);

        Assertions.assertNotNull(isoC);
        Assertions.assertEquals(formula, isoC.getFormula());
        Assertions.assertEquals(intensity, isoC.getIntensity(), 0.001);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testIsotopeContainer_double_double() {
        double mass = 130.00;
        double intensity = 500000.00;
        IsotopeContainer isoC = new IsotopeContainer(mass, intensity);

        Assertions.assertNotNull(isoC);
        Assertions.assertEquals(mass, isoC.getMass(), 0.001);
        Assertions.assertEquals(intensity, isoC.getIntensity(), 0.001);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testSetFormula_IMolecularFormula() {
        IsotopeContainer isoC = new IsotopeContainer();
        IMolecularFormula formula = builder.newInstance(IMolecularFormula.class);
        isoC.setFormula(formula);
        Assertions.assertNotNull(isoC);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testSetMass_double() {
        IsotopeContainer isoC = new IsotopeContainer();
        isoC.setMass(130.00);
        Assertions.assertNotNull(isoC);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testSetIntensity_double() {
        IsotopeContainer isoC = new IsotopeContainer();
        isoC.setIntensity(5000000.0);
        Assertions.assertNotNull(isoC);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testGetFormula() {
        IsotopeContainer isoC = new IsotopeContainer();
        IMolecularFormula formula = builder.newInstance(IMolecularFormula.class);
        isoC.setFormula(formula);
        Assertions.assertEquals(formula, isoC.getFormula());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testGetMass() {
        IsotopeContainer isoC = new IsotopeContainer();
        double mass = 130.00;
        isoC.setMass(mass);
        Assertions.assertEquals(mass, isoC.getMass(), 0.001);

    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testGetIntensity() {
        IsotopeContainer isoC = new IsotopeContainer();
        double intensity = 130.00;
        isoC.setIntensity(intensity);
        Assertions.assertEquals(intensity, isoC.getIntensity(), 0.001);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
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
        Assertions.assertEquals(mass, clone.getMass(), 0.001);
        Assertions.assertEquals(intensity, clone.getIntensity(), 0.001);
        Assertions.assertEquals(formula, clone.getFormula());

    }

}
