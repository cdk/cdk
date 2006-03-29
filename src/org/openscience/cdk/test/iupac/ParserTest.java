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
package org.openscience.cdk.test.iupac;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.isomorphism.IsomorphismTester;
import org.openscience.cdk.iupac.parser.NomParser;
import org.openscience.cdk.iupac.parser.ParseException;
import org.openscience.cdk.iupac.parser.TokenMgrError;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;

/**
 * JUnit test routines for the core parser.
 *
 * @cdk.module test-extra
 * @cdk.require ant1.6
 *
 * @author Stephen Tomkinson
 */
public class ParserTest extends CDKTestCase
{
    HydrogenAdder adder = new HydrogenAdder();
    IsomorphismTester comparer = new IsomorphismTester();
    
    public ParserTest() {}

    public ParserTest(java.lang.String testName)
    {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(ParserTest.class);
        return suite;
    }
    
    // Add test methods here, they have to start with 'test' name.
    // for example:
    // public void testHello() {}
    public void testEthane() throws ParseException
    {
        Molecule parserMolecule = null;
        try {
            parserMolecule = NomParser.generate("ethane");
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
        Molecule correctMolecule = MoleculeFactory.makeAlkane(2);   

        assertTrue("The molecule built by the parser isn't the same as the expected one", 
                   comparer.isIsomorphic(parserMolecule, correctMolecule));
    }

    public void testPentane() throws ParseException
    {
        Molecule parserMolecule = null;
        try {
            parserMolecule = NomParser.generate("pentane");
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
        Molecule correctMolecule = MoleculeFactory.makeAlkane(5);  

        assertTrue("The molecule built by the parser isn't the same as the expected one", 
                   comparer.isIsomorphic(parserMolecule, correctMolecule));
    }

    public void testSeptane() throws ParseException
    {
        Molecule parserMolecule = null;
        try {
            parserMolecule = NomParser.generate("heptane");
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
        Molecule correctMolecule = MoleculeFactory.makeAlkane(7);  

        assertTrue("The molecule built by the parser isn't the same as the expected one", 
                   comparer.isIsomorphic(parserMolecule, correctMolecule));
    }    
    
    public void testEicosane() throws ParseException
    {
        Molecule parserMolecule = null;
        try {
            parserMolecule = NomParser.generate("Eicosane");
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
        Molecule correctMolecule = MoleculeFactory.makeAlkane(20);

        assertTrue("The molecule built by the parser isn't the same as the expected one", 
                   comparer.isIsomorphic(parserMolecule, correctMolecule));
    }
    
    public void testTokenMgrErrorCharacterName()
    {
        try
        {
            NomParser.generate("!\"$%^&*()-=_+");
            fail("Molecule was successfully generated but should have thrown a TokenMgrError");
        } catch (ParseException pe) {
            fail("The molecule did throw a class, but it incorrectly threw a ParseException," +
            "I was expected a TokenMgrError, perhaps this signifies a change in the error logic of JavaCC?" +
            "In which case check the error logic of the parser.");
        } catch (TokenMgrError tme) {
            assertTrue (true);
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
    }
    
    public void testParseExceptionNoName()
    {
        try
        {
            NomParser.generate("");
            fail("Molecule was successfully generated but should have thrown a ParseException");
        } catch (ParseException pe) {
            assertTrue (true);
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
    }
        
    public void testParseExceptionMissingToken()
    {
        try
        {
            NomParser.generate("ethol");
            fail("Molecule was successfully generated but should have thrown a ParseException");
        } catch (ParseException pe) {
            assertTrue (true);
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
    }
    
    public void testUnconnectingGroup()
    {
        try
        {
            NomParser.generate("7-chloropentane");
            fail("Molecule was successfully generated but should have thrown a ParseException");
        } catch (ParseException pe) {
            assertTrue (true);
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }        
    }
    
    public void testUnconnectingSubChain()
    {
        try
        {
            NomParser.generate("9-ethylhexane");
            fail("Molecule was successfully generated but should have thrown a ParseException");
        } catch (ParseException pe) {
            assertTrue (true);
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }        
    }
}
