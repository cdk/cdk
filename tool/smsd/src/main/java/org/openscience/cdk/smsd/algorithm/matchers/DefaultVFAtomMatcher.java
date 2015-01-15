/* Copyright (C) 2009-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
 *
 * MX Cheminformatics Tools for Java
 *
 * Copyright (c) 2007-2009 Metamolecular, LLC
 *
 * http://metamolecular.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.openscience.cdk.smsd.algorithm.matchers;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.smsd.algorithm.vflib.builder.TargetProperties;

/**
 * Checks if atom is matching between query and target molecules.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
public class DefaultVFAtomMatcher implements VFAtomMatcher {

    static final long  serialVersionUID = -7861469841127327812L;
    private int        maximumNeighbors;
    private String     symbol           = null;
    private IAtom      qAtom            = null;
    private IQueryAtom smartQueryAtom   = null;
    private boolean    shouldMatchBonds = false;

    /**
     * @return the shouldMatchBonds
     */
    public boolean isBondMatchFlag() {
        return shouldMatchBonds;
    }

    /**
     * @param shouldMatchBonds the shouldMatchBonds to set
     */
    public final void setBondMatchFlag(boolean shouldMatchBonds) {
        this.shouldMatchBonds = shouldMatchBonds;
    }

    /**
     * Constructor
     */
    public DefaultVFAtomMatcher() {
        this.qAtom = null;
        symbol = null;
        maximumNeighbors = -1;
    }

    /**
     * Constructor
     * @param queryContainer query atom container
     * @param atom query atom
     * @param shouldMatchBonds bond matching flag
     */
    public DefaultVFAtomMatcher(IAtomContainer queryContainer, IAtom atom, boolean shouldMatchBonds) {
        this();
        this.qAtom = atom;
        this.symbol = atom.getSymbol();
        setBondMatchFlag(shouldMatchBonds);

        //        System.out.println("Atom " + atom.getSymbol());
        //        System.out.println("MAX allowed " + maximumNeighbors);
    }

    /**
     * Constructor
     * @param smartQueryAtom query atom
     * @param container
     */
    public DefaultVFAtomMatcher(IQueryAtom smartQueryAtom, IQueryAtomContainer container) {
        this();
        this.smartQueryAtom = smartQueryAtom;
        this.symbol = smartQueryAtom.getSymbol();
    }

    /**
     * Constructor
     * @param queryContainer query atom container
     * @param template query atom
     * @param blockedPositions
     * @param shouldMatchBonds bond matching flag
     */
    public DefaultVFAtomMatcher(IAtomContainer queryContainer, IAtom template, int blockedPositions,
            boolean shouldMatchBonds) {
        this(queryContainer, template, shouldMatchBonds);
        this.maximumNeighbors = countImplicitHydrogens(template) + queryContainer.getConnectedAtomsCount(template)
                - blockedPositions;
    }

    /**
     *
     * @param maximum numbers of connected atoms allowed
     */
    public void setMaximumNeighbors(int maximum) {
        this.maximumNeighbors = maximum;
    }

    /**
     * @param symbol
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    private boolean matchSymbol(IAtom atom) {
        if (symbol == null) {
            return false;
        }
        return symbol.equals(atom.getSymbol());
    }

    private boolean matchMaximumNeighbors(TargetProperties targetContainer, IAtom targetAtom) {
        if (maximumNeighbors == -1 || !isBondMatchFlag()) {
            return true;
        }

        int maximumTargetNeighbors = targetContainer.countNeighbors(targetAtom);
        return maximumTargetNeighbors <= maximumNeighbors;
    }

    private int countImplicitHydrogens(IAtom atom) {
        return (atom.getImplicitHydrogenCount() == null) ? 0 : atom.getImplicitHydrogenCount();
    }

    /** {@inheritDoc}
     */
    @Override
    public boolean matches(TargetProperties targetContainer, IAtom targetAtom) {
        if (smartQueryAtom != null && qAtom == null) {
            if (!smartQueryAtom.matches(targetAtom)) {
                return false;
            }
        } else {
            if (!matchSymbol(targetAtom)) {
                return false;
            }
            if (!matchMaximumNeighbors(targetContainer, targetAtom)) {
                return false;
            }
        }
        return true;
    }
}
//
//    private String symbol;
//    private int maximumNeighbors;
//    private int minimumNeighbors;
//    private int minimumValence;
//    private int maximumValence;
//
//    public AtomMatcher() {
//        symbol = null;
//        maximumNeighbors = -1;
//        minimumNeighbors = -1;
//        minimumValence = -1;
//        maximumValence = -1;
//    }
//
//    public AtomMatcher(IAtom atom) {
//        this();
//
//        this.symbol = atom.getSymbol();
//        this.minimumNeighbors = atom.getFormalNeighbourCount();
//        Integer hCount = atom.getImplicitHydrogenCount();
//        if (hCount != null) {
//            this.minimumValence = atom.getFormalNeighbourCount() + atom.getImplicitHydrogenCount();
//        } else {
//            this.minimumValence = atom.getFormalNeighbourCount();
//        }
//
////        System.out.println("symbol:" + symbol);
////        System.out.println("minimumNeighbors:" + minimumNeighbors);
////        System.out.println("minimumValence:" + minimumValence);
//    }
//
//    /**
//     *
//     * @param atom
//     * @return
//     */
//
//    public boolean matches(IAtom atom) {
//        if (!matchSymbol(atom)) {
//            return false;
//        }
//
//        if (!matchMaximumNeighbors(atom)) {
//            return false;
//        }
//
//        if (!matchMinimumNeighbors(atom)) {
//            return false;
//        }
//
//        if (!matchMinimumValence(atom)) {
//            return false;
//        }
//
//        if (!matchMaximumValence(atom)) {
//            return false;
//        }
//
//        return true;
//    }
//
//    public void setMinimumValence(int minimum) {
//        if (minimum > maximumValence && maximumValence != -1) {
//            throw new IllegalStateException("Minimum " + minimum + " exceeds maximum");
//        }
//        this.minimumValence = minimum;
//    }
//
//    public void setMaximumValence(int maximum) {
//        if (maximum < minimumValence) {
//            throw new IllegalStateException("Maximum " + maximum + " less than minimum");
//        }
//        this.maximumValence = maximum;
//    }
//
//    public void setMaximumNeighbors(int maximum) {
//        if (maximum < minimumNeighbors) {
//            throw new IllegalStateException("Maximum " + maximum + " exceeds minimum " + minimumNeighbors);
//        }
//
//        this.maximumNeighbors = maximum;
//    }
//
//    public void setMinimumNeighbors(int minimum) {
//        if (minimum > maximumNeighbors && maximumNeighbors != -1) {
//            throw new IllegalStateException("Minimum " + minimum + " exceeds maximum " + maximumNeighbors);
//        }
//
//        this.minimumNeighbors = minimum;
//    }
//
//    public void setSymbol(String symbol) {
//        this.symbol = symbol;
//    }
//
//    private boolean matchSymbol(IAtom atom) {
//        if (symbol == null) {
//            return true;
//        }
//
//        return symbol.equalsIgnoreCase(atom.getSymbol());
//    }
//
//    private boolean matchMaximumNeighbors(IAtom atom) {
//        if (maximumNeighbors == -1) {
//            return true;
//        }
//
//        return atom.getFormalNeighbourCount() <= maximumNeighbors;
//    }
//
//    private boolean matchMinimumNeighbors(IAtom atom) {
//        if (minimumNeighbors == -1) {
//            return true;
//        }
//
//        return atom.getFormalNeighbourCount() >= minimumNeighbors;
//    }
//
//    private boolean matchMinimumValence(IAtom atom) {
//        if (minimumValence == -1) {
//            return true;
//        }
//
//        Integer hCount = atom.getImplicitHydrogenCount();
//        if (hCount != null) {
//            return atom.getFormalNeighbourCount() + hCount >= minimumValence;
//        } else {
//            return atom.getFormalNeighbourCount() >= minimumValence;
//        }
//
//    }
//
//    private boolean matchMaximumValence(IAtom atom) {
//        if (maximumValence == -1) {
//            return true;
//        }
//
//        return atom.getFormalNeighbourCount() + atom.getImplicitHydrogenCount() <= maximumValence;
//    }
//}
//

