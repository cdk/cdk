/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, version 2.1.
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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.atomtypes.OWLAtomTypeMappingReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * An <code>AtomTypeMapper</code> allows the mapping of atom types between atom type
 * schemes. For example, it allows to convert atom types from the CDK scheme to the
 * Sybyl scheme; using this approach it is possible to use the CDK atom type perception
 * algorithm and write the resulting atom types using the Sybyl atom type scheme.
 * 
 * @cdk.module atomtype
 */
@TestClass("org.openscience.cdk.atomtype.mapper.AtomTypeMapperTest")
public class AtomTypeMapper {

	private static Map<String,AtomTypeMapper> mappers = new HashMap<String,AtomTypeMapper>();
	
	private String mappingFile;
	
	private Map<String,String> mappings;
	
	private AtomTypeMapper(String mappingFile) {
		this.mappingFile = mappingFile;
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream(mappingFile);
		OWLAtomTypeMappingReader reader = new OWLAtomTypeMappingReader(
			new InputStreamReader(stream)
		);
		mappings = reader.readAtomTypeMappings();
	}

  private AtomTypeMapper(String mappingFile, InputStream stream) {
      this.mappingFile = mappingFile;
      OWLAtomTypeMappingReader reader = new OWLAtomTypeMappingReader(
        new InputStreamReader(stream)
      );
      mappings = reader.readAtomTypeMappings();
    }

	/**
	 * Instantiates an atom type to atom type mapping, based on the given mapping file.
	 * For example, the mapping file <code>org.openscience.cdk.config.data.cdk-sybyl-mappings.owl</code>
	 * which defines how CDK atom types are mapped to Sybyl atom types.
	 * 
	 * @param  mappingFile File name of the OWL file defining the atom type to atom type mappings.
	 * @return             An instance of AtomTypeMapper for the given mapping file.
	 */
	@TestMethod("testGetInstance_String")
	public static AtomTypeMapper getInstance(String mappingFile) {
		if (!mappers.containsKey(mappingFile)) {
			mappers.put(mappingFile, new AtomTypeMapper(mappingFile));
		}
		return mappers.get(mappingFile);
	}

    @TestMethod("testGetInstance_String_InputStream")
    public static AtomTypeMapper getInstance(String mappingFile, InputStream stream) {
        if (!mappers.containsKey(mappingFile)) {
            mappers.put(mappingFile, new AtomTypeMapper(mappingFile, stream));
        }
        return mappers.get(mappingFile);
    }

  @TestMethod("testMapAtomType_String")
	public String mapAtomType(String type) {
		return mappings.get(type);
	}

	@TestMethod("testGetMapping")
	public String getMapping() {
		return mappingFile;
	}

}
