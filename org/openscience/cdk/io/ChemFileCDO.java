/*
 * $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, geelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */
package org.openscience.cdk.io;

import org.openscience.cdk.*;
import org.openscience.cml.*;
import org.openscience.cdopi.*;

/**
 * 
 */ 
public class ChemFileCDO extends ChemFile implements CDOInterface {

    public ChemFileCDO() {
    }
    
    // procedures required by CDOInterface

    public void startDocument() {};
    public void endDocument() {};
    public void setDocumentProperty(String type, String value) {};
    
    public void startObject(String objectType) {};
    public void endObject(String objectType) {};
    public void setObjectProperty(String objectType, String propertyType,
				  String propertyValue) {};
    
    public CDOAcceptedObjects acceptObjects() {
	CDOAcceptedObjects objects = new CDOAcceptedObjects();
	objects.add("Molecule");
	objects.add("Fragment");
	objects.add("Atom");
	objects.add("Bond");
	return objects;      
    };
}

