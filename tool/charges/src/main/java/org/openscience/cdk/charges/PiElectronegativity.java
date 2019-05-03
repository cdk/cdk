/*  Copyright (C) 2008  Miguel Rojas <miguelrojasch@yahoo.es>
 *
 *  Contact: cdk-devel@list.sourceforge.net
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
package org.openscience.cdk.charges;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Calculation of the electronegativity of orbitals of a molecule
 * by the method Gasteiger based on electronegativity is given by X = a + bq + c(q*q).
 *
 * @author       Miguel Rojas Cherto
 * @cdk.created  2008-104-31
 * @cdk.module   charges
 * @cdk.keyword  electronegativity
 * @cdk.githash
 */
public class PiElectronegativity {

    private GasteigerMarsiliPartialCharges peoe  = null;
    private GasteigerPEPEPartialCharges    pepe  = null;

    /**Number of maximum iterations*/
    private int                            maxI  = 6;
    /**Number of maximum resonance structures*/
    private int                            maxRS = 50;

    private IAtomContainer                 molPi;
    private IAtomContainer                 acOldP;
    private double[][]                     gasteigerFactors;

    private final ILoggingTool logger = LoggingToolFactory.createLoggingTool(PiElectronegativity.class);

    /**
     * Constructor for the PiElectronegativity object.
     */
    public PiElectronegativity() {
        this(6, 50);
    }

    /**
     * Constructor for the Electronegativity object.
     *
     * @param maxIterations         The maximal number of Iteration
     * @param maxResonStruc         The maximal number of Resonance Structures
     */
    public PiElectronegativity(int maxIterations, int maxResonStruc) {
        peoe = new GasteigerMarsiliPartialCharges();
        pepe = new GasteigerPEPEPartialCharges();
        maxI = maxIterations;
        maxRS = maxResonStruc;
    }

    /**
     * calculate the electronegativity of orbitals pi.
     *
     * @param ac                    IAtomContainer
     * @param atom                  atom for which effective atom electronegativity should be calculated
     *
     * @return piElectronegativity
     */
    public double calculatePiElectronegativity(IAtomContainer ac, IAtom atom) {

        return calculatePiElectronegativity(ac, atom, maxI, maxRS);
    }

    /**
     * calculate the electronegativity of orbitals pi.
     *
     * @param ac                    IAtomContainer
     * @param atom                  atom for which effective atom electronegativity should be calculated
     * @param maxIterations         The maximal number of Iteration
     * @param maxResonStruc         The maximal number of Resonance Structures
     *
     * @return piElectronegativity
     */
    public double calculatePiElectronegativity(IAtomContainer ac, IAtom atom, int maxIterations, int maxResonStruc) {
        maxI = maxIterations;
        maxRS = maxResonStruc;

        double electronegativity = 0;

        try {
            if (!ac.equals(acOldP)) {
                molPi = ac.getBuilder().newInstance(IAtomContainer.class, ac);

                peoe = new GasteigerMarsiliPartialCharges();
                peoe.assignGasteigerMarsiliSigmaPartialCharges(molPi, true);
                IAtomContainerSet iSet = ac.getBuilder().newInstance(IAtomContainerSet.class);
                iSet.addAtomContainer(molPi);
                iSet.addAtomContainer(molPi);

                gasteigerFactors = pepe.assignrPiMarsilliFactors(iSet);

                acOldP = ac;
            }
            IAtom atomi = molPi.getAtom(ac.indexOf(atom));
            int atomPosition = molPi.indexOf(atomi);
            int stepSize = pepe.getStepSize();
            int start = (stepSize * (atomPosition) + atomPosition);
            double q = atomi.getCharge();
            if (molPi.getConnectedLonePairsCount(molPi.getAtom(atomPosition)) > 0
                    || molPi.getMaximumBondOrder(atomi) != IBond.Order.SINGLE || atomi.getFormalCharge() != 0) {
                return ((gasteigerFactors[1][start]) + (q * gasteigerFactors[1][start + 1]) + (gasteigerFactors[1][start + 2] * (q * q)));
            }
        } catch (Exception e) {
            logger.error(e);
        }

        return electronegativity;
    }

    /**
     * set the maximum number of Iterations.
     *
     * @param maxIterations The maximum number of iterations
     */
    public void setMaxIterations(int maxIterations) {
        maxI = maxIterations;
    }

    /**
     * set the maximum number of resonance structures.
     *
     * @param maxResonStruc The maximum number of resonance structures
     */
    public void setMaxResonStruc(int maxResonStruc) {
        maxRS = maxResonStruc;
    }

    /**
     * get the maximum number of Iterations.
     *
     * @return The maximum number of iterations
     */
    public int getMaxIterations() {
        return maxI;
    }

    /**
     * get the maximum number of resonance structures.
     *
     * @return The maximum number of resonance structures
     */
    public int getMaxResonStruc() {
        return maxRS;
    }

}
