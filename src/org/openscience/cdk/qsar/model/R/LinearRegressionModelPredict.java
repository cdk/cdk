/*
 *  Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
    int df;
    double residualScale;

    public LinearRegressionModelPredict(double[] p, double[] se,
            double[] l, double[] u,
            int df, double residualScale) {
        setPredicted(p);
        setSEFit(se);
        setLower(l);
        setUpper(u);
        setDF(df);
        setResidualScale(residualScale);
    }

    public int getDF() { return(this.df); }
    public void setDF(int df) { this.df = df; }

    public double getResidualScale() { return(this.residualScale); }
    public void setResidualScale(double scale) { this.residualScale = scale; }

    public double[] getPredicted() { return(this.pred); }
    public void setPredicted(double[] v) { 
        this.pred = new double[v.length];
        for (int i = 0; i < v.length; i++) this.pred[i] = v[i];
    }
    public double[] getLower() { return(this.lwr); }
    public void setLower(double[] v) { 
        this.lwr = new double[v.length];
        for (int i = 0; i < v.length; i++) this.lwr[i] = v[i];
    }
    public double[] getUpper() { return(this.upr); }
    public void setUpper(double[] v) { 
        this.upr = new double[v.length];
        for (int i = 0; i < v.length; i++) this.upr[i] = v[i];
    }
    public double[] getSEFit() { return(this.sefit); }
    public void setSEFit(double[] v) { 
        this.sefit = new double[v.length];
        for (int i = 0; i < v.length; i++) this.sefit[i] = v[i];
    }

}


