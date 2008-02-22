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

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.XPathContext;

/**
 * This is a tool that creates HTML for a subset of the entry types
 * defined in BibTeXML. It expects the document to be valid according to
 * the BibTeXML schema, or will fail horribly.
 * 
 * @author egonw
 */
public class BibTeXMLEntry {

	private Node entry;
	private XPathContext context;
	
	public BibTeXMLEntry(Node entry) {
		this.entry = entry;
		context = new XPathContext("bibtex", BibTeXMLFile.BIBTEXML_NAMESPACE);
		context.addNamespace("b", BibTeXMLFile.BIBTEXML_NAMESPACE);		
	}
	
	/**
	 * The style is undefined and just made to look nice.
	 */
	public String toHTML() {
		// b:article
		Nodes results = entry.query("./b:article", context);
		for (int i=0; i<results.size(); i++) {
			Element article = (Element)results.get(i);
			// the obligatory fields
			return formatArticle(
				getString(article, "author", "?Authors?"),
				getString(article, "title", "?Title?"),
				getString(article, "journal", "?Journal?"),
				getString(article, "year", "19??"),
				getString(article, "volume", "?"),
				getString(article, "pages", "?-?")
			);
		}
		// b:article
		results = entry.query("./b:misc", context);
		for (int i=0; i<results.size(); i++) {
			Element misc = (Element)results.get(i);
			// the obligatory fields
			return formatMisc(
				getString(misc, "author", "?Authors?"),
				getString(misc, "title", "?Title?")
			);
		}
		return "Unknown BibTeXML type: " + ((Element)entry).getAttributeValue("id");
	}

	protected String formatArticle(String authors, String title, String journal, String year, String volume, String pages) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(authors).append(", <i>").append(title).append("</i>, ");
		buffer.append(journal).append(", <b>").append(year).append("</b>, ");
		buffer.append(volume).append(":").append(pages);
		return buffer.toString();
	}
	
	protected String formatMisc(String authors, String title) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(authors).append(", <i>").append(title).append("</i>");
		return buffer.toString();
	}
	
	/**
	 * @param node         Parent for the child.
	 * @param childElement Localname of the child element.
	 * @param def          String to default to if no child element is found.
	 * @return             String value for the child node.
	 */
	private String getString(Node node, String childElement, String def) {
		Nodes result = node.query("./b:" + childElement, context);
		return result.size() > 0 ? ((Element)result.get(0)).getValue() : def;
	}
	
}
