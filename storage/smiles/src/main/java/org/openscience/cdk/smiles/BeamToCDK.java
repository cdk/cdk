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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.TetrahedralChirality;
import uk.ac.ebi.beam.Graph;
import uk.ac.ebi.beam.Atom;
import uk.ac.ebi.beam.Bond;
import uk.ac.ebi.beam.Configuration;
import uk.ac.ebi.beam.Edge;
import uk.ac.ebi.beam.Element;

import java.util.Arrays;

import static org.openscience.cdk.CDKConstants.ATOM_ATOM_MAPPING;
import static org.openscience.cdk.CDKConstants.ISAROMATIC;
import static org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation;
import static org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;
import static uk.ac.ebi.beam.Configuration.Type.DoubleBond;
import static uk.ac.ebi.beam.Configuration.Type.ExtendedTetrahedral;
import static uk.ac.ebi.beam.Configuration.Type.Tetrahedral;

/**
 * Convert the Beam toolkit object model to the CDK. Currently the aromatic
 * bonds from SMILES are loaded as singly bonded {@link IBond}s with the {@link
 * org.openscience.cdk.CDKConstants#ISAROMATIC} flag set.
 *
 * <blockquote><pre>
 * IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
 * ChemicalGraph      g       = ChemicalGraph.fromSmiles("CCO");
 *
 * BeamToCDK          g2c     = new BeamToCDK(builder);
 *
 * // make sure the Beam notation is expanded - this converts organic
 * // subset atoms with inferred hydrogen counts to atoms with a
 * // set implicit hydrogen property
 * IAtomContainer    ac       = g2c.toAtomContainer(Functions.expand(g));
 * </pre></blockquote>
 *
 * @author John May
 * @cdk.module smiles
 * @see <a href="http://johnmay.github.io/beam">Beam SMILES Toolkit</a>
 */
final class BeamToCDK {

    /** The builder used to create the CDK objects. */
    private final IChemObjectBuilder builder;

    /** Base atom objects for cloning - SMILES is very efficient and noticeable
     *  lag is seen using the IChemObjectBuilders. */
    private final IAtom              templateAtom;

    /** Base atom objects for cloning - SMILES is very efficient and noticeable
     *  lag is seen using the IChemObjectBuilders. */
    private final IBond              templateBond;

    /**
     * Base atom container for cloning - SMILES is very efficient and noticeable
     * lag is seen using the IChemObjectBuilders.
     */
    private final IAtomContainer     emptyContainer;

    /**
     * Create a new converter for the Beam SMILES toolkit. The converter needs
     * an {@link IChemObjectBuilder}. Currently the 'cdk-silent' builder will
     * give the best performance.
     *
     * @param builder chem object builder
     */
    BeamToCDK(IChemObjectBuilder builder) {
        this.builder = builder;
        this.templateAtom = builder.newInstance(IAtom.class);
        this.templateBond = builder.newInstance(IBond.class);
        this.emptyContainer = builder.newInstance(IAtomContainer.class, 0, 0, 0, 0);
    }

    /**
     * Convert a Beam ChemicalGraph to a CDK IAtomContainer.
     *
     * @param g Beam graph instance
     * @param kekule the input has been kekulzied
     * @return the CDK {@link IAtomContainer} for the input
     * @throws IllegalArgumentException the Beam graph was not 'expanded' - and
     *                                  contained organic subset atoms. If this
     *                                  happens use the Beam Functions.expand()
     *                                  to
     */
    IAtomContainer toAtomContainer(Graph g, boolean kekule) {

        IAtomContainer ac = emptyContainer();
        IAtom[] atoms = new IAtom[g.order()];
        IBond[] bonds = new IBond[g.size()];

        int j = 0; // bond index

        for (int i = 0; i < g.order(); i++)
            atoms[i] = toCDKAtom(g.atom(i), g.implHCount(i));
        for (Edge e : g.edges())
            bonds[j++] = toCDKBond(e, atoms, kekule);

        // atom-centric stereo-specification (only tetrahedral ATM)
        for (int u = 0; u < g.order(); u++) {

            Configuration c = g.configurationOf(u);
            if (c.type() == Tetrahedral) {

                IStereoElement se = newTetrahedral(u, g.neighbors(u), atoms, c);

                if (se != null) ac.addStereoElement(se);
            } else if (c.type() == ExtendedTetrahedral) {
                IStereoElement se = newExtendedTetrahedral(u, g, atoms);

                if (se != null) ac.addStereoElement(se);
            }
        }

        ac.setAtoms(atoms);
        ac.setBonds(bonds);

        // use directional bonds to assign bond-based stereo-specification
        addDoubleBondStereochemistry(g, ac);

        // title suffix
        ac.setProperty(CDKConstants.TITLE, g.getTitle());

        return ac;
    }

    /**
     * Adds double-bond conformations ({@link DoubleBondStereochemistry}) to the
     * atom-container.
     *
     * @param g  Beam graph object (for directional bonds)
     * @param ac The atom-container built from the Beam graph
     */
    private void addDoubleBondStereochemistry(Graph g, IAtomContainer ac) {

        for (final Edge e : g.edges()) {

            if (e.bond() != Bond.DOUBLE) continue;

            int u = e.either();
            int v = e.other(u);

            // find a directional bond for either end
            Edge first = findDirectionalEdge(g, u);
            Edge second = findDirectionalEdge(g, v);

            // if either atom is not incident to a directional label there
            // is no configuration
            if (first != null && second != null) {

                // if the directions (relative to the double bond) are the
                // same then they are on the same side - otherwise they
                // are opposite
                Conformation conformation = first.bond(u) == second.bond(v) ? Conformation.TOGETHER : Conformation.OPPOSITE;

                // get the stereo bond and build up the ligands for the
                // stereo-element - linear search could be improved with
                // map or API change to double bond element
                IBond db = ac.getBond(ac.getAtom(u), ac.getAtom(v));

                IBond[] ligands = new IBond[]{ac.getBond(ac.getAtom(u), ac.getAtom(first.other(u))),
                                              ac.getBond(ac.getAtom(v), ac.getAtom(second.other(v)))};

                ac.addStereoElement(new DoubleBondStereochemistry(db, ligands, conformation));
            }
            // extension F[C@]=[C@@]F
            else {
                Configuration uConf = g.configurationOf(u);
                Configuration vConf = g.configurationOf(v);
                if (uConf.type() == Configuration.Type.DoubleBond &&
                    vConf.type() == Configuration.Type.DoubleBond) {


                    int[] nbrs = new int[6];
                    int[] uNbrs = g.neighbors(u);
                    int[] vNbrs = g.neighbors(v);

                    if (uNbrs.length < 2 || uNbrs.length > 3)
                        continue;
                    if (vNbrs.length < 2 || vNbrs.length > 3)
                        continue;

                    int idx   = 0;
                    System.arraycopy(uNbrs, 0, nbrs, idx, uNbrs.length);
                    idx += uNbrs.length;
                    if (uNbrs.length == 2) nbrs[idx++] = u;
                    System.arraycopy(vNbrs, 0, nbrs, idx, vNbrs.length);
                    idx += vNbrs.length;
                    if (vNbrs.length == 2) nbrs[idx] = v;
                    Arrays.sort(nbrs, 0, 3);
                    Arrays.sort(nbrs, 3, 6);

                    int vPos = Arrays.binarySearch(nbrs, 0, 3, v);
                    int uPos = Arrays.binarySearch(nbrs, 3, 6, u);

                    int uhi = 0, ulo = 0;
                    int vhi = 0, vlo = 0;

                    uhi = nbrs[(vPos + 1) % 3];
                    ulo = nbrs[(vPos + 2) % 3];
                    vhi = nbrs[3 + ((uPos + 1) % 3)];
                    vlo = nbrs[3 + ((uPos + 2) % 3)];

                    if (uConf.shorthand() == Configuration.CLOCKWISE) {
                        int tmp = uhi;
                        uhi = ulo;
                        ulo = tmp;
                    }
                    if (vConf.shorthand() == Configuration.ANTI_CLOCKWISE) {
                        int tmp = vhi;
                        vhi = vlo;
                        vlo = tmp;
                    }

                    DoubleBondStereochemistry.Conformation conf = null;
                    IBond[] bonds = new IBond[2];

                    if (uhi != u) {
                        bonds[0] = ac.getBond(ac.getAtom(u), ac.getAtom(uhi));
                        if (vhi != v) {
                            // System.err.println(uhi + "\\=/" + vhi);
                            conf = Conformation.TOGETHER;
                            bonds[1] = ac.getBond(ac.getAtom(v), ac.getAtom(vhi));
                        } else if (vlo != v) {
                            // System.err.println(uhi + "\\=\\" + vlo);
                            conf = Conformation.OPPOSITE;
                            bonds[1] = ac.getBond(ac.getAtom(v), ac.getAtom(vlo));
                        }
                    } else if (ulo != u) {
                        bonds[0] = ac.getBond(ac.getAtom(u), ac.getAtom(ulo));
                        if (vhi != v) {
                            // System.err.println(ulo + "/=/" + vhi);
                            conf = Conformation.OPPOSITE;
                            bonds[1] = ac.getBond(ac.getAtom(v), ac.getAtom(vhi));
                        } else if (vlo != v) {
                            // System.err.println(ulo + "/=\\" + vlo);
                            conf = Conformation.TOGETHER;
                            bonds[1] = ac.getBond(ac.getAtom(v), ac.getAtom(vlo));
                        }
                    }

                    ac.addStereoElement(new DoubleBondStereochemistry(ac.getBond(ac.getAtom(u), ac.getAtom(v)),
                                                                      bonds,
                                                                      conf));
                }
            }
        }
    }

    /**
     * Utility for find the first directional edge incident to a vertex. If
     * there are no directional labels then null is returned.
     *
     * @param g graph from Beam
     * @param u the vertex for which to find
     * @return first directional edge (or null if none)
     */
    private Edge findDirectionalEdge(Graph g, int u) {
        for (Edge e : g.edges(u)) {
            Bond b = e.bond();
            if (b == Bond.UP || b == Bond.DOWN) return e;
        }
        return null;
    }

    /**
     * Creates a tetrahedral element for the given configuration. Currently only
     * tetrahedral centres with 4 explicit atoms are handled.
     *
     * @param u     central atom
     * @param vs    neighboring atom indices (in order)
     * @param atoms array of the CDK atoms (pre-converted)
     * @param c     the configuration of the neighbors (vs) for the order they
     *              are given
     * @return tetrahedral stereo element for addition to an atom container
     */
    private IStereoElement newTetrahedral(int u, int[] vs, IAtom[] atoms, Configuration c) {

        // no way to handle tetrahedral configurations with implicit
        // hydrogen or lone pair at the moment
        if (vs.length != 4) {

            // sanity check
            if (vs.length != 3) return null;

            // there is an implicit hydrogen (or lone-pair) we insert the
            // central atom in sorted position
            vs = insert(u, vs);
        }

        // @TH1/@TH2 = anti-clockwise and clockwise respectively
        Stereo stereo = c == Configuration.TH1 ? Stereo.ANTI_CLOCKWISE : Stereo.CLOCKWISE;

        return new TetrahedralChirality(atoms[u], new IAtom[]{atoms[vs[0]], atoms[vs[1]], atoms[vs[2]], atoms[vs[3]]},
                stereo);
    }

    private IStereoElement newExtendedTetrahedral(int u, Graph g, IAtom[] atoms) {

        int[] terminals = g.neighbors(u);
        int[] xs = new int[]{-1, terminals[0], -1, terminals[1]};

        int n = 0;
        for (Edge e : g.edges(terminals[0])) {
            if (e.bond().order() == 1) xs[n++] = e.other(terminals[0]);
        }
        n = 2;
        for (Edge e : g.edges(terminals[1])) {
            if (e.bond().order() == 1) xs[n++] = e.other(terminals[1]);
        }

        Arrays.sort(xs);

        Stereo stereo = g.configurationOf(u).shorthand() == Configuration.CLOCKWISE ? Stereo.CLOCKWISE
                : Stereo.ANTI_CLOCKWISE;

        return new org.openscience.cdk.stereo.ExtendedTetrahedral(atoms[u], new IAtom[]{atoms[xs[0]], atoms[xs[1]],
                atoms[xs[2]], atoms[xs[3]]}, stereo);
    }

    /**
     * Insert the vertex 'v' into sorted position in the array 'vs'.
     *
     * @param v  a vertex (int id)
     * @param vs array of vertices (int ids)
     * @return array with 'u' inserted in sorted order
     */
    private static int[] insert(int v, int[] vs) {

        final int n = vs.length;
        final int[] ws = Arrays.copyOf(vs, n + 1);
        ws[n] = v;

        // insert 'u' in to sorted position
        for (int i = n; i > 0 && ws[i] < ws[i - 1]; i--) {
            int tmp = ws[i];
            ws[i] = ws[i - 1];
            ws[i - 1] = tmp;
        }

        return ws;
    }

    /**
     * Create a new CDK {@link IAtom} from the Beam Atom.
     *
     * @param beamAtom an Atom from the Beam ChemicalGraph
     * @param hCount   hydrogen count for the atom
     * @return the CDK atom to have it's properties set
     */
    IAtom toCDKAtom(Atom beamAtom, int hCount) {

        IAtom cdkAtom = newCDKAtom(beamAtom);

        cdkAtom.setImplicitHydrogenCount(hCount);
        cdkAtom.setFormalCharge(beamAtom.charge());

        if (beamAtom.isotope() >= 0) cdkAtom.setMassNumber(beamAtom.isotope());

        if (beamAtom.aromatic()) cdkAtom.setIsAromatic(true);

        if (beamAtom.atomClass() > 0) cdkAtom.setProperty(ATOM_ATOM_MAPPING, beamAtom.atomClass());

        return cdkAtom;
    }

    /**
     * Create a new CDK {@link IAtom} from the Beam Atom. If the element is
     * unknown (i.e. '*') then an pseudo atom is created.
     *
     * @param atom an Atom from the Beam Graph
     * @return the CDK atom to have it's properties set
     */
    IAtom newCDKAtom(Atom atom) {
        Element element = atom.element();
        boolean unknown = element == Element.Unknown;
        if (unknown) {
            IPseudoAtom pseudoAtom = builder.newInstance(IPseudoAtom.class, element.symbol());
            pseudoAtom.setSymbol(element.symbol());
            pseudoAtom.setLabel(atom.label());
            return pseudoAtom;
        }
        return createAtom(element);
    }

    /**
     * Convert an edge from the Beam Graph to a CDK bond. Note -
     * currently aromatic bonds are set to SINGLE and then.
     *
     * @param edge  the Beam edge to convert
     * @param atoms the already converted atoms
     * @return new bond instance
     */
    IBond toCDKBond(Edge edge, IAtom[] atoms, boolean kekule) {

        int u = edge.either();
        int v = edge.other(u);

        IBond bond = createBond(atoms[u], atoms[v], toCDKBondOrder(edge));

        // switch on the edge label to set aromatic flags
        switch (edge.bond()) {
            case AROMATIC:
            case IMPLICIT_AROMATIC:
            case DOUBLE_AROMATIC:
                bond.setIsAromatic(true);
                atoms[u].setIsAromatic(true);
                atoms[v].setIsAromatic(true);
                break;
            case IMPLICIT:
                if (!kekule && atoms[u].isAromatic() && atoms[v].isAromatic()) {
                    bond.setIsAromatic(true);
                    bond.setOrder(IBond.Order.UNSET);
                    atoms[u].setIsAromatic(true);
                    atoms[v].setIsAromatic(true);
                }
                break;
        }

        return bond;
    }

    /**
     * Convert bond label on the edge to a CDK bond order - there is no aromatic
     * bond order and as such this is currently set 'SINGLE' with the aromatic
     * flag to be set.
     *
     * @param edge beam edge
     * @return CDK bond order for the edge type
     * @throws IllegalArgumentException the bond was a 'DOT' - should not be
     *                                  loaded but the exception is there
     *                                  in-case
     */
    private IBond.Order toCDKBondOrder(Edge edge) {
        switch (edge.bond()) {
            case SINGLE:
            case UP:
            case DOWN:
            case IMPLICIT: // single/aromatic - aromatic ~ single atm.
            case IMPLICIT_AROMATIC:
            case AROMATIC: // we will also set the flag
                return IBond.Order.SINGLE;
            case DOUBLE:
            case DOUBLE_AROMATIC:
                return IBond.Order.DOUBLE;
            case TRIPLE:
                return IBond.Order.TRIPLE;
            case QUADRUPLE:
                return IBond.Order.QUADRUPLE;
            default:
                throw new IllegalArgumentException("Edge label " + edge.bond()
                        + "cannot be converted to a CDK bond order");
        }
    }

    /**
     * Create a new empty atom container instance.
     * @return a new atom container instance
     */
    private IAtomContainer emptyContainer() {
        try {
            return (IAtomContainer) emptyContainer.clone();
        } catch (CloneNotSupportedException e) {
            return builder.newInstance(IAtomContainer.class, 0, 0, 0, 0);
        }
    }

    /**
     * Create a new atom for the provided symbol. The atom is created by cloning
     * an existing 'template'. Unfortunately IChemObjectBuilders really show a
     * slow down when SMILES processing.
     *
     * @param element Beam element
     * @return new atom with configured symbol and atomic number
     */
    private IAtom createAtom(Element element) {
        try {
            IAtom atom = (IAtom) templateAtom.clone();
            atom.setSymbol(element.symbol());
            atom.setAtomicNumber(element.atomicNumber());
            return atom;
        } catch (CloneNotSupportedException e) {
            // clone is always supported if overridden but just in case :-)
            return builder.newInstance(IAtom.class, element.symbol());
        }
    }

    /**
     * Create a new bond for the provided symbol. The bond is created by cloning
     * an existing 'template'. Unfortunately IChemObjectBuilders really show a
     * slow down when SMILES processing.
     *
     * @param either an atom of the bond
     * @param other another atom of the bond
     * @param order the order of the bond
     *
     * @return new bond instance
     */
    private IBond createBond(IAtom either, IAtom other, IBond.Order order) {
        try {
            IBond bond = (IBond) templateBond.clone();
            bond.setAtoms(new IAtom[]{either, other});
            bond.setOrder(order);
            return bond;
        } catch (CloneNotSupportedException e) {
            // clone is always supported if overridden but just in case  :-)
            return builder.newInstance(IBond.class, either, other, order);
        }
    }
}
