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
import junit.framework.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IMapper;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.algorithm.vflib.map.VFMapper;

/**
 *
 * @author Richard L. Apodaca <rapodaca at metamolecular.com>
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * @cdk.module test-smsd
 * @cdk.require java1.6+
 */
public class VFMapperTest extends CDKTestCase {

    private IMolecule hexane;
    private IMolecule benzene;
    private IMolecule pyridine;
    private IMolecule toluene4;
    private IMolecule pyridazine;
    private IMolecule naphthalene;
    private IMolecule chlorobenzene;
    private IMolecule chloroisoquinoline4;
    private IMolecule toluene;
    private IMolecule phenol;
    private IMolecule acetone;
    private IMolecule propane;
    private IMolecule cyclopropane;
    private IMolecule bigmol;

//    public VFMapperTest() {
//        // see: http://gist.github.com/144912
//        bigmol = MoleculeKit.readMolfile("241\n  -OEChem-07100913442D\n\n 67 72  0  0  1  0            999 V2000\n    5.3950   -3.1602    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n    4.9853   -3.8822    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n    5.4045   -4.6006    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0\n    6.2342   -4.5947    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n    6.6482   -3.8727    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0\n    6.2284   -3.1565    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n    7.4799   -3.8687    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n    4.9958   -5.3274    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n    4.9776   -2.4425    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    4.1536   -3.8873    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n    6.6567   -5.3161    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n    5.3878   -1.7207    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n    4.1459   -2.4468    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n   13.2577   -4.4546    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n   13.6743   -3.7340    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n   13.2533   -3.0100    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0\n   12.4237   -3.0128    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n   12.0114   -3.7375    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0\n   12.4243   -4.4553    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n   11.1796   -3.7385    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n   13.6691   -2.2889    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n   13.6766   -5.1737    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   14.5060   -3.7320    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n   12.0081   -2.2941    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n   13.2596   -5.8941    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n   14.5084   -5.1726    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n   12.8766   -8.3196    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   13.5177   -8.8032    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   13.1451   -7.5594    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n   14.1838   -8.3419    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   13.9476   -7.5771    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    5.8674   -8.3480    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    5.2145   -8.8214    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    5.6186   -7.5808    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n    4.5139   -8.3857    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    5.2048   -9.6520    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    4.7732   -7.6213    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    4.4315   -6.8694    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n   10.1699   -8.3375    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   10.4138   -7.5717    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    9.3511   -8.7486    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   10.8181   -8.8045    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n   11.2181   -7.5717    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   10.2013   -6.7887    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    8.5504   -8.3375    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   11.4662   -8.3375    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   11.6152   -6.8705    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   10.4104   -6.0055    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    8.2980   -7.5787    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    7.8973   -8.8158    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n   12.1571   -8.7382    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   10.1943   -5.2154    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    7.5009   -7.5787    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    8.5043   -6.7992    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    7.2456   -8.3375    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    9.7266   -5.2224    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n    7.0932   -6.8775    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    8.2910   -6.0161    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    6.5549   -8.7486    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    8.4931   -5.2328    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    9.0141   -5.2224    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n   13.4995   -9.6348    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   14.2100  -10.0685    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    3.7211   -8.6383    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    3.5455   -9.4504    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   14.9696   -8.6151    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   14.4518   -6.9145    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n 16 17  1  0  0  0  0\n 17 18  1  0  0  0  0\n 18 19  1  0  0  0  0\n 32 33  1  0  0  0  0\n 32 34  1  0  0  0  0\n 33 35  2  0  0  0  0\n 33 36  1  0  0  0  0\n 34 37  1  0  0  0  0\n 37 38  2  0  0  0  0\n 35 37  1  0  0  0  0\n 19 14  1  0  0  0  0\n  3  4  1  0  0  0  0\n 18 20  1  1  0  0  0\n  2 10  1  6  0  0  0\n 16 21  1  1  0  0  0\n  4  5  1  0  0  0  0\n 14 22  1  1  0  0  0\n  4 11  1  6  0  0  0\n 15 23  1  6  0  0  0\n  5  6  1  0  0  0  0\n 17 24  1  6  0  0  0\n  9 12  1  0  0  0  0\n 22 25  1  0  0  0  0\n  6  1  1  0  0  0  0\n 22 26  2  0  0  0  0\n  9 13  2  0  0  0  0\n  5  7  1  1  0  0  0\n  1  2  1  0  0  0  0\n  3  8  1  1  0  0  0\n 27 28  1  0  0  0  0\n 27 29  1  0  0  0  0\n 28 30  2  0  0  0  0\n 39 40  2  0  0  0  0\n 39 41  1  0  0  0  0\n 39 42  1  0  0  0  0\n 40 43  1  0  0  0  0\n 40 44  1  0  0  0  0\n 41 45  1  0  0  0  0\n 42 46  1  0  0  0  0\n 43 47  1  0  0  0  0\n 44 48  1  0  0  0  0\n 45 49  2  0  0  0  0\n 45 50  1  0  0  0  0\n 46 51  1  0  0  0  0\n 48 52  1  0  0  0  0\n 49 53  1  0  0  0  0\n 49 54  1  0  0  0  0\n 50 55  1  0  0  0  0\n 52 20  1  0  0  0  0\n 52 56  2  0  0  0  0\n 53 57  1  0  0  0  0\n 54 58  1  0  0  0  0\n 55 59  1  0  0  0  0\n 58 60  1  0  0  0  0\n 59 32  2  0  0  0  0\n 60  7  1  0  0  0  0\n 60 61  2  0  0  0  0\n 43 46  2  0  0  0  0\n 53 55  2  0  0  0  0\n 51 27  2  0  0  0  0\n 29 31  1  0  0  0  0\n 28 62  1  0  0  0  0\n 30 31  1  0  0  0  0\n 62 63  2  0  0  0  0\n  2  3  1  0  0  0  0\n 35 64  1  0  0  0  0\n  1  9  1  1  0  0  0\n 64 65  2  0  0  0  0\n 14 15  1  0  0  0  0\n 30 66  1  0  0  0  0\n 15 16  1  0  0  0  0\n 31 67  2  0  0  0  0\nM  END");
//    }
    protected void setUp() throws Exception {
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
    public void testItShouldMatchHexaneToHexane() {
        IMapper mapper = new VFMapper(hexane, true);

        Assert.assertTrue(mapper.hasMap(hexane));
    }

    @Test
    public void testItShouldMatchHexaneToHexaneWhenUsingMolecule() {
        IMapper mapper = new VFMapper(hexane, true);

        Assert.assertTrue(mapper.hasMap(hexane));
    }

    @Test
    public void testItShouldMatchBenzeneToBenzene() {
        IMapper mapper = new VFMapper(benzene, true);

        Assert.assertTrue(mapper.hasMap(benzene));
    }

    @Test
    public void testItShouldNotMatchHexaneToBenzene() {
        IMapper mapper = new VFMapper(hexane, true);

        Assert.assertFalse(mapper.hasMap(benzene));
    }

    @Test
    public void testItShouldNotMatchPyridazineToNaphthalene() {
        IMapper mapper = new VFMapper(pyridazine, true);

        Assert.assertFalse(mapper.hasMap(naphthalene));
    }

    @Test
    public void testItShouldNotMatchChlorobenzeneTo4ChloroIsoquinoline() {
        IMapper mapper = new VFMapper(chlorobenzene, true);

        Assert.assertFalse(mapper.hasMap(chloroisoquinoline4));
    }

    @Test
    public void testItShouldNotMatchBenzeneToPyridine() {
        IMapper mapper = new VFMapper(benzene, true);

        Assert.assertFalse(mapper.hasMap(pyridine));

        mapper = new VFMapper(pyridine, true);

        Assert.assertFalse(mapper.hasMap(benzene));
    }

    @Test
    public void testItShouldNotMatchTolueneToBenzene() {
        IMapper mapper = new VFMapper(toluene, true);

        Assert.assertFalse(mapper.hasMap(benzene));
    }

    @Test
    public void testItShouldMatchAcetoneToAcetone() {
        IMapper mapper = new VFMapper(acetone, true);

        Assert.assertTrue(mapper.hasMap(acetone));
    }

    @Test
    public void testItShouldMatchPropaneToCyclopropane() {
        IMapper mapper = new VFMapper(propane, true);

        Assert.assertTrue(mapper.hasMap(cyclopropane));
    }

    @Test
    public void testItShouldFindTwoMapsFromHexaneToHexane() {
        IMapper mapper = new VFMapper(hexane, true);

        List<Map<INode, IAtom>> maps = mapper.getMaps(hexane);

        Assert.assertEquals(2, maps.size());
    }

    @Test
    public void testItShouldNotMatchTolueneToPhenol() {
        IMapper mapper = new VFMapper(toluene, true);

        Assert.assertFalse(mapper.hasMap(phenol));
    }

    @Test
    public void testItShouldMapSixAtomsOfBenzeneOntoBenzene() {
        IMapper mapper = new VFMapper(benzene, true);
        Map<INode, IAtom> map = mapper.getFirstMap(benzene);

        Assert.assertEquals(6, map.size());
    }

    @Test
    public void testItShouldCountTwelveMapsForBenzeneOntoBenzene() {
        IMapper mapper = new VFMapper(benzene, true);

        Assert.assertEquals(12, mapper.countMaps(benzene));
    }

    @Test
    public void testItShouldCountTwoMapsForTolueneOntoToluene() {
        IMapper mapper = new VFMapper(toluene, true);

        Assert.assertEquals(2, mapper.countMaps(toluene));
    }

    @Test
    public void testItShouldFindTwelveMapsForBenzeneOntoBenzene() {
        IMapper mapper = new VFMapper(benzene, true);
        List<Map<INode, IAtom>> maps = mapper.getMaps(benzene);

        Assert.assertEquals(12, maps.size());
    }

    @Test
    public void testItShouldFindTwentyFourMapsForBenzeneOntoNaphthalene() {
        IMapper mapper = new VFMapper(benzene, true);
        List<Map<INode, IAtom>> maps = mapper.getMaps(naphthalene);

        Assert.assertEquals(24, maps.size());
    }

    @Test
    public void testItShouldFindAMapForEquivalentFormsOfToluene() {
        IMapper mapper = new VFMapper(toluene, true);
        Map<INode, IAtom> map = mapper.getFirstMap(toluene4);

        Assert.assertEquals(7, map.size());
    }

    @Test
    public void testItShouldFindTwoMapsForEquivalentFormsOfToluene() {
        IMapper mapper = new VFMapper(toluene, true);
        List<Map<INode, IAtom>> maps = mapper.getMaps(toluene4);

        Assert.assertEquals(2, maps.size());
    }

//    @Test
//    public void testItMapsBlockedPropaneOntoPropane() throws CDKException {
//        IMolecule blockedPropane = Molecules.createPropane();
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
//        IMolecule mol = Molecules.createPropane();
//        IQuery querComp = QueryCompiler.compile(mol, true);
//        IMapper mapper = new VFMapper(querComp);
//        assertTrue(mapper.hasMap(Molecules.createAcetone()));
//    }
//
//    @Test
//    public void testDoesntMapImineToAmine() throws CDKException {
//        IMolecule mol = Molecules.createSimpleImine();
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
