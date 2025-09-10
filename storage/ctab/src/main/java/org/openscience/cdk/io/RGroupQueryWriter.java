/*
 * Copyright (C) 2010  Mark Rijnbeek <mark_rynbeek@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may
 * distribute with programs based on this work.
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.RGroupQueryFormat;
import org.openscience.cdk.isomorphism.matchers.IRGroup;
import org.openscience.cdk.isomorphism.matchers.IRGroupList;
import org.openscience.cdk.isomorphism.matchers.IRGroupQuery;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * A writer for Symyx' Rgroup files (RGFiles).<br>
 * An RGfile describes a single molecular query with Rgroups.
 * Each RGfile is a combination of Ctabs defining the root molecule and each
 * member of each Rgroup in the query.
 * <br>
 * This class relies on the {@link org.openscience.cdk.io.MDLV2000Writer} to
 * create CTAB data blocks.
 *
 * @cdk.iooptions
 * @cdk.keyword Rgroup
 * @cdk.keyword R group
 * @cdk.keyword R-group
 * @author Mark Rijnbeek
 */

public class RGroupQueryWriter extends DefaultChemObjectWriter {

    private static final String M_APO = "M  APO";
    private static final String END_RGP = "$END RGP";
    private static final String END_MOL = "$END MOL";
    private static final String END_CTAB = "$END CTAB";
    private static final String LINE_SEP = "\n";
    public static final String M_END = "M  END";

    private BufferedWriter writer;

    /**
     * Constructs a new writer that can write an {@link IRGroupQuery}
     * to the Symx RGFile format.
     *
     * @param   out  The Writer to write to
     */
    public RGroupQueryWriter(Writer out) {
        if (out instanceof BufferedWriter) {
            writer = (BufferedWriter) out;
        } else {
            writer = new BufferedWriter(out);
        }
    }

    /**
     * Zero argument constructor.
     */
    public RGroupQueryWriter() {
        this(new StringWriter());
    }

    /**
     * Returns true for accepted input types.
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean accepts(Class<? extends IChemObject> classObject) {
        if (IRGroupQuery.class.equals(classObject)) return true;
        Class<?>[] interfaces = classObject.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            if (IRGroupQuery.class.equals(anInterface)) return true;
        }
        Class superClass = classObject.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
        return false;
    }

    /**
     * Flushes the output and closes this object.
     */
    @Override
    public void close() throws IOException {
        writer.close();
    }

    private static void adjustImplH(IAtom atom, int val) {
        Integer hcnt = atom.getImplicitHydrogenCount();
        if (hcnt == null) return;
        atom.setImplicitHydrogenCount(hcnt + val);
    }

    private static boolean isAttachmentPoint(IAtom atom) {
        if (atom.getAtomicNumber() != IElement.Wildcard)
            return false;
        return atom instanceof IPseudoAtom &&
               ((IPseudoAtom) atom).getAttachPointNum() != 0;
    }

    private void adjustAttachmentValence(IAtomContainer mol, int sign) {
        if (sign != -1 && sign != +1)
            throw new IllegalArgumentException("Sign should be -1/+1 only!");
        for (IBond bond : mol.bonds()) {
            if (isAttachmentPoint(bond.getBegin()))
                adjustImplH(bond.getEnd(), sign * bond.getOrder().numeric());
            else if (isAttachmentPoint(bond.getEnd()))
                adjustImplH(bond.getBegin(), sign * bond.getOrder().numeric());
        }
    }

    /**
     * RGrp files don't write explicit attachment points so we strip these out
     * as a shallow copy before writing.
     *
     * @param mol the molecule to copy
     * @return a copy (or the input if no attachment points)
     */
    private IAtomContainer copyWithoutExplicitAttachments(IAtomContainer mol) {
        boolean strip = false;
        for (IAtom atom : mol.atoms()) {
            if (isAttachmentPoint(atom)) {
                strip = true;
                break;
            }
        }
        if (!strip) return mol;
        IAtomContainer cpy = mol.getBuilder().newAtomContainer();
        AtomContainerManipulator.copy(cpy, mol, a -> !isAttachmentPoint(a));
        return cpy;
    }


    /**
     * Produces a CTAB block for an atomContainer, without the header lines.
     * @param atomContainer
     * @return CTAB block
     * @throws CDKException
     */
    private String getCTAB(IAtomContainer atomContainer) throws CDKException {
        StringWriter strWriter = new StringWriter();
        MDLV2000Writer mdlWriter = new MDLV2000Writer(strWriter);
        mdlWriter.write(atomContainer);
        try {
            mdlWriter.close();
        } catch (IOException exception) {
            // FIXME
        }
        String ctab = strWriter.toString();
        //strip of the individual header, as we have one super header instead.
        for (int line = 1; line <= 3; line++) {
            ctab = ctab.substring(ctab.indexOf(LINE_SEP) + (LINE_SEP.length()));
        }
        return ctab;
    }

    /**
     * Returns output format.
     */
    @Override
    public IResourceFormat getFormat() {
        return RGroupQueryFormat.getInstance();
    }

    /**
     * Sets the writer to given output stream.
     */
    @Override
    public void setWriter(OutputStream output) throws CDKException {
        setWriter(new OutputStreamWriter(output));
    }

    /**
     * Sets the writer.
     */
    @Override
    public void setWriter(Writer out) throws CDKException {
        if (out instanceof BufferedWriter) {
            writer = (BufferedWriter) out;
        } else {
            writer = new BufferedWriter(out);
        }
    }

    /**
     * The actual writing of the output.
     * @throws CDKException could not write RGroup query
     */
    @Override
    public void write(IChemObject object) throws CDKException {
        if (!(object instanceof IRGroupQuery)) {
            throw new CDKException("Only IRGroupQuery input is accepted.");
        }
        try {

            IRGroupQuery rGroupQuery = (IRGroupQuery) object;
            String now = new SimpleDateFormat("MMddyyHHmm").format(System.currentTimeMillis());
            IAtomContainer rootAtc = rGroupQuery.getRootStructure();

            //Construct header
            StringBuilder rootBlock = new StringBuilder();
            String header = "$MDL  REV  1   " + now + LINE_SEP + "$MOL" + LINE_SEP + "$HDR" + LINE_SEP
                            + "  Rgroup query file (RGFile)" + LINE_SEP + "  CDK    " + now + "2D" + LINE_SEP + LINE_SEP + "$END HDR"
                            + LINE_SEP + "$CTAB";
            rootBlock.append(header).append(LINE_SEP);

            //Construct the root structure, the scaffold
            String rootCTAB = getCTAB(rootAtc);
            rootCTAB = rootCTAB.replaceAll(LINE_SEP + M_END + LINE_SEP, "");
            rootBlock.append(rootCTAB).append(LINE_SEP);

            //Write the root's LOG lines
            for (Integer rgrpNum : rGroupQuery.getRGroupDefinitions().keySet()) {
                IRGroupList rgList = rGroupQuery.getRGroupDefinitions().get(rgrpNum);
                int restH = rgList.isRestH() ? 1 : 0;
                String logLine = "M  LOG" + MDLV2000Writer.formatMDLInt(1, 3) + MDLV2000Writer.formatMDLInt(rgrpNum, 4)
                        + MDLV2000Writer.formatMDLInt(rgList.getRequiredRGroupNumber(), 4)
                        + MDLV2000Writer.formatMDLInt(restH, 4) + "   " + rgList.getOccurrence();
                rootBlock.append(logLine).append(LINE_SEP);
            }

            //AAL lines are optional, they are needed for R-atoms with multiple bonds to the root
            //for which the order of the attachment points can not be implicitly derived
            //from the order in the atom block. See CT spec for more on that.
            for (IAtom rgroupAtom : rGroupQuery.getRootAttachmentPoints().keySet()) {
                Map<Integer, IBond> rApo = rGroupQuery.getRootAttachmentPoints().get(rgroupAtom);
                if (rApo.size() > 1) {
                    int prevPos = -1;
                    int apoIdx = 1;
                    boolean implicitlyOrdered = true;
                    while (rApo.get(apoIdx) != null && implicitlyOrdered) {
                        IAtom partner = rApo.get(apoIdx).getOther(rgroupAtom);
                        for (int atIdx = 0; atIdx < rootAtc.getAtomCount(); atIdx++) {
                            if (rootAtc.getAtom(atIdx).equals(partner)) {
                                if (atIdx < prevPos) implicitlyOrdered = false;
                                prevPos = atIdx;
                                break;
                            }
                        }
                        apoIdx++;
                    }
                    if (!implicitlyOrdered) {
                        StringBuilder aalLine = new StringBuilder("M  AAL");
                        for (int atIdx = 0; atIdx < rootAtc.getAtomCount(); atIdx++) {
                            if (rootAtc.getAtom(atIdx).equals(rgroupAtom)) {
                                aalLine.append(MDLV2000Writer.formatMDLInt((atIdx + 1), 4));
                                aalLine.append(MDLV2000Writer.formatMDLInt(rApo.size(), 3));

                                apoIdx = 1;
                                while (rApo.get(apoIdx) != null) {
                                    IAtom partner = rApo.get(apoIdx).getOther(rgroupAtom);

                                    for (int a = 0; a < rootAtc.getAtomCount(); a++) {
                                        if (rootAtc.getAtom(a).equals(partner)) {
                                            aalLine.append(MDLV2000Writer.formatMDLInt(a + 1, 4));
                                            aalLine.append(MDLV2000Writer.formatMDLInt(apoIdx, 4));
                                        }
                                    }
                                    apoIdx++;
                                }
                            }
                        }
                        rootBlock.append(aalLine).append(LINE_SEP);
                    }
                }
            }

            rootBlock.append(M_END).append(LINE_SEP).append(END_CTAB).append(LINE_SEP);

            //Construct each R-group block
            StringBuilder rgpBlock = new StringBuilder();
            for (Integer rgrpNum : rGroupQuery.getRGroupDefinitions().keySet()) {
                List<IRGroup> rgrpList = rGroupQuery.getRGroupDefinitions().get(rgrpNum).getRGroups();
                if (rgrpList != null && rgrpList.size() != 0) {
                    rgpBlock.append("$RGP").append(LINE_SEP);
                    rgpBlock.append(MDLV2000Writer.formatMDLInt(rgrpNum, 4)).append(LINE_SEP);

                    for (IRGroup rgroup : rgrpList) {
                        //CTAB block
                        rgpBlock.append("$CTAB").append(LINE_SEP);

                        adjustAttachmentValence(rgroup.getGroup(), +1);
                        IAtomContainer group = copyWithoutExplicitAttachments(rgroup.getGroup());
                        String ctab = getCTAB(group);
                        adjustAttachmentValence(rgroup.getGroup(), -1);

                        ctab = ctab.replaceAll(LINE_SEP + M_END + LINE_SEP, "");
                        rgpBlock.append(ctab).append(LINE_SEP);

                        //The APO line
                        IAtom firstAttachmentPoint = rgroup.getFirstAttachmentPoint();
                        IAtom secondAttachmentPoint = rgroup.getSecondAttachmentPoint();
                        int apoCount = 0;
                        if (firstAttachmentPoint != null) {
                            StringBuilder apoLine = new StringBuilder();
                            for (int atIdx = 0; atIdx < group.getAtomCount(); atIdx++) {
                                if (group.getAtom(atIdx).equals(firstAttachmentPoint)) {
                                    apoLine.append(MDLV2000Writer.formatMDLInt((atIdx + 1), 4));
                                    apoCount++;
                                    if (secondAttachmentPoint != null
                                            && secondAttachmentPoint.equals(firstAttachmentPoint)) {
                                        apoLine.append(MDLV2000Writer.formatMDLInt(3, 4));
                                    } else {
                                        apoLine.append(MDLV2000Writer.formatMDLInt(1, 4));
                                    }
                                }
                            }
                            if (secondAttachmentPoint != null && !secondAttachmentPoint.equals(firstAttachmentPoint)) {
                                for (int atIdx = 0; atIdx < group.getAtomCount(); atIdx++) {
                                    if (group.getAtom(atIdx).equals(secondAttachmentPoint)) {
                                        apoCount++;
                                        apoLine.append(MDLV2000Writer.formatMDLInt((atIdx + 1), 4));
                                        apoLine.append(MDLV2000Writer.formatMDLInt(2, 4));
                                    }
                                }
                            }
                            if (apoCount > 0) {
                                apoLine.insert(0, M_APO + MDLV2000Writer.formatMDLInt(apoCount, 3));
                                rgpBlock.append(apoLine).append(LINE_SEP);
                            }
                        }

                        rgpBlock.append(M_END).append(LINE_SEP);
                        rgpBlock.append(END_CTAB).append(LINE_SEP);
                    }
                    rgpBlock.append(END_RGP).append(LINE_SEP);
                }
            }
            rgpBlock.append(END_MOL).append(LINE_SEP);

            writer.write(rootBlock.toString());
            writer.write(rgpBlock.toString());
            writer.flush();

        } catch (IOException e) {
            throw new CDKException("Unexpected exception when writing RGFile" + LINE_SEP + e.getMessage());
        }

    }
}
