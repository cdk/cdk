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

/**
 * Core CML 1.0 conventions are parsable by this class.
 *
 * Please report a bug report if this parser fails to parse
 * a certain element or attribute value.
 **/
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
    protected Vector formalCharges;
    protected Vector partialCharges;

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

    protected String currentChars;

    public Convention(CDOInterface cdo) {
        logger = new org.openscience.cdk.tools.LoggingTool(
                       this.getClass().getName());
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
        this.formalCharges = conv.formalCharges;
        this.partialCharges = conv.partialCharges;
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

    private void newMolecule() {
        elsym = new Vector();
        elid = new Vector();
        formalCharges = new Vector();
        partialCharges = new Vector();
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
    }

    public void startDocument() {
        logger.info("Start XML Doc");
        cdo.startDocument();
        newMolecule();
        BUILTIN = "";
        curRef = 0;
    };

    public void endDocument() {
        cdo.endDocument();
        logger.info("End XML Doc");
    };


    public void startElement (String uri, String local, String raw, Attributes atts) {
        String name = local;
        logger.debug("StartElement");
        setCurrentElement(name);
        switch (CurrentElement) {
        case ATOM :
            for (int i = 0; i < atts.getLength(); i++) {
                if (atts.getQName(i).equals("id")) {
                    logger.debug("T3 " + atts.getValue(i));
                    elid.addElement(atts.getValue(i));
                    logger.debug("T3 " + elid);
                }
            }
            break;
        case BOND :
            for (int i = 0; i < atts.getLength(); i++) {
            logger.debug("B2 " + atts.getQName(i) + "=" + atts.getValue(i));
            if (atts.getQName(i).equals("id")) {
                bondid.addElement(atts.getValue(i));
                logger.debug("B3 " + bondid);
            } else if (atts.getQName(i).equals("atomRefs")) {
                // expect only two references
                try {
                StringTokenizer st = new StringTokenizer(atts.getValue(i));
                bondARef1.addElement((String)st.nextElement());
                bondARef2.addElement((String)st.nextElement());
                } catch (Exception e) {
                logger.error("Error in CML file: " + e.toString());
                }
            }
            }
            stereoGiven = false;
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
                        logger.debug("BUILTIN value set for coordinate3: " +atts.getValue(i));
                BUILTIN = atts.getValue(i);
            } else {
                        logger.warn("Unkown coordinate3 builtin value: " + atts.getValue(i));
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
        case INTEGER :
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
            newMolecule();
            BUILTIN = "";
            cdo.startObject("Molecule");
            break;
        case CRYSTAL:
            cdo.startObject("Crystal");
            break;
        case LIST :
            break;
        }
    }

    public void endElement(String uri, String name, String raw) {
        logger.debug("EndElement: " + name);
        setCurrentElement(name);
        switch (CurrentElement) {
            case BOND :
                if (!stereoGiven) bondStereo.addElement("");
                break;
            case ATOM :
                if (x3.size() > formalCharges.size()) {
                  formalCharges.add(new Integer(0));
                }
                break;
            case MOLECULE :
                storeData();
                cdo.endObject("Molecule");
                break;
            case COORDINATE3:
                if (BUILTIN.equals("xyz3")) {
                    logger.debug("New coord3 xyz3 found: " + currentChars);
                    try {
                        StringTokenizer st = new StringTokenizer(currentChars);
                        x3.addElement(st.nextToken());
                        y3.addElement(st.nextToken());
                        z3.addElement(st.nextToken());
                        logger.debug("coord3 x3.length: " + x3.size());
                        logger.debug("coord3 y3.length: " + y3.size());
                        logger.debug("coord3 z3.length: " + z3.size());
                    } catch (Exception e) {
                        logger.error("CMLParsing error while setting coordinate3!");
                    }
                } else {
                    logger.warn("Unknown coordinate3 BUILTIN: " + BUILTIN);
                }
                break;
        }
        currentChars = "";
        BUILTIN = "";
        elementTitle = "";
    }

    public void characterData (char ch[], int start, int length) {
        logger.debug("CD");
        String s = new String(ch, start, length);
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
                    // NOTE: this combination is in violation of the CML DTD!!!
                    logger.debug("Charge: " + s.trim());
                    formalCharges.addElement(s.trim());
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
                } else if (BUILTIN.equals("order")) {
                    // NOTE: this combination is in violation of the CML DTD!!!
                    order.addElement(s.trim());
                }
                break;
            case INTEGER :
                if (BUILTIN.equals("formalCharge")) {
                    formalCharges.addElement(s.trim());
                }
                break;
            case COORDINATE2 :
                if (BUILTIN.equals("xy2")) {
                    logger.debug("New coord2 xy2 found." + s);
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
                currentChars = currentChars + s;
                break;
            case STRINGARRAY :
                if (BUILTIN.equals("id") || BUILTIN.equals("atomId")) {
                    // use of "id" seems incorrect by quick look at DTD
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
                            formalCharges.addElement(token);
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
                        notify("CMLParsing error: " + e, SYSTEMID, 454,1);
                    }
                } else if (elementTitle.equals("partialCharge")) {
                    logger.debug("New floatArray with partial charges found.");
                    try {
                        StringTokenizer st = new StringTokenizer(s);
                        while (st.hasMoreTokens()) partialCharges.addElement(st.nextToken());
                    } catch (Exception e) {
                        notify("CMLParsing error: " + e, SYSTEMID, 462,1);
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

    protected void storeData() {
      int atomcount = elid.size();
      logger.debug("No atom ids: " + atomcount);
      boolean has3D = false;
      boolean has2D = false;
      boolean hasFormalCharge = false;
      boolean hasPartialCharge = false;
      boolean hasSymbols = false;
      if (elsym.size() == atomcount) {
        hasSymbols = true;
      } else {
        logger.debug("No atom symbols: " + elsym.size() + " != " + atomcount);
      }
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
      if (formalCharges.size() == atomcount) {
        hasFormalCharge = true;
      } else {
        logger.debug("No formal Charge info: " + formalCharges.size() + " != " + atomcount);
      }
      if (partialCharges.size() == atomcount) {
        hasPartialCharge = true;
      } else {
        logger.debug("No partial Charge info: " + partialCharges.size() + " != " + atomcount);
      }
      for (int i=0; i<atomcount; i++) {
        logger.info("Storing atom: " + i);
        cdo.startObject("Atom");
        cdo.setObjectProperty("Atom", "id", (String)elid.elementAt(i));
        // store optional atom properties
        if (hasSymbols) {
            cdo.setObjectProperty("Atom", "type", (String)elsym.elementAt(i));
        }
        if (has3D) {
          cdo.setObjectProperty("Atom", "x3", (String)x3.elementAt(i));
          cdo.setObjectProperty("Atom", "y3", (String)y3.elementAt(i));
          cdo.setObjectProperty("Atom", "z3", (String)z3.elementAt(i));
        }
        if (hasFormalCharge) {
          cdo.setObjectProperty("Atom", "charge", (String)formalCharges.elementAt(i));
        }
        if (hasPartialCharge) {
          logger.debug("Storing partial atomic charge...");
          cdo.setObjectProperty("Atom", "partialCharge", (String)partialCharges.elementAt(i));
        }
        if (has2D) {
          cdo.setObjectProperty("Atom", "x2", (String)x2.elementAt(i));
          cdo.setObjectProperty("Atom", "y2", (String)y2.elementAt(i));
        }
        cdo.endObject("Atom");
      }
      int bondcount = order.size();
      logger.debug("Testing a1,a2,stereo: " + bondARef1.size() + "," + bondARef2.size() + "," + bondStereo.size() + "=" + order.size());
      if ((bondARef1.size() == bondcount) &&
          (bondARef2.size() == bondcount)) {
        logger.debug("About to add bond info to " + cdo.getClass().getName());
        Enumeration orders = order.elements();
        Enumeration bar1s = bondARef1.elements();
        Enumeration bar2s = bondARef2.elements();
        Enumeration stereos = bondStereo.elements();
      while (orders.hasMoreElements()) {
          cdo.startObject("Bond");
          cdo.setObjectProperty("Bond", "atom1", new Integer(elid.indexOf((String)bar1s.nextElement())).toString());
          cdo.setObjectProperty("Bond", "atom2", new Integer(elid.indexOf((String)bar2s.nextElement())).toString());
          String bondOrder = (String)orders.nextElement();
          if ("S".equals(bondOrder)) {
              cdo.setObjectProperty("Bond", "order", "1");
          } else if ("D".equals(bondOrder)) {
              cdo.setObjectProperty("Bond", "order", "2");
          } else if ("T".equals(bondOrder)) {
              cdo.setObjectProperty("Bond", "order", "3");
          } else if ("A".equals(bondOrder)) {
              cdo.setObjectProperty("Bond", "order", "1.5");
          } else {
              cdo.setObjectProperty("Bond", "order", bondOrder);
          }
          if (stereos.hasMoreElements()) {
            cdo.setObjectProperty("Bond", "stereo", (String)stereos.nextElement()); 
          }
          cdo.endObject("Bond");
        }
      }
    }
}
