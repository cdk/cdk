/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
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
package org.openscience.cdk.test.tools;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.Normalizer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @cdk.module test
 */
public class NormalizerTest extends CDKTestCase {
	
	public NormalizerTest(String name)
	{
		super(name);
	}
  
  public void setUp() {}
    
	public static Test suite() 
	{
		return new TestSuite(NormalizerTest.class);
	}

	public void testNormalize()	throws ParserConfigurationException, Exception{
    Molecule ac=new Molecule();
    ac.addAtom(new Atom("C"));
    ac.addAtom(new Atom("N"));
    ac.addAtom(new Atom("O"));
    ac.addAtom(new Atom("O"));
    ac.addBond(new Bond(ac.getAtomAt(0),ac.getAtomAt(1)));
    ac.addBond(new Bond(ac.getAtomAt(1),ac.getAtomAt(2),2));
    ac.addBond(new Bond(ac.getAtomAt(1),ac.getAtomAt(3),2));
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document doc = db.newDocument();
    Element set = doc.createElement("replace-set");
    doc.appendChild(set);
    Element replace=doc.createElement("replace");
    set.appendChild(replace);
    replace.appendChild(doc.createTextNode("O=N=O"));
    Element replacement=doc.createElement("replacement");
    set.appendChild(replacement);
    replacement.appendChild(doc.createTextNode("[O-][N+]=O"));
    Normalizer.normalize(ac,doc);
    assertTrue(ac.getBondAt(1).getOrder()==1 ^ ac.getBondAt(2).getOrder()==1);
	}
}

