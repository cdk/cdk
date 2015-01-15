/* Copyright (C) 2007-2008  Egon Willighagen <egonw@users.sf.net>
 *                    2014  Mark B Vine (orcid:0000-0002-7794-0426)
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
package org.openscience.cdk.qsar.descriptors.bond;

import java.io.IOException;

import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.AbstractBondDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IBondDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 * Describes the imbalance in atomic number of the IBond.
 *
 * @author      Egon Willighagen
 * @cdk.created 2007-12-29
 * @cdk.module  qsarbond
 * @cdk.githash
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:bondAtomicNumberImbalance
 */
public class AtomicNumberDifferenceDescriptor extends AbstractBondDescriptor implements IBondDescriptor {

    private static IsotopeFactory factory = null;

    private final static String[] NAMES = {"MNDiff"};

    public AtomicNumberDifferenceDescriptor() {
    }

    private void ensureIsotopeFactory() {
        if (factory == null) {
            try {
                factory = Isotopes.getInstance();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#bondAtomicNumberImbalance", this
                .getClass().getName(), "The Chemistry Development Kit");
    }

    @Override
    public void setParameters(Object[] params) throws CDKException {
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public String[] getDescriptorNames() {
        return NAMES;
    }

    @Override
    public DescriptorValue calculate(IBond bond, IAtomContainer ac) {
        ensureIsotopeFactory();
        if (bond.getAtomCount() != 2) {
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(
                    Double.NaN), NAMES, new CDKException("Only 2-center bonds are considered"));
        }

        IAtom[] atoms = BondManipulator.getAtomArray(bond);

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(
                Math.abs(factory.getElement(atoms[0].getSymbol()).getAtomicNumber()
                                 - factory.getElement(atoms[1].getSymbol()).getAtomicNumber())), NAMES);
    }

    @Override
    public String[] getParameterNames() {
        return new String[0];
    }

    @Override
    public Object getParameterType(String name) {
        return null;
    }
}
