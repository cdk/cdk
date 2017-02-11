/* Copyright (C) 1997-2008  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.listener.IChemObjectIOListener;
import org.openscience.cdk.io.setting.IOSetting;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;

/**
 * This class is the interface that all IO readers should implement.
 * Programs need only care about this interface for any kind of IO.
 * Currently, database IO and file IO is supported.
 *
 * <p>The easiest way to implement a new {@link IChemObjectReader} is to
 * subclass the {@link DefaultChemObjectReader}.
 *
 * @cdk.module  io
 * @cdk.githash
 *
 * @see DefaultChemObjectReader
 *
 * @author Egon Willighagen &lt;egonw@sci.kun.nl&gt;
 **/
public interface IChemObjectIO extends Closeable {

    /**
     * Returns the {@link IResourceFormat} class for this IO class.
     */
    public IResourceFormat getFormat();

    /**
     * Returns whether the given {@link IChemObject} can be read or written.
     *
     * @param classObject {@link IChemObject} of which is tested if it can be handled.
     * @return true, if the {@link IChemObject} can be handled.
     */
    public boolean accepts(Class<? extends IChemObject> classObject);

    /**
     * Closes this IChemObjectIO's resources.
     *
     * @throws IOException when the wrapper IO class cannot be closed.
     */
    @Override
    public void close() throws IOException;

    /**
     * Returns an array of {@link IOSetting}s defined by this IChemObjectIO class.
     *
     * @return the {@link IOSetting}s for this class.
     */
    public IOSetting[] getIOSettings();

    /**
     * Adds a {@link IChemObjectIOListener} to this IChemObjectIO.
     *
     * @param listener the reader listener to add.
     */
    public void addChemObjectIOListener(IChemObjectIOListener listener);

    /**
     * Removes a {@link IChemObjectIOListener} from this IChemObjectIO.
     *
     * @param listener the listener to be removed.
     */
    public void removeChemObjectIOListener(IChemObjectIOListener listener);

    /**
     * Access all the listeners for this ChemObject Reader or Writer. This will
     * returned an unmodifiable list of listeners. Listeners should be added to and
     * removed from the reader/writer using {@link #addChemObjectIOListener(org.openscience.cdk.io.listener.IChemObjectIOListener)} and
     * {@link #removeChemObjectIOListener(org.openscience.cdk.io.listener.IChemObjectIOListener)}
     *
     * @return all listeners managed by this IO object
     */
    public Collection<IChemObjectIOListener> getListeners();

    /**
     * Add an IOSetting to the reader/writer. If the name clashes with
     * another setting the original setting will be returned. This method
     * should be called when assigning field settings:
     *
     * <pre>{@code
     * private BooleanIOSetting setting; // field
     *
     * ...
     *
     * setting = addSetting(new BooleanIOSetting("setting", ...));
     * // if setting was already added we are now using the correct instance
     *
     *}</pre>
     *
     * @param setting setting to add
     *
     * @return usable setting
     *
     * @see org.openscience.cdk.io.setting.SettingManager#add(org.openscience.cdk.interfaces.ISetting)
     */
    public <S extends IOSetting> S addSetting(IOSetting setting);

    /**
     * Adds a collection of {@link IOSetting}s to the reader/writer. This
     * is useful for transferring/propagating settings between different
     * reader/writer.
     *
     * When the new settings are added if there is a setting with the same
     * name already stored the value for the new setting is set on the managed
     * setting (See. IteratingSDFReader/SDFWriter for propagation examples).
     * Note that if the setting is invalid (a CDKException thrown) then the setting
     * will not be set.
     *
     * <pre>{@code
     * // two different readers (of same or different type)
     * IChemObjectReader reader1 = ...;
     * IChemObjectReader reader2 = ...;
     *
     * // settings transferred from reader2 to reader1
     * reader1.addSettings(reader2.getSettings());
     * }</pre>
     *
     * @param settings collection of settings to add
     * @see #getSettings()
     */
    public void addSettings(Collection<IOSetting> settings);

    /**
     * Determine whether this reader/writer has a setting of the
     * provided name.
     *
     * @param name name of a setting
     *
     * @return whether the setting is available
     *
     * @see org.openscience.cdk.io.setting.SettingManager#has(String)
     */
    public boolean hasSetting(String name);

    /**
     * Access a named setting managed by this reader/writer.
     *
     * @param name name of the setting
     * @param <S>  type to cast to
     *
     * @return instance of the setting for the name (InvalidParameterException is thrown
     *         if no setting for the provided name is found)
     *
     * @see #getSetting(String, Class)
     * @see org.openscience.cdk.io.setting.SettingManager#get(String)
     */
    public <S extends IOSetting> S getSetting(String name);

    /**
     * Access a named setting managed by this reader/writer.
     *
     * @param name name of the setting
     * @param c    the class of the setting (matching generic return type). This is need
     *             as due to type erasure we don't know the class of 'S' at runtime.
     * @param <S>  type to cast to
     *
     * @return instance of the setting for the name (InvalidParameterException is thrown
     *         if no setting for the provided name is found)
     *
     * @see #getSetting(String)
     * @see org.openscience.cdk.io.setting.SettingManager#get(String, Class)
     */
    public <S extends IOSetting> S getSetting(String name, Class<S> c);

    /**
     * Access a collection of {@link IOSetting}s for this reader/writer.
     * @return collection of IOSetting's
     * @see #addSettings(java.util.Collection)
     */
    public Collection<IOSetting> getSettings();

}
