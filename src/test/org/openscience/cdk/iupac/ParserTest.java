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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.isomorphism.IsomorphismTester;
import org.openscience.cdk.iupac.parser.NomParser;
import org.openscience.cdk.iupac.parser.ParseException;
import org.openscience.cdk.iupac.parser.TokenMgrError;
import org.openscience.cdk.templates.MoleculeFactory;

/**
 * JUnit test routines for the core parser.
 *
 * @cdk.module test-extra
 * @cdk.require ant1.6
 *
 * @author Stephen Tomkinson
 */
public class ParserTest extends NewCDKTestCase
{
    IsomorphismTester comparer = new IsomorphismTester();
    
    // Add test methods here, they have to start with 'test' name.
    // for example:
    // @Test public void testHello() {}
    @Test public void testEthane() throws Exception
    {
        Molecule parserMolecule = null;
        try {
            parserMolecule = NomParser.generate("ethane");
        } catch (CDKException exception) {
            Assert.fail(exception.getMessage());
        }
        Molecule correctMolecule = MoleculeFactory.makeAlkane(2);   

        Assert.assertTrue("The molecule built by the parser isn't the same as the expected one", 
                   comparer.isIsomorphic(parserMolecule, correctMolecule));
    }

    @Test public void testPentane() throws Exception
    {
        Molecule parserMolecule = null;
        try {
            parserMolecule = NomParser.generate("pentane");
        } catch (CDKException exception) {
            Assert.fail(exception.getMessage());
        }
        Molecule correctMolecule = MoleculeFactory.makeAlkane(5);  

        Assert.assertTrue("The molecule built by the parser isn't the same as the expected one", 
                   comparer.isIsomorphic(parserMolecule, correctMolecule));
    }

    @Test public void testSeptane() throws Exception
    {
        Molecule parserMolecule = null;
        try {
            parserMolecule = NomParser.generate("heptane");
        } catch (CDKException exception) {
            Assert.fail(exception.getMessage());
        }
        Molecule correctMolecule = MoleculeFactory.makeAlkane(7);  

        Assert.assertTrue("The molecule built by the parser isn't the same as the expected one", 
                   comparer.isIsomorphic(parserMolecule, correctMolecule));
    }    
    
    @Test public void testEicosane() throws Exception
    {
        Molecule parserMolecule = NomParser.generate("Eicosane");
        Molecule correctMolecule = MoleculeFactory.makeAlkane(20);

        Assert.assertTrue("The molecule built by the parser isn't the same as the expected one", 
                   comparer.isIsomorphic(parserMolecule, correctMolecule));
    }
    
    @Test public void testTokenMgrErrorCharacterName() throws CDKException
    {
        try
        {
            NomParser.generate("!\"$%^&*()-=_+");
            Assert.fail("Molecule was successfully generated but should have thrown a TokenMgrError");
        } catch (ParseException pe) {
            Assert.fail("The molecule did throw a class, but it incorrectly threw a ParseException," +
            "I was expected a TokenMgrError, perhaps this signifies a change in the error logic of JavaCC?" +
            "In which case check the error logic of the parser.");
        } catch (TokenMgrError tme) {
            Assert.assertTrue (true);
        }
    }
    
    @Test public void testParseExceptionNoName()
    {
        try
        {
            NomParser.generate("");
            Assert.fail("Molecule was successfully generated but should have thrown a ParseException");
        } catch (ParseException pe) {
            Assert.assertTrue (true);
        } catch (CDKException exception) {
            Assert.fail(exception.getMessage());
        }
    }
        
    @Test public void testParseExceptionMissingToken()
    {
        try
        {
            NomParser.generate("ethol");
            Assert.fail("Molecule was successfully generated but should have thrown a ParseException");
        } catch (ParseException pe) {
            Assert.assertTrue (true);
        } catch (CDKException exception) {
            Assert.fail(exception.getMessage());
        }
    }
    
    @Test public void testUnconnectingGroup()
    {
        try
        {
            NomParser.generate("7-chloropentane");
            Assert.fail("Molecule was successfully generated but should have thrown a ParseException");
        } catch (ParseException pe) {
            Assert.assertTrue (true);
        } catch (CDKException exception) {
            Assert.fail(exception.getMessage());
        }        
    }
    
    @Test public void testUnconnectingSubChain()
    {
        try
        {
            NomParser.generate("9-ethylhexane");
            Assert.fail("Molecule was successfully generated but should have thrown a ParseException");
        } catch (ParseException pe) {
            Assert.assertTrue (true);
        } catch (CDKException exception) {
            Assert.fail(exception.getMessage());
        }        
    }
}
