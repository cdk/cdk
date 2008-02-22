/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
 */
package org.openscience.cdk.test.isomorphism;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.OrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.SymbolAndChargeQueryAtom;
import org.openscience.cdk.isomorphism.matchers.SymbolQueryAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyOrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.ImplicitHCountAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module  test-smarts
 * @cdk.require java1.4+
 */
public class SMARTSTest extends CDKTestCase {
	
	public SMARTSTest(String name) {
		super(name);
	}
    
	public static Test suite() {
		return new TestSuite(SMARTSTest.class);
	}

	public void testStrictSMARTS() throws Exception {
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("CC(=O)OC(=O)C"); // acetic acid anhydride
        QueryAtomContainer query = new QueryAtomContainer();
        SymbolQueryAtom atom1 = new SymbolQueryAtom();
        atom1.setSymbol("N");
        SymbolQueryAtom atom2 = new SymbolQueryAtom();
        atom2.setSymbol("C");
        query.addAtom(atom1);
        query.addAtom(atom2);
        query.addBond(new OrderQueryBond(atom1, atom2, IBond.Order.DOUBLE));
        
        assertFalse(UniversalIsomorphismTester.isSubgraph(atomContainer, query));
    }
	
	public void testSMARTS() throws Exception {
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("CC(=O)OC(=O)C"); // acetic acid anhydride
        QueryAtomContainer query = new QueryAtomContainer();
        AnyAtom atom1 = new AnyAtom();
        SymbolQueryAtom atom2 = new SymbolQueryAtom();
        atom2.setSymbol("C");
        query.addAtom(atom1);
        query.addAtom(atom2);
        query.addBond(new OrderQueryBond(atom1, atom2, IBond.Order.DOUBLE));
        
        assertTrue(UniversalIsomorphismTester.isSubgraph(atomContainer, query));
    }
	
    private IAtomContainer createEthane() {
        IAtomContainer container = new org.openscience.cdk.AtomContainer(); // SMILES "CC"
        IAtom carbon = new org.openscience.cdk.Atom("C");
        IAtom carbon2 = carbon.getBuilder().newAtom("C");
        carbon.setHydrogenCount(3);
        carbon2.setHydrogenCount(3);
        container.addAtom(carbon);
        container.addAtom(carbon2);
        container.addBond(carbon.getBuilder().newBond(carbon, carbon2, IBond.Order.SINGLE));
        return container;
    }
    
	public void testImplicitHCountAtom() throws Exception {
        IAtomContainer container = createEthane();

        QueryAtomContainer query1 = new QueryAtomContainer(); // SMARTS [h3][h3]
        SMARTSAtom atom1 = new ImplicitHCountAtom(3);
        SMARTSAtom atom2 = new ImplicitHCountAtom(3);
        query1.addAtom(atom1);
        query1.addAtom(atom2);
        query1.addBond(new OrderQueryBond(atom1, atom2, IBond.Order.SINGLE));
        assertTrue(UniversalIsomorphismTester.isSubgraph(container, query1));
    }

	public void testImplicitHCountAtom2() throws Exception {
        IAtomContainer container = createEthane();

        QueryAtomContainer query1 = new QueryAtomContainer(); // SMARTS [h3][h2]
        SMARTSAtom atom1 = new ImplicitHCountAtom(3);
        SMARTSAtom atom2 = new ImplicitHCountAtom(2);
        query1.addAtom(atom1);
        query1.addAtom(atom2);
        query1.addBond(new OrderQueryBond(atom1, atom2, IBond.Order.SINGLE));
        assertFalse(UniversalIsomorphismTester.isSubgraph(container, query1));
    }

	public void testMatchInherited() {
		try {
			SymbolQueryAtom c1 = new SymbolQueryAtom(
				new org.openscience.cdk.Atom("C")
			);
			SymbolAndChargeQueryAtom c2 = new
			SymbolAndChargeQueryAtom(new org.openscience.cdk.Atom("C"));
			
			IAtomContainer c = MoleculeFactory.makeAlkane(2);
			
			QueryAtomContainer query1 = new QueryAtomContainer();
			query1.addAtom(c1);
			query1.addAtom(c2);
			query1.addBond(new OrderQueryBond(c1,c2,CDKConstants.BONDORDER_SINGLE));
			assertTrue(UniversalIsomorphismTester.isSubgraph(c,query1));
			
			QueryAtomContainer query = new
			QueryAtomContainer();
			query.addAtom(c1);
			query.addAtom(c2);
			query.addBond(new AnyOrderQueryBond(c1, c2,
				CDKConstants.BONDORDER_SINGLE)
			);
			assertTrue(UniversalIsomorphismTester.isSubgraph(c,query));
			
		} catch (CDKException exception) {
			fail(exception.getMessage());
		}
		
	}
}

