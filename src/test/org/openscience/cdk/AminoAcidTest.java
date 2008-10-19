/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
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
public class AminoAcidTest extends MonomerTest {

	protected static IChemObjectBuilder builder;
	
    @BeforeClass public static void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }
    
    @Test public void testAminoAcid() {
        IAminoAcid oAminoAcid = builder.newAminoAcid();
        Assert.assertNotNull(oAminoAcid);
    }
    
    @Test public void testAddCTerminus_IAtom() {
        IAminoAcid m = builder.newAminoAcid();
        IAtom cTerminus = builder.newAtom("C");
        m.addCTerminus(cTerminus);
        Assert.assertEquals(cTerminus, m.getCTerminus());
    }
    @Test public void testGetCTerminus() {
        IAminoAcid m = builder.newAminoAcid();
        Assert.assertNull(m.getCTerminus());
    }

    @Test public void testAddNTerminus_IAtom() {
        IAminoAcid m = builder.newAminoAcid();
        IAtom nTerminus = builder.newAtom("N");
        m.addNTerminus(nTerminus);
        Assert.assertEquals(nTerminus, m.getNTerminus());
    }
    @Test public void testGetNTerminus() {
        IAminoAcid m = builder.newAminoAcid();
        Assert.assertNull(m.getNTerminus());
    }
    
    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test public void testToString() {
        IAminoAcid m = builder.newAminoAcid();
        IAtom nTerminus = builder.newAtom("N");
        m.addNTerminus(nTerminus);
        String description = m.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue('\n' != description.charAt(i));
            Assert.assertTrue('\r' != description.charAt(i));
        }

        m = builder.newAminoAcid();
        IAtom cTerminus = builder.newAtom("C");
        m.addNTerminus(cTerminus);
        description = m.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue('\n' != description.charAt(i));
            Assert.assertTrue('\r' != description.charAt(i));
        }
}

    @Test public void testClone() throws Exception {
        IAminoAcid aa = builder.newAminoAcid();
        Object clone = aa.clone();
        Assert.assertTrue(clone instanceof IAminoAcid);
        Assert.assertNotSame(aa, clone);
        
        aa = builder.newAminoAcid();
        IAtom nTerminus = builder.newAtom("N");
        aa.addNTerminus(nTerminus);
        clone = aa.clone();
        Assert.assertTrue(clone instanceof IAminoAcid);
        Assert.assertNotSame(aa, clone);

        aa = builder.newAminoAcid();
        IAtom cTerminus = builder.newAtom("C");
        aa.addNTerminus(cTerminus);
        clone = aa.clone();
        Assert.assertTrue(clone instanceof IAminoAcid);
        Assert.assertNotSame(aa, clone);
    }
}
