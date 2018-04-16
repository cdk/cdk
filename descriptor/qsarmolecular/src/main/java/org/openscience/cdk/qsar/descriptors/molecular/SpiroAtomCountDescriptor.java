/* Copyright (C) 2018  Rajarshi Guha <rajarshi.guha@gmail.com>
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
package org.openscience.cdk.qsar.descriptors.molecular;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.AbstractMolecularDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.qsar.result.IntegerResultType;

import java.util.ArrayList;
import java.util.List;


/**
 * Returns the number of spiro atoms.
 *
 * @author rguha
 * @cdk.dictref qsar-descriptors:nSpiroAtom
 */
public class SpiroAtomCountDescriptor extends AbstractMolecularDescriptor implements IMolecularDescriptor {

    private final static String[] NAMES = {"nSpiroAtoms"};

    /**
     * Creates a new {@link SpiroAtomCountDescriptor}.
     */
    public SpiroAtomCountDescriptor() {
    }

    @Override
    public void initialise(IChemObjectBuilder builder) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#nSpiroAtom",
                this.getClass().getName(), "The Chemistry Development Kit");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParameters(Object[] params) throws CDKException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getParameters() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getDescriptorNames() {
        return NAMES;
    }

    private static void traverseRings(IAtomContainer mol, IAtom atom, IBond prev) {
        atom.setFlag(CDKConstants.VISITED, true);
        prev.setFlag(CDKConstants.VISITED, true);
        for (IBond bond : mol.getConnectedBondsList(atom)) {
            IAtom nbr = bond.getOther(atom);
            if (!nbr.getFlag(CDKConstants.VISITED))
                traverseRings(mol, nbr, bond);
            else
                bond.setFlag(CDKConstants.VISITED, true);
        }
    }

    private static int getSpiroDegree(IAtomContainer mol, IAtom atom) {
        if (!atom.isInRing())
            return 0;
        List<IBond> rbonds = new ArrayList<>(4);
        for (IBond bond : mol.getConnectedBondsList(atom)) {
            if (bond.isInRing())
                rbonds.add(bond);
        }
        if (rbonds.size() < 4)
            return 0;
        int degree = 0;
        // clear flags
        for (IBond b : mol.bonds())
            b.setFlag(CDKConstants.VISITED, false);
        for (IAtom a : mol.atoms())
            a.setFlag(CDKConstants.VISITED, false);
        // visit rings
        atom.setFlag(CDKConstants.VISITED, true);
        for (IBond rbond : rbonds) {
            if (!rbond.getFlag(CDKConstants.VISITED)) {
                traverseRings(mol, rbond.getOther(atom), rbond);
                degree++;
            }
        }
        return degree < 2 ? 0 : degree;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DescriptorValue calculate(IAtomContainer atomContainer) {
        int nSpiro = 0;

        try {
            IAtomContainer local = atomContainer.clone();
            Cycles.markRingAtomsAndBonds(local);
            for (IAtom atom : local.atoms()) {
                if (getSpiroDegree(local, atom) != 0)
                    nSpiro++;
            }
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    new IntegerResult(nSpiro), getDescriptorNames());
        } catch (CloneNotSupportedException e) {
            return getDummyDescriptorValue(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDescriptorResult getDescriptorResultType() {
        return new IntegerResultType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getParameterNames() {
        return new String[0];

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getParameterType(String name) {
        return null;
    }

    private DescriptorValue getDummyDescriptorValue(Exception exception) {
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new IntegerResult(-1),
                getDescriptorNames(), exception);
    }
}
