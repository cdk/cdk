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

package org.openscience.cdk.inchi;

import io.github.dan2097.jnainchi.InchiFlag;
import io.github.dan2097.jnainchi.InchiOptions;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides parsing of InChI options from a string. Using the JNA InchiOptions builder directly.
 * @author John Mayfield
 */
final class InChIOptionParser {

    private final ILoggingTool logger = LoggingToolFactory.createLoggingTool(InChIOptionParser.class);
    private final Map<String,InchiFlag> optMap = new HashMap<>();
    private final InchiOptions.InchiOptionsBuilder options;

    private InChIOptionParser() {
        for (InchiFlag flag : InchiFlag.values())
            optMap.put(flag.name(), flag);
        optMap.put("15T", InchiFlag.OneFiveT);
        options = new InchiOptions.InchiOptionsBuilder();
    }

    private void processString(String optstr) {
        int pos = 0;
        while (pos < optstr.length()) {
            switch (optstr.charAt(pos)) {
                case ' ':
                case '-':
                case '/':
                case ',':
                    pos++; // skip
                    break;
                case 'W': // timeout
                    pos++;
                    int next = optstr.indexOf(',', pos);
                    if (next < 0)
                        next = optstr.length();
                    String substring = optstr.substring(pos, next);
                    try {
                        // Note: locale sensitive e.g. 0,01 but we can not pass in milliseconds so doesn't matter so much
                        options.withTimeoutMilliSeconds((int)(1000*Double.parseDouble(substring)));
                    } catch (NumberFormatException ex) {
                        logger.warn("Invalid timtoue:" + substring);
                    }
                    break;
                default:
                    next = optstr.indexOf(',', pos);
                    if (next < 0)
                        next = optstr.length();
                    InchiFlag flag = optMap.get(optstr.substring(pos, next));
                    if (flag != null)
                        options.withFlag(flag);
                    else
                        logger.warn("Ignore unrecognized InChI flag:" + flag);
                    pos = next;
            }
        }
    }

    static InchiOptions parseString(String str) {
        if (str == null)
            return null;
        InChIOptionParser parser = new InChIOptionParser();
        parser.processString(str);
        return parser.options.build();
    }

    static InchiOptions parseStrings(List<String> strs) {
        if (strs == null)
            return null;
        InChIOptionParser parser = new InChIOptionParser();
        for (String str : strs)
            parser.processString(str);
        return parser.options.build();
    }
}
