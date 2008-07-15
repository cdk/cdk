/* $Revision: 10837 $ $Author: miguelrojasch $ $Date: 2008-05-05 22:55:01 +0200 (Mon, 05 May 2008) $
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
package org.openscience.cdk.modulesuites;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.RenderCoverageTest;
import org.openscience.cdk.renderer.color.CDKAtomColorsTest;
import org.openscience.cdk.renderer.color.CPKAtomColorsTest;

/**
 * TestSuite that runs all the tests for the CDK <code>render</code> module.
 *
 * @cdk.module  test-render
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
public class MrenderTests {
    
    public static Test suite() {
        TestSuite suite= new TestSuite("CDK render Tests");

        suite.addTest(new JUnit4TestAdapter(RenderCoverageTest.class));
        
        // from cdk.render.color
        suite.addTest(new JUnit4TestAdapter(CPKAtomColorsTest.class));
        suite.addTest(new JUnit4TestAdapter(CDKAtomColorsTest.class));

        return suite;
    }
    
}
