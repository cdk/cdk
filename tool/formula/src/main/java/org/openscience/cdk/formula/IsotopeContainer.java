package org.openscience.cdk.formula;

import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private List<IMolecularFormula> forms = new ArrayList<>();
    private double                  masOs;
    private double                  inte;

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
        forms.add(formula);
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

    public IsotopeContainer(IsotopeContainer container) {
        masOs = container.masOs;
        inte  = container.inte;
        forms = new ArrayList<>(container.forms);
    }

    /**
     * Set IMolecularFormula object of this container.
     *
     * @param formula The IMolecularFormula of the this container
     */
    public void setFormula(IMolecularFormula formula) {
        forms.clear();
        forms.add(formula);
    }

    /**
     * Add a formula to this isotope container.
     * @param formula the new formula
     */
    public void addFormula(IMolecularFormula formula) {
        this.forms.add(formula);
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
        return forms.isEmpty() ? null : forms.get(0);
    }

    /**
     * Access the formulas of this isotope container.
     * @return the formulas
     */
    public List<IMolecularFormula> getFormulas() {
        return Collections.unmodifiableList(forms);
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
        isoClone.forms.addAll(getFormulas());
        isoClone.setIntensity(getIntensity());
        isoClone.setMass(getMass());
        return isoClone;
    }

    /**
     * Pretty-print the MFs of this isotope container.
     * @return the MFs
     */
    String getFormulasString() {
        StringBuilder sb = new StringBuilder();
        for (IMolecularFormula mf : getFormulas()) {
            if (sb.length() != 0)
                sb.append(", ");
            sb.append(MolecularFormulaManipulator.getString(mf, false, true));
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "IsotopeContainer{" +
               "mass=" + masOs +
               ", intensity=" + inte +
               ", MF=" + getFormulasString() +
               '}';
    }
}
