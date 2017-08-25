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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.RGroupQueryFormat;
import org.openscience.cdk.isomorphism.matchers.IRGroupQuery;
import org.openscience.cdk.isomorphism.matchers.RGroup;
import org.openscience.cdk.isomorphism.matchers.RGroupList;
import org.openscience.cdk.isomorphism.matchers.RGroupQuery;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * A reader for Symyx' Rgroup files (RGFiles).
 * An RGfile describes a single molecular query with Rgroups.
 * Each RGfile is a combination of Ctabs defining the root molecule and each
 * member of each Rgroup in the query.
 *
 * <p>The RGFile format is described in the manual
 * <a href="http://www.symyx.com/downloads/public/ctfile/ctfile.pdf">
 * "CTFile Formats"</a> , Chapter 5.
 *
 * @cdk.module io
 * @cdk.githash
 * @cdk.iooptions
 *
 * @cdk.keyword Rgroup
 * @cdk.keyword R group
 * @cdk.keyword R-group
 * @author Mark Rijnbeek
 */
public class RGroupQueryReader extends DefaultChemObjectReader {

    /**
     * Private bean style class to capture LOG (logic) lines.
     */
    private class RGroupLogic {

        int     rgoupNumberRequired;
        boolean restH;
        String  occurence;
    }

    BufferedReader              input  = null;
    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(RGroupQueryReader.class);

    /**
     * Default constructor, input not set.
     */
    public RGroupQueryReader() {
        this(new StringReader(""));
    }

    /**
     * Constructs a new RgroupQueryReader that can read RgroupAtomContainerSet
     * from a given InputStream.
     * @param in The InputStream to read from.
     */
    public RGroupQueryReader(InputStream in) {
        this(new InputStreamReader(in));
    }

    /**
     * Constructs a new RgroupQueryReader that can read RgroupAtomContainerSet
     * from a given Reader.
     * @param  in  The Reader to read from.
     */
    public RGroupQueryReader(Reader in) {
        input = new BufferedReader(in);
    }

    /**
     * Sets the input Reader.
     * @param input Reader object
     * @throws CDKException
     */
    @Override
    public void setReader(Reader input) throws CDKException {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader) input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    @Override
    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

    @Override
    public IResourceFormat getFormat() {
        return RGroupQueryFormat.getInstance();
    }

    @Override
    public boolean accepts(Class<? extends IChemObject> classObject) {
        Class<?>[] interfaces = classObject.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            if (IRGroupQuery.class.equals(anInterface)) return true;
        }
        Class superClass = classObject.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
        return false;
    }

    @Override
    public void close() throws IOException {
        input.close();
    }

    /**
     * Check input IChemObject and proceed to parse.
     * Accepts/returns IChemObject of type RGroupQuery only.
     * @return IChemObject read from file
     * @param object class must be of type RGroupQuery
     */
    @Override
    public <T extends IChemObject> T read(T object) throws CDKException {
        if (object instanceof RGroupQuery) {
            return (T) parseRGFile((RGroupQuery) object);
        } else {
            throw new CDKException("Reader only supports " + RGroupQuery.class.getName() + " objects");
        }
    }

    /**
     * Parse the RGFile. Uses of {@link org.openscience.cdk.io.MDLV2000Reader}
     * to parse individual $CTAB blocks.
     *
     * @param rGroupQuery empty
     * @return populated query
     * @throws CDKException
     */
    private RGroupQuery parseRGFile(RGroupQuery rGroupQuery) throws CDKException {
        IChemObjectBuilder defaultChemObjectBuilder = rGroupQuery.getBuilder();
        String line = "";
        int lineCount = 0;
        String eol = "\n";
        StringTokenizer strTk = null;
        /* Variable to capture the LOG line(s) */
        Map<Integer, RGroupLogic> logicDefinitions = new HashMap<Integer, RGroupLogic>();

        /*
         * Variable to captures attachment order for Rgroups. Contains: - pseudo
         * atom (Rgroup) - map with (integer,bond) meaning "bond" has attachment
         * order "integer" (1,2,3) for the Rgroup The order is based on the atom
         * block, unless there is an AAL line for the pseudo atom.
         */
        Map<IAtom, Map<Integer, IBond>> attachmentPoints = new HashMap<IAtom, Map<Integer, IBond>>();

        try {
            // Process the Header block_________________________________________
            //__________________________________________________________________
            logger.info("Process the Header block");
            checkLineBeginsWith(input.readLine(), "$MDL", ++lineCount);
            checkLineBeginsWith(input.readLine(), "$MOL", ++lineCount);
            checkLineBeginsWith(input.readLine(), "$HDR", ++lineCount);

            for (int i = 1; i <= 3; i++) {
                lineCount++;
                if (input.readLine() == null) {
                    throw new CDKException("RGFile invalid, empty/null header line at #" + lineCount);
                }
                //optional: parse header info here (not implemented)
            }
            checkLineBeginsWith(input.readLine(), "$END HDR", ++lineCount);

            //Process the root structure (scaffold)_____________________________
            //__________________________________________________________________
            logger.info("Process the root structure (scaffold)");
            checkLineBeginsWith(input.readLine(), "$CTAB", ++lineCount);
            //Force header
            StringBuilder sb = new StringBuilder(RGroup.ROOT_LABEL + "\n\n\n");
            line = input.readLine();
            ++lineCount;
            while (line != null && !line.equals("$END CTAB")) {
                sb.append(line + eol);

                //LOG lines: Logic, Unsatisfied Sites, Range of Occurrence.
                if (line.startsWith("M  LOG")) {
                    strTk = new StringTokenizer(line);
                    strTk.nextToken();
                    strTk.nextToken();
                    strTk.nextToken();
                    RGroupLogic log = null;

                    log = new RGroupLogic();
                    int rgroupNumber = Integer.valueOf(strTk.nextToken());
                    String tok = strTk.nextToken();
                    log.rgoupNumberRequired = tok.equals("0") ? 0 : Integer.valueOf(tok);
                    log.restH = strTk.nextToken().equals("1") ? true : false;
                    tok = "";
                    while (strTk.hasMoreTokens()) {
                        tok += strTk.nextToken();
                    }
                    log.occurence = tok;
                    logicDefinitions.put(rgroupNumber, log);
                }

                line = input.readLine();
                ++lineCount;
            }
            String rootStr = sb.toString();

            //Let MDL reader process $CTAB block of the root structure.
            MDLV2000Reader reader = new MDLV2000Reader(new StringReader(rootStr), ISimpleChemObjectReader.Mode.STRICT);
            IAtomContainer root = reader.read(defaultChemObjectBuilder.newInstance(IAtomContainer.class));
            rGroupQuery.setRootStructure(root);

            //Atom attachment order: parse AAL lines first
            strTk = new StringTokenizer(rootStr, eol);
            while (strTk.hasMoreTokens()) {
                line = strTk.nextToken();
                if (line.startsWith("M  AAL")) {
                    StringTokenizer stAAL = new StringTokenizer(line);
                    stAAL.nextToken();
                    stAAL.nextToken();
                    int pos = Integer.valueOf(stAAL.nextToken());
                    IAtom rGroup = root.getAtom(pos - 1);
                    stAAL.nextToken();
                    Map<Integer, IBond> bondMap = new HashMap<Integer, IBond>();
                    while (stAAL.hasMoreTokens()) {
                        pos = Integer.valueOf(stAAL.nextToken());
                        IAtom partner = root.getAtom(pos - 1);
                        IBond bond = root.getBond(rGroup, partner);
                        int order = Integer.valueOf(stAAL.nextToken());
                        bondMap.put(order, bond);
                        logger.info("AAL " + order + " " + ((IPseudoAtom) rGroup).getLabel() + "-"
                                + partner.getSymbol());
                    }
                    if (bondMap.size() != 0) {
                        attachmentPoints.put(rGroup, bondMap);
                    }

                }
            }
            //Deal with remaining attachment points (non AAL)
            for (IAtom atom : root.atoms()) {
                if (atom instanceof IPseudoAtom) {
                    IPseudoAtom rGroup = (IPseudoAtom) atom;
                    if (rGroup.getLabel().startsWith("R") && !rGroup.getLabel().equals("R") && // only numbered ones
                            !attachmentPoints.containsKey(rGroup)) {
                        //Order reflects the order of atoms in the Atom Block
                        int order = 0;
                        Map<Integer, IBond> bondMap = new HashMap<Integer, IBond>();
                        for (IAtom atom2 : root.atoms()) {
                            if (!atom.equals(atom2)) {
                                for (IBond bond : root.bonds()) {
                                    if (bond.contains(atom) && bond.contains(atom2)) {
                                        bondMap.put(++order, bond);
                                        logger.info("Def " + order + " " + rGroup.getLabel() + "-" + atom2.getSymbol());
                                        break;
                                    }
                                }
                            }
                        }
                        if (bondMap.size() != 0) {
                            attachmentPoints.put(rGroup, bondMap);
                        }
                    }
                }
            }
            //Done with attachment points
            rGroupQuery.setRootAttachmentPoints(attachmentPoints);
            logger.info("Attachm.points defined for " + attachmentPoints.size() + " R# atoms");

            //Process each Rgroup's $CTAB block(s)_____________________________
            //__________________________________________________________________

            //Set up the RgroupLists, one for each unique R# (# = 1..32 max)
            Map<Integer, RGroupList> rGroupDefinitions = new HashMap<Integer, RGroupList>();

            for (IAtom atom : root.atoms()) {
                if (atom instanceof IPseudoAtom) {
                    IPseudoAtom rGroup = (IPseudoAtom) atom;
                    if (RGroupQuery.isValidRgroupQueryLabel(rGroup.getLabel())) {
                        int rgroupNum = Integer.valueOf(rGroup.getLabel().substring(1));
                        RGroupList rgroupList = new RGroupList(rgroupNum);
                        if (!rGroupDefinitions.containsKey(rgroupNum)) {
                            logger.info("Define Rgroup R" + rgroupNum);
                            RGroupLogic logic = logicDefinitions.get(rgroupNum);
                            if (logic != null) {
                                rgroupList.setRestH(logic.restH);
                                rgroupList.setOccurrence(logic.occurence);
                                rgroupList.setRequiredRGroupNumber(logic.rgoupNumberRequired);
                            } else {
                                rgroupList.setRestH(false);
                                rgroupList.setOccurrence(">0");
                                rgroupList.setRequiredRGroupNumber(0);
                            }
                            rgroupList.setRGroups(new ArrayList<RGroup>());
                            rGroupDefinitions.put(rgroupNum, rgroupList);
                        }
                    }
                }
            }

            //Parse all $CTAB blocks per Rgroup (there can be more than one)
            line = input.readLine();
            ++lineCount;
            boolean hasMoreRGP = true;
            while (hasMoreRGP) {

                checkLineBeginsWith(line, "$RGP", lineCount);
                line = input.readLine();
                ++lineCount;
                logger.info("line for num is " + line);
                int rgroupNum = Integer.valueOf(line.trim());
                line = input.readLine();
                ++lineCount;

                boolean hasMoreCTAB = true;
                while (hasMoreCTAB) {

                    checkLineBeginsWith(line, "$CTAB", lineCount);
                    sb = new StringBuilder(RGroup.makeLabel(rgroupNum) + "\n\n\n");
                    line = input.readLine();
                    while (line != null && !line.startsWith("$END CTAB")) {
                        sb.append(line + eol);
                        line = input.readLine();
                        ++lineCount;
                    }
                    String groupStr = sb.toString();
                    reader = new MDLV2000Reader(new StringReader(groupStr), ISimpleChemObjectReader.Mode.STRICT);
                    IAtomContainer group = reader.read(defaultChemObjectBuilder.newInstance(IAtomContainer.class));
                    RGroup rGroup = new RGroup();
                    rGroup.setGroup(group);

                    //Parse the Rgroup's attachment points (APO)
                    strTk = new StringTokenizer(groupStr, eol);
                    while (strTk.hasMoreTokens()) {
                        line = strTk.nextToken();
                        if (line.startsWith("M  APO")) {
                            StringTokenizer stAPO = new StringTokenizer(line);
                            stAPO.nextToken();
                            stAPO.nextToken();
                            stAPO.nextToken();
                            while (stAPO.hasMoreTokens()) {
                                int pos = Integer.valueOf(stAPO.nextToken());
                                int apo = Integer.valueOf(stAPO.nextToken());
                                IAtom at = group.getAtom(pos - 1);
                                switch (apo) {
                                    case 1:
                                        rGroup.setFirstAttachmentPoint(at);
                                        break;
                                    case 2:
                                        rGroup.setSecondAttachmentPoint(at);
                                        break;
                                    case 3: {
                                        rGroup.setFirstAttachmentPoint(at);
                                        rGroup.setSecondAttachmentPoint(at);
                                    }
                                        break;
                                }
                            }
                        }
                    }
                    RGroupList rList = rGroupDefinitions.get(rgroupNum);
                    if (rList == null) {
                        throw new CDKException("R" + rgroupNum + " not defined but referenced in $RGP.");
                    } else {
                        rList.getRGroups().add(rGroup);
                    }
                    line = input.readLine();
                    ++lineCount;
                    if (line.startsWith("$END RGP")) {
                        logger.info("end of RGP block");
                        hasMoreCTAB = false;
                    }
                }

                line = input.readLine();
                ++lineCount;
                if (line.startsWith("$END MOL")) {
                    hasMoreRGP = false;
                }
            }

            rGroupQuery.setRGroupDefinitions(rGroupDefinitions);
            logger.info("Number of lines was " + lineCount);
            return rGroupQuery;

        } catch (CDKException exception) {
            String error = "CDK Error while parsing line " + lineCount + ": " + line + " -> " + exception.getMessage();
            logger.error(error);
            logger.debug(exception);
            throw exception;
        } catch (IOException | IllegalArgumentException exception) {
            exception.printStackTrace();
            String error = exception.getClass() + "Error while parsing line " + lineCount + ": " + line + " -> "
                    + exception.getMessage();
            logger.error(error);
            logger.debug(exception);
            throw new CDKException(error, exception);
        }
    }

    /**
     * Checks that a given line starts as expected, according to RGFile format.
     * @param line
     * @param expect
     * @param lineCount
     * @throws CDKException
     */
    private void checkLineBeginsWith(String line, String expect, int lineCount) throws CDKException {
        if (line == null) {
            throw new CDKException("RGFile invalid, empty/null line at #" + lineCount);
        }
        if (!line.startsWith(expect)) {
            throw new CDKException("RGFile invalid, line #" + lineCount + " should start with:" + expect + ".");
        }
    }

}
