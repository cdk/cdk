/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2008  Rajarshi Guha <rajarshi.guha@gmail.com>
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

package org.openscience.cdk.qsar.model.R;

/**
 * A class that wraps the return value from R function, pls.
 *
 * This is an internal class used by R to return the result of
 * the call to 
 * <a href="http://www.maths.lth.se/help/R/.R/library/pls.pcr/html/mvr.html" target="_top">pls</a>.
 * As a result it should not be instantiated by the user. The actual modeling
 * class, <code>PLSRegressionModel</code>, provides acess to the various
 * fields of this object.
 * 
 *
 * @author Rajarshi Guha
 * @cdk.require r-project
 * @cdk.module qsar
 * @cdk.svnrev  $Revision$
 * @deprecated
 */

class V2M {
    static  double[][] VectorToMatrix(double[] v, int nrow, int ncol) {
        double[][] m = new double[nrow][ncol];
        for (int i = 0; i < ncol; i++) {
            for (int j = 0; j < nrow; j++) {
                m[j][i] = v[j + i*nrow];
            }
        }
        return(m);
    }
    static  double[][][] VectorToCube(double[] v, int d1, int d2, int d3) {
        // d2 ~ nrow, d3 ~ ncol
        double[][][] m = new double[d1][d2][d3];
        for (int k = 0; k < d1; k++) {
            for (int i = 0; i < d3; i++) {
                for (int j = 0; j < d2; j++) {
                    m[k][j][i] = v[j + i*d2 + k*d2*d3];
                }
            }
        }
        return(m);
    }
}
/*
 ncase tells us how many latent variable cases are being considered.
 So if ncase == 1, the model only considered 1 LV. 
 if ncase == 2, the model considered the cases of 1 LV and 2 LV

 ncomp will contain the number of latent variables for each case. So
 if pls() was called with 2:3 there are 2 cases, the first case considered
 2 LV's, the second case considered 3 LV's.

 But note that xscores, yscores, xload and yload will have the number
 of columns equal to the max value of ncomp. So even if ncomp contains 2:3
 these arrays will have 3 columns (for the three cases - 1LV, 2LV & 3LV)

 npvar is the number of Y variables

 rms     - ncase x npvar
 yscores - nobs x max(ncomp)
 xscores - nobs x max(nncomp)
 yload   - npvar x max(ncomp)
 xload   - nvar x max(ncomp)
 ypred   - ncase x nobs x npvar
 B       - ncase x nvar x npvar
*/
class PLSTraining {
    double[][] rms = null;
    double[][] xscores = null;
    double[][] xload = null;
    double[][] yscores = null;
    double[][] yload = null;
    double[][][] B = null;
    double[][][] ypred = null;

    PLSTraining(int[] ncomp, double[] B, double[] ypred,
            double[] rms, 
            double[] xscores, double[] xload,
            double[] yscores, double[] yload) {
        
        int ncase = ncomp.length;
        int nobs = xscores.length / ncase;
        int nvar = xload.length / ncase;
        int npvar = yload.length / ncase;

        int maxncomp = -999999;
        for (int i = 0; i < ncomp.length; i++) {
            if (ncomp[i] > maxncomp) maxncomp = ncomp[i];
        }
        
        this.rms = V2M.VectorToMatrix(rms, ncase, npvar);
        this.xscores = V2M.VectorToMatrix(xscores, nobs, maxncomp);
        this.yscores = V2M.VectorToMatrix(yscores, nobs, maxncomp);
        this.yload = V2M.VectorToMatrix(yload, npvar, maxncomp);
        this.xload = V2M.VectorToMatrix(xload, nvar, maxncomp);
        this.ypred = V2M.VectorToCube(ypred, ncase, nobs, npvar);
        this.B = V2M.VectorToCube(B, ncase, nvar, npvar);
    }
}

/*
 * npvar is the number of Y variables
 * rms   - ncase x npvar
 * rmssd - ncase x npvar
 * r2    - ncase x npvar
 * ypred - ncase x nobs x npvar
 */
class PLSValidation {
    double[][][] ypred = null;
    int niter, nlv;
    double[][] rms = null;
    double[][]  rmssd = null;
    double[][] r2 = null;

    PLSValidation(int[] ncomp, int nobs, int niter, int nlv,
            double[] ypred,  double[] rms, double[] rmssd, double[] r2) {
        
        int ncase = ncomp.length;
        int npvar = rms.length / ncase;
        
        this.niter = niter;
        this.nlv = nlv;
        this.rms = V2M.VectorToMatrix(rms, ncase, npvar);
        this.rmssd = V2M.VectorToMatrix(rmssd, ncase, npvar);
        this.r2 = V2M.VectorToMatrix(r2, ncase, npvar);;
        this.ypred = V2M.VectorToCube(ypred, ncase, nobs, npvar);
    }
}

public class PLSRegressionModelFit {
    int nobs, nvar, npvar, ncase;
    int[] ncomp = null;
    String method;
    PLSTraining train = null;
    PLSValidation valid = null;

    public PLSRegressionModelFit(int nobs, int nvar, int npred,
            int[] ncomp, String method) {

        this.nobs = nobs;
        this.nvar = nvar;
        this.npvar = npred;
        this.ncase = ncomp.length;
        this.method = method;

        this.ncomp = new int[this.ncase];
        for (int i = 0; i < this.ncase; i++) this.ncomp[i] = ncomp[i];
    }

    public void setTrainingData(double[] B, double[] ypred, double[] rms, 
            double[] xscores, double[] yscores,
            double[] xload, double[] yload) {
        this.train = new PLSTraining(this.ncomp, B, ypred, rms, xscores, yscores, xload, yload);
    }
    public void setValidationData(int niter, int nlv,
            double[] ypred,  double[] rms, double[] rmssd, double[] r2) {
        this.valid = new PLSValidation(this.ncomp, this.nobs, niter, nlv, ypred, rms, rmssd, r2);
    }


    public boolean wasValidated() { 
        if (this.valid != null) return(true); 
        else return(false);
    }
        
    public int[]        getNComp() { return this.ncomp; }
    public String       getMethod() { return this.method; }

    public double[][][] getB() { return this.train.B; }
    public double[][][] getTrainingYPred() { return this.train.ypred; }
    public double[][]   getTrainingRMS() { return this.train.rms; }
    public double[][]   getXScores() { return this.train.xscores; }
    public double[][]   getYScores() { return this.train.yscores; }
    public double[][]   getXLoading() { return this.train.xload; }
    public double[][]   getYLoading() { return this.train.yload; }

    public double[][][] getValidationYPred() { return this.valid.ypred; }
    public double[][]   getValidationRMS() { return this.valid.rms; }
    public double[][]   getValidationRMSSD() { return this.valid.rmssd; }
    public double[][]   getValidationR2() { return this.valid.r2; }
    public int          getValidationLV() { return this.valid.nlv; }
    public int          getValidationIter() { return this.valid.niter; }

}


    
