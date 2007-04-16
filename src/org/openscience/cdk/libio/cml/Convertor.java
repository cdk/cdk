/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2005-2007  Stefan Kuhn <shk3@users.sf.net>
 *
 * Contact: jchempaint-devel@lists.sourceforge.net
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
 */
package org.openscience.cdk.libio.cml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OptionalDataException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Monomer;
import org.openscience.cdk.Strand;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.dict.DictRef;
import org.openscience.cdk.dict.DictionaryDatabase;
import org.openscience.cdk.geometry.CrystalGeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.protein.data.PDBPolymer;
import org.openscience.cdk.tools.IDCreator;
import org.openscience.cdk.tools.LoggingTool;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondStereo;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLIdentifier;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLProduct;
import org.xmlcml.cml.element.CMLProductList;
import org.xmlcml.cml.element.CMLReactant;
import org.xmlcml.cml.element.CMLReactantList;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.CMLReactionList;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLSubstance;
import org.xmlcml.cml.element.CMLSubstanceList;

/**
 * @cdk.module       libio-cml
 * @cdk.keyword      CML
 * @cdk.keyword      class convertor
 * @cdk.builddepends jumbo52.jar
 * @cdk.require      java1.5+
 */
public class Convertor {

    public final static String NS_CML = "http://www.xml-cml.org/schema";

    private LoggingTool logger;

    private final static String CUSTOMIZERS_LIST = "libio-cml-customizers.set";
    private static Map customizers = null;

    private boolean useCMLIDs;
    private String prefix;

    /**
     * Constructs a CML convertor.
     *
     * @param useCMLIDs Uses object IDs like 'a1' instead of 'a&lt;hash>'.
     * @param prefix    Namespace prefix to use. If null, then no prefix is used;
     */
    public Convertor(boolean useCMLIDs, String prefix) {
        logger = new LoggingTool(this);
        this.useCMLIDs = useCMLIDs;
        this.prefix = prefix;
        setupCustomizers();
    }

    public void registerCustomizer(ICMLCustomizer customizer) {
    	if (customizers == null) customizers = new HashMap();
    	
    	if (!customizers.containsKey(customizer.getClass().getName())) {
    		customizers.put(customizer.getClass().getName(), customizer);
    		logger.info("Loaded Customizer: ", customizer.getClass().getName());
    	} else {
    		logger.warn("Duplicate attempt to register a customizer");
    	}
    }
    
    private void setupCustomizers() {
        if (customizers == null) customizers = new HashMap();
        
        try {
        	logger.debug("Starting loading Customizers...");
        	BufferedReader reader = new BufferedReader(new InputStreamReader(
        			this.getClass().getClassLoader().getResourceAsStream(CUSTOMIZERS_LIST)
        	));
        	int customizerCount = 0;
        	while (reader.ready()) {
        		// load them one by one
        		String customizerName = reader.readLine();
        		customizerCount++;
    			if (customizers.containsKey(customizerName)) {
    				try {
    					ICMLCustomizer customizer = (ICMLCustomizer) this.getClass().getClassLoader().
    					loadClass(customizerName).newInstance();
    					customizers.put(customizer.getClass().getName(), customizer);
    					logger.info("Loaded Customizer: ", customizer.getClass().getName());
    				} catch (ClassNotFoundException exception) {
    					logger.info("Could not find this Customizer: ", customizerName);
    					logger.debug(exception);
    				} catch (Exception exception) {
    					logger.warn("Could not load this Customizer: ", customizerName);
    					logger.warn(exception.getMessage());
    					logger.debug(exception);
    				}
    			} else {
    				logger.warn("Duplicate attempt to register a customizer");
    			}
        	}
        	logger.info("Number of loaded customizers: ", customizerCount);
        } catch (Exception exception) {
        	logger.error("Could not load this list: ", CUSTOMIZERS_LIST);
        	logger.debug(exception);
        }
    }

    public CMLCml cdkChemFileToCMLList(IChemFile file) {
        return cdkChemFileToCMLList(file, true);
    }

    private CMLCml cdkChemFileToCMLList(IChemFile file, boolean setIDs) {
        CMLCml cmlList = new CMLCml();
        cmlList.setDictRef("cdk:document");

        if (useCMLIDs && setIDs) {
            IDCreator.createIDs(file);
        }

        if (file.getChemSequenceCount() > 0) {
            java.util.Iterator sequences = file.chemSequences();
            while (sequences.hasNext()) {
                cmlList.appendChild(cdkChemSequenceToCMLList((IChemSequence)sequences.next()));
            }
        }

        return cmlList;
    }

    public CMLList cdkChemSequenceToCMLList(IChemSequence sequence) {
        return cdkChemSequenceToCMLList(sequence, true);
    }

    private CMLList cdkChemSequenceToCMLList(IChemSequence sequence, boolean setIDs) {
        CMLList cmlList = new CMLList();
        cmlList.setDictRef("cdk:sequence");

        if (useCMLIDs && setIDs) {
            IDCreator.createIDs(sequence);
        }

        if (sequence.getChemModelCount() > 0) {
            for (int i = 0; i < sequence.getChemModelCount(); i++) {
                cmlList.appendChild(cdkChemModelToCMLList(sequence.getChemModel(i)));
            }
        }

        return cmlList;
    }

    public CMLList cdkChemModelToCMLList(IChemModel model) {
        return cdkChemModelToCMLList(model, true);
    }

    private CMLList cdkChemModelToCMLList(IChemModel model, boolean setIDs) {
        CMLList cmlList = new CMLList();
        cmlList.setDictRef("cdk:model");

        if (useCMLIDs && setIDs) {
            IDCreator.createIDs(model);
        }

        if (model.getCrystal() != null) {
            cmlList.appendChild(cdkCrystalToCMLMolecule(model.getCrystal(), false));
        }
        if (model.getReactionSet() != null) {
            cmlList.appendChild(cdkReactionSetToCMLReactionList(model.getReactionSet(), false));
        }
        if (model.getMoleculeSet() != null) {
            cmlList.appendChild(cdkMoleculeSetToCMLList(model.getMoleculeSet(), false));
        }

        return cmlList;
    }

    public CMLReactionList cdkReactionSetToCMLReactionList(IReactionSet reactionSet) {
        return cdkReactionSetToCMLReactionList(reactionSet, true);
    }

    private CMLReactionList cdkReactionSetToCMLReactionList(IReactionSet reactionSet, boolean setIDs) {
        CMLReactionList reactionList = new CMLReactionList();

        if (useCMLIDs && setIDs) {
            IDCreator.createIDs(reactionSet);
        }

        java.util.Iterator reactionIter = reactionSet.reactions();
        while (reactionIter.hasNext()) {
            reactionList.appendChild(cdkReactionToCMLReaction((IReaction)reactionIter.next(), false));
        }

        return reactionList;
    }

    public CMLList cdkMoleculeSetToCMLList(IMoleculeSet moleculeSet) {
        return cdkMoleculeSetToCMLList(moleculeSet, true);
    }

    private CMLList cdkMoleculeSetToCMLList(IMoleculeSet moleculeSet, boolean setIDs) {
        CMLList cmlList = new CMLList();
        cmlList.setDictRef("cdk:moleculeSet");

        if (useCMLIDs && setIDs) {
            IDCreator.createIDs(moleculeSet);
        }
        
        for (int i = 0; i < moleculeSet.getAtomContainerCount(); i++) {
            cmlList.appendChild(cdkMoleculeToCMLMolecule(moleculeSet.getMolecule(i), false));
        }
        return cmlList;
    }

    public CMLReaction cdkReactionToCMLReaction(IReaction reaction) {
        return cdkReactionToCMLReaction(reaction, true);
    }

    private CMLReaction cdkReactionToCMLReaction(IReaction reaction, boolean setIDs) {
        CMLReaction cmlReaction = new CMLReaction();

        if (useCMLIDs && setIDs) {
            IDCreator.createIDs(reaction);
        }

        // reactants
        CMLReactantList cmlReactants = new CMLReactantList();
        java.util.Iterator reactants = reaction.getReactants().molecules();
        while (reactants.hasNext()) {
            CMLReactant cmlReactant = new CMLReactant();
            cmlReactant.addMolecule(cdkMoleculeToCMLMolecule((IMolecule)reactants.next(), false));
            cmlReactants.addReactant(cmlReactant);
            
        }

        // products
        CMLProductList cmlProducts = new CMLProductList();
        java.util.Iterator products = reaction.getProducts().molecules();
        while (products.hasNext()) {
            CMLProduct cmlProduct = new CMLProduct();
            cmlProduct.addMolecule(cdkMoleculeToCMLMolecule((IMolecule)products.next(), false));
            cmlProducts.addProduct(cmlProduct);
        }
        
//      substance
        CMLSubstanceList cmlSubstances = new CMLSubstanceList();
        java.util.Iterator substance = reaction.getAgents().molecules();
        while (substance.hasNext()) {
            CMLSubstance cmlSubstance = new CMLSubstance();
            cmlSubstance.addMolecule(cdkMoleculeToCMLMolecule((IMolecule)substance.next(), false));
            cmlSubstances.addSubstance(cmlSubstance);
        }
        
        if (reaction.getID() != null) cmlReaction.setId(reaction.getID());
        cmlReaction.addReactantList(cmlReactants);
        cmlReaction.addProductList(cmlProducts);
        cmlReaction.addSubstanceList(cmlSubstances);
        return cmlReaction;
    }

    public CMLMolecule cdkCrystalToCMLMolecule(ICrystal crystal) {
        return cdkCrystalToCMLMolecule(crystal, true);
    }

    private CMLMolecule cdkCrystalToCMLMolecule(ICrystal crystal, boolean setIDs) {
        CMLMolecule molecule = cdkAtomContainerToCMLMolecule(crystal, false);
        CMLCrystal cmlCrystal = new CMLCrystal();

        if (useCMLIDs && setIDs) {
            IDCreator.createIDs(crystal);
        }

        this.checkPrefix(cmlCrystal);
        cmlCrystal.setZ(crystal.getZ());
        double[] params = CrystalGeometryTools.cartesianToNotional(
                crystal.getA(), crystal.getB(), crystal.getC()
        );
        logger.debug("Number of cell params: ", params.length);
        try {
            cmlCrystal.setCellParameters(params);
        } catch (CMLException exception) {
            logger.error("Could not set crystal cell parameters!");
        }
        molecule.addCrystal(cmlCrystal);
        return molecule;
    }
    
    public CMLMolecule cdkPDBPolymerToCMLMolecule(PDBPolymer pdbPolymer) {
        return cdkPDBPolymerToCMLMolecule(pdbPolymer, true);
    }

    private CMLMolecule cdkPDBPolymerToCMLMolecule(PDBPolymer pdbPolymer, boolean setIDs) {
    	CMLMolecule cmlMolecule = new CMLMolecule();
       	cmlMolecule.setConvention("PDB");
       	cmlMolecule.setDictRef("pdb:model");
       	
       	Map mapS = pdbPolymer.getStrands();
       	Iterator iter = mapS.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            Strand strand = (Strand) mapS.get(key);
            Map mapM = strand.getMonomers();
           	Iterator iterM = mapM.keySet().iterator();
            while (iterM.hasNext()) {
                Monomer monomer = (Monomer) mapM.get(iterM.next());
                CMLMolecule clmono = cdkMonomerToCMLMolecule(monomer, true);
               	cmlMolecule.appendChild(clmono);
            }
        }
       	

        return cmlMolecule;
    }
    
    public CMLMolecule cdkMonomerToCMLMolecule(Monomer monomer) {
        return cdkMonomerToCMLMolecule(monomer, true);
    }

    private CMLMolecule cdkMonomerToCMLMolecule(Monomer monomer, boolean setIDs) {
    	CMLMolecule cmlMolecule = new CMLMolecule();
       	cmlMolecule.setDictRef("pdb:sequence");
       	
       	if (monomer.getMonomerName() != null)cmlMolecule.setId(monomer.getMonomerName());
       	
       	for(int i = 0 ; i < monomer.getAtomCount(); i++){
       		IAtom cdkAtom = monomer.getAtom(i);
            CMLAtom cmlAtom = cdkAtomToCMLAtom(monomer, cdkAtom);
            if (monomer.getConnectedSingleElectronsCount(cdkAtom) > 0) {
                cmlAtom.setSpinMultiplicity(monomer.getConnectedSingleElectronsCount(cdkAtom) + 1);
            }
            cmlMolecule.addAtom(cmlAtom, false);
       	}
        return cmlMolecule;
    }

    public CMLMolecule cdkMoleculeToCMLMolecule(IMolecule structure) {
        return cdkMoleculeToCMLMolecule(structure, true);
    }

    private CMLMolecule cdkMoleculeToCMLMolecule(IMolecule structure, boolean setIDs) {
        return cdkAtomContainerToCMLMolecule(structure, setIDs);
    }

    public CMLMolecule cdkAtomContainerToCMLMolecule(IAtomContainer structure) {
        return cdkAtomContainerToCMLMolecule(structure, true);
    }

    private CMLMolecule cdkAtomContainerToCMLMolecule(IAtomContainer structure, boolean setIDs) {
        CMLMolecule cmlMolecule = new CMLMolecule();

        if (useCMLIDs && setIDs) {
            IDCreator.createIDs(structure);
        }

        this.checkPrefix(cmlMolecule);
        if (structure.getID() != null) cmlMolecule.setId(structure.getID());
        if (structure.getProperty(CDKConstants.TITLE) != null) {
            cmlMolecule.setTitle((String) structure.getProperty(CDKConstants.TITLE));
        }
        if (structure.getProperty(CDKConstants.INCHI) != null) {
        	CMLIdentifier ident = new CMLIdentifier();
        	ident.setConvention("iupac:inchi");
        	ident.setCMLValue(structure.getProperty(CDKConstants.INCHI).toString());
        	cmlMolecule.addIdentifier(ident);
        }
        for (int i = 0; i < structure.getAtomCount(); i++) {
            IAtom cdkAtom = structure.getAtom(i);
            CMLAtom cmlAtom = cdkAtomToCMLAtom(structure, cdkAtom);
            if (structure.getConnectedSingleElectronsCount(cdkAtom) > 0) {
                cmlAtom.setSpinMultiplicity(structure.getConnectedSingleElectronsCount(cdkAtom) + 1);
            }
            cmlMolecule.addAtom(cmlAtom, false);
        }
        for (int i = 0; i < structure.getBondCount(); i++) {
            CMLBond cmlBond = cdkBondToCMLBond(structure.getBond(i));
            cmlMolecule.addBond(cmlBond, true);
        }
        
        // ok, output molecular properties, but not TITLE, INCHI, or DictRef's
        Map props = structure.getProperties();
        Iterator keys = props.keySet().iterator();
        while (keys.hasNext()) {
        	Object key = keys.next();
        	// but only if a String
        	if (key instanceof String && props.get(key) instanceof String) {
        		Object value = props.get(key);
        		if (!key.toString().equals(CDKConstants.TITLE) &&
        			!key.toString().equals(CDKConstants.INCHI)) {
        			// ok, should output this
        			CMLScalar scalar = new CMLScalar();
                    this.checkPrefix(scalar);
                    scalar.setDictRef("cdk:molecularProperty");
                    scalar.setTitle(key.toString());
                    scalar.setValue(value.toString());
                    cmlMolecule.addScalar(scalar);
        		}
        	}
        }

        Iterator elements = customizers.keySet().iterator();
        while (elements.hasNext()) {
            ICMLCustomizer customizer = (ICMLCustomizer)customizers.get(elements.next());
            try {
                customizer.customize(structure, cmlMolecule);
            } catch (Exception exception) {
                logger.error("Error while customizing CML output with customizer: ",
                        customizer.getClass().getName());
                logger.debug(exception);
            }
        }
        return cmlMolecule;
    }

    private boolean addDictRef(IChemObject object, CMLElement cmlElement) {
        Hashtable properties = object.getProperties();
        Iterator iter = properties.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            if (key instanceof String) {
                String keyName = (String) key;
                if (keyName.startsWith(DictionaryDatabase.DICTREFPROPERTYNAME)) {
                    String dictRef = (String) properties.get(keyName);
                    cmlElement.setProperty("dictRef", dictRef);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean addAtomID(IAtom cdkAtom, CMLAtom cmlAtom) {
        if (cdkAtom.getID() != null && !cdkAtom.getID().equals("")) {
            cmlAtom.setId(cdkAtom.getID());
        } else {
            cmlAtom.setId("a" + new Integer(cdkAtom.hashCode()).toString());
        }
        return true;
    }

    public CMLAtom cdkAtomToCMLAtom(IAtomContainer container, IAtom cdkAtom) {
        CMLAtom cmlAtom = new CMLAtom();
        this.checkPrefix(cmlAtom);
        addAtomID(cdkAtom, cmlAtom);
        addDictRef(cdkAtom, cmlAtom);
        cmlAtom.setElementType(cdkAtom.getSymbol());
        if (cdkAtom instanceof IPseudoAtom) {
            String label = ((IPseudoAtom) cdkAtom).getLabel();
            if (label != null) cmlAtom.setTitle(label);
            cmlAtom.setElementType("Du");
        }
        map2DCoordsToCML(cmlAtom, cdkAtom);
        map3DCoordsToCML(cmlAtom, cdkAtom);
        mapFractionalCoordsToCML(cmlAtom, cdkAtom);
        cmlAtom.setFormalCharge(cdkAtom.getFormalCharge());
        // CML's hydrogen count consists of the sum of implicit and explicit
        // hydrogens (see bug #1655045).
        int totalHydrogen = cdkAtom.getHydrogenCount();
        if (container != null) {
        	Iterator bonds = container.getConnectedBondsList(cdkAtom).iterator();
        	while (bonds.hasNext()) {
        		Iterator atoms = ((IBond)bonds.next()).atoms();
        		while (atoms.hasNext()) {
        			if (Elements.HYDROGEN.getSymbol().equals(((IAtom)atoms.next()).getSymbol())) totalHydrogen++;
        		}
        	}
        } // else: it is the implicit hydrogen count
        cmlAtom.setHydrogenCount(totalHydrogen);

        int massNumber = cdkAtom.getMassNumber();
        if (!(cdkAtom instanceof IPseudoAtom)) {
            try {
                IIsotope majorIsotope = IsotopeFactory.getInstance(cdkAtom.getBuilder()).getMajorIsotope(cdkAtom.getSymbol());

                if (majorIsotope != null) {
                    int majorMassNumber = majorIsotope.getMassNumber();
                    if (massNumber != 0 && massNumber != majorMassNumber) {
                        cmlAtom.setIsotope(massNumber);
                    }
                }
            } catch (OptionalDataException e) {
                logger.debug(e);
            } catch (IOException e) {
                logger.debug(e);
            }
        }
        if (cdkAtom.getCharge() != 0.0) {
            CMLScalar scalar = new CMLScalar();
            this.checkPrefix(scalar);
//            scalar.setDataType("xsd:float");
            scalar.setDictRef("cdk:partialCharge");
            scalar.setValue(cdkAtom.getCharge());
            cmlAtom.addScalar(scalar);
        }
        writeProperties(cdkAtom, cmlAtom);

        Iterator elements = customizers.keySet().iterator();
        while (elements.hasNext()) {
            ICMLCustomizer customizer = (ICMLCustomizer)customizers.get(elements.next());
            try {
                customizer.customize(cdkAtom, cmlAtom);
            } catch (Exception exception) {
                logger.error("Error while customizing CML output with customizer: ",
                        customizer.getClass().getName());
                logger.debug(exception);
            }
        }
        return cmlAtom;
    }

    public CMLBond cdkBondToCMLBond(IBond cdkBond) {
        CMLBond cmlBond = new CMLBond();
        this.checkPrefix(cmlBond);
        if (cdkBond.getID() == null || cdkBond.getID().length() == 0) {
            cmlBond.setId("b" + cdkBond.hashCode());
        } else {
            cmlBond.setId(cdkBond.getID());
        }

        String[] atomRefArray = new String[cdkBond.getAtomCount()];
        for (int i = 0; i < cdkBond.getAtomCount(); i++) {
            String atomID = cdkBond.getAtom(i).getID();
            if (atomID == null || atomID.length() == 0) {
                atomRefArray[i] = "a" + new Integer(cdkBond.getAtom(i).hashCode()).toString();
            } else {
                atomRefArray[i] = atomID;
            }
        }
        if (atomRefArray.length == 2) {
            cmlBond.setAtomRefs2(atomRefArray);
        } else {
            cmlBond.setAtomRefs(atomRefArray);
        }

        double border = cdkBond.getOrder();
        if (cdkBond.getFlag(CDKConstants.ISAROMATIC) |
                border == CDKConstants.BONDORDER_AROMATIC) {
            cmlBond.setOrder("A");
        } else if (border == CDKConstants.BONDORDER_SINGLE) {
            cmlBond.setOrder("S");
        } else if (border == CDKConstants.BONDORDER_DOUBLE) {
            cmlBond.setOrder("D");
        } else if (border == CDKConstants.BONDORDER_TRIPLE) {
            cmlBond.setOrder("T");
        } else {
            CMLScalar scalar = new CMLScalar();
            this.checkPrefix(scalar);
//            scalar.setDataType("xsd:float");
            scalar.setDictRef("cdk:bondOrder");
            scalar.setTitle("order");
            scalar.setValue(cdkBond.getOrder());
            cmlBond.appendChild(scalar);
        }

        if (cdkBond.getStereo() == CDKConstants.STEREO_BOND_UP ||
                cdkBond.getStereo() == CDKConstants.STEREO_BOND_DOWN) {
        	CMLBondStereo bondStereo = new CMLBondStereo();
            this.checkPrefix(bondStereo);
            if (cdkBond.getStereo() == CDKConstants.STEREO_BOND_UP) {
                bondStereo.setDictRef("cml:W");
            } else {
                bondStereo.setDictRef("cml:H");
            }
            cmlBond.appendChild(bondStereo);
        }
        if (cdkBond.getProperties().size() > 0) writeProperties(cdkBond, cmlBond);

        return cmlBond;
    }

    private void writeProperties(IChemObject object, CMLElement cmlElement) {
        Hashtable props = object.getProperties();
        Enumeration keys = props.keys();
        CMLElement propList = null;
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if (key instanceof DictRef) {
                Object value = props.get(key);
                CMLScalar scalar = new CMLScalar();
                this.checkPrefix(scalar);
                scalar.setDictRef(((DictRef) key).getType());
                scalar.setValue(value.toString());
                cmlElement.appendChild(scalar);
            } else if (key instanceof String) {
                String stringKey = (String) key;
                if (stringKey.equals(CDKConstants.TITLE)) {
                    // don't output this one. It's covered by addTitle()
                } else if (!(stringKey.startsWith("org.openscience.cdk"))) {
                    Object value = props.get(key);
                    CMLScalar scalar = new CMLScalar();
                    this.checkPrefix(scalar);
                    scalar.setTitle((String) key);
                    scalar.setValue(value.toString());
                    cmlElement.appendChild(scalar);
                }
            }
        }
        if (propList != null) {
            cmlElement.appendChild(propList);
        }
    }

    private void mapFractionalCoordsToCML(CMLAtom cmlAtom, IAtom cdkAtom) {
        if (cdkAtom.getFractionalPoint3d() != null) {
            cmlAtom.setXFract(cdkAtom.getFractionalPoint3d().x);
            cmlAtom.setYFract(cdkAtom.getFractionalPoint3d().y);
            cmlAtom.setZFract(cdkAtom.getFractionalPoint3d().z);
        }
    }

    private void map3DCoordsToCML(CMLAtom cmlAtom, IAtom cdkAtom) {
        if (cdkAtom.getPoint3d() != null) {
            cmlAtom.setX3(cdkAtom.getPoint3d().x);
            cmlAtom.setY3(cdkAtom.getPoint3d().y);
            cmlAtom.setZ3(cdkAtom.getPoint3d().z);
        }
    }

    private void map2DCoordsToCML(CMLAtom cmlAtom, IAtom cdkAtom) {
        if (cdkAtom.getPoint2d() != null) {
            cmlAtom.setX2(cdkAtom.getPoint2d().x);
            cmlAtom.setY2(cdkAtom.getPoint2d().y);
        }
    }

    private void checkPrefix(CMLElement element) {
        if (this.prefix != null) {
            this.prefix.trim();
            if (this.prefix.length() == 0) prefix = null;
        }
        if (this.prefix != null) element.setNamespacePrefix(this.prefix);
    }

}
