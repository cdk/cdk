/* $RCSfile$ 
 * $Author$ 
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2003  The Chemistry Development Kit (CDK) project
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

import java.io.*;
import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;
import org.openscience.cdk.tools.IsotopeFactory;
import org.openscience.cdk.tools.IDCreator;
import org.openscience.cdk.io.setting.*;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;


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
 * @see FileWriter
 * @see StringWriter
 *
 * @author Egon Willighagen
 *
 * @keyword file format, CML
 */
public class CMLWriter extends DefaultChemObjectWriter {

    private Writer output;

    private BooleanIOSetting cmlIds;

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

    /**
     * Constructs a new CMLWriter class. Output will be stored in the Writer
     * class given as parameter. The CML code will be valid CML code with a
     * XML header. More than object can be stored.
     *
     * @param out       Writer to redirect the output to.
     * @param fragment  Boolean denoting that the content is not
     */
    public CMLWriter(Writer w, boolean fragment) {
        output = w;
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
        this.fragment = fragment;
        this.done = false;
        try {
            isotopeFactory = IsotopeFactory.getInstance();
        } catch (Exception exception) {
            logger.error("Failed to initiate isotope factory: " + exception.toString());
        }
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
            if (!fragment) {
                write("<?xml version=\"1.0\"?>\n");
            }
            if (object instanceof SetOfMolecules) {
                write((SetOfMolecules)object);
            } else if (object instanceof Molecule) {
                write((Molecule)object);
            } else if (object instanceof Crystal) {
                write((Crystal)object);
            } else if (object instanceof ChemSequence) {
                write((ChemSequence)object);
            } else if (object instanceof ChemFile) {
                write((ChemFile)object);
            } else if (object instanceof ChemModel) {
                write((ChemModel)object);
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

    private void write(ChemFile cf) {
        if (cf.getChemSequenceCount() > 1) {
            write("<cml title=\"sequence\">\n");
            for (int i=0; i < cf.getChemSequenceCount(); i++ ) {
                write(cf.getChemSequence(i));
            }
            write("</cml>\n");
        } else {
            for (int i=0; i < cf.getChemSequenceCount(); i++ ) {
                write(cf.getChemSequence(i));
            }
        }
    }

    private void write(Crystal crystal) {
        // FIXME: does this produce CML 2
        write("<molecule>\n");
        write("  <crystal>\n");
        write("    <string builtin=\"spacegroup\">" + crystal.getSpaceGroup() + "</string>");
        write("    <floatArray title=\"a\" convention=\"PMP\">");
        write(crystal.getA());
        write("</floatArray>\n");
        write("    <floatArray title=\"b\" convention=\"PMP\">");
        write(crystal.getB());
        write("</floatArray>\n");
        write("    <floatArray title=\"c\" convention=\"PMP\">");
        write(crystal.getC());
        write("</floatArray>\n");
        write("  </crystal>\n");
        write((AtomContainer)crystal);
        write("</molecule>\n");
    }

    private void write(AtomContainer ac) {
        write(ac.getAtoms());
        write(ac.getBonds());
    }

    private void write(SetOfMolecules som) {
        logger.debug("Writing SOM");
        int count = som.getMoleculeCount();
        logger.debug("Found " + count + " molecule(s) in set");
        if (count > 1) {
            write("<list>\n");
        }
        for (int i = 0; i < count; i++) {
            this.write(som.getMolecule(i));
        }
        if (count > 1) {
            write("</list>\n");
        }
    }

    private void write(ChemSequence chemseq) {
        int count = chemseq.getChemModelCount();
        if (count > 1)
            write("<list>\n");
        for (int i = 0; i < count; i++) {
            this.write(chemseq.getChemModel(i));
        }
        if (count > 1)
            write("</list>\n");
    }

    private void write(ChemModel model) {
        logger.debug("Writing ChemModel");
        Crystal crystal = model.getCrystal();
        SetOfMolecules som = model.getSetOfMolecules();
        SetOfReactions reactionSet = model.getSetOfReactions();
        if (crystal != null) {
            write(crystal);
        }
        if (som != null) {
            write(som);
        }
        if (reactionSet != null) {
            write(reactionSet);
        }
        if (crystal == null && som == null && reactionSet == null) {
            write("<!-- model contains no data -->\n");
        }
    }

    private void write(SetOfReactions reactionSet) {
        Reaction[] reactions = reactionSet.getReactions();
        if (reactions.length > 0) {
            write("<reactionList>\n");
            for (int i=0; i < reactions.length; i++) {
                write(reactions[i]);
            }
            write("</reactionList>\n");
        }
    }
    
    private void write(Reaction reaction) {
        write("<reaction>\n");
        Molecule[] reactants = reaction.getReactants();
        if (reactants.length > 0) {
            write("    <reactantList>\n");
            for (int i=0; i<reactants.length; i++) {
                write("    <reactant>\n");
                write(reactants[i]);
                write("    </reactant>\n");
            }
            write("    </reactantList>\n");
        }
        Molecule[] products = reaction.getReactants();
        if (products.length > 0) {
            write("  <productList>\n");
            for (int i=0; i<products.length; i++) {
                write("    <product>\n");
                write(products[i]);
                write("    </product>\n");
            }
            write("  </productList>\n");
        }
        write("</reaction>\n");
    }
    
    private void write(Molecule mol) {
        // create CML atom and bond ids
        if (cmlIds.isSet()) {
            IDCreator.createAtomAndBondIDs(mol);
        }

        write("<molecule");
        if (mol.getID() != null && mol.getID().length() != 0) {
            write(" id=\"" + mol.getID() + "\"");
        }
        write(">\n");
        write((AtomContainer)mol);
        write("</molecule>\n");
    }

    private void write(Atom atoms[]) {
		write("  <atomArray>\n");
		for (int i = 0; i < atoms.length; i++) {
		    write(atoms[i]);
		}
		write("  </atomArray>\n");
    }
    
    private void write(Bond bonds[]) {
        if (bonds.length > 0) {
            write("  <bondArray>\n");
            for (int i = 0; i < bonds.length; i++) {
                write(bonds[i]);
            }
            write("  </bondArray>\n");
        }
    }

    private void writeAtomID(Atom atom) {
        if (atom.getID() != null && atom.getID().length() != 0) {
            write(atom.getID());
        } else {
            // use some unique default -> the hashcode
            write("a" + atom.hashCode());
        }
    }
    
    private void write(Atom atom) {
		write("    <atom id=\"");
        writeAtomID(atom);
        write("\" ");
		write("elementType=\"");
		write(atom.getSymbol());
		write("\" ");
		write(atom.getPoint2D());
		write(atom.getPoint3D());
        int fCharge = atom.getFormalCharge();
        if (fCharge != 0) {
            write("formalCharge=\"" + fCharge + "\" ");
        }
        int hydrogenCount = atom.getHydrogenCount();
        if (hydrogenCount != 0) {
            write("hydrogenCount=\"" + hydrogenCount + "\" ");
        }
        int massNumber = atom.getAtomicMass();
        Isotope majorIsotope = isotopeFactory.getMajorIsotope(atom.getSymbol());
        if (majorIsotope != null) {
            int majorMassNumber = majorIsotope.getAtomicMass();
            if (massNumber != 0 && massNumber != majorMassNumber) {
                write("isotope=\"" + massNumber + "\" ");
            }
        } else {
            logger.warn("Could not find major isotope for : " + atom.getSymbol());
        }
		write("/>\n");
    }

    private void write(Bond bond) {
        StringBuffer childElements = new StringBuffer();
		write("    <bond id=\"");
        logger.debug("Bond id: " + bond.getID());
        if (bond.getID() != null && bond.getID().length() != 0) {
            write(bond.getID());
        } else {
            // use some unique default -> the hashcode
            write("b" + bond.hashCode());
        }
        write("\" ");
		Atom atoms[] = bond.getAtoms();
        if (atoms.length == 2) {
            write("atomRefs2=\"");
            for (int i = 0; i < 2; i++) {
                writeAtomID(atoms[i]);
                if (i == 0) {
                    write(" ");
                }
            }
            write("\" ");
        } else {
            write("atomRefs=\"");
            for (int i = 0; i < atoms.length; i++) {
                writeAtomID(atoms[i]);
                if (i < atoms.length-1) {
                    write(" ");
                }
            }
            write("\" ");
        }
        double border = bond.getOrder();
        if (border == CDKConstants.BONDORDER_SINGLE) {
            write("order=\"S\" ");
        } else if (border == CDKConstants.BONDORDER_DOUBLE) {
            write("order=\"D\" ");
        } else if (border == CDKConstants.BONDORDER_TRIPLE) {
            write("order=\"T\" ");
        } else if (border == CDKConstants.BONDORDER_AROMATIC) {
            write("order=\"A\" ");
        } else {
            logger.warn("Outputing bond order in non CML2 default way.");
            childElements.append("      <string convention=\"CDK\" builtin=\"order\"" + 
                                 bond.getOrder() + "\"/>\n");
        }
        if (bond.getStereo() == CDKConstants.STEREO_BOND_UP &&
            bond.getStereo() == CDKConstants.STEREO_BOND_DOWN) {
            childElements.append("      <string builtin=\"stereo\" convention=\"MDLMol\">");
		    if (bond.getStereo() == CDKConstants.STEREO_BOND_UP) {
                childElements.append("W");
		    } else if (bond.getStereo() == CDKConstants.STEREO_BOND_DOWN) {
			    childElements.append("H");
		    }
		    childElements.append("</string>\n");
		}
		if (childElements.length() > 0) { 
            write(">\n");
            write(childElements.toString());
            write("    </bond>\n");
        } else {
            write("/>\n");
        }
    }

    private void write(Point2d p) {
		if (p != null) {
		    write("x2=\"");
		    write(new Float(p.x).toString());
		    write("\" ");
		    write("y2=\"");
		    write(new Float(p.y).toString());
		    write("\" ");
		}
    }

    private void write(Point3d p) {
		if (p != null) {
		    write("x3=\"");
		    write(new Float(p.x).toString());
		    write("\" ");
		    write("y3=\"");
		    write(new Float(p.y).toString());
		    write("\" ");
		    write("z3=\"");
		    write(new Float(p.z).toString());
		    write("\" ");
		}
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
    
    private void customizeJob() {
        cmlIds = new BooleanIOSetting("CMLIDs", IOSetting.LOW,
          "Should the output use CML identifiers?", 
          "true");
        fireWriterSettingQuestion(cmlIds);
    }
}
