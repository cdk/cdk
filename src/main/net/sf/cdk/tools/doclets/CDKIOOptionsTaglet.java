/* Copyright (C) 2012  Egon Willighagen <egonw@users.sf.net>
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

import org.openscience.cdk.io.IChemObjectIO;
import org.openscience.cdk.io.setting.IOSetting;

import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * Source for the cdk.iooptions JavaDoc tag. When a class is tagged with this
 * tag, the JavaDoc will an overview of the IO options.
 *
 * <p>The syntax must be as follows:
 * <pre>
 *   @cdk.iooptions
 * </pre>
 */
public class CDKIOOptionsTaglet implements Taglet {
    
    private static final String NAME = "cdk.iooptions";
    
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
    
    public static void register(Map<String, CDKIOOptionsTaglet> tagletMap) {
    	CDKIOOptionsTaglet tag = new CDKIOOptionsTaglet();
    	Taglet t = (Taglet) tagletMap.get(tag.getName());
    	if (t != null) {
    		tagletMap.remove(tag.getName());
    	}
    	tagletMap.put(tag.getName(), tag);
    }

    public String toString(Tag tag) {
    	// create a table with IOOptions
    	StringBuffer tableContent = new StringBuffer();
    	SourcePosition file = tag.position();
    	String pathAndFile = file.file().toString();
    	pathAndFile = pathAndFile.replaceAll("/", ".");
    	pathAndFile = pathAndFile.substring(pathAndFile.indexOf("src.main") + 9);
    	pathAndFile = pathAndFile.substring(0, pathAndFile.indexOf(".java"));
    	try {
			Class ioClass = Class.forName(pathAndFile);
			Object ioInstance = ioClass.newInstance();
			if (ioInstance instanceof IChemObjectIO) {
				IChemObjectIO objectIO = (IChemObjectIO)ioInstance;
				if (objectIO.getIOSettings().length == 0) return ""; 
	    		tableContent.append("<table>");
	    		for (IOSetting setting : objectIO.getIOSettings()) {
	    			tableContent.append("<tr>");
	    			tableContent.append("<td><b>" + setting.getName() + "</b></td>");
	    			tableContent.append(
	    				"<td>" + setting.getQuestion() +
	    				" [Default: " + setting.getDefaultSetting() + "]</td>"
	    			);
	    			tableContent.append("</tr>");
	    		}
	    		tableContent.append("</table>");
			} else {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
    	return "<DT><B>IO options: </B><DD>"
                + tableContent.toString() + "</DD>\n";
    }
    
    public String toString(Tag[] tags) {
        if (tags.length == 0) {
            return null;
        } else {
            return toString(tags[0]);
        }
    }

}
