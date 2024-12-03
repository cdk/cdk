/*
 * Copyright (C) 2024 John Mayfield
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.openscience.cdk.tautomer;

import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;

import static org.junit.jupiter.api.Assertions.*;

class SayleDelanyStateTest {

    IAtomContainer fromSmi(String smi) throws InvalidSmilesException {
        IAtomContainer mol = new SmilesParser(SilentChemObjectBuilder.getInstance()).parseSmiles(smi);
        Cycles.markRingAtomsAndBonds(mol);
        return mol;
    }

    @Test
    void testBondOrderSetting() throws CDKException {
        IAtomContainer mol = fromSmi("O=C1C=CNC=C1CCCN1CC=CC1");
        SayleDelanyState state = new SayleDelanyState(mol);
        System.err.println(toSmi(state.container()));
        state.add(mol.getBond(0), IBond.Order.DOUBLE);
        System.err.println(toSmi(state.container()));
        state.remove(mol.getBond(0));
        System.err.println(toSmi(state.container()));
        state.add(mol.getBond(1), IBond.Order.SINGLE);
        System.err.println(toSmi(state.container()));
    }

    String toSmi(IAtomContainer container) throws CDKException {
        for (IBond bond : container.bonds())
            if (bond.getOrder() == null)
                bond.setOrder(IBond.Order.QUADRUPLE);
        String res = new SmilesGenerator(SmiFlavor.Generic + SmiFlavor.AtomAtomMap).create(container);
        for (IBond bond : container.bonds())
            if (bond.getOrder() == IBond.Order.QUADRUPLE)
                bond.setOrder(null);
        return res;
    }
}