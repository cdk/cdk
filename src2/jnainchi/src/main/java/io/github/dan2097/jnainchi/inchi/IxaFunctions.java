/**
 * JNA-InChI - Library for calling InChI from Java
 * Copyright © 2018 Daniel Lowe
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dan2097.jnainchi.inchi;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class IxaFunctions {
  
  public static final IXA_ATOMID IXA_ATOMID_IMPLICIT_H = new IXA_ATOMID(Pointer.createConstant(-1L));

  public static class IXA_ATOMID extends PointerType {
    public IXA_ATOMID(Pointer address) {
        super(address);
    }
    public IXA_ATOMID() {
        super();
    }
  };
  
  public static class IXA_BONDID extends PointerType {
    public IXA_BONDID(Pointer address) {
        super(address);
    }
    public IXA_BONDID() {
        super();
    }
  };
  
  public static class IXA_INCHIBUILDER_HANDLE extends PointerType {
    public IXA_INCHIBUILDER_HANDLE(Pointer address) {
        super(address);
    }
    public IXA_INCHIBUILDER_HANDLE() {
        super();
    }
  };

  public static class IXA_INCHIKEYBUILDER_HANDLE extends PointerType {
    public IXA_INCHIKEYBUILDER_HANDLE(Pointer address) {
        super(address);
    }
    public IXA_INCHIKEYBUILDER_HANDLE() {
        super();
    }
  };

  public static class IXA_MOL_HANDLE extends PointerType {
    public IXA_MOL_HANDLE(Pointer address) {
        super(address);
    }
    public IXA_MOL_HANDLE() {
        super();
    }
  };
  
  public static class IXA_POLYMERUNITID extends PointerType {
    public IXA_POLYMERUNITID(Pointer address) {
        super(address);
    }
    public IXA_POLYMERUNITID() {
        super();
    }
  };

  public static class IXA_STATUS_HANDLE extends PointerType {
    public IXA_STATUS_HANDLE(Pointer address) {
        super(address);
    }
    public IXA_STATUS_HANDLE() {
        super();
    }
  };
  
  public static class IXA_STEREOID extends PointerType {
    public IXA_STEREOID(Pointer address) {
        super(address);
    }
    public IXA_STEREOID() {
        super();
    }
  };

  public static IXA_STATUS_HANDLE IXA_STATUS_Create(){
    return new IXA_STATUS_HANDLE(InchiLibrary.IXA_STATUS_Create());
  }

  public static void IXA_STATUS_Clear(IXA_STATUS_HANDLE hStatus) {
    InchiLibrary.IXA_STATUS_Clear(hStatus.getPointer());
  }

  public static void IXA_STATUS_Destroy(IXA_STATUS_HANDLE hStatus) {
    InchiLibrary.IXA_STATUS_Destroy(hStatus.getPointer());
  }

  public static boolean IXA_STATUS_HasError(IXA_STATUS_HANDLE hStatus) {
    return InchiLibrary.IXA_STATUS_HasError(hStatus.getPointer());
  }

  public static boolean IXA_STATUS_HasWarning(IXA_STATUS_HANDLE hStatus) {
    return InchiLibrary.IXA_STATUS_HasWarning(hStatus.getPointer());
  }

  public static int IXA_STATUS_GetCount(IXA_STATUS_HANDLE hStatus) {
    return InchiLibrary.IXA_STATUS_GetCount(hStatus.getPointer());
  }

  public static int IXA_STATUS_GetSeverity(IXA_STATUS_HANDLE hStatus, int vIndex) {
    return InchiLibrary.IXA_STATUS_GetSeverity(hStatus.getPointer(), vIndex);
  }

  public static String IXA_STATUS_GetMessage(IXA_STATUS_HANDLE hStatus, int vIndex) {
    return InchiLibrary.IXA_STATUS_GetMessage(hStatus.getPointer(), vIndex);
  }

  public static IXA_MOL_HANDLE IXA_MOL_Create(IXA_STATUS_HANDLE hStatus) {
    return new IXA_MOL_HANDLE(InchiLibrary.IXA_MOL_Create(hStatus.getPointer()));
  }

  public static void IXA_MOL_Clear(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule) {
    InchiLibrary.IXA_MOL_Clear(hStatus.getPointer(), hMolecule.getPointer());
  }

  public static void IXA_MOL_Destroy(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule) {
    InchiLibrary.IXA_MOL_Destroy(hStatus.getPointer(), hMolecule.getPointer());
  }

  public static void IXA_MOL_ReadMolfile(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, String pBytes) {
    InchiLibrary.IXA_MOL_ReadMolfile(hStatus.getPointer(), hMolecule.getPointer(), fromString(pBytes));
  }

  public static void IXA_MOL_ReadInChI(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, String pInChI) {
    InchiLibrary.IXA_MOL_ReadInChI(hStatus.getPointer(), hMolecule.getPointer(), fromString(pInChI));
  }

  public static void IXA_MOL_SetChiral(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, boolean vChiral) {
    InchiLibrary.IXA_MOL_SetChiral(hStatus.getPointer(), hMolecule.getPointer(), vChiral);
  }

  public static boolean IXA_MOL_GetChiral(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule) {
    return InchiLibrary.IXA_MOL_GetChiral(hStatus.getPointer(), hMolecule.getPointer());
  }

  public static IXA_ATOMID IXA_MOL_CreateAtom(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule) {
    return new IXA_ATOMID(InchiLibrary.IXA_MOL_CreateAtom(hStatus.getPointer(), hMolecule.getPointer()));
  }

  public static void IXA_MOL_SetAtomElement(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom, String pElement) {
    InchiLibrary.IXA_MOL_SetAtomElement(hStatus.getPointer(), hMolecule.getPointer(), vAtom.getPointer(), fromString(pElement));
  }

  public static void IXA_MOL_SetAtomAtomicNumber(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom, int vAtomicNumber) {
    InchiLibrary.IXA_MOL_SetAtomAtomicNumber(hStatus.getPointer(), hMolecule.getPointer(), vAtom.getPointer(), vAtomicNumber);
  }

  public static void IXA_MOL_SetAtomMass(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom, int vMassNumber) {
    InchiLibrary.IXA_MOL_SetAtomMass(hStatus.getPointer(), hMolecule.getPointer(), vAtom.getPointer(), vMassNumber);
  }

  public static void IXA_MOL_SetAtomCharge(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom, int vCharge) {
    InchiLibrary.IXA_MOL_SetAtomCharge(hStatus.getPointer(), hMolecule.getPointer(), vAtom.getPointer(), vCharge);
  }

  public static void IXA_MOL_SetAtomRadical(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom, int vRadical) {
    InchiLibrary.IXA_MOL_SetAtomRadical(hStatus.getPointer(), hMolecule.getPointer(), vAtom.getPointer(), vRadical);
  }

  public static void IXA_MOL_SetAtomHydrogens(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom, int vHydrogenMassNumber, int vHydrogenCount) {
    InchiLibrary.IXA_MOL_SetAtomHydrogens(hStatus.getPointer(), hMolecule.getPointer(), vAtom.getPointer(), vHydrogenMassNumber, vHydrogenCount);
  }

  public static void IXA_MOL_SetAtomX(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom, double vX) {
    InchiLibrary.IXA_MOL_SetAtomX(hStatus.getPointer(), hMolecule.getPointer(), vAtom.getPointer(), vX);
  }

  public static void IXA_MOL_SetAtomY(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom, double vY) {
    InchiLibrary.IXA_MOL_SetAtomY(hStatus.getPointer(), hMolecule.getPointer(), vAtom.getPointer(), vY);
  }

  public static void IXA_MOL_SetAtomZ(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom, double vZ) {
    InchiLibrary.IXA_MOL_SetAtomZ(hStatus.getPointer(), hMolecule.getPointer(), vAtom.getPointer(), vZ);
  }

  public static IXA_BONDID IXA_MOL_CreateBond(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom1, IXA_ATOMID vAtom2) {
    return new IXA_BONDID(InchiLibrary.IXA_MOL_CreateBond(hStatus.getPointer(), hMolecule.getPointer(), vAtom1.getPointer(), vAtom2.getPointer()));
  }

  public static void IXA_MOL_SetBondType(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_BONDID vBond, int vType) {
    InchiLibrary.IXA_MOL_SetBondType(hStatus.getPointer(), hMolecule.getPointer(), vBond.getPointer(), vType);
  }

  public static void IXA_MOL_SetBondWedge(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_BONDID vBond, IXA_ATOMID vRefAtom, int vDirection) {
    InchiLibrary.IXA_MOL_SetBondWedge(hStatus.getPointer(), hMolecule.getPointer(), vBond.getPointer(), vRefAtom.getPointer(), vDirection);
  }

  public static void IXA_MOL_SetDblBondConfig(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_BONDID vBond, int vConfig) {
    InchiLibrary.IXA_MOL_SetDblBondConfig(hStatus.getPointer(), hMolecule.getPointer(), vBond.getPointer(), vConfig);
  }

  public static IXA_STEREOID IXA_MOL_CreateStereoTetrahedron(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vCentralAtom, IXA_ATOMID vVertex1, IXA_ATOMID vVertex2, IXA_ATOMID vVertex3, IXA_ATOMID vVertex4) {
    return new IXA_STEREOID(InchiLibrary.IXA_MOL_CreateStereoTetrahedron(hStatus.getPointer(), hMolecule.getPointer(), vCentralAtom.getPointer(), vVertex1.getPointer(), vVertex2.getPointer(), vVertex3.getPointer(), vVertex4.getPointer()));
  }

  public static IXA_STEREOID IXA_MOL_CreateStereoRectangle(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_BONDID vCentralBond, IXA_ATOMID vVertex1, IXA_ATOMID vVertex2, IXA_ATOMID vVertex3, IXA_ATOMID vVertex4) {
    return new IXA_STEREOID(InchiLibrary.IXA_MOL_CreateStereoRectangle(hStatus.getPointer(), hMolecule.getPointer(), vCentralBond.getPointer(), vVertex1.getPointer(), vVertex2.getPointer(), vVertex3.getPointer(), vVertex4.getPointer()));
  }

  public static IXA_STEREOID IXA_MOL_CreateStereoAntiRectangle(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vCentralAtom, IXA_ATOMID vVertex1, IXA_ATOMID vVertex2, IXA_ATOMID vVertex3, IXA_ATOMID vVertex4) {
    return new IXA_STEREOID(InchiLibrary.IXA_MOL_CreateStereoAntiRectangle(hStatus.getPointer(), hMolecule.getPointer(), vCentralAtom.getPointer(), vVertex1.getPointer(), vVertex2.getPointer(), vVertex3.getPointer(), vVertex4.getPointer()));
  }

  public static void IXA_MOL_SetStereoParity(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_STEREOID vStereo, int vParity) {
    InchiLibrary.IXA_MOL_SetStereoParity(hStatus.getPointer(), hMolecule.getPointer(), vStereo.getPointer(), vParity);
  }

  public static int IXA_MOL_ReserveSpace(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, int num_atoms, int num_bonds, int num_stereos) {
    return InchiLibrary.IXA_MOL_ReserveSpace(hStatus.getPointer(), hMolecule.getPointer(), num_atoms, num_bonds, num_stereos);
  }

  //FIXME IXA_MOL_CreatePolymerUnit and IXA_MOL_SetPolymerUnit are missing from Linux build
//  public static IXA_POLYMERUNITID IXA_MOL_CreatePolymerUnit(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule) {
//    return new IXA_POLYMERUNITID(InchiLibrary.IXA_MOL_CreatePolymerUnit(hStatus.getPointer(), hMolecule.getPointer()));
//  }
//  public static void IXA_MOL_SetPolymerUnit(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_POLYMERUNITID vPunit, int vid, int vtype, int vsubtype, int vconn, int vlabel, int vna, int vnb, DoubleBuffer vxbr1, DoubleBuffer vxbr2, ByteBuffer vsmt, IntBuffer valist, IntBuffer vblist) {
//    InchiLibrary.IXA_MOL_SetPolymerUnit(hStatus.getPointer(), hMolecule.getPointer(), vPunit.getPointer(), vid, vtype, vsubtype, vconn, vlabel, vna, vnb, vxbr1, vxbr2, vsmt, valist, vblist);
//  } 

  public static int IXA_MOL_GetNumAtoms(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule) {
    return InchiLibrary.IXA_MOL_GetNumAtoms(hStatus.getPointer(), hMolecule.getPointer());
  }

  public static int IXA_MOL_GetNumBonds(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule) {
    return InchiLibrary.IXA_MOL_GetNumBonds(hStatus.getPointer(), hMolecule.getPointer());
  }

  public static IXA_ATOMID IXA_MOL_GetAtomId(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, int vAtomIndex) {
    return new IXA_ATOMID(InchiLibrary.IXA_MOL_GetAtomId(hStatus.getPointer(), hMolecule.getPointer(), vAtomIndex));
  }

  public static IXA_BONDID IXA_MOL_GetBondId(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, int vBondIndex) {
    return new IXA_BONDID(InchiLibrary.IXA_MOL_GetBondId(hStatus.getPointer(), hMolecule.getPointer(), vBondIndex));
  }

  public static int IXA_MOL_GetAtomIndex(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom) {
    return InchiLibrary.IXA_MOL_GetAtomIndex(hStatus.getPointer(), hMolecule.getPointer(), vAtom.getPointer());
  }

  public static int IXA_MOL_GetBondIndex(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_BONDID vBond) {
    return InchiLibrary.IXA_MOL_GetBondIndex(hStatus.getPointer(), hMolecule.getPointer(), vBond.getPointer());
  }

  public static int IXA_MOL_GetAtomNumBonds(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom) {
    return InchiLibrary.IXA_MOL_GetAtomNumBonds(hStatus.getPointer(), hMolecule.getPointer(), vAtom.getPointer());
  }
  //FIXME IXA_MOL_GetPolymerUnitId and IXA_MOL_GetPolymerUnitIndex are missing from Linux build
//  
//  public static IXA_POLYMERUNITID IXA_MOL_GetPolymerUnitId(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, int vPolymerUnitIndex) {
//    return new IXA_POLYMERUNITID(InchiLibrary.IXA_MOL_GetPolymerUnitId(hStatus.getPointer(), hMolecule.getPointer(), vPolymerUnitIndex));
//  }
//
//  public static int IXA_MOL_GetPolymerUnitIndex(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_POLYMERUNITID vPolymerUnit) {
//    return InchiLibrary.IXA_MOL_GetPolymerUnitIndex(hStatus.getPointer(), hMolecule.getPointer(), vPolymerUnit.getPointer());
//  }

  public static IXA_BONDID IXA_MOL_GetAtomBond(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom, int vBondIndex) {
    return new IXA_BONDID(InchiLibrary.IXA_MOL_GetAtomBond(hStatus.getPointer(), hMolecule.getPointer(), vAtom.getPointer(), vBondIndex));
  }

  public static IXA_BONDID IXA_MOL_GetCommonBond(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom1, IXA_ATOMID vAtom2) {
    return new IXA_BONDID(InchiLibrary.IXA_MOL_GetCommonBond(hStatus.getPointer(), hMolecule.getPointer(), vAtom1.getPointer(), vAtom2.getPointer()));
  }

  public static IXA_ATOMID IXA_MOL_GetBondAtom1(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_BONDID vBond) {
    return new IXA_ATOMID(InchiLibrary.IXA_MOL_GetBondAtom1(hStatus.getPointer(), hMolecule.getPointer(), vBond.getPointer()));
  }

  public static IXA_ATOMID IXA_MOL_GetBondAtom2(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_BONDID vBond) {
    return new IXA_ATOMID(InchiLibrary.IXA_MOL_GetBondAtom2(hStatus.getPointer(), hMolecule.getPointer(), vBond.getPointer()));
  }
  
  public static IXA_ATOMID IXA_MOL_GetBondOtherAtom(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_BONDID vBond, IXA_ATOMID vAtom) {
    return new IXA_ATOMID(InchiLibrary.IXA_MOL_GetBondOtherAtom(hStatus.getPointer(), hMolecule.getPointer(), vBond.getPointer(), vAtom.getPointer()));
  }

  public static String IXA_MOL_GetAtomElement(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom) {
    return InchiLibrary.IXA_MOL_GetAtomElement(hStatus.getPointer(), hMolecule.getPointer(), vAtom.getPointer());
  }

  public static int IXA_MOL_GetAtomAtomicNumber(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom) {
    return InchiLibrary.IXA_MOL_GetAtomAtomicNumber(hStatus.getPointer(), hMolecule.getPointer(), vAtom.getPointer());
  }

  public static int IXA_MOL_GetAtomMass(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom) {
    return InchiLibrary.IXA_MOL_GetAtomMass(hStatus.getPointer(), hMolecule.getPointer(), vAtom.getPointer());
  }

  public static int IXA_MOL_GetAtomCharge(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom) {
    return InchiLibrary.IXA_MOL_GetAtomCharge(hStatus.getPointer(), hMolecule.getPointer(), vAtom.getPointer());
  }

  public static int IXA_MOL_GetAtomRadical(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom) {
    return InchiLibrary.IXA_MOL_GetAtomRadical(hStatus.getPointer(), hMolecule.getPointer(), vAtom.getPointer());
  }

  public static int IXA_MOL_GetAtomHydrogens(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom, int vHydrogenMassNumber) {
    return InchiLibrary.IXA_MOL_GetAtomHydrogens(hStatus.getPointer(), hMolecule.getPointer(), vAtom.getPointer(), vHydrogenMassNumber);
  }

  public static double IXA_MOL_GetAtomX(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom) {
    return InchiLibrary.IXA_MOL_GetAtomX(hStatus.getPointer(), hMolecule.getPointer(), vAtom.getPointer());
  }

  public static double IXA_MOL_GetAtomY(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom) {
    return InchiLibrary.IXA_MOL_GetAtomY(hStatus.getPointer(), hMolecule.getPointer(), vAtom.getPointer());
  }

  public static double IXA_MOL_GetAtomZ(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_ATOMID vAtom) {
    return InchiLibrary.IXA_MOL_GetAtomZ(hStatus.getPointer(), hMolecule.getPointer(), vAtom.getPointer());
  }

  public static int IXA_MOL_GetBondType(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_BONDID vBond) {
    return InchiLibrary.IXA_MOL_GetBondType(hStatus.getPointer(), hMolecule.getPointer(), vBond.getPointer());
  }

  public static int IXA_MOL_GetBondWedge(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_BONDID vBond, IXA_ATOMID vRefAtom) {
    return InchiLibrary.IXA_MOL_GetBondWedge(hStatus.getPointer(), hMolecule.getPointer(), vBond.getPointer(), vRefAtom.getPointer());
  }

  public static int IXA_MOL_GetDblBondConfig(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_BONDID vBond) {
    return InchiLibrary.IXA_MOL_GetDblBondConfig(hStatus.getPointer(), hMolecule.getPointer(), vBond.getPointer());
  }

  public static int IXA_MOL_GetNumStereos(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule) {
    return InchiLibrary.IXA_MOL_GetNumStereos(hStatus.getPointer(), hMolecule.getPointer());
  }

  public static IXA_STEREOID IXA_MOL_GetStereoId(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, int vStereoIndex) {
    return new IXA_STEREOID(InchiLibrary.IXA_MOL_GetStereoId(hStatus.getPointer(), hMolecule.getPointer(), vStereoIndex));
  }

  public static int IXA_MOL_GetStereoIndex(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_STEREOID vStereo) {
    return InchiLibrary.IXA_MOL_GetStereoIndex(hStatus.getPointer(), hMolecule.getPointer(), vStereo.getPointer());
  }

  public static int IXA_MOL_GetStereoTopology(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_STEREOID vStereo) {
    return InchiLibrary.IXA_MOL_GetStereoTopology(hStatus.getPointer(), hMolecule.getPointer(), vStereo.getPointer());
  }

  public static IXA_ATOMID IXA_MOL_GetStereoCentralAtom(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_STEREOID vStereo) {
    return new IXA_ATOMID(InchiLibrary.IXA_MOL_GetStereoCentralAtom(hStatus.getPointer(), hMolecule.getPointer(), vStereo.getPointer()));
  }

  public static IXA_BONDID IXA_MOL_GetStereoCentralBond(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_STEREOID vStereo) {
    return new IXA_BONDID(InchiLibrary.IXA_MOL_GetStereoCentralBond(hStatus.getPointer(), hMolecule.getPointer(), vStereo.getPointer()));
  }

  public static int IXA_MOL_GetStereoNumVertices(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_STEREOID vStereo) {
    return InchiLibrary.IXA_MOL_GetStereoNumVertices(hStatus.getPointer(), hMolecule.getPointer(), vStereo.getPointer());
  }

  public static IXA_ATOMID IXA_MOL_GetStereoVertex(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_STEREOID vStereo, int vVertexIndex) {
    return new IXA_ATOMID(InchiLibrary.IXA_MOL_GetStereoVertex(hStatus.getPointer(), hMolecule.getPointer(), vStereo.getPointer(), vVertexIndex));
  }

  public static int IXA_MOL_GetStereoParity(IXA_STATUS_HANDLE hStatus, IXA_MOL_HANDLE hMolecule, IXA_STEREOID vStereo) {
    return InchiLibrary.IXA_MOL_GetStereoParity(hStatus.getPointer(), hMolecule.getPointer(), vStereo.getPointer());
  }

  public static IXA_INCHIBUILDER_HANDLE IXA_INCHIBUILDER_Create(IXA_STATUS_HANDLE hStatus) {
    return new IXA_INCHIBUILDER_HANDLE(InchiLibrary.IXA_INCHIBUILDER_Create(hStatus.getPointer()));
  }

  public static void IXA_INCHIBUILDER_SetMolecule(IXA_STATUS_HANDLE hStatus, IXA_INCHIBUILDER_HANDLE hInChIBuilder, IXA_MOL_HANDLE hMolecule) {
    InchiLibrary.IXA_INCHIBUILDER_SetMolecule(hStatus.getPointer(), hInChIBuilder.getPointer(), hMolecule.getPointer());
  }

  public static String IXA_INCHIBUILDER_GetInChI(IXA_STATUS_HANDLE hStatus, IXA_INCHIBUILDER_HANDLE hInChIBuilder) {
    return InchiLibrary.IXA_INCHIBUILDER_GetInChI(hStatus.getPointer(), hInChIBuilder.getPointer());
  }

  public static String IXA_INCHIBUILDER_GetInChIEx(IXA_STATUS_HANDLE hStatus, IXA_INCHIBUILDER_HANDLE hBuilder) {
    return InchiLibrary.IXA_INCHIBUILDER_GetInChIEx(hStatus.getPointer(), hBuilder.getPointer());
  }

  public static String IXA_INCHIBUILDER_GetAuxInfo(IXA_STATUS_HANDLE hStatus, IXA_INCHIBUILDER_HANDLE hInChIBuilder) {
    return InchiLibrary.IXA_INCHIBUILDER_GetAuxInfo(hStatus.getPointer(), hInChIBuilder.getPointer());
  }

  public static String IXA_INCHIBUILDER_GetLog(IXA_STATUS_HANDLE hStatus, IXA_INCHIBUILDER_HANDLE hInChIBuilder) {
    return InchiLibrary.IXA_INCHIBUILDER_GetLog(hStatus.getPointer(), hInChIBuilder.getPointer());
  }

  public static void IXA_INCHIBUILDER_Destroy(IXA_STATUS_HANDLE hStatus, IXA_INCHIBUILDER_HANDLE hInChIBuilder) {
    InchiLibrary.IXA_INCHIBUILDER_Destroy(hStatus.getPointer(), hInChIBuilder.getPointer());
  }

  public static void IXA_INCHIBUILDER_SetOption(IXA_STATUS_HANDLE hStatus, IXA_INCHIBUILDER_HANDLE hInChIBuilder, int vOption, boolean vValue) {
    InchiLibrary.IXA_INCHIBUILDER_SetOption(hStatus.getPointer(), hInChIBuilder.getPointer(), vOption, vValue);
  }

  public static void IXA_INCHIBUILDER_SetOption_Stereo(IXA_STATUS_HANDLE hStatus, IXA_INCHIBUILDER_HANDLE hInChIBuilder, int vValue) {
    InchiLibrary.IXA_INCHIBUILDER_SetOption_Stereo(hStatus.getPointer(), hInChIBuilder.getPointer(), vValue);
  }

  public static void IXA_INCHIBUILDER_SetOption_Timeout(IXA_STATUS_HANDLE hStatus, IXA_INCHIBUILDER_HANDLE hInChIBuilder, int vValue) {
    InchiLibrary.IXA_INCHIBUILDER_SetOption_Timeout(hStatus.getPointer(), hInChIBuilder.getPointer(), vValue);
  }
  
  public static void IXA_INCHIBUILDER_SetOption_Timeout_MilliSeconds(IXA_STATUS_HANDLE hStatus, IXA_INCHIBUILDER_HANDLE hInChIBuilder, long vValue) {
    InchiLibrary.IXA_INCHIBUILDER_SetOption_Timeout_MilliSeconds(hStatus.getPointer(), hInChIBuilder.getPointer(), vValue);
  }

  public static boolean IXA_INCHIBUILDER_CheckOption(IXA_STATUS_HANDLE hStatus, IXA_INCHIBUILDER_HANDLE hInChIBuilder, int vOption) {
    return InchiLibrary.IXA_INCHIBUILDER_CheckOption(hStatus.getPointer(), hInChIBuilder.getPointer(), vOption);
  }

  public static boolean IXA_INCHIBUILDER_CheckOption_Stereo(IXA_STATUS_HANDLE hStatus, IXA_INCHIBUILDER_HANDLE hInChIBuilder, int vValue) {
    return InchiLibrary.IXA_INCHIBUILDER_CheckOption_Stereo(hStatus.getPointer(), hInChIBuilder.getPointer(), vValue);
  }

  //FIXME IXA_INCHIBUILDER_GetOption_Timeout_MilliSeconds is missing from Linux build
//  public static long IXA_INCHIBUILDER_GetOption_Timeout_MilliSeconds(IXA_STATUS_HANDLE hStatus, IXA_INCHIBUILDER_HANDLE hInChIBuilder) {
//    return InchiLibrary.IXA_INCHIBUILDER_GetOption_Timeout_MilliSeconds(hStatus.getPointer(), hInChIBuilder.getPointer());
//  }

  public static IXA_INCHIKEYBUILDER_HANDLE IXA_INCHIKEYBUILDER_Create(IXA_STATUS_HANDLE hStatus) {
    return new IXA_INCHIKEYBUILDER_HANDLE(InchiLibrary.IXA_INCHIKEYBUILDER_Create(hStatus.getPointer()));
  }

  public static void IXA_INCHIKEYBUILDER_SetInChI(IXA_STATUS_HANDLE hStatus, IXA_INCHIKEYBUILDER_HANDLE hInChIKeyBuilder, String pInChI) {
    InchiLibrary.IXA_INCHIKEYBUILDER_SetInChI(hStatus.getPointer(), hInChIKeyBuilder.getPointer(), fromString(pInChI));
  }

  public static String IXA_INCHIKEYBUILDER_GetInChIKey(IXA_STATUS_HANDLE hStatus, IXA_INCHIKEYBUILDER_HANDLE hInChIKeyBuilder) {
    return InchiLibrary.IXA_INCHIKEYBUILDER_GetInChIKey(hStatus.getPointer(), hInChIKeyBuilder.getPointer());
  }

  public static void IXA_INCHIKEYBUILDER_Destroy(IXA_STATUS_HANDLE hStatus, IXA_INCHIKEYBUILDER_HANDLE hInChIKeyBuilder) {
    InchiLibrary.IXA_INCHIKEYBUILDER_Destroy(hStatus.getPointer(), hInChIKeyBuilder.getPointer());
  }
  
  private static byte[] fromString(String jstr) {
    int strLen = jstr.length();
    byte[] cstr = new byte[strLen + 1];
    for (int i = 0; i < strLen; i++) {
      cstr[i] = (byte) jstr.charAt(i);
    }
    cstr[strLen] = '\0';
    return cstr;
  }

}
