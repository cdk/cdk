/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.charges.Polarizability;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

/**
 * Eigenvalue based descriptor noted for its utility in chemical diversity.
 * Described by Pearlman et al. {@cdk.cite PEA99}.
 * 
 * <p>The descriptor is based on a weighted version of the Burden matrix {@cdk.cite BUR89, BUR97}
 * which takes into account both the connectivity as well as atomic
 * properties of a molecule. The weights are a variety of atom properties placed along the 
 * diagonal of the Burden matrix. Currently three weighting schemes are employed
 * <ul>
 * <li>atomic weight
 * <li>partial charge (Gasteiger Marsilli)
 * <li>polarizability {@cdk.cite KJ81}
 * </ul>
 * <p>By default, the descriptor will return the highest and lowest eigenvalues for the three
 * classes of descriptor in a single ArrayList (in the order shown above). However it is also
 * possible to supply a parameter list indicating how many of the highest and lowest eigenvalues
 * (for each class of descriptor) are required.
 * 
 * <p>The descriptor works with the hydrogen depleted molecule and thus the maximum number
 * of eigenvalues calculated for any class of BCUT descriptor is equal to the number
 * of heavy atoms present.
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>nhigh</td>
 *     <td>1</td>
 *     <td>The number of highest eigenvalue</td>
 *   </tr>
 *   <tr>
 *     <td>nlow</td>
 *     <td>1</td>
 *     <td>The number of lowest eigenvalue</td>
 *   </tr>
 * </table>
 *
 * 
 * @author      Rajarshi Guha
 * @cdk.created     2004-11-30
 * 
 * @cdk.builddepends Jama-1.0.1.jar
 * @cdk.depends Jama-1.0.1.jar
 *
 * @cdk.module qsar
 * @cdk.set    qsar-descriptors
 * @cdk.dictref qsar-descriptors:BCUT
 * 
 * @cdk.keyword BCUT
 * @cdk.keyword descriptor
 */
public class BCUTDescriptor implements IMolecularDescriptor {

    // the number of negative & positive eigenvalues
    // to return for each class of BCUT descriptor
    private int nhigh;
    private int nlow;
    private boolean checkAromaticity = true;
    
    public BCUTDescriptor() {
        // set the default number of BCUT's
        this.nhigh = 1;
        this.nlow = 1;
    }

    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#BCUT",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    };

    /**
     *  Sets the parameters attribute of the BCUTDescriptor object.
     *
     *@param  params            The new parameter values. This descriptor takes 3 parameters: number of highest
     *                          eigenvalues and number of lowest eigenvalues. If 0 is specified for either (the default)
     *                          then all calculated eigenvalues are returned. The third parameter checkAromaticity is a boolean.
     *                          If checkAromaticity is true, the method check the aromaticity, if false, means that the aromaticity has
	 *  						already been checked.
     *@throws  CDKException  if the parameters are of the wrong type
     *@see #getParameters
     */
    public void setParameters(Object[] params) throws CDKException {
        // we expect 3 parameters
        if (params.length != 3) {
            throw new CDKException("BCUTDescriptor requires 3 parameters");
        }
        if (!(params[0] instanceof Integer) || !(params[1] instanceof Integer)) {
            throw new CDKException("Parameters must be of type Integer");
        }else if(!(params[2] instanceof Boolean)) {
        	throw new CDKException("The third parameter must be of type Boolean");
        }
        // ok, all should be fine
               
        this.nhigh = ((Integer)params[0]).intValue();
        this.nlow = ((Integer)params[1]).intValue();
        this.checkAromaticity = ((Boolean) params[2]).booleanValue();

        if (this.nhigh < 0 || this.nlow < 0) {
            throw new CDKException("Number of eigenvalues to return must be positive or 0");
        }
    }

    /**
     *  Gets the parameters attribute of the BCUTDescriptor object.
     *
     *@return    Three element array of Integer and one boolean representing number of highest and lowest eigenvalues and the checkAromaticity flag
     *           to return respectively
     *@see #setParameters
     */
    public Object[] getParameters() {
        Object params[] = new Object[3];
        params[0] = new Integer(this.nhigh);
        params[1] = new Integer(this.nlow);
        params[2] = new Boolean(this.checkAromaticity);
        return(params);
    }
    /**
     *  Gets the parameterNames attribute of the BCUTDescriptor object.
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        String[] params = new String[3];
        params[0] = "nhigh";
        params[1] = "nlow";
        params[2] = "checkAromaticity";
        return(params);
    }


    /**
     *  Gets the parameterType attribute of the BCUTDescriptor object.
     *
     *@param  name  Description of the Parameter (can be either 'nhigh' or 'nlow' or checkAromaticity)
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
    Object o = null;
        if (name.equals("nhigh")) o = new Integer(1);
        if (name.equals("nlow")) o = new Integer(1);
        if (name.equals("checkAromaticity")) o = new Integer(1);
        return(o);
    }


    static private class BurdenMatrix {
        
        static double[][] evalMatrix(IAtomContainer ac, double[] vsd) {
            IAtomContainer local = AtomContainerManipulator.removeHydrogens(ac);

            int natom = local.getAtomCount();
            double[][] m = new double[natom][natom];

            /* set the off diagonal entries */
            for (int i = 0; i < natom-1; i++) {
                for (int j = i+1; j < natom; j++) {
                    for (int k = 0; k < local.getBondCount(); k++) {
                    	org.openscience.cdk.interfaces.IBond b = local.getBondAt(k);
                        if (b.contains(local.getAtomAt(i)) && b.contains(local.getAtomAt(j))) {
                            if (b.getOrder() == CDKConstants.BONDORDER_SINGLE) m[i][j] = 0.1;
                            else if (b.getOrder() == CDKConstants.BONDORDER_DOUBLE) m[i][j] = 0.2;
                            else if (b.getOrder() == CDKConstants.BONDORDER_TRIPLE) m[i][j] = 0.3;
                            else if (b.getOrder() == CDKConstants.BONDORDER_AROMATIC) m[i][j] = 0.15;

                            if (local.getBondCount(i) == 1 || local.getBondCount(j) == 1) {
                                m[i][j] += 0.01;
                            }
                            m[j][i] = m[i][j];
                        } else {
                            m[i][j] = 0.001;
                            m[j][i] = 0.001;
                        }
                    }
                }
            }

            /* set the diagonal entries */
            for (int i = 0; i < natom; i++) {
                if (vsd != null) m[i][i] = vsd[i];
                else m[i][i] = 0.0;
            }
            return(m);
        }
    }
    
   /**
     *  Calculates the three classes of BCUT descriptors.
     *
     *@param  container  Parameter is the atom container.
     *@return            An ArrayList containing the descriptors. The default is to return
     *                   all calculated eigenvalues of the Burden matrices in the order described
     *                   above. If a parameter list was supplied, then only the specified number
     *                   of highest and lowest eigenvalues (for each class of BCUT) will be returned.
     *@throws CDKException if the wrong number of eigenvalues are requested (negative or more than the number
     * of heavy atoms)
     */
    public DescriptorValue calculate(IAtomContainer container) throws CDKException {
        int j = 0;
        Molecule ac = new Molecule(container);

        // add H's in case they're not present
        HydrogenAdder ha = new HydrogenAdder();
        try {
            ha.addExplicitHydrogensToSatisfyValency(ac);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        // do aromaticity detecttion for calculating polarizability later on
        if (this.checkAromaticity) {
        	HueckelAromaticityDetector.detectAromaticity(ac);
        }

        // find number of heavy atoms
        int nheavy = 0;
        for (int i = 0; i < ac.getAtomCount(); i++) {
            if (ac.getAtomAt(i).getSymbol().equals("H")) continue;
            else nheavy++;
        }

        if (this.nhigh > nheavy || this.nlow > nheavy) {
            throw new CDKException("Number of negative or positive eigenvalues cannot be more than number of heavy atoms");
        }

        double[] diagvalue = new double[ nheavy ];

        // get atomic mass weighted BCUT
        j = 0;
        try {
            for (int i = 0; i < ac.getAtomCount(); i++) {
                if (ac.getAtomAt(i).getSymbol().equals("H")) continue;
                diagvalue[j] = IsotopeFactory.getInstance(ac.getBuilder()).
                    getMajorIsotope( ac.getAtomAt(i).getSymbol() ).getExactMass();
                j++;        
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        double[][]  bm = BurdenMatrix.evalMatrix(ac, diagvalue);
        Matrix m = new Matrix(bm);
        EigenvalueDecomposition ed = new EigenvalueDecomposition(m);
        double[] eval1 = ed.getRealEigenvalues();


        // get charge weighted BCUT
        GasteigerMarsiliPartialCharges peoe = null;
        try {
            peoe = new GasteigerMarsiliPartialCharges();
            peoe.assignGasteigerMarsiliSigmaPartialCharges(ac, true);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        j = 0;
        for (int i = 0; i < ac.getAtomCount(); i++) {
            if (ac.getAtomAt(i).getSymbol().equals("H")) continue;
            diagvalue[j] = ac.getAtomAt(i).getCharge();
            j++;
        }
        bm = BurdenMatrix.evalMatrix(ac, diagvalue);
        m = new Matrix(bm);
        ed = new EigenvalueDecomposition(m);
        double[] eval2 = ed.getRealEigenvalues();

        // get polarizability weighted BCUT
        Polarizability pol = new Polarizability();
        j = 0;
        for (int i =0 ; i < ac.getAtomCount(); i++) {
            if (ac.getAtomAt(i).getSymbol().equals("H")) continue;
            diagvalue[j] = pol.calculateGHEffectiveAtomPolarizability(ac, ac.getAtomAt(i), 1000);
            j++;
        }
        bm = BurdenMatrix.evalMatrix(ac, diagvalue);
        m = new Matrix(bm);
        ed = new EigenvalueDecomposition(m);
        double[] eval3 = ed.getRealEigenvalues();

        DoubleArrayResult retval = new DoubleArrayResult( eval1.length + eval2.length + eval3.length );

        if (nhigh == 0 || nlow == 0) {
            // return all calculated eigenvalues
            for (int i = 0; i < eval1.length; i++) retval.add( eval1[i] );
            for (int i = 0; i < eval2.length; i++) retval.add( eval2[i] );
            for (int i = 0; i < eval3.length; i++) retval.add( eval3[i] );
        } else {
            // return only the n highest & lowest eigenvalues
            for (int i = 0; i < nlow; i++) retval.add( eval1[i] );
            for (int i = 0; i < nhigh; i++) retval.add( eval1[ eval1.length-i-1 ] );
        
            for (int i = 0; i < nlow; i++) retval.add( eval2[i] );
            for (int i = 0; i < nhigh; i++) retval.add( eval2[ eval2.length-i-1 ] );
        
            for (int i = 0; i < nlow; i++) retval.add( eval3[i] );
            for (int i = 0; i < nhigh; i++) retval.add( eval3[ eval3.length-i-1 ] );
        }
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), retval);
    }
}


