/* Copyright (C) 2009-2010  Syed Asad Rahman {asad@ebi.ac.uk}
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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.smsd.algorithm.vflib.builder.VFQueryBuilder;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQuery;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQueryCompiler;
import org.openscience.cdk.smsd.algorithm.vflib.validator.VFAtomMatcher;
import org.openscience.cdk.smsd.algorithm.vflib.validator.VFBondMatcher;

/**
 * This class creates an template for MCS/substructure query.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
@TestClass("org.openscience.cdk.smsd.algorithm.vflib.VFLibTest")
public class TemplateCompiler implements IQueryCompiler {

    private IAtomContainer molecule;

    /**
     *
     * Template Constructer
     */
    public TemplateCompiler() {
    }

    /**
     * Construct query object from the molecule
     * @param molecule
     * @return IQuery
     */
    public static IQuery compile(IAtomContainer molecule) {
        TemplateCompiler compiler = new TemplateCompiler();
        compiler.setMolecule(molecule);
        return compiler.compile();
    }

    /**
     * Set Molecule
     * @param molecule
     */
    public void setMolecule(IAtomContainer molecule) {
        this.molecule = molecule;
    }

    /**
     * Return molecule
     * @return Atom Container
     */
    public IAtomContainer getMolecule() {
        return molecule;
    }

    /** {@inheritDoc}
     */
    @Override
    public IQuery compile() {
        return build(molecule);
    }

    private IQuery build(IAtomContainer queryMolecule) {
        VFQueryBuilder result = new VFQueryBuilder();


        for (int i = 0; i < queryMolecule.getAtomCount(); i++) {
            IAtom atom = queryMolecule.getAtom(i);
            IQueryAtom matcher = createAtomMatcher(atom);
            if (matcher != null) {
                result.addNode(matcher, atom);
            }

        }

        for (int i = 0; i < queryMolecule.getBondCount(); i++) {
            IBond bond = queryMolecule.getBond(i);
            IAtom sourceAtom = bond.getAtom(0);
            IAtom targetAtom = bond.getAtom(1);
            result.connect(result.getNode(sourceAtom), result.getNode(targetAtom), createBondMatcher(bond));
        }

        return result;
    }

    private IQueryAtom createAtomMatcher(IAtom atom) {
        return new VFAtomMatcher(atom);
    }

    private IQueryBond createBondMatcher(IBond bond) {
        return new VFBondMatcher(bond);
    }
}
