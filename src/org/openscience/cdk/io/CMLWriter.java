/* $RCSfile$ 
 * $Author$ 
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.io;

import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;
import org.openscience.cdk.tools.IsotopeFactory;
import org.openscience.cdk.tools.IDCreator;
import org.openscience.cdk.io.setting.*;
import org.openscience.cdk.dict.*;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Enumeration;


/**
 * <p>Serializes a SetOfMolecules or a Molecule object to CML 2 code.
 * Output can be redirected to other Writer objects like StringWriter
 * and FileWriter. An example:
 *
 * <pre>
 *   StringWriter output = new StringWriter();
 *   boolean makeFragment = true;
 *   CMLWriter cmlwriter = new CMLWriter(output, makeFragment);
 *   cmlwriter.write(molecule);
 *   cmlwriter.close();
 *   String cmlcode = output.toString();
 * </pre>
 *
 * <p>Output to a file called "molecule.cml" can done with:
 *
 * <pre>
 *   FileWriter output = new FileWriter("molecule.cml");
 *   CMLWriter cmlwriter = new CMLWriter(output);
 *   cmlwriter.write(molecule);
 *   cmlwriter.close();
 * </pre>
 *
 * <p>For atoms it outputs: coordinates, element type and formal charge.
 * For bonds it outputs: order, atoms (2, or more) and wedges.
 * 
 * <p>References:
 *   <a href="http://cdk.sf.net/biblio.html#PMR99">PMR99</a>
 *
 * @see java.io.FileWriter
 * @see java.io.StringWriter
 *
 * @author Egon Willighagen
 *
 * @keyword file format, CML
 */
public class CMLWriter extends DefaultChemObjectWriter {

    private Writer output;

    private BooleanIOSetting cmlIds;
    private BooleanIOSetting namespacedOutput;
    private BooleanIOSetting xmlDecl;
    
    private final String namespace = "cml";

    private boolean done;
    private boolean fragment;

    private org.openscience.cdk.tools.LoggingTool logger;
    private IsotopeFactory isotopeFactory = null;

    /**
     * Constructs a new CMLWriter class. Output will be stored in the Writer
     * class given as parameter. The CML code will be valid CML code with a
     * XML header. Only one object can be stored.
     *
     * @param out Writer to redirect the output to.
     */
    public CMLWriter(Writer out) {
        this(out, false);
    }

    public CMLWriter() {
        this(new StringWriter());
    }

    public String getFormatName() {
        return "Chemical Markup Language";
    }
    
    /**
     * Constructs a new CMLWriter class. Output will be stored in the Writer
     * class given as parameter. The CML code will be valid CML code with a
     * XML header. More than object can be stored.
     *
     * @param w         Writer to redirect the output to.
     * @param fragment  Boolean denoting that the content is not
     */
    public CMLWriter(Writer w, boolean fragment) {
        this(fragment);
        output = w;
    }

    public CMLWriter(boolean fragment) {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
        this.fragment = fragment;
        this.done = false;
        try {
            isotopeFactory = IsotopeFactory.getInstance();
        } catch (Exception exception) {
            logger.error("Failed to initiate isotope factory: " + exception.toString());
        }
        initIOSettings();
    }

    /**
     * Flushes the output and closes this object
     */
    public void close() throws IOException {
        output.close();
    }

    /**
     * Serializes the ChemObject to CML and redirects it to the output Writer.
     *
     * @param object A Molecule of SetOfMolecules object
     */
    public void write(ChemObject object) throws CDKException {
        logger.debug("Writing object in CML of type: " + object.getClass().getName());
        
        customizeJob();
        
        if (!done) {
            if (!fragment && xmlDecl.isSet()) {
                write("<?xml version=\"1.0\"?>\n");
            }
            if (object instanceof SetOfMolecules) {
                writeSetOfMolecules((SetOfMolecules)object);
            } else if (object instanceof Molecule) {
                writeMolecule((Molecule)object);
            } else if (object instanceof Crystal) {
                writeCrystal((Crystal)object);
            } else if (object instanceof ChemSequence) {
                writeChemSequence((ChemSequence)object);
            } else if (object instanceof ChemFile) {
                writeChemFile((ChemFile)object);
            } else if (object instanceof ChemModel) {
                writeChemModel((ChemModel)object);
            } else if (object instanceof Atom) {
                writeAtom((Atom)object);
            } else if (object instanceof Bond) {
                writeBond((Bond)object);
            } else if (object instanceof Reaction) {
                writeReaction((Reaction)object);
            } else {
                logger.error("This object type is not supported.");
                throw new CDKException("This object type is not supported.");
            }
            if (!fragment) {
                done = true;
            }
        } else {};
    };

    public ChemObject highestSupportedChemObject() {
        return new ChemFile();
    }

    // Private procedures

    private void writeChemFile(ChemFile cf) {
        if (cf.getChemSequenceCount() > 1) {
            Hashtable listAtts = new Hashtable();
            listAtts.put("title", "sequence");
            writeOpenTag("cml", listAtts);
            for (int i=0; i < cf.getChemSequenceCount(); i++ ) {
                writeChemSequence(cf.getChemSequence(i));
            }
            writeCloseTag("cml");
        } else {
            for (int i=0; i < cf.getChemSequenceCount(); i++ ) {
                writeChemSequence(cf.getChemSequence(i));
            }
        }
    }

    private void writeCrystal(Crystal crystal) {
        // FIXME: does this produce CML 2
        writeOpenTag("molecule");
        writeOpenTag("crystal");
        write("    <");
        writeElementName("string");
        write(" builtin=\"spacegroup\">" + crystal.getSpaceGroup()
              + "</string>");
        write("    <floatArray title=\"a\" convention=\"PMP\">");
        write(crystal.getA());
        write("</floatArray>\n");
        write("    <floatArray title=\"b\" convention=\"PMP\">");
        write(crystal.getB());
        write("</floatArray>\n");
        write("    <floatArray title=\"c\" convention=\"PMP\">");
        write(crystal.getC());
        write("</floatArray>\n");
        writeCloseTag("crystal");
        writeAtomContainer((AtomContainer)crystal);
        writeCloseTag("molecule");
    }

    private void writeAtomContainer(AtomContainer ac) {
        writeAtomArray(ac.getAtoms());
        writeBondArray(ac.getBonds());
    }

    private void writeSetOfMolecules(SetOfMolecules som) {
        logger.debug("Writing SOM");
        int count = som.getMoleculeCount();
        logger.debug("Found " + count + " molecule(s) in set");
        if (count > 1) {
            writeOpenTag("list");
        }
        for (int i = 0; i < count; i++) {
            writeMolecule(som.getMolecule(i));
        }
        if (count > 1) {
            writeCloseTag("list");
        }
    }

    private void writeChemSequence(ChemSequence chemseq) {
        int count = chemseq.getChemModelCount();
        if (count > 1)
            writeOpenTag("list");
        for (int i = 0; i < count; i++) {
            this.writeChemModel(chemseq.getChemModel(i));
        }
        if (count > 1)
            writeCloseTag("list");
    }

    private void writeChemModel(ChemModel model) {
        logger.debug("Writing ChemModel");
        Crystal crystal = model.getCrystal();
        SetOfMolecules som = model.getSetOfMolecules();
        SetOfReactions reactionSet = model.getSetOfReactions();
        if (crystal != null) {
            writeCrystal(crystal);
        }
        if (som != null) {
            writeSetOfMolecules(som);
        }
        if (reactionSet != null) {
            writeSetOfReactions(reactionSet);
        }
        if (crystal == null && som == null && reactionSet == null) {
            write("<!-- model contains no data -->\n");
        }
    }

    private void writeSetOfReactions(SetOfReactions reactionSet) {
        Reaction[] reactions = reactionSet.getReactions();
        if (reactions.length > 0) {
            Hashtable reactionListAtts = new Hashtable();
            addID(reactionListAtts, reactionSet);
            addTitle(reactionListAtts, reactionSet);
            writeOpenTag("reactionList", reactionListAtts);
            // first reaction properties
            writeProperties(reactionSet);
            // now come the actual reactions
            for (int i=0; i < reactions.length; i++) {
                writeReaction(reactions[i]);
            }
            writeCloseTag("reactionList");
        }
    }
    
    private void writeReaction(Reaction reaction) {
        Hashtable reactionAtts = new Hashtable();
        addID(reactionAtts, reaction);
        addTitle(reactionAtts, reaction);
        writeOpenTag("reaction", reactionAtts);
        // first reaction properties
        writeProperties(reaction);
        // now come reactants and products
        Molecule[] reactants = reaction.getReactants();
        if (reactants.length > 0) {
            writeOpenTag("reactantList");
            for (int i=0; i<reactants.length; i++) {
                writeOpenTag("reactant");
                writeMolecule(reactants[i]);
                writeCloseTag("reactant");
            }
            writeCloseTag("reactantList");
        }
        Molecule[] products = reaction.getProducts();
        if (products.length > 0) {
            writeOpenTag("productList");
            for (int i=0; i<products.length; i++) {
                writeOpenTag("product");
                writeMolecule(products[i]);
                writeCloseTag("product");
            }
            writeCloseTag("productList");
        }
        writeCloseTag("reaction");
    }
    
    private void writeMolecule(Molecule mol) {
        // create CML atom and bond ids
        if (cmlIds.isSet()) {
            IDCreator.createAtomAndBondIDs(mol);
        }

        Hashtable moleculeAtts = new Hashtable();
        if (mol.getID() != null && mol.getID().length() != 0) {
            moleculeAtts.put("id", mol.getID());
        }
        writeOpenTag("molecule", moleculeAtts);
        writeAtomContainer((AtomContainer)mol);
        writeCloseTag("molecule");
    }

    private void writeAtomArray(Atom atoms[]) {
        writeOpenTag("atomArray");
        for (int i = 0; i < atoms.length; i++) {
            writeAtom(atoms[i]);
        }
        writeCloseTag("atomArray");
    }
    
    private void writeBondArray(Bond bonds[]) {
        if (bonds.length > 0) {
            writeOpenTag("bondArray");
            for (int i = 0; i < bonds.length; i++) {
                writeBond(bonds[i]);
            }
            writeCloseTag("bondArray");
        }
    }
    
    private void writeProperties(ChemObject object) {
        Hashtable props = object.getProperties();
        Enumeration keys = props.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if (key instanceof DictRef) {
                Object value = props.get(key);
                Hashtable atts = new Hashtable();
                atts.put("dictRef", ((DictRef)key).getType());
                writeOpenTag("scalar", atts);
                write(value.toString());
                writeCloseTag("scalar");
            } else if (key instanceof String && !((String)key).startsWith("org.openscience.cdk")) {
                Object value = props.get(key);
                Hashtable atts = new Hashtable();
                atts.put("title", key);
                writeOpenTag("scalar", atts);
                write(value.toString());
                writeCloseTag("scalar");
            } else {
                logger.warn("Don't know what to do with this property key: " +
                    key.getClass().getName()
                );
            }
        }
    }

    private boolean addID(Hashtable atts, ChemObject object) {
        if (object.getID() == null || object.getID().length() == 0) {
            return false;
        } else {
            // use some unique default -> the hashcode
            atts.put("id", object.getID());
            return true;
        }
    }
    
    private boolean addTitle(Hashtable atts, ChemObject object) {
        if (object.getProperty(CDKConstants.TITLE) != null) {
            atts.put("title", object.getProperty(CDKConstants.TITLE));
            return true;
        }
        return false;
    }

    /**
     * Picks the first dictRef it finds. CML support only one, but CDK 
     * tends to have more than one, i.e. also dictRefs for fields.
     */
    private boolean addDictRef(Hashtable atts, ChemObject object) {
        Hashtable properties = object.getProperties();
        Iterator iter = properties.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            if (key instanceof String) {
                String keyName = (String)key;
                if (keyName.startsWith(DictionaryDatabase.DICTREFPROPERTYNAME)) {
                    String dictRef = (String)properties.get(keyName);
                    String details = "Dictref being anaylyzed: " + dictRef;
                    atts.put("dictRef", dictRef);
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean addAtomID(Hashtable atts, Atom atom) {
        atts.put("id", "a" + new Integer(atom.hashCode()).toString());
        return true;
    }
    
    private void writeAtom(Atom atom) {
        Hashtable atomAtts = new Hashtable();
        if (!addID(atomAtts, atom)) addAtomID(atomAtts, atom);
        addDictRef(atomAtts, atom);
        if (atom instanceof PseudoAtom) {
            String label = ((PseudoAtom)atom).getLabel();
            if (label != null) atomAtts.put("title", label);
            atomAtts.put("elementType", "Du");
        } else {
            atomAtts.put("elementType", atom.getSymbol());
        }
        add(atomAtts, atom.getPoint2D());
        add(atomAtts, atom.getPoint3D());
        addFractional(atomAtts, atom.getFractionalPoint3D());
        int fCharge = atom.getFormalCharge();
        if (fCharge != 0) {
            atomAtts.put("formalCharge", new Integer(fCharge));
        }
        int hydrogenCount = atom.getHydrogenCount();
        if (hydrogenCount != 0) {
            atomAtts.put("hydrogenCount", new Integer(hydrogenCount));
        }
        int massNumber = atom.getMassNumber();
        if (!(atom instanceof PseudoAtom)) {
            Isotope majorIsotope = isotopeFactory.getMajorIsotope(atom.getSymbol());
            if (majorIsotope != null) {
                int majorMassNumber = majorIsotope.getMassNumber();
                if (massNumber != 0 && massNumber != majorMassNumber) {
                    atomAtts.put("isotope", new Integer(massNumber));
                }
            } else {
                logger.warn("Could not find major isotope for : " + atom.getSymbol());
            }
        }
        Hashtable props = atom.getProperties();
        if (props.size() > 0) {
            writeOpenTag("atom", atomAtts);
            writeProperties(atom);
            writeCloseTag("atom");
        } else {
            writeEmptyElement("atom", atomAtts);
        }
    }

    private void writeBond(Bond bond) {
        StringBuffer childElements = new StringBuffer();
        Hashtable bondAtts = new Hashtable();
        logger.debug("Bond id: " + bond.getID());
        if (!addID(bondAtts, bond)) {
            // use some unique default -> the hashcode
            bondAtts.put("id", "b" + bond.hashCode());
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
            bondAtts.put("atomRefs2", atomRefs);
        } else {
            bondAtts.put("atomRefs", atomRefs);
        }
        double border = bond.getOrder();
        if (bond.getFlag(CDKConstants.ISAROMATIC)) {
            bondAtts.put("order", "A");
        } else if (border == CDKConstants.BONDORDER_SINGLE) {
            bondAtts.put("order", "S");
        } else if (border == CDKConstants.BONDORDER_DOUBLE) {
            bondAtts.put("order", "D");
        } else if (border == CDKConstants.BONDORDER_TRIPLE) {
            bondAtts.put("order", "T");
        } else {
            logger.warn("Outputing bond order in non CML2 default way.");
            childElements.append("      <scalar dataType=\"xsd:float\" title=\"order\">" +
                                 bond.getOrder() + "</scalar>\n");
        }
        if (bond.getStereo() == CDKConstants.STEREO_BOND_UP ||
            bond.getStereo() == CDKConstants.STEREO_BOND_DOWN) {
            childElements.append("      <scalar dataType=\"xsd:string\" dictRef=\"mdl:stereo\">");
		    if (bond.getStereo() == CDKConstants.STEREO_BOND_UP) {
                childElements.append("W");
		    } else if (bond.getStereo() == CDKConstants.STEREO_BOND_DOWN) {
			    childElements.append("H");
		    }
		    childElements.append("</scalar>\n");
		}
        Hashtable props = bond.getProperties();
        if (childElements.length() > 0 || props.size() > 0) {
            writeOpenTag("bond", bondAtts);
            if (childElements.length() > 0) write(childElements.toString());
            if (props.size() > 0) writeProperties(bond);
            writeCloseTag("bond");
        } else {
            writeEmptyElement("bond", bondAtts);
        }
    }

    private void add(Hashtable atts, Point2d p) {
        if (p != null) {
            atts.put("x2", new Float(p.x).toString());
            atts.put("y2", new Float(p.y).toString());
        }
    }

    private void add(Hashtable atts, Point3d p) {
        if (p != null) {
            atts.put("x3", new Float(p.x).toString());
            atts.put("y3", new Float(p.y).toString());
            atts.put("z3", new Float(p.z).toString());
        }
    }

    private void addFractional(Hashtable atts, Point3d p) {
        if (p != null) {
            atts.put("xFract", new Float(p.x).toString());
            atts.put("yFract", new Float(p.y).toString());
            atts.put("zFract", new Float(p.z).toString());
        }
    }

    private void writeElementName(String name) {
        if (namespacedOutput.isSet()) {
            write(namespace + ":");
        }
        write(name);
    }
    
    private void writeOpenTag(String name) {
        writeOpenTag(name, null);
    }
    
    private void writeOpenTag(String name, Hashtable atts) {
        write("<");
        writeElementName(name);
        writeOpenTagAtts(atts);
        write(">\n");
    }

    private void writeEmptyElement(String name, Hashtable atts) {
        write("<");
        writeElementName(name);
        writeOpenTagAtts(atts);
        write("/>\n");
    }
    private void writeOpenTagAtts(Hashtable atts) {
        if (atts != null) {
            Enumeration keys = atts.keys();
            while (keys.hasMoreElements()) {
                String key = (String)keys.nextElement();
                write(" " + key + "=\"");
                write(atts.get(key).toString());
                write("\"");
            }
        }
    }
    
    private void writeCloseTag(String name) {
        write("</");
        writeElementName(name);
        write(">\n");
    }
    
    private void write(double[] da) {
        for (int i=0; i < da.length; i++) {
            write(new Double(da[i]).toString());
            if (i < (da.length -1)) {
                write(" ");
            }
        }
    }

    private void write(String s) {
		try {
		    output.write(s);
		} catch (IOException e) {
		    logger.error("CMLWriter IOException while printing \"" + 
	                s + "\":\n" + e.toString());
		}
    }
    
    private void initIOSettings() {
        cmlIds = new BooleanIOSetting("CMLIDs", IOSetting.LOW,
          "Should the output use CML identifiers?", 
          "yes");

        namespacedOutput = new BooleanIOSetting("NamespacedOutput", IOSetting.LOW,
          "Should the output use namespaced output?", 
          "no");

        xmlDecl = new BooleanIOSetting("XMLDeclaration", IOSetting.LOW,
          "Should the output use have a XMLDeclaration?", 
          "no");
    }
    
    private void customizeJob() {
        fireIOSettingQuestion(cmlIds);
        fireIOSettingQuestion(namespacedOutput);
        fireIOSettingQuestion(xmlDecl);
    }

    public IOSetting[] getIOSettings() {
        IOSetting[] settings = new IOSetting[3];
        settings[0] = cmlIds;
        settings[1] = namespacedOutput;
        settings[2] = xmlDecl;
        return settings;
    }
    
}
