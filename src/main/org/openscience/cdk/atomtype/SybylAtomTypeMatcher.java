/* $Revision: 11555 $ $Author: egonw $ $Date: 2008-07-12 20:31:17 +0200 (Sat, 12 Jul 2008) $
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
package org.openscience.cdk.atomtype;

import java.util.Hashtable;
import java.util.Map;

import org.openscience.cdk.atomtype.mapper.AtomTypeMapper;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * Atom Type matcher for Sybyl atom types. It uses the {@link CDKAtomTypeMatcher}
 * for perception and then maps CDK to Sybyl atom types.
 *
 * @author         egonw
 * @cdk.created    2008-07-13
 * @cdk.module     atomtype
 * @cdk.svnrev     $Revision: 11555 $
 * @cdk.keyword    atom types, Sybyl
 */
public class SybylAtomTypeMatcher implements IAtomTypeMatcher {

	private AtomTypeFactory factory;
	private CDKAtomTypeMatcher cdkMatcher;
	private AtomTypeMapper mapper;

    private static Map<IChemObjectBuilder,SybylAtomTypeMatcher> 
        factories = new Hashtable<IChemObjectBuilder,SybylAtomTypeMatcher>(1); 

    private SybylAtomTypeMatcher(IChemObjectBuilder builder) {
    	factory = AtomTypeFactory.getInstance(
			"org/openscience/cdk/dict/data/sybyl-atom-types.owl",
			builder
		);
    	cdkMatcher = CDKAtomTypeMatcher.getInstance(builder);
    	mapper = AtomTypeMapper.getInstance(
            "org/openscience/cdk/dict/data/cdk-sybyl-mappings.owl"
        );
    }

    public static SybylAtomTypeMatcher getInstance(IChemObjectBuilder builder) {
    	if (!factories.containsKey(builder))
    		factories.put(builder, new SybylAtomTypeMatcher(builder));
    	return factories.get(builder);
    }

    public IAtomType findMatchingAtomType(IAtomContainer atomContainer, IAtom atom)
        throws CDKException {
        IAtomType type = cdkMatcher.findMatchingAtomType(atomContainer, atom);
        if (type == null) return null;
        String mappedType = mapper.mapAtomType(type.getAtomTypeName());
        return factory.getAtomType(mappedType);
    }
    
}

