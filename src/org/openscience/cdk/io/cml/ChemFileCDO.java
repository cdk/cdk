/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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

import javax.vecmath.Vector3d;

import org.openscience.cdk.CDKConstants;
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
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.io.cml.cdopi.CDOAcceptedObjects;
import org.openscience.cdk.io.cml.cdopi.IChemicalDocumentObject;
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
    private IMoleculeSet currentSetOfMolecules;
    private IChemModel currentChemModel;
    private IChemSequence currentChemSequence;
    private IReactionSet currentSetOfReactions;
    private IReaction currentReaction;
    private IAtom currentAtom;
    private Hashtable atomEnumeration;

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

    /**
     * Basic contructor
     */
    public ChemFileCDO(IChemFile file) {
      logger = new LoggingTool(this);
      currentChemFile = file;
      currentChemSequence = file.getBuilder().newChemSequence();
      currentChemModel = file.getBuilder().newChemModel();
      currentSetOfMolecules = file.getBuilder().newMoleculeSet();
      currentSetOfReactions = null;
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
      currentSetOfMolecules = currentChemFile.getBuilder().newMoleculeSet();
      currentMolecule = currentChemFile.getBuilder().newMolecule();
      atomEnumeration = new Hashtable();
    }

    /**
     * Procedure required by the CDOInterface. This function is only
     * supposed to be called by the JCFL library
     */
    public void endDocument() {
        logger.debug("Closing document");
        if (currentSetOfReactions != null && currentSetOfReactions.getReactionCount() == 0 &&
            currentReaction != null) {
            logger.debug("Adding reaction to SetOfReactions");
            currentSetOfReactions.addReaction(currentReaction);
        }
        if (currentSetOfReactions != null && currentChemModel.getSetOfReactions() == null) {
            logger.debug("Adding SOR to ChemModel");
            currentChemModel.setSetOfReactions(currentSetOfReactions);
        }
        if (currentSetOfMolecules != null && currentSetOfMolecules.getMoleculeCount() != 0) {
            logger.debug("Adding reaction to SetOfMolecules");
            currentChemModel.setSetOfMolecules(currentSetOfMolecules);
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
          if (currentSetOfMolecules == null) currentSetOfMolecules = currentChemFile.getBuilder().newMoleculeSet();
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
      } else if (objectType.equals("SetOfMolecules")) {
        currentSetOfMolecules = currentChemFile.getBuilder().newMoleculeSet();
        currentMolecule = currentChemFile.getBuilder().newMolecule();
      } else if (objectType.equals("Crystal")) {
        currentMolecule = currentChemFile.getBuilder().newCrystal(currentMolecule);
      } else if (objectType.equals("a-axis") ||
                 objectType.equals("b-axis") ||
                 objectType.equals("c-axis")) {
          crystal_axis_x = 0.0;
          crystal_axis_y = 0.0;
          crystal_axis_z = 0.0;
      } else if (objectType.equals("SetOfReactions")) {
          currentSetOfReactions = currentChemFile.getBuilder().newReactionSet();
      } else if (objectType.equals("Reaction")) {
          if (currentSetOfReactions == null) startObject("SetOfReactions");
          currentReaction = currentChemFile.getBuilder().newReaction();
      } else if (objectType.equals("Reactant")) {
          if (currentReaction == null) startObject("Reaction");
          currentMolecule = currentChemFile.getBuilder().newMolecule();
      } else if (objectType.equals("Product")) {
          if (currentReaction == null) startObject("Reaction");
          currentMolecule = currentChemFile.getBuilder().newMolecule();
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
                currentSetOfMolecules.addMolecule((IMolecule)currentMolecule);
                logger.debug("#mols in set: " + currentSetOfMolecules.getMoleculeCount());
            } else if (currentMolecule instanceof ICrystal) {
                logger.debug("Adding crystal to chemModel");
                currentChemModel.setCrystal((ICrystal)currentMolecule);
                currentChemSequence.addChemModel(currentChemModel);
            }
        } else if (objectType.equals("SetOfMolecules")) {
            currentChemModel.setSetOfMolecules(currentSetOfMolecules);
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
      } else if (objectType.equals("SetOfReactions")) {
          currentChemModel.setSetOfReactions(currentSetOfReactions);
          currentChemSequence.addChemModel(currentChemModel);
          /* FIXME: this should be when document is closed! */ 
      } else if (objectType.equals("Reaction")) {
          logger.debug("Adding reaction to SOR");
          currentSetOfReactions.addReaction(currentReaction);
      } else if (objectType.equals("Reactant")) {
          currentReaction.addReactant((IMolecule)currentMolecule);
      } else if (objectType.equals("Product")) {
          currentReaction.addProduct((IMolecule)currentMolecule);
      } else if (objectType.equals("Crystal")) {
          logger.debug("Crystal: " + currentMolecule);
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
        } else if (propertyType.equals("pdb:residueName")) {
          currentMolecule.setProperty(
        	new DictRef(propertyType, propertyValue), propertyValue
          );
        } else if (propertyType.equals("pdb:oneLetterCode")) {
          currentMolecule.setProperty(
          	new DictRef(propertyType, propertyValue), propertyValue
          );
        } else if (propertyType.equals("pdb:id")) {
            currentMolecule.setProperty(
            	new DictRef(propertyType, propertyValue), propertyValue
            );
        } else {
        	logger.warn("Not adding molecule property!");
        }
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
        } else if (propertyType.equals("x2")) {
          currentAtom.setX2d(new Double(propertyValue).doubleValue());
        } else if (propertyType.equals("y2")) {
          currentAtom.setY2d(new Double(propertyValue).doubleValue());
        } else if (propertyType.equals("x3")) {
          currentAtom.setX3d(new Double(propertyValue).doubleValue());
        } else if (propertyType.equals("y3")) {
          currentAtom.setY3d(new Double(propertyValue).doubleValue());
        } else if (propertyType.equals("z3")) {
          currentAtom.setZ3d(new Double(propertyValue).doubleValue());
        } else if (propertyType.equals("xFract")) {
          currentAtom.setFractX3d(new Double(propertyValue).doubleValue());
        } else if (propertyType.equals("yFract")) {
          currentAtom.setFractY3d(new Double(propertyValue).doubleValue());
        } else if (propertyType.equals("zFract")) {
          currentAtom.setFractZ3d(new Double(propertyValue).doubleValue());
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
        } else if (propertyType.equals("massNumber")) {
            currentAtom.setMassNumber((new Double(propertyValue)).intValue());
        } else if (propertyType.equals("spinMultiplicity")) {
            int unpairedElectrons = new Integer(propertyValue).intValue() -1;
            for (int i=0; i<unpairedElectrons; i++) {
                currentMolecule.addElectronContainer(currentChemFile.getBuilder().newSingleElectron(currentAtom));
            }
        } else if (propertyType.equals("id")) {
          logger.debug("id: ", propertyValue);
          currentAtom.setID(propertyValue);
          atomEnumeration.put(propertyValue, new Integer(numberOfAtoms));
        }
      } else if (objectType.equals("Bond")) {
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
      } else if (objectType.equals("SetOfReactions")) {
          if (propertyType.equals("id")) {
              currentSetOfReactions.setID(propertyValue);
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
        objects.add("Bond");
        objects.add("Animation");
        objects.add("Frame");
        objects.add("Crystal");
        objects.add("a-axis");
        objects.add("b-axis");
        objects.add("c-axis");
        objects.add("SetOfReactions");
        objects.add("Reactions");
        objects.add("Reactant");
        objects.add("Product");
      return objects;
    }

	public void addChemSequence(IChemSequence chemSequence) {
		currentChemFile.addChemSequence(chemSequence);
	}

	public IChemSequence[] getChemSequences() {
		return currentChemFile.getChemSequences();
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

