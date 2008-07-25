/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2005-2007  Egon Willighagen <egonw@sci.kun.nl>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.io.iterator.event;

import java.util.Hashtable;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.cml.CMLHandler;
import org.openscience.cdk.tools.LoggingTool;

/**
 * CDO object needed as interface with the JCFL library for reading CML
 * in a event based manner.
 *
 * <p>The CDO only takes care about atoms, bonds and molecules.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 * 
 * @author Egon Willighagen <egonw@sci.kun.nl>
*/ 
public class EventCMLHandler extends CMLHandler {
    
	private IChemObjectBuilder builder;
    private IAtomContainer currentMolecule;
    private IAtom currentAtom;
    
    private Map<String,Integer> atomEnumeration;
    
    private int numberOfAtoms = 0;
    
    private int bond_a1;
    private int bond_a2;
    private IBond.Order bond_order;
    private int bond_stereo;
    private String bond_id;
    
    protected LoggingTool logger;
    
    private DefaultEventChemObjectReader eventReader;
    
    /**
    * Constructs an iterating-abled CDO. After reading one molecule it
    * fires a frameRead event.
    */
    public EventCMLHandler(DefaultEventChemObjectReader eventReader,
    		                IChemObjectBuilder builder) {
    	super(builder.newChemFile());
        logger = new LoggingTool(this);
        this.eventReader = eventReader;
        this.builder = builder;
        clearData();
    }
    
    private void clearData() {
        currentMolecule = null;
        atomEnumeration = null;
        currentAtom = null;
    }
    
    public IAtomContainer getAtomContainer() {
        return currentMolecule;
    }
    
    // procedures required by CDOInterface
    
    /**
    * Procedure required by the CDOInterface. This function is only
    * supposed to be called by the JCFL library
    */
    public void startDocument() {
        logger.info("New CDO Object");
    }
    
    /**
    * Procedure required by the CDOInterface. This function is only
    * supposed to be called by the JCFL library
    */
    public void endDocument() {
        logger.debug("Closing document");
        logger.info("End CDO Object");
    }
    
    /**
    * Procedure required by the CDOInterface. This function is only
    * supposed to be called by the JCFL library
    */
    public void setDocumentProperty(String type, String value) {}
    
    /**
    * Procedure required by the CDOInterface. This function is only
    * supposed to be called by the JCFL library
    */
    public void startObject(String objectType) {
        logger.debug("START:" + objectType);
        if (objectType.equals("Molecule")) {
            currentMolecule = builder.newAtomContainer();
            atomEnumeration = new Hashtable<String,Integer>();
        } else if (objectType.equals("Atom")) {
            currentAtom = builder.newAtom("H");
            logger.debug("Atom # " + numberOfAtoms);
            numberOfAtoms++;
        } else if (objectType.equals("Bond")) {
            bond_id = null;
            bond_stereo = -99;
        }
    }
    
    /**
    * Procedure required by the CDOInterface. This function is only
    * supposed to be called by the JCFL library
    */
    public void endObject(String objectType) {
        logger.debug("END: " + objectType);
        if (objectType.equals("Molecule")) {
            eventReader.fireFrameRead();
            clearData();
        } else if (objectType.equals("Atom")) {
            currentMolecule.addAtom(currentAtom);
        } else if (objectType.equals("Bond")) {
            logger.debug("Bond(" + bond_id + "): " + bond_a1 + ", " + bond_a2 + ", " + bond_order);
            if (bond_a1 > currentMolecule.getAtomCount() ||
            bond_a2 > currentMolecule.getAtomCount()) {
                logger.error("Cannot add bond between at least one non-existant atom: " + bond_a1 +
                " and " + bond_a2);
            } else {
            	IAtom a1 = currentMolecule.getAtom(bond_a1);
            	IAtom a2 = currentMolecule.getAtom(bond_a2);
                IBond b = builder.newBond(a1, a2, bond_order);
                if (bond_id != null) b.setID(bond_id);
                if (bond_stereo != -99) {
                    b.setStereo(bond_stereo);
                }
                currentMolecule.addBond(b);
            }
        }
    }
    
    /**
    * Procedure required by the CDOInterface. This function is only
    * supposed to be called by the JCFL library
    */
    public void setObjectProperty(String objectType, String propertyType,
    String propertyValue) {
        logger.debug("objectType: " + objectType);
        logger.debug("propType: " + propertyType);
        logger.debug("property: " + propertyValue);
        
        if (objectType == null) {
            logger.error("Cannot add property for null object");
            return;
        }
        if (propertyType == null) {
            logger.error("Cannot add property for null property type");
            return;
        }
        if (propertyValue == null) {
            logger.warn("Will not add null property");
            return;
        }
        
        if (objectType.equals("Molecule")) {
            if (propertyType.equals("id")) {
                currentMolecule.setID(propertyValue);
            } else if (propertyType.equals("inchi")) {
                currentMolecule.setProperty("iupac.nist.chemical.identifier", propertyValue);
            }
        } else if (objectType.equals("PseudoAtom")) {
            if (propertyType.equals("label")) {
                if (!(currentAtom instanceof IPseudoAtom)) {
                    currentAtom = builder.newPseudoAtom(currentAtom);
                }
                ((IPseudoAtom)currentAtom).setLabel(propertyValue);
            }
        } else if (objectType.equals("Atom")) {
            if (propertyType.equals("type")) {
                if (propertyValue.equals("R") && !(currentAtom instanceof IPseudoAtom)) {
                    currentAtom = builder.newPseudoAtom(currentAtom);
                }
                currentAtom.setSymbol(propertyValue);
            } else if (propertyType.equals("x2")) {
            	Point2d coord = currentAtom.getPoint2d();
            	if (coord == null) coord = new Point2d();
            	coord.x = Double.parseDouble(propertyValue);
            	currentAtom.setPoint2d(coord);
            } else if (propertyType.equals("y2")) {
            	Point2d coord = currentAtom.getPoint2d();
            	if (coord == null) coord = new Point2d();
            	coord.y = Double.parseDouble(propertyValue);
            	currentAtom.setPoint2d(coord);
            } else if (propertyType.equals("x3")) {
            	Point3d coord = currentAtom.getPoint3d();
            	if (coord == null) coord = new Point3d();
            	coord.x = Double.parseDouble(propertyValue);
            	currentAtom.setPoint3d(coord);
            } else if (propertyType.equals("y3")) {
            	Point3d coord = currentAtom.getPoint3d();
            	if (coord == null) coord = new Point3d();
            	coord.y = Double.parseDouble(propertyValue);
            	currentAtom.setPoint3d(coord);
            } else if (propertyType.equals("z3")) {
            	Point3d coord = currentAtom.getPoint3d();
            	if (coord == null) coord = new Point3d();
            	coord.z = Double.parseDouble(propertyValue);
            	currentAtom.setPoint3d(coord);
            } else if (propertyType.equals("xFract")) {
            	Point3d coord = currentAtom.getFractionalPoint3d();
            	if (coord == null) coord = new Point3d();
            	coord.x = Double.parseDouble(propertyValue);
            	currentAtom.setFractionalPoint3d(coord);
            } else if (propertyType.equals("yFract")) {
            	Point3d coord = currentAtom.getFractionalPoint3d();
            	if (coord == null) coord = new Point3d();
            	coord.y = Double.parseDouble(propertyValue);
            	currentAtom.setFractionalPoint3d(coord);
            } else if (propertyType.equals("zFract")) {
            	Point3d coord = currentAtom.getFractionalPoint3d();
            	if (coord == null) coord = new Point3d();
            	coord.z = Double.parseDouble(propertyValue);
            	currentAtom.setFractionalPoint3d(coord);
            } else if (propertyType.equals("formalCharge")) {
                currentAtom.setFormalCharge(Integer.parseInt(propertyValue));
            } else if (propertyType.equals("charge") ||
            propertyType.equals("partialCharge")) {
                currentAtom.setCharge(Double.parseDouble(propertyValue));
            } else if (propertyType.equals("hydrogenCount")) {
                currentAtom.setHydrogenCount(Integer.parseInt(propertyValue));
            } else if (propertyType.equals("dictRef")) {
                currentAtom.setProperty("org.openscience.cdk.dict", propertyValue);
            } else if (propertyType.equals("atomicNumber")) {
                currentAtom.setAtomicNumber(Integer.parseInt(propertyValue));
            } else if (propertyType.equals("massNumber")) {
                currentAtom.setMassNumber((int)Double.parseDouble(propertyValue));
            } else if (propertyType.equals("id")) {
                logger.debug("id: ", propertyValue);
                currentAtom.setID(propertyValue);
                atomEnumeration.put(propertyValue, numberOfAtoms);
            }
        } else if (objectType.equals("Bond")) {
            if (propertyType.equals("atom1")) {
                bond_a1 = Integer.parseInt(propertyValue);
            } else if (propertyType.equals("atom2")) {
                bond_a2 = Integer.parseInt(propertyValue);
            } else if (propertyType.equals("id")) {
                logger.debug("id: " + propertyValue);
                bond_id = propertyValue;
            } else if (propertyType.equals("order")) {
                try {
                    Double order = Double.parseDouble(propertyValue);
                    if (order == 1.0) {
                    	bond_order = IBond.Order.SINGLE;
                    } else if (order == 2.0) {
                    	bond_order = IBond.Order.DOUBLE;
                    } else if (order == 3.0) {
                    	bond_order = IBond.Order.TRIPLE;
                    } else if (order == 4.0) {
                    	bond_order = IBond.Order.QUADRUPLE;
                    } else {
                        bond_order = IBond.Order.SINGLE;
                    }
                } catch (Exception e) {
                    logger.error("Cannot convert to double: " + propertyValue);
                    bond_order = IBond.Order.SINGLE;
                }
            } else if (propertyType.equals("stereo")) {
                if (propertyValue.equals("H")) {
                    bond_stereo = CDKConstants.STEREO_BOND_DOWN;
                } else if (propertyValue.equals("W")) {
                    bond_stereo = CDKConstants.STEREO_BOND_UP;
                }
            }
        }
        logger.debug("Object property set...");
    }
    
}

