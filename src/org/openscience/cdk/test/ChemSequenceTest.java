/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemSequence;

/**
 * Checks the funcitonality of the ChemSequence class.
 *
 * @cdkPackage test
 *
 * @see org.openscience.cdk.ChemSequence
 */
public class ChemSequenceTest extends TestCase {

    public ChemSequenceTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(ChemSequenceTest.class);
    }
    
    public void testAddChemModel() {
        ChemSequence cs = new ChemSequence();
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());
        assertEquals(3, cs.getChemModelCount());
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel()); // this one should enfore array grow
        assertEquals(6, cs.getChemModelCount());
    }

    public void testGetChemModels() {
        ChemSequence cs = new ChemSequence();
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());

        assertEquals(3, cs.getChemModelCount());
        ChemModel[] models = cs.getChemModels();
        assertEquals(3, models.length);
        assertNotNull(models[0]);
        assertNotNull(models[1]);
        assertNotNull(models[2]);
    }

}
