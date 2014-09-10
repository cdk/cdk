/* Copyright (C) 2014  Egon Willighagen <egonw@users.sf.net>
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

import org.openscience.cdk.interfaces.IChemFile;
import org.xml.sax.Attributes;

/**
 * Modules that ignore all content, for use in XML in namespaces other than CML.
 *
 * @author egonw
 */
final class OtherNamespace implements ICMLModule {

    /** {@inheritDoc} */
    @Override
    public void startDocument() {
        // ignore content from other namespaces
    }

    /** {@inheritDoc} */
    @Override
    public void endDocument() {
        // ignore content from other namespaces
    }

    /** {@inheritDoc} */
    @Override
    public void startElement(CMLStack xpath, String uri, String local, String raw, Attributes atts) {
        // ignore content from other namespaces
    }

    /** {@inheritDoc} */
    @Override
    public void endElement(CMLStack xpath, String uri, String local, String raw) {
        // ignore content from other namespaces
    }

    /** {@inheritDoc} */
    @Override
    public void characterData(CMLStack xpath, char[] ch, int start, int length) {
        // ignore content from other namespaces
    }

    /** {@inheritDoc} */
    @Override
    public IChemFile returnChemFile() {
        // ignore content from other namespaces
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void inherit(ICMLModule conv) {}

}
