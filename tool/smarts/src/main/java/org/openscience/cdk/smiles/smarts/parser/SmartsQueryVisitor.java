/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * (or see http://www.gnu.org/copyleft/lesser.html)
 */
package org.openscience.cdk.smiles.smarts.parser;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ReactionRole;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.isomorphism.ComponentGrouping;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.AliphaticAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AliphaticSymbolAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyOrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticOrSingleQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticSymbolAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AtomicNumberAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.ChiralityAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.ExplicitConnectionAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.FormalChargeAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.HybridizationNumberAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.HydrogenAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.ImplicitHCountAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.LogicalOperatorAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.LogicalOperatorBond;
import org.openscience.cdk.isomorphism.matchers.smarts.MassAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.NonCHHeavyAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.PeriodicGroupNumberAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.ReactionRoleQueryAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.RecursiveSmartsAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.RingBond;
import org.openscience.cdk.isomorphism.matchers.smarts.RingIdentifierAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.RingMembershipAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SmallestRingAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.StereoBond;
import org.openscience.cdk.isomorphism.matchers.smarts.TotalConnectionAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.TotalHCountAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.TotalRingConnectionAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.TotalValencyAtom;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.TetrahedralChirality;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation;

/**
 * An AST tree visitor. It builds an instance of <code>QueryAtomContainer</code>
 * from the AST tree.
 *
 * To use this visitor:
 * <pre>
 * SMARTSParser parser = new SMARTSParser(new java.io.StringReader("C*C"));
 * ASTStart ast = parser.start();
 * SmartsQueryVisitor visitor = new SmartsQueryVisitor();
 * QueryAtomContainer query = visitor.visit(ast, null);
 * </pre>
 *
 * @author Dazhi Jiao
 * @cdk.created 2007-04-24
 * @cdk.module smarts
 * @cdk.githash
 * @cdk.keyword SMARTS AST
 */
public class SmartsQueryVisitor implements SMARTSParserVisitor {

    // current atoms with a ring identifier
    private RingIdentifierAtom[]                ringAtoms;

    private Multimap<IAtom, RingIdentifierAtom> ringAtomLookup = HashMultimap.create(10, 2);

    // query
    private IQueryAtomContainer                 query;

    private final IChemObjectBuilder            builder;

    /**
     * Maintain order of neighboring atoms - required for atom-based
     * stereochemistry.
     */
    private Map<IAtom, List<IAtom>>             neighbors      = new HashMap<IAtom, List<IAtom>>();

    /**
     * Lookup of atom indices.
     */
    private BitSet                              tetrahedral    = new BitSet();

    /**
     * Stores the directional '/' or '\' bonds. Speeds up looking for double
     * bond configurations.
     */
    private List<IBond>                         stereoBonds    = new ArrayList<IBond>();

    /**
     * Stores the double bonds in the query.
     */
    private List<IBond>                         doubleBonds    = new ArrayList<IBond>();

    public SmartsQueryVisitor(IChemObjectBuilder builder) {
        this.builder = builder;
    }

    public Object visit(ASTRingIdentifier node, Object data) {
        IQueryAtom atom = (IQueryAtom) data;
        RingIdentifierAtom ringIdAtom = new RingIdentifierAtom(builder);
        ringIdAtom.setAtom(atom);
        IQueryBond bond;
        if (node.jjtGetNumChildren() == 0) { // implicit bond
            bond = null;
        } else {
            bond = (IQueryBond) node.jjtGetChild(0).jjtAccept(this, data);
        }
        ringIdAtom.setRingBond(bond);
        return ringIdAtom;
    }

    public Object visit(ASTAtom node, Object data) {
        IQueryAtom atom = (IQueryAtom) node.jjtGetChild(0).jjtAccept(this, data);
        for (int i = 1; i < node.jjtGetNumChildren(); i++) { // if there are ring identifiers
            ASTRingIdentifier ringIdentifier = (ASTRingIdentifier) node.jjtGetChild(i);
            RingIdentifierAtom ringIdAtom = (RingIdentifierAtom) ringIdentifier.jjtAccept(this, atom);

            // if there is already a RingIdentifierAtom, create a bond between
            // them and add the bond to the query
            int ringId = ringIdentifier.getRingId();

            // ring digit > 9 - expand capacity
            if (ringId >= ringAtoms.length) ringAtoms = Arrays.copyOf(ringAtoms, 100);

            // Ring Open
            if (ringAtoms[ringId] == null) {
                ringAtoms[ringId] = ringIdAtom;
                ringAtomLookup.put(atom, ringIdAtom);
            }

            // Ring Close
            else {
                IQueryBond ringBond;
                // first check if the two bonds ma
                if (ringAtoms[ringId].getRingBond() == null) {
                    if (ringIdAtom.getRingBond() == null) {
                        if (atom instanceof AromaticSymbolAtom
                                && ringAtoms[ringId].getAtom() instanceof AromaticSymbolAtom) {
                            ringBond = new AromaticQueryBond(builder);
                        } else {
                            ringBond = new RingBond(builder);
                        }
                    } else {
                        ringBond = ringIdAtom.getRingBond();
                    }
                } else {
                    // Here I assume the bond are always same. This should be checked by the parser already
                    ringBond = ringAtoms[ringId].getRingBond();
                }
                ((IBond) ringBond).setAtoms(new IAtom[]{ringAtoms[ringId].getAtom(), atom});
                query.addBond((IBond) ringBond);

                // if the connected atoms was tracking neighbors, replace the
                // placeholder reference
                if (neighbors.containsKey(ringAtoms[ringId].getAtom())) {
                    List<IAtom> localNeighbors = neighbors.get(ringAtoms[ringId].getAtom());
                    localNeighbors.set(localNeighbors.indexOf(ringAtoms[ringId]), atom);
                }

                ringAtomLookup.remove(ringAtoms[ringId].getAtom(), ringIdAtom);
                ringAtoms[ringId] = null;
            }
        }
        return atom;
    }

    private final static ILoggingTool logger = LoggingToolFactory.createLoggingTool(SmartsQueryVisitor.class);

    public Object visit(SimpleNode node, Object data) {
        return null;
    }

    public Object visit(ASTStart node, Object data) {
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    public Object visit(ASTReaction node, Object data) {
        IAtomContainer query = new QueryAtomContainer(builder);
        for (int grpIdx = 0; grpIdx < node.jjtGetNumChildren(); grpIdx++) {

            int rollback = query.getAtomCount();

            ASTGroup group = (ASTGroup) node.jjtGetChild(grpIdx);
            group.jjtAccept(this, query);

            // fill in the roles for newly create atoms
            if (group.getRole() != ASTGroup.ROLE_ANY) {
                IQueryAtom roleQueryAtom = null;
                ReactionRole role = null;

                // use single instances
                switch (group.getRole()) {
                    case ASTGroup.ROLE_REACTANT:
                        roleQueryAtom = ReactionRoleQueryAtom.RoleReactant;
                        role = ReactionRole.Reactant;
                        break;
                    case ASTGroup.ROLE_AGENT:
                        roleQueryAtom = ReactionRoleQueryAtom.RoleAgent;
                        role = ReactionRole.Agent;
                        break;
                    case ASTGroup.ROLE_PRODUCT:
                        roleQueryAtom = ReactionRoleQueryAtom.RoleProduct;
                        role = ReactionRole.Product;
                        break;
                }

                if (roleQueryAtom != null) {
                    while (rollback < query.getAtomCount()) {
                        IAtom org = query.getAtom(rollback);
                        IAtom rep = LogicalOperatorAtom.and(roleQueryAtom, (IQueryAtom) org);
                        // ensure AAM is propagated
                        rep.setProperty(CDKConstants.ATOM_ATOM_MAPPING, org.getProperty(CDKConstants.ATOM_ATOM_MAPPING));
                        rep.setProperty(CDKConstants.REACTION_ROLE, role);
                        AtomContainerManipulator.replaceAtomByAtom(query,
                                                                   org,
                                                                   rep);
                        rollback++;
                    }
                }
            }
        }
        return query;
    }

    public Object visit(ASTGroup node, Object data) {
        IAtomContainer fullQuery = (IAtomContainer) data;

        if (fullQuery == null)
            fullQuery = new QueryAtomContainer(builder);

        // keeps track of component grouping
        int[] components = new int[0];
        int maxId = 0;

        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            ASTSmarts smarts = (ASTSmarts) node.jjtGetChild(i);
            ringAtoms = new RingIdentifierAtom[10];
            query = new QueryAtomContainer(builder);

            smarts.jjtAccept(this, null);

            // update component info
            if (smarts.componentId() > 0) {
                components = Arrays.copyOf(components, 1 + fullQuery.getAtomCount() + query.getAtomCount());
                int id = smarts.componentId();
                Arrays.fill(components, fullQuery.getAtomCount(), components.length, id);
                if (id > maxId) maxId = id;
            }

            fullQuery.add(query);
        }

        // only store if there was a component grouping
        if (maxId > 0) {
            components[components.length - 1] = maxId; // we left space to store how many groups there were
            fullQuery.setProperty(ComponentGrouping.KEY, components);
        }

        // create tetrahedral elements
        for (IAtom atom : neighbors.keySet()) {
            List<IAtom> localNeighbors = neighbors.get(atom);
            if (localNeighbors.size() == 4) {
                fullQuery.addStereoElement(new TetrahedralChirality(atom, localNeighbors.toArray(new IAtom[4]),
                        ITetrahedralChirality.Stereo.CLOCKWISE)); // <- to be modified later
            } else if (localNeighbors.size() == 5) {
                localNeighbors.remove(atom); // remove central atom (which represented implicit part)
                fullQuery.addStereoElement(new TetrahedralChirality(atom, localNeighbors.toArray(new IAtom[4]),
                        ITetrahedralChirality.Stereo.CLOCKWISE)); // <- to be modified later
            }
        }

        // for each double bond, find the stereo bonds. currently doesn't
        // handle logical bonds i.e. C/C-,=C/C
        for (IBond bond : doubleBonds) {
            IAtom left = bond.getAtom(0);
            IAtom right = bond.getAtom(1);
            StereoBond leftBond = findStereoBond(left);
            StereoBond rightBond = findStereoBond(right);
            if (leftBond == null || rightBond == null) continue;
            Conformation conformation = leftBond.direction(left) == rightBond.direction(right) ? Conformation.TOGETHER
                    : Conformation.OPPOSITE;
            fullQuery.addStereoElement(new DoubleBondStereochemistry(bond, new IBond[]{leftBond, rightBond},
                    conformation));
        }

        return fullQuery;
    }

    /**
     * Locate a stereo bond adjacent to the {@code atom}.
     *
     * @param atom an atom
     * @return a stereo bond or null if non found
     */
    private StereoBond findStereoBond(IAtom atom) {
        for (IBond bond : stereoBonds)
            if (bond.contains(atom)) return (StereoBond) bond;
        return null;
    }

    public Object visit(ASTSmarts node, Object data) {
        SMARTSAtom atom = null;
        SMARTSBond bond = null;

        ASTAtom first = (ASTAtom) node.jjtGetChild(0);
        atom = (SMARTSAtom) first.jjtAccept(this, null);
        if (data != null) { // this is a sub smarts
            bond = (SMARTSBond) ((Object[]) data)[1];
            IAtom prev = (SMARTSAtom) ((Object[]) data)[0];
            if (bond == null) { // since no bond was specified it could be aromatic or single
                bond = new AromaticOrSingleQueryBond(builder);
                bond.setAtoms(new IAtom[]{prev, atom});
            } else {
                bond.setAtoms(new IAtom[]{prev, atom});
            }
            if (neighbors.containsKey(prev)) {
                neighbors.get(prev).add(atom);
            }
            query.addBond(bond);
            bond = null;
        }
        query.addAtom(atom);

        if (tetrahedral.get(query.getAtomCount() - 1)) {
            List<IAtom> localNeighbors = new ArrayList<IAtom>(query.getConnectedAtomsList(atom));
            localNeighbors.add(atom);
            // placeholders for ring closure
            for (RingIdentifierAtom ringIdAtom : ringAtomLookup.get(atom))
                localNeighbors.add(ringIdAtom);
            neighbors.put(atom, localNeighbors);
        }

        for (int i = 1; i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            if (child instanceof ASTLowAndBond) {
                bond = (SMARTSBond) child.jjtAccept(this, data);
            } else if (child instanceof ASTAtom) {
                SMARTSAtom newAtom = (SMARTSAtom) child.jjtAccept(this, null);
                if (bond == null) { // since no bond was specified it could be aromatic or single
                    bond = new AromaticOrSingleQueryBond(builder);
                }
                bond.setAtoms(new IAtom[]{atom, newAtom});
                query.addBond(bond);
                query.addAtom(newAtom);

                if (neighbors.containsKey(atom)) {
                    neighbors.get(atom).add(newAtom);
                }
                if (tetrahedral.get(query.getAtomCount() - 1)) {
                    List<IAtom> localNeighbors = new ArrayList<IAtom>(query.getConnectedAtomsList(newAtom));
                    localNeighbors.add(newAtom);
                    // placeholders for ring closure
                    for (RingIdentifierAtom ringIdAtom : ringAtomLookup.get(newAtom))
                        localNeighbors.add(ringIdAtom);
                    neighbors.put(newAtom, localNeighbors);
                }

                atom = newAtom;
                bond = null;
            } else if (child instanceof ASTSmarts) { // another smarts
                child.jjtAccept(this, new Object[]{atom, bond});
                bond = null;
            }
        }

        return query;
    }

    public Object visit(ASTNotBond node, Object data) {
        Object left = node.jjtGetChild(0).jjtAccept(this, data);
        if (node.getType() == SMARTSParserConstants.NOT) {
            LogicalOperatorBond bond = new LogicalOperatorBond(builder);
            bond.setOperator("not");
            bond.setLeft((IQueryBond) left);
            return bond;
        } else {
            return left;
        }
    }

    public Object visit(ASTImplicitHighAndBond node, Object data) {
        Object left = node.jjtGetChild(0).jjtAccept(this, data);
        if (node.jjtGetNumChildren() == 1) {
            return left;
        }
        LogicalOperatorBond bond = new LogicalOperatorBond(builder);
        bond.setOperator("and");
        bond.setLeft((IQueryBond) left);
        IQueryBond right = (IQueryBond) node.jjtGetChild(1).jjtAccept(this, data);
        bond.setRight(right);
        return bond;
    }

    public Object visit(ASTLowAndBond node, Object data) {
        Object left = node.jjtGetChild(0).jjtAccept(this, data);
        if (node.jjtGetNumChildren() == 1) {
            return left;
        }
        LogicalOperatorBond bond = new LogicalOperatorBond(builder);
        bond.setOperator("and");
        bond.setLeft((IQueryBond) left);
        IQueryBond right = (IQueryBond) node.jjtGetChild(1).jjtAccept(this, data);
        bond.setRight(right);
        return bond;
    }

    public Object visit(ASTOrBond node, Object data) {
        Object left = node.jjtGetChild(0).jjtAccept(this, data);
        if (node.jjtGetNumChildren() == 1) {
            return left;
        }
        LogicalOperatorBond bond = new LogicalOperatorBond(builder);
        bond.setOperator("or");
        bond.setLeft((IQueryBond) left);
        IQueryBond right = (IQueryBond) node.jjtGetChild(1).jjtAccept(this, data);
        bond.setRight(right);
        return bond;
    }

    public Object visit(ASTExplicitHighAndBond node, Object data) {
        Object left = node.jjtGetChild(0).jjtAccept(this, data);
        if (node.jjtGetNumChildren() == 1) {
            return left;
        }
        LogicalOperatorBond bond = new LogicalOperatorBond(builder);
        bond.setOperator("and");
        bond.setLeft((IQueryBond) left);
        IQueryBond right = (IQueryBond) node.jjtGetChild(1).jjtAccept(this, data);
        bond.setRight(right);
        return bond;
    }

    public Object visit(ASTSimpleBond node, Object data) {
        SMARTSBond bond = null;
        switch (node.getBondType()) {
            case SMARTSParserConstants.S_BOND:
                bond = new OrderQueryBond(IBond.Order.SINGLE, builder);
                break;
            case SMARTSParserConstants.D_BOND:
                bond = new OrderQueryBond(IBond.Order.DOUBLE, builder);
                doubleBonds.add(bond);
                break;
            case SMARTSParserConstants.T_BOND:
                bond = new OrderQueryBond(IBond.Order.TRIPLE, builder);
                break;
            case SMARTSParserConstants.DOLLAR:
                bond = new OrderQueryBond(IBond.Order.QUADRUPLE, builder);
                break;
            case SMARTSParserConstants.ANY_BOND:
                bond = new AnyOrderQueryBond(builder);
                break;
            case SMARTSParserConstants.AR_BOND:
                bond = new AromaticQueryBond(builder);
                break;
            case SMARTSParserConstants.R_BOND:
                bond = new RingBond(builder);
                break;
            case SMARTSParserConstants.UP_S_BOND:
                bond = new StereoBond(builder, StereoBond.Direction.UP, false);
                stereoBonds.add(bond);
                break;
            case SMARTSParserConstants.DN_S_BOND:
                bond = new StereoBond(builder, StereoBond.Direction.DOWN, false);
                stereoBonds.add(bond);
                break;
            case SMARTSParserConstants.UP_OR_UNSPECIFIED_S_BOND:
                bond = new StereoBond(builder, StereoBond.Direction.UP, true);
                stereoBonds.add(bond);
                break;
            case SMARTSParserConstants.DN_OR_UNSPECIFIED_S_BOND:
                bond = new StereoBond(builder, StereoBond.Direction.DOWN, true);
                stereoBonds.add(bond);
                break;
            default:
                logger.error("Un parsed bond: " + node.toString());
                break;
        }
        return bond;
    }

    public Object visit(ASTRecursiveSmartsExpression node, Object data) {
        SmartsQueryVisitor recursiveVisitor = new SmartsQueryVisitor(builder);
        recursiveVisitor.query = new QueryAtomContainer(builder);
        recursiveVisitor.ringAtoms = new RingIdentifierAtom[10];
        return new RecursiveSmartsAtom((IQueryAtomContainer) node.jjtGetChild(0).jjtAccept(recursiveVisitor, null));
    }

    public ASTStart getRoot(Node node) {
        if (node instanceof ASTStart) {
            return (ASTStart) node;
        }
        return getRoot(node.jjtGetParent());
    }

    public Object visit(ASTElement node, Object data) {
        String symbol = node.getSymbol();
        SMARTSAtom atom;
        if ("o".equals(symbol) || "n".equals(symbol) || "c".equals(symbol) || "s".equals(symbol) || "p".equals(symbol)
                || "as".equals(symbol) || "se".equals(symbol)) {
            String atomSymbol = symbol.substring(0, 1).toUpperCase() + symbol.substring(1);
            atom = new AromaticSymbolAtom(atomSymbol, builder);
        } else {
            atom = new AliphaticSymbolAtom(symbol, builder);
        }
        return atom;
    }

    public Object visit(ASTTotalHCount node, Object data) {
        return new TotalHCountAtom(node.getCount(), builder);
    }

    public Object visit(ASTImplicitHCount node, Object data) {
        return new ImplicitHCountAtom(node.getCount(), builder);
    }

    public Object visit(ASTExplicitConnectivity node, Object data) {
        return new ExplicitConnectionAtom(node.getNumOfConnection(), builder);
    }

    public Object visit(ASTAtomicNumber node, Object data) {
        return new AtomicNumberAtom(node.getNumber(), builder);
    }

    public Object visit(ASTHybrdizationNumber node, Object data) {
        return new HybridizationNumberAtom(node.getHybridizationNumber(), builder);
    }

    public Object visit(ASTCharge node, Object data) {
        if (node.isPositive()) {
            return new FormalChargeAtom(node.getCharge(), builder);
        } else {
            return new FormalChargeAtom(0 - node.getCharge(), builder);
        }
    }

    public Object visit(ASTRingConnectivity node, Object data) {
        return new TotalRingConnectionAtom(node.getNumOfConnection(), builder);
    }

    public Object visit(ASTPeriodicGroupNumber node, Object data) {
        return new PeriodicGroupNumberAtom(node.getGroupNumber(), builder);
    }

    public Object visit(ASTTotalConnectivity node, Object data) {
        return new TotalConnectionAtom(node.getNumOfConnection(), builder);
    }

    public Object visit(ASTValence node, Object data) {
        return new TotalValencyAtom(node.getOrder(), builder);
    }

    public Object visit(ASTRingMembership node, Object data) {
        return new RingMembershipAtom(node.getNumOfMembership(), builder);
    }

    public Object visit(ASTSmallestRingSize node, Object data) {
        return new SmallestRingAtom(node.getSize(), builder);
    }

    public Object visit(ASTAliphatic node, Object data) {
        return new AliphaticAtom(builder);
    }

    public Object visit(ASTNonCHHeavyAtom node, Object data) {
        return new NonCHHeavyAtom(builder);
    }

    public Object visit(ASTAromatic node, Object data) {
        return new AromaticAtom(builder);
    }

    public Object visit(ASTAnyAtom node, Object data) {
        return new AnyAtom(builder);
    }

    public Object visit(ASTAtomicMass node, Object data) {
        return new MassAtom(node.getMass(), builder);
    }

    public Object visit(ASTChirality node, Object data) {
        ChiralityAtom atom = new ChiralityAtom(builder);
        atom.setClockwise(node.isClockwise());
        atom.setUnspecified(node.isUnspecified());
        tetrahedral.set(query.getAtomCount());
        return atom;
    }

    public Object visit(ASTLowAndExpression node, Object data) {
        IAtom expr = (IAtom) node.jjtGetChild(0).jjtAccept(this, data);
        if (node.jjtGetNumChildren() > 1) {
            IQueryAtom right = (IQueryAtom) node.jjtGetChild(1).jjtAccept(this, data);
            expr = LogicalOperatorAtom.and((IQueryAtom) expr, right);
        }
        if (node.getMapIdx()>0)
            expr.setProperty(CDKConstants.ATOM_ATOM_MAPPING, node.getMapIdx());
        return expr;
    }

    public Object visit(ASTOrExpression node, Object data) {
        Object left = node.jjtGetChild(0).jjtAccept(this, data);
        if (node.jjtGetNumChildren() == 1) {
            return left;
        }
        IQueryAtom right = (IQueryAtom) node.jjtGetChild(1).jjtAccept(this, data);
        return LogicalOperatorAtom.or((IQueryAtom) left, right);
    }

    public Object visit(ASTNotExpression node, Object data) {
        Object left = node.jjtGetChild(0).jjtAccept(this, data);
        if (node.getType() == SMARTSParserConstants.NOT) {
            return LogicalOperatorAtom.not((IQueryAtom) left);
        }
        return left;
    }

    public Object visit(ASTExplicitHighAndExpression node, Object data) {
        Object left = node.jjtGetChild(0).jjtAccept(this, data);
        if (node.jjtGetNumChildren() == 1) {
            return left;
        }
        IQueryAtom right = (IQueryAtom) node.jjtGetChild(1).jjtAccept(this, data);
        return LogicalOperatorAtom.and((IQueryAtom) left, right);
    }

    public Object visit(ASTImplicitHighAndExpression node, Object data) {
        Object left = node.jjtGetChild(0).jjtAccept(this, data);
        if (node.jjtGetNumChildren() == 1) {
            return left;
        }
        IQueryAtom right = (IQueryAtom) node.jjtGetChild(1).jjtAccept(this, data);
        return LogicalOperatorAtom.and((IQueryAtom) left, right);
    }

    public Object visit(ASTExplicitAtom node, Object data) {
        IQueryAtom atom = null;
        String symbol = node.getSymbol();
        if ("*".equals(symbol)) {
            atom = new AnyAtom(builder);
        } else if ("A".equals(symbol)) {
            atom = new AliphaticAtom(builder);
        } else if ("a".equals(symbol)) {
            atom = new AromaticAtom(builder);
        } else if ("o".equals(symbol) || "n".equals(symbol) || "c".equals(symbol) || "s".equals(symbol)
                || "p".equals(symbol) || "as".equals(symbol) || "se".equals(symbol)) {
            String atomSymbol = symbol.substring(0, 1).toUpperCase() + symbol.substring(1);
            atom = new AromaticSymbolAtom(atomSymbol, builder);
        } else if ("H".equals(symbol)) {
            atom = new HydrogenAtom(builder);
            atom.setSymbol(symbol.toUpperCase());
            atom.setMassNumber(1);
        } else if ("D".equals(symbol)) {
            atom = new HydrogenAtom(builder);
            atom.setSymbol(symbol.toUpperCase());
            atom.setMassNumber(2);
        } else if ("T".equals(symbol)) {
            atom = new HydrogenAtom(builder);
            atom.setSymbol(symbol.toUpperCase());
            atom.setMassNumber(3);
        } else {
            atom = new AliphaticSymbolAtom(symbol, builder);
        }
        return atom;
    }
}
