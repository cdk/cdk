/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.libio.cml;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xmlcml.cmlimpl.*;
import org.xmlcml.cml.*;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Isotope;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.SetOfReactions;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.dict.*;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.CrystalGeometryTools;
import org.openscience.cdk.io.setting.StringIOSetting;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.*;
import org.openscience.cdk.tools.*;

/**
 * Class that provides convertor procedures to
 * convert CDK classes to CMLDOM 4.6 classes/documents.
 *
 * <p>
 * <table>
 *   <tr>
 *     <td><b>CDK class</b></td>
 *     <td><b>CMLDOM class</b></td>
 *   </tr>
 *   <tr>
 *     <td><b>AtomContainer</b></td>
 *     <td><b>MoleculeImpl</b></td>
 *   </tr>
 *   <tr>
 *     <td><b>Crystal</b></td>
 *     <td><b>MoleculeImpl</b></td>
 *   </tr>
 *   <tr>
 *     <td><b>Molecule</b></td>
 *     <td><b>MoleculeImpl</b></td>
 *   </tr>
 *   <tr>
 *     <td><b>Reaction</b></td>
 *     <td><b>ReactionImpl</b></td>
 *   </tr>
 *   <tr>
 *     <td><b>SetOfMolecule</b></td>
 *     <td><b>ListImpl</b></td>
 *   </tr>
 * </table>
 * 
 * @author        shk3
 * @author        egonw
 * @cdk.created   2004-02-19
 * @cdk.module    libio-cml
 * @cdk.keyword   CML
 * @cdk.keyword   class convertor
 * @cdk.bug       905062
 * @cdk.require   java1.4
 *
 * @cdk.builddepends base.jar
 * @cdk.builddepends pmrlib.jar
 * @cdk.builddepends cmlAll.jar
 */
public class Convertor {

    public final static int COORDINATES_3D = 3;
    public final static int COORDINATES_2D = 2;
    private static LoggingTool logger;
    private CMLDocument doc;
    private IsotopeFactory isotopeFactory;
    private boolean useCmlIdentifiers;
    private boolean setNamespaceUri;
    private boolean schemaInstanceOutput;
    private String prefix;
    private String instanceLocation;
    private String namespace = "http://www.xml-cml.org/schema/cml2/core";
    
    private final static String CUSTOMIZERS_LIST = "libio-cml-customizers.set";
    private static Vector customizers = null;

    public Convertor() {
        this(true, false, false, "", "");
    }

    public Convertor (boolean useCmlIdentifiers, boolean setNamespaceUri, boolean schemaInstanceOutput, String instanceLocation, String prefix) {
        logger = new LoggingTool(this);
        try {
            isotopeFactory = IsotopeFactory.getInstance();
        } catch (Exception exception) {
            logger.error("Failed to initiate isotope factory: ", exception.getMessage());
            logger.debug(exception);
        }
        this.useCmlIdentifiers = useCmlIdentifiers;
        this.setNamespaceUri = setNamespaceUri;
        this.schemaInstanceOutput = schemaInstanceOutput;
        this.instanceLocation = instanceLocation;
        this.prefix = prefix;
        
        setupCustomizers();
    }
    
    private void setupCustomizers() {
         if (customizers == null) {
            customizers = new Vector();
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
                    try {
                        Customizer customizer = (Customizer)this.getClass().getClassLoader().
                            loadClass(customizerName).newInstance();
                        customizers.addElement(customizer);
                        logger.info("Loaded Customizer: " + customizer.getClass().getName());
                    } catch (ClassNotFoundException exception) {
                        logger.error("Could not find this Customizer: ", customizerName);
                        logger.debug(exception);
                        exception.printStackTrace();
                    } catch (Exception exception) {
                        logger.error("Could not load this Customizer: ", customizerName);
                        logger.error(exception.getMessage());
                        logger.debug(exception);
                        exception.printStackTrace();
                    }
                }
                logger.info("Number of loaded customizers: ", customizerCount);
            } catch (Exception exception) {
                logger.error("Could not load this list: ", CUSTOMIZERS_LIST);
                logger.debug(exception);
            }
        }
   }
    
    /**
     * Serializes the ChemObject to CML and redirects it to the output Writer.
     *
     * @param object A Molecule of SetOfMolecules object
     */
    public Node convert(ChemObject object, CMLDocument doc) throws CDKException, CMLException {
        logger.debug("Writing object in CML of type: ", object.getClass().getName());
        this.doc=doc;
        Element element = this.createElement("test");
        if (object instanceof SetOfMolecules) {
            writeSetOfMolecules((SetOfMolecules)object, element);
        } else if (object instanceof Crystal) {
            writeCrystal((Crystal)object, element);
        } else if (object instanceof Molecule) {
            // create CML atom and bond ids
            Molecule mol = (Molecule)object;
            if (useCmlIdentifiers) {
                new IDCreator().createIDs(mol);
            }
            writeMolecule(mol, element);
        } else if (object instanceof AtomContainer) {
            writeWrappedAtomContainer((AtomContainer)object, element);
        } else if (object instanceof ChemSequence) {
            writeChemSequence((ChemSequence)object, element);
        } else if (object instanceof ChemFile) {
            writeChemFile((ChemFile)object, element);
        } else if (object instanceof ChemModel) {
            writeChemModel((ChemModel)object, element);
        } else if (object instanceof Atom) {
            writeAtom(null, (Atom)object, element);
        } else if (object instanceof Bond) {
            writeBond((Bond)object, element);
        } else if (object instanceof Reaction) {
            writeReaction((Reaction)object, element);
        } else {
            String errorMessage = "This object type is not supported: " + object.getClass().getName();
            logger.error(errorMessage);
            throw new CDKException(errorMessage);
        }
        if (setNamespaceUri && 
            schemaInstanceOutput && instanceLocation.length() > 0) {
            Attr schemaLocAttr = doc.createAttribute("xsi");
            schemaLocAttr.setPrefix("xmlns");
            schemaLocAttr.setValue("http://www.w3.org/2001/XMLSchema-instance");
            ((Element)element.getFirstChild()).setAttributeNodeNS(schemaLocAttr);
            schemaLocAttr = doc.createAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");
            schemaLocAttr.setPrefix("xsi");
            schemaLocAttr.setValue(namespace + " " + instanceLocation);
            ((Element)element.getFirstChild()).setAttributeNodeNS(schemaLocAttr);
        }
        return(element.getFirstChild());
    };

    // Private procedures

    private void writeChemFile(ChemFile cf, Element nodeToAppend) throws CMLException{
        CmlImpl cml=new CmlImpl(doc);
        nodeToAppend.appendChild(cml);
        if (cf.getChemSequenceCount() > 1) {
            cml.setTitle("sequence");
            for (int i=0; i < cf.getChemSequenceCount(); i++ ) {
                writeChemSequence(cf.getChemSequence(i),cml);
            }
        } else {
            for (int i=0; i < cf.getChemSequenceCount(); i++ ) {
                writeChemSequence(cf.getChemSequence(i),cml);
            }
        }
    }

    private void writeCrystal(Crystal crystal, Element nodeToAppend) throws CMLException {
        MoleculeImpl mol = new MoleculeImpl(doc);
        
        // output the crystal info
        double[] notionalCoords = CrystalGeometryTools.cartesianToNotional(
            crystal.getA(), crystal.getB(), crystal.getC()
        );
        CrystalImpl crystalimp = new CrystalImpl(doc);
        CMLScalar scalar = new ScalarImpl(doc);
        scalar.setTitle("a");
        scalar.setContentValue(Double.toString(notionalCoords[0]));
        crystalimp.appendScalar(scalar);
        scalar = new ScalarImpl(doc);
        scalar.setTitle("b");
        scalar.setContentValue(Double.toString(notionalCoords[1]));
        crystalimp.appendScalar(scalar);
        scalar = new ScalarImpl(doc);
        scalar.setTitle("c");
        scalar.setContentValue(Double.toString(notionalCoords[2]));
        crystalimp.appendScalar(scalar);
        scalar = new ScalarImpl(doc);
        scalar.setTitle("alpha");
        scalar.setContentValue(Double.toString(notionalCoords[3]));
        crystalimp.appendScalar(scalar);
        scalar = new ScalarImpl(doc);
        scalar.setTitle("beta");
        scalar.setContentValue(Double.toString(notionalCoords[4]));
        crystalimp.appendScalar(scalar);
        scalar = new ScalarImpl(doc);
        scalar.setTitle("gamma");
        scalar.setContentValue(Double.toString(notionalCoords[5]));
        crystalimp.appendScalar(scalar);
        CMLSymmetry symmetry = new SymmetryImpl(doc);
        symmetry.setSpaceGroup(crystal.getSpaceGroup());
        crystalimp.appendSymmetry(symmetry);

        mol.appendCrystal(crystalimp);
        writeAtomContainer((AtomContainer)crystal,mol);
        nodeToAppend.appendChild(mol);
    }

    private void writeWrappedAtomContainer(AtomContainer ac, Element nodeToAppend) throws CMLException{
        // create CML atom and bond ids
        if (useCmlIdentifiers) {
            new IDCreator().createIDs(ac);
        }
        MoleculeImpl molecule = new MoleculeImpl(doc);
        addID(ac, molecule);
        addTitle(ac, molecule);
        nodeToAppend.appendChild(molecule);
        writeAtomContainer(ac,molecule);
    }
    
    private void writeAtomContainer(AtomContainer container, Element nodeToAppend) throws CMLException{
        writeAtomArray(container, container.getAtoms(),nodeToAppend);
        writeBondArray(container.getBonds(),nodeToAppend);
        writeProperties(container, nodeToAppend);
    }

    private void writeSetOfMolecules(SetOfMolecules som, Element nodeToAppend) throws CMLException{
        logger.debug("Writing SOM");
        // create CML atom and bond ids
        if (useCmlIdentifiers) {
            new IDCreator().createIDs(som);
        }
        Molecule[] molecules = som.getMolecules();
        logger.debug("Found # molecule(s) in set: ", molecules.length);
        if (molecules.length > 1) {
            ListImpl list = new ListImpl(doc);
            nodeToAppend.appendChild(list);
            for (int i = 0; i < molecules.length; i++) {
                writeMolecule(molecules[i], list);
            }
        } else if (molecules.length == 1) {
            writeMolecule(molecules[0], nodeToAppend);
        }
    }

    private void writeChemSequence(ChemSequence chemseq, Element nodeToAppend) throws CMLException {
        int count = chemseq.getChemModelCount();
        if (count > 1){
            ListImpl list=new ListImpl(doc);
            nodeToAppend.appendChild(list);
            for (int i = 0; i < count; i++) {
                writeChemModel(chemseq.getChemModel(i),list);
            }
        }else{
            writeChemModel(chemseq.getChemModel(0), nodeToAppend);
        }
    }

    private void writeChemModel(ChemModel model, Element nodeToAppend) throws CMLException {
        logger.debug("Writing ChemModel");
        CmlImpl cml=new CmlImpl(doc);
        nodeToAppend.appendChild(cml);
        Crystal crystal = model.getCrystal();
        SetOfMolecules som = model.getSetOfMolecules();
        SetOfReactions reactionSet = model.getSetOfReactions();
        if (crystal != null) {
            writeCrystal(crystal, cml);
        }
        if (som != null) {
            writeSetOfMolecules(som, cml);
        }
        if (reactionSet != null) {
            writeSetOfReactions(reactionSet, cml);
        }
        if (crystal == null && som == null && reactionSet == null) {
            cml.appendChild(doc.createComment("model contains no data"));
        }
    }

    private void writeSetOfReactions(SetOfReactions reactionSet, Element nodeToAppend) throws CMLException{
        Reaction[] reactions = reactionSet.getReactions();
        logger.debug("Writing SetOfReactions: ", reactions.length);
        if (reactions.length > 0) {
            ReactionListImpl reactionlist=new ReactionListImpl(doc);
            namespace="http://www.xml-cml.org/schema/cml2/react";
            nodeToAppend.appendChild(reactionlist);
            addID(reactionSet, reactionlist);
            addTitle(reactionSet, reactionlist);
            // first reaction properties
            writeProperties(reactionSet, reactionlist);
            // now come the actual reactions
            for (int i=0; i < reactions.length; i++) {
                writeReaction(reactions[i], reactionlist);
            }
        }
    }
    
    private void writeReaction(Reaction reaction, Element nodeToAppend) throws CMLException{
        logger.debug("Writing Reaction...");
        // create CML atom and bond ids
        if (useCmlIdentifiers) {
            new IDCreator().createIDs(reaction);
        }
        ReactionImpl reactionimpl=new ReactionImpl(doc);
        namespace="http://www.xml-cml.org/schema/cml2/react";
        nodeToAppend.appendChild(reactionimpl);
        addID(reaction, reactionimpl);
        addTitle(reaction, reactionimpl);
        // first reaction properties
        writeProperties(reaction, reactionimpl);
        // now come reactants and products
        Molecule[] reactants = reaction.getReactants().getMolecules();
        if (reactants.length > 0) {
            ReactantListImpl reactantslist=new ReactantListImpl(doc);
            reactionimpl.appendChild(reactantslist);
            for (int i=0; i<reactants.length; i++) {
                ReactantImpl reactant=new ReactantImpl(doc);
                reactantslist.appendChild(reactant);
                writeMolecule(reactants[i], reactant);
            }
        }
        Molecule[] products = reaction.getProducts().getMolecules();
        if (products.length > 0) {
            ProductListImpl productslist=new ProductListImpl(doc);
            reactionimpl.appendChild(productslist);
            for (int i=0; i<products.length; i++) {
                ProductImpl product=new ProductImpl(doc);
                productslist.appendChild(product);
                writeMolecule(products[i], product);
            }
        }
    }
    
    private boolean addID(ChemObject object, Element nodeToAdd) {
        if (object.getID() == null || object.getID().length() == 0) {
            return false;
        } else {
            // use some unique default -> the hashcode
            nodeToAdd.setAttribute("id", object.getID());
            return true;
        }
    }
    
    private boolean addTitle(ChemObject object, Element nodeToAdd) {
        if (object.getProperty(CDKConstants.TITLE) != null) {
            nodeToAdd.setAttribute("title", (String)object.getProperty(CDKConstants.TITLE));
            return true;
        }
        return false;
    }

    private void writeMolecule(Molecule mol, Element nodeToAppend) throws CMLException{
        logger.debug("Writing molecule");
        MoleculeImpl molecule = new MoleculeImpl(doc);
        addID(mol, molecule);
        addTitle(mol, molecule);
        nodeToAppend.appendChild(molecule);
        writeAtomContainer(mol,molecule);
        Enumeration elements = customizers.elements();
        while (elements.hasMoreElements()) {
            Customizer customizer = (Customizer)elements.nextElement();
            try {
                customizer.customize(this, mol, molecule);
            } catch (Exception exception) {
                logger.error("Error while customizing CML output with customizer: ",
                    customizer.getClass().getName());
                logger.debug(exception);
            }
        }
    }

    private void writeAtomArray(AtomContainer container, Atom atoms[], Element nodeToAppend) throws CMLException {
        AtomArrayImpl atomarray = new AtomArrayImpl(doc);
        nodeToAppend.appendChild(atomarray);
        for (int i = 0; i < atoms.length; i++) {
            writeAtom(container, atoms[i], atomarray);
        }
    }
    
    private void writeBondArray(Bond bonds[], Element nodeToAppend) throws CMLException {
        if (bonds.length > 0) {
            BondArrayImpl bondarray=new BondArrayImpl(doc);
            nodeToAppend.appendChild(bondarray);
            for (int i = 0; i < bonds.length; i++) {
                writeBond(bonds[i], bondarray);
            }
        }
    }
    
    private void writeProperties(ChemObject object, Element nodeToAppend) {
        Hashtable props = object.getProperties();
        Enumeration keys = props.keys();
        Element propList = null;
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if (key instanceof DictRef) {
                Object value = props.get(key);
                Element scalar = this.createElement("scalar");
                scalar.setAttribute("dictRef",((DictRef)key).getType());
                nodeToAppend.appendChild(scalar);
                scalar.appendChild(doc.createTextNode(value.toString()));
            } else if (key instanceof String) {
                String stringKey = (String)key;
                if (stringKey.equals(CDKConstants.TITLE)) {
                    // don't output this one. It's covered by addTitle()
                } else if (!(stringKey.startsWith("org.openscience.cdk"))) {
                    Object value = props.get(key);
                    Element scalar = this.createElement("scalar");
                    scalar.setAttribute("title",(String)key);
                    nodeToAppend.appendChild(scalar);
                    scalar.appendChild(doc.createTextNode(value.toString()));
                }
            } else {
                logger.warn("Don't know what to do with this property key: " +
                    key.getClass().getName()
                );
            }
        }
        if (propList != null) {
            nodeToAppend.appendChild(propList);
        }
    }

    /**
     * Picks the first dictRef it finds. CML support only one, but CDK 
     * tends to have more than one, i.e. also dictRefs for fields.
     */
    private boolean addDictRef(ChemObject object, Element nodeToAppend) {
        Hashtable properties = object.getProperties();
        Iterator iter = properties.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            if (key instanceof String) {
                String keyName = (String)key;
                if (keyName.startsWith(DictionaryDatabase.DICTREFPROPERTYNAME)) {
                    String dictRef = (String)properties.get(keyName);
                    String details = "Dictref being anaylyzed: " + dictRef;
                    nodeToAppend.setAttribute("dictRef", dictRef);
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean addAtomID(Atom atom, Element nodeToAppend) {
        if(atom.getID()!=null && !atom.getID().equals(""))
          nodeToAppend.setAttribute("id", atom.getID());
        else
          nodeToAppend.setAttribute("id", "a" + new Integer(atom.hashCode()).toString());
        return true;
    }
    
    private void writeAtom(AtomContainer container, Atom atom, Element nodeToAppend) throws CMLException {
        AtomImpl atomimpl=new AtomImpl(doc);
        nodeToAppend.appendChild(atomimpl);
        addAtomID(atom, atomimpl);
        addDictRef(atom, atomimpl);
        if (atom instanceof PseudoAtom) {
            String label = ((PseudoAtom)atom).getLabel();
            if (label != null) atomimpl.setAttribute("title", label);
            atomimpl.setAttribute("elementType", "Du");
        } else {
            atomimpl.setAttribute("elementType", atom.getSymbol());
        }
        add(atom.getPoint2d(), atomimpl);
        add(atom.getPoint3d(), atomimpl);
        addFractional(atom.getFractionalPoint3d(), atomimpl);
        int fCharge = atom.getFormalCharge();
        if (fCharge != 0) {
            atomimpl.setAttribute("formalCharge", fCharge+"");
        }
        int hydrogenCount = atom.getHydrogenCount();
        if (hydrogenCount != 0) {
            atomimpl.setAttribute("hydrogenCount", hydrogenCount+"");
        }
        int massNumber = atom.getMassNumber();
        if (!(atom instanceof PseudoAtom)) {
            Isotope majorIsotope = isotopeFactory.getMajorIsotope(atom.getSymbol());
            if (majorIsotope != null) {
                int majorMassNumber = majorIsotope.getMassNumber();
                if (massNumber != 0 && massNumber != majorMassNumber) {
                    atomimpl.setAttribute("isotope", massNumber+"");
                }
            } else {
                logger.warn("Could not find major isotope for : " + atom.getSymbol());
            }
        }
        if (atom.getCharge() != 0.0) {
            CMLScalar scalar = new ScalarImpl(doc);
            scalar.setDataType("xsd:float");
            scalar.setDictRef("cdk:partialCharge");
            scalar.appendChild(doc.createTextNode("" + atom.getCharge()));
            atomimpl.appendScalar(scalar);
        }
        if (container != null && container.getSingleElectronSum(atom) > 0) {
            atomimpl.setAttribute("spinMultiplicity", 
                (container.getSingleElectronSum(atom)+1)+""
            );
        }
        writeProperties(atom, atomimpl);
        Enumeration elements = customizers.elements();
        while (elements.hasMoreElements()) {
            Customizer customizer = (Customizer)elements.nextElement();
            try {
                customizer.customize(this, atom, atomimpl);
            } catch (Exception exception) {
                logger.error("Error while customizing CML output with customizer: ",
                    customizer.getClass().getName());
                logger.debug(exception);
            }
        }
    }

    private void writeBond(Bond bond, Element nodeToAdd) throws CMLException {
        BondImpl bondimpl=new BondImpl(doc);
        nodeToAdd.appendChild(bondimpl);
        logger.debug("Bond id: ", bond.getID());
        if (bond.getID() == null || bond.getID().length() == 0) {
            bondimpl.setAttribute("id", "b" + bond.hashCode());
        }else{
            bondimpl.setAttribute("id", bond.getID());
        }
        StringBuffer atomRefs = new StringBuffer();
        Atom[] atoms = bond.getAtoms();
        for (int i = 0; i < atoms.length; i++) {
            String atomID = atoms[i].getID();
            if (atomID == null || atomID.length() == 0) {
                atomRefs.append("a" + new Integer(atoms[i].hashCode()).toString());
            } else {
                atomRefs.append(atomID);
            }
            if (i < atoms.length-1) {
                atomRefs.append(" ");
            }
        }
        if (atoms.length == 2) {
            bondimpl.setAtomRefs2(atomRefs.toString());
        } else {
            bondimpl.setAtomRefs(atomRefs.toString());
        }
        double border = bond.getOrder();
        if (bond.getFlag(CDKConstants.ISAROMATIC) | 
            border == CDKConstants.BONDORDER_AROMATIC) {
            bondimpl.setAttribute("order", "A");
        } else if (border == CDKConstants.BONDORDER_SINGLE) {
            bondimpl.setAttribute("order", "S");
        } else if (border == CDKConstants.BONDORDER_DOUBLE) {
            bondimpl.setAttribute("order", "D");
        } else if (border == CDKConstants.BONDORDER_TRIPLE) {
            bondimpl.setAttribute("order", "T");
        } else {
            logger.warn("Outputing bond order in non CML2 default way.");
            Element scalar = this.createElement("scalar");
            scalar.setAttribute("dataType","xsd:float");
            scalar.setAttribute("dictRef","cdk:bondOrder");
            scalar.setAttribute("title","order");
            bondimpl.appendChild(scalar);
            scalar.appendChild(doc.createTextNode(bond.getOrder()+""));
        }
        if (bond.getStereo() == CDKConstants.STEREO_BOND_UP ||
            bond.getStereo() == CDKConstants.STEREO_BOND_DOWN) {
            Element scalar = this.createElement("scalar");
            scalar.setAttribute("dataType","xsd:string");
            scalar.setAttribute("dictRef","mdl:stereo");
            bondimpl.appendChild(scalar);
            if (bond.getStereo() == CDKConstants.STEREO_BOND_UP) {
              scalar.appendChild(doc.createTextNode("W"));
            }else{
              scalar.appendChild(doc.createTextNode("H"));
            }
        }
        if (bond.getProperties().size() > 0) writeProperties(bond, bondimpl);
    }

    private void add(Point2d p, Element nodeToAdd) {
        if (p != null) {
            nodeToAdd.setAttribute("x2", new Float(p.x).toString());
            nodeToAdd.setAttribute("y2", new Float(p.y).toString());
        }
    }

    private void add(Point3d p, Element nodeToAdd) {
        if (p != null) {
            nodeToAdd.setAttribute("x3", new Float(p.x).toString());
            nodeToAdd.setAttribute("y3", new Float(p.y).toString());
            nodeToAdd.setAttribute("z3", new Float(p.z).toString());
        }
    }

    private void addFractional(Point3d p, Element nodeToAdd) {
        if (p != null) {
            nodeToAdd.setAttribute("xFract", new Float(p.x).toString());
            nodeToAdd.setAttribute("yFract", new Float(p.y).toString());
            nodeToAdd.setAttribute("zFract", new Float(p.z).toString());
        }
    }  
    
    protected String write(double[] da) {
        StringBuffer sb=new StringBuffer();
        for (int i=0; i < da.length; i++) {
            sb.append(new Double(da[i]).toString());
            if (i < (da.length -1)) {
                sb.append(" ");
            }
        }
        return(sb.toString());
    }
    
    protected Element createElement(String elementName) {
        logger.debug("Creating element: ", elementName);
        Element element = null;
        if (setNamespaceUri) {
            logger.debug("Setting NS to: ", namespace);
            element = doc.createElementNS(namespace, elementName);
            if (prefix.length() > 0) {
                element.setPrefix(prefix);
            }
        } else {
            element = doc.createElement(elementName);
        }
        return element;
    }

    protected Text createTextNode(String text) {
        return doc.createTextNode(text);
    }
    
}

