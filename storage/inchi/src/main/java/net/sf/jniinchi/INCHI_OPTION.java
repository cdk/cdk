/*
 * Copyright (C) 2021  John Mayfield
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

package net.sf.jniinchi;

import io.github.dan2097.jnainchi.InchiFlag;

/**
 * This class provides backwards compatibility of JNA-INCHI with JNI-INCHI, this enum was exposed in the CDK API.
 * @author John Mayfield
 */
public enum INCHI_OPTION {
    SUCF,
    ChiralFlagON,
    ChiralFlagOFF,
    SNon,
    SAbs,
    SRel,
    SRac,
    SUU,
    NEWPS,
    RecMet,
    FixedH,
    AuxNone,
    NoADP,
    Compress,
    DoNotAddH,
    Wnumber,
    OutputSDF,
    WarnOnEmptyStructure,
    FixSp3Bug,
    FB,
    SPXYZ,
    SAsXYZ;

    public static INCHI_OPTION wrap(InchiFlag flag) {
        switch (flag) {
            case SUCF: return SUCF;
            case ChiralFlagON: return ChiralFlagON;
            case ChiralFlagOFF: return ChiralFlagOFF;
            case SNon: return SNon;
            case SRel: return SRel;
            case SRac: return SRac;
            case SUU: return SUU;
            case RecMet: return RecMet;
            case FixedH: return FixedH;
            case AuxNone: return AuxNone;
            case DoNotAddH: return DoNotAddH;
            case WarnOnEmptyStructure: return WarnOnEmptyStructure;

            default: throw new IllegalArgumentException(flag + " not supported?");
        }
    }

    public static InchiFlag wrap(INCHI_OPTION flag) {
        switch (flag) {
            case SUCF: return InchiFlag.SUCF;
            case ChiralFlagON: return InchiFlag.ChiralFlagON;
            case ChiralFlagOFF: return InchiFlag.ChiralFlagOFF;
            case SNon: return InchiFlag.SNon;
            case SRel: return InchiFlag.SRel;
            case SRac: return InchiFlag.SRac;
            case SUU: return InchiFlag.SUU;
            case RecMet: return InchiFlag.RecMet;
            case FixedH: return InchiFlag.FixedH;
            case AuxNone: return InchiFlag.AuxNone;
            case DoNotAddH: return InchiFlag.DoNotAddH;
            case WarnOnEmptyStructure: return InchiFlag.WarnOnEmptyStructure;
            default:
                System.err.println("Unsupported flag: " + flag);
                return null;
        }

        // case SAbs: return SAbs;
        // case NEWPS: return NEWPS;
        // case NoADP: return NoADP;
        // case Compress: return Compress;
        // case Wnumber: return Wnumber;
        // case OutputSDF: return OutputSDF;
        //            case FixSp3Bug: return FixSp3Bug;
//            case FB: return FB;
//            case SPXYZ: return SPXYZ;
//            case SAsXYZ: return SAsXYZ;
    }
}
