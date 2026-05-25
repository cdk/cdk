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
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import static org.junit.jupiter.api.Assertions.*;

class TautSubSearch2Test {

    @Test
    public void testBenzene() throws InvalidSmilesException {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
//        IAtomContainer mol = smipar.parseSmiles("CCCN1CC(NS(=O)(=O)N(CC)CC)CC2Cc3c(O)cccc3CC21.Cl");
        IAtomContainer mol = smipar.parseSmiles("O=C1c2ccccc2C(=O)c3c1cc(NS(=O)(=O)O)c(O)c3O");
        System.err.println(new TautSubSearch2(bldr, "O=c1ccccc1").matches(mol));
    }

    @Test
    public void testPhenol() throws InvalidSmilesException {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        IAtomContainer mol = smipar.parseSmiles("Cc1ccc2c(-c3nc4ccccc4[nH]3)cc(=O)oc2c1");
        System.err.println(new TautSubSearch2(bldr, "Oc1ccccc1").matches(mol));
    }

    @Test
    public void testPhenone() throws InvalidSmilesException {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        IAtomContainer mol = smipar.parseSmiles("C=C(C)/C=C/c1ccc2oc3c(O)cc(OC)c(CC=O)c3c(=O)c2c1O");
        System.err.println(new TautSubSearch4(bldr, "O=c1ccccc1").matches(mol));
    }

    @Test
    public void testPhenone_mem() throws InvalidSmilesException {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        IAtomContainer mol = smipar.parseSmiles("O=C1c2c(O)cc(O)cc2O[C@H](c3ccc(O)c(O)c3)[C@H]1O CHEMBL66");
        System.err.println(new TautSubSearch4(bldr, "O=c1ccccc1").matches(mol));
    }

    @Test
    public void testPhenone_null() throws InvalidSmilesException {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        IAtomContainer mol = smipar.parseSmiles("O=c1cc(-c2ccccc2)oc3cc(O)cc(O)c13 CHEMBL117");
        System.err.println(new TautSubSearch4(bldr, "O=c1ccccc1").matches(mol));
    }

    @Test
    public void testPhenone_null2() throws InvalidSmilesException {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        IAtomContainer mol = smipar.parseSmiles("COc1ccc2c(c1)OC[C@H]3[C@@H]2C4=C(OC3(C)C)C5=C(C(=O)C4=O)[C@H]6c7ccc(OC)cc7OC[C@H]6C(C)(C)O5 CHEMBL1");
        System.err.println(new TautSubSearch4(bldr, "O=c1ccccc1").matches(mol));
    }

    @Test
    public void testPhenone_null3() throws InvalidSmilesException {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        IAtomContainer mol = smipar.parseSmiles("Oc1ccc2ncnc(Nc3ccc(OCc4ccccc4)cc3)c2c1 CHEMBL14932");
        System.err.println(new TautSubSearch4(bldr, "O=c1ccccc1").matches(mol));
    }

    @Test
    public void testPhenone_shouldMatch() throws InvalidSmilesException {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        IAtomContainer mol = smipar.parseSmiles("Oc1ccc(-c2n[nH]c3cc(O)ccc23)cc1");
        System.err.println(new TautSubSearch4(bldr, "O=c1ccccc1").matches(mol));
    }

    @Test
    public void testPhenone_shouldMatch2() throws InvalidSmilesException {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        IAtomContainer mol = smipar.parseSmiles("COC(=O)c1c-2nc3ccc(N(C)C)cc3oc2c(O)c(=O)c1N4CCCCC4");
        System.err.println(new TautSubSearch4(bldr, "O=c1ccccc1").matches(mol));
    }
}