/* Copyright (C) 2006-2007  Sam Adams <sea36@users.sf.net>
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

import io.github.dan2097.jnainchi.InchiFlag;
import io.github.dan2097.jnainchi.InchiStatus;
import net.sf.jniinchi.INCHI_OPTION;
import net.sf.jniinchi.INCHI_RET;

/**
 * This class provides conversion from JNI InChI enum options to JNA (input) and
 * from JNA return status to JNI status (output).
 */
final class JniInchiSupport {

    private JniInchiSupport() {}

    static INCHI_RET toJniStatus(InchiStatus status) {
        switch (status) {
            case SUCCESS: return INCHI_RET.OKAY;
            case WARNING: return INCHI_RET.WARNING;
            case ERROR:   return INCHI_RET.ERROR;
            default:
                throw new IllegalArgumentException("Unexpected status!");
        }
    }

    static InchiFlag toJnaOption(INCHI_OPTION flag) {
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
