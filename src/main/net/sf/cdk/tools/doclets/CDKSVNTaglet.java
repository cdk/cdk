/* $Revision: 7973 $ $Author: egonw $ $Date: 2007-02-19 13:16:03 +0100 (Mon, 19 Feb 2007) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * Taglet that expands @cdk.svnrev tag into a link to the SVN
 * source tree. The syntax must be as follows:
 * <pre>
 *   @cdk.svnrev $Revision: 7973 $
 * </pre>
 * 
 * <p>The actual version number is automatically updated by the
 * SVN repository.
 */
public class CDKSVNTaglet implements Taglet {
    
    private static final String NAME = "cdk.svnrev";
    private final static Pattern svnrevPattern = Pattern.compile("\\$Revision:\\s*(\\d*)\\s*\\$");
    private final static Pattern pathPattern = Pattern.compile(".*/(src/.*\\.java)");
    
    public String getName() {
        return NAME;
    }
    
    public boolean inField() {
        return false;
    }

    public boolean inConstructor() {
        return false;
    }
    
    public boolean inMethod() {
        return false;
    }
    
    public boolean inOverview() {
        return false;
    }

    public boolean inPackage() {
        return false;
    }

    public boolean inType() {
        return true;
    }
    
    public boolean isInlineTag() {
        return false;
    }
    
    public static void register(Map<String, CDKSVNTaglet> tagletMap) {
       CDKSVNTaglet tag = new CDKSVNTaglet();
       Taglet t = (Taglet) tagletMap.get(tag.getName());
       if (t != null) {
           tagletMap.remove(tag.getName());
       }
       tagletMap.put(tag.getName(), tag);
    }

    public String toString(Tag tag) {
        return "<DT><B>Source code: </B><DD>"
               + expand(tag) + "</DD>\n";
    }
    
    public String toString(Tag[] tags) {
        if (tags.length == 0) {
            return null;
        } else {
            return toString(tags[0]);
        }
    }

    private String expand(Tag tag) {
    	// get the version number
    	String text = tag.text();
    	Matcher matcher = svnrevPattern.matcher(text);
    	String revision = "HEAD";
    	if (matcher.matches()) {
    		revision = matcher.group(1);
    	} else {
    		System.out.println("Malformed @cdk.svnrev content: " + text);
    		return "";
    	}
    	// create the URL
    	SourcePosition file = tag.position();
    	String path = correctSlashes(file.file().getAbsolutePath());
    	matcher = pathPattern.matcher(path);
    	if (matcher.matches()) {
        	String url = "http://cdk.svn.sourceforge.net/viewvc/cdk/trunk/cdk/" +
            			 matcher.group(1) + "?revision=" + 
            			 revision + "&amp;view=markup";
        	return "<a href=\"" + url + "\">revision " + revision + "</a>";
    	} else {
    		System.out.println("Could not resolve class name from: " + path);
    	}
    	return "";
    }

	private String correctSlashes(String absolutePath) {
		StringBuffer buffer = new StringBuffer();
		for (int i=0; i<absolutePath.length(); i++) {
			char character = absolutePath.charAt(i); 
			if (character == '\\') {
				buffer.append('/');
			} else {
				buffer.append(character);
			}
		}
		return buffer.toString();
	}
}
