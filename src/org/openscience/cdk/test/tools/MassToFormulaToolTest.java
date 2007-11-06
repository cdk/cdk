/* $Revision: 8658 $ $Author: egonw $ $Date: 2007-08-03 15:20:28 +0200 (Fri, 03 Aug 2007) $
 * 
 *  Copyright (C) 2005-2007  Miguel Rojasch <miguelrojasch@users.sf.net>
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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.MassToFormulaTool;
import org.openscience.cdk.tools.MassToFormulaTool.IElement_Nr;

/**
 * @cdk.module test-experimental
 */
public class MassToFormulaToolTest extends CDKTestCase {

	/**
	 *  Constructor for the MassToFormulaToolTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public MassToFormulaToolTest(String name){
		
		super(name);
	}

    /**
    *  The JUnit setup method
    */
    public void setUp() throws Exception {
    }

	/**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public static Test suite() {
    	
        TestSuite suite = new TestSuite(MassToFormulaToolTest.class);
        return suite;
	}
    
    /**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public void testMassToFormulaTool_Notnull(){
    	
		assertNotNull(new MassToFormulaTool());
	}
    /**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public void testMassToFormulaTool_null(){
    	
		assertNull(new MassToFormulaTool().generate(0.0));
	}
	/**
	 * A unit test suite for JUnit. Orthinine
	 *
	 * @return    The test suite
	 */
	public void testMassToFormulaToolOrthinine(){
		IElement_Nr[] elem = new IElement_Nr[6];
		MassToFormulaRestrictions restrictions = new MassToFormulaRestrictions();
		restrictions.restrict(Elements.CARBON, 0, 15);
		restrictions.restrict(Elements.HYDROGEN, 0, 15);
		restrictions.restrict(Elements.OXYGEN, 0, 15);
		restrictions.restrict(Elements.NITROGEN, 0, 15);
		restrictions.restrict(Elements.PHOSPHORUS, 0, 15);
		restrictions.restrict(Elements.SULFUR, 0, 15);
		restrictions.setTole
		double myMass = 133.0968;
		MassToFormulaTool mf = new MassToFormulaTool(restrictions);
		mf.setElements(elem);
		mf.setTolerance(0.01);
		
		List<IMolecularFormula> resultsMF = mf.generate(myMass);

		
		for(int i = 0 ; i < resultsMF.size(); i++){
			
			String stringMF = resultsMF.get(i).toString();
			System.out.println(stringMF);
		}
	}
}

