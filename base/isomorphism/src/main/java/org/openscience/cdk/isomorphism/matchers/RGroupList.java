/*
 * Copyright (C) 2010  Mark Rijnbeek <mark_rynbeek@users.sf.net>
 *               2014  Mark B Vine (orcid:0000-0002-7794-0426)
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
package org.openscience.cdk.isomorphism.matchers;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openscience.cdk.exception.CDKException;

/**
 * Represents a list of Rgroup substitutes to be associated with some
 * {@link RGroupQuery}.
 *
 * @cdk.module  isomorphism
 * @cdk.githash
 * @cdk.keyword Rgroup
 * @cdk.keyword R group
 * @cdk.keyword R-group
 * @author Mark Rijnbeek
 */
public class RGroupList {

    /**
     * Default value for occurrence field.
     */
    public final static String DEFAULT_OCCURRENCE = ">0";

    /**
     * Unique number to identify the Rgroup.
     */
    private int                rGroupNumber;

    /**
     * Indicates that sites labeled with this Rgroup may only be
     * substituted with a member of the Rgroup or with hydrogen.
     */
    private boolean            restH;

    /**
     * Occurrence required:
     * <UL>
     * <LI>n : exactly n ;</LI>
     * <LI>n - m : n through m ;</LI>
     * <LI>&#62; n : greater than n ;</LI>
     * <LI>&#60; n : fewer than n ;</LI>
     * <LI>default (blank) is > 0 ;</LI>
     * </UL>
     * Any non-contradictory combination of the preceding values is also
     * allowed; for example "1, 3-7, 9, >11".
     */
    private String             occurrence;

    /**
     * List of substitute structures.
     */
    private List<RGroup>       rGroups;

    /**
     * The rGroup (say B) that is required when this one (say A) exists.<p>
     * This captures the "LOG" information 'IF A (this) THEN B'.
     */
    private int                requiredRGroupNumber;

    /**
     * Default constructor.
     */
    public RGroupList(int rGroupNumber) {
        setRGroupNumber(rGroupNumber);
        this.restH = false;
        this.occurrence = DEFAULT_OCCURRENCE;
        this.requiredRGroupNumber = 0;
    }

    /**
     * Constructor with attributes given.
     *
     * @param rGroupNumber R-Group number
     * @param restH restH
     * @param occurrence occurrence
     * @param requiredRGroupNumber number of other R-Group required
     * @throws CDKException
     */
    public RGroupList(int rGroupNumber, boolean restH, String occurrence, int requiredRGroupNumber) throws CDKException {
        setRGroupNumber(rGroupNumber);
        setRestH(restH);
        setOccurrence(occurrence);
        setRequiredRGroupNumber(requiredRGroupNumber);
    }

    /**
     * Setter for rGroupNumber, checks for valid range.
     * Spec: "value from 1 to 32 *, labels position of Rgroup on root."
     * @param rGroupNumber R-Group number
     */
    public void setRGroupNumber(int rGroupNumber) {

        if (rGroupNumber < 1 || rGroupNumber > 32) {
            throw new RuntimeException("Rgroup number must be between 1 and 32.");
        }
        this.rGroupNumber = rGroupNumber;
    }

    public int getRGroupNumber() {
        return rGroupNumber;
    }

    public void setRestH(boolean restH) {
        this.restH = restH;
    }

    public boolean isRestH() {
        return restH;
    }

    public void setRequiredRGroupNumber(int rGroupNumberImplicated) {
        this.requiredRGroupNumber = rGroupNumberImplicated;
    }

    public int getRequiredRGroupNumber() {
        return requiredRGroupNumber;
    }

    public void setRGroups(List<RGroup> rGroups) {
        this.rGroups = rGroups;
    }

    public List<RGroup> getRGroups() {
        return rGroups;
    }

    /**
     * Returns the occurrence value.
     * @return occurrence
     */
    public String getOccurrence() {
        return occurrence;
    }

    /**
     * Picky setter for occurrence fields. Validates user input to be conform
     * the (Symyx) specification.
     * @param occurrence occurence value
     */
    public void setOccurrence(String occurrence) throws CDKException {
        if (occurrence == null || occurrence.equals("")) {
            occurrence = ">0"; //revert to default
        } else {
            occurrence = occurrence.trim().replaceAll(" ", "");
            if (isValidOccurrenceSyntax(occurrence)) {
                this.occurrence = occurrence;
            } else
                throw new CDKException("Invalid occurence line: " + occurrence);
        }
    }

    /**
     * Validates the occurrence value.
     * <UL>
     * <LI>n : exactly n ;</LI>
     * <LI>n - m : n through m ;</LI>
     * <LI>&#62; n : greater than n ;</LI>
     * <LI>&#60; n : fewer than n ;</LI>
     * <LI>default (blank) is > 0 ;</LI>
     * </UL>
     * Any combination of the preceding values is also
     * allowed; for example "1, 3-7, 9, >11".
     * @param occ String to validate.
     * @return true if valid String provided.
     */
    public static boolean isValidOccurrenceSyntax(String occ) {
        StringTokenizer st = new StringTokenizer(occ, ",");
        while (st.hasMoreTokens()) {
            String cond = st.nextToken().trim().replaceAll(" ", "");
            do {
                //Number: "n"
                if (match("^\\d+$", cond)) {
                    if (Integer.valueOf(cond) < 0) // not allowed
                        return false;
                    break;
                }
                //Range: "n-m"
                if (match("^\\d+-\\d+$", cond)) {
                    int from = Integer.valueOf(cond.substring(0, cond.indexOf('-')));
                    int to = Integer.valueOf(cond.substring(cond.indexOf('-') + 1, cond.length()));
                    if (from < 0 || to < 0 || to < from) // not allowed
                        return false;
                    break;
                }
                //Smaller than: "<n"
                if (match("^<\\d+$", cond)) {
                    int n = Integer.valueOf(cond.substring(cond.indexOf('<') + 1, cond.length()));
                    if (n == 0) // not allowed
                        return false;
                    break;
                }
                //Greater than: ">n"
                if (match("^>\\d+$", cond)) {
                    break;
                }

                return false;
            } while (1 == 0);
        }

        return true;
    }

    /**
     * Helper method for regular expression matching.
     * @param regExp regular expression String
     * @param userInput user's input
     * @return the regular expression matched the user input
     */
    private static boolean match(String regExp, String userInput) {
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(userInput);
        if (matcher.find())
            return true;
        else
            return false;
    }

    /**
     * Matches the 'occurrence' condition with a provided maximum number of
     * RGroup attachments. Returns the valid occurrences (numeric) for these
     * two combined. If none found, returns empty list.<P>
     * Example: if R1 occurs 3 times attached to some root structure, then
     * stating ">5" as an occurrence for that RGoupList does not make
     * sense: the example R1 can occur 0..3 times. Empty would be returned.<BR>
     * If the occurence would be >2, then 3 would be returned. Etcetera.
     *
     * @param maxAttachments number of attachments
     * @return valid values by combining a max for R# with the occurrence cond.
     */
    public List<Integer> matchOccurence(int maxAttachments) {

        List<Integer> validValues = new ArrayList<Integer>();

        for (int val = 0; val <= maxAttachments; val++) {
            boolean addVal = false;

            StringTokenizer st = new StringTokenizer(occurrence, ",");
            while (st.hasMoreTokens() && !addVal) {
                String cond = st.nextToken().trim().replaceAll(" ", "");
                if (match("^\\d+$", cond)) { // n
                    if (Integer.valueOf(cond) == val) addVal = true;
                }
                if (match("^\\d+-\\d+$", cond)) { // n-m
                    int from = Integer.valueOf(cond.substring(0, cond.indexOf('-')));
                    int to = Integer.valueOf(cond.substring(cond.indexOf('-') + 1, cond.length()));
                    if (val >= from && val <= to) {
                        addVal = true;
                    }
                }
                if (match("^>\\d+$", cond)) { // <n
                    int n = Integer.valueOf(cond.substring(cond.indexOf('>') + 1, cond.length()));
                    if (val > n) {
                        addVal = true;
                    }
                }
                if (match("^<\\d+$", cond)) { // >n
                    int n = Integer.valueOf(cond.substring(cond.indexOf('<') + 1, cond.length()));
                    if (val < n) {
                        addVal = true;
                    }
                }
                if (addVal) {
                    validValues.add(val);
                }

            }
        }
        return validValues;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RGroupList && this.rGroupNumber == ((RGroupList) obj).rGroupNumber)
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        return this.rGroupNumber;
    }

}
