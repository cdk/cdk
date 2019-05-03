/* Copyright (C) 2009-2010 maclean {gilleain.torrance@gmail.com}
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
package org.openscience.cdk.signature;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

import signature.AbstractGraphBuilder;

/**
 * Builds a molecule from a signature.
 *
 * @cdk.module signature
 * @author maclean
 * @cdk.githash
 */
public class MoleculeFromSignatureBuilder extends AbstractGraphBuilder {

    /**
     * The chem object builder
     */
    private IChemObjectBuilder builder;

    /**
     * The container that is being constructed
     */
    private IAtomContainer     container;

    /**
     * Uses the chem object builder for making molecules.
     *
     * @param builder a builder for CDK molecules.
     */
    public MoleculeFromSignatureBuilder(IChemObjectBuilder builder) {
        this.builder = builder;
    }

    @Override
    /** {@inheritDoc} */
    public void makeEdge(int vertexIndex1, int vertexIndex2, String vertexSymbol1, String vertexSymbol2,
            String edgeLabel) {
        if (edgeLabel.equals("")) {
            container.addBond(vertexIndex1, vertexIndex2, IBond.Order.SINGLE);
        } else if (edgeLabel.equals("=")) {
            container.addBond(vertexIndex1, vertexIndex2, IBond.Order.DOUBLE);
        } else if (edgeLabel.equals("#")) {
            container.addBond(vertexIndex1, vertexIndex2, IBond.Order.TRIPLE);
        } else if (edgeLabel.equals("p")) {
            IBond bond = builder.newInstance(IBond.class, container.getAtom(vertexIndex1),
                    container.getAtom(vertexIndex2), IBond.Order.SINGLE);
            bond.setFlag(CDKConstants.ISAROMATIC, true);
            container.addBond(bond);
        }
    }

    @Override
    /** {@inheritDoc} */
    public void makeGraph() {
        this.container = this.builder.newInstance(IAtomContainer.class);
    }

    @Override
    /** {@inheritDoc} */
    public void makeVertex(String label) {
        this.container.addAtom(this.builder.newInstance(IAtom.class, label));
    }

    /**
     * Gets the atom container.
     *
     * @return the constructed atom container
     */
    public IAtomContainer getAtomContainer() {
        return this.container;
    }

}
