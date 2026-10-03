/* Copyright (C) 2009-2010 Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your container code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.smsd;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.openscience.cdk.AtomRef;
import org.openscience.cdk.BondRef;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryBond;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.Bond;
import org.openscience.cdk.smsd.algorithm.matchers.DefaultVFAtomMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.DefaultVFBondMatcher;
import org.openscience.cdk.smsd.algorithm.vflib.builder.VFQueryBuilder;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IEdge;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQuery;
import org.openscience.cdk.smsd.algorithm.vflib.map.VFMapper;
import org.openscience.cdk.smsd.algorithm.vflib.query.QueryCompiler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QueryGraphValidationTest {

    @Test
    void rejectsNullCompilerContainersImmediately() {
        assertThrows(NullPointerException.class, () -> new QueryCompiler((IAtomContainer) null, true));
        assertThrows(NullPointerException.class, () -> new QueryCompiler((IQueryAtomContainer) null));
    }

    @Test
    void rejectsNullNodesBeforeChangingTheBuilder() {
        VFQueryBuilder builder = new VFQueryBuilder();
        assertThrows(NullPointerException.class, () -> builder.addNode(null, new Atom("C")));
        assertThrows(NullPointerException.class, () -> builder.addNode(new DefaultVFAtomMatcher(), null));
        assertEquals(0, builder.countNodes());
        assertEquals(0, builder.countEdges());
    }

    @Test
    void duplicateAtomsAreRejectedAcrossReferenceWrappers() {
        VFQueryBuilder builder = new VFQueryBuilder();
        IAtom atom = new Atom("C");
        IAtom reference = new AtomRef(new AtomRef(atom));
        INode node = builder.addNode(new DefaultVFAtomMatcher(), reference);
        assertThrows(IllegalArgumentException.class,
                () -> builder.addNode(new DefaultVFAtomMatcher(), atom));
        assertThrows(IllegalArgumentException.class,
                () -> builder.addNode(new DefaultVFAtomMatcher(), new AtomRef(atom)));
        assertEquals(1, builder.countNodes());
        assertSame(reference, builder.getAtom(node));
    }

    @Test
    void atomLookupPreservesContainerAndUnderlyingReferences() {
        IAtom atom = new Atom("C");
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        molecule.addAtom(atom);
        IAtomContainer other = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        other.addAtom(atom);
        VFQueryBuilder builder = new VFQueryBuilder();
        INode node = builder.addNode(new DefaultVFAtomMatcher(), molecule.getAtom(0));
        assertSame(node, builder.getNode(atom));
        assertSame(node, builder.getNode(molecule.getAtom(0)));
        assertSame(node, builder.getNode(other.getAtom(0)));
        assertSame(node, builder.getNode(new AtomRef(atom)));
        assertNull(builder.getNode(new Atom("C")));
        assertNull(builder.getNode((IAtom) null));
    }

    @Test
    void rejectsNullAndForeignEdgesWithoutChangingEitherGraph() {
        VFQueryBuilder builder = new VFQueryBuilder();
        INode first = builder.addNode(new DefaultVFAtomMatcher(), new Atom("C"));
        INode second = builder.addNode(new DefaultVFAtomMatcher(), new Atom("O"));
        VFQueryBuilder other = new VFQueryBuilder();
        INode foreign = other.addNode(new DefaultVFAtomMatcher(), new Atom("C"));
        DefaultVFBondMatcher matcher = new DefaultVFBondMatcher();
        assertThrows(NullPointerException.class, () -> builder.connect(null, second, matcher));
        assertThrows(NullPointerException.class, () -> builder.connect(first, null, matcher));
        assertThrows(NullPointerException.class, () -> builder.connect(first, second, null));
        assertThrows(IllegalArgumentException.class, () -> builder.connect(first, foreign, matcher));
        assertThrows(IllegalArgumentException.class, () -> builder.connect(foreign, second, matcher));
        assertEquals(0, builder.countEdges());
        assertEquals(0, other.countEdges());
        assertEquals(0, first.countNeighbors());
        assertEquals(0, second.countNeighbors());
        assertEquals(0, foreign.countNeighbors());
        assertNull(builder.getEdge(first, foreign));
    }

    @Test
    void rejectsLoopsAndParallelEdgesBeforeUpdatingAdjacency() {
        VFQueryBuilder builder = new VFQueryBuilder();
        INode first = builder.addNode(new DefaultVFAtomMatcher(), new Atom("C"));
        INode second = builder.addNode(new DefaultVFAtomMatcher(), new Atom("O"));
        DefaultVFBondMatcher matcher = new DefaultVFBondMatcher();
        assertThrows(IllegalArgumentException.class, () -> builder.connect(first, first, matcher));
        IEdge edge = builder.connect(first, second, matcher);
        assertThrows(IllegalArgumentException.class, () -> builder.connect(first, second, matcher));
        assertThrows(IllegalArgumentException.class, () -> builder.connect(second, first, matcher));
        assertEquals(1, builder.countEdges());
        assertEquals(1, first.countNeighbors());
        assertEquals(1, second.countNeighbors());
        assertSame(edge, builder.getEdge(first, second));
        assertSame(edge, builder.getEdge(second, first));
    }

    @Test
    void highDegreeEdgeLookupWorksFromBothEndpoints() {
        VFQueryBuilder builder = new VFQueryBuilder();
        INode hub = builder.addNode(new DefaultVFAtomMatcher(), new Atom("C"));
        INode[] leaves = new INode[128];
        IEdge[] edges = new IEdge[leaves.length];
        for (int i = 0; i < leaves.length; i++) {
            leaves[i] = builder.addNode(new DefaultVFAtomMatcher(), new Atom("C"));
            edges[i] = i % 2 == 0
                    ? builder.connect(hub, leaves[i], new DefaultVFBondMatcher())
                    : builder.connect(leaves[i], hub, new DefaultVFBondMatcher());
        }
        assertEquals(leaves.length, hub.countNeighbors());
        for (int i = 0; i < leaves.length; i++) {
            assertEquals(1, leaves[i].countNeighbors());
            assertSame(edges[i], builder.getEdge(hub, leaves[i]));
            assertSame(edges[i], builder.getEdge(leaves[i], hub));
        }
        assertNull(builder.getEdge(leaves[0], leaves[1]));
    }

    @Test
    void queryViewsRemainLiveAndCannotMutateTheBuilder() {
        VFQueryBuilder builder = new VFQueryBuilder();
        Iterable<INode> nodes = builder.nodes();
        Iterable<IEdge> edges = builder.edges();
        INode first = builder.addNode(new DefaultVFAtomMatcher(), new Atom("C"));
        INode second = builder.addNode(new DefaultVFAtomMatcher(), new Atom("O"));
        IEdge edge = builder.connect(first, second, new DefaultVFBondMatcher());
        Iterator<INode> nodeIterator = nodes.iterator();
        assertSame(first, nodeIterator.next());
        assertThrows(UnsupportedOperationException.class, nodeIterator::remove);
        assertSame(second, nodeIterator.next());
        Iterator<IEdge> edgeIterator = edges.iterator();
        assertSame(edge, edgeIterator.next());
        assertThrows(UnsupportedOperationException.class, edgeIterator::remove);
        assertEquals(2, builder.countNodes());
        assertEquals(1, builder.countEdges());
    }

    @Test
    void compilerRejectsNullAtomsAndBonds() {
        assertThrows(NullPointerException.class,
                () -> new QueryCompiler(raw(new IAtom[]{null}), true).compile());
        IAtom atom = new Atom("C");
        assertThrows(NullPointerException.class,
                () -> new QueryCompiler(raw(new IAtom[]{atom}, (IBond) null), true).compile());
    }

    @Test
    void compilerRejectsMissingAndNullEndpoints() {
        IAtom first = new Atom("C");
        IAtom foreign = new Atom("O");
        assertThrows(IllegalArgumentException.class,
                () -> new QueryCompiler(raw(new IAtom[]{first}, new Bond(first, foreign)), true).compile());
        assertThrows(NullPointerException.class,
                () -> new QueryCompiler(raw(new IAtom[]{first}, new Bond(first, null)), true).compile());
        assertThrows(NullPointerException.class,
                () -> new QueryCompiler(raw(new IAtom[]{first}, new Bond(null, first)), true).compile());
    }

    @Test
    void compilerRejectsLoopsAndNonBinaryBonds() {
        IAtom first = new Atom("C");
        IAtom second = new Atom("O");
        IAtom third = new Atom("N");
        assertThrows(IllegalArgumentException.class,
                () -> new QueryCompiler(raw(new IAtom[]{first}, new Bond(first, first)), true).compile());
        for (IAtom[] endpoints : new IAtom[][]{{}, {first}, {first, second, third}}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new QueryCompiler(raw(new IAtom[]{first, second, third},
                            new Bond(endpoints, IBond.Order.SINGLE)), true).compile());
        }
    }

    @Test
    void compilerRejectsRepeatedUndirectedBonds() {
        IAtom first = new Atom("C");
        IAtom second = new Atom("O");
        assertThrows(IllegalArgumentException.class,
                () -> new QueryCompiler(raw(new IAtom[]{first, second},
                        new Bond(first, second, IBond.Order.SINGLE),
                        new Bond(second, first, IBond.Order.DOUBLE)), true).compile());
    }

    @Test
    void compilerRejectsRepeatedUnderlyingAtoms() {
        IAtom atom = new Atom("C");
        assertThrows(IllegalArgumentException.class,
                () -> new QueryCompiler(raw(new IAtom[]{atom, new AtomRef(atom)}), true).compile());
    }

    @Test
    void compilerPreservesWrappedPredicatesAndOriginalAtoms() {
        IAtom oxygen = new AtomRef(new QueryAtom(new Expr(Expr.Type.ELEMENT, 8)));
        IAtom carbon = new Atom("C");
        IBond bond = new BondRef(new QueryBond(oxygen, carbon, new Expr(Expr.Type.ALIPHATIC_ORDER, 2)));
        IAtomContainer query = raw(new IAtom[]{oxygen, carbon}, bond);
        IQuery compiled = new QueryCompiler(query, false).compile();
        assertSame(oxygen, compiled.getAtom(compiled.getNode(0)));
        IAtomContainer target = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        target.addAtom(new Atom("O"));
        target.addAtom(new Atom("C"));
        target.addBond(0, 1, IBond.Order.DOUBLE);
        assertTrue(new VFMapper(compiled).hasMap(target));
        target.getBond(0).setOrder(IBond.Order.SINGLE);
        assertFalse(new VFMapper(compiled).hasMap(target));
    }

    private static IAtomContainer raw(IAtom[] queryAtoms, IBond... queryBonds) {
        return new org.openscience.cdk.AtomContainerLegacy() {
            @Override
            public Iterable<IAtom> atoms() {
                return Arrays.asList(queryAtoms);
            }

            @Override
            public Iterable<IBond> bonds() {
                return Arrays.asList(queryBonds);
            }
        };
    }
}
