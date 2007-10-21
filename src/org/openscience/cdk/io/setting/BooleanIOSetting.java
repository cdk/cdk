/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2007  The CDK Development Team
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io.setting;

import org.openscience.cdk.exception.CDKException;

/**
 * An class for a reader setting which must be of type String.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 */
public class BooleanIOSetting extends IOSetting {

    public BooleanIOSetting(String name, int level, 
                            String question, String defaultSetting) {
        super(name, level, question, defaultSetting);
    }

    /**
     * Sets the setting for a certain question. The setting
     * is a boolean, and it accepts only "true" and "false".
     */
    public void setSetting(String setting) throws CDKException {
        if (setting.equals("true") || setting.equals("false")) {
            this.setting = setting;
        } else if (setting.equals("yes") || setting.equals("y")) {
            this.setting = "true";
        } else if (setting.equals("no") || setting.equals("n")) {
            this.setting = "false";
        } else {
            throw new CDKException("Setting " + setting + " is not a boolean.");
        }
    }

    public boolean isSet() {
        if (setting.equals("true")) {
            return true;
        } else {
            return false;
        }
    }
}
