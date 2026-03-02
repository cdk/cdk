/*
 * Copyright (c) 2026 John Mayfield
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */
package org.openscience.cdk.isomorphism.matchers;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Functions for working with {@link IRGroupQuery}s - specifically it provides
 * the ability to flatten/unflatten as a single IAtomContainer. When
 *
 * @author John Mayfield
 */
public class RGroupQueryManipulator {

    private static final Pattern VALID_RNUM = Pattern.compile("R\\d*");

    private RGroupQueryManipulator() {
    }

    static int setComponentGroup(IAtomContainer mol, int componentId) {
        for (IAtom atom : mol.atoms()) {
            atom.setProperty(CDKConstants.COMPONENT_GROUP, componentId);
        }
        return mol.isEmpty() ? componentId : componentId + 1;
    }

    static void setRgroupLabel(IAtomContainer mol, String label) {
        for (IAtom atom : mol.atoms()) {
            atom.setProperty(CDKConstants.RGROUP_MEMBERSHIP, label);
        }
    }

    private static String getRgroupLabel(IAtomContainer mol) {
        for (IAtom atom : mol.atoms()) {
            String rGrpLabel = atom.getProperty(CDKConstants.RGROUP_MEMBERSHIP);
            if (rGrpLabel != null) {
                return rGrpLabel;
            }
        }
        return null;
    }

    private static boolean isR(IAtom atom) {
        if (atom instanceof IPseudoAtom) {
            String lab = ((IPseudoAtom) atom).getLabel();
            return lab.matches("R\\d*");
        }
        return false;
    }

    /**
     * Flatten an RGroupQuery into an IAtomContainer making it easier to work
     * with for some situations.
     * <p/>
     * Note currently any RGroup logic is lost during the conversion.
     *
     * @param rgq the RGroup Query
     * @return the flattened Markush structure
     */
    public static IAtomContainer toAtomContainer(IRGroupQuery rgq) {
        List<Sgroup> sgroups = new ArrayList<>();
        IAtomContainer mol = rgq.getBuilder().newAtomContainer();
        IAtomContainer root = rgq.getRootStructure();
        if (root != null) {
            mol.add(root);
            List<Sgroup> rootSgroups = root.getProperty(CDKConstants.CTAB_SGROUPS);
            if (rootSgroups != null)
                sgroups.addAll(rootSgroups);
        }

        int componentNum = setComponentGroup(mol, 1);
        for (Map.Entry<Integer, IRGroupList> e : rgq.getRGroupDefinitions().entrySet()) {
            String label = "R" + e.getKey();
            for (IRGroup rgroup : e.getValue().getRGroups()) {
                IAtomContainer molDef = rgroup.getGroup();
                if (molDef == null)
                    continue;
                setRgroupLabel(molDef, label);
                componentNum = setComponentGroup(molDef, componentNum);
                mol.add(molDef);
                List<Sgroup> defSgroups = molDef.getProperty(CDKConstants.CTAB_SGROUPS);
                if (defSgroups != null)
                    sgroups.addAll(defSgroups);
            }
        }

        mol.set(IChemObject.MARKUSH);
        mol.setProperty(CDKConstants.CTAB_SGROUPS, sgroups);
        return mol;
    }

    /**
     * Unpack a flattened RGroupQuery from an IAtomContainer into an RGroupQuery
     * making it easier to work with for some situations.
     *
     * @param mol the molecule
     * @return the unpacked Markush structure
     */
    public static IRGroupQuery toRgroupQuery(IAtomContainer mol) throws CDKException {

        Map<Integer, List<IRGroup>> definitions = new HashMap<>();
        IAtomContainer rootStructure = null;
        for (IAtomContainer part : ConnectivityChecker.partitionIntoMolecules(mol, false, false)) {
            String lab = getRgroupLabel(part);
            if (lab == null) {
                if (rootStructure != null) rootStructure.add(part);
                else rootStructure = part;
            } else {
                if (!VALID_RNUM.matcher(lab).matches()) {
                    throw new CDKException("R label '" + lab + "' cannot be converted to an RGroupQuery must be of the form 'R<num>'");
                }
                // note: 'R' is interpreted as 'R0'
                int rid = lab.length() > 1 ? Integer.parseInt(lab.substring(1)) : 0;
                RGroup rGrp = new RGroup();
                rGrp.setGroup(part);
                definitions.computeIfAbsent(rid, k -> new ArrayList<>()).add(rGrp);
            }
        }

        Map<Integer, IRGroupList> rgroupListMap = new HashMap<>();
        for (Map.Entry<Integer, List<IRGroup>> e : definitions.entrySet()) {
            RGroupList rGroupList = new RGroupList(e.getKey());
            rGroupList.setRGroups(e.getValue());
            rgroupListMap.put(e.getKey(), rGroupList);
        }

        RGroupQuery query = new RGroupQuery(mol.getBuilder());
        query.setRootStructure(rootStructure);
        query.setRGroupDefinitions(rgroupListMap);

        // ensure all root attachments are defined
        if (rootStructure != null) {
            Map<IAtom, Map<Integer, IBond>> rootAttachmentPoints = new HashMap<>(query.getRootAttachmentPoints());
            for (IAtom atom : rootStructure.atoms()) {
                if (rootAttachmentPoints.containsKey(atom))
                    continue;
                if (isR(atom)) {
                    Map<Integer,IBond> bonding = new HashMap<>();
                    for (IBond bond : atom.bonds()) {
                        bonding.put(bonding.size()+1, bond);
                    }
                    rootAttachmentPoints.put(atom, bonding);
                    if (atom.getBondCount() > 1) {
                        LoggingToolFactory
                                .createLoggingTool(RGroupQueryManipulator.class)
                                .warn("Rgroup: Root connection is ambiguous (multiple bonds)");
                    }
                }
            }
            query.setRootAttachmentPoints(rootAttachmentPoints);
        }

        return query;
    }
}
