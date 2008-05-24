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

import java.util.List;

import org.openscience.cdk.exception.CDKException;

/**
 * An class for a reader setting which must be found in the list 
 * of possible settings.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 */
public class OptionIOSetting extends IOSetting {

    private List<String> settings;
    
    /**
     * OptionIOSetting is IOSetting for which the value must be
     * in the list of possible options.
     */
    public OptionIOSetting(String name, int level, 
                           String question, List<String> settings, 
                           String defaultSetting) {
        super(name, level, question, defaultSetting);
        this.settings = settings;
        if (!this.settings.contains(defaultSetting)) {
            this.settings.add(defaultSetting);
        }
    }
    
    /**
     * Sets the setting for a certain question. It will throw
     * a CDKException when the setting is not valid.    
     *
     */
    public void setSetting(String setting) throws CDKException {
        if (settings.contains(setting)) {
            this.setting = setting;
        } else {
            throw new CDKException("Setting " + setting + " is not allowed.");
        }
    }

    /**
     * Sets the setting for a certain question. It will throw
     * a CDKException when the setting is not valid. The first setting is
     * setting 1.
     *
     */
    public void setSetting(int setting) throws CDKException {
        if (setting < settings.size() + 1 && setting > 0) {
            this.setting = (String)settings.get(setting-1);
        } else {
            throw new CDKException("Setting " + setting + " does not exist.");
        }
    }
    
    /**
     * Returns a Vector of Strings containing all possible options.
     */
    public List<String> getOptions() {
        return settings;
    }
    
}
