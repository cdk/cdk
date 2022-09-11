/*
 * MX - Essential Cheminformatics
 *
 * Copyright (c) 2007-2009 Metamolecular, LLC
 *
 * http://metamolecular.com/mx
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.

 *Copyright (C) 2009-2010 Syed Asad Rahman <asad@ebi.ac.uk>
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

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IMapper;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.algorithm.vflib.map.VFMapper;

/**
 *
 * @author Richard L. Apodaca &lt;rapodaca at metamolecular.com&gt;
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 *
 * @cdk.module test-smsd
 * @cdk.require java1.6+
 */
class VFMapperTest {

    private IAtomContainer hexane;
    private IAtomContainer benzene;
    private IAtomContainer pyridine;
    private IAtomContainer toluene4;
    private IAtomContainer pyridazine;
    private IAtomContainer naphthalene;
    private IAtomContainer chlorobenzene;
    private IAtomContainer chloroisoquinoline4;
    private IAtomContainer toluene;
    private IAtomContainer phenol;
    private IAtomContainer acetone;
    private IAtomContainer propane;
    private IAtomContainer cyclopropane;

    public VFMapperTest() {

    }

    @BeforeEach
    void setUp() throws Exception {
        hexane = Molecules.createHexane();
        benzene = Molecules.createBenzene();
        pyridine = Molecules.createPyridine();
        toluene4 = Molecules.create4Toluene();
        pyridazine = Molecules.createPyridazine();
        chloroisoquinoline4 = Molecules.createChloroisoquinoline4();
        chlorobenzene = Molecules.createChlorobenzene();
        naphthalene = Molecules.createNaphthalene();
        toluene = Molecules.createToluene();
        phenol = Molecules.createPhenol();
        acetone = Molecules.createAcetone();
        propane = Molecules.createPropane();
        cyclopropane = Molecules.createCyclopropane();
    }

    @Test
    void testItShouldMatchHexaneToHexane() {
        IMapper mapper = new VFMapper(hexane, true);

        Assertions.assertTrue(mapper.hasMap(hexane));
    }

    @Test
    void testItShouldMatchHexaneToHexaneWhenUsingMolecule() {
        IMapper mapper = new VFMapper(hexane, true);

        Assertions.assertTrue(mapper.hasMap(hexane));
    }

    @Test
    void testItShouldMatchBenzeneToBenzene() {
        IMapper mapper = new VFMapper(benzene, true);

        Assertions.assertTrue(mapper.hasMap(benzene));
    }

    @Test
    void testItShouldNotMatchHexaneToBenzene() {
        IMapper mapper = new VFMapper(hexane, true);

        Assertions.assertFalse(mapper.hasMap(benzene));
    }

    @Test
    void testItShouldNotMatchPyridazineToNaphthalene() {
        IMapper mapper = new VFMapper(pyridazine, true);

        Assertions.assertFalse(mapper.hasMap(naphthalene));
    }

    @Test
    void testItShouldNotMatchChlorobenzeneTo4ChloroIsoquinoline() {
        IMapper mapper = new VFMapper(chlorobenzene, true);

        Assertions.assertFalse(mapper.hasMap(chloroisoquinoline4));
    }

    @Test
    void testItShouldNotMatchBenzeneToPyridine() {
        IMapper mapper = new VFMapper(benzene, true);

        Assertions.assertFalse(mapper.hasMap(pyridine));

        mapper = new VFMapper(pyridine, true);

        Assertions.assertFalse(mapper.hasMap(benzene));
    }

    @Test
    void testItShouldNotMatchTolueneToBenzene() {
        IMapper mapper = new VFMapper(toluene, true);

        Assertions.assertFalse(mapper.hasMap(benzene));
    }

    @Test
    void testItShouldMatchAcetoneToAcetone() {
        IMapper mapper = new VFMapper(acetone, true);

        Assertions.assertTrue(mapper.hasMap(acetone));
    }

    @Test
    void testItShouldMatchPropaneToCyclopropane() {
        IMapper mapper = new VFMapper(propane, true);

        Assertions.assertTrue(mapper.hasMap(cyclopropane));
    }

    @Test
    void testItShouldFindTwoMapsFromHexaneToHexane() {
        IMapper mapper = new VFMapper(hexane, true);

        List<Map<INode, IAtom>> maps = mapper.getMaps(hexane);

        Assertions.assertEquals(2, maps.size());
    }

    @Test
    void testItShouldNotMatchTolueneToPhenol() {
        IMapper mapper = new VFMapper(toluene, true);

        Assertions.assertFalse(mapper.hasMap(phenol));
    }

    @Test
    void testItShouldMapSixAtomsOfBenzeneOntoBenzene() {
        IMapper mapper = new VFMapper(benzene, true);
        Map<INode, IAtom> map = mapper.getFirstMap(benzene);

        Assertions.assertEquals(6, map.size());
    }

    @Test
    void testItShouldCountTwelveMapsForBenzeneOntoBenzene() {
        IMapper mapper = new VFMapper(benzene, true);

        Assertions.assertEquals(12, mapper.countMaps(benzene));
    }

    @Test
    void testItShouldCountTwoMapsForTolueneOntoToluene() {
        IMapper mapper = new VFMapper(toluene, true);

        Assertions.assertEquals(2, mapper.countMaps(toluene));
    }

    @Test
    void testItShouldFindTwelveMapsForBenzeneOntoBenzene() {
        IMapper mapper = new VFMapper(benzene, true);
        List<Map<INode, IAtom>> maps = mapper.getMaps(benzene);

        Assertions.assertEquals(12, maps.size());
    }

    @Test
    void testItShouldFindTwentyFourMapsForBenzeneOntoNaphthalene() {
        IMapper mapper = new VFMapper(benzene, true);
        List<Map<INode, IAtom>> maps = mapper.getMaps(naphthalene);

        Assertions.assertEquals(24, maps.size());
    }

    @Test
    void testItShouldFindAMapForEquivalentFormsOfToluene() {
        IMapper mapper = new VFMapper(toluene, true);
        Map<INode, IAtom> map = mapper.getFirstMap(toluene4);

        Assertions.assertEquals(7, map.size());
    }

    @Test
    void testItShouldFindTwoMapsForEquivalentFormsOfToluene() {
        IMapper mapper = new VFMapper(toluene, true);
        List<Map<INode, IAtom>> maps = mapper.getMaps(toluene4);

        Assertions.assertEquals(2, maps.size());
    }
    //    @Test
    //    public void testItMapsBlockedPropaneOntoPropane() throws CDKException {
    //        IAtomContainer blockedPropane = Molecules.createPropane();
    //        IAtom atom = blockedPropane.getBuilder().newInstance(IAtom.class, "H");
    //        blockedPropane.addAtom(atom);
    //        IBond bond = blockedPropane.getBuilder().newInstance(IBond.class, atom, blockedPropane.getAtom(1), IBond.Order.SINGLE);
    //
    //        blockedPropane.addBond(bond);
    //
    //        IMapper mapper = new VFMapper(blockedPropane, true);
    //
    //        assertTrue(mapper.hasMap(propane));
    //    }
    //    public void testItMapsBlockedBenzaldehydeOntoBenzaldehyde() {
    //        Molecule blockedBenzaldehyde = this.createBlockedBenzaldehyde();
    //        IMapper mapper = new VFMapper(blockedBenzaldehyde, true);
    //
    //        assertTrue(mapper.hasMap(createBenzaldehyde()));
    //    }
    //
    //    public void testItDoesntMapBlockedBenzaldehydeOntoBenzoicAcid() {
    //        Molecule blockedBenzaldehyde = this.createBlockedBenzaldehyde();
    //        IMapper mapper = new VFMapper(blockedBenzaldehyde, true);
    //
    //        assertFalse(mapper.hasMap(createBenzoicAcid()));
    //    }
    //
    //    public void testItMapsDimethylsulfideToChargelessDMSO() {
    //        IMapper mapper = new VFMapper(Molecules.createDimethylsulfide(), true);
    //
    //        assertTrue(mapper.hasMap(Molecules.createChargelessDMSO()));
    //    }
    //
    //    public void testItMapsDimethylsulfideToChargedDMSO() {
    //        IMapper mapper = new VFMapper(Molecules.createDimethylsulfide());
    //
    //        assertTrue(mapper.hasMap(Molecules.createChargedDMSO()));
    //    }
    //  public void testItMapsChargelessDMSOToChargeledDMSO()
    //  {
    //    Mapper mapper = new DefaultMapper(Molecules.createChargelessDMSO());
    //
    //    assertTrue(mapper.hasMap(Molecules.createChargedDMSO()));
    //  }
    //    @Test
    //    public void testItMapsPropaneToAcetone() throws CDKException {
    //        IAtomContainer mol = Molecules.createPropane();
    //        IQuery querComp = QueryCompiler.compile(mol, true);
    //        IMapper mapper = new VFMapper(querComp);
    //        assertTrue(mapper.hasMap(Molecules.createAcetone()));
    //    }
    //
    //    @Test
    //    public void testDoesntMapImineToAmine() throws CDKException {
    //        IAtomContainer mol = Molecules.createSimpleImine();
    //        IQuery querComp = QueryCompiler.compile(mol, true);
    //        IMapper mapper = new VFMapper(querComp);
    //        Map<INode, IAtom> map = mapper.getFirstMap(Molecules.createSimpleAmine());
    //        assertEquals(0, map.size());
    //    }
    //
    //    public void testItMapsBigmolToItself() {
    //        IMapper mapper = new VFMapper(bigmol, true);
    //
    //        assertEquals(bigmol.getAtomCount(), mapper.getFirstMap(bigmol).size());
    //    }
    //
    //    public void testBigmolHasOneMap() {
    //        IMapper mapper = new VFMapper(bigmol, true);
    //
    //        assertEquals(1, mapper.countMaps(bigmol));
    //    }
}
