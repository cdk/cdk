/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */

package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemSequence;

/**
 * Checks the funcitonality of the ChemSequence class.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.ChemSequence
 */
public class ChemFileTest extends TestCase {

    public ChemFileTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(ChemFileTest.class);
    }
    
    public void testChemFile() {
        ChemFile cs = new ChemFile();
        assertNotNull(cs);
    }

    public void testAddChemSequence_ChemSequence() {
        ChemFile cs = new ChemFile();
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
        assertEquals(3, cs.getChemSequenceCount());
    }
    
    public void testGrowChemSequenceArray() {
        ChemFile cs = new ChemFile();
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
        assertEquals(3, cs.getChemSequenceCount());
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence()); // this one should enfore array grow
        assertEquals(6, cs.getChemSequenceCount());
    }

    public void testGetChemSequences() {
        ChemFile cs = new ChemFile();
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());

        assertNotNull(cs.getChemSequences());
        assertEquals(3, cs.getChemSequences().length);
    }

    public void testGetChemSequenceCount() {
        ChemFile cs = new ChemFile();
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
 
        assertEquals(3, cs.getChemSequenceCount());
    }

    /** Test for RFC #9 */
    public void testToString() {
        ChemFile cs = new ChemFile();
        String description = cs.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }
}
