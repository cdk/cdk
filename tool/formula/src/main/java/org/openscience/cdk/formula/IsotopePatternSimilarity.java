package org.openscience.cdk.formula;

/**
 * This class gives a score hit of similarity between two different
 * isotope abundance pattern.
 *
 * @author Miguel Rojas Cherto
 *
 * @cdk.module  formula
 * @cdk.githash
 */
public class IsotopePatternSimilarity {

    private double        chargeToAdd;

    private double        tolerance_ppm = 1;

    private static double massE         = 0.0005485;

    /**
     *  Constructor for the IsotopePatternSimilarity object.
     */
    public IsotopePatternSimilarity() {}

    /**
     * Set the tolerance of the mass accuracy.
     *
     * @param tolerance  The tolerance value
     */
    public void seTolerance(double tolerance) {
        tolerance_ppm = tolerance;
    }

    /**
     * Get the tolerance of the mass accuracy.
     *
     * @return The tolerance value
     */
    public double getTolerance() {
        return tolerance_ppm;
    }

    /**
     * Compare the IMolecularFormula with a isotope
     * abundance pattern.
     *
     *
     * @param  isoto1  The Isotope pattern reference (predicted)
     * @param  isoto2  The Isotope pattern reference (detected)
     * @return         The hit score of similarity
     */
    public double compare(IsotopePattern isoto1, IsotopePattern isoto2) {

        IsotopePattern iso1 = IsotopePatternManipulator.sortAndNormalizedByIntensity(isoto1);
        IsotopePattern iso2 = IsotopePatternManipulator.sortAndNormalizedByIntensity(isoto2);

        /* charge to add */
        if (isoto1.getCharge() == 1)
            chargeToAdd = massE;
        else if (isoto1.getCharge() == -1)
            chargeToAdd = -massE;
        else
            chargeToAdd = 0;

        for (IsotopeContainer isoC : iso1.getIsotopes()) {
            double mass = isoC.getMass();
            isoC.setMass(mass + chargeToAdd);
        }

        double diffMass, diffAbun, factor, totalFactor = 0d;
        double score = 0d, tempScore;
        // Maximum number of isotopes to be compared according predicted isotope
        // pattern. It is assumed that this will have always more isotopeContainers
        int length = iso1.getNumberOfIsotopes();

        for (int i = 0; i < length; i++) {
            IsotopeContainer isoContainer = iso1.getIsotopes().get(i);
            factor = isoContainer.getIntensity();
            totalFactor += factor;

            // Search for the closest isotope in the second pattern (detected) to the
            // current isotope (predicted pattern)
            int closestDp = getClosestDataDiff(isoContainer, iso2);
            if (closestDp == -1) continue;

            diffMass = isoContainer.getMass() - iso2.getIsotopes().get(closestDp).getMass();
            diffMass = Math.abs(diffMass);

            diffAbun = 1.0d - (isoContainer.getIntensity() / iso2.getIsotopes().get(closestDp).getIntensity());
            diffAbun = Math.abs(diffAbun);

            tempScore = 1 - (diffMass + diffAbun);

            if (tempScore < 0) tempScore = 0;

            score += (tempScore * factor);

        }

        return score / totalFactor;
    }

    /**
     * Search and find the closest difference in an array in terms of mass and
     * intensity. Always return the position in this List.
     *
     * @param diffValue  The difference to look for
     * @param normMass   A List of normalized masses
     * @return           The position in the List
     */
    private int getClosestDataDiff(IsotopeContainer isoContainer, IsotopePattern pattern) {
        double diff = 100;
        int posi = -1;
        for (int i = 0; i < pattern.getNumberOfIsotopes(); i++) {
            double tempDiff = Math.abs((isoContainer.getMass()) - pattern.getIsotopes().get(i).getMass());
            if (tempDiff <= (tolerance_ppm / isoContainer.getMass()) && tempDiff < diff) {
                diff = tempDiff;
                posi = i;
            }
        }

        return posi;
    }
}
