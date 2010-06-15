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

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;

/**
 * @cdk.module test-signature
 * @author maclean
 *
 */
public class AtomSignatureTest extends AbstractSignatureTest {
    
    @Test
    public void integerInvariantsTest() {
        IMolecule isotopeChiralMol = builder.newInstance(IMolecule.class);
        isotopeChiralMol.addAtom(builder.newInstance(IAtom.class, "C"));
        
        IAtom s32 = builder.newInstance(IAtom.class, "S");
        s32.setMassNumber(32);
        isotopeChiralMol.addAtom(s32);
        
        IAtom s33 = builder.newInstance(IAtom.class, "S");
        s33.setMassNumber(33);
        isotopeChiralMol.addAtom(s33);
        
        IAtom s34 = builder.newInstance(IAtom.class, "S");
        s34.setMassNumber(34);
        isotopeChiralMol.addAtom(s34);
        
        IAtom s36 = builder.newInstance(IAtom.class, "S");
        s36.setMassNumber(36);
        isotopeChiralMol.addAtom(s36);
        
        
    }
    
    @Test
    public void heightTest() {
        IMolecule benzene = makeBenzene();
        AtomSignature atomSignature = new AtomSignature(0, 1, benzene);
        System.out.println(atomSignature.toCanonicalString());
    }
    
    @Test
    public void cubaneHeightTest() {
        IMolecule cubane = AbstractSignatureTest.makeCubane();
        moleculeIsCarbon3Regular(cubane);
        int height = 3;
        AtomSignature atomSignature = new AtomSignature(0, height, cubane);
        String canonicalString = atomSignature.toCanonicalString();
        System.out.println(canonicalString);
    }
    
    @Test
    public void cuneaneCubaneHeightTest() {
        IMolecule cuneane = AbstractSignatureTest.makeCuneane();
        IMolecule cubane = AbstractSignatureTest.makeCubane();
        int height = 1;
        AtomSignature cuneaneSignature = new AtomSignature(0, height, cuneane);
        AtomSignature cubaneSignature = new AtomSignature(0, height, cubane);
        String cuneaneSigString = cuneaneSignature.toCanonicalString();
        String cubaneSigString = cubaneSignature.toCanonicalString();
        System.out.println(cuneaneSigString);
        System.out.println(cubaneSigString);
        Assert.assertEquals(cuneaneSigString, cubaneSigString);
    }
    
    public void moleculeIsCarbon3Regular(IMolecule molecule) {
        int i = 0;
        for (IAtom a : molecule.atoms()) {
            int count = 0;
            for (IAtom connected : molecule.getConnectedAtomsList(a)) {
                if (connected.getSymbol().equals("C")) {
                    count++;
                }
            }
            Assert.assertEquals("Failed for atom " + i, 3, count);
            i++;
        }
    }
    
    @Test
    public void dodecahedraneHeightTest() {
        IMolecule dodecahedrane = AbstractSignatureTest.makeDodecahedrane();
        moleculeIsCarbon3Regular(dodecahedrane);
        int diameter = 5;
        for (int height = 0; height <= diameter; height++) {
            allEqualAtHeightTest(dodecahedrane, height);
        }
    }
    
    @Test
    public void allHeightsOfASymmetricGraphAreEqualTest() {
        IMolecule cubane = makeCubane();
        int diameter = 3;
        for (int height = 0; height <= diameter; height++) {
            allEqualAtHeightTest(cubane, height);
        }
    }
    
    public void allEqualAtHeightTest(IMolecule molecule, int height) {
        Map<String, Integer> sigfreq = new HashMap<String, Integer>();
        for (int i = 0; i < molecule.getAtomCount(); i++) {
            AtomSignature atomSignature = new AtomSignature(i, height, molecule);
            String canonicalSignature = atomSignature.toCanonicalString();
            if (sigfreq.containsKey(canonicalSignature)) {
                sigfreq.put(canonicalSignature, sigfreq.get(canonicalSignature) + 1);
            } else {
                sigfreq.put(canonicalSignature, 1);
            }
//            System.out.println(i + " " + canonicalSignature);
        }
//        for (String key : sigfreq.keySet()) { System.out.println(key + " " + sigfreq.get(key));}
        Assert.assertEquals(1, sigfreq.keySet().size());
    }
    
    @Test
    public void testNonZeroRootForSubsignature() {
        IMolecule cubane = makeCubane();
        AtomSignature atomSignature = new AtomSignature(1, 2, cubane);
        String canonicalSignature = atomSignature.toCanonicalString();
        System.out.println(canonicalSignature);
    }

}
