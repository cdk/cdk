/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2005-2006  The Chemistry Development Kit (CDK) project
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
 *  */
package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAminoAcid;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * TestCase for the AminoAcid class.
 *
 * @cdk.module test-data
 *
 * @author  Edgar Luttman <edgar@uni-paderborn.de>
 * @cdk.created 2001-08-09
 */
public class AminoAcidTest extends CDKTestCase {

	protected IChemObjectBuilder builder;
	
    public AminoAcidTest(String name) {
        super(name);
    }

    public void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }
    
    public static Test suite() {
        return new TestSuite(AminoAcidTest.class);
    }

    public void testAminoAcid() {
        IAminoAcid oAminoAcid = builder.newAminoAcid();
        assertNotNull(oAminoAcid);
    }
    
    public void testAddCTerminus_IAtom() {
        IAminoAcid m = builder.newAminoAcid();
        IAtom cTerminus = builder.newAtom("C");
        m.addCTerminus(cTerminus);
        assertEquals(cTerminus, m.getCTerminus());
    }
    public void testGetCTerminus() {
        IAminoAcid m = builder.newAminoAcid();
        assertNull(m.getCTerminus());
    }

    public void testAddNTerminus_IAtom() {
        IAminoAcid m = builder.newAminoAcid();
        IAtom nTerminus = builder.newAtom("N");
        m.addNTerminus(nTerminus);
        assertEquals(nTerminus, m.getNTerminus());
    }
    public void testGetNTerminus() {
        IAminoAcid m = builder.newAminoAcid();
        assertNull(m.getNTerminus());
    }
    
    /**
     * Method to test wether the class complies with RFC #9.
     */
    public void testToString() {
        IAminoAcid m = builder.newAminoAcid();
        IAtom nTerminus = builder.newAtom("N");
        m.addNTerminus(nTerminus);
        String description = m.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue('\n' != description.charAt(i));
            assertTrue('\r' != description.charAt(i));
        }
    }

    public void testClone() throws Exception {
        IAminoAcid aa = builder.newAminoAcid();
        Object clone = aa.clone();
        assertTrue(clone instanceof IAminoAcid);
        assertNotSame(aa, clone);
    }
}
