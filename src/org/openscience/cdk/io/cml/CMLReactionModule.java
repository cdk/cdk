/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2003-2007  Egon Willighagen <egonw@users.sf.net>
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

    private String objectType;

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
            String id = atts.getValue("id");
            if(id != null)
            	cdo.setObjectProperty("Reaction", "id", id);
        } else if ("reactionList".equals(local)) {
            cdo.startObject("ReactionSet");
            String id = atts.getValue("id");
            if(id != null)
            	cdo.setObjectProperty("reactionList", "id", id);
        } else if ("reactant".equals(local)) {
            cdo.startObject("Reactant");
            objectType = "Reactant";
            String id = atts.getValue("id");
            if(id != null)
            	cdo.setObjectProperty("Reactant", "id", id);
        } else if ("product".equals(local)) {
            cdo.startObject("Product");
            objectType = "Product";
            String id = atts.getValue("id");
            if(id != null)
            	cdo.setObjectProperty("Product", "id", id);
        } else if ("substance".equals(local)) {
            cdo.startObject("Agent");
            objectType = "Agent";
            String id = atts.getValue("id");
            if(id != null)
            	cdo.setObjectProperty("Agent", "id", id);
        } else if ("molecule".equals(local)) {
            // do nothing for now
            super.newMolecule();
            String id = atts.getValue("id");
            if(id != null)
                cdo.setObjectProperty(objectType, "id", id);
            
        } else {
            super.startElement(xpath, uri, local, raw, atts);
        }
    }

    public void endElement(CMLStack xpath, String uri, String local, String raw) {
        if ("reaction".equals(local)) {
            cdo.endObject("Reaction");
        } else if ("reactionList".equals(local)) {
            cdo.endObject("ReactionSet");
        } else if ("reactant".equals(local)) {
            cdo.endObject("Reactant");
        } else if ("product".equals(local)) {
            cdo.endObject("Product");
        } else if ("substance".equals(local)) {
            cdo.endObject("Agent");
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
