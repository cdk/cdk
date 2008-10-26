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
import org.openscience.cdk.coverage.IoCoverageTest;
import org.openscience.cdk.io.*;
import org.openscience.cdk.io.cml.CMLIOTests;
import org.openscience.cdk.io.formats.*;
import org.openscience.cdk.io.iterator.*;

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
        suite.addTest(new JUnit4TestAdapter(CrystClustReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(CDKSourceCodeWriterTest.class));
        suite.addTest(new JUnit4TestAdapter(CIFReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(CTXReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(GamessReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(Gaussian98ReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(GhemicalReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(HINReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(INChIReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(INChIPlainTextReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(MDLReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(MDLV2000ReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(MDLV3000ReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(SDFReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(MDLWriterTest.class));
        suite.addTest(new JUnit4TestAdapter(MDLRXNReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(MDLRXNV2000ReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(MDLRXNV3000ReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(MDLRXNWriterTest.class));
        suite.addTest(new JUnit4TestAdapter(Mol2ReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(PCCompoundASNReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(PDBWriterTest.class));
        suite.addTest(new JUnit4TestAdapter(PMPReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(ShelXReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(XYZReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(XYZWriterTest.class));

        suite.addTest(ReaderFactoryTest.suite());
        suite.addTest(WriterFactoryTest.suite());
        suite.addTest(new JUnit4TestAdapter(ChemObjectIOInstantionTests.class));

        // cdk.io.iterator package
        suite.addTest(new JUnit4TestAdapter(IteratingMDLReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(IteratingMDLConformerReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(IteratingPCCompoundASNReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(IteratingPCCompoundXMLReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(IteratingPCSubstancesXMLReaderTest.class));
        
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
