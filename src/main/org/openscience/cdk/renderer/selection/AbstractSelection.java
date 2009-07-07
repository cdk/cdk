/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2009 Arvid Berg <goglepox@users.sf.net>
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
package org.openscience.cdk.renderer.selection;

import java.util.Collection;
import java.util.Collections;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;

/**
 * @author Arvid
 * @cdk.module renderbasic
 */
public abstract class AbstractSelection implements IChemObjectSelection {

    public static IChemObjectSelection EMPTY_SELECTION = new AbstractSelection() {

		public IAtomContainer getConnectedAtomContainer() {
			return null;
		}

		public boolean isFilled() {

			return false;
		}

		public boolean contains(IChemObject obj) {
			return false;
		}

		public <E extends IChemObject> Collection<E> elements(Class<E> clazz) {

			return Collections.emptySet();
		}


	};

	public void select(IChemModel chemModel) {
		// TODO Auto-generated method stub

	}

	protected void addToAtomContainer(IAtomContainer ac, IChemObject item) {

		if (item instanceof IAtomContainer) {
			ac.add((IAtomContainer) item);
		} else if (item instanceof IAtom) {
			ac.addAtom((IAtom) item);
		} else if (item instanceof IBond) {
			ac.addBond((IBond) item);
		}
	}

}
