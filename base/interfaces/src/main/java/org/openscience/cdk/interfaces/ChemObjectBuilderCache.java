/*
 * Copyright (C) 2025 John Mayfield
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.openscience.cdk.interfaces;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This enum is used to provide a cache of silent/default ChemObjectBuilder's
 * that are loaded via reflection. It should not be made public and servers only
 * to provide for {@link IChemObjectBuilder#find()}.
 */
enum ChemObjectBuilderCache {
    INSTANCE;

    private final IChemObjectBuilder silentBuilder;
    private final IChemObjectBuilder defaultBuilder;

    ChemObjectBuilderCache() {
        silentBuilder = load("org.openscience.cdk.silent.SilentChemObjectBuilder");
        defaultBuilder = load("org.openscience.cdk.DefaultChemObjectBuilder");
    }

    private static IChemObjectBuilder load(String path)
    {
        try {
            Class<?> cls = Class.forName(path);
            Method method = cls.getDeclaredMethod("getInstance");
            return  (IChemObjectBuilder) method.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException e) {
            return null;
        }
    }

    public IChemObjectBuilder get() {
        if (silentBuilder != null)
            return silentBuilder;
        return defaultBuilder;
    }
}
