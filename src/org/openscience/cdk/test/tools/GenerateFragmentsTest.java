/* 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.tools;

import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.GenerateFragments;
import org.openscience.cdk.tools.HydrogenAdder;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-extra
 */
public class GenerateFragmentsTest extends CDKTestCase{
	

	public GenerateFragmentsTest() {}

	public static Test suite() {
	      return new TestSuite(GenerateFragmentsTest.class);
	}

	public void testGenerateMurckoFragments1() throws ClassNotFoundException, CDKException, java.lang.Exception {
	    	String filename = "data/mdl/murckoTest1.mol";
	    	//logger.debug("\nMurckoTesting: " + filename);
	    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
	    	GenerateFragments gf=new GenerateFragments();
	    	try {
	        	MDLReader reader = new MDLReader(ins);
	        	Molecule mol = (Molecule)reader.read(new Molecule());
	        	gf.generateMurckoFragments(mol,true,true,4);
	        	//logger.debug("Murcko Fragments generated");
	        	String[] smiles=gf.getMurckoFrameworksAsSmileArray();
	        	for (int i =0;i<smiles.length;i++){
	        		//logger.debug("MF"+i+" :"+smiles[i]);
	        	}
	        	assertEquals("c1ccc(cc1)CCc2ccccc2",smiles[0]);
	        	assertEquals(1,smiles.length);
	        }catch (Exception e){
	        	System.out.println("Error in testGenerateMurckoFragments1:");
	        	e.printStackTrace();
	        }
	}
	public void testGenerateMurckoFragments2() throws ClassNotFoundException, CDKException, java.lang.Exception {
    	String filename = "data/mdl/murckoTest2.mol";
    	//logger.debug("\nMurckoTesting: " + filename);
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	GenerateFragments gf=new GenerateFragments();
    	try {
        	MDLReader reader = new MDLReader(ins);
        	Molecule mol = (Molecule)reader.read(new Molecule());
        	gf.generateMurckoFragments(mol,true,true,4);
        	//logger.debug("Murcko Fragments generated");
        	String[] smiles=gf.getMurckoFrameworksAsSmileArray();
        	for (int i =0;i<smiles.length;i++){
        		//logger.debug("MF"+i+" :"+smiles[i]);
        	}
        	assertEquals("C1CCC(C1)CCC2Cc3ccccc3(C2)",smiles[0]);
        	assertEquals(1,smiles.length);
        }catch (Exception e){
        	System.out.println("Error in testGenerateMurckoFragments2:");
        	e.printStackTrace();
        }
	}
	public void testGenerateMurckoFragments3() throws ClassNotFoundException, CDKException, java.lang.Exception {
    	String filename = "data/mdl/murckoTest3.mol";
    	//logger.debug("\nMurckoTesting: " + filename);
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	GenerateFragments gf=new GenerateFragments();
    	try {
        	MDLReader reader = new MDLReader(ins);
        	Molecule mol = (Molecule)reader.read(new Molecule());
        	gf.generateMurckoFragments(mol,true,true,4);
        	//logger.debug("Murcko Fragments generated");
        	String[] smiles=gf.getMurckoFrameworksAsSmileArray();
        	boolean found=false;
        	for (int i =0;i<smiles.length;i++){
        		//logger.debug("MF"+i+" :"+smiles[i]);
        		if (smiles[i].equals("c1ccc2C=CCc2(c1)") || smiles[i].equals("c1ccc2CC=Cc2(c1)")){
        			found=true;
        		}        		
        	}
        	assertEquals(true,found);
        	assertEquals(1,smiles.length);
        }catch (Exception e){
        	System.out.println("Error in testGenerateMurckoFragments3:");
        	e.printStackTrace();
        }
	}
	public void testGenerateMurckoFragments4() throws ClassNotFoundException, CDKException, java.lang.Exception {
    	String filename = "data/mdl/murckoTest4.mol";
    	//logger.debug("\nMurckoTesting: " + filename);
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	GenerateFragments gf=new GenerateFragments();
    	try {
        	MDLReader reader = new MDLReader(ins);
        	Molecule mol = (Molecule)reader.read(new Molecule());
        	gf.generateMurckoFragments(mol,true,true,4);
        	//logger.debug("Murcko Fragments generated");
        	String[] smiles=gf.getMurckoFrameworksAsSmileArray();
        	boolean found=false;
        	for (int i =0;i<smiles.length;i++){
        		//logger.debug("MF"+i+" :"+smiles[i]);
        		if (smiles[i].equals("c1ccc(cc1)CCC3CCC(CCc2ccccc2)C3")){
        			found=true;
        		}
        	}
        	assertEquals(true,found);
        	//assertEquals("c1ccc(cc1)CCC2CCC(C2)C4C4(c3ccccc3)",smiles[2]);
        	assertEquals(3,smiles.length);
        	
        	/*String[] rings=gf.getRingFragmentsAsSmileArray();
        	for (int i =0;i<rings.length;i++){
        		System.out.println("RF"+i+" :"+smiles[i]);
        	}*/
        	String[] linker=gf.getLinkerFragmentsAsSmileArray();
        	for (int i =0;i<linker.length;i++){
        		//logger.debug("LF"+i+" :"+linker[i]);
        	}
        	
        	
        	
        }catch (Exception e){
        	System.out.println("Error in testGenerateMurckoFragments4:");
        	e.printStackTrace();
        }
	}
	public void testGenerateMurckoFragments5() throws ClassNotFoundException, CDKException, java.lang.Exception {
    	String filename = "data/mdl/murckoTest5.mol";
    	//logger.debug("\nMurckoTesting: " + filename);
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	GenerateFragments gf=new GenerateFragments();
    	try {
        	MDLReader reader = new MDLReader(ins);
        	Molecule mol = (Molecule)reader.read(new Molecule());
        	gf.generateMurckoFragments(mol,true,true,4);
        	//logger.debug("Murcko Fragments generated");
        	String[] smiles=gf.getMurckoFrameworksAsSmileArray();
        	for (int i =0;i<smiles.length;i++){
        		//logger.debug("MF"+i+" :"+smiles[i]);
        	}
        	assertEquals(0,smiles.length);
        }catch (Exception e){
        	System.out.println("Error in testGenerateMurckoFragments5:");
        	e.printStackTrace();
        }
	}
	public void testGenerateMurckoFragments6() throws ClassNotFoundException, CDKException, java.lang.Exception {
    	String filename = "data/mdl/murckoTest6.mol";
    	//logger.debug("\nMurckoTesting: " + filename);
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	GenerateFragments gf=new GenerateFragments();
    	try {
        	MDLReader reader = new MDLReader(ins);
        	Molecule mol = (Molecule)reader.read(new Molecule());
        	gf.generateMurckoFragments(mol,true,true,4);
        	//logger.debug("Murcko Fragments generated");
        	String[] smiles=gf.getMurckoFrameworksAsSmileArray();
        	boolean found=false;
        	for (int i =0;i<smiles.length;i++){
        		//logger.debug("MF"+i+" :"+smiles[i]);
        		if (smiles[i].equals("NC3(C(=O)c1ccccc1)(C3(c2ccccc2") || smiles[i].equals("NC(Cc1ccccc1)C(=O)c2ccccc2")){
        			found=true;
        		}
        	}
        	assertEquals(true,found);
        	assertEquals(1,smiles.length);
        }catch (Exception e){
        	System.out.println("Error in testGenerateMurckoFragments6:");
        	e.printStackTrace();
        }
	}
	public void testGenerateMurckoFragments7() throws ClassNotFoundException, CDKException, java.lang.Exception {
    	//logger.debug("\nMurckoTesting 7");
    	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String smile="OC(CC[n+]1c(Nc2ccccc2)scc1c3ccccc3)(P(=O)([O-])[O-])P(=O)([O-])[O-]";//ZINK5
        IMolecule mol = sp.parseSmiles(smile); 
      
    	GenerateFragments gf=new GenerateFragments();
    	try {
        	
        	gf.generateMurckoFragments(mol,true,true,4);
        	//logger.debug("Murcko Fragments generated");
        	String[] smiles=gf.getMurckoFrameworksAsSmileArray();
        	for (int i =0;i<smiles.length;i++){
        		//logger.debug("MF"+i+" :"+smiles[i]);
        	}
        	//assertEquals("NC3(Cc1ccccc1)(C3(=O)(c2ccccc2))",smiles[0]);
        	assertEquals(3,smiles.length);
        }catch (Exception e){
        	System.out.println("Error in testGenerateMurckoFragments6:");
        	e.printStackTrace();
        }
	}
	
	public void testGenerateMurckoFragments8() throws ClassNotFoundException, CDKException, java.lang.Exception {
    	//logger.debug("\nMurckoTesting 8");
    	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String smile="c2ccc(Cc1ccccc1)cc2";
        IMolecule mol = sp.parseSmiles(smile); 
      
    	GenerateFragments gf=new GenerateFragments();
    	try {
        	
        	gf.generateMurckoFragments(mol,true,true,4);
        	//logger.debug("Murcko Fragments generated");
        	String[] smiles=gf.getMurckoFrameworksAsSmileArray();
        	for (int i =0;i<smiles.length;i++){
        		//logger.debug("MF"+i+" :"+smiles[i]);
        	}
        	assertEquals("c1ccc(cc1)Cc2ccccc2",smiles[0]);
        	assertEquals(1,smiles.length);
        }catch (Exception e){
        	System.out.println("Error in testGenerateMurckoFragments6:");
        	e.printStackTrace();
        }
	}
	public void testGenerateMurckoFragments9() throws ClassNotFoundException, CDKException, java.lang.Exception {
    	//logger.debug("\nMurckoTesting 9");
    	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String smile="c2ccc(c1ccccc1)cc2";
        IMolecule mol = sp.parseSmiles(smile); 
      
    	GenerateFragments gf=new GenerateFragments();
    	try {
        	
        	gf.generateMurckoFragments(mol,true,true,4);
        	//logger.debug("Murcko Fragments generated");
        	String[] smiles=gf.getMurckoFrameworksAsSmileArray();
        	for (int i =0;i<smiles.length;i++){
        		//logger.debug("MF"+i+" :"+smiles[i]);
        	}
        	assertEquals("c1ccc(cc1)c2ccccc2",smiles[0]);
        	assertEquals(1,smiles.length);
        }catch (Exception e){
        	System.out.println("Error in testGenerateMurckoFragments6:");
        	e.printStackTrace();
        }
	}
	
	/*public void testGenerateMurckoFragments10() throws ClassNotFoundException, CDKException, java.lang.Exception {
    	//logger.debug("\nMurckoTesting 10");
    	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String smile="Cc1nn(C)cc1[C@H]2[C@H](C(=O)N)C(=O)C[C@@](C)(O)[C@@H]2C(=O)N";//ZINK19
                      
        IMolecule mol = sp.parseSmiles(smile); 
        
       	GenerateFragments gf=new GenerateFragments();
    	try {
        	
        	gf.generateMurckoFragments(mol,true,true);
        	System.out.println("Murcko Fragments generated");
        	String[] smiles=gf.getMurckoFrameworksAsSmileArray();
        	for (int i =0;i<smiles.length;i++){
        		//logger.debug("MF"+i+" :"+smiles[i]);
        	}
        	//assertEquals("NC3(Cc1ccccc1)(C3(=O)(c2ccccc2))",smiles[0]);
        	//assertEquals(1,smiles.length);
        }catch (Exception e){
        	System.out.println("Error in testGenerateMurckoFragments6:");
        	e.printStackTrace();
        }
	}*/
	
	public void testGenerateMurckoFragments11() throws ClassNotFoundException, CDKException, java.lang.Exception {
    	String filename = "data/mdl/murckoTest7.mol";
    	//logger.debug("\nMurckoTesting: " + filename);
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	GenerateFragments gf=new GenerateFragments();
    	try {
        	MDLReader reader = new MDLReader(ins);
        	Molecule mol = (Molecule)reader.read(new Molecule());
        	gf.generateMurckoFragments(mol,true,true,4);
        	//logger.debug("Murcko Fragments generated");
        	String[] smiles=gf.getMurckoFrameworksAsSmileArray();
        	boolean found=false;
        	for (int i =0;i<smiles.length;i++){
        		//logger.debug("MF"+i+" :"+smiles[i]);
        		if (smiles[i].equals("c1ccc(cc1)C2CCCC(C2)c3ccccc3")){
        			found=true;
        		}
        	}
        	assertEquals(true,found);
        	assertEquals(3,smiles.length);
        }catch (Exception e){
        	System.out.println("Error in testGenerateMurckoFragments11:");
        	//e.printStackTrace();
        }
	}
	
	public void testGenerateMurckoFragments12() throws ClassNotFoundException, CDKException, java.lang.Exception {
    	String filename = "data/mdl/murckoTest8.mol";
    	//logger.debug("\nMurckoTesting: " + filename);
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	GenerateFragments gf=new GenerateFragments();
    	try {
        	MDLReader reader = new MDLReader(ins);
        	Molecule mol = (Molecule)reader.read(new Molecule());
        	gf.generateMurckoFragments(mol,true,true,4);
        	//logger.debug("Murcko Fragments generated");
        	String[] smiles=gf.getMurckoFrameworksAsSmileArray();
        	boolean found=false;
        	for (int i =0;i<smiles.length;i++){
        		if (smiles[i].equals("c1ccc(cc1)C2CC(C2)c3ccccc3")){
        			found=true;
        		}
        		//logger.debug("MF"+i+" :"+smiles[i]);
        	}
        	assertEquals(true,found);
        	assertEquals(3,smiles.length);
        }catch (Exception e){
        	System.out.println("Error in testGenerateMurckoFragments12:");
        	//e.printStackTrace();
        }
	}
	//without add explicit hydrogen thetest fails due to problems with smile generator 
	public void testGenerateMurckoFragments13() throws ClassNotFoundException, CDKException, java.lang.Exception {
		//logger.debug("\nMurckoTesting 13");
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		String smile="Oc1cc2ccccn2c1C(=O)OCCN3CCCCC3";//MDDR 31 
                  
		IMolecule mol = sp.parseSmiles(smile); 
		HydrogenAdder ha= new HydrogenAdder();
   
		ha.addExplicitHydrogensToSatisfyValency(mol);
    
		GenerateFragments gf=new GenerateFragments();
		try {
    	
			gf.generateMurckoFragments(mol,false,false,4);
			String[] smiles=gf.getMurckoFrameworksAsSmileArray();
			boolean found=false;
			for (int i =0;i<smiles.length;i++){
				//logger.debug("MF"+i+" :"+smiles[i]);
				if (smiles[i].equals("C1CCN(CC1)CCOCc=2cc=c3c=cc=cn=23")){
        			found=true;
        		}
			}
			assertEquals(true,found);
			assertEquals(1,smiles.length);
		}catch (Exception e){
			System.out.println("Error in testGenerateMurckoFragments6:");
			e.printStackTrace();
		}
	}
	
	//same as test 13
	public void testGenerateMurckoFragments14() throws ClassNotFoundException, CDKException, java.lang.Exception {
		//logger.debug("\nMurckoTesting 14");
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		String smile="C(c1ccc(cc1)c2ccccc2)n3cnc4cccnc34";//MDDR 52 
                  
		IMolecule mol = sp.parseSmiles(smile); 
		HydrogenAdder ha= new HydrogenAdder();
   
		ha.addExplicitHydrogensToSatisfyValency(mol);
    
		GenerateFragments gf=new GenerateFragments();
		try {
    	
			gf.generateMurckoFragments(mol,false,false,4);
			String[] smiles=gf.getMurckoFrameworksAsSmileArray();
			boolean found=false;
			for (int i =0;i<smiles.length;i++){
				//logger.debug("MF"+i+" :"+smiles[i]);
				if (smiles[i].equals("c1ccc(cc1)c2=ccc(c=c2)Cn4cnc3cccnc34")){
        			found=true;
        		}
			}
			assertEquals(true,found);
			assertEquals(3,smiles.length);
		}catch (Exception e){
			System.out.println("Error in testGenerateMurckoFragments6:");
			e.printStackTrace();
		}
	}
	//check for spiro ring systems
	public void testGenerateMurckoFragments15() throws ClassNotFoundException, CDKException, java.lang.Exception {
    	String filename = "data/mdl/murckoTest9.mol";
    	//logger.debug("\nMurckoTesting: " + filename);
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	GenerateFragments gf=new GenerateFragments();
    	try {
        	MDLReader reader = new MDLReader(ins);
        	Molecule mol = (Molecule)reader.read(new Molecule());
        	gf.generateMurckoFragments(mol,true,true,4);
        	//logger.debug("Murcko Fragments generated");
        	String[] smiles=gf.getMurckoFrameworksAsSmileArray();
        	for (int i =0;i<smiles.length;i++){
        		//logger.debug("MF"+i+" :"+smiles[i]);
        	}
        	assertEquals("C1CCC2(CC1)(CCCC2)",smiles[0]);
        	assertEquals(1,smiles.length);
        }catch (Exception e){
        	System.out.println("Error in testGenerateMurckoFragments5:");
        	e.printStackTrace();
        }
	}
	
	public void testGenerateMurckoFragments16() throws ClassNotFoundException, CDKException, java.lang.Exception {
    	String filename = "data/mdl/murckoTest10.mol";
    	//logger.debug("\nMurckoTesting: " + filename);
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	GenerateFragments gf=new GenerateFragments();
    	try {
        	MDLReader reader = new MDLReader(new InputStreamReader(ins));
        	Molecule mol = (Molecule)reader.read(new Molecule());
        	gf.generateMurckoFragments(mol,true,true,4);
        	//logger.debug("Murcko Fragments generated");
        	String[] smiles=gf.getMurckoFrameworksAsSmileArray();
        	for (int i =0;i<smiles.length;i++){
        		//logger.debug("MF"+i+" :"+smiles[i]);
        	}
        	assertEquals("O=C2CC=CN2(Cc1ccccc1)",smiles[0]);
        	assertEquals(1,smiles.length);
        }catch (Exception e){
        	System.out.println("Error in testGenerateMurckoFragments5:");
        	e.printStackTrace();
        }
	}
	public void testGenerateMurckoFragments17() throws ClassNotFoundException, CDKException, java.lang.Exception {
    	String filename = "data/mdl/murckoTest11.mol";
    	//logger.debug("\nMurckoTesting: " + filename);
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	GenerateFragments gf=new GenerateFragments();
    	try {
        	MDLReader reader = new MDLReader(new InputStreamReader(ins));
        	Molecule mol = (Molecule)reader.read(new Molecule());
        	gf.generateMurckoFragments(mol,true,true,4);
        	//logger.debug("Murcko Fragments generated");
        	String[] smiles=gf.getMurckoFrameworksAsSmileArray();
        	for (int i =0;i<smiles.length;i++){
        		//logger.debug("MF"+i+" :"+smiles[i]);
        	}
        	assertEquals("C1CCc2ccccc2(C1)",smiles[0]);
        	assertEquals(1,smiles.length);
        }catch (Exception e){
        	System.out.println("Error in testGenerateMurckoFragments5:");
        	e.printStackTrace();
        }
	}
}
	            
