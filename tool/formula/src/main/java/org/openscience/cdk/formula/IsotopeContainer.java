package org.openscience.cdk.formula;

import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 * This class defines a isotope container. It contains in principle a
 * IMolecularFormula, a mass and intensity/abundance value.
 *
 * @author Miguel Rojas Cherto
 *
 * @cdk.module  formula
 * @cdk.githash
 */
public class IsotopeContainer {

    private IMolecularFormula form;
    private double            masOs;
    private double            inte;

    /**
     * Constructor of the IsotopeContainer object.
     */
    public IsotopeContainer() {

    }

    /**
     * Constructor of the IsotopeContainer object setting a IMolecularFormula
     * object and intensity value.
     *
     * @param formula        The formula of this container
     * @param intensity      The intensity of this container
     */
    public IsotopeContainer(IMolecularFormula formula, double intensity) {
        form = formula;
        if (formula != null) masOs = MolecularFormulaManipulator.getTotalExactMass(formula);
        inte = intensity;
    }

    /**
     * Constructor of the IsotopeContainer object setting a mass
     *  and intensity value.
     *
     * @param mass           The mass of this container
     * @param intensity      The intensity of this container
     */
    public IsotopeContainer(double mass, double intensity) {
        masOs = mass;
        inte = intensity;
    }

    /**
     * Set IMolecularFormula object of this container.
     *
     * @param formula The IMolecularFormula of the this container
     */
    public void setFormula(IMolecularFormula formula) {
        form = formula;
    }

    /**
     * Set the mass value of this container.
     *
     * @param mass The mass of the this container
     */
    public void setMass(double mass) {
        masOs = mass;
    }

    /**
     * Set the intensity value of this container.
     *
     * @param intensity The intensity of the this container
     */
    public void setIntensity(double intensity) {
        inte = intensity;
    }

    /**
     * Get the IMolecularFormula object of this container.
     *
     * @return The IMolecularformula of the this container
     */
    public IMolecularFormula getFormula() {
        return form;
    }

    /**
     * Get the mass value of this container.
     *
     * @return The mass of the this container
     */
    public double getMass() {
        return masOs;
    }

    /**
     * Get the intensity value of this container.
     *
     * @return The intensity of the this container
     */
    public double getIntensity() {
        return inte;
    }

    /**
     * Clones this IsotopeContainer object and its content.
     *
     * @return    The cloned object
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        IsotopeContainer isoClone = new IsotopeContainer();
        isoClone.setFormula(getFormula());
        isoClone.setIntensity(getIntensity());
        isoClone.setMass(getMass());
        return isoClone;
    }

}
