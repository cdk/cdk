/*  Copyright (C) 2008  Miguel Rojas <miguelrojasch@yahoo.es>
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.charges;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.graph.ShortestPaths;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.HyperconjugationReaction;
import org.openscience.cdk.tools.StructureResonanceGenerator;

/**
 * The stabilization of the positive and the negative charge
 * obtained (e.g in the polar breaking of a bond) is calculated from the sigma- and
 *  lone pair-electronegativity values of the atoms that are in conjugation to the atoms
 *  obtaining the charges. Based on H. Saller Dissertation @cdk.cite{SallerH1895}.
 *
 * @author       Miguel Rojas Cherto
 * @cdk.created  2008-104-31
 * @cdk.module   charges
 * @cdk.keyword  stabilization charge
 * @cdk.githash
 */
public class StabilizationCharges {

    /**
     * Constructor for the StabilizationCharges object.
     */
    public StabilizationCharges() {}

    /**
     * calculate the stabilization of orbitals when they contain deficiency of charge.
     *
     * @param atomContainer the molecule to be considered
     * @param atom       IAtom for which effective atom StabilizationCharges
     *                   factor should be calculated
     *
     * @return stabilizationValue
     */
    public double calculatePositive(IAtomContainer atomContainer, IAtom atom) {
        /* restrictions */
        //    	if(atomContainer.getConnectedSingleElectronsCount(atom) > 0 || atom.getFormalCharge() != 1){
        if (atom.getFormalCharge() != 1) {
            return 0.0;
        }

        // only must be generated all structures which stabilize the atom in question.
        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
        List<IReactionProcess> reactionList = gRI.getReactions();
        reactionList.add(new HyperconjugationReaction());
        gRI.setReactions(reactionList);
        IAtomContainerSet resonanceS = gRI.getStructures(atomContainer);
        IAtomContainerSet containerS = gRI.getContainers(atomContainer);
        if (resonanceS.getAtomContainerCount() < 2) // meaning it was not find any resonance structure
            return 0.0;

        final int positionStart = atomContainer.getAtomNumber(atom);

        List<Double> result1 = new ArrayList<Double>();
        List<Integer> distance1 = new ArrayList<Integer>();

        resonanceS.removeAtomContainer(0);// the first is the initial structure
        for (Iterator<IAtomContainer> itA = resonanceS.atomContainers().iterator(); itA.hasNext();) {
            final IAtomContainer resonance = itA.next();

            if (resonance.getAtomCount() < 2) // resonance with only one atom donnot have resonance
                continue;

            final ShortestPaths shortestPaths = new ShortestPaths(resonance, resonance.getAtom(positionStart));

            /* search positive charge */

            PiElectronegativity electronegativity = new PiElectronegativity();

            for (Iterator<IAtom> itAtoms = resonance.atoms().iterator(); itAtoms.hasNext();) {
                IAtom atomP = itAtoms.next();
                IAtom atomR = atomContainer.getAtom(resonance.getAtomNumber(atomP));
                if (containerS.getAtomContainer(0).contains(atomR)) {

                    electronegativity.setMaxIterations(6);
                    double result = electronegativity.calculatePiElectronegativity(resonance, atomP);
                    result1.add(result);

                    int dis = shortestPaths.distanceTo(atomP);
                    distance1.add(dis);
                }

            }
        }
        /* logarithm */
        double value = 0.0;
        double sum = 0.0;
        Iterator<Integer> itDist = distance1.iterator();
        for (Iterator<Double> itElec = result1.iterator(); itElec.hasNext();) {
            double suM = itElec.next();
            if (suM < 0) suM = -1 * suM;
            sum += suM * Math.pow(0.67, itDist.next().intValue());
        }
        value = sum;

        return value;
    }
}
