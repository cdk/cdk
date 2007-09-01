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
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Evaluates chi path descriptors.
 * <p/>
 * <p/>
 * It utilizes the graph isomorphism code of the CDK to find fragments matching
 * SMILES strings representing the fragments corresponding to each type of chain.
 * <p/>
 * The order of the values returned is
 * <ol>
 * <li>SP-0, SP-1, ..., SP-7 - Simple path, orders 0 to 7
 * <li>VP-0, VP-1, ..., VP-7 - Valence path, orders 0 to 7
 * </ol>
 * <p/>
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
 * @cdk.dictref qsar-descriptors:chiPath
 * @cdk.keyword chi path index
 * @cdk.keyword descriptor
 */
public class ChiPathDescriptor implements IMolecularDescriptor {
    private LoggingTool logger;
    private SmilesParser sp;

    public ChiPathDescriptor() {
        logger = new LoggingTool(this);
        sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
    }

    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#chiPath",
                this.getClass().getName(),
                "$Id: ChiPathDescriptor.java 6906 2006-11-12 14:58:42Z rajarshi $",
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

        // removeHydrogens does a deep copy, so no need to clone
        IAtomContainer localAtomContainer = AtomContainerManipulator.removeHydrogens(container);
        HydrogenAdder hadder = new HydrogenAdder();
        hadder.addImplicitHydrogensToSatisfyValency(localAtomContainer);

        List subgraph0 = order0(localAtomContainer);
        List subgraph1 = order1(localAtomContainer);
        List subgraph2 = order2(localAtomContainer);
        List subgraph3 = order3(localAtomContainer);
        List subgraph4 = order4(localAtomContainer);
        List subgraph5 = order5(localAtomContainer);
        List subgraph6 = order6(localAtomContainer);
        List subgraph7 = order7(localAtomContainer);

        double order0s = ChiIndexUtils.evalSimpleIndex(localAtomContainer, subgraph0);
        double order1s = ChiIndexUtils.evalSimpleIndex(localAtomContainer, subgraph1);
        double order2s = ChiIndexUtils.evalSimpleIndex(localAtomContainer, subgraph2);
        double order3s = ChiIndexUtils.evalSimpleIndex(localAtomContainer, subgraph3);
        double order4s = ChiIndexUtils.evalSimpleIndex(localAtomContainer, subgraph4);
        double order5s = ChiIndexUtils.evalSimpleIndex(localAtomContainer, subgraph5);
        double order6s = ChiIndexUtils.evalSimpleIndex(localAtomContainer, subgraph6);
        double order7s = ChiIndexUtils.evalSimpleIndex(localAtomContainer, subgraph7);

        double order0v = ChiIndexUtils.evalValenceIndex(localAtomContainer, subgraph0);
        double order1v = ChiIndexUtils.evalValenceIndex(localAtomContainer, subgraph1);
        double order2v = ChiIndexUtils.evalValenceIndex(localAtomContainer, subgraph2);
        double order3v = ChiIndexUtils.evalValenceIndex(localAtomContainer, subgraph3);
        double order4v = ChiIndexUtils.evalValenceIndex(localAtomContainer, subgraph4);
        double order5v = ChiIndexUtils.evalValenceIndex(localAtomContainer, subgraph5);
        double order6v = ChiIndexUtils.evalValenceIndex(localAtomContainer, subgraph6);
        double order7v = ChiIndexUtils.evalValenceIndex(localAtomContainer, subgraph7);

        DoubleArrayResult retval = new DoubleArrayResult();
        retval.add(order0s);
        retval.add(order1s);
        retval.add(order2s);
        retval.add(order3s);
        retval.add(order4s);
        retval.add(order5s);
        retval.add(order6s);
        retval.add(order7s);

        retval.add(order0v);
        retval.add(order1v);
        retval.add(order2v);
        retval.add(order3v);
        retval.add(order4v);
        retval.add(order5v);
        retval.add(order6v);
        retval.add(order7v);

        String[] names = new String[16];
        for (int i = 0; i < 8; i++) {
            names[i] = "SP-" + i;
            names[i + 8] = "VP-" + (i + 8);
        }
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

    private List order0(IAtomContainer atomContainer) {
        List fragments = new ArrayList();
        Iterator atoms = atomContainer.atoms();
        while (atoms.hasNext()) {
            IAtom atom = (IAtom) atoms.next();
            List tmp = new ArrayList();
            tmp.add(new Integer(atomContainer.getAtomNumber(atom)));
            fragments.add(tmp);
        }
        return fragments;
    }

    private List order1(IAtomContainer atomContainer) throws CDKException {
        List fragments = new ArrayList();

        Iterator bonds = atomContainer.bonds();

        while (bonds.hasNext()) {
            IBond bond = (IBond) bonds.next();
            if (bond.getAtomCount() != 2) throw new CDKException("We only consider 2 center bonds");
            List tmp = new ArrayList();
            tmp.add(new Integer(atomContainer.getAtomNumber(bond.getAtom(0))));
            tmp.add(new Integer(atomContainer.getAtomNumber(bond.getAtom(1))));
            fragments.add(tmp);
        }
        return fragments;
    }


    private List order2(IAtomContainer atomContainer) {
        QueryAtomContainer[] queries = new QueryAtomContainer[1];
        try {
            queries[0] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CCC"), false);
        } catch (InvalidSmilesException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ChiIndexUtils.getFragments(atomContainer, queries);
    }

    private List order3(IAtomContainer atomContainer) {
        QueryAtomContainer[] queries = new QueryAtomContainer[1];
        try {
            queries[0] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CCCC"), false);
        } catch (InvalidSmilesException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ChiIndexUtils.getFragments(atomContainer, queries);
    }

    private List order4(IAtomContainer atomContainer) {
        QueryAtomContainer[] queries = new QueryAtomContainer[1];
        try {
            queries[0] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CCCCC"), false);
        } catch (InvalidSmilesException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ChiIndexUtils.getFragments(atomContainer, queries);
    }

    private List order5(IAtomContainer atomContainer) {
        QueryAtomContainer[] queries = new QueryAtomContainer[1];
        try {
            queries[0] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CCCCCC"), false);
        } catch (InvalidSmilesException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ChiIndexUtils.getFragments(atomContainer, queries);
    }

    private List order6(IAtomContainer atomContainer) {
        QueryAtomContainer[] queries = new QueryAtomContainer[1];
        try {
            queries[0] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CCCCCCC"), false);
        } catch (InvalidSmilesException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ChiIndexUtils.getFragments(atomContainer, queries);
    }

    private List order7(IAtomContainer atomContainer) {
        QueryAtomContainer[] queries = new QueryAtomContainer[1];
        try {
            queries[0] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CCCCCCCC"), false);
        } catch (InvalidSmilesException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ChiIndexUtils.getFragments(atomContainer, queries);
    }

}
