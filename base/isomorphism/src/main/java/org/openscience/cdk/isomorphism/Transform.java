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
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Transform a molecule using a query pattern and a sequence of
 * {@link TransformOp}s. The query pattern is matched against a given molecule
 * and based on that mapping a series of additions/deletions/changes are made.
 * Transformations can be used for both standardisation/normalisation and
 * library generation. <br/>
 * This base class provides the low level implementation needed to run a
 * transformation, in practice you would use something like
 * {@link org.openscience.cdk.smirks.Smirks} in the <code>cdk-smarts</code>
 * module to parse a transform from a string representation.
 * <br/>
 * The simplest way to use a transform is the {@link #apply(org.openscience.cdk.interfaces.IAtomContainer)}
 * method, this runs the transform in-place over non-overlapping matches.
 * <pre>{@code
 * Transform tform = new Transform();
 * if (Smirks.parse(tform, "[*:1][NH2D1:2]>>[*:1]O amine to hydroxy")) {
 *   IAtomContainer mol = ...;
 *   tform.apply(mol); // replace all NH2 groups with OH
 * }
 * }</pre>
 * Note that non-overlapping matches can depend on the order of the atoms in a
 * query or molecule being transformed. A more verbose mode is to provide the
 * optional second {@link org.openscience.cdk.isomorphism.Transform.Mode}
 * argument, this results in a copy of the input molecule being made multiple
 * results being returned:
 *
 * <pre>{@code
 * Transform tform = new Transform();
 * if (Smirks.parse(tform, "[*:1][NH2D1:2]>>[*:1]O amine to hydroxy")) {
 *   IAtomContainer mol = ...;
 *   // replace each NH2 groups with OH one at a time
 *   for (IAtomContainer copy : tform.apply(mol, Mode.All)) {
 *
 *   }
 * }
 * }</pre>
 *
 * @author John Mayfield
 * @see org.openscience.cdk.isomorphism.Pattern
 * @see org.openscience.cdk.isomorphism.TransformOp
 * @see org.openscience.cdk.smirks.Smirks
 */
public class Transform {

    private static final ILoggingTool LOGGER = LoggingToolFactory.createLoggingTool(Transform.class);
    public static final String NO_TRANSFORM_DEFINED = "No transform defined";

    public enum Mode {
        /**
         * Run the transform at all places the query matches. A collection is
         * returned.
         */
        All,
        /**
         * Run the transform at all (unique) places the query matches.
         * {@link Mappings#uniqueAtoms()}
         */
        Unique,
        /**
         * Run the transform at all (exclusive) places the query matches.
         * Note: A single result is returned or none.
         * {@link Mappings#exclusiveAtoms()}
         */
        Exclusive
    }

    private enum Status {
        OK,
        WARNING,
        ERROR
    }

    private Pattern pattern;
    private TransformPlan plan;
    private Status status;
    private String message;

    /**
     * Create an empty transform.
     */
    public Transform() {
        setError(NO_TRANSFORM_DEFINED);
    }

    Transform(Pattern substructure, List<TransformOp> ops) {
        init(substructure, ops, null);
    }

    /**
     * Initialize the transform.
     *
     * @param pattern the substructure pattern to match
     * @param ops the ops to run on the atoms of the pattern
     * @param warning the warning message (optional)
     */
    public void init(Pattern pattern, List<TransformOp> ops, String warning) {
        if (pattern == null || ops == null)
            throw new NoSuchElementException("Pattern and ops must be provided!");
        this.pattern = pattern;
        this.plan = new TransformPlan(ops);
        if (warning != null && !warning.isEmpty())
            setWarning(warning);
        else
            setOk();
    }

    /**
     * Initialize the transform.
     *
     * @param pattern the substructure pattern to match
     * @param ops the ops to run on the atoms of the pattern
     */
    public void init(Pattern pattern, List<TransformOp> ops) {
        init(pattern, ops, null);
    }

    private void setOk() {
        this.status = Status.OK;
        this.message = null;
    }

    private void setWarning(String warning) {
        this.status = Status.WARNING;
        this.message = warning;
    }

    /**
     * Indicate there is an error with this transform. This
     * method will clear any existing message and status. Any attempt to call
     * {@link #apply(org.openscience.cdk.interfaces.IAtomContainer)} will
     * be ignored (no-op) and return false (did not apply).
     * <br/>
     * You can clear the error status by calling,
     * {@link #init(Pattern, java.util.List)}.
     *
     * @param message the warning/error message
     * @return returns false
     */
    public boolean setError(String message) {
        this.status = Status.ERROR;
        this.message = message;
        return false;
    }

    /**
     * Access the warning/error message on a problem found with the transform.
     *
     * @return the message (null if there are no warnings or errors)
     */
    public String message() {
        return message;
    }

    /**
     * Reset this transform so it can be reused. This will set the status to
     * Error and indicate a message that the transform is not initialized.
     */
    public void reset() {
        setError(NO_TRANSFORM_DEFINED);
        pattern = null;
        plan.clear();
    }

    /**
     * Apply the transform to the provided molecule and obtain the results of
     * applying the transform. The original molecule is <b>NOT</b> modified.
     *
     * @param mol  the molecule to transform
     * @param mode how to transform the molecule
     * @return an iterable which may be empty or contain copies of the molecule
     * transformed
     */
    public Iterable<IAtomContainer> apply(IAtomContainer mol, Mode mode) {

        if (status == Status.ERROR)
            return Collections.emptyList();

        // We can make this lazy
        List<IAtomContainer> results = new ArrayList<>();

        if (mode == Mode.Exclusive) {
            IAtomContainer cpy = copyOf(mol);
            if (apply(cpy))
                results.add(cpy);
        } else {
            Mappings mappings = pattern.matchAll(mol);
            if (mode == Mode.Unique)
                mappings = mappings.uniqueAtoms();
            IAtom[] amap = new IAtom[plan.requiredAtomCapacity(mol)];
            for (int[] match : mappings) {
                IAtomContainer cpy = copyOf(mol);
                permute(amap, match, cpy);
                if (plan.apply(cpy, amap))
                    results.add(cpy);
            }
        }
        return Collections.unmodifiableList(results);
    }

    /**
     * Applies the exclusive transform to the provided molecule modifying it as
     * required.
     *
     * @param mol the molecule to modify
     * @return the molecule was modified or not
     */
    public boolean apply(IAtomContainer mol) {
        if (status == Status.ERROR)
            return false;

        // atoms may be deleted, so we need to the atoms by index separately
        IAtom[] atoms = AtomContainerManipulator.getAtomArray(mol);
        IAtom[] amap = new IAtom[plan.requiredAtomCapacity(mol)];

        boolean changed = false;
        for (int[] match : pattern.matchAll(mol).exclusiveAtoms().toArray()) {
            permute(amap, match, atoms);
            if (plan.apply(mol, amap))
                changed = true;
        }

        return changed;
    }

    /* Internal Methods */

    // important we store atoms at index +1 to make it easier to debug/compare
    // to the labelled atom maps
    private void permute(IAtom[] amap, int[] match, IAtom[] atoms) {
        for (int i = 0; i < match.length; i++)
            amap[i + 1] = atoms[match[i]];
    }

    // important we store atoms at index +1 to make it easier to debug/compare
    // to the labelled atom maps
    private void permute(IAtom[] amap, int[] match, IAtomContainer mol) {
        for (int i = 0; i < match.length; i++)
            amap[i + 1] = mol.getAtom(match[i]);
        for (int i = 1; i <= match.length; i++)
            amap[i].setFlag(CDKConstants.MAPPED, true);
    }

    private static IAtomContainer copyOf(IAtomContainer mol) {
        try {
            return mol.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Could not clone() molecule");
        }
    }
}
