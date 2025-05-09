/* Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.atomtype.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * This class tests the mapper that maps CDK atom types to other atom type
 * schemes.
 *
 */
class AtomTypeMapperTest {

    @Test
    void testGetInstance_String() {
        AtomTypeMapper mapper = AtomTypeMapper.getInstance("org/openscience/cdk/dict/data/cdk-sybyl-mappings.owl");
        Assertions.assertNotNull(mapper);
    }

    @Test
    void testGetInstance_String_InputStream() {
        AtomTypeMapper mapper = AtomTypeMapper.getInstance("org/openscience/cdk/dict/data/cdk-sybyl-mappings.owl", this
                .getClass().getClassLoader()
                .getResourceAsStream("org/openscience/cdk/dict/data/cdk-sybyl-mappings.owl"));
        Assertions.assertNotNull(mapper);
    }

    @Test
    void testGetMapping() {
        final String mapping = "org/openscience/cdk/dict/data/cdk-sybyl-mappings.owl";
        AtomTypeMapper mapper = AtomTypeMapper.getInstance(mapping);
        Assertions.assertNotNull(mapper);
        Assertions.assertEquals(mapping, mapper.getMapping());
    }

    @Test
    void testMapAtomType_String() {
        final String mapping = "org/openscience/cdk/dict/data/cdk-sybyl-mappings.owl";
        AtomTypeMapper mapper = AtomTypeMapper.getInstance(mapping);
        Assertions.assertNotNull(mapper);
        Assertions.assertEquals("C.3", mapper.mapAtomType("C.sp3"));
    }

}
