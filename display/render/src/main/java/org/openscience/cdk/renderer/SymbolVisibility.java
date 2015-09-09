/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.renderer;

import org.openscience.cdk.config.Elements;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IIsotope;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.io.IOException;
import java.util.List;

/**
 * Predicate that defines whether an atom symbol is displayed in a structure diagram.
 *
 * <pre>{@code
 * SymbolVisibility visibility = SymbolVisibility.iupacRecommendations();
 * }</pre>
 *
 * @author John May
 */
public abstract class SymbolVisibility {

    /**
     * Determine if an atom with the specified bonds is visible.
     *
     * @param atom      an atom
     * @param neighbors neighboring bonds
     * @return whether the atom symbol is visible
     */
    public abstract boolean visible(IAtom atom, List<IBond> neighbors, RendererModel model);

    /**
     * All atom symbols are visible.
     *
     * @return visibility that displays all symbols
     */
    public static SymbolVisibility all() {
        return new SymbolVisibility() {

            @Override
            public boolean visible(IAtom atom, List<IBond> neighbors, RendererModel model) {
                return true;
            }
        };
    }

    /**
     * Displays a symbol based on the preferred representation from the IUPAC guidelines (GR-2.1.2)
     * {@cdk.cite Brecher08}. Carbons are unlabeled unless they have abnormal valence, parallel
     * bonds, or are terminal (i.e. methyl, methylene, etc).
     *
     * @return symbol visibility instance
     */
    public static SymbolVisibility iupacRecommendations() {
        return new IupacVisibility(true);
    }

    /**
     * Displays a symbol based on the acceptable representation from the IUPAC guidelines (GR-2.1.2)
     * {@cdk.cite Brecher08}. Carbons are unlabeled unless they have abnormal valence, parallel
     * bonds. The recommendations note that it is acceptable to leave methyl groups unlabelled.
     *
     * @return symbol visibility instance
     */
    public static SymbolVisibility iupacRecommendationsWithoutTerminalCarbon() {
        return new IupacVisibility(false);
    }

    /**
     * Visibility following IUPAC guidelines.
     */
    private static final class IupacVisibility extends SymbolVisibility {

        private boolean terminal = false;

        private IupacVisibility(boolean terminal) {
            this.terminal = terminal;
        }

        /**
         * @inheritDoc
         */
        @Override
        public boolean visible(IAtom atom, List<IBond> bonds, RendererModel model) {

            final Elements element = Elements.ofNumber(atom.getAtomicNumber());

            // all non-carbons are displayed
            if (element != Elements.Carbon) return true;

            // methane
            if (bonds.size() == 0) return true;

            // methyl (optional)
            if (bonds.size() == 1 && terminal) return true;

            // abnormal valence, could be due to charge or unpaired electrons
            if (!isFourValent(atom, bonds)) return true;

            // carbon isotopes are displayed
            Integer mass = atom.getMassNumber();
            if (mass != null && !isMajorIsotope(element.number(), mass)) return true;

            // no kink between bonds to imply the presence of a carbon and it must
            // be displayed
            if (hasParallelBonds(atom, bonds)) {
                // TODO only when both bonds are single?
                return true;
            }

            // ProblemMarker ?

            return false;
        }

        /**
         * Determine if the specified mass is the major isotope for the given atomic number.
         *
         * @param number atomic number
         * @param mass   atomic mass
         * @return the mass is the major mass for the atomic number
         */
        private static boolean isMajorIsotope(int number, int mass) {
            try {
                IIsotope isotope = Isotopes.getInstance().getMajorIsotope(number);
                return isotope != null && isotope.getMassNumber().equals(mass);
            } catch (IOException e) {
                return false;
            }
        }

        /**
         * Check the valency of the atom.
         *
         * @param atom  an atom
         * @param bonds bonds connected to the atom
         * @return whether the atom is four valent
         */
        private static boolean isFourValent(IAtom atom, List<IBond> bonds) {
            Integer valence = atom.getImplicitHydrogenCount();
            if (valence == null) return true;
            for (final IBond bond : bonds) {
                valence += bond.getOrder().numeric();
            }
            return valence == 4;
        }

        /**
         * Check whether the atom has only two bonds connected and they are (or close to) parallel.
         *
         * @param atom  an atom
         * @param bonds bonds connected to the atom
         * @return whether the atom has parallele bonds
         */
        private static boolean hasParallelBonds(IAtom atom, List<IBond> bonds) {
            if (bonds.size() != 2) return false;
            final double thetaInRad = getAngle(atom, bonds.get(0), bonds.get(1));
            final double thetaInDeg = Math.toDegrees(thetaInRad);
            final double delta = Math.abs(thetaInDeg - 180);
            return delta < 8;
        }

        /**
         * Determine the angle between two bonds of one atom.
         *
         * @param atom  an atom
         * @param bond1 a bond connected to the atom
         * @param bond2 another bond connected to the atom
         * @return the angle (in radians)
         */
        private static double getAngle(IAtom atom, IBond bond1, IBond bond2) {
            final Point2d pA = atom.getPoint2d();
            final Point2d pB = bond1.getConnectedAtom(atom).getPoint2d();
            final Point2d pC = bond2.getConnectedAtom(atom).getPoint2d();
            final Vector2d u = new Vector2d(pB.x - pA.x, pB.y - pA.y);
            final Vector2d v = new Vector2d(pC.x - pA.x, pC.y - pA.y);
            return u.angle(v);
        }
    }
}
