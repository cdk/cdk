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

	@Test void makePhenylAmine() { Assertions.assertNotNull(TestMoleculeFactory.makePhenylAmine()); }
	@Test void makeFusedRings() { Assertions.assertNotNull(TestMoleculeFactory.makeFusedRings()); }
	@Test void makePyrroleAnion() { Assertions.assertNotNull(TestMoleculeFactory.makePyrroleAnion()); }
	@Test void makePyrazole() { Assertions.assertNotNull(TestMoleculeFactory.makePyrazole()); }
	@Test void make124Triazole() { Assertions.assertNotNull(TestMoleculeFactory.make124Triazole()); }
	@Test void makeOxazole() { Assertions.assertNotNull(TestMoleculeFactory.makeOxazole()); }
	@Test void makeIsoxazole() { Assertions.assertNotNull(TestMoleculeFactory.makeIsoxazole()); }
	@Test void makeIsothiazole() { Assertions.assertNotNull(TestMoleculeFactory.makeIsothiazole()); }
	@Test void makeThiadiazole() { Assertions.assertNotNull(TestMoleculeFactory.makeThiadiazole()); }
	@Test void makeOxadiazole() { Assertions.assertNotNull(TestMoleculeFactory.makeOxadiazole()); }
	@Test void makePyridazine() { Assertions.assertNotNull(TestMoleculeFactory.makePyridazine()); }
	@Test void makeTriazine() { Assertions.assertNotNull(TestMoleculeFactory.makeTriazine()); }
	@Test void makeSingleRing() { Assertions.assertNotNull(TestMoleculeFactory.makeSingleRing()); }
	@Test void makePiperidine() { Assertions.assertNotNull(TestMoleculeFactory.makePiperidine()); }
	@Test void makeTetrahydropyran() { Assertions.assertNotNull(TestMoleculeFactory.makeTetrahydropyran()); }
	@Test void makeAnthracene() { Assertions.assertNotNull(TestMoleculeFactory.makeAnthracene()); }
	@Test void makeCyclophaneLike() { Assertions.assertNotNull(TestMoleculeFactory.makeCyclophaneLike()); }
	@Test void makeGappedCyclophaneLike() { Assertions.assertNotNull(TestMoleculeFactory.makeGappedCyclophaneLike()); }

}
