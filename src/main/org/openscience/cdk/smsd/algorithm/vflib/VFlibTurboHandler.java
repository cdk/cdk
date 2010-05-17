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
 */
package org.openscience.cdk.smsd.algorithm.vflib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IMapper;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQuery;
import org.openscience.cdk.smsd.algorithm.vflib.query.TemplateCompiler;
import org.openscience.cdk.smsd.helper.MolHandler;
import org.openscience.cdk.smsd.interfaces.AbstractSubGraph;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smsd.algorithm.vflib.map.VFMapper;
import org.openscience.cdk.smsd.interfaces.IMCSBase;

/**
 * This is an ultra fast method to report if query
 * is a substructure for target molecule. If this case is true
 * then it returns only one mapping. 
 * 
 * This is much faster than {@link
 * org.openscience.cdk.smsd.algorithm.vflib.VFlibHandler} class
 * as it only reports first match and backtraces.
 *
 * This class should only be used to report if a query
 * graph is a substructure of the target graph. 
 *
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
@TestClass("org.openscience.cdk.smsd.algorithm.vflib.VFlibTurboHandlerTest")
public class VFlibTurboHandler extends AbstractSubGraph implements IMCSBase {

    private IAtomContainer source;
    private IAtomContainer target;
    private static List<Map<IAtom, IAtom>> allAtomMCS = null;
    private static Map<IAtom, IAtom> atomsMCS = null;
    private static Map<Integer, Integer> firstMCS = null;
    private static List<Map<Integer, Integer>> allMCS = null;

    /**
     * Constructor for the VF Algorithm for substructure search in a fast mode
     * as this returns only one match if any
     */
    public VFlibTurboHandler() {
        allAtomMCS = new ArrayList<Map<IAtom, IAtom>>();
        atomsMCS = new HashMap<IAtom, IAtom>();
        firstMCS = new TreeMap<Integer, Integer>();
        allMCS = new ArrayList<Map<Integer, Integer>>();
    }

    /**
     * {@inheritDoc}
     * @return true if Query/source is a subgraph of Target/target
     * else false
     */
    @Override
    @TestMethod("testIsSubgraph")
    public boolean isSubgraph() {

        IQuery query = TemplateCompiler.compile(source);
        IMapper mapper = new VFMapper(query);
        Map<INode, IAtom> vfLibSolution = mapper.getFirstMap(target);
        
        Map<IAtom, IAtom> atomatomMapping = new HashMap<IAtom, IAtom>();
        TreeMap<Integer, Integer> indexindexMapping = new TreeMap<Integer, Integer>();

        int counter = 0;

        for (Map.Entry<INode, IAtom> mapping : vfLibSolution.entrySet()) {

            IAtom qAtom = query.getAtom(mapping.getKey());
            IAtom tAtom = mapping.getValue();

            Integer qIndex = source.getAtomNumber(qAtom);
            Integer tIndex = target.getAtomNumber(tAtom);

            atomatomMapping.put(qAtom, tAtom);
            indexindexMapping.put(qIndex, tIndex);

        }
        if (!atomatomMapping.isEmpty()) {
            allAtomMCS.add(counter, atomatomMapping);
            allMCS.add(counter, indexindexMapping);
            atomsMCS.putAll(allAtomMCS.get(0));
            firstMCS.putAll(allMCS.get(0));
        }
        return !vfLibSolution.isEmpty() ? true : false;
    }

    /** {@inheritDoc}
     *
     * @param reactant
     * @param product
     */
    @Override
    @TestMethod("testSet_IAtomContainer_IAtomContainer")
    public void set(IAtomContainer reactant, IAtomContainer product) {

        IAtomContainer mol1 = reactant;
        IAtomContainer mol2 = product;

        MolHandler Reactant = new MolHandler(mol1, false);
        MolHandler Product = new MolHandler(mol2, false);
        this.set(Reactant, Product);

    }

    /** {@inheritDoc}
     *
     *
     * @param Reactant
     * @param Product
     */
    @Override
    @TestMethod("testSet_MolHandler_MolHandler")
    public void set(MolHandler Reactant, MolHandler Product) {
        source = Reactant.getMolecule();
        target = Product.getMolecule();
    }

    /** {@inheritDoc}
     *
     * Creates atoms new instance of SearchCliques
     * @param ReactantMolFileName
     * @param ProductMolFileName
     */
    @Override
    @TestMethod("testSet_String_String")
    public void set(String ReactantMolFileName, String ProductMolFileName) {

        String mol1 = ReactantMolFileName;
        String mol2 = ProductMolFileName;

        MolHandler Reactant = new MolHandler(mol1, false);
        MolHandler Product = new MolHandler(mol2, false);
        this.set(Reactant, Product);
    }

    /** {@inheritDoc}
     *
     * @param source
     * @param target
     */
    @TestMethod("testSet_IMolecule_IMolecule")
    public void set(IMolecule source, IMolecule target) throws CDKException {
        MolHandler Reactant = new MolHandler(source, false);
        MolHandler Product = new MolHandler(target, false);
        this.set(Reactant, Product);
    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testGetAllAtomMapping")
    public List<Map<IAtom, IAtom>> getAllAtomMapping() {
        return Collections.unmodifiableList(allAtomMCS);
    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testGetAllMapping")
    public List<Map<Integer, Integer>> getAllMapping() {
        return Collections.unmodifiableList(allMCS);
    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testGetFirstAtomMapping")
    public Map<IAtom, IAtom> getFirstAtomMapping() {
        return Collections.unmodifiableMap(atomsMCS);
    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testGetFirstMapping")
    public Map<Integer, Integer> getFirstMapping() {
        return Collections.unmodifiableMap(firstMCS);
    }
}
