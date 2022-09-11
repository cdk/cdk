/*
 * Copyright (C) 2018  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smiles.smarts;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.OrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.SymbolAndChargeQueryAtom;
import org.openscience.cdk.isomorphism.matchers.SymbolQueryAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyOrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.ImplicitHCountAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.LogicalOperatorAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.MassAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.smiles.smarts.parser.SMARTSParser;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @cdk.module  test-smarts
 * @cdk.require java1.4+
 */
class SMARTSTest extends CDKTestCase {

    private UniversalIsomorphismTester uiTester;

    @BeforeEach
    void setUpUITester() {
        uiTester = new UniversalIsomorphismTester();
    }

    @Test
    void testStrictSMARTS() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

        SmilesParser sp = new SmilesParser(builder);
        IAtomContainer atomContainer = sp.parseSmiles("CC(=O)OC(=O)C"); // acetic acid anhydride
        QueryAtomContainer query = new QueryAtomContainer(builder);
        SymbolQueryAtom atom1 = new SymbolQueryAtom(builder);
        atom1.setSymbol("N");
        SymbolQueryAtom atom2 = new SymbolQueryAtom(builder);
        atom2.setSymbol("C");
        query.addAtom(atom1);
        query.addAtom(atom2);
        query.addBond(new OrderQueryBond(atom1, atom2, IBond.Order.DOUBLE, builder));

        Assertions.assertFalse(uiTester.isSubgraph(atomContainer, query));
    }

    @Test
    void testSMARTS() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        SmilesParser sp = new SmilesParser(builder);
        IAtomContainer atomContainer = sp.parseSmiles("CC(=O)OC(=O)C"); // acetic acid anhydride
        QueryAtomContainer query = new QueryAtomContainer(builder);
        AnyAtom atom1 = new AnyAtom(builder);
        SymbolQueryAtom atom2 = new SymbolQueryAtom(builder);
        atom2.setSymbol("C");
        query.addAtom(atom1);
        query.addAtom(atom2);
        query.addBond(new OrderQueryBond(atom1, atom2, IBond.Order.DOUBLE, builder));

        Assertions.assertTrue(uiTester.isSubgraph(atomContainer, query));
    }

    private IAtomContainer createEthane() {
        IAtomContainer container = new org.openscience.cdk.AtomContainer(); // SMILES "CC"
        IAtom carbon = new org.openscience.cdk.Atom("C");
        IAtom carbon2 = carbon.getBuilder().newInstance(IAtom.class, "C");
        carbon.setImplicitHydrogenCount(3);
        carbon2.setImplicitHydrogenCount(3);
        container.addAtom(carbon);
        container.addAtom(carbon2);
        container.addBond(carbon.getBuilder().newInstance(IBond.class, carbon, carbon2, IBond.Order.SINGLE));
        return container;
    }

    @Test
    void testImplicitHCountAtom() throws Exception {
        IAtomContainer container = createEthane();

        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

        QueryAtomContainer query1 = new QueryAtomContainer(builder); // SMARTS [h3][h3]
        SMARTSAtom atom1 = new ImplicitHCountAtom(3, builder);
        SMARTSAtom atom2 = new ImplicitHCountAtom(3, builder);
        query1.addAtom(atom1);
        query1.addAtom(atom2);
        query1.addBond(new OrderQueryBond(atom1, atom2, IBond.Order.SINGLE, builder));
        Assertions.assertTrue(uiTester.isSubgraph(container, query1));
    }

    @Test
    void testImplicitHCountAtom2() throws Exception {
        IAtomContainer container = createEthane();

        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

        QueryAtomContainer query1 = new QueryAtomContainer(builder); // SMARTS [h3][h2]
        SMARTSAtom atom1 = new ImplicitHCountAtom(3, builder);
        SMARTSAtom atom2 = new ImplicitHCountAtom(2, builder);
        query1.addAtom(atom1);
        query1.addAtom(atom2);
        query1.addBond(new OrderQueryBond(atom1, atom2, IBond.Order.SINGLE, builder));
        Assertions.assertFalse(uiTester.isSubgraph(container, query1));
    }

    @Test
    void testMatchInherited() {
        try {
            IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

            SymbolQueryAtom c1 = new SymbolQueryAtom(new org.openscience.cdk.Atom("C"));
            SymbolAndChargeQueryAtom c2 = new SymbolAndChargeQueryAtom(new org.openscience.cdk.Atom("C"));

            IAtomContainer c = TestMoleculeFactory.makeAlkane(2);

            QueryAtomContainer query1 = new QueryAtomContainer(builder);
            query1.addAtom(c1);
            query1.addAtom(c2);
            query1.addBond(new OrderQueryBond(c1, c2, Order.SINGLE, builder));
            Assertions.assertTrue(uiTester.isSubgraph(c, query1));

            QueryAtomContainer query = new QueryAtomContainer(builder);
            query.addAtom(c1);
            query.addAtom(c2);
            query.addBond(new AnyOrderQueryBond(c1, c2, Order.SINGLE, builder));
            Assertions.assertTrue(uiTester.isSubgraph(c, query));

        } catch (CDKException exception) {
            Assertions.fail(exception.getMessage());
        }

    }

    @Test
    void testUnspecifiedIsotope() {
        IAtom aexpr = SMARTSParser.parse("[!0]", SilentChemObjectBuilder.getInstance())
                                  .getAtom(0);
        assertThat(aexpr, instanceOf(LogicalOperatorAtom.class));
        assertThat(((LogicalOperatorAtom)aexpr).getOperator(),
                   Is.is("not"));
        IQueryAtom subexpr = ((LogicalOperatorAtom) aexpr).getLeft();
        assertThat(subexpr, instanceOf(MassAtom.class));
        assertThat(subexpr.getMassNumber(), Is.is(0));
    }
}
