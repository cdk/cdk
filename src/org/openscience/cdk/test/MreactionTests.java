/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-03-30 02:18:21 +0200 (Thu, 30 Mar 2006) $
 * $Revision: 5867 $
 *
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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

package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.reaction.type.DisplacementChargeFromAcceptorReactionTest;
import org.openscience.cdk.test.reaction.type.DisplacementChargeFromDonorReactionTest;
import org.openscience.cdk.test.reaction.type.ElectronImpactNBEReactionTest;
import org.openscience.cdk.test.reaction.type.ElectronImpactPDBReactionTest;
import org.openscience.cdk.test.reaction.type.ReactionBalancerTest;
import org.openscience.cdk.test.reaction.type.ReactionTypeTest;
import org.openscience.cdk.test.reaction.type.RearrangementAnion1ReactionTest;
import org.openscience.cdk.test.reaction.type.RearrangementAnion2ReactionTest;
import org.openscience.cdk.test.reaction.type.RearrangementAnion3ReactionTest;
import org.openscience.cdk.test.reaction.type.RearrangementCation1ReactionTest;
import org.openscience.cdk.test.reaction.type.RearrangementCation2ReactionTest;
import org.openscience.cdk.test.reaction.type.RearrangementCation3ReactionTest;
import org.openscience.cdk.test.reaction.type.RearrangementRadical1ReactionTest;
import org.openscience.cdk.test.reaction.type.RearrangementRadical2ReactionTest;
import org.openscience.cdk.test.reaction.type.RearrangementRadical3ReactionTest;

/**
 * TestSuite that runs all the tests for the CDK reaction module.
 *
 * @cdk.module  test-reaction
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
public class MreactionTests {
    
    public static Test suite() {
        TestSuite suite= new TestSuite("CDK standard Tests");

        // make sure to check it agains src/test-reaction.files
        // before each release!
        suite.addTest(ReactionTypeTest.suite());
        
        return suite;
    }
    
}
