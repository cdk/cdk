/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2003 The Chemistry Development Kit (CDK) project
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
 * This is an implementation for the CDK convention.
 * 
 * @author egonw
 */
public class CDKConvention extends CMLCoreModule {

    private boolean isBond;

    public CDKConvention(CDOInterface cdo) {
        super(cdo);
    };

    public CDKConvention(ModuleInterface conv) {
        super(conv);
    }
    
    public CDOInterface returnCDO() {
        return this.cdo;
    };

    public void startDocument() {
        super.startDocument();
        isBond = false;
    };

    public void endDocument() {
        super.endDocument();
    };

    public void startElement(Stack xpath, String uri, String local, String raw, Attributes atts) {
        String name = raw;
        setCurrentElement(name);
        isBond = false;
        if (CurrentElement == STRING) {
            for (int i = 0; i < atts.getLength(); i++) {
                if (atts.getQName(i).equals("buildin") &&
                    atts.getValue(i).equals("order")) {
                    isBond = true;
                }
            }
        } else {
            super.startElement(xpath, uri, local, raw, atts);
        }
    };

    public void endElement (Stack xpath, String uri, String local, String raw) {
        super.endElement(xpath, uri, local, raw);
    }

    public void characterData(Stack xpath, char ch[], int start, int length) {
        String s = new String(ch, start, length).trim();
        if (isBond) {
            logger.debug("CharData (bond): " + s);
            StringTokenizer st = new StringTokenizer(s);
            while (st.hasMoreElements()) {
                String border = (String)st.nextElement();
                logger.debug("new bond order: " + border);
                // assume cdk bond object has already started
                cdo.setObjectProperty("Bond", "order", border);
            }
        } else {
            super.characterData(xpath, ch, start, length);
        }
    }
}
