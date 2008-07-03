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

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.IoCoverageTest;
import org.openscience.cdk.io.CDKSourceCodeWriterTest;
import org.openscience.cdk.io.ChemObjectIOInstantionTests;
import org.openscience.cdk.io.CrystClustReaderTest;
import org.openscience.cdk.io.GamessReaderTest;
import org.openscience.cdk.io.Gaussian98ReaderTest;
import org.openscience.cdk.io.GhemicalReaderTest;
import org.openscience.cdk.io.HINReaderTest;
import org.openscience.cdk.io.INChIPlainTextReaderTest;
import org.openscience.cdk.io.INChIReaderTest;
import org.openscience.cdk.io.MDLRXNReaderTest;
import org.openscience.cdk.io.MDLRXNV2000ReaderTest;
import org.openscience.cdk.io.MDLRXNV3000ReaderTest;
import org.openscience.cdk.io.MDLRXNWriterTest;
import org.openscience.cdk.io.MDLReaderTest;
import org.openscience.cdk.io.MDLV2000ReaderTest;
import org.openscience.cdk.io.MDLV3000ReaderTest;
import org.openscience.cdk.io.MDLWriterTest;
import org.openscience.cdk.io.Mol2ReaderTest;
import org.openscience.cdk.io.PCCompoundASNReaderTest;
import org.openscience.cdk.io.PDBWriterTest;
import org.openscience.cdk.io.PMPReaderTest;
import org.openscience.cdk.io.ReaderFactoryTest;
import org.openscience.cdk.io.SDFReaderTest;
import org.openscience.cdk.io.ShelXReaderTest;
import org.openscience.cdk.io.WriterFactoryTest;
import org.openscience.cdk.io.XYZReaderTest;
import org.openscience.cdk.io.XYZWriterTest;
import org.openscience.cdk.io.cml.CMLIOTests;
import org.openscience.cdk.io.formats.ABINITFormatTest;
import org.openscience.cdk.io.formats.ADFFormatTest;
import org.openscience.cdk.io.formats.Aces2FormatTest;
import org.openscience.cdk.io.formats.AlchemyFormatTest;
import org.openscience.cdk.io.formats.BGFFormatTest;
import org.openscience.cdk.io.formats.BSFormatTest;
import org.openscience.cdk.io.formats.CACheFormatTest;
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
import org.openscience.cdk.io.formats.GROMOS96FormatTest;
import org.openscience.cdk.io.formats.GamessFormatTest;
import org.openscience.cdk.io.formats.Gaussian03FormatTest;
import org.openscience.cdk.io.formats.Gaussian90FormatTest;
import org.openscience.cdk.io.formats.Gaussian92FormatTest;
import org.openscience.cdk.io.formats.Gaussian94FormatTest;
import org.openscience.cdk.io.formats.Gaussian95FormatTest;
import org.openscience.cdk.io.formats.Gaussian98FormatTest;
import org.openscience.cdk.io.formats.GhemicalMMFormatTest;
import org.openscience.cdk.io.formats.GhemicalSPMFormatTest;
import org.openscience.cdk.io.formats.HINFormatTest;
import org.openscience.cdk.io.formats.INChIFormatTest;
import org.openscience.cdk.io.formats.INChIPlainTextFormatTest;
import org.openscience.cdk.io.formats.JMEFormatTest;
import org.openscience.cdk.io.formats.JaguarFormatTest;
import org.openscience.cdk.io.formats.MACiEFormatTest;
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
import org.openscience.cdk.io.formats.Mol2FormatTest;
import org.openscience.cdk.io.formats.NWChemFormatTest;
import org.openscience.cdk.io.formats.PCModelFormatTest;
import org.openscience.cdk.io.formats.PDBFormatTest;
import org.openscience.cdk.io.formats.PDBMLFormatTest;
import org.openscience.cdk.io.formats.PMPFormatTest;
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
import org.openscience.cdk.io.iterator.IteratingMDLConformerReaderTest;
import org.openscience.cdk.io.iterator.IteratingMDLReaderTest;
import org.openscience.cdk.io.iterator.IteratingPCCompoundASNReaderTest;
import org.openscience.cdk.io.iterator.IteratingPCCompoundXMLReaderTest;
import org.openscience.cdk.io.iterator.IteratingPCSubstancesXMLReaderTest;

/**
 * TestSuite that runs all the sample tests for the cdk.io package.
 *
 * @cdk.module test-io
 */
public class MioTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("The cdk.io Tests");

        suite.addTest(IoCoverageTest.suite());
        
        suite.addTest(CMLIOTests.suite());
        suite.addTest(CrystClustReaderTest.suite());
        suite.addTest(new JUnit4TestAdapter(CDKSourceCodeWriterTest.class));
        suite.addTest(GamessReaderTest.suite());
        suite.addTest(Gaussian98ReaderTest.suite());
        suite.addTest(GhemicalReaderTest.suite());
        suite.addTest(HINReaderTest.suite());
        //suite.addTest(IChIReaderTest.suite());
        suite.addTest(INChIReaderTest.suite());
        suite.addTest(INChIPlainTextReaderTest.suite());
        suite.addTest(MDLReaderTest.suite());
        suite.addTest(MDLV2000ReaderTest.suite());
        suite.addTest(MDLV3000ReaderTest.suite());
        suite.addTest(SDFReaderTest.suite());
        suite.addTest(MDLWriterTest.suite());
        suite.addTest(MDLRXNReaderTest.suite());
        suite.addTest(MDLRXNV2000ReaderTest.suite());
        suite.addTest(MDLRXNV3000ReaderTest.suite());
        suite.addTest(MDLRXNWriterTest.suite());
        suite.addTest(Mol2ReaderTest.suite());
        suite.addTest(PCCompoundASNReaderTest.suite());
        suite.addTest(PDBWriterTest.suite());
        suite.addTest(PMPReaderTest.suite());
        suite.addTest(ShelXReaderTest.suite());
        //suite.addTest(VASPReaderTest.suite()); Class is in experimental
        /* suite.addTest(ZMatrixReaderTest.suite()); This is not a JUnit test yet! */
        suite.addTest(XYZReaderTest.suite());
        suite.addTest(XYZWriterTest.suite());

        suite.addTest(ReaderFactoryTest.suite());
        suite.addTest(WriterFactoryTest.suite());
        suite.addTest(ChemObjectIOInstantionTests.suite());

        // cdk.io.iterator package
        suite.addTest(IteratingMDLReaderTest.suite());
        suite.addTest(IteratingMDLConformerReaderTest.suite());
        suite.addTest(IteratingPCCompoundASNReaderTest.suite());
        suite.addTest(IteratingPCCompoundXMLReaderTest.suite());
        suite.addTest(IteratingPCSubstancesXMLReaderTest.suite());
        
        // cdk.io.formats package
        suite.addTest(new JUnit4TestAdapter(ABINITFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(Aces2FormatTest.class));
        suite.addTest(new JUnit4TestAdapter(ADFFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(AlchemyFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(BGFFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(BSFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(CacaoCartesianFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(CacaoInternalFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(CACheFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(Chem3D_Cartesian_1FormatTest.class));
        suite.addTest(new JUnit4TestAdapter(Chem3D_Cartesian_2FormatTest.class));
        suite.addTest(new JUnit4TestAdapter(ChemDrawFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(ChemtoolFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(CIFFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(CMLFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(CMLRSSFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(CRK2DFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(CRK3DFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(CrystClustFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(CTXFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(DaltonFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(DMol3FormatTest.class));
        suite.addTest(new JUnit4TestAdapter(DOCK5FormatTest.class));
        suite.addTest(new JUnit4TestAdapter(FenskeHall_ZMatrixFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(GamessFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(Gaussian03FormatTest.class));
        suite.addTest(new JUnit4TestAdapter(Gaussian90FormatTest.class));
        suite.addTest(new JUnit4TestAdapter(Gaussian92FormatTest.class));
        suite.addTest(new JUnit4TestAdapter(Gaussian94FormatTest.class));
        suite.addTest(new JUnit4TestAdapter(Gaussian95FormatTest.class));
        suite.addTest(new JUnit4TestAdapter(Gaussian98FormatTest.class));
        suite.addTest(new JUnit4TestAdapter(GhemicalMMFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(GhemicalSPMFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(GROMOS96FormatTest.class));
        suite.addTest(new JUnit4TestAdapter(HINFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(INChIFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(INChIPlainTextFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(JaguarFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(JMEFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(MACiEFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(MacroModelFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(MDLFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(MDLRXNFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(MDLRXNV3000FormatTest.class));
        suite.addTest(new JUnit4TestAdapter(MDLV2000FormatTest.class));
        suite.addTest(new JUnit4TestAdapter(MDLV3000FormatTest.class));
        suite.addTest(new JUnit4TestAdapter(MMODFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(Mol2FormatTest.class));
        suite.addTest(new JUnit4TestAdapter(MOPAC2002FormatTest.class));
        suite.addTest(new JUnit4TestAdapter(MOPAC7FormatTest.class));
        suite.addTest(new JUnit4TestAdapter(MOPAC93FormatTest.class));
        suite.addTest(new JUnit4TestAdapter(MOPAC97FormatTest.class));
        suite.addTest(new JUnit4TestAdapter(MPQCFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(NWChemFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(PCModelFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(PDBFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(PDBMLFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(PMPFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(PQSChemFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(PubChemASNFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(PubChemFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(PubChemCompoundsXMLFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(PubChemCompoundXMLFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(PubChemSubstancesASNFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(PubChemSubstancesXMLFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(PubChemSubstanceXMLFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(QChemFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(RawCopyFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(SDFFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(ShelXFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(SMARTSFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(SMILESFIXFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(SMILESFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(SpartanFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(SybylDescriptorFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(TinkerMM2FormatTest.class));
        suite.addTest(new JUnit4TestAdapter(TinkerXYZFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(TurboMoleFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(UniChemXYZFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(VASPFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(ViewmolFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(XEDFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(XYZFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(YasaraFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(ZindoFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(ZMatrixFormatTest.class));
        
        return suite;
    }

}
