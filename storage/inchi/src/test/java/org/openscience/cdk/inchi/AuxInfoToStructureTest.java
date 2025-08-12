/* Copyright (C) 2025 Felix Bänsch (Beilstein-Institute)
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.inchi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.stereo.TetrahedralChirality;

import java.util.Collection;

/**
 * Test case for {@link AuxInfoToStructure} class.
 */
public class AuxInfoToStructureTest {

  @Test
  void testConstructor_String_IChemObjectBuilder() throws CDKException {
    String auxInfo = "AuxInfo=1/0/N:1/rA:1nC/rB:/rC:8.775,-3.125,0;";
    AuxInfoToStructure parser = new AuxInfoToStructure(auxInfo, SilentChemObjectBuilder.getInstance());
    Assertions.assertNotNull(parser);
  }

  @Test
  void testGetAtomContainer() throws CDKException {
    String auxInfo = "AuxInfo=1/0/N:1/rA:1nC/rB:/rC:8.775,-3.125,0;";
    AuxInfoToStructure parser = new AuxInfoToStructure(auxInfo, SilentChemObjectBuilder.getInstance());
    IAtomContainer mol = parser.getAtomContainer();
    Assertions.assertNotNull(mol);
    Assertions.assertEquals(1, mol.getAtomCount());
  }

  @Test
  void testNonNullAtomicNumbers() throws CDKException {
    String auxInfo = "AuxInfo=1/0/N:1/rA:1nC/rB:/rC:8.775,-3.125,0;";
    AuxInfoToStructure parser = new AuxInfoToStructure(auxInfo, SilentChemObjectBuilder.getInstance());
    IAtomContainer mol = parser.getAtomContainer();
    for (IAtom atom : mol.atoms()) {
      Assertions.assertNotNull(atom.getAtomicNumber());
    }
    Assertions.assertNotNull(mol);
    Assertions.assertEquals(1, mol.getAtomCount());
  }

  @Test
  void testNullAuxInfo_throwsIllegalArgumentException() {
    Assertions.assertThrows(IllegalArgumentException.class, ()-> {
      new AuxInfoToStructure(null, SilentChemObjectBuilder.getInstance());
    });
  }

  @Test
  void testInvalidAuxInfo_EmptyAtomContainer() throws CDKException {
    String auxInfo = "INVALID";
    AuxInfoToStructure parser = new AuxInfoToStructure(auxInfo, SilentChemObjectBuilder.getInstance());
    IAtomContainer mol = parser.getAtomContainer();
    Assertions.assertEquals(0, mol.getAtomCount());
    Assertions.assertEquals(0, mol.getBondCount());
  }

  @Test
  void testAtomStorageOrder() throws CDKException {
    String auxInfo = "AuxInfo=1/0/N:3,2,5,1,4/it:im/rA:5ClC.oCIBr/rB:p1;s2;s2;N2;/rC:0,-1.54,0;;0,1.54,0;1.54,0,0;-1.54,0,0;";
    AuxInfoToStructure parser = new AuxInfoToStructure(auxInfo, SilentChemObjectBuilder.getInstance());
    IAtomContainer mol = parser.getAtomContainer();
    Assertions.assertNotNull(mol);
    Assertions.assertEquals(5, mol.getAtomCount());
    Assertions.assertEquals("Cl", mol.getAtom(0).getSymbol());
    Assertions.assertEquals("C", mol.getAtom(1).getSymbol());
    Assertions.assertEquals("C", mol.getAtom(2).getSymbol());
    Assertions.assertEquals("I", mol.getAtom(3).getSymbol());
    Assertions.assertEquals("Br", mol.getAtom(4).getSymbol());
  }

  @Test
  void testStereoTetrahedral() throws CDKException {
    String auxInfo = "AuxInfo=1/0/N:3,2,5,1,4/it:im/rA:5ClC.oCIBr/rB:p1;s2;s2;N2;/rC:0,-1.54,0;;0,1.54,0;1.54,0,0;-1.54,0,0;";
    AuxInfoToStructure parser = new AuxInfoToStructure(auxInfo, SilentChemObjectBuilder.getInstance());
    IAtomContainer mol = parser.getAtomContainer();
    Assertions.assertNotNull(mol);

    Assertions.assertEquals(1, ((Collection<?>)mol.stereoElements()).size());

    if (mol.stereoElements().iterator().hasNext()) {
      IStereoElement<?,?> first = mol.stereoElements().iterator().next();
      Assertions.assertInstanceOf(TetrahedralChirality.class, first);
      Assertions.assertEquals("C", ((IAtom)first.getFocus()).getSymbol());
      Assertions.assertEquals("Cl", ((IAtom)first.getCarriers().get(0)).getSymbol());
      Assertions.assertEquals("C", ((IAtom)first.getCarriers().get(1)).getSymbol());
      Assertions.assertEquals("I", ((IAtom)first.getCarriers().get(2)).getSymbol());
      Assertions.assertEquals("Br", ((IAtom)first.getCarriers().get(3)).getSymbol());
    }
  }

  @Test
  @Disabled("InChI API does not return the cis trans stereochemistry.")
  void testExtendedCisTrans() throws CDKException {
    String auxInfo = "AuxInfo=1/0/N:1,2,3,4,5,6,7,8/rA:8nCCCCBrClFI/rB:d1;d1;d2;s3;s4;s4;s3;/rC:8.25,-4.55,0;7.25,-4.55,0;9.25,-4.55,0;6.25,-4.55,0;9.75,-5.416,0;5.75,-3.684,0;5.75,-5.416,0;9.75,-3.684,0;";
    AuxInfoToStructure parser = new AuxInfoToStructure(auxInfo, SilentChemObjectBuilder.getInstance());
    IAtomContainer mol = parser.getAtomContainer();

    Assertions.assertNotNull(mol);
    Assertions.assertEquals(1, ((Collection<?>)mol.stereoElements()).size());
  }
}
