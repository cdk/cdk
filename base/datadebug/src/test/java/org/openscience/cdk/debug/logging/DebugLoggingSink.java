/* Copyright (C) 2024  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.debug.logging;

import org.openscience.cdk.tools.ILoggingTool;

/**
 * Helper class for the 'datadebug' classes to test that the report debug info.
 */
public class DebugLoggingSink implements ILoggingTool {

	private static StringBuffer log = new StringBuffer();
	private final String classname;

	public DebugLoggingSink(Class<?> sourceClass) {
		this.classname = sourceClass.getName();
	}

	public static ILoggingTool create(Class<?> sourceClass) {
        return new DebugLoggingSink(sourceClass);
    }

	public static void reset() {
		log = new StringBuffer();
	}

	public static String getLog() {
		return log.toString();
	}

	@Override
	public void dumpSystemProperties() {}

	@Override
	public void setStackLength(int length) {}

	@Override
	public void dumpClasspath() {}

	@Override
	public void debug(Object object) {
        if (object instanceof Throwable) {
            // don't capture this for now
        } else {
            debugString("" + object);
        }
    }

	private void debugString(String message) {
		for (String line : message.split("\n"))
            log.append(classname + " DEBUG: " + line + "\n");
	}

	@Override
	public void debug(Object object, Object... objects) {
		StringBuilder result = new StringBuilder();
        result.append(object);
        for (Object obj : objects) {
            result.append(obj);
        }
        debugString(result.toString());
	}

	@Override
	public void error(Object object) {}

	@Override
	public void error(Object object, Object... objects) {}

	@Override
	public void fatal(Object object) {}

	@Override
	public void info(Object object) {}

	@Override
	public void info(Object object, Object... objects) {}

	@Override
	public void warn(Object object) {}

	@Override
	public void warn(Object object, Object... objects) {}

	@Override
	public boolean isDebugEnabled() {
		return true;
	}

	@Override
	public void setLevel(int level) {}

	@Override
	public int getLevel() {
		return 0;
	}

}
