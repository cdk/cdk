/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2002-2007  Egon Willighagen <egonw@users.sf.net>
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

import java.util.StringTokenizer;

import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.tools.manipulator.BondManipulator;
import org.xml.sax.Attributes;

/**
 * This is an implementation for the CDK convention.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 * 
 * @author egonw
 */
public class CDKConvention extends CMLCoreModule {

    private boolean isBond;

    public CDKConvention(IChemFile chemFile) {
        super(chemFile);
    }

    public CDKConvention(ICMLModule conv) {
        super(conv);
    }
    
    public void startDocument() {
        super.startDocument();
        isBond = false;
    }

    public void startElement(CMLStack xpath, String uri, String local, String raw, Attributes atts) {
        isBond = false;
        if (xpath.toString().endsWith("string/")) {
            for (int i = 0; i < atts.getLength(); i++) {
                if (atts.getQName(i).equals("buildin") &&
                    atts.getValue(i).equals("order")) {
                    isBond = true;
                }
            }
        } else {
            super.startElement(xpath, uri, local, raw, atts);
        }
    }

    public void characterData(CMLStack xpath, char ch[], int start, int length) {
        String s = new String(ch, start, length).trim();
        if (isBond) {
            logger.debug("CharData (bond): " + s);
            StringTokenizer st = new StringTokenizer(s);
            while (st.hasMoreElements()) {
                String border = (String)st.nextElement();
                logger.debug("new bond order: " + border);
                // assume cdk bond object has already started
//                cdo.setObjectProperty("Bond", "order", border);
                currentBond.setOrder(
                	BondManipulator.createBondOrder(Double.parseDouble(border))
                );
            }
        } else {
            super.characterData(xpath, ch, start, length);
        }
    }
}
