/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
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
 *
 */
package org.openscience.cdk.dict;

import java.util.ArrayList;
import java.util.List;

/**
 * Entry in a Dictionary.
 * 
 * @author       Egon Willighagen <egonw@users.sf.net>
 * @cdk.svnrev  $Revision$
 * @cdk.created  2003-08-23
 * @cdk.keyword  dictionary
 * @cdk.module   dict
 *
 * @see          Dictionary
 */
public class Entry {
    
	private String className;
    private String label;
    private String identifier;
    private List<String> descriptorInfo;
	private String definition;
	private String description;
	private Object rawContent;
    
    public Entry(String identifier, String term) {
        this.identifier = identifier.toLowerCase();
        this.label = term;
        this.descriptorInfo = new ArrayList<String>();
    }
    
    public Entry(String identifier) {
    	this(identifier, "");
    }
    
    public Entry() {
        this("", "");
    }
    
    public void setLabel(String term) {
        this.label = term;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public void setID(String identifier) {
        this.identifier = identifier.toLowerCase();
    }
    
    public String getID() {
        return this.identifier;
    }

    public String getDefinition() {
    	return this.definition;
    }
    
    public void setDefinition(String definition) {
    	this.definition = definition;
    }
    
    public String getDescription() {
    	return this.description;
    }

    public void setDescription(String description) {
    	this.description = description;
    }
    
    public void setDescriptorMetadata(String metadata) {
        this.descriptorInfo.add( metadata );
    }
    
    public List<String> getDescriptorMetadata() {
        return this.descriptorInfo;
    }
    
    public String toString() {
        return "Entry[" + getID() + "](" + getLabel() + ")";
    }

	/**
	 * @return Returns the rawContent.
	 */
	public Object getRawContent() {
		return rawContent;
	}

	/**
	 * @param rawContent The rawContent to set.
	 */
	public void setRawContent(Object rawContent) {
		this.rawContent = rawContent;
	}

	/**
	 * @return Returns the className.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className The className to set.
	 */
	public void setClassName(String className) {
		this.className = className;
	}
}
