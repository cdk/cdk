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

import java.io.InputStream;
import java.util.Properties;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Helper class to provide general information about this CDK library.
 *
 * @cdk.module core
 */
@TestClass("org.openscience.cdk.CDKTest")
public class CDK {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(CDK.class);

    /**
     * Returns the version of this CDK library.
     *
     * @return A {@link String} representation of the version number.
     */
    @TestMethod("testGetVersion")
    public static String getVersion() {
        String propsFilename = "build.props";
        Properties props = new Properties();
        try {
            InputStream stream = CDK.class.getClassLoader().getResourceAsStream(propsFilename);
            props.load(stream);
            return props.getProperty("version");
        } catch (Exception exception) {
            // there is no back up
            logger.error(
                "Error while loading the buid.props file: ", exception.getMessage()
            );
            logger.debug(exception);
            exception.printStackTrace();
        }
        return "ERROR";
    }
    
}
