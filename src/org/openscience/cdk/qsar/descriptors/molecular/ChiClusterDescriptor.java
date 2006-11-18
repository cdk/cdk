/*
 *  $RCSfile$
 *  $Author: rajarshi $
 *  $Date: 2006-11-12 10:58:42 -0400 (Mon, 18 Sep 2006) $
 *  $Revision: 6906 $
 *
 *  Copyright (C) 2004-2006  Rajarshi Guha <rajarshi@users.sourceforge.net>
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
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.List;

/**
 * Evaluates chi cluster descriptors.
 * <p/>
 * The code currently evluates the simple and valence chi chain descriptors of orders 3, 4,5 and 6.
 * It utilizes the graph isomorphism code of the CDK to find fragments matching
 * SMILES strings representing the fragments corresponding to each type of chain.
 * <p/>
 * The order of the values returned is
 * <ol>
 * <li>SC-3 - Simple cluster, order 3
 * <li>SC-4 - Simple cluster, order 4
 * <li>SC-5 - Simple cluster, order 5
 * <li>SC-6 - Simple cluster, order 6
 * <li>VC-3 - Valence cluster, order 3
 * <li>VC-4 - Valence cluster, order 4
 * <li>VC-5 - Valence cluster, order 5
 * <li>VC-6 - Valence cluster, order 6
 * </ol>
 *
 * @author Rajarshi Guha
 * @cdk.created 2006-11-13
 * @cdk.module qsar
 * @cdk.set qsar-descriptors
 * @cdk.dictref qsar-descriptors:chiCluster
 * @cdk.keyword chi cluster index
 * @cdk.keyword descriptor
 */
public class ChiClusterDescriptor implements IMolecularDescriptor {
    private LoggingTool logger;
    private SmilesParser sp;

    public ChiClusterDescriptor() {
        logger = new LoggingTool(this);
        sp = new SmilesParser();
    }

    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#chiCluster",
                this.getClass().getName(),
                "$Id: ChiClusterDescriptor.java 6906 2006-11-12 14:58:42Z rajarshi $",
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

        // make a copy and remove hydrogens
        IAtomContainer localAtomContainer = null;
        try {
            localAtomContainer = (IAtomContainer) container.clone();
            localAtomContainer = AtomContainerManipulator.removeHydrogens(localAtomContainer);
            HydrogenAdder hadder = new HydrogenAdder();
            hadder.addImplicitHydrogensToSatisfyValency(localAtomContainer);

        } catch (CloneNotSupportedException e) {
            logger.debug("Error occured during clone");
            throw new CDKException("Error occured during clone");
        }

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
                "SC-3", "SC-4",  "SC-5",  "SC-6",
                "VC-3", "VC-4",  "VC-5",  "VC-6"
        };
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), retval, names);

    }

    private List order3(IAtomContainer atomContainer) {
        QueryAtomContainer[] queries = new QueryAtomContainer[1];
        try {
            queries[0] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("C(C)(C)(C)"), false);
        } catch (InvalidSmilesException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ChiIndexUtils.getFragments(atomContainer, queries);
    }

    private List order4(IAtomContainer atomContainer) {
        QueryAtomContainer[] queries = new QueryAtomContainer[1];
        try {
            queries[0] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("C(C)(C)(C)(C)"), false);
        } catch (InvalidSmilesException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ChiIndexUtils.getFragments(atomContainer, queries);
    }

    private List order5(IAtomContainer atomContainer) {
        QueryAtomContainer[] queries = new QueryAtomContainer[1];
        try {
            queries[0] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CC(C)C(C)(C)"), false);
        } catch (InvalidSmilesException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ChiIndexUtils.getFragments(atomContainer, queries);
    }

    private List order6(IAtomContainer atomContainer) {
        QueryAtomContainer[] queries = new QueryAtomContainer[2];
        try {
            queries[0] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("C1(C)C(C)C1(C)"), false);
            queries[1] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CC(C)C(C)(C)C"), false);
        } catch (InvalidSmilesException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ChiIndexUtils.getFragments(atomContainer, queries);
    }

}
