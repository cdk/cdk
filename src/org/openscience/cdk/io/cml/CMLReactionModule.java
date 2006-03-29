/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.io.cml;

import org.openscience.cdk.io.cml.cdopi.IChemicalDocumentObject;
import org.xml.sax.Attributes;

/**
 * @author Egon Willighagen <elw38@cam.ac.uk>
 *
 * @cdk.module io
 */
public class CMLReactionModule extends CMLCoreModule {

    public CMLReactionModule(IChemicalDocumentObject cdo) {
        super(cdo);
    }

    public CMLReactionModule(ICMLModule conv) {
        super(conv);
        logger.debug("New CML-Reaction Module!");
    }

    public IChemicalDocumentObject returnCDO() {
        return this.cdo;
    }

    public void startDocument() {
        super.startDocument();
    }

    public void endDocument() {
        super.endDocument();
    }
    
    public void startElement(CMLStack xpath, String uri, String local, String raw, Attributes atts) {
        if ("reaction".equals(local)) {
            cdo.startObject("Reaction");
            for (int i = 0; i < atts.getLength(); i++) {
                String att = atts.getQName(i);
                String value = atts.getValue(i);
                if (att.equals("id")) {
                    cdo.setObjectProperty("Reaction", "id", value);
                }
            }
        } else if ("reactionList".equals(local)) {
            cdo.startObject("SetOfReactions");
            for (int i = 0; i < atts.getLength(); i++) {
                String att = atts.getQName(i);
                String value = atts.getValue(i);
                if (att.equals("id")) {
                    cdo.setObjectProperty("SetOfReactions", "id", value);
                }
            }
        } else if ("reactant".equals(local)) {
            cdo.startObject("Reactant");
            for (int i = 0; i < atts.getLength(); i++) {
                String att = atts.getQName(i);
                String value = atts.getValue(i);
                if (att.equals("id")) {
                    cdo.setObjectProperty("Reactant", "id", value);
                }
            }
        } else if ("product".equals(local)) {
            cdo.startObject("Product");
            for (int i = 0; i < atts.getLength(); i++) {
                String att = atts.getQName(i);
                String value = atts.getValue(i);
                if (att.equals("id")) {
                    cdo.setObjectProperty("Product", "id", value);
                }
            }
        } else if ("molecule".equals(local)) {
            // do nothing for now
            super.newMolecule();
        } else {
            super.startElement(xpath, uri, local, raw, atts);
        }
    }

    public void endElement(CMLStack xpath, String uri, String local, String raw) {
        if ("reaction".equals(local)) {
            cdo.endObject("Reaction");
        } else if ("reactionList".equals(local)) {
            cdo.endObject("SetOfReactions");
        } else if ("reactant".equals(local)) {
            cdo.endObject("Reactant");
        } else if ("product".equals(local)) {
            cdo.endObject("Product");
        } else if ("molecule".equals(local)) {
            logger.debug("Storing Molecule");
            super.storeData();
            // do nothing else but store atom/bond information
        } else {
            super.endElement(xpath, uri, local, raw);
        }
    }

    public void characterData(CMLStack xpath, char ch[], int start, int length) {
        super.characterData(xpath, ch, start, length);
    }
}
