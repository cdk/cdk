/* Copyright (C) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: jchempaint-devel@lists.sourceforge.net
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
package org.openscience.cdk.libio.jena;

import java.util.HashMap;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Helper class that converts a CDK {@link IChemObject} into RDF using a
 * Jena model and the CDK data model ontology.
 *
 * @cdk.module       iordf
 * @cdk.githash
 * @cdk.keyword      Resource Description Framework
 * @cdk.keyword      Jena
 * @cdk.keyword      RDF
 * @cdk.keyword      Web Ontology Language
 * @cdk.keyword      OWL
 */
public class Convertor {

    public static Model molecule2Model(IMolecule molecule) {
        Model model = createCDKModel();
        Resource subject = model.createResource(
            createIdentifier(model, molecule)
        );
        model.add(subject, RDF.type, CDK.Molecule);
        Map<IAtom,Resource> cdkToRDFAtomMap = new HashMap<IAtom, Resource>();
        for (IAtom atom : molecule.atoms()) {
            Resource rdfAtom = model.createResource(
                createIdentifier(model, atom)
            );
            cdkToRDFAtomMap.put(atom, rdfAtom);
            model.add(rdfAtom, RDF.type, CDK.Atom);
            model.add(rdfAtom, CDK.symbol, atom.getSymbol());
            model.add(subject, CDK.hasAtom, rdfAtom);
            serializeChemObjectFields(model, rdfAtom, atom);
        }
        for (IBond bond : molecule.bonds()) {
            Resource rdfBond = model.createResource(
                createIdentifier(model, bond)
            );
            model.add(rdfBond, RDF.type, CDK.Bond);
            for (IAtom atom : bond.atoms()) {
                model.add(rdfBond, CDK.bindsAtom, cdkToRDFAtomMap.get(atom));
            }
            if (bond.getOrder() == IBond.Order.SINGLE) {
                model.add(rdfBond, CDK.hasOrder, CDK.SingleBond);
            } else if (bond.getOrder() == IBond.Order.DOUBLE) {
                model.add(rdfBond, CDK.hasOrder, CDK.DoubleBond);
            } else if (bond.getOrder() == IBond.Order.TRIPLE) {
                model.add(rdfBond, CDK.hasOrder, CDK.TripleBond);
            } else if (bond.getOrder() == IBond.Order.QUADRUPLE) {
                model.add(rdfBond, CDK.hasOrder, CDK.QuadrupleBond);
            }
            model.add(subject, CDK.hasBond, rdfBond);
        }
        return model;
    }

    private static void serializeChemObjectFields(Model model,
            Resource rdfObject, IChemObject object) {
        if (object.getID() != null)
            model.add(rdfObject, CDK.identfier, object.getID());
    }

    private static void deserializeChemObjectFields(Model model,
            Resource rdfObject, IChemObject object) {
        Statement id = rdfObject.getProperty(CDK.identfier);
        if (id != null) object.setID(id.getString());
    }

    private static String createIdentifier(Model model, IChemObject object) {
        StringBuilder result = new StringBuilder();
        result.append("http://example.com/");
        result.append(model.hashCode()).append('/');
        result.append(object.getClass().getSimpleName()).append('/');
        result.append(object.hashCode());
        return result.toString();
    }

    public static IMolecule model2Molecule(Model model,
        IChemObjectBuilder builder) {
        ResIterator mols =
            model.listSubjectsWithProperty(RDF.type, CDK.Molecule);
        IMolecule mol = null;
        if (mols.hasNext()) {
            Resource rdfMol = mols.next();
            mol = builder.newMolecule();
            Map<Resource,IAtom> rdfToCDKAtomMap = new HashMap<Resource,IAtom>();
            StmtIterator atoms = rdfMol.listProperties(CDK.hasAtom);
            while (atoms.hasNext()) {
                Resource rdfAtom = atoms.nextStatement().getResource();
                IAtom atom = builder.newAtom();
                rdfToCDKAtomMap.put(rdfAtom, atom);
                Statement symbol = rdfAtom.getProperty(CDK.symbol);
                if (symbol != null) atom.setSymbol(symbol.getString());
                deserializeChemObjectFields(model, rdfAtom, atom);
                mol.addAtom(atom);
            }
            StmtIterator bonds = rdfMol.listProperties(CDK.hasBond);
            while (bonds.hasNext()) {
                Statement rdfBond = bonds.nextStatement();
                IBond bond = builder.newBond();
                StmtIterator bondAtoms = rdfBond.getResource()
                    .listProperties(CDK.bindsAtom);
                int atomCounter = 0;
                while (bondAtoms.hasNext()) {
                    Statement rdfAtom = bondAtoms.nextStatement();
                    IAtom atom = rdfToCDKAtomMap.get(rdfAtom.getResource());
                    bond.setAtom(atom, atomCounter);
                    atomCounter++;
                }
                Resource order = rdfBond.
                    getProperty(CDK.hasOrder).getResource();
                if (order.equals(CDK.SingleBond)) {
                    bond.setOrder(IBond.Order.SINGLE);
                } else if (order.equals(CDK.DoubleBond)) {
                    bond.setOrder(IBond.Order.DOUBLE);
                } else if (order.equals(CDK.TripleBond)) {
                    bond.setOrder(IBond.Order.TRIPLE);
                } else if (order.equals(CDK.QuadrupleBond)) {
                    bond.setOrder(IBond.Order.QUADRUPLE);
                }
                mol.addBond(bond);
            }
        }
        return mol;
    }

    private static Model createCDKModel() {
        Model model = ModelFactory.createOntologyModel();
        model.setNsPrefix("cdk", "http://cdk.sourceforge.net/model.owl#");
        return model;
    }

}
