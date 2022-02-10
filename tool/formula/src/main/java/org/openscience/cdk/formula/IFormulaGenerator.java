package org.openscience.cdk.formula;

import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;

interface IFormulaGenerator {
    IMolecularFormula getNextFormula();
    IMolecularFormulaSet getAllFormulas();
    double getFinishedPercentage();
    void cancel();
}
