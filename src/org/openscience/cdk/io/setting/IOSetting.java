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
 * An interface for reader settings. It is subclassed by implementations,
 * one for each type of field, e.g. IntReaderSetting.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 */
public abstract class IOSetting {

    /** The levels available:
     *    HIGH         important question
     *    MEDIUM
     *    LOW          unimportant question
     */
    public static final int HIGH   = 0;
    public static final int MEDIUM = 1;
    public static final int LOW    = 2;
    
    protected int    level;
    protected String name;
    protected String question;
    protected String setting;
    
    /**
     * The default constructor that sets this field. All textual 
     * information is supposed to be English. Localization is taken care
     * off by the ReaderConfigurator.
     *
     * @param name           Name of the setting
     * @param level          Level at which question is asked
     * @param question       Question that is poped to the user when the 
     *                       ReaderSetting needs setting
     * @param defaultSetting The default setting, used if not overwritten
     *                       by a user
     */
    public IOSetting(String name, int level, 
                         String question, String defaultSetting) {
        this.level = level;
        this.name  = name;
        this.question = question;
        this.setting = defaultSetting;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getQuestion() {
        return this.question;
    }

    public String getDefaultSetting() {
        return this.setting;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    /**
     * Sets the setting for a certain question. It will throw
     * a CDKException when the setting is not valid.    
     *
     */
    public void setSetting(String setting) throws CDKException {
        // by default, except all input, so no setting checking                     
        this.setting = setting;
    }

    /**
     * Sets the setting for a certain question. It will throw
     * a CDKException when the setting is not valid.    
     *
     */
    public String getSetting() {
        // by default, except all input, so no setting checking                     
        return this.setting;
    }
}
