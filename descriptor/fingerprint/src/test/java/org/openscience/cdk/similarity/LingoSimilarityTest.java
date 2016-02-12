/*
 *
 * Copyright (C) 2010  Rajarshi Guha <rajarshi.guha@gmail.com>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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

package org.openscience.cdk.similarity;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.fingerprint.LingoFingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * @cdk.module test-fingerprint
 */
public class LingoSimilarityTest extends CDKTestCase {

    @Test
    public void testLingoSim() throws Exception {
        IAtomContainer mol1 = TestMoleculeFactory.makeIndole();
        IAtomContainer mol2 = TestMoleculeFactory.makeIndole();
        addImplicitHydrogens(mol1);
        addImplicitHydrogens(mol2);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);
        LingoFingerprinter fingerprinter = new LingoFingerprinter();
        Map<String, Integer> bs1 = fingerprinter.getRawFingerprint(mol1);
        Map<String, Integer> bs2 = fingerprinter.getRawFingerprint(mol2);
        float lingosim = LingoSimilarity.calculate(bs1, bs2);
        Assert.assertEquals(1.0, lingosim, 0.01);
    }

}
