/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2005  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk.test.tools;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.BioPolymer;
import org.openscience.cdk.templates.AminoAcids;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.ProteinBuilderTool;

/**
 * @cdk.module test
 */
public class ProteinBuilderToolTest extends CDKTestCase {

    public static Test suite() {
        return new TestSuite(ProteinBuilderToolTest.class);
    }

    public void testCreateProtein() {
        BioPolymer protein = null;
        try {
            protein = ProteinBuilderTool.createProtein("GAGA");
        } catch (Exception exception) {
            fail(exception.getMessage());
            exception.printStackTrace();
        }
        assertNotNull(protein);
        assertEquals(4, protein.getMonomerCount());
        assertEquals(1, protein.getStrandCount());
        assertEquals(18, protein.getAtomCount());
        assertEquals(14+3, protein.getBondCount()); // 3 = extra back bone bonds
    }

}

