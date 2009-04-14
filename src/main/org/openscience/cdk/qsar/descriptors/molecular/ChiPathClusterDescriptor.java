/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
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
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleArrayResultType;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

import java.util.Iterator;
import java.util.List;

/**
 * Evaluates chi path cluster descriptors.
 * <p/>
 * The code currently evluates the simple and valence chi chain descriptors of orders 3, 4,5 and 6.
 * It utilizes the graph isomorphism code of the CDK to find fragments matching
 * SMILES strings representing the fragments corresponding to each type of chain.
 * <p/>
 * The order of the values returned is
 * <ol>
 * <li>SPC-4 - Simple path cluster, order 4
 * <li>SPC-5 - Simple path cluster, order 5
 * <li>SPC-6 - Simple path cluster, order 6
 * <li>VPC-4 - Valence path cluster, order 4
 * <li>VPC-5 - Valence path cluster, order 5
 * <li>VPC-6 - Valence path cluster, order 6
 * </ol>
 * <p/>
 * <p/>
 * <b>Note</b>: These descriptors are calculated using graph isomorphism to identify
 * the various fragments. As a result calculations may be slow. In addition, recent
 * versions of Molconn-Z use simplified fragment definitions (i.e., rings without
 * branches etc.) whereas these descriptors use the older more complex fragment
 * definitions.
 *
 * @author Rajarshi Guha
 * @cdk.created 2006-11-13
 * @cdk.module qsarmolecular
 * @cdk.svnrev  $Revision$
 * @cdk.set qsar-descriptors
 * @cdk.dictref qsar-descriptors:chiPathCluster
 * @cdk.keyword chi path cluster index
 * @cdk.keyword descriptor
 */
@TestClass("org.openscience.cdk.qsar.descriptors.molecular.ChiPathClusterDescriptorTest")
public class ChiPathClusterDescriptor implements IMolecularDescriptor {
    private LoggingTool logger;
    private SmilesParser sp;

    private static final String[] names = {
            "SPC-4", "SPC-5", "SPC-6",
            "VPC-4", "VPC-5", "VPC-6"
    };

    public ChiPathClusterDescriptor() {
        logger = new LoggingTool(this);
        sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
    }

    @TestMethod("testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#chiPathCluster",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }

    @TestMethod("testGetParameterNames")
    public String[] getParameterNames() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @TestMethod("testGetParameterType_String")
    public Object getParameterType(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @TestMethod("testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @TestMethod("testGetParameters")
    public Object[] getParameters() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return names;
    }


    @TestMethod("testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtomContainer container) {

        IAtomContainer localAtomContainer = AtomContainerManipulator.removeHydrogens(container);
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(container.getBuilder());
        Iterator<IAtom> atoms = localAtomContainer.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom atom = atoms.next();
            IAtomType type;
            try {
                type = matcher.findMatchingAtomType(localAtomContainer, atom);
                AtomTypeManipulator.configure(atom, type);
            } catch (Exception e) {
                return getDummyDescriptorValue(new CDKException("Error in atom typing: " + e.getMessage()));
            }            
        }
        CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(container.getBuilder());
        try {
            hAdder.addImplicitHydrogens(localAtomContainer);
        } catch (Exception e) {
            return getDummyDescriptorValue(new CDKException("Error in hydrogen addition: " + e.getMessage()));
        }

        List subgraph4 = order4(localAtomContainer);
        List subgraph5 = order5(localAtomContainer);
        List subgraph6 = order6(localAtomContainer);

        double order4s = ChiIndexUtils.evalSimpleIndex(localAtomContainer, subgraph4);
        double order5s = ChiIndexUtils.evalSimpleIndex(localAtomContainer, subgraph5);
        double order6s = ChiIndexUtils.evalSimpleIndex(localAtomContainer, subgraph6);

        double order4v, order5v, order6v;
        try {
            order4v = ChiIndexUtils.evalValenceIndex(localAtomContainer, subgraph4);
            order5v = ChiIndexUtils.evalValenceIndex(localAtomContainer, subgraph5);
            order6v = ChiIndexUtils.evalValenceIndex(localAtomContainer, subgraph6);
        } catch (CDKException e) {
            return getDummyDescriptorValue(new CDKException("Error in substructure search: " + e.getMessage()));
        }

        DoubleArrayResult retval = new DoubleArrayResult();
        retval.add(order4s);
        retval.add(order5s);
        retval.add(order6s);

        retval.add(order4v);
        retval.add(order5v);
        retval.add(order6v);

      
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                retval, getDescriptorNames());

    }

    private DescriptorValue getDummyDescriptorValue(Exception e) {
        int ndesc = getDescriptorNames().length;
        DoubleArrayResult results = new DoubleArrayResult(ndesc);
        for (int i = 0; i < ndesc; i++) results.add(Double.NaN);
        return new DescriptorValue(getSpecification(), getParameterNames(),
                getParameters(), results, getDescriptorNames(), e);
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
    @TestMethod("testGetDescriptorResultType")
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleArrayResultType(6);
    }

    private List order4(IAtomContainer atomContainer) {
        QueryAtomContainer[] queries = new QueryAtomContainer[1];
        try {
            queries[0] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CC(C)CC"), false);
        } catch (InvalidSmilesException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ChiIndexUtils.getFragments(atomContainer, queries);
    }

    private List order5(IAtomContainer atomContainer) {
        QueryAtomContainer[] queries = new QueryAtomContainer[3];
        try {
            queries[0] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CC(C)CCC"), false);
            queries[1] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CC(C)(C)CC"), false);
            queries[2] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CCC(C)CC"), false);
        } catch (InvalidSmilesException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ChiIndexUtils.getFragments(atomContainer, queries);
    }

    private List order6(IAtomContainer atomContainer) {
        QueryAtomContainer[] queries = new QueryAtomContainer[5];
        try {
            queries[0] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CC(C)(C)CCC"), false);
            queries[1] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CC(C)C(C)CC"), false);
            queries[2] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CC(C)CC(C)C"), false);
            queries[3] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CC(C)CCCC"), false);
            queries[4] = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CCC(C)CCC"), false);
        } catch (InvalidSmilesException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ChiIndexUtils.getFragments(atomContainer, queries);

    }
}
