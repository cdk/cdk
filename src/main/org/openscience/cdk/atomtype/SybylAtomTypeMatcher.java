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

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
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
@TestClass("org.openscience.cdk.atomtype.SybylAtomTypeMatcherTest")
public class SybylAtomTypeMatcher implements IAtomTypeMatcher {

    private final static String SYBYL_ATOM_TYPE_LIST = "org/openscience/cdk/dict/data/sybyl-atom-types.owl";
    private final static String CDK_TO_SYBYL_MAP = "org/openscience/cdk/dict/data/cdk-sybyl-mappings.owl";
    
	private AtomTypeFactory factory;
	private CDKAtomTypeMatcher cdkMatcher;
	private AtomTypeMapper mapper;

    private static Map<IChemObjectBuilder,SybylAtomTypeMatcher> 
        factories = new Hashtable<IChemObjectBuilder,SybylAtomTypeMatcher>(1); 

    private SybylAtomTypeMatcher(IChemObjectBuilder builder) {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(SYBYL_ATOM_TYPE_LIST);
        factory = AtomTypeFactory.getInstance(stream, "owl", builder);
        cdkMatcher = CDKAtomTypeMatcher.getInstance(builder);
        InputStream mapStream = this.getClass().getClassLoader().getResourceAsStream(CDK_TO_SYBYL_MAP);
        mapper = AtomTypeMapper.getInstance(CDK_TO_SYBYL_MAP, mapStream);
    }

    @TestMethod("testGetInstance_IChemObjectBuilder")
    public static SybylAtomTypeMatcher getInstance(IChemObjectBuilder builder) {
    	if (!factories.containsKey(builder))
    		factories.put(builder, new SybylAtomTypeMatcher(builder));
    	return factories.get(builder);
    }

    @TestMethod("testFindMatchingAtomType_IAtomContainer")
    public IAtomType[] findMatchingAtomType(IAtomContainer atomContainer) throws CDKException {
        for (IAtom atom : atomContainer.atoms()) {
            IAtomType type = cdkMatcher.findMatchingAtomType(atomContainer, atom);
            atom.setAtomTypeName(type == null ? null : type.getAtomTypeName());
            atom.setHybridization(type == null ? null : type.getHybridization());
        }
        CDKHueckelAromaticityDetector.detectAromaticity(atomContainer);
        IAtomType[] types = new IAtomType[atomContainer.getAtomCount()];
        int typeCounter = 0;
        for (IAtom atom : atomContainer.atoms()) {
            String mappedType = mapCDKToSybylType(atom);
            if (mappedType == null) {
                types[typeCounter] = null;
            } else {
                types[typeCounter] = factory.getAtomType(mappedType);
            }
            typeCounter++;
        }
        return types;
    }

    /**
     * Sybyl atom type perception for a single atom. The molecular property <i>aromaticity</i> is not perceived; 
     * Aromatic carbons will, therefore, be perceived as <i>C.2</i> and not <i>C.ar</i>. If the latter is
     * required, please use findMatchingAtomType(IAtomContainer) instead.
     */
    @TestMethod("testFindMatchingAtomType_IAtomContainer_IAtom")
    public IAtomType findMatchingAtomType(IAtomContainer atomContainer, IAtom atom)
        throws CDKException {
        IAtomType type = cdkMatcher.findMatchingAtomType(atomContainer, atom);
        if (type == null) return null;
        String mappedType = mapCDKToSybylType(type);
        if (mappedType == null) return null;
        return factory.getAtomType(mappedType);
    }
    
    private String mapCDKToSybylType(IAtomType atom) {
        String typeName = atom.getAtomTypeName();
        if (typeName == null) return null;
        String mappedType = mapper.mapAtomType(typeName);
        if ("C.2".equals(mappedType) && atom.getFlag(CDKConstants.ISAROMATIC)) {
            mappedType = "C.ar";
        } else if ("N.pl3".equals(mappedType) && atom.getFlag(CDKConstants.ISAROMATIC)) {
            mappedType = "N.ar";
        }
        return mappedType;
    }
    
}

