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
 */
package org.openscience.cdk.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.SDFFormat;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.smiles.InvPair;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * Writes MDL SD files ({@cdk.cite DAL92}). A MDL SD file contains one or more molecules,
 * complemented by properties.
 *
 * @cdk.module  io
 * @cdk.githash
 * @cdk.iooptions
 * @cdk.keyword file format, MDL SD file
 */
public class SDFWriter extends DefaultChemObjectWriter {

    private final static ILoggingTool logger = LoggingToolFactory.createLoggingTool(SDFWriter.class);

    public static final String OptAlwaysV3000 = "writeV3000";
    public static final String OptWriteData = "writeProperties";
    public static final String OptTruncateLongData  = "TruncateLongData";
    public static final String SD_RECORD_DELIM = "$$$$";


    private BufferedWriter   writer;
    private BooleanIOSetting paramWriteData;
    private BooleanIOSetting paramWriteV3000;
    private BooleanIOSetting truncateData;
    private Set<String>      propertiesToWrite;

    /**
     * Create an SDfile writer that will output directly to the provided buffered writer.
     *
     * @param wtr writer
     */
    public SDFWriter(BufferedWriter wtr) {
        this.writer = wtr;
        initIOSettings();
    }

    /**
     * Create an SDfile writer, the provided writer is buffered
     * if it's not an instance of BufferedWriter. For flush control
     * etc please create with {@link BufferedWriter}.
     *
     * @param wtr writer
     */
    public SDFWriter(Writer wtr) {
        this(ensureBuffered(wtr));
        initIOSettings();
    }

    /**
     * Create an SDfile writer, the provided output stream is wrapped
     * in a UTF-8 buffered writer.
     *
     * @param output out stream
     */
    public SDFWriter(OutputStream output) {
        this(new OutputStreamWriter(output, StandardCharsets.UTF_8));
    }


    public SDFWriter() {
        this(new StringWriter());
    }

    /**
     * Constructs a new SDFWriter that writes to the given {@link Writer}.
     *
     * @param out The {@link Writer} to write to
     */
    public SDFWriter(Writer out, Set<String> propertiesToWrite) {
        this(out);
        initIOSettings();
        this.propertiesToWrite = propertiesToWrite;
    }

    /**
     * Constructs a new SdfWriter that can write to a given
     * {@link OutputStream}.
     *
     * @param output The {@link OutputStream} to write to
     */
    public SDFWriter(OutputStream output, Set<String> propertiesToWrite) {
        this(new OutputStreamWriter(output), propertiesToWrite);
    }

    /**
     * Writes SD-File to a String including the given properties
     */
    public SDFWriter(Set<String> propertiesToWrite) {
        this(new StringWriter(), propertiesToWrite);
    }

    /**
     * Ensures a writer is buffered.
     *
     * @param wtr writer, may be buffered
     * @return a BufferedWriter
     */
    private static BufferedWriter ensureBuffered(Writer wtr) {
        if (wtr == null) throw new NullPointerException("Provided writer was null");
        return wtr instanceof BufferedWriter ? (BufferedWriter) wtr
                                             : new BufferedWriter(wtr);
    }

    @Override
    public IResourceFormat getFormat() {
        return SDFFormat.getInstance();
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
     * Flushes the output and closes this object.
     */
    @Override
    public void close() throws IOException {
        writer.close();
    }

    @Override
    public boolean accepts(Class<? extends IChemObject> classObject) {
        Class<?>[] interfaces = classObject.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            if (IAtomContainer.class.equals(anInterface)) return true;
            if (IChemFile.class.equals(anInterface)) return true;
            if (IChemModel.class.equals(anInterface)) return true;
            if (IAtomContainerSet.class.equals(anInterface)) return true;
        }
        if (IAtomContainer.class.equals(classObject)) return true;
        if (IChemFile.class.equals(classObject)) return true;
        if (IChemModel.class.equals(classObject)) return true;
        if (IAtomContainerSet.class.equals(classObject)) return true;
        Class superClass = classObject.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
        return false;
    }

    /**
     * Writes a IChemObject to the MDL SD file formated output. It can only
     * output IChemObjects of type {@link IChemFile}, {@link IAtomContainerSet}
     * and {@link IAtomContainerSet}.
     *
     * @param object an acceptable {@link IChemObject}
     *
     * @see #accepts(Class)
     */
    @Override
    public void write(IChemObject object) throws CDKException {
        try {
            if (object instanceof IAtomContainerSet) {
                writeMoleculeSet((IAtomContainerSet) object);
                return;
            } else if (object instanceof IChemFile) {
                writeChemFile((IChemFile) object);
                return;
            } else if (object instanceof IChemModel) {
                writeChemModel((IChemModel) object);
                return;
            } else if (object instanceof IAtomContainer) {
                writeMolecule((IAtomContainer) object);
                return;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.debug(ex);
            throw new CDKException("Exception while writing MDL file: " + ex.getMessage(), ex);
        }
        throw new CDKException(
                "Only supported is writing of ChemFile, MoleculeSet, AtomContainer and Molecule objects.");
    }

    /**
     * Writes an {@link IAtomContainerSet}.
     *
     * @param   som  the {@link IAtomContainerSet} to serialize
     */
    private void writeMoleculeSet(IAtomContainerSet som) throws Exception {
        for (IAtomContainer mol : som.atomContainers()) {
            writeMolecule(mol);
        }
    }

    private void writeChemFile(IChemFile file) throws Exception {
        for (IAtomContainer container : ChemFileManipulator.getAllAtomContainers(file)) {
            writeMolecule(container);
        }
    }

    private void writeChemModel(IChemModel model) throws Exception {
        for (IAtomContainer container : ChemModelManipulator.getAllAtomContainers(model)) {
            writeMolecule(container);
        }
    }

    private static String replaceInvalidHeaderChars(String headerKey) {
        return headerKey.replaceAll("[-<>.=% ]", "_");
    }

    private void writeMolecule(IAtomContainer container) throws CDKException {
        try {
            // write the MDL molfile bits
            StringWriter stringWriter = new StringWriter();
            IChemObjectWriter mdlWriter;

            if (writeV3000(container))
                mdlWriter = new MDLV3000Writer(stringWriter);
            else
                mdlWriter = new MDLV2000Writer(stringWriter);

            mdlWriter.addSettings(getSettings());
            mdlWriter.write(container);
            mdlWriter.close();

            // write non-structural data (mol properties in our case)
            if (paramWriteData.isSet()) {
                MDLV2000Writer.writeNonStructuralData(stringWriter,
                                                      container,
                                                      cdkInternalProperties,
                                                      propertiesToWrite,
                                                      truncateData.isSet());
            }
            stringWriter.append(SD_RECORD_DELIM)
                        .append('\n');

            writer.write(stringWriter.toString());
        } catch (IOException exception) {
            throw new CDKException("Error while writing a SD file entry: " + exception.getMessage(), exception);
        }
    }


    private boolean writeV3000(IAtomContainer container) {
        if (paramWriteV3000.isSet())
            return true;
        if (container.getAtomCount() > 999)
            return true;
        if (container.getBondCount() > 999)
            return true;

        // enhanced stereo check, if every tetrahedral element is Absolute (ABS) or in the same Racemic (RAC) group then
        // we can use V2000
        boolean init     = false;
        int     grp      = 0;
        for (IStereoElement<?,?> se : container.stereoElements()) {
            if (se.getConfigClass() == IStereoElement.TH) {
                if (!init) {
                    init = true;
                    grp = se.getGroupInfo();
                } else if (grp != se.getGroupInfo()) {
                    // >1 group types e.g. &1 &2, &1 or1 etc, use V3000
                    return true;
                }
            }
        }

        // original V2000 didn't distinguish racemic and relative stereo (flag=0) however
        // MDL/Accelrys/BIOVIA decided these should be read as racemic, so even if all
        if ((grp & IStereoElement.GRP_TYPE_MASK) == IStereoElement.GRP_REL)
            return true;

        // check for positional variation, this can be output in V3000 and not V2000
        List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);
        if (sgroups != null) {
            for (Sgroup sgroup : sgroups)
                if (sgroup.getType() == SgroupType.ExtMulticenter)
                    return true;
        }
        return false;
    }

    /**
     * A list of properties used by CDK algorithms which must never be
     * serialized into the SD file format.
     */
    private static final Set<String> cdkInternalProperties = new HashSet<>();

    static {
        cdkInternalProperties.add(InvPair.CANONICAL_LABEL);
        cdkInternalProperties.add(InvPair.INVARIANCE_PAIR);
        cdkInternalProperties.add(CDKConstants.CTAB_SGROUPS);
        // TITLE/REMARK written in Molfile header
        cdkInternalProperties.add(CDKConstants.REMARK);
        cdkInternalProperties.add(CDKConstants.TITLE);
        // I think there are a few more, but cannot find them right now
    }

    private boolean isCDKInternalProperty(Object propKey) {
        return cdkInternalProperties.contains(propKey);
    }

    private void initIOSettings() {
        paramWriteData = addSetting(new BooleanIOSetting(OptWriteData,
                                                         IOSetting.Importance.LOW,
                                                         "Should molecule properties be written as non-structural data", "true"));
        paramWriteV3000 = addSetting(new BooleanIOSetting(OptAlwaysV3000,
                                                          IOSetting.Importance.LOW,
                                                          "Write all records as V3000", "false"));
        truncateData = addSetting(new BooleanIOSetting(OptTruncateLongData,
                                                       IOSetting.Importance.LOW,
                                                       "Truncate long data files >200 characters", "false"));
        try (MDLV2000Writer mdlv2 = new MDLV2000Writer();
             MDLV3000Writer mdlv3 = new MDLV3000Writer()) {
            addSettings(mdlv2.getSettings());
            addSettings(mdlv3.getSettings());
        } catch (IOException ignored) {
        }
    }

    public void setAlwaysV3000(boolean val) {
        try {
            paramWriteV3000.setSetting(Boolean.toString(val));
        } catch (CDKException e) {
            // ignored, no type unsafety
            throw new InternalError(e.getMessage());
        }
    }

    public void customizeJob() {
        for (IOSetting setting : getSettings()) {
            fireIOSettingQuestion(setting);
        }
    }

    public void flush() throws IOException {
	        writer.flush();
    }

}
