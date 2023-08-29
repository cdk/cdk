/*
 * Copyright (C) 2022 John Mayfield
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.openscience.cdk.smirks;

import org.openscience.cdk.BondRef;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ReactionRole;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.isomorphism.DfPattern;
import org.openscience.cdk.isomorphism.Transform;
import org.openscience.cdk.isomorphism.TransformOp;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryBond;
import org.openscience.cdk.smarts.Smarts;
import org.openscience.cdk.smarts.SmartsResult;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Support for parsing a SMIRKS transform and utilities to parse/apply in one
 * step.
 *
 * <pre>{@code
 * if (Smirks.apply(mol, "[*:1][H]>>[*:1]Cl")) {
 *     System.err.println("Success!");
 * }
 * }</pre>
 * <p>
 * If the SMIRKS is invalid a runtime exception is thrown. If you expect
 * to be processing possibly invalid inputs consider using the more verbose
 * {@link #parse(org.openscience.cdk.isomorphism.Transform, String)}
 * function and apply it separately. Note you can parse in either the
 * low-level {@link org.openscience.cdk.isomorphism.Transform} or the
 * higher-level {@link org.openscience.cdk.smirks.SmirksTransform}.
 *
 * <pre>
 * {@code
 * Transform transform = new SmirksTransform();
 * if (!Smirks.parse(transform, "[*:1][H]>>[*:1]Cl"))
 *   System.err.println("BAD SMIRKS: " + transform.message());
 *
 * IAtomContainer mol = ...;
 * transform.apply(mol);
 * }
 * </pre>
 *
 * @see org.openscience.cdk.isomorphism.Transform
 * @see org.openscience.cdk.smirks.SmirksTransform
 */
public class Smirks {

    private static final ILoggingTool LOGGER = LoggingToolFactory.createLoggingTool(Smirks.class);

    private Smirks() {
    }

    private static final class BondKey {
        int beg;
        int end;

        BondKey(int beg, int end) {
            this.beg = beg;
            this.end = end;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BondKey key = (BondKey) o;
            return beg == key.beg && end == key.end || beg == key.end && end == key.beg;
        }

        @Override
        public int hashCode() {
            return Objects.hash(Math.min(beg, end), Math.max(beg, end));
        }
    }

    /**
     * Convenience function to compile a SMIRKS string into a transform.
     *
     * <pre>{@code
     * Smirks.compile("[*:1][H]>>[*:1]Cl")
     *       .apply(mol);
     * }</pre>
     * <p>
     * If the SMIRKS is invalid a runtime exception is thrown. If you expect
     * to be processing possibly invalid inputs consider using the more verbose
     * {@link #parse(org.openscience.cdk.isomorphism.Transform, String)}
     * function.
     *
     * @param smirks the SMIRKS string
     * @return a SmirksTransform
     */
    public static SmirksTransform compile(String smirks) {
        SmirksTransform transform = new SmirksTransform();
        if (!Smirks.parse(transform, smirks))
            throw new IllegalStateException("Invalid SMIRKS: " + transform.message());
        return transform;
    }

    /**
     * Convenience function to compile and apply a SMIRKS string on a molecule
     * to all non-overlapping (exclusive) matches.
     *
     * <pre>{@code
     * if (Smirks.apply(mol, "[*:1][H]>>[*:1]Cl")) {
     *     System.err.println("Success!");
     * }
     * }</pre>
     * <p>
     * If the SMIRKS is invalid a runtime exception is thrown. If you expect
     * to be processing possibly invalid inputs consider using the more verbose
     * {@link #parse(org.openscience.cdk.isomorphism.Transform, String)}
     * function and apply it separately.
     *
     * @param mol    the molecule to apply the SMIRKS to
     * @param smirks the SMIRKS string
     * @return the pattern was applied or not
     * @see org.openscience.cdk.isomorphism.Transform#apply(org.openscience.cdk.interfaces.IAtomContainer)
     */
    public static boolean apply(IAtomContainer mol, String smirks) {
        return compile(smirks).apply(mol);
    }

    /**
     * Convenience function to compile and apply a SMIRKS string on a copy of
     * the molecule returning the places the transform matched and applied.
     *
     * <pre>{@code
     * for (IAtomContainer res : Smirks.apply(mol, "[*:1][H]>>[*:1]Cl")) {
     *    // ... further process result molecule
     * }
     * }</pre>
     * <p>
     * If the SMIRKS is invalid a runtime exception is thrown. If you expect
     * to be processing possibly invalid inputs consider using the more verbose
     * {@link #parse(org.openscience.cdk.isomorphism.Transform, String)}
     * function and apply it separately.
     *
     * @param mol    the molecule to apply the SMIRKS to
     * @param smirks the SMIRKS string
     * @return the pattern was applied or not
     * @see org.openscience.cdk.isomorphism.Transform#apply(org.openscience.cdk.interfaces.IAtomContainer, org.openscience.cdk.isomorphism.Transform.Mode)
     */
    public static Iterable<IAtomContainer> apply(IAtomContainer mol,
                                                 String smirks,
                                                 Transform.Mode mode) {
        return compile(smirks).apply(mol, mode);
    }

    /**
     * Parse a SMIRKS string into a transform.
     *
     * <pre>
     * {@code
     * Transform transform = new SmirksTransform();
     * if (!Smirks.parse(transform, "[*:1][H]>>[*:1]Cl")) {
     *   System.err.println("BAD SMIRKS: " + transform.message());
     *   return;
     * }
     *
     * IAtomContainer mol = ...;
     * transform.apply(mol);
     * }
     * </pre>
     * <p>
     * If the SMIRKS could not be interpreted or was invalid this method returns
     * <b>false</b> and sets the transform into an error state meaning calling
     * {@code apply()} will do nothing. The {@link Transform#message()} may
     * still be set if there were warnings generated when interpreting the
     * SMIRKS.
     *
     * @param transform the transform to load into
     * @param smirks    the SMIRKS string
     * @param options   options for parsing a SMIRKS
     * @return the SMIRKS interpretable or not (there was an error)
     * @see org.openscience.cdk.isomorphism.Transform
     * @see org.openscience.cdk.smirks.SmirksTransform
     */
    public static boolean parse(Transform transform,
                                String smirks,
                                Set<SmirksOption> options) {

        if (transform == null)
            throw new NullPointerException("No transform provided");
        if (smirks == null)
            throw new NullPointerException("No SMIRKS string provided");

        QueryAtomContainer query = new QueryAtomContainer(null);
        SmartsResult result = Smarts.parseToResult(query, smirks,
                                                   Smarts.FLAVOR_LOOSE);
        if (!result.ok())
            return transform.setError(result.getMessage());

        SmirksState state = new SmirksState(query, result, options);

        // based on the atom mapping pair up the atoms/bonds from the left side
        // of the reaction to the right side
        if (!collectAtomPairs(state))
            return transform.setError(state.getMessage());
        if (!collectBondPairs(state))
            return transform.setError(state.getMessage());

        // now we have the pairs of atoms/bonds we need to work out which were
        // added/removed/changed. For the changes we detect we insert Op-codes
        // that will be run to carry out the transform
        List<TransformOp> ops = new ArrayList<>();
        determineHydrogenMovement(ops, state);
        determineAtomChanges(ops, state);
        determineBondChanges(ops, state);
        determineStereoChanges(ops, state);

        checkValence(state, ops);

        // build the query pattern based on the left-hand side of the reaction
        prepareQuery(state);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(Smarts.generate(query));
            LOGGER.debug(ops);
        }

        transform.init(DfPattern.findSubstructure(query), ops, state.getMessage());

        return true;
    }

    private static Set<SmirksOption> wrap(SmirksOption[] options) {
        Set<SmirksOption> optset = options.length > 1
                ? EnumSet.of(options[0], options)
                : options.length > 0 ? EnumSet.of(options[0])
                : EnumSet.noneOf(SmirksOption.class);
        return optset;
    }

    /**
     * Parse a SMIRKS string into a transform.
     *
     * <pre>
     * {@code
     * Transform transform = new SmirksTransform();
     * if (!Smirks.parse(transform, "[*:1][H]>>[*:1]Cl")) {
     *   System.err.println("BAD SMIRKS: " + transform.message());
     *   return;
     * }
     *
     * IAtomContainer mol = ...;
     * transform.apply(mol);
     * }
     * </pre>
     * <p>
     * If the SMIRKS could not be interpreted or was invalid this method returns
     * <b>false</b> and sets the transform into an error state meaning calling
     * {@code apply()} will do nothing. The {@link Transform#message()} may
     * still be set if there were warnings generated when interpreting the
     * SMIRKS.
     *
     * @param transform the transform to load into
     * @param smirks    the SMIRKS string
     * @param options   options for parsing a SMIRKS
     * @return the SMIRKS interpretable or not (there was an error)
     * @see org.openscience.cdk.isomorphism.Transform
     * @see org.openscience.cdk.smirks.SmirksTransform
     */
    public static boolean parse(Transform transform,
                                String smirks,
                                SmirksOption ... options) {
        Set<SmirksOption> optset = wrap(options);
        return parse(transform,
                     smirks,
                     optset);
    }

    /**
     * Parse a SMIRKS string into a transform.
     *
     * <pre>
     * {@code
     * Transform transform = new SmirksTransform();
     * if (!Smirks.parse(transform, "[*:1][H]>>[*:1]Cl")) {
     *   System.err.println("BAD SMIRKS: " + transform.message());
     *   return;
     * }
     *
     * IAtomContainer mol = ...;
     * transform.apply(mol);
     * }
     * </pre>
     * <p>
     * If the SMIRKS could not be interpreted or was invalid this method returns
     * <b>false</b> and sets the transform into an error state meaning calling
     * {@code apply()} will do nothing. The {@link Transform#message()} may
     * still be set if there were warnings generated when interpreting the
     * SMIRKS.
     *
     * @param transform the transform to load into
     * @param smirks    the SMIRKS string
     * @return the SMIRKS interpretable or not (there was an error)
     * @see org.openscience.cdk.isomorphism.Transform
     * @see org.openscience.cdk.smirks.SmirksTransform
     */
    public static boolean parse(Transform transform,
                                String smirks) {
        return parse(transform,
                     smirks,
                     EnumSet.noneOf(SmirksOption.class));
    }

    private static final class SmirksState {
        Set<SmirksOption> opts = EnumSet.noneOf(SmirksOption.class);
        Set<String> errors = new LinkedHashSet<>();
        Set<String> warnings = new LinkedHashSet<>();

        QueryAtomContainer query;
        SmartsResult input;

        int numAtoms = 1; // start numbering from one to we line up with atom maps.
        int numPairs = 0;
        Map<IAtom, Integer> atomidx = new HashMap<>();
        Map<Integer, Integer> remap = new HashMap<>();

        List<IAtom[]> atomPairs = new ArrayList<>();
        List<IBond[]> bondPairs = new ArrayList<>();

        int[] hcount;
        int[] hmin;

        SmirksState(QueryAtomContainer query,
                    SmartsResult input,
                    Set<SmirksOption> options) {
            this.query = query;
            this.input = input;
            this.hcount = new int[query.getAtomCount() + 1];
            this.hmin = new int[query.getAtomCount() + 1];
            this.opts = options;
        }

        private int calcImplH(IAtom atom) {
            // if the atom is not in square brackets [C:1][O] vs [C:1]O
            // "iscomplex" is not defined
            if (atom.getProperty("cdk.smarts.iscomplex") == null) {
                // guaranteed to be defined since it is not an expression
                int elem = getAtomicNumber(atom).val;
                Integer valence = getExplValence(query, atom);
                if (valence == null) {
                    warning("Created (right hand side) unbracketed " + Elements.ofNumber(elem)
                                                                                    .symbol() + " atom was connected with bond expressions",
                            atom);
                    return 0;
                }
                if (isAromatic(atom).val == 1)
                    valence++;
                switch (elem) {
                    case IElement.Wildcard:
                        return 0;
                    case IElement.B:
                        if (valence <= 3) return 3 - valence;
                        break;
                    case IElement.C:
                        if (valence <= 4) return 4 - valence;
                        break;
                    case IElement.N:
                    case IElement.P:
                        if (valence <= 3) return 3 - valence;
                        else if (valence <= 5) return 5 - valence;
                        break;
                    case IElement.O:
                        if (valence <= 2) return 2 - valence;
                        break;
                    case IElement.S:
                        if (valence <= 2) return 2 - valence;
                        else if (valence <= 4) return 4 - valence;
                        else if (valence <= 6) return 6 - valence;
                        break;
                    case IElement.F:
                    case IElement.Cl:
                    case IElement.Br:
                    case IElement.I:
                        if (valence <= 1) return 1 - valence;
                        else if (valence <= 3) return 3 - valence;
                        else if (valence <= 5) return 5 - valence;
                        else if (valence <= 7) return 7 - valence;
                        break;
                    default:
                        throw new IllegalStateException("No default valence for element=" + elem);
                }
            }
            return 0;
        }
        boolean error(String s) {
            errors.add(s);
            return false;
        }


        boolean error(String s, IBond bond) {
            errors.add(s + "\n" + input.displayErrorLocation(input.getBondLocation(query.indexOf(bond))));
            return false;
        }

        void warning(String s) {
            warnings.add(s);
        }

        void warning(String s, IBond bond) {
            warnings.add(s + "\n" + input.displayErrorLocation(input.getBondLocation(query.indexOf(bond))));
        }

        void warning(String s, IAtom atom) {
            warnings.add(s + "\n" + input.displayErrorLocation(input.getAtomLocation(query.indexOf(atom))));
        }

        String getMessage() {
            if (errors.isEmpty() && warnings.isEmpty())
                return null;
            StringBuilder sb = new StringBuilder();
            for (String e : errors) {
                if (sb.length() > 0 && sb.charAt(sb.length()-1) != '\n')
                    sb.append('\n');
                sb.append(e);
            }
            if (sb.length() == 0) {
                for (String w : warnings) {
                    if (sb.length() > 0 && sb.charAt(sb.length()-1) != '\n')
                        sb.append('\n');
                    sb.append(w);
                }
            }
            return sb.toString();
        }
    }

    private static boolean collectAtomPairs(SmirksState state) {
        for (IAtom atom : state.query.atoms()) {
            ReactionRole role = atom.getProperty(CDKConstants.REACTION_ROLE);
            if (role == null)
                return state.error("SMIRKS was not a reaction!");
            boolean maybeImplH = isSuppressibleH(state.query, atom);

            int mapidx = getMapIdx(atom);
            Integer pairidx = state.remap.get(mapidx);
            if (pairidx == null) {
                pairidx = state.numPairs++;
                if (mapidx != 0)
                    state.remap.put(mapidx, pairidx);
            }
            while (state.atomPairs.size() <= pairidx)
                state.atomPairs.add(new IAtom[2]);

            IAtom[] atoms = state.atomPairs.get(pairidx);
            switch (role) {
                case Reactant:
                case Agent:
                    if (atoms[0] != null)
                        return duplicateAtomMap(state, atoms[0], atom);
                    atoms[0] = atom;
                    if (maybeImplH)
                        break;
                    state.atomidx.put(atom, state.numAtoms++);
                    break;
                case Product:
                    if (atoms[1] != null)
                        return duplicateAtomMap(state, atoms[1], atom);
                    atoms[1] = atom;
                    if ((maybeImplH || isSuppressibleH(state.query, atoms[0])) &&
                            (atoms[0] == null || !state.atomidx.containsKey(atoms[0])))
                        break;
                    int aidx = atoms[0] != null ? state.atomidx.get(atoms[0]) : state.numAtoms++;
                    state.atomidx.put(atom, aidx);
                    state.hcount[aidx] = state.calcImplH(atom);
                    break;
                default:
                    throw new IllegalStateException("Atom without a role!");
            }
        }
        return true;
    }

    private static boolean collectBondPairs(SmirksState state) {
        Map<BondKey, IBond[]> bondMap = new HashMap<>();
        for (IBond bond : state.query.bonds()) {
            IAtom beg = bond.getBegin();
            IAtom end = bond.getEnd();

            int begIdx = getMapIdx(beg);
            int endIdx = getMapIdx(end);

            // suppressed hydrogen
            if (!state.atomidx.containsKey(beg)) {
                if (!state.atomidx.containsKey(end))
                    continue; // MOVE_TO_EXPL_H
                state.hcount[state.atomidx.get(end)] += isProduct(end) ? +1 : -1;
                if (!isProduct(end))
                    state.hmin[state.atomidx.get(end)]++;
                continue;
            }
            if (!state.atomidx.containsKey(end)) {
                state.hcount[state.atomidx.get(beg)] += isProduct(beg) ? +1 : -1;
                if (!isProduct(beg))
                    state.hmin[state.atomidx.get(beg)]++;
                continue;
            }

            IBond[] bondPair = null;
            if (begIdx != 0 && endIdx != 0)
                bondPair = bondMap.get(new BondKey(getMapIdx(beg), getMapIdx(end)));
            if (bondPair == null) {
                state.bondPairs.add(bondPair = new IBond[2]);
                if (begIdx != 0 && endIdx != 0)
                    bondMap.put(new BondKey(getMapIdx(beg), getMapIdx(end)), bondPair);
            }
            ReactionRole role = beg.getProperty(CDKConstants.REACTION_ROLE);
            switch (role) {
                case Reactant:
                case Agent:
                    bondPair[0] = bond;
                    break;
                case Product:
                    bondPair[1] = bond;
                    break;
            }
        }
        return true;
    }

    private static void determineHydrogenMovement(List<TransformOp> ops,
                                                     SmirksState state) {
        Iterator<IAtom[]> iter = state.atomPairs.iterator();
        while (iter.hasNext()) {
            IAtom[] pair = iter.next();
            if (isSuppressibleH(state.query, pair[0])) {
                if (isHydrogen(state.query, pair[1])) {
                    IAtom begNbor = state.query.getConnectedBondsList(pair[0]).get(0).getOther(pair[0]);
                    IAtom endNbor = state.query.getConnectedBondsList(pair[1]).get(0).getOther(pair[1]);
                    int begNborIdx = state.atomidx.get(begNbor);
                    Integer endNborIdx = state.atomidx.get(endNbor);
                    if (endNborIdx == null) {
                        state.hcount[begNborIdx]++;
                        int atomIdx = state.numAtoms++;
                        state.atomidx.put(pair[1], atomIdx);
                        ops.add(new TransformOp(TransformOp.Type.PromoteH, begNborIdx, atomIdx));
                    }
                    else if (begNborIdx != endNborIdx) {
                        state.hcount[begNborIdx]++;
                        state.hcount[endNborIdx]--;
                        ops.add(new TransformOp(TransformOp.Type.MoveH, begNborIdx, endNborIdx));
                    }
                    iter.remove();
                } else if (pair[1] == null) {
                    iter.remove();
                }
            } else if (isSuppressibleH(state.query, pair[1])) {
                iter.remove();
            }
        }
    }

    private static String generateAtom(IAtom atom) {
        return "[" + Smarts.generateAtom(((QueryAtom) atom).getExpression())
                           .replaceAll("(?:^\\[)|(?:]$)", "") + ":" + getMapIdx(atom) + "]";
    }

    private static void checkAtomMap(SmirksState state, IAtom atom) {
        if (!state.opts.contains(SmirksOption.PEDANTIC))
            return;
        if (getMapIdx(atom) != 0)
            state.warning("Added/removed atoms do not need to be mapped", atom);
    }

    private static boolean duplicateAtomMap(SmirksState state, IAtom atom1, IAtom atom2) {
        return state.error("Duplicate atom map " + generateAtom(atom1) + " and " + generateAtom(atom2));
    }

    private static void determineAtomChanges(List<TransformOp> ops,
                                             SmirksState state) {
        for (IAtom[] pair : state.atomPairs) {
            Integer aidx = pair[0] != null ? state.atomidx.get(pair[0]) : state.atomidx.get(pair[1]);
            if (aidx == null)
                continue;
            if (pair[0] != null && pair[1] == null) {
                checkAtomMap(state, pair[0]);
                ops.add(new TransformOp(TransformOp.Type.DeleteAtom, aidx));
            } else if (pair[0] == null && pair[1] != null) {
                checkAtomMap(state, pair[1]);
                ops.addAll(atomTypeOps(state.atomidx.get(pair[1]), null, pair[1], state.hcount[aidx]));
            } else {
                ops.addAll(atomTypeOps(aidx, pair[0], pair[1], state.hcount[aidx]));
            }
        }
    }

    private static boolean determineBondChanges(List<TransformOp> ops,
                                                SmirksState state) {
        for (IBond[] pair : state.bondPairs) {
            int begIdx = pair[0] == null ? state.atomidx.get(pair[1].getBegin()) : state.atomidx.get(pair[0].getBegin());
            int endIdx = pair[0] == null ? state.atomidx.get(pair[1].getEnd()) : state.atomidx.get(pair[0].getEnd());

//            if (pair[0] != null && pair[1] != null) {
//                System.err.println(begIdx + "-" + endIdx + " changed?");
//            } else if (pair[0] != null && pair[1] == null) {
//                System.err.println(begIdx + "-" + endIdx + " deleted");
//            } else if (pair[1] != null && pair[0] == null) {
//                System.err.println(begIdx + "-" + endIdx + " new bond");
//            }

            // warn if someone puts something like >>C:C, better written as
            // >>c:c or even >>cc
            if (pair[1] != null) {
                BinaryExprValue bndArom = isAromatic(pair[1]);
                if (bndArom.ok() && bndArom.val == 1) {
                    BinaryExprValue begArom = isAromatic(pair[1].getBegin());
                    BinaryExprValue endArom = isAromatic(pair[1].getEnd());
                    if (begArom.equals(BinaryExprValue.FALSE) ||
                        endArom.equals(BinaryExprValue.FALSE))
                        state.warning("Aromatic bond ':' connected to an aliphatic atom", pair[1]);
                }
            }

            BinaryExprValue lft = getBondOrder(pair[0]);
            BinaryExprValue rgt = getBondOrder(pair[1]);
            if (pair[0] != null && pair[1] == null) {
                ops.add(new TransformOp(TransformOp.Type.DeleteBond, begIdx, endIdx, getBondOrder(pair[0]).val));
            } else {
                if (pair[0] == null && pair[1] != null) {
                    if (!rgt.ok()) {
                        BinaryExprValue begArom = isAromatic(pair[1].getBegin());
                        BinaryExprValue endArom = isAromatic(pair[1].getEnd());
                        if (begArom.equals(BinaryExprValue.FALSE) || endArom.equals(BinaryExprValue.FALSE)) {
                            state.warning("Cannot determine bond order for newly created bond (presumed aliphatic single)", pair[1]);
                            ops.add(new TransformOp(TransformOp.Type.NewBond, begIdx, endIdx, 1, 0));
                        } else if (begArom.equals(BinaryExprValue.TRUE) && endArom.equals(BinaryExprValue.TRUE)) {
                            state.warning("Cannot determine bond order for newly created bond (presumed aromatic single)", pair[1]);
                            ops.add(new TransformOp(TransformOp.Type.NewBond, begIdx, endIdx, 1, 1));
                        } else {
                            return state.error("Cannot determine bond order for newly created bond");
                        }
                    } else {
                        ops.add(new TransformOp(TransformOp.Type.NewBond, begIdx, endIdx, rgt.val, 0));
                    }
//                    if (rgt.val == 5)
//                       ops.add(new TransformOp(TransformOp.Type.AromaticBond, begIdx, endIdx, 1, 0));
                } else {
                    if (!rgt.ok()) {
                        if (isAnyBond(pair[1]))
                            continue;
                        state.warning("Ignored query bond (implicit: -,:), use '~' to suppress this warning", pair[1]);
                    } else {
                        if (changed(lft, rgt)) {
                            // record what the bond order was if possible (valence checking)
                            if (lft.ok())
                                ops.add(new TransformOp(TransformOp.Type.BondOrder,
                                                        begIdx, endIdx, rgt.val, lft.val));
                            else
                                ops.add(new TransformOp(TransformOp.Type.BondOrder,
                                                        begIdx, endIdx, rgt.val));
                        }
                        lft = isAromatic(pair[0]);
                        rgt = isAromatic(pair[1]);
                        if (changed(lft, rgt))
                            ops.add(new TransformOp(TransformOp.Type.AromaticBond, begIdx, endIdx, rgt.val, 0));
                    }
                }
            }
        }
        return true;
    }

    private static void determineStereoChanges(List<TransformOp> ops, SmirksState state) {
        List<IStereoElement> stereoElements = new ArrayList<>();
        for (IStereoElement se : state.query.stereoElements()) {
            switch (se.getConfigClass()) {
                case IStereoElement.Tetrahedral:
                    if (isProduct((IAtom) se.getFocus())) {
                        IStereoElement<IAtom, IAtom> th = (IStereoElement<IAtom, IAtom>) se;
                        BinaryExprValue val = getProperty(th.getFocus(), Expr.Type.STEREOCHEMISTRY);
                        if (val.val == 1) {
                            ops.add(new TransformOp(TransformOp.Type.Tetrahedral,
                                                    state.atomidx.get(th.getFocus()),
                                                    state.atomidx.get(th.getCarriers().get(0)),
                                                    state.atomidx.get(th.getCarriers().get(1)),
                                                    state.atomidx.get(th.getCarriers().get(2))));
                        } else if (val.val == 2) {
                            ops.add(new TransformOp(TransformOp.Type.Tetrahedral,
                                                    state.atomidx.get(th.getFocus()),
                                                    state.atomidx.get(th.getCarriers().get(0)),
                                                    state.atomidx.get(th.getCarriers().get(2)),
                                                    state.atomidx.get(th.getCarriers().get(1))));
                        }
                    } else {
                        stereoElements.add(se);
                    }
                    break;
                case IStereoElement.CisTrans:
                    if (isProduct(((IBond) se.getFocus()).getBegin()) &&
                            isProduct(((IBond) se.getFocus()).getEnd())) {
                        IStereoElement<IBond, IBond> db = (IStereoElement<IBond, IBond>) se;
                        BinaryExprValue val = getProperty(db.getFocus(), Expr.Type.STEREOCHEMISTRY);
                        IAtom a = db.getFocus().getBegin();
                        IAtom b = db.getFocus().getEnd();
                        IAtom c = db.getCarriers().get(0).getOther(a);
                        IAtom d = db.getCarriers().get(1).getOther(b);
                        if (val.val == 1) {
                            ops.add(new TransformOp(TransformOp.Type.DbOpposite,
                                                    state.atomidx.get(a),
                                                    state.atomidx.get(b),
                                                    state.atomidx.get(c),
                                                    state.atomidx.get(d)));
                        } else {
                            ops.add(new TransformOp(TransformOp.Type.DbTogether,
                                                    state.atomidx.get(a),
                                                    state.atomidx.get(b),
                                                    state.atomidx.get(c),
                                                    state.atomidx.get(d)));
                        }
                    } else {
                        stereoElements.add(se);
                    }
                    break;
                case IStereoElement.SquarePlanar:
                case IStereoElement.TrigonalBipyramidal:
                case IStereoElement.Octahedral:
                case IStereoElement.Allenal:
                    if (isProduct((IAtom) se.getFocus()))
                        state.warning("Ignored setting atom stereo on right hand side - unsupported stereo class: " + se.getConfigClass());
                    else
                        stereoElements.add(se);
                    break;
                case IStereoElement.Atropisomeric:
                case IStereoElement.Cumulene:
                    if (isProduct(((IBond) se.getFocus()).getBegin()) &&
                            isProduct(((IBond) se.getFocus()).getEnd()))
                        state.warning("Ignored setting bond stereo on right hand side - unsupported stereo class: " + se.getConfigClass());
                    else
                        stereoElements.add(se);
                    break;
                default:
                    state.warning("Ignored stereo on right hand side - unsupported stereo class: " + se.getConfigClass());
            }
        }

        // only reactant/query stereo will be kept
        state.query.setStereoElements(stereoElements);
    }

    private static void prepareQuery(SmirksState state) {
        Set<IAtom> toremove = new HashSet<>();
        for (IAtom atom : state.query.atoms()) {
            if (isProduct(atom) || isSuppressibleH(state.query, atom)) {
                toremove.add(atom);
            } else {
                // clear the role and renumber the maps
                atom.setProperty(CDKConstants.REACTION_ROLE, null);
                atom.setProperty(CDKConstants.ATOM_ATOM_MAPPING, state.atomidx.get(atom));
                stripRxnRole(atom);
                constrainMinHydrogenCount(atom, state.hmin[state.atomidx.get(atom)]);
            }
        }
        for (IAtom atom : toremove)
            state.query.removeAtom(atom);
    }

    private static void update(Map<Integer,Integer> map, int key, int adj) {
        Integer v = map.get(key);
        if (v == null)
            v = 0;
        map.put(key, v + adj);
    }

    private static void checkValence(SmirksState state, List<TransformOp> ops) {
//        if (!state.opts.contains(SmirksOption.PEDANTIC))
//            return;
        Map<Integer,Integer> valence  = new HashMap<>();
        Set<Integer>         unknown  = new HashSet<>();
        Map<Integer,Integer> hcnts    = new HashMap<>();
        for (TransformOp op : ops) {
            switch (op.type()) {
                case BondOrder:
                    // When possible left-hand-side (before) value is (D)
                    if (op.argD() != 0) {
                        update(valence, op.argA(), op.argC() - op.argD());
                        update(valence, op.argB(), op.argC() - op.argD());
                    } else {
                        unknown.add(op.argA());
                        unknown.add(op.argB());
                    }
                    break;
                case DeleteBond:
                    update(valence, op.argA(), -op.argC());
                    update(valence, op.argB(), -op.argC());
                    break;
                case NewBond:
                    update(valence, op.argA(), op.argC());
                    update(valence, op.argB(), op.argC());
                    break;
                case PromoteH:
                    update(valence, op.argA(), -1);
                    break;
                case MoveH:
                    update(valence, op.argA(), -1);
                    update(valence, op.argB(), +1);
                    break;
                case AdjustH:
                    update(valence, op.argA(), op.argB());
                    break;
                case Charge:
                    unknown.add(op.argA());
                    break;
                case TotalH:
                case ImplH:
                    hcnts.put(op.argA(), op.argB());
                    break;
            }
        }
        for (IAtom[] pair : state.atomPairs) {
            if (pair[0] != null && pair[1] != null) {
                Integer id   = state.atomidx.get(pair[0]);
                if (unknown.contains(id))
                    continue;
                Integer diff = valence.get(id);
                if (diff != null && diff != 0) {
                    Integer hcnt = hcnts.get(id);
                    if (hcnt != null) {
                        state.warning("Possible valence change," +
                                      " add H" + (hcnt + diff) + " to reactant to supress",
                                      pair[0]);
                    } else {
                        state.warning("Possible valence change",
                                      pair[0]);
                    }
                }
            }
        }
    }

    private static Integer getExplValence(QueryAtomContainer query, IAtom atom) {
        int count = 0;
        for (IBond bond : query.getConnectedBondsList(atom)) {
            BinaryExprValue result = getBondOrder(bond, BinaryExprValue.FALSE);
            if (!result.ok())
                return null;
            count += result.val;
        }
        return count;
    }

    private static void constrainMinHydrogenCount(IAtom atom, int hcount) {
        if (hcount <= 0)
            return;
        QueryAtom qatom = (QueryAtom) atom;
        Expr expr = qatom.getExpression();
        for (int i = 0; i < hcount; i++)
            expr.and(new Expr(Expr.Type.TOTAL_H_COUNT, i).negate());
        qatom.setExpression(expr);
    }

    private static void stripRxnRole(IAtom atom) {
        QueryAtom qatom = (QueryAtom) atom;
        qatom.setExpression(stripRxnRole(qatom.getExpression()));
    }

    private static Expr stripRxnRole(Expr e) {
        switch (e.type()) {
            case REACTION_ROLE:
                return new Expr(Expr.Type.TRUE);
            case OR:
                return stripRxnRole(e.left()).or(stripRxnRole(e.right()));
            case AND:
                return stripRxnRole(e.left()).and(stripRxnRole(e.right()));
            default:
                // n.b. REACTION_ROLE should not be negated so don't handle that
                return e;
        }
    }

    private static boolean isProduct(IAtom end) {
        return end.getProperty(CDKConstants.REACTION_ROLE) == ReactionRole.Product;
    }

    private static boolean isSuppressibleH(Expr e) {
        return (e.type() == Expr.Type.ELEMENT ||
                e.type() == Expr.Type.ALIPHATIC_ELEMENT) &&
                e.value() == 1;
    }

    // is a suppressible explicit H, 2H etc. is not suppressible
    private static boolean isExplHWithOptRole(Expr e) {
        if (e.type() == Expr.Type.AND) {
            Expr l = e.left();
            Expr r = e.right();
            return (isSuppressibleH(l) && r.type() == Expr.Type.REACTION_ROLE) ||
                    (isSuppressibleH(r) && l.type() == Expr.Type.REACTION_ROLE);
        }
        return isSuppressibleH(e);
    }

    private static boolean isSuppressibleH(IAtomContainer mol, IAtom a) {
        if (a == null)
            return false;
        if (!isExplHWithOptRole(((QueryAtom) a).getExpression()))
            return false;
        List<IBond> bonds = mol.getConnectedBondsList(a);
        return bonds.size() == 1 && getAtomicNumber(bonds.get(0).getOther(a)).val != 1;
    }

    private static boolean isHydrogen(IAtomContainer mol, IAtom a) {
        if (a == null)
            return false;
        return getAtomicNumber(a).val == 1;
    }

    private static boolean isWildCard(IAtomContainer mol, IAtom a) {
        if (a == null)
            return false;
        if (!isExplHWithOptRole(((QueryAtom) a).getExpression()))
            return false;
        List<IBond> bonds = mol.getConnectedBondsList(a);
        return bonds.size() == 1; // + single acyclic?
    }

    private static boolean changed(BinaryExprValue lft, BinaryExprValue rgt) {
        return rgt.ok() && !lft.equals(rgt);
    }

    private static Integer getMapIdx(IAtom atom) {
        Integer x = atom.getProperty(CDKConstants.ATOM_ATOM_MAPPING);
        return x != null ? x : 0;
    }

    private static BinaryExprValue getAtomicNumber(IAtom atom) {
        return atom == null ? BinaryExprValue.UNDEF : getAtomicNumber(((QueryAtom) atom).getExpression());
    }

    private static BinaryExprValue getAtomicNumber(Expr expr) {
        switch (expr.type()) {
            case ELEMENT:
            case ALIPHATIC_ELEMENT:
            case AROMATIC_ELEMENT:
                return new BinaryExprValue(expr.value());
            case AND:
                return getAtomicNumber(expr.left()).and(getAtomicNumber(expr.right()));
            case OR:
                return getAtomicNumber(expr.left()).or(getAtomicNumber(expr.right()));
            case NOT:
                return getAtomicNumber(expr.left()).not();
            default:
                return BinaryExprValue.UNDEF;
        }
    }

    private static BinaryExprValue isAromatic(Expr expr) {
        return isAromatic(expr, BinaryExprValue.UNDEF);
    }

    private static BinaryExprValue isAromatic(Expr expr, BinaryExprValue context) {
        switch (expr.type()) {
            case ELEMENT:
                switch (expr.value()) {
                    case IElement.B:
                    case IElement.C:
                    case IElement.N:
                    case IElement.O:
                    case IElement.Al:
                    case IElement.Si:
                    case IElement.P:
                    case IElement.S:
                    case IElement.Ge:
                    case IElement.As:
                    case IElement.Se:
                    case IElement.Sb:
                    case IElement.Te:
                        return BinaryExprValue.UNDEF; // these 'might' be aromatic
                    default:
                        return BinaryExprValue.FALSE;
                }
            case AROMATIC_ELEMENT:
            case IS_AROMATIC:
                return BinaryExprValue.TRUE;
            case SINGLE_OR_AROMATIC:
            case DOUBLE_OR_AROMATIC:
                // depends on the end atoms
                if (context == BinaryExprValue.TRUE)
                    return BinaryExprValue.TRUE;
                else if (context == BinaryExprValue.FALSE)
                    return BinaryExprValue.FALSE;
                else
                    return BinaryExprValue.UNDEF;
            case ALIPHATIC_ELEMENT:
            case IS_ALIPHATIC:
            case IS_IN_CHAIN:
            case IS_ALIPHATIC_HETERO:
            case ALIPHATIC_ORDER:
            case SINGLE_OR_DOUBLE:
                return BinaryExprValue.FALSE;
            case TOTAL_H_COUNT:
            case IMPL_H_COUNT:
                // sane aromaticity: hcnt > 1 => Aliphatic
                if (expr.value() > 1)
                    return BinaryExprValue.FALSE;
                return BinaryExprValue.UNDEF;
            case AND:
                return isAromatic(expr.left()).and(isAromatic(expr.right()));
            case OR:
                return isAromatic(expr.left()).or(isAromatic(expr.right()));
            case NOT:
                return isAromatic(expr.left()).not();
            default:
                return BinaryExprValue.UNDEF;
        }
    }

    private static BinaryExprValue isAromatic(IAtom atom) {
        if (atom == null)
            return BinaryExprValue.UNDEF;
        return isAromatic(((QueryAtom) atom).getExpression());
    }

    private static BinaryExprValue isAromatic(IBond bond) {
        BinaryExprValue begIsArom = isAromatic(bond.getBegin());
        BinaryExprValue endIsArom = isAromatic(bond.getEnd());
        BinaryExprValue aromContext;
        if (begIsArom.equals(BinaryExprValue.TRUE) &&
                endIsArom.equals(BinaryExprValue.TRUE))
            aromContext = BinaryExprValue.TRUE;
        else if (begIsArom.equals(BinaryExprValue.FALSE) ||
                endIsArom.equals(BinaryExprValue.FALSE))
            aromContext = BinaryExprValue.FALSE;
        else
            aromContext = BinaryExprValue.UNDEF;
        return isAromatic(((QueryBond) bond).getExpression(), aromContext);
    }

    private static BinaryExprValue getProperty(Expr expr, Expr.Type type) {
        switch (expr.type()) {
            case AND:
                return getProperty(expr.left(), type).and(getProperty(expr.right(), type));
            case OR:
                return getProperty(expr.left(), type).or(getProperty(expr.right(), type));
            case NOT:
                return BinaryExprValue.CONFLICTING;
            default:
                if (type == Expr.Type.ISOTOPE && expr.type() == Expr.Type.HAS_UNSPEC_ISOTOPE)
                    return new BinaryExprValue(0);
                if (expr.type() == type)
                    return new BinaryExprValue(expr.value());
                return BinaryExprValue.UNDEF;
        }
    }

    private static BinaryExprValue getProperty(IAtom atom, Expr.Type type) {
        return atom == null ? BinaryExprValue.UNDEF
                : getProperty(((QueryAtom) atom).getExpression(), type);
    }

    private static BinaryExprValue getProperty(IBond bond, Expr.Type type) {
        return bond == null ? BinaryExprValue.UNDEF
                : getProperty(((QueryBond) bond).getExpression(), type);
    }

    private static boolean isAnyBond(IBond bond) {
        return bond != null && isAnyBond(((QueryBond) bond).getExpression());
    }

    private static boolean isAnyBond(Expr expr) {
        return expr.type() == Expr.Type.TRUE;
    }

    private static BinaryExprValue getBondOrder(IBond bond) {
        if (bond == null)
            return BinaryExprValue.UNDEF;
        BinaryExprValue begIsArom = isAromatic(bond.getBegin());
        BinaryExprValue endIsArom = isAromatic(bond.getEnd());
        BinaryExprValue aromContext;
        if (begIsArom.equals(BinaryExprValue.TRUE) &&
                endIsArom.equals(BinaryExprValue.TRUE))
            aromContext = BinaryExprValue.TRUE;
        else if (begIsArom.equals(BinaryExprValue.FALSE) ||
                endIsArom.equals(BinaryExprValue.FALSE))
            aromContext = BinaryExprValue.FALSE;
        else
            aromContext = BinaryExprValue.UNDEF;
        return getBondOrder(bond, aromContext);
    }

    private static BinaryExprValue getBondOrder(IBond bond,
                                                BinaryExprValue aromContext) {
        if (bond == null)
            return BinaryExprValue.UNDEF;
        return getBondOrder(((QueryBond) bond).getExpression(), aromContext);
    }

    private static BinaryExprValue getBondOrder(Expr expr,
                                                BinaryExprValue aromContext) {
        switch (expr.type()) {
            case IS_AROMATIC:
                return new BinaryExprValue(5);
            case SINGLE_OR_AROMATIC:
                if (aromContext == BinaryExprValue.TRUE)
                    return new BinaryExprValue(5);
                else if (aromContext == BinaryExprValue.FALSE)
                    return new BinaryExprValue(1);
                // warning?
                return BinaryExprValue.UNDEF;
            case DOUBLE_OR_AROMATIC:
                if (aromContext == BinaryExprValue.TRUE)
                    return new BinaryExprValue(5);
                else if (aromContext == BinaryExprValue.FALSE)
                    return new BinaryExprValue(2);
                // warning?
                return BinaryExprValue.UNDEF;
            case SINGLE_OR_DOUBLE:
                return BinaryExprValue.CONFLICTING;
            case ALIPHATIC_ORDER:
            case ORDER: // from '/' or '\' ?
                return new BinaryExprValue(expr.value());
            case AND:
                return getBondOrder(expr.left(), aromContext).and(getBondOrder(expr.right(), aromContext));
            case OR:
                return getBondOrder(expr.left(), aromContext).or(getBondOrder(expr.right(), aromContext));
            case NOT:
                return getBondOrder(expr.left(), aromContext).not();
            default:
                return BinaryExprValue.UNDEF;
        }
    }

    static List<TransformOp> atomTypeOps(IAtom before, IAtom after) {
        return atomTypeOps(0, before, after, 0);
    }


    private static List<TransformOp> atomTypeOps(int aidx, IAtom before, IAtom after, int hAdjust) {
        List<TransformOp> ops = new ArrayList<>(4);
        BinaryExprValue lft = getAtomicNumber(before);
        BinaryExprValue rgt = getAtomicNumber(after);
        if (before == null) {
            ops.add(new TransformOp(TransformOp.Type.NewAtom, aidx, rgt.val, hAdjust, isAromatic(after).val));
        } else {
            if (changed(lft, rgt))
                ops.add(new TransformOp(TransformOp.Type.Element, aidx, rgt.val));
            // make aromatic if non-wildcard
            if (rgt.ok()) {
                lft = isAromatic(before);
                rgt = isAromatic(after);
                if (changed(lft, rgt))
                    ops.add(new TransformOp(TransformOp.Type.Aromatic, aidx, rgt.val));
            }
        }
        lft = getProperty(before, Expr.Type.FORMAL_CHARGE);
        rgt = getProperty(after, Expr.Type.FORMAL_CHARGE);
        if (changed(lft, rgt))
            ops.add(new TransformOp(TransformOp.Type.Charge, aidx, rgt.val));
        lft = getProperty(before, Expr.Type.TOTAL_H_COUNT);
        rgt = getProperty(after, Expr.Type.TOTAL_H_COUNT);
        if (changed(lft, rgt)) {
            // adjust it if possible so we can have simpler valence error checking
            if (lft.ok() && rgt.ok())
                ops.add(new TransformOp(TransformOp.Type.AdjustH, aidx, rgt.val - lft.val));
            else
                ops.add(new TransformOp(TransformOp.Type.TotalH, aidx, rgt.val));
        }
        lft = getProperty(before, Expr.Type.IMPL_H_COUNT);
        rgt = getProperty(after, Expr.Type.IMPL_H_COUNT);
        if (changed(lft, rgt))
            ops.add(new TransformOp(TransformOp.Type.ImplH, aidx, rgt.val));
        else if (before != null && hAdjust != 0) {
            ops.add(new TransformOp(TransformOp.Type.AdjustH, aidx, hAdjust));
        }
        lft = getProperty(before, Expr.Type.ISOTOPE);
        rgt = getProperty(after, Expr.Type.ISOTOPE);
        if (changed(lft, rgt))
            ops.add(new TransformOp(TransformOp.Type.Mass, aidx, rgt.val));

        return ops;
    }
}
