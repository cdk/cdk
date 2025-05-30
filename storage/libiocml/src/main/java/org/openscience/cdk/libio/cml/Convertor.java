/* Copyright (C) 2005-2007  Stefan Kuhn <shk3@users.sf.net>
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.dict.DictRef;
import org.openscience.cdk.dict.DictionaryDatabase;
import org.openscience.cdk.geometry.CrystalGeometryTools;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.tools.IDCreator;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
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
 * @cdk.keyword      CML
 * @cdk.keyword      class convertor
 * @cdk.require      java1.5+
 */
public class Convertor {

    public final static String          NS_CML           = "http://www.xml-cml.org/schema";

    private static final ILoggingTool         logger           = LoggingToolFactory.createLoggingTool(Convertor.class);

    private final static String         CUSTOMIZERS_LIST = "libio-cml-customizers.set";
    private Map<String, ICMLCustomizer> customizers      = null;

    private final boolean                     useCMLIDs;
    private String                      prefix;

    /**
     * Constructs a CML convertor.
     *
     * @param useCMLIDs Uses object IDs like 'a1' instead of 'a&lt;hash&gt;'.
     * @param prefix    Namespace prefix to use. If null, then no prefix is used;
     */
    public Convertor(boolean useCMLIDs, String prefix) {
        this.useCMLIDs = useCMLIDs;
        this.prefix = prefix;
        setupCustomizers();
    }

    public void registerCustomizer(ICMLCustomizer customizer) {
        if (customizers == null) customizers = new HashMap<>();

        if (!customizers.containsKey(customizer.getClass().getName())) {
            customizers.put(customizer.getClass().getName(), customizer);
            logger.info("Registered Customizer: ", customizer.getClass().getName());
        } else {
            logger.warn("Duplicate attempt to register a customizer");
        }
    }

    private void setupCustomizers() {
        if (customizers == null) customizers = new HashMap<>();

        try (InputStream in = this.getClass().getResourceAsStream(CUSTOMIZERS_LIST);
             InputStreamReader rdr = new InputStreamReader(Objects.requireNonNull(in));
             BufferedReader brdr = new BufferedReader(rdr)) {
            logger.debug("Starting loading Customizers...");

            int numLoaded = 0;
            String customizerName;
            while ((customizerName = brdr.readLine()) != null) {
                // load them one by one
                if (!customizers.containsKey(customizerName)) {
                    if (loadCustomizer(customizerName))
                        numLoaded++;
                } else {
                    logger.warn("Duplicate attempt to load a customizer");
                }
            }
            logger.info("Number of loaded customizers: ", numLoaded);
        } catch (Exception exception) {
            logger.error("Could not load this list: ", CUSTOMIZERS_LIST);
            logger.debug(exception);
        }
    }

    private boolean loadCustomizer(String customizerName) {
        try {
            ICMLCustomizer customizer = (ICMLCustomizer) Class.forName(customizerName)
                                                              .getDeclaredConstructor()
                                                              .newInstance();
            customizers.put(customizer.getClass().getName(), customizer);
            logger.info("Loaded Customizer: ", customizer.getClass().getName());
            return true;
        } catch (ClassNotFoundException exception) {
            logger.info("Could not find this Customizer: ", customizerName);
            logger.debug(exception);
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException exception) {
            logger.warn("Could not load this Customizer: ", customizerName);
            logger.warn(exception.getMessage());
            logger.debug(exception);
        }
        return false;
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
        if (file.getID() != null && !file.getID().equals("")) cmlList.setId(file.getID());

        if (file.getChemSequenceCount() > 0) {
            for (IChemSequence iChemSequence : file.chemSequences()) {
                cmlList.appendChild(cdkChemSequenceToCMLList(iChemSequence));
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
        if (sequence.getID() != null && !sequence.getID().equals("")) cmlList.setId(sequence.getID());

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
        if (model.getID() != null && !model.getID().equals("")) cmlList.setId(model.getID());

        if (model.getCrystal() != null) {
            cmlList.appendChild(cdkCrystalToCMLMolecule(model.getCrystal(), false));
        }
        if (model.getReactionSet() != null) {
            cmlList.appendChild(cdkReactionSetToCMLReactionList(model.getReactionSet(), false));
        }
        if (model.getMoleculeSet() != null) {
            cmlList.appendChild(cdkAtomContainerSetToCMLList(model.getMoleculeSet(), false));
        }

        return cmlList;
    }

    public CMLCml cdkReactionSchemeToCMLReactionSchemeAndMoleculeList(IReactionScheme cdkScheme) {
        CMLCml cml = new CMLCml();
        cml.appendChild(cdkAtomContainerSetToCMLList(ReactionSchemeManipulator.getAllAtomContainers(cdkScheme)));
        cml.appendChild(cdkReactionSchemeToCMLReactionScheme(cdkScheme, true));
        return cml;
    }

    public CMLReactionScheme cdkReactionSchemeToCMLReactionScheme(IReactionScheme cdkScheme) {
        return cdkReactionSchemeToCMLReactionScheme(cdkScheme, true);
    }

    private CMLReactionScheme cdkReactionSchemeToCMLReactionScheme(IReactionScheme cdkScheme, boolean setIDs) {

        CMLReactionScheme reactionScheme = new CMLReactionScheme();

        if (useCMLIDs && setIDs) {
            IDCreator.createIDs(cdkScheme);
        }
        if (cdkScheme.getID() != null && !cdkScheme.getID().equals("")) reactionScheme.setId(cdkScheme.getID());

        for (IReaction iReaction : cdkScheme.reactions()) {
            reactionScheme.appendChild(cdkReactionToCMLReaction(iReaction, true));
        }
        for (IReactionScheme intScheme : cdkScheme.reactionSchemes()) {
            reactionScheme.appendChild(cdkReactionSchemeToCMLReactionScheme(intScheme));
        }

        return reactionScheme;
    }

    public CMLReactionStep cdkReactionToCMLReactionStep(IReaction reaction) {
        return cdkReactionToCMLReactionStep(reaction, true);
    }

    private CMLReactionStep cdkReactionToCMLReactionStep(IReaction reaction, boolean setIDs) {
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
        if (reactionSet.getID() != null && !reactionSet.getID().equals("")) reactionList.setId(reactionSet.getID());

        for (IReaction iReaction : reactionSet.reactions()) {
            reactionList.appendChild(cdkReactionToCMLReaction(iReaction, false));
        }

        return reactionList;
    }

    public CMLMoleculeList cdkAtomContainerSetToCMLList(IAtomContainerSet moleculeSet) {
        return cdkAtomContainerSetToCMLList(moleculeSet, true);
    }

    private CMLMoleculeList cdkAtomContainerSetToCMLList(IAtomContainerSet moleculeSet, boolean setIDs) {
        CMLMoleculeList cmlList = new CMLMoleculeList();
        cmlList.setConvention("cdk:moleculeSet");

        if (useCMLIDs && setIDs) {
            IDCreator.createIDs(moleculeSet);
        }
        if (moleculeSet.getID() != null && !moleculeSet.getID().equals("")) cmlList.setId(moleculeSet.getID());

        for (int i = 0; i < moleculeSet.getAtomContainerCount(); i++) {
            IAtomContainer container = moleculeSet.getAtomContainer(i);
            cmlList.appendChild(cdkAtomContainerToCMLMolecule(container, false, false));
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
        if (reaction.getID() != null && !reaction.getID().equals("")) cmlReaction.setId(reaction.getID());

        Map<Object, Object> props = reaction.getProperties();
        for (Object key : props.keySet()) {
            if (key instanceof String && props.get(key) instanceof String) {
                Object value = props.get(key);
                if (!key.toString().equals(CDKConstants.TITLE)) {
                    CMLScalar scalar = new CMLScalar();
                    this.checkPrefix(scalar);
                    scalar.setDictRef("cdk:reactionProperty");
                    scalar.setTitle(key.toString());
                    scalar.setValue(value.toString());
                    cmlReaction.appendChild(scalar);
                }
            }
        }

        // reactants
        CMLReactantList cmlReactants = new CMLReactantList();
        for (IAtomContainer iAtomContainer : reaction.getReactants().atomContainers()) {
            CMLReactant cmlReactant = new CMLReactant();
            cmlReactant.addMolecule(cdkAtomContainerToCMLMolecule(iAtomContainer));
            cmlReactants.addReactant(cmlReactant);

        }

        // products
        CMLProductList cmlProducts = new CMLProductList();
        for (IAtomContainer atomContainer : reaction.getProducts().atomContainers()) {
            CMLProduct cmlProduct = new CMLProduct();
            cmlProduct.addMolecule(cdkAtomContainerToCMLMolecule(atomContainer));
            cmlProducts.addProduct(cmlProduct);
        }

        //      substance
        CMLSubstanceList cmlSubstances = new CMLSubstanceList();
        for (IAtomContainer container : reaction.getAgents().atomContainers()) {
            CMLSubstance cmlSubstance = new CMLSubstance();
            cmlSubstance.addMolecule(cdkAtomContainerToCMLMolecule(container));
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
        CMLMolecule molecule = cdkAtomContainerToCMLMolecule(crystal, false, false);
        CMLCrystal cmlCrystal = new CMLCrystal();

        if (useCMLIDs && setIDs) {
            IDCreator.createIDs(crystal);
        }
        if (crystal.getID() != null && !crystal.getID().equals("")) cmlCrystal.setId(crystal.getID());

        this.checkPrefix(cmlCrystal);
        cmlCrystal.setZ(crystal.getZ());
        double[] params = CrystalGeometryTools.cartesianToNotional(crystal.getA(), crystal.getB(), crystal.getC());
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
        for (Object key : mapS.keySet()) {
            IStrand strand = mapS.get(key);
            List<String> monomerNames = new ArrayList<>(strand.getMonomerNames());
            Collections.sort(monomerNames);
            for (String name : monomerNames) {
                IMonomer monomer = strand.getMonomer(name);
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

        for (int i = 0; i < monomer.getAtomCount(); i++) {
            IAtom cdkAtom = monomer.getAtom(i);
            CMLAtom cmlAtom = cdkAtomToCMLAtom(monomer, cdkAtom);
            if (monomer.getConnectedSingleElectronsCount(cdkAtom) > 0) {
                cmlAtom.setSpinMultiplicity(monomer.getConnectedSingleElectronsCount(cdkAtom) + 1);
            }
            cmlMolecule.addAtom(cmlAtom);
        }
        return cmlMolecule;
    }

    public CMLMolecule cdkAtomContainerToCMLMolecule(IAtomContainer structure) {
        return cdkAtomContainerToCMLMolecule(structure, true, false);
    }

    private CMLMolecule cdkAtomContainerToCMLMolecule(IAtomContainer structure, boolean setIDs, boolean isRef) {
        CMLMolecule cmlMolecule = new CMLMolecule();

        if (useCMLIDs && setIDs) {
            IDCreator.createIDs(structure);
        }

        this.checkPrefix(cmlMolecule);
        if (structure.getID() != null && !structure.getID().equals("")) if (!isRef)
            cmlMolecule.setId(structure.getID());
        else
            cmlMolecule.setRef(structure.getID());

        if (structure.getTitle() != null) {
            cmlMolecule.setTitle(structure.getTitle());
        }
        if (structure.getProperty(CDKConstants.INCHI) != null) {
            CMLIdentifier ident = new CMLIdentifier();
            ident.setConvention("iupac:inchi");
            ident.setCMLValue(structure.getProperty(CDKConstants.INCHI).toString());
            cmlMolecule.appendChild(ident);
        }
        if (!isRef) {
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
        }

        // ok, output molecular properties, but not TITLE, INCHI, or DictRef's
        Map<Object, Object> props = structure.getProperties();
        for (Object key : props.keySet()) {
            // but only if a String
            if (key instanceof String && !isRef && props.get(key) instanceof String) {
                Object value = props.get(key);
                if (!key.toString().equals(CDKConstants.TITLE) && !key.toString().equals(CDKConstants.INCHI)) {
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
            if (key instanceof String && !isRef && key.toString().equals(CDKConstants.FORMULA)) {
                if (props.get(key) instanceof IMolecularFormula) {
                    IMolecularFormula cdkFormula = (IMolecularFormula) props.get(key);

                    CMLFormula cmlFormula = new CMLFormula();
                    List<IIsotope> isotopesList = MolecularFormulaManipulator.putInOrder(
                            MolecularFormulaManipulator.generateOrderEle(), cdkFormula);
                    for (IIsotope iIsotope : isotopesList) {
                        cmlFormula
                                .add(iIsotope.getSymbol(), cdkFormula.getIsotopeCount(iIsotope));
                    }
                    cmlMolecule.appendChild(cmlFormula);
                } else if (props.get(key) instanceof IMolecularFormulaSet) {
                    IMolecularFormulaSet cdkFormulaSet = (IMolecularFormulaSet) props.get(key);
                    for (IMolecularFormula cdkFormula : cdkFormulaSet.molecularFormulas()) {
                        List<IIsotope> isotopesList = MolecularFormulaManipulator.putInOrder(
                                MolecularFormulaManipulator.generateOrderEle(), cdkFormula);
                        CMLFormula cmlFormula = new CMLFormula();
                        cmlFormula.setDictRef("cdk:possibleMachts");
                        for (IIsotope iIsotope : isotopesList) {
                            cmlFormula.add(iIsotope.getSymbol(),
                                    cdkFormula.getIsotopeCount(iIsotope));
                        }
                        cmlMolecule.appendChild(cmlFormula);
                    }
                }
            }
        }

        for (String s : customizers.keySet()) {
            ICMLCustomizer customizer = customizers.get(s);
            try {
                customizer.customize(structure, cmlMolecule);
            } catch (Exception exception) {
                logger.error("Error while customizing CML output with customizer: ", customizer.getClass().getName());
                logger.debug(exception);
            }
        }
        return cmlMolecule;
    }

    private boolean addDictRef(IChemObject object, CMLElement cmlElement) {
        Map<Object, Object> properties = object.getProperties();
        for (Object key : properties.keySet()) {
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
        Integer totalHydrogen = cdkAtom.getImplicitHydrogenCount();
        if (totalHydrogen != null) {
            if (container != null) {
                for (IBond iBond : container.getConnectedBondsList(cdkAtom)) {
                    for (IAtom atom : iBond.atoms()) {
                        if (atom.getAtomicNumber() == IElement.H && !Objects.equals(atom, cdkAtom)) totalHydrogen++;
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

        if (cdkAtom.getFlag(IChemObject.AROMATIC)) {
            CMLScalar aromAtom = new CMLScalar();
            aromAtom.setDictRef("cdk:aromaticAtom");
            cmlAtom.appendChild(aromAtom);
        }

        for (String s : customizers.keySet()) {
            ICMLCustomizer customizer = customizers.get(s);
            try {
                customizer.customize(cdkAtom, cmlAtom);
            } catch (Exception exception) {
                logger.error("Error while customizing CML output with customizer: ", customizer.getClass().getName());
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
        if (border == Order.SINGLE) {
            cmlBond.setOrder("S");
        } else if (border == Order.DOUBLE) {
            cmlBond.setOrder("D");
        } else if (border == Order.TRIPLE) {
            cmlBond.setOrder("T");
        } else {
            CMLScalar scalar = new CMLScalar();
            this.checkPrefix(scalar);
            //            scalar.setDataType("xsd:float");
            scalar.setDictRef("cdk:bondOrder");
            scalar.setTitle("order");
            scalar.setValue(cdkBond.getOrder().numeric());
            cmlBond.appendChild(scalar);
        }
        if (cdkBond.getFlag(IChemObject.AROMATIC)) {
            CMLBondType bType = new CMLBondType();
            bType.setDictRef("cdk:aromaticBond");
            cmlBond.appendChild(bType);
        }

        if (cdkBond.getStereo() == IBond.Stereo.UP || cdkBond.getStereo() == IBond.Stereo.DOWN) {
            CMLBondStereo bondStereo = new CMLBondStereo();
            this.checkPrefix(bondStereo);
            if (cdkBond.getStereo() == IBond.Stereo.UP) {
                bondStereo.setDictRef("cml:W");
                bondStereo.setXMLContent("W");
            } else {
                bondStereo.setDictRef("cml:H");
                bondStereo.setXMLContent("H");
            }
            cmlBond.appendChild(bondStereo);
        }
        if (cdkBond.getProperties().size() > 0) writeProperties(cdkBond, cmlBond);

        for (String s : customizers.keySet()) {
            ICMLCustomizer customizer = customizers.get(s);
            try {
                customizer.customize(cdkBond, cmlBond);
            } catch (Exception exception) {
                logger.error("Error while customizing CML output with customizer: ", customizer.getClass().getName());
                logger.debug(exception);
            }
        }

        return cmlBond;
    }

    private void writeProperties(IChemObject object, CMLElement cmlElement) {
        Map<Object, Object> props = object.getProperties();
        for (Object key : props.keySet()) {
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
            this.prefix = this.prefix.trim();
            if (this.prefix.length() == 0) prefix = null;
        }
        if (this.prefix != null) element.setNamespacePrefix(this.prefix);
    }

}
