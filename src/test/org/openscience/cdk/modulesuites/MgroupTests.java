/* Copyright (C) 2012  Gilleain Torrance <gilleain.torrance@gmail.com>
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
import org.openscience.cdk.group.AbstractDiscretePartitionRefinerTest;
import org.openscience.cdk.group.AtomDiscretePartitionRefinerTest;
import org.openscience.cdk.group.AtomEquitablePartitionRefinerTest;
import org.openscience.cdk.group.AtomGroupTests;
import org.openscience.cdk.group.AtomPermutationTests;
import org.openscience.cdk.group.BondDiscretePartitionRefinerTest;
import org.openscience.cdk.group.BondEquitablePartitionRefinerTest;
import org.openscience.cdk.group.BondGroupTests;
import org.openscience.cdk.group.DisjointSetForestTest;
import org.openscience.cdk.group.PartitionTest;
import org.openscience.cdk.group.PermutationGroupTest;
import org.openscience.cdk.group.PermutationTest;

/**
 * TestSuite that runs all the tests for the CDK <code>group</code> module.
 *
 * @cdk.module test-group
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={
        AbstractDiscretePartitionRefinerTest.class,
        AtomDiscretePartitionRefinerTest.class,
        AtomEquitablePartitionRefinerTest.class,
        AtomGroupTests.class,
        AtomPermutationTests.class,
        BondDiscretePartitionRefinerTest.class,
        BondEquitablePartitionRefinerTest.class,
        BondGroupTests.class,
        DisjointSetForestTest.class,
        PartitionTest.class,
        PermutationTest.class,
        PermutationGroupTest.class
})
public class MgroupTests {}
