/*
 * @(#)CMLHandler.java   0.2 2000/01/06
 *
 * Information can be found at http://www.openscience.org/egonw/cml/
 *
 * Copyright (c) 2000 E.L. Willighagen (egonw@sci.kun.nl)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 **/

package org.openscience.cdk.io.cml;

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Vector;
import org.openscience.cdk.io.cml.cdopi.CDOInterface;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class CMLHandler extends DefaultHandler {
    private Convention conv;
    private org.openscience.cdk.tools.LoggingTool logger;

    private Hashtable userConventions;

    public CMLHandler(CDOInterface cdo) {
        logger = new org.openscience.cdk.tools.LoggingTool(
                       this.getClass().getName());
        conv = new Convention(cdo);
        userConventions = new Hashtable();
    }

    public void registerConvention(String convention, Convention conv) {
      userConventions.put(convention, conv);
    }

    public void characters(char ch[], int start, int length) {
       logger.debug("character data");
       conv.characterData(ch, start, length);
    }

    public void doctypeDecl(String name, String publicId, String systemId) throws Exception {}

    public void endDocument() {
        conv.endDocument();
    }

    public void endElement(String uri, String local, String raw) {
       logger.debug("end element: " + raw);
       conv.endElement(uri, local, raw);
    }

    public CDOInterface returnCDO() {
        return conv.returnCDO();
    }

    public void startDocument() {
        conv.startDocument();
    }

    public void startElement(String uri, String local, String raw, Attributes atts) {
      logger.debug("startElement: " + raw);
        logger.debug("uri: " + uri);
        logger.debug("local: " + local);
        logger.debug("raw: " + raw);
        String name = raw;
        for (int i = 0; i < atts.getLength(); i++)
        {
            if (atts.getQName(i).equals("convention"))
            {
                logger.info(new StringBuffer("New Convention: ").append(atts.getValue(i)).toString());
                if (atts.getValue(i).equals("CML")) {
                    logger.debug("Doing nothing");
                } else if (atts.getValue(i).equals("PDB")) {
                    conv = new PDBConvention(conv);
		} else if (atts.getValue(i).equals("PMP")) {
                    conv = new PMPConvention(conv);
		} else if (atts.getValue(i).equals("MDLMol")) {
		    logger.debug("MDLMolConvetion instantiated...");
                    conv = new MDLMolConvention(conv);
                } else {
                    //unknown convention. userConvention?
                    if (userConventions.containsKey(atts.getValue(i))) {
                      Convention newconv = (Convention)userConventions.get(atts.getValue(i));
                      newconv.inherit(conv);
                      conv = newconv;
                    }
                }
            }
        }
        conv.startElement(uri, local, raw, atts);
    }

}
