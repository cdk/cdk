/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2005-2007  Stefan Kuhn <shk3@users.sf.net>
 *                    2008  Aleksey Tarkhov <bayern7105@yahoo.de>
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
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
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
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IPDBPolymer;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionScheme;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IStrand;
import org.openscience.cdk.tools.IDCreator;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import org.openscience.cdk.tools.manipulator.ReactionSchemeManipulator;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondStereo;
import org.xmlcml.cml.element.CMLBondType;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLIdentifier;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMoleculeList;
import org.xmlcml.cml.element.CMLProduct;
import org.xmlcml.cml.element.CMLProductList;
import org.xmlcml.cml.element.CMLReactant;
import org.xmlcml.cml.element.CMLReactantList;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.CMLReactionList;
import org.xmlcml.cml.element.CMLReactionScheme;
import org.xmlcml.cml.element.CMLReactionStep;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLSubstance;
import org.xmlcml.cml.element.CMLSubstanceList;

/**
 * @cdk.module       libiocml
 * @cdk.svnrev  $Revision$
 * @cdk.keyword      CML
 * @cdk.keyword      class convertor
 * @cdk.builddepends jumbo52.jar
 * @cdk.require      java1.5+
 */
public class Convertor {

    public final static String NS_CML = "http://www.xml-cml.org/schema";

    private LoggingTool logger;

    private final static String CUSTOMIZERS_LIST = "libio-cml-customizers.set";
    private static Map<String, ICMLCustomizer> customizers = null;

    private boolean useCMLIDs;
    /** specify if the IMolecule object need to put identify or reference definition*/
    private boolean isRef = false;
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
    	if (customizers == null) customizers = new HashMap<String, ICMLCustomizer>();
    	
    	if (!customizers.containsKey(customizer.getClass().getName())) {
    		customizers.put(customizer.getClass().getName(), customizer);
    		logger.info("Loaded Customizer: ", customizer.getClass().getName());
    	} else {
    		logger.warn("Duplicate attempt to register a customizer");
    	}
    }
    
    private void setupCustomizers() {
        if (customizers == null) customizers = new HashMap<String, ICMLCustomizer>();
        
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
        cmlList.setConvention("cdk:document");

        if (useCMLIDs && setIDs) {
            IDCreator.createIDs(file);
        }
        if (file.getID() != null && !file.getID().equals(""))
        	cmlList.setId(file.getID());
        
        if (file.getChemSequenceCount() > 0) {
            Iterator<IChemSequence> sequences = file.chemSequences().iterator();
            while (sequences.hasNext()) {
                cmlList.appendChild(cdkChemSequenceToCMLList(sequences.next()));
            }
        }

        return cmlList;
    }

    public CMLList cdkChemSequenceToCMLList(IChemSequence sequence) {
        return cdkChemSequenceToCMLList(sequence, true);
    }

    private CMLList cdkChemSequenceToCMLList(IChemSequence sequence, boolean setIDs) {
        CMLList cmlList = new CMLList();
        cmlList.setConvention("cdk:sequence");

        if (useCMLIDs && setIDs) {
            IDCreator.createIDs(sequence);
        }
        if (sequence.getID() != null && !sequence.getID().equals(""))
        	cmlList.setId(sequence.getID());
        	
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
        cmlList.setConvention("cdk:model");

        if (useCMLIDs && setIDs) {
            IDCreator.createIDs(model);
        }
        if (model.getID() != null && !model.getID().equals(""))
        	cmlList.setId(model.getID());
        	
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
    
    public CMLCml cdkReactionSchemeToCMLReactionSchemeAndMoleculeList(IReactionScheme cdkScheme){
    	CMLCml cml = new CMLCml();
    	cml.appendChild(cdkMoleculeSetToCMLList(ReactionSchemeManipulator.getAllMolecules(cdkScheme)));
    	isRef = true;
    	cml.appendChild(cdkReactionSchemeToCMLReactionScheme(cdkScheme, true));
    	isRef = false;
    	return cml;
    }
    
	public CMLReactionScheme cdkReactionSchemeToCMLReactionScheme(IReactionScheme cdkScheme){
    	return cdkReactionSchemeToCMLReactionScheme(cdkScheme, true);
    }
    
    private CMLReactionScheme cdkReactionSchemeToCMLReactionScheme(IReactionScheme cdkScheme, boolean setIDs){
    	CMLReactionScheme reactionScheme = new CMLReactionScheme();
    	
    	if (useCMLIDs && setIDs) {
            IDCreator.createIDs(cdkScheme);
        }
        if (cdkScheme.getID() != null && !cdkScheme.getID().equals(""))
        	reactionScheme.setId(cdkScheme.getID());
        	
    	for(Iterator<IReaction> it = cdkScheme.reactions().iterator(); it.hasNext();){
    		reactionScheme.appendChild(cdkReactionToCMLReaction(it.next(), true));
    	}
    	for(IReactionScheme intScheme : cdkScheme.reactionSchemes()){
        		reactionScheme.appendChild(cdkReactionSchemeToCMLReactionScheme(intScheme));
    	}
        	
    	return reactionScheme;
    } 
    
    public CMLReactionStep cdkReactionToCMLReactionStep(IReaction reaction){
    	return cdkReactionToCMLReactionStep(reaction, true);
    }
    
    private CMLReactionStep cdkReactionToCMLReactionStep(IReaction reaction, boolean setIDs){
    	CMLReactionStep reactionStep = new CMLReactionStep();
    	
    	reactionStep.appendChild(cdkReactionToCMLReaction(reaction, true));
    	
    	return reactionStep;
    }
    
    public CMLReactionList cdkReactionSetToCMLReactionList(IReactionSet reactionSet) {
        return cdkReactionSetToCMLReactionList(reactionSet, true);
    }

    private CMLReactionList cdkReactionSetToCMLReactionList(IReactionSet reactionSet, boolean setIDs) {
        CMLReactionList reactionList = new CMLReactionList();

        if (useCMLIDs && setIDs) {
            IDCreator.createIDs(reactionSet);
        }
        if (reactionSet.getID() != null && !reactionSet.getID().equals(""))
        	reactionList.setId(reactionSet.getID());
        	
        Iterator<IReaction> reactionIter = reactionSet.reactions().iterator();
        while (reactionIter.hasNext()) {
            reactionList.appendChild(cdkReactionToCMLReaction(reactionIter.next(), false));
        }

        return reactionList;
    }

    public CMLMoleculeList cdkMoleculeSetToCMLList(IMoleculeSet moleculeSet) {
        return cdkMoleculeSetToCMLList(moleculeSet, true);
    }

    private CMLMoleculeList cdkMoleculeSetToCMLList(IMoleculeSet moleculeSet, boolean setIDs) {
    	CMLMoleculeList cmlList = new CMLMoleculeList();
        cmlList.setConvention("cdk:moleculeSet");

        if (useCMLIDs && setIDs) {
            IDCreator.createIDs(moleculeSet);
        }
        if (moleculeSet.getID() != null && !moleculeSet.getID().equals(""))
        	cmlList.setId(moleculeSet.getID());
        	
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
        if (reaction.getID() != null && !reaction.getID().equals(""))
        	cmlReaction.setId(reaction.getID());
        
        // reactants
        CMLReactantList cmlReactants = new CMLReactantList();
        Iterator<IAtomContainer> reactants = reaction.getReactants().molecules().iterator();
        while (reactants.hasNext()) {
            CMLReactant cmlReactant = new CMLReactant();
            cmlReactant.addMolecule(cdkMoleculeToCMLMolecule((IMolecule)reactants.next(), false));
            cmlReactants.addReactant(cmlReactant);
            
        }

        // products
        CMLProductList cmlProducts = new CMLProductList();
        Iterator<IAtomContainer> products = reaction.getProducts().molecules().iterator();
        while (products.hasNext()) {
            CMLProduct cmlProduct = new CMLProduct();
            cmlProduct.addMolecule(cdkMoleculeToCMLMolecule((IMolecule)products.next(), false));
            cmlProducts.addProduct(cmlProduct);
        }
        
//      substance
        CMLSubstanceList cmlSubstances = new CMLSubstanceList();
        Iterator<IAtomContainer> substance = reaction.getAgents().molecules().iterator();
        while (substance.hasNext()) {
            CMLSubstance cmlSubstance = new CMLSubstance();
            cmlSubstance.addMolecule(cdkMoleculeToCMLMolecule((IMolecule)substance.next(), false));
            cmlSubstances.addSubstance(cmlSubstance);
        }
        if (reaction.getID() != null) 
        	cmlReaction.setId(reaction.getID());
        
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
        if (crystal.getID() != null && !crystal.getID().equals(""))
        	cmlCrystal.setId(crystal.getID());
        
        this.checkPrefix(cmlCrystal);
        cmlCrystal.setZ(crystal.getZ());
        double[] params = CrystalGeometryTools.cartesianToNotional(
                crystal.getA(), crystal.getB(), crystal.getC()
        );
        logger.debug("Number of cell params: ", params.length);
        cmlCrystal.setCellParameters(params);
        molecule.appendChild(cmlCrystal);
        return molecule;
    }
    
    public CMLMolecule cdkPDBPolymerToCMLMolecule(IPDBPolymer pdbPolymer) {
        return cdkPDBPolymerToCMLMolecule(pdbPolymer, true);
    }

    private CMLMolecule cdkPDBPolymerToCMLMolecule(IPDBPolymer pdbPolymer, boolean setIDs) {
    	CMLMolecule cmlMolecule = new CMLMolecule();
       	cmlMolecule.setConvention("PDB");
       	cmlMolecule.setDictRef("pdb:model");
       	
       	Map<String, IStrand> mapS = pdbPolymer.getStrands();
       	Iterator<String> iter = mapS.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            IStrand strand = mapS.get(key);
            Map<String, IMonomer> mapM = strand.getMonomers();
           	Iterator<String> iterM = mapM.keySet().iterator();
            while (iterM.hasNext()) {
                IMonomer monomer = mapM.get(iterM.next());
                CMLMolecule clmono = cdkMonomerToCMLMolecule(monomer, true);
               	cmlMolecule.appendChild(clmono);
            }
        }
       	

        return cmlMolecule;
    }
    
    public CMLMolecule cdkMonomerToCMLMolecule(IMonomer monomer) {
        return cdkMonomerToCMLMolecule(monomer, true);
    }

    private CMLMolecule cdkMonomerToCMLMolecule(IMonomer monomer, boolean setIDs) {
    	CMLMolecule cmlMolecule = new CMLMolecule();
       	cmlMolecule.setDictRef("pdb:sequence");
       	
       	if (monomer.getMonomerName() != null && !monomer.getMonomerName().equals(""))
        	cmlMolecule.setId(monomer.getMonomerName());
        	
       	for(int i = 0 ; i < monomer.getAtomCount(); i++){
       		IAtom cdkAtom = monomer.getAtom(i);
            CMLAtom cmlAtom = cdkAtomToCMLAtom(monomer, cdkAtom);
            if (monomer.getConnectedSingleElectronsCount(cdkAtom) > 0) {
                cmlAtom.setSpinMultiplicity(monomer.getConnectedSingleElectronsCount(cdkAtom) + 1);
            }
            cmlMolecule.addAtom(cmlAtom);
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
        if (structure.getID() != null && !structure.getID().equals(""))
        	if(!isRef) cmlMolecule.setId(structure.getID());
        	else cmlMolecule.setRef(structure.getID());
        
        if (structure.getProperty(CDKConstants.TITLE) != null) {
            cmlMolecule.setTitle((String) structure.getProperty(CDKConstants.TITLE));
        }
        if (structure.getProperty(CDKConstants.INCHI) != null) {
        	CMLIdentifier ident = new CMLIdentifier();
        	ident.setConvention("iupac:inchi");
        	ident.setCMLValue(structure.getProperty(CDKConstants.INCHI).toString());
          cmlMolecule.appendChild(ident);
        }
        for (int i = 0; i < structure.getAtomCount(); i++) {
            IAtom cdkAtom = structure.getAtom(i);
            CMLAtom cmlAtom = cdkAtomToCMLAtom(structure, cdkAtom);
            if (structure.getConnectedSingleElectronsCount(cdkAtom) > 0) {
                cmlAtom.setSpinMultiplicity(structure.getConnectedSingleElectronsCount(cdkAtom) + 1);
            }
            cmlMolecule.addAtom(cmlAtom);
        }
        for (int i = 0; i < structure.getBondCount(); i++) {
            CMLBond cmlBond = cdkBondToCMLBond(structure.getBond(i));
            cmlMolecule.addBond(cmlBond);
        }
        
        // ok, output molecular properties, but not TITLE, INCHI, or DictRef's
        Map<Object, Object> props = structure.getProperties();
        Iterator<Object> keys = props.keySet().iterator();
        while (keys.hasNext()) {
        	Object key = keys.next();
        		// but only if a String
             	if (key instanceof String &&  !isRef &&
             			props.get(key) instanceof String) {
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
             	// FIXME: At the moment the order writing the formula is into properties
             	// but it should be that IMolecularFormula is a extension of IAtomContainer
        		if (key instanceof String && !isRef &&
        				key.toString().equals(CDKConstants.FORMULA)){
        			if(props.get(key) instanceof IMolecularFormula){
	        			IMolecularFormula cdkFormula = (IMolecularFormula)props.get(key);
	        			
	        			CMLFormula cmlFormula = new CMLFormula();
	        			List<IIsotope> isotopesList = MolecularFormulaManipulator.putInOrder(MolecularFormulaManipulator.generateOrderEle(),cdkFormula);
	        			for(int i = 0; i< isotopesList.size(); i++){
	        				cmlFormula.add(isotopesList.get(i).getSymbol(), cdkFormula.getIsotopeCount(isotopesList.get(i)));
	        			}
	        			cmlMolecule.appendChild(cmlFormula);
        			}else if (props.get(key) instanceof IMolecularFormulaSet){
        				IMolecularFormulaSet cdkFormulaSet = (IMolecularFormulaSet)props.get(key);
	        			for(Iterator<IMolecularFormula> it = cdkFormulaSet.molecularFormulas().iterator(); it.hasNext();){
	        				IMolecularFormula cdkFormula = it.next(); 
	        				List<IIsotope> isotopesList = MolecularFormulaManipulator.putInOrder(MolecularFormulaManipulator.generateOrderEle(),cdkFormula);
	        				CMLFormula cmlFormula = new CMLFormula();
	        				cmlFormula.setDictRef("cdk:possibleMachts");
	        				for(int i = 0; i< isotopesList.size(); i++){
		        				cmlFormula.add(isotopesList.get(i).getSymbol(), cdkFormula.getIsotopeCount(isotopesList.get(i)));
		        			}
	                         cmlMolecule.appendChild(cmlFormula);
	        			}
        			}
        		}
        }

        Iterator<String> elements = customizers.keySet().iterator();
        while (elements.hasNext()) {
            ICMLCustomizer customizer = customizers.get(elements.next());
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
    	Map<Object,Object> properties = object.getProperties();
        Iterator<Object> iter = properties.keySet().iterator();
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
            cmlAtom.setId("a" + Integer.valueOf(cdkAtom.hashCode()).toString());
        }
        return true;
    }

    public CMLAtom cdkAtomToCMLAtom(IAtom cdkAtom) {
        return cdkAtomToCMLAtom(null, cdkAtom);
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

        Integer formalCharge = cdkAtom.getFormalCharge();
        if (formalCharge != null) cmlAtom.setFormalCharge(formalCharge);
        
        // CML's hydrogen count consists of the sum of implicit and explicit
        // hydrogens (see bug #1655045).
        Integer totalHydrogen = cdkAtom.getHydrogenCount();
        if (totalHydrogen != null) {
        	if (container != null) {
        		Iterator<IBond> bonds = container.getConnectedBondsList(cdkAtom).iterator();
        		while (bonds.hasNext()) {
        			Iterator<IAtom> atoms = (bonds.next()).atoms().iterator();
        			while (atoms.hasNext()) {
        				IAtom atom= atoms.next();
        				if ("H".equals(atom.getSymbol()) && atom!=cdkAtom) totalHydrogen++;
        			}
        		}
        	} // else: it is the implicit hydrogen count
        	cmlAtom.setHydrogenCount(totalHydrogen);
        } // else: don't report it, people can count the explicit Hs themselves

        Integer massNumber = cdkAtom.getMassNumber();
        if (!(cdkAtom instanceof IPseudoAtom)) {
            if (massNumber != null) {
                cmlAtom.setIsotopeNumber(massNumber);
            }
        }

        if (cdkAtom.getCharge() != CDKConstants.UNSET) {
            CMLScalar scalar = new CMLScalar();
            this.checkPrefix(scalar);
//            scalar.setDataType("xsd:float");
            scalar.setDictRef("cdk:partialCharge");
            scalar.setValue(cdkAtom.getCharge());
            cmlAtom.addScalar(scalar);
        }
        writeProperties(cdkAtom, cmlAtom);

        Iterator<String> elements = customizers.keySet().iterator();
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
                atomRefArray[i] = "a" + Integer.valueOf(cdkBond.getAtom(i).hashCode()).toString();
            } else {
                atomRefArray[i] = atomID;
            }
        }
        if (atomRefArray.length == 2) {
            cmlBond.setAtomRefs2(atomRefArray);
        } else {
            cmlBond.setAtomRefs(atomRefArray);
        }

        IBond.Order border = cdkBond.getOrder();
        if (border == CDKConstants.BONDORDER_SINGLE) {
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
            scalar.setValue(cdkBond.getOrder().ordinal()+1);
            cmlBond.appendChild(scalar);
        }
        if (cdkBond.getFlag(CDKConstants.ISAROMATIC)) {
        	CMLBondType bType = new CMLBondType();
        	bType.setDictRef("cdk:aromaticBond");
          cmlBond.appendChild(bType);
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

        Iterator<String> elements = customizers.keySet().iterator();
        while (elements.hasNext()) {
        	ICMLCustomizer customizer = customizers.get(elements.next());
        	try {
        		customizer.customize(cdkBond, cmlBond);
        	} catch (Exception exception) {
        		logger.error("Error while customizing CML output with customizer: ",
        				customizer.getClass().getName());
        		logger.debug(exception);
        	}
        }
        
        return cmlBond;
    }

    private void writeProperties(IChemObject object, CMLElement cmlElement) {
    	Map<Object,Object> props = object.getProperties();
        Iterator<Object> keys = props.keySet().iterator();
        CMLElement propList = null;
        while (keys.hasNext()) {
            Object key = keys.next();
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
