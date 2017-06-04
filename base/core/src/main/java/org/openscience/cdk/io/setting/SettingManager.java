/*
 * Copyright (C) 2012 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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

import org.openscience.cdk.interfaces.ISetting;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Provides dynamic management of settings. This
 * was created with the intention of managing {@link IOSetting}'s for {@link org.openscience.cdk.io.IChemObjectIO}
 * however it could be recycled for other purposes where dynamic settings are required.
 * Settings are stored in a {@link Map} using the name of the setting as the key. The name is
 * normalised (lowercase and whitespace removal) to allow 'fuzzy' setting access. This means
 * that character case differences do not affect the retrieval of objects.
 * 
 * <b>Usage:</b>
 * <pre>{@code
 * // create the manager and add a setting
 * SettingManager<IOSetting>   manager = new SettingManager<IOSetting>();
 * manager.add(new BooleanIOSetting("Sample", IOSetting.MEDIUM, "This is a sample?", "true"));
 *
 * // check the setting is present (case insensitive
 * if(manager.has("sample")) {
 *
 *      // access requiring multiple lines of code
 *      BooleanIOSetting setting = manager.get("sample");
 *      String           v1      = setting.getSetting();
 *
 *      // single line access (useful for conditional statements)
 *      String v2 = manager.get("sample", BooleanIOSetting.class).getSetting();
 *
 * }
 * }</pre>
 *
 * @param <T> the type of setting that will be managed (e.g. IOSetting).
 *
 * @author       John May
 * @cdk.module   io
 * @cdk.githash
 * @cdk.created  20.03.2012
 * @see          ISetting
 * @see          IOSetting
 */
public class SettingManager<T extends ISetting> {

    /**
     * Uses to remove white space from names.
     *
     * @see #key(org.openscience.cdk.interfaces.ISetting)
     * @see #key(String)
     */
    private static final Pattern WHITE_SPACE = Pattern.compile("\\s+");

    /**
     * Settings are stored in a map of name -> instance.
     */
    private Map<String, T>       settings    = new HashMap<String, T>(3);

    /**
     * Generate a simple key for the given name. This method normalises the name by
     * converting to lower case and replacing spaces with '.' (e.g. "Buffer Size" is
     * converted to "buffer.size").
     *
     * @param name the name of a setting
     *
     * @return keyed setting name
     */
    private static String key(String name) {
        return WHITE_SPACE.matcher(name).replaceAll(".").toLowerCase(Locale.ENGLISH);
    }

    /**
     * Generate a simple key for the given setting. This method is a convenience
     * method for {@link #key(String)}
     *
     * @param setting the setting to which a key will be generated for
     *
     * @return the keyed name for the setting
     */
    private static String key(ISetting setting) {
        return key(setting.getName());
    }

    /**
     * Add a setting to the manager and return the instance to use. If a 'new' setting is added
     * to the manager which matches the name and class of an previously added 'original' setting,
     * the original setting will be returned. Otherwise the new setting is returned. This allows
     * the add to be used in assignments as follows:
     *
     * <pre>{@code
     *
     * SettingManager   manager  = new SettingManager();
     * BooleanIOSetting setting1 = manager.add(new BooleanIOSetting("use.3d", ...));
     * BooleanIOSetting setting2 = manager.add(new BooleanIOSetting("use.3d", ...));
     *
     * // setting1 == setting2 and so changing a field in setting1 will also change the field
     * // in setting2
     *
     * }</pre>
     *
     * If the names are not equal or the names are equal but the classes are not the new setting
     * is added and returned.
     *
     * @param setting the setting to add
     * @return usable setting
     */
    public T add(T setting) {

        String key = key(setting);

        if (settings.containsKey(key)) {
            T instance = settings.get(key);
            if (instance.getClass() == setting.getClass()) {
                return instance;
            }
        }

        // we could not add if we have a clash, but actual it might be useful
        // to 'override' a setting in sub classes with a new one
        settings.put(key(setting), setting);

        return setting;

    }

    /**
     * Access the setting stored for given name. If not setting is found the provided
     * name an {@link InvalidParameterException} will be thrown. The method is generic
     * to allow simplified access to settings. This however means that if the incorrect
     * type is provided a {@link ClassCastException} may be thrown.
     * 
     * <pre>{@code
     * SettingManager manager = ...;
     * manger.add(new BooleanIOSetting("name", ...));
     * 
     * BooleanIOSetting setting = manager.get("Name"); // okay
     * OptionIOSetting setting  = manager.get("Name"); // class cast exception
     * }</pre>
     *
     * @param name name of the setting to retrieve
     * @param <S>  type that will be return
     *
     * @return instance of the setting for the provided name
     *
     * @see #get(String, Class)
     */
    public <S extends T> S get(String name) {

        String key = key(name);

        if (settings.containsKey(key)) return (S) settings.get(key);

        throw new InvalidParameterException("No setting found for name " + name + "(key=" + key + ") "
                + "available settings are: " + settings.keySet());

    }

    /**
     * Convenience method that allows specification of return ISetting type so that you can nest the call to
     * access the setting value.
     * 
     * <pre>{@code
     * SettingManager manager = ...;
     * manger.add(new BooleanIOSetting("Setting", ...));
     * 
     * if(manager.get("Setting", BooleanIOSetting.class).isSet()){
     *     // do something
     * }
     * 
     * }</pre>
     *
     * @param name name of the setting to retrieve
     * @param c    the class of the setting (matching generic return type). This is need
     *             as due to type erasure we don't know the class of 'S' at runtime
     * @param <S>  type that will be return
     *
     * @return instance of the setting
     *
     * @see #get(String)
     */
    public <S extends T> S get(String name, Class<S> c) {
        return (S) get(name);
    }

    /**
     * Determines whether the manager currently holds a setting of
     * the provided name.
     *
     * @param name name of the setting
     *
     * @return whether the manager currently contains the desired setting
     */
    public boolean has(String name) {
        return settings.containsKey(key(name));
    }

    /**
     * Access a collection of all settings in the manager.
     *
     * @return collection of managed settings
     */
    public Collection<T> getSettings() {
        return settings.values();
    }

    /**
     * Compatibility method generates an array of ISetting objects. This method
     * wraps a call to {@link Collection#toArray(Object[])}} and so is used the same way.
     * Note: it is preferable to use the collection's accessor {@link #getSettings()}
     * 
     * Usage: <pre>{@code
     * IOSetting[] settings = manager.toArray(new IOSetting[0]);
     * }</pre>
     *
     * @param c empty array of type to generate
     *
     * @return new fixed array of the settings managed by the manager
     *
     * @see #getSettings()
     */
    public T[] toArray(T[] c) {
        return (T[]) getSettings().toArray(c);
    }

}
