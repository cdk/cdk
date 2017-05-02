/* Copyright (C) 2009-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
 *
 * MX Cheminformatics Tools for Java
 *
 * Copyright (c) 2007-2009 Metamolecular, LLC
 *
 * http://metamolecular.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.openscience.cdk.smsd.algorithm.vflib.query;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.smsd.algorithm.matchers.DefaultVFAtomMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.DefaultVFBondMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.VFAtomMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.VFBondMatcher;
import org.openscience.cdk.smsd.algorithm.vflib.builder.VFQueryBuilder;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQuery;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQueryCompiler;

/**
 * This class creates an template for MCS/substructure query.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class QueryCompiler implements IQueryCompiler {

    private IAtomContainer      molecule         = null;
    private IQueryAtomContainer queryMolecule    = null;
    private boolean             shouldMatchBonds = true;

    /**
     * Construct query object from the molecule
     * @param molecule
     * @param shouldMatchBonds
     */
    public QueryCompiler(IAtomContainer molecule, boolean shouldMatchBonds) {
        this.setMolecule(molecule);
        this.setBondMatchFlag(shouldMatchBonds);
    }

    /**
     * Construct query object from the molecule
     * @param molecule
     */
    public QueryCompiler(IQueryAtomContainer molecule) {
        this.setQueryMolecule(molecule);
    }

    /**
     * Set Molecule
     * @param molecule
     */
    private void setMolecule(IAtomContainer molecule) {
        this.molecule = molecule;
    }

    /**
     * Set Molecule
     * @param molecule
     */
    private void setQueryMolecule(IQueryAtomContainer molecule) {
        this.queryMolecule = molecule;
    }

    /**
     * Return molecule
     * @return Atom Container
     */
    private IAtomContainer getMolecule() {
        return queryMolecule == null ? molecule : queryMolecule;
    }

    /** {@inheritDoc} */
    @Override
    public IQuery compile() {
        return this.queryMolecule == null ? build(molecule) : build(queryMolecule);
    }

    private IQuery build(IAtomContainer queryMolecule) {
        VFQueryBuilder result = new VFQueryBuilder();
        for (IAtom atom : queryMolecule.atoms()) {
            VFAtomMatcher matcher = createAtomMatcher(queryMolecule, atom);
            if (matcher != null) {
                result.addNode(matcher, atom);
            }
        }
        for (int i = 0; i < queryMolecule.getBondCount(); i++) {
            IBond bond = queryMolecule.getBond(i);
            IAtom atomI = bond.getBegin();
            IAtom atomJ = bond.getEnd();
            result.connect(result.getNode(atomI), result.getNode(atomJ), createBondMatcher(queryMolecule, bond));
        }
        return result;
    }

    private IQuery build(IQueryAtomContainer queryMolecule) {
        VFQueryBuilder result = new VFQueryBuilder();
        for (IAtom atoms : queryMolecule.atoms()) {
            IQueryAtom atom = (IQueryAtom) atoms;
            VFAtomMatcher matcher = createAtomMatcher(atom, queryMolecule);
            if (matcher != null) {
                result.addNode(matcher, atom);
            }
        }
        for (int i = 0; i < queryMolecule.getBondCount(); i++) {
            IBond bond = queryMolecule.getBond(i);
            IQueryAtom atomI = (IQueryAtom) bond.getBegin();
            IQueryAtom atomJ = (IQueryAtom) bond.getEnd();
            result.connect(result.getNode(atomI), result.getNode(atomJ), createBondMatcher((IQueryBond) bond));
        }
        return result;
    }

    private VFAtomMatcher createAtomMatcher(IAtomContainer mol, IAtom atom) {
        return new DefaultVFAtomMatcher(mol, atom, isBondMatchFlag());
    }

    private VFBondMatcher createBondMatcher(IAtomContainer mol, IBond bond) {
        return new DefaultVFBondMatcher(mol, bond, isBondMatchFlag());
    }

    private VFAtomMatcher createAtomMatcher(IQueryAtom atom, IQueryAtomContainer container) {
        return new DefaultVFAtomMatcher(atom, container);
    }

    private VFBondMatcher createBondMatcher(IQueryBond bond) {
        return new DefaultVFBondMatcher(bond);
    }

    /**
     * @return the shouldMatchBonds
     */
    private boolean isBondMatchFlag() {
        return shouldMatchBonds;
    }

    /**
     * @param shouldMatchBonds the shouldMatchBonds to set
     */
    private void setBondMatchFlag(boolean shouldMatchBonds) {
        this.shouldMatchBonds = shouldMatchBonds;
    }
}
