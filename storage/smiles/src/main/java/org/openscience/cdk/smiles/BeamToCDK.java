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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.ExtendedCisTrans;
import org.openscience.cdk.stereo.Octahedral;
import org.openscience.cdk.stereo.SquarePlanar;
import org.openscience.cdk.stereo.TetrahedralChirality;
import org.openscience.cdk.stereo.TrigonalBipyramidal;
import uk.ac.ebi.beam.Atom;
import uk.ac.ebi.beam.Bond;
import uk.ac.ebi.beam.Configuration;
import uk.ac.ebi.beam.Edge;
import uk.ac.ebi.beam.Element;
import uk.ac.ebi.beam.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.openscience.cdk.CDKConstants.ATOM_ATOM_MAPPING;
import static org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation;
import static org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;

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

        IAtomContainer ac    = emptyContainer();
        int            numAtoms = g.order();
        IAtom[]        atoms = new IAtom[numAtoms];
        IBond[]        bonds = new IBond[g.size()];

        int j = 0; // bond index

        boolean checkAtomStereo = false;
        boolean checkBondStereo = false;

        for (int i = 0; i < g.order(); i++) {
            checkAtomStereo = checkAtomStereo || g.configurationOf(i).type() != Configuration.Type.None;
            atoms[i] = toCDKAtom(g.atom(i), g.implHCount(i));
        }
        ac.setAtoms(atoms);
        // get the atom-refs
        for (int i = 0; i < g.order(); i++)
            atoms[i] = ac.getAtom(i);
        for (Edge edge : g.edges()) {

            final int u = edge.either();
            final int v = edge.other(u);
            IBond bond = builder.newBond();
            bond.setAtoms(new IAtom[]{atoms[u], atoms[v]});
            bonds[j++] = bond;

            switch (edge.bond()) {
                case SINGLE:
                    bond.setOrder(IBond.Order.SINGLE);
                    break;
                case UP:
                case DOWN:
                    checkBondStereo = true;
                    bond.setOrder(IBond.Order.SINGLE);
                    break;
                case IMPLICIT:
                    bond.setOrder(IBond.Order.SINGLE);
                    if (!kekule && atoms[u].isAromatic() && atoms[v].isAromatic()) {
                        bond.setIsAromatic(true);
                        bond.setOrder(IBond.Order.UNSET);
                        atoms[u].setIsAromatic(true);
                        atoms[v].setIsAromatic(true);
                    }
                    break;
                case IMPLICIT_AROMATIC:
                case AROMATIC:
                    bond.setOrder(IBond.Order.SINGLE);
                    bond.setIsAromatic(true);
                    atoms[u].setIsAromatic(true);
                    atoms[v].setIsAromatic(true);
                    break;
                case DOUBLE:
                    bond.setOrder(IBond.Order.DOUBLE);
                    break;
                case DOUBLE_AROMATIC:
                    bond.setOrder(IBond.Order.DOUBLE);
                    bond.setIsAromatic(true);
                    atoms[u].setIsAromatic(true);
                    atoms[v].setIsAromatic(true);
                    break;
                case TRIPLE:
                    bond.setOrder(IBond.Order.TRIPLE);
                    break;
                case QUADRUPLE:
                    bond.setOrder(IBond.Order.QUADRUPLE);
                    break;
                default:
                    throw new IllegalArgumentException("Edge label " + edge.bond()
                                                       + "cannot be converted to a CDK bond order");
            }
        }

        // atom-centric stereo-specification (only tetrahedral ATM)
        if (checkAtomStereo) {
            for (int u = 0; u < g.order(); u++) {

                Configuration c = g.configurationOf(u);
                switch (c.type()) {
                    case Tetrahedral: {

                        IStereoElement se = newTetrahedral(u, g.neighbors(u), atoms, c);

                        if (se != null) ac.addStereoElement(se);
                        break;
                    }
                    case ExtendedTetrahedral: {
                        IStereoElement se = newExtendedTetrahedral(u, g, atoms);
                        if (se != null) ac.addStereoElement(se);
                        break;
                    }
                    case DoubleBond: {
                        checkBondStereo = true;
                        break;
                    }
                    case SquarePlanar: {
                        IStereoElement se = newSquarePlanar(u, g.neighbors(u), atoms, c);
                        if (se != null) ac.addStereoElement(se);
                        break;
                    }
                    case TrigonalBipyramidal: {
                        IStereoElement se = newTrigonalBipyramidal(u, g.neighbors(u), atoms, c);
                        if (se != null) ac.addStereoElement(se);
                        break;
                    }
                    case Octahedral: {
                        IStereoElement se = newOctahedral(u, g.neighbors(u), atoms, c);
                        if (se != null) ac.addStereoElement(se);
                        break;
                    }
                }
            }
        }

        ac.setBonds(bonds);

        // use directional bonds to assign bond-based stereo-specification
        if (checkBondStereo) {
            addDoubleBondStereochemistry(g, ac);
        }

        // title suffix
        ac.setTitle(g.getTitle());

        return ac;
    }

    private Edge findCumulatedEdge(Graph g, int v, Edge e) {
        Edge res = null;
        for (Edge f : g.edges(v)) {
            if (f != e && f.bond() == Bond.DOUBLE) {
                if (res != null) return null;
                res = f;
            }
        }
        return res;
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
            Edge first  = null;
            Edge second = null;

            // if either atom is not incident to a directional label there
            // is no configuration
            if ((first = findDirectionalEdge(g, u)) != null) {
                if ((second = findDirectionalEdge(g, v)) != null) {

                    // if the directions (relative to the double bond) are the
                    // same then they are on the same side - otherwise they
                    // are opposite
                    Conformation conformation = first.bond(u) == second.bond(v) ?
                                                Conformation.TOGETHER : Conformation.OPPOSITE;

                    // get the stereo bond and build up the ligands for the
                    // stereo-element - linear search could be improved with
                    // map or API change to double bond element
                    IBond db = ac.getBond(ac.getAtom(u), ac.getAtom(v));

                    IBond[] ligands = new IBond[]{
                        ac.getBond(ac.getAtom(u), ac.getAtom(first.other(u))),
                        ac.getBond(ac.getAtom(v), ac.getAtom(second.other(v)))};

                    ac.addStereoElement(new DoubleBondStereochemistry(db, ligands, conformation));
                } else if (g.degree(v) == 2) {
                    List<Edge> edges = new ArrayList<>();
                    edges.add(e);
                    Edge f = findCumulatedEdge(g, v, e);
                    while (f != null) {
                        edges.add(f);
                        v = f.other(v);
                        f = findCumulatedEdge(g, v, f);
                    }
                    // only odd number of cumulated bonds here, otherwise is
                    // extended tetrahedral
                    if ((edges.size() & 0x1) == 0)
                        continue;
                    second = findDirectionalEdge(g, v);
                    if (second != null) {
                        int   cfg        = first.bond(u) == second.bond(v)
                                           ? IStereoElement.TOGETHER
                                           : IStereoElement.OPPOSITE;
                        Edge  middleEdge = edges.get(edges.size()/2);
                        IBond middleBond = ac.getBond(ac.getAtom(middleEdge.either()),
                                                      ac.getAtom(middleEdge.other(middleEdge.either())));
                        IBond[] ligands = new IBond[]{
                            ac.getBond(ac.getAtom(u), ac.getAtom(first.other(u))),
                            ac.getBond(ac.getAtom(v), ac.getAtom(second.other(v)))};

                        ac.addStereoElement(new ExtendedCisTrans(middleBond, ligands, cfg));
                    }
                }
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
        List<Edge> edges = g.edges(u);
        if (edges.size() == 1)
            return null;
        Edge first = null;
        for (Edge e : edges) {
            Bond b = e.bond();
            if (b == Bond.UP || b == Bond.DOWN) {
                if (first == null)
                    first = e;
                else if (((first.either() == e.either()) == (first.bond() == b)))
                    return null;
            }
        }
        return first;
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

    private IStereoElement newSquarePlanar(int u, int[] vs, IAtom[] atoms, Configuration c) {

        if (vs.length != 4)
            return null;

        int order;
        switch (c) {
            case SP1:
                order = IStereoElement.SP | 1;
                break;
            case SP2:
                order = IStereoElement.SP | 2;
                break;
            case SP3:
                order = IStereoElement.SP | 3;
                break;
            default:
                return null;
        }

        return new SquarePlanar(atoms[u],
                                new IAtom[]{atoms[vs[0]], atoms[vs[1]], atoms[vs[2]], atoms[vs[3]]},
                                order);
    }

    private IStereoElement newTrigonalBipyramidal(int u, int[] vs, IAtom[] atoms, Configuration c) {
        if (vs.length != 5)
            return null;
        int order = 1 + c.ordinal() - Configuration.TB1.ordinal();
        if (order < 1 || order > 20)
            return null;
        return new TrigonalBipyramidal(atoms[u],
                                       new IAtom[]{atoms[vs[0]], atoms[vs[1]], atoms[vs[2]], atoms[vs[3]], atoms[vs[4]]},
                                       order);
    }

    private IStereoElement newOctahedral(int u, int[] vs, IAtom[] atoms, Configuration c) {
        if (vs.length != 6)
            return null;
        int order = 1 + c.ordinal() - Configuration.OH1.ordinal();
        if (order < 1 || order > 30)
            return null;
        return new Octahedral(atoms[u],
                              new IAtom[]{atoms[vs[0]],
                                          atoms[vs[1]],
                                          atoms[vs[2]],
                                          atoms[vs[3]],
                                          atoms[vs[4]],
                                          atoms[vs[5]]},
                              order);
    }

    private int getOtherDb(Graph g, int u, int v) {
        for (Edge e : g.edges(u)) {
            if (e.bond() != Bond.DOUBLE)
                continue;
            int nbr = e.other(u);
            if (nbr == v)
                continue;
            return nbr;
        }
        return -1;
    }

    private int[] findExtendedTetrahedralEnds(Graph g, int focus) {
        List<Edge> es = g.edges(focus);
        int prevEnd1 = focus;
        int prevEnd2 = focus;
        int end1 = es.get(0).other(prevEnd2);
        int end2 = es.get(1).other(prevEnd2);
        int tmp;
        while (end1 >= 0 && end2 >= 0) {
            tmp = getOtherDb(g, end1, prevEnd1);
            prevEnd1 = end1;
            end1 = tmp;
            tmp = getOtherDb(g, end2, prevEnd2);
            prevEnd2 = end2;
            end2 = tmp;
        }
        return new int[]{prevEnd1, prevEnd2};
    }

    private IStereoElement newExtendedTetrahedral(int u, Graph g, IAtom[] atoms) {

        int[] terminals = findExtendedTetrahedralEnds(g, u);
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
     * Create a new empty atom container instance.
     * @return a new atom container instance
     */
    private IAtomContainer emptyContainer() {
        return builder.newAtomContainer();
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
        IAtom atom = builder.newAtom();
        atom.setSymbol(element.symbol());
        atom.setAtomicNumber(element.atomicNumber());
        return atom;
    }
}
