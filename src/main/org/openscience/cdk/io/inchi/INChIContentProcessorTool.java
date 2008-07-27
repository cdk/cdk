/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io.inchi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Tool to help process INChI 1.12beta content.
 *
 * @cdk.module extra
 * @cdk.svnrev  $Revision$
 */
public class INChIContentProcessorTool {

    private LoggingTool logger;
    
    public INChIContentProcessorTool() {
        logger = new LoggingTool(this);
    }

    /**
     * Processes the content from the formula field of the INChI.
     * Typical values look like C6H6, from INChI=1.12Beta/C6H6/c1-2-4-6-5-3-1/h1-6H.
     */
    public IAtomContainer processFormula(IAtomContainer parsedContent, String atomsEncoding) {
        logger.debug("Parsing atom data: ", atomsEncoding);

        Pattern pattern = Pattern.compile("([A-Z][a-z]?)(\\d+)?(.*)");
        String remainder = atomsEncoding;
        while (remainder.length() > 0) {
            logger.debug("Remaining: ", remainder);
            Matcher matcher = pattern.matcher(remainder);
            if (matcher.matches()) {
                String symbol = matcher.group(1);
                logger.debug("Atom symbol: ", symbol);
                if (symbol.equals("H")) {
                    // don't add explicit hydrogens
                } else {
                    String occurenceStr = matcher.group(2);
                    int occurence = 1;
                    if (occurenceStr != null) {
                        occurence = Integer.parseInt(occurenceStr);
                    }
                    logger.debug("  occurence: ", occurence);
                    for (int i=1; i<=occurence; i++) {
                        parsedContent.addAtom(parsedContent.getBuilder().newAtom(symbol));
                    }
                }
                remainder = matcher.group(3);
                if (remainder == null) remainder = "";
                logger.debug("  Remaining: ", remainder);
            } else {
                logger.error("No match found!");
                remainder = "";
            }
            logger.debug("NO atoms: ", parsedContent.getAtomCount());
        }
        return parsedContent;
    }

    /**
     * Processes the content from the connections field of the INChI.
     * Typical values look like 1-2-4-6-5-3-1, from INChI=1.12Beta/C6H6/c1-2-4-6-5-3-1/h1-6H.
     *
     * @param bondsEncoding the content of the INChI connections field
     * @param container     the atomContainer parsed from the formula field
     * @param source        the atom to build the path upon. If -1, then start new path
     *
     * @see   #processFormula
     */
    public void processConnections(String bondsEncoding, 
                     IAtomContainer container, int source){
        logger.debug("Parsing bond data: ", bondsEncoding);

        IBond bondToAdd = null;
        /* Fixme: treatment of branching is too limited! */
        String remainder = bondsEncoding;
        while (remainder.length() > 0) {
            logger.debug("Bond part: ", remainder);
            if (remainder.charAt(0) == '(') {
                String branch = chopBranch(remainder);
                processConnections(branch, container, source);
                if (branch.length()+2 <= remainder.length()) {
                    remainder = remainder.substring(branch.length()+2);
                } else {
                    remainder = "";
                }
            } else {
                Pattern pattern = Pattern.compile("^(\\d+)-?(.*)");
                Matcher matcher = pattern.matcher(remainder);
                if (matcher.matches()) {
                    String targetStr = matcher.group(1);
                    int target = Integer.parseInt(targetStr);
                    logger.debug("Source atom: ", source);
                    logger.debug("Target atom: ", targetStr);
                    IAtom targetAtom = container.getAtom(target-1);
                    if (source != -1) {
                    	IAtom sourceAtom = container.getAtom(source-1);
                        bondToAdd = container.getBuilder().newBond(sourceAtom, targetAtom, IBond.Order.SINGLE);
                        container.addBond(bondToAdd);
                    }
                    remainder = matcher.group(2);
                    source = target;
                    logger.debug("  remainder: ", remainder);
                } else {
                    logger.error("Could not get next bond info part");
                    return;
                }
            }
        }
    }

    /**
     * Extracts the first full branch. It extracts everything between the first
     * '(' and the corresponding ')' char.
     */
    private String chopBranch(String remainder) {
        boolean doChop = false;
        int branchLevel = 0;
        StringBuffer choppedString = new StringBuffer();
        for (int i=0; i<remainder.length(); i++) {
            char currentChar = remainder.charAt(i);
            if (currentChar == '(') {
                if (doChop) choppedString.append(currentChar);
                doChop = true;
                branchLevel++;
            } else if (currentChar == ')') {
                branchLevel--;
                if (branchLevel == 0) doChop = false;
                if (doChop) choppedString.append(currentChar);
            } else if (doChop) {
                choppedString.append(currentChar);
            }
        }
        return choppedString.toString();
    }

}
