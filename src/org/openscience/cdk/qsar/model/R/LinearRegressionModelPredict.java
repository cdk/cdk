/*
 *  Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.openscience.cdk.qsar.model.R;

/**
 * A class that wraps the return value from the R function, predict.lm.
 *
 * This is an internal class used by R to return the result of
 * the call to <a href="http://stat.ethz.ch/R-manual/R-patched/library/stats/html/predict.lm.html">predict.lm</a>.
 * As a result it should not be instantiated by the user. The actual modeling
 * class, <code>LinearRegressionModel</code>, provides acess to the various
 * fields of this object.
 * 
 *
 * @author Rajarshi Guha
 * @cdk.require r-project
 * @cdk.module qsar
 */
public class LinearRegressionModelPredict {
    double[] pred, lwr, upr, sefit;
    int degreesOfFreedom;
    double residualScale;

    public LinearRegressionModelPredict(double[] predicted, double[] standardErrors,
            double[] lowerBounds, double[] upperBounds,
            int degreesOfFreedom, double residualScale) {
        setPredicted(predicted);
        setSEFit(standardErrors);
        setLower(lowerBounds);
        setUpper(upperBounds);
        setDF(degreesOfFreedom);
        setResidualScale(residualScale);
    }

    public int getDF() { return(this.degreesOfFreedom); }
    public void setDF(int degreesOfFreedom) { this.degreesOfFreedom = degreesOfFreedom; }

    public double getResidualScale() { return(this.residualScale); }
    public void setResidualScale(double scale) { this.residualScale = scale; }

    public double[] getPredicted() { return(this.pred); }
    public void setPredicted(double[] predicted) { 
        this.pred = new double[predicted.length];
        for (int i = 0; i < predicted.length; i++) this.pred[i] = predicted[i];
    }
    public double[] getLower() { return(this.lwr); }
    public void setLower(double[] lowerBounds) { 
        this.lwr = new double[lowerBounds.length];
        for (int i = 0; i < lowerBounds.length; i++) this.lwr[i] = lowerBounds[i];
    }
    public double[] getUpper() { return(this.upr); }
    public void setUpper(double[] upperBounds) { 
        this.upr = new double[upperBounds.length];
        for (int i = 0; i < upperBounds.length; i++) this.upr[i] = upperBounds[i];
    }
    public double[] getSEFit() { return(this.sefit); }
    public void setSEFit(double[] standardErrors) { 
        this.sefit = new double[standardErrors.length];
        for (int i = 0; i < standardErrors.length; i++) this.sefit[i] = standardErrors[i];
    }

}


