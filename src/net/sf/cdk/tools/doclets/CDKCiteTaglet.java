/* $Revision: 7327 $ $Author: egonw $ $Date: 2006-11-20 20:22:51 +0100 (Mon, 20 Nov 2006) $
 * 
 * Copyright (C) 2004-2007  Egon Willighagen <egonw@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package net.sf.cdk.tools.doclets;

import java.io.File;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.cdk.tools.bibtex.BibTeXMLEntry;
import net.sf.cdk.tools.bibtex.BibTeXMLFile;
import nu.xom.Builder;
import nu.xom.Document;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * Taglet that expands inline cdk.cite tags into a weblink to the CDK
 * bibliography webpage. Like all inline tags it's used in the JavaDoc
 * text as:
 * <pre>
 * This class does nothing {@cdk.cite NULL}.
 * </pre>
 * For this code a reference is created like this:
 * <pre>
 * <a href="http://cdk.sf.net/biblio.html#NULL">NULL</a>
 * </pre>
 *
 * <p>Citations can be singular, like <code>{@cdk.cite BLA}</code>,
 * and multiple, like <code>{@cdk.cite BLA,BLA2,FOO}</code>.
 */
public class CDKCiteTaglet implements Taglet {
    
    private static final String NAME = "cdk.cite";
    
    private static BibTeXMLFile bibtex = null;
    
    static {
    	try {
        	Builder parser = new Builder();
    		Document doc = parser.build(new File("doc/refs/cheminf.bibx"));
    		bibtex = new BibTeXMLFile(doc.getRootElement());
    	} catch (Exception exc) {
    		System.out.println("Horrible problem: " + exc.getMessage());
    		exc.printStackTrace();
    	}
    }
    
    public String getName() {
        return NAME;
    }
    
    public boolean inField() {
        return true;
    }

    public boolean inConstructor() {
        return true;
    }
    
    public boolean inMethod() {
        return true;
    }
    
    public boolean inOverview() {
        return true;
    }

    public boolean inPackage() {
        return true;
    }

    public boolean inType() {
        return true;
    }
    
    public boolean isInlineTag() {
        return true;
    }
    
    public static void register(Map tagletMap) {
       CDKCiteTaglet tag = new CDKCiteTaglet();
       Taglet t = (Taglet) tagletMap.get(tag.getName());
       if (t != null) {
           tagletMap.remove(tag.getName());
       }
       tagletMap.put(tag.getName(), tag);
    }

    public String toString(Tag tag) {
        return "[" + expandCitation(tag.text()) + "]";
    }
    
    public String toString(Tag[] tags) {
        String result = null;
        if (tags.length > 0) {
            result = "[";
            for (int i=0; i<tags.length; i++) {
                result += expandCitation(tags[i].text());
                if ((i+1)<tags.length) result += ", ";
            }
            result += "]";
        }
        return result;
    }

    /**
     * Expands a citation into HTML code.
     */
    private String expandCitation(String citation) {
        String result = "";
        final String separator = ",";
        result += "<!-- indexOf" + citation.indexOf(separator) + " -->";
        if (citation.indexOf(separator) != -1) {
            StringTokenizer tokenizer = new StringTokenizer(citation, separator);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken().trim();
                BibTeXMLEntry entry = bibtex.getEntry(token);
                if (entry != null) {
                	result += entry.toHTML();
                } else {
                	result += token + " (not found in db)";
                }
                if (tokenizer.hasMoreTokens()) {
                    result += ", ";
                }
            }
        } else {
            citation = citation.trim();
            BibTeXMLEntry entry = bibtex.getEntry(citation);
            if (entry != null) {
            	result += entry.toHTML();
            } else {
            	result += citation + " (not found in db)";
            }
        }
        return result;
    }
    
}
