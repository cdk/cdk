/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.forcefield.mmff;

import org.junit.Assert;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesGenerator;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;

/**
 * Ensure the atom types of the validation suite (http://server.ccl.net/cca/data/MMFF94/) are
 * correctly assigned.
 *
 * @author John May
 */
// @Category(SlowTest.class) // waiting on SlowTest patch
public class MmffAtomTypeValidationSuiteTest extends AbstractMmffAtomTypeValidationSuiteTest {

    static final MmffAtomTypeMatcher INSTANCE = new MmffAtomTypeMatcher();

    @Override
    String[] assign(IAtomContainer container) {
        return INSTANCE.symbolicTypes(container);
    }

    @Override
    void assertMatchingTypes(IAtomContainer container, String[] actual, String[] expected) {

        // create a useful failure message that displays a SMILES and tags incorrectly typed atoms
        String mesg = "";
        if (!Arrays.equals(actual, expected)) {
            for (int i = 0; i < actual.length; i++) {
                if (!expected[i].equals(actual[i])) {
                    container.getAtom(i).setProperty(CDKConstants.ATOM_ATOM_MAPPING, 1);
                }
            }
            try {
                mesg = SmilesGenerator.generic().aromatic().withAtomClasses().create(container);
            } catch (CDKException e) {
                System.err.println(e.getMessage());
            }
        }

        org.hamcrest.MatcherAssert.assertThat(mesg, actual, is(expected));
    }
}
