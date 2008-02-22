/* $Revision: 8418 $ $Author: egonw $ $Date: 2007-06-25 22:05:44 +0200 (Mon, 25 Jun 2007) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.test.qsar.descriptors.molecular;

import javax.vecmath.Point3d;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.test.qsar.descriptors.DescriptorTest;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Tests for molecular descriptors.
 *
 * @cdk.module test-qsarmolecular
 */
public abstract class MolecularDescriptorTest extends DescriptorTest {
	
	protected IMolecularDescriptor descriptor;

	public MolecularDescriptorTest() {}
	
	public MolecularDescriptorTest(String name) {
		super(name);
	}
	
	public void setDescriptor(Class descriptorClass) throws Exception {
		if (descriptor == null) {
			Object descriptor = (Object)descriptorClass.newInstance();
			if (!(descriptor instanceof IMolecularDescriptor)) {
				throw new CDKException("The passed descriptor class must be a IMolecularDescriptor");
			}
			this.descriptor = (IMolecularDescriptor)descriptor;
		}
		super.setDescriptor(descriptorClass);
	}

    public void testCalculate_IAtomContainer() throws Exception {
        IAtomContainer mol = someoneBringMeSomeWater();
        
        DescriptorValue v = descriptor.calculate(mol);
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
        
        DescriptorValue v = descriptor.calculate(mol);
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

    public void testGetDescriptorResultType() throws Exception {
    	IDescriptorResult result = descriptor.getDescriptorResultType();
    	assertNotNull(
    		"The getDescriptorResultType() must not be null.",
    		result
    	);
    	
    	IAtomContainer mol = someoneBringMeSomeWater();
        DescriptorValue v = descriptor.calculate(mol);
        
    	assertTrue(
    		"The getDescriptorResultType() is inconsistent with the calculated descriptor results",
    		result.getClass().getName().contains(v.getValue().getClass().getName()) 
    	);
    	assertEquals(
    		"The specified getDescriptorResultType() length does not match the actually calculated result vector length",
    		v.getValue().length(), result.length()
    	);
    }
    
    private IMolecule someoneBringMeSomeWater() throws Exception {
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
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        return mol;
    }
    
}
