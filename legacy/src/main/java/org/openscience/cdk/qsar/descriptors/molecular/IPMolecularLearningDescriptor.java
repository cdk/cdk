/* Copyright (C) 2006-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.molecular;

import java.util.ArrayList;
import java.util.Iterator;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.AbstractMolecularDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.bond.IPBondLearningDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.DoubleResultType;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.tools.IonizationPotentialTool;
import org.openscience.cdk.tools.LonePairElectronChecker;

/**
 *  This class returns the ionization potential of a molecule. Up to now is
 *  only possible for atomContainers which contain; see IPAtomicDescriptor and
 *  IPBondDescriptor.
 *
 * The descriptor assumes that explicit hydrogens have been added to the molecule
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>addlp</td>
 *     <td>true</td>
 *     <td>If true lone pairs are added to the molecule</td>
 *   </tr>
 * </table>
 *
 * @author           Miguel Rojas
 * @cdk.created      2006-05-26
 * @cdk.module       qsarionpot
 * @cdk.githash
 * @cdk.set          qsar-descriptors
 * @cdk.dictref      qsar-descriptors:ionizationPotential
 * @cdk.keyword      ionization potential
 *
 * @see IPAtomicHOSEDescriptor
 * @see IPBondLearningDescriptor
 */
@Deprecated
public class IPMolecularLearningDescriptor extends AbstractMolecularDescriptor implements IMolecularDescriptor {

    private boolean               addlp = true;
    private static final String[] names = {"MolIP"};

    /**
     *  Constructor for the IPMolecularLearningDescriptor object
     */
    public IPMolecularLearningDescriptor() {}

    /**
     *  Gets the specification attribute of the IPMolecularLearningDescriptor object
     *
     *@return    The specification value
     */
    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification("http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#ip",
                this.getClass().getName(), "The Chemistry Development Kit");
    }

    /**
     *  Sets the parameters attribute of the IPMolecularLearningDescriptor object
     *
     *@param  params            The new parameters value
     *@exception  CDKException  Description of the Exception
     */
    @Override
    public void setParameters(Object[] params) throws CDKException {
        if (params.length != 1) throw new CDKException("One parameter expected");
        if (!(params[0] instanceof Boolean)) throw new CDKException("Boolean parameter expected");
        addlp = (Boolean) params[0];
    }

    /**
     * Gets the parameters attribute of the IPMolecularLearningDescriptor object
     *
     * @return The parameters value
     */
    @Override
    public Object[] getParameters() {
        return new Object[]{addlp};
    }

    @Override
    public String[] getDescriptorNames() {
        return names;
    }

    /**
     *  It calculates the first ionization energy of a molecule.
     *
     *@param  atomContainer     AtomContainer
     *@return                   The first ionization energy
     */
    @Override
    public DescriptorValue calculate(IAtomContainer atomContainer) {
        IAtomContainer local;
        if (addlp) {
            try {
                local = (IAtomContainer) atomContainer.clone();
                LonePairElectronChecker lpcheck = new LonePairElectronChecker();
                lpcheck.saturate(local);
            } catch (CloneNotSupportedException e) {
                return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(
                        Double.NaN), getDescriptorNames(), e);
            } catch (CDKException e) {
                return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(
                        Double.NaN), getDescriptorNames(), e);
            }

        } else
            local = atomContainer;

        DoubleResult value;
        try {
            value = new DoubleResult(((DoubleArrayResult) calculatePlus(local).getValue()).get(0));
        } catch (CDKException e) {
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(
                    Double.NaN), getDescriptorNames(), e);
        }
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), value,
                getDescriptorNames());
    }

    /**
     *  It calculates the 1,2,.. ionization energies of a molecule.
     *
     *@param  container         AtomContainer
     *@return                   The 1, 2, .. ionization energies
     *@exception  CDKException  Possible Exceptions
     */
    public DescriptorValue calculatePlus(IAtomContainer container) throws CDKException {

        ArrayList<Double> dar = new ArrayList<Double>();
        for (Iterator<IAtom> itA = container.atoms().iterator(); itA.hasNext();) {
            IAtom atom = itA.next();
            double value = IonizationPotentialTool.predictIP(container, atom);
            if (value != 0) dar.add(value);
        }
        for (Iterator<IBond> itB = container.bonds().iterator(); itB.hasNext();) {
            IBond bond = itB.next();
            if (bond.getOrder() == IBond.Order.DOUBLE & bond.getAtom(0).getSymbol().equals("C")
                    & bond.getAtom(1).getSymbol().equals("C")) {
                double value = IonizationPotentialTool.predictIP(container, bond);
                if (value != 0) dar.add(value);

            }
        }

        DoubleArrayResult results = arrangingEnergy(dar);

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), results,
                getDescriptorNames(), null);

    }

    /**
     * put in increasing order the ArrayList
     *
     * @param array The ArrayList to order
     * @return      The DoubleArrayResult ordered
     */
    private DoubleArrayResult arrangingEnergy(ArrayList<Double> array) {

        DoubleArrayResult results = new DoubleArrayResult();
        int count = array.size();
        for (int i = 0; i < count; i++) {
            double min = array.get(0);
            int pos = 0;
            for (int j = 0; j < array.size(); j++) {
                double value = array.get(j);
                if (value < min) {
                    min = value;
                    pos = j;
                }
            }
            array.remove(pos);
            results.add(min);
        }
        return results;
    }

    /**
     * Returns the specific type of the DescriptorResult object.
     * <p/>
     * The return value from this method really indicates what type of result will
     * be obtained from the {@link org.openscience.cdk.qsar.DescriptorValue} object. Note that the same result
     * can be achieved by interrogating the {@link org.openscience.cdk.qsar.DescriptorValue} object; this method
     * allows you to do the same thing, without actually calculating the descriptor.
     *
     * @return an object that implements the {@link org.openscience.cdk.qsar.result.IDescriptorResult} interface indicating
     *         the actual type of values returned by the descriptor in the {@link org.openscience.cdk.qsar.DescriptorValue} object
     */
    @Override
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleResultType();
    }

    /**
     *  Gets the parameterNames attribute of the IPMolecularLearningDescriptor object
     *
     *@return    The parameterNames value
     */
    @Override
    public String[] getParameterNames() {
        return new String[]{"addlp"};
    }

    /**
     *  Gets the parameterType attribute of the IPMolecularLearningDescriptor object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    @Override
    public Object getParameterType(String name) {
        return addlp;
    }

}
