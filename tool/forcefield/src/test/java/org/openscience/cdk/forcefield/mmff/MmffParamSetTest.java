/*
 * Copyright (c) 2015 John May <jwmay@users.sf.net>
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

import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author John May
 */
public class MmffParamSetTest {

    public static final MmffParamSet mmffParams = MmffParamSet.INSTANCE;

    @Test public void formalCharge() {
        assertThat(mmffParams.getFormalCharge("NCN+"),
                   is(new BigDecimal("0.500")));
    }

    @Test public void formalChargeAdjustment() {
        assertThat(mmffParams.getFormalChargeAdjustment(32),
                   is(new BigDecimal("0.500")));
    }

    @Test public void crd() {
        assertThat(mmffParams.getCrd(32), is(1));
    }
    
    @Test public void bciBetween1And18() {
        assertThat(mmffParams.getBondChargeIncrement(0, 1, 18),
                   is(new BigDecimal("-0.1052")));
    }

    @Test public void bciBetween18And1() {
        assertThat(mmffParams.getBondChargeIncrement(0, 18, 1),
                   is(new BigDecimal("0.1052")));
    }
    
    @Test public void bciBetween37And63WithBondClass() {
        assertThat(mmffParams.getBondChargeIncrement(0, 37, 63),
                   is(new BigDecimal("0.0000")));
        assertThat(mmffParams.getBondChargeIncrement(1, 37, 63),
                   is(new BigDecimal("-0.0530")));
    }         
}
