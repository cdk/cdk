/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
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
package org.openscience.cdk.geometry.cip.rules;

import java.util.Arrays;

import org.openscience.cdk.geometry.cip.CIPTool;
import org.openscience.cdk.geometry.cip.ILigand;

/**
 * Compares to {@link ILigand}s based on CIP sequences sub rules. The used CIP sub rules are:
 * <ol>
 *   <li>{@link MassNumberRule}</li>
 *   <li>{@link AtomicNumberRule}</li>
 * </ol>
 *
 * @cdk.module cip
 * @cdk.githash
 */
public class CIPLigandRule implements ISequenceSubRule<ILigand> {

    CombinedAtomicMassNumberRule numberRule = new CombinedAtomicMassNumberRule();

    /** {@inheritDoc} */
    @Override
    public int compare(ILigand ligand1, ILigand ligand2) {
        int numberComp = numberRule.compare(ligand1, ligand2);
        if (numberComp != 0) return numberComp;

        // OK, now I need to recurse...
        ILigand[] ligand1Ligands = CIPTool.getLigandLigands(ligand1);
        ILigand[] ligand2Ligands = CIPTool.getLigandLigands(ligand2);
        // if neither have ligands:
        if (ligand1Ligands.length == 0 && ligand2Ligands.length == 0) return 0;
        // else if one has no ligands
        if (ligand1Ligands.length == 0) return -1;
        if (ligand2Ligands.length == 0) return 1;
        // ok, both have at least one ligand
        int minLigandCount = Math.min(ligand1Ligands.length, ligand2Ligands.length);
        if (ligand1Ligands.length > 1) ligand1Ligands = order(ligand1Ligands);
        if (ligand2Ligands.length > 1) ligand2Ligands = order(ligand2Ligands);
        // first do a basic number rule
        for (int i = 0; i < minLigandCount; i++) {
            int comparison = numberRule.compare(ligand1Ligands[i], ligand2Ligands[i]);
            if (comparison != 0) return comparison;
        }
        if (ligand1Ligands.length == ligand2Ligands.length) {
            // it that does not resolve it, do a full, recursive compare
            for (int i = 0; i < minLigandCount; i++) {
                int comparison = compare(ligand1Ligands[i], ligand2Ligands[i]);
                if (comparison != 0) return comparison;
            }
        }
        // OK, if we reached this point, then the ligands they 'share' are all equals, so the one
        // with more ligands wins
        if (ligand1Ligands.length > ligand2Ligands.length)
            return 1;
        else if (ligand1Ligands.length < ligand2Ligands.length)
            return -1;
        else
            return 0;
    }

    /**
     * Order the ligands from high to low precedence according to atomic and mass numbers.
     */
    private ILigand[] order(ILigand[] ligands) {
        ILigand[] newLigands = new ILigand[ligands.length];
        System.arraycopy(ligands, 0, newLigands, 0, ligands.length);

        Arrays.sort(newLigands, numberRule);
        // this above list is from low to high precendence, so we need to revert the array
        ILigand[] reverseLigands = new ILigand[newLigands.length];
        for (int i = 0; i < newLigands.length; i++) {
            reverseLigands[(newLigands.length - 1) - i] = newLigands[i];
        }
        return reverseLigands;
    }

}
