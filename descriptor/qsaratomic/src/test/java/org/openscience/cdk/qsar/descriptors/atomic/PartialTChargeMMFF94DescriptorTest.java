/* Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.qsar.descriptors.atomic;

import org.junit.Before;

/**
 * See tests in MmffTest.
 *
 * @cdk.module test-qsaratomic
 * @cdk.bug 1627763
 */
public class PartialTChargeMMFF94DescriptorTest extends AtomicDescriptorTest {

    /**
     * Constructor for the PartialTChargeMMFF94DescriptorTest object
     *
     * All values taken from table V of Merck Molecular Force Field. II. Thomas
     * A. Halgren DOI:
     * 10.1002/(SICI)1096-987X(199604)17:5/6<520::AID-JCC2>3.0.CO;2-W
     *
     */
    public PartialTChargeMMFF94DescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(PartialTChargeMMFF94Descriptor.class);
    }
}
