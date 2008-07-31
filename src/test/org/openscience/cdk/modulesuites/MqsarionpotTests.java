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

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.coverage.QsarionpotCoverageTest;
import org.openscience.cdk.qsar.descriptors.atomic.IPAtomicHOSEDescriptorTest;
import org.openscience.cdk.qsar.descriptors.atomic.IPAtomicLearningDescriptorTest;
import org.openscience.cdk.qsar.descriptors.bond.IPBondLearningDescriptorTest;
import org.openscience.cdk.qsar.descriptors.molecular.IPMolecularLearningDescriptorTest;

/**
 * TestSuite that runs all the sample tests for the qsarionpot module.
 *
 * @cdk.module  test-qsarionpot
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
public class MqsarionpotTests {

    public static Test suite() {

        TestSuite suite = new TestSuite("All QSAR ionpot Tests");

        suite.addTest(new JUnit4TestAdapter(QsarionpotCoverageTest.class));	

        suite.addTest(IPAtomicLearningDescriptorTest.suite());
        suite.addTest(IPBondLearningDescriptorTest.suite());
        suite.addTest(IPMolecularLearningDescriptorTest.suite());
        
        return suite;
    }

}
