/*
 * @(#)PDBConvention.java   0.3 2000/12/05
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
 * This is a lousy implementation for the PDB convention:
 * 
 * Problems that will arise:
 *   - when this new convention is adopted in the root element no
 *     currentFrame was set. This is done when <list sequence=""> is found
 *   - multiple sequences are not yet supported
 *   - the frame is now added when the doc is ended, which will result in problems
 *     but work for one sequence files made by PDB2CML v.??
 *
 * What is does:
 *   - work for now
 *   - give an idea on the API of the plugable CML import filter
 *     (a real one will be made)
 *   - read CML files generated with Steve Zara's PDB 2 CML converter
 *
 */

package org.openscience.cdk.io.cml;

import java.util.*;
import org.xml.sax.*;
import org.openscience.cdk.io.cml.cdopi.*;

public class PDBConvention extends Convention {

    private boolean connectionTable;

    private boolean isELSYM;
    private boolean isBond;

    private String connect_root;

    public PDBConvention(CDOInterface cdo) {
	super(cdo);
    };
  
    public PDBConvention(Convention conv) {
	super(conv);
    }
    
    public CDOInterface returnCDO() {
	return this.cdo;
    };
  
    public void startDocument() {
	super.startDocument();
	cdo.startObject("Frame");
	cdo.startObject("Molecule");
    };

    public void endDocument() {
	storeData();
	cdo.endObject("Molecule");
	cdo.endObject("Frame");
	super.endDocument();
    };
    
    
    public void startElement(String uri, String local, String raw, Attributes atts) {
	String name = raw;
	isELSYM = false;
	setCurrentElement(name);
	if (CurrentElement == Convention.LIST) {
	    for (int i = 0; i < atts.getLength(); i++) {
		if (atts.getQName(i).equals("title") && 
                    atts.getValue(i).equals("sequence")) {
		} else if (atts.getQName(i).equals("title") && 
                           atts.getValue(i).equals("connections")) {
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
	    super.startElement(uri, local, raw, atts);
	}
    };

    public void endElement (String uri, String local, String raw) {
	String name = raw;
	if (name.equals("list") && connectionTable && !isBond) {
	    logger.debug("End Connection Table");
	    connectionTable = false;
	}
	isELSYM = false;
        isBond = false;
	super.endElement(uri, local, raw);
    }

    public void characterData (char ch[], int start, int length) {
	String s = toString(ch, start, length).trim();
	if (isELSYM) {
	    elsym.addElement(s);
	} else if (isBond) {
	    logger.debug("CD (bond): " + s);
            if (connect_root.length() > 0) {
		StringTokenizer st = new StringTokenizer(s);
		while (st.hasMoreElements()) {
		    String atom = (String)st.nextElement();
		    if (!atom.equals("0")) {
			logger.debug("new bond: " + connect_root + "-" + atom);
			cdo.startObject("Bond");
                        int atom1 = Integer.parseInt(connect_root) - 1;
                        int atom2 = Integer.parseInt(atom) - 1;
			cdo.setObjectProperty("Bond", "atom1", (new Integer(atom1)).toString());
			cdo.setObjectProperty("Bond", "atom2", (new Integer(atom2)).toString());
			cdo.setObjectProperty("Bond", "order", "1");
			cdo.endObject("Bond");
		    }
		}
	    }
	} else {
	    super.characterData(ch, start, length);
	}
    }
}
