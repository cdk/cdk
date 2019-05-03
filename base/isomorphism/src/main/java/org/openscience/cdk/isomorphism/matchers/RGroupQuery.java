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
package org.openscience.cdk.isomorphism.matchers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.vecmath.Point2d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Represents information contained in a Symyx RGfile (R-group query file).<br>
 * It contains a root structure (the scaffold if you like), a map with
 * R-group definitions (each of which can contain multiple substitutes) and
 * a map with attachment points. The attachment points define a connection
 * order for the substitutes, which is relevant when an Rgroup is connected
 * to the scaffold with more than one bond.
 * <P>
 * This class can also be used to produce all the valid configurations
 * for the combination of its root,definitions and conditions.
 * <P>
 * This Javadoc does not contain a code sample how to create a new RGroupQuery
 * from scratch, because a sensible RGroupQuery has quite a few attributes to be set
 * including a root plus a bunch of substituents, which are all atom containers.
 * So that would be a lot of sample code here. <br>
 * The best way to get a feel for the way the RGroup objects are populated is to
 * run the RGroupQueryReaderTest and look at the sample
 * input RGroup query files contained in the CDK and how they translate into
 * RGroupXX objects. The JChempaint application can visualize the input files for you.
 *
 * @cdk.module  isomorphism
 * @cdk.githash
 * @cdk.keyword Rgroup
 * @cdk.keyword R group
 * @cdk.keyword R-group
 * @author Mark Rijnbeek
 */
public class RGroupQuery extends QueryChemObject implements IChemObject, Serializable, IRGroupQuery {

    private static final long               serialVersionUID = -1656116487614720605L;

    private static ILoggingTool             logger           = LoggingToolFactory.createLoggingTool(RGroupQuery.class);

    /**
     * The root structure (or scaffold) to which R-groups r attached.
     */
    private IAtomContainer                  rootStructure;

    /**
     * Rgroup definitions, each a list of possible substitutes for the
     * given R number.
     */
    private Map<Integer, RGroupList>        rGroupDefinitions;

    /**
     * For each Rgroup Atom there may be a map containing (number,bond),
     * being the attachment order (1,2) and the bond to attach to.
     */
    private Map<IAtom, Map<Integer, IBond>> rootAttachmentPoints;

    public RGroupQuery(IChemObjectBuilder builder) {
        super(builder);
    }

    /**
     * Returns all R# type atoms (pseudo atoms) found in the root structure
     * for a certain provided RGgroup number.<p>
     * @param rgroupNumber R# number, 1..32
     * @return list of (pseudo) atoms with the provided rgroupNumber as label
     */
    public List<IAtom> getRgroupQueryAtoms(Integer rgroupNumber) {

        List<IAtom> rGroupQueryAtoms = null;

        if (rootStructure != null) {
            rGroupQueryAtoms = new ArrayList<IAtom>();

            for (int i = 0; i < rootStructure.getAtomCount(); i++) {
                IAtom atom = rootStructure.getAtom(i);
                if (atom instanceof IPseudoAtom) {
                    IPseudoAtom rGroup = (IPseudoAtom) atom;
                    if (!rGroup.getLabel().equals("R")
                            && // just "R" is not a proper query atom
                            rGroup.getLabel().startsWith("R")
                            && (rgroupNumber == null || Integer.valueOf(rGroup.getLabel().substring(1)).equals(
                                    rgroupNumber))) rGroupQueryAtoms.add(atom);
                }
            }
        }
        return rGroupQueryAtoms;
    }

    /**
     * Returns all R# type atoms (pseudo atoms) found in the root structure.
     * @return list of (pseudo) R# atoms
     */
    public List<IAtom> getAllRgroupQueryAtoms() {
        return getRgroupQueryAtoms(null);
    }

    private static Pattern validLabelPattern = Pattern.compile("^R\\d+$");

    /**
     * Validates a Pseudo atom's label to be valid RGroup query label (R1..R32).
     * @param Rxx R-group label like R1 or R10
     * @return true if R1..R32, otherwise false
     */
    public static boolean isValidRgroupQueryLabel(String Rxx) {
        Matcher matcher = validLabelPattern.matcher(Rxx);
        if (matcher.find()) {
            int groupNumber = Integer.valueOf(Rxx.substring(1));
            if (groupNumber >= 1 && groupNumber <= 32) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean areSubstituentsDefined() {
        List<IAtom> allRgroupAtoms = getAllRgroupQueryAtoms();
        if (allRgroupAtoms == null) return false;

        for (IAtom rgp : allRgroupAtoms) {
            if (RGroupQuery.isValidRgroupQueryLabel(((IPseudoAtom) rgp).getLabel())) {
                int groupNum = Integer.valueOf(((IPseudoAtom) rgp).getLabel().substring(1));
                if (rGroupDefinitions == null || rGroupDefinitions.get(groupNum) == null
                        || rGroupDefinitions.get(groupNum).getRGroups() == null
                        || rGroupDefinitions.get(groupNum).getRGroups().size() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean areRootAtomsDefined() {
        for (Integer rgpNum : rGroupDefinitions.keySet()) {
            boolean represented = false;
            rootLoop: for (IAtom rootAtom : this.getRootStructure().atoms()) {
                if (rootAtom instanceof IPseudoAtom && rootAtom.getSymbol().startsWith("R")) {
                    IPseudoAtom pseudo = (IPseudoAtom) rootAtom;
                    if (pseudo.getLabel().length() > 1) {
                        int rootAtomRgrpNumber = Integer.valueOf(pseudo.getLabel().substring(1));
                        if (rootAtomRgrpNumber == rgpNum) {
                            represented = true;
                            break rootLoop;
                        }
                    }
                }
            }
            if (!represented) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<IAtomContainer> getAllConfigurations() throws CDKException {

        if (!areSubstituentsDefined()) {
            throw new CDKException("Can not configure molecules: missing R# group definitions.");
        }

        //result = a list of concrete atom containers that are valid interpretations of the RGroup query
        List<IAtomContainer> result = new ArrayList<IAtomContainer>();

        //rGroupNumbers = list holding each R# number for this RGroup query
        List<Integer> rGroupNumbers = new ArrayList<Integer>();

        //distributions  = a list of valid distributions, that is a one/zero representation
        //                 indicating which atom in an atom series belonging to a particular
        //                 R# group is present (1) or absent (0).
        List<Integer[]> distributions = new ArrayList<Integer[]>();

        List<List<RGroup>> substitutes = new ArrayList<List<RGroup>>();

        //Valid occurrences for each R# group
        List<List<Integer>> occurrences = new ArrayList<List<Integer>>();
        List<Integer> occurIndexes = new ArrayList<Integer>();

        //Build up each R# group data before recursively finding configurations.
        Iterator<Integer> rGroupNumItr = rGroupDefinitions.keySet().iterator();
        if (rGroupNumItr.hasNext()) {
            while (rGroupNumItr.hasNext()) {
                int r = rGroupNumItr.next();
                rGroupNumbers.add(r);
                List<Integer> validOcc = rGroupDefinitions.get(r).matchOccurence(getRgroupQueryAtoms(r).size());
                if (validOcc.size() == 0) {
                    throw new CDKException("Occurrence '" + rGroupDefinitions.get(r).getOccurrence()
                            + "' defined for Rgroup " + r + " results in no subsititute options for this R-group.");
                }
                occurrences.add(validOcc);
                occurIndexes.add(0);
            }
            //Init distributions: empty and with the right list size
            for (int i = 0; i < rGroupNumbers.size(); i++) {
                distributions.add(null);
                substitutes.add(null);
            }

            //Start finding valid configurations using recursion, output will be put in 'result'.
            findConfigurationsRecursively(rGroupNumbers, occurrences, occurIndexes, distributions, substitutes, 0,
                    result);

        }
        return result;
    }

    /**
     * Recursive function to produce valid configurations
     * for {@link #getAllConfigurations()}.
     */
    private void findConfigurationsRecursively(List<Integer> rGroupNumbers, List<List<Integer>> occurrences,
            List<Integer> occurIndexes, List<Integer[]> distributions, List<List<RGroup>> substitutes, int level,
            List<IAtomContainer> result) throws CDKException {

        if (level == rGroupNumbers.size()) {

            if (!checkIfThenConditionsMet(rGroupNumbers, distributions)) return;

            // Clone the root to get a scaffold to plug the substitutes into.
            IAtomContainer root = this.getRootStructure();
            IAtomContainer rootClone = null;
            try {
                rootClone = (IAtomContainer) root.clone();
            } catch (CloneNotSupportedException e) {
                //Abort with CDK exception
                throw new CDKException("clone() failed; could not perform R-group substitution.");
            }

            for (int rgpIdx = 0; rgpIdx < rGroupNumbers.size(); rgpIdx++) {

                int rNum = rGroupNumbers.get(rgpIdx);
                int pos = 0;

                List<RGroup> mapped = substitutes.get(rgpIdx);
                for (RGroup substitute : mapped) {
                    IAtom rAtom = this.getRgroupQueryAtoms(rNum).get(pos);
                    if (substitute != null) {

                        IAtomContainer rgrpClone = null;
                        try {
                            rgrpClone = (IAtomContainer) (substitute.getGroup().clone());
                        } catch (CloneNotSupportedException e) {
                            throw new CDKException("clone() failed; could not perform R-group substitution.");
                        }

                        //root cloned, substitute cloned. These now need to be attached to each other..
                        rootClone.add(rgrpClone);

                        Map<Integer, IBond> rAttachmentPoints = this.getRootAttachmentPoints().get(rAtom);
                        if (rAttachmentPoints != null) {
                            // Loop over attachment points of the R# atom
                            for (int apo = 0; apo < rAttachmentPoints.size(); apo++) {
                                IBond bond = rAttachmentPoints.get(apo + 1);
                                //Check how R# is attached to bond
                                int whichAtomInBond = 0;
                                if (bond.getEnd().equals(rAtom)) whichAtomInBond = 1;
                                IAtom subsAt = null;
                                if (apo == 0)
                                    subsAt = substitute.getFirstAttachmentPoint();
                                else
                                    subsAt = substitute.getSecondAttachmentPoint();

                                //Do substitution with the clones
                                IBond cloneBond = rootClone.getBond(getBondPosition(bond, root));
                                if (subsAt != null) {
                                    IAtom subsCloneAtom = rgrpClone.getAtom(getAtomPosition(subsAt,
                                            substitute.getGroup()));
                                    cloneBond.setAtom(subsCloneAtom, whichAtomInBond);
                                }
                            }
                        }

                        //Optional: shift substitutes 2D for easier visual checking
                        if (rAtom.getPoint2d() != null && substitute != null
                                && substitute.getFirstAttachmentPoint() != null
                                && substitute.getFirstAttachmentPoint().getPoint2d() != null) {
                            Point2d pointR = rAtom.getPoint2d();
                            Point2d pointC = substitute.getFirstAttachmentPoint().getPoint2d();
                            double xDiff = pointC.x - pointR.x;
                            double yDiff = pointC.y - pointR.y;
                            for (IAtom subAt : rgrpClone.atoms()) {
                                if (subAt.getPoint2d() != null) {
                                    subAt.getPoint2d().x -= xDiff;
                                    subAt.getPoint2d().y -= yDiff;
                                }
                            }
                        }
                    } else {
                        //Distribution flag is 0, this means the R# group will not be substituted.
                        //Any atom connected to this group should be given the defined RestH value.
                        IAtom discarded = rootClone.getAtom(getAtomPosition(rAtom, root));
                        for (IBond r0Bond : rootClone.bonds()) {
                            if (r0Bond.contains(discarded)) {
                                for (IAtom atInBond : r0Bond.atoms()) {
                                    atInBond.setProperty(CDKConstants.REST_H, this.getRGroupDefinitions().get(rNum)
                                            .isRestH());
                                }
                            }
                        }
                    }

                    pos++;
                }
            }

            //Remove R# remnants from the clone, bonds and atoms that may linger.
            boolean confHasRGroupBonds = true;
            while (confHasRGroupBonds) {
                for (IBond cloneBond : rootClone.bonds()) {
                    boolean removeBond = false;
                    if (cloneBond.getBegin() instanceof IPseudoAtom
                            && isValidRgroupQueryLabel(((IPseudoAtom) cloneBond.getBegin()).getLabel()))
                        removeBond = true;
                    else if (cloneBond.getEnd() instanceof IPseudoAtom
                            && isValidRgroupQueryLabel(((IPseudoAtom) cloneBond.getEnd()).getLabel()))
                        removeBond = true;

                    if (removeBond) {
                        rootClone.removeBond(cloneBond);
                        confHasRGroupBonds = true;
                        break;
                    }
                    confHasRGroupBonds = false;
                }
            }
            boolean confHasRGroupAtoms = true;
            while (confHasRGroupAtoms) {
                for (IAtom cloneAt : rootClone.atoms()) {
                    if (cloneAt instanceof IPseudoAtom)
                        if (isValidRgroupQueryLabel(((IPseudoAtom) cloneAt).getLabel())) {
                            rootClone.removeAtomOnly(cloneAt);
                            confHasRGroupAtoms = true;
                            break;
                        }
                    confHasRGroupAtoms = false;
                }
            }
            //Add to result list
            result.add(rootClone);

        } else {
            for (int idx = 0; idx < occurrences.get(level).size(); idx++) {
                occurIndexes.set(level, idx);
                //With an occurrence picked 0..n for this level's R-group, now find
                //all possible distributions (positional alternatives).
                int occurrence = occurrences.get(level).get(idx);
                int positions = this.getRgroupQueryAtoms(rGroupNumbers.get(level)).size();
                Integer[] candidate = new Integer[positions];
                for (int j = 0; j < candidate.length; j++) {
                    candidate[j] = 0;
                }
                List<Integer[]> rgrpDistributions = new ArrayList<Integer[]>();
                findDistributions(occurrence, candidate, rgrpDistributions, 0);

                for (Integer[] distribution : rgrpDistributions) {
                    distributions.set(level, distribution);

                    RGroup[] mapping = new RGroup[distribution.length];
                    List<List<RGroup>> mappedSubstitutes = new ArrayList<List<RGroup>>();
                    mapSubstitutes(this.getRGroupDefinitions().get(rGroupNumbers.get(level)), 0, distribution, mapping,
                            mappedSubstitutes);

                    for (List<RGroup> mappings : mappedSubstitutes) {
                        substitutes.set(level, mappings);
                        findConfigurationsRecursively(rGroupNumbers, occurrences, occurIndexes, distributions,
                                substitutes, level + 1, result);

                    }
                }
            }
        }
    }

    /**
     * Finds valid distributions for a given R# group and it occurrence
     * condition taken from the LOG line.<br>
     * For example: if we have three Rn group atoms, and ">2" for
     * the occurrence, then there are fours possible ways to make a
     * distribution: 3 ways to put in two atoms, and one way
     * to put in all 3 atoms. Etc.
     * @param occur
     * @param candidate
     * @param distributions
     * @param level
     */
    private void findDistributions(int occur, Integer[] candidate, List<Integer[]> distributions, int level) {
        if (level != candidate.length) {
            for (int i = 0; i < 2; i++) {
                candidate[level] = i;

                int sum = 0;
                for (int x = 0; x < candidate.length; x++)
                    sum += candidate[x];

                if (sum == occur) {
                    distributions.add(candidate.clone());
                } else {
                    findDistributions(occur, candidate, distributions, level + 1);
                }
            }
        }
    }

    /**
     * Maps the distribution of an R-group to all possible substitute combinations.
     * This is best illustrated by an example.<br>
     * Say R2 occurs twice in the root, and has condition >0. So a valid
     * output configuration can have either one or two substitutes.
     * The distributions will have been calculated to be the following
     * solutions: [0,1], [1,0], [1,1]   <br>
     * To start with [1,1], assume two possible substitutes have been
     * defined for R2, namely *C=O and *C-N. Then the distribution [1,1]
     * should lead to four mappings:   <br>
     * [*C=O,*C=O], [*C-N,*C-N], [*C=O,*C-N], [*C-N,*C=O].    <br>
     * These mappings are generated in this function, as well as the other valid mappings
     * for [0,1] and [1,0]: <br>
     * [*C=O,null], [*C-N,null], [null,*C=O], [null,*C-N].   <br>
     * So the example would have this function produce eight mappings (result list size==8).
     *
     * @param rgpList
     * @param listOffset
     * @param distribution
     * @param mapping
     * @param result
     */
    private void mapSubstitutes(RGroupList rgpList, int listOffset, Integer[] distribution, RGroup[] mapping,
            List<List<RGroup>> result) {
        if (listOffset == distribution.length) {
            List<RGroup> mapped = new ArrayList<RGroup>();
            for (RGroup rgrp : mapping)
                mapped.add(rgrp);
            result.add(mapped);
        } else {
            if (distribution[listOffset] == 0) {
                mapping[listOffset] = null;
                mapSubstitutes(rgpList, listOffset + 1, distribution, mapping, result);
            } else {
                for (RGroup rgrp : rgpList.getRGroups()) {
                    mapping[listOffset] = rgrp;
                    mapSubstitutes(rgpList, listOffset + 1, distribution, mapping, result);
                }
            }
        }
    }

    /**
     * Helper method, used to help construct a configuration.
     * @param atom
     * @param container
     * @return the array position of atom in container
     */
    private int getAtomPosition(IAtom atom, IAtomContainer container) {
        for (int i = 0; i < container.getAtomCount(); i++) {
            if (atom.equals(container.getAtom(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Helper method, used to help construct a configuration.
     * @param bond
     * @param container
     * @return the array position of the bond in the container
     */
    private int getBondPosition(IBond bond, IAtomContainer container) {
        for (int i = 0; i < container.getBondCount(); i++) {
            if (bond.equals(container.getBond(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Helper method to see if an array is all zeroes or not.
     * Used to check if the distribution of substitutes over an R-group
     * is all zeroes, meaning there will be no substitution done.
     * @param arr
     * @return true if arr's values are all zero.
     */
    private boolean allZeroArray(Integer[] arr) {
        for (int flag : arr)
            if (flag != 0) return false;
        return true;
    }

    /**
     * Checks whether IF..THEN conditions that can be set for the R-groups are met.
     * It is used to filter away invalid configurations in {@link #findConfigurationsRecursively}.
     * <P>
     * Scenario: suppose R1 is substituted 0 times, whereas R2 is substituted.
     * Also suppose there is a condition IF R2 THEN R1. Because R1 does not
     * occur but R2 does, the IF..THEN condition is not met: this function
     * will return false, the configuration should be discarded.
     * @param rGroupNumbers
     * @param distributions
     * @return true if all IF..THEN RGroup conditions are met.
     */
    private boolean checkIfThenConditionsMet(List<Integer> rGroupNumbers, List<Integer[]> distributions) {
        for (int outer = 0; outer < rGroupNumbers.size(); outer++) {
            int rgroupNum = rGroupNumbers.get(outer);
            if (allZeroArray(distributions.get(outer))) {
                for (int inner = 0; inner < rGroupNumbers.size(); inner++) {
                    int rgroupNum2 = rGroupNumbers.get(inner);
                    if (!allZeroArray(distributions.get(inner))) {
                        RGroupList rgrpList = rGroupDefinitions.get(rgroupNum2);
                        if (rgrpList.getRequiredRGroupNumber() == rgroupNum) {
                            logger.info(" Rejecting >> all 0 for " + rgroupNum + " but requirement found from "
                                    + rgrpList.getRGroupNumber());
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int getAtomContainerCount() {
        int retVal = 0;
        if (this.rootStructure != null) retVal++;
        for (Integer r : rGroupDefinitions.keySet()) {
            for (RGroup rgrp : rGroupDefinitions.get(r).getRGroups()) {
                if (rgrp.getGroup() != null) {
                    retVal++;
                }
            }
        }
        return retVal;
    }

    @Override
    public List<IAtomContainer> getSubstituents() {
        List<IAtomContainer> substitutes = new ArrayList<IAtomContainer>();
        for (Integer r : rGroupDefinitions.keySet()) {
            for (RGroup rgrp : rGroupDefinitions.get(r).getRGroups()) {
                IAtomContainer subst = rgrp.getGroup();
                if (subst != null) substitutes.add(subst);
            }
        }
        return substitutes;
    }

    @Override
    public void setRootStructure(IAtomContainer rootStructure) {
        this.rootStructure = rootStructure;
    }

    @Override
    public IAtomContainer getRootStructure() {
        return rootStructure;
    }

    @Override
    public void setRootAttachmentPoints(Map<IAtom, Map<Integer, IBond>> rootAttachmentPoints) {
        this.rootAttachmentPoints = rootAttachmentPoints;
    }

    @Override
    public Map<IAtom, Map<Integer, IBond>> getRootAttachmentPoints() {
        return rootAttachmentPoints;
    }

    @Override
    public void setRGroupDefinitions(Map<Integer, RGroupList> rGroupDefinitions) {
        this.rGroupDefinitions = rGroupDefinitions;
    }

    @Override
    public Map<Integer, RGroupList> getRGroupDefinitions() {
        return rGroupDefinitions;
    }
}
