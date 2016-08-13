/*
 * Copyright (c) 2016 John May <jwmay@users.sf.net>
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

package org.openscience.cdk.smiles;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupKey;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class CxSmilesTest {

    private final SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());

    @Test
    public void fragmentGroupingReactants() throws InvalidSmilesException {
        IReaction reaction = smipar.parseReactionSmiles("CC1=NC2=C(O)C=CC=C2C=C1.CC(Cl)=O.[Al+3].[Cl-].[Cl-].[Cl-]>[O-][N+](=O)C1=CC=CC=C1>CC(=O)C1=C2C=CC(C)=NC2=C(O)C=C1 |f:2.3.4.5|");
        assertThat(reaction.getReactantCount(), is(3));
        assertThat(reaction.getAgents().getAtomContainerCount(), is(1));
        assertThat(reaction.getProductCount(), is(1));
        assertThat(reaction.getProperty(CDKConstants.TITLE, String.class), is(""));
    }

    // grouping is invalid as we group 4 in two separate fragments
    @Test
    public void fragmentGroupingInvalid() throws InvalidSmilesException {
        IReaction reaction = smipar.parseReactionSmiles("CC1=NC2=C(O)C=CC=C2C=C1.CC(Cl)=O.[Al+3].[Cl-].[Cl-].[Cl-]>[O-][N+](=O)C1=CC=CC=C1>CC(=O)C1=C2C=CC(C)=NC2=C(O)C=C1 |f:2.3.4.5,4.6|");
        assertThat(reaction.getReactantCount(), is(6));
        assertThat(reaction.getAgents().getAtomContainerCount(), is(1));
        assertThat(reaction.getProductCount(), is(1));
        assertThat(reaction.getProperty(CDKConstants.TITLE, String.class), is(""));
    }

    @Test
    public void fragmentGroupingAgents() throws InvalidSmilesException {
        IReaction reaction = smipar.parseReactionSmiles("CC1=NC2=C(O)C=CC=C2C=C1.CC(Cl)=O>[Al+3].[Cl-].[Cl-].[Cl-].[O-][N+](=O)C1=CC=CC=C1>CC(=O)C1=C2C=CC(C)=NC2=C(O)C=C1 |f:2.3.4.5|");
        assertThat(reaction.getReactantCount(), is(2));
        assertThat(reaction.getAgents().getAtomContainerCount(), is(2));
        assertThat(reaction.getProductCount(), is(1));
        assertThat(reaction.getProperty(CDKConstants.TITLE, String.class), is(""));
    }

    @Test
    public void emptyCXSMILES() throws InvalidSmilesException {
        IReaction reaction = smipar.parseReactionSmiles("CC1=NC2=C(O)C=CC=C2C=C1.CC(Cl)=O>[Al+3].[Cl-].[Cl-].[Cl-].[O-][N+](=O)C1=CC=CC=C1>CC(=O)C1=C2C=CC(C)=NC2=C(O)C=C1 ||");
        assertThat(reaction.getProperty(CDKConstants.TITLE, String.class), is(""));
    }

    @Test
    public void fragmentGroupingProducts() throws InvalidSmilesException {
        IReaction reaction = smipar.parseReactionSmiles("CC1=NC2=C(O)C=CC=C2C=C1.CC(Cl)=O>[O-][N+](=O)C1=CC=CC=C1>CC(=O)C1=C2C=CC(C)=NC2=C(O)C=C1.[Al+3].[Cl-].[Cl-].[Cl-] |f:3.4.5.6|");
        assertThat(reaction.getReactantCount(), is(2));
        assertThat(reaction.getAgents().getAtomContainerCount(), is(1));
        assertThat(reaction.getProductCount(), is(2));
        assertThat(reaction.getProperty(CDKConstants.TITLE, String.class), is(""));
    }

    @Test
    public void nonCXSMILESLayer() throws InvalidSmilesException {
        IAtomContainer mol = smipar.parseSmiles("c1ccccc1 |<benzene>|");
        assertNotNull(mol);
        assertThat(mol.getProperty(CDKConstants.TITLE, String.class), is("|<benzene>|"));
    }

    @Test
    public void truncatedCXSMILES() throws InvalidSmilesException {
        IAtomContainer mol = smipar.parseSmiles("c1ccccc1 |");
        assertNotNull(mol);
        assertThat(mol.getProperty(CDKConstants.TITLE, String.class), is("|"));
    }

    @Test
    public void correctTitle() throws InvalidSmilesException {
        IAtomContainer mol = smipar.parseSmiles("c1ccccc1 |c:1,3,4| benzene");
        assertNotNull(mol);
        assertThat(mol.getProperty(CDKConstants.TITLE, String.class), is("benzene"));
    }

    @Test
    public void atomLabels() throws InvalidSmilesException {
        IAtomContainer mol = smipar.parseSmiles("**.c1ccccc1CC |$R'$|");
        assertThat(mol.getAtom(0), is(instanceOf(IPseudoAtom.class)));
        assertThat(((IPseudoAtom) mol.getAtom(0)).getLabel(), is("R'"));
    }

    @Test
    public void attachPoints() throws InvalidSmilesException {
        IAtomContainer mol = smipar.parseSmiles("**.c1ccccc1CC |$;;;;;;;;;_AP1$|");
        assertThat(mol.getAtom(9), is(instanceOf(IPseudoAtom.class)));
        assertThat(((IPseudoAtom) mol.getAtom(9)).getAttachPointNum(), is(1));
    }

    @Test
    public void positionalVariation() throws InvalidSmilesException {
        IAtomContainer mol = smipar.parseSmiles("**.c1ccccc1CC |m:1:2.3.4.5.6.7|");
        List<Sgroup> sgroups = mol.getProperty(CDKConstants.CTAB_SGROUPS);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getType(), is(SgroupType.ExtMulticenter));
        assertThat(sgroups.get(0).getAtoms().size(), is(7));
        assertThat(sgroups.get(0).getBonds().size(), is(1));
    }

    @Test
    public void structuralRepeatUnit() throws InvalidSmilesException {
        IAtomContainer mol = smipar.parseSmiles("**.c1ccccc1CC |Sg:n:8:m:ht|");
        List<Sgroup> sgroups = mol.getProperty(CDKConstants.CTAB_SGROUPS);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getType(), is(SgroupType.CtabStructureRepeatUnit));
        assertThat(sgroups.get(0).getSubscript(), is("m"));
        assertThat(sgroups.get(0).getValue(SgroupKey.CtabConnectivity), CoreMatchers.<Object>is("ht"));
        assertThat(sgroups.get(0).getAtoms().size(), is(1));
        assertThat(sgroups.get(0).getBonds().size(), is(2));
    }

    @Test
    public void markushFragment() throws InvalidSmilesException {
        IAtomContainer mol = smipar.parseSmiles("**.c1ccccc1CC |m:1:2.3.4.5.6.7,Sg:n:8:m:ht,$R';;;;;;;;;_AP1$|");
        List<Sgroup> sgroups = mol.getProperty(CDKConstants.CTAB_SGROUPS);
        // P-var and F-var
        assertThat(sgroups.size(), is(2));
        // atom-labels
        assertThat(mol.getAtom(0), is(instanceOf(IPseudoAtom.class)));
        assertThat(((IPseudoAtom) mol.getAtom(0)).getLabel(), is("R'"));
        // attach-points
        assertThat(mol.getAtom(9), is(instanceOf(IPseudoAtom.class)));
        assertThat(((IPseudoAtom) mol.getAtom(9)).getAttachPointNum(), is(1));
        assertThat(mol.getProperty(CDKConstants.TITLE, String.class), is(""));
    }

    @Test
    public void atomCoordinates2D() throws InvalidSmilesException {
        IAtomContainer mol = smipar.parseSmiles("CCC |(0,1,;0,2,;0,3,)|");
        assertThat(mol.getAtom(0).getPoint2d(), is(new Point2d(0, 1)));
        assertThat(mol.getAtom(1).getPoint2d(), is(new Point2d(0, 2)));
        assertThat(mol.getAtom(2).getPoint2d(), is(new Point2d(0, 3)));
    }

    @Test
    public void atomCoordinates3D() throws InvalidSmilesException {
        IAtomContainer mol = smipar.parseSmiles("CCC |(0,1,1;0,2,1;0,3,1)|");
        assertThat(mol.getAtom(0).getPoint3d(), is(new Point3d(0, 1, 1)));
        assertThat(mol.getAtom(1).getPoint3d(), is(new Point3d(0, 2, 1)));
        assertThat(mol.getAtom(2).getPoint3d(), is(new Point3d(0, 3, 1)));
    }

    @Test
    public void atomValues() throws InvalidSmilesException {
        IAtomContainer mol = smipar.parseSmiles("N1CN=CC1 |$_AV:HydDonor;;HydAcceptor$|");
        assertThat(mol.getAtom(0).getProperty(CDKConstants.COMMENT, String.class), is("HydDonor"));
        assertThat(mol.getAtom(2).getProperty(CDKConstants.COMMENT, String.class), is("HydAcceptor"));
    }

    @Test public void monovalentRadical() throws Exception {
        IAtomContainer mol = smipar.parseSmiles("[N]1C=CC=C1 |c:1,3,^1:0|");
        assertThat(mol.getConnectedSingleElectronsCount(mol.getAtom(0)), is(1));
    }

    @Test public void divalentRadical() throws Exception {
        IAtomContainer mol = smipar.parseSmiles("[C]1C2=CC=CC=C2C2=CC=CC=C12 |c:3,5,10,t:1,8,12,^3:0|");
        assertThat(mol.getConnectedSingleElectronsCount(mol.getAtom(0)), is(2));
    }

    @Test public void genericReaction() throws Exception {
        IReaction rxn = smipar.parseReactionSmiles("C1=CC(=CC=C1)C(CC(N)=O)=O.*C>C1(=CC=CC=C1)N.C*.C1=CC(=CC=C1)C=2C=C(C3=C(N2)C=CC=C3)O.C*.C*> |$;;;;;;;;;;;;R22;;;;;;;;;;;;;;;;;;;;;;;;;;;;;R22$,f:0.1,2.3,4.5.6,m:13:0.1.2.3.4.5,21:14.15.16.17.18.19,40:23.24.25.26.27.28,42:29.30.31.32.33.34.35.36.37.38|");
    }

    @Test public void trailingAtomLabelSemiColonAndAtomValues() throws Exception {
        IAtomContainer mol = smipar.parseSmiles("[H]C1=C([H])N2C(=O)C(=C([O-])[N+](CC3=CN=C(Cl)S3)=C2C(C)=C1[H])C1=CC(*)=CC=C1.** |$;;;;;;;;;;;;;;;;;;;;;;;;;;R;;;;RA;$,$_AV:;;;;;;;;;;;;;;;;;;;;;;;;2;;;4;5;6;;$,c:1,18,22,29,31,t:7,12,14,26,m:31:29.28.27.25.24.23|");
        List<Sgroup> sgroups = mol.getProperty(CDKConstants.CTAB_SGROUPS);
        assertThat(mol.getAtom(26), is(instanceOf(IPseudoAtom.class)));
        assertThat(mol.getAtom(30), is(instanceOf(IPseudoAtom.class)));
        assertThat(((IPseudoAtom)mol.getAtom(26)).getLabel(), is("R"));
        assertThat(((IPseudoAtom)mol.getAtom(30)).getLabel(), is("RA"));
        assertThat(mol.getAtom(24).getProperty(CDKConstants.COMMENT), CoreMatchers.<Object>is("2"));
        assertThat(mol.getAtom(27).getProperty(CDKConstants.COMMENT), CoreMatchers.<Object>is("4"));
        assertThat(mol.getAtom(28).getProperty(CDKConstants.COMMENT), CoreMatchers.<Object>is("5"));
        assertThat(mol.getAtom(29).getProperty(CDKConstants.COMMENT), CoreMatchers.<Object>is("6"));
        assertThat(sgroups.size(), is(1));
    }


}
