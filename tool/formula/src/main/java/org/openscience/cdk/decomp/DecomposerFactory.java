/*
 *  This file is part of the SIRIUS library for analyzing MS and MS/MS data
 *
 *  Copyright (C) 2013-2015 Kai Dührkop
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with SIRIUS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openscience.cdk.decomp;

import org.openscience.cdk.interfaces.IIsotope;

import java.util.ArrayList;
import java.util.List;

public final class DecomposerFactory {

    protected List<RangeMassDecomposer<IIsotope>> decomposerCache;
    protected static final int maximalNumberOfCachedDecomposers = 10;

    protected final static DecomposerFactory instance = new DecomposerFactory();

    public static DecomposerFactory getInstance() {
        return instance;
    }

    public DecomposerFactory() {
        this.decomposerCache = new ArrayList<>(maximalNumberOfCachedDecomposers);
    }

    public RangeMassDecomposer<IIsotope> getDecomposerFor(ChemicalAlphabet alphabet) {
        for (RangeMassDecomposer<IIsotope> decomposer : decomposerCache) {
            if (((ChemicalAlphabet)decomposer.getAlphabet()).isCompatible(alphabet)) {
                return decomposer;
            }
        }
        if (decomposerCache.size()>= maximalNumberOfCachedDecomposers) decomposerCache.remove(0);
        final RangeMassDecomposer<IIsotope> decomposer = new RangeMassDecomposer<>(alphabet);
        decomposerCache.add(decomposer);
        return decomposer;
    }

}
