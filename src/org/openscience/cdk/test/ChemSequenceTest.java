/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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

import org.openscience.cdk.*;
import java.util.*;
import junit.framework.*;
import javax.vecmath.*;

/**
 * Checks the funcitonality of the ChemSequence class.
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
}
