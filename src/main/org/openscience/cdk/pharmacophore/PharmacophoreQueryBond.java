package org.openscience.cdk.pharmacophore;

import org.openscience.cdk.Bond;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;

/**
 * Represents a pharmacophore query distance constraint.
 *
 * @author Rajarshi Guha
 * @cdk.module pcore
 * @cdk.svnrev  $Revision$
 * @cdk.keyword pharmacophore
 * @cdk.keyword 3D isomorphism
 * @see org.openscience.cdk.pharmacophore.PharmacophoreQueryAtom
 * @see org.openscience.cdk.pharmacophore.PharmacophoreMatcher
 * @see org.openscience.cdk.isomorphism.matchers.QueryAtomContainer
 */
@TestClass("org.openscience.cdk.pharmacophore.PharmacophoreQueryBondTest")
public class PharmacophoreQueryBond extends Bond implements IQueryBond {
    private double upper;
    private double lower;

    public PharmacophoreQueryBond() {
    }

    /**
     * Create a query distance constraint between two query groups.
     *
     * Note that the distance is only considered upto 2 decimal places.
     *
     * @param atom1 The first pharmacophore group
     * @param atom2 The second pharmacophore group
     * @param lower The lower bound of the distance between the two groups
     * @param upper The upper bound of the distance between the two groups
     * @see #PharmacophoreQueryBond(PharmacophoreQueryAtom,PharmacophoreQueryAtom,double)
     */
    public PharmacophoreQueryBond(PharmacophoreQueryAtom atom1,
                                  PharmacophoreQueryAtom atom2,
                                  double lower, double upper) {
        super(atom1, atom2);
        this.upper = round(upper,2);
        this.lower = round(lower, 2);
    }

    /**
     * Create a query distance constraint between two query groups.
     * <p/>
     * This constructor allows you to define a query distance constraint
     * such that the distance between the two query groups is exact
     * (i.e., not a range).
     *
     * Note that the distance is only considered upto 2 decimal places.
     *
     * @param atom1    The first pharmacophore group
     * @param atom2    The second pharmacophore group
     * @param distance The exact distance between the two groups
     * @see #PharmacophoreQueryBond(PharmacophoreQueryAtom, PharmacophoreQueryAtom, double, double)
     */
    public PharmacophoreQueryBond(PharmacophoreQueryAtom atom1,
                                  PharmacophoreQueryAtom atom2,
                                  double distance) {
        super(atom1, atom2);
        this.upper = round(distance,2);
        this.lower = round(distance, 2);
    }

    /**
     * Checks whether the query distance constraint matches a target distance.
     * <p/>
     * This method checks whether a query constraint is satisfied by an observed
     * distance (represented by a {@link PharmacophoreBond} in the target molecule.
     * Note that distance are compared upto 2 decimal places.
     *
     * @param bond The distance relationship in a target molecule
     * @return true if the target distance lies within the range of the query constraint
     */
    @TestMethod("testMatches")
    public boolean matches(IBond bond) {
        if (bond instanceof PharmacophoreBond) {
        PharmacophoreBond pbond = (PharmacophoreBond) bond;
        double bondLength = round(pbond.getBondLength(), 2);
        return bondLength >= lower && bondLength <= upper;
        } else return false;
    }

    public double getUpper() {
        return upper;
    }

    public double getLower() {
        return lower;
    }

    private double round(double val, int places) {
        long factor = (long) Math.pow(10, places);
        val = val * factor;
        long tmp = Math.round(val);
        return (double) tmp / factor;
    }

}
