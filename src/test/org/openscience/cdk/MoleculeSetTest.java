/* $Revision$ $Author$ $Date$    
 * 
 * Copyright (C) 1997-2008  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.AbstractMoleculeSetTest;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.ITestObjectBuilder;

/**
 * Checks the functionality of the MoleculeSet class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.MoleculeSet
 */
public class MoleculeSetTest extends AbstractMoleculeSetTest {

    @BeforeClass public static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {
            public IChemObject newTestObject() {
                return new MoleculeSet();
            }
        });
    }
    
    @Test public void testClone() throws CloneNotSupportedException{
        IMoleculeSet moleculeSet = DefaultChemObjectBuilder.getInstance()
            .newInstance(IMoleculeSet.class);
        IMolecule mol = moleculeSet.getBuilder().newInstance(IMolecule.class);
        moleculeSet.addAtomContainer(mol);
        //we test that the molecule added is actually in the moleculeSet
        Assert.assertSame(mol, moleculeSet.getAtomContainer(0));
        moleculeSet.clone();
        //after the clone, the molecule added should still be in the moleculeSet
        Assert.assertSame(mol, moleculeSet.getAtomContainer(0));
    }
}
