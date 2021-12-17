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

import io.github.dan2097.jnainchi.InchiStatus;

/**
 * This class provides backwards compatibility of JNA-INCHI with JNI-INCHI, this enum was exposed in the CDK API.
 * @author John Mayfield
 */
public enum INCHI_RET {
    SKIP,
    EOF,
    OKAY,
    WARNING,
    ERROR,
    FATAL,
    UNKNOWN,
    BUSY;

    public static INCHI_RET wrap(InchiStatus status) {
        switch (status) {
            case SUCCESS: return INCHI_RET.OKAY;
            case WARNING: return INCHI_RET.WARNING;
            case ERROR:   return INCHI_RET.ERROR;
            default:
                throw new IllegalArgumentException("Unexpected status!");
        }
    }
}
