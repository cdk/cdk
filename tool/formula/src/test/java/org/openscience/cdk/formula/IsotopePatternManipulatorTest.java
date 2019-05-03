package org.openscience.cdk.formula;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;

/**
 * Class testing the IsotopePatternManipulator class.
 *
 * @cdk.module test-formula
 */
public class IsotopePatternManipulatorTest extends CDKTestCase {

    public IsotopePatternManipulatorTest() {
        super();
    }

    /**
     * Junit test
     *
     * @throws Exception
     */
    @Test
    public void testNormalize_IsotopePattern() {
        IsotopePattern spExp = new IsotopePattern();
        spExp.setMonoIsotope(new IsotopeContainer(156.07770, 2));
        spExp.addIsotope(new IsotopeContainer(157.08059, 0.0006));
        spExp.addIsotope(new IsotopeContainer(157.07503, 0.0002));
        spExp.addIsotope(new IsotopeContainer(158.08135, 0.004));
        spExp.setCharge(1);

        IsotopePattern isoNorma = IsotopePatternManipulator.normalize(spExp);
        List<IsotopeContainer> listISO = isoNorma.getIsotopes();
        Assert.assertEquals(1, isoNorma.getMonoIsotope().getIntensity(), 0.00001);
        Assert.assertEquals(1, listISO.get(0).getIntensity(), 0.00001);
        Assert.assertEquals(0.0003, listISO.get(1).getIntensity(), 0.00001);
        Assert.assertEquals(0.0001, listISO.get(2).getIntensity(), 0.00001);
        Assert.assertEquals(0.002, listISO.get(3).getIntensity(), 0.00001);

        Assert.assertEquals(156.07770, isoNorma.getMonoIsotope().getMass(), 0.00001);
        Assert.assertEquals(156.07770, listISO.get(0).getMass(), 0.00001);
        Assert.assertEquals(157.08059, listISO.get(1).getMass(), 0.00001);
        Assert.assertEquals(157.07503, listISO.get(2).getMass(), 0.00001);
        Assert.assertEquals(158.08135, listISO.get(3).getMass(), 0.00001);

        Assert.assertEquals(1, isoNorma.getCharge(), 0.00001);

    }

    /**
     * Junit test
     *
     * @throws Exception
     */
    @Test
    public void testSortByIntensity_IsotopePattern() {
        IsotopePattern spExp = new IsotopePattern();
        spExp.setMonoIsotope(new IsotopeContainer(157.07503, 0.0001));
        spExp.addIsotope(new IsotopeContainer(156.07770, 1));
        spExp.addIsotope(new IsotopeContainer(157.08059, 0.0003));
        spExp.addIsotope(new IsotopeContainer(158.08135, 0.002));
        spExp.setCharge(1);

        IsotopePattern isoNorma = IsotopePatternManipulator.sortByIntensity(spExp);
        List<IsotopeContainer> listISO = isoNorma.getIsotopes();
        Assert.assertEquals(156.07770, isoNorma.getMonoIsotope().getMass(), 0.00001);
        Assert.assertEquals(156.07770, listISO.get(0).getMass(), 0.00001);
        Assert.assertEquals(158.08135, listISO.get(1).getMass(), 0.00001);
        Assert.assertEquals(157.08059, listISO.get(2).getMass(), 0.00001);
        Assert.assertEquals(157.07503, listISO.get(3).getMass(), 0.00001);

        Assert.assertEquals(1, isoNorma.getMonoIsotope().getIntensity(), 0.00001);
        Assert.assertEquals(1, listISO.get(0).getIntensity(), 0.00001);
        Assert.assertEquals(0.002, listISO.get(1).getIntensity(), 0.00001);
        Assert.assertEquals(0.0003, listISO.get(2).getIntensity(), 0.001);
        Assert.assertEquals(0.0001, listISO.get(3).getIntensity(), 0.00001);

        Assert.assertEquals(1, isoNorma.getCharge(), 0.00001);
    }

    /**
     * Junit test
     *
     * @throws Exception
     */
    @Test
    public void testSortAndNormalizedByIntensity_IsotopePattern() {
        IsotopePattern spExp = new IsotopePattern();
        spExp.addIsotope(new IsotopeContainer(157.07503, 0.0002));
        spExp.setMonoIsotope(new IsotopeContainer(156.07770, 2));
        spExp.addIsotope(new IsotopeContainer(158.08135, 0.004));
        spExp.addIsotope(new IsotopeContainer(157.08059, 0.0006));
        spExp.setCharge(1);

        IsotopePattern isoNorma = IsotopePatternManipulator.sortAndNormalizedByIntensity(spExp);
        List<IsotopeContainer> listISO = isoNorma.getIsotopes();
        Assert.assertEquals(156.07770, isoNorma.getMonoIsotope().getMass(), 0.00001);
        Assert.assertEquals(156.07770, listISO.get(0).getMass(), 0.00001);
        Assert.assertEquals(158.08135, listISO.get(1).getMass(), 0.00001);
        Assert.assertEquals(157.08059, listISO.get(2).getMass(), 0.00001);
        Assert.assertEquals(157.07503, listISO.get(3).getMass(), 0.00001);

        Assert.assertEquals(1, isoNorma.getMonoIsotope().getIntensity(), 0.00001);
        Assert.assertEquals(1, listISO.get(0).getIntensity(), 0.00001);
        Assert.assertEquals(0.002, listISO.get(1).getIntensity(), 0.00001);
        Assert.assertEquals(0.0003, listISO.get(2).getIntensity(), 0.00001);
        Assert.assertEquals(0.0001, listISO.get(3).getIntensity(), 0.00001);

        Assert.assertEquals(1, isoNorma.getCharge(), 0.001);
    }

    /**
     * Junit test
     *
     * @throws Exception
     */
    @Test
    public void testSortByMass_IsotopePattern() {
        IsotopePattern spExp = new IsotopePattern();
        spExp.addIsotope(new IsotopeContainer(157.07503, 0.0002));
        spExp.setMonoIsotope(new IsotopeContainer(156.07770, 2));
        spExp.addIsotope(new IsotopeContainer(158.08135, 0.004));
        spExp.addIsotope(new IsotopeContainer(157.08059, 0.0006));
        spExp.setCharge(1);

        IsotopePattern isoNorma = IsotopePatternManipulator.sortByMass(spExp);
        List<IsotopeContainer> listISO = isoNorma.getIsotopes();
        Assert.assertEquals(156.07770, isoNorma.getMonoIsotope().getMass(), 0.001);
        Assert.assertEquals(156.07770, listISO.get(0).getMass(), 0.00001);
        Assert.assertEquals(157.07503, listISO.get(1).getMass(), 0.00001);
        Assert.assertEquals(157.08059, listISO.get(2).getMass(), 0.00001);
        Assert.assertEquals(158.08135, listISO.get(3).getMass(), 0.00001);

        Assert.assertEquals(2, isoNorma.getMonoIsotope().getIntensity(), 0.001);
        Assert.assertEquals(2, listISO.get(0).getIntensity(), 0.001);
        Assert.assertEquals(0.0002, listISO.get(1).getIntensity(), 0.00001);
        Assert.assertEquals(0.0006, listISO.get(2).getIntensity(), 0.00001);
        Assert.assertEquals(0.004, listISO.get(3).getIntensity(), 0.00001);

        Assert.assertEquals(1, isoNorma.getCharge(), 0.001);
    }
}
