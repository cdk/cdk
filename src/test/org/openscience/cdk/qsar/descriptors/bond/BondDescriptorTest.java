/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2007-2008  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.qsar.descriptors.bond;

import org.junit.Assert;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IBondDescriptor;
import org.openscience.cdk.qsar.descriptors.DescriptorTest;

import javax.vecmath.Point3d;

/**
 * Tests for bond descriptors.
 *
 * @cdk.module test-qsarbond
 */
public abstract class BondDescriptorTest extends DescriptorTest {
	
	protected IBondDescriptor descriptor;

	public BondDescriptorTest() {}
	
	public BondDescriptorTest(String name) {
		super(name);
	}
	
	public void setDescriptor(Class descriptorClass) throws Exception {
		if (descriptor == null) {
			Object descriptor = descriptorClass.newInstance();
			if (!(descriptor instanceof IBondDescriptor)) {
				throw new CDKException("The passed descriptor class must be a IBondDescriptor");
			}
			this.descriptor = (IBondDescriptor)descriptor;
		}
		super.setDescriptor(descriptorClass);
	}

    public void testCalculate_IBond_IAtomContainer() throws Exception {
        IAtomContainer mol = someoneBringMeSomeWater();

        DescriptorValue v = null;
        try {
            v = descriptor.calculate(mol.getBond(0), mol);
        } catch (Exception e) {
            fail("A descriptor must not throw an exception");
        }
        assertNotNull(v);
        assertNotSame(
        	"The descriptor did not calculate any value.",
        	0, v.getValue().length()
        );
    }

    /**
	 * Checks if the given labels are consistent.
	 * 
	 * @throws Exception Passed on from calculate.
	 */
    public void testLabels() throws Exception {
        IAtomContainer mol = someoneBringMeSomeWater();
        
        DescriptorValue v = descriptor.calculate(mol.getBond(0), mol);
        assertNotNull(v);
        String[] names = v.getNames();
        assertNotNull(
        	"The descriptor must return labels using the getNames() method.",
        	names
        );
        assertNotSame(
        	"At least one label must be given.",
        	0, names.length
        );
        for (int i=0; i<names.length; i++) {
        	assertNotNull(
        		"A descriptor label may not be null.",
        		names[i]
        	);
        	assertNotSame(
        		"The label string must not be empty.",
        		0, names[i].length()
        	);
//        	System.out.println("Label: " + names[i]);
        }
        assertNotNull(v.getValue());
        int valueCount = v.getValue().length();
        assertEquals(
        	"The number of labels must equals the number of values.",
        	names.length, valueCount
        );
    }

     /**
     * Check if the names obtained directly from the descriptor without
     * calculation match those obtained from the descriptor value object.
     * Also ensure that the number of actual values matches the length
     * of the names
     */
    public void testNamesConsistency() {
        IAtomContainer mol = someoneBringMeSomeWater();

        String[] names1 = descriptor.getDescriptorNames();
        DescriptorValue v = descriptor.calculate(mol.getBond(1), mol);
        String[] names2 = v.getNames();

        assertEquals(names1.length, names2.length);
        Assert.assertArrayEquals(names1, names2);

        int valueCount = v.getValue().length();
        assertEquals(valueCount, names1.length);
    }

    public void testCalculate_NoModifications() throws Exception {
        IAtomContainer mol = someoneBringMeSomeWater();
        IBond bond = mol.getBond(0);
        String priorString = bond.toString();
        descriptor.calculate(bond, mol);
        String afterString = bond.toString();
        assertEquals(
          "The descriptor must not change the passed bond in any respect.",
          priorString, afterString
        );
    }

    private IMolecule someoneBringMeSomeWater() {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom c1 = DefaultChemObjectBuilder.getInstance().newAtom("O");
        c1.setPoint3d(new Point3d(0.0, 0.0, 0.0));
        IAtom h1 = DefaultChemObjectBuilder.getInstance().newAtom("H");
        h1.setPoint3d(new Point3d(1.0, 0.0, 0.0));
        IAtom h2 = DefaultChemObjectBuilder.getInstance().newAtom("H");
        h2.setPoint3d(new Point3d(-1.0, 0.0, 0.0));
        mol.addAtom(c1);
        mol.addAtom(h1);
        mol.addAtom(h2);
        mol.addBond(0,1,IBond.Order.SINGLE);
        mol.addBond(0,2,IBond.Order.SINGLE);
        return mol;
    }
    
}
