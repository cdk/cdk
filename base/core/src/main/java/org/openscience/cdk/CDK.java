/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
package org.openscience.cdk;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Helper class to provide general information about this CDK library.
 *
 * @cdk.module core
 * @cdk.githash
 */
public class CDK {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(CDK.class);
    private static final String RESOURCE_LOCATION = "/build.props";
    private static volatile String version;

    /**
     * Returns the version of this CDK library.
     *
     * @return The library version, or null if it could not be found
     */
    public static String getVersion() {
        if (version != null)
            return version;
        try (InputStream stream = CDK.class.getResourceAsStream(RESOURCE_LOCATION)) {
            if (stream == null) {
                // load from JAR (as packaged with maven)
                version = CDK.class.getPackage().getImplementationVersion();
            }
            Properties props = new Properties();
            props.load(stream);
            version = props.getProperty("version");
            return version;
        } catch (IOException exception) {
            // there is no back up
            logger.error("Error while loading the build.props file: ", exception.getMessage());
            logger.debug(exception);
        }
        return null;
    }
}
