/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
 *
 */
import com.sun.tools.doclets.Taglet;
import com.sun.javadoc.*;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Taglet that expands inline cdk.dictref tags into a weblink to the appropriate
 * dictionary. For example.
 * <pre>
 * @cdk.dictref bo:graphPartitioning
 * </pre>
 *
 * <p>The known dictionaries are:
 * <ul>
 *  <li>bo: Blue Obelisk <a href=""></a>
 * </ul>
 */
public class CDKDictRefTaglet implements Taglet {
    
    private static final String NAME = "cdk.dictref";
    
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
        return false;
    }
    
    public static void register(Map tagletMap) {
       CDKDictRefTaglet tag = new CDKDictRefTaglet();
       Taglet t = (Taglet) tagletMap.get(tag.getName());
       if (t != null) {
           tagletMap.remove(tag.getName());
       }
       tagletMap.put(tag.getName(), tag);
    }

    public String toString(Tag tag) {
        String tagText = tag.text();
        String separator = ":";
        if (tagText.indexOf(separator) != -1) {
            StringTokenizer tokenizer = new StringTokenizer(tagText, separator);
            String dictCode = tokenizer.nextToken();
            String dictRef = tokenizer.nextToken();
            String output = "<DT><B>A pointer to a dictionary: </B><DD>";
            if ("bo".equals(dictCode)) {
                String url = "http://qsar.sourceforge.net/dicts/blue-obelisk/index.xhtml#" + dictRef;
                output += "<a href=\"" + url + "\">" + dictRef + "</a></DD>\n";
            } else {
                output += "Unknown code: " + tagText + "</DD>\n";
            }
            return output;
        } else {
            return "<DT><B>A pointer to a dictionary: </B><DD>Unknown code: " + tagText + "</DD>\n";
        }
    }
    
    public String toString(Tag[] tags) {
        if (tags.length == 0) {
            return null;
        } else {
            return toString(tags[0]);
        }
    }
    
}
