/*
 * @(#)Convention.java   0.2 2000/03/19
 *
 * Information can be found at http://www.openscience.org/~egonw/cdopi/
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
 */

package org.openscience.cdk.io.cml;

import java.util.*;
import org.xml.sax.*;
import org.openscience.cdk.io.cml.cdopi.*;

public class Convention implements ConventionInterface {

    protected org.openscience.cdk.tools.LoggingTool logger;

    public final static int UNKNOWN = -1;
    
    public final static int STRING = 1;
    public final static int LINK = 2;
    public final static int FLOAT = 3;
    public final static int INTEGER = 4;
    public final static int STRINGARRAY = 5;
    public final static int FLOATARRAY = 6;
    public final static int INTEGERARRAY = 7;
    public final static int FLOATMATRIX = 8;
    public final static int COORDINATE2 = 9;
    public final static int COORDINATE3 = 10;
    public final static int ANGLE = 11;
    public final static int TORSION = 12;
    public final static int LIST = 13;
    public final static int MOLECULE = 14;
    public final static int ATOM = 15;
    public final static int ATOMARRAY = 16;
    public final static int BOND = 17;
    public final static int BONDARRAY = 18;
    public final static int ELECTRON = 19;
    public final static int REACTION = 20;
    public final static int CRYSTAL = 21;
    public final static int SEQUENCE = 22;
    public final static int FEATURE = 23;
    
    protected final String SYSTEMID = "CML-1999-05-15";
    
    protected CDOInterface cdo;
    
    protected Vector elsym;
    protected Vector elid;
    protected Vector elcharge;

    protected Vector x3;
    protected Vector y3;
    protected Vector z3;
  
    protected Vector x2;
    protected Vector y2;

    protected Vector bondid;
    protected Vector bondARef1;
    protected Vector bondARef2;
    protected Vector order;
    protected Vector bondStereo;
    protected boolean stereoGiven;

    protected int curRef;

    protected int CurrentElement;
    protected String BUILTIN;
    protected String elementTitle;

    public Convention(CDOInterface cdo) {
        logger = new org.openscience.cdk.tools.LoggingTool();
	this.cdo = cdo;
  };
  
    public Convention(Convention conv) {
	inherit(conv);
    }

    public void inherit(Convention conv) {
        this.logger = conv.logger;
	this.cdo = conv.returnCDO();
	this.BUILTIN = conv.BUILTIN;
	this.elsym = conv.elsym;
	this.elid = conv.elid;
	this.elcharge = conv.elcharge;
	this.x3 = conv.x3;
	this.y3 = conv.y3;
	this.z3 = conv.z3;
	this.x2 = conv.x2;
	this.y2 = conv.y2;
	this.bondid = conv.bondid;
	this.bondARef1 = conv.bondARef1;
	this.bondARef2 = conv.bondARef2;
	this.order = conv.order;
	this.bondStereo = conv.bondStereo;
	this.curRef = conv.curRef;
    } 
  
    public CDOInterface returnCDO() {
	return (CDOInterface)this.cdo;
    };
  
    public void startDocument() {
	logger.debug("Start Doc");
	elsym = new Vector();
	elid = new Vector();
	elcharge = new Vector();
	x3 = new Vector();
	y3 = new Vector();
	z3 = new Vector();
	x2 = new Vector();
	y2 = new Vector();
	bondid = new Vector();
	bondARef1 = new Vector();
	bondARef2 = new Vector();
	order = new Vector();
	bondStereo = new Vector();
	BUILTIN = "";
	curRef = 0;
    };

    public void endDocument() {
	logger.debug("End Doc");
    };


    public void startElement (String uri, String local, String raw, Attributes atts) {
	String name = raw;
	logger.debug("StartElement");
	setCurrentElement(name);
	switch (CurrentElement) {
	case ATOM :
	    logger.debug("T1");
	    for (int i = 0; i < atts.getLength(); i++) {
		logger.debug("T2");
		if (atts.getQName(i).equals("id")) {
		    logger.debug("T3 " + atts.getValue(i));
		    elid.addElement(atts.getValue(i));
		    logger.debug("T3 " + elid);          
		}
	    }
	    logger.debug("T4");
	    break;
	case BOND :        
	    logger.debug("B1");
	    for (int i = 0; i < atts.getLength(); i++) {
		logger.debug("B2 " + atts.getQName(i) + "=" + atts.getValue(i));          
		if (atts.getQName(i).equals("id")) {
		    bondid.addElement(atts.getValue(i));
		    logger.debug("B3 " + bondid);
		}
	    }
	    stereoGiven = false;
	    logger.debug("B4");
	    curRef = 0;
	    break;
	case COORDINATE2 :
	    for (int i = 0; i < atts.getLength(); i++) {
		if (atts.getQName(i).equals("builtin")) {
		    BUILTIN = atts.getValue(i);
		    logger.debug("Valid element coord found, builtin: " + atts.getValue(i));
		}
	    }
	    break;
	case COORDINATE3 :
	    for (int i = 0; i < atts.getLength(); i++) {
		if (atts.getQName(i).equals("builtin")) {
		    BUILTIN = atts.getValue(i);
		}
	    }
	    break;
	case STRING :
	    for (int i = 0; i < atts.getLength(); i++) {
		if (atts.getQName(i).equals("builtin")) {
		    BUILTIN = atts.getValue(i); 
		} else if (atts.getQName(i).equals("title")) {
		    elementTitle = atts.getValue(i);
		}
	    }
	    break;
	case FLOAT :
	    for (int i = 0; i < atts.getLength(); i++) {
		if (atts.getQName(i).equals("builtin")) {
		    BUILTIN = atts.getValue(i); 
		} else if (atts.getQName(i).equals("title")) {
		    elementTitle = atts.getValue(i);
		}
	    }
	    break;
	case ATOMARRAY :       
	    break;	
	case INTEGERARRAY :       
	    for (int i = 0; i < atts.getLength(); i++) {
		if (atts.getQName(i).equals("builtin"))
		    BUILTIN = atts.getValue(i);
	    }
	    break;	
	case STRINGARRAY :       
	    for (int i = 0; i < atts.getLength(); i++) {
		if (atts.getQName(i).equals("builtin"))
		    BUILTIN = atts.getValue(i);
	    }
	    break;	
	case FLOATARRAY :       
	    for (int i = 0; i < atts.getLength(); i++) {
		if (atts.getQName(i).equals("builtin"))
		    BUILTIN = atts.getValue(i);
		if (atts.getQName(i).equals("title"))
		    elementTitle = atts.getValue(i);
	    }
	    break;	
	case MOLECULE :
	    elsym = new Vector();
	    elid = new Vector();
	    x3 = new Vector();
	    y3 = new Vector();
	    z3 = new Vector();
	    BUILTIN = "";
	    cdo.startObject("Molecule");
	    cdo.startObject("Frame");
	    for (int i = 0; i < atts.getLength(); i++) {
		if (atts.getQName(i).equals("id")) cdo.setObjectProperty("Frame", "title", atts.getValue(i));
	    }
	    break;
	case CRYSTAL:
	    cdo.startObject("Crystal");
	    break;
	case LIST :
	    break;
	}
    }
    
    public void endElement(String uri, String local, String raw) {
	String name = raw;
	logger.debug("EndElement");
	setCurrentElement(name);
	BUILTIN = "";
	switch (CurrentElement) {
	case BOND :
	    if (!stereoGiven) bondStereo.addElement("");
	    break;
	case MOLECULE :
	    storeData();
	    cdo.endObject("Frame");
	    cdo.endObject("Molecule");
	    break;
	}
    }
    
    public void characterData (char ch[], int start, int length) {
	// logger.debug("CD");
	String s = toString(ch, start, length).trim();
	switch (CurrentElement) {
	case STRING :
	    logger.debug("Builtin: " + BUILTIN);
	    if (BUILTIN.equals("elementType")) {
		logger.debug("Element: " + s.trim());
		elsym.addElement(s);	
	    } else if (BUILTIN.equals("atomRef")) {
		curRef++;
		logger.debug("Bond: ref #" + curRef);
		if (curRef == 1) {
		    bondARef1.addElement(s.trim());
		} else if (curRef == 2) {
		    bondARef2.addElement(s.trim());
		}
	    } else if (BUILTIN.equals("order")) {
		logger.debug("Bond: order " + s.trim());
		order.addElement(s.trim());
	    } else if (BUILTIN.equals("formalCharge")) {
		logger.debug("Charge: " + s.trim());
		elcharge.addElement(s.trim());
	    }
	    break;
	case FLOAT :
	    if (BUILTIN.equals("x3")) {
		x3.addElement(s.trim());
	    } else if (BUILTIN.equals("y3")) {
		y3.addElement(s.trim());
	    } else if (BUILTIN.equals("z3")) {
		z3.addElement(s.trim());
	    } else if (BUILTIN.equals("x2")) {
		x2.addElement(s.trim());
	    } else if (BUILTIN.equals("y2")) {
		y2.addElement(s.trim());
	    }
	    break;
	case COORDINATE2 :
	    if (BUILTIN.equals("xy2")) {
		logger.debug("New coord found." + s);
		try {	  
		    StringTokenizer st = new StringTokenizer(s);
		    x2.addElement(st.nextToken());
		    y2.addElement(st.nextToken());
		} catch (Exception e) {
		    notify("CMLParsing error: " + e, SYSTEMID, 175,1);
		}
	    }
	    break;
	case COORDINATE3 :
	    if (BUILTIN.equals("xyz3")) {
		logger.debug("New coord found." + s);
		try {	  
		    StringTokenizer st = new StringTokenizer(s);
		    x3.addElement(st.nextToken());
		    y3.addElement(st.nextToken());
		    z3.addElement(st.nextToken());
		} catch (Exception e) {
		    notify("CMLParsing error: " + e, SYSTEMID, 175,1);
		}
	    }
	    break;
	case STRINGARRAY :
	    if (BUILTIN.equals("id")) {
		try {	  
		    StringTokenizer st = new StringTokenizer(s);
		    while (st.hasMoreTokens()) {
			String token = st.nextToken();
			logger.debug("StringArray (Token): " + token);
			elid.addElement(token);
		    }
		} catch (Exception e) {
		    notify("CMLParsing error: " + e, SYSTEMID, 186,1);
		}
	    } else if (BUILTIN.equals("elementType")) {
		try {	  
		    StringTokenizer st = new StringTokenizer(s);
		    while (st.hasMoreTokens()) elsym.addElement(st.nextToken());
		} catch (Exception e) {
		    notify("CMLParsing error: " + e, SYSTEMID, 194,1);
		}
	    } else if (BUILTIN.equals("atomRefs")) {
		curRef++;
		logger.debug("New atomRefs found: " + curRef);
		try {	  
		    StringTokenizer st = new StringTokenizer(s);
		    while (st.hasMoreTokens()) {
			String token = st.nextToken();
			logger.debug("Token: " + token);
			if (curRef == 1) {
			    bondARef1.addElement(token);
			} else if (curRef == 2) {
			    bondARef2.addElement(token);
			}
		    }
		} catch (Exception e) {
		    notify("CMLParsing error: " + e, SYSTEMID, 194,1);
		}
	    } else if (BUILTIN.equals("order")) {
		logger.debug("New bond order found.");
		try {	  
		    StringTokenizer st = new StringTokenizer(s);
		    while (st.hasMoreTokens()) {
			String token = st.nextToken();
			logger.debug("Token: " + token);
			order.addElement(token);
		    }
		} catch (Exception e) {
		    notify("CMLParsing error: " + e, SYSTEMID, 194,1);
		}
	    }
	    break;
	case INTEGERARRAY :
	    logger.debug("IntegerArray: builtin = " + BUILTIN);
	    if (BUILTIN.equals("formalCharge")) {
		try {	  
		    StringTokenizer st = new StringTokenizer(s);		    
		    while (st.hasMoreTokens()) {
			String token = st.nextToken();
			logger.debug("Charge added: " + token);
			elcharge.addElement(token);
		    }
		} catch (Exception e) {
		    notify("CMLParsing error: " + e, SYSTEMID, 205,1);
		}
	    }
	case FLOATARRAY :
	    if (BUILTIN.equals("x3")) {
		try {	  
		    StringTokenizer st = new StringTokenizer(s);
		    while (st.hasMoreTokens()) x3.addElement(st.nextToken());
		} catch (Exception e) {
		    notify("CMLParsing error: " + e, SYSTEMID, 205,1);
		}
	    } else if (BUILTIN.equals("y3")) {
		try {	  
		    StringTokenizer st = new StringTokenizer(s);
		    while (st.hasMoreTokens()) y3.addElement(st.nextToken());
		} catch (Exception e) {
		    notify("CMLParsing error: " + e, SYSTEMID, 213,1);
		}
	    } else if (BUILTIN.equals("z3")) {
		try {	  
		    StringTokenizer st = new StringTokenizer(s);
		    while (st.hasMoreTokens()) z3.addElement(st.nextToken());
		} catch (Exception e) {
		    notify("CMLParsing error: " + e, SYSTEMID, 221,1);
		}
	    } else if (BUILTIN.equals("x2")) {
		logger.debug("New floatArray found.");
		try {	  
		    StringTokenizer st = new StringTokenizer(s);
		    while (st.hasMoreTokens()) x2.addElement(st.nextToken());
		} catch (Exception e) {
		    notify("CMLParsing error: " + e, SYSTEMID, 205,1);
		}
	    } else if (BUILTIN.equals("y2")) {
		logger.debug("New floatArray found.");
		try {	  
		    StringTokenizer st = new StringTokenizer(s);
		    while (st.hasMoreTokens()) y2.addElement(st.nextToken());
		} catch (Exception e) {
		    notify("CMLParsing error: " + e, SYSTEMID, 213,1);
		}
	    }
	    break;
	}
    }
    
    protected void setCurrentElement(String name) {
	// logger.debug("CE: " + name);
	if (name.equals("string")) {
	    CurrentElement = STRING;
	} else if (name.equals("link")) {
	    CurrentElement = LINK;
	} else if (name.equals("float")) {
	    CurrentElement = FLOAT;
	} else if (name.equals("integer")) {
	    CurrentElement = INTEGER;
	} else if (name.equals("stringArray")) {
	    CurrentElement = STRINGARRAY;
	} else if (name.equals("floatArray")) {
	    CurrentElement = FLOATARRAY;
	} else if (name.equals("integerArray")) {
	    CurrentElement = INTEGERARRAY;
	} else if (name.equals("floatMatrix")) {
	    CurrentElement = FLOATMATRIX;
	} else if (name.equals("coordinate2")) {
	    CurrentElement = COORDINATE2;
	} else if (name.equals("coordinate3")) {
	    CurrentElement = COORDINATE3;
	} else if (name.equals("angle")) {
	    CurrentElement = ANGLE;
	} else if (name.equals("torsion")) {
	    CurrentElement = TORSION;
	} else if (name.equals("list")) {
	    CurrentElement = LIST;
	} else if (name.equals("molecule")) {
	    CurrentElement = MOLECULE;
	} else if (name.equals("atom")) {
	    CurrentElement = ATOM;
	} else if (name.equals("atomArray")) {
	    CurrentElement = ATOMARRAY;
	} else if (name.equals("bond")) {
	    CurrentElement = BOND;
	} else if (name.equals("bondArray")) {
	    CurrentElement = BONDARRAY;
	} else if (name.equals("electron")) {
	    CurrentElement = ELECTRON;
	} else if (name.equals("reaction")) {
	    CurrentElement = REACTION;
	} else if (name.equals("crystal")) {
	    CurrentElement = CRYSTAL;
	} else if (name.equals("sequence")) {
	    CurrentElement = SEQUENCE;
	} else if (name.equals("feature")) {
	    CurrentElement = FEATURE;
	} else {
	    CurrentElement = UNKNOWN;
	};
    }
    
    protected void notify(String message, String systemId, int line, int column) {
	logger.debug("Message: " + message);
	logger.debug("SystemId: " + systemId);
	logger.debug("Line: " + line);
	logger.debug("Column: " + column);
    }
    
    protected String toString(char ch[], int start, int length) {
	StringBuffer x = new StringBuffer();
	for (int i =0; i < length; i++)
	    x.append(ch[start+i]);
	return x.toString();
    }
    
    protected void storeData() {
      int atomcount = elsym.size();
      boolean has3D = false;
      boolean has2D = false;
      boolean hasCharge = false;
      if ((x3.size() == atomcount) &&
          (y3.size() == atomcount) &&
          (z3.size() == atomcount)) {
        has3D = true;
      } else {
        logger.debug("No 3D info: " + x3.size() + " " + y3.size() +
                       " " + z3.size() + " != " + atomcount);
      }
      if ((x2.size() == atomcount) &&
          (y2.size() == atomcount)) {
        has2D = true;
      } else {
        logger.debug("No 2D info: " + x2.size() + " " + y2.size() +
                       " != " + atomcount);
      }     
      if (elcharge.size() == atomcount) {
        hasCharge = true;
      } else {
        logger.debug("No Charge info: " + elcharge.size() + " != " + atomcount);
      }
      Enumeration atoms = elsym.elements();
      Enumeration ids = elid.elements();
      Enumeration charges = elcharge.elements();
      Enumeration x3s = x3.elements();
      Enumeration y3s = y3.elements();
      Enumeration z3s = z3.elements();
	    Enumeration x2s = x2.elements();
	    Enumeration y2s = y2.elements();
      while (atoms.hasMoreElements()) {
        cdo.startObject("Atom");
        cdo.setObjectProperty("Atom", "id", (String)ids.nextElement());
        cdo.setObjectProperty("Atom", "type", (String)atoms.nextElement());
        if (has3D) {
          cdo.setObjectProperty("Atom", "x3", (String)x3s.nextElement());
          cdo.setObjectProperty("Atom", "y3", (String)y3s.nextElement());
          cdo.setObjectProperty("Atom", "z3", (String)z3s.nextElement());
        }
        // store optional charge
        if (hasCharge) {
          cdo.setObjectProperty("Atom", "charge", (String)charges.nextElement());
        }
        if (has2D) {
          cdo.setObjectProperty("Atom", "x2", (String)x2s.nextElement());
          cdo.setObjectProperty("Atom", "y2", (String)y2s.nextElement());
        }
        cdo.endObject("Atom");
      }
      int bondcount = order.size();
      logger.debug("Testing a1,a2,stereo: " + bondARef1.size() + "," + bondARef2.size() + "," + bondStereo.size() + "=" + order.size());
      if ((bondARef1.size() == bondcount) &&
	      (bondARef2.size() == bondcount)) {	
        logger.debug("About to add bond info to JChemPaintModel.");
        Enumeration orders = order.elements();
        Enumeration bar1s = bondARef1.elements();
        Enumeration bar2s = bondARef2.elements();
        Enumeration stereos = bondStereo.elements();
	      while (orders.hasMoreElements()) {            
          cdo.startObject("Bond");
          cdo.setObjectProperty("Bond", "atom1", new Integer(elid.indexOf((String)bar1s.nextElement())).toString());
          cdo.setObjectProperty("Bond", "atom2", new Integer(elid.indexOf((String)bar2s.nextElement())).toString());
          cdo.setObjectProperty("Bond", "order", (String)orders.nextElement());
          if (stereos.hasMoreElements()) {
            cdo.setObjectProperty("Bond", "stereo", (String)stereos.nextElement()); 
          }
          cdo.endObject("Bond");
        }
      }
    }
}
