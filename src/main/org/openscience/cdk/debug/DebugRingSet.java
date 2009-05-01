/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2005-2007  Egon Willighagen <egonw@users.sf.net>
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

import org.openscience.cdk.RingSet;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Debugging data class.
 * 
 * @author     egonw
 * @cdk.module datadebug
 * @cdk.svnrev  $Revision$
 */
public class DebugRingSet extends RingSet
    implements IRingSet {

    private static final long serialVersionUID = -4144201128508373352L;
    
    LoggingTool logger = new LoggingTool(DebugRingSet.class);

	public DebugRingSet() {
		super();
	}
	
	public IRingSet getRings(IBond bond) {
		logger.debug("Getting rings for bond: ", bond);
		return super.getRings(bond);
	}

	public IRingSet getRings(IAtom atom) {
		logger.debug("Getting rings for atom: ", atom);
		return super.getRings(atom);
	}

	public IRingSet getConnectedRings(IRing ring) {
		logger.debug("Getting connected rings for ring: ", ring);
		return super.getConnectedRings(ring);
	}

	public void add(IRingSet ringSet) {
		logger.debug("Adding ring set: ", ringSet);
		super.add(ringSet);
	}

	public boolean contains(IAtom atom) {
		logger.debug("Contains atom: ", super.contains(atom));
		return super.contains(atom);
	}

}
