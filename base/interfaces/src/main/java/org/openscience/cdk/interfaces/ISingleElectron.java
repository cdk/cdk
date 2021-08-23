/* Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.interfaces;

/**
 * A Single Electron is an orbital which is occupied by only one electron. A radical in CDK is
 * represented by an AtomContainer that contains an Atom and a SingleElectron type
 * ElectronContainer:
 *
 * <pre>
 *   AtomContainer radical = new AtomContainer();
 *   Atom carbon = new Atom("C");
 *   carbon.setImplicitHydrogens(3);
 *   radical.addElectronContainer(new SingleElectron(carbon));
 * </pre>
 *
 * @cdk.module interfaces
 * @cdk.githash
 * @cdk.keyword radical
 * @cdk.keyword electron, unpaired
 */
public interface ISingleElectron extends IElectronContainer {

    /**
     * Returns the associated Atom.
     *
     * @return the associated Atom.
     * @see #setAtom
     */
    public IAtom getAtom();

    /**
     * Sets the associated Atom.
     *
     * @param atom the Atom this SingleElectron will be associated with
     * @see #getAtom
     */
    public void setAtom(IAtom atom);

    /**
     * Returns true if the given atom participates in this SingleElectron.
     *
     * @param atom The atom to be tested if it participates in this bond
     * @return true if this SingleElectron is associated with the atom
     */
    public boolean contains(IAtom atom);
}
