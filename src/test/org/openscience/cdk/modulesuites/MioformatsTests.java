/* $RCSfile: $    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.modulesuites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.openscience.cdk.coverage.IoformatsCoverageTest;
import org.openscience.cdk.io.FormatFactoryTest;
import org.openscience.cdk.io.formats.ABINITFormatTest;
import org.openscience.cdk.io.formats.ADFFormatTest;
import org.openscience.cdk.io.formats.Aces2FormatTest;
import org.openscience.cdk.io.formats.AlchemyFormatTest;
import org.openscience.cdk.io.formats.BGFFormatTest;
import org.openscience.cdk.io.formats.BSFormatTest;
import org.openscience.cdk.io.formats.CACheFormatTest;
import org.openscience.cdk.io.formats.CDKOWLFormatTest;
import org.openscience.cdk.io.formats.CDKSourceCodeFormatTest;
import org.openscience.cdk.io.formats.CIFFormatTest;
import org.openscience.cdk.io.formats.CMLFormatTest;
import org.openscience.cdk.io.formats.CMLRSSFormatTest;
import org.openscience.cdk.io.formats.CRK2DFormatTest;
import org.openscience.cdk.io.formats.CRK3DFormatTest;
import org.openscience.cdk.io.formats.CTXFormatTest;
import org.openscience.cdk.io.formats.CacaoCartesianFormatTest;
import org.openscience.cdk.io.formats.CacaoInternalFormatTest;
import org.openscience.cdk.io.formats.Chem3D_Cartesian_1FormatTest;
import org.openscience.cdk.io.formats.Chem3D_Cartesian_2FormatTest;
import org.openscience.cdk.io.formats.ChemDrawFormatTest;
import org.openscience.cdk.io.formats.ChemtoolFormatTest;
import org.openscience.cdk.io.formats.CrystClustFormatTest;
import org.openscience.cdk.io.formats.DMol3FormatTest;
import org.openscience.cdk.io.formats.DOCK5FormatTest;
import org.openscience.cdk.io.formats.DaltonFormatTest;
import org.openscience.cdk.io.formats.FenskeHall_ZMatrixFormatTest;
import org.openscience.cdk.io.formats.FingerprintFormatTest;
import org.openscience.cdk.io.formats.GROMOS96FormatTest;
import org.openscience.cdk.io.formats.GamessFormatTest;
import org.openscience.cdk.io.formats.Gaussian03FormatTest;
import org.openscience.cdk.io.formats.Gaussian90FormatTest;
import org.openscience.cdk.io.formats.Gaussian92FormatTest;
import org.openscience.cdk.io.formats.Gaussian94FormatTest;
import org.openscience.cdk.io.formats.Gaussian95FormatTest;
import org.openscience.cdk.io.formats.Gaussian98FormatTest;
import org.openscience.cdk.io.formats.GaussianInputFormatTest;
import org.openscience.cdk.io.formats.GhemicalMMFormatTest;
import org.openscience.cdk.io.formats.GhemicalSPMFormatTest;
import org.openscience.cdk.io.formats.HINFormatTest;
import org.openscience.cdk.io.formats.INChIFormatTest;
import org.openscience.cdk.io.formats.INChIPlainTextFormatTest;
import org.openscience.cdk.io.formats.JMEFormatTest;
import org.openscience.cdk.io.formats.JaguarFormatTest;
import org.openscience.cdk.io.formats.MDLFormatTest;
import org.openscience.cdk.io.formats.MDLRXNFormatTest;
import org.openscience.cdk.io.formats.MDLRXNV3000FormatTest;
import org.openscience.cdk.io.formats.MDLV2000FormatTest;
import org.openscience.cdk.io.formats.MDLV3000FormatTest;
import org.openscience.cdk.io.formats.MMODFormatTest;
import org.openscience.cdk.io.formats.MOPAC2002FormatTest;
import org.openscience.cdk.io.formats.MOPAC7FormatTest;
import org.openscience.cdk.io.formats.MOPAC93FormatTest;
import org.openscience.cdk.io.formats.MOPAC97FormatTest;
import org.openscience.cdk.io.formats.MPQCFormatTest;
import org.openscience.cdk.io.formats.MacroModelFormatTest;
import org.openscience.cdk.io.formats.MoSSOutputFormatTest;
import org.openscience.cdk.io.formats.Mol2FormatTest;
import org.openscience.cdk.io.formats.NWChemFormatTest;
import org.openscience.cdk.io.formats.PCModelFormatTest;
import org.openscience.cdk.io.formats.PDBFormatTest;
import org.openscience.cdk.io.formats.PDBMLFormatTest;
import org.openscience.cdk.io.formats.PMPFormatTest;
import org.openscience.cdk.io.formats.POVRayFormatTest;
import org.openscience.cdk.io.formats.PQSChemFormatTest;
import org.openscience.cdk.io.formats.PubChemASNFormatTest;
import org.openscience.cdk.io.formats.PubChemCompoundXMLFormatTest;
import org.openscience.cdk.io.formats.PubChemCompoundsXMLFormatTest;
import org.openscience.cdk.io.formats.PubChemFormatTest;
import org.openscience.cdk.io.formats.PubChemSubstanceXMLFormatTest;
import org.openscience.cdk.io.formats.PubChemSubstancesASNFormatTest;
import org.openscience.cdk.io.formats.PubChemSubstancesXMLFormatTest;
import org.openscience.cdk.io.formats.QChemFormatTest;
import org.openscience.cdk.io.formats.RawCopyFormatTest;
import org.openscience.cdk.io.formats.SDFFormatTest;
import org.openscience.cdk.io.formats.SMARTSFormatTest;
import org.openscience.cdk.io.formats.SMILESFIXFormatTest;
import org.openscience.cdk.io.formats.SMILESFormatTest;
import org.openscience.cdk.io.formats.SVGFormatTest;
import org.openscience.cdk.io.formats.ShelXFormatTest;
import org.openscience.cdk.io.formats.SpartanFormatTest;
import org.openscience.cdk.io.formats.SybylDescriptorFormatTest;
import org.openscience.cdk.io.formats.TinkerMM2FormatTest;
import org.openscience.cdk.io.formats.TinkerXYZFormatTest;
import org.openscience.cdk.io.formats.TurboMoleFormatTest;
import org.openscience.cdk.io.formats.UniChemXYZFormatTest;
import org.openscience.cdk.io.formats.VASPFormatTest;
import org.openscience.cdk.io.formats.ViewmolFormatTest;
import org.openscience.cdk.io.formats.XEDFormatTest;
import org.openscience.cdk.io.formats.XYZFormatTest;
import org.openscience.cdk.io.formats.YasaraFormatTest;
import org.openscience.cdk.io.formats.ZMatrixFormatTest;
import org.openscience.cdk.io.formats.ZindoFormatTest;

/**
 * TestSuite that runs all the sample tests for the cdk.io package.
 *
 * @cdk.module test-ioformats
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={
    IoformatsCoverageTest.class,
    FormatFactoryTest.class,

    ABINITFormatTest.class,
    Aces2FormatTest.class,
    ADFFormatTest.class,
    AlchemyFormatTest.class,
    BGFFormatTest.class,
    BSFormatTest.class,
    CacaoCartesianFormatTest.class,
    CacaoInternalFormatTest.class,
    CACheFormatTest.class,
    CDKOWLFormatTest.class,
    CDKSourceCodeFormatTest.class,
    Chem3D_Cartesian_1FormatTest.class,
    Chem3D_Cartesian_2FormatTest.class,
    ChemDrawFormatTest.class,
    ChemtoolFormatTest.class,
    CIFFormatTest.class,
    CMLFormatTest.class,
    CMLRSSFormatTest.class,
    CRK2DFormatTest.class,
    CRK3DFormatTest.class,
    CrystClustFormatTest.class,
    CTXFormatTest.class,
    DaltonFormatTest.class,
    DMol3FormatTest.class,
    DOCK5FormatTest.class,
    FenskeHall_ZMatrixFormatTest.class,
    FingerprintFormatTest.class,
    GamessFormatTest.class,
    Gaussian03FormatTest.class,
    Gaussian90FormatTest.class,
    Gaussian92FormatTest.class,
    Gaussian94FormatTest.class,
    Gaussian95FormatTest.class,
    Gaussian98FormatTest.class,
    GaussianInputFormatTest.class,
    GhemicalMMFormatTest.class,
    GhemicalSPMFormatTest.class,
    GROMOS96FormatTest.class,
    HINFormatTest.class,
    INChIFormatTest.class,
    INChIPlainTextFormatTest.class,
    JaguarFormatTest.class,
    JMEFormatTest.class,
    MacroModelFormatTest.class,
    MDLFormatTest.class,
    MDLRXNFormatTest.class,
    MDLRXNV3000FormatTest.class,
    MDLV2000FormatTest.class,
    MDLV3000FormatTest.class,
    MMODFormatTest.class,
    Mol2FormatTest.class,
    MOPAC2002FormatTest.class,
    MOPAC7FormatTest.class,
    MOPAC93FormatTest.class,
    MOPAC97FormatTest.class,
    MoSSOutputFormatTest.class,
    MPQCFormatTest.class,
    NWChemFormatTest.class,
    PCModelFormatTest.class,
    PDBFormatTest.class,
    PDBMLFormatTest.class,
    PMPFormatTest.class,
    POVRayFormatTest.class,
    PQSChemFormatTest.class,
    PubChemASNFormatTest.class,
    PubChemFormatTest.class,
    PubChemCompoundsXMLFormatTest.class,
    PubChemCompoundXMLFormatTest.class,
    PubChemSubstancesASNFormatTest.class,
    PubChemSubstancesXMLFormatTest.class,
    PubChemSubstanceXMLFormatTest.class,
    QChemFormatTest.class,
    RawCopyFormatTest.class,
    SDFFormatTest.class,
    ShelXFormatTest.class,
    SMARTSFormatTest.class,
    SMILESFIXFormatTest.class,
    SMILESFormatTest.class,
    SpartanFormatTest.class,
    SVGFormatTest.class,
    SybylDescriptorFormatTest.class,
    TinkerMM2FormatTest.class,
    TinkerXYZFormatTest.class,
    TurboMoleFormatTest.class,
    UniChemXYZFormatTest.class,
    VASPFormatTest.class,
    ViewmolFormatTest.class,
    XEDFormatTest.class,
    XYZFormatTest.class,
    YasaraFormatTest.class,
    ZindoFormatTest.class,
    ZMatrixFormatTest.class
})
public class MioformatsTests {}