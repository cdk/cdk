/* $Revision$ $Author$ $Date$    
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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.openscience.cdk.coverage.DiffCoverageTest;
import org.openscience.cdk.tools.diff.AtomContainerDiffTest;
import org.openscience.cdk.tools.diff.AtomDiffTest;
import org.openscience.cdk.tools.diff.AtomTypeDiffTest;
import org.openscience.cdk.tools.diff.BondDiffTest;
import org.openscience.cdk.tools.diff.ChemObjectDiffTest;
import org.openscience.cdk.tools.diff.ElectronContainerDiffTest;
import org.openscience.cdk.tools.diff.ElementDiffTest;
import org.openscience.cdk.tools.diff.IsotopeDiffTest;
import org.openscience.cdk.tools.diff.LonePairDiffTest;
import org.openscience.cdk.tools.diff.SingleElectronDiffTest;
import org.openscience.cdk.tools.diff.tree.AbstractDifferenceListTest;
import org.openscience.cdk.tools.diff.tree.AbstractDifferenceTest;
import org.openscience.cdk.tools.diff.tree.AtomTypeHybridizationDifferenceTest;
import org.openscience.cdk.tools.diff.tree.BondOrderDifferenceTest;
import org.openscience.cdk.tools.diff.tree.BooleanArrayDifferenceTest;
import org.openscience.cdk.tools.diff.tree.BooleanDifferenceTest;
import org.openscience.cdk.tools.diff.tree.ChemObjectDifferenceTest;
import org.openscience.cdk.tools.diff.tree.DoubleDifferenceTest;
import org.openscience.cdk.tools.diff.tree.IntegerDifferenceTest;
import org.openscience.cdk.tools.diff.tree.Point2dDifferenceTest;
import org.openscience.cdk.tools.diff.tree.Point3dDifferenceTest;
import org.openscience.cdk.tools.diff.tree.StringDifferenceTest;

/**
 * TestSuite that runs all the JUnit tests for the diff module.
 *
 * @cdk.module test-diff
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={
    DiffCoverageTest.class, 
        
    // cdk.tools.diff
    AtomDiffTest.class,
    AtomTypeDiffTest.class,
    ChemObjectDiffTest.class,
    ElectronContainerDiffTest.class,
    ElementDiffTest.class,
    IsotopeDiffTest.class,
    BondDiffTest.class,
    LonePairDiffTest.class,
    SingleElectronDiffTest.class,
    AtomContainerDiffTest.class,

    // cdk.tools.diff.tree
    AbstractDifferenceTest.class,
    AbstractDifferenceListTest.class,
    ChemObjectDifferenceTest.class,
    BooleanDifferenceTest.class,
    BooleanArrayDifferenceTest.class,
    DoubleDifferenceTest.class,
    IntegerDifferenceTest.class,
    StringDifferenceTest.class,
    BondOrderDifferenceTest.class,
    AtomTypeHybridizationDifferenceTest.class,
    Point2dDifferenceTest.class,
    Point3dDifferenceTest.class
})
public class MdiffTests {}
