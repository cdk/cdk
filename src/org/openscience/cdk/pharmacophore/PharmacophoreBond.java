package org.openscience.cdk.pharmacophore;

import org.openscience.cdk.Bond;

/**
 * Represents a distance relationship between two pharmacophore groups.
 *
 * @author Rajarshi Guha
 * @cdk.module pcore
 * @cdk.keywords pharmacophore
 * @cdk.keywords 3D isomorphism
 * @see org.openscience.cdk.pharmacophore.PharmacophoreAtom
 */

public class PharmacophoreBond extends Bond {

    /**
     * Create a pharmacophore distance constraint.
     *
     * @param patom1 The first pharmacophore group
     * @param patom2 The second pharmacophore group
     */
    public PharmacophoreBond(PharmacophoreAtom patom1, PharmacophoreAtom patom2) {
        super(patom1, patom2);
    }

    /**
     * Get the distance between the two pharmacophore groups that make up the constraint.
     *
     * @return The distance between the two groups
     */
    public double getBondLength() {
        PharmacophoreAtom atom1 = (PharmacophoreAtom) getAtom(0);
        PharmacophoreAtom atom2 = (PharmacophoreAtom) getAtom(1);
        return atom1.getPoint3d().distance(atom2.getPoint3d());
    }


}
