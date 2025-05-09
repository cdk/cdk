/* Copyright (C) 2025  Egon Willighagen <egon.willighagen@maastrichtuniversity.nl>
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
 */
package org.openscience.cdk.io.setting;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.setting.IOSetting.Importance;

class OptionIOSettingTest {

    @Test
    void testConstructure() {
    	List<String> options = new ArrayList<>();
    	options.add("Option1");
    	options.add("Option2");
        IOSetting setting = new OptionIOSetting("Menu", Importance.LOW, "What option do you want?", options, "Option1");
        Assertions.assertNotNull(setting);
        Assertions.assertEquals("Menu", setting.getName());
        Assertions.assertEquals(Importance.LOW, setting.getLevel());
        Assertions.assertEquals("What option do you want?", setting.getQuestion());
        Assertions.assertEquals("Option1", setting.getSetting());
    }

    @Test
    void testSetSetting() {
    	List<String> options = new ArrayList<>();
    	options.add("Option1");
    	options.add("Option2");
        IOSetting setting = new OptionIOSetting("Menu", Importance.LOW, "What option do you want?", options, "Option1");
        Assertions.assertEquals("Option1", setting.getSetting());
        try {
			setting.setSetting("Option2");
		} catch (CDKException e) {
			Assertions.fail(e); // should not happen
		}
        Assertions.assertEquals("Option2", setting.getSetting());
    }

    @Test
    void testSetSetting_InvalidValue() {
    	List<String> options = new ArrayList<>();
    	options.add("Option1");
    	options.add("Option2");
        IOSetting setting = new OptionIOSetting("Menu", Importance.LOW, "What option do you want?", options, "Option1");
        try {
			setting.setSetting("Option3");
			Assertions.fail("Expected exception was not thrown"); // should not happen
		} catch (CDKException e) {
			// should happen
		}
    }

    @Test
    void testSetSetting_Integer() {
    	List<String> options = new ArrayList<>();
    	options.add("Option1");
    	options.add("Option2");
    	OptionIOSetting setting = new OptionIOSetting("Menu", Importance.LOW, "What option do you want?", options, "Option1");
        Assertions.assertEquals("Option1", setting.getSetting());
        try {
			setting.setSetting(2);
		} catch (CDKException e) {
			Assertions.fail(e); // should not happen
		}
        Assertions.assertEquals("Option2", setting.getSetting());
    }

    @Test
    void testSetSetting_InvalidValue_Integer() {
    	List<String> options = new ArrayList<>();
    	options.add("Option1");
    	options.add("Option2");
    	OptionIOSetting setting = new OptionIOSetting("Menu", Importance.LOW, "What option do you want?", options, "Option1");
        try {
			setting.setSetting(3);
			Assertions.fail("Expected exception was not thrown"); // should not happen
		} catch (CDKException e) {
			// should happen
		}
    }

    @Test
    void testGetSetting() {
    	List<String> options = new ArrayList<>();
    	options.add("Option1");
    	options.add("Option2");
    	OptionIOSetting setting = new OptionIOSetting("Menu", Importance.LOW, "What option do you want?", options, "Option1");
    	List<String> options2 = setting.getOptions();
        Assertions.assertEquals(2, options2.size());
        Assertions.assertTrue(options2.contains("Option1"));
        Assertions.assertTrue(options2.contains("Option2"));
    }
}
