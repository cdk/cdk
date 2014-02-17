/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
import org.openscience.cdk.coverage.CipCoverageTest;
import org.openscience.cdk.geometry.cip.CIPSMILESTest;
import org.openscience.cdk.geometry.cip.CIPToolTest;
import org.openscience.cdk.geometry.cip.ImmutableHydrogenTest;
import org.openscience.cdk.geometry.cip.ImplicitHydrogenLigandTest;
import org.openscience.cdk.geometry.cip.LigancyFourChiralityTest;
import org.openscience.cdk.geometry.cip.LigandTest;
import org.openscience.cdk.geometry.cip.TerminalLigandTest;
import org.openscience.cdk.geometry.cip.VisitedAtomsTest;
import org.openscience.cdk.geometry.cip.rules.AtomicNumberRuleTest;
import org.openscience.cdk.geometry.cip.rules.CIPLigandRuleTest;
import org.openscience.cdk.geometry.cip.rules.CombinedAtomicMassNumberRuleTest;
import org.openscience.cdk.geometry.cip.rules.MassNumberRuleTest;

/**
 * TestSuite that runs all the tests for the CDK cip module.
 *
 * @cdk.module  test-cip
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={
    CipCoverageTest.class,
    CIPToolTest.class,
    LigandTest.class,
    TerminalLigandTest.class,
    ImplicitHydrogenLigandTest.class,
    ImmutableHydrogenTest.class,
    LigancyFourChiralityTest.class,
    AtomicNumberRuleTest.class,
    CIPLigandRuleTest.class,
    MassNumberRuleTest.class,
    CombinedAtomicMassNumberRuleTest.class,
    CIPSMILESTest.class,
    VisitedAtomsTest.class
})
public class McipTests {}
