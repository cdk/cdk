package org.openscience.cdk.formula;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines the properties of a deisotoped
 * pattern distribution. A isotope pattern is a set of
 * compounds with different set of isotopes.
 *
 * @author Miguel Rojas Cherto
 *
 * @cdk.module formula
 * @cdk.githash
 */
public class IsotopePattern {

    private List<IsotopeContainer> isotopeCList = new ArrayList<IsotopeContainer>();

    private int                    monoIsotopePosition;

    private double                 chargI       = 0;

    /**
     * Constructor of the IsotopePattern object.
     */
    public IsotopePattern() {

    }

    /**
     * Set the mono isotope object. Adds the isoContainer to the isotope 
     *                  pattern, if it is not already added. 
     *
     *  @param isoContainer   The IsotopeContainer object
     */
    public void setMonoIsotope(IsotopeContainer isoContainer) {
        if (!isotopeCList.contains(isoContainer)) 
            isotopeCList.add(isoContainer);
        monoIsotopePosition = isotopeCList.indexOf(isoContainer);
    }

    /**
     * Add an isotope object.
     *
     *  @param isoContainer   The IsotopeContainer object
     */
    public void addIsotope(IsotopeContainer isoContainer) {
        isotopeCList.add(isoContainer);
    }

    /**
     * Returns the mono-isotope peak that form this isotope pattern.
     *
     * @return The IsotopeContainer acting as mono-isotope
     */
    public IsotopeContainer getMonoIsotope() {
        return isotopeCList.get(monoIsotopePosition);
    }

    /**
     * Returns the all isotopes that form this isotope pattern.
     *
     * @return The IsotopeContainer acting as mono-isotope
     */
    public List<IsotopeContainer> getIsotopes() {
        return isotopeCList;
    }

    /**
     * Returns the a isotopes given a specific position.
     *
     * @param  position position of the isotope to return
     * @return The isotope
     */
    public IsotopeContainer getIsotope(int position) {
        return isotopeCList.get(position);
    }

    /**
     * Returns the number of isotopes in this pattern.
     *
     * @return The number of isotopes
     */
    public int getNumberOfIsotopes() {
        return isotopeCList.size();
    }

    /**
     * Set the charge in this pattern.
     *
     * @param charge The charge value
     */
    public void setCharge(double charge) {
        chargI = charge;

    }

    /**
     * Get the charge in this pattern.
     *
     * @return The charge value
     */
    public double getCharge() {
        return chargI;

    }

    /**
     * Clones this IsotopePattern object and its content.
     *
     * @return    The cloned object
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        IsotopePattern isoClone = new IsotopePattern();
        IsotopeContainer isoHighest = getMonoIsotope();
        for (IsotopeContainer isoContainer : isotopeCList) {
            if (isoHighest.equals(isoContainer))
                isoClone.setMonoIsotope((IsotopeContainer) isoContainer.clone());
            else
                isoClone.addIsotope((IsotopeContainer) isoContainer.clone());
        }
        isoClone.setCharge(getCharge());
        return isoClone;
    }
}
