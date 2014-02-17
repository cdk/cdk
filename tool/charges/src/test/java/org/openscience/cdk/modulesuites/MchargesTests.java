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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.openscience.cdk.charges.AtomTypeChargesTest;
import org.openscience.cdk.charges.GasteigerMarsiliPartialChargesTest;
import org.openscience.cdk.charges.InductivePartialChargesTest;
import org.openscience.cdk.charges.PolarizabilityTest;
import org.openscience.cdk.coverage.ChargesCoverageTest;

/**
 * TestSuite that runs all the sample tests for the charges module.
 *
 * @cdk.module test-charges
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={
    ChargesCoverageTest.class,
    GasteigerMarsiliPartialChargesTest.class,
    InductivePartialChargesTest.class,
    PolarizabilityTest.class,
    AtomTypeChargesTest.class
})
public class MchargesTests {}
