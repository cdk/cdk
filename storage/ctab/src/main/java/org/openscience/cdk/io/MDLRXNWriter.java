/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLFormat;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.io.setting.StringIOSetting;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Writes a reaction to a MDL rxn or SDF file. Attention: Stoichiometric
 * coefficients have to be natural numbers.
 *
 * <pre>
 * MDLRXNWriter writer = new MDLRXNWriter(new FileWriter(new File("output.mol")));
 * writer.write((Molecule)molecule);
 * writer.close();
 * </pre>
 *
 * See {@cdk.cite DAL92}.
 *
 * @cdk.module io
 * @cdk.githash
 * @cdk.iooptions
 *
 * @cdk.keyword file format, MDL RXN file
 */
public class MDLRXNWriter extends DefaultChemObjectWriter {

    public static final String OptWriteAgents = "WriteAgents";

    private BooleanIOSetting writeAgents;

    private BufferedWriter      writer;
    private static ILoggingTool logger   = LoggingToolFactory.createLoggingTool(MDLRXNWriter.class);
    private int                 reactionNumber;
    public Map<String, Object>  rdFields = null;

    /**
     * Constructs a new MDLWriter that can write an array of
     * Molecules to a Writer.
     *
     * @param   out  The Writer to write to
     */
    public MDLRXNWriter(Writer out) {
        try {
            if (out instanceof BufferedWriter) {
                writer = (BufferedWriter) out;
            } else {
                writer = new BufferedWriter(out);
            }
        } catch (Exception exc) {
        }
        this.reactionNumber = 1;
        initIOSettings();
    }

    /**
     * Constructs a new MDLWriter that can write an array of
     * Molecules to a given OutputStream.
     *
     * @param   output  The OutputStream to write to
     */
    public MDLRXNWriter(OutputStream output) {
        this(new OutputStreamWriter(output));
    }

    public MDLRXNWriter() {
        this(new StringWriter());
    }

    private void initIOSettings() {
        writeAgents = addSetting(new BooleanIOSetting(OptWriteAgents,
                                                      IOSetting.Importance.LOW,
                                                      "Output agents in the RXN file",
                                                      "true"));
    }

    @Override
    public IResourceFormat getFormat() {
        return MDLFormat.getInstance();
    }

    @Override
    public void setWriter(Writer out) throws CDKException {
        if (out instanceof BufferedWriter) {
            writer = (BufferedWriter) out;
        } else {
            writer = new BufferedWriter(out);
        }
    }

    @Override
    public void setWriter(OutputStream output) throws CDKException {
        setWriter(new OutputStreamWriter(output));
    }

    /**
     * Here you can set a map which will be used to build rd fields in the file.
     * The entries will be translated to rd fields like this:<br>
     * &gt; &lt;key&gt;<br>
     * &gt; value<br>
     * empty line<br>
     *
     * @param  map The map to be used, map of String-String pairs
     */
    public void setRdFields(Map<String, Object> map) {
        rdFields = map;
    }

    /**
     * Flushes the output and closes this object.
     */
    @Override
    public void close() throws IOException {
        writer.close();
    }

    @Override
    public boolean accepts(Class<? extends IChemObject> classObject) {
        if (IReaction.class.equals(classObject)) return true;
        if (IReactionSet.class.equals(classObject)) return true;
        Class<?>[] interfaces = classObject.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            if (IReaction.class.equals(anInterface)) return true;
            if (IReactionSet.class.equals(anInterface)) return true;
        }
        Class superClass = classObject.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
        return false;
    }

    /**
     * Writes a IChemObject to the MDL RXN file formated output.
     * It can only output ChemObjects of type Reaction
     *
     * @param object class must be of type Molecule or MoleculeSet.
     *
     * @see org.openscience.cdk.ChemFile
     */
    @Override
    public void write(IChemObject object) throws CDKException {
        if (object instanceof IReactionSet) {
            writeReactionSet((IReactionSet) object);
        } else if (object instanceof IReaction) {
            writeReaction((IReaction) object);
        } else {
            throw new CDKException("Only supported is writing ReactionSet, Reaction objects.");
        }
    }

    /**
     *  Writes an array of Reaction to an OutputStream in MDL rdf format.
     *
     * @param   reactions  Array of Reactions that is written to an OutputStream
     */
    private void writeReactionSet(IReactionSet reactions) throws CDKException {

        for (IReaction iReaction : reactions.reactions()) {
            writeReaction(iReaction);
        }
    }

    /**
     * Writes a Reaction to an OutputStream in MDL sdf format.
     *
     * @param   reaction  A Reaction that is written to an OutputStream
     */
    private void writeReaction(IReaction reaction) throws CDKException {
        int reactantCount = reaction.getReactantCount();
        int productCount = reaction.getProductCount();
        int agentCount = reaction.getAgents().getAtomContainerCount();
        if (reactantCount + productCount + agentCount == 0) {
            // JWM: an empty record is still valid though..?!?
            throw new CDKException("Either no reactants or no products present.");
        }

        try {
            // taking care of the $$$$ signs:
            // we do not write such a sign at the end of the first reaction, thus we have to write on BEFORE the second reaction
            if (reactionNumber == 2) {
                writer.write("$$$$");
                writer.write('\n');
            }

            writer.write("$RXN");
            writer.write('\n');
            // reaction name
            String line = (String) reaction.getProperty(CDKConstants.TITLE);
            if (line == null) line = "";
            if (line.length() > 80) line = line.substring(0, 80);
            writer.write(line);
            writer.write('\n');
            // user/program/date&time/reaction registry no. line
            writer.write('\n');
            // comment line
            line = (String) reaction.getProperty(CDKConstants.REMARK);
            if (line == null) line = "";
            if (line.length() > 80) line = line.substring(0, 80);
            writer.write(line);
            writer.write('\n');

            line = "";
            line += formatMDLInt(reactantCount, 3);
            line += formatMDLInt(productCount, 3);
            if (agentCount > 0 && writeAgents.isSet())
                line += formatMDLInt(agentCount, 3);
            writer.write(line);
            writer.write('\n');

            int i = 0;
            for (IMapping mapping : reaction.mappings()) {
                Iterator<IChemObject> it = mapping.relatedChemObjects().iterator();
                it.next().setProperty(CDKConstants.ATOM_ATOM_MAPPING, i + 1);
                it.next().setProperty(CDKConstants.ATOM_ATOM_MAPPING, i + 1);
                i++;
            }
            writeAtomContainerSet(reaction.getReactants());
            writeAtomContainerSet(reaction.getProducts());
            if (agentCount > 0 && writeAgents.isSet())
                writeAtomContainerSet(reaction.getAgents());

            //write sdfields, if any
            if (rdFields != null) {
                Set<String> set = rdFields.keySet();
                Iterator<String> iterator = set.iterator();
                while (iterator.hasNext()) {
                    Object element = iterator.next();
                    writer.write("> <" + (String) element + ">");
                    writer.write('\n');
                    writer.write(rdFields.get(element).toString());
                    writer.write('\n');
                    writer.write('\n');
                }
            }
            // taking care of the $$$$ signs:
            // we write such a sign at the end of all except the first molecule
            if (reactionNumber != 1) {
                writer.write("$$$$");
                writer.write('\n');
            }
            reactionNumber++;

        } catch (IOException ex) {
            logger.error(ex.getMessage());
            logger.debug(ex);
            throw new CDKException("Exception while writing MDL file: " + ex.getMessage(), ex);
        }
    }

    /**
     * Writes a MoleculeSet to an OutputStream for the reaction.
     *
     * @param   som  The MoleculeSet that is written to an OutputStream
     */
    private void writeAtomContainerSet(IAtomContainerSet som) throws IOException, CDKException {

        for (int i = 0; i < som.getAtomContainerCount(); i++) {
            IAtomContainer mol = som.getAtomContainer(i);
            for (int j = 0; j < som.getMultiplier(i); j++) {
                StringWriter sw = new StringWriter();
                writer.write("$MOL");
                writer.write('\n');
                MDLV2000Writer mdlwriter = null;
                try {
                    mdlwriter = new MDLV2000Writer(sw);
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                    logger.debug(ex);
                    throw new CDKException("Exception while creating MDLWriter: " + ex.getMessage(), ex);
                }
                mdlwriter.write(mol);
                mdlwriter.close();
                writer.write(sw.toString());
            }
        }
    }

    /**
     * Formats an int to fit into the connectiontable and changes it
     * to a String.
     *
     * @param   i  The int to be formated
     * @param   l  Length of the String
     * @return     The String to be written into the connectiontable
     */
    private String formatMDLInt(int i, int l) {
        String s = "", fs = "";
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        nf.setParseIntegerOnly(true);
        nf.setMinimumIntegerDigits(1);
        nf.setMaximumIntegerDigits(l);
        nf.setGroupingUsed(false);
        s = nf.format(i);
        l = l - s.length();
        for (int f = 0; f < l; f++)
            fs += " ";
        fs += s;
        return fs;
    }

}
