package org.openscience.cdk.pharmacophore;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;

/**
 * Represents a query pharmacophore group.
 * <p/>
 * This class is meant to be used to construct pharmacophore queries in conjunction
 * with {@link org.openscience.cdk.pharmacophore.PharmacophoreQueryBond} and an
 * {@link org.openscience.cdk.isomorphism.matchers.QueryAtomContainer}.
 *
 * @author Rajarshi Guha
 * @cdk.module pcore
 * @cdk.keyword pharmacophore
 * @cdk.keyword 3D isomorphism
 * @see org.openscience.cdk.pharmacophore.PharmacophoreQueryBond
 * @see org.openscience.cdk.isomorphism.matchers.QueryAtomContainer
 * @see org.openscience.cdk.pharmacophore.PharmacophoreMatcher
 */
public class PharmacophoreQueryAtom extends Atom implements IQueryAtom {
    private String smarts;

    /**
     * Creat a new query pharmacophore group
     *
     * @param symbol The symbol for the group
     * @param smarts The SMARTS pattern to be used for matching
     */
    public PharmacophoreQueryAtom(String symbol, String smarts) {
        setSymbol(symbol);
        this.smarts = smarts;
    }

    /**
     * Get the SMARTS pattern for this pharmacophore group.
     *
     * @return The SMARTS pattern
     */
    public String getSmarts() {
        return smarts;
    }

    /**
     * Checks whether this query atom matches a target atom.
     * <p/>
     * Currently a query atom will match a target pharmacophore group if the
     * symbols of the two groups match. This is based on the assumption that
     * pharmacophore groups with the same symbol will have the same SMARTS
     * pattern.
     *
     * @param atom A target pharmacophore group
     * @return true if the current query group has the same symbol as the target group
     */
    public boolean matches(IAtom atom) {
        PharmacophoreAtom patom = (PharmacophoreAtom) atom;
        return patom.getSymbol().equals(getSymbol());
    }

    public void setOperator(String ID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
