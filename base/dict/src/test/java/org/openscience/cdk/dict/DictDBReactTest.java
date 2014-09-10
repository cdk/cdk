/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.dict;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;

/**
 * Checks the functionality of the dictionary reaction-processes class.
 *
 * @cdk.module test-dict
 *
 * @see org.openscience.cdk.dict.DictionaryDatabase
 */
public class DictDBReactTest extends CDKTestCase {

    @Test
    public void testDictDBReact() {
        DictionaryDatabase db = new DictionaryDatabase();
        Assert.assertTrue(db.hasDictionary("reaction-processes"));
    }

    @Test
    public void TestCheckUniqueID() {
        DictionaryDatabase db = new DictionaryDatabase();
        Dictionary dict = db.getDictionary("reaction-processes");
        Entry[] entries = dict.getEntries();
        List<String> idList = new ArrayList<String>();
        idList.add(entries[0].getID());
        for (int i = 1; i < entries.length; i++) {
            //    		System.out.println(entries[i].getID());
            if (!idList.contains(entries[i].getID()))
                idList.add(entries[i].getID());
            else
                Assert.assertFalse("The entry is contained " + entries[i] + "two times",
                        idList.contains(entries[i].getID()));

        }
    }

}
