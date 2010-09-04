/* Copyright (C) 2008-2009  Gilleain Torrance <gilleain.torrance@gmail.com>
 *               2008-2009  Arvid Berg <goglepox@users.sf.net>
 *                    2009  Stefan Kuhn <shk3@users.sf.net>
 *               2009-2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@list.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.renderer;

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;

/**
 * @cdk.module renderbasic
 */
public class AverageBondLengthCalculator {

    public static double calculateAverageBondLength(IReaction reaction) {

        IMoleculeSet reactants = reaction.getReactants();
        double reactantAverage = 0.0;
        if (reactants != null) {
            reactantAverage =
                calculateAverageBondLength(reactants) /
                reactants.getAtomContainerCount();
        }

        IMoleculeSet products = reaction.getProducts();
        double productAverage = 0.0;
        if (products != null) {
            productAverage =
                calculateAverageBondLength(products) /
                products.getAtomContainerCount();
        }

        if (productAverage == 0.0 && reactantAverage == 0.0) {
            return 1.0;
        } else {
            return (productAverage + reactantAverage) / 2.0;
        }
    }

    public static double calculateAverageBondLength(IMoleculeSet moleculeSet) {
        double averageBondModelLength = 0.0;
        for (IAtomContainer atomContainer : moleculeSet.molecules()) {
            averageBondModelLength +=
                GeometryTools.getBondLengthAverage(atomContainer);
        }
        return averageBondModelLength / moleculeSet.getAtomContainerCount();
    }

    /**
    *
    *
    *  @param model the model for which to calculate the average bond length
    */
   public static double calculateAverageBondLength(IChemModel model) {

       // empty models have to have a scale
       IMoleculeSet moleculeSet = model.getMoleculeSet();
       if (moleculeSet == null) {
           IReactionSet reactionSet = model.getReactionSet();
           if (reactionSet != null) {
               return calculateAverageBondLength(reactionSet);
           }
           return 0.0;
       }

       return calculateAverageBondLength(moleculeSet);
   }

   public static double calculateAverageBondLength(IReactionSet reactionSet) {
       double averageBondModelLength = 0.0;
       for (IReaction reaction : reactionSet.reactions()) {
           averageBondModelLength +=
               calculateAverageBondLength(reaction);
       }
       return averageBondModelLength / reactionSet.getReactionCount();
   }

}
