/* $Revision: 11293 $ $Author: rajarshi $ $Date: 2008-06-06 22:46:01 +0200 (Fri, 06 Jun 2008) $
 * 
 * Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.atomtype;

import java.io.InputStream;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.Mol2Reader;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * This class tests the perception of Sybyl atom types, which uses
 * CDK atom type perception and mapping of CDK atom types to Sybyl
 * atom types.
 *
 * @cdk.module test-atomtype
 */
public class SybylAtomTypeMatcherTest extends NewCDKTestCase {

	@Test public void testGetInstance_IChemObjectBuilder() {
		IAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(NoNotificationChemObjectBuilder.getInstance());
		Assert.assertNotNull(matcher);
	}
	
	@Test public void testFindMatchingAtomType_IAtomContainer_IAtom() throws Exception {
		IAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(NoNotificationChemObjectBuilder.getInstance());
		Assert.assertNotNull(matcher);
		Molecule ethane = MoleculeFactory.makeAlkane(2);
		String sybylType = matcher.findMatchingAtomType(ethane, ethane.getAtom(0)).getAtomTypeName();
		Assert.assertEquals("C.3", sybylType);
	}

    @Test public void testAtomTyping() throws Exception {
        String filename = "data/mol2/atomtyping.mol2";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        IMolecule molecule = (IMolecule)reader.read(new Molecule());
        Assert.assertNotNull(molecule);
        IMolecule reference = (IMolecule)molecule.clone();
        
        // test if the perceived atom types match that
        percieveAtomTypesAndConfigureAtoms(molecule);
        Iterator<IAtom> refAtoms = reference.atoms().iterator();
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext() && refAtoms.hasNext()) {
        	// work around aromaticity, which we skipped for now
        	Assert.assertEquals(
        		"Perceived atom type does not match atom type in file",
        		refAtoms.next().getAtomTypeName(),
        		atoms.next().getAtomTypeName()
        	);
        }
    }

	@Test public void testAtomTyping4() throws Exception {
        String filename = "data/mol2/atomtyping4.mol2";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        IMolecule molecule = (IMolecule)reader.read(new Molecule());
        Assert.assertNotNull(molecule);
        IMolecule reference = (IMolecule)molecule.clone();
        
        // test if the perceived atom types match that
        percieveAtomTypesAndConfigureAtoms(molecule);
        Iterator<IAtom> refAtoms = reference.atoms().iterator();
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext() && refAtoms.hasNext()) {
        	// work around aromaticity, which we skipped for now
        	IAtom refAtom = refAtoms.next();
        	Assert.assertEquals(
        		"Perceived atom type does not match atom type in file",
        		refAtom.getAtomTypeName(),
        		atoms.next().getAtomTypeName()
        	);
        }
    }

    @Test public void testAtomTyping2() throws Exception {
        String filename = "data/mol2/atomtyping2.mol2";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        IMolecule molecule = (IMolecule)reader.read(new Molecule());
        Assert.assertNotNull(molecule);
        IMolecule reference = (IMolecule)molecule.clone();
        
        // test if the perceived atom types match that
        percieveAtomTypesAndConfigureAtoms(molecule);
        Iterator<IAtom> refAtoms = reference.atoms().iterator();
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext() && refAtoms.hasNext()) {
        	// work around aromaticity, which we skipped for now
        	IAtom refAtom = refAtoms.next();
        	Assert.assertEquals(
        		"Perceived atom type does not match atom type in file",
        		refAtom.getAtomTypeName(),
        		atoms.next().getAtomTypeName()
        	);
        }
    }

    @Test public void testAtomTyping3() throws Exception {
        String filename = "data/mol2/atomtyping3.mol2";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        IMolecule molecule = (IMolecule)reader.read(new Molecule());
        Assert.assertNotNull(molecule);
        IMolecule reference = (IMolecule)molecule.clone();
        
        // test if the perceived atom types match that
        percieveAtomTypesAndConfigureAtoms(molecule);
        Iterator<IAtom> refAtoms = reference.atoms().iterator();
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext() && refAtoms.hasNext()) {
        	// work around aromaticity, which we skipped for now
        	IAtom refAtom = refAtoms.next();
        	Assert.assertEquals(
        		"Perceived atom type does not match atom type in file",
        		refAtom.getAtomTypeName(),
        		atoms.next().getAtomTypeName()
        	);
        }
    }

    private void percieveAtomTypesAndConfigureAtoms(IAtomContainer container) throws CDKException {
    	SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(container.getBuilder());
        Iterator<IAtom> atoms = container.atoms().iterator();
        while (atoms.hasNext()) {
        	IAtom atom = atoms.next();
        	atom.setAtomTypeName(null);
        	IAtomType matched = matcher.findMatchingAtomType(container, atom);
        	if (matched != null) AtomTypeManipulator.configure(atom, matched);
        }
	}
}
