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

import org.openscience.cdk.interfaces.IAtom;
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
        int atomCounter = 0;
        for (IAtom atom : molecule.atoms()) {
            atomCounter++;
            Resource rdfAtom = model.createResource(
                createIdentifier(model, atom)
            );
            model.add(rdfAtom, RDF.type, CDK.Atom);
            model.add(rdfAtom, CDK.symbol, atom.getSymbol());
            model.add(subject, CDK.hasAtom, rdfAtom);
        }
        return model;
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
            StmtIterator atoms = rdfMol.listProperties(CDK.hasAtom);
            while (atoms.hasNext()) {
                Statement rdfAtom = atoms.nextStatement();
                IAtom atom = builder.newAtom();
                Statement symbol = rdfAtom.getProperty(CDK.symbol);
                if (symbol != null) atom.setSymbol(symbol.getString());
                mol.addAtom(atom);
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
