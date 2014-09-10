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

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Helper class to provide a Java API to the CDK OWL ontology, following the design of similar namespace
 * classes in the Jena library, like {@link RDF}.
 *
 * @cdk.module iordf
 * @cdk.githash
 */
public class CDK {

    public static final String URI = "http://cdk.sourceforge.net/model.owl#";

    private static final Resource resource(String local) {
        return ResourceFactory.createResource(URI + local);
    }

    private static final Property property(String local) {
        return ResourceFactory.createProperty(URI, local);
    }

    public static final Resource MOLECULE            = resource("Molecule");
    public static final Resource ATOM                = resource("Atom");
    public static final Resource PSEUDOATOM          = resource("PseudoAtom");
    public static final Resource BOND                = resource("Bond");
    public static final Resource CHEMOBJECT          = resource("ChemObject");
    public static final Resource ELEMENT             = resource("Element");
    public static final Resource ATOMTYPE            = resource("AtomType");
    public static final Resource ISOTOPE             = resource("Isotope");

    // IBond.Order
    public static final Resource SINGLEBOND          = resource("SingleBond");
    public static final Resource DOUBLEBOND          = resource("DoubleBond");
    public static final Resource TRIPLEBOND          = resource("TripleBond");
    public static final Resource QUADRUPLEBOND       = resource("QuadrupleBond");

    // IAtomType.Hybridization
    public static final Resource HYBRID_S            = resource("S");
    public static final Resource HYBRID_SP1          = resource("SP1");
    public static final Resource HYBRID_SP2          = resource("SP2");
    public static final Resource HYBRID_SP3          = resource("SP3");
    public static final Resource HYBRID_PLANAR3      = resource("PLANAR3");
    public static final Resource HYBRID_SP3D1        = resource("SP3D1");
    public static final Resource HYBRID_SP3D2        = resource("SP3D2");
    public static final Resource HYBRID_SP3D3        = resource("SP3D3");
    public static final Resource HYBRID_SP3D4        = resource("SP3D4");
    public static final Resource HYBRID_SP3D5        = resource("SP3D5");

    public static final Property HASATOM             = property("hasAtom");
    public static final Property HASBOND             = property("hasBond");
    public static final Property BINDSATOM           = property("bindsAtom");
    public static final Property HASORDER            = property("hasOrder");
    public static final Property SYMBOL              = property("symbol");
    public static final Property HASLABEL            = property("hasLabel");
    public static final Property IDENTIFIER          = property("identifier");
    public static final Property HASATOMICNUMBER     = property("hasAtomicNumber");
    public static final Property HASHYBRIDIZATION    = property("hasHybridization");
    public static final Property HASATOMTYPENAME     = property("hasAtomTypeName");
    public static final Property HASMAXBONDORDER     = property("hasMaxBondOrder");
    public static final Property HASFORMALCHARGE     = property("hasFormalCharge");
    public static final Property HASMASSNUMBER       = property("hasMassNumber");
    public static final Property HASEXACTMASS        = property("hasExactMass");
    public static final Property HASNATURALABUNDANCE = property("hasNaturalAbundance");
    public static final Property HASELECTRONCOUNT    = property("hasElectronCount");

}
