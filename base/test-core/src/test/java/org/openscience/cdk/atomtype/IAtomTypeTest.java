/* Copyright (C) 2013  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.atomtype;

import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * Interface for {@link IAtomTypeMatcher} unit tests. It provides various methods
 * to allow such unit test classes to extend {@link AbstractAtomTypeTest} and
 * take advantage of the functionality that abstract class provides.
 *
 * @cdk.githash
 * @cdk.module test-core
 */
public interface IAtomTypeTest {

    /**
     * Returns a name for the atom type scheme being tested.
     *
     * @return a String of the name.
     */
    public String getAtomTypeListName();

    /**
     * Returns an {@link AtomTypeFactory} instance for the atom type scheme
     * being tested. It is used to provide a list of atom types the scheme
     * defines.
     *
     * @return an {@link AtomTypeFactory} instance
     */
    public AtomTypeFactory getFactory();

    /**
     * The {@link IAtomTypeMatcher} being tested.
     *
     * @param builder the {@link IChemObjectBuilder} used to create atom types.
     * @return return an {@link IAtomTypeMatcher} instance
     */
    public IAtomTypeMatcher getAtomTypeMatcher(IChemObjectBuilder builder);

}
