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
 * A class that wraps the return value from R function, lm..
 *
 * This is an internal class used by R to return the result of
 * the call to <a href="http://stat.ethz.ch/R-manual/R-patched/library/stats/html/lm.html">lm</a>.
 * As a result it should not be instantiated by the user. The actual modeling
 * class, <code>LinearRegressionModel</code>, provides acess to the various
 * fields of this object.
 * 
 *
 * @author Rajarshi Guha
 * @cdk.require r-project
 * @cdk.module qsar
 */

public class LinearRegressionModelFit {
    double[] coeff, res, fitted;
    int rank, dfResidual;

    public LinearRegressionModelFit(double[] coeff, double[] res, double[] fitted, int rank, int df) {
        setCoefficients(coeff);
        setResiduals(res);
        setFitted(fitted);
        setRank(rank);
        setdfResidual(df);
    }
    public int getRank() { return(this.rank); }
    public void setRank(int v) { this.rank = v; };

    public double[] getResiduals() { return(this.res); }
    public void setResiduals(double[] v) { 
        this.res = new double[v.length];
        for (int i = 0; i < v.length; i++) this.res[i] = v[i];
    }

    public double[] getCoefficients() { return(this.coeff); }
    public void setCoefficients(double[] v) { 
        this.coeff = new double[v.length];
        for (int i = 0; i < v.length; i++) this.coeff[i] = v[i];
    }

    public int getdfResidual() { return(this.dfResidual); }
    public void setdfResidual(int df) { this.dfResidual = df; }

    public double[] getFitted() { return(this.fitted); }
    public void setFitted(double[] v) { 
        this.fitted = new double[v.length];
        for (int i = 0; i < v.length; i++) this.fitted[i] = v[i];
    }
}


    
