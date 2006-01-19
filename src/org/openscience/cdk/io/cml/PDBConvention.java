/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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

/**
 * Implements the PDB convention used by PDB2CML.
 *
 * <p>This is a lousy implementation, though. Problems that will arise:
 * <ul>
 *   <li>when this new convention is adopted in the root element no
 *     currentFrame was set. This is done when <list sequence=""> is found
 *   <li>multiple sequences are not yet supported
 *   <li>the frame is now added when the doc is ended, which will result in problems
 *     but work for one sequence files made by PDB2CML v.??
 * <ul>
 *
 * <p>What is does:
 * <ul>
 *   <li>work for now
 *   <li>give an idea on the API of the plugable CML import filter
 *     (a real one will be made)
 *   <li>read CML files generated with Steve Zara's PDB 2 CML converter
 *     (of which version 1999 produces invalid CML 1.0)
 * </ul>
 *
 * @cdk.module io
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 */
public class PDBConvention extends CMLCoreModule {

    private boolean connectionTable;
    private boolean isELSYM;
    private boolean isBond;
    private String connect_root;

    public PDBConvention(IChemicalDocumentObject cdo) {
        super(cdo);
    }
    
    public PDBConvention(ICMLModule conv) {
        super(conv);
    }

    public IChemicalDocumentObject returnCDO() {
        return this.cdo;
    }
    
    public void startDocument() {
        super.startDocument();
        cdo.startObject("Frame");
        cdo.startObject("Molecule");
    }
    
    public void endDocument() {
        storeData();
        cdo.endObject("Molecule");
        cdo.endObject("Frame");
        super.endDocument();
    }
    
    public void startElement(CMLStack xpath, String uri, String local, String raw, 
                              Attributes atts) {
        String name = raw;
        isELSYM = false;

        if ("list".equals(name)) {

            for (int i = 0; i < atts.getLength(); i++) {

                if (atts.getQName(i).equals("title") && 
                    atts.getValue(i).equals("sequence")) {
                } else if (atts.getQName(i).equals("title") && 
                         atts.getValue(i).equals("connections")) {
                    // assume that Atom's have been read
                    logger.debug("Assuming that Atom's have been read: storing them");
                    super.storeAtomData();
                    connectionTable = true;
                    logger.debug("Start Connection Table");
                } else if (atts.getQName(i).equals("title") && 
                         atts.getValue(i).equals("connect")) {
                    logger.debug("New connection");
                    isBond = true;
                } else if (atts.getQName(i).equals("id") && isBond) {
                    connect_root = atts.getValue(i);
                }

                // ignore other list items at this moment
            }
        } else {
            super.startElement(xpath, uri, local, raw, atts);
        }
    }
    
    public void endElement(CMLStack xpath, String uri, String local, String raw) {

        String name = raw;

        if (name.equals("list") && connectionTable && !isBond) {
            logger.debug("End Connection Table");
            connectionTable = false;
        }

        isELSYM = false;
        isBond = false;
        super.endElement(xpath, uri, local, raw);
    }

    public void characterData(CMLStack xpath, char[] ch, int start, int length) {

        String s = new String(ch, start, length).trim();

        if (isELSYM) {
            elsym.addElement(s);
        } else if (isBond) {
            logger.debug("CD (bond): " + s);

            if (connect_root.length() > 0) {

                StringTokenizer st = new StringTokenizer(s);

                while (st.hasMoreElements()) {

                    String atom = (String)st.nextElement();

                    if (!atom.equals("0")) {
                        logger.debug("new bond: " + connect_root + "-" + 
                                     atom);
                        cdo.startObject("Bond");

                        int atom1 = Integer.parseInt(connect_root) - 1;
                        int atom2 = Integer.parseInt(atom) - 1;
                        cdo.setObjectProperty("Bond", "atom1", 
                                              (new Integer(atom1)).toString());
                        cdo.setObjectProperty("Bond", "atom2", 
                                              (new Integer(atom2)).toString());
                        cdo.setObjectProperty("Bond", "order", "1");
                        cdo.endObject("Bond");
                    }
                }
            }
        } else {
            super.characterData(xpath, ch, start, length);
        }
    }
}
