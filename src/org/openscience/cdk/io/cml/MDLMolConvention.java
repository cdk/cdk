/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.io.cml;

import java.util.*;
import org.xml.sax.*;
import org.openscience.cdk.io.cml.cdopi.*;

/**
 * Implementation of the MDLMol Covention for CML.
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 */
public class MDLMolConvention extends CMLCoreModule {

    public MDLMolConvention(CDOInterface cdo) {
        super(cdo);
    };

    public MDLMolConvention(ModuleInterface conv) {
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

    public void startElement(Stack xpath, String uri, String local, String raw, Attributes atts) {
        logger.debug("MDLMol element: name");
        super.startElement(xpath, uri, local, raw, atts);
    };

    public void endElement(Stack xpath, String uri, String local, String raw) {
        super.endElement(xpath, uri, local, raw);
    }

    public void characterData(Stack xpath, char ch[], int start, int length) {
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
            super.characterData(xpath, ch, start, length);
        }
    }
}
