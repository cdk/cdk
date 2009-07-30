/* $Revision: 7691 $ $Author: egonw $ $Date: 2007-01-11 12:47:48 +0100 (Thu, 11 Jan 2007) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.structgen.stochastic.operator;

import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SMILESReader;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 * @cdk.module test-structgen
 */
public class CrossoverMachineTest extends CDKTestCase {
    

    @Test public void testCrossoverMachine() throws Exception {
        String filename = "data/smiles/c10h16isomers.smi";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        SMILESReader reader = new SMILESReader(ins);
        MoleculeSet som = (MoleculeSet)reader.read(new MoleculeSet());
        Assert.assertEquals(99, som.getMoleculeCount());
		CrossoverMachine cm = new CrossoverMachine();
		String correctFormula="C10";
        CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(som.getBuilder());
        for(int i=0;i<som.getAtomContainerCount();i++){
            hAdder.addImplicitHydrogens(som.getAtomContainer(i));
        }
        int errorcount=0;
        for(int i=0;i<som.getAtomContainerCount();i++){
			int[] c0=new int[4];
			for(IAtom atom : som.getAtomContainer(i).atoms()){
				c0[atom.getHydrogenCount()]++;
			}
        	for(int k=i+1;k<som.getAtomContainerCount();k++){
        		try{
        		List<IAtomContainer> result = cm.doCrossover(som.getAtomContainer(i), som.getAtomContainer(k));
    			int[] c1=new int[4];
    			for(IAtom atom : som.getAtomContainer(k).atoms()){
    				c1[atom.getHydrogenCount()]++;
    			}
        		Assert.assertEquals(2, result.size());
        		for(int l=0;l<2;l++){
        			IAtomContainer ac = (IAtomContainer)result.get(l);
        			Assert.assertTrue(ConnectivityChecker.isConnected(ac));
        			Assert.assertEquals(MolecularFormulaManipulator.getString(MolecularFormulaManipulator.getMolecularFormula(ac)), correctFormula);
        			int[] c=new int[4];
        			int hcounttotal=0;
        			for(IAtom atom : ((IAtomContainer)result.get(l)).atoms()){
        				c[atom.getHydrogenCount()]++;
        				hcounttotal+=atom.getHydrogenCount();
        			}
        			if(c0[0]==c1[0])
        				Assert.assertEquals(c0[0],c[0]);
        			if(c0[1]==c1[1])
        				Assert.assertEquals(c0[1],c[1]);
        			if(c0[2]==c1[2])
        				Assert.assertEquals(c0[2],c[2]);
        			if(c0[3]==c1[3])
        				Assert.assertEquals(c0[3],c[3]);
        			Assert.assertEquals(16,hcounttotal);
        		}
        		}catch(CDKException ex){
        			errorcount++;
        		}
        		Assert.assertTrue(errorcount<300);
        	}
        }
    }
}


