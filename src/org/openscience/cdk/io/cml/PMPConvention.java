/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 1997-2006  Egon Willighagen <egonw@users.sf.net>
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

import org.openscience.cdk.io.cml.cdopi.IChemicalDocumentObject;
import org.xml.sax.Attributes;

/***
 *  Implementation of the PMPMol Covention for CML.
 *
 *  <p>PMP stands for PolyMorph Predictor and is a module
 *  of Cerius2 (tm).
 *
 * @cdk.module io
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 */
public class PMPConvention extends CMLCoreModule {

    public PMPConvention(IChemicalDocumentObject cdo) {
        super(cdo);
    }

    public PMPConvention(ICMLModule conv) {
        super(conv);
        logger.debug("New PMP Convention!");
    }

    public IChemicalDocumentObject returnCDO() {
        return this.cdo;
    }

    public void startDocument() {
        super.startDocument();
        cdo.startObject("Frame");
    }

    public void endDocument() {
        cdo.endObject("Frame");
        super.endDocument();
    }
    
    
    public void startElement(CMLStack xpath, String uri, String local, String raw, Attributes atts) {
        logger.debug("PMP element: name");
        super.startElement(xpath, uri, local, raw, atts);
    }

    public void endElement(CMLStack xpath, String uri, String local, String raw) {
        super.endElement(xpath, uri, local, raw);
    }

    public void characterData(CMLStack xpath, char ch[], int start, int length) {
        String s = new String(ch, start, length).trim();
        logger.debug("Start PMP chardata (" + CurrentElement + ") :" + s);
        logger.debug(" ElTitle: " + elementTitle);
        if (xpath.toString().endsWith("string/") && BUILTIN.equals("spacegroup")) {
            String sg = "P1";
            // standardize space group names (see Crystal.java)
            if ("P 21 21 21 (1)".equals(s)) {
                sg = "P 2_1 2_1 2_1";
            }
            cdo.setObjectProperty("Crystal", "spacegroup", sg);
        } else if (xpath.toString().endsWith("floatArray/") &&
           (elementTitle.equals("a") || elementTitle.equals("b") ||
            elementTitle.equals("c"))) {
            String axis = elementTitle + "-axis";
            cdo.startObject(axis);
            try {
                StringTokenizer st = new StringTokenizer(s);
                logger.debug("Tokens: " + st.countTokens());
                if (st.countTokens() > 2) {
                    String token = st.nextToken();
                    logger.debug("FloatArray (Token): " + token);
                    cdo.setObjectProperty(axis, "x", token);
                    token = st.nextToken();
                    logger.debug("FloatArray (Token): " + token);
                    cdo.setObjectProperty(axis, "y", token);
                    token = st.nextToken();
                    logger.debug("FloatArray (Token): " + token);
                    cdo.setObjectProperty(axis, "z", token);
                } else {
                    logger.debug("PMP Convention error: incorrect number of cell axis fractions!\n");
                }
            } catch (Exception e) {
                logger.debug("PMP Convention error: " + e.toString());
            }
            cdo.endObject(axis);
        } else {
            super.characterData(xpath, ch, start, length);
        }
        logger.debug("End PMP chardata");
    }
}
