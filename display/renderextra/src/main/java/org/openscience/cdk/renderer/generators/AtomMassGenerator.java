/*  Copyright (C) 2008  Arvid Berg <goglepox@users.sf.net>
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
package org.openscience.cdk.renderer.generators;

import java.io.IOException;

import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * {@link IGenerator} that can render mass number information of atoms.
 *
 * @cdk.module renderextra
 * @cdk.githash
 */
public class AtomMassGenerator extends BasicAtomGenerator {

    private ILoggingTool logger = LoggingToolFactory.createLoggingTool(AtomMassGenerator.class);

    /**
     * Returns true if the mass number of this element is set and not
     * equal the mass number of the most abundant isotope of this element.
     *
     * @param  atom      {@link IAtom} which is being examined
     * @param  container {@link IAtomContainer} of which the atom is part
     * @param  model     the {@link RendererModel}
     * @return true, when mass number information should be depicted
     */
    @Override
    public boolean showCarbon(IAtom atom, IAtomContainer container, RendererModel model) {

        Integer massNumber = atom.getMassNumber();
        if (massNumber != null) {
            try {
                Integer expectedMassNumber = Isotopes.getInstance().getMajorIsotope(atom.getSymbol()).getMassNumber();
                if (massNumber != expectedMassNumber) return true;
            } catch (IOException e) {
                logger.warn(e);
            }
        }
        return super.showCarbon(atom, container, model);
    }
}
