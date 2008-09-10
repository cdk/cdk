/* $Revision: 11645 $ $Author: rajarshi $ $Date: 2008-07-18 13:16:08 -0400 (Fri, 18 Jul 2008) $
 *
 * Copyright (c) 2006-2008 Rational Discovery LLC, Greg Landrum, and Julie Penzotti
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *     * Neither the name of Rational Discovery nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openscience.cdk.config.fragments;

/**
 * A class representing the 79 E-state atom types in terms of SMARTS.
 * <p/>
 * These fragments were originally described in {@cdk.cite HALL1995} and were
 * converted into SMARTS forms by the <a href="http://www.rdkit.org">RDKit</a> project
 *
 * @cdk.module  standard
 * @author  Greg Landrum
 * @cdk.created 2008-07-22
 * @cdk.keyword estate
 * @cdk.keyword fragment
 */
public class EStateFragments {

    /**
     * The names of the fragments.
     * <p/>
     * See <a href="http://www.edusoft-lc.com/molconn/manuals/350/appV.html">
     * here</a> for the corresponding chemical groups
     */
    private static final String[] names = {
            "sLi", "ssBe", "ssssBe",
            "ssBH", "sssB", "ssssB",
            "sCH3", "dCH2", "ssCH2",
            "tCH", "dsCH", "aaCH",
            "sssCH", "ddC", "tsC",
            "dssC", "aasC", "aaaC",
            "ssssC", "sNH3", "sNH2",
            "ssNH2", "dNH", "ssNH",
            "aaNH", "tN", "sssNH",
            "dsN", "aaN", "sssN",
            "ddsN", "aasN", "ssssN",
            "sOH", "dO", "ssO",
            "aaO", "sF", "sSiH3",
            "ssSiH2", "sssSiH", "ssssSi",
            "sPH2", "ssPH", "sssP",
            "dsssP", "sssssP", "sSH",
            "dS", "ssS", "aaS",
            "dssS", "ddssS", "sCl",
            "sGeH3", "ssGeH2", "sssGeH",
            "ssssGe", "sAsH2", "ssAsH",
            "sssAs", "sssdAs", "sssssAs",
            "sSeH", "dSe", "ssSe",
            "aaSe", "dssSe", "ddssSe",
            "sBr", "sSnH3", "ssSnH2",
            "sssSnH", "ssssSn", "sI",
            "sPbH3", "ssPbH2", "sssPbH",
            "ssssPb",};

    /**
     * The SMARTS patterns.
     */
    private static final String[] smarts = {
            "[LiD1]-*", "[BeD2](-*)-*", "[BeD4](-*)(-*)(-*)-*",
            "[BD2H](-*)-*", "[BD3](-*)(-*)-*", "[BD4](-*)(-*)(-*)-*",
            "[CD1H3]-*", "[CD1H2]=*", "[CD2H2](-*)-*",
            "[CD1H]#*", "[CD2H](=*)-*", "[C,c;D2H](:*):*",
            "[CD3H](-*)(-*)-*", "[CD2H0](=*)=*", "[CD2H0](#*)-*",
            "[CD3H0](=*)(-*)-*", "[C,c;D3H0](:*)(:*)-*", "[C,c;D3H0](:*)(:*):*",
            "[CD4H0](-*)(-*)(-*)-*", "[ND1H3]-*", "[ND1H2]-*",
            "[ND2H2](-*)-*", "[ND1H]=*", "[ND2H](-*)-*",
            "[N,nD2H](:*):*", "[ND1H0]#*", "[ND3H](-*)(-*)-*",
            "[ND2H0](=*)-*", "[N,nD2H0](:*):*", "[ND3H0](-*)(-*)-*",
            "[ND3H0](~[OD1H0])(~[OD1H0])-,:*", "[N,nD3H0](:*)(:*)-,:*", "[ND4H0](-*)(-*)(-*)-*",
            "[OD1H]-*", "[OD1H0]=*", "[OD2H0](-*)-*",
            "[O,oD2H0](:*):*", "[FD1]-*", "[SiD1H3]-*",
            "[SiD2H2](-*)-*", "[SiD3H1](-*)(-*)-*", "[SiD4H0](-*)(-*)(-*)-*",
            "[PD1H2]-*", "[PD2H1](-*)-*", "[PD3H0](-*)(-*)-*",
            "[PD4H0](=*)(-*)(-*)-*", "[PD5H0](-*)(-*)(-*)(-*)-*", "[SD1H1]-*",
            "[SD1H0]=*", "[SD2H0](-*)-*", "[S,sD2H0](:*):*",
            "[SD3H0](=*)(-*)-*", "[SD4H0](~[OD1H0])(~[OD1H0])(-*)-*", "[ClD1]-*",
            "[GeD1H3](-*)", "[GeD2H2](-*)-*", "[GeD3H1](-*)(-*)-*",
            "[GeD4H0](-*)(-*)(-*)-*", "[AsD1H2]-*", "[AsD2H1](-*)-*",
            "[AsD3H0](-*)(-*)-*", "[AsD4H0](=*)(-*)(-*)-*", "[AsD5H0](-*)(-*)(-*)(-*)-*",
            "[SeD1H1]-*", "[SeD1H0]=*", "[SeD2H0](-*)-*",
            "[SeD2H0](:*):*", "[SeD3H0](=*)(-*)-*", "[SeD4H0](=*)(=*)(-*)-*",
            "[BrD1]-*", "[SnD1H3]-*", "[SnD2H2](-*)-*",
            "[SnD3H1](-*)(-*)-*", "[SnD4H0](-*)(-*)(-*)-*", "[ID1]-*",
            "[PbD1H3]-*", "[PbD2H2](-*)-*", "[PbD3H1](-*)(-*)-*",
            "[PbD4H0](-*)(-*)(-*)-*",};

    /**
     * Get the fragment names.
     *
     * @return a string array of the names
     */
    public static String[] getNames() {
        return names;
    }

    /**
     * Get the SMARTS patterns.
     *
     * @return a string array with the SMARTS patterns
     */
    public static String[] getSmarts() {
        return smarts;
    }
}
