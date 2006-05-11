/* $RCSfile$    
 * $Author: egonw $    
 * $Date: 2006-04-06 19:24:31 +0200 (Thu, 06 Apr 2006) $    
 * $Revision: 5897 $
 * 
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.validate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Element;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.MFAnalyser;
import org.openscience.cdk.validate.Geometry3DValidator;
import org.openscience.cdk.validate.ValidationReport;
import org.openscience.cdk.validate.ValidatorEngine;

/**
 * @cdk.module test-standard
 */
public class Geometry3DValidatorTest extends CDKTestCase {
	
	public Geometry3DValidatorTest(String name)
	{
		super(name);
	}

	public void setUp() {};

	public static Test suite() 
	{
		return new TestSuite(Geometry3DValidatorTest.class);
	}

	public void testEthane() {
		String filename = "data/Heptan-TestFF-output.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            ValidatorEngine engine = new ValidatorEngine();
            engine.addValidator(new Geometry3DValidator());
            ValidationReport report = engine.validateChemFile(chemFile);
            assertEquals(0, report.getErrorCount());
            assertEquals(0, report.getWarningCount());
        } catch (Exception e) {
            fail(e.toString());
        }
    }
}

