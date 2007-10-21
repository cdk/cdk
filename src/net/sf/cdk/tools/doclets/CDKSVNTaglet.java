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
    private final static Pattern svnrevPattern = Pattern.compile("$Revision:\\s*(\\d*)\\s*$");
    
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
    
    public static void register(Map tagletMap) {
       CDKInChITaglet tag = new CDKInChITaglet();
       Taglet t = (Taglet) tagletMap.get(tag.getName());
       if (t != null) {
           tagletMap.remove(tag.getName());
       }
       tagletMap.put(tag.getName(), tag);
    }

    public String toString(Tag tag) {
        return "<DT><B>SVN: </B><DD>"
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
    	String text = tag.text();
//    	String file = tag.
    	Matcher matcher = svnrevPattern.matcher(text);
    	if (matcher.matches()) {
    		String revision = matcher.group(1);
    		return revision;
    	} else {
    		System.out.println("Malformed @cdk.svnrev content: " + text);
    	}
    	return "";
    }
}
