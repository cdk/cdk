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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.setting.IOSetting.Importance;

class IntegerIOSettingTest {

    @Test
    void testConstructure() {
        IOSetting setting = new IntegerIOSetting("Number of files", Importance.LOW, "How many copies do you want?", "1");
        Assertions.assertNotNull(setting);
        Assertions.assertEquals("Number of files", setting.getName());
        Assertions.assertEquals(Importance.LOW, setting.getLevel());
        Assertions.assertEquals("How many copies do you want?", setting.getQuestion());
        Assertions.assertEquals("1", setting.getSetting());
    }

    @Test
    void testIsSet() {
        IntegerIOSetting setting = new IntegerIOSetting("Number of files", Importance.LOW, "How many copies do you want?", "1");
        Assertions.assertNotNull(setting);
        Assertions.assertEquals(1, setting.getSettingValue());
    }

    @Test
    void testSetSetting() {
        IOSetting setting = new IntegerIOSetting("Number of files", Importance.LOW, "How many copies do you want?", "1");
        Assertions.assertEquals("1", setting.getSetting());
        try {
			setting.setSetting("2");
		} catch (CDKException e) {
			Assertions.fail(e); // should not happen
		}
        Assertions.assertEquals("2", setting.getSetting());
    }

    @Test
    void testSetSetting_InvalidValue() {
        IOSetting setting = new IntegerIOSetting("Number of files", Importance.LOW, "How many copies do you want?", "1");
        try {
			setting.setSetting("false");
			Assertions.fail("Expected exception was not thrown"); // should not happen
		} catch (CDKException e) {
			// should happen
		}
    }
}
