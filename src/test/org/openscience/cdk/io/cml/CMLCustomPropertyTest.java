package org.openscience.cdk.io.cml;

/*
 *  $Revision$ $Author$ $Date$
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;
import nu.xom.Element;

import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.libio.cml.Convertor;
import org.openscience.cdk.libio.cml.QSARCustomizer;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for custom properties in CML files. Based
 * 
 * @cdk.module test-libiocml
 * @cdk.require xom-1.0.jar
 * @cdk.require java1.5+
 */
public class CMLCustomPropertyTest extends CDKTestCase {

   private LoggingTool logger;
   private Convertor convertor;

   public CMLCustomPropertyTest(String name) {
       super(name);
       logger = new LoggingTool(this);
       convertor = new Convertor(true, "");
       convertor.registerCustomizer(new QSARCustomizer());
   }

   public static Test suite() {
       return new TestSuite(CMLCustomPropertyTest.class);
   }

   /**
    * Tests whether the custom atom properties survive the CML round-trip
    * @throws Exception
    */
   public void testAtomProperty() throws Exception {
	   String key = "customAtomProperty";
	   String value = "true";
	   
       Molecule mol = MoleculeFactory.makeBenzene();
       for (Iterator<IAtom> it = mol.atoms(); it.hasNext();) {
          IAtom a = it.next();
          a.setProperty(key, value);
       }       
       
       IMolecule roundTrippedMol = roundTripMolecule(mol);
       //assertEquals(convertor.cdkMoleculeToCMLMolecule(mol).toXML(), 
       //	convertor.cdkMoleculeToCMLMolecule(roundTrippedMol).toXML());
       
       for (Iterator<IAtom> it = roundTrippedMol.atoms(); it.hasNext();) {
           IAtom a = it.next();
           String actual = (String)a.getProperty(key);
           assertNotNull(actual);
           assertEquals(value, actual);
        }       
   }
   
   /**
    * Tests whether the custom bond properties survive the CML round-trip
    * @throws Exception
    */
   public void testBondProperty() throws Exception {
	   String key = "customBondProperty";
	   String value = "true";
       Molecule mol = MoleculeFactory.makeBenzene();
       for (Iterator<IBond> it = mol.bonds(); it.hasNext();) {
          IBond b = it.next();
          b.setProperty(key, value);
       }       
       
       IMolecule roundTrippedMol = roundTripMolecule(mol);
       //assertEquals(convertor.cdkMoleculeToCMLMolecule(mol).toXML(), 
       //	   convertor.cdkMoleculeToCMLMolecule(roundTrippedMol).toXML());
       
       for (Iterator<IBond> it = roundTrippedMol.bonds(); it.hasNext();) {
           IBond b = it.next();
           String actual = (String)b.getProperty(key); 
           assertNotNull(actual);
           assertEquals(value, actual);
        }       
   }
   
   /**
    * Tests whether the custom molecule properties survive the CML round-trip
    * @throws Exception
    */
   public void testMoleculeProperty() throws Exception {
	   String key = "customMoleculeProperty";
	   String value = "true";      

	   IMolecule mol = MoleculeFactory.makeAdenine();
       mol.setProperty(key, value);	   
       IMolecule roundTrippedMol = roundTripMolecule(mol);
       //assertEquals(convertor.cdkMoleculeToCMLMolecule(mol).toXML(), 
       //	convertor.cdkMoleculeToCMLMolecule(roundTrippedMol).toXML());
       String actual = (String)roundTrippedMol.getProperty(key);
       assertNotNull(actual);
       assertEquals(value, actual);
   }
   
   /**
	 * Convert a Molecule to CML and back to a Molecule again. Given that CML
	 * reading is working, the problem is with the CMLWriter.
	 * 
	 * @see org.openscience.cdk.CMLFragmentsTest
	 */
   private IMolecule roundTripMolecule(IMolecule mol) throws Exception {
       String cmlString = "<!-- failed -->";
       Element cmlDOM = convertor.cdkMoleculeToCMLMolecule(mol);
       cmlString = cmlDOM.toXML();
       
       IMolecule roundTrippedMol = null;
       logger.debug("CML string: ", cmlString);
       CMLReader reader = new CMLReader(new ByteArrayInputStream(cmlString.getBytes()));

       IChemFile file = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());
       assertNotNull(file);
       assertEquals(1, file.getChemSequenceCount());
       IChemSequence sequence = file.getChemSequence(0);
       assertNotNull(sequence);
       assertEquals(1, sequence.getChemModelCount());
       IChemModel chemModel = sequence.getChemModel(0);
       assertNotNull(chemModel);
       IMoleculeSet moleculeSet = chemModel.getMoleculeSet();
       assertNotNull(moleculeSet);
       assertEquals(1, moleculeSet.getMoleculeCount());
       roundTrippedMol = moleculeSet.getMolecule(0);
       assertNotNull(roundTrippedMol);
       
       return roundTrippedMol;
   }
}


 	  	 
