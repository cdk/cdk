/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
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
import org.openscience.cdk.coverage.SignatureCoverageTest;
import org.openscience.cdk.fingerprint.SignatureFingerprinterTest;
import org.openscience.cdk.signature.AtomSignatureTest;
import org.openscience.cdk.signature.MoleculeFromSignatureBuilderTest;
import org.openscience.cdk.signature.MoleculeSignatureTest;
import org.openscience.cdk.signature.OrbitTest;
import org.openscience.cdk.signature.SignatureQuotientGraphTest;

/**
 * TestSuite that runs all the tests for the CDK <code>signature</code> module.
 *
 * @cdk.module  test-signature
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={
    SignatureCoverageTest.class,
    SignatureQuotientGraphTest.class,
    OrbitTest.class,
    MoleculeSignatureTest.class,
    MoleculeFromSignatureBuilderTest.class,
    AtomSignatureTest.class,
    SignatureFingerprinterTest.class
})
public class MsignatureTests {}
