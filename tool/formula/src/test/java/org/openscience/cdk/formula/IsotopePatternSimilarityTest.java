package org.openscience.cdk.formula;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 * Class testing the IsotopePatternSimilarity class.
 *
 * @cdk.module test-formula
 */
class IsotopePatternSimilarityTest extends CDKTestCase {

    private final static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    IsotopePatternSimilarityTest() {
        super();
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testIsotopePatternSimilarity() {
        IsotopePatternSimilarity is = new IsotopePatternSimilarity();
        Assertions.assertNotNull(is);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testSeTolerance_double() {
        IsotopePatternSimilarity is = new IsotopePatternSimilarity();
        is.seTolerance(0.001);
        Assertions.assertNotNull(is);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testGetTolerance() {
        IsotopePatternSimilarity is = new IsotopePatternSimilarity();
        is.seTolerance(0.001);
        Assertions.assertEquals(0.001, is.getTolerance(), 0.000001);
    }

    /**
     * Histidine example
     *
     * @throws Exception
     */
    @Test
    void testCompare_IsotopePattern_IsotopePattern() {
        IsotopePatternSimilarity is = new IsotopePatternSimilarity();

        IsotopePattern spExp = new IsotopePattern();
        spExp.setMonoIsotope(new IsotopeContainer(156.07770, 1));
        spExp.addIsotope(new IsotopeContainer(157.07503, 0.0004));
        spExp.addIsotope(new IsotopeContainer(157.08059, 0.0003));
        spExp.addIsotope(new IsotopeContainer(158.08135, 0.002));

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C6H10N3O2", builder);
        IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator(0.1);
        IsotopePattern patternIsoPredicted = isotopeGe.getIsotopes(formula);
        IsotopePattern patternIsoNormalize = IsotopePatternManipulator.normalize(patternIsoPredicted);
        double score = is.compare(spExp, patternIsoNormalize);
        Assertions.assertNotSame(0.0, score);
    }

    /**
     * Histidine example
     *
     * @throws Exception
     */
    @Test
    void testSelectingMF() {
        IsotopePatternSimilarity is = new IsotopePatternSimilarity();

        IsotopePattern spExp = new IsotopePattern();
        spExp.setCharge(1);
        spExp.setMonoIsotope(new IsotopeContainer(156.07770, 1));
        spExp.addIsotope(new IsotopeContainer(157.07503, 0.0101));
        spExp.addIsotope(new IsotopeContainer(157.08059, 0.074));
        spExp.addIsotope(new IsotopeContainer(158.08135, 0.0024));

        double score = 0;
        String mfString = "";
        String[] listMF = {"C4H8N6O", "C2H12N4O4", "C3H12N2O5", "C6H10N3O2", "CH10N5O4", "C4H14NO5"};

        for (String s : listMF) {
            IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula(s, builder);
            IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator(0.01);
            IsotopePattern patternIsoPredicted = isotopeGe.getIsotopes(formula);

            IsotopePattern patternIsoNormalize = IsotopePatternManipulator.normalize(patternIsoPredicted);
            double tempScore = is.compare(spExp, patternIsoNormalize);
            if (score < tempScore) {
                mfString = MolecularFormulaManipulator.getString(formula);
                score = tempScore;
            }
        }
        Assertions.assertEquals("C6H10N3O2", mfString);
    }

    /**
     * Real example. Lipid PC
     *
     * @throws Exception
     */
    @Test
    void testExperiment() {

        IsotopePattern spExp = new IsotopePattern();
        spExp.setMonoIsotope(new IsotopeContainer(762.6006, 124118304));
        spExp.addIsotope(new IsotopeContainer(763.6033, 57558840));
        spExp.addIsotope(new IsotopeContainer(764.6064, 15432262));
        spExp.setCharge(1.0);

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C42H85NO8P",
                SilentChemObjectBuilder.getInstance());

        IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator(0.01);
        IsotopePattern patternIsoPredicted = isotopeGe.getIsotopes(formula);

        IsotopePatternSimilarity is = new IsotopePatternSimilarity();
        double score = is.compare(spExp, patternIsoPredicted);

        Assertions.assertEquals(0.97, score, .01);
    }

}
