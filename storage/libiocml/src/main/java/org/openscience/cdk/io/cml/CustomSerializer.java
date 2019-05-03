/* Copyright (C) 2009  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.io.cml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import nu.xom.Serializer;

/**
 * Custom {@link Serializer} with the sole purpose and functionality to not
 * output the XML declaration.
 *
 * @author     egonw
 * @cdk.module libiocml
 * @cdk.githash
 */
public class CustomSerializer extends Serializer {

    /**
     * Instantiates a new {@link CustomSerializer} using the matching
     * {@link Serializer#Serializer(OutputStream)}.
     *
     * @param out the output stream to write the document on
     */
    public CustomSerializer(OutputStream out) {
        super(out);
    }

    /**
     * Instantiates a new {@link CustomSerializer} using the matching
     * {@link Serializer#Serializer(OutputStream, String)}.
     *
     * @param out      the output stream to write the document on
     * @param encoding the character encoding for the serialization
     */
    public CustomSerializer(OutputStream out, String encoding) throws UnsupportedEncodingException {
        super(out, encoding);
    }

    /**
     * Overwrite the {@link Serializer#writeXMLDeclaration()} method, and have
     * it not output the XML declaration.
     */
    @Override
    protected void writeXMLDeclaration() throws IOException {
        // do nothing
    }

}
