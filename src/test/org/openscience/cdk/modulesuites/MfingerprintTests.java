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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.openscience.cdk.coverage.FingerprintCoverageTest;
import org.openscience.cdk.fingerprint.EStateFingerprinterTest;
import org.openscience.cdk.fingerprint.ExtendedFingerprinterTest;
import org.openscience.cdk.fingerprint.KlekotaRothFingerprinterTest;
import org.openscience.cdk.fingerprint.MACCSFingerprinterTest;
import org.openscience.cdk.fingerprint.PubchemFingerprinterTest;
import org.openscience.cdk.similarity.DistanceMomentTest;
import org.openscience.cdk.similarity.LingoSimilarityTest;
import org.openscience.cdk.similarity.TanimotoTest;

/**
 * TestSuite that runs all the sample tests.
 *
 * @cdk.module  test-fingerprint
 * @cdk.depends junit.jar
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={
    FingerprintCoverageTest.class,
    ExtendedFingerprinterTest.class,
    MACCSFingerprinterTest.class,
    EStateFingerprinterTest.class,
    PubchemFingerprinterTest.class,
    DistanceMomentTest.class,
    TanimotoTest.class,
    LingoSimilarityTest.class,
    KlekotaRothFingerprinterTest.class
})
public class MfingerprintTests {}
