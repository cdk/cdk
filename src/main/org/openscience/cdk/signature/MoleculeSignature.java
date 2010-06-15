/* Copyright (C) 2009-2010 maclean {gilleain.torrance@gmail.com}
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
package org.openscience.cdk.signature;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;

import signature.AbstractGraphSignature;
import signature.AbstractVertexSignature;
import signature.ColoredTree;
import signature.SymmetryClass;

/**
 * A signature for an entire molecule.
 * 
 * @cdk.module signature
 * @author maclean
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.signature.MoleculeSignatureTest")
public class MoleculeSignature extends AbstractGraphSignature {
    
    private IAtomContainer molecule;
    
    /**
     * Creates a signature that represents this molecule.
     * 
     * @param molecule the molecule to convert to a signature
     */
    public MoleculeSignature(IAtomContainer molecule) {
        super();
        this.molecule = molecule;
    }
    
    /**
     * Creates a signature with a maximum height of <code>height</code>
     * for molecule <code>molecule</code>.
     *  
     * @param molecule the molecule to convert to a signature
     * @param height the maximum height of the signature
     */
    public MoleculeSignature(IMolecule molecule, int height) {
        super(height);
        this.molecule = molecule;
    }

    @Override /** {@inheritDoc} */
    public int getVertexCount() {
        return this.molecule.getAtomCount();
    }

    @Override /** {@inheritDoc} */
    public String signatureStringForVertex(int vertexIndex) {
        AtomSignature atomSignature;
        int height = super.getHeight();
        if (height == -1) {
            atomSignature = new AtomSignature(vertexIndex, this.molecule);
        } else {
            atomSignature = 
                new AtomSignature(vertexIndex, height, this.molecule);
        }
        return atomSignature.toCanonicalString();
    }

    @Override /** {@inheritDoc} */
    public String signatureStringForVertex(int vertexIndex, int height) {
        AtomSignature atomSignature = 
            new AtomSignature(vertexIndex, height, this.molecule);
        return atomSignature.toCanonicalString();
    }

    @Override /** {@inheritDoc} */
    public AbstractVertexSignature signatureForVertex(int vertexIndex) {
        return new AtomSignature(vertexIndex, this.molecule);
    }

    /**
     * Calculates the orbits of the atoms of the molecule. 
     * 
     * @return a list of orbits
     */
    public List<Orbit> calculateOrbits() {
        List<Orbit> orbits = new ArrayList<Orbit>();
        List<SymmetryClass> symmetryClasses = super.getSymmetryClasses();
        for (SymmetryClass symmetryClass : symmetryClasses) {
            Orbit orbit = new Orbit(symmetryClass.getSignatureString(), -1);
            for (int atomIndex : symmetryClass) {
                orbit.addAtom(atomIndex);
            }
            orbits.add(orbit);
        }
        return orbits;
    }
    
    /**
     * Builder for molecules (rather, for atom containers) from signature 
     * strings.
     * 
     * @param signatureString the signature string to use
     * @param coBuilder {@link IChemObjectBuilder} to build the returned atom container from
     * @return an atom container
     */
    public static IAtomContainer fromSignatureString(String signatureString,
            IChemObjectBuilder coBuilder) {
        ColoredTree tree = AtomSignature.parse(signatureString);
        MoleculeFromSignatureBuilder builder =
            new MoleculeFromSignatureBuilder(coBuilder);
        builder.makeFromColoredTree(tree);
        return builder.getAtomContainer();
    }

    public String toCanonicalSignatureString(int height) {
        String canonicalSignature = null;
        for (int i = 0; i < getVertexCount(); i++) {
            String signatureForI = signatureStringForVertex(i, height);
            if (canonicalSignature == null || 
                    canonicalSignature.compareTo(signatureForI) < 0) {
                canonicalSignature = signatureForI;
            }
        }
        return canonicalSignature;
    }
}
