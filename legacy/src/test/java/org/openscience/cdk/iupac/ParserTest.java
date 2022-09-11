/*
 *   Copyright (C) 2003-2005  University of Manchester
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.iupac;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.IsomorphismTester;
import org.openscience.cdk.iupac.parser.NomParser;
import org.openscience.cdk.iupac.parser.ParseException;
import org.openscience.cdk.iupac.parser.TokenMgrError;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.templates.TestMoleculeFactory;

/**
 * JUnit test routines for the core parser.
 *
 * @cdk.module test-extra
 * @cdk.require ant1.6
 *
 * @author Stephen Tomkinson
 */
class ParserTest extends CDKTestCase {

    private final IsomorphismTester comparer = new IsomorphismTester();

    // Add test methods here, they have to start with 'test' name.
    // for example:
    // @Test public void testHello() {}
    @Test
    void testEthane() throws Exception {
        IAtomContainer parserMolecule = null;
        try {
            parserMolecule = NomParser.generate("ethane", SilentChemObjectBuilder.getInstance());
        } catch (CDKException exception) {
            Assertions.fail(exception.getMessage());
        }
        IAtomContainer correctMolecule = TestMoleculeFactory.makeAlkane(2);

        Assertions.assertTrue(comparer.isIsomorphic(parserMolecule, correctMolecule), "The molecule built by the parser isn't the same as the expected one");
    }

    @Test
    void testPentane() throws Exception {
        IAtomContainer parserMolecule = null;
        try {
            parserMolecule = NomParser.generate("pentane", SilentChemObjectBuilder.getInstance());
        } catch (CDKException exception) {
            Assertions.fail(exception.getMessage());
        }
        IAtomContainer correctMolecule = TestMoleculeFactory.makeAlkane(5);

        Assertions.assertTrue(comparer.isIsomorphic(parserMolecule, correctMolecule), "The molecule built by the parser isn't the same as the expected one");
    }

    @Test
    void testSeptane() throws Exception {
        IAtomContainer parserMolecule = null;
        try {
            parserMolecule = NomParser.generate("heptane", SilentChemObjectBuilder.getInstance());
        } catch (CDKException exception) {
            Assertions.fail(exception.getMessage());
        }
        IAtomContainer correctMolecule = TestMoleculeFactory.makeAlkane(7);

        Assertions.assertTrue(comparer.isIsomorphic(parserMolecule, correctMolecule), "The molecule built by the parser isn't the same as the expected one");
    }

    @Test
    void testEicosane() throws Exception {
        IAtomContainer parserMolecule = NomParser.generate("Eicosane", SilentChemObjectBuilder.getInstance());
        IAtomContainer correctMolecule = TestMoleculeFactory.makeAlkane(20);

        Assertions.assertTrue(comparer.isIsomorphic(parserMolecule, correctMolecule), "The molecule built by the parser isn't the same as the expected one");
    }

    @Test
    void testTokenMgrErrorCharacterName() throws Exception {
        try {
            NomParser.generate("!\"$%^&*()-=_+", SilentChemObjectBuilder.getInstance());
            Assertions.fail("Molecule was successfully generated but should have thrown a TokenMgrError");
        } catch (ParseException pe) {
            Assertions.fail("The molecule did throw a class, but it incorrectly threw a ParseException,"
                    + "I was expected a TokenMgrError, perhaps this signifies a change in the error logic of JavaCC?"
                    + "In which case check the error logic of the parser.");
        } catch (TokenMgrError tme) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    void testParseExceptionNoName() {
        try {
            NomParser.generate("", SilentChemObjectBuilder.getInstance());
            Assertions.fail("Molecule was successfully generated but should have thrown a ParseException");
        } catch (ParseException pe) {
            Assertions.assertTrue(true);
        } catch (CDKException exception) {
            Assertions.fail(exception.getMessage());
        }
    }

    @Test
    void testParseExceptionMissingToken() {
        try {
            NomParser.generate("ethol", SilentChemObjectBuilder.getInstance());
            Assertions.fail("Molecule was successfully generated but should have thrown a ParseException");
        } catch (ParseException pe) {
            Assertions.assertTrue(true);
        } catch (CDKException exception) {
            Assertions.fail(exception.getMessage());
        }
    }

    @Test
    void testUnconnectingGroup() {
        try {
            NomParser.generate("7-chloropentane", SilentChemObjectBuilder.getInstance());
            Assertions.fail("Molecule was successfully generated but should have thrown a ParseException");
        } catch (ParseException pe) {
            Assertions.assertTrue(true);
        } catch (CDKException exception) {
            Assertions.fail(exception.getMessage());
        }
    }

    @Test
    void testUnconnectingSubChain() {
        try {
            NomParser.generate("9-ethylhexane", SilentChemObjectBuilder.getInstance());
            Assertions.fail("Molecule was successfully generated but should have thrown a ParseException");
        } catch (ParseException pe) {
            Assertions.assertTrue(true);
        } catch (CDKException exception) {
            Assertions.fail(exception.getMessage());
        }
    }
}
