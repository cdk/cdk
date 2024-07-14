/* Copyright (C) 2024  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.templates;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests minimal functionality of methods not tested elsewhere.
 */
public class TestMoleculeFactoryTest {

	@Test public void makePhenylAmine() { Assertions.assertNotNull(TestMoleculeFactory.makePhenylAmine()); }
	@Test public void makeFusedRings() { Assertions.assertNotNull(TestMoleculeFactory.makeFusedRings()); }
	@Test public void makePyrroleAnion() { Assertions.assertNotNull(TestMoleculeFactory.makePyrroleAnion()); }
	@Test public void makePyrazole() { Assertions.assertNotNull(TestMoleculeFactory.makePyrazole()); }
	@Test public void make124Triazole() { Assertions.assertNotNull(TestMoleculeFactory.make124Triazole()); }
	@Test public void makeOxazole() { Assertions.assertNotNull(TestMoleculeFactory.makeOxazole()); }
	@Test public void makeIsoxazole() { Assertions.assertNotNull(TestMoleculeFactory.makeIsoxazole()); }
	@Test public void makeIsothiazole() { Assertions.assertNotNull(TestMoleculeFactory.makeIsothiazole()); }
	@Test public void makeThiadiazole() { Assertions.assertNotNull(TestMoleculeFactory.makeThiadiazole()); }
	@Test public void makeOxadiazole() { Assertions.assertNotNull(TestMoleculeFactory.makeOxadiazole()); }
	@Test public void makePyridazine() { Assertions.assertNotNull(TestMoleculeFactory.makePyridazine()); }
	@Test public void makeTriazine() { Assertions.assertNotNull(TestMoleculeFactory.makeTriazine()); }
	@Test public void makeSingleRing() { Assertions.assertNotNull(TestMoleculeFactory.makeSingleRing()); }
	@Test public void makePiperidine() { Assertions.assertNotNull(TestMoleculeFactory.makePiperidine()); }
	@Test public void makeTetrahydropyran() { Assertions.assertNotNull(TestMoleculeFactory.makeTetrahydropyran()); }
	@Test public void makeAnthracene() { Assertions.assertNotNull(TestMoleculeFactory.makeAnthracene()); }
	@Test public void makeCyclophaneLike() { Assertions.assertNotNull(TestMoleculeFactory.makeCyclophaneLike()); }
	@Test public void makeGappedCyclophaneLike() { Assertions.assertNotNull(TestMoleculeFactory.makeGappedCyclophaneLike()); }

}
