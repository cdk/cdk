/*
 *  $RCSfile$
 *  $Author: rajarshi $
 *  $Date: 2006-11-12 10:58:42 -0400 (Mon, 18 Sep 2006) $
 *  $Revision: 6906 $
 *
 *  Copyright (C) 2004-2007  Rajarshi Guha <rajarshi@users.sourceforge.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.qsar.descriptors.molecular;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.qsar.ChiIndexUtils;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.List;


/**
 * Evaluates chi chain descriptors.
 * <p/>
 * The code currently evluates the simple and valence chi chain descriptors of orders 3, 4,5 and 6.
 * It utilizes the graph isomorphism code of the CDK to find fragments matching
 * SMILES strings representing the fragments corresponding to each type of chain.
 * <p/>
 * The order of the values returned is
 * <ol>
 * <li>SCH-3 - Simple chain, order 3
 * <li>SCH-4 - Simple chain, order 4
 * <li>SCH-5 - Simple chain, order 5
 * <li>SCH-6 - Simple chain, order 6
 * <li>VCH-3 - Valence chain, order 3
 * <li>VCH-4 - Valence chain, order 4
 * <li>VCH-5 - Valence chain, order 5
 * <li>VCH-6 - Valence chain, order 6
 * </ol>
 *
 * <b>Note</b>: These descriptors are calculated using graph isomorphism to identify
 * the various fragments. As a result calculations may be slow. In addition, recent
 * versions of Molconn-Z use simplified fragment definitions (i.e., rings without
 * branches etc.) whereas these descriptors use the older more complex fragment
 * definitions.
 *
 * @author Rajarshi Guha
 * @cdk.created 2006-11-12
 * @cdk.module qsar
 * @cdk.set qsar-descriptors
 * @cdk.dictref qsar-descriptors:chiChain
 * @cdk.keyword chi chain index
 * @cdk.keyword descriptor
 */
public class ChiChainDescriptor implements IMolecularDescriptor {
    private LoggingTool logger;
    private SmilesParser sp;

    public ChiChainDescriptor() {
        logger = new LoggingTool(this);
        sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
    }

    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#chiChain",
                this.getClass().getName(),
                "$Id: ChiChainDescriptor.java 6906 2006-11-12 14:58:42Z rajarshi $",
                "The Chemistry Development Kit");
    }

    public String[] getParameterNames() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object getParameterType(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setParameters(Object[] params) throws CDKException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object[] getParameters() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public DescriptorValue calculate(IAtomContainer container) throws CDKException {

        // we don't make a clone, since removeHydrogens returns a deep copy
        IAtomContainer localAtomContainer = AtomContainerManipulator.removeHydrogens(container);
        HydrogenAdder hadder = new HydrogenAdder();
        hadder.addImplicitHydrogensToSatisfyValency(localAtomContainer);

        List subgraph3 = order3(localAtomContainer);
        List subgraph4 = order4(localAtomContainer);
        List subgraph5 = order5(localAtomContainer);
        List subgraph6 = order6(localAtomContainer);

        double order3s = ChiIndexUtils.evalSimpleIndex(localAtomContainer, subgraph3);
        double order4s = ChiIndexUtils.evalSimpleIndex(localAtomContainer, subgraph4);
        double order5s = ChiIndexUtils.evalSimpleIndex(localAtomContainer, subgraph5);
        double order6s = ChiIndexUtils.evalSimpleIndex(localAtomContainer, subgraph6);

        double order3v = ChiIndexUtils.evalValenceIndex(localAtomContainer, subgraph3);
        double order4v = ChiIndexUtils.evalValenceIndex(localAtomContainer, subgraph4);
        double order5v = ChiIndexUtils.evalValenceIndex(localAtomContainer, subgraph5);
        double order6v = ChiIndexUtils.evalValenceIndex(localAtomContainer, subgraph6);

        DoubleArrayResult retval = new DoubleArrayResult();
        retval.add(order3s);
        retval.add(order4s);
        retval.add(order5s);
        retval.add(order6s);

        retval.add(order3v);
        retval.add(order4v);
        retval.add(order5v);
        retval.add(order6v);

        String[] names = {
                "SCH-3", "SCH-4", "SCH-5", "SCH-6",
                "VCH-3", "VCH-4", "VCH-5", "VCH-6"};
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), retval, names);

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
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleArrayResult();
    }

    private List order3(IAtomContainer atomContainer) {
        QueryAtomContainer[] queries = new QueryAtomContainer[1];
        try {
            queries[0] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("C1CC1"), false);
        } catch (InvalidSmilesException e) {
            e.printStackTrace();
        }
        return ChiIndexUtils.getFragments(atomContainer, queries);
    }

    private List order4(IAtomContainer atomContainer) {
        QueryAtomContainer[] queries = new QueryAtomContainer[2];
        try {
            queries[0] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("C1CCC1"), false);
            queries[1] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CC1CC1"), false);
        } catch (InvalidSmilesException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ChiIndexUtils.getFragments(atomContainer, queries);
    }

    private List order5(IAtomContainer atomContainer) {
        QueryAtomContainer[] queries = new QueryAtomContainer[3];
        try {
            queries[0] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("C1CCCC1"), false);
            queries[1] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CC1CCC1"), false);
            queries[2] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CC1CC1(C)"), false);
        } catch (InvalidSmilesException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ChiIndexUtils.getFragments(atomContainer, queries);
    }

    private List order6(IAtomContainer atomContainer) {
        QueryAtomContainer[] queries = new QueryAtomContainer[9];
        try {
            queries[0] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CC1CCCC1"), false);
            queries[1] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CC1CC(C)C1"), false);
            queries[2] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CC1(C)(CCC1)"), false);
            queries[3] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CCC1CCC1"), false);
            queries[4] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("C1CCCCC1"), false);
            queries[5] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CC1CCC1(C)"), false);
            queries[6] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CC1C(C)C1(C)"), false);
            queries[7] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CCCC1CC1"), false);
            queries[8] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CCC1CC1(C)"), false);
        } catch (InvalidSmilesException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ChiIndexUtils.getFragments(atomContainer, queries);

    }

}
