package org.openscience.cdk.pharmacophore;

import org.openscience.cdk.Bond;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;

/**
 * Represents a pharmacophore query distance constraint.
 *
 * @author Rajarshi Guha
 * @cdk.module pcore
 * @cdk.svnrev  $Revision: 9162 $
 * @cdk.keyword pharmacophore
 * @cdk.keyword 3D isomorphism
 * @see org.openscience.cdk.pharmacophore.PharmacophoreQueryAtom
 * @see org.openscience.cdk.pharmacophore.PharmacophoreMatcher
 * @see org.openscience.cdk.isomorphism.matchers.QueryAtomContainer
 */
public class PharmacophoreQueryBond extends Bond implements IQueryBond {
    private PharmacophoreQueryAtom[] atoms;
    private double upper;
    private double lower;

    public PharmacophoreQueryBond() {
    }

    /**
     * Create a query distance constraint between two query groups.
     *
     * @param atom1 The first pharmacophore group
     * @param atom2 The second pharmacophore group
     * @param lower The lower bound of the distance between the two groups
     * @param upper The upper bound of the distance between the two groups
     * @see #PharmacophoreQueryBond(PharmacophoreQueryAtom,PharmacophoreQueryAtom,double)
     */
    public PharmacophoreQueryBond(IAtom atom1,
                                  IAtom atom2,
                                  double lower, double upper) {
        super(atom1, atom2);
        this.upper = upper;
        this.lower = lower;
    }

    /**
     * Create a query distance constraint between two query groups.
     * <p/>
     * This constructor allows you to define a query distance constraint
     * such that the distance between the two query groups is exact
     * (i.e., not a range)
     *
     * @param atom1    The first pharmacophore group
     * @param atom2    The second pharmacophore group
     * @param distance The exact distance between the two groups
     * @see #PharmacophoreQueryBond(org.openscience.cdk.interfaces.IAtom,org.openscience.cdk.interfaces.IAtom,double,double)
     */
    public PharmacophoreQueryBond(PharmacophoreQueryAtom atom1,
                                  PharmacophoreQueryAtom atom2,
                                  double distance) {
        super(atom1, atom2);
        this.upper = distance;
        this.lower = distance;
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
    public boolean matches(IBond bond) {        
        PharmacophoreBond pbond = (PharmacophoreBond) bond;
        double bondLength = round(pbond.getBondLength(), 2);
        return bondLength >= lower && bondLength <= upper;
    }

    private double round(double val, int places) {
        long factor = (long) Math.pow(10, places);
        val = val * factor;
        long tmp = Math.round(val);
        return (double) tmp / factor;
    }

}
