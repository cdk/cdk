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

import java.util.ArrayList;
import java.util.List;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 * Generates all Combinatorial chemical isotopes given a structure.
 *
 * @cdk.module formula
 * @author Miguel Rojas Cherto
 * @cdk.created 2007-11-20
 * @cdk.githash
 * @cdk.keyword isotope pattern
 */
public class IsotopePatternGenerator {

    private IChemObjectBuilder builder = null;
    private IsotopeFactory isoFactory;

    private ILoggingTool logger =
            LoggingToolFactory.createLoggingTool(IsotopePatternGenerator.class);

    /** Minimal abundance of the isotopes to be added in the combinatorial search. */
    private double minIntensity = 0.00001;

    private double minAbundance = 1E-10; // n.b. not actually abundance
    private double resolution = 0.00005f;
    private boolean storeFormula = false;

    /**
     * Constructor for the IsotopeGenerator. The minimum abundance is set to 0.1 (10% abundance) by
     * default.
     */
    public IsotopePatternGenerator() {
        this(0.1);
    }

    /**
     * Constructor for the IsotopeGenerator.
     *
     * @param minIntensity Minimal intensity of the isotopes to be added in the combinatorial search
     *     (scale 0.0 to 1.0)
     */
    public IsotopePatternGenerator(double minIntensity) {
        this.minIntensity = minIntensity;
        logger.info("Generating all Isotope structures with IsotopeGenerator");
    }

    /**
     * Set the minimum (normalised) intensity to generate.
     *
     * @param minIntensity the minimum intensity
     * @return self for method chaining
     */
    public IsotopePatternGenerator setMinIntensity(double minIntensity) {
        this.minIntensity = minIntensity;
        return this;
    }

    /**
     * Set the minimum resolution at which peaks within this mass difference should be considered
     * equivalent.
     *
     * @param resolution the minimum resolution
     * @return self for method chaining
     */
    public IsotopePatternGenerator setMinResolution(double resolution) {
        this.resolution = resolution;
        return this;
    }

    /**
     * When generating the isotope containers store the MF for each {@link IsotopeContainer}.
     *
     * @param storeFormula formulas should be stored
     * @return self for method chaining
     */
    public IsotopePatternGenerator setStoreFormulas(boolean storeFormula) {
        this.storeFormula = storeFormula;
        return this;
    }

    /**
     * Get all combinatorial chemical isotopes given a structure.
     *
     * @param molFor The IMolecularFormula to start
     * @return A IsotopePattern object containing the different combinations
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

        IMolecularFormula molecularFormula =
                MolecularFormulaManipulator.getMajorIsotopeMolecularFormula(mf, builder);

        IsotopePattern abundance_Mass = null;

        for (IIsotope isos : molecularFormula.isotopes()) {
            String elementSymbol = isos.getSymbol();
            int atomCount = molecularFormula.getIsotopeCount(isos);

            // Generate possible isotope containers for the current atom's
            // these will then me 'multiplied' with the existing patten
            List<IsotopeContainer> additional = new ArrayList<>();
            for (IIsotope isotope : isoFactory.getIsotopes(elementSymbol)) {
                double mass = isotope.getExactMass();
                double abundance = isotope.getNaturalAbundance();
                if (abundance <= 0.000000001) continue;
                IsotopeContainer container = new IsotopeContainer(mass, abundance);
                if (storeFormula) container.setFormula(asFormula(isotope));
                additional.add(container);
            }

            for (int i = 0; i < atomCount; i++)
                abundance_Mass = calculateAbundanceAndMass(abundance_Mass, additional);
        }

        IsotopePattern isoP =
                IsotopePatternManipulator.sortAndNormalizedByIntensity(abundance_Mass);
        isoP = cleanAbundance(isoP, minIntensity);
        IsotopePattern isoPattern = IsotopePatternManipulator.sortByMass(isoP);
        return isoPattern;
    }

    private IMolecularFormula asFormula(IIsotope isotope) {
        IMolecularFormula mf = builder.newInstance(IMolecularFormula.class);
        mf.addIsotope(isotope);
        return mf;
    }

    private IMolecularFormula union(IMolecularFormula a, IMolecularFormula b) {
        IMolecularFormula mf = builder.newInstance(IMolecularFormula.class);
        mf.add(a);
        mf.add(b);
        return mf;
    }

    private static IsotopeContainer findExisting(
            List<IsotopeContainer> containers, double mass, double treshhold) {
        for (IsotopeContainer container : containers) {
            if (Math.abs(container.getMass() - mass) <= treshhold) {
                return container;
            }
        }
        return null;
    }

    private void addDistinctFormula(IsotopeContainer container, IMolecularFormula mf) {
        for (IMolecularFormula curr : container.getFormulas())
            if (MolecularFormulaManipulator.compare(curr, mf)) return;
        container.addFormula(mf);
    }

    /**
     * Calculates the mass and abundance of all isotopes generated by adding one atom. Receives the
     * periodic table element and calculate the isotopes, if there exist a previous calculation, add
     * these new isotopes. In the process of adding the new isotopes, remove those that has an
     * abundance less than setup parameter minIntensity, and remove duplicated masses.
     *
     * @param additional additional isotopes to 'multiple' the current pattern by
     * @return the calculation was successful
     */
    private IsotopePattern calculateAbundanceAndMass(
            IsotopePattern current, List<IsotopeContainer> additional) {

        if (additional == null || additional.size() == 0) return current;

        List<IsotopeContainer> containers = new ArrayList<>();

        // Verify if there is a previous calculation. If it exists, add the new
        // isotopes
        if (current == null) {
            current = new IsotopePattern();
            for (IsotopeContainer container : additional) current.addIsotope(container);
        } else {
            for (IsotopeContainer container : current.getIsotopes()) {
                for (IsotopeContainer other : additional) {

                    double abundance = container.getIntensity() * other.getIntensity() * 0.01;
                    double mass = container.getMass() + other.getMass();

                    // merge duplicates with some resolution
                    IsotopeContainer existing = findExisting(containers, mass, resolution);
                    if (existing != null) {
                        double newIntensity = existing.getIntensity() + abundance;
                        // moving weighted avg
                        existing.setMass(
                                (existing.getMass() * existing.getIntensity() + mass * abundance)
                                        / newIntensity);
                        existing.setIntensity(newIntensity);
                        if (storeFormula) {
                            for (IMolecularFormula mf : container.getFormulas())
                                addDistinctFormula(existing, union(mf, other.getFormula()));
                        }
                        continue;
                    }

                    // Filter isotopes too small
                    if (abundance > minAbundance) {
                        IsotopeContainer newcontainer = new IsotopeContainer(mass, abundance);
                        if (storeFormula) {
                            for (IMolecularFormula mf : container.getFormulas())
                                newcontainer.addFormula(union(mf, other.getFormula()));
                        }
                        containers.add(newcontainer);
                    }
                }
            }

            current = new IsotopePattern();
            for (IsotopeContainer container : containers) {
                current.addIsotope(container);
            }
        }
        return current;
    }

    /**
     * Normalize the intensity (relative abundance) of all isotopes in relation of the most abundant
     * isotope.
     *
     * @param isopattern The IsotopePattern object
     * @param minIntensity The minimum abundance
     * @return The IsotopePattern cleaned
     */
    private IsotopePattern cleanAbundance(IsotopePattern isopattern, double minIntensity) {

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
        sortedIsoPattern.setMonoIsotope(new IsotopeContainer(isopattern.getIsotopes().get(0)));
        for (int i = 1; i < isopattern.getNumberOfIsotopes(); i++) {
            if (isopattern.getIsotopes().get(i).getIntensity() >= (minIntensity)) {
                IsotopeContainer container = new IsotopeContainer(isopattern.getIsotopes().get(i));
                sortedIsoPattern.addIsotope(container);
            }
        }
        return sortedIsoPattern;
    }
}
