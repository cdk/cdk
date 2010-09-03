/* Copyright (C) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.modulesuites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.openscience.cdk.coverage.RenderbasicCoverageTest;
import org.openscience.cdk.renderer.AtomContainerRendererTest;
import org.openscience.cdk.renderer.elements.PathElementTest;
import org.openscience.cdk.renderer.elements.WedgeLineElementTest;
import org.openscience.cdk.renderer.generators.BasicAtomGeneratorTest;
import org.openscience.cdk.renderer.generators.BasicBondGeneratorTest;

/**
 * TestSuite that runs all the tests for the CDK <code>renderbasic</code>
 * module.
 *
 * @cdk.module  test-renderbasic
 */
@RunWith(Suite.class)
@SuiteClasses({
	RenderbasicCoverageTest.class,
	AtomContainerRendererTest.class,
	BasicAtomGeneratorTest.class,
	BasicBondGeneratorTest.class,
	WedgeLineElementTest.class,
	PathElementTest.class
})
public class MrenderbasicTests {}
