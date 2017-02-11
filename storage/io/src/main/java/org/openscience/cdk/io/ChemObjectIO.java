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
package org.openscience.cdk.io;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.listener.IChemObjectIOListener;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.io.setting.SettingManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Provides some basic functionality for readers and writers. This includes
 * managing the {@link IChemObjectIOListener}'s and managing of {@link IOSetting}'s.
 * The IOSettings are managed via the {@link SettingManager} class with most
 * method's wrapped to more descriptive method names (e.g.
 * {@link SettingManager#get(String)} is invoked by {@link #getSetting(String)}).
 *
 * @author johnmay
 * @cdk.module io
 * @cdk.githash
 * @cdk.created 20.03.2012
 */
public abstract class ChemObjectIO implements IChemObjectIO {

    /**
     * Holder of reader event listeners.
     */
    private List<IChemObjectIOListener> listeners = new ArrayList<IChemObjectIOListener>(2);
    private SettingManager<IOSetting>   settings  = new SettingManager<IOSetting>();

    /**
     *{@inheritDoc}
     */
    @Override
    public List<IChemObjectIOListener> getListeners() {
        return Collections.unmodifiableList(listeners);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void addChemObjectIOListener(IChemObjectIOListener listener) {
        listeners.add(listener);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void removeChemObjectIOListener(IChemObjectIOListener listener) {
        listeners.remove(listener);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public <S extends IOSetting> S addSetting(IOSetting setting) {
        return (S) settings.add(setting);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void addSettings(Collection<IOSetting> settings) {
        for (IOSetting setting : settings) {
            if (hasSetting(setting.getName())) {
                try {
                    getSetting(setting.getName()).setSetting(setting.getSetting());
                } catch (CDKException ex) {
                    // setting value was invalid (ignore as we already have a value for this setting
                    // and we can't throw CDKException as IChemObject is in interfaces module)
                }
            } else {
                addSetting(setting);
            }
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean hasSetting(String name) {
        return settings.has(name);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public <S extends IOSetting> S getSetting(String name) {
        return settings.get(name);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public <S extends IOSetting> S getSetting(String name, Class<S> c) {
        return settings.get(name, c);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public IOSetting[] getIOSettings() {
        return settings.toArray(new IOSetting[0]);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public Collection<IOSetting> getSettings() {
        return settings.getSettings();
    }

    /**
     * Fires {@link IChemObjectIOListener#processIOSettingQuestion(org.openscience.cdk.io.setting.IOSetting)}
     * for all managed listeners.
     *
     * @param setting the setting to process
     */
    protected void fireIOSettingQuestion(IOSetting setting) {
        for (IChemObjectIOListener listener : listeners) {
            listener.processIOSettingQuestion(setting);
        }
    }
}
