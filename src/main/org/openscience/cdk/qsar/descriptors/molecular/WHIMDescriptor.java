/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2007  Rajarshi Guha <rajarshi@users.sourceforge.net>
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
package org.openscience.cdk.qsar.descriptors.molecular;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleArrayResultType;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.tools.LoggingTool;

import javax.vecmath.Point3d;
import java.util.HashMap;
import java.util.Map;


/**
 * Holistic descriptors described by Todeschini et al {@cdk.cite TOD98}.
 * The descriptors are based on a number of atom weightings. There are 6 different
 * possible weightings:
 * <ol>
 * <li>unit weights
 * <li>atomic masses
 * <li>van der Waals volumes
 * <li>Mulliken atomic electronegativites
 * <li>atomic polarizabilities
 * <li>E-state values described by Kier &amp; Hall
 * </ol>
 * Currently weighting schemes 1,2,3,4 &amp; 5 are implemented. The weight values
 * are taken from {@cdk.cite TOD98} and as a result 19 elements are considered.
 * <p/>
 * <p>For each weighting scheme we can obtain
 * <ul>
 * <li>11 directional WHIM descriptors (&lambda;<sub>1 .. 3</sub>, &nu;<sub>1 .. 2</sub>, &gamma;<sub>1 .. 3</sub>,  &eta;<sub>1 .. 3</sub>)
 * <li>6 non-directional WHIM descriptors (T, A, V, K, G, D)
 * </ul>
 * <p/>
 * <p>Though {@cdk.cite TOD98} mentions that for planar molecules only 8 directional WHIM
 * descriptors are required the current code will return all 11.
 * <p/>
 * The descriptor returns 17 values for a given weighting scheme, named as follows:
 * <ol>
 * <li>Wlambda1
 * <li>Wlambda2
 * <li>wlambda3
 * <li>Wnu1
 * <li>Wnu2
 * <li>Wgamma1
 * <li>Wgamma2
 * <li>Wgamma3
 * <li>Weta1
 * <li>Weta2
 * <li>Weta3
 * <li>WT
 * <li>WA
 * <li>WV
 * <li>WK
 * <li>WG
 * <li>WD
 * </ol>
 * Each name will have a suffix of the form <i>.X</i> where <i>X</i> indicates
 * the weighting scheme used. Possible values of <i>X</i> are
 * <ul>
 * <li>unity
 * <li>mass
 * <li>volume
 * <li>eneg
 * <li>polar
 * </ul>
 * <p/>
 * <p/>
 * <p>This descriptor uses these parameters:
 * <table border="1">
 * <tr>
 * <td>Name</td>
 * <td>Default</td>
 * <td>Description</td>
 * </tr>
 * <tr>
 * <td>type</td>
 * <td>unity</td>
 * <td>Type of weighting as described above</td>
 * </tr>
 * </table>
 *
 * @author Rajarshi Guha
 * @cdk.created 2004-12-1
 * @cdk.builddepends Jama-1.0.1.jar
 * @cdk.depends Jama-1.0.1.jar
 * @cdk.module qsarmolecular
 * @cdk.svnrev  $Revision$
 * @cdk.set qsar-descriptors
 * @cdk.dictref qsar-descriptors:WHIM
 * @cdk.keyword WHIM
 * @cdk.keyword descriptor
 */
public class WHIMDescriptor implements IMolecularDescriptor {

    LoggingTool logger;
    String type = "";
    Map<String,Double> hashatwt, hashvdw, hasheneg, hashpol;

    public WHIMDescriptor() {
        logger = new LoggingTool(this);
        this.type = "unity"; // default weighting scheme

        // set up the values from TOD98

        this.hashatwt = new HashMap<String,Double>();
        this.hashvdw = new HashMap<String, Double>();
        this.hasheneg = new HashMap<String, Double>();
        this.hashpol = new HashMap<String, Double>();

        this.hashatwt.put("H", new Double(0.084));
        this.hashatwt.put("B", new Double(0.900));
        this.hashatwt.put("C", new Double(1.000));
        this.hashatwt.put("N", new Double(1.166));
        this.hashatwt.put("O", new Double(1.332));
        this.hashatwt.put("F", new Double(1.582));
        this.hashatwt.put("Al", new Double(2.246));
        this.hashatwt.put("Si", new Double(2.339));
        this.hashatwt.put("P", new Double(2.579));
        this.hashatwt.put("S", new Double(2.670));
        this.hashatwt.put("Cl", new Double(2.952));
        this.hashatwt.put("Fe", new Double(4.650));
        this.hashatwt.put("Co", new Double(4.907));
        this.hashatwt.put("Ni", new Double(4.887));
        this.hashatwt.put("Cu", new Double(5.291));
        this.hashatwt.put("Zn", new Double(5.445));
        this.hashatwt.put("Br", new Double(6.653));
        this.hashatwt.put("Sn", new Double(9.884));
        this.hashatwt.put("I", new Double(10.566));

        this.hashvdw.put("H", new Double(0.299));
        this.hashvdw.put("B", new Double(0.796));
        this.hashvdw.put("C", new Double(1.000));
        this.hashvdw.put("N", new Double(0.695));
        this.hashvdw.put("O", new Double(0.512));
        this.hashvdw.put("F", new Double(0.410));
        this.hashvdw.put("Al", new Double(1.626));
        this.hashvdw.put("Si", new Double(1.424));
        this.hashvdw.put("P", new Double(1.181));
        this.hashvdw.put("S", new Double(1.088));
        this.hashvdw.put("Cl", new Double(1.035));
        this.hashvdw.put("Fe", new Double(1.829));
        this.hashvdw.put("Co", new Double(1.561));
        this.hashvdw.put("Ni", new Double(0.764));
        this.hashvdw.put("Cu", new Double(0.512));
        this.hashvdw.put("Zn", new Double(1.708));
        this.hashvdw.put("Br", new Double(1.384));
        this.hashvdw.put("Sn", new Double(2.042));
        this.hashvdw.put("I", new Double(1.728));

        this.hasheneg.put("H", new Double(0.944));
        this.hasheneg.put("B", new Double(0.828));
        this.hasheneg.put("C", new Double(1.000));
        this.hasheneg.put("N", new Double(1.163));
        this.hasheneg.put("O", new Double(1.331));
        this.hasheneg.put("F", new Double(1.457));
        this.hasheneg.put("Al", new Double(0.624));
        this.hasheneg.put("Si", new Double(0.779));
        this.hasheneg.put("P", new Double(0.916));
        this.hasheneg.put("S", new Double(1.077));
        this.hasheneg.put("Cl", new Double(1.265));
        this.hasheneg.put("Fe", new Double(0.728));
        this.hasheneg.put("Co", new Double(0.728));
        this.hasheneg.put("Ni", new Double(0.728));
        this.hasheneg.put("Cu", new Double(0.740));
        this.hasheneg.put("Zn", new Double(0.810));
        this.hasheneg.put("Br", new Double(1.172));
        this.hasheneg.put("Sn", new Double(0.837));
        this.hasheneg.put("I", new Double(1.012));

        this.hashpol.put("H", new Double(0.379));
        this.hashpol.put("B", new Double(1.722));
        this.hashpol.put("C", new Double(1.000));
        this.hashpol.put("N", new Double(0.625));
        this.hashpol.put("O", new Double(0.456));
        this.hashpol.put("F", new Double(0.316));
        this.hashpol.put("Al", new Double(3.864));
        this.hashpol.put("Si", new Double(3.057));
        this.hashpol.put("P", new Double(2.063));
        this.hashpol.put("S", new Double(1.648));
        this.hashpol.put("Cl", new Double(1.239));
        this.hashpol.put("Fe", new Double(4.773));
        this.hashpol.put("Co", new Double(4.261));
        this.hashpol.put("Ni", new Double(3.864));
        this.hashpol.put("Cu", new Double(3.466));
        this.hashpol.put("Zn", new Double(4.034));
        this.hashpol.put("Br", new Double(1.733));
        this.hashpol.put("Sn", new Double(4.375));
        this.hashpol.put("I", new Double(3.040));
    }

    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#WHIM",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }

    ;

    /**
     * Sets the parameters attribute of the WHIMDescriptor object.
     *
     * @param params The new parameter values. The Object array should have a single element
     *               which should be a String. The possible values of this String are: unity,
     *               mass, volume, eneg, polar
     * @throws CDKException if the parameters are of the wrong type
     * @see #getParameters
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length != 1) {
            throw new CDKException("WHIMDescriptor requires 1 parameter");
        }
        if (!(params[0] instanceof String)) {
            throw new CDKException("Parameters must be of type String");
        }
        this.type = (String) params[0];
        if (!this.type.equals("unity") &&
                !this.type.equals("mass") &&
                !this.type.equals("volume") &&
                !this.type.equals("eneg") &&
                !this.type.equals("polar"))
            throw new CDKException("Weighting scheme must be one of those specified in the API");
    }

    /**
     * Gets the parameters attribute of the WHIMDescriptor object.
     *
     * @return Two element array of Integer representing number of highest and lowest eigenvalues
     *         to return respectively
     * @see #setParameters
     */
    public Object[] getParameters() {
        Object[] o = new Object[1];
        o[0] = this.type;
        return (o);
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        String[] names = {
                "Wlambda1", "Wlambda2", "Wlambda3",
                "Wnu1", "Wnu2",
                "Wgamma1", "Wgamma2", "Wgamma3",
                "Weta1", "Weta2", "Weta3",
                "WT", "WA", "WV", "WK", "WG", "WD"
        };
        for (int i = 0; i < names.length; i++) names[i] += "." + type;
        return names;
    }

    /**
     * Gets the parameterNames attribute of the WHIMDescriptor object.
     *
     * @return The parameterNames value
     */
    public String[] getParameterNames() {
        String[] pname = new String[1];
        pname[0] = "type";
        return (pname);
    }


    /**
     * Gets the parameterType attribute of the WHIMDescriptor object.
     *
     * @param name Description of the Parameter
     * @return The parameterType value
     */
    public Object getParameterType(String name) {        
        return ("");
    }

    private DescriptorValue getDummyDescriptorValue(Exception e) {
         int ndesc = getDescriptorNames().length;
         DoubleArrayResult results = new DoubleArrayResult(ndesc);
         for (int i = 0; i < ndesc; i++) results.add(Double.NaN);
         return new DescriptorValue(getSpecification(), getParameterNames(),
                 getParameters(), results, getDescriptorNames(), e);
     }
    

    /**
     * Calculates 11 directional and 6 non-directional WHIM descriptors for.
     * the specified weighting scheme
     *
     * @param container Parameter is the atom container.
     * @return An ArrayList containing the descriptors in the order described above.
     */
    public DescriptorValue calculate(IAtomContainer container) {
        if (!GeometryTools.has3DCoordinates(container))
            return getDummyDescriptorValue(new CDKException("Molecule must have 3D coordinates"));

        double sum = 0.0;
        Molecule ac;

        try {
            ac = (Molecule) container.clone();
        } catch (CloneNotSupportedException e) {
            return getDummyDescriptorValue(e);
        }

        // do aromaticity detecttion for calculating polarizability later on
        //HueckelAromaticityDetector had = new HueckelAromaticityDetector();
        //had.detectAromaticity(ac);

        // get the coordinate matrix
        double[][] cmat = new double[ac.getAtomCount()][3];
        for (int i = 0; i < ac.getAtomCount(); i++) {
            Point3d coords = ac.getAtom(i).getPoint3d();            
            cmat[i][0] = coords.x;
            cmat[i][1] = coords.y;
            cmat[i][2] = coords.z;
        }

        // set up the weight vector
        Map<String,Double> hash = null;
        double[] wt = new double[ac.getAtomCount()];

        if (this.type.equals("unity")) {
            for (int i = 0; i < ac.getAtomCount(); i++) wt[i] = 1.0;
        } else {
            if (this.type.equals("mass")) {
                hash = this.hashatwt;
            } else if (this.type.equals("volume")) {
                hash = this.hashvdw;
            } else if (this.type.equals("eneg")) {
                hash = this.hasheneg;
            } else if (this.type.equals("polar")) {
                hash = this.hashpol;
            }
            for (int i = 0; i < ac.getAtomCount(); i++) {
                String sym = ac.getAtom(i).getSymbol();
                wt[i] = (Double) hash.get(sym);
            }
        }

        PCA pcaobject = null;
        try {
            pcaobject = new PCA(cmat, wt);
        } catch (CDKException cdke) {
            logger.debug(cdke);
        }

        // directional WHIM's
        double[] lambda = pcaobject.getEigenvalues();
        double[] gamma = new double[3];
        double[] nu = new double[3];
        double[] eta = new double[3];

        for (int i = 0; i < 3; i++) sum += lambda[i];
        for (int i = 0; i < 3; i++) nu[i] = lambda[i] / sum;

        double[][] scores = pcaobject.getScores();
        for (int i = 0; i < 3; i++) {
            sum = 0.0;
            for (int j = 0; j < ac.getAtomCount(); j++)
                sum += scores[j][i] * scores[j][i] * scores[j][i] * scores[j][i];
            sum = sum / (lambda[i] * lambda[i] * ac.getAtomCount());
            eta[i] = 1.0 / sum;
        }

        // look for symmetric & asymmetric atoms for the gamma descriptor
        for (int i = 0; i < 3; i++) {
            double ns = 0.0;
            double na = 0.0;
            for (int j = 0; j < ac.getAtomCount(); j++) {
                boolean foundmatch = false;
                for (int k = 0; k < ac.getAtomCount(); k++) {
                    if (k == j) continue;
                    if (scores[j][i] == -1 * scores[k][i]) {
                        ns++;
                        foundmatch = true;
                        break;
                    }
                }
                if (!foundmatch) na++;
            }
            double n = (double) ac.getAtomCount();
            gamma[i] = -1.0 * ((ns / n) * Math.log(ns / n) / Math.log(2.0) + (na / n) * Math.log(1.0 / n) / Math.log(2.0));
            gamma[i] = 1.0 / (1.0 + gamma[i]);
            //logger.debug("ns = "+ns+" na = "+na+"  gamma = "+gamma[i]);
        }

        // non directional WHIMS's
        double t = lambda[0] + lambda[1] + lambda[2];
        double a = lambda[0] * lambda[1] + lambda[0] * lambda[2] + lambda[1] * lambda[2];
        double v = t + a + lambda[0] * lambda[1] * lambda[2];

        double k = 0.0;
        sum = 0.0;
        for (int i = 0; i < 3; i++) sum += lambda[i];
        for (int i = 0; i < 3; i++) k = (lambda[i] / sum) - (1.0 / 3.0);
        k = k / (4.0 / 3.0);

        double g = Math.pow(gamma[0] * gamma[1] * gamma[2], 1.0 / 3.0);
        double d = eta[0] + eta[1] + eta[2];

        // return all the stuff we calculated
        DoubleArrayResult retval = new DoubleArrayResult(11 + 6);
        retval.add(lambda[0]);
        retval.add(lambda[1]);
        retval.add(lambda[2]);

        retval.add(nu[0]);
        retval.add(nu[1]);

        retval.add(gamma[0]);
        retval.add(gamma[1]);
        retval.add(gamma[2]);

        retval.add(eta[0]);
        retval.add(eta[1]);
        retval.add(eta[2]);

        retval.add(t);
        retval.add(a);
        retval.add(v);
        retval.add(k);
        retval.add(g);
        retval.add(d);


        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                retval, getDescriptorNames());
    }

    /**
     * Returns the specific type of the DescriptorResult object.
     * <p/>
     * The return value from this method really indicates what type of result will
     * be obtained from the {@link org.openscience.cdk.qsar.DescriptorValue} object. Note that the same result
     * can be achieved by interrogating the {@link org.openscience.cdk.qsar.DescriptorValue} object; this method
     * allows you to do the same thing, without actually calculating the descriptor.
     *
     * @return an object that implements the {@link org.openscience.cdk.qsar.result.IDescriptorResult} interface indicating
     *         the actual type of values returned by the descriptor in the {@link org.openscience.cdk.qsar.DescriptorValue} object
     */
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleArrayResultType(17);
    }


    class PCA {

        Matrix evec;
        Matrix t;
        double[] eval;


        public PCA(double[][] cmat, double[] wt) throws CDKException {

            int ncol = 3;
            int nrow = wt.length;

            if (cmat.length != wt.length) {
                throw new CDKException("WHIMDescriptor: number of weights should be equal to number of atoms");
            }

            // make a copy of the coordinate matrix
            double[][] d = new double[nrow][ncol];
            for (int i = 0; i < nrow; i++) {
                for (int j = 0; j < ncol; j++)
                    d[i][j] = cmat[i][j];
            }

            // do mean centering - though the first paper used
            // barymetric centering
            for (int i = 0; i < ncol; i++) {
                double mean = 0.0;
                for (int j = 0; j < nrow; j++)
                    mean += d[j][i];
                mean = mean / (double) nrow;
                for (int j = 0; j < nrow; j++)
                    d[j][i] = (d[j][i] - mean);
            }

            // get the covariance matrix
            double[][] covmat = new double[ncol][ncol];
            double sumwt = 0.;
            for (int i = 0; i < nrow; i++) sumwt += wt[i];
            for (int i = 0; i < ncol; i++) {
                double meanx = 0;
                for (int k = 0; k < nrow; k++) meanx += d[k][i];
                meanx = meanx / (double) nrow;
                for (int j = 0; j < ncol; j++) {
                    double meany = 0.0;
                    for (int k = 0; k < nrow; k++) meany += d[k][j];
                    meany = meany / (double) nrow;

                    double sum = 0.;
                    for (int k = 0; k < nrow; k++) {
                        //double dd =  wt[k] * (d[k][i] - meanx) * (d[k][j] - meany);
                        //logger.debug("("+i+","+j+") "+wts[k] + " * " + d[k][i] + "-" + meanx + " * " + d[k][j] + "-" + meany + "==" + dd);
                        sum += wt[k] * (d[k][i] - meanx) * (d[k][j] - meany);
                    }
                    //logger.debug(sum+" / "+sumwt+"="+sum/sumwt);
                    covmat[i][j] = sum / sumwt;
                }
            }

            // get the loadings (ie eigenvector matrix)
            Matrix m = new Matrix(covmat);
            EigenvalueDecomposition ed = m.eig();
            this.eval = ed.getRealEigenvalues();
            this.evec = ed.getV();
            Matrix x = new Matrix(d);
            this.t = x.times(this.evec);
        }

        double[] getEigenvalues() {
            return (this.eval);
        }

        double[][] getScores() {
            return (t.getArray());
        }
    }

}


