/* $Revision: 6707 $ $Author: egonw $ $Date: 2006-07-30 16:38:18 -0400 (Sun, 30 Jul 2006) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
 */
package net.sf.cdk.tools.bibtex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.XPathContext;

/**
 * Wrapper for a BibTeXML file.
 * 
 * @author egonw
 */
public class BibTeXMLFile {

	public final static String BIBTEXML_NAMESPACE = "http://bibtexml.sf.net/";
	
	private Node root;
	private XPathContext context;
	
	public BibTeXMLFile(Node root) {
		this.root = root;
		context = new XPathContext("bibtex", BIBTEXML_NAMESPACE);
		context.addNamespace("b", BIBTEXML_NAMESPACE);		
	}
	
	/**
	 * Returns an Iterator&lt;BibTeXMLEntry>.
	 * 
	 * @return The BibTeXMLEntry
	 */
	public Iterator<BibTeXMLEntry> getEntries() {
		List<BibTeXMLEntry> entries = new ArrayList<BibTeXMLEntry>();
		Nodes results = root.query("//b:entry", context);
		for (int i=0; i<results.size(); i++) {
			entries.add(new BibTeXMLEntry(results.get(i)));
		}
		return entries.iterator();
	}
	
	public BibTeXMLEntry getEntry(String id) {
		Nodes results = root.query("//b:entry[./@id='" + id+ "']", context);
		if (results.size() > 0) {
			return new BibTeXMLEntry(results.get(0));
		}
		return null;
	}
	
}
