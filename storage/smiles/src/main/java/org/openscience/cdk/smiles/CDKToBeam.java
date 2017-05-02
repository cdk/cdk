/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
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

package org.openscience.cdk.smiles;

import com.google.common.collect.Maps;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;

import org.openscience.cdk.stereo.ExtendedTetrahedral;
import uk.ac.ebi.beam.Atom;
import uk.ac.ebi.beam.AtomBuilder;
import uk.ac.ebi.beam.Bond;
import uk.ac.ebi.beam.Element;
import uk.ac.ebi.beam.Graph;
import uk.ac.ebi.beam.Configuration;
import uk.ac.ebi.beam.Edge;
import uk.ac.ebi.beam.GraphBuilder;

import java.io.IOException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.openscience.cdk.CDKConstants.ATOM_ATOM_MAPPING;
import static org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation.TOGETHER;
import static org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo.CLOCKWISE;

/**
 * Convert a CDK {@link IAtomContainer} to a Beam graph object for generating
 * SMILES. Once converted the Beam ChemicalGraph can be manipulated further to
 * generate a standard-from SMILES and/or arrange the vertices in a canonical
 * output order.
 *
 * <b>Important:</b> The conversion respects the implicit hydrogen count and if
 * the number of implicit hydrogen ({@link IAtom#getImplicitHydrogenCount()}) is
 * null an exception will be thrown. To ensure correct conversion please ensure
 * all atoms have their implicit hydrogen count set.
 *
 * <blockquote><pre>
 * IAtomContainer m   = ...;
 *
 * // converter is thread-safe and can be used by multiple threads
 * CDKToBeam      c2g = new CDKToBeam();
 * ChemicalGraph  g   = c2g.toBeamGraph(m);
 *
 * // get the SMILES notation from the Beam graph
 * String         smi = g.toSmiles():
 * </pre></blockquote>
 *
 * @author John May
 * @cdk.module smiles
 * @cdk.keyword SMILES
 * @see <a href="http://johnmay.github.io/Beam">Beam SMILES Toolkit</a>
 */
final class CDKToBeam {

    /**
     * Whether to convert the molecule with isotope and stereo information -
     * Isomeric SMILES.
     */
    private final int flavour;

    /** Create a isomeric and aromatic converter. */
    CDKToBeam() {
        this(SmiFlavor.AtomicMass | SmiFlavor.AtomAtomMap | SmiFlavor.UseAromaticSymbols);
    }

    CDKToBeam(int flavour) {
        this.flavour = flavour;
    }

    Graph toBeamGraph(IAtomContainer ac) throws CDKException {
        return toBeamGraph(ac, flavour);
    }

    Atom toBeamAtom(IAtom atom) throws CDKException {
        return toBeamAtom(atom, flavour);
    }

    Edge toBeamEdge(IBond b, Map<IAtom, Integer> indices) throws CDKException {

        checkArgument(b.getAtomCount() == 2, "Invalid number of atoms on bond");

        int u = indices.get(b.getBegin());
        int v = indices.get(b.getEnd());

        return toBeamEdgeLabel(b, this.flavour).edge(u, v);
    }

    /**
     * Convert a CDK {@link IAtomContainer} to a Beam ChemicalGraph. The graph
     * can when be written directly as to a SMILES or manipulated further (e.g
     * canonical ordering/standard-form and other normalisations).
     *
     * @param ac an atom container instance
     * @return the Beam ChemicalGraph for additional manipulation
     */
    static Graph toBeamGraph(IAtomContainer ac, int flavour) throws CDKException {

        int order = ac.getAtomCount();

        GraphBuilder gb = GraphBuilder.create(order);
        Map<IAtom, Integer> indices = Maps.newHashMapWithExpectedSize(order);

        for (IAtom a : ac.atoms()) {
            indices.put(a, indices.size());
            gb.add(toBeamAtom(a, flavour));
        }

        for (IBond b : ac.bonds()) {
            gb.add(toBeamEdge(b, flavour, indices));
        }

        // configure stereo-chemistry by encoding the stereo-elements
        if (SmiFlavor.isSet(flavour, SmiFlavor.Stereo)) {
            for (IStereoElement se : ac.stereoElements()) {
                if (SmiFlavor.isSet(flavour, SmiFlavor.StereoTetrahedral) &&
                    se instanceof ITetrahedralChirality) {
                    addTetrahedralConfiguration((ITetrahedralChirality) se, gb, indices);
                } else if (SmiFlavor.isSet(flavour, SmiFlavor.StereoCisTrans) &&
                           se instanceof IDoubleBondStereochemistry) {
                    addGeometricConfiguration((IDoubleBondStereochemistry) se, flavour, gb, indices);
                } else if (SmiFlavor.isSet(flavour, SmiFlavor.StereoExTetrahedral) &&
                           se instanceof ExtendedTetrahedral) {
                    addExtendedTetrahedralConfiguration((ExtendedTetrahedral) se, gb, indices);
                }
            }
        }

        return gb.build();
    }

    private static Integer getMajorMassNumber(Element e) {
        try {
            switch (e) {
                case Hydrogen:   return 1;
                case Boron:      return 11;
                case Carbon:     return 12;
                case Nitrogen:   return 14;
                case Oxygen:     return 16;
                case Fluorine:   return 19;
                case Silicon:    return 28;
                case Phosphorus: return 31;
                case Sulfur:     return 32;
                case Chlorine:   return 35;
                case Iodine:     return 127;
                default:
                    IsotopeFactory isotopes = Isotopes.getInstance();
                    IIsotope       isotope  = isotopes.getMajorIsotope(e.symbol());
                    if (isotope != null)
                        return isotope.getMassNumber();
                    return null;
            }

        } catch (IOException ex) {
            throw new InternalError("Isotope factory wouldn't load: " + ex.getMessage());
        }
    }

    /**
     * Convert an CDK {@link IAtom} to a Beam Atom. The symbol and implicit
     * hydrogen count are not optional. If the symbol is not supported by the
     * SMILES notation (e.g. 'R1') the element will automatically default to
     * UNKNOWN ('*').
     *
     * @param a cdk Atom instance
     * @return a Beam atom
     * @throws NullPointerException the atom had an undefined symbol or implicit
     *                              hydrogen count
     */
    static Atom toBeamAtom(final IAtom a, final int flavour) {

        final boolean aromatic = SmiFlavor.isSet(flavour, SmiFlavor.UseAromaticSymbols) && a.getFlag(CDKConstants.ISAROMATIC);
        final Integer charge = a.getFormalCharge();
        final String symbol = checkNotNull(a.getSymbol(), "An atom had an undefined symbol");

        Element element = Element.ofSymbol(symbol);
        if (element == null) element = Element.Unknown;

        AtomBuilder ab = aromatic ? AtomBuilder.aromatic(element) : AtomBuilder.aliphatic(element);

        // CDK leaves nulls on pseudo atoms - we need to check this special case
        Integer hCount = a.getImplicitHydrogenCount();
        if (element == Element.Unknown) {
            ab.hydrogens(hCount != null ? hCount : 0);
        } else {
            ab.hydrogens(checkNotNull(hCount, "One or more atoms had an undefined number of implicit hydrogens"));
        }

        if (charge != null) ab.charge(charge);

        // use the mass number to specify isotope?
        if (SmiFlavor.isSet(flavour, SmiFlavor.AtomicMass | SmiFlavor.AtomicMassStrict)) {
            Integer massNumber = a.getMassNumber();
            if (massNumber != null) {
                if (SmiFlavor.isSet(flavour, SmiFlavor.AtomicMassStrict) ||
                        !massNumber.equals(getMajorMassNumber(element)))
                    ab.isotope(massNumber);
            }
        }

        Integer atomClass = a.getProperty(ATOM_ATOM_MAPPING);
        if (SmiFlavor.isSet(flavour, SmiFlavor.AtomAtomMap) && atomClass != null) {
            ab.atomClass(atomClass);
        }

        return ab.build();
    }

    /**
     * Convert a CDK {@link IBond} to a Beam edge.
     *
     * @param b       the CDK bond
     * @param indices map of atom indices
     * @return a Beam edge
     * @throws IllegalArgumentException the bond did not have 2 atoms or an
     *                                  unsupported order
     * @throws NullPointerException     the bond order was undefined
     */
    static Edge toBeamEdge(IBond b, int flavour, Map<IAtom, Integer> indices) throws CDKException {

        checkArgument(b.getAtomCount() == 2, "Invalid number of atoms on bond");

        int u = indices.get(b.getBegin());
        int v = indices.get(b.getEnd());

        return toBeamEdgeLabel(b, flavour).edge(u, v);
    }

    /**
     * Convert a CDK {@link IBond} to the Beam edge label type.
     *
     * @param b cdk bond
     * @return the edge label for the Beam edge
     * @throws NullPointerException     the bond order was null and the bond was
     *                                  not-aromatic
     * @throws IllegalArgumentException the bond order could not be converted
     */
    private static Bond toBeamEdgeLabel(IBond b, int flavour) throws CDKException {

        if (SmiFlavor.isSet(flavour, SmiFlavor.UseAromaticSymbols) && b.isAromatic()) {
            if (!b.getBegin().isAromatic() || !b.getEnd().isAromatic())
                throw new IllegalStateException("Aromatic bond connects non-aromatic atomic atoms");
            return Bond.AROMATIC;
        }

        if (b.getOrder() == null) throw new CDKException("A bond had undefined order, possible query bond?");

        IBond.Order order = b.getOrder();

        switch (order) {
            case SINGLE:
                return Bond.SINGLE;
            case DOUBLE:
                return Bond.DOUBLE;
            case TRIPLE:
                return Bond.TRIPLE;
            case QUADRUPLE:
                return Bond.QUADRUPLE;
            default:
                if (!SmiFlavor.isSet(flavour, SmiFlavor.UseAromaticSymbols) && b.isAromatic())
                    throw new CDKException("Cannot write Kekulé SMILES output due to aromatic bond with unset bond order - molecule should be Kekulized");
                throw new CDKException("Unsupported bond order: " + order);
        }
    }

    /**
     * Add double-bond stereo configuration to the Beam GraphBuilder.
     *
     * @param dbs     stereo element specifying double-bond configuration
     * @param gb      the current graph builder
     * @param indices atom indices
     */
    private static void addGeometricConfiguration(IDoubleBondStereochemistry dbs, int flavour, GraphBuilder gb, Map<IAtom, Integer> indices) {

        IBond db = dbs.getStereoBond();
        IBond[] bs = dbs.getBonds();

        // don't try to set a configuration on aromatic bonds
        if (SmiFlavor.isSet(flavour, SmiFlavor.UseAromaticSymbols) && db.getFlag(CDKConstants.ISAROMATIC)) return;

        int u = indices.get(db.getBegin());
        int v = indices.get(db.getEnd());

        // is bs[0] always connected to db.atom(0)?
        int x = indices.get(bs[0].getOther(db.getBegin()));
        int y = indices.get(bs[1].getOther(db.getEnd()));

        if (dbs.getStereo() == TOGETHER) {
            gb.geometric(u, v).together(x, y);
        } else {
            gb.geometric(u, v).opposite(x, y);
        }
    }

    /**
     * Add tetrahedral stereo configuration to the Beam GraphBuilder.
     *
     * @param tc      stereo element specifying tetrahedral configuration
     * @param gb      the current graph builder
     * @param indices atom indices
     */
    private static void addTetrahedralConfiguration(ITetrahedralChirality tc, GraphBuilder gb, Map<IAtom, Integer> indices) {

        IAtom[] ligands = tc.getLigands();

        int u = indices.get(tc.getChiralAtom());
        int vs[] = new int[]{indices.get(ligands[0]), indices.get(ligands[1]), indices.get(ligands[2]),
                indices.get(ligands[3])};

        gb.tetrahedral(u).lookingFrom(vs[0]).neighbors(vs[1], vs[2], vs[3])
                .winding(tc.getStereo() == CLOCKWISE ? Configuration.CLOCKWISE : Configuration.ANTI_CLOCKWISE).build();
    }

    /**
     * Add extended tetrahedral stereo configuration to the Beam GraphBuilder.
     *
     * @param et      stereo element specifying tetrahedral configuration
     * @param gb      the current graph builder
     * @param indices atom indices
     */
    private static void addExtendedTetrahedralConfiguration(ExtendedTetrahedral et, GraphBuilder gb,
            Map<IAtom, Integer> indices) {

        IAtom[] ligands = et.peripherals();

        int u = indices.get(et.focus());
        int vs[] = new int[]{indices.get(ligands[0]), indices.get(ligands[1]), indices.get(ligands[2]),
                indices.get(ligands[3])};

        gb.extendedTetrahedral(u).lookingFrom(vs[0]).neighbors(vs[1], vs[2], vs[3])
                .winding(et.winding() == CLOCKWISE ? Configuration.CLOCKWISE : Configuration.ANTI_CLOCKWISE).build();
    }
}
