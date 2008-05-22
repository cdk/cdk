/* $Revision$ $Author$ $Date$    
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

package org.openscience.cdk.modulesuites;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.FormulaCoverageTest;
import org.openscience.cdk.formula.IsotopePatternGeneratorTest;
import org.openscience.cdk.formula.MassToFormulaToolTest;
import org.openscience.cdk.formula.MolecularFormulaManipulatorTest;
import org.openscience.cdk.formula.MolecularFormulaRangeManipulatorTest;
import org.openscience.cdk.formula.MolecularFormulaRangeTest;
import org.openscience.cdk.formula.MolecularFormulaSetManipulatorTest;

/**
 * TestSuite that runs all the JUnit tests for the formula module.
 *
 * @cdk.module test-formula
 */
public class MformulaTests {

    public static Test suite () {
        TestSuite suite= new TestSuite("The CDK formula module Tests");
        
        suite.addTest(new JUnit4TestAdapter(FormulaCoverageTest.class));	
        
        suite.addTest(new JUnit4TestAdapter(IsotopePatternGeneratorTest.class));
        suite.addTest(new JUnit4TestAdapter(MassToFormulaToolTest.class));
        suite.addTest(new JUnit4TestAdapter(MolecularFormulaManipulatorTest.class));
        suite.addTest(new JUnit4TestAdapter(MolecularFormulaRangeManipulatorTest.class));
        suite.addTest(new JUnit4TestAdapter(MolecularFormulaRangeTest.class));
        suite.addTest(new JUnit4TestAdapter(MolecularFormulaSetManipulatorTest.class));

        return suite;
    }

}
