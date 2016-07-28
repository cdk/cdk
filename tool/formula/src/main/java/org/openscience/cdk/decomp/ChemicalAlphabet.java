/*
 *  This file is part of the SIRIUS library for analyzing MS and MS/MS data
 *
 *  Copyright (C) 2013-2015 Kai Dührkop
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with SIRIUS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openscience.cdk.decomp;

import org.openscience.cdk.formula.MolecularFormulaRange;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;

import java.util.Arrays;

/**
 * Implements the {@link Alphabet} for chemical elements.
 */
public class ChemicalAlphabet implements Alphabet<IIsotope> {

    /**
     * Is used to convert compomeres to IMolecularFormula instances
     */
    protected final IChemObjectBuilder objectBuilder;

    /**
     * The characters (chemical elements) of the alphabet
     */
    protected final IIsotope[] characters;

    /**
     * Construct a new chemical alphabet from the given search space using the given object builder.
     *
     * @param molecularFormulaRange Search spacel, defining the allowed elements
     */
    public ChemicalAlphabet(IChemObjectBuilder builder, MolecularFormulaRange molecularFormulaRange) {
        this.objectBuilder = builder;
        IIsotope[] chars = new IIsotope[molecularFormulaRange.getIsotopeCount()];
        int k=0;
        for (IIsotope i : molecularFormulaRange.isotopes()) {
            if (molecularFormulaRange.getIsotopeCountMax(i) > 0) chars[k++] = i;
        }
        if (k < chars.length) chars = Arrays.copyOf(chars, k);
        this.characters = chars;
    }

    /**
     * Translates a compomere (multiset of characters) into a IMolecularFormula
     */
    public IMolecularFormula buildFormulaFromCompomere(int[] compomere, int[] orderedIndizes) {
        IMolecularFormula formula = objectBuilder.newInstance(IMolecularFormula.class);
        for (int k=0; k < orderedIndizes.length; ++k) {
            if (compomere[k] > 0) formula.addIsotope(characters[orderedIndizes[k]], compomere[k]);
        }
        return formula;
    }

    /**
     * Checks if two chemical alphabets are compatible. In theory, an alphabet would be compatible if it is a subset
     * of another alphabet. However, we directly check for equality to keep this operation symetric.
     *
     * A decomposer can decompose every mass with alphabet as long as the alphabet is compatible to the decomposers
     * own alphabet.
     */
    public boolean isCompatible(ChemicalAlphabet other) {
        return Arrays.equals(characters, other.characters);
    }

    @Override
    public int size() {
        return characters.length;
    }

    @Override
    public double weightOf(int i) {
        return characters[i].getExactMass();
    }

    @Override
    public IIsotope get(int i) {
        return characters[i];
    }

    /**
     * maps each character to its index. This operation is quite slow, but have to be done only once when starting the
     * decomposer. Therefore, we don't need a hash table here.
     */
    @Override
    public int indexOf(IIsotope character) {
        for (int k=0; k < characters.length; ++k)
            if (characters[k]==character) return k;
        return -1;
    }
}
