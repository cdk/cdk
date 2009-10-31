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

/**
 * @cdk.module iordf
 */
public class CDK {

    public static final String URI ="http://cdk.sourceforge.net/model.owl#";

    private static final Resource resource(String local) {
        return ResourceFactory.createResource(URI + local);
    }

    private static final Property property(String local) {
        return ResourceFactory.createProperty(URI, local);
    }

    public static final Resource Molecule = resource("Molecule");
    public static final Resource Atom = resource("Atom");
    public static final Resource Bond = resource("Bond");
    public static final Resource ChemObject = resource("ChemObject");
    public static final Resource Element = resource("Element");
    public static final Resource AtomType = resource("AtomType");
    public static final Resource Isotope = resource("Isotope");
    
    // IBond.Order
    public static final Resource SingleBond = resource("SingleBond");
    public static final Resource DoubleBond = resource("DoubleBond");
    public static final Resource TripleBond = resource("TripleBond");
    public static final Resource QuadrupleBond = resource("QuadrupleBond");

    // IAtomType.Hybridization
    public static final Resource S = resource("S");
    public static final Resource SP1 = resource("SP1");
    public static final Resource SP2 = resource("SP2");
    public static final Resource SP3 = resource("SP3");
    public static final Resource PLANAR3 = resource("PLANAR3");
    public static final Resource SP3D1 = resource("SP3D1");
    public static final Resource SP3D2 = resource("SP3D2");
    public static final Resource SP3D3 = resource("SP3D3");
    public static final Resource SP3D4 = resource("SP3D4");
    public static final Resource SP3D5 = resource("SP3D5");
    
    public static final Property hasAtom = property("hasAtom");
    public static final Property hasBond = property("hasBond");
    public static final Property bindsAtom = property("bindsAtom");
    public static final Property hasOrder = property("hasOrder");
    public static final Property symbol = property("symbol");
    public static final Property identfier = property("identifier");
    public static final Property hasAtomicNumber = property("hasAtomicNumber");
    public static final Property hasHybridization = property("hasHybridization");
    public static final Property hasAtomTypeName = property("hasAtomTypeName");
    public static final Property hasMaxBondOrder = property("hasMaxBondOrder");
    public static final Property hasFormalCharge = property("hasFormalCharge");
    public static final Property hasMassNumber = property("hasMassNumber");
    public static final Property hasExactMass = property("hasExactMass");
    public static final Property hasNaturalAbundance =
        property("hasNaturalAbundance");
    public static final Property hasElectronCount =
        property("hasElectronCount");

}