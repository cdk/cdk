/* Copyright (C) 2007  Miguel Rojasch <miguelrojasch@users.sf.net>
 *               2014  Mark B Vine (orcid:0000-0002-7794-0426)
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.formula;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 * Generates all Combinatorial chemical isotopes given a structure.
 *
 * @cdk.module  formula
 * @author      Miguel Rojas Cherto
 * @cdk.created 2007-11-20
 * @cdk.githash
 *
 * @cdk.keyword isotope pattern
 *
 */
public class IsotopePatternGenerator {

    private IChemObjectBuilder builder        = null;
    private IsotopeFactory     isoFactory;
    private IsotopePattern     abundance_Mass = null;

    private ILoggingTool       logger         = LoggingToolFactory.createLoggingTool(IsotopePatternGenerator.class);

    /** Minimal abundance of the isotopes to be added in the combinatorial search.*/
    private double             minAbundance   = .1;

    /**
     *  Constructor for the IsotopeGenerator.
     */
    public IsotopePatternGenerator() {
        this(0.1);
    }

    /**
     * Constructor for the IsotopeGenerator.
     *
     * @param minAb Minimal abundance of the isotopes to be added
     * 				in the combinatorial search
     *
     */
    public IsotopePatternGenerator(double minAb) {
        minAbundance = minAb;
        logger.info("Generating all Isotope structures with IsotopeGenerator");
    }

    /**
     * Get all combinatorial chemical isotopes given a structure.
     *
     * @param molFor  The IMolecularFormula to start
     * @return        A IsotopePattern object containing the different combinations
     */
    public IsotopePattern getIsotopes(IMolecularFormula molFor) {

        if (builder == null) {
            try {
                isoFactory = Isotopes.getInstance();
                builder = molFor.getBuilder();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String mf = MolecularFormulaManipulator.getString(molFor, true);

        // Divide the chemical formula into tokens (element and coefficients)
        HashMap<String, Integer> tokens = new HashMap<String, Integer>();
        IMolecularFormula molecularFormula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula(mf, builder);
        for (IIsotope isos : molecularFormula.isotopes())
            tokens.put(isos.getSymbol(), molecularFormula.getIsotopeCount(isos));

        int atomCount;
        for (IIsotope isos : molecularFormula.isotopes()) {
            String elementSymbol = isos.getSymbol();
            atomCount = tokens.get(elementSymbol);

            for (int i = 0; i < atomCount; i++) {
                if (!calculateAbundanceAndMass(elementSymbol)) {
                }
            }
        }

        IsotopePattern isoP = IsotopePatternManipulator.sortAndNormalizedByIntensity(abundance_Mass);
        isoP = cleanAbundance(isoP, minAbundance);
        IsotopePattern isoPattern = IsotopePatternManipulator.sortByMass(isoP);

        return isoPattern;

    }

    /**
     * Calculates the mass and abundance of all isotopes generated by adding one
     * atom. Receives the periodic table element and calculate the isotopes, if
     * there exist a previous calculation, add these new isotopes. In the
     * process of adding the new isotopes, remove those that has an abundance
     * less than setup parameter minAbundance, and remove duplicated masses.
     *
     * @param elementSymbol  The chemical element symbol
     * @return the calculation was successful
     */
    private boolean calculateAbundanceAndMass(String elementSymbol) {

        IIsotope[] isotopes = isoFactory.getIsotopes(elementSymbol);

        if (isotopes == null) return false;

        if (isotopes.length == 0) return false;

        double mass, previousMass, abundance, totalAbundance, newAbundance;

        HashMap<Double, Double> isotopeMassAndAbundance = new HashMap<Double, Double>();
        IsotopePattern currentISOPattern = new IsotopePattern();

        // Generate isotopes for the current atom (element)
        for (int i = 0; i < isotopes.length; i++) {
            mass = isotopes[i].getExactMass();
            abundance = isotopes[i].getNaturalAbundance();
            currentISOPattern.addIsotope(new IsotopeContainer(mass, abundance));
        }

        // Verify if there is a previous calculation. If it exists, add the new
        // isotopes
        if (abundance_Mass == null) {

            abundance_Mass = currentISOPattern;
            return true;

        } else {
            for (int i = 0; i < abundance_Mass.getNumberOfIsotopes(); i++) {
                totalAbundance = abundance_Mass.getIsotopes().get(i).getIntensity();

                if (totalAbundance == 0) continue;

                for (int j = 0; j < currentISOPattern.getNumberOfIsotopes(); j++) {

                    abundance = currentISOPattern.getIsotopes().get(j).getIntensity();
                    mass = abundance_Mass.getIsotopes().get(i).getMass();

                    if (abundance == 0) continue;

                    newAbundance = totalAbundance * abundance * 0.01f;
                    mass += currentISOPattern.getIsotopes().get(j).getMass();

                    // Filter duplicated masses
                    previousMass = searchMass(isotopeMassAndAbundance.keySet(), mass);
                    if (isotopeMassAndAbundance.containsKey(previousMass)) {
                        newAbundance += isotopeMassAndAbundance.get(previousMass);
                        mass = previousMass;
                    }

                    // Filter isotopes too small
                    if (isNotZero(newAbundance)) {
                        isotopeMassAndAbundance.put(mass, newAbundance);
                    }
                    previousMass = 0;
                }
            }

            Iterator<Double> itr = isotopeMassAndAbundance.keySet().iterator();
            abundance_Mass = new IsotopePattern();
            while (itr.hasNext()) {
                mass = itr.next();
                abundance_Mass.addIsotope(new IsotopeContainer(mass, isotopeMassAndAbundance.get(mass)));
            }
        }

        return true;

    }

    /**
     * Search the key mass in this Set.
     *
     * @param keySet  The Set object
     * @param mass    The mass to look for
     * @return        The key value
     */
    private double searchMass(Set<Double> keySet, double mass) {
        double TOLERANCE = 0.00005f;
        double diff;
        for (double key : keySet) {
            diff = Math.abs(key - mass);
            if (diff < TOLERANCE) return key;
        }

        return 0.0d;
    }

    /**
     * Detection if the value is zero.
     *
     * @param number The number to analyze
     * @return       TRUE, if it zero
     */
    private boolean isNotZero(double number) {
        double pow = (double) Math.pow(10, 6);
        int fraction = (int) (number * pow);

        if (fraction <= 0) return false;

        return true;
    }

    /**
     * Normalize the intensity (relative abundance) of all isotopes in relation
     * of the most abundant isotope.
     *
     * @param isopattern   The IsotopePattern object
     * @param minAbundance The minimum abundance
     * @return             The IsotopePattern cleaned
     */
    private IsotopePattern cleanAbundance(IsotopePattern isopattern, double minAbundance) {

        double intensity, biggestIntensity = 0.0f;

        for (IsotopeContainer sc : isopattern.getIsotopes()) {

            intensity = sc.getIntensity();
            if (intensity > biggestIntensity) biggestIntensity = intensity;

        }

        for (IsotopeContainer sc : isopattern.getIsotopes()) {

            intensity = sc.getIntensity();
            intensity /= biggestIntensity;
            if (intensity < 0) intensity = 0;

            sc.setIntensity(intensity);

        }

        IsotopePattern sortedIsoPattern = new IsotopePattern();
        sortedIsoPattern.setMonoIsotope(new IsotopeContainer(isopattern.getIsotopes().get(0).getMass(), isopattern
                .getIsotopes().get(0).getIntensity()));
        for (int i = 1; i < isopattern.getNumberOfIsotopes(); i++) {
            if (isopattern.getIsotopes().get(i).getIntensity() >= (minAbundance))
                sortedIsoPattern.addIsotope(new IsotopeContainer(isopattern.getIsotopes().get(i).getMass(), isopattern
                        .getIsotopes().get(i).getIntensity()));
        }
        return sortedIsoPattern;

    }
}
