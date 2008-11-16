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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.dict.DictRef;
import org.openscience.cdk.geometry.CrystalGeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IStrand;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.BondManipulator;
import org.xml.sax.Attributes;

/**
 * Core CML 1.x and 2.x elements are parsed by this class.
 *
 * <p>Please file a bug report if this parser fails to parse
 * a certain element or attribute value in a valid CML document.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 **/
public class CMLCoreModule implements ICMLModule {

    protected org.openscience.cdk.tools.LoggingTool logger;
    protected final String SYSTEMID = "CML-1999-05-15";
//    protected IChemicalDocumentObject cdo;

    // data model to store things into
    protected IChemFile currentChemFile;
	
    protected IAtomContainer currentMolecule;
    protected IMoleculeSet currentMoleculeSet;
    protected IChemModel currentChemModel;
    protected IChemSequence currentChemSequence;
    protected IReactionSet currentReactionSet;
    protected IReaction currentReaction;
    protected IAtom currentAtom;
    protected IBond currentBond;
    protected IStrand currentStrand;
    protected IMonomer currentMonomer;
    protected Map<String, IAtom> atomEnumeration;
    protected List<String> moleculeCustomProperty;
    
    // helper fields    
    protected int formulaCounter;
    protected int atomCounter;
    protected List<String> elsym;
    protected List<String> eltitles;
    protected List<String> elid;
    protected List<String> formula;
    protected List<String> formalCharges;
    protected List<String> partialCharges;
    protected List<String> isotope;
    protected List<String> atomicNumbers;
    protected List<String> exactMasses;
    protected List<String> x3;
    protected List<String> y3;
    protected List<String> z3;
    protected List<String> x2;
    protected List<String> y2;
    protected List<String> xfract;
    protected List<String> yfract;
    protected List<String> zfract;
    protected List<String> hCounts;
    protected List<String> atomParities;
    protected List<String> atomDictRefs;
    protected List<String> spinMultiplicities;
    protected List<String> occupancies;
    protected Map<Integer,List<String>> atomCustomProperty;


    protected int bondCounter;
    protected List<String> bondid;
    protected List<String> bondARef1;
    protected List<String> bondARef2;
    protected List<String> order;
    protected List<String> bondStereo;
    protected List<String> bondDictRefs;
    protected List<String> bondElid;
    protected List<Boolean> bondAromaticity;
    protected Map<String,Map<String,String>> bondCustomProperty;
    protected boolean stereoGiven;
    protected String inchi;
    protected int curRef;
    protected int CurrentElement;
    protected String BUILTIN;
    protected String DICTREF;
    protected String elementTitle;
    protected String currentChars;
    
    protected double[] unitcellparams;
    protected int crystalScalar;
    
//    private Vector3d aAxis;
//    private Vector3d bAxis;
//    private Vector3d cAxis;
    boolean cartesianAxesSet = false;
    
    public CMLCoreModule(IChemFile chemFile) {
        logger = new LoggingTool(this);
		this.currentChemFile = chemFile;
    }
    
    public CMLCoreModule(ICMLModule conv) {
    	logger = new LoggingTool(this);
        inherit(conv);
    }

    public void inherit(ICMLModule convention) {
        if (convention instanceof CMLCoreModule) {
            CMLCoreModule conv = (CMLCoreModule)convention;
            
            // copy the data model
            this.currentChemFile = conv.currentChemFile;
            this.currentMolecule = conv.currentMolecule;
            this.currentMoleculeSet = conv.currentMoleculeSet;
            this.currentChemModel = conv.currentChemModel;
            this.currentChemSequence = conv.currentChemSequence;
            this.currentReactionSet = conv.currentReactionSet;
            this.currentReaction = conv.currentReaction;
            this.currentAtom = conv.currentAtom;
            this.currentStrand = conv.currentStrand;
            this.currentMonomer = conv.currentMonomer;
            this.atomEnumeration = conv.atomEnumeration;
            this.moleculeCustomProperty = conv.moleculeCustomProperty;
            
            // copy the intermediate fields
            this.logger = conv.logger;
            this.BUILTIN = conv.BUILTIN;
            this.atomCounter = conv.atomCounter;
            this.formulaCounter = conv.formulaCounter;
            this.elsym = conv.elsym;
            this.eltitles = conv.eltitles;
            this.elid = conv.elid;
            this.formalCharges = conv.formalCharges;
            this.partialCharges = conv.partialCharges;
            this.isotope = conv.isotope;
            this.atomicNumbers = conv.atomicNumbers;
            this.exactMasses = conv.exactMasses;
            this.x3 = conv.x3;
            this.y3 = conv.y3;
            this.z3 = conv.z3;
            this.x2 = conv.x2;
            this.y2 = conv.y2;
            this.xfract = conv.xfract;
            this.yfract = conv.yfract;
            this.zfract = conv.zfract;
            this.hCounts = conv.hCounts;
            this.atomParities = conv.atomParities;
            this.atomDictRefs = conv.atomDictRefs;
            this.spinMultiplicities = conv.spinMultiplicities;
            this.occupancies = conv.occupancies;
            this.bondCounter = conv.bondCounter;
            this.bondid = conv.bondid;
            this.bondARef1 = conv.bondARef1;
            this.bondARef2 = conv.bondARef2;
            this.order = conv.order;
            this.bondStereo = conv.bondStereo;
            this.bondCustomProperty = conv.bondCustomProperty;
            this.atomCustomProperty = conv.atomCustomProperty;
            this.bondDictRefs = conv.bondDictRefs;
            this.bondAromaticity = conv.bondAromaticity;
            this.curRef = conv.curRef;
            this.unitcellparams = conv.unitcellparams;
            this.inchi = conv.inchi;
        } else {
            logger.warn("Cannot inherit information from module: ", convention.getClass().getName());
        }
    }

    public IChemFile returnChemFile() {
        return currentChemFile;
    }
    
    /**
     * Clean all data about parsed data.
     */
    protected void newMolecule() {
        newMoleculeData();
        newAtomData();
        newBondData();
        newCrystalData();
        newFormulaData();
    }
    
    /**
     * Clean all data about the molecule itself.
     */
    protected void newMoleculeData() {
        this.inchi = null;
    }

    /**
     * Clean all data about read formulas.
     */
    protected void newFormulaData() {
    	formulaCounter = 0;
        formula = new ArrayList<String>();
    }
    /**
     * Clean all data about read atoms.
     */
    protected void newAtomData() {
        atomCounter = 0;
        elsym = new ArrayList<String>();
        elid = new ArrayList<String>();
        eltitles = new ArrayList<String>();
        formalCharges = new ArrayList<String>();
        partialCharges = new ArrayList<String>();
        isotope = new ArrayList<String>();
        atomicNumbers = new ArrayList<String>();
        exactMasses = new ArrayList<String>();
        x3 = new ArrayList<String>();
        y3 = new ArrayList<String>();
        z3 = new ArrayList<String>();
        x2 = new ArrayList<String>();
        y2 = new ArrayList<String>();
        xfract = new ArrayList<String>();
        yfract = new ArrayList<String>();
        zfract = new ArrayList<String>();
        hCounts = new ArrayList<String>();
        atomParities = new ArrayList<String>();
        atomDictRefs = new ArrayList<String>();
        spinMultiplicities = new ArrayList<String>();
        occupancies = new ArrayList<String>();
        atomCustomProperty = new HashMap<Integer,List<String>>();
    }

    /**
     * Clean all data about read bonds.
     */
    protected void newBondData() {
        bondCounter = 0;
        bondid = new ArrayList<String>();
        bondARef1 = new ArrayList<String>();
        bondARef2 = new ArrayList<String>();
        order = new ArrayList<String>();
        bondStereo = new ArrayList<String>();
        bondCustomProperty = new Hashtable<String,Map<String,String>>();
        bondDictRefs = new ArrayList<String>();
        bondElid = new ArrayList<String>();
        bondAromaticity = new ArrayList<Boolean>();
    }

    /**
     * Clean all data about read bonds.
     */
    protected void newCrystalData() {
        unitcellparams = new double[6];
        cartesianAxesSet = false;
        crystalScalar = 0;
//        aAxis = new Vector3d();
//        bAxis = new Vector3d();
//        cAxis = new Vector3d();
    }

    public void startDocument() {
        logger.info("Start XML Doc");
        // cdo.startDocument();
        currentChemSequence = currentChemFile.getBuilder().newChemSequence();
        currentChemModel = currentChemFile.getBuilder().newChemModel();
        currentMoleculeSet = currentChemFile.getBuilder().newMoleculeSet();
        currentMolecule = currentChemFile.getBuilder().newMolecule();
        atomEnumeration = new HashMap<String, IAtom>();
        moleculeCustomProperty = new ArrayList<String>();
        
        newMolecule();
        BUILTIN = "";
        curRef = 0;
    }
    
    public void endDocument() {
//        cdo.endDocument();
    	if (currentReactionSet != null && currentReactionSet.getReactionCount() == 0
    			&& currentReaction != null) {
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
    	if (currentChemFile.getChemSequenceCount() == 0) {
    		// assume there is one non-animation ChemSequence
//    		addChemSequence(currentChemSequence);
    		currentChemFile.addChemSequence(currentChemSequence);
    	}
    	
        logger.info("End XML Doc");
    }
    
    public void startElement(CMLStack xpath, String uri, String local, String raw, 
                              Attributes atts) {
        String name = local;
        logger.debug("StartElement");
        currentChars = "";
        
        BUILTIN = "";
        DICTREF = "";
        
        for (int i=0; i<atts.getLength(); i++) {
            String qname = atts.getQName(i);
            if (qname.equals("builtin")) {
                BUILTIN = atts.getValue(i);
                logger.debug(name, "->BUILTIN found: ", atts.getValue(i));
            } else if (qname.equals("dictRef")) {
                DICTREF = atts.getValue(i);
                logger.debug(name, "->DICTREF found: ", atts.getValue(i));
            } else if (qname.equals("title")) {
                elementTitle = atts.getValue(i);
                logger.debug(name, "->TITLE found: ", atts.getValue(i));
            } else {
                logger.debug("Qname: ", qname);
            }
        }
        
        if ("atom".equals(name)) {
            atomCounter++;
            for (int i = 0; i < atts.getLength(); i++) {
                
                String att = atts.getQName(i);
                String value = atts.getValue(i);
                
                if (att.equals("id")) { // this is supported in CML 1.x
                    elid.add(value);
                } // this is supported in CML 2.0 
                else if (att.equals("elementType")) {
                    elsym.add(value);
                } // this is supported in CML 2.0 
                else if (att.equals("title")) {
                    eltitles.add(value);
                } // this is supported in CML 2.0 
                else if (att.equals("x2")) {
                    x2.add(value);
                } // this is supported in CML 2.0 
                else if (att.equals("xy2")) {
                    StringTokenizer tokenizer = new StringTokenizer(value);
                    x2.add(tokenizer.nextToken());
                    y2.add(tokenizer.nextToken());
                } // this is supported in CML 2.0 
                else if (att.equals("xyzFract")) {
                    StringTokenizer tokenizer = new StringTokenizer(value);
                    xfract.add(tokenizer.nextToken());
                    yfract.add(tokenizer.nextToken());
                    zfract.add(tokenizer.nextToken());
                } // this is supported in CML 2.0 
                else if (att.equals("xyz3")) {
                    StringTokenizer tokenizer = new StringTokenizer(value);
                    x3.add(tokenizer.nextToken());
                    y3.add(tokenizer.nextToken());
                    z3.add(tokenizer.nextToken());
                } // this is supported in CML 2.0 
                else if (att.equals("y2")) {
                    y2.add(value);
                } // this is supported in CML 2.0 
                else if (att.equals("x3")) {
                    x3.add(value);
                } // this is supported in CML 2.0 
                else if (att.equals("y3")) {
                    y3.add(value);
                } // this is supported in CML 2.0 
                else if (att.equals("z3")) {
                    z3.add(value);
                } // this is supported in CML 2.0 
                else if (att.equals("xFract")) {
                    xfract.add(value);
                } // this is supported in CML 2.0 
                else if (att.equals("yFract")) {
                    yfract.add(value);
                } // this is supported in CML 2.0 
                else if (att.equals("zFract")) {
                    zfract.add(value);
                } // this is supported in CML 2.0 
                else if (att.equals("formalCharge")) {
                    formalCharges.add(value);
                } // this is supported in CML 2.0 
                else if (att.equals("hydrogenCount")) {
                    hCounts.add(value);
                }
                else if (att.equals("isotopeNumber")) {
                    isotope.add(value);
                }
                else if (att.equals("dictRef")) {                	
                    logger.debug("ocupaccy: "+value);
                    atomDictRefs.add(value);
                } 
                else if (att.equals("spinMultiplicity")) {
                    spinMultiplicities.add(value);
                }
                else if (att.equals("occupancy")) {
                    occupancies.add(value);
                } 
                 
                else {
                    logger.warn("Unparsed attribute: " + att);
                }
            }
        } else if ("atomArray".equals(name) &&
        		   !xpath.endsWith("formula", "atomArray")) {
            boolean atomsCounted = false;
            for (int i = 0; i < atts.getLength(); i++) {
                String att = atts.getQName(i);
                int count = 0;
                if (att.equals("atomID")) {
                    count = addArrayElementsTo(elid, atts.getValue(i));
                } else if (att.equals("elementType")) {
                    count = addArrayElementsTo(elsym, atts.getValue(i));
                } else if (att.equals("x2")) {
                    count = addArrayElementsTo(x2, atts.getValue(i));
                } else if (att.equals("y2")) {
                    count = addArrayElementsTo(y2, atts.getValue(i));
                } else if (att.equals("x3")) {
                    count = addArrayElementsTo(x3, atts.getValue(i));
                } else if (att.equals("y3")) {
                    count = addArrayElementsTo(y3, atts.getValue(i));
                } else if (att.equals("z3")) {
                    count = addArrayElementsTo(z3, atts.getValue(i));
                } else if (att.equals("xFract")) {
                    count = addArrayElementsTo(xfract, atts.getValue(i));
                } else if (att.equals("yFract")) {
                    count = addArrayElementsTo(yfract, atts.getValue(i));
                } else if (att.equals("zFract")) {
                    count = addArrayElementsTo(zfract, atts.getValue(i));
                } else {
                    logger.warn("Unparsed attribute: " + att);
                }
                if (!atomsCounted) {
                    atomCounter += count;
                    atomsCounted = true;
                }
            }
        } else if ("bond".equals(name)) {
            bondCounter++;
            for (int i = 0; i < atts.getLength(); i++) {
                String att = atts.getQName(i);
                logger.debug("B2 ", att, "=", atts.getValue(i));
                
                if (att.equals("id")) {
                    bondid.add(atts.getValue(i));
                    logger.debug("B3 ", bondid);
                } else if (att.equals("atomRefs") || // this is CML 1.x support
                           att.equals("atomRefs2")) { // this is CML 2.0 support
                    
                    // expect exactly two references
                    try {
                        StringTokenizer st = new StringTokenizer(
                            atts.getValue(i)
                        );
                        bondARef1.add((String)st.nextElement());
                        bondARef2.add((String)st.nextElement());
                    } catch (Exception e) {
                        logger.error("Error in CML file: ", e.getMessage());
                        logger.debug(e);
                    }
                } else if (att.equals("order")) { // this is CML 2.0 support
                    order.add(atts.getValue(i).trim());
                } else if (att.equals("dictRef")) {
                    bondDictRefs.add(atts.getValue(i).trim());
                }
            }
            
            stereoGiven = false;
            curRef = 0;
        } else if ("bondArray".equals(name)) {
            boolean bondsCounted = false;
            for (int i = 0; i < atts.getLength(); i++) {
                String att = atts.getQName(i);
                int count = 0;
                if (att.equals("bondID")) {
                    count = addArrayElementsTo(bondid, atts.getValue(i));
                } else if (att.equals("atomRefs1")) {
                    count = addArrayElementsTo(bondARef1, atts.getValue(i));
                } else if (att.equals("atomRefs2")) {
                    count = addArrayElementsTo(bondARef2, atts.getValue(i));
                } else if (att.equals("atomRef1")) {
                    count = addArrayElementsTo(bondARef1, atts.getValue(i));
                } else if (att.equals("atomRef2")) {
                    count = addArrayElementsTo(bondARef2, atts.getValue(i));
                } else if (att.equals("order")) {
                    count = addArrayElementsTo(order, atts.getValue(i));
                } else {
                    logger.warn("Unparsed attribute: " + att);
                }
                if (!bondsCounted) {
                    bondCounter += count;
                    bondsCounted = true;
                }
            }
            curRef = 0;
        } else if ("bondStereo".equals(name)) {
            for (int i = 0; i < atts.getLength(); i++) {
                if (atts.getQName(i).equals("dictRef")) {
                	if (atts.getValue(i).startsWith("cml:"))
                	bondStereo.add(atts.getValue(i).substring(4));
                    stereoGiven=true;
                }
            }
        } else if ("bondType".equals(name)) {
            for (int i = 0; i < atts.getLength(); i++) {
                if (atts.getQName(i).equals("dictRef")) {
                	if (atts.getValue(i).equals("cdk:aromaticBond"))
                		bondAromaticity.add(Boolean.TRUE);
                }
            }
        } else if ("molecule".equals(name)) {
            newMolecule();
            BUILTIN = "";
//            cdo.startObject("Molecule");
            if (currentChemModel == null) currentChemModel = currentChemFile.getBuilder().newChemModel();
            if (currentMoleculeSet == null) currentMoleculeSet = currentChemFile.getBuilder().newMoleculeSet();
            currentMolecule = currentChemFile.getBuilder().newMolecule();
            for (int i = 0; i < atts.getLength(); i++) {
                if (atts.getQName(i).equals("id")) {
//                    cdo.setObjectProperty("Molecule", "id", atts.getValue(i));
                	currentMolecule.setID(atts.getValue(i));
                } else if (atts.getQName(i).equals("dictRef")) {
//                	cdo.setObjectProperty("Molecule", "dictRef", atts.getValue(i));
                	currentMolecule.setProperty(new DictRef(DICTREF, atts.getValue(i)), atts.getValue(i));
                }
            }
        } else if ("crystal".equals(name)) {
            newCrystalData();
//            cdo.startObject("Crystal");
            currentMolecule = currentChemFile.getBuilder().newCrystal(currentMolecule);
            for (int i = 0; i < atts.getLength(); i++) {
                String att = atts.getQName(i);
                if (att.equals("z")) {
//                    cdo.setObjectProperty("Crystal", "z", atts.getValue(i));
                	((ICrystal)currentMolecule).setZ(Integer.parseInt(atts.getValue(i)));
                }
            }
        } else if ("symmetry".equals(name)) {
            for (int i = 0; i < atts.getLength(); i++) {
                String att = atts.getQName(i);
                if (att.equals("spaceGroup")) {
//                    cdo.setObjectProperty("Crystal", "spacegroup", atts.getValue(i));
                	((ICrystal)currentMolecule).setSpaceGroup(atts.getValue(i));
                }
            }
        } else if ("identifier".equals(name)) {
        	if (atts.getValue("convention") != null && 
        		atts.getValue("convention").equals("iupac:inchi") &&
        		atts.getValue("value") != null) {
//                cdo.setObjectProperty("Molecule", "inchi", atts.getValue("value"));
        		currentMolecule.setProperty(CDKConstants.INCHI, atts.getValue("value"));
            }
        } else if ("scalar".equals(name)) {
            if (xpath.endsWith("crystal", "scalar"))
                crystalScalar++;
        } else if ("label".equals(name)) {
            if (xpath.endsWith("atomType", "label")) {
//            	cdo.setObjectProperty("Atom", "atomTypeLabel", atts.getValue("value"));
            	currentAtom.setAtomTypeName(atts.getValue("value"));
            }
        } else if ("list".equals(name)) {
//            cdo.startObject("MoleculeSet");
        	if (DICTREF.equals("cdk:model")) {
        		currentChemModel = currentChemFile.getBuilder().newChemModel();
        		// see if there is an ID attribute
        		for (int i = 0; i < atts.getLength(); i++) {
        			String att = atts.getQName(i);
        			if (att.equals("id")) {
        				currentChemModel.setID(atts.getValue(i));
        				}
        			}
        	} else if (DICTREF.equals("cdk:moleculeSet")) {
        		currentMoleculeSet = currentChemFile.getBuilder().newMoleculeSet();
        		// see if there is an ID attribute
        		for (int i = 0; i < atts.getLength(); i++) {
        			String att = atts.getQName(i);
        			if (att.equals("id")) {
        				currentMoleculeSet.setID(atts.getValue(i));
        				}
        			}
        		currentMolecule = currentChemFile.getBuilder().newMolecule();
        	} else {
        		// the old default
        		currentMoleculeSet = currentChemFile.getBuilder().newMoleculeSet();
        		// see if there is an ID attribute
        		for (int i = 0; i < atts.getLength(); i++) {
        			String att = atts.getQName(i);
        			if (att.equals("id")) {
        				currentMoleculeSet.setID(atts.getValue(i));
        				}
        			}
        		currentMolecule = currentChemFile.getBuilder().newMolecule();
        	}
        }else if ("formula".equals(name)){
        	formulaCounter++;
            for (int i = 0; i < atts.getLength(); i++) {
                String att = atts.getQName(i);
                String value = atts.getValue(i);
                if (att.equals("concise")) {
                	formula.add(value);
                }
            }
        }
    }

    public void endElement(CMLStack xpath, String uri, String name, String raw) {
        logger.debug("EndElement: ", name);

        String cData = currentChars;
        if ("bond".equals(name)) {
        	if (!stereoGiven)
                bondStereo.add("");
            if (bondCounter > bondDictRefs.size())
                bondDictRefs.add(null);
            if (bondCounter > bondAromaticity.size())
                bondAromaticity.add(null);
        } else if ("atom".equals(name)) {
            if (atomCounter > eltitles.size()) {
                eltitles.add(null);
            }
            if (atomCounter > hCounts.size()) {
                hCounts.add(null);
            }
            if (atomCounter > atomDictRefs.size()) {
                atomDictRefs.add(null);
            }
            if (atomCounter > isotope.size()) {
                isotope.add(null);
            }
            if (atomCounter > atomicNumbers.size()) {
                atomicNumbers.add(null);
            }
            if (atomCounter > exactMasses.size()) {
                exactMasses.add(null);
            }
            if (atomCounter > spinMultiplicities.size()) {
                spinMultiplicities.add(null);
            }
            if (atomCounter > occupancies.size()) {
                occupancies.add(null);
            }
            if (atomCounter > formalCharges.size()) {
                /* while strictly undefined, assume zero 
                formal charge when no number is given */
                formalCharges.add("0");
            }
            /* It may happen that not all atoms have
            associated 2D or 3D coordinates. accept that */
            if (atomCounter > x2.size() && x2.size() != 0) {
                /* apparently, the previous atoms had atomic
                coordinates, add 'null' for this atom */
                x2.add(null);
                y2.add(null);
            }
            if (atomCounter > x3.size() && x3.size() != 0) {
                /* apparently, the previous atoms had atomic
                coordinates, add 'null' for this atom */
                x3.add(null);
                y3.add(null);
                z3.add(null);
            }
            
            if (atomCounter > xfract.size() && xfract.size() != 0) {
                /* apparently, the previous atoms had atomic
                coordinates, add 'null' for this atom */
                xfract.add(null);
                yfract.add(null);
                zfract.add(null);
            }
        } else if ("molecule".equals(name)) {
            storeData();
//            cdo.endObject("Molecule");
            if (currentMolecule instanceof IMolecule) {
                logger.debug("Adding molecule to set");
                currentMoleculeSet.addMolecule((IMolecule)currentMolecule);
                logger.debug("#mols in set: " + currentMoleculeSet.getMoleculeCount());
            } else if (currentMolecule instanceof ICrystal) {
                logger.debug("Adding crystal to chemModel");
                currentChemModel.setCrystal((ICrystal)currentMolecule);
                currentChemSequence.addChemModel(currentChemModel);
            }
        } else if ("crystal".equals(name)) {
            if (crystalScalar > 0) {
                // convert unit cell parameters to cartesians
                Vector3d[] axes = CrystalGeometryTools.notionalToCartesian(
                    unitcellparams[0], unitcellparams[1], unitcellparams[2],
                    unitcellparams[3], unitcellparams[4], unitcellparams[5]
                );
                cartesianAxesSet = true;
//                cdo.startObject("a-axis");
//                cdo.setObjectProperty("a-axis", "x", new Double(aAxis.x).toString());
//                cdo.setObjectProperty("a-axis", "y", new Double(aAxis.y).toString());
//                cdo.setObjectProperty("a-axis", "z", new Double(aAxis.z).toString());
//                cdo.endObject("a-axis");
//                cdo.startObject("b-axis");
//                cdo.setObjectProperty("b-axis", "x", new Double(bAxis.x).toString());
//                cdo.setObjectProperty("b-axis", "y", new Double(bAxis.y).toString());
//                cdo.setObjectProperty("b-axis", "z", new Double(bAxis.z).toString());
//                cdo.endObject("b-axis");
//                cdo.startObject("c-axis");
//                cdo.setObjectProperty("c-axis", "x", new Double(cAxis.x).toString());
//                cdo.setObjectProperty("c-axis", "y", new Double(cAxis.y).toString());
//                cdo.setObjectProperty("c-axis", "z", new Double(cAxis.z).toString());
//                cdo.endObject("c-axis");
                ((ICrystal)currentMolecule).setA(axes[0]);
                ((ICrystal)currentMolecule).setB(axes[1]);
                ((ICrystal)currentMolecule).setC(axes[2]);
            } else {
                logger.error("Could not find crystal unit cell parameters");
            }
//            cdo.endObject("Crystal");
        } else if ("list".equals(name)) {
//            cdo.endObject("MoleculeSet");
        	// FIXME: I really should check the DICTREF, but there is currently
        	// no mechanism for storing these for use with endTag() :(
        	// So, instead, for now, just see if it already has done the setting
        	// to work around duplication
        	if (currentChemModel.getMoleculeSet() != currentMoleculeSet) {
        		currentChemModel.setMoleculeSet(currentMoleculeSet);
        		currentChemSequence.addChemModel(currentChemModel);
        	}
        } else if ("coordinate3".equals(name)) {
            if (BUILTIN.equals("xyz3")) {
                logger.debug("New coord3 xyz3 found: ", currentChars);
                
                try {
                    
                    StringTokenizer st = new StringTokenizer(currentChars);
                    x3.add(st.nextToken());
                    y3.add(st.nextToken());
                    z3.add(st.nextToken());
                    logger.debug("coord3 x3.length: ", x3.size());
                    logger.debug("coord3 y3.length: ", y3.size());
                    logger.debug("coord3 z3.length: ", z3.size());
                } catch (Exception exception) {
                    logger.error(
                    "CMLParsing error while setting coordinate3!");
                    logger.debug(exception);
                }
            } else {
                logger.warn("Unknown coordinate3 BUILTIN: " + BUILTIN);
            }
        } else if ("string".equals(name)) {
            if (BUILTIN.equals("elementType")) {
                logger.debug("Element: ", cData.trim());
                elsym.add(cData);
            } else if (BUILTIN.equals("atomRef")) {
                curRef++;
                logger.debug("Bond: ref #", curRef);
                
                if (curRef == 1) {
                    bondARef1.add(cData.trim());
                } else if (curRef == 2) {
                    bondARef2.add(cData.trim());
                }
            } else if (BUILTIN.equals("order")) {
                logger.debug("Bond: order ", cData.trim());
                order.add(cData.trim());
            } else if (BUILTIN.equals("formalCharge")) {
                // NOTE: this combination is in violation of the CML DTD!!!
                logger.warn("formalCharge BUILTIN accepted but violating CML DTD");
                logger.debug("Charge: ", cData.trim());
                String charge = cData.trim();
                if (charge.startsWith("+") && charge.length() > 1) {
                    charge = charge.substring(1);
                }
                formalCharges.add(charge);
            }
        } else if ("float".equals(name)) {
            if (BUILTIN.equals("x3")) {
                x3.add(cData.trim());
            } else if (BUILTIN.equals("y3")) {
                y3.add(cData.trim());
            } else if (BUILTIN.equals("z3")) {
                z3.add(cData.trim());
            } else if (BUILTIN.equals("x2")) {
                x2.add(cData.trim());
            } else if (BUILTIN.equals("y2")) {
                y2.add(cData.trim());
            } else if (BUILTIN.equals("order")) {
                // NOTE: this combination is in violation of the CML DTD!!!
                order.add(cData.trim());
            } else if (BUILTIN.equals("charge") || BUILTIN.equals("partialCharge")) {
                partialCharges.add(cData.trim());
            }
        } else if ("integer".equals(name)) {
            if (BUILTIN.equals("formalCharge")) {
                formalCharges.add(cData.trim());
            }
        } else if ("coordinate2".equals(name)) {
            if (BUILTIN.equals("xy2")) {
                logger.debug("New coord2 xy2 found.", cData);
                
                try {
                    
                    StringTokenizer st = new StringTokenizer(cData);
                    x2.add(st.nextToken());
                    y2.add(st.nextToken());
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 175, 1);
                }
            }
        } else if ("stringArray".equals(name)) {
            if (BUILTIN.equals("id") || BUILTIN.equals("atomId")
                || BUILTIN.equals("atomID")) { // invalid according to CML1 DTD but found in OpenBabel 1.x output
                
                try {
                    boolean countAtoms = (atomCounter == 0) ? true : false;
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens()) {
                        if (countAtoms) { atomCounter++; }
                        String token = st.nextToken();
                        logger.debug("StringArray (Token): ", token);
                        elid.add(token);
                    }
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 186, 1);
                }
            } else if (BUILTIN.equals("elementType")) {
                
                try {
                    boolean countAtoms = (atomCounter == 0) ? true : false;
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens()) {
                        if (countAtoms) { atomCounter++; }
                        elsym.add(st.nextToken());
                    }
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 194, 1);
                }
            } else if (BUILTIN.equals("atomRefs")) {
                curRef++;
                logger.debug("New atomRefs found: ", curRef);
                
                try {
                    boolean countBonds = (bondCounter == 0) ? true : false;
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens()) {
                        if (countBonds) { bondCounter++; }
                        String token = st.nextToken();
                        logger.debug("Token: ", token);
                        
                        if (curRef == 1) {
                            bondARef1.add(token);
                        } else if (curRef == 2) {
                            bondARef2.add(token);
                        }
                    }
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 194, 1);
                }
            } else if (BUILTIN.equals("atomRef")) {
                curRef++;
                logger.debug("New atomRef found: ", curRef); // this is CML1 stuff, we get things like:
                /*
                  <bondArray>
                  <stringArray builtin="atomRef">a2 a2 a2 a2 a3 a3 a4 a4 a5 a6 a7 a9</stringArray>
                  <stringArray builtin="atomRef">a9 a11 a12 a13 a5 a4 a6 a9 a7 a8 a8 a10</stringArray>
                  <stringArray builtin="order">1 1 1 1 2 1 2 1 1 1 2 2</stringArray>
                  </bondArray>
                */
                
                try {
                    boolean countBonds = (bondCounter == 0) ? true : false;
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens()) {
                        if (countBonds) { bondCounter++; }
                        String token = st.nextToken();
                        logger.debug("Token: ", token);
                        
                        if (curRef == 1) {
                            bondARef1.add(token);
                        } else if (curRef == 2) {
                            bondARef2.add(token);
                        }
                    }
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 194, 1);
                }
            } else if (BUILTIN.equals("order")) {
                logger.debug("New bond order found.");
                
                try {
                    
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens()) {
                        
                        String token = st.nextToken();
                        logger.debug("Token: ", token);
                        order.add(token);
                    }
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 194, 1);
                }
            }
        } else if ("integerArray".equals(name)) {
            logger.debug("IntegerArray: builtin = ", BUILTIN);
            
            if (BUILTIN.equals("formalCharge")) {
                
                try {
                    
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens()) {
                        
                        String token = st.nextToken();
                        logger.debug("Charge added: ", token);
                        formalCharges.add(token);
                    }
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 205, 1);
                }
            }
        } else if ("scalar".equals(name)) {
            if (xpath.endsWith("crystal", "scalar")) {
                logger.debug("Going to set a crystal parameter: " + crystalScalar, 
                    " to ", cData);
                try {
                    unitcellparams[crystalScalar-1] = Double.parseDouble(cData.trim());
                } catch (NumberFormatException exception) {
                    logger.error("Content must a float: " + cData);
                }
            } else if (xpath.endsWith("bond", "scalar")) {
                if (DICTREF.equals("mdl:stereo")) {
                	bondStereo.add(cData.trim());
                    stereoGiven=true;
                }else{
                	Map<String,String> bp = bondCustomProperty.get(bondid.get(bondid.size()-1));
                	if (bp == null) {
                		bp = new Hashtable<String, String>();
                		bondCustomProperty.put(bondid.get(bondid.size()-1), bp);
                	}
                	bp.put(elementTitle, cData.trim());
                }
            } else if (xpath.endsWith("atom", "scalar")) {
                if (DICTREF.equals("cdk:partialCharge")) {
                    partialCharges.add(cData.trim());
                } else if (DICTREF.equals("cdk:atomicNumber")) {
                    atomicNumbers.add(cData.trim());
                } else if (DICTREF.equals("cdk:isotopicMass")) {
                    exactMasses.add(cData.trim());
                }else {
                	if(atomCustomProperty.get(Integer.valueOf(atomCounter-1))==null)
                		atomCustomProperty.put(Integer.valueOf(atomCounter-1),new ArrayList<String>());
                	atomCustomProperty.get(Integer.valueOf(atomCounter-1)).add(elementTitle);
                	atomCustomProperty.get(Integer.valueOf(atomCounter-1)).add(cData.trim());
                }
            } else if (xpath.endsWith("molecule", "scalar")) {
                if (DICTREF.equals("pdb:id")) {
//                	cdo.setObjectProperty("Molecule", DICTREF, cData);
                	currentMolecule.setProperty(new DictRef(DICTREF, cData), cData);
                } else if (DICTREF.equals("cdk:molecularProperty")) {
                	currentMolecule.setProperty(elementTitle, cData);
                }else{
                	moleculeCustomProperty.add(elementTitle);
                	moleculeCustomProperty.add(cData.trim());
                }
            } else {
                logger.warn("Ignoring scalar: " + xpath);
            }
        } else if ("floatArray".equals(name)) {
            if (BUILTIN.equals("x3")) {
                
                try {
                    
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens())
                        x3.add(st.nextToken());
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 205, 1);
                }
            } else if (BUILTIN.equals("y3")) {
                
                try {
                    
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens())
                        y3.add(st.nextToken());
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 213, 1);
                }
            } else if (BUILTIN.equals("z3")) {
                
                try {
                    
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens())
                        z3.add(st.nextToken());
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 221, 1);
                }
            } else if (BUILTIN.equals("x2")) {
                logger.debug("New floatArray found.");
                
                try {
                    
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens())
                        x2.add(st.nextToken());
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 205, 1);
                }
            } else if (BUILTIN.equals("y2")) {
                logger.debug("New floatArray found.");
                
                try {
                    
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens())
                        y2.add(st.nextToken());
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 454, 1);
                }
            } else if (BUILTIN.equals("partialCharge")) {
                logger.debug("New floatArray with partial charges found.");
                
                try {
                    
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens())
                        partialCharges.add(st.nextToken());
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 462, 1);
                }
            }
        } else if ("basic".equals(name)) {
            // assuming this is the child element of <identifier>
            this.inchi = cData;
        } else if ("name".equals(name)) {
            if (xpath.endsWith("molecule", "name")) {
            	if (DICTREF.length() > 0) {
//            		cdo.setObjectProperty("Molecule", DICTREF, cData);
            		
            		currentMolecule.setProperty(new DictRef(DICTREF, cData), cData);
            	} else {
//            		cdo.setObjectProperty("Molecule", "Name", cData);
            		currentMolecule.setProperty(CDKConstants.TITLE, cData);
            	}
            }
        }else if ("formula".equals(name)) {
        	currentMolecule.setProperty(CDKConstants.FORMULA, cData);
        }else {
        
            logger.warn("Skipping element: " + name);
        }

        currentChars = "";
        BUILTIN = "";
        elementTitle = "";
    }

    public void characterData(CMLStack xpath, char[] ch, int start, int length) {
        currentChars = currentChars + new String(ch, start, length);
        logger.debug("CD: ", currentChars);
    }

    protected void notify(String message, String systemId, int line, 
                          int column) {
        logger.debug("Message: ", message);
        logger.debug("SystemId: ", systemId);
        logger.debug("Line: ", line);
        logger.debug("Column: ", column);
    }

    protected void storeData() {
        if (inchi != null) {
//            cdo.setObjectProperty("Molecule", "inchi", inchi);
        	currentMolecule.setProperty(CDKConstants.INCHI, inchi);
        }
        if (formula != null){
        	currentMolecule.setProperty(CDKConstants.FORMULA, formula);
        }
        Iterator<String> customs=moleculeCustomProperty.iterator();
        while(customs.hasNext()){
        	String x = customs.next();
        	String y = customs.next();
       		currentMolecule.setProperty(x,y);
        }
        storeAtomData();
        storeBondData();
        convertCMLToCDKHydrogenCounts();
    }

    private void convertCMLToCDKHydrogenCounts() {
        for (IAtom atom : currentMolecule.atoms()) {
            if (atom.getHydrogenCount() != null) {
                int explicitHCount = AtomContainerManipulator.countExplicitHydrogens(currentMolecule, atom);
                if (explicitHCount != 0) {
                    atom.setHydrogenCount(atom.getHydrogenCount() - explicitHCount);
                }
            }
        }
	}

	protected void storeAtomData() {
        logger.debug("No atoms: ", atomCounter);
        if (atomCounter == 0) {
            return;
        }

        boolean hasID = false;
        boolean has3D = false;
        boolean has3Dfract = false;
        boolean has2D = false;
        boolean hasFormalCharge = false;
        boolean hasPartialCharge = false;
        boolean hasHCounts = false;
        boolean hasSymbols = false;
        boolean hasTitles = false;
        boolean hasIsotopes = false;
        boolean hasAtomicNumbers = false;
        boolean hasExactMasses = false;
        boolean hasDictRefs = false;
        boolean hasSpinMultiplicities = false;
        boolean hasOccupancies = false;

        if (elid.size() == atomCounter) {
            hasID = true;
        } else {
            logger.debug("No atom ids: " + elid.size(), " != " + atomCounter);
        }

        if (elsym.size() == atomCounter) {
            hasSymbols = true;
        } else {
            logger.debug(
                    "No atom symbols: " + elsym.size(), " != " + atomCounter);
        }

        if (eltitles.size() == atomCounter) {
            hasTitles = true;
        } else {
            logger.debug(
                    "No atom titles: " + eltitles.size(), " != " + atomCounter);
        }

        if ((x3.size() == atomCounter) && (y3.size() == atomCounter) && 
            (z3.size() == atomCounter)) {
            has3D = true;
        } else {
            logger.debug(
                    "No 3D info: " + x3.size(), " " + y3.size(), " " + 
                    z3.size(), " != " + atomCounter);
        }

        if ((xfract.size() == atomCounter) && (yfract.size() == atomCounter) && 
            (zfract.size() == atomCounter)) {
            has3Dfract = true;
        } else {
            logger.debug(
                    "No 3D fractional info: " + xfract.size(), " " + yfract.size(), " " + 
                    zfract.size(), " != " + atomCounter);
        }

        if ((x2.size() == atomCounter) && (y2.size() == atomCounter)) {
            has2D = true;
        } else {
            logger.debug(
                    "No 2D info: " + x2.size(), " " + y2.size(), " != " + 
                    atomCounter);
        }

        if (formalCharges.size() == atomCounter) {
            hasFormalCharge = true;
        } else {
            logger.debug(
                    "No formal Charge info: " + formalCharges.size(), 
                    " != " + atomCounter);
        }

        if (partialCharges.size() == atomCounter) {
            hasPartialCharge = true;
        } else {
            logger.debug(
                    "No partial Charge info: " + partialCharges.size(),
                    " != " + atomCounter);
        }

        if (hCounts.size() == atomCounter) {
            hasHCounts = true;
        } else {
            logger.debug(
                    "No hydrogen Count info: " + hCounts.size(), 
                    " != " + atomCounter);
        }

        if (spinMultiplicities.size() == atomCounter) {
            hasSpinMultiplicities = true;
        } else {
            logger.debug(
                    "No spinMultiplicity info: " + spinMultiplicities.size(),
                    " != " + atomCounter);
        }

        if (occupancies.size() == atomCounter) {
            hasOccupancies = true;
        } else {
            logger.debug(
                    "No occupancy info: " + occupancies.size(),
                    " != " + atomCounter);
        }

        if (atomDictRefs.size() == atomCounter) {
            hasDictRefs = true;
        } else {
            logger.debug(
                    "No dictRef info: " + atomDictRefs.size(),
                    " != " + atomCounter);
        }

        if (isotope.size() == atomCounter) {
            hasIsotopes = true;
        } else {
            logger.debug(
                    "No isotope info: " + isotope.size(),
                    " != " + atomCounter);
        }
        if (atomicNumbers.size() == atomCounter) {
            hasAtomicNumbers = true;
        } else {
            logger.debug(
                    "No atomicNumbers info: " + atomicNumbers.size(),
                    " != " + atomCounter);
        }
        if (exactMasses.size() == atomCounter) {
            hasExactMasses = true;
        } else {
            logger.debug(
                    "No atomicNumbers info: " + atomicNumbers.size(),
                    " != " + atomCounter);
        }

        for (int i = 0; i < atomCounter; i++) {
            logger.info("Storing atom: ", i);
//            cdo.startObject("Atom");
            currentAtom = currentChemFile.getBuilder().newAtom("H");
            logger.debug("Atom # " + atomCounter);
            if (hasID) {
//                cdo.setObjectProperty("Atom", "id", (String)elid.get(i));
            	logger.debug("id: ", (String)elid.get(i));
                currentAtom.setID((String)elid.get(i));
                atomEnumeration.put((String)elid.get(i), currentAtom);
            }
            if (hasTitles) {
                if (hasSymbols) {
                    String symbol = (String)elsym.get(i);
                    if (symbol.equals("Du") || symbol.equals("Dummy")) {
//                        cdo.setObjectProperty("PseudoAtom", "label", (String)eltitles.get(i));
                    	if (!(currentAtom instanceof IPseudoAtom)) {
                            currentAtom = currentChemFile.getBuilder().newPseudoAtom(currentAtom);
                            if (hasID)
                            	atomEnumeration.put((String)elid.get(i), currentAtom);
                        }
                        ((IPseudoAtom)currentAtom).setLabel((String)eltitles.get(i));
                    } else {
//                        cdo.setObjectProperty("Atom", "title", (String)eltitles.get(i));
                    	// FIXME: huh?
                    	if (eltitles.get(i) != null)
                    		currentAtom.setProperty(CDKConstants.TITLE, (String)eltitles.get(i));
                    }
                } else {
//                    cdo.setObjectProperty("Atom", "title", (String)eltitles.get(i));
                	// FIXME: huh?
                	if (eltitles.get(i) != null)
                		currentAtom.setProperty(CDKConstants.TITLE, (String)eltitles.get(i));
                }
            }

            // store optional atom properties
            if (hasSymbols) {
                String symbol = (String)elsym.get(i);
                if (symbol.equals("Du") || symbol.equals("Dummy")) {
                    symbol = "R";
                }
//                cdo.setObjectProperty("Atom", "type", symbol);
                if (symbol.equals("R") && !(currentAtom instanceof IPseudoAtom)) {
                    currentAtom = currentChemFile.getBuilder().newPseudoAtom(currentAtom);
                    if (hasID)
                    	atomEnumeration.put((String)elid.get(i), currentAtom);
                }
                currentAtom.setSymbol(symbol);
            }

            if (has3D) {
//                cdo.setObjectProperty("Atom", "x3", (String)x3.get(i));
//                cdo.setObjectProperty("Atom", "y3", (String)y3.get(i));
//                cdo.setObjectProperty("Atom", "z3", (String)z3.get(i));
            	if (x3.get(i) != null &&
            		y3.get(i) != null &&
            		z3.get(i) != null) {
            		currentAtom.setPoint3d(
           				new Point3d(
          					Double.parseDouble((String)x3.get(i)),
          					Double.parseDouble((String)y3.get(i)),
            				Double.parseDouble((String)z3.get(i))
            			)
            		);
            	}
            }

            if (has3Dfract) {
                // ok, need to convert fractional into eucledian coordinates
//                cdo.setObjectProperty("Atom", "xFract", (String)xfract.get(i));
//                cdo.setObjectProperty("Atom", "yFract", (String)yfract.get(i));
//                cdo.setObjectProperty("Atom", "zFract", (String)zfract.get(i));
                currentAtom.setFractionalPoint3d(
               		new Point3d(
               			Double.parseDouble((String)xfract.get(i)),
               			Double.parseDouble((String)yfract.get(i)),
               			Double.parseDouble((String)zfract.get(i))
               		)
               	);
            }

            if (hasFormalCharge) {
//                cdo.setObjectProperty("Atom", "formalCharge", 
//                                      (String)formalCharges.get(i));
                currentAtom.setFormalCharge(Integer.parseInt((String)formalCharges.get(i)));
            }

            if (hasPartialCharge) {
                logger.debug("Storing partial atomic charge...");
//                cdo.setObjectProperty("Atom", "partialCharge", 
//                                      (String)partialCharges.get(i));
                currentAtom.setCharge(Double.parseDouble((String)partialCharges.get(i)));
            }

            if (hasHCounts) {
//                cdo.setObjectProperty("Atom", "hydrogenCount", (String)hCounts.get(i));
            	// FIXME: the hCount in CML is the total of implicit *and* explicit
                String hCount = hCounts.get(i);
                if (hCount != null) {
                    currentAtom.setHydrogenCount(Integer.parseInt(hCount));
                } else {
                    currentAtom.setHydrogenCount((Integer)CDKConstants.UNSET);
                }
            }

            if (has2D) {
                if (x2.get(i) != null && y2.get(i) != null) {
//                    cdo.setObjectProperty("Atom", "x2", (String)x2.get(i));
//                    cdo.setObjectProperty("Atom", "y2", (String)y2.get(i));
                	currentAtom.setPoint2d(
                		new Point2d(
                			Double.parseDouble((String)x2.get(i)),
                			Double.parseDouble((String)y2.get(i))
               			)
                	);
                }
            }
            
            if (hasDictRefs) {
//                cdo.setObjectProperty("Atom", "dictRef", (String)atomDictRefs.get(i));
            	if (atomDictRefs.get(i) != null)
            		currentAtom.setProperty("org.openscience.cdk.dict", (String)atomDictRefs.get(i));
            }

            if (hasSpinMultiplicities && spinMultiplicities.get(i) != null) {
//                cdo.setObjectProperty("Atom", "spinMultiplicity", (String)spinMultiplicities.get(i));
            	int unpairedElectrons = Integer.parseInt((String)spinMultiplicities.get(i))-1;
                for (int sm=0; sm<unpairedElectrons; sm++) {
                    currentMolecule.addSingleElectron(currentChemFile.getBuilder().newSingleElectron(currentAtom));
                }
            }

            if (hasOccupancies && occupancies.get(i) != null) {
//                cdo.setObjectProperty("Atom", "occupanciy", (String)occupancies.get(i));
            	// FIXME: this has no ChemFileCDO equivalent, not even if spelled correctly
            }

            if (hasIsotopes) {
//                cdo.setObjectProperty("Atom", "massNumber", (String)isotope.get(i));
            	if (isotope.get(i) != null)
            		currentAtom.setMassNumber((int)Double.parseDouble((String)isotope.get(i)));
            }
            
            if (hasAtomicNumbers) {
              if (atomicNumbers.get(i) != null)
                currentAtom.setAtomicNumber(Integer.parseInt(atomicNumbers.get(i)));
            }

            if (hasExactMasses) {
                if (exactMasses.get(i) != null)
                  currentAtom.setExactMass(Double.parseDouble(exactMasses.get(i)));
              }

            if(atomCustomProperty.get(Integer.valueOf(i))!=null){
            	Iterator<String> it=atomCustomProperty.get(Integer.valueOf(i)).iterator();
            	while(it.hasNext()){
	            	currentAtom.setProperty(it.next(),it.next());
            	}
            }

//            cdo.endObject("Atom");
            currentMolecule.addAtom(currentAtom);
        }
        if (elid.size() > 0) {
            // assume this is the current working list
            bondElid = elid;
        }
        newAtomData();
    }
    
    protected void storeBondData() {
        logger.debug(
                "Testing a1,a2,stereo,order = count: " + bondARef1.size(), "," + 
                bondARef2.size(), "," + bondStereo.size(), "," + order.size(), "=" +
                bondCounter);

        if ((bondARef1.size() == bondCounter) && 
            (bondARef2.size() == bondCounter)) {
            logger.debug("About to add bond info...");

            Iterator<String> orders = order.iterator();
            Iterator<String> ids = bondid.iterator();
            Iterator<String> bar1s = bondARef1.iterator();
            Iterator<String> bar2s = bondARef2.iterator();
            Iterator<String> stereos = bondStereo.iterator();
            Iterator<Boolean> aroms = bondAromaticity.iterator();

            while (bar1s.hasNext()) {
//                cdo.startObject("Bond");
//                if (ids.hasNext()) {
//                    cdo.setObjectProperty("Bond", "id", (String)ids.next());
//                }
//                cdo.setObjectProperty("Bond", "atom1", 
//                                      Integer.valueOf(bondElid.indexOf(
//                                                          (String)bar1s.next())).toString());
//                cdo.setObjectProperty("Bond", "atom2", 
//                                      Integer.valueOf(bondElid.indexOf(
//                                                          (String)bar2s.next())).toString());
                IAtom a1 = (IAtom)atomEnumeration.get((String)bar1s.next());
            	IAtom a2 = (IAtom)atomEnumeration.get((String)bar2s.next());
            	currentBond = currentChemFile.getBuilder().newBond(a1, a2);
            	if (ids.hasNext()) {
            		currentBond.setID((String)ids.next());
            	}

                if (orders.hasNext()) {
                    String bondOrder = (String)orders.next();
                    
                    if ("S".equals(bondOrder)) {
//                        cdo.setObjectProperty("Bond", "order", "1");
                    	currentBond.setOrder(CDKConstants.BONDORDER_SINGLE);
                    } else if ("D".equals(bondOrder)) {
//                        cdo.setObjectProperty("Bond", "order", "2");
                    	currentBond.setOrder(CDKConstants.BONDORDER_DOUBLE);
                    } else if ("T".equals(bondOrder)) {
//                        cdo.setObjectProperty("Bond", "order", "3");
                    	currentBond.setOrder(CDKConstants.BONDORDER_TRIPLE);
                    } else if ("A".equals(bondOrder)) {
//                        cdo.setObjectProperty("Bond", "order", "1.5");
                    	currentBond.setOrder(CDKConstants.BONDORDER_SINGLE);
                    	currentBond.setFlag(CDKConstants.ISAROMATIC, true);
                    } else {
//                        cdo.setObjectProperty("Bond", "order", bondOrder);
                    	currentBond.setOrder(
                    		BondManipulator.createBondOrder(Double.parseDouble(bondOrder))
                    	);
                    }
                }

                if (stereos.hasNext()) {
//                    cdo.setObjectProperty("Bond", "stereo", 
//                                          (String)stereos.next());
                	String nextStereo = (String)stereos.next();
                    if ("H".equals(nextStereo)) {
                    	currentBond.setStereo(CDKConstants.STEREO_BOND_DOWN);
                    } else if ("W".equals(nextStereo)) {
                    	currentBond.setStereo(CDKConstants.STEREO_BOND_UP);
                    } else if (nextStereo != null){
                    	logger.warn("Cannot interpret stereo information: " + nextStereo);
                    }
                }

                if (aroms.hasNext()) {
                	Object nextArom = aroms.next();
                	if (nextArom != null && nextArom == Boolean.TRUE) {
                		currentBond.setFlag(CDKConstants.ISAROMATIC, true);
                	}
                }
                
                if (currentBond.getID() != null) {
                    Map<String,String> currentBondProperties = bondCustomProperty.get(currentBond.getID());
                    if (currentBondProperties != null) {
                        Iterator<String> keys = currentBondProperties.keySet().iterator();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            currentBond.setProperty(key,currentBondProperties.get(key));
                        }
                        bondCustomProperty.remove(currentBond.getID());
                    }
                }

//                cdo.endObject("Bond");
                currentMolecule.addBond(currentBond);
            }
        }
        newBondData();
    }

    protected int addArrayElementsTo(List<String> toAddto, String array) {
        StringTokenizer tokenizer = new StringTokenizer(array);
        int i = 0;
        while (tokenizer.hasMoreElements()) {
            toAddto.add(tokenizer.nextToken());
            i++;
        }
        return i;
    }
}
