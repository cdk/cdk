package org.openscience.cdk.smsd.algorithm.vflib;

/* Copyright (C) 2009-2010  Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * MX Cheminformatics Tools for Java
 *
 * Copyright (c) 2007-2009 Metamolecular, LLC
 *
 * http://metamolecular.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
import org.openscience.cdk.Bond;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * query and target molecules.
 * @cdk.module test-smsd
 * @cdk.githash
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 */
public class Molecules {

    public static IAtomContainer create4Toluene() throws CDKException {
        IAtomContainer result = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c1.setID("1");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c2.setID("2");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c3.setID("3");
        IAtom c4 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c4.setID("4");
        IAtom c5 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c5.setID("5");
        IAtom c6 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c6.setID("6");
        IAtom c7 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c7.setID("7");

        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);
        result.addAtom(c4);
        result.addAtom(c5);
        result.addAtom(c6);
        result.addAtom(c7);

        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.DOUBLE);
        IBond bond3 = new Bond(c3, c4, IBond.Order.SINGLE);
        IBond bond4 = new Bond(c4, c5, IBond.Order.DOUBLE);
        IBond bond5 = new Bond(c5, c6, IBond.Order.SINGLE);
        IBond bond6 = new Bond(c6, c1, IBond.Order.DOUBLE);
        IBond bond7 = new Bond(c7, c4, IBond.Order.SINGLE);

        result.addBond(bond1);
        result.addBond(bond2);
        result.addBond(bond3);
        result.addBond(bond4);
        result.addBond(bond5);
        result.addBond(bond6);
        result.addBond(bond7);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(result.getBuilder());
        adder.addImplicitHydrogens(result);
        Aromaticity.cdkLegacy().apply(result);

        return result;
    }

    public static IAtomContainer createMethane() throws CDKException {
        IAtomContainer result = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        result.addAtom(c1);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(result.getBuilder());
        adder.addImplicitHydrogens(result);

        return result;
    }

    public static IAtomContainer createPropane() throws CDKException {
        IAtomContainer result = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom c1 = result.getBuilder().newInstance(IAtom.class, "C");
        IAtom c2 = result.getBuilder().newInstance(IAtom.class, "C");
        IAtom c3 = result.getBuilder().newInstance(IAtom.class, "C");

        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);

        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.SINGLE);

        result.addBond(bond1);
        result.addBond(bond2);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(result.getBuilder());
        adder.addImplicitHydrogens(result);
        Aromaticity.cdkLegacy().apply(result);

        SmilesGenerator sg = new SmilesGenerator();
        String oldSmiles = sg.create(result);
        System.out.println("Propane " + oldSmiles);

        return result;
    }

    public static IAtomContainer createHexane() throws CDKException {
        IAtomContainer result = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);

        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c1.setID("1");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c2.setID("2");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c3.setID("3");
        IAtom c4 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c4.setID("4");
        IAtom c5 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c5.setID("5");
        IAtom c6 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c6.setID("6");

        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);
        result.addAtom(c4);
        result.addAtom(c5);
        result.addAtom(c6);

        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.SINGLE);
        IBond bond3 = new Bond(c3, c4, IBond.Order.SINGLE);
        IBond bond4 = new Bond(c4, c5, IBond.Order.SINGLE);
        IBond bond5 = new Bond(c5, c6, IBond.Order.SINGLE);

        result.addBond(bond1);
        result.addBond(bond2);
        result.addBond(bond3);
        result.addBond(bond4);
        result.addBond(bond5);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(result.getBuilder());
        adder.addImplicitHydrogens(result);
        Aromaticity.cdkLegacy().apply(result);

        SmilesGenerator sg = new SmilesGenerator();
        String oldSmiles = sg.create(result);
        System.out.println("Hexane " + oldSmiles);

        return result;
    }

    public static IAtomContainer createBenzene() throws CDKException {
        IAtomContainer result = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);

        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c1.setID("1");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c2.setID("2");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c3.setID("3");
        IAtom c4 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c4.setID("4");
        IAtom c5 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c5.setID("5");
        IAtom c6 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c6.setID("6");

        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);
        result.addAtom(c4);
        result.addAtom(c5);
        result.addAtom(c6);

        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.DOUBLE);
        IBond bond3 = new Bond(c3, c4, IBond.Order.SINGLE);
        IBond bond4 = new Bond(c4, c5, IBond.Order.DOUBLE);
        IBond bond5 = new Bond(c5, c6, IBond.Order.SINGLE);
        IBond bond6 = new Bond(c6, c1, IBond.Order.DOUBLE);

        result.addBond(bond1);
        result.addBond(bond2);
        result.addBond(bond3);
        result.addBond(bond4);
        result.addBond(bond5);
        result.addBond(bond6);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(result.getBuilder());
        adder.addImplicitHydrogens(result);
        Aromaticity.cdkLegacy().apply(result);

        SmilesGenerator sg = new SmilesGenerator();
        String oldSmiles = sg.create(result);
        System.out.println("Benzene " + oldSmiles);

        return result;
    }

    public static IAtomContainer createNaphthalene() throws CDKException {
        IAtomContainer result = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);

        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c1.setID("1");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c2.setID("2");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c3.setID("3");
        IAtom c4 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c4.setID("4");
        IAtom c5 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c5.setID("5");
        IAtom c6 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c6.setID("6");
        IAtom c7 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c4.setID("7");
        IAtom c8 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c5.setID("8");
        IAtom c9 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c6.setID("9");
        IAtom c10 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c6.setID("10");

        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);
        result.addAtom(c4);
        result.addAtom(c5);
        result.addAtom(c6);
        result.addAtom(c7);
        result.addAtom(c8);
        result.addAtom(c9);
        result.addAtom(c10);

        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.DOUBLE);
        IBond bond3 = new Bond(c3, c4, IBond.Order.SINGLE);
        IBond bond4 = new Bond(c4, c5, IBond.Order.DOUBLE);
        IBond bond5 = new Bond(c5, c6, IBond.Order.SINGLE);
        IBond bond6 = new Bond(c6, c1, IBond.Order.DOUBLE);
        IBond bond7 = new Bond(c5, c7, IBond.Order.SINGLE);
        IBond bond8 = new Bond(c7, c8, IBond.Order.DOUBLE);
        IBond bond9 = new Bond(c8, c9, IBond.Order.SINGLE);
        IBond bond10 = new Bond(c9, c10, IBond.Order.DOUBLE);
        IBond bond11 = new Bond(c10, c6, IBond.Order.SINGLE);

        result.addBond(bond1);
        result.addBond(bond2);
        result.addBond(bond3);
        result.addBond(bond4);
        result.addBond(bond5);
        result.addBond(bond6);
        result.addBond(bond7);
        result.addBond(bond8);
        result.addBond(bond9);
        result.addBond(bond10);
        result.addBond(bond11);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(result.getBuilder());
        adder.addImplicitHydrogens(result);
        Aromaticity.cdkLegacy().apply(result);

        SmilesGenerator sg = new SmilesGenerator();
        String oldSmiles = sg.create(result);
        System.out.println("Naphthalene " + oldSmiles);

        return result;
    }

    public static IAtomContainer createPyridazine() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String smiles = "C1=CN=NC=C1";
        IAtomContainer result = sp.parseSmiles(smiles);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(result.getBuilder());
        adder.addImplicitHydrogens(result);
        Aromaticity.cdkLegacy().apply(result);
        return result;
    }

    public static IAtomContainer createChloroisoquinoline4() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String smiles = "ClC1=CC=NC2=C1C=CC=C2";
        IAtomContainer result = sp.parseSmiles(smiles);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(result.getBuilder());
        adder.addImplicitHydrogens(result);
        Aromaticity.cdkLegacy().apply(result);
        return result;
    }

    public static IAtomContainer createChlorobenzene() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String smiles = "Clc1ccccc1";
        IAtomContainer result = sp.parseSmiles(smiles);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(result.getBuilder());
        adder.addImplicitHydrogens(result);
        Aromaticity.cdkLegacy().apply(result);
        return result;
    }

    public static IAtomContainer createAcetone() throws CDKException {
        IAtomContainer result = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);

        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c1.setID("1");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c2.setID("2");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c3.setID("3");
        IAtom c4 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");
        c4.setID("4");

        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);
        result.addAtom(c4);

        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.SINGLE);
        IBond bond3 = new Bond(c3, c4, IBond.Order.DOUBLE);

        result.addBond(bond1);
        result.addBond(bond2);
        result.addBond(bond3);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(result.getBuilder());
        adder.addImplicitHydrogens(result);
        Aromaticity.cdkLegacy().apply(result);

        return result;
    }

    //
    //    public static Molecule createNeopentane() {
    //        Molecule result = new DefaultMolecule();
    //        Atom c0 = result.addAtom("C");
    //        Atom c1 = result.addAtom("C");
    //        Atom c2 = result.addAtom("C");
    //        Atom c3 = result.addAtom("C");
    //        Atom c4 = result.addAtom("C");
    //
    //        result.connect(c0, c1, 1);
    //        result.connect(c0, c2, 1);
    //        result.connect(c0, c3, 1);
    //        result.connect(c0, c4, 1);
    //
    //        return result;
    //    }
    //

    public static IAtomContainer createCubane() throws CDKException {
        IAtomContainer result = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c1.setID("1");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c2.setID("2");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c3.setID("3");
        IAtom c4 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c4.setID("4");
        IAtom c5 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c5.setID("5");
        IAtom c6 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c6.setID("6");
        IAtom c7 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c7.setID("7");
        IAtom c8 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c8.setID("8");

        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);
        result.addAtom(c4);
        result.addAtom(c5);
        result.addAtom(c6);
        result.addAtom(c7);
        result.addAtom(c8);

        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.SINGLE);
        IBond bond3 = new Bond(c3, c4, IBond.Order.SINGLE);
        IBond bond4 = new Bond(c4, c1, IBond.Order.SINGLE);

        IBond bond5 = new Bond(c5, c6, IBond.Order.SINGLE);
        IBond bond6 = new Bond(c6, c7, IBond.Order.SINGLE);
        IBond bond7 = new Bond(c7, c8, IBond.Order.SINGLE);
        IBond bond8 = new Bond(c8, c5, IBond.Order.SINGLE);

        IBond bond9 = new Bond(c1, c5, IBond.Order.SINGLE);
        IBond bond10 = new Bond(c2, c6, IBond.Order.SINGLE);
        IBond bond11 = new Bond(c3, c7, IBond.Order.SINGLE);
        IBond bond12 = new Bond(c4, c8, IBond.Order.SINGLE);

        result.addBond(bond1);
        result.addBond(bond2);
        result.addBond(bond3);
        result.addBond(bond4);
        result.addBond(bond5);
        result.addBond(bond6);
        result.addBond(bond7);
        result.addBond(bond8);
        result.addBond(bond9);
        result.addBond(bond10);
        result.addBond(bond11);
        result.addBond(bond12);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(result.getBuilder());
        adder.addImplicitHydrogens(result);
        Aromaticity.cdkLegacy().apply(result);

        return result;
    }

    //
    //    public static Molecule createBicyclo220hexane() {
    //        Molecule result = new DefaultMolecule();
    //        Atom c0 = result.addAtom("C");
    //        Atom c1 = result.addAtom("C");
    //        Atom c2 = result.addAtom("C");
    //        Atom c3 = result.addAtom("C");
    //        Atom c4 = result.addAtom("C");
    //        Atom c5 = result.addAtom("C");
    //
    //        result.connect(c0, c1, 1);
    //        result.connect(c1, c2, 1);
    //        result.connect(c2, c3, 1);
    //        result.connect(c3, c4, 1);
    //        result.connect(c4, c5, 1);
    //        result.connect(c5, c0, 1);
    //        result.connect(c2, c5, 1);
    //
    //        return result;
    //    }
    //
    //    public static Molecule createEthylbenzeneWithSuperatom() {
    //        Molecule result = Molecules.createBenzene();
    //        Atom carbon1 = result.addAtom("C");
    //        Atom carbon2 = result.addAtom("C");
    //        Bond crossingBond = result.connect(result.getAtom(0), carbon1, 1);
    //        result.connect(carbon1, carbon2, 1);
    //
    //        Superatom substructure = result.addSuperatom();
    //        substructure.addAtom(carbon1);
    //        substructure.addAtom(carbon2);
    //        substructure.addCrossingBond(crossingBond);
    //        substructure.setCrossingVector(crossingBond, 0.1, 0.1);
    //        substructure.setLabel("Ethyl");
    //
    //        return result;
    //    }
    //

    public static IAtomContainer createPyridine() throws CDKException {
        IAtomContainer result = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);

        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c1.setID("1");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c2.setID("2");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c3.setID("3");
        IAtom c4 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c4.setID("4");
        IAtom c5 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c5.setID("5");
        IAtom c6 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "N");
        c6.setID("6");

        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);
        result.addAtom(c4);
        result.addAtom(c5);
        result.addAtom(c6);

        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.DOUBLE);
        IBond bond3 = new Bond(c3, c4, IBond.Order.SINGLE);
        IBond bond4 = new Bond(c4, c5, IBond.Order.DOUBLE);
        IBond bond5 = new Bond(c5, c6, IBond.Order.SINGLE);
        IBond bond6 = new Bond(c6, c1, IBond.Order.DOUBLE);

        result.addBond(bond1);
        result.addBond(bond2);
        result.addBond(bond3);
        result.addBond(bond4);
        result.addBond(bond5);
        result.addBond(bond6);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(result.getBuilder());
        adder.addImplicitHydrogens(result);
        Aromaticity.cdkLegacy().apply(result);

        return result;
    }

    public static IAtomContainer createToluene() throws CDKException {
        IAtomContainer result = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);

        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c1.setID("1");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c2.setID("2");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c3.setID("3");
        IAtom c4 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c4.setID("4");
        IAtom c5 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c5.setID("5");
        IAtom c6 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c6.setID("6");
        IAtom c7 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c6.setID("7");

        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);
        result.addAtom(c4);
        result.addAtom(c5);
        result.addAtom(c6);
        result.addAtom(c7);

        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.DOUBLE);
        IBond bond3 = new Bond(c3, c4, IBond.Order.SINGLE);
        IBond bond4 = new Bond(c4, c5, IBond.Order.DOUBLE);
        IBond bond5 = new Bond(c5, c6, IBond.Order.SINGLE);
        IBond bond6 = new Bond(c6, c1, IBond.Order.DOUBLE);
        IBond bond7 = new Bond(c7, c1, IBond.Order.SINGLE);

        result.addBond(bond1);
        result.addBond(bond2);
        result.addBond(bond3);
        result.addBond(bond4);
        result.addBond(bond5);
        result.addBond(bond6);
        result.addBond(bond7);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(result.getBuilder());
        adder.addImplicitHydrogens(result);
        Aromaticity.cdkLegacy().apply(result);

        return result;
    }

    public static IAtomContainer createPhenol() throws CDKException {

        IAtomContainer result = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);

        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c1.setID("1");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c2.setID("2");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c3.setID("3");
        IAtom c4 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c4.setID("4");
        IAtom c5 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c5.setID("5");
        IAtom c6 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c6.setID("6");
        IAtom c7 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");
        c6.setID("7");

        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);
        result.addAtom(c4);
        result.addAtom(c5);
        result.addAtom(c6);
        result.addAtom(c7);

        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.DOUBLE);
        IBond bond3 = new Bond(c3, c4, IBond.Order.SINGLE);
        IBond bond4 = new Bond(c4, c5, IBond.Order.DOUBLE);
        IBond bond5 = new Bond(c5, c6, IBond.Order.SINGLE);
        IBond bond6 = new Bond(c6, c1, IBond.Order.DOUBLE);
        IBond bond7 = new Bond(c7, c1, IBond.Order.SINGLE);

        result.addBond(bond1);
        result.addBond(bond2);
        result.addBond(bond3);
        result.addBond(bond4);
        result.addBond(bond5);
        result.addBond(bond6);
        result.addBond(bond7);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(result.getBuilder());
        adder.addImplicitHydrogens(result);
        Aromaticity.cdkLegacy().apply(result);

        return result;
    }

    public static IAtomContainer createCyclohexane() throws CDKException {

        IAtomContainer result = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);

        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c1.setID("1");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c2.setID("2");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c3.setID("3");
        IAtom c4 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c4.setID("4");
        IAtom c5 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c5.setID("5");
        IAtom c6 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c6.setID("6");

        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);
        result.addAtom(c4);
        result.addAtom(c5);
        result.addAtom(c6);

        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.SINGLE);
        IBond bond3 = new Bond(c3, c4, IBond.Order.SINGLE);
        IBond bond4 = new Bond(c4, c5, IBond.Order.SINGLE);
        IBond bond5 = new Bond(c5, c6, IBond.Order.SINGLE);
        IBond bond6 = new Bond(c6, c1, IBond.Order.SINGLE);

        result.addBond(bond1);
        result.addBond(bond2);
        result.addBond(bond3);
        result.addBond(bond4);
        result.addBond(bond5);
        result.addBond(bond6);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(result.getBuilder());
        adder.addImplicitHydrogens(result);
        Aromaticity.cdkLegacy().apply(result);

        return result;

    }

    public static IAtomContainer createCyclopropane() throws CDKException {
        IAtomContainer result = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);

        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");

        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);

        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.SINGLE);
        IBond bond3 = new Bond(c3, c1, IBond.Order.SINGLE);

        result.addBond(bond1);
        result.addBond(bond2);
        result.addBond(bond3);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(result.getBuilder());
        adder.addImplicitHydrogens(result);
        Aromaticity.cdkLegacy().apply(result);

        return result;
    }

    public static IAtomContainer createIsobutane() throws CDKException {
        IAtomContainer result = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);

        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom c4 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");

        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);
        result.addAtom(c4);

        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.SINGLE);
        IBond bond3 = new Bond(c2, c4, IBond.Order.SINGLE);

        result.addBond(bond1);
        result.addBond(bond2);
        result.addBond(bond3);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(result.getBuilder());
        adder.addImplicitHydrogens(result);
        Aromaticity.cdkLegacy().apply(result);

        return result;
    }

    //    private IAtomContainer createBenzaldehyde() {
    //        Molecule result = new DefaultMolecule();
    //        Atom c1 = result.addAtom("C");
    //        Atom c2 = result.addAtom("C");
    //        Atom c3 = result.addAtom("C");
    //        Atom c4 = result.addAtom("C");
    //        Atom c5 = result.addAtom("C");
    //        Atom c6 = result.addAtom("C");
    //        Atom c7 = result.addAtom("C");
    //        Atom o8 = result.addAtom("O");
    //
    //        result.connect(c1, c2, 1);
    //        result.connect(c2, c3, 2);
    //        result.connect(c3, c4, 1);
    //        result.connect(c4, c5, 2);
    //        result.connect(c5, c6, 1);
    //        result.connect(c6, c1, 2);
    //        result.connect(c7, c1, 1);
    //        result.connect(c7, o8, 2);
    //
    //        return result;
    //    }
    //
    //    private IAtomContainer createBenzoicAcid() {
    //        Molecule result = createBenzaldehyde();
    //
    //        result.connect(result.getAtom(6), result.addAtom("O"), 1);
    //
    //        return result;
    //    }
    //
    //    private IAtomContainer createBlockedBenzaldehyde() {
    //        Molecule result = createBenzaldehyde();
    //
    //        result.connect(result.getAtom(6), result.addAtom("H"), 1);
    //
    //        return result;
    //    }
    //    private Molecule create4Toluene() {
    //        Molecule result = new DefaultMolecule();
    //        Atom c1 = result.addAtom("C");
    //        Atom c2 = result.addAtom("C");
    //        Atom c3 = result.addAtom("C");
    //        Atom c4 = result.addAtom("C");
    //        Atom c5 = result.addAtom("C");
    //        Atom c6 = result.addAtom("C");
    //        Atom c7 = result.addAtom("C");
    //
    //        result.connect(c1, c2, 1);
    //        result.connect(c2, c3, 2);
    //        result.connect(c3, c4, 1);
    //        result.connect(c4, c5, 2);
    //        result.connect(c5, c6, 1);
    //        result.connect(c6, c1, 2);
    //        result.connect(c7, c4, 1);
    //
    //        return result;
    //    }
    public static IAtomContainer createSimpleImine() throws CDKException {
        IAtomContainer result = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);

        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "N");

        result.addAtom(c1);
        result.addAtom(c2);

        IBond bond = new Bond(c1, c2, IBond.Order.DOUBLE);
        result.addBond(bond);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(result.getBuilder());
        adder.addImplicitHydrogens(result);
        Aromaticity.cdkLegacy().apply(result);

        SmilesGenerator sg = new SmilesGenerator();
        String oldSmiles = sg.create(result);
        System.out.println("SimpleImine " + oldSmiles);

        return result;
    }

    public static IAtomContainer createSimpleAmine() throws CDKException {
        IAtomContainer result = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);

        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "N");

        result.addAtom(c1);
        result.addAtom(c2);

        IBond bond = new Bond(c1, c2, IBond.Order.SINGLE);
        result.addBond(bond);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(result.getBuilder());
        adder.addImplicitHydrogens(result);
        Aromaticity.cdkLegacy().apply(result);

        SmilesGenerator sg = new SmilesGenerator();
        String oldSmiles = sg.create(result);
        System.out.println("SimpleAmine " + oldSmiles);

        return result;
    }
}
