/* Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
import java.util.List;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.atomtype.mapper.AtomTypeMapper;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * Atom Type matcher for Sybyl atom types. It uses the {@link CDKAtomTypeMatcher}
 * for perception and then maps CDK to Sybyl atom types.
 *
 * @author         egonw
 * @cdk.created    2008-07-13
 * @cdk.module     atomtype
 * @cdk.githash
 * @cdk.keyword    atom type, Sybyl
 */
public class SybylAtomTypeMatcher implements IAtomTypeMatcher {

    private final static String                                  SYBYL_ATOM_TYPE_LIST = "org/openscience/cdk/dict/data/sybyl-atom-types.owl";
    private final static String                                  CDK_TO_SYBYL_MAP     = "org/openscience/cdk/dict/data/cdk-sybyl-mappings.owl";

    private AtomTypeFactory                                      factory;
    private CDKAtomTypeMatcher                                   cdkMatcher;
    private AtomTypeMapper                                       mapper;

    private static Map<IChemObjectBuilder, SybylAtomTypeMatcher> factories            = new Hashtable<IChemObjectBuilder, SybylAtomTypeMatcher>(
                                                                                              1);

    private SybylAtomTypeMatcher(IChemObjectBuilder builder) {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(SYBYL_ATOM_TYPE_LIST);
        factory = AtomTypeFactory.getInstance(stream, "owl", builder);
        cdkMatcher = CDKAtomTypeMatcher.getInstance(builder);
        InputStream mapStream = this.getClass().getClassLoader().getResourceAsStream(CDK_TO_SYBYL_MAP);
        mapper = AtomTypeMapper.getInstance(CDK_TO_SYBYL_MAP, mapStream);
    }

    /**
     * Returns an instance of this atom typer. It uses the given <code>builder</code> to
     * create atom type objects.
     *
     * @param builder {@link IChemObjectBuilder} to use to create {@link IAtomType} instances.
     * @return an instance of this atom type matcher.
     */
    public static SybylAtomTypeMatcher getInstance(IChemObjectBuilder builder) {
        if (!factories.containsKey(builder)) factories.put(builder, new SybylAtomTypeMatcher(builder));
        return factories.get(builder);
    }

    /** {@inheritDoc} */
    @Override
    public IAtomType[] findMatchingAtomTypes(IAtomContainer atomContainer) throws CDKException {
        for (IAtom atom : atomContainer.atoms()) {
            IAtomType type = cdkMatcher.findMatchingAtomType(atomContainer, atom);
            atom.setAtomTypeName(type == null ? null : type.getAtomTypeName());
            atom.setHybridization(type == null ? null : type.getHybridization());
        }
        Aromaticity.cdkLegacy().apply(atomContainer);
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
     *
     * @param  atomContainer the {@link IAtomContainer} in which the atom is found
     * @param  atom          the {@link IAtom} to find the atom type of
     * @return               the atom type perceived from the given atom
     */
    @Override
    public IAtomType findMatchingAtomType(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        IAtomType type = cdkMatcher.findMatchingAtomType(atomContainer, atom);
        if ("Cr".equals(atom.getSymbol())) {
            // if only I had good descriptions of the Sybyl atom types
            int neighbors = atomContainer.getConnectedBondsCount(atom);
            if (neighbors > 4 && neighbors <= 6)
                return factory.getAtomType("Cr.oh");
            else if (neighbors > 0) return factory.getAtomType("Cr.th");
        } else if ("Co".equals(atom.getSymbol())) {
            // if only I had good descriptions of the Sybyl atom types
            int neibors = atomContainer.getConnectedBondsCount(atom);
            if (neibors == 6) return factory.getAtomType("Co.oh");
        }
        if (type == null)
            return null;
        else
            atom.setAtomTypeName(type.getAtomTypeName());
        String mappedType = mapCDKToSybylType(atom);
        if (mappedType == null) return null;
        // special case: O.co2
        if (("O.3".equals(mappedType) || "O.2".equals(mappedType)) && isCarbonyl(atomContainer, atom))
            mappedType = "O.co2";
        // special case: nitrates, which can be perceived as N.2
        if ("N.2".equals(mappedType) && isNitro(atomContainer, atom)) mappedType = "N.pl3"; // based on sparse examples
        return factory.getAtomType(mappedType);
    }

    private boolean isCarbonyl(IAtomContainer atomContainer, IAtom atom) {
        List<IBond> neighbors = atomContainer.getConnectedBondsList(atom);
        if (neighbors.size() != 1) return false;
        IBond neighbor = neighbors.get(0);
        IAtom neighborAtom = neighbor.getConnectedAtom(atom);
        if (neighborAtom.getSymbol().equals("C")) {
            if (neighbor.getOrder() == IBond.Order.SINGLE) {
                if (countAttachedBonds(atomContainer, neighborAtom, IBond.Order.DOUBLE, "O") == 1) return true;
            } else if (neighbor.getOrder() == IBond.Order.DOUBLE) {
                if (countAttachedBonds(atomContainer, neighborAtom, IBond.Order.SINGLE, "O") == 1) return true;
            }
        }
        return false;
    }

    private boolean isNitro(IAtomContainer atomContainer, IAtom atom) {
        List<IAtom> neighbors = atomContainer.getConnectedAtomsList(atom);
        if (neighbors.size() != 3) return false;
        int oxygenCount = 0;
        for (IAtom neighbor : neighbors)
            if ("O".equals(neighbor.getSymbol())) oxygenCount++;
        return (oxygenCount == 2);
    }

    private int countAttachedBonds(IAtomContainer container, IAtom atom, IBond.Order order, String symbol) {
        List<IBond> neighbors = container.getConnectedBondsList(atom);
        int neighborcount = neighbors.size();
        int doubleBondedAtoms = 0;
        for (int i = neighborcount - 1; i >= 0; i--) {
            IBond bond = neighbors.get(i);
            if (bond.getOrder() == order) {
                if (bond.getAtomCount() == 2 && bond.contains(atom)) {
                    if (symbol != null) {
                        IAtom neighbor = bond.getConnectedAtom(atom);
                        if (neighbor.getSymbol().equals(symbol)) {
                            doubleBondedAtoms++;
                        }
                    } else {
                        doubleBondedAtoms++;
                    }
                }
            }
        }
        return doubleBondedAtoms;
    }

    private String mapCDKToSybylType(IAtom atom) {
        String typeName = atom.getAtomTypeName();
        if (typeName == null) return null;
        String mappedType = mapper.mapAtomType(typeName);
        if ("C.2".equals(mappedType) && atom.getFlag(CDKConstants.ISAROMATIC)) {
            mappedType = "C.ar";
        } else if ("N.2".equals(mappedType) && atom.getFlag(CDKConstants.ISAROMATIC)) {
            mappedType = "N.ar";
        } else if ("N.pl3".equals(mappedType) && atom.getFlag(CDKConstants.ISAROMATIC)) {
            mappedType = "N.ar";
        }
        return mappedType;
    }

}
