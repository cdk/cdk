/* Copyright (C) 2003-2007  The CDK Development Team
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
import org.openscience.cdk.interfaces.ISetting;

/**
 * An interface for reader settings. It is subclassed by implementations,
 * one for each type of field, e.g. IntReaderSetting.
 *
 * @author Egon Willighagen &lt;egonw@sci.kun.nl&gt;
 */
public abstract class IOSetting<T> implements ISetting {

    public enum Importance {
        HIGH, MEDIUM, LOW
    }

    protected final Importance level;
    protected final String     name;
    protected final String     question;
    protected T                setting;
    protected T                defaultSetting;

    /**
     * The default constructor that sets this field. All textual
     * information is supposed to be English. Localization is taken care
     * off by the ReaderConfigurator.
     *
     * @param name           Name of the setting
     * @param level          Level at which question is asked
     * @param question       Question that is popped to the user when the
     *                       ReaderSetting needs setting
     * @param defaultSetting The default setting, used if not overwritten
     *                       by a user
     */
    public IOSetting(String name, Importance level, String question, T defaultSetting) {
        this.level = level;
        this.name = name;
        this.question = question;
        this.setting = defaultSetting;
        this.defaultSetting = defaultSetting;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getQuestion() {
        return this.question;
    }

    public String getDefaultSetting() {
        return this.defaultSetting.toString();
    }

    public Importance getLevel() {
        return this.level;
    }

    abstract public void setSetting(String setting) throws CDKException;

    /**
     * Sets the setting for a certain question. It will throw
     * a CDKException when the setting is not valid.
     *
     */
    public String getSetting() {
        // by default, except all input, so no setting checking
        return this.setting.toString();
    }

    /**
     * Access the current value of this option.
     * @return the value
     */
    public T get() {
        return setting;
    }

    /**
     * Set the current value of this option.
     * @param value the new value
     */
    public void set(T value) {
        this.setting = value;
    }
}
