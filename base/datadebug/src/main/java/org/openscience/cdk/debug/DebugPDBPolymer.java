/* Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.debug;

import java.util.Collection;

import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.IPDBStructure;
import org.openscience.cdk.protein.data.PDBPolymer;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Debugging data class.
 *
 * @author     Miguel Rojas
 * @cdk.module datadebug
 * @cdk.githash
 */
public class DebugPDBPolymer extends PDBPolymer implements IBioPolymer {

    private static final long serialVersionUID = -8485559594520919850L;

    ILoggingTool              logger           = LoggingToolFactory.createLoggingTool(DebugAtomContainer.class);

    /** {@inheritDoc} */
    @Override
    public Collection<IPDBStructure> getStructures() {
        logger.debug("Getting Structure: ", super.getStructures());
        return super.getStructures();
    }

    /** {@inheritDoc} */
    @Override
    public void addStructure(IPDBStructure structure) {
        logger.debug("Adding Structure: ", structure);
        super.addStructure(structure);
    }

}
