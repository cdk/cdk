package org.openscience.cdk.formula;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class to manipulate IsotopePattern objects.
 *
 * @author Miguel Rojas Cherto
 *
 * @cdk.module  formula
 * @cdk.githash
 */
public class IsotopePatternManipulator {

    /**
     * Return the isotope pattern normalized to the highest abundance.
     *
     * @param isotopeP  The IsotopePattern object to normalize
     * @return          The IsotopePattern normalized
     */
    public static IsotopePattern normalize(IsotopePattern isotopeP) {
        IsotopeContainer isoHighest = null;

        double biggestAbundance = 0;
        /* Extraction of the isoContainer with the highest abundance */
        for (IsotopeContainer isoContainer : isotopeP.getIsotopes()) {
            double abundance = isoContainer.getIntensity();
            if (biggestAbundance < abundance) {
                biggestAbundance = abundance;
                isoHighest = isoContainer;
            }
        }
        /* Normalize */
        IsotopePattern isoNormalized = new IsotopePattern();
        for (IsotopeContainer isoContainer : isotopeP.getIsotopes()) {
            double inten = isoContainer.getIntensity() / isoHighest.getIntensity();
            IsotopeContainer icClone;
            try {
                icClone = (IsotopeContainer) isoContainer.clone();
                icClone.setIntensity(inten);
                if (isoHighest.equals(isoContainer))
                    isoNormalized.setMonoIsotope(icClone);
                else
                    isoNormalized.addIsotope(icClone);

            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

        }
        isoNormalized.setCharge(isotopeP.getCharge());
        return isoNormalized;
    }

    /**
     * Return the isotope pattern sorted and normalized by intensity
     * to the highest abundance.
     *
     * @param isotopeP  The IsotopePattern object to sort
     * @return          The IsotopePattern sorted
     */
    public static IsotopePattern sortAndNormalizedByIntensity(IsotopePattern isotopeP) {
        IsotopePattern isoNorma = normalize(isotopeP);
        return sortByIntensity(isoNorma);
    }

    /**
     * Return the isotope pattern sorted by intensity
     * to the highest abundance.
     *
     * @param isotopeP  The IsotopePattern object to sort
     * @return          The IsotopePattern sorted
     */
    public static IsotopePattern sortByIntensity(IsotopePattern isotopeP) {
        try {
            
            IsotopePattern isoSort = (IsotopePattern) isotopeP.clone();
            
            // Do nothing for empty isotope pattern
            if (isoSort.getNumberOfIsotopes() == 0)
                return isoSort;

            // Sort the isotopes
            List<IsotopeContainer> listISO = isoSort.getIsotopes();
            Collections.sort(listISO, new Comparator<IsotopeContainer>() {
                @Override
                public int compare(IsotopeContainer o1, IsotopeContainer o2) {
                    return Double.compare(o2.getIntensity(),o1.getIntensity());
                }
            });
           
            // Set the monoisotopic peak to the one with highest intensity
            isoSort.setMonoIsotope(listISO.get(0));
            
            return isoSort;

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Return the isotope pattern sorted by mass
     * to the highest abundance.
     *
     * @param isotopeP  The IsotopePattern object to sort
     * @return          The IsotopePattern sorted
     */
    public static IsotopePattern sortByMass(IsotopePattern isotopeP) {
        try {
            IsotopePattern isoSort = (IsotopePattern) isotopeP.clone();
            
            // Do nothing for empty isotope pattern
            if (isoSort.getNumberOfIsotopes() == 0) 
                return isoSort;

            // Sort the isotopes
            List<IsotopeContainer> listISO = isoSort.getIsotopes();
            Collections.sort(listISO, new Comparator<IsotopeContainer>() {
                @Override
                public int compare(IsotopeContainer o1, IsotopeContainer o2) {
                    return Double.compare(o1.getMass(),o2.getMass());
                }
            });
           
            // Set the monoisotopic peak to the one with lowest mass
            isoSort.setMonoIsotope(listISO.get(0));
            
            return isoSort;

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
