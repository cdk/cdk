/*
 * $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 *
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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

/***
 *  Implementation of the PMPMol Covention for CML.
 *
 *  PMP stands for PolyMorph Predictor and is a module
 *  of Cerius (tm).
 */
public class PMPConvention extends Convention {

    public PMPConvention(CDOInterface cdo) {
	super(cdo);
    };
  
    public PMPConvention(Convention conv) {
	super(conv);
	logger.debug("New PMP Convention!");
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
        logger.debug("PMP element: name");
	super.startElement(uri, local, raw, atts);
    };

    public void endElement(String uri, String local, String raw) {
	super.endElement(uri, local, raw);
    }

    public void characterData (char ch[], int start, int length) {
	String s = new String(ch, start, length).trim();
	logger.debug("Start PMP chardata (" + CurrentElement + ") :" + s);
	logger.debug(" ElTitle: " + elementTitle);
	if (CurrentElement == STRING && BUILTIN.equals("spacegroup")) {
	    cdo.setObjectProperty("Crystal", "spacegroup", s);
	} else if (CurrentElement == FLOATARRAY && 
		   (elementTitle.equals("a") ||
		    elementTitle.equals("b") ||
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
	    super.characterData(ch, start, length);
	}
	logger.debug("End PMP chardata");
    }
}
