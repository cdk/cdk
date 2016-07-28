package org.openscience.cdk.formula;

import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;

interface IFormulaGenerator {
    public IMolecularFormula getNextFormula();
    public IMolecularFormulaSet getAllFormulas();
    public double getFinishedPercentage();
    public void cancel();
}
