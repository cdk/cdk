/* Copyright (C) 2022,2024 Nikolay Kochev <nick@uni-plovdiv.net>, Uli Fechner
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
package org.openscience.cdk.rinchi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.io.MDLV2000Writer.SPIN_MULTIPLICITY;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.test.CDKTestCase;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Nikolay Kochev
 * @author Uli Fechner
 */
public class RInChIGeneratorFactoryTest extends CDKTestCase {

	@Test
	public void testGetInstance() {
		RInChIGeneratorFactory factory = RInChIGeneratorFactory.getInstance();
		assertNotNull(factory);
	}

	@Test
	public void testGetInstance_multipleCalls_sameInstance() {
		RInChIGeneratorFactory factory1 = RInChIGeneratorFactory.getInstance();
		assertNotNull(factory1);

		RInChIGeneratorFactory factory2 = RInChIGeneratorFactory.getInstance();
		assertNotNull(factory2);

		RInChIGeneratorFactory factory3 = RInChIGeneratorFactory.getInstance();
		assertNotNull(factory3);

		assertSame(factory1, factory2, "Asserting that getInstance returns the same instance with every call");
		assertSame(factory2, factory3, "Asserting that getInstance returns the same instance with every call");
	}

	@Test
	public void testGetInstance_threadSafety() throws InterruptedException {
		RInChIGeneratorFactory singletonInstance = RInChIGeneratorFactory.getInstance();

		int numberOfMethodCalls = 10000;
		ConcurrentLinkedQueue<RInChIGeneratorFactory> factoryInstancesQueue = new ConcurrentLinkedQueue<>();
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfMethodCalls / 10);

		for (int i = 0; i < numberOfMethodCalls; i++) {
			executorService.execute(() -> {				
					RInChIGeneratorFactory factory = RInChIGeneratorFactory.getInstance();
					factoryInstancesQueue.add(factory);
			});
		}

		executorService.shutdown();
		assertTrue(executorService.awaitTermination(5, TimeUnit.SECONDS));

		Assertions.assertEquals(numberOfMethodCalls, factoryInstancesQueue.size());
		for (RInChIGeneratorFactory factory: factoryInstancesQueue) {
			assertSame(singletonInstance, factory);
		}
	}
	
	@Disabled("not implemented yet")
	@Test
	public void test01() {
		//Create Diels–Alder Reaction using CDK
		//Reactant 1
		IAtomContainer molecule1 = new AtomContainer();
		IAtom reactantAtom1 = new Atom("C");
		IAtom reactantAtom2 = new Atom("C");
		molecule1.addAtom(reactantAtom1);
		molecule1.addAtom(reactantAtom2);
		molecule1.addBond(new Bond(reactantAtom1, reactantAtom2, IBond.Order.DOUBLE));
		//Reactant 2
		IAtomContainer molecule2 = new AtomContainer();
		IAtom reactantAtom3 = new Atom("C");
		IAtom reactantAtom4 = new Atom("C");
		IAtom reactantAtom5 = new Atom("C");
		IAtom reactantAtom6 = new Atom("C");
		molecule2.addAtom(reactantAtom3);
		molecule2.addAtom(reactantAtom4);
		molecule2.addAtom(reactantAtom5);
		molecule2.addAtom(reactantAtom6);
		molecule2.addBond(new Bond (reactantAtom3, reactantAtom4, IBond.Order.DOUBLE));
		molecule2.addBond(new Bond (reactantAtom4, reactantAtom5, IBond.Order.SINGLE));
		molecule2.addBond(new Bond (reactantAtom5, reactantAtom6, IBond.Order.DOUBLE));
		//Product
		IAtomContainer molecule3 = new AtomContainer();
		IAtom productAtom1 = new Atom("C");
		IAtom productAtom2 = new Atom("C");
		IAtom productAtom3 = new Atom("C");
		IAtom productAtom4 = new Atom("C");
		IAtom productAtom5 = new Atom("C");
		IAtom productAtom6 = new Atom("C");
		molecule3.addAtom(productAtom1);
		molecule3.addAtom(productAtom2);
		molecule3.addAtom(productAtom3);
		molecule3.addAtom(productAtom4);
		molecule3.addAtom(productAtom5);
		molecule3.addAtom(productAtom6);
		molecule3.addBond(new Bond (productAtom1, productAtom2, IBond.Order.DOUBLE));
		molecule3.addBond(new Bond (productAtom2, productAtom3, IBond.Order.SINGLE));
		molecule3.addBond(new Bond (productAtom3, productAtom4, IBond.Order.SINGLE));
		molecule3.addBond(new Bond (productAtom2, productAtom3, IBond.Order.SINGLE));
		molecule3.addBond(new Bond (productAtom2, productAtom3, IBond.Order.SINGLE));
		molecule3.addBond(new Bond (productAtom2, productAtom3, IBond.Order.SINGLE));
		
		//Create reaction and set reagents and products
		IReaction reaction = SilentChemObjectBuilder.getInstance().newReaction();
		reaction.addReactant(molecule1);
		reaction.addReactant(molecule2);
		reaction.addProduct(molecule3);
		
		//Generate RInChI
		RInChIGenerator generator1 = RInChIGeneratorFactory.getInstance().getRInChIGenerator(reaction);
		Assertions.assertEquals(StatusMessagesOutput.Status.SUCCESS, generator1.getStatus(), "RInChI status: ");
		Assertions.assertTrue(generator1.getRInChI().endsWith("/d+"), "Forward reaction RInChI: ");
		
		//Generate RInChI with option ForceEquilibrium		
		RInChIGenerator generatorEquilibrium = RInChIGeneratorFactory.getInstance().getRInChIGenerator(reaction, RInChIOption.FORCE_EQUILIBRIUM);
		Assertions.assertTrue(generatorEquilibrium.getRInChI().endsWith("/d="), "Equilibrium reaction RInChI: ");
				
		//Create reverse reaction and generate RInChI
		IReaction reaction2 = SilentChemObjectBuilder.getInstance().newReaction();
		reaction2.addReactant(molecule3);
		reaction2.addProduct(molecule1);
		reaction2.addProduct(molecule2);
		RInChIGenerator generator2 = RInChIGeneratorFactory.getInstance().getRInChIGenerator(reaction2);
		Assertions.assertTrue(generator2.getRInChI().endsWith("/d-"), "Backward reaction RInChI: ");
		
		//Backward, forward and equilibrium RInChIs differ only in their last char
		int n = generator1.getRInChI().length();
		Assertions.assertEquals(generator1.getRInChI().substring(0, n-1), generator2.getRInChI().substring(0, n-1),
				"Forward and backward RInChI comparison: ");
		Assertions.assertEquals(generator1.getRInChI().substring(0, n-1), generatorEquilibrium.getRInChI().substring(0, n-1),
				"Forward and equilibrium RInChI comparison: ");
		
		//Backward, forward and equilibrium RInChIs-Keys differ only in their 19-th char
		Assertions.assertEquals(generator1.getLongRInChIKey().substring(0, 18), generator2.getLongRInChIKey().substring(0, 18),
				"Forward and backward Long-RInChI-Key comparison: ");
		Assertions.assertEquals(generator1.getLongRInChIKey().substring(19), generator2.getLongRInChIKey().substring(19),
				"Forward and backward Long-RInChI-Key comparison: ");
		Assertions.assertEquals('F', generator1.getLongRInChIKey().charAt(18), "Reaction direction char in Long-RInChI-Key");
		Assertions.assertEquals('E', generatorEquilibrium.getLongRInChIKey().charAt(18), "Reaction direction char in Long-RInChI-Key");
		Assertions.assertEquals('B', generator2.getLongRInChIKey().charAt(18), "Reaction direction char in Long-RInChI-Key");
		
		//Generate back a IReaction object from RInChI
		RInChIToReaction rinchiToReaction = RInChIGeneratorFactory.getInstance().getRInChIToReaction(generator1.getRInChI());
		Assertions.assertEquals(StatusMessagesOutput.Status.SUCCESS, rinchiToReaction.getStatus(), "RInChI status: ");
		IReaction reaction3 = rinchiToReaction.getReaction();
		Assertions.assertEquals(2, reaction3.getReactantCount(), "Reactant count: ");
		Assertions.assertEquals(1, reaction3.getProductCount(), "Product count: ");
		Assertions.assertEquals(2, reaction3.getReactants().getAtomContainer(0).getAtomCount(), "Reactant 1 atom count: ");
		Assertions.assertEquals(4, reaction3.getReactants().getAtomContainer(1).getAtomCount(), "Reactant 2 atom count: ");
		Assertions.assertEquals(6, reaction3.getProducts().getAtomContainer(0).getAtomCount(), "Product atom count: ");
	}

	@Disabled("not implemented yet")
	@Test
	public void test02_benzene_kekulized() {
		//Create kekulized benzene
		IAtomContainer molecule = new AtomContainer();
		IAtom atom0 = new Atom("C");
		atom0.setImplicitHydrogenCount(1);
		atom0.setIsAromatic(true);
		molecule.addAtom(atom0);
		IAtom atom1 = new Atom("C");
		atom1.setImplicitHydrogenCount(1);
		atom1.setIsAromatic(true);
		molecule.addAtom(atom1);
		IAtom atom2 = new Atom("C");
		atom2.setImplicitHydrogenCount(1);
		atom2.setIsAromatic(true);
		molecule.addAtom(atom2);
		IAtom atom3 = new Atom("C");
		atom3.setImplicitHydrogenCount(1);
		atom3.setIsAromatic(true);
		molecule.addAtom(atom3);
		IAtom atom4 = new Atom("C");
		atom4.setImplicitHydrogenCount(1);
		atom4.setIsAromatic(true);
		molecule.addAtom(atom4);
		IAtom atom5 = new Atom("C");
		atom5.setImplicitHydrogenCount(1);
		atom5.setIsAromatic(true);
		molecule.addAtom(atom5);
		IBond bond0 = new Bond(atom0 ,atom1 ,IBond.Order.DOUBLE);
		bond0.setIsAromatic(true);
		molecule.addBond(bond0);
		IBond bond1 = new Bond(atom1 ,atom2 ,IBond.Order.SINGLE);
		bond1.setIsAromatic(true);
		molecule.addBond(bond1);
		IBond bond2 = new Bond(atom2 ,atom3 ,IBond.Order.DOUBLE);
		bond2.setIsAromatic(true);
		molecule.addBond(bond2);
		IBond bond3 = new Bond(atom3 ,atom4 ,IBond.Order.SINGLE);
		bond3.setIsAromatic(true);
		molecule.addBond(bond3);
		IBond bond4 = new Bond(atom4 ,atom5 ,IBond.Order.DOUBLE);
		bond4.setIsAromatic(true);
		molecule.addBond(bond4);
		IBond bond5 = new Bond(atom0 ,atom5 ,IBond.Order.SINGLE);
		bond5.setIsAromatic(true);
		molecule.addBond(bond5);

		//Create reaction and set benzene as a reagent
		IReaction reaction = SilentChemObjectBuilder.getInstance().newReaction();
		reaction.addReactant(molecule);

		//Generate RInChI
		RInChIGenerator generator = RInChIGeneratorFactory.getInstance().getRInChIGenerator(reaction);
		Assertions.assertEquals(RInChIGenerator.Status.SUCCESS, generator.getStatus(), "RInChI status: ");
		Assertions.assertEquals("RInChI=1.00.1S/<>C6H6/c1-2-4-6-5-3-1/h1-6H/d-", generator.getRInChI());
	}

	@Disabled("not implemented yet")
	@Test
	public void test03_benzene_aromatic() {
		//Create aromatic benzene for testing conversion of CDK bonds of type UNSET flagged as aromatic
		IAtomContainer molecule = new AtomContainer();
		IAtom atom0 = new Atom("C");
		atom0.setImplicitHydrogenCount(1);
		atom0.setIsAromatic(true);
		molecule.addAtom(atom0);
		IAtom atom1 = new Atom("C");
		atom1.setImplicitHydrogenCount(1);
		atom1.setIsAromatic(true);
		molecule.addAtom(atom1);
		IAtom atom2 = new Atom("C");
		atom2.setImplicitHydrogenCount(1);
		atom2.setIsAromatic(true);
		molecule.addAtom(atom2);
		IAtom atom3 = new Atom("C");
		atom3.setImplicitHydrogenCount(1);
		atom3.setIsAromatic(true);
		molecule.addAtom(atom3);
		IAtom atom4 = new Atom("C");
		atom4.setImplicitHydrogenCount(1);
		atom4.setIsAromatic(true);
		molecule.addAtom(atom4);
		IAtom atom5 = new Atom("C");
		atom5.setImplicitHydrogenCount(1);
		atom5.setIsAromatic(true);
		molecule.addAtom(atom5);
		IBond bond0 = new Bond(atom0 ,atom1 ,IBond.Order.UNSET);
		bond0.setIsAromatic(true);
		molecule.addBond(bond0);
		IBond bond1 = new Bond(atom1 ,atom2 ,IBond.Order.UNSET);
		bond1.setIsAromatic(true);
		molecule.addBond(bond1);
		IBond bond2 = new Bond(atom2 ,atom3 ,IBond.Order.UNSET);
		bond2.setIsAromatic(true);
		molecule.addBond(bond2);
		IBond bond3 = new Bond(atom3 ,atom4 ,IBond.Order.UNSET);
		bond3.setIsAromatic(true);
		molecule.addBond(bond3);
		IBond bond4 = new Bond(atom4 ,atom5 ,IBond.Order.UNSET);
		bond4.setIsAromatic(true);
		molecule.addBond(bond4);
		IBond bond5 = new Bond(atom0 ,atom5 ,IBond.Order.UNSET);
		bond5.setIsAromatic(true);
		molecule.addBond(bond5);

		//Create reaction and set benzene as a reagent
		IReaction reaction = SilentChemObjectBuilder.getInstance().newReaction();
		reaction.addReactant(molecule);

		//Generate RInChI
		RInChIGenerator generator = RInChIGeneratorFactory.getInstance().getRInChIGenerator(reaction);
		Assertions.assertEquals(StatusMessagesOutput.Status.SUCCESS, generator.getStatus(), "RInChI status: ");
		Assertions.assertEquals("RInChI=1.00.1S/<>C6H6/c1-2-4-6-5-3-1/h1-6H/d-", generator.getRInChI(), "RInChI for benzene: ");
	}

	@Disabled("not implemented yet")
	@Test
	public void test04_radical_doublet() {
		//Create propane doublet radical (monovalent)
		IAtomContainer molecule = new AtomContainer();
		IAtom atom0 = new Atom("C");
		atom0.setImplicitHydrogenCount(2);
		molecule.addAtom(atom0);
		IAtom atom1 = new Atom("C");
		atom1.setImplicitHydrogenCount(2);
		molecule.addAtom(atom1);
		IAtom atom2 = new Atom("C");
		atom2.setImplicitHydrogenCount(3);
		molecule.addAtom(atom2);
		IBond bond0 = new Bond(atom0 ,atom1 ,IBond.Order.SINGLE);
		molecule.addBond(bond0);
		IBond bond1 = new Bond(atom1 ,atom2 ,IBond.Order.SINGLE);
		molecule.addBond(bond1);
		//Set radical info
		atom0.setProperty(CDKConstants.SPIN_MULTIPLICITY, SPIN_MULTIPLICITY.Monovalent);
		molecule.addSingleElectron(0);
						
		//Create reaction and set propane as a reagent
		IReaction reaction = SilentChemObjectBuilder.getInstance().newReaction();
		reaction.addReactant(molecule);
		
		//Generate RInChI
		RInChIGenerator generator = RInChIGeneratorFactory.getInstance().getRInChIGenerator(reaction);
		Assertions.assertEquals(StatusMessagesOutput.Status.SUCCESS, generator.getStatus(), "RInChI status: ");
		Assertions.assertEquals("RInChI=1.00.1S/<>C3H7/c1-3-2/h1,3H2,2H3/d-", generator.getRInChI(), "RInChI for propane radical: ");
		Assertions.assertEquals("RAuxInfo=1.00.1/<>0/N:1,3,2/CRV:1d/rA:3nC.2CC/rB:s1;s2;/rC:;;;", generator.getAuxInfo(), "RAuxInfo for propane radical: ");
	}

	@Disabled("not implemented yet")
	@Test
	public void test05_radical_triplet() {
		//Create propane triple radical (divalent)
		//!!! propane singlet radical produces the same RAuxInfo (bug or feature in RInChI - unknown ??)
		IAtomContainer molecule = new AtomContainer();
		IAtom atom0 = new Atom("C");
		atom0.setImplicitHydrogenCount(1);
		molecule.addAtom(atom0);
		IAtom atom1 = new Atom("C");
		atom1.setImplicitHydrogenCount(2);
		molecule.addAtom(atom1);
		IAtom atom2 = new Atom("C");
		atom2.setImplicitHydrogenCount(3);
		molecule.addAtom(atom2);
		IBond bond0 = new Bond(atom0 ,atom1 ,IBond.Order.SINGLE);
		molecule.addBond(bond0);
		IBond bond1 = new Bond(atom1 ,atom2 ,IBond.Order.SINGLE);
		molecule.addBond(bond1);
		//Set radical info		
		atom0.setProperty(CDKConstants.SPIN_MULTIPLICITY, SPIN_MULTIPLICITY.DivalentTriplet);
		molecule.addSingleElectron(0);
		molecule.addSingleElectron(0);
						
		//Create reaction and set propane as a reagent
		IReaction reaction = SilentChemObjectBuilder.getInstance().newReaction();
		reaction.addReactant(molecule);
		
		//Generate RInChI
		RInChIGenerator generator = RInChIGeneratorFactory.getInstance().getRInChIGenerator(reaction);
		Assertions.assertEquals(RInChIGenerator.Status.SUCCESS, generator.getStatus(), "RInChI status: ");
		Assertions.assertEquals("RInChI=1.00.1S/<>C3H6/c1-3-2/h1H,3H2,2H3/d-", generator.getRInChI(), "RInChI for propane radical: ");
		Assertions.assertEquals("RAuxInfo=1.00.1/<>0/N:1,3,2/CRV:1t/rA:3nC.3CC/rB:s1;s2;/rC:;;;", generator.getAuxInfo(), "RAuxInfo for propane radical: ");
	}

}
