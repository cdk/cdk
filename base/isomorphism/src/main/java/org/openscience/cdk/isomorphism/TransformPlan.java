/*
 * Copyright (C) 2022 NextMove Software
 *               2022 John Mayfield
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

package org.openscience.cdk.isomorphism;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.ExtendedCisTrans;
import org.openscience.cdk.stereo.ExtendedTetrahedral;
import org.openscience.cdk.stereo.TetrahedralChirality;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Internal - Optimize and run a series of op-codes over a set of matched atoms.
 */
final class TransformPlan {

    private static final ILoggingTool LOGGER
            = LoggingToolFactory.createLoggingTool(TransformPlan.class);
    private static final String SMIRKS_NEWSTEREO = "smirks.newstereo";

    private final List<TransformOp> ops;
    private final int maxNewAtomIdx;
    private final int fstNewAtomIdx;
    private final int numNewAtoms;

    TransformPlan(List<TransformOp> ops) {
        // determine strategy/plan params
        this.numNewAtoms = numNewAtoms(ops);
        this.fstNewAtomIdx = firstNewAtom(ops);
        this.maxNewAtomIdx = maxAtomIdx(ops);
        this.ops = new ArrayList<>(ops);

        // ensure op-codes are run in a specific order, bonds must be deleted
        // before atoms, atoms must be created before bonds. We also prioritize
        // the op-codes that can be trivially undo (and might fail) so we can
        // roll them back if needed
        Collections.sort(this.ops);
        optimize(this.ops);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Optimizes op-codes:" + this.ops);
        }
    }

    /**
     * The required atom capacity is the number atoms in the query + any new
     * atoms + 1 (since we index from 1 rather than 0). In practice we use
     * number of atoms in the target molecule since this is more readily to
     * hand and the number of atoms in the query must be less-than-or-equal to
     * this.
     */
    int requiredAtomCapacity(IAtomContainer mol) {
        return mol.getAtomCount() + numNewAtoms + 1;
    }

    /**
     * Apply the plan a molecule with the atoms index in the {@code amap} array.
     *
     * @param mol  the molecule
     * @param amap atom-mapping
     * @return if the operations were applied successfully or not
     */
    boolean apply(IAtomContainer mol, IAtom[] amap) {
        List<IStereoElement<?,?>> stereo = new ArrayList<>();
        for (IStereoElement<?,?> se : mol.stereoElements())
            stereo.add(se);
        Map<IAtom,IBond[]> bonding = new HashMap<>();
        prepare(mol, amap, bonding);
        for (int i = 0; i < ops.size(); i++) {
            TransformOp op = ops.get(i);
            if (!apply(mol, amap, op)) {
                undo(mol, amap, i);
                return false;
            }
        }
        resyncStereo(mol, stereo, bonding);
        return true;
    }

    /**
     * Clear the ops.
     */
    void clear() {
        ops.clear();
    }

    @Override
    public String toString() {
        return ops.toString();
    }

    /* Internal Methods */

    private static void resetFlags(IAtom atom) {
        atom.setFlag(CDKConstants.MAPPED, false);
        atom.setFlag(CDKConstants.REACTIVE_CENTER, false);
    }

    private void prepare(IAtomContainer mol, IAtom[] amap, Map<IAtom,IBond[]> bonding) {
        for (IAtom atom : mol.atoms())
            resetFlags(atom);
        for (int i = 1; i < amap.length; i++) {
            if (amap[i] != null)
                amap[i].setFlag(CDKConstants.MAPPED, true);
        }
        if (mol.stereoElements().iterator().hasNext()) {
            for (IStereoElement<?, ?> se : mol.stereoElements()) {
                if (se.getConfigClass() == IStereoElement.Tetrahedral) {
                    IAtom focus = (IAtom) se.getFocus();
                    bonding.put(focus, getBondArray(focus));
                } else if (se.getConfigClass() == IStereoElement.CisTrans) {
                    IBond focus = (IBond) se.getFocus();
                    bonding.put(focus.getBegin(), getBondArray(focus.getBegin()));
                    bonding.put(focus.getEnd(), getBondArray(focus.getEnd()));
                }
            }
        }
    }

    private static IBond[] getBondArray(IAtom focus) {
        IBond[] nbors = new IBond[focus.getBondCount()];
        int i = 0;
        for (IBond bond : focus.bonds())
            nbors[i++] = bond;
        return nbors;
    }

    private void optimize(List<TransformOp> ops) {
        // if we delete an atom after remove one of it's bonds we
        // can better run this just by deleting the atom
        for (int i = ops.size() - 1; i > 2; i--)
            if (canDoReplaceAtom(ops.get(i - 3), ops.get(i - 2),
                                 ops.get(i - 1), ops.get(i)))
                i = optimizeReplaceAtom(ops, i);

        // if we add a single bonded atom on then remove a single hydrogen
        // special case this to preserve stereochemistry
        for (int i = ops.size() - 1; i > 1; i--)
            if (canDoReplaceH(ops.get(i - 2), ops.get(i - 1), ops.get(i)))
                i = optimizeReplaceHydrogen(ops, i);

        // if we delete an atom after remove one of it's bonds we
        // can better run this just by deleting the atom
        for (int i = ops.size() - 1; i > 0; i--)
            if (deleteBondedAtom(ops.get(i - 1), ops.get(i)))
                ops.remove(i - 1);
    }

    private int optimizeReplaceHydrogen(List<TransformOp> ops, int i) {
        // adjH i-2, newAtom i-1, newBond i
        ops.set(i - 2, new TransformOp(TransformOp.Type.ReplaceHydrogen,
                                       ops.get(i - 2).a,
                                       ops.get(i - 1).a,
                                       ops.get(i - 1).b,
                                       ops.get(i - 1).c));
        if (ops.get(i - 1).d != 0)
            ops.set(i - 1, new TransformOp(TransformOp.Type.Aromatic, ops.get(i - 1).a, 1));
        else
            ops.remove(i--);
        ops.remove(i--);
        return i;
    }

    private int optimizeReplaceAtom(List<TransformOp> ops, int i) {
        int from = ops.get(i - 3).a;
        int to = ops.get(i).a;
        ops.set(i - 3, new TransformOp(TransformOp.Type.ReplaceAtom,
                                       ops.get(i).a,
                                       ops.get(i - 3).b,
                                       ops.get(i - 3).c,
                                       ops.get(i - 3).d));
        if (ops.get(i - 1).c != ops.get(i - 2).c) {
            ops.set(i - 2, new TransformOp(TransformOp.Type.BondOrder,
                                           ops.get(i - 1).a,
                                           ops.get(i - 1).b,
                                           ops.get(i - 2).c));
        } else {
            ops.remove(i--);
        }
        ops.remove(i--);
        ops.remove(i--);
        for (int j = i; j < ops.size(); j++)
            ops.set(j, ops.get(j).remap(from, to));
        return i;
    }

    private boolean deleteBondedAtom(TransformOp fst, TransformOp snd) {
        return snd.type == TransformOp.Type.DeleteAtom &&
                fst.type == TransformOp.Type.DeleteBond &&
                (snd.a == fst.a || snd.a == fst.b);
    }

    private boolean canReuseBond(TransformOp newBnd, TransformOp delBnd) {
        if (newBnd.type != TransformOp.Type.NewBond)
            return false;
        if (delBnd.type != TransformOp.Type.DeleteBond)
            return false;
        // is there an atom index overlap?
        return newBnd.a == delBnd.a ||
                newBnd.a == delBnd.b ||
                newBnd.b == delBnd.a ||
                newBnd.b == delBnd.b;
    }

    private boolean canDoReplaceAtom(TransformOp op1, TransformOp op2, TransformOp op3, TransformOp op4) {
        // NewAtm, NewBnd, DelBnd, DelAtm
        return op1.type == TransformOp.Type.NewAtom &&
                canReuseBond(op2, op3) &&
                op4.type == TransformOp.Type.DeleteAtom;
    }

    private boolean canDoReplaceH(TransformOp adjH, TransformOp newAtm, TransformOp newBnd) {
        // NewAtm, NewBnd, AdjustH
        return newAtm.type == TransformOp.Type.NewAtom &&
                newBnd.type == TransformOp.Type.NewBond &&
                adjH.type == TransformOp.Type.AdjustH &&
                adjH.b == -1 &&
                newAtm.a == newBnd.getOtherIdx(adjH.a) &&
                (newBnd.c == 1 || newBnd.c == 5);
    }


    private int numNewAtoms(List<TransformOp> ops) {
        int count = 0;
        for (TransformOp op : ops)
            if (op.type == TransformOp.Type.NewAtom ||
                    op.type == TransformOp.Type.PromoteH)
                count++;
        return count;
    }

    private int maxAtomIdx(List<TransformOp> ops) {
        int max = 0;
        for (TransformOp op : ops)
            max = Math.max(op.getMaxAtomIdx(), max);
        return max;
    }

    private int firstNewAtom(List<TransformOp> ops) {
        int min = Integer.MAX_VALUE;
        for (TransformOp op : ops)
            if (op.type == TransformOp.Type.NewAtom)
                min = Math.min(op.a, min);
        return min;
    }

    private static final IBond.Order[] BOND_ORDERS = new IBond.Order[]{
            IBond.Order.UNSET,
            IBond.Order.SINGLE,
            IBond.Order.DOUBLE,
            IBond.Order.TRIPLE,
            IBond.Order.QUADRUPLE,
            IBond.Order.SINGLE // aromatic also set
    };


    private static boolean apply(IAtomContainer mol,
                                 IAtom[] amap,
                                 TransformOp op) {
        switch (op.type) {
            case NewAtom:
                amap[op.a] = newAtom(mol, op.b, op.c, op.d);
                break;
            case ReplaceAtom:
                replaceAtom(mol, amap, op);
                break;
            case ReplaceHydrogen:
                if (!replaceHydrogen(mol, amap, op))
                    return false;
                break;
            case NewBond:
                if (amap[op.a].getBond(amap[op.b]) != null)
                    return false;
                if (amap[op.a] == null || amap[op.b] == null)
                    throw new IllegalStateException(op + " atoms={null=" + (amap[op.a] == null) + ",null=" + (amap[op.b] == null) + "}");
                mol.addBond(amap[op.a].getIndex(), amap[op.b].getIndex(),
                            BOND_ORDERS[op.c]);
                if (op.c == 5 && amap[op.a].isAromatic() && amap[op.b].isAromatic())
                    amap[op.a].getBond(amap[op.b]).setIsAromatic(true);
                markBondingChanged(amap[op.a], amap[op.b]);
                break;
            case DeleteAtom:
                // mark and sweep would be more optimal but require API changes
                // Important: can remove stereochemistry
                mol.removeAtom(amap[op.a]);
                markBondingChanged(amap[op.a]);
                break;
            case DeleteBond:
                mol.removeBond(amap[op.a].getBond(amap[op.b]));
                markBondingChanged(amap[op.a], amap[op.b]);
                break;
            case BondOrder:
                amap[op.a].getBond(amap[op.b]).setOrder(BOND_ORDERS[op.c]);
                markBondingChanged(amap[op.a], amap[op.b]);
                break;
            case Element:
                amap[op.a].setAtomicNumber(op.b);
                break;
            case Aromatic:
                amap[op.a].setIsAromatic(op.b != 0);
                break;
            case AromaticBond:
                amap[op.a].getBond(amap[op.b]).setIsAromatic(op.c != 0);
                break;
            case Charge:
                amap[op.a].setFormalCharge(op.b);
                break;
            case ImplH:
                amap[op.a].setImplicitHydrogenCount(op.b);
                markBondingChanged(amap[op.a]);
                break;
            case TotalH:
                if (!setTotalH(amap[op.a], op.b))
                    return false;
                markBondingChanged(amap[op.a]);
                break;
            case AdjustH:
                if (!adjustHydrogenCount(mol, amap[op.a], op.b))
                    return false;
                markBondingChanged(amap[op.a]);
                break;
            case MoveH:
                if (!moveHydrogen(mol, amap[op.a], amap[op.b]))
                    return false;
                markBondingChanged(amap[op.a], amap[op.b]);
                break;
            case PromoteH:
                if (!promoteHydrogen(mol, amap, amap[op.a], op.b))
                    return false;
                break;
            case Mass:
                amap[op.a].setMassNumber(op.b);
                break;
            case Tetrahedral:
                setLeftHandedTetrahedral(mol, amap, op);
                break;
            case DbTogether:
            case DbOpposite:
                setDbStereo(mol, amap, op);
                break;
            default:
                return false;
        }

        return true;
    }

    private static boolean setTotalH(IAtom atm, int hcnt) {
        int numExplH = 0;
        for (IBond bond : atm.bonds()) {
            IAtom atom = bond.getOther(atm);
            if (atom.getAtomicNumber() == 1)
                numExplH++;
        }
        if (hcnt >= numExplH) {
            atm.setImplicitHydrogenCount(hcnt - numExplH);
        } else {
            // need to delete some explicit hydrogens
            atm.setImplicitHydrogenCount(0);
            List<IAtom> terminalHydrogens = new ArrayList<>(numExplH);
            for (IBond bond : atm.bonds()) {
                IAtom nbor = bond.getOther(atm);
                if (nbor.getAtomicNumber() == 1 &&
                    nbor.getBondCount() == 1)
                    terminalHydrogens.add(nbor);
            }
            if (numExplH-terminalHydrogens.size() > hcnt)
                return false;
            IAtomContainer mol = atm.getContainer();
            for (IAtom hatm : terminalHydrogens) {
                mol.removeAtom(hatm); // also deletes
                numExplH--;
                if (numExplH == hcnt)
                    break;
            }
        }
        return true;
    }

    /**
     * Synchronize stereochemistry, remove any stereochemistry where the bonding
     * around the central atom/bond (the focus) changed during the transform.
     *
     * @param mol the molecule to synchronised
     */
    private static void resyncStereo(IAtomContainer mol, List<IStereoElement<?,?>> stereo, Map<IAtom,IBond[]> bonding) {
        if (!stereo.isEmpty()) {
            boolean modified = false;
            List<IStereoElement<?, ?>> updatedStereo = new ArrayList<>();
            for (IStereoElement<?, ?> se : stereo) {
                switch (se.getConfigClass()) {
                    case IStereoElement.Tetrahedral:
                    case IStereoElement.SquarePlanar:
                    case IStereoElement.TrigonalBipyramidal:
                    case IStereoElement.Octahedral:
                        if (!bondingChanged(se.getFocus())) {
                            updatedStereo.add(se);
                        } else {
                            modified = true;
                            boolean okay = true;
                            @SuppressWarnings("unchecked")
                            IStereoElement<IAtom, IAtom> atomStereo = (IStereoElement<IAtom, IAtom>)se;
                            IAtom focus = atomStereo.getFocus();
                            Set<IAtom> oldNbors = new HashSet<>(atomStereo.getCarriers());
                            Set<IAtom> newNbors = new HashSet<>();
                            for (IBond bond : focus.bonds()) {
                                if (bond.isAromatic())
                                    okay = false;
                                IAtom nbor = bond.getOther(focus);
                                if (!oldNbors.remove(nbor))
                                    newNbors.add(nbor);
                            }
                            if (oldNbors.size() > newNbors.size())
                                oldNbors.remove(focus);
                            if (okay &&
                                (oldNbors.size() == 1 && newNbors.size() == 1)) {
                                Map<IChemObject,IChemObject> mapping = new HashMap<>();
                                mapping.put(focus, focus);
                                for (IAtom old : atomStereo.getCarriers())
                                    mapping.put(old, old);
                                mapping.put(oldNbors.iterator().next(),
                                            newNbors.iterator().next());
                                List<IAtom> newLigands = new ArrayList<>();
                                for (IAtom atom : atomStereo.getCarriers())
                                    newLigands.add((IAtom)mapping.get(atom));
                                updatedStereo.add(new TetrahedralChirality(focus,
                                                                           newLigands.toArray(new IAtom[0]),
                                                                           se.getConfig()));
                            }
                        }
                        break;
                    case IStereoElement.Atropisomeric:
                        if (!bondingChanged(((IBond) se.getFocus()).getBegin()) &&
                                !bondingChanged(((IBond) se.getFocus()).getEnd())) {
                            updatedStereo.add(se);
                        } else {
                            modified = true;
                        }
                        break;
                    case IStereoElement.CisTrans:
                        if (!bondingChanged(((IBond) se.getFocus()).getBegin()) &&
                            !bondingChanged(((IBond) se.getFocus()).getEnd())) {
                            updatedStereo.add(se);
                        } else {
                            modified = true;
                            @SuppressWarnings("unchecked")
                            IStereoElement<IBond,IBond> bondStereo = (IStereoElement<IBond,IBond>)se;
                            IAtom beg = ((IBond) se.getFocus()).getBegin();
                            IAtom end = ((IBond) se.getFocus()).getEnd();
                            boolean okay = bondStereo.getFocus().getOrder() == IBond.Order.DOUBLE &&
                                           !bondStereo.getFocus().isAromatic() &&
                                           beg.getBond(end) == bondStereo.getFocus();
                            Set<IBond> oldBegNbors = new HashSet<>(Arrays.asList(bonding.get(beg)));
                            Set<IBond> oldEndNbors = new HashSet<>(Arrays.asList(bonding.get(end)));
                            Set<IBond> newBegNbors = new HashSet<>();
                            Set<IBond> newEndNbors = new HashSet<>();
                            Map<IChemObject,IChemObject> mapping = new HashMap<>();
                            for (IBond bond : beg.bonds()) {
                                if (bond == bondStereo.getFocus())
                                    continue;
                                if (!oldBegNbors.remove(bond))
                                    newBegNbors.add(bond);
                                else
                                    mapping.put(bond, bond);
                                if (bond.getOrder() != IBond.Order.SINGLE || bond.isAromatic())
                                    okay = false;
                            }
                            for (IBond bond : end.bonds()) {
                                if (bond == bondStereo.getFocus())
                                    continue;
                                if (!oldEndNbors.remove(bond))
                                    newEndNbors.add(bond);
                                else
                                    mapping.put(bond, bond);
                                if (bond.getOrder() != IBond.Order.SINGLE || bond.isAromatic())
                                    okay = false;
                            }
                            oldBegNbors.remove(bondStereo.getFocus());
                            oldEndNbors.remove(bondStereo.getFocus());
                            if (oldBegNbors.size() == 1 && newBegNbors.size() == 1)
                                mapping.put(oldBegNbors.iterator().next(), newBegNbors.iterator().next());
                            else if (oldBegNbors.size() != 0 || newBegNbors.size() != 0)
                                okay = false;
                            if (oldEndNbors.size() == 1 && newEndNbors.size() == 1)
                                mapping.put(oldEndNbors.iterator().next(), newEndNbors.iterator().next());
                            else if (oldEndNbors.size() != 0 || newEndNbors.size() != 0)
                                okay = false;
                            if (okay)
                                updatedStereo.add(se.map(mapping));
                        }
                        break;
                    case IStereoElement.Allenal: {
                        IAtom[] ends = ExtendedTetrahedral.findTerminalAtoms(mol, (IAtom) se.getFocus());
                        if (!bondingChanged(se.getFocus()) &&
                                !bondingChanged(ends[0]) && !bondingChanged(ends[1]))
                            updatedStereo.add(se);
                        else
                            modified = true;
                    }
                    break;
                    case IStereoElement.Cumulene: {
                        IAtom[] ends = ExtendedCisTrans.findTerminalAtoms(mol, (IBond) se.getFocus());
                        if (!bondingChanged(((IBond) se.getFocus()).getBegin()) &&
                            !bondingChanged(((IBond) se.getFocus()).getEnd()) &&
                                ends != null && !bondingChanged(ends[0]) && !bondingChanged(ends[1]))
                            updatedStereo.add(se);
                        else
                            modified = true;
                    }
                    break;
                    default:
                        throw new IllegalStateException("Unhandled stereochemistry type");
                }
            }
            if (modified) {
                mol.setStereoElements((List) updatedStereo);
            }
        }
        List<IStereoElement<?,?>> stereos = mol.getProperty(SMIRKS_NEWSTEREO);
        if (stereos != null) {
            for (IStereoElement<?, ?> se : stereos)
                mol.addStereoElement(se);
        }
        mol.removeProperty(SMIRKS_NEWSTEREO);
    }

    // this is inefficient, need better AtomContainer API points but OK for now
    private static void removeStereo(IAtomContainer mol, IChemObject atom) {
        List<IStereoElement<?,?>> stereo = new ArrayList<>();
        for (IStereoElement<?, ?> se : mol.stereoElements())
            if (!se.getFocus().equals(atom))
                stereo.add(se);
        mol.setStereoElements((List)stereo);
    }

    private static void setLeftHandedTetrahedral(IAtomContainer mol, IAtom[] amap, TransformOp op) {
        IAtom focus = amap[op.a];
        removeStereo(mol, focus);
        IAtom[] carriers = new IAtom[]{amap[op.b], amap[op.c], amap[op.d], focus};
        // replace the placeholder atom if this atom has 4 atoms
        if (focus.getBondCount() == 4) {
            for (IBond bond : focus.bonds()) {
                IAtom nbor = bond.getOther(focus);
                if (!nbor.equals(amap[op.b]) && !nbor.equals(amap[op.c]) && !nbor.equals(amap[op.d]))
                    carriers[3] = nbor;
            }
        }
        List<IStereoElement<?, ?>> newStereo = mol.getProperty(SMIRKS_NEWSTEREO);
        if (newStereo == null)
            mol.setProperty(SMIRKS_NEWSTEREO, newStereo = new ArrayList<>());
        newStereo.add(new TetrahedralChirality(focus,
                                               carriers,
                                               ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
    }

    private static void setDbStereo(IAtomContainer mol, IAtom[] amap, TransformOp op) {
        IBond dbBond = amap[op.a].getBond(amap[op.b]);
        if (dbBond == null)
            throw new IllegalStateException();
        removeStereo(mol, dbBond);
        IBond begNbor = amap[op.a].getBond(amap[op.c]);
        IBond endNBor = amap[op.b].getBond(amap[op.d]);
        if (begNbor == null || endNBor == null)
            throw new IllegalStateException();
        List<IStereoElement<?,?>> newStereo = mol.getProperty(SMIRKS_NEWSTEREO);
        if (newStereo == null)
            mol.setProperty(SMIRKS_NEWSTEREO, newStereo = new ArrayList<>());
        if (op.type == TransformOp.Type.DbTogether)
            newStereo.add(new DoubleBondStereochemistry(dbBond,
                                                        new IBond[]{begNbor, endNBor},
                                                        IDoubleBondStereochemistry.Conformation.TOGETHER));
        else {
            newStereo.add(new DoubleBondStereochemistry(dbBond,
                                                        new IBond[]{begNbor, endNBor},
                                                        IDoubleBondStereochemistry.Conformation.OPPOSITE));
        }
    }

    private static void replaceAtom(IAtomContainer mol, IAtom[] amap, TransformOp op) {
        amap[op.a].setMassNumber(null);
        amap[op.a].setAtomicNumber(op.b);
        amap[op.a].setImplicitHydrogenCount(op.c);
        amap[op.a].setIsAromatic(op.d != 0);
        removeUnmappedBonds(amap[op.a], mol);
    }

    private static boolean replaceHydrogen(IAtomContainer mol, IAtom[] amap, TransformOp op) {
        IAtom atom = amap[op.a];
        IAtom hAtm = null;
        int hcnt = atom.getImplicitHydrogenCount();
        if (hcnt == 0) {
            for (IBond bond : atom.bonds()) {
                if (bond.getOrder() != IBond.Order.SINGLE)
                    continue;
                IAtom nbor = bond.getOther(atom);
                if (isUnmappedExplH(nbor)) {
                    hAtm = nbor;
                    break;
                }
            }
            if (hAtm == null)
                return false;
        } else {
            amap[op.a].setImplicitHydrogenCount(hcnt - 1);
        }
        if (hAtm != null) {
            removeUnmappedBonds(hAtm, mol);
            hAtm.setMassNumber(null);
            hAtm.setAtomicNumber(op.c);
            hAtm.setImplicitHydrogenCount(op.d);
            hAtm.setFormalCharge(0);
            hAtm.setIsAromatic(false);
            amap[op.b] = hAtm;
        } else {
            IAtom newAtom = mol.getBuilder().newAtom();
            newAtom.setAtomicNumber(op.c);
            newAtom.setImplicitHydrogenCount(op.d);
            mol.addAtom(newAtom);
            amap[op.b] = mol.getAtom(mol.getAtomCount() - 1);
            mol.addBond(atom.getIndex(), amap[op.b].getIndex(),
                        IBond.Order.SINGLE);
        }
        return true;
    }

    private static void removeUnmappedBonds(IAtom hAtm, IAtomContainer mol) {
        List<IBond> bondsToDelete = new ArrayList<>();
        for (IBond bond : hAtm.bonds())
            if (isUnmapped(bond.getOther(hAtm)))
                bondsToDelete.add(bond);
        for (IBond bond : bondsToDelete)
            mol.removeBond(bond);
    }

    private static IAtom newAtom(IAtomContainer mol, int elem, int hnct, int arom) {
        IAtom atom = mol.getBuilder().newAtom();
        atom.setAtomicNumber(elem);
        atom.setImplicitHydrogenCount(hnct);
        atom.setIsAromatic(arom != 0);
        mol.addAtom(atom);
        return mol.getAtom(mol.getAtomCount() - 1);
    }

    // important!! this operation is atomic, the atom is not updated unless the operation is possible
    private static boolean adjustHydrogenCount(IAtomContainer mol, IAtom atom, int adjustment) {
        int updatedHcnt = atom.getImplicitHydrogenCount() + adjustment;
        if (updatedHcnt >= 0) {
            atom.setImplicitHydrogenCount(updatedHcnt);
        } else {
            if (!removeExplH(mol, atom, -updatedHcnt))
                return false;
            atom.setImplicitHydrogenCount(0);
        }
        return true;
    }

    private static boolean removeExplH(IAtomContainer mol,
                                       IAtom atom,
                                       int required) {
        Set<IAtom> deleted = new HashSet<>();
        for (IBond bond : atom.bonds()) {
            if (bond.getOrder() != IBond.Order.SINGLE)
                continue;
            IAtom nbor = bond.getOther(atom);
            if (isUnmappedExplH(nbor)) {
                deleted.add(nbor);
                if (deleted.size() == required)
                    break;
            }
        }
        if (deleted.size() != required)
            return false;
        for (IAtom a : deleted)
            mol.removeAtom(a);
        return true;
    }

    // important !! this operation is atomic, the atoms are not updated unless the operation is possible
    private static boolean moveHydrogen(IAtomContainer mol, IAtom from, IAtom to) {
        // if possible we move an implicit hydrogen (cheap)
        if (from.getImplicitHydrogenCount() != 0) {
            from.setImplicitHydrogenCount(from.getImplicitHydrogenCount() - 1);
            to.setImplicitHydrogenCount(to.getImplicitHydrogenCount() + 1);
            return true;
        } else {
            return moveExplH(mol, from, to);
        }
    }

    private static boolean promoteHydrogen(IAtomContainer mol, IAtom[] amap, IAtom from, int to) {
        // if possible we move an implicit hydrogen (cheap)
        if (from.getImplicitHydrogenCount() != 0) {
            from.setImplicitHydrogenCount(from.getImplicitHydrogenCount() - 1);
            IAtom atom = mol.getBuilder().newAtom();
            atom.setAtomicNumber(1);
            atom.setImplicitHydrogenCount(0);
            mol.addAtom(atom);
            amap[to] = mol.getAtom(mol.getAtomCount() - 1);
            return true;
        } else {
            if (moveExplH(mol, from, null)) {
                amap[to] = from;
                return true;
            }
            return false;
        }
    }

    private static boolean moveExplH(IAtomContainer mol,
                                     IAtom src, IAtom dst) {
        IAtom hAtm = null;
        IBond hBnd = null;
        for (IBond bond : src.bonds()) {
            if (bond.getOrder() != IBond.Order.SINGLE)
                continue;
            IAtom nbor = bond.getOther(src);
            if (isUnmappedExplH(nbor)) {
                hBnd = bond;
                hAtm = nbor;
                break;
            }
        }

        if (hAtm == null)
            return false;

        mol.removeBond(hBnd);
        if (dst != null)
            mol.addBond(dst.getIndex(), hAtm.getIndex(), IBond.Order.SINGLE);
        return true;
    }

    private static boolean isUnmappedExplH(IAtom atom) {
        if (atom.getAtomicNumber() != 1)
            return false;
        return isUnmapped(atom);
    }

    private static boolean isUnmapped(IAtom atom) {
        return !atom.getFlag(CDKConstants.MAPPED);
    }

    private static void markBondingChanged(IAtom atom) {
        atom.setFlag(CDKConstants.REACTIVE_CENTER, true);
    }

    private static void markBondingChanged(IAtom beg, IAtom end) {
        beg.setFlag(CDKConstants.REACTIVE_CENTER, true);
        end.setFlag(CDKConstants.REACTIVE_CENTER, true);
    }

    private static boolean bondingChanged(IChemObject chemobj) {
        return chemobj.getFlag(CDKConstants.REACTIVE_CENTER);
    }

    private void undo(IAtomContainer mol, IAtom[] amap, TransformOp op) {
        switch (op.type) {
            case NewAtom:
                mol.removeAtom(amap[op.a]);
                break;
            case NewBond:
                mol.removeBond(amap[op.a].getBond(amap[op.b]));
                break;
            case AdjustH:
                if (!adjustHydrogenCount(mol, amap[op.a], -op.b))
                    throw new IllegalStateException("Was not able to undo AdjustH");
                break;
            case MoveH:
                if (!moveHydrogen(mol, amap[op.b], amap[op.a]))
                    throw new IllegalStateException("Was not able to undo MoveH");
                break;
            default:
                throw new IllegalStateException("OpCode cannot be undone: " + op);
        }
    }

    /**
     * Undo the OpCodes (0 .. n)
     *
     * @param mol  the molecule
     * @param amap the atom map
     * @param n    undo operations before this op idx
     */
    private void undo(IAtomContainer mol, IAtom[] amap, int n) {
        for (int i = n - 1; i >= 0; i--)
            undo(mol, amap, ops.get(i));
    }
}
