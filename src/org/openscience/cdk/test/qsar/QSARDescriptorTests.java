/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.test.qsar;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.applications.swing.MoleculeListViewer;
import org.openscience.cdk.test.qsar.*;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test
 */
 
 public class QSARDescriptorTests {
    
    static MoleculeListViewer moleculeListViewer = null;
    
    public static Test suite() {
        TestSuite suite = new TestSuite("All QSAR Descriptor Tests");
        suite.addTest(AtomCountDescriptorTest.suite());
        suite.addTest(BondCountDescriptorTest.suite());
        suite.addTest(QsarDescriptors2DTest.suite());
        suite.addTest(RotatableBondsCountDescriptorTest.suite());
	suite.addTest(IsProtonInAromaticSystemDescriptorTest.suite());
	suite.addTest(SigmaElectronegativityDescriptorTest.suite());
	suite.addTest(AromaticAtomsCountDescriptorTest.suite());
	suite.addTest(IsProtonInConjugatedPiSystemDescriptorTest.suite());
	suite.addTest(ProtonTotalPartialChargeDescriptorTest.suite());
	suite.addTest(EffectivePolarizabilityDescriptorTest.suite());
	suite.addTest(HBondAcceptorCountDescriptorTest.suite());
	suite.addTest(HBondDonorCountDescriptorTest.suite());
	suite.addTest(ValenceConnectivityOrderZeroDescriptorTest.suite());
	suite.addTest(ValenceConnectivityOrderOneDescriptorTest.suite());
	suite.addTest(ConnectivityOrderZeroDescriptorTest.suite());
	suite.addTest(ConnectivityOrderOneDescriptorTest.suite());
        suite.addTest(ZagrebIndexDescriptorTest.suite());
	suite.addTest(GravitationalIndexDescriptorTest.suite());
	suite.addTest(BCUTDescriptorTest.suite());
	suite.addTest(WHIMDescriptorTest.suite());
        return suite;
    }
    
}
