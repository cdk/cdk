/* Copyright (C) 2007  Miguel Rojasch <miguelrojasch@users.sf.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.formula.rules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.formula.IsotopeContainer;
import org.openscience.cdk.formula.IsotopePattern;
import org.openscience.cdk.formula.IsotopePatternGenerator;
import org.openscience.cdk.formula.IsotopePatternManipulator;
import org.openscience.cdk.formula.IsotopePatternSimilarity;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * This class validate if the Isotope Pattern from a given IMolecularFormula
 *  correspond with other to compare.
 *
 *
 * <table border="1">
 *   <caption>Table 1: Parameters set by this rule.</caption>
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>isotopePattern</td>
 *     <td><pre>{@code List <Double[]>}</pre></td>
 *     <td>The Isotope Pattern to compare</td>
 *   </tr>
 * </table>
 *
 * @cdk.module  formula
 * @author      Miguel Rojas Cherto
 * @cdk.created 2007-11-20
 * @cdk.githash
 */
public class IsotopePatternRule implements IRule {

    private static ILoggingTool      logger        = LoggingToolFactory.createLoggingTool(IsotopePatternRule.class);

    /** Accuracy on the mass measuring isotope pattern*/
    private double                   toleranceMass = 0.001;

    private IsotopePattern           pattern;

    IsotopePatternGenerator          isotopeGe;

    private IsotopePatternSimilarity is;

    /**
     *  Constructor for the IsotopePatternRule object.
     */
    public IsotopePatternRule() {
        isotopeGe = new IsotopePatternGenerator(0.01);
        is = new IsotopePatternSimilarity();
        is.seTolerance(toleranceMass);
    }

    /**
     * Sets the parameters attribute of the IsotopePatternRule object.
     *
     * @param params          The new parameters value
     * @throws CDKException   Description of the Exception
     *
     * @see                   #getParameters
     */
    @Override
    public void setParameters(Object[] params) throws CDKException {
        if (params.length != 2) throw new CDKException("IsotopePatternRule expects two parameter");

        if (!(params[0] instanceof List)) throw new CDKException("The parameter one must be of type List<Double[]>");

        if (!(params[1] instanceof Double)) throw new CDKException("The parameter two must be of type Double");

        pattern = new IsotopePattern();
        for (double[] listISO : (List<double[]>) params[0]) {
            pattern.addIsotope(new IsotopeContainer(listISO[0], listISO[1]));
        }

        is.seTolerance((Double) params[1]);
    }

    /**
     * Gets the parameters attribute of the IsotopePatternRule object.
     *
     * @return The parameters value
     * @see    #setParameters
     */
    @Override
    public Object[] getParameters() {
        // return the parameters as used for the rule validation
        Object[] params = new Object[2];
        if (pattern == null)
            params[0] = null;
        else {
            List<double[]> params0 = new ArrayList<double[]>();
            for (IsotopeContainer isotope : pattern.getIsotopes()) {
                params0.add(new double[] { isotope.getMass(), isotope.getIntensity() });
            }
            params[0] = params0;
        }
        params[1] = toleranceMass;
        return params;
    }

    /**
     * Validate the isotope pattern of this IMolecularFormula. Important, first
     * you have to add with the {@link #setParameters(Object[])} a IMolecularFormulaSet
     * which represents the isotope pattern to compare.
     *
     * @param formula   Parameter is the IMolecularFormula
     * @return          A double value meaning 1.0 True, 0.0 False
     */

    @Override
    public double validate(IMolecularFormula formula) throws CDKException {
        logger.info("Start validation of ", formula);

        IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator(0.1);
        IsotopePattern patternIsoPredicted = isotopeGe.getIsotopes(formula);
        IsotopePattern patternIsoNormalize = IsotopePatternManipulator.normalize(patternIsoPredicted);

        return is.compare(pattern, patternIsoNormalize);
    }

}
