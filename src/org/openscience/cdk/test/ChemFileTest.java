/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
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
 * @cdkPackage test
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
    
    public void testAddChemSequence() {
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
 
        assertEquals(3, cs.getChemSequenceCount());
        assertEquals(3, cs.getChemSequences().length);
    }

}
