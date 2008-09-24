/*
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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

package org.openscience.cdk.geometry.alignment;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import org.openscience.cdk.Atom;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.LoggingTool;

import javax.vecmath.Point3d;

/**
 * Aligns two structures to minimize the RMSD using the Kabsch algorithm.
 *
 * <p>This class is an implementation of the Kabsch algorithm ({@cdk.cite KAB76}, {@cdk.cite KAB78})
 * and evaluates the optimal rotation matrix (U) to minimize the RMSD between the two structures.
 * Since the algorithm assumes that the number of points are the same in the two structures
 * it is the job of the caller to pass the proper number of atoms from the two structures. Constructors
 * which take whole <code>AtomContainer</code>'s are provided but they should have the same number
 * of atoms.
 * The algorithm allows for the use of atom weightings and by default all points are given a weight of 1.0
 *
 * <p>Example usage can be:
 * <pre>
 * AtomContainer ac1, ac2;
 *
 * try {
 *    KabschAlignment sa = new KabschAlignment(ac1.getAtoms(),ac2.getAtoms());
 *    sa.align();
 *    System.out.println(sa.getRMSD());
 * } catch (CDKException e){}
 * </pre>
 * In many cases, molecules will be aligned based on some common substructure.
 * In this case the center of masses calculated during alignment refer to these
 * substructures rather than the whole molecules. To superimpose the molecules
 * for display, the second molecule must be rotated and translated by calling
 * <code>rotateAtomContainer</code>. However, since this will also translate the
 * second molecule, the first molecule should also be translated to the center of mass
 * of the substructure specifed for this molecule. This center of mass can be obtained
 * by a call to <code>getCenterOfMass</code> and then manually translating the coordinates.
 * Thus an example would be
 * <pre>
 * AtomContainer ac1, ac2;  // whole molecules
 * Atom[] a1, a2;           // some subset of atoms from the two molecules
 * KabschAlignment sa;
 * 
 * try {
 *    sa = new KabschAlignment(a1,a2);
 *    sa.align();
 * } catch (CDKException e){}
 *
 * Point3d cm1 = sa.getCenterOfMass();
 * for (int i = 0; i &lt; ac1.getAtomCount(); i++) {
 *    Atom a = ac1.getAtomAt(i);
 *    a.setX3d( a.getPoint3d().x - cm1.x );
 *    a.setY3d( a.getPoint3d().y - cm1.y );
 *    a.setY3d( a.getPoint3d().z - cm1.z );
 * }
 * sa.rotateAtomContainer(ac2);
 *
 * // display the two AtomContainer's
 *</pre>
 * 
 * @author           Rajarshi Guha
 * @cdk.created      2004-12-11
 * @cdk.builddepends Jama-1.0.1.jar
 * @cdk.depends      Jama-1.0.1.jar
 * @cdk.dictref      blue-obelisk:alignmentKabsch
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.geometry.alignment.KabschAlignmentTest")
public class KabschAlignment {

	private LoggingTool logger = new LoggingTool(KabschAlignment.class);
	
    private double[][] U;
    private double rmsd = -1.0;
    private Point3d[] p1,p2,rp; // rp are the rotated coordinates
    private double[] wts;
    private int npoint;
    private Point3d cm1, cm2;
    private double[] atwt1, atwt2;
    
    private Point3d[] getPoint3dArray(org.openscience.cdk.interfaces.IAtom[] a) {
        Point3d[] p = new Point3d[ a.length ];
        for (int i = 0; i < a.length; i++) {
            p[i] = new Point3d( a[i].getPoint3d() );
        }
        return(p);
    }

    private Point3d[] getPoint3dArray(org.openscience.cdk.interfaces.IAtomContainer ac) {
        Point3d[] p = new Point3d[ ac.getAtomCount() ];
        for (int i = 0; i < ac.getAtomCount(); i++) {
            p[i] = new Point3d( ac.getAtom(i).getPoint3d() );
        }
        return(p);
    }
    
    private double[] getAtomicMasses(org.openscience.cdk.interfaces.IAtom[] a) {
        double[] am = new double[a.length];
        IsotopeFactory factory = null;
        try {
            factory = IsotopeFactory.getInstance(a[0].getBuilder());
        } catch (Exception e) {
        	logger.error("Error while instantiating the isotope factory: ",
        		e.getMessage());
            logger.debug(e);
        }

        assert factory != null;
        for (int i = 0; i < a.length; i++) {
            am[i] = factory.getMajorIsotope( a[i].getSymbol() ).getExactMass();
        }
        return(am);
    }
    
    private double[] getAtomicMasses(org.openscience.cdk.interfaces.IAtomContainer ac) {
        double[] am = new double[ac.getAtomCount()];
        IsotopeFactory factory = null;
        try {
            factory = IsotopeFactory.getInstance(ac.getAtom(0).getBuilder());
        } catch (Exception e) {
        	logger.error("Error while instantiating the isotope factory: ",
            	e.getMessage());
        	logger.debug(e);
        }

        assert factory != null;
        for (int i = 0; i < ac.getAtomCount(); i++) {
            am[i] = factory.getMajorIsotope( ac.getAtom(i).getSymbol() ).getExactMass();
        }
        return(am);
    }

    private Point3d getCenterOfMass(Point3d[] p, double[] atwt) {
        double x = 0.;
        double y = 0.;
        double z = 0.;
        double totalmass = 0.;
        for (int i = 0; i  < p.length; i++) {
            x += atwt[i]*p[i].x;
            y += atwt[i]*p[i].y;
            z += atwt[i]*p[i].z;
            totalmass += atwt[i];
        }
        return( new Point3d(x/totalmass, y/totalmass, z/totalmass) );
    }
        

    /**
     * Sets up variables for the alignment algorithm.
     *
     * The algorithm allows for atom weighting and the default is 1.0 for all
     * atoms.
     *
     * @param al1 An array of {@link Atom} objects
     * @param al2 An array of {@link Atom} objects. This array will have its coordinates rotated
     *            so that the RMDS is minimzed to the coordinates of the first array
     * @throws CDKException if the number of Atom's are not the same in the two arrays
     */            
    public KabschAlignment(Atom[] al1, Atom[] al2) throws CDKException {
        if (al1.length != al2.length) {
            throw new CDKException("The Atom[]'s being aligned must have the same numebr of atoms");
        }
        this.npoint = al1.length;
        this.p1 = getPoint3dArray(al1);
        this.p2 = getPoint3dArray(al2);
        this.wts = new double[this.npoint];

        this.atwt1 = getAtomicMasses(al1);
        this.atwt2 = getAtomicMasses(al2);

        for (int i = 0; i < this.npoint; i++) this.wts[i] = 1.0;
    }
    /**
     * Sets up variables for the alignment algorithm.
     *
     * @param al1 An array of {@link Atom} objects
     * @param al2 An array of {@link Atom} objects. This array will have its coordinates rotated
     *            so that the RMDS is minimzed to the coordinates of the first array
     * @param wts A vector atom weights.           
     * @throws CDKException if the number of Atom's are not the same in the two arrays or
     *                         length of the weight vector is not the same as the Atom arrays
     */            
    public KabschAlignment(Atom[] al1, Atom[] al2, double[] wts) throws CDKException {
        if (al1.length != al2.length) {
            throw new CDKException("The Atom[]'s being aligned must have the same number of atoms");
        }
        if (al1.length != wts.length) {
            throw new CDKException("Number of weights must equal number of atoms");
        }
        this.npoint = al1.length;
        this.p1 = getPoint3dArray(al1);
        this.p2 = getPoint3dArray(al2);
        this.wts = new double[this.npoint];
        System.arraycopy(wts, 0, this.wts, 0, this.npoint);

        this.atwt1 = getAtomicMasses(al1);
        this.atwt2 = getAtomicMasses(al2);
    }

    /**
     * Sets up variables for the alignment algorithm.
     *
     * The algorithm allows for atom weighting and the default is 1.0 for all
     * atoms.
     *
     * @param ac1 An {@link IAtomContainer}
     * @param ac2 An {@link IAtomContainer}. This AtomContainer will have its coordinates rotated
     *            so that the RMDS is minimzed to the coordinates of the first one
     * @throws CDKException if the number of atom's are not the same in the two AtomContainer's
     */
    @TestMethod("testAlign")
    public KabschAlignment(IAtomContainer ac1, IAtomContainer ac2) throws CDKException {
        if (ac1.getAtomCount() != ac2.getAtomCount()) {
            throw new CDKException("The AtomContainer's being aligned must have the same number of atoms");
        }
        this.npoint = ac1.getAtomCount();
        this.p1 = getPoint3dArray(ac1);
        this.p2 = getPoint3dArray(ac2);
        this.wts = new double[npoint];
        for (int i = 0; i < npoint; i++) this.wts[i] = 1.0;

        this.atwt1 = getAtomicMasses(ac1);
        this.atwt2 = getAtomicMasses(ac2);
    }

    /**
     * Sets up variables for the alignment algorithm.
     *
     * @param ac1 An {@link IAtomContainer}
     * @param ac2 An {@link IAtomContainer}. This AtomContainer will have its coordinates rotated
     *            so that the RMDS is minimzed to the coordinates of the first one
     * @param wts A vector atom weights.           
     * @throws CDKException if the number of atom's are not the same in the two AtomContainer's or
     *                         length of the weight vector is not the same as number of atoms.
     */            
    public KabschAlignment(IAtomContainer ac1, IAtomContainer ac2, double[] wts) throws CDKException {
        if (ac1.getAtomCount() != ac2.getAtomCount()) {
            throw new CDKException("The AtomContainer's being aligned must have the same number of atoms");
        }
        if (ac1.getAtomCount() != wts.length) {
            throw new CDKException("Number of weights must equal number of atoms");
        }
        this.npoint = ac1.getAtomCount();
        this.p1 = getPoint3dArray(ac1);
        this.p2 = getPoint3dArray(ac2);
        this.wts = new double[npoint];
        System.arraycopy(wts, 0, this.wts, 0, npoint);

        this.atwt1 = getAtomicMasses(ac1);
        this.atwt2 = getAtomicMasses(ac2);
    }

    /**
     * Perform an alignment.
     *
     * This method aligns to set of atoms which should have been specified
     * prior to this call
     */
    @TestMethod("testAlign")
    public void align() {
        
        Matrix tmp;

       // get center of gravity and translate both to 0,0,0 
        this.cm1 = new Point3d();
        this.cm2 = new Point3d();

        this.cm1 = getCenterOfMass(p1, atwt1);
        this.cm2 = getCenterOfMass(p2, atwt2);

        // move the points 
        for (int i = 0; i < this.npoint; i++) {
            p1[i].x = p1[i].x - this.cm1.x;
            p1[i].y = p1[i].y - this.cm1.y;
            p1[i].z = p1[i].z - this.cm1.z;

            p2[i].x = p2[i].x - this.cm2.x;
            p2[i].y = p2[i].y - this.cm2.y;
            p2[i].z = p2[i].z - this.cm2.z;
        }
        
        // get the R matrix 
        double[][] tR = new double[3][3];
        for (int i = 0; i < this.npoint; i++) {
            wts[i] = 1.0;
        }
        for (int i = 0; i < this.npoint; i++) {
            tR[0][0] += p1[i].x * p2[i].x * wts[i];
            tR[0][1] += p1[i].x * p2[i].y * wts[i];
            tR[0][2] += p1[i].x * p2[i].z * wts[i];

            tR[1][0] += p1[i].y * p2[i].x * wts[i];
            tR[1][1] += p1[i].y * p2[i].y * wts[i];
            tR[1][2] += p1[i].y * p2[i].z * wts[i];

            tR[2][0] += p1[i].z * p2[i].x * wts[i];
            tR[2][1] += p1[i].z * p2[i].y * wts[i];
            tR[2][2] += p1[i].z * p2[i].z * wts[i];
        }
        double[][] R = new double[3][3];
        tmp = new Matrix(tR);
        R = tmp.transpose().getArray();


        // now get the RtR (=R'R) matrix 
        double[][] RtR = new double[3][3];
        Matrix jamaR = new Matrix(R);
        tmp = tmp.times(jamaR);
        RtR = tmp.getArray();

        // get eigenvalues of RRt (a's)
        Matrix jamaRtR = new Matrix(RtR);
        EigenvalueDecomposition ed = jamaRtR.eig();
        double[] mu = ed.getRealEigenvalues();
        double[][] a = ed.getV().getArray();


 
        // Jama returns the eigenvalues in increasing order so
        // swap the eigenvalues and vectors
        double tmp2 = mu[2];
        mu[2] = mu[0];
        mu[0] = tmp2;
        for (int i = 0; i < 3; i++) {
            tmp2 = a[i][2];
            a[i][2] = a[i][0];
            a[i][0] = tmp2;
        }

        // make sure that the a3 = a1 x a2
        a[0][2] = (a[1][0]*a[2][1]) - (a[1][1]*a[2][0]);
        a[1][2] = (a[0][1]*a[2][0]) - (a[0][0]*a[2][1]);
        a[2][2] = (a[0][0]*a[1][1]) - (a[0][1]*a[1][0]);

        // lets work out the b vectors
        double[][] b = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    b[i][j] += R[i][k] * a[k][j];
                }
                b[i][j] = b[i][j] / Math.sqrt(mu[j]);
            }
        }
        
        // normalize and set b3 = b1 x b2
        double norm1 = 0.;
        double norm2 = 0.;
        for (int i = 0; i < 3; i++) {
            norm1 += b[i][0]*b[i][0];
            norm2 += b[i][1]*b[i][1];
        }
        norm1 = Math.sqrt(norm1);
        norm2 = Math.sqrt(norm2);
        for (int i = 0; i < 3; i++) {
            b[i][0] = b[i][0] / norm1;
            b[i][1] = b[i][1] / norm2;
        }
        b[0][2] = (b[1][0]*b[2][1]) - (b[1][1]*b[2][0]);
        b[1][2] = (b[0][1]*b[2][0]) - (b[0][0]*b[2][1]);
        b[2][2] = (b[0][0]*b[1][1]) - (b[0][1]*b[1][0]);

        // get the rotation matrix
        double[][] tU = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    tU[i][j] += b[i][k]*a[j][k];
                }
            }
        }

        // take the transpose
        U = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                U[i][j] = tU[j][i];
            }
        }

        // now eval the RMS error
        // first, rotate the second set of points and ...
        this.rp = new Point3d[ this.npoint ];
        for (int i = 0; i < this.npoint; i++) {
            this.rp[i] = new Point3d(
                    U[0][0]*p2[i].x + U[0][1]*p2[i].y + U[0][2]*p2[i].z,
                    U[1][0]*p2[i].x + U[1][1]*p2[i].y + U[1][2]*p2[i].z,
                    U[2][0]*p2[i].x + U[2][1]*p2[i].y + U[2][2]*p2[i].z
                    );
        }

        // ... then eval rms
        double rms = 0.;
        for (int i = 0; i < this.npoint; i++) {
            rms += (p1[i].x-this.rp[i].x)*(p1[i].x-this.rp[i].x) +
                (p1[i].y-this.rp[i].y)*(p1[i].y-this.rp[i].y) +
                (p1[i].z-this.rp[i].z)*(p1[i].z-this.rp[i].z);
        }
        this.rmsd = Math.sqrt(rms / this.npoint);
    }

    /**
     * Returns the RMSD from the alignment.
     *
     * If align() has not been called the return value is -1.0
     *
     * @return The RMSD for this alignment
     * @see #align
     */
    @TestMethod("testAlign")
    public double getRMSD() {
        return(this.rmsd);
    }

    /**
     * Returns the rotation matrix (u).
     *
     * @return A double[][] representing the rotation matrix
     * @see #align
     */
    @TestMethod("testAlign")
    public double[][] getRotationMatrix() {
        return(this.U);
    }

    /**
     * Returns the center of mass for the first molecule or fragment used in the calculation.
     *
     * This method is useful when using this class to align the coordinates
     * of two molecules and them displaying them superimposed. Since the center of
     * mass used during the alignment may not be based on the whole molecule (in 
     * general common substructures are aligned), when preparing molecules for display
     * the first molecule should be translated to the center of mass. Then displaying the
     * first molecule and the rotated version of the second one will result in superimposed
     * structures.
     * 
     * @return A Point3d containing the coordinates of the center of mass
     */
    public Point3d getCenterOfMass() {
        return(this.cm1);
    }

    /**
     * Rotates the {@link IAtomContainer} coordinates by the rotation matrix.
     *
     * In general if you align a subset of atoms in a AtomContainer
     * this function can be applied to the whole AtomContainer to rotate all
     * atoms. This should be called with the second AtomContainer (or Atom[])
     * that was passed to the constructor.
     *
     * Note that the AtomContainer coordinates also get translated such that the
     * center of mass of the original fragment used to calculate the alignment is at the origin.
     *
     * @param ac The {@link IAtomContainer} whose coordinates are to be rotated
     */
    public void rotateAtomContainer(IAtomContainer ac)  {
        Point3d[] p = getPoint3dArray( ac );
        for (int i = 0; i < ac.getAtomCount(); i++) {
            // translate the the origin we have calculated
            p[i].x = p[i].x - this.cm2.x;
            p[i].y = p[i].y - this.cm2.y;
            p[i].z = p[i].z - this.cm2.z;

            // do the actual rotation
            ac.getAtom(i).setPoint3d( 
            	new Point3d(
            		U[0][0]*p[i].x + U[0][1]*p[i].y + U[0][2]*p[i].z,
            		U[1][0]*p[i].x + U[1][1]*p[i].y + U[1][2]*p[i].z,
            		U[2][0]*p[i].x + U[2][1]*p[i].y + U[2][2]*p[i].z
            	)
            );
        }
    }

 }

