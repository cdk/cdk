/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.geometry;

import java.awt.Dimension;
import java.util.Vector;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.*;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Calculator of radial distribution functions.
 *
 * @cdk.module  extra
 *
 * @author      Egon Willighagen
 * @cdk.created 2005-01-10
 *
 * @keyword     radial distribution function
 * @keyword     RDF
 */
public class RDFCalculator {

    private LoggingTool logger;
    
    private double startCutoff;
    private double cutoff;
    private double resolution;
    private double peakWidth;
    
    /**
     * Constructs a RDF calculator that calculates a digitized
     * RDF function.
     *
     * @param startCutoff radial length in Angstrom at which the RDF starts
     * @param cutoff      radial length in Angstrom at which the RDF stops
     * @param resolution  width of the bins
     * @param peakWidth   width of the gaussian applied to the peaks in Angstrom
     */
    public RDFCalculator(double startCutoff, double cutoff, double resolution, 
                         double peakWidth) {
        logger = new LoggingTool(this);
        
         this.startCutoff = startCutoff;
         this.cutoff = cutoff;
         this.resolution = resolution;
         this.peakWidth = peakWidth;
    }

    /**
     * Calculates a RDF for <code>Atom</code> atom in the environment
     * of the atoms in the <code>AtomContainer</code>.
     */
    public double[] calculate(AtomContainer container, Atom atom) {
        int length = (int)((cutoff-startCutoff)/resolution) + 1;
        logger.debug("Creating RDF of length ", length);

        // the next we need for Gaussian smoothing
        int binsToFillOnEachSide = (int)(peakWidth*3.0/resolution);
        double sigmaSquare = Math.pow(peakWidth, 2.0);
        double[] factors = new double[binsToFillOnEachSide];
        for (int binCounter=0; binCounter<binsToFillOnEachSide; binCounter++) {
            factors[binCounter] = Math.exp(-1.0*(Math.pow(((double)binCounter)*resolution, 2.0))/sigmaSquare);
        }
        
        // this we need always
        double[] rdf = new double[length];
        double distance = 0.0;
        int index = 0;
        
        Point3d atomPoint = atom.getPoint3d();
        Atom[] atomsInContainer = container.getAtoms();
        for (int i=0; i<atomsInContainer.length; i++) {
            distance = atomPoint.distance(atomsInContainer[i].getPoint3d());
            index = (int)((distance-startCutoff)/this.resolution);
            rdf[index] += 1; // unweighted
            if (this.peakWidth > 0.0) {
                // apply Gaussian smoothing
                for (int binCounter=1; binCounter<=binsToFillOnEachSide; binCounter++) {
                    if ((index - binCounter) >= 0) {
                        rdf[index - binCounter] += factors[binCounter];
                    }
                    if ((index + binCounter) < length) {
                        rdf[index + binCounter] += factors[binCounter];
                    }
                }
            }
        }
        return rdf;
    }
    
}



