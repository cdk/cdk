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
 * A class that wraps the return value from the R function, predict.mvr.
 *
 * This is an internal class used by R to return the result of
 * the call to <a href="http://www.maths.lth.se/help/R/.R/library/pls.pcr/html/mvr.html" target="_top">predict.mvr</a>.
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
public class PLSRegressionModelPredict {
    double[][] preds = null;

    private double[][] VectorToMatrix(double[] v, int nrow, int ncol) {
        double[][] m = new double[nrow][ncol];
        for (int i = 0; i < ncol; i++) {
            for (int j = 0; j < nrow; j++) {
                m[j][i] = v[j + i*nrow];
            }
        }
        return(m);
    }
        
    /**
     * Constructor to contain the results of a PLS prediction.
     *
     * This class should not be instantiated directly and is really
     * only meant to be instantiated from an R session
     *
     * @param ncol The number of predicted variables
     * @param preds A 1-dimensional array of predicted values
     */
    public PLSRegressionModelPredict(int ncol, double[] preds) {
        this.preds = VectorToMatrix(preds, preds.length/ncol, ncol);
    }

    /** 
     * Get the predicted values.
     *
     * This method returns the predicted values obtained by using new data
     * with a previously built PLS regression model
     *
     * @return A 2-dimensional array of predictions, columns correspond to the 
     * predicted variables
     */
    public double[][] getPredictions() {
        return(this.preds);
    }
}


