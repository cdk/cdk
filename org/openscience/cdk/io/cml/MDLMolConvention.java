/*
 * @(#)MDLMolConvention.java   0.2 2000/12/18
 *
 * Information can be found at http://www.openscience.org/~egonw/cml/
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
 
/*** 
 *  Implementation of the MDLMol Covention for CML.
 */

package org.openscience.cdk.io.cml;

import java.util.*;
import org.xml.sax.*;
import org.openscience.cdk.io.cml.cdopi.*;

public class MDLMolConvention extends Convention {

    public MDLMolConvention(CDOInterface cdo) {
        super(cdo);
    };

    public MDLMolConvention(Convention conv) {
        super(conv);
    }

    public CDOInterface returnCDO() {
        return this.cdo;
    };

    public void startDocument() {
        super.startDocument();
        cdo.startObject("Frame");
    };

    public void endDocument() {
        cdo.endObject("Frame");
        super.endDocument();
    };

    public void startElement (String uri, String local, String raw, Attributes atts) {
        logger.debug("MDLMol element: name");
        super.startElement(uri, local, raw, atts);
    };

    public void endElement (String uri, String local, String raw) {
        super.endElement(uri, local, raw);
    }

    public void characterData (char ch[], int start, int length) {
        String s = new String(ch, start, length).trim();
        if (CurrentElement == STRING && BUILTIN.equals("stereo")) {
            stereoGiven = true;
            if (s.trim().equals("W")) {
                logger.debug("CML W stereo found");
                bondStereo.addElement("1");
            } else if (s.trim().equals("H")) {
                logger.debug("CML H stereo found");
                bondStereo.addElement("6");
            }
        } else {
            super.characterData(ch, start, length);
        }
    }
}
