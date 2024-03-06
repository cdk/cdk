/*
 * Copyright (C) 2022 John Mayfield
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.openscience.cdk.smarts;

import java.util.Arrays;

/**
 * The result of parsing a SMARTS. The result indicates if the input was parsed
 * OK or not ({@link #ok()}, if the parse was not OK an error message and
 * location is set.
 *
 * @author John Mayfield
 * @see Smarts#parseToResult(org.openscience.cdk.interfaces.IAtomContainer, String)
 */
public class SmartsResult {

    private final String str;
    private final int pos;
    private final String mesg;
    private final int[] aoffsets;
    private final int[] boffsets;
    private final boolean status;

    SmartsResult(String str, int pos, String mesg) {
        this.str = str;
        this.pos = pos;
        this.mesg = mesg;
        this.status = false;
        this.aoffsets = null;
        this.boffsets = null;
    }

    SmartsResult(String str, int[] aoffsets, int[] boffsets) {
        this.str = str;
        this.pos = str.length();
        this.mesg = "OK";
        this.status = true;
        this.aoffsets = aoffsets;
        this.boffsets = boffsets;
    }

    /**
     * The error/warning message.
     *
     * @return the message
     */
    public String getMessage() {
        return this.mesg;
    }

    public int getAtomLocation(int idx) {
        if (idx < 0)
            return 0;
        if (idx >= aoffsets.length)
            return str.length();
        return aoffsets[idx];
    }

    public int getBondLocation(int idx) {
        if (idx < 0)
            return 0;
        if (idx >= boffsets.length)
            return str.length();
        return boffsets[idx];
    }

    public String displayErrorLocation() {
        return displayErrorLocation(pos-1);
    }

    /**
     * Displays where the error occurred on the input string.
     *
     * @return the error location
     */
    public String displayErrorLocation(int pos) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append('\n');
        if (pos <= str.length()) {
            char[] cs = new char[pos];
            Arrays.fill(cs, ' ');
            sb.append(cs);
            sb.append('^');
            if (str.charAt(pos) == '[') {
                int end = str.indexOf(']', pos);
                while (pos++ < end)
                    sb.append('^');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Was the SMARTS parsed ok.
     *
     * @return parse was OK.
     */
    public boolean ok() {
        return status;
    }

    /**
     * The position (string index) in the input that was interpreted.
     *
     * @return the position
     */
    public int getPosition() {
        return pos;
    }
}
