/*
 * Copyright (c) 2013, European Bioinformatics Institute (EMBL-EBI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */

package uk.ac.ebi.beam;

import java.util.*;

import static java.util.Map.Entry;

/**
 * Parse a SMILES string and create a {@link Graph}. A new parser should be
 * created for each invocation, for convenience {@link #parse(String)} is
 * provided.
 *
 * <blockquote><pre>
 * Graph g = Parser.parse("CCO");
 * </pre></blockquote>
 *
 * @author John May
 */
final class Parser {

    /**
     * Keep track of branching.
     */
    private final IntStack stack = new IntStack(10);

    /**
     * Molecule being loaded.
     */
    private final Graph g;

    /**
     * Keep track of ring information.
     */
    private RingBond[] rings = new RingBond[10];

    /**
     * Local arrangement for ring openings.
     */
    private Map<Integer, LocalArrangement> arrangement
            = new HashMap<Integer, LocalArrangement>(5);

    private Map<Integer, Configuration> configurations
            = new HashMap<Integer, Configuration>(5);

    /**
     * Current bond.
     */
    private Bond bond = Bond.IMPLICIT;

    /**
     * Current configuration.
     */
    private Configuration configuration = Configuration.UNKNOWN;


    /**
     * Which vertices start a new run of tokens. This includes the first vertex
     * and all vertices which immediately follow a 'dot' bond. These are
     * required to correctly store atom topologies.
     */
    private Set<Integer> start = new TreeSet<Integer>();

    /**
     * Number of open rings - all rings should be closed.
     */
    private int openRings = 0;

    /**
     * Strict parsing.
     */
    private final boolean strict;

    private BitSet checkDirectionalBonds = new BitSet();

    private int lastBondPos = -1;
    private Map<Edge, Integer> bondStrPos = new HashMap<>();

    private List<String> warnings = new ArrayList<>();

    private boolean hasAstrix = false;

    /**
     * Create a new parser for the specified buffer.
     *
     * @param buffer character buffer holding a SMILES string
     * @throws InvalidSmilesException thrown if the SMILES could not be parsed
     */
    Parser(CharBuffer buffer, boolean strict) throws InvalidSmilesException {
        this.strict = strict;
        g = new Graph(1 + (2 * (buffer.length() / 3)));
        readSmiles(buffer);
        if (openRings > 0)
            throw new InvalidSmilesException("Unclosed ring detected, SMILES may be truncated:", buffer);
        if (stack.size() > 1)
            throw new InvalidSmilesException("Unclosed branch detected, SMILES may be truncated:", buffer);
        start.add(0); // always include first vertex as start
        if (g.getFlags(Graph.HAS_STRO) != 0) {
            createTopologies(buffer);
        }
        if (hasAstrix) {
            for (int i = 0; i < g.order(); i++) {
                Atom atom = g.atom(i);
                if (atom.element() == Element.Unknown) {
                    int nArom = 0;
                    for (Edge e : g.edges(i)) {
                        if (e.bond() == Bond.AROMATIC ||
                            e.bond() == Bond.IMPLICIT && g.atom(e.other(i)).aromatic())
                            nArom++;
                    }
                    if (nArom >= 2) {
                        if (atom == AtomImpl.AliphaticSubset.Any)
                            g.setAtom(i, AtomImpl.AromaticSubset.Any);
                        else
                            g.setAtom(i,
                                      new AtomImpl.BracketAtom(-1,
                                                               Element.Unknown,
                                                               atom.label(),
                                                               atom.hydrogens(),
                                                               atom.charge(),
                                                               atom.atomClass(),
                                                               true));
                    }
                }
            }
        }
    }

    /**
     * Create a new (loose) parser for the specified string.
     *
     * @param str SMILES string
     * @throws InvalidSmilesException thrown if the SMILES could not be parsed
     */
    Parser(String str) throws InvalidSmilesException {
        this(CharBuffer.fromString(str), false);
    }

    /**
     * Strict parsing of the provided SMILES string. The strict parser will
     * throw more exceptions for unusual input.
     *
     * @param str the SMILES string to process
     * @return a graph created with the strict parser
     * @throws InvalidSmilesException
     */
    static Graph strict(String str) throws InvalidSmilesException {
        return new Parser(CharBuffer.fromString(str), true).molecule();
    }

    /**
     * Loose parsing of the provided SMILES string. The loose parser is more
     * relaxed and will allow abnormal aromatic elements (e.g. 'te') as well as
     * bare 'H', 'D' and 'T' for hydrogen and it's isotopes. Note the hydrogen
     * and isotopes are replaced with their correct bracket equivalent.
     *
     * @param str the SMILES string to process
     * @return a graph created with the loose parser
     * @throws InvalidSmilesException
     */
    static Graph losse(String str) throws InvalidSmilesException {
        return new Parser(CharBuffer.fromString(str), false).molecule();
    }

    /**
     * Access the molecule created by the parser.
     *
     * @return the chemical graph for the parsed smiles string
     */
    Graph molecule() {
        return g;
    }

    /**
     * Create the topologies (stereo configurations) for the chemical graph. The
     * topologies define spacial arrangement around atoms.
     */
    private void createTopologies(CharBuffer buffer) throws InvalidSmilesException {
        // create topologies (stereo configurations)
        for (Entry<Integer, Configuration> e : configurations.entrySet()) {
            addTopology(e.getKey(),
                        e.getValue(),
                        Topology.toExplicit(g, e.getKey(), e.getValue()));
        }

        for (int v = checkDirectionalBonds.nextSetBit(0); v >= 0; v = checkDirectionalBonds.nextSetBit(v + 1)) {
            int nUpV = 0;
            int nDownV = 0;
            int nUpW = 0;
            int nDownW = 0;
            int w = -1;

            {
                final int d = g.degree(v);
                for (int j = 0; j < d; ++j) {
                    final Edge e = g.edgeAt(v, j);
                    Bond bond = e.bond(v);
                    if (bond == Bond.UP)
                        nUpV++;
                    else if (bond == Bond.DOWN)
                        nDownV++;
                    else if (bond == Bond.DOUBLE)
                        w = e.other(v);
                }
            }

            if (w < 0)
                continue;

            checkDirectionalBonds.clear(w);

            {
                final int d = g.degree(w);
                for (int j = 0; j < d; ++j) {
                    final Edge e = g.edgeAt(w, j);
                    Bond bond = e.bond(w);
                    if (bond == Bond.UP)
                        nUpW++;
                    else if (bond == Bond.DOWN)
                        nDownW++;
                }
            }

            if (nUpV + nDownV == 0 || nUpW + nDownW == 0)
                continue;

            if (nUpV > 1 || nDownV > 1) {
                int offset1 = -1, offset2 = -1;
                for (Edge e : g.edges(v)) {
                    if (e.bond().directional())
                        if (offset1 < 0)
                            offset1 = bondStrPos.get(e);
                        else
                            offset2 = bondStrPos.get(e);
                }
                String errorPos = InvalidSmilesException.display(buffer,
                                                                 offset1 - buffer.length(),
                                                                 offset2 - buffer.length());
                if (strict)
                    throw new InvalidSmilesException("Ignored invalid Cis/Trans specification: " + errorPos);
                else
                    warnings.add("Ignored invalid Cis/Trans specification: " + errorPos);
            }
            if (nUpW > 1 || nDownW > 1) {
                int offset1 = -1, offset2 = -1;
                for (Edge e : g.edges(w)) {
                    if (e.bond().directional())
                        if (offset1 < 0)
                            offset1 = bondStrPos.get(e);
                        else
                            offset2 = bondStrPos.get(e);
                }
                String errorPos = InvalidSmilesException.display(buffer,
                                                                 offset1 - buffer.length(),
                                                                 offset2 - buffer.length());
                if (strict)
                    throw new InvalidSmilesException("Ignored invalid Cis/Trans specification: " + errorPos);
                else
                    warnings.add("Ignored invalid Cis/Trans specification: " + errorPos);
            }
        }
    }

    public List<Edge> getEdges(LocalArrangement localArrangement, int u) {
        if (localArrangement == null)
            return g.edges(u);
        int[] vs = localArrangement.toArray();
        List<Edge> edges = new ArrayList<Edge>(vs.length);
        for (int v : vs)
            edges.add(g.edge(u, v));
        return edges;
    }

    private int getOtherDb(int u, int v) {
        for (Edge e : getLocalEdges(u)) {
            if (e.bond() != Bond.DOUBLE)
                continue;
            int nbr = e.other(u);
            if (nbr == v)
                continue;
            return nbr;
        }
        return -1;
    }

    private int[] findExtendedTetrahedralEnds(int focus) {
        List<Edge> es = getLocalEdges(focus);
        int prevEnd1 = focus;
        int prevEnd2 = focus;
        int end1 = es.get(0).other(prevEnd2);
        int end2 = es.get(1).other(prevEnd2);
        int tmp;
        while (end1 >= 0 && end2 >= 0) {
            tmp = getOtherDb(end1, prevEnd1);
            prevEnd1 = end1;
            end1 = tmp;
            tmp = getOtherDb(end2, prevEnd2);
            prevEnd2 = end2;
            end2 = tmp;
        }
        return new int[]{prevEnd1, prevEnd2};
    }

    /**
     * Access the local edges in order.
     */
    private List<Edge> getLocalEdges(int end) {
        return getEdges(arrangement.get(end), end);
    }

    /**
     * Complicated process to get correct Allene neighbors.
     *
     * @param focus the focus (central cumualted atom)
     * @return the carrier list
     */
    public int[] getAlleneCarriers(int focus) {
        int[] carriers = new int[4];
        int i = 0;
        int[] ends = findExtendedTetrahedralEnds(focus);
        int beg = ends[0];
        int end = ends[1];
        boolean begh = g.implHCount(beg) == 1;
        boolean endh = g.implHCount(end) == 1;
        List<Edge> begEdges = new ArrayList<>(getLocalEdges(beg));
        if (begh)
            begEdges.add(start.contains(beg) ? 0 : 1, null);
        for (Edge bEdge : getLocalEdges(beg)) {
            if (bEdge == null) {
                carriers[i++] = beg;
                continue;
            }
            int bnbr = bEdge.other(beg);
            if (beg < bnbr && begh) {
                carriers[i++] = beg;
                begh = false;
            }
            if (bEdge.bond() == Bond.DOUBLE) {
                // neighbors next to end
                List<Edge> endEdges = new ArrayList<>(getLocalEdges(end));
                if (endh)
                    endEdges.add(1, null);
                for (Edge eEdge : endEdges) {
                    if (eEdge == null)
                        carriers[i++] = end;
                    else if (eEdge.bond() != Bond.DOUBLE)
                        carriers[i++] = eEdge.other(end);
                }
            } else {
                carriers[i++] = bnbr;
            }
        }
        if (i != 4)
            return null;
        return carriers;
    }

    /**
     * Add a topology for vertex 'u' with configuration 'c'. If the atom 'u' was
     * involved in a ring closure the local arrangement is used instead of the
     * order in the graph. The configuration should be explicit '@TH1' or '@TH2'
     * instead of '@' or '@@'.
     *
     * @param u a vertex
     * @param c explicit configuration of that vertex
     * @see Topology#toExplicit(Graph, int, Configuration)
     */
    private void addTopology(int u, Configuration input, Configuration c) throws
            InvalidSmilesException {
        // stereo on ring closure - use local arrangement
        if (arrangement.containsKey(u)) {
            int[] us = arrangement.get(u).toArray();
            List<Edge> es = getLocalEdges(u);

            if (c.type() == Configuration.Type.Tetrahedral) {
                us = insertThImplicitRef(u, us); // XXX: temp fix
            } else if (c.type() == Configuration.Type.DoubleBond) {
                us = insertDbImplicitRef(u, us); // XXX: temp fix
            } else if (c.type() == Configuration.Type.ExtendedTetrahedral) {
                g.addFlags(Graph.HAS_EXT_STRO);
                if ((us = getAlleneCarriers(u)) == null) {
                    if (strict)
                        throw new InvalidSmilesException("Invalid Allene stereo");
                    else
                        warnings.add("Ignored invalid Allene stereochemistry");
                    return;
                }
            } else if (input.type() == Configuration.Type.SquarePlanar) {
                us = insertMultipleImplicitRefs(u, us, 4);
            } else if (input.type() == Configuration.Type.TrigonalBipyramidal) {
                us = insertMultipleImplicitRefs(u, us, 5);
            } else if (input.type() == Configuration.Type.Octahedral) {
                us = insertMultipleImplicitRefs(u, us, 6);
            } else if (c.type() == Configuration.Type.SquarePlanar &&
                       us.length != 4) {
                if (strict)
                    throw new InvalidSmilesException("SquarePlanar without 4 explicit neighbours");
                else
                    warnings.add("SquarePlanar without 4 explicit neighbours");
                return;
            } else if (c.type() == Configuration.Type.TrigonalBipyramidal &&
                       us.length != 5) {
                if (strict)
                    throw new InvalidSmilesException("TrigonalBipyramidal without 5 explicit neighbours");
                else
                    warnings.add("SquarePlanar without 5 explicit neighbours");
                return;
            } else if (c.type() == Configuration.Type.Octahedral &&
                       us.length != 6) {
                if (strict)
                    throw new InvalidSmilesException("Octahedral without 6 explicit neighbours");
                else
                    warnings.add("SquarePlanar without 6 explicit neighbours");
                return;
            }
            g.addTopology(Topology.create(u, us, es, c));
        } else {
            int[] us = new int[g.degree(u)];
            List<Edge> es = g.edges(u);
            for (int i = 0; i < us.length; i++)
                us[i] = es.get(i).other(u);

            if (c.type() == Configuration.Type.Tetrahedral) {
                us = insertThImplicitRef(u, us); // XXX: temp fix
            } else if (c.type() == Configuration.Type.DoubleBond) {
                us = insertDbImplicitRef(u, us); // XXX: temp fix
            } else if (c.type() == Configuration.Type.ExtendedTetrahedral) {
                g.addFlags(Graph.HAS_EXT_STRO);
                if ((us = getAlleneCarriers(u)) == null)
                    return;
            } else if (input.type() == Configuration.Type.SquarePlanar) {
                us = insertMultipleImplicitRefs(u, us, 4);
            } else if (input.type() == Configuration.Type.TrigonalBipyramidal) {
                us = insertMultipleImplicitRefs(u, us, 5);
            } else if (input.type() == Configuration.Type.Octahedral) {
                us = insertMultipleImplicitRefs(u, us, 6);
            } else if (c.type() == Configuration.Type.SquarePlanar &&
                       us.length != 4) {
                if (strict)
                    throw new InvalidSmilesException("SquarePlanar without 4 explicit neighbours");
                else
                    warnings.add("SquarePlanar without 4 explicit neighbours");
                return;
            } else if (c.type() == Configuration.Type.TrigonalBipyramidal &&
                       us.length != 5) {
                if (strict)
                    throw new InvalidSmilesException("TrigonalBipyramidal without 5 explicit neighbours");
                else
                    warnings.add("SquarePlanar without 5 explicit neighbours");
                return;
            } else if (c.type() == Configuration.Type.Octahedral &&
                       us.length != 6) {
                if (strict)
                    throw new InvalidSmilesException("Octahedral without 6 explicit neighbours");
                else
                    warnings.add("SquarePlanar without 6 explicit neighbours");
                return;
            }
            g.addTopology(Topology.create(u, us, es, c));
        }
    }

    private int[] insertThImplicitRef(int u, int[] vs) throws
            InvalidSmilesException {
        if (vs.length == 4)
            return vs;
        if (vs.length != 3)
            throw new InvalidSmilesException("Invalid number of vertices for TH1/TH2 stereo chemistry");
        if (start.contains(u))
            return new int[]{u, vs[0], vs[1], vs[2]};
        else
            return new int[]{vs[0], u, vs[1], vs[2]};
    }

    private int[] insertMultipleImplicitRefs(int u, int[] vs, int n) throws
            InvalidSmilesException {
        if (vs.length == n)
            return vs;
        if (vs.length <= 1)
            throw new InvalidSmilesException("Cannot have <= 1 vertices for high-order stereo chemistry");
        int cnt = n - vs.length;
        int srcIdx = 0;
        int dstIdx = 0;
        int[] padded = new int[n];
        if (!start.contains(u))
            padded[dstIdx++] = vs[srcIdx++];
        while (cnt-- > 0)
            padded[dstIdx++] = u;
        while (srcIdx < vs.length)
            padded[dstIdx++] = vs[srcIdx++];
        return padded;
    }

    // XXX: temporary fix for correcting configurations
    private int[] insertDbImplicitRef(int u, int[] vs) throws
            InvalidSmilesException {
        if (vs.length == 3)
            return vs;
        if (vs.length != 2)
            throw new InvalidSmilesException("Invaid number of verticies for DB1/DB2 stereo chemistry");
        if (start.contains(u))
            return new int[]{u, vs[0], vs[1]};
        else
            return new int[]{vs[0], u, vs[1]};
    }

    /**
     * Add an atom and bond with the atom on the stack (if available and non-dot
     * bond).
     *
     * @param a an atom to add
     */
    private void addAtom(Atom a, CharBuffer buffer) throws InvalidSmilesException {
        int v = g.addAtom(a);
        if (!stack.empty()) {
            int u = stack.pop();
            if (bond != Bond.DOT) {
                Edge e = new Edge(u, v, bond);
                if (bond.directional()) {
                    bondStrPos.put(e, lastBondPos);
                    checkDirectionalBonds.set(u);
                    checkDirectionalBonds.set(v);
                }
                g.addEdge(e);
                if (arrangement.containsKey(u))
                    arrangement.get(u).add(v);
            } else {
                start.add(v); // start of a new run
            }
        }
        stack.push(v);
        bond = Bond.IMPLICIT;

        // configurations used to create topologies after parsing
        if (configuration != Configuration.UNKNOWN) {
            g.addFlags(Graph.HAS_ATM_STRO);
            configurations.put(v, configuration);
            configuration = Configuration.UNKNOWN;
        }
    }

    /**
     * Read a molecule from the character buffer.
     *
     * @param buffer a character buffer
     * @throws InvalidSmilesException invalid grammar
     */
    private void readSmiles(final CharBuffer buffer) throws
            InvalidSmilesException {
        // primary dispatch
        while (buffer.hasRemaining()) {
            char c = buffer.get();
            switch (c) {

                // aliphatic subset
                case '*':
                    hasAstrix = true;
                    addAtom(AtomImpl.AliphaticSubset.Any, buffer);
                    break;
                case 'B':
                    if (buffer.getIf('r'))
                        addAtom(AtomImpl.AliphaticSubset.Bromine, buffer);
                    else
                        addAtom(AtomImpl.AliphaticSubset.Boron, buffer);
                    break;
                case 'C':
                    if (buffer.getIf('l'))
                        addAtom(AtomImpl.AliphaticSubset.Chlorine, buffer);
                    else
                        addAtom(AtomImpl.AliphaticSubset.Carbon, buffer);
                    break;
                case 'N':
                    addAtom(AtomImpl.AliphaticSubset.Nitrogen, buffer);
                    break;
                case 'O':
                    addAtom(AtomImpl.AliphaticSubset.Oxygen, buffer);
                    break;
                case 'P':
                    addAtom(AtomImpl.AliphaticSubset.Phosphorus, buffer);
                    break;
                case 'S':
                    addAtom(AtomImpl.AliphaticSubset.Sulfur, buffer);
                    break;
                case 'F':
                    addAtom(AtomImpl.AliphaticSubset.Fluorine, buffer);
                    break;
                case 'I':
                    addAtom(AtomImpl.AliphaticSubset.Iodine, buffer);
                    break;

                // aromatic subset
                case 'b':
                    addAtom(AtomImpl.AromaticSubset.Boron, buffer);
                    g.addFlags(Graph.HAS_AROM);
                    break;
                case 'c':
                    addAtom(AtomImpl.AromaticSubset.Carbon, buffer);
                    g.addFlags(Graph.HAS_AROM);
                    break;
                case 'n':
                    addAtom(AtomImpl.AromaticSubset.Nitrogen, buffer);
                    g.addFlags(Graph.HAS_AROM);
                    break;
                case 'o':
                    addAtom(AtomImpl.AromaticSubset.Oxygen, buffer);
                    g.addFlags(Graph.HAS_AROM);
                    break;
                case 'p':
                    addAtom(AtomImpl.AromaticSubset.Phosphorus, buffer);
                    g.addFlags(Graph.HAS_AROM);
                    break;
                case 's':
                    addAtom(AtomImpl.AromaticSubset.Sulfur, buffer);
                    g.addFlags(Graph.HAS_AROM);
                    break;


                // D/T for hydrogen isotopes - non-standard but OpenSMILES spec
                // says it's possible. The D and T here are automatic converted
                // to [2H] and [3H].
                case 'H':
                    if (strict)
                        throw new InvalidSmilesException("hydrogens should be specified in square brackets - '[H]'",
                                                         buffer);
                    addAtom(AtomImpl.EXPLICIT_HYDROGEN, buffer);
                    break;
                case 'D':
                    if (strict)
                        throw new InvalidSmilesException("deuterium should be specified as a hydrogen isotope - '[2H]'",
                                                         buffer);
                    addAtom(AtomImpl.DEUTERIUM, buffer);
                    break;
                case 'T':
                    if (strict)
                        throw new InvalidSmilesException("tritium should be specified as a hydrogen isotope - '[3H]'",
                                                         buffer);
                    addAtom(AtomImpl.TRITIUM, buffer);
                    break;

                // bracket atom
                case '[':
                    addAtom(readBracketAtom(buffer), buffer);
                    break;

                // ring bonds
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    ring(c - '0', buffer);
                    break;
                case '%':
                    int num = buffer.getNumber(2);
                    if (num < 0)
                        throw new InvalidSmilesException("a number (<digit>+) must follow '%':", buffer);
                    if (strict && num < 10)
                        throw new InvalidSmilesException("two digits must follow '%'", buffer);
                    ring(num, buffer);
                    lastBondPos = buffer.position();
                    break;

                // bond/dot
                case '-':
                    if (bond != Bond.IMPLICIT)
                        throw new InvalidSmilesException("Multiple bonds specified:", buffer);
                    bond = Bond.SINGLE;
                    lastBondPos = buffer.position();
                    break;
                case '=':
                    if (bond != Bond.IMPLICIT)
                        throw new InvalidSmilesException("Multiple bonds specified:", buffer);
                    bond = Bond.DOUBLE;
                    lastBondPos = buffer.position();
                    break;
                case '#':
                    if (bond != Bond.IMPLICIT)
                        throw new InvalidSmilesException("Multiple bonds specified:", buffer);
                    bond = Bond.TRIPLE;
                    lastBondPos = buffer.position();
                    break;
                case '$':
                    if (bond != Bond.IMPLICIT)
                        throw new InvalidSmilesException("Multiple bonds specified:", buffer);
                    bond = Bond.QUADRUPLE;
                    lastBondPos = buffer.position();
                    break;
                case ':':
                    if (bond != Bond.IMPLICIT)
                        throw new InvalidSmilesException("Multiple bonds specified:", buffer);
                    g.addFlags(Graph.HAS_AROM);
                    bond = Bond.AROMATIC;
                    lastBondPos = buffer.position();
                    break;
                case '/':
                    if (bond != Bond.IMPLICIT)
                        throw new InvalidSmilesException("Multiple bonds specified:", buffer);
                    bond = Bond.UP;
                    lastBondPos = buffer.position();
                    g.addFlags(Graph.HAS_BND_STRO);
                    break;
                case '\\':
                    // we allow C\\C=C/C since it could be an escaping error
                    if (bond != Bond.IMPLICIT && bond != Bond.DOWN)
                        throw new InvalidSmilesException("Multiple bonds specified:", buffer);
                    bond = Bond.DOWN;
                    lastBondPos = buffer.position();
                    g.addFlags(Graph.HAS_BND_STRO);
                    break;
                case '.':
                    if (bond != Bond.IMPLICIT)
                        throw new InvalidSmilesException("Bond specified before disconnection:", buffer);
                    bond = Bond.DOT;
                    break;

                // branching
                case '(':
                    if (stack.empty())
                        throw new InvalidSmilesException("Cannot open branch at this position, SMILES may be truncated:",
                                                         buffer);
                    stack.push(stack.peek());
                    break;
                case ')':
                    if (stack.size() < 2)
                        throw new InvalidSmilesException("Closing of an unopened branch, SMILES may be truncated:",
                                                         buffer);
                    stack.pop();
                    break;

                // termination
                case '\t':
                case ' ':

                    // String suffix is title 
                    StringBuilder sb = new StringBuilder();
                    while (buffer.hasRemaining()) {
                        c = buffer.get();
                        if (c == '\n' || c == '\r')
                            break;
                        sb.append(c);
                    }
                    g.setTitle(sb.toString());
                    return;
                case '\n':
                case '\r':
                    return;

                default:
                    throw new InvalidSmilesException("unexpected character:", buffer);
            }
        }
    }

    /**
     * Read a bracket atom from the buffer. A bracket atom optionally defines
     * isotope, chirality, hydrogen count, formal charge and the atom class.
     *
     * <blockquote><pre>
     * bracket_atom ::= '[' isotope? symbol chiral? hcount? charge? class? ']'
     * </pre></blockquote>
     *
     * @param buffer a character buffer
     * @return a bracket atom
     * @throws InvalidSmilesException thrown if the bracket atom did not match
     *                                the grammar, invalid symbol, missing
     *                                closing bracket or invalid chiral
     *                                specification.
     */
    Atom readBracketAtom(final CharBuffer buffer) throws
            InvalidSmilesException {
        int start = buffer.position;

        boolean arbitraryLabel = false;

        if (!buffer.hasRemaining())
            throw new InvalidSmilesException("Unclosed bracket atom, SMILES may be truncated", buffer);

        final int isotope = buffer.getNumber();
        final boolean aromatic = buffer.next() >= 'a' && buffer.next() <= 'z';
        final Element element = Element.read(buffer);
        if (element == Element.Unknown)
            hasAstrix = true;

        if (strict && element == null)
            throw new InvalidSmilesException("unrecognised element symbol, SMILES may be truncated: ", buffer);

        if (element != null && aromatic)
            g.addFlags(Graph.HAS_AROM);

        // element isn't aromatic as per the OpenSMILES specification
        if (strict && aromatic && !element.aromatic(Element.AromaticSpecification.OpenSmiles))
            throw new InvalidSmilesException("abnormal aromatic element", buffer);

        if (element == null) {
            arbitraryLabel = true;
        }

        configuration = Configuration.read(buffer);

        int hCount = readHydrogens(buffer);
        int charge = readCharge(buffer);
        int atomClass = readClass(buffer);

        if (!arbitraryLabel && !buffer.getIf(']')) {
            if (strict) {
                throw InvalidSmilesException.invalidBracketAtom(buffer);
            } else {
                arbitraryLabel = true;
            }
        }

        if (arbitraryLabel) {
            int end = buffer.position;
            int depth = 1;
            while (buffer.hasRemaining()) {
                char c = buffer.get();
                if (c == '[')
                    depth++;
                else if (c == ']') {
                    depth--;
                    if (depth == 0)
                        break;
                }
                end++;
            }
            if (depth != 0)
                throw new InvalidSmilesException("unparsable label in bracket atom",
                                                 buffer,
                                                 buffer.position - 1);
            String label = buffer.substr(start, end);
            hasAstrix = true;
            return new AtomImpl.BracketAtom(label);
        }

        return new AtomImpl.BracketAtom(isotope,
                                        element,
                                        hCount,
                                        charge,
                                        atomClass,
                                        aromatic);
    }

    /**
     * Read the hydrogen count and progress the provided buffer. The hydrogen
     * count is specified by a 'H' an 0 or more digits. A 'H' without digits is
     * intercepted as 'H1'. When there is no 'H' or 'H0' is specified then the
     * the hydrogen count is 0.
     *
     * @param buffer a character buffer
     * @return the hydrogen count, 0 if none
     */
    static int readHydrogens(final CharBuffer buffer) {
        if (buffer.getIf('H')) {
            // when no number is specified 'H' then there is 1 hydrogen
            int count = buffer.getNumber();
            return count < 0 ? 1 : count;
        }
        return 0;
    }

    /**
     * Read a charge value and progress the provide buffer. The charge value is
     * present in bracket atoms either directly after the symbol, the chiral
     * specification or the hydrogen count. The specification of charge by
     * concatenated signs (e.g. ++, --) and other bad form (e.g. '++-1') is
     * intercepted.
     *
     * @param buffer a character buffer
     * @return the formal charge value, 0 if none present
     * @see <a href="http://www.opensmiles.org/opensmiles.html#charge">Charge -
     * OpenSMILES Specification</a>
     */
    static int readCharge(final CharBuffer buffer) {
        return readCharge(0, buffer);
    }

    /**
     * Internal method for parsing charge, to allow concatenated signs (--, ++)
     * the method recursively invokes increment or decrementing an accumulator.
     *
     * @param acc    accumulator
     * @param buffer a character buffer
     * @return the charge value
     */
    private static int readCharge(int acc, final CharBuffer buffer) {
        if (buffer.getIf('+'))
            return buffer.nextIsDigit() ? acc + buffer.getNumber()
                    : readCharge(acc + 1, buffer);
        if (buffer.getIf('-'))
            return buffer.nextIsDigit() ? acc - buffer.getNumber()
                    : readCharge(acc - 1, buffer);
        return acc;
    }

    /**
     * Read the atom class of a bracket atom and progress the buffer (if read).
     * The atom class is the last attribute of the bracket atom and is
     * identified by a ':' followed by one or more digits. The atom class may be
     * padded such that ':005' and ':5' are equivalent.
     *
     * @param buffer a character buffer
     * @return the atom class, or 0
     * @see <a href="http://www.opensmiles.org/opensmiles.html#atomclass">Atom
     * Class - OpenSMILES Specification</a>
     */
    static int readClass(CharBuffer buffer) throws InvalidSmilesException {
        if (buffer.getIf(':')) {
            if (buffer.nextIsDigit())
                return buffer.getNumber();
            throw new InvalidSmilesException("invalid atom class, <digit>+ must follow ':'", buffer);
        }
        return 0;
    }

    /**
     * Handle the ring open/closure of the specified ring number 'rnum'.
     *
     * @param rnum ring number
     * @throws InvalidSmilesException bond types did not match on ring closure
     */
    private void ring(int rnum, CharBuffer buffer) throws InvalidSmilesException {
        if (bond == Bond.DOT)
            throw new InvalidSmilesException("a ring bond can not be a 'dot':",
                                             buffer,
                                             buffer.position());
        if (stack.empty())
            throw new InvalidSmilesException("No previous atom for ring open!",
                                             buffer,
                                             buffer.position());

        if (rings.length <= rnum || rings[rnum] == null)
            openRing(rnum, buffer);
        else
            closeRing(rnum, buffer);
    }

    /**
     * Open the ring bond with the specified 'rnum'.
     *
     * @param rnum ring number
     */
    private void openRing(int rnum, CharBuffer buf) {
        if (rnum >= rings.length)
            rings = Arrays.copyOf(rings,
                                  Math.min(100, rnum * 2)); // max rnum: 99

        int u = stack.peek();

        // create a ring bond
        rings[rnum] = new RingBond(u, bond, lastBondPos);

        // keep track of arrangement (important for stereo configurations)
        createArrangement(u).add(-rnum);
        openRings++;

        bond = Bond.IMPLICIT;
    }

    /**
     * Create the current local arrangement for vertex 'u' - if the arrangment
     * already exists then that arrangement is used.
     *
     * @param u vertex to get the arrangement around
     * @return current local arrangement
     */
    private LocalArrangement createArrangement(int u) {
        LocalArrangement la = arrangement.get(u);
        if (la == null) {
            la = new LocalArrangement();
            final int d = g.degree(u);
            for (int j = 0; j < d; ++j) {
                final Edge e = g.edgeAt(u, j);
                la.add(e.other(u));
            }
            arrangement.put(u, la);
        }
        return la;
    }

    /**
     * Close the ring bond with the specified 'rnum'.
     *
     * @param rnum ring number
     * @throws InvalidSmilesException bond types did not match
     */
    private void closeRing(int rnum, CharBuffer buffer) throws InvalidSmilesException {
        RingBond rbond = rings[rnum];
        rings[rnum] = null;
        int u = rbond.u;
        int v = stack.peek();

        if (u == v)
            throw new InvalidSmilesException("Endpoints of ringbond are the same - loops are not allowed",
                                             buffer);

        if (g.adjacent(u, v))
            throw new InvalidSmilesException("Endpoints of ringbond are already connected - multi-edges are not allowed",
                                             buffer);

        bond = decideBond(rbond.bond, bond.inverse(), rbond.pos, buffer);

        Edge e = new Edge(u, v, bond);
        if (bond.directional()) {
            checkDirectionalBonds.set(u);
            checkDirectionalBonds.set(v);
            if (rbond.bond.directional())
                bondStrPos.put(e, rbond.pos);
            else
                bondStrPos.put(e, lastBondPos);
        }
        g.addEdge(e);
        bond = Bond.IMPLICIT;
        // adjust the arrangement replacing where this ring number was openned
        arrangement.get(rbond.u).replace(-rnum, stack.peek());
        if (arrangement.containsKey(v))
            arrangement.get(v).add(rbond.u);
        openRings--;
    }

    /**
     * Decide the bond to use for a ring bond. The bond symbol can be present on
     * either or both bonded atoms. This method takes those bonds, chooses the
     * correct one or reports an error if there is a conflict.
     * <p>
     * Equivalent SMILES:
     * <blockquote><pre>
     *     C=1CCCCC=1
     *     C=1CCCCC1    (preferred)
     *     C1CCCCC=1
     * </pre></blockquote>
     *
     * @param a      a bond
     * @param b      other bond
     * @param pos    the position in the string of bond a
     * @param buffer the buffer and it's current position
     * @return the bond to use for this edge
     * @throws InvalidSmilesException ring bonds did not match
     */
    Bond decideBond(final Bond a, final Bond b, int pos, CharBuffer buffer) throws InvalidSmilesException {
        if (a == b)
            return a;
        else if (a == Bond.IMPLICIT)
            return b;
        else if (b == Bond.IMPLICIT)
            return a;
        if (strict || a.inverse() != b)
            throw new InvalidSmilesException("Ring closure bonds did not match,  '" + a + "'!='" + b + "':" +
                                             InvalidSmilesException.display(buffer,
                                                                            pos - buffer.position,
                                                                            lastBondPos - buffer.position));
        warnings.add("Ignored invalid Cis/Trans on ring closure, should flip:" +
                     InvalidSmilesException.display(buffer, pos - buffer.position,
                                                    lastBondPos - buffer.position));
        return Bond.IMPLICIT;
    }

    /**
     * Convenience method for parsing a SMILES string.
     *
     * @param str SMILES string
     * @return the chemical graph for the provided SMILES notation
     * @throws InvalidSmilesException thrown if the SMILES could not be
     *                                interpreted
     */
    static Graph parse(String str) throws InvalidSmilesException {
        return new Parser(str).molecule();
    }

    /**
     * Access any warning messages from parsing the SMILES.
     *
     * @return the warnings.
     */
    public Collection<? extends String> getWarnings() {
        return Collections.unmodifiableCollection(warnings);
    }

    /**
     * Hold information about ring open/closures. The ring bond can optionally
     * specify the bond type.
     */
    private static final class RingBond {
        int u;
        Bond bond;
        int pos;

        private RingBond(int u, Bond bond, int pos) {
            this.u = u;
            this.bond = bond;
            this.pos = pos;
        }
    }

    /**
     * Hold information on the local arrangement around an atom. The arrangement
     * is normally identical to the order loaded unless the atom is involved in
     * a ring closure. This is particularly important for stereo specification
     * where the ring bonds should be in the order listed. This class stores the
     * local arrangement by setting a negated 'rnum' as a placeholder and then
     * replacing it once the connected atom has been read. Although this could
     * be stored directly on the graph (negated edge) it allows us to keep all
     * edges in sorted order.
     */
    private static final class LocalArrangement {

        int[] vs;
        int n;

        /**
         * New local arrangement.
         */
        private LocalArrangement() {
            this.vs = new int[4];
        }

        /**
         * Append a vertex to the arrangement.
         *
         * @param v vertex to append
         */
        void add(final int v) {
            if (n == vs.length)
                vs = Arrays.copyOf(vs, n * 2);
            vs[n++] = v;
        }

        /**
         * Replace the vertex 'u' with 'v'. Allows us to use negated values as
         * placeholders.
         *
         * <blockquote><pre>
         * LocalArrangement la = new LocalArrangement();
         * la.add(1);
         * la.add(-2);
         * la.add(-1);
         * la.add(5);
         * la.replace(-1, 4);
         * la.replace(-2, 6);
         * la.toArray() = {1, 6, 4, 5}
         * </pre></blockquote>
         *
         * @param u negated vertex
         * @param v new vertex
         */
        void replace(final int u, final int v) {
            for (int i = 0; i < n; i++) {
                if (vs[i] == u) {
                    vs[i] = v;
                    return;
                }
            }
        }

        /**
         * Access the local arrange of vertices.
         *
         * @return array of vertices and there order around an atom.
         */
        int[] toArray() {
            return Arrays.copyOf(vs, n);
        }
    }
}
