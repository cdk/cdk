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
 * A class that wraps the return value from R function, nnet().
 *
 * This is an internal class used by R to return the result of
 * the call to <a href="http://stat.ethz.ch/R-manual/R-patched/library/nnet/html/nnet.html">nnet</a>.
 * As a result it should not be instantiated by the user. The actual modeling
 * class, <code>CNNRegressionModel</code>, provides acess to the various
 * fields of this object.
 * 
 *
 * @author Rajarshi Guha
 * @cdk.require r-project
 * @cdk.module qsar
 */

public class CNNRegressionModelFit {
    private int noutput; // number of output neurons (== number of classes)
    private double[] weights;
    private double[][] fitted;
    private double[][] residuals;
    private double value;
    private double[][] hessian = null;

    private double[][] vectorToMatrix(double[] v, int nrow, int ncol) {
        double[][] m = new double[nrow][ncol];
        for (int i = 0; i < ncol; i++) {
            for (int j = 0; j < nrow; j++) {
                m[j][i] = v[j + i*nrow];
            }
        }
        return(m);
    }
    
    public CNNRegressionModelFit(
            int noutput, 
            int nobs,
            double[] weights, 
            double[] fitted, double[] residual, 
            double value,
            double[] hessian) {

        // dimensions of hessian = nwt x nwt
        // dimensions of fitted, residual = nobs x noutput
        // also note that matrices come in as columnwise 1D arrays

        this.noutput = noutput;
        setWeights(weights);
        setResiduals(vectorToMatrix(residual, nobs,noutput));
        setFitted(vectorToMatrix(fitted, nobs,noutput));
        setValue(value);
        setHessian(vectorToMatrix(hessian,weights.length,weights.length));
    }
    public CNNRegressionModelFit(
            int noutput, 
            int nobs,
            double[] weights, 
            double[] fitted, double[] residual, 
            double value) {
        this.noutput = noutput;
        setWeights(weights);
        setResiduals(vectorToMatrix(residual, nobs,noutput));
        setFitted(vectorToMatrix(fitted, nobs,noutput));
        setValue(value);
    }

    public double getValue() {
        return(this.value);
    }
    public void setValue(double value) {
        this.value = value;
    }

    public double[][] getHessian() { return(this.hessian); }
    public void setHessian(double[][] theHessian) { 
        if (theHessian == null) return;
        this.hessian = new double[theHessian.length][this.noutput];
        for (int i = 0; i < theHessian.length; i++) {
            for (int j = 0; j < this.noutput; j++) {
                this.hessian[i][j] = theHessian[i][j];
            }
        }
    }

    public double[] getWeights() { return(this.weights); }
    public void setWeights(double[] weights) {
        this.weights = new double[weights.length];
        for (int i = 0; i < weights.length; i++) {
            this.weights[i] = weights[i];
        }
    }

    public double[][] getResiduals() { return(this.residuals); }
    public void setResiduals(double[][] residuals) { 
        this.residuals = new double[residuals.length][this.noutput];
        for (int i = 0; i < residuals.length; i++) {
            for (int j = 0; j < this.noutput; j++) {
                this.residuals[i][j] = residuals[i][j];
            }
        }
    }

    public double[][] getFitted() { return(this.fitted); }
    public void setFitted(double[][] fitted) { 
        this.fitted = new double[fitted.length][this.noutput];
        for (int i = 0; i < fitted.length; i++) {
            for (int j = 0; j < this.noutput; j++) {
                this.fitted[i][j] = fitted[i][j];
            }
        }
    }
}


    
