/* Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
package org.openscience.cdk.smsd.interfaces;

import java.util.List;
import java.util.Map;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.smsd.tools.MolHandler;

/**
 * Interface that holds basic core interface for all MCS algorithm.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
public interface IMCSBase {

    /**
     * Initialise the query and target molecule.
     *
     * @param source source molecule
     * @param target target molecule
     * @throws CDKException
     *
     */
    public abstract void set(MolHandler source, MolHandler target) throws CDKException;

    /**
     * Initialise the query and target molecule.
     *
     * @param source source molecule
     * @param target target molecule
     * @throws CDKException
     *
     */
    public abstract void set(IQueryAtomContainer source, IAtomContainer target) throws CDKException;

    /**
     * Returns all plausible mappings between query and target molecules.
     * Each map in the list has atom-atom equivalence of the mappings
     * between query and target molecule i.e. map.getKey() for the query
     * and map.getValue() for the target molecule
     * @return All possible MCS atom Mappings
     */
    public abstract List<Map<IAtom, IAtom>> getAllAtomMapping();

    /**
     * Returns all plausible mappings between query and target molecules.
     * Each map in the list has atom-atom equivalence index of the mappings
     * between query and target molecule i.e. map.getKey() for the query
     * and map.getValue() for the target molecule
     * @return All possible MCS Mapping Index
     */
    public abstract List<Map<Integer, Integer>> getAllMapping();

    /**
     * Returns one of the best matches with atoms mapped.
     * @return Best Atom Mapping
     */
    public abstract Map<IAtom, IAtom> getFirstAtomMapping();

    /**
     * Returns one of the best matches with atom indexes mapped.
     * @return Best Mapping Index
     */
    public abstract Map<Integer, Integer> getFirstMapping();
}
