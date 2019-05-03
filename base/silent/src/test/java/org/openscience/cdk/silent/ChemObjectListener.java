/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.silent;

import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;

/**
 * Helper class to test the functionality of the {@link ChemObjectListener}.
 *
 * @cdk.module test-silent
 */
public class ChemObjectListener implements IChemObjectListener {

    private boolean                changed;
    private IChemObjectChangeEvent event;

    public ChemObjectListener() {
        changed = false;
        event = null;
    }

    @Override
    public void stateChanged(IChemObjectChangeEvent e) {
        changed = true;
        event = e;
    }

    public void reset() {
        changed = false;
        event = null;
    }

    public boolean getChanged() {
        return changed;
    }

    public IChemObjectChangeEvent getEvent() {
        return event;
    }
}
