/* $Revision: 11781 $ $Author: egonw $ $Date: 2008-07-27 08:41:04 +0200 (Sun, 27 Jul 2008) $
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

import org.openscience.cdk.FingerprintCoverageTest;
import org.openscience.cdk.fingerprint.EStateFingerprinterTest;
import org.openscience.cdk.fingerprint.ExtendedFingerprinterTest;
import org.openscience.cdk.fingerprint.MACCSFingerprinterTest;

/**
 * TestSuite that runs all the sample tests.
 *
 * @cdk.module  test-fingerprint
 * @cdk.depends junit.jar
 */
public class MfingerprintTests {

    public static Test suite() {
        TestSuite suite= new TestSuite("All CDK fingerprint Tests");

        suite.addTest(FingerprintCoverageTest.suite());
        
        suite.addTest(new JUnit4TestAdapter(ExtendedFingerprinterTest.class));
        suite.addTest(new JUnit4TestAdapter(MACCSFingerprinterTest.class));
        suite.addTest(new JUnit4TestAdapter(EStateFingerprinterTest.class));

        return suite;
    }

}
