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

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.AbstractMolecularDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.qsar.result.IntegerResultType;
import org.openscience.cdk.ringsearch.RingSearch;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Returns the number of spiro atoms.
 *
 * @author rguha
 * @cdk.module qsarmolecular
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
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#nSpiroAtoms",
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

    /**
     * {@inheritDoc}
     */
    @Override
    public DescriptorValue calculate(IAtomContainer atomContainer) {

        RingSearch rs = new RingSearch(atomContainer);
        Set<Integer> spiroAtomIdx = new HashSet<>();

        // rings identified this way are either spiro or have no common edges
        int[][] isorings = rs.isolated();

        for (int i = 0; i < isorings.length - 1; i++) {
            for (int j = i + 1; j < isorings.length; j++) {
                List<Integer> idxi = new ArrayList<>();
                for (int k = 0; k < isorings[i].length; k++) idxi.add(isorings[i][k]);

                List<Integer> idxj = new ArrayList<>();
                for (int k = 0; k < isorings[j].length; k++) idxj.add(isorings[j][k]);

                // find atom indices common to the two rings
                idxi.retainAll(idxj);
                
                // for spiro rings, should just have 1 atom
                if (idxi.size() == 1)
                    spiroAtomIdx.add(idxi.get(0));
            }
        }

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                new IntegerResult(spiroAtomIdx.size()), getDescriptorNames());
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
