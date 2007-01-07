/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 1997-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.io.cml;

import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.dict.DictRef;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IPDBAtom;
import org.openscience.cdk.interfaces.IPDBMonomer;
import org.openscience.cdk.interfaces.IPDBPolymer;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IStrand;
import org.openscience.cdk.io.cml.cdopi.CDOAcceptedObjects;
import org.openscience.cdk.io.cml.cdopi.IChemicalDocumentObject;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.BooleanResult;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.tools.LoggingTool;

/**
 * CDO object needed as interface with the JCFL library for reading CML
 * encoded data.
 *
 * @cdk.module io
 * 
 * @author Egon Willighagen <egonw@sci.kun.nl>
 */ 
public class ChemFileCDO implements IChemFile, IChemicalDocumentObject {

	private IChemFile currentChemFile;
	
    private IAtomContainer currentMolecule;
    private IMoleculeSet currentMoleculeSet;
    private IChemModel currentChemModel;
    private IChemSequence currentChemSequence;
    private IReactionSet currentReactionSet;
    private IReaction currentReaction;
    private IAtom currentAtom;
    private IStrand currentStrand;
    private IMonomer currentMonomer;
    private Hashtable atomEnumeration;

    private String currentDescriptorAlgorithmSpecification;
    private String currentDescriptorImplementationTitel;
    private String currentDescriptorImplementationVendor;
    private String currentDescriptorImplementationIdentifier;
    private String currentDescriptorDataType;
    private String currentDescriptorResult;
    private boolean currentDescriptorDataIsArray;
    
    private int numberOfAtoms = 0;

    private int bond_a1;
    private int bond_a2;
    private double bond_order;
    private int bond_stereo;
    private String bond_id;
    
    private double crystal_axis_x;
    private double crystal_axis_y;
    private double crystal_axis_z;
    
    protected LoggingTool logger;

	private DescriptorSpecification currentDescriptorSpecification;

    /**
     * Basic contructor
     */
    public ChemFileCDO(IChemFile file) {
      logger = new LoggingTool(this);
      currentChemFile = file;
      currentChemSequence = file.getBuilder().newChemSequence();
      currentChemModel = file.getBuilder().newChemModel();
      currentMoleculeSet = file.getBuilder().newMoleculeSet();
      currentReactionSet = null;
      currentReaction = null;
      currentMolecule = file.getBuilder().newMolecule();
      atomEnumeration = new Hashtable();    
    }

    // procedures required by CDOInterface

    /**
     * Procedure required by the CDOInterface. This function is only
     * supposed to be called by the JCFL library
     */
    public void startDocument() {
      logger.info("New CDO Object");
      currentChemSequence = currentChemFile.getBuilder().newChemSequence();
      currentChemModel = currentChemFile.getBuilder().newChemModel();
      currentMoleculeSet = currentChemFile.getBuilder().newMoleculeSet();
      currentMolecule = currentChemFile.getBuilder().newMolecule();
      atomEnumeration = new Hashtable();
    }

    /**
     * Procedure required by the CDOInterface. This function is only
     * supposed to be called by the JCFL library
     */
    public void endDocument() {
        logger.debug("Closing document");
        if (currentReactionSet != null && currentReactionSet.getReactionCount() == 0 &&
            currentReaction != null) {
            logger.debug("Adding reaction to ReactionSet");
            currentReactionSet.addReaction(currentReaction);
        }
        if (currentReactionSet != null && currentChemModel.getReactionSet() == null) {
            logger.debug("Adding SOR to ChemModel");
            currentChemModel.setReactionSet(currentReactionSet);
        }
        if (currentMoleculeSet != null && currentMoleculeSet.getMoleculeCount() != 0) {
            logger.debug("Adding reaction to MoleculeSet");
            currentChemModel.setMoleculeSet(currentMoleculeSet);
        }
        if (currentChemSequence.getChemModelCount() == 0) {
            logger.debug("Adding ChemModel to ChemSequence");
            currentChemSequence.addChemModel(currentChemModel);
        }
        if (getChemSequenceCount() == 0) {
            // assume there is one non-animation ChemSequence
            addChemSequence(currentChemSequence);
        }
        logger.info("End CDO Object");
        logger.info("Number of sequences:", getChemSequenceCount());
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
          if (currentChemModel == null) currentChemModel = currentChemFile.getBuilder().newChemModel();
          if (currentMoleculeSet == null) currentMoleculeSet = currentChemFile.getBuilder().newMoleculeSet();
          currentMolecule = currentChemFile.getBuilder().newMolecule();
      } else if (objectType.equals("Atom")) {
        currentAtom = currentChemFile.getBuilder().newAtom("H");
        logger.debug("Atom # " + numberOfAtoms);
        numberOfAtoms++;
      } else if (objectType.equals("Bond")) {
          bond_id = null;
          bond_stereo = -99;
      } else if (objectType.equals("Animation")) {
        currentChemSequence = currentChemFile.getBuilder().newChemSequence();
      } else if (objectType.equals("Frame")) {
        currentChemModel = currentChemFile.getBuilder().newChemModel();
      } else if (objectType.equals("MoleculeSet")) {
        currentMoleculeSet = currentChemFile.getBuilder().newMoleculeSet();
        currentMolecule = currentChemFile.getBuilder().newMolecule();
      } else if (objectType.equals("Crystal")) {
        currentMolecule = currentChemFile.getBuilder().newCrystal(currentMolecule);
      } else if (objectType.equals("a-axis") ||
                 objectType.equals("b-axis") ||
                 objectType.equals("c-axis")) {
          crystal_axis_x = 0.0;
          crystal_axis_y = 0.0;
          crystal_axis_z = 0.0;
      } else if (objectType.equals("ReactionSet")) {
          currentReactionSet = currentChemFile.getBuilder().newReactionSet();
      } else if (objectType.equals("Reaction")) {
          if (currentReactionSet == null) startObject("ReactionSet");
          currentReaction = currentChemFile.getBuilder().newReaction();
      } else if (objectType.equals("Reactant")) {
          if (currentReaction == null) startObject("Reaction");
          currentMolecule = currentChemFile.getBuilder().newMolecule();
      } else if (objectType.equals("Product")) {
          if (currentReaction == null) startObject("Reaction");
          currentMolecule = currentChemFile.getBuilder().newMolecule();
      } else if (objectType.equals("PDBAtom")) {
          currentAtom = currentChemFile.getBuilder().newPDBAtom("H");
          logger.debug("Atom # " + numberOfAtoms);
          numberOfAtoms++;
      } else if (objectType.equals("PDBPolymer")) {
    	  currentStrand = currentChemFile.getBuilder().newStrand();
    	  currentStrand.setStrandName("A");
          currentMolecule = currentChemFile.getBuilder().newPDBPolymer();
      } else if (objectType.equals("PDBMonomer")) {
    	  currentMonomer = currentChemFile.getBuilder().newPDBMonomer();
      } else if (objectType.equals("MolecularDescriptor")) {
    	  currentDescriptorDataIsArray = false;
    	  currentDescriptorAlgorithmSpecification = "";
    	  currentDescriptorImplementationTitel = "";
    	  currentDescriptorImplementationVendor = "";
    	  currentDescriptorImplementationIdentifier = "";
    	  currentDescriptorDataType = "";
    	  currentDescriptorResult = "";
      }
    }

    /**
     * Procedure required by the CDOInterface. This function is only
     * supposed to be called by the JCFL library
     */
    public void endObject(String objectType) {
        logger.debug("END: " + objectType);
        if (objectType.equals("Molecule")) {
            if (currentMolecule instanceof IMolecule) {
                logger.debug("Adding molecule to set");
                currentMoleculeSet.addMolecule((IMolecule)currentMolecule);
                logger.debug("#mols in set: " + currentMoleculeSet.getMoleculeCount());
            } else if (currentMolecule instanceof ICrystal) {
                logger.debug("Adding crystal to chemModel");
                currentChemModel.setCrystal((ICrystal)currentMolecule);
                currentChemSequence.addChemModel(currentChemModel);
            }
        } else if (objectType.equals("MoleculeSet")) {
            currentChemModel.setMoleculeSet(currentMoleculeSet);
            currentChemSequence.addChemModel(currentChemModel);
        } else if (objectType.equals("Frame")) {
            // endObject("Molecule");
        } else if (objectType.equals("Animation")) {
            addChemSequence(currentChemSequence);
            logger.info("This file has " + getChemSequenceCount() + " sequence(s).");
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
                IBond b = currentChemFile.getBuilder().newBond(a1, a2, bond_order);
                if (bond_id != null) b.setID(bond_id);
                if (bond_stereo != -99) {
                    b.setStereo(bond_stereo);
                }
                if (bond_order == CDKConstants.BONDORDER_AROMATIC) {
                    b.setFlag(CDKConstants.ISAROMATIC, true);
                }
                currentMolecule.addBond(b);
            }
        } else if (objectType.equals("a-axis")) {
          // set these variables
          if (currentMolecule instanceof ICrystal) {
              ICrystal current = (ICrystal)currentMolecule;
              current.setA(new Vector3d(crystal_axis_x,
                                        crystal_axis_y,
                                        crystal_axis_z));
          } else {
              logger.warn("Current object is not a crystal");
          }
        } else if (objectType.equals("b-axis")) {
          if (currentMolecule instanceof ICrystal) {
              ICrystal current = (ICrystal)currentMolecule;
              current.setB(new Vector3d(crystal_axis_x,
                                        crystal_axis_y,
                                        crystal_axis_z));
          } else {
              logger.warn("Current object is not a crystal");
          }
        } else if (objectType.equals("c-axis")) {
          if (currentMolecule instanceof ICrystal) {
              ICrystal current = (ICrystal)currentMolecule;
              current.setC(new Vector3d(crystal_axis_x,
                                        crystal_axis_y,
                                        crystal_axis_z));
          } else {
              logger.warn("Current object is not a crystal");
          }
      } else if (objectType.equals("ReactionSet")) {
          currentChemModel.setReactionSet(currentReactionSet);
          currentChemSequence.addChemModel(currentChemModel);
          /* FIXME: this should be when document is closed! */ 
      } else if (objectType.equals("Reaction")) {
          logger.debug("Adding reaction to SOR");
          currentReactionSet.addReaction(currentReaction);
      } else if (objectType.equals("Reactant")) {
          currentReaction.addReactant((IMolecule)currentMolecule);
      } else if (objectType.equals("Product")) {
          currentReaction.addProduct((IMolecule)currentMolecule);
      } else if (objectType.equals("Crystal")) {
          logger.debug("Crystal: " + currentMolecule);
      } else if (objectType.equals("PDBAtom")) {
    	  String cResidue = ((IPDBAtom)currentAtom).getResName()+"A"+((IPDBAtom)currentAtom).getResSeq();
    	  ((IPDBMonomer)currentMonomer).setMonomerName(cResidue);
    	  ((IPDBMonomer)currentMonomer).setMonomerType(((IPDBAtom)currentAtom).getResName());
    	  ((IPDBMonomer)currentMonomer).setChainID(((IPDBAtom)currentAtom).getChainID());
    	  ((IPDBMonomer)currentMonomer).setICode(((IPDBAtom)currentAtom).getICode());
          ((IPDBPolymer)currentMolecule).addAtom(
        		  ((IPDBAtom)currentAtom),currentMonomer,currentStrand);
      } else if (objectType.equals("PDBMonomer")) {
    	  
      } else if (objectType.equals("MolecularDescriptor")) {
    	  DescriptorSpecification descriptorSpecification = new DescriptorSpecification(
    		  currentDescriptorAlgorithmSpecification,
    		  currentDescriptorImplementationTitel,
    		  currentDescriptorImplementationIdentifier,
    		  currentDescriptorImplementationVendor
    	  );
    	  currentMolecule.setProperty(descriptorSpecification, 
    		  new DescriptorValue(
    		      descriptorSpecification,
    		      new String[0], new Object[0],
    		      currentDescriptorDataIsArray ?
    		          newDescriptorResultArray(currentDescriptorResult) :
    		          newDescriptorResult(currentDescriptorResult),
    		      new String[0]
    		  )
    	  );
      } 
    }

    private IDescriptorResult newDescriptorResult(String descriptorValue) {
    	IDescriptorResult result = null;
    	if ("xsd:double".equals(currentDescriptorDataType)) {
    		result = new DoubleResult(Double.parseDouble(descriptorValue));    		
    	} else if ("xsd:integer".equals(currentDescriptorDataType)) {
    		result = new IntegerResult(Integer.parseInt(descriptorValue));
    	} else if ("xsd:boolean".equals(currentDescriptorDataType)) {
    		result = new BooleanResult(Boolean.parseBoolean(descriptorValue));
    	}
		return result;
	}

    private IDescriptorResult newDescriptorResultArray(String descriptorValue) {
    	IDescriptorResult result = null;
    	if ("xsd:double".equals(currentDescriptorDataType)) {
    		result = new DoubleArrayResult();
    		StringTokenizer tokenizer = new StringTokenizer(descriptorValue);
            while (tokenizer.hasMoreElements()) {
                ((DoubleArrayResult)result).add(Double.parseDouble(tokenizer.nextToken()));
            }
    	} else if ("xsd:integer".equals(currentDescriptorDataType)) {
    		result = new IntegerArrayResult();
    		StringTokenizer tokenizer = new StringTokenizer(descriptorValue);
            while (tokenizer.hasMoreElements()) {
                ((IntegerArrayResult)result).add(Integer.parseInt(tokenizer.nextToken()));
            }
    	}
		return result;
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
        } else if (propertyType.equals("Name")) {
            currentMolecule.setProperty(CDKConstants.TITLE, propertyValue);
        } else if (propertyType.equals("inchi")) {
          currentMolecule.setProperty(CDKConstants.INCHI, propertyValue);
        } else if (propertyType.equals("dict")) {
            currentMolecule.setProperty(new DictRef(propertyType, propertyValue), propertyValue);
        } else if (propertyType.equals("pdb:residueName")) {
          currentMolecule.setProperty(new DictRef(propertyType, propertyValue), propertyValue);
        } else if (propertyType.equals("pdb:oneLetterCode")) {
          currentMolecule.setProperty(new DictRef(propertyType, propertyValue), propertyValue);
        } else if (propertyType.equals("pdb:id")) {
            currentMolecule.setProperty(new DictRef(propertyType, propertyValue), propertyValue);
        } else {
        	logger.warn("Not adding molecule property!");
        }
      } else if (objectType.equals("MolecularProperty")) {
    	  currentMolecule.setProperty(propertyType, propertyValue);
      } else if (objectType.equals("PseudoAtom")) {
        if (propertyType.equals("label")) {
            if (!(currentAtom instanceof IPseudoAtom)) {
                currentAtom = currentChemFile.getBuilder().newPseudoAtom(currentAtom);
            }
            ((IPseudoAtom)currentAtom).setLabel(propertyValue);
        }
      } else if (objectType.equals("Atom")) {

    	  if (propertyType.equals("type")) {
            if (propertyValue.equals("R") && !(currentAtom instanceof IPseudoAtom)) {
                currentAtom = currentChemFile.getBuilder().newPseudoAtom(currentAtom);
            }
            currentAtom.setSymbol(propertyValue);
            try{
            	IsotopeFactory.getInstance(currentAtom.getBuilder()).configure(currentAtom);
            }catch(Exception ex){
            	logger.warn("Could not configure atom");
            }
        } else if (propertyType.equals("x2")) {
        	Point2d coord = currentAtom.getPoint2d();
        	if (coord == null) coord = new Point2d();
        	coord.x = new Double(propertyValue).doubleValue();
        	currentAtom.setPoint2d(coord);
        } else if (propertyType.equals("y2")) {
        	Point2d coord = currentAtom.getPoint2d();
        	if (coord == null) coord = new Point2d();
        	coord.y = new Double(propertyValue).doubleValue();
        	currentAtom.setPoint2d(coord);
        } else if (propertyType.equals("x3")) {
        	Point3d coord = currentAtom.getPoint3d();
        	if (coord == null) coord = new Point3d();
        	coord.x = new Double(propertyValue).doubleValue();
        	currentAtom.setPoint3d(coord);
        } else if (propertyType.equals("y3")) {
        	Point3d coord = currentAtom.getPoint3d();
        	if (coord == null) coord = new Point3d();
        	coord.y = new Double(propertyValue).doubleValue();
        	currentAtom.setPoint3d(coord);
        } else if (propertyType.equals("z3")) {
        	Point3d coord = currentAtom.getPoint3d();
        	if (coord == null) coord = new Point3d();
        	coord.z = new Double(propertyValue).doubleValue();
        	currentAtom.setPoint3d(coord);
        } else if (propertyType.equals("xFract")) {
        	Point3d coord = currentAtom.getFractionalPoint3d();
        	if (coord == null) coord = new Point3d();
        	coord.x = new Double(propertyValue).doubleValue();
        	currentAtom.setFractionalPoint3d(coord);
        } else if (propertyType.equals("yFract")) {
        	Point3d coord = currentAtom.getFractionalPoint3d();
        	if (coord == null) coord = new Point3d();
        	coord.y = new Double(propertyValue).doubleValue();
        	currentAtom.setFractionalPoint3d(coord);
        } else if (propertyType.equals("zFract")) {
        	Point3d coord = currentAtom.getFractionalPoint3d();
        	if (coord == null) coord = new Point3d();
        	coord.z = new Double(propertyValue).doubleValue();
        	currentAtom.setFractionalPoint3d(coord);
        } else if (propertyType.equals("formalCharge")) {
          currentAtom.setFormalCharge(new Integer(propertyValue).intValue());
        } else if (propertyType.equals("charge") ||
                   propertyType.equals("partialCharge")) {
          currentAtom.setCharge(new Double(propertyValue).doubleValue());
        } else if (propertyType.equals("hydrogenCount")) {
          currentAtom.setHydrogenCount(new Integer(propertyValue).intValue());
        } else if (propertyType.equals("dictRef")) {
            currentAtom.setProperty("org.openscience.cdk.dict", propertyValue);
        } else if (propertyType.equals("atomicNumber")) {
            currentAtom.setAtomicNumber(Integer.parseInt(propertyValue));
        } else if (propertyType.equals("atomTypeLabel")) {
            currentAtom.setAtomTypeName(propertyValue);
        } else if (propertyType.equals("massNumber")) {
            currentAtom.setMassNumber((new Double(propertyValue)).intValue());
        } else if (propertyType.equals("spinMultiplicity")) {
            int unpairedElectrons = new Integer(propertyValue).intValue() -1;
            for (int i=0; i<unpairedElectrons; i++) {
                currentMolecule.addSingleElectron(currentChemFile.getBuilder().newSingleElectron(currentAtom));
            }
        } else if (propertyType.equals("id")) {
          logger.debug("id: ", propertyValue);
          currentAtom.setID(propertyValue);
          atomEnumeration.put(propertyValue, new Integer(numberOfAtoms));
        } 
      } else if(objectType.equals("PDBAtom")){
    	  if (propertyType.equals("occupancy")) {
              int occ = (new Double(propertyValue)).intValue();
              if(occ >= 0)
                  ((IPDBAtom)currentAtom).setOccupancy(occ);
          } else if (propertyType.equals("altLoc"))
    		  ((IPDBAtom)currentAtom).setAltLoc(propertyValue);
    	  else if (propertyType.equals("chainID"))
    		  ((IPDBAtom)currentAtom).setChainID(propertyValue);
    	  else if (propertyType.equals("hetAtom")){
    		  boolean hetAtom = false;
    		  if(propertyValue.equals("true"))
    			  hetAtom = true;
    		  ((IPDBAtom)currentAtom).setHetAtom(hetAtom);
    	  }
    	  else if (propertyType.equals("iCode"))
    		  ((IPDBAtom)currentAtom).setICode(propertyValue);
    	  else if (propertyType.equals("name"))
    		  ((IPDBAtom)currentAtom).setName(propertyValue);
    	  else if (propertyType.equals("oxt")){
    		  boolean oxt = false;
    		  if(propertyValue.equals("true"))
    			  oxt = true;
    		  ((IPDBAtom)currentAtom).setOxt(oxt);
    	  }
    	  else if (propertyType.equals("resSeq"))
    		  ((IPDBAtom)currentAtom).setResSeq(propertyValue);
    	  else if (propertyType.equals("record"))
    		  ((IPDBAtom)currentAtom).setRecord(propertyValue);
    	  else if (propertyType.equals("resName"))
    		  ((IPDBAtom)currentAtom).setResName(propertyValue);
    	  else if (propertyType.equals("segID"))
    		  ((IPDBAtom)currentAtom).setSegID(propertyValue);
    	  else if (propertyType.equals("serial"))
    		  ((IPDBAtom)currentAtom).setSerial((new Double(propertyValue)).intValue());
    	  else if (propertyType.equals("tempFactor"))
    		  ((IPDBAtom)currentAtom).setTempFactor((new Double(propertyValue)).doubleValue());
    	  
      }else if (objectType.equals("Bond")) {
        if (propertyType.equals("atom1")) {
          bond_a1 = new Integer(propertyValue).intValue();
        } else if (propertyType.equals("atom2")) {
          bond_a2 = new Integer(propertyValue).intValue();
        } else if (propertyType.equals("id")) {
          logger.debug("id: " + propertyValue);
          bond_id = propertyValue;
        } else if (propertyType.equals("order")) {
          try {
            bond_order = Double.parseDouble(propertyValue);
          } catch (Exception e) {
            logger.error("Cannot convert to double: " + propertyValue);
            bond_order = 1.0;
          }
        } else if (propertyType.equals("stereo")) {
            if (propertyValue.equals("H")) {
                bond_stereo = CDKConstants.STEREO_BOND_DOWN;
            } else if (propertyValue.equals("W")) {
                bond_stereo = CDKConstants.STEREO_BOND_UP;
            }
        }
      } else if (objectType.equals("Reaction")) {
        if (propertyType.equals("id")) {
          currentReaction.setID(propertyValue);
        }
      } else if (objectType.equals("ReactionSet")) {
          if (propertyType.equals("id")) {
              currentReactionSet.setID(propertyValue);
          }
      } else if (objectType.equals("Reactant")) {
          if (propertyType.equals("id")) {
              currentMolecule.setID(propertyValue);
          }
      } else if (objectType.equals("Product")) {
          if (propertyType.equals("id")) {
              currentMolecule.setID(propertyValue);
          }
      } else if (objectType.equals("Crystal")) {
          // set these variables
          if (currentMolecule instanceof ICrystal) {
              ICrystal current = (ICrystal)currentMolecule;
              if (propertyType.equals("spacegroup")) {
                  logger.debug("Setting crystal spacegroup to: " + propertyValue);
                  current.setSpaceGroup(propertyValue);
              } else if (propertyType.equals("z")) {
                  try {
                      logger.debug("Setting z to: " + propertyValue);
                      current.setZ(Integer.parseInt(propertyValue));
                  } catch (NumberFormatException exception) {
                      logger.error("Error in format of Z value");
                  }
              }
          } else {
              logger.warn("Cannot add crystal cell parameters to a non " +
                           "Crystal class!");
          }
      } else if (objectType.equals("a-axis") ||
                 objectType.equals("b-axis") ||
                 objectType.equals("c-axis")) {
          // set these variables
          if (currentMolecule instanceof ICrystal) {
              logger.debug("Setting axis (" + objectType + "): " + propertyValue);
              if (propertyType.equals("x")) {
                  crystal_axis_x = Double.parseDouble(propertyValue);
              } else if (propertyType.equals("y")) {
                  crystal_axis_y = Double.parseDouble(propertyValue);
              } else if (propertyType.equals("z")) {
                  crystal_axis_z = Double.parseDouble(propertyValue);
              }
          } else {
              logger.warn("Cannot add crystal cell parameters to a non " +
                           "Crystal class!");
          }
      } else if (objectType.equals("MolecularDescriptor")) {
    	  if (propertyType.equals("SpecificationReference")) {
    		  currentDescriptorAlgorithmSpecification = propertyValue;
    	  } else if (propertyType.equals("ImplementationTitle")) {
    		  currentDescriptorImplementationTitel = propertyValue;
    	  } else if (propertyType.equals("ImplementationIdentifier")) {
    		  currentDescriptorImplementationIdentifier = propertyValue;
    	  } else if (propertyType.equals("ImplementationVendor")) {
    		  currentDescriptorImplementationVendor = propertyValue;
    	  } else if (propertyType.equals("DataType")) {
    		  currentDescriptorDataType = propertyValue;
    	  } else if (propertyType.equals("DataIsArray")) {
    		  currentDescriptorDataIsArray = true;
    	  } else if (propertyType.equals("DescriptorValue")) {
    		  currentDescriptorResult = propertyValue;
    	  } 
      }
      logger.debug("Object property set...");
    }
    /**
     * Procedure required by the CDOInterface. This function is only
     * supposed to be called by the JCFL library
     */
    public CDOAcceptedObjects acceptObjects() {
        CDOAcceptedObjects objects = new CDOAcceptedObjects();
        objects.add("Molecule");
        objects.add("Fragment");
        objects.add("Atom");
        objects.add("PDBPolymer");
        objects.add("PDBAtom");
        objects.add("Bond");
        objects.add("Animation");
        objects.add("Frame");
        objects.add("Crystal");
        objects.add("a-axis");
        objects.add("b-axis");
        objects.add("c-axis");
        objects.add("ReactionSet");
        objects.add("Reactions");
        objects.add("Reactant");
        objects.add("Product");
        objects.add("MolecularDescriptor");
      return objects;
    }

	public void addChemSequence(IChemSequence chemSequence) {
		currentChemFile.addChemSequence(chemSequence);
	}

	public void removeChemSequence(int pos) {
		currentChemFile.removeChemSequence(pos);
	}
	
	public java.util.Iterator chemSequences() {
		return currentChemFile.chemSequences();
	}

	public IChemSequence getChemSequence(int number) {
		return currentChemFile.getChemSequence(number);
	}

	public int getChemSequenceCount() {
		return currentChemFile.getChemSequenceCount();
	}

	public void addListener(IChemObjectListener col) {
		currentChemFile.addListener(col);
	}

	public int getListenerCount() {
		return currentChemFile.getListenerCount();
	}

	public void removeListener(IChemObjectListener col) {
		currentChemFile.removeListener(col);
	}

	public void notifyChanged() {
		currentChemFile.notifyChanged();
	}

	public void notifyChanged(IChemObjectChangeEvent evt) {
		currentChemFile.notifyChanged(evt);
	}

	public void setProperty(Object description, Object property) {
		currentChemFile.setProperty(description, property);
	}

	public void removeProperty(Object description) {
		currentChemFile.removeProperty(description);
	}

	public Object getProperty(Object description) {
		return currentChemFile.getProperty(description);
	}

	public Hashtable getProperties() {
		return currentChemFile.getProperties();
	}

	public String getID() {
		return currentChemFile.getID();
	}

	public void setID(String identifier) {
		currentChemFile.setID(identifier);
	}

	public void setFlag(int flag_type, boolean flag_value) {
		currentChemFile.setFlag(flag_type, flag_value);
	}

	public boolean getFlag(int flag_type) {
		return currentChemFile.getFlag(flag_type);
	}

	public void setProperties(Hashtable properties) {
		currentChemFile.setProperties(properties);
	}

	public void setFlags(boolean[] flagsNew) {
		currentChemFile.setFlags(flagsNew);
	}

	public boolean[] getFlags() {
		return currentChemFile.getFlags();
	}

	public Object clone() throws CloneNotSupportedException {
		return currentChemFile.clone();
	}

	public IChemObjectBuilder getBuilder() {
		return currentChemFile.getBuilder();
	}

	private boolean doNotification = true;
	
	public void setNotification(boolean bool) {
		this.doNotification = bool;
	}

	public boolean getNotification() {
		return this.doNotification;
	}
	
}

