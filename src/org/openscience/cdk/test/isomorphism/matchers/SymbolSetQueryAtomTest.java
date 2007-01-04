/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
 */
package org.openscience.cdk.test.isomorphism.matchers;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.isomorphism.matchers.SymbolSetQueryAtom;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Checks the funcitonality of the IsomorphismTester
 *
 * @cdk.module test-extra
 */
public class SymbolSetQueryAtomTest extends CDKTestCase
{
	
    SymbolSetQueryAtom symbolSet = null;
    
    public SymbolSetQueryAtomTest(String name)
	{
		super(name);
	}
	
	public void setUp() 
    {
        symbolSet = new SymbolSetQueryAtom();
        symbolSet.addSymbol("C");
        symbolSet.addSymbol("Fe");
    }
	
	public static Test suite() {
		return new TestSuite(SymbolSetQueryAtomTest.class);
	}

	public void testMatches()
	{
        Atom c = new Atom("C");
        Atom n = new Atom("N");
		assertTrue(symbolSet.matches(c));
        assertFalse(symbolSet.matches(n));
	}
    
    public void testRemoveSymbol()
    {
        symbolSet.removeSymbol("Fe");
        assertEquals(1, symbolSet.getSymbolSet().size());
        assertFalse(symbolSet.hasSymbol("Fe"));
        assertTrue(symbolSet.hasSymbol("C"));
        symbolSet.addSymbol("Fe");
    }
    
    public void testHasSymbol()
    {
        assertTrue(symbolSet.hasSymbol("C"));
        assertFalse(symbolSet.hasSymbol("N"));
    }
    
}
