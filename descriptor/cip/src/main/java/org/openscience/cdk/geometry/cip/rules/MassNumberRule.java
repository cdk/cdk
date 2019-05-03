/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *               2014  Mark B Vine (orcid:0000-0002-7794-0426)
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

import java.io.IOException;

import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.geometry.cip.ILigand;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Compares to {@link ILigand}s based on mass numbers.
 *
 * @cdk.module cip
 * @cdk.githash
 */
class MassNumberRule implements ISequenceSubRule<ILigand> {

    ILoggingTool   logger = LoggingToolFactory.createLoggingTool(MassNumberRule.class);
    IsotopeFactory factory;

    /** {@inheritDoc} */
    @Override
    public int compare(ILigand ligand1, ILigand ligand2) {
        ensureFactory();
        return getMassNumber(ligand1).compareTo(getMassNumber(ligand2));
    }

    private void ensureFactory() {
        if (factory == null) {
            try {
                factory = Isotopes.getInstance();
            } catch (IOException exception) {
                logger.error("Could not load the IsotopeFactory: " + exception.getMessage());
            }
        }
    }

    private Integer getMassNumber(ILigand ligand) {
        Integer massNumber = ligand.getLigandAtom().getMassNumber();
        if (massNumber != null) return massNumber;
        if (factory == null) return 0;
        return factory.getMajorIsotope(ligand.getLigandAtom().getSymbol()).getMassNumber();
    }

}
