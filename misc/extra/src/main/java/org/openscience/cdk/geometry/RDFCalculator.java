/* Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
 *                    2009  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.geometry;

import java.util.Iterator;
import javax.vecmath.Point3d;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Calculator of radial distribution functions. The RDF has bins defined around a point, i.e. the
 * first bin starts at 0 &Aring; and ends at 0.5*resolution &Aring;, and the second bins ends at
 * 1.5*resolution &Aring;.
 *
 * <p>By default, the RDF is unweighted. By implementing and registering a <code>RDFWeightFunction
 * </code>, the RDF can become weighted. For example, to weight according to partial charge
 * interaction, this code could be used:
 *
 * <pre>
 * RDFCalculator calculator = new RDFCalculator(0.0, 5.0, 0.1, 0.0,
 *     new RDFWeightFunction() {
 *         public double calculate(Atom atom, Atom atom2) {
 *             return atom.getCharge()*atom2.getCharge();
 *         }
 *     }
 * );
 * </pre>
 *
 * @cdk.module extra
 * @cdk.githash
 * @author Egon Willighagen
 * @cdk.created 2005-01-10
 * @cdk.keyword radial distribution function
 * @cdk.keyword RDF
 * @see org.openscience.cdk.geometry.IRDFWeightFunction
 */
public class RDFCalculator {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(RDFCalculator.class);

    private double startCutoff;
    private double cutoff;
    private double resolution;
    private double peakWidth;

    private IRDFWeightFunction weightFunction;

    /**
     * Constructs a RDF calculator that calculates a unweighted, digitized RDF function.
     *
     * @param startCutoff radial length in &Aring;ngstrom at which the RDF starts
     * @param cutoff radial length in &Aring;ngstrom at which the RDF stops
     * @param resolution width of the bins
     * @param peakWidth width of the gaussian applied to the peaks in &Aring;ngstrom
     */
    public RDFCalculator(double startCutoff, double cutoff, double resolution, double peakWidth) {
        this(startCutoff, cutoff, resolution, peakWidth, null);
    }

    /**
     * Constructs a RDF calculator that calculates a digitized RDF function.
     *
     * @param startCutoff radial length in &Aring;ngstrom at which the RDF starts
     * @param cutoff radial length in &Aring;ngstrom at which the RDF stops
     * @param resolution width of the bins
     * @param peakWidth width of the gaussian applied to the peaks in &Aring;ngstrom
     * @param weightFunction the weight function. If null, then an unweighted RDF is calculated
     */
    public RDFCalculator(
            double startCutoff,
            double cutoff,
            double resolution,
            double peakWidth,
            IRDFWeightFunction weightFunction) {
        this.startCutoff = startCutoff;
        this.cutoff = cutoff;
        this.resolution = resolution;
        this.peakWidth = peakWidth;
        this.weightFunction = weightFunction;
    }

    /**
     * Calculates a RDF for <code>Atom</code> atom in the environment of the atoms in the <code>
     * AtomContainer</code>.
     */
    public double[] calculate(IAtomContainer container, IAtom atom) {
        int length = (int) ((cutoff - startCutoff) / resolution) + 1;
        logger.debug("Creating RDF of length ", length);

        // the next we need for Gaussian smoothing
        int binsToFillOnEachSide = (int) (peakWidth * 3.0 / resolution);
        double sigmaSquare = Math.pow(peakWidth, 2.0);
        // factors is only half a Gaussian, taking advantage of being symmetrical!
        double[] factors = new double[binsToFillOnEachSide];
        double totalArea = 0.0;
        if (factors.length > 0) {
            factors[0] = 1;
            for (int binCounter = 1; binCounter < factors.length; binCounter++) {
                double height =
                        Math.exp(
                                -1.0
                                        * (Math.pow(((double) binCounter) * resolution, 2.0))
                                        / sigmaSquare);
                factors[binCounter] = height;
                totalArea += height;
            }
            // normalize the Gaussian to unit area
            for (int binCounter = 0; binCounter < factors.length; binCounter++) {
                factors[binCounter] = factors[binCounter] / totalArea;
            }
        }

        // this we need always
        double[] rdf = new double[length];
        double distance = 0.0;
        int index = 0;

        Point3d atomPoint = atom.getPoint3d();
        Iterator<IAtom> atomsInContainer = container.atoms().iterator();
        while (atomsInContainer.hasNext()) {
            IAtom atomInContainer = (IAtom) atomsInContainer.next();
            if (atomInContainer.equals(atom)) continue; // don't include the central atom
            distance = atomPoint.distance(atomInContainer.getPoint3d());
            index = (int) ((distance - startCutoff) / this.resolution);
            double weight = 1.0;
            if (weightFunction != null) {
                weight = weightFunction.calculate(atom, atomInContainer);
            }
            if (factors.length > 0) {
                // apply Gaussian smoothing
                rdf[index] += weight * factors[0];
                for (int binCounter = 1; binCounter < factors.length; binCounter++) {
                    double diff = weight * factors[binCounter];
                    if ((index - binCounter) >= 0) {
                        rdf[index - binCounter] += diff;
                    }
                    if ((index + binCounter) < length) {
                        rdf[index + binCounter] += diff;
                    }
                }
            } else {
                rdf[index] += weight; // unweighted
            }
        }
        return rdf;
    }
}
