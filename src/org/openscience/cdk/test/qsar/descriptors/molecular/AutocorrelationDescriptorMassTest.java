/* $Revision: 7636 $ $Author: egonw $ $Date: 2007-01-04 18:46:10 +0100 (gio, 04 gen 2007)$
 *  
 * Copyright (C) 2007  Federico
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

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.molecular.AutocorrelationDescriptorMass;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-qsar
 */
public class AutocorrelationDescriptorMassTest extends CDKTestCase{

	public AutocorrelationDescriptorMassTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(AutocorrelationDescriptorMassTest.class);
	}

//	public void testscaledAtomicMasses_IElement(){
//		try{
//		String filename = "data/mdl/clorobenzene.mol";
//		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(
//				filename);
//		MDLReader reader = new MDLReader(ins);
//		IMolecule container = (Molecule) reader.read((ChemObject) new Molecule());
//		double cont = AutocorrelationDescriptorMass.scaledAtomicMasses(container.getAtom(0));
//		double cont2 = AutocorrelationDescriptorMass.scaledAtomicMasses(container.getAtom(6));
//		assertEquals(1,.0001, cont);
//		assertEquals(2.952, .001, cont2);
//		System.out.println(cont);
//		}catch(Exception ex){
//			fail(ex.getMessage());
//		}
//	
//	}
//	
//	public void testlistconvertion_IAtomContainer(){
//		try{
//		String filename = "data/mdl/clorobenzene.mol";
//		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(
//				filename);
//		MDLReader reader = new MDLReader(ins);
//		IMolecule container = (Molecule) reader.read((ChemObject) new Molecule());
//		List list = AutocorrelationDescriptorMass.listconvertion(container);
//		System.out.println("The element in position 6 of the list is: " + list.get(6));
//		System.out.println(list);
//		}catch(Exception ex){
//			fail(ex.getMessage());
//		}
//	}	
		
	public void testcalculate_IAtomContainer() throws Exception {
		String filename = "data/mdl/clorobenzene.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(
				filename);
		MDLReader reader = new MDLReader(ins);
		IMolecule container = (Molecule) reader.read((ChemObject) new Molecule());
		DescriptorValue count = new AutocorrelationDescriptorMass().calculate(container);
//		System.out.println(count.getValue());
		fail("Missing assert");
	}
	
}